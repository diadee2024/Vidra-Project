package com.zyroplay.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zyroplay.app.model.PlayRequest
import com.zyroplay.app.ui.player.ExoPlayerView
import com.zyroplay.app.ui.theme.LocalZyroTheme
import com.zyroplay.app.ui.theme.ZyroTextPrimary
import com.zyroplay.app.ui.theme.ZyroTextSecondary

@Composable
fun PlayerScreen(
    playRequest: PlayRequest,
    isLoading: Boolean = false,
    onBack: () -> Unit
) {
    val theme = LocalZyroTheme.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        if (playRequest.streamUrl.isNotBlank()) {
            ExoPlayerView(streamUrl = playRequest.streamUrl)
        } else if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = theme.primary
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 72.dp, top = 24.dp, end = 24.dp)
        ) {
            Text(
                text = playRequest.title,
                style = MaterialTheme.typography.headlineSmall,
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

        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Retour",
            tint = Color.White,
            modifier = Modifier
                .padding(24.dp)
                .size(32.dp)
                .clickable(onClick = onBack)
                .align(Alignment.TopStart)
        )
    }
}
