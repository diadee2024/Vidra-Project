package com.zyroplay.app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zyroplay.app.model.PlaylistCredentials
import com.zyroplay.app.model.PlaylistType
import com.zyroplay.app.model.SavedPlaylist
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "zyroplay_prefs")

class PreferencesManager(private val context: Context) {

    private val gson = Gson()

    val playlistsFlow: Flow<List<SavedPlaylist>> = context.dataStore.data.map { prefs ->
        val json = prefs[KEY_PLAYLISTS] ?: return@map emptyList()
        val type = object : TypeToken<List<SavedPlaylist>>() {}.type
        runCatching { gson.fromJson<List<SavedPlaylist>>(json, type) }.getOrDefault(emptyList())
    }

    val activePlaylistIdFlow: Flow<String?> = context.dataStore.data.map { it[KEY_ACTIVE_PLAYLIST] }

    val activeCredentialsFlow: Flow<PlaylistCredentials?> = context.dataStore.data.map { prefs ->
        val playlistsJson = prefs[KEY_PLAYLISTS] ?: return@map null
        val activeId = prefs[KEY_ACTIVE_PLAYLIST] ?: return@map null
        val type = object : TypeToken<List<SavedPlaylist>>() {}.type
        val playlists = runCatching { gson.fromJson<List<SavedPlaylist>>(playlistsJson, type) }.getOrDefault(emptyList())
        playlists.find { it.id == activeId }?.credentials
    }

    val themeIndexFlow: Flow<Int> = context.dataStore.data.map { it[KEY_THEME] ?: 0 }

    val favoritesFlow: Flow<Set<String>> = context.dataStore.data.map { it[KEY_FAVORITES] ?: emptySet() }

    suspend fun savePlaylist(playlist: SavedPlaylist) {
        context.dataStore.edit { prefs ->
            val current = readPlaylists(prefs).toMutableList()
            val index = current.indexOfFirst { it.id == playlist.id }
            if (index >= 0) current[index] = playlist else current.add(playlist)
            prefs[KEY_PLAYLISTS] = gson.toJson(current)
            prefs[KEY_ACTIVE_PLAYLIST] = playlist.id
        }
    }

    suspend fun setActivePlaylist(id: String) {
        context.dataStore.edit { it[KEY_ACTIVE_PLAYLIST] = id }
    }

    suspend fun deletePlaylist(id: String) {
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

    private fun readPlaylists(prefs: Preferences): List<SavedPlaylist> {
        val json = prefs[KEY_PLAYLISTS] ?: return emptyList()
        val type = object : TypeToken<List<SavedPlaylist>>() {}.type
        return runCatching { gson.fromJson<List<SavedPlaylist>>(json, type) }.getOrDefault(emptyList())
    }

    companion object {
        private val KEY_PLAYLISTS = stringPreferencesKey("playlists_json")
        private val KEY_ACTIVE_PLAYLIST = stringPreferencesKey("active_playlist_id")
        private val KEY_THEME = intPreferencesKey("theme_index")
        private val KEY_FAVORITES = stringSetPreferencesKey("favorites")
    }
}
