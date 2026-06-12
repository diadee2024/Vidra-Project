package com.zyroplay.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.zyroplay.app.model.PlayerSettings
import com.zyroplay.app.ui.components.CategoryChip
import com.zyroplay.app.ui.theme.LocalZyroTheme
import com.zyroplay.app.ui.theme.ZyroCard
import com.zyroplay.app.ui.theme.ZyroTextPrimary

@Composable
fun PlayerSettingsDialog(
    settings: PlayerSettings,
    tmdbApiKey: String = "",
    onDismiss: () -> Unit,
    onSave: (PlayerSettings) -> Unit,
    onTmdbKeyChange: (String) -> Unit = {}
) {
    val theme = LocalZyroTheme.current
    var buffer by remember { mutableStateOf(settings.bufferSeconds.toFloat()) }
    var subtitles by remember { mutableStateOf(settings.subtitlesEnabled) }
    var aspectRatio by remember { mutableStateOf(settings.aspectRatio) }
    var tmdbKey by remember { mutableStateOf(tmdbApiKey) }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(ZyroCard)
                .padding(24.dp)
        ) {
            Text("Réglages lecteur", style = MaterialTheme.typography.titleLarge, color = ZyroTextPrimary)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Tampon: ${buffer.toInt()}s", color = ZyroTextPrimary)
            Slider(value = buffer, onValueChange = { buffer = it }, valueRange = 5f..60f, steps = 10)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Sous-titres", color = ZyroTextPrimary)
                Switch(checked = subtitles, onCheckedChange = { subtitles = it })
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Ratio d'aspect", color = ZyroTextPrimary)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("fit" to "Ajuster", "fill" to "Remplir", "zoom" to "Zoom").forEach { (value, label) ->
                    CategoryChip(label = label, selected = aspectRatio == value, onClick = { aspectRatio = value })
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("Clé API TMDB (optionnel)", color = ZyroTextPrimary, style = MaterialTheme.typography.labelMedium)
            Spacer(modifier = Modifier.height(4.dp))
            androidx.compose.foundation.text.BasicTextField(
                value = tmdbKey,
                onValueChange = { tmdbKey = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Black.copy(alpha = 0.3f))
                    .padding(12.dp),
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = Color.White),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text("Annuler") }
                Button(
                    onClick = {
                        onTmdbKeyChange(tmdbKey)
                        onSave(settings.copy(
                            bufferSeconds = buffer.toInt(),
                            subtitlesEnabled = subtitles,
                            aspectRatio = aspectRatio
                        ))
                        onDismiss()
                    },
                    modifier = Modifier.weight(1f)
                ) { Text("Enregistrer") }
            }
        }
    }
}
