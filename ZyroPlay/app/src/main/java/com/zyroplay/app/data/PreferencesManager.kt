package com.zyroplay.app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.zyroplay.app.model.PlaylistCredentials
import com.zyroplay.app.model.PlaylistType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "zyroplay_prefs")

class PreferencesManager(private val context: Context) {

    val credentialsFlow: Flow<PlaylistCredentials?> = context.dataStore.data.map { prefs ->
        val type = prefs[KEY_TYPE] ?: return@map null
        when (type) {
            PlaylistType.XTREAM.name -> PlaylistCredentials(
                type = PlaylistType.XTREAM,
                serverUrl = prefs[KEY_SERVER] ?: "",
                username = prefs[KEY_USER] ?: "",
                password = prefs[KEY_PASS] ?: ""
            )
            PlaylistType.M3U.name -> PlaylistCredentials(
                type = PlaylistType.M3U,
                m3uUrl = prefs[KEY_M3U] ?: ""
            )
            else -> null
        }
    }

    val themeIndexFlow: Flow<Int> = context.dataStore.data.map { it[KEY_THEME] ?: 0 }

    val favoritesFlow: Flow<Set<String>> = context.dataStore.data.map { it[KEY_FAVORITES] ?: emptySet() }

    suspend fun saveCredentials(credentials: PlaylistCredentials) {
        context.dataStore.edit { prefs ->
            prefs[KEY_TYPE] = credentials.type.name
            prefs[KEY_SERVER] = credentials.serverUrl
            prefs[KEY_USER] = credentials.username
            prefs[KEY_PASS] = credentials.password
            prefs[KEY_M3U] = credentials.m3uUrl
        }
    }

    suspend fun clearCredentials() {
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

    companion object {
        private val KEY_TYPE = stringPreferencesKey("playlist_type")
        private val KEY_SERVER = stringPreferencesKey("server_url")
        private val KEY_USER = stringPreferencesKey("username")
        private val KEY_PASS = stringPreferencesKey("password")
        private val KEY_M3U = stringPreferencesKey("m3u_url")
        private val KEY_THEME = intPreferencesKey("theme_index")
        private val KEY_FAVORITES = stringSetPreferencesKey("favorites")
    }
}
