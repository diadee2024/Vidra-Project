package com.zyroplay.app.data

import com.google.gson.Gson
import com.google.gson.JsonObject
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

class TmdbClient(
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build(),
    private val gson: Gson = Gson()
) {

    data class TmdbInfo(
        val posterUrl: String? = null,
        val overview: String? = null,
        val rating: String? = null
    )

    fun searchMovie(apiKey: String, title: String, year: String = ""): TmdbInfo? {
        if (apiKey.isBlank() || title.isBlank()) return null
        val yearParam = if (year.isNotBlank()) "&year=$year" else ""
        val url = "https://api.themoviedb.org/3/search/movie?api_key=$apiKey&query=${title.encode()}$yearParam&language=fr-FR"
        return parseFirstResult(url)
    }

    fun searchTv(apiKey: String, title: String): TmdbInfo? {
        if (apiKey.isBlank() || title.isBlank()) return null
        val url = "https://api.themoviedb.org/3/search/tv?api_key=$apiKey&query=${title.encode()}&language=fr-FR"
        return parseFirstResult(url)
    }

    private fun parseFirstResult(url: String): TmdbInfo? {
        return runCatching {
            val request = Request.Builder().url(url).get().build()
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return null
                val body = response.body?.string() ?: return null
                val json = gson.fromJson(body, JsonObject::class.java)
                val results = json.getAsJsonArray("results") ?: return null
                if (results.size() == 0) return null
                val item = results[0].asJsonObject
                val posterPath = item.get("poster_path")?.takeIf { !it.isJsonNull }?.asString
                val overview = item.get("overview")?.takeIf { !it.isJsonNull }?.asString
                val rating = item.get("vote_average")?.takeIf { !it.isJsonNull }?.asString
                TmdbInfo(
                    posterUrl = posterPath?.let { "https://image.tmdb.org/t/p/w500$it" },
                    overview = overview,
                    rating = rating
                )
            }
        }.getOrNull()
    }

    private fun String.encode(): String = java.net.URLEncoder.encode(this, "UTF-8")
}
