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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.zyroplay.app.data.MockData
import com.zyroplay.app.model.VodItem
import com.zyroplay.app.ui.components.SectionHeader
import com.zyroplay.app.ui.theme.ZyroCard
import com.zyroplay.app.ui.theme.ZyroCyan
import com.zyroplay.app.ui.theme.ZyroTextMuted
import com.zyroplay.app.ui.theme.ZyroTextPrimary
import com.zyroplay.app.ui.theme.ZyroTextSecondary

@Composable
fun CatchUpScreen(onProgramClick: (VodItem) -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        SectionHeader(
            title = "Replay / Catch-Up",
            subtitle = "Revoyez les programmes des 7 derniers jours"
        )

        LazyColumn(
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(MockData.catchUpPrograms) { program ->
                CatchUpItem(program = program, onClick = { onProgramClick(program) })
            }
        }
    }
}

@Composable
private fun CatchUpItem(program: VodItem, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(ZyroCard)
            .border(1.dp, ZyroTextMuted.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(ZyroCyan.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Replay, null, tint = ZyroCyan, modifier = Modifier.size(28.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(program.title, style = MaterialTheme.typography.titleMedium, color = ZyroTextPrimary)
            Text(
                "${program.genre} • ${program.duration}",
                style = MaterialTheme.typography.bodyMedium,
                color = ZyroTextSecondary
            )
        }
        Icon(Icons.Default.PlayArrow, null, tint = ZyroCyan, modifier = Modifier.size(32.dp))
    }
}
