package com.zyroplay.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.zyroplay.app.data.IptvContent
import com.zyroplay.app.data.IptvRepository
import com.zyroplay.app.data.MockData
import com.zyroplay.app.data.PreferencesManager
import com.zyroplay.app.model.Channel
import com.zyroplay.app.model.ContentRow
import com.zyroplay.app.model.EpgProgram
import com.zyroplay.app.model.EpisodeItem
import com.zyroplay.app.model.PlayRequest
import com.zyroplay.app.model.PlaylistCredentials
import com.zyroplay.app.model.PlaylistType
import com.zyroplay.app.model.SavedPlaylist
import com.zyroplay.app.model.SearchResults
import com.zyroplay.app.model.SeriesItem
import com.zyroplay.app.model.VodItem
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
    val favorites: Set<String> = emptySet(),
    val themeIndex: Int = 0,
    val searchQuery: String = "",
    val selectedSeriesEpisodes: List<EpisodeItem> = emptyList(),
    val selectedSeries: SeriesItem? = null,
    val useDemoData: Boolean = false
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

    val favoriteChannels: List<Channel>
        get() = liveChannels.filter { favorites.contains(it.id) }

    val favoriteMovies: List<VodItem>
        get() = movies.filter { favorites.contains(it.id) }

    val liveCategories: List<String>
        get() = listOf("Tous") + liveChannels.map { it.category }.distinct().sorted()

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
}

class IptvViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = PreferencesManager(application)
    private val repository = IptvRepository()

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
                    favorites = it.favorites
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
            _uiState.update { it.copy(isLoading = true, selectedSeries = series, selectedSeriesEpisodes = emptyList()) }
            try {
                val episodes = if (_uiState.value.useDemoData) {
                    listOf(
                        EpisodeItem("d1", "Épisode 1", 1, 1, ""),
                        EpisodeItem("d2", "Épisode 2", 1, 2, "")
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

    fun clearSeriesSelection() {
        _uiState.update { it.copy(selectedSeries = null, selectedSeriesEpisodes = emptyList()) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun buildPlayRequest(channel: Channel): PlayRequest? {
        if (channel.streamUrl.isBlank() && !_uiState.value.useDemoData) return null
        return PlayRequest(
            channel.name,
            channel.streamUrl.ifBlank { SAMPLE_HLS },
            channel.currentProgram,
            channel.logoUrl
        )
    }

    fun buildPlayRequest(movie: VodItem): PlayRequest? {
        if (movie.streamUrl.isBlank() && !_uiState.value.useDemoData) return null
        return PlayRequest(
            movie.title,
            movie.streamUrl.ifBlank { SAMPLE_HLS },
            "${movie.year} • ${movie.genre}",
            movie.posterUrl
        )
    }

    fun buildPlayRequest(episode: EpisodeItem, seriesTitle: String): PlayRequest? {
        if (episode.streamUrl.isBlank() && !_uiState.value.useDemoData) return null
        return PlayRequest(
            "$seriesTitle — ${episode.title}",
            episode.streamUrl.ifBlank { SAMPLE_HLS },
            "S${episode.seasonNumber} E${episode.episodeNumber}",
            episode.posterUrl
        )
    }

    private fun applyContent(content: IptvContent, credentials: PlaylistCredentials, playlist: SavedPlaylist) {
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
                error = null,
                statusMessage = "${playlist.name} — ${content.liveChannels.size} chaînes • ${content.movies.size} films"
            )
        }
    }

    companion object {
        const val SAMPLE_HLS = "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8"
    }
}
