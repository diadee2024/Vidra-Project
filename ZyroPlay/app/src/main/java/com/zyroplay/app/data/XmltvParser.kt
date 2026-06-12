package com.zyroplay.app.data

import com.zyroplay.app.model.EpgProgram
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

object XmltvParser {

    private val dateFormats = listOf(
        SimpleDateFormat("yyyyMMddHHmmss Z", Locale.US),
        SimpleDateFormat("yyyyMMddHHmmss", Locale.US)
    ).onEach { it.timeZone = TimeZone.getTimeZone("UTC") }

    fun parse(xml: String): List<EpgProgram> {
        if (xml.isBlank()) return emptyList()
        val factory = XmlPullParserFactory.newInstance()
        val parser = factory.newPullParser()
        parser.setInput(xml.reader())

        val programs = mutableListOf<EpgProgram>()
        var eventType = parser.eventType
        var currentChannel = ""
        var title = ""
        var description = ""
        var start = 0L
        var end = 0L
        var inProgramme = false

        while (eventType != XmlPullParser.END_DOCUMENT) {
            when (eventType) {
                XmlPullParser.START_TAG -> when (parser.name) {
                    "programme" -> {
                        inProgramme = true
                        currentChannel = parser.getAttributeValue(null, "channel") ?: ""
                        start = parseXmltvDate(parser.getAttributeValue(null, "start"))
                        end = parseXmltvDate(parser.getAttributeValue(null, "stop"))
                        title = ""
                        description = ""
                    }
                    "title" -> if (inProgramme) title = parser.nextText()
                    "desc" -> if (inProgramme) description = parser.nextText()
                }
                XmlPullParser.END_TAG -> if (parser.name == "programme" && inProgramme) {
                    if (currentChannel.isNotBlank() && title.isNotBlank()) {
                        programs += EpgProgram(
                            id = "${currentChannel}_${start}",
                            channelId = currentChannel,
                            title = title,
                            description = description,
                            startTime = start,
                            endTime = if (end > start) end else start + 3_600_000
                        )
                    }
                    inProgramme = false
                }
            }
            eventType = parser.next()
        }
        return programs
    }

    private fun parseXmltvDate(raw: String?): Long {
        if (raw.isNullOrBlank()) return 0L
        val normalized = if (raw.length > 14 && !raw.contains(" ")) {
            raw.substring(0, 14) + " " + raw.substring(14)
        } else raw
        for (format in dateFormats) {
            try {
                return format.parse(normalized)?.time ?: continue
            } catch (_: Exception) {
            }
        }
        return 0L
    }
}
