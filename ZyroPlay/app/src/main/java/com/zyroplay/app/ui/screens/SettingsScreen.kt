package com.zyroplay.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.PlaylistPlay
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
import androidx.compose.ui.unit.dp
import com.zyroplay.app.ui.components.SectionHeader
import com.zyroplay.app.ui.theme.ZyroCard
import com.zyroplay.app.ui.theme.ZyroCyan
import com.zyroplay.app.ui.theme.ZyroPurple
import com.zyroplay.app.ui.theme.ZyroTextMuted
import com.zyroplay.app.ui.theme.ZyroTextPrimary
import com.zyroplay.app.ui.theme.ZyroTextSecondary

private data class SettingItem(
    val icon: ImageVector,
    val title: String,
    val subtitle: String,
    val hasToggle: Boolean = false
)

@Composable
fun SettingsScreen(onLogout: () -> Unit) {
    var parentalControl by remember { mutableStateOf(false) }
    var autoUpdateEpg by remember { mutableStateOf(true) }

    val settings = listOf(
        SettingItem(Icons.Default.PlaylistPlay, "Gérer les playlists", "Xtream Codes / M3U"),
        SettingItem(Icons.Default.Palette, "Thème", "Sombre Premium (actif)"),
        SettingItem(Icons.Default.HighQuality, "Qualité vidéo", "Auto — jusqu'à 4K"),
        SettingItem(Icons.Default.PlayCircle, "Lecteur externe", "Lecteur intégré"),
        SettingItem(Icons.Default.Subtitles, "Sous-titres", "Français par défaut"),
        SettingItem(Icons.Default.Language, "Langue audio", "Français"),
        SettingItem(Icons.Default.ChildCare, "Contrôle parental", "Restreindre le contenu", hasToggle = true),
        SettingItem(Icons.Default.Update, "Mise à jour EPG", "Automatique", hasToggle = true)
    )

    Column(modifier = Modifier.fillMaxSize()) {
        SectionHeader(title = "Réglages", subtitle = "Personnalisez votre expérience ZyroPlay")

        LazyColumn(
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(settings) { setting ->
                SettingRow(
                    setting = setting,
                    toggleValue = when (setting.title) {
                        "Contrôle parental" -> parentalControl
                        "Mise à jour EPG" -> autoUpdateEpg
                        else -> false
                    },
                    onToggle = { enabled ->
                        when (setting.title) {
                            "Contrôle parental" -> parentalControl = enabled
                            "Mise à jour EPG" -> autoUpdateEpg = enabled
                        }
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(ZyroCard)
                        .border(1.dp, ZyroTextMuted.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.AutoMirrored.Filled.Logout, null, tint = Color(0xFFFF3D57))
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        "Déconnexion",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFFFF3D57),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingRow(
    setting: SettingItem,
    toggleValue: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(ZyroCard)
            .border(1.dp, ZyroTextMuted.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(setting.icon, null, tint = ZyroCyan, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(setting.title, style = MaterialTheme.typography.titleMedium, color = ZyroTextPrimary)
            Text(setting.subtitle, style = MaterialTheme.typography.bodyMedium, color = ZyroTextSecondary)
        }
        if (setting.hasToggle) {
            Switch(
                checked = toggleValue,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = ZyroPurple,
                    uncheckedThumbColor = ZyroTextMuted,
                    uncheckedTrackColor = ZyroCard
                )
            )
        } else {
            Icon(Icons.Default.ChevronRight, null, tint = ZyroTextMuted)
        }
    }
}
