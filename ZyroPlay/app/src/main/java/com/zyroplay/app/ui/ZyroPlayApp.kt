package com.zyroplay.app.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.zyroplay.app.model.NavDestination
import com.zyroplay.app.ui.components.ZyroGradientBackground
import com.zyroplay.app.ui.components.ZyroSidebar
import com.zyroplay.app.ui.screens.CatchUpScreen
import com.zyroplay.app.ui.screens.FavoritesScreen
import com.zyroplay.app.ui.screens.HomeScreen
import com.zyroplay.app.ui.screens.LiveScreen
import com.zyroplay.app.ui.screens.LoginScreen
import com.zyroplay.app.ui.screens.MoviesScreen
import com.zyroplay.app.ui.screens.PlayerScreen
import com.zyroplay.app.ui.screens.SeriesScreen
import com.zyroplay.app.ui.screens.SettingsScreen
import com.zyroplay.app.ui.screens.SplashScreen

private enum class AppState { Splash, Login, Main, Player }

@Composable
fun ZyroPlayApp() {
    var appState by remember { mutableStateOf(AppState.Splash) }
    var currentRoute by remember { mutableStateOf(NavDestination.Home.route) }
    var playerTitle by remember { mutableStateOf("") }

    val openPlayer: (String) -> Unit = { title ->
        playerTitle = title
        appState = AppState.Player
    }

    when (appState) {
        AppState.Splash -> SplashScreen(onFinished = { appState = AppState.Login })
        AppState.Login -> LoginScreen(onLoginSuccess = { appState = AppState.Main })
        AppState.Player -> PlayerScreen(
            title = playerTitle,
            onBack = { appState = AppState.Main }
        )
        AppState.Main -> {
            Box(modifier = Modifier.fillMaxSize()) {
                ZyroGradientBackground()
                Row(modifier = Modifier.fillMaxSize()) {
                    ZyroSidebar(
                        currentRoute = currentRoute,
                        onNavigate = { dest -> currentRoute = dest.route }
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        when (currentRoute) {
                            NavDestination.Home.route -> HomeScreen(onPlayFeatured = { openPlayer("Dune: Part Two") })
                            NavDestination.Live.route -> LiveScreen(onChannelClick = { openPlayer(it.name) })
                            NavDestination.Movies.route -> MoviesScreen(onMovieClick = { openPlayer(it.title) })
                            NavDestination.Series.route -> SeriesScreen(onSeriesClick = { openPlayer(it.title) })
                            NavDestination.CatchUp.route -> CatchUpScreen(onProgramClick = { openPlayer(it.title) })
                            NavDestination.Favorites.route -> FavoritesScreen(onItemClick = { openPlayer(it.title) })
                            NavDestination.Settings.route -> SettingsScreen(onLogout = { appState = AppState.Login })
                        }
                    }
                }
            }
        }
    }
}
