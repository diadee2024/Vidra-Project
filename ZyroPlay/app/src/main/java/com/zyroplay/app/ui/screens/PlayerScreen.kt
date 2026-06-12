package com.zyroplay.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PictureInPicture
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zyroplay.app.cast.CastManager
import com.zyroplay.app.model.Channel
import com.zyroplay.app.model.PlayRequest
import com.zyroplay.app.model.PlayerSettings
import com.zyroplay.app.player.PlayerHolder
import com.zyroplay.app.ui.components.ZyroCastButton
import com.zyroplay.app.ui.player.ExoPlayerView
import com.zyroplay.app.ui.theme.ZyroTextPrimary
import com.zyroplay.app.ui.theme.ZyroTextSecondary

@Composable
fun PlayerScreen(
    playRequest: PlayRequest,
    playerSettings: PlayerSettings = PlayerSettings(),
    onBack: () -> Unit,
    onZap: ((PlayRequest) -> Unit)? = null,
    onEnterPiP: (() -> Unit)? = null,
    onProgressUpdate: ((contentId: String, positionMs: Long, durationMs: Long) -> Unit)? = null
) {
    val context = LocalContext.current
    var currentRequest by remember(playRequest) { mutableStateOf(playRequest) }
    var isCasting by remember { mutableStateOf(CastManager.isConnected(context)) }

    LaunchedEffect(currentRequest.streamUrl) {
        isCasting = CastManager.isConnected(context)
        if (isCasting) {
            PlayerHolder.pause()
            CastManager.castMedia(
                context,
                currentRequest.title,
                currentRequest.streamUrl,
                currentRequest.subtitle,
                currentRequest.posterUrl
            )
        }
    }

    DisposableEffect(currentRequest.contentId) {
        onDispose {
            if (currentRequest.contentId.isNotBlank() && !currentRequest.isLive) {
                val player = PlayerHolder.exoPlayer
                if (player != null) {
                    onProgressUpdate?.invoke(
                        currentRequest.contentId,
                        player.currentPosition,
                        player.duration.coerceAtLeast(0)
                    )
                }
            }
        }
    }

    val channelList = currentRequest.channelList
    val currentChannelIndex = if (currentRequest.isLive && channelList.isNotEmpty()) {
        channelList.indexOfFirst { it.id == currentRequest.contentId }.coerceAtLeast(0)
    } else -1

    fun zapToChannel(channel: Channel) {
        onZap?.invoke(
            PlayRequest(
                title = channel.name,
                streamUrl = channel.streamUrl,
                subtitle = channel.currentProgram,
                posterUrl = channel.logoUrl,
                contentId = channel.id,
                isLive = true,
                channelList = channelList
            )
        ) ?: run { currentRequest = currentRequest.copy(
            title = channel.name,
            streamUrl = channel.streamUrl,
            subtitle = channel.currentProgram,
            posterUrl = channel.logoUrl,
            contentId = channel.id
        ) }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        if (!isCasting && currentRequest.streamUrl.isNotBlank()) {
            ExoPlayerView(
                streamUrl = currentRequest.streamUrl,
                startPositionMs = currentRequest.startPositionMs,
                playerSettings = playerSettings,
                onProgress = { pos, dur ->
                    if (currentRequest.contentId.isNotBlank() && !currentRequest.isLive) {
                        onProgressUpdate?.invoke(currentRequest.contentId, pos, dur)
                    }
                }
            )
        } else if (isCasting) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Diffusion sur Chromecast", color = Color.White, style = MaterialTheme.typography.titleLarge)
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Retour",
                tint = Color.White,
                modifier = Modifier
                    .size(32.dp)
                    .clickable(onClick = onBack)
            )
            ZyroCastButton(modifier = Modifier.padding(start = 8.dp).size(40.dp))
            if (onEnterPiP != null) {
                Icon(
                    Icons.Default.PictureInPicture,
                    contentDescription = "PiP",
                    tint = Color.White,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .size(32.dp)
                        .clickable(onClick = onEnterPiP)
                )
            }
        }

        if (currentRequest.isLive && channelList.size > 1) {
            Row(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.SkipPrevious,
                    contentDescription = "Chaîne précédente",
                    tint = Color.White,
                    modifier = Modifier
                        .size(40.dp)
                        .clickable {
                            val prev = channelList[(currentChannelIndex - 1 + channelList.size) % channelList.size]
                            zapToChannel(prev)
                        }
                )
                Icon(
                    Icons.Default.SkipNext,
                    contentDescription = "Chaîne suivante",
                    tint = Color.White,
                    modifier = Modifier
                        .size(40.dp)
                        .clickable {
                            val next = channelList[(currentChannelIndex + 1) % channelList.size]
                            zapToChannel(next)
                        }
                )
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 120.dp, top = 20.dp, end = 24.dp)
        ) {
            Text(
                text = currentRequest.title,
                style = MaterialTheme.typography.titleLarge,
                color = ZyroTextPrimary,
                fontWeight = FontWeight.Bold
            )
            if (currentRequest.subtitle.isNotBlank()) {
                Text(
                    text = currentRequest.subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = ZyroTextSecondary
                )
            }
        }
    }
}
