package com.zyroplay.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlaylistPlay
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zyroplay.app.model.SavedPlaylist
import com.zyroplay.app.ui.components.SectionHeader
import com.zyroplay.app.ui.theme.LocalZyroTheme
import com.zyroplay.app.ui.theme.ZyroCard
import com.zyroplay.app.ui.theme.ZyroTextMuted
import com.zyroplay.app.ui.theme.ZyroTextPrimary
import com.zyroplay.app.ui.theme.ZyroTextSecondary

@Composable
fun PlaylistsScreen(
    playlists: List<SavedPlaylist>,
    activeId: String?,
    onSelect: (SavedPlaylist) -> Unit,
    onDelete: (String) -> Unit,
    onAddNew: () -> Unit
) {
    val theme = LocalZyroTheme.current

    Column(modifier = Modifier.fillMaxSize()) {
        SectionHeader(
            title = "Mes playlists",
            subtitle = "${playlists.size} playlist(s) enregistrée(s)"
        )

        LazyColumn(
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(playlists) { playlist ->
                val isActive = playlist.id == activeId
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isActive) theme.primary.copy(alpha = 0.15f) else ZyroCard)
                        .border(
                            1.dp,
                            if (isActive) theme.primary.copy(alpha = 0.5f) else ZyroTextMuted.copy(alpha = 0.1f),
                            RoundedCornerShape(16.dp)
                        )
                        .clickable { onSelect(playlist) }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.PlaylistPlay, null, tint = theme.secondary, modifier = Modifier.size(28.dp))
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(playlist.name, color = ZyroTextPrimary, fontWeight = FontWeight.SemiBold)
                        Text(
                            playlist.credentials.displayLabel,
                            style = MaterialTheme.typography.bodySmall,
                            color = ZyroTextSecondary
                        )
                    }
                    if (isActive) {
                        Icon(Icons.Default.Check, null, tint = theme.secondary)
                    }
                    IconButton(onClick = { onDelete(playlist.id) }) {
                        Icon(Icons.Default.Delete, null, tint = Color(0xFFFF3D57))
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(theme.gradient)
                        .clickable(onClick = onAddNew)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text("+ Ajouter une playlist", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
