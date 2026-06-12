package com.zyroplay.app.model

import java.util.UUID

enum class PlaylistType { XTREAM, M3U }

data class PlaylistCredentials(
    val type: PlaylistType,
    val serverUrl: String = "",
    val username: String = "",
    val password: String = "",
    val m3uUrl: String = ""
) {
    val isValid: Boolean
        get() = when (type) {
            PlaylistType.XTREAM -> serverUrl.isNotBlank() && username.isNotBlank() && password.isNotBlank()
            PlaylistType.M3U -> m3uUrl.isNotBlank()
        }

    val displayLabel: String
        get() = when (type) {
            PlaylistType.XTREAM -> serverUrl.substringAfter("://").substringBefore("/").ifBlank { serverUrl }
            PlaylistType.M3U -> m3uUrl.substringAfter("://").take(40)
        }
}

data class SavedPlaylist(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val credentials: PlaylistCredentials
)

data class Channel(
    val id: String,
    val name: String,
    val category: String,
    val logoUrl: String? = null,
    val streamUrl: String = "",
    val epgChannelId: String? = null,
    val isLive: Boolean = true,
    val currentProgram: String = "",
    val tvArchive: Boolean = false,
    val tvArchiveDurationDays: Int = 0
)

data class VodItem(
    val id: String,
    val title: String,
    val year: String,
    val rating: String,
    val genre: String,
    val posterUrl: String? = null,
    val streamUrl: String = "",
    val containerExtension: String = "mp4",
    val duration: String = "",
    val description: String = ""
)

data class SeriesItem(
    val id: String,
    val title: String,
    val seasons: Int,
    val rating: String,
    val genre: String,
    val posterUrl: String? = null,
    val episodes: Int = 0,
    val description: String = ""
)

data class EpisodeItem(
    val id: String,
    val title: String,
    val seasonNumber: Int,
    val episodeNumber: Int,
    val streamUrl: String,
    val posterUrl: String? = null,
    val duration: String = ""
)

data class EpgProgram(
    val id: String,
    val channelId: String,
    val title: String,
    val description: String = "",
    val startTime: Long,
    val endTime: Long
)

data class ContentRow(
    val title: String,
    val items: List<VodItem>
)

data class PlayRequest(
    val title: String,
    val streamUrl: String,
    val subtitle: String = "",
    val posterUrl: String? = null,
    val contentId: String = "",
    val isLive: Boolean = false,
    val channelList: List<Channel> = emptyList(),
    val startPositionMs: Long = 0L
)

data class CatchUpProgram(
    val id: String,
    val channelId: String,
    val channelName: String,
    val title: String,
    val description: String = "",
    val startTime: Long,
    val endTime: Long,
    val streamUrl: String
)

data class WatchHistoryItem(
    val contentId: String,
    val title: String,
    val streamUrl: String,
    val posterUrl: String? = null,
    val subtitle: String = "",
    val positionMs: Long = 0L,
    val durationMs: Long = 0L,
    val lastWatched: Long = System.currentTimeMillis(),
    val isLive: Boolean = false
)

data class PlayerSettings(
    val bufferSeconds: Int = 15,
    val subtitlesEnabled: Boolean = true,
    val preferredAudioLanguage: String = "auto",
    val aspectRatio: String = "fit"
)

data class SearchResults(
    val channels: List<Channel> = emptyList(),
    val movies: List<VodItem> = emptyList(),
    val series: List<SeriesItem> = emptyList()
) {
    val total: Int get() = channels.size + movies.size + series.size
}

enum class NavDestination(
    val route: String,
    val label: String
) {
    Home("home", "Accueil"),
    Live("live", "Live TV"),
    Movies("movies", "Films"),
    Series("series", "Séries"),
    Search("search", "Recherche"),
    Epg("epg", "Guide TV"),
    CatchUp("catchup", "Replay"),
    Favorites("favorites", "Favoris"),
    Settings("settings", "Réglages")
}
