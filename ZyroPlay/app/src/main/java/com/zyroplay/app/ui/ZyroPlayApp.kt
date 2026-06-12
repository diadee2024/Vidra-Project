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
import com.zyroplay.app.ui.screens.ParentalPinDialog
import com.zyroplay.app.ui.screens.PlayerScreen
import com.zyroplay.app.ui.screens.SearchScreen
import com.zyroplay.app.ui.screens.SeriesDetailScreen
import com.zyroplay.app.ui.screens.SeriesScreen
import com.zyroplay.app.ui.screens.SettingsScreen
import com.zyroplay.app.ui.screens.SplashScreen
import com.zyroplay.app.ui.screens.VodDetailScreen
import com.zyroplay.app.ui.theme.LocalZyroTheme
import com.zyroplay.app.viewmodel.IptvViewModel

private enum class AppState { Splash, Login, Main, Player }

@Composable
fun ZyroPlayApp(
    viewModel: IptvViewModel = viewModel(),
    onEnterPiP: (() -> Unit)? = null
) {
    val uiState by viewModel.uiState.collectAsState()
    var appState by remember { mutableStateOf(AppState.Splash) }
    var currentRoute by remember { mutableStateOf(NavDestination.Home.route) }
    var playRequest by remember { mutableStateOf<PlayRequest?>(null) }
    var showPinDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val theme = LocalZyroTheme.current

    val openPlayer: (PlayRequest) -> Unit = { request ->
        playRequest = request
        appState = AppState.Player
    }

    LaunchedEffect(uiState.isLoggedIn) {
        if (uiState.isLoggedIn && appState == AppState.Login) appState = AppState.Main
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    LaunchedEffect(uiState.parentalControlEnabled, uiState.parentalUnlocked) {
        if (uiState.parentalControlEnabled && !uiState.parentalUnlocked && appState == AppState.Main) {
            showPinDialog = true
        }
    }

    if (showPinDialog) {
        ParentalPinDialog(
            title = "Déverrouiller ZyroPlay",
            onDismiss = { showPinDialog = false },
            onConfirm = { pin -> viewModel.verifyParentalPin(pin) }
        )
    }

    when (appState) {
        AppState.Splash -> SplashScreen(
            onFinished = { appState = if (uiState.isLoggedIn) AppState.Main else AppState.Login }
        )
        AppState.Login -> LoginScreen(
            isLoading = uiState.isLoading,
            savedPlaylists = uiState.savedPlaylists,
            onLogin = viewModel::login,
            onQuickConnect = viewModel::switchPlaylist,
            onImportM3u = viewModel::importLocalM3u,
            onDemoMode = {
                viewModel.loadDemoData()
                appState = AppState.Main
            }
        )
        AppState.Player -> playRequest?.let { request ->
            PlayerScreen(
                playRequest = request,
                playerSettings = uiState.playerSettings,
                onBack = { appState = AppState.Main },
                onZap = { openPlayer(it) },
                onEnterPiP = onEnterPiP,
                onProgressUpdate = { id, pos, dur ->
                    viewModel.saveWatchProgress(
                        contentId = id,
                        title = request.title,
                        streamUrl = request.streamUrl,
                        positionMs = pos,
                        durationMs = dur,
                        posterUrl = request.posterUrl,
                        subtitle = request.subtitle,
                        isLive = request.isLive
                    )
                }
            )
        }
        AppState.Main -> {
            Box(modifier = Modifier.fillMaxSize()) {
                ZyroGradientBackground()
                if (uiState.isLoading) {
                    CircularProgressIndicator(Modifier.align(Alignment.Center), color = theme.primary)
                }
                Row(Modifier.fillMaxSize()) {
                    ZyroSidebar(currentRoute = currentRoute, onNavigate = { currentRoute = it.route })
                    Box(Modifier.weight(1f).fillMaxHeight()) {
                        when (currentRoute) {
                            NavDestination.Home.route -> HomeScreen(
                                featured = uiState.featuredMovie,
                                movieRows = uiState.movieRows,
                                continueWatching = uiState.continueWatching,
                                statusMessage = uiState.statusMessage,
                                onPlay = { viewModel.buildPlayRequest(it)?.let(openPlayer) },
                                onMovieDetail = viewModel::openMovieDetail,
                                onResume = { viewModel.buildPlayRequestFromHistory(it)?.let(openPlayer) },
                                onOpenSearch = { currentRoute = NavDestination.Search.route }
                            )
                            NavDestination.Live.route -> LiveScreen(
                                channels = uiState.liveChannels,
                                categories = uiState.liveCategories,
                                favorites = uiState.favorites,
                                onChannelClick = { viewModel.buildPlayRequest(it)?.let(openPlayer) },
                                onToggleFavorite = viewModel::toggleFavorite
                            )
                            NavDestination.Movies.route -> MoviesScreen(
                                movies = uiState.movies,
                                favorites = uiState.favorites,
                                onMovieClick = viewModel::openMovieDetail,
                                onToggleFavorite = viewModel::toggleFavorite
                            )
                            NavDestination.Series.route -> SeriesScreen(
                                series = uiState.series,
                                selectedSeries = uiState.selectedSeries,
                                episodes = uiState.selectedSeriesEpisodes,
                                favorites = uiState.favorites,
                                onSeriesClick = viewModel::openSeriesDetail,
                                onEpisodeClick = { ep ->
                                    uiState.selectedSeries?.let { s ->
                                        viewModel.buildPlayRequest(ep, s.title)?.let(openPlayer)
                                    }
                                },
                                onBackFromEpisodes = viewModel::clearSeriesSelection,
                                onToggleFavorite = viewModel::toggleFavorite
                            )
                            NavDestination.Search.route -> SearchScreen(
                                query = uiState.searchQuery,
                                results = uiState.searchResults,
                                favorites = uiState.favorites,
                                onQueryChange = viewModel::setSearchQuery,
                                onChannelClick = { viewModel.buildPlayRequest(it)?.let(openPlayer) },
                                onMovieClick = viewModel::openMovieDetail,
                                onSeriesClick = { series ->
                                    viewModel.openSeriesDetail(series)
                                    currentRoute = NavDestination.Series.route
                                },
                                onToggleFavorite = viewModel::toggleFavorite
                            )
                            NavDestination.Epg.route -> EpgScreen(
                                channels = uiState.liveChannels,
                                programs = uiState.epgPrograms,
                                onProgramClick = { channel, program ->
                                    viewModel.buildPlayRequestFromEpg(channel, program)?.let(openPlayer)
                                }
                            )
                            NavDestination.CatchUp.route -> CatchUpScreen(
                                programs = uiState.catchUpPrograms,
                                onProgramClick = { viewModel.buildPlayRequest(it)?.let(openPlayer) }
                            )
                            NavDestination.Favorites.route -> FavoritesScreen(
                                channels = uiState.favoriteChannels,
                                movies = uiState.favoriteMovies,
                                series = uiState.favoriteSeries,
                                onChannelClick = { viewModel.buildPlayRequest(it)?.let(openPlayer) },
                                onMovieClick = viewModel::openMovieDetail,
                                onSeriesClick = viewModel::openSeriesDetail,
                                onToggleFavorite = viewModel::toggleFavorite
                            )
                            NavDestination.Settings.route -> SettingsScreen(
                                currentThemeIndex = uiState.themeIndex,
                                savedPlaylists = uiState.savedPlaylists,
                                activePlaylistId = uiState.activePlaylist?.id,
                                playerSettings = uiState.playerSettings,
                                parentalControlEnabled = uiState.parentalControlEnabled,
                                tmdbApiKey = viewModel.getTmdbApiKey(),
                                onThemeSelected = viewModel::setTheme,
                                onSelectPlaylist = viewModel::switchPlaylist,
                                onDeletePlaylist = viewModel::deletePlaylist,
                                onAddPlaylist = {
                                    viewModel.logout()
                                    appState = AppState.Login
                                },
                                onLogout = {
                                    viewModel.logout()
                                    appState = AppState.Login
                                },
                                onSavePlayerSettings = viewModel::updatePlayerSettings,
                                onParentalControlChange = viewModel::setParentalControl,
                                onTmdbKeyChange = viewModel::saveTmdbApiKey
                            )
                        }
                    }
                }

                uiState.detailMovie?.let { movie ->
                    val resumePos = uiState.watchHistory.find { it.contentId == movie.id }?.positionMs ?: 0L
                    VodDetailScreen(
                        movie = movie,
                        isFavorite = uiState.favorites.contains(movie.id),
                        resumePositionMs = resumePos,
                        onBack = viewModel::clearDetailMovie,
                        onPlay = { pos -> viewModel.buildPlayRequest(movie, pos)?.let(openPlayer) },
                        onToggleFavorite = { viewModel.toggleFavorite(movie.id) }
                    )
                }

                uiState.detailSeries?.let { series ->
                    SeriesDetailScreen(
                        series = series,
                        isFavorite = uiState.favorites.contains(series.id),
                        onBack = viewModel::clearDetailSeries,
                        onViewEpisodes = {
                            viewModel.loadSeriesEpisodes(series)
                            viewModel.clearDetailSeries()
                        },
                        onToggleFavorite = { viewModel.toggleFavorite(series.id) }
                    )
                }

                SnackbarHost(snackbarHostState, Modifier.align(Alignment.BottomCenter))
            }
        }
    }
}
