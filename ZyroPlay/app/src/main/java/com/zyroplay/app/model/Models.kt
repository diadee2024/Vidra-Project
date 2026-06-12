package com.zyroplay.app.model

data class Channel(
    val id: String,
    val name: String,
    val category: String,
    val logoUrl: String? = null,
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
    val episodes: Int = 0
)

data class ContentRow(
    val title: String,
    val items: List<VodItem>
)

enum class NavDestination(
    val route: String,
    val label: String
) {
    Home("home", "Accueil"),
    Live("live", "Live TV"),
    Movies("movies", "Films"),
    Series("series", "Séries"),
    CatchUp("catchup", "Replay"),
    Favorites("favorites", "Favoris"),
    Settings("settings", "Réglages")
}
