package com.zyroplay.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zyroplay.app.data.MockData
import com.zyroplay.app.model.VodItem
import com.zyroplay.app.ui.components.CategoryChip
import com.zyroplay.app.ui.components.PosterCard
import com.zyroplay.app.ui.components.SectionHeader

@Composable
fun MoviesScreen(onMovieClick: (VodItem) -> Unit) {
    val categories = listOf("Tous", "Action", "Comédie", "Drame", "Sci-Fi", "Horreur", "Animation")
    var selectedCategory by remember { mutableStateOf("Tous") }

    val allMovies = MockData.movieRows.flatMap { it.items }

    Column(modifier = Modifier.fillMaxSize()) {
        SectionHeader(
            title = "Films",
            subtitle = "${allMovies.size}+ films en VOD"
        )

        LazyRow(
            modifier = Modifier.padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { category ->
                CategoryChip(
                    label = category,
                    selected = selectedCategory == category,
                    onClick = { selectedCategory = category }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Adaptive(150.dp),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(24.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(allMovies) { movie ->
                PosterCard(item = movie, onClick = { onMovieClick(movie) })
            }
        }
    }
}
