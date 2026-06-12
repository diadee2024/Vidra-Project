package com.zyroplay.app.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zyroplay.app.model.NavDestination
import com.zyroplay.app.model.PlayRequest
import com.zyroplay.app.ui.components.ZyroGradientBackground
import com.zyroplay.app.ui.components.ZyroSidebar
import com.zyroplay.app.ui.screens.CatchUpScreen
import com.zyroplay.app.ui.screens.EpgScreen
import com.zyroplay.app.ui.screens.FavoritesScreen
import com.zyroplay.app.ui.screens.HomeScreen
import com.zyroplay.app.ui.screens.LiveScreen
import com.zyroplay.app.ui.screens.LoginScreen
import com.zyroplay.app.ui.screens.MoviesScreen
import com.zyroplay.app.ui.screens.PlayerScreen
import com.zyroplay.app.ui.screens.SeriesScreen
import com.zyroplay.app.ui.screens.SettingsScreen
import com.zyroplay.app.ui.screens.SplashScreen
import com.zyroplay.app.ui.theme.LocalZyroTheme
import com.zyroplay.app.viewmodel.IptvViewModel

private enum class AppState { Splash, Login, Main, Player }

@Composable
fun ZyroPlayApp(viewModel: IptvViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    var appState by remember { mutableStateOf(AppState.Splash) }
    var currentRoute by remember { mutableStateOf(NavDestination.Home.route) }
    var playRequest by remember { mutableStateOf<PlayRequest?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val theme = LocalZyroTheme.current

    LaunchedEffect(uiState.isLoggedIn) {
        if (uiState.isLoggedIn && appState == AppState.Login) {
            appState = AppState.Main
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    when (appState) {
        AppState.Splash -> SplashScreen(
            onFinished = {
                appState = if (uiState.isLoggedIn) AppState.Main else AppState.Login
            }
        )
        AppState.Login -> LoginScreen(
            isLoading = uiState.isLoading,
            onLogin = viewModel::login,
            onDemoMode = {
                viewModel.loadDemoData()
                appState = AppState.Main
            }
        )
        AppState.Player -> playRequest?.let { request ->
            PlayerScreen(
                playRequest = request,
                isLoading = uiState.isLoading,
                onBack = { appState = AppState.Main }
            )
        }
        AppState.Main -> {
            Box(modifier = Modifier.fillMaxSize()) {
                ZyroGradientBackground()
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = theme.primary
                    )
                }
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
                            NavDestination.Home.route -> HomeScreen(
                                featured = uiState.featuredMovie,
                                movieRows = uiState.movieRows,
                                statusMessage = uiState.statusMessage,
                                onPlay = { movie ->
                                    viewModel.buildPlayRequest(movie)?.let {
                                        playRequest = it
                                        appState = AppState.Player
                                    }
                                }
                            )
                            NavDestination.Live.route -> LiveScreen(
                                channels = uiState.liveChannels,
                                categories = uiState.liveCategories,
                                favorites = uiState.favorites,
                                onChannelClick = { channel ->
                                    viewModel.buildPlayRequest(channel)?.let {
                                        playRequest = it
                                        appState = AppState.Player
                                    }
                                },
                                onToggleFavorite = viewModel::toggleFavorite
                            )
                            NavDestination.Movies.route -> MoviesScreen(
                                movies = uiState.movies,
                                favorites = uiState.favorites,
                                onMovieClick = { movie ->
                                    viewModel.buildPlayRequest(movie)?.let {
                                        playRequest = it
                                        appState = AppState.Player
                                    }
                                },
                                onToggleFavorite = viewModel::toggleFavorite
                            )
                            NavDestination.Series.route -> SeriesScreen(
                                series = uiState.series,
                                selectedSeries = uiState.selectedSeries,
                                episodes = uiState.selectedSeriesEpisodes,
                                onSeriesClick = viewModel::loadSeriesEpisodes,
                                onEpisodeClick = { episode ->
                                    val series = uiState.selectedSeries ?: return@SeriesScreen
                                    viewModel.buildPlayRequest(episode, series.title)?.let {
                                        playRequest = it
                                        appState = AppState.Player
                                    }
                                },
                                onBackFromEpisodes = viewModel::clearSeriesSelection
                            )
                            NavDestination.Epg.route -> EpgScreen(
                                channels = uiState.liveChannels,
                                programs = uiState.epgPrograms
                            )
                            NavDestination.CatchUp.route -> CatchUpScreen(
                                programs = uiState.epgPrograms.filter {
                                    it.endTime < System.currentTimeMillis()
                                }.map {
                                    com.zyroplay.app.model.VodItem(
                                        id = it.id,
                                        title = it.title,
                                        year = "",
                                        rating = "",
                                        genre = "Replay",
                                        duration = "",
                                        description = it.description
                                    )
                                }.ifEmpty { com.zyroplay.app.data.MockData.catchUpPrograms },
                                onProgramClick = { item ->
                                    viewModel.buildPlayRequest(item)?.let {
                                        playRequest = it
                                        appState = AppState.Player
                                    } ?: run {
                                        playRequest = PlayRequest(item.title, IptvViewModel.SAMPLE_HLS, item.genre)
                                        appState = AppState.Player
                                    }
                                }
                            )
                            NavDestination.Favorites.route -> FavoritesScreen(
                                channels = uiState.favoriteChannels,
                                movies = uiState.favoriteMovies,
                                onChannelClick = { channel ->
                                    viewModel.buildPlayRequest(channel)?.let {
                                        playRequest = it
                                        appState = AppState.Player
                                    }
                                },
                                onMovieClick = { movie ->
                                    viewModel.buildPlayRequest(movie)?.let {
                                        playRequest = it
                                        appState = AppState.Player
                                    }
                                }
                            )
                            NavDestination.Settings.route -> SettingsScreen(
                                currentThemeIndex = uiState.themeIndex,
                                onThemeSelected = viewModel::setTheme,
                                onLogout = {
                                    viewModel.logout()
                                    appState = AppState.Login
                                }
                            )
                        }
                    }
                }
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
    }
}
