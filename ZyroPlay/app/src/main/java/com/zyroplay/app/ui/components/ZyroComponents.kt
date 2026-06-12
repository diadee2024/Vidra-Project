package com.zyroplay.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zyroplay.app.model.Channel
import com.zyroplay.app.model.ContentRow
import com.zyroplay.app.model.SeriesItem
import com.zyroplay.app.model.VodItem
import com.zyroplay.app.ui.theme.LocalZyroTheme
import com.zyroplay.app.ui.theme.ZyroCard
import com.zyroplay.app.ui.theme.ZyroLive
import com.zyroplay.app.ui.theme.ZyroSurfaceElevated
import com.zyroplay.app.ui.theme.ZyroTextMuted
import com.zyroplay.app.ui.theme.ZyroTextPrimary
import com.zyroplay.app.ui.theme.ZyroTextSecondary
import com.zyroplay.app.ui.theme.ZyroWarning

@Composable
fun ZyroGradientBackground(modifier: Modifier = Modifier) {
    val theme = LocalZyroTheme.current
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(theme.primary.copy(alpha = 0.15f), Color.Transparent),
                        radius = 800f
                    )
                )
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(theme.secondary.copy(alpha = 0.08f), Color.Transparent),
                        radius = 600f,
                        center = androidx.compose.ui.geometry.Offset(1200f, 400f)
                    )
                )
        )
    }
}

@Composable
fun ZyroLogo(modifier: Modifier = Modifier, size: Int = 48) {
    val theme = LocalZyroTheme.current
    Box(
        modifier = modifier.size(size.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(size.dp)
                .background(brush = theme.gradient, shape = RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "ZyroPlay",
                tint = Color.White,
                modifier = Modifier.size((size * 0.5).dp)
            )
        }
    }
}

@Composable
fun ZyroSearchBar(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(44.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(ZyroSurfaceElevated)
            .border(1.dp, ZyroTextMuted.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Rechercher",
            tint = ZyroTextMuted,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = "Rechercher chaînes, films, séries...",
            style = MaterialTheme.typography.bodyMedium,
            color = ZyroTextMuted
        )
    }
}

@Composable
fun CategoryChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val theme = LocalZyroTheme.current
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .then(
                if (selected) {
                    Modifier.background(brush = theme.gradient)
                } else {
                    Modifier
                        .background(ZyroSurfaceElevated)
                        .border(1.dp, ZyroTextMuted.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
                }
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = if (selected) Color.White else ZyroTextSecondary,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

@Composable
fun ChannelCard(
    channel: Channel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val theme = LocalZyroTheme.current
    Column(
        modifier = modifier
            .width(140.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(ZyroCard)
            .border(1.dp, ZyroTextMuted.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(ZyroSurfaceElevated),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = channel.name.take(2).uppercase(),
                style = MaterialTheme.typography.titleMedium,
                color = theme.secondary,
                fontWeight = FontWeight.Bold
            )
            if (channel.isLive) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(10.dp)
                        .background(ZyroLive, CircleShape)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = channel.name,
            style = MaterialTheme.typography.labelMedium,
            color = ZyroTextPrimary,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        if (channel.currentProgram.isNotEmpty()) {
            Text(
                text = channel.currentProgram,
                style = MaterialTheme.typography.labelMedium,
                color = ZyroTextMuted,
                fontSize = 10.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun PosterCard(
    item: VodItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    width: Int = 150,
    height: Int = 220,
    isFavorite: Boolean = false
) {
    val theme = LocalZyroTheme.current
    Column(
        modifier = modifier
            .width(width.dp)
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .width(width.dp)
                .height(height.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    Brush.linearGradient(
                        listOf(theme.primary.copy(alpha = 0.4f), theme.secondary.copy(alpha = 0.2f), theme.card)
                    )
                )
                .border(1.dp, ZyroTextMuted.copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = item.title.take(3).uppercase(),
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White.copy(alpha = 0.3f),
                fontWeight = FontWeight.Bold
            )
            if (item.rating.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.Black.copy(alpha = 0.6f))
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Star, null, tint = ZyroWarning, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(item.rating, fontSize = 10.sp, color = Color.White)
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = item.title,
            style = MaterialTheme.typography.labelLarge,
            color = ZyroTextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = "${item.year} • ${item.genre}",
            style = MaterialTheme.typography.labelMedium,
            color = ZyroTextMuted,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun SeriesCard(
    series: SeriesItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    PosterCard(
        item = VodItem(series.id, series.title, "", series.rating, series.genre),
        onClick = onClick,
        modifier = modifier,
        width = 150,
        height = 220
    )
}

@Composable
fun ContentRowSection(
    row: ContentRow,
    onItemClick: (VodItem) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = row.title,
            style = MaterialTheme.typography.titleLarge,
            color = ZyroTextPrimary,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(row.items) { item ->
                PosterCard(item = item, onClick = { onItemClick(item) })
            }
        }
    }
}

@Composable
fun ChannelGrid(
    channels: List<Channel>,
    onChannelClick: (Channel) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(140.dp),
        modifier = modifier,
        contentPadding = PaddingValues(24.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(channels) { channel ->
            ChannelCard(channel = channel, onClick = { onChannelClick(channel) })
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            color = ZyroTextPrimary
        )
        subtitle?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium,
                color = ZyroTextSecondary
            )
        }
    }
}
