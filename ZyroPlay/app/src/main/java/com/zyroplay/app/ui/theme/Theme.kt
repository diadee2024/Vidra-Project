package com.zyroplay.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

@Composable
fun ZyroPlayTheme(
    themeIndex: Int = 0,
    content: @Composable () -> Unit
) {
    val theme = zyroThemes.getOrElse(themeIndex) { zyroThemes[0] }
    CompositionLocalProvider(LocalZyroTheme provides theme) {
        MaterialTheme(
            colorScheme = zyroColorScheme(theme),
            typography = ZyroTypography,
            content = content
        )
    }
}
