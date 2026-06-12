package com.zyroplay.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistPlay
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tv
import androidx.compose.ui.graphics.vector.ImageVector
import com.zyroplay.app.model.NavDestination

data class NavItem(
    val destination: NavDestination,
    val icon: ImageVector
)

val mainNavItems = listOf(
    NavItem(NavDestination.Home, Icons.Default.Home),
    NavItem(NavDestination.Live, Icons.Default.LiveTv),
    NavItem(NavDestination.Movies, Icons.Default.Movie),
    NavItem(NavDestination.Series, Icons.Default.Tv),
    NavItem(NavDestination.Search, Icons.Default.Search),
    NavItem(NavDestination.Epg, Icons.Default.CalendarMonth),
    NavItem(NavDestination.CatchUp, Icons.Default.Replay),
    NavItem(NavDestination.Favorites, Icons.Default.Favorite),
    NavItem(NavDestination.Settings, Icons.Default.Settings)
)
