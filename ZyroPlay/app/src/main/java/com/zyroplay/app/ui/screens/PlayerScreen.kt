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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zyroplay.app.cast.CastManager
import com.zyroplay.app.model.PlayRequest
import androidx.compose.runtime.LaunchedEffect
import com.zyroplay.app.ui.components.ZyroCastButton
import com.zyroplay.app.ui.player.ExoPlayerView
import com.zyroplay.app.ui.theme.ZyroTextPrimary
import com.zyroplay.app.ui.theme.ZyroTextSecondary

@Composable
fun PlayerScreen(
    playRequest: PlayRequest,
    isLoading: Boolean = false,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(playRequest.streamUrl) {
        if (CastManager.isConnected(context)) {
            CastManager.castMedia(
                context, playRequest.title, playRequest.streamUrl,
                playRequest.subtitle, playRequest.posterUrl
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        if (playRequest.streamUrl.isNotBlank()) {
            ExoPlayerView(streamUrl = playRequest.streamUrl)
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
        }

        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 96.dp, top = 20.dp, end = 24.dp)
        ) {
            Text(
                text = playRequest.title,
                style = MaterialTheme.typography.titleLarge,
                color = ZyroTextPrimary,
                fontWeight = FontWeight.Bold
            )
            if (playRequest.subtitle.isNotBlank()) {
                Text(
                    text = playRequest.subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = ZyroTextSecondary
                )
            }
        }
    }
}
