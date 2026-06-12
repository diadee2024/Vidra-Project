package com.zyroplay.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.zyroplay.app.ui.theme.LocalZyroTheme
import com.zyroplay.app.ui.theme.ZyroSurfaceElevated
import com.zyroplay.app.ui.theme.ZyroTextMuted

@Composable
fun ZyroAsyncImage(
    url: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    fallbackText: String = "",
    contentScale: ContentScale = ContentScale.Crop
) {
    val theme = LocalZyroTheme.current
    val initials = fallbackText.take(3).uppercase().ifBlank { "?" }

    if (url.isNullOrBlank()) {
        Box(
            modifier = modifier.background(
                Brush.linearGradient(
                    listOf(theme.primary.copy(alpha = 0.4f), theme.secondary.copy(alpha = 0.2f), ZyroSurfaceElevated)
                )
            ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initials,
                style = MaterialTheme.typography.headlineMedium,
                color = theme.secondary.copy(alpha = 0.6f),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
        return
    }

    SubcomposeAsyncImage(
        model = url,
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = contentScale,
        loading = {
            Box(modifier = Modifier.fillMaxSize().background(ZyroSurfaceElevated), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = theme.primary, strokeWidth = 2.dp)
            }
        },
        error = {
            Box(
                modifier = Modifier.fillMaxSize().background(ZyroSurfaceElevated),
                contentAlignment = Alignment.Center
            ) {
                Text(initials, color = ZyroTextMuted, fontWeight = FontWeight.Bold)
            }
        }
    )
}
