package com.zyroplay.app.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class M3uParserTest {

    private val sampleM3u = """
        #EXTM3U url-tvg="http://epg.example.com/xmltv.xml"
        #EXTINF:-1 tvg-id="ch1" tvg-logo="http://logo.png" group-title="News",Channel One
        http://stream.example.com/ch1.m3u8
        #EXTINF:-1 group-title="VOD Movies",Action Movie 2024
        http://stream.example.com/movie.mp4
        #EXTINF:-1 group-title="Series",Breaking Bad S01 E01
        http://stream.example.com/series1.mp4
    """.trimIndent()

    @Test
    fun parseChannels_returnsAllEntries() {
        val channels = M3uParser.parseChannels(sampleM3u)
        assertEquals(3, channels.size)
        assertEquals("Channel One", channels[0].name)
        assertEquals("http://logo.png", channels[0].logoUrl)
    }

    @Test
    fun parseVodFromM3u_filtersMovies() {
        val movies = M3uParser.parseVodFromM3u(sampleM3u)
        assertTrue(movies.isNotEmpty())
        assertEquals("Action Movie 2024", movies.first().title)
    }

    @Test
    fun extractEpgUrl_readsFromHeader() {
        val url = M3uParser.extractEpgUrl(sampleM3u)
        assertEquals("http://epg.example.com/xmltv.xml", url)
    }

    @Test
    fun parseSeriesFromM3u_groupsByTitle() {
        val series = M3uParser.parseSeriesFromM3u(sampleM3u)
        assertTrue(series.isNotEmpty())
        assertTrue(series.first().title.contains("Breaking Bad"))
    }
}
