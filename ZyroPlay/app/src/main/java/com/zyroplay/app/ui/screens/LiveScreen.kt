package com.zyroplay.app.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zyroplay.app.model.Channel
import com.zyroplay.app.ui.components.CategoryChip
import com.zyroplay.app.ui.components.ChannelGrid
import com.zyroplay.app.ui.components.SectionHeader
import com.zyroplay.app.ui.components.ZyroSearchBar

private const val PAGE_SIZE = 50

@Composable
fun LiveScreen(
    channels: List<Channel>,
    totalChannels: Int = channels.size,
    categories: List<String>,
    favorites: Set<String>,
    onChannelClick: (Channel) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onLoadMore: () -> Unit = {},
    hasMore: Boolean = false
) {
    var selectedCategory by remember { mutableStateOf("Tous") }
    var page by remember { mutableStateOf(0) }
  val filtered = if (selectedCategory == "Tous") channels
    else channels.filter { it.category == selectedCategory }
    val visible = filtered.take((page + 1) * PAGE_SIZE)
    val moreAvailable = visible.size < filtered.size

    Column(modifier = Modifier.fillMaxSize()) {
        SectionHeader(
            title = "Live TV",
            subtitle = "$totalChannels chaînes • ${favorites.count { id -> channels.any { it.id == id } }} favoris"
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            ZyroSearchBar(modifier = Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(16.dp))
        LazyRow(
            modifier = Modifier.padding(horizontal = 24.dp),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { category ->
                CategoryChip(
                    label = category,
                    selected = selectedCategory == category,
                    onClick = { selectedCategory = category; page = 0 }
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        ChannelGrid(
            channels = visible,
            onChannelClick = onChannelClick,
            modifier = Modifier.weight(1f),
            favorites = favorites,
            onToggleFavorite = onToggleFavorite
        )
        if (moreAvailable) {
            Button(
                onClick = { page++ },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(16.dp)
            ) {
                Text("Charger plus (${visible.size}/${filtered.size})")
            }
        }
    }
}
