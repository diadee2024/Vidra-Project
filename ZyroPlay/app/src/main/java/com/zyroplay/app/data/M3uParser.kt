package com.zyroplay.app.data

import com.zyroplay.app.model.Channel
import com.zyroplay.app.model.VodItem

object M3uParser {

    fun parseChannels(content: String): List<Channel> {
        val lines = content.lines().map { it.trim() }.filter { it.isNotEmpty() }
        val channels = mutableListOf<Channel>()
        var index = 0
        var pendingInfo: String? = null

        for (line in lines) {
            when {
                line.startsWith("#EXTINF:", ignoreCase = true) -> pendingInfo = line
                !line.startsWith("#") && pendingInfo != null -> {
                    val meta = parseExtInf(pendingInfo)
                    channels += Channel(
                        id = "m3u_${index++}",
                        name = meta.title,
                        category = meta.group,
                        logoUrl = meta.logo,
                        streamUrl = line,
                        epgChannelId = meta.epgId,
                        currentProgram = meta.group
                    )
                    pendingInfo = null
                }
            }
        }
        return channels
    }

    fun parseVodFromM3u(content: String): List<VodItem> {
        return parseChannels(content)
            .filter { it.category.contains("vod", true) || it.category.contains("film", true) || it.category.contains("movie", true) }
            .map { ch ->
                VodItem(
                    id = ch.id,
                    title = ch.name,
                    year = "",
                    rating = "",
                    genre = ch.category,
                    posterUrl = ch.logoUrl,
                    streamUrl = ch.streamUrl
                )
            }
    }

    private data class ExtInfMeta(
        val title: String,
        val group: String,
        val logo: String?,
        val epgId: String?
    )

    private fun parseExtInf(line: String): ExtInfMeta {
        val commaIndex = line.lastIndexOf(',')
        val title = if (commaIndex >= 0) line.substring(commaIndex + 1).trim() else "Chaîne"
        val attrs = line.substringBeforeLast(',')

        return ExtInfMeta(
            title = title,
            group = extractAttr(attrs, "group-title") ?: "Général",
            logo = extractAttr(attrs, "tvg-logo"),
            epgId = extractAttr(attrs, "tvg-id")
        )
    }

    private fun extractAttr(source: String, key: String): String? {
        val pattern = """$key="([^"]*)"""".toRegex(RegexOption.IGNORE_CASE)
        return pattern.find(source)?.groupValues?.getOrNull(1)
    }
}
