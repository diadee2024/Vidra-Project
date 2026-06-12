package com.zyroplay.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
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
import com.zyroplay.app.model.ContentRow
import com.zyroplay.app.model.VodItem
import com.zyroplay.app.model.WatchHistoryItem
import com.zyroplay.app.ui.components.ContentRowSection
import com.zyroplay.app.ui.components.PosterCard
import com.zyroplay.app.ui.components.ZyroAsyncImage
import com.zyroplay.app.ui.components.ZyroSearchBar
import com.zyroplay.app.ui.theme.LocalZyroTheme
import com.zyroplay.app.ui.theme.ZyroTextMuted
import com.zyroplay.app.ui.theme.ZyroTextPrimary
import com.zyroplay.app.ui.theme.ZyroTextSecondary
import com.zyroplay.app.ui.theme.ZyroWarning

@Composable
fun HomeScreen(
    featured: VodItem?,
    movieRows: List<ContentRow>,
    continueWatching: List<WatchHistoryItem> = emptyList(),
    statusMessage: String?,
    onPlay: (VodItem) -> Unit,
    onMovieDetail: (VodItem) -> Unit = onPlay,
    onResume: (WatchHistoryItem) -> Unit,
    onOpenSearch: () -> Unit = {}
) {
    val theme = LocalZyroTheme.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ZyroSearchBar(modifier = Modifier.weight(1f), onClick = onOpenSearch)
            statusMessage?.let {
                Text(it, style = MaterialTheme.typography.labelMedium, color = theme.secondary)
            }
        }

        if (continueWatching.isNotEmpty()) {
            Text(
                "Continuer à regarder",
                style = MaterialTheme.typography.titleLarge,
                color = ZyroTextPrimary,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
            )
            LazyRow(
                contentPadding = PaddingValues(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(continueWatching) { item ->
                    Column(
                        modifier = Modifier
                            .width(150.dp)
                            .clickable { onResume(item) }
                    ) {
                        ZyroAsyncImage(
                            url = item.posterUrl,
                            contentDescription = item.title,
                            fallbackText = item.title,
                            modifier = Modifier
                                .width(150.dp)
                                .height(90.dp)
                                .clip(RoundedCornerShape(12.dp))
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(item.title, color = ZyroTextPrimary, maxLines = 1, style = MaterialTheme.typography.labelMedium)
                        val pct = if (item.durationMs > 0) (item.positionMs * 100 / item.durationMs).toInt() else 0
                        Text("$pct% vu", color = theme.secondary, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        featured?.let { movie ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .padding(horizontal = 24.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { onMovieDetail(movie) }
            ) {
                ZyroAsyncImage(
                    url = movie.posterUrl,
                    contentDescription = movie.title,
                    fallbackText = movie.title,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                            )
                        )
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(28.dp)
                ) {
                    Text("À la une", color = theme.secondary, style = MaterialTheme.typography.labelLarge)
                    Text(
                        movie.title,
                        style = MaterialTheme.typography.displayLarge,
                        color = ZyroTextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        if (movie.rating.isNotBlank()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Star, null, tint = ZyroWarning)
                                Text(movie.rating, color = ZyroTextPrimary)
                            }
                        }
                        if (movie.year.isNotBlank()) Text(movie.year, color = ZyroTextSecondary)
                        Text(movie.genre, color = ZyroTextSecondary)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(theme.gradient)
                            .clickable { onPlay(movie) }
                            .padding(horizontal = 24.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.PlayArrow, null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Lecture", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        movieRows.forEach { row ->
            ContentRowSection(row = row, onItemClick = onMovieDetail)
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
