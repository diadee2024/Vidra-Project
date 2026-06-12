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
import com.zyroplay.app.data.MockData
import com.zyroplay.app.ui.components.ContentRowSection
import com.zyroplay.app.ui.components.ZyroGradient
import com.zyroplay.app.ui.components.ZyroSearchBar
import com.zyroplay.app.ui.theme.ZyroCard
import com.zyroplay.app.ui.theme.ZyroCyan
import com.zyroplay.app.ui.theme.ZyroPurple
import com.zyroplay.app.ui.theme.ZyroTextMuted
import com.zyroplay.app.ui.theme.ZyroTextPrimary
import com.zyroplay.app.ui.theme.ZyroTextSecondary
import com.zyroplay.app.ui.theme.ZyroWarning

@Composable
fun HomeScreen(onPlayFeatured: () -> Unit) {
    val featured = MockData.featuredMovie

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ZyroSearchBar(modifier = Modifier.weight(1f))
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .padding(horizontal = 24.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.linearGradient(
                        listOf(
                            ZyroPurple.copy(alpha = 0.5f),
                            ZyroCyan.copy(alpha = 0.2f),
                            ZyroCard
                        )
                    )
                )
                .border(1.dp, ZyroTextMuted.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(28.dp)
            ) {
                Text(
                    text = "À la une",
                    style = MaterialTheme.typography.labelLarge,
                    color = ZyroCyan
                )
                Text(
                    text = featured.title,
                    style = MaterialTheme.typography.displayLarge,
                    color = ZyroTextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, null, tint = ZyroWarning, modifier = Modifier.padding(end = 4.dp))
                        Text(featured.rating, color = ZyroTextPrimary, style = MaterialTheme.typography.bodyMedium)
                    }
                    Text(featured.year, color = ZyroTextSecondary, style = MaterialTheme.typography.bodyMedium)
                    Text(featured.genre, color = ZyroTextSecondary, style = MaterialTheme.typography.bodyMedium)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(brush = ZyroGradient)
                        .clickable(onClick = onPlayFeatured)
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

        MockData.movieRows.forEach { row ->
            ContentRowSection(row = row, onItemClick = { onPlayFeatured() })
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
