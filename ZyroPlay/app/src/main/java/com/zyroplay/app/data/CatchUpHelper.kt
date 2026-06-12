package com.zyroplay.app.data

import com.zyroplay.app.model.CatchUpProgram
import com.zyroplay.app.model.Channel
import com.zyroplay.app.model.EpgProgram
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

object CatchUpHelper {

    private val startFormat = SimpleDateFormat("yyyy-MM-dd:HH-mm", Locale.US)

    fun buildCatchUpUrl(
        serverBase: String,
        username: String,
        password: String,
        streamId: String,
        startTimeMs: Long,
        durationMs: Long
    ): String {
        val start = startFormat.format(startTimeMs)
        val durationMinutes = TimeUnit.MILLISECONDS.toMinutes(durationMs).coerceAtLeast(1)
        val base = serverBase.trimEnd('/')
        return "$base/streaming/timeshift.php?username=$username&password=$password&stream=$streamId&start=$start&duration=$durationMinutes"
    }

    fun buildCatchUpPrograms(
        channels: List<Channel>,
        programs: List<EpgProgram>,
        serverBase: String,
        username: String,
        password: String
    ): List<CatchUpProgram> {
        val now = System.currentTimeMillis()
        val archiveChannels = channels.filter { it.tvArchive }
        if (archiveChannels.isEmpty()) return emptyList()

        return archiveChannels.flatMap { channel ->
            val cutoff = now - TimeUnit.DAYS.toMillis(channel.tvArchiveDurationDays.toLong().coerceAtLeast(1))
            programs
                .filter {
                    (it.channelId == channel.id || it.channelId == channel.epgChannelId) &&
                        it.endTime < now && it.startTime >= cutoff
                }
                .map { program ->
                    val duration = (program.endTime - program.startTime).coerceAtLeast(3_600_000L)
                    CatchUpProgram(
                        id = program.id,
                        channelId = channel.id,
                        channelName = channel.name,
                        title = program.title,
                        description = program.description,
                        startTime = program.startTime,
                        endTime = program.endTime,
                        streamUrl = buildCatchUpUrl(
                            serverBase, username, password, channel.id,
                            program.startTime, duration
                        )
                    )
                }
        }.sortedByDescending { it.startTime }
    }
}
