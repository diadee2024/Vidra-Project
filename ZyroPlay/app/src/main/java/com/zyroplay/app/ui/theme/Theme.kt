package com.zyroplay.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val ZyroColorScheme = darkColorScheme(
    primary = ZyroPurple,
    onPrimary = ZyroTextPrimary,
    secondary = ZyroCyan,
    onSecondary = ZyroBlack,
    background = ZyroBackground,
    onBackground = ZyroTextPrimary,
    surface = ZyroSurface,
    onSurface = ZyroTextPrimary,
    surfaceVariant = ZyroCard,
    onSurfaceVariant = ZyroTextSecondary,
    outline = ZyroTextMuted
)

@Composable
fun ZyroPlayTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ZyroColorScheme,
        typography = ZyroTypography,
        content = content
    )
}
