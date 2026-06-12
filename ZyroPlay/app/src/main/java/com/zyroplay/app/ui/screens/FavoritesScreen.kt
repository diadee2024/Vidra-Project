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
import com.zyroplay.app.data.MockData
import com.zyroplay.app.model.VodItem
import com.zyroplay.app.ui.components.PosterCard
import com.zyroplay.app.ui.components.SectionHeader
import com.zyroplay.app.ui.theme.ZyroTextMuted

@Composable
fun FavoritesScreen(onItemClick: (VodItem) -> Unit) {
    val favorites = MockData.movieRows.first().items

    Column(modifier = Modifier.fillMaxSize()) {
        SectionHeader(
            title = "Favoris",
            subtitle = "Vos chaînes et contenus sauvegardés"
        )

        if (favorites.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(48.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Default.FavoriteBorder,
                    contentDescription = null,
                    tint = ZyroTextMuted,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Text(
                    "Aucun favori pour le moment",
                    style = MaterialTheme.typography.bodyLarge,
                    color = ZyroTextMuted
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(150.dp),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(favorites) { item ->
                    PosterCard(item = item, onClick = { onItemClick(item) })
                }
            }
        }
    }
}
