package com.zyroplay.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zyroplay.app.data.MockData
import com.zyroplay.app.model.SeriesItem
import com.zyroplay.app.ui.components.SectionHeader
import com.zyroplay.app.ui.components.SeriesCard

@Composable
fun SeriesScreen(onSeriesClick: (SeriesItem) -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        SectionHeader(
            title = "Séries",
            subtitle = "${MockData.seriesList.size} séries disponibles"
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyVerticalGrid(
            columns = GridCells.Adaptive(150.dp),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(24.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(MockData.seriesList) { series ->
                SeriesCard(series = series, onClick = { onSeriesClick(series) })
            }
        }
    }
}
