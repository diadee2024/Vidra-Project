package com.zyroplay.app.data

import com.zyroplay.app.model.Channel
import com.zyroplay.app.model.EpgProgram
import com.zyroplay.app.model.EpisodeItem
import com.zyroplay.app.model.PlaylistCredentials
import com.zyroplay.app.model.PlaylistType
import com.zyroplay.app.model.SeriesItem
import com.zyroplay.app.model.VodItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

class IptvRepository(
    private val xtreamApi: XtreamApi = XtreamApi(),
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(90, TimeUnit.SECONDS)
        .build()
) {
    private var xtreamAuth: XtreamApi.XtreamAuth? = null

    fun clear() {
        xtreamAuth = null
    }

    suspend fun loadAll(credentials: PlaylistCredentials): IptvContent = withContext(Dispatchers.IO) {
        when (credentials.type) {
            PlaylistType.XTREAM -> loadXtream(credentials)
            PlaylistType.M3U -> loadM3u(credentials)
        }
    }

    suspend fun loadSeriesEpisodes(seriesId: String): List<EpisodeItem> = withContext(Dispatchers.IO) {
        val auth = xtreamAuth ?: return@withContext emptyList()
        xtreamApi.getSeriesEpisodes(auth, seriesId)
    }

    private fun loadXtream(credentials: PlaylistCredentials): IptvContent {
        val auth = xtreamApi.authenticate(credentials)
        xtreamAuth = auth
        val live = xtreamApi.getLiveStreams(auth)
        val movies = xtreamApi.getVodStreams(auth)
        val series = xtreamApi.getSeries(auth)
        val epg = try {
            xtreamApi.getXmltvEpg(auth).ifEmpty {
                live.take(30).flatMap { ch ->
                    xtreamApi.getShortEpg(auth, ch.id, 10).map { it.copy(channelId = ch.id) }
                }
            }
        } catch (_: Exception) {
            live.take(20).flatMap { ch ->
                try {
                    xtreamApi.getShortEpg(auth, ch.id, 8).map { it.copy(channelId = ch.id) }
                } catch (_: Exception) {
                    emptyList()
                }
            }
        }
        val liveWithEpg = attachCurrentPrograms(live, epg)
        return IptvContent(liveWithEpg, movies, series, epg)
    }

    private fun loadM3u(credentials: PlaylistCredentials): IptvContent {
        xtreamAuth = null
        val content = fetchUrl(credentials.m3uUrl)
        val live = M3uParser.parseChannels(content)
        val movies = M3uParser.parseVodFromM3u(content)
        return IptvContent(live, movies, emptyList(), emptyList())
    }

    private fun fetchUrl(url: String): String {
        val request = Request.Builder().url(url).get().build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IllegalStateException("Impossible de charger la playlist (${response.code})")
            return response.body?.string() ?: throw IllegalStateException("Playlist vide")
        }
    }

    private fun attachCurrentPrograms(channels: List<Channel>, epg: List<EpgProgram>): List<Channel> {
        val now = System.currentTimeMillis()
        return channels.map { channel ->
            val current = epg.filter { it.channelId == channel.id || it.channelId == channel.epgChannelId }
                .find { it.startTime <= now && it.endTime > now }
            channel.copy(currentProgram = current?.title ?: channel.currentProgram)
        }
    }
}

data class IptvContent(
    val liveChannels: List<Channel>,
    val movies: List<VodItem>,
    val series: List<SeriesItem>,
    val epgPrograms: List<EpgProgram>
)
