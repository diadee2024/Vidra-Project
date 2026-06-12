package com.zyroplay.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zyroplay.app.model.EpisodeItem
import com.zyroplay.app.model.SeriesItem
import com.zyroplay.app.ui.components.SectionHeader
import com.zyroplay.app.ui.components.SeriesCard
import com.zyroplay.app.ui.theme.LocalZyroTheme
import com.zyroplay.app.ui.theme.ZyroCard
import com.zyroplay.app.ui.theme.ZyroTextPrimary
import com.zyroplay.app.ui.theme.ZyroTextSecondary

@Composable
fun SeriesScreen(
    series: List<SeriesItem>,
    selectedSeries: SeriesItem?,
    episodes: List<EpisodeItem>,
    onSeriesClick: (SeriesItem) -> Unit,
    onEpisodeClick: (EpisodeItem) -> Unit,
    onBackFromEpisodes: () -> Unit
) {
    val theme = LocalZyroTheme.current

    if (selectedSeries != null) {
        Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable(onClick = onBackFromEpisodes).padding(bottom = 16.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = theme.secondary)
                Spacer(modifier = Modifier.padding(4.dp))
                Text("Retour", color = theme.secondary)
            }
            Text(
                selectedSeries.title,
                style = MaterialTheme.typography.headlineMedium,
                color = ZyroTextPrimary,
                fontWeight = FontWeight.Bold
            )
            Text(selectedSeries.genre, color = ZyroTextSecondary)
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(episodes) { episode ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(ZyroCard)
                            .clickable { onEpisodeClick(episode) }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "S${episode.seasonNumber} E${episode.episodeNumber}",
                                color = theme.secondary,
                                style = MaterialTheme.typography.labelLarge
                            )
                            Text(episode.title, color = ZyroTextPrimary, fontWeight = FontWeight.Medium)
                        }
                        Icon(Icons.Default.PlayArrow, null, tint = theme.primary)
                    }
                }
            }
        }
    } else {
        Column(modifier = Modifier.fillMaxSize()) {
            SectionHeader(title = "Séries", subtitle = "${series.size} séries disponibles")
            LazyVerticalGrid(
                columns = GridCells.Adaptive(150.dp),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(series) { item ->
                    SeriesCard(series = item, onClick = { onSeriesClick(item) })
                }
            }
        }
    }
}
