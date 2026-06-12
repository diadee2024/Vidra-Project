package com.zyroplay.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zyroplay.app.model.Channel
import com.zyroplay.app.model.SeriesItem
import com.zyroplay.app.model.VodItem
import com.zyroplay.app.ui.components.ChannelCard
import com.zyroplay.app.ui.components.PosterCard
import com.zyroplay.app.ui.components.SectionHeader
import com.zyroplay.app.ui.components.SeriesCard
import com.zyroplay.app.ui.theme.ZyroTextMuted

@Composable
fun FavoritesScreen(
    channels: List<Channel>,
    movies: List<VodItem>,
    series: List<SeriesItem> = emptyList(),
    onChannelClick: (Channel) -> Unit,
    onMovieClick: (VodItem) -> Unit,
    onSeriesClick: (SeriesItem) -> Unit = {},
    onToggleFavorite: (String) -> Unit = {}
) {
    val isEmpty = channels.isEmpty() && movies.isEmpty() && series.isEmpty()

    Column(modifier = Modifier.fillMaxSize()) {
        SectionHeader(
            title = "Favoris",
            subtitle = "${channels.size} chaînes • ${movies.size} films • ${series.size} séries"
        )

        if (isEmpty) {
            Column(
                modifier = Modifier.fillMaxSize().padding(48.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Default.FavoriteBorder, null, tint = ZyroTextMuted)
                Text("Aucun favori", style = MaterialTheme.typography.bodyLarge, color = ZyroTextMuted)
            }
        } else {
            if (channels.isNotEmpty()) {
                Text("Chaînes", modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp))
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(140.dp),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(channels) { ch ->
                        ChannelCard(
                            channel = ch,
                            onClick = { onChannelClick(ch) },
                            isFavorite = true,
                            onToggleFavorite = { onToggleFavorite(ch.id) }
                        )
                    }
                }
            }
            if (movies.isNotEmpty()) {
                Text("Films", modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp))
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(150.dp),
                    contentPadding = PaddingValues(24.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(movies) { movie ->
                        PosterCard(
                            item = movie,
                            onClick = { onMovieClick(movie) },
                            isFavorite = true,
                            onToggleFavorite = { onToggleFavorite(movie.id) }
                        )
                    }
                }
            }
            if (series.isNotEmpty()) {
                Text("Séries", modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp))
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(150.dp),
                    contentPadding = PaddingValues(24.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(series) { s ->
                        SeriesCard(
                            series = s,
                            onClick = { onSeriesClick(s) },
                            isFavorite = true,
                            onToggleFavorite = { onToggleFavorite(s.id) }
                        )
                    }
                }
            }
        }
    }
}
