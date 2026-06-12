package com.zyroplay.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zyroplay.app.model.Channel
import com.zyroplay.app.model.SearchResults
import com.zyroplay.app.model.SeriesItem
import com.zyroplay.app.model.VodItem
import com.zyroplay.app.ui.components.ChannelCard
import com.zyroplay.app.ui.components.PosterCard
import com.zyroplay.app.ui.components.SectionHeader
import com.zyroplay.app.ui.components.SeriesCard
import com.zyroplay.app.ui.components.ZyroSearchField
import com.zyroplay.app.ui.theme.LocalZyroTheme
import com.zyroplay.app.ui.theme.ZyroTextMuted

@Composable
fun SearchScreen(
    query: String,
    results: SearchResults,
    onQueryChange: (String) -> Unit,
    onChannelClick: (Channel) -> Unit,
    onMovieClick: (VodItem) -> Unit,
    onSeriesClick: (SeriesItem) -> Unit
) {
    val theme = LocalZyroTheme.current

    Column(modifier = Modifier.fillMaxSize()) {
        SectionHeader(title = "Recherche", subtitle = "Chaînes, films et séries")
        ZyroSearchField(
            query = query,
            onQueryChange = onQueryChange,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (query.isBlank()) {
            Text(
                "Tapez pour rechercher dans votre playlist",
                modifier = Modifier.padding(24.dp),
                color = ZyroTextMuted,
                style = MaterialTheme.typography.bodyLarge
            )
        } else if (results.total == 0) {
            Text(
                "Aucun résultat pour \"$query\"",
                modifier = Modifier.padding(24.dp),
                color = ZyroTextMuted
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (results.channels.isNotEmpty()) {
                    item {
                        Text(
                            "Chaînes (${results.channels.size})",
                            modifier = Modifier.padding(horizontal = 24.dp),
                            color = theme.secondary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    item {
                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(140.dp),
                            modifier = Modifier.height(((results.channels.size / 4 + 1) * 160).coerceAtMost(480).dp),
                            contentPadding = PaddingValues(horizontal = 24.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(results.channels) { ch ->
                                ChannelCard(channel = ch, onClick = { onChannelClick(ch) })
                            }
                        }
                    }
                }
                if (results.movies.isNotEmpty()) {
                    item {
                        Text(
                            "Films (${results.movies.size})",
                            modifier = Modifier.padding(horizontal = 24.dp),
                            color = theme.secondary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    items(results.movies) { movie ->
                        PosterCard(
                            item = movie,
                            onClick = { onMovieClick(movie) },
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                    }
                }
                if (results.series.isNotEmpty()) {
                    item {
                        Text(
                            "Séries (${results.series.size})",
                            modifier = Modifier.padding(horizontal = 24.dp),
                            color = theme.secondary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    items(results.series) { s ->
                        SeriesCard(
                            series = s,
                            onClick = { onSeriesClick(s) },
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                    }
                }
            }
        }
    }
}
