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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zyroplay.app.data.MockData
import com.zyroplay.app.model.Channel
import com.zyroplay.app.ui.components.CategoryChip
import com.zyroplay.app.ui.components.ChannelGrid
import com.zyroplay.app.ui.components.SectionHeader
import com.zyroplay.app.ui.components.ZyroSearchBar

@Composable
fun LiveScreen(onChannelClick: (Channel) -> Unit) {
    var selectedCategory by remember { mutableStateOf("Tous") }

    Column(modifier = Modifier.fillMaxSize()) {
        SectionHeader(
            title = "Live TV",
            subtitle = "${MockData.channels.size} chaînes disponibles"
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
            items(MockData.liveCategories) { category ->
                CategoryChip(
                    label = category,
                    selected = selectedCategory == category,
                    onClick = { selectedCategory = category }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        val filtered = if (selectedCategory == "Tous") {
            MockData.channels
        } else {
            MockData.channels.filter { it.category == selectedCategory }
        }

        ChannelGrid(
            channels = filtered,
            onChannelClick = onChannelClick,
            modifier = Modifier.weight(1f)
        )
    }
}
