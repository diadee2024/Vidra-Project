package com.zyroplay.app.model

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
}

data class Channel(
    val id: String,
    val name: String,
    val category: String,
    val logoUrl: String? = null,
    val streamUrl: String = "",
    val epgChannelId: String? = null,
    val isLive: Boolean = true,
    val currentProgram: String = ""
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
    val subtitle: String = ""
)

enum class NavDestination(
    val route: String,
    val label: String
) {
    Home("home", "Accueil"),
    Live("live", "Live TV"),
    Movies("movies", "Films"),
    Series("series", "Séries"),
    Epg("epg", "Guide TV"),
    CatchUp("catchup", "Replay"),
    Favorites("favorites", "Favoris"),
    Settings("settings", "Réglages")
}
