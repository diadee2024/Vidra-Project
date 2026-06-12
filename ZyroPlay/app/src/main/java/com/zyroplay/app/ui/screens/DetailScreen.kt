package com.zyroplay.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zyroplay.app.model.SeriesItem
import com.zyroplay.app.model.VodItem
import com.zyroplay.app.ui.components.ZyroAsyncImage
import com.zyroplay.app.ui.theme.LocalZyroTheme
import com.zyroplay.app.ui.theme.ZyroCard
import com.zyroplay.app.ui.theme.ZyroTextMuted
import com.zyroplay.app.ui.theme.ZyroTextPrimary
import com.zyroplay.app.ui.theme.ZyroTextSecondary
import com.zyroplay.app.ui.theme.ZyroWarning

@Composable
fun VodDetailScreen(
    movie: VodItem,
    isFavorite: Boolean,
    resumePositionMs: Long = 0L,
    onBack: () -> Unit,
    onPlay: (Long) -> Unit,
    onToggleFavorite: () -> Unit
) {
    val theme = LocalZyroTheme.current
    val scroll = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.95f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scroll)
                .padding(24.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Retour",
                    tint = theme.secondary,
                    modifier = Modifier
                        .size(32.dp)
                        .clickable(onClick = onBack)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text("Détail du film", color = ZyroTextSecondary)
            }
            Spacer(modifier = Modifier.height(24.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                ZyroAsyncImage(
                    url = movie.posterUrl,
                    contentDescription = movie.title,
                    fallbackText = movie.title,
                    modifier = Modifier
                        .width(200.dp)
                        .height(300.dp)
                        .clip(RoundedCornerShape(16.dp))
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        movie.title,
                        style = MaterialTheme.typography.headlineLarge,
                        color = ZyroTextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        if (movie.rating.isNotBlank()) {
                            Icon(Icons.Default.Star, null, tint = ZyroWarning, modifier = Modifier.size(18.dp))
                            Text(movie.rating, color = ZyroTextPrimary)
                        }
                        if (movie.year.isNotBlank()) Text(movie.year, color = ZyroTextSecondary)
                        Text(movie.genre, color = ZyroTextSecondary)
                        if (movie.duration.isNotBlank()) Text(movie.duration, color = ZyroTextMuted)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    if (movie.description.isNotBlank()) {
                        Text(movie.description, color = ZyroTextSecondary, style = MaterialTheme.typography.bodyLarge)
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(theme.gradient)
                                .clickable { onPlay(resumePositionMs) }
                                .padding(horizontal = 24.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.PlayArrow, null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                if (resumePositionMs > 0) "Reprendre" else "Lecture",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        IconButton(onClick = onToggleFavorite) {
                            Icon(
                                if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favori",
                                tint = if (isFavorite) Color(0xFFFF3D57) else ZyroTextMuted
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SeriesDetailScreen(
    series: SeriesItem,
    isFavorite: Boolean,
    onBack: () -> Unit,
    onViewEpisodes: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    val theme = LocalZyroTheme.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.95f))
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Retour",
                    tint = theme.secondary,
                    modifier = Modifier.size(32.dp).clickable(onClick = onBack)
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                ZyroAsyncImage(
                    url = series.posterUrl,
                    contentDescription = series.title,
                    fallbackText = series.title,
                    modifier = Modifier.width(200.dp).height(300.dp).clip(RoundedCornerShape(16.dp))
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(series.title, style = MaterialTheme.typography.headlineLarge, color = ZyroTextPrimary, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("${series.seasons} saison(s) • ${series.genre}", color = ZyroTextSecondary)
                    if (series.rating.isNotBlank()) Text("Note: ${series.rating}", color = ZyroTextMuted)
                    Spacer(modifier = Modifier.height(16.dp))
                    if (series.description.isNotBlank()) {
                        Text(series.description, color = ZyroTextSecondary)
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(theme.gradient)
                                .clickable(onClick = onViewEpisodes)
                                .padding(horizontal = 24.dp, vertical = 12.dp)
                        ) {
                            Text("Voir les épisodes", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        IconButton(onClick = onToggleFavorite) {
                            Icon(
                                if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favori",
                                tint = if (isFavorite) Color(0xFFFF3D57) else ZyroTextMuted
                            )
                        }
                    }
                }
            }
        }
    }
}
