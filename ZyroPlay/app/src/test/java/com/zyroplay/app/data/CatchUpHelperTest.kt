package com.zyroplay.app.data

import com.zyroplay.app.model.Channel
import com.zyroplay.app.model.EpgProgram
import org.junit.Assert.assertTrue
import org.junit.Test

class CatchUpHelperTest {

    @Test
    fun buildCatchUpUrl_containsRequiredParams() {
        val url = CatchUpHelper.buildCatchUpUrl(
            serverBase = "http://example.com",
            username = "user",
            password = "pass",
            streamId = "123",
            startTimeMs = 1_704_067_200_000L,
            durationMs = 3_600_000L
        )
        assertTrue(url.contains("timeshift.php"))
        assertTrue(url.contains("username=user"))
        assertTrue(url.contains("password=pass"))
        assertTrue(url.contains("stream=123"))
        assertTrue(url.contains("duration=60"))
    }

    @Test
    fun buildCatchUpPrograms_filtersArchiveChannels() {
        val channels = listOf(
            Channel("1", "Ch1", "News", tvArchive = true, tvArchiveDurationDays = 7),
            Channel("2", "Ch2", "Sports", tvArchive = false)
        )
        val now = System.currentTimeMillis()
        val programs = listOf(
            EpgProgram("p1", "1", "News Show", startTime = now - 7_200_000, endTime = now - 3_600_000),
            EpgProgram("p2", "2", "Match", startTime = now - 7_200_000, endTime = now - 3_600_000)
        )
        val result = CatchUpHelper.buildCatchUpPrograms(
            channels, programs, "http://srv.com", "u", "p"
        )
        assertTrue(result.all { it.channelId == "1" })
        assertTrue(result.isNotEmpty())
    }
}
