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
import androidx.compose.foundation.layout.width
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
import com.zyroplay.app.ui.components.ContentRowSection
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
    statusMessage: String?,
    onPlay: (VodItem) -> Unit
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
            ZyroSearchBar(modifier = Modifier.weight(1f))
            statusMessage?.let {
                Text(it, style = MaterialTheme.typography.labelMedium, color = theme.secondary)
            }
        }

        featured?.let { movie ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .padding(horizontal = 24.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(theme.primary.copy(alpha = 0.5f), theme.secondary.copy(alpha = 0.2f), theme.card)
                        )
                    )
                    .border(1.dp, ZyroTextMuted.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
            ) {
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
            ContentRowSection(row = row, onItemClick = onPlay)
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
