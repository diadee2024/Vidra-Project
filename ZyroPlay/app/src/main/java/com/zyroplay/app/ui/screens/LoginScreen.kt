package com.zyroplay.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zyroplay.app.model.PlaylistCredentials
import com.zyroplay.app.model.PlaylistType
import com.zyroplay.app.ui.components.ZyroGradientBackground
import com.zyroplay.app.ui.components.ZyroLogo
import com.zyroplay.app.ui.theme.LocalZyroTheme
import com.zyroplay.app.ui.theme.ZyroSurfaceElevated
import com.zyroplay.app.ui.theme.ZyroTextMuted
import com.zyroplay.app.ui.theme.ZyroTextPrimary
import com.zyroplay.app.ui.theme.ZyroTextSecondary

@Composable
fun LoginScreen(
    isLoading: Boolean,
    onLogin: (PlaylistCredentials) -> Unit,
    onDemoMode: () -> Unit
) {
    val theme = LocalZyroTheme.current
    var selectedTab by remember { mutableIntStateOf(0) }
    var serverUrl by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var m3uUrl by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        ZyroGradientBackground()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            ZyroLogo(size = 72)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Connexion Playlist",
                style = MaterialTheme.typography.headlineLarge,
                color = ZyroTextPrimary,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Xtream Codes ou URL M3U",
                style = MaterialTheme.typography.bodyMedium,
                color = ZyroTextSecondary
            )
            Spacer(modifier = Modifier.height(32.dp))

            Box(
                modifier = Modifier
                    .width(480.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(theme.card)
                    .border(1.dp, ZyroTextMuted.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
                    .padding(32.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        LoginTab("Xtream Codes", selected = selectedTab == 0) { selectedTab = 0 }
                        LoginTab("M3U URL", selected = selectedTab == 1) { selectedTab = 1 }
                    }
                    Spacer(modifier = Modifier.height(24.dp))

                    if (selectedTab == 0) {
                        LoginField("URL du serveur", serverUrl, onValueChange = { serverUrl = it })
                        Spacer(modifier = Modifier.height(12.dp))
                        LoginField("Nom d'utilisateur", username, onValueChange = { username = it })
                        Spacer(modifier = Modifier.height(12.dp))
                        LoginField("Mot de passe", password, onValueChange = { password = it })
                    } else {
                        LoginField("URL M3U / M3U8", m3uUrl, onValueChange = { m3uUrl = it })
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(theme.gradient)
                            .clickable(enabled = !isLoading) {
                                val creds = if (selectedTab == 0) {
                                    PlaylistCredentials(PlaylistType.XTREAM, serverUrl, username, password)
                                } else {
                                    PlaylistCredentials(PlaylistType.M3U, m3uUrl = m3uUrl)
                                }
                                onLogin(creds)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.height(24.dp))
                        } else {
                            Text("Se connecter", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Essayer le mode démo",
                        style = MaterialTheme.typography.labelLarge,
                        color = theme.secondary,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .clickable(onClick = onDemoMode)
                            .padding(8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun RowScope.LoginTab(label: String, selected: Boolean, onClick: () -> Unit) {
    val theme = LocalZyroTheme.current
    Box(
        modifier = Modifier
            .weight(1f)
            .height(40.dp)
            .clip(RoundedCornerShape(10.dp))
            .then(
                if (selected) Modifier.background(theme.gradient)
                else Modifier.background(ZyroSurfaceElevated)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (selected) Color.White else ZyroTextSecondary,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

@Composable
private fun LoginField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    val theme = LocalZyroTheme.current
    Column {
        Text(label, style = MaterialTheme.typography.labelMedium, color = ZyroTextSecondary)
        Spacer(modifier = Modifier.height(6.dp))
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(ZyroSurfaceElevated)
                .border(1.dp, ZyroTextMuted.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
                .padding(horizontal = 16.dp),
            textStyle = MaterialTheme.typography.bodyMedium.copy(color = ZyroTextPrimary),
            cursorBrush = SolidColor(theme.secondary),
            singleLine = true
        )
    }
}
