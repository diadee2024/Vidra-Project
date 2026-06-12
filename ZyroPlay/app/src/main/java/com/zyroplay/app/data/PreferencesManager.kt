package com.zyroplay.app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zyroplay.app.model.PlayerSettings
import com.zyroplay.app.model.SavedPlaylist
import com.zyroplay.app.model.WatchHistoryItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.security.MessageDigest

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "zyroplay_prefs")

class PreferencesManager(private val context: Context) {

    private val gson = Gson()
    private val secureStore = SecureCredentialsStore(context)

    val playlistsFlow: Flow<List<SavedPlaylist>> = context.dataStore.data.map { prefs ->
        val json = prefs[KEY_PLAYLISTS] ?: return@map emptyList()
        val type = object : TypeToken<List<SavedPlaylist>>() {}.type
        runCatching { gson.fromJson<List<SavedPlaylist>>(json, type) }
            .getOrDefault(emptyList())
            .map { secureStore.mergeCredentials(it) }
    }

    val activePlaylistIdFlow: Flow<String?> = context.dataStore.data.map { it[KEY_ACTIVE_PLAYLIST] }

    val activeCredentialsFlow: Flow<com.zyroplay.app.model.PlaylistCredentials?> =
        context.dataStore.data.map { prefs ->
            val playlistsJson = prefs[KEY_PLAYLISTS] ?: return@map null
            val activeId = prefs[KEY_ACTIVE_PLAYLIST] ?: return@map null
            val type = object : TypeToken<List<SavedPlaylist>>() {}.type
            val playlists = runCatching { gson.fromJson<List<SavedPlaylist>>(playlistsJson, type) }
                .getOrDefault(emptyList())
            playlists.find { it.id == activeId }
                ?.let { secureStore.mergeCredentials(it).credentials }
        }

    val themeIndexFlow: Flow<Int> = context.dataStore.data.map { it[KEY_THEME] ?: 0 }

    val favoritesFlow: Flow<Set<String>> = context.dataStore.data.map { it[KEY_FAVORITES] ?: emptySet() }

    val watchHistoryFlow: Flow<List<WatchHistoryItem>> = context.dataStore.data.map { prefs ->
        val json = prefs[KEY_WATCH_HISTORY] ?: return@map emptyList()
        val type = object : TypeToken<List<WatchHistoryItem>>() {}.type
        runCatching { gson.fromJson<List<WatchHistoryItem>>(json, type) }.getOrDefault(emptyList())
    }

    val playerSettingsFlow: Flow<PlayerSettings> = context.dataStore.data.map { prefs ->
        val json = prefs[KEY_PLAYER_SETTINGS] ?: return@map PlayerSettings()
        runCatching { gson.fromJson(json, PlayerSettings::class.java) }.getOrDefault(PlayerSettings())
    }

    val parentalControlEnabledFlow: Flow<Boolean> =
        context.dataStore.data.map { it[KEY_PARENTAL_ENABLED] ?: false }

    suspend fun savePlaylist(playlist: SavedPlaylist) {
        secureStore.saveCredentials(playlist.id, playlist.credentials)
        val stripped = playlist.copy(
            credentials = secureStore.stripForStorage(playlist.credentials)
        )
        context.dataStore.edit { prefs ->
            val current = readPlaylists(prefs).toMutableList()
            val index = current.indexOfFirst { it.id == stripped.id }
            if (index >= 0) current[index] = stripped else current.add(stripped)
            prefs[KEY_PLAYLISTS] = gson.toJson(current)
            prefs[KEY_ACTIVE_PLAYLIST] = stripped.id
        }
    }

    suspend fun setActivePlaylist(id: String) {
        context.dataStore.edit { it[KEY_ACTIVE_PLAYLIST] = id }
    }

    suspend fun deletePlaylist(id: String) {
        secureStore.deleteCredentials(id)
        context.dataStore.edit { prefs ->
            val current = readPlaylists(prefs).filter { it.id != id }
            prefs[KEY_PLAYLISTS] = gson.toJson(current)
            if (prefs[KEY_ACTIVE_PLAYLIST] == id) {
                prefs.remove(KEY_ACTIVE_PLAYLIST)
            }
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { prefs ->
            prefs.remove(KEY_ACTIVE_PLAYLIST)
        }
    }

    suspend fun clearAll() {
        context.dataStore.edit { it.clear() }
    }

    suspend fun saveTheme(index: Int) {
        context.dataStore.edit { it[KEY_THEME] = index }
    }

    suspend fun toggleFavorite(id: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[KEY_FAVORITES]?.toMutableSet() ?: mutableSetOf()
            if (!current.add(id)) current.remove(id)
            prefs[KEY_FAVORITES] = current
        }
    }

    suspend fun saveWatchProgress(item: WatchHistoryItem) {
        context.dataStore.edit { prefs ->
            val current = readWatchHistory(prefs).toMutableList()
            current.removeAll { it.contentId == item.contentId }
            current.add(0, item)
            prefs[KEY_WATCH_HISTORY] = gson.toJson(current.take(MAX_HISTORY))
        }
    }

    suspend fun removeWatchHistory(contentId: String) {
        context.dataStore.edit { prefs ->
            val current = readWatchHistory(prefs).filter { it.contentId != contentId }
            prefs[KEY_WATCH_HISTORY] = gson.toJson(current)
        }
    }

    suspend fun savePlayerSettings(settings: PlayerSettings) {
        context.dataStore.edit { prefs ->
            prefs[KEY_PLAYER_SETTINGS] = gson.toJson(settings)
        }
    }

    suspend fun setParentalControlEnabled(enabled: Boolean) {
        context.dataStore.edit { it[KEY_PARENTAL_ENABLED] = enabled }
    }

    fun saveParentalPin(pin: String) {
        secureStore.savePinHash(hashPin(pin))
    }

    fun verifyParentalPin(pin: String): Boolean {
        val stored = secureStore.getPinHash() ?: return false
        return stored == hashPin(pin)
    }

    fun hasParentalPin(): Boolean = secureStore.getPinHash() != null

    fun clearParentalPin() {
        secureStore.clearPin()
    }

    fun saveTmdbApiKey(key: String) {
        secureStore.saveTmdbApiKey(key)
    }

    fun getTmdbApiKey(): String = secureStore.getTmdbApiKey()

    private fun hashPin(pin: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val bytes = digest.digest(pin.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    private fun readPlaylists(prefs: Preferences): List<SavedPlaylist> {
        val json = prefs[KEY_PLAYLISTS] ?: return emptyList()
        val type = object : TypeToken<List<SavedPlaylist>>() {}.type
        return runCatching { gson.fromJson<List<SavedPlaylist>>(json, type) }.getOrDefault(emptyList())
    }

    private fun readWatchHistory(prefs: Preferences): List<WatchHistoryItem> {
        val json = prefs[KEY_WATCH_HISTORY] ?: return emptyList()
        val type = object : TypeToken<List<WatchHistoryItem>>() {}.type
        return runCatching { gson.fromJson<List<WatchHistoryItem>>(json, type) }.getOrDefault(emptyList())
    }

    companion object {
        private const val MAX_HISTORY = 30
        private val KEY_PLAYLISTS = stringPreferencesKey("playlists_json")
        private val KEY_ACTIVE_PLAYLIST = stringPreferencesKey("active_playlist_id")
        private val KEY_THEME = intPreferencesKey("theme_index")
        private val KEY_FAVORITES = stringSetPreferencesKey("favorites")
        private val KEY_WATCH_HISTORY = stringPreferencesKey("watch_history")
        private val KEY_PLAYER_SETTINGS = stringPreferencesKey("player_settings")
        private val KEY_PARENTAL_ENABLED = booleanPreferencesKey("parental_enabled")
    }
}
