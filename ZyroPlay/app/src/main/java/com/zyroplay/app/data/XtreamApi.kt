package com.zyroplay.app.data

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.zyroplay.app.model.Channel
import com.zyroplay.app.model.EpgProgram
import com.zyroplay.app.model.EpisodeItem
import com.zyroplay.app.model.PlaylistCredentials
import com.zyroplay.app.model.SeriesItem
import com.zyroplay.app.model.VodItem
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

class XtreamApi(
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build(),
    private val gson: Gson = Gson()
) {

    data class XtreamAuth(
        val serverBase: String,
        val username: String,
        val password: String
    )

    fun authenticate(credentials: PlaylistCredentials): XtreamAuth {
        val base = normalizeServer(credentials.serverUrl)
        val url = "$base/player_api.php?username=${credentials.username}&password=${credentials.password}"
        val body = get(url)
        val json = gson.fromJson(body, JsonObject::class.java)
        if (!json.has("user_info")) {
            throw IllegalStateException("Identifiants Xtream invalides")
        }
        val userInfo = json.getAsJsonObject("user_info")
        if (userInfo.has("auth") && userInfo.get("auth").asInt == 0) {
            throw IllegalStateException("Authentification refusée par le serveur")
        }
        return XtreamAuth(base, credentials.username, credentials.password)
    }

    fun getLiveStreams(auth: XtreamAuth): List<Channel> {
        val categories = fetchMap(auth, "get_live_categories")
        val streams = fetchArray(auth, "get_live_streams")
        return streams.mapNotNull { item ->
            val obj = item.asJsonObject
            val streamId = obj.string("stream_id") ?: return@mapNotNull null
            val catId = obj.string("category_id") ?: ""
            Channel(
                id = streamId,
                name = obj.string("name") ?: "Chaîne",
                category = categories[catId] ?: "Général",
                logoUrl = obj.string("stream_icon"),
                streamUrl = buildLiveUrl(auth, streamId, obj.string("container_extension") ?: "ts"),
                epgChannelId = obj.string("epg_channel_id") ?: obj.string("name"),
                currentProgram = categories[catId] ?: ""
            )
        }
    }

    fun getVodStreams(auth: XtreamAuth): List<VodItem> {
        val categories = fetchMap(auth, "get_vod_categories")
        val streams = fetchArray(auth, "get_vod_streams")
        return streams.mapNotNull { item ->
            val obj = item.asJsonObject
            val streamId = obj.string("stream_id") ?: return@mapNotNull null
            val catId = obj.string("category_id") ?: ""
            val ext = obj.string("container_extension") ?: "mp4"
            VodItem(
                id = streamId,
                title = obj.string("name") ?: "Film",
                year = obj.string("year") ?: "",
                rating = obj.string("rating") ?: obj.string("rating_5based") ?: "",
                genre = categories[catId] ?: obj.string("genre") ?: "",
                posterUrl = obj.string("stream_icon"),
                streamUrl = buildVodUrl(auth, streamId, ext),
                containerExtension = ext,
                duration = formatDuration(obj.string("duration") ?: ""),
                description = obj.string("plot") ?: ""
            )
        }
    }

    fun getSeries(auth: XtreamAuth): List<SeriesItem> {
        val categories = fetchMap(auth, "get_series_categories")
        val streams = fetchArray(auth, "get_series")
        return streams.mapNotNull { item ->
            val obj = item.asJsonObject
            val seriesId = obj.string("series_id") ?: return@mapNotNull null
            val catId = obj.string("category_id") ?: ""
            SeriesItem(
                id = seriesId,
                title = obj.string("name") ?: "Série",
                seasons = obj.string("episode_run_time")?.toIntOrNull() ?: 1,
                rating = obj.string("rating") ?: "",
                genre = categories[catId] ?: obj.string("genre") ?: "",
                posterUrl = obj.string("cover"),
                episodes = 0,
                description = obj.string("plot") ?: ""
            )
        }
    }

    fun getSeriesEpisodes(auth: XtreamAuth, seriesId: String): List<EpisodeItem> {
        val url = apiUrl(auth, "get_series_info&series_id=$seriesId")
        val body = get(url)
        val json = gson.fromJson(body, JsonObject::class.java)
        val episodes = json.getAsJsonObject("episodes") ?: return emptyList()
        val result = mutableListOf<EpisodeItem>()
        episodes.entrySet().forEach { (seasonKey, seasonValue) ->
            val seasonNum = seasonKey.toIntOrNull() ?: 1
            seasonValue.asJsonArray.forEach { epEl ->
                val ep = epEl.asJsonObject
                val id = ep.string("id") ?: return@forEach
                val ext = ep.string("container_extension") ?: "mp4"
                result += EpisodeItem(
                    id = id,
                    title = ep.string("title") ?: "Épisode ${ep.string("episode_num") ?: ""}",
                    seasonNumber = seasonNum,
                    episodeNumber = ep.string("episode_num")?.toIntOrNull() ?: 1,
                    streamUrl = buildSeriesUrl(auth, id, ext),
                    posterUrl = ep.string("info")?.let { null },
                    duration = ep.string("duration") ?: ""
                )
            }
        }
        return result.sortedWith(compareBy({ it.seasonNumber }, { it.episodeNumber }))
    }

    fun getShortEpg(auth: XtreamAuth, streamId: String, limit: Int = 20): List<EpgProgram> {
        val url = apiUrl(auth, "get_short_epg&stream_id=$streamId&limit=$limit")
        val body = get(url)
        val json = gson.fromJson(body, JsonObject::class.java)
        val listings = json.getAsJsonArray("epg_listings") ?: JsonArray()
        return listings.mapNotNull { el ->
            val obj = el.asJsonObject
            val start = parseTimestamp(obj.string("start") ?: obj.string("start_timestamp"))
            val end = parseTimestamp(obj.string("end") ?: obj.string("stop_timestamp"))
            if (start <= 0L) return@mapNotNull null
            EpgProgram(
                id = obj.string("id") ?: "${streamId}_$start",
                channelId = streamId,
                title = decodeBase64Title(obj.string("title") ?: "Programme"),
                description = decodeBase64Title(obj.string("description") ?: ""),
                startTime = start,
                endTime = if (end > 0L) end else start + 3_600_000
            )
        }
    }

    fun getXmltvEpg(auth: XtreamAuth): List<EpgProgram> {
        val url = "${auth.serverBase}/xmltv.php?username=${auth.username}&password=${auth.password}"
        val body = get(url)
        return XmltvParser.parse(body)
    }

    private fun fetchArray(auth: XtreamAuth, action: String): JsonArray {
        val body = get(apiUrl(auth, action))
        val element = gson.fromJson(body, JsonElement::class.java)
        return when {
            element == null || element.isJsonNull -> JsonArray()
            element.isJsonArray -> element.asJsonArray
            else -> JsonArray()
        }
    }

    private fun fetchMap(auth: XtreamAuth, action: String): Map<String, String> {
        val array = fetchArray(auth, action)
        return array.mapNotNull { el ->
            val obj = el.asJsonObject
            val id = obj.string("category_id") ?: return@mapNotNull null
            id to (obj.string("category_name") ?: "Général")
        }.toMap()
    }

    private fun apiUrl(auth: XtreamAuth, action: String): String {
        return "${auth.serverBase}/player_api.php?username=${auth.username}&password=${auth.password}&action=$action"
    }

    private fun buildLiveUrl(auth: XtreamAuth, streamId: String, ext: String): String {
        return "${auth.serverBase}/live/${auth.username}/${auth.password}/$streamId.$ext"
    }

    private fun buildVodUrl(auth: XtreamAuth, streamId: String, ext: String): String {
        return "${auth.serverBase}/movie/${auth.username}/${auth.password}/$streamId.$ext"
    }

    private fun buildSeriesUrl(auth: XtreamAuth, episodeId: String, ext: String): String {
        return "${auth.serverBase}/series/${auth.username}/${auth.password}/$episodeId.$ext"
    }

    private fun get(url: String): String {
        val request = Request.Builder().url(url).get().build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw IllegalStateException("Erreur réseau ${response.code}")
            }
            return response.body?.string() ?: throw IllegalStateException("Réponse vide")
        }
    }

    private fun normalizeServer(url: String): String {
        var base = url.trim().removeSuffix("/")
        if (!base.startsWith("http")) base = "http://$base"
        return base
    }

    private fun JsonObject.string(key: String): String? {
        if (!has(key) || get(key).isJsonNull) return null
        return when {
            get(key).isJsonPrimitive -> get(key).asString
            else -> null
        }
    }

    private fun parseTimestamp(value: String?): Long {
        if (value.isNullOrBlank()) return 0L
        value.toLongOrNull()?.let { ts ->
            return if (ts < 10_000_000_000L) ts * 1000 else ts
        }
        return try {
            val format = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.US)
            format.parse(value)?.time ?: 0L
        } catch (_: Exception) {
            0L
        }
    }

    private fun decodeBase64Title(value: String): String {
        return try {
            val decoded = android.util.Base64.decode(value, android.util.Base64.DEFAULT)
            String(decoded, Charsets.UTF_8)
        } catch (_: Exception) {
            value
        }
    }

    private fun formatDuration(raw: String): String {
        val seconds = raw.toIntOrNull() ?: return raw
        val h = seconds / 3600
        val m = (seconds % 3600) / 60
        return if (h > 0) "${h}h ${m}min" else "${m}min"
    }
}
