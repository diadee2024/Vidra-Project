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
    val liveChannels: List<Channel> = emptyList(),
    val movies: List<VodItem> = emptyList(),
    val series: List<SeriesItem> = emptyList(),
    val epgPrograms: List<EpgProgram> = emptyList(),
    val favorites: Set<String> = emptySet(),
    val themeIndex: Int = 0,
    val selectedSeriesEpisodes: List<EpisodeItem> = emptyList(),
    val selectedSeries: SeriesItem? = null,
    val useDemoData: Boolean = false
) {
    val movieRows: List<ContentRow>
        get() {
            if (movies.isEmpty()) return emptyList()
            val recent = movies.take(10)
            val rest = movies.drop(10).take(20)
            return buildList {
                add(ContentRow("Nouveautés", recent))
                if (rest.isNotEmpty()) add(ContentRow("Catalogue", rest))
            }
        }

    val featuredMovie: VodItem?
        get() = movies.firstOrNull()

    val favoriteChannels: List<Channel>
        get() = liveChannels.filter { favorites.contains(it.id) }

    val favoriteMovies: List<VodItem>
        get() = movies.filter { favorites.contains(it.id) }

    val liveCategories: List<String>
        get() = listOf("Tous") + liveChannels.map { it.category }.distinct().sorted()
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
            prefs.credentialsFlow.collect { creds ->
                if (creds != null && creds.isValid) {
                    login(creds, silent = true)
                }
            }
        }
    }

    fun login(credentials: PlaylistCredentials, silent: Boolean = false) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, statusMessage = if (silent) null else "Connexion...") }
            try {
                prefs.saveCredentials(credentials)
                val content = repository.loadAll(credentials)
                applyContent(content, credentials)
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
        val demoCreds = PlaylistCredentials(PlaylistType.M3U, m3uUrl = "demo")
        _uiState.update {
            it.copy(
                isLoggedIn = true,
                isLoading = false,
                useDemoData = true,
                credentials = demoCreds,
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
            prefs.clearCredentials()
            repository.clear()
            _uiState.value = IptvUiState(themeIndex = _uiState.value.themeIndex)
        }
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
        val url = channel.streamUrl.ifBlank { SAMPLE_HLS }
        return PlayRequest(channel.name, url, channel.currentProgram)
    }

    fun buildPlayRequest(movie: VodItem): PlayRequest? {
        if (movie.streamUrl.isBlank() && !_uiState.value.useDemoData) return null
        val url = movie.streamUrl.ifBlank { SAMPLE_HLS }
        return PlayRequest(movie.title, url, "${movie.year} • ${movie.genre}")
    }

    fun buildPlayRequest(episode: EpisodeItem, seriesTitle: String): PlayRequest? {
        if (episode.streamUrl.isBlank() && !_uiState.value.useDemoData) return null
        val url = episode.streamUrl.ifBlank { SAMPLE_HLS }
        return PlayRequest("$seriesTitle — ${episode.title}", url, "S${episode.seasonNumber} E${episode.episodeNumber}")
    }

    private fun applyContent(content: IptvContent, credentials: PlaylistCredentials) {
        _uiState.update {
            it.copy(
                isLoading = false,
                isLoggedIn = true,
                useDemoData = false,
                credentials = credentials,
                liveChannels = content.liveChannels,
                movies = content.movies,
                series = content.series,
                epgPrograms = content.epgPrograms,
                error = null,
                statusMessage = "${content.liveChannels.size} chaînes • ${content.movies.size} films • ${content.series.size} séries"
            )
        }
    }

    companion object {
        const val SAMPLE_HLS = "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8"
    }
}
