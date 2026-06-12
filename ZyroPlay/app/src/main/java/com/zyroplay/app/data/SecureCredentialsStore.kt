package com.zyroplay.app.data

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.zyroplay.app.model.PlaylistCredentials
import com.zyroplay.app.model.PlaylistType
import com.zyroplay.app.model.SavedPlaylist

class SecureCredentialsStore(context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs = EncryptedSharedPreferences.create(
        context,
        PREFS_NAME,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveCredentials(playlistId: String, credentials: PlaylistCredentials) {
        prefs.edit()
            .putString(keyPassword(playlistId), credentials.password)
            .putString(keyM3u(playlistId), credentials.m3uUrl)
            .apply()
    }

    fun deleteCredentials(playlistId: String) {
        prefs.edit()
            .remove(keyPassword(playlistId))
            .remove(keyM3u(playlistId))
            .apply()
    }

    fun mergeCredentials(playlist: SavedPlaylist): SavedPlaylist {
        val password = prefs.getString(keyPassword(playlist.id), "") ?: ""
        val m3uUrl = prefs.getString(keyM3u(playlist.id), playlist.credentials.m3uUrl) ?: playlist.credentials.m3uUrl
        return playlist.copy(
            credentials = playlist.credentials.copy(
                password = password,
                m3uUrl = m3uUrl
            )
        )
    }

    fun stripForStorage(credentials: PlaylistCredentials): PlaylistCredentials {
        return when (credentials.type) {
            PlaylistType.XTREAM -> credentials.copy(password = "")
            PlaylistType.M3U -> credentials.copy(m3uUrl = "")
        }
    }

    fun savePinHash(hash: String) {
        prefs.edit().putString(KEY_PIN_HASH, hash).apply()
    }

    fun getPinHash(): String? = prefs.getString(KEY_PIN_HASH, null)

    fun clearPin() {
        prefs.edit().remove(KEY_PIN_HASH).apply()
    }

    fun saveTmdbApiKey(key: String) {
        prefs.edit().putString(KEY_TMDB, key).apply()
    }

    fun getTmdbApiKey(): String = prefs.getString(KEY_TMDB, "") ?: ""

    private fun keyPassword(id: String) = "pwd_$id"
    private fun keyM3u(id: String) = "m3u_$id"

    companion object {
        private const val PREFS_NAME = "zyroplay_secure"
        private const val KEY_PIN_HASH = "parental_pin_hash"
        private const val KEY_TMDB = "tmdb_api_key"
    }
}
