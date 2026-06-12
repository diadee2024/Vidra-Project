package com.zyroplay.app.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.PlaylistPlay
import androidx.compose.material.icons.filled.Cast
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zyroplay.app.model.PlayerSettings
import com.zyroplay.app.model.SavedPlaylist
import com.zyroplay.app.ui.components.SectionHeader
import com.zyroplay.app.ui.theme.LocalZyroTheme
import com.zyroplay.app.ui.theme.ZyroCard
import com.zyroplay.app.ui.theme.ZyroTextMuted
import com.zyroplay.app.ui.theme.ZyroTextPrimary
import com.zyroplay.app.ui.theme.ZyroTextSecondary
import com.zyroplay.app.ui.theme.zyroThemes

@Composable
fun SettingsScreen(
    currentThemeIndex: Int,
    savedPlaylists: List<SavedPlaylist>,
    activePlaylistId: String?,
    playerSettings: PlayerSettings = PlayerSettings(),
    parentalControlEnabled: Boolean = false,
    tmdbApiKey: String = "",
    onThemeSelected: (Int) -> Unit,
    onSelectPlaylist: (SavedPlaylist) -> Unit,
    onDeletePlaylist: (String) -> Unit,
    onAddPlaylist: () -> Unit,
    onLogout: () -> Unit,
    onSavePlayerSettings: (PlayerSettings) -> Unit = {},
    onParentalControlChange: (Boolean, String?) -> Unit = { _, _ -> },
    onTmdbKeyChange: (String) -> Unit = {}
) {
    var showPlaylists by remember { mutableStateOf(false) }
    var showPlayerSettings by remember { mutableStateOf(false) }
    var showPinSetup by remember { mutableStateOf(false) }
    var autoUpdateEpg by remember { mutableStateOf(true) }

    if (showPlayerSettings) {
        PlayerSettingsDialog(
            settings = playerSettings,
            tmdbApiKey = tmdbApiKey,
            onDismiss = { showPlayerSettings = false },
            onSave = onSavePlayerSettings,
            onTmdbKeyChange = onTmdbKeyChange
        )
    }

    if (showPinSetup) {
        ParentalPinDialog(
            title = "Définir le code PIN",
            onDismiss = { showPinSetup = false },
            onConfirm = { pin ->
                onParentalControlChange(true, pin)
                true
            }
        )
    }

    if (showPlaylists) {
        PlaylistsScreen(
            playlists = savedPlaylists,
            activeId = activePlaylistId,
            onSelect = { onSelectPlaylist(it); showPlaylists = false },
            onDelete = onDeletePlaylist,
            onAddNew = { showPlaylists = false; onAddPlaylist() }
        )
    } else {
        SettingsMain(
            currentThemeIndex = currentThemeIndex,
            onThemeSelected = onThemeSelected,
            onManagePlaylists = { showPlaylists = true },
            playlistCount = savedPlaylists.size,
            playerSettings = playerSettings,
            parentalControlEnabled = parentalControlEnabled,
            onOpenPlayerSettings = { showPlayerSettings = true },
            onParentalToggle = { enabled ->
                if (enabled) showPinSetup = true
                else onParentalControlChange(false, null)
            },
            autoUpdateEpg = autoUpdateEpg,
            onAutoUpdateEpg = { autoUpdateEpg = it },
            onLogout = onLogout
        )
    }
}

@Composable
private fun SettingsMain(
    currentThemeIndex: Int,
    onThemeSelected: (Int) -> Unit,
    onManagePlaylists: () -> Unit,
    playlistCount: Int,
    playerSettings: PlayerSettings,
    parentalControlEnabled: Boolean,
    onOpenPlayerSettings: () -> Unit,
    onParentalToggle: (Boolean) -> Unit,
    autoUpdateEpg: Boolean,
    onAutoUpdateEpg: (Boolean) -> Unit,
    onLogout: () -> Unit
) {
    val theme = LocalZyroTheme.current

    Column(modifier = Modifier.fillMaxSize()) {
        SectionHeader(title = "Réglages", subtitle = "Thèmes • Playlists • Lecteur")

        LazyColumn(
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text("Thèmes", color = ZyroTextPrimary, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.height(180.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(zyroThemes) { item ->
                        Box(
                            modifier = Modifier.fillMaxWidth().height(72.dp).clip(RoundedCornerShape(12.dp))
                                .background(item.card)
                                .border(
                                    if (item.id == currentThemeIndex) 2.dp else 1.dp,
                                    brush = item.gradient,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { onThemeSelected(item.id) }
                                .padding(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(Modifier.size(24.dp).background(item.gradient, CircleShape))
                                Spacer(Modifier.width(8.dp))
                                Text(item.name, color = ZyroTextPrimary, style = MaterialTheme.typography.labelSmall)
                            }
                            if (item.id == currentThemeIndex) {
                                Icon(Icons.Default.Check, null, tint = item.secondary, modifier = Modifier.align(Alignment.TopEnd))
                            }
                        }
                    }
                }
            }
            item {
                SettingRow(Icons.AutoMirrored.Filled.PlaylistPlay, "Gérer les playlists", "$playlistCount enregistrée(s)", onManagePlaylists)
            }
            item {
                SettingRow(
                    Icons.Default.Settings,
                    "Réglages lecteur",
                    "Tampon ${playerSettings.bufferSeconds}s • Ratio ${playerSettings.aspectRatio}",
                    onOpenPlayerSettings
                )
            }
            item { SettingRow(Icons.Default.Cast, "Chromecast", "Diffusez sur TV Google Cast", null) }
            item { SettingRow(Icons.Default.HighQuality, "Qualité vidéo", "Auto — 4K HLS", null) }
            item { SettingRow(Icons.Default.PlayCircle, "Lecteur", "ExoPlayer intégré", null) }
            item { SettingRow(Icons.Default.Subtitles, "Sous-titres", if (playerSettings.subtitlesEnabled) "Activés" else "Désactivés", onOpenPlayerSettings) }
            item { SettingRow(Icons.Default.Language, "Audio", playerSettings.preferredAudioLanguage, onOpenPlayerSettings) }
            item {
                SettingToggle(Icons.Default.ChildCare, "Contrôle parental", parentalControlEnabled) {
                    onParentalToggle(it)
                }
            }
            item { SettingToggle(Icons.Default.Update, "Mise à jour EPG", autoUpdateEpg, onAutoUpdateEpg) }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(ZyroCard)
                        .border(1.dp, ZyroTextMuted.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                        .clickable(onClick = onLogout).padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.AutoMirrored.Filled.Logout, null, tint = Color(0xFFFF3D57))
                    Spacer(Modifier.width(16.dp))
                    Text("Déconnexion", color = Color(0xFFFF3D57), fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun SettingRow(icon: ImageVector, title: String, subtitle: String, onClick: (() -> Unit)?) {
    val theme = LocalZyroTheme.current
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(ZyroCard)
            .border(1.dp, ZyroTextMuted.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = theme.secondary, modifier = Modifier.size(24.dp))
        Spacer(Modifier.width(16.dp))
        Column {
            Text(title, color = ZyroTextPrimary, fontWeight = FontWeight.Medium)
            Text(subtitle, color = ZyroTextSecondary, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun SettingToggle(icon: ImageVector, title: String, checked: Boolean, onChecked: (Boolean) -> Unit) {
    val theme = LocalZyroTheme.current
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(ZyroCard)
            .border(1.dp, ZyroTextMuted.copy(alpha = 0.1f), RoundedCornerShape(16.dp)).padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = theme.secondary, modifier = Modifier.size(24.dp))
        Spacer(Modifier.width(16.dp))
        Text(title, color = ZyroTextPrimary, modifier = Modifier.weight(1f))
        Switch(checked, onChecked, colors = SwitchDefaults.colors(checkedTrackColor = theme.primary, checkedThumbColor = Color.White))
    }
}
