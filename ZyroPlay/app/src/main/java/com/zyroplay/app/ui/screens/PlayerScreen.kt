package com.zyroplay.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zyroplay.app.ui.theme.ZyroCyan
import com.zyroplay.app.ui.theme.ZyroPurple
import com.zyroplay.app.ui.theme.ZyroTextPrimary
import com.zyroplay.app.ui.theme.ZyroTextSecondary

@Composable
fun PlayerScreen(
    title: String,
    onBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(ZyroPurple.copy(alpha = 0.2f), Color.Black)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(listOf(ZyroPurple, ZyroCyan))
                        )
                        .clickable { },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        tint = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = ZyroTextPrimary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 24.dp)
                )
                Text(
                    text = "Lecteur ExoPlayer — Prêt pour le streaming",
                    style = MaterialTheme.typography.bodyMedium,
                    color = ZyroTextSecondary,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }

        Icon(
            imageVector = Icons.Default.ArrowBack,
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
