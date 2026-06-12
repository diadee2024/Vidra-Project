package com.zyroplay.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.zyroplay.app.model.Channel
import com.zyroplay.app.model.EpgProgram
import com.zyroplay.app.ui.components.SectionHeader
import com.zyroplay.app.ui.modifier.tvFocusable
import com.zyroplay.app.ui.theme.LocalZyroTheme
import com.zyroplay.app.ui.theme.ZyroCard
import com.zyroplay.app.ui.theme.ZyroTextMuted
import com.zyroplay.app.ui.theme.ZyroTextPrimary
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.max

private const val HOUR_WIDTH_DP = 120
private const val CHANNEL_WIDTH_DP = 160
private const val ROW_HEIGHT_DP = 64
private const val DAYS = 7

@Composable
fun EpgScreen(
    channels: List<Channel>,
    programs: List<EpgProgram>,
    onProgramClick: (Channel, EpgProgram) -> Unit = { _, _ -> }
) {
    val theme = LocalZyroTheme.current
    val now = System.currentTimeMillis()
    val dayStart = remember {
        Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }
    val rangeStart = dayStart
    val rangeEnd = dayStart + DAYS * 24L * 3_600_000L
    val totalHours = DAYS * 24
    val hourFormat = remember { SimpleDateFormat("HH:mm", Locale.FRANCE) }
    val dayFormat = remember { SimpleDateFormat("EEE dd/MM", Locale.FRANCE) }

    Column(modifier = Modifier.fillMaxSize()) {
        SectionHeader(
            title = "Guide TV",
            subtitle = "Programme sur $DAYS jours • ${channels.size} chaînes"
        )

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Column(modifier = Modifier.width(CHANNEL_WIDTH_DP.dp)) {
                Box(modifier = Modifier.height(40.dp))
                channels.forEach { channel ->
                    Box(
                        modifier = Modifier
                            .height(ROW_HEIGHT_DP.dp)
                            .fillMaxWidth()
                            .padding(vertical = 2.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(ZyroCard)
                            .padding(horizontal = 8.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = channel.name,
                            style = MaterialTheme.typography.labelMedium,
                            color = ZyroTextPrimary,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .horizontalScroll(rememberScrollState())
            ) {
                Row {
                    repeat(totalHours) { hourIndex ->
                        val hourTs = rangeStart + hourIndex * 3_600_000L
                        val cal = Calendar.getInstance().apply { timeInMillis = hourTs }
                        val label = if (cal.get(Calendar.HOUR_OF_DAY) == 0) {
                            dayFormat.format(Date(hourTs))
                        } else {
                            hourFormat.format(Date(hourTs))
                        }
                        Box(
                            modifier = Modifier
                                .width(HOUR_WIDTH_DP.dp)
                                .height(40.dp)
                                .padding(1.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall,
                                color = ZyroTextMuted,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                Box {
                    val nowOffsetHours = ((now - rangeStart).coerceAtLeast(0L) / 3_600_000f)
                    Box(
                        modifier = Modifier
                            .offset(x = (nowOffsetHours * HOUR_WIDTH_DP).dp)
                            .width(2.dp)
                            .height((channels.size * ROW_HEIGHT_DP).dp)
                            .background(theme.primary.copy(alpha = 0.8f))
                    )

                    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                        channels.forEach { channel ->
                            val channelPrograms = programs.filter {
                                it.channelId == channel.id || it.channelId == channel.epgChannelId
                            }.filter { it.endTime > rangeStart && it.startTime < rangeEnd }

                            Box(
                                modifier = Modifier
                                    .width((totalHours * HOUR_WIDTH_DP).dp)
                                    .height(ROW_HEIGHT_DP.dp)
                                    .padding(vertical = 2.dp)
                            ) {
                                channelPrograms.forEach { program ->
                                    val startOffset = max(0L, program.startTime - rangeStart)
                                    val endOffset = minOf(program.endTime, rangeEnd) - rangeStart
                                    val durationMs = (endOffset - startOffset).coerceAtLeast(3_600_000L / 2)
                                    val leftDp = (startOffset / 3_600_000f) * HOUR_WIDTH_DP
                                    val widthDp = (durationMs / 3_600_000f) * HOUR_WIDTH_DP
                                    val isPast = program.endTime < now

                                    Box(
                                        modifier = Modifier
                                            .offset(x = leftDp.dp)
                                            .width(widthDp.coerceAtLeast(40f).dp)
                                            .height((ROW_HEIGHT_DP - 4).dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(
                                                if (isPast) theme.secondary.copy(alpha = 0.2f)
                                                else theme.primary.copy(alpha = 0.25f)
                                            )
                                            .border(
                                                1.dp,
                                                if (isPast) theme.secondary.copy(alpha = 0.5f)
                                                else theme.secondary.copy(alpha = 0.4f),
                                                RoundedCornerShape(6.dp)
                                            )
                                            .tvFocusable()
                                            .clickable { onProgramClick(channel, program) }
                                            .padding(horizontal = 6.dp, vertical = 4.dp),
                                        contentAlignment = Alignment.CenterStart
                                    ) {
                                        Text(
                                            text = program.title,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = ZyroTextPrimary,
                                            fontWeight = FontWeight.Medium,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
