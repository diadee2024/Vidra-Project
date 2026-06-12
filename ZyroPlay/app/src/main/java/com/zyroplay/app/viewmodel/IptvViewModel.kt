package com.zyroplay.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.zyroplay.app.data.IptvContent
import com.zyroplay.app.data.IptvRepository
import com.zyroplay.app.data.MockData
import com.zyroplay.app.data.PreferencesManager
import com.zyroplay.app.data.TmdbClient
import com.zyroplay.app.model.CatchUpProgram
import com.zyroplay.app.model.Channel
import com.zyroplay.app.model.ContentRow
import com.zyroplay.app.model.EpgProgram
import com.zyroplay.app.model.EpisodeItem
import com.zyroplay.app.model.PlayRequest
import com.zyroplay.app.model.PlayerSettings
import com.zyroplay.app.model.PlaylistCredentials
import com.zyroplay.app.model.PlaylistType
import com.zyroplay.app.model.SavedPlaylist
import com.zyroplay.app.model.SearchResults
import com.zyroplay.app.model.SeriesItem
import com.zyroplay.app.model.VodItem
import com.zyroplay.app.model.WatchHistoryItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class IptvUiState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val error: String? = null,
    val statusMessage: String? = null,
    val credentials: PlaylistCredentials? = null,
    val activePlaylist: SavedPlaylist? = null,
    val savedPlaylists: List<SavedPlaylist> = emptyList(),
    val liveChannels: List<Channel> = emptyList(),
    val movies: List<VodItem> = emptyList(),
    val series: List<SeriesItem> = emptyList(),
    val epgPrograms: List<EpgProgram> = emptyList(),
    val catchUpPrograms: List<CatchUpProgram> = emptyList(),
    val favorites: Set<String> = emptySet(),
    val watchHistory: List<WatchHistoryItem> = emptyList(),
    val themeIndex: Int = 0,
    val searchQuery: String = "",
    val selectedSeriesEpisodes: List<EpisodeItem> = emptyList(),
    val selectedSeries: SeriesItem? = null,
    val detailMovie: VodItem? = null,
    val detailSeries: SeriesItem? = null,
    val playerSettings: PlayerSettings = PlayerSettings(),
    val parentalControlEnabled: Boolean = false,
    val parentalUnlocked: Boolean = false,
    val useDemoData: Boolean = false,
    val moviesPage: Int = 0,
    val livePage: Int = 0
) {
    val movieRows: List<ContentRow>
        get() {
            if (movies.isEmpty()) return emptyList()
            return buildList {
                add(ContentRow("Nouveautés", movies.take(10)))
                val rest = movies.drop(10).take(20)
                if (rest.isNotEmpty()) add(ContentRow("Catalogue", rest))
            }
        }

    val featuredMovie: VodItem? get() = movies.firstOrNull()

    val continueWatching: List<WatchHistoryItem>
        get() = watchHistory.filter { !it.isLive && it.positionMs > 0 }

    val favoriteChannels: List<Channel>
        get() = liveChannels.filter { favorites.contains(it.id) }

    val favoriteMovies: List<VodItem>
        get() = movies.filter { favorites.contains(it.id) }

    val favoriteSeries: List<SeriesItem>
        get() = series.filter { favorites.contains(it.id) }

    val liveCategories: List<String>
        get() = listOf("Tous") + liveChannels.map { it.category }.distinct().sorted()

    val paginatedMovies: List<VodItem>
        get() = movies.take((moviesPage + 1) * PAGE_SIZE)

    val paginatedLiveChannels: List<Channel>
        get() = liveChannels.take((livePage + 1) * PAGE_SIZE)

    val searchResults: SearchResults
        get() {
            val q = searchQuery.trim().lowercase()
            if (q.isBlank()) return SearchResults()
            return SearchResults(
                channels = liveChannels.filter {
                    it.name.lowercase().contains(q) || it.category.lowercase().contains(q)
                },
                movies = movies.filter {
                    it.title.lowercase().contains(q) || it.genre.lowercase().contains(q)
                },
                series = series.filter {
                    it.title.lowercase().contains(q) || it.genre.lowercase().contains(q)
                }
            )
        }

    companion object {
        const val PAGE_SIZE = 50
    }
}

class IptvViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = PreferencesManager(application)
    private val repository = IptvRepository()
    private val tmdb = TmdbClient()

    private val _uiState = MutableStateFlow(IptvUiState())
    val uiState: StateFlow<IptvUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            prefs.themeIndexFlow.collect { index ->
                _uiState.update { it.copy(themeIndex = index) }
            }
        }
        viewModelScope.launch {
            prefs.favoritesFlow.collect { fav ->
                _uiState.update { it.copy(favorites = fav) }
            }
        }
        viewModelScope.launch {
            prefs.watchHistoryFlow.collect { history ->
                _uiState.update { it.copy(watchHistory = history) }
            }
        }
        viewModelScope.launch {
            prefs.playerSettingsFlow.collect { settings ->
                _uiState.update { it.copy(playerSettings = settings) }
            }
        }
        viewModelScope.launch {
            prefs.parentalControlEnabledFlow.collect { enabled ->
                _uiState.update { it.copy(parentalControlEnabled = enabled, parentalUnlocked = !enabled) }
            }
        }
        viewModelScope.launch {
            prefs.activePlaylistIdFlow.collect { activeId ->
                _uiState.update { state ->
                    val active = state.savedPlaylists.find { it.id == activeId }
                        ?: state.savedPlaylists.firstOrNull()
                    if (active != null) state.copy(activePlaylist = active) else state
                }
            }
        }
        viewModelScope.launch {
            prefs.playlistsFlow.collect { playlists ->
                _uiState.update { state ->
                    val activeId = state.activePlaylist?.id
                    val active = playlists.find { it.id == activeId } ?: playlists.firstOrNull()
                    state.copy(savedPlaylists = playlists, activePlaylist = active)
                }
            }
        }
        viewModelScope.launch {
            prefs.activeCredentialsFlow.collect { creds ->
                if (creds != null && creds.isValid && !_uiState.value.isLoggedIn && !_uiState.value.isLoading) {
                    connect(creds, silent = true)
                }
            }
        }
    }

    fun login(name: String, credentials: PlaylistCredentials, silent: Boolean = false) {
        viewModelScope.launch {
            val playlistName = name.ifBlank { credentials.displayLabel }
            val playlist = SavedPlaylist(name = playlistName, credentials = credentials)
            prefs.savePlaylist(playlist)
            connect(credentials, silent, playlist)
        }
    }

    fun importLocalM3u(name: String, content: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, statusMessage = "Import M3U...") }
            try {
                val playlist = SavedPlaylist(name = name.ifBlank { "M3U local" }, credentials = PlaylistCredentials(PlaylistType.M3U))
                val localFile = java.io.File(getApplication<Application>().filesDir, "playlist_${playlist.id}.m3u")
                localFile.writeText(content)
                val credentials = PlaylistCredentials(PlaylistType.M3U, m3uUrl = "local://${localFile.absolutePath}")
                val saved = playlist.copy(credentials = credentials)
                prefs.savePlaylist(saved)
                val iptvContent = repository.loadM3uFromContent(content)
                applyContent(iptvContent, credentials, saved)
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun switchPlaylist(playlist: SavedPlaylist) {
        viewModelScope.launch {
            prefs.setActivePlaylist(playlist.id)
            connect(playlist.credentials, silent = false, playlist)
        }
    }

    fun deletePlaylist(id: String) {
        viewModelScope.launch {
            prefs.deletePlaylist(id)
            if (_uiState.value.activePlaylist?.id == id) {
                repository.clear()
                _uiState.update {
                    it.copy(
                        isLoggedIn = false,
                        liveChannels = emptyList(),
                        movies = emptyList(),
                        series = emptyList(),
                        epgPrograms = emptyList(),
                        catchUpPrograms = emptyList(),
                        credentials = null,
                        activePlaylist = null,
                        statusMessage = null
                    )
                }
            }
        }
    }

    private fun connect(
        credentials: PlaylistCredentials,
        silent: Boolean,
        playlist: SavedPlaylist? = _uiState.value.savedPlaylists.find { it.credentials == credentials }
    ) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true, error = null, statusMessage = if (silent) null else "Connexion...")
            }
            try {
                val content = repository.loadAll(credentials)
                val active = playlist ?: SavedPlaylist(name = credentials.displayLabel, credentials = credentials)
                applyContent(content, credentials, active)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Erreur de connexion",
                        isLoggedIn = false
                    )
                }
            }
        }
    }

    fun loadDemoData() {
        val demo = SavedPlaylist(name = "Démo", credentials = PlaylistCredentials(PlaylistType.M3U, m3uUrl = "demo"))
        _uiState.update {
            it.copy(
                isLoggedIn = true,
                isLoading = false,
                useDemoData = true,
                credentials = demo.credentials,
                activePlaylist = demo,
                liveChannels = MockData.channels,
                movies = MockData.movieRows.flatMap { row -> row.items },
                series = MockData.seriesList,
                epgPrograms = emptyList(),
                catchUpPrograms = emptyList(),
                error = null,
                statusMessage = "Mode démo actif"
            )
        }
    }

    fun logout() {
        viewModelScope.launch {
            prefs.clearSession()
            repository.clear()
            _uiState.update {
                IptvUiState(
                    themeIndex = it.themeIndex,
                    savedPlaylists = it.savedPlaylists,
                    favorites = it.favorites,
                    watchHistory = it.watchHistory,
                    playerSettings = it.playerSettings,
                    parentalControlEnabled = it.parentalControlEnabled,
                    parentalUnlocked = it.parentalUnlocked
                )
            }
        }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun setTheme(index: Int) {
        viewModelScope.launch {
            prefs.saveTheme(index)
            _uiState.update { it.copy(themeIndex = index) }
        }
    }

    fun toggleFavorite(id: String) {
        viewModelScope.launch { prefs.toggleFavorite(id) }
    }

    fun loadSeriesEpisodes(series: SeriesItem) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, selectedSeries = series, selectedSeriesEpisodes = emptyList(), detailSeries = null) }
            try {
                val episodes = if (_uiState.value.useDemoData) {
                    listOf(
                        EpisodeItem("d1", "Épisode 1", 1, 1, SAMPLE_HLS),
                        EpisodeItem("d2", "Épisode 2", 1, 2, SAMPLE_HLS)
                    )
                } else {
                    repository.loadSeriesEpisodes(series.id)
                }
                _uiState.update { it.copy(isLoading = false, selectedSeriesEpisodes = episodes) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun openSeriesDetail(series: SeriesItem) {
        viewModelScope.launch {
            val enriched = enrichSeries(series)
            _uiState.update { it.copy(detailSeries = enriched, selectedSeries = null, selectedSeriesEpisodes = emptyList()) }
        }
    }

    fun openMovieDetail(movie: VodItem) {
        viewModelScope.launch {
            val enriched = enrichMovie(movie)
            _uiState.update { it.copy(detailMovie = enriched) }
        }
    }

    fun clearSeriesSelection() {
        _uiState.update { it.copy(selectedSeries = null, selectedSeriesEpisodes = emptyList()) }
    }

    fun clearDetailMovie() {
        _uiState.update { it.copy(detailMovie = null) }
    }

    fun clearDetailSeries() {
        _uiState.update { it.copy(detailSeries = null) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun loadMoreMovies() {
        _uiState.update { it.copy(moviesPage = it.moviesPage + 1) }
    }

    fun loadMoreLiveChannels() {
        _uiState.update { it.copy(livePage = it.livePage + 1) }
    }

    fun saveWatchProgress(
        contentId: String,
        title: String,
        streamUrl: String,
        positionMs: Long,
        durationMs: Long,
        posterUrl: String? = null,
        subtitle: String = "",
        isLive: Boolean = false
    ) {
        if (contentId.isBlank() || isLive) return
        viewModelScope.launch {
            prefs.saveWatchProgress(
                WatchHistoryItem(
                    contentId = contentId,
                    title = title,
                    streamUrl = streamUrl,
                    posterUrl = posterUrl,
                    subtitle = subtitle,
                    positionMs = positionMs,
                    durationMs = durationMs,
                    isLive = isLive
                )
            )
        }
    }

    fun updatePlayerSettings(settings: PlayerSettings) {
        viewModelScope.launch {
            prefs.savePlayerSettings(settings)
            _uiState.update { it.copy(playerSettings = settings) }
        }
    }

    fun setParentalControl(enabled: Boolean, pin: String? = null) {
        viewModelScope.launch {
            if (enabled && !pin.isNullOrBlank()) {
                prefs.saveParentalPin(pin)
            } else if (!enabled) {
                prefs.clearParentalPin()
            }
            prefs.setParentalControlEnabled(enabled)
            _uiState.update { it.copy(parentalControlEnabled = enabled, parentalUnlocked = !enabled) }
        }
    }

    fun verifyParentalPin(pin: String): Boolean {
        val valid = prefs.verifyParentalPin(pin)
        if (valid) _uiState.update { it.copy(parentalUnlocked = true) }
        return valid
    }

    fun saveTmdbApiKey(key: String) {
        prefs.saveTmdbApiKey(key)
    }

    fun getTmdbApiKey(): String = prefs.getTmdbApiKey()

    fun isContentBlocked(category: String): Boolean {
        val state = _uiState.value
        if (!state.parentalControlEnabled || state.parentalUnlocked) return false
        val blocked = listOf("adult", "xxx", "18+", "erotic", "porn")
        return blocked.any { category.lowercase().contains(it) }
    }

    fun buildPlayRequest(channel: Channel): PlayRequest? {
        if (isContentBlocked(channel.category)) {
            _uiState.update { it.copy(error = "Contenu bloqué — saisissez le code PIN") }
            return null
        }
        if (channel.streamUrl.isBlank() && !_uiState.value.useDemoData) return null
        return PlayRequest(
            title = channel.name,
            streamUrl = channel.streamUrl.ifBlank { SAMPLE_HLS },
            subtitle = channel.currentProgram,
            posterUrl = channel.logoUrl,
            contentId = channel.id,
            isLive = true,
            channelList = _uiState.value.liveChannels
        )
    }

    fun buildPlayRequest(movie: VodItem, startPositionMs: Long = 0L): PlayRequest? {
        if (isContentBlocked(movie.genre)) {
            _uiState.update { it.copy(error = "Contenu bloqué — saisissez le code PIN") }
            return null
        }
        if (movie.streamUrl.isBlank() && !_uiState.value.useDemoData) return null
        val historyPos = _uiState.value.watchHistory.find { it.contentId == movie.id }?.positionMs ?: startPositionMs
        return PlayRequest(
            title = movie.title,
            streamUrl = movie.streamUrl.ifBlank { SAMPLE_HLS },
            subtitle = "${movie.year} • ${movie.genre}",
            posterUrl = movie.posterUrl,
            contentId = movie.id,
            startPositionMs = historyPos
        )
    }

    fun buildPlayRequest(episode: EpisodeItem, seriesTitle: String): PlayRequest? {
        if (episode.streamUrl.isBlank() && !_uiState.value.useDemoData) return null
        val contentId = episode.id
        val historyPos = _uiState.value.watchHistory.find { it.contentId == contentId }?.positionMs ?: 0L
        return PlayRequest(
            title = "$seriesTitle — ${episode.title}",
            streamUrl = episode.streamUrl.ifBlank { SAMPLE_HLS },
            subtitle = "S${episode.seasonNumber} E${episode.episodeNumber}",
            posterUrl = episode.posterUrl,
            contentId = contentId,
            startPositionMs = historyPos
        )
    }

    fun buildPlayRequest(catchUp: CatchUpProgram): PlayRequest {
        return PlayRequest(
            title = "${catchUp.channelName} — ${catchUp.title}",
            streamUrl = catchUp.streamUrl,
            subtitle = "Replay",
            contentId = catchUp.id
        )
    }

    fun buildPlayRequestFromHistory(item: WatchHistoryItem): PlayRequest {
        return PlayRequest(
            title = item.title,
            streamUrl = item.streamUrl,
            subtitle = item.subtitle,
            posterUrl = item.posterUrl,
            contentId = item.contentId,
            isLive = item.isLive,
            startPositionMs = item.positionMs
        )
    }

    fun buildPlayRequestFromEpg(channel: Channel, program: EpgProgram): PlayRequest? {
        val catchUp = _uiState.value.catchUpPrograms.find { it.id == program.id }
        if (catchUp != null) return buildPlayRequest(catchUp)
        return buildPlayRequest(channel)
    }

    private fun applyContent(content: IptvContent, credentials: PlaylistCredentials, playlist: SavedPlaylist) {
        val catchUp = if (credentials.type == PlaylistType.XTREAM) {
            repository.buildCatchUpPrograms(content.liveChannels, content.epgPrograms)
        } else emptyList()
        _uiState.update {
            it.copy(
                isLoading = false,
                isLoggedIn = true,
                useDemoData = false,
                credentials = credentials,
                activePlaylist = playlist,
                liveChannels = content.liveChannels,
                movies = content.movies,
                series = content.series,
                epgPrograms = content.epgPrograms,
                catchUpPrograms = catchUp,
                moviesPage = 0,
                livePage = 0,
                error = null,
                statusMessage = "${playlist.name} — ${content.liveChannels.size} chaînes • ${content.movies.size} films"
            )
        }
    }

    private suspend fun enrichMovie(movie: VodItem): VodItem {
        if (!movie.posterUrl.isNullOrBlank() && movie.description.isNotBlank()) return movie
        val apiKey = prefs.getTmdbApiKey()
        val info = tmdb.searchMovie(apiKey, movie.title, movie.year) ?: return movie
        return movie.copy(
            posterUrl = movie.posterUrl ?: info.posterUrl,
            description = movie.description.ifBlank { info.overview ?: "" },
            rating = movie.rating.ifBlank { info.rating ?: "" }
        )
    }

    private suspend fun enrichSeries(series: SeriesItem): SeriesItem {
        if (!series.posterUrl.isNullOrBlank() && series.description.isNotBlank()) return series
        val apiKey = prefs.getTmdbApiKey()
        val info = tmdb.searchTv(apiKey, series.title) ?: return series
        return series.copy(
            posterUrl = series.posterUrl ?: info.posterUrl,
            description = series.description.ifBlank { info.overview ?: "" },
            rating = series.rating.ifBlank { info.rating ?: "" }
        )
    }

    companion object {
        const val SAMPLE_HLS = "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8"
    }
}
