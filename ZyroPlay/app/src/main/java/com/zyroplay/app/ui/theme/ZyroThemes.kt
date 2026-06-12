package com.zyroplay.app.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

data class ZyroThemeColors(
    val id: Int,
    val name: String,
    val primary: Color,
    val secondary: Color,
    val background: Color,
    val surface: Color,
    val card: Color,
    val gradient: Brush
)

val LocalZyroTheme = compositionLocalOf { zyroThemes[0] }

val zyroThemes = listOf(
    ZyroThemeColors(
        id = 0,
        name = "Zyro Neon",
        primary = Color(0xFFB026FF),
        secondary = Color(0xFF00D2FF),
        background = Color(0xFF0A0A0F),
        surface = Color(0xFF12121A),
        card = Color(0xFF16161F),
        gradient = Brush.horizontalGradient(listOf(Color(0xFFB026FF), Color(0xFF00D2FF)))
    ),
    ZyroThemeColors(
        id = 1,
        name = "Midnight Gold",
        primary = Color(0xFFFFB300),
        secondary = Color(0xFFFF6F00),
        background = Color(0xFF0D0D0D),
        surface = Color(0xFF1A1610),
        card = Color(0xFF221C14),
        gradient = Brush.horizontalGradient(listOf(Color(0xFFFFB300), Color(0xFFFF6F00)))
    ),
    ZyroThemeColors(
        id = 2,
        name = "Ocean Blue",
        primary = Color(0xFF2979FF),
        secondary = Color(0xFF00B8D4),
        background = Color(0xFF060A12),
        surface = Color(0xFF0E1524),
        card = Color(0xFF121C2E),
        gradient = Brush.horizontalGradient(listOf(Color(0xFF2979FF), Color(0xFF00B8D4)))
    ),
    ZyroThemeColors(
        id = 3,
        name = "Emerald Pro",
        primary = Color(0xFF00E676),
        secondary = Color(0xFF1DE9B6),
        background = Color(0xFF050F0A),
        surface = Color(0xFF0C1A14),
        card = Color(0xFF102019),
        gradient = Brush.horizontalGradient(listOf(Color(0xFF00E676), Color(0xFF1DE9B6)))
    ),
    ZyroThemeColors(
        id = 4,
        name = "Crimson Elite",
        primary = Color(0xFFFF3D57),
        secondary = Color(0xFFFF6E40),
        background = Color(0xFF100608),
        surface = Color(0xFF1A0C10),
        card = Color(0xFF221016),
        gradient = Brush.horizontalGradient(listOf(Color(0xFFFF3D57), Color(0xFFFF6E40)))
    ),
    ZyroThemeColors(
        id = 5,
        name = "Arctic White",
        primary = Color(0xFFE0E0E0),
        secondary = Color(0xFF90CAF9),
        background = Color(0xFF0F0F10),
        surface = Color(0xFF1A1A1C),
        card = Color(0xFF222224),
        gradient = Brush.horizontalGradient(listOf(Color(0xFFE0E0E0), Color(0xFF90CAF9)))
    )
)

@Composable
fun zyroColorScheme(theme: ZyroThemeColors) = darkColorScheme(
    primary = theme.primary,
    onPrimary = Color.White,
    secondary = theme.secondary,
    onSecondary = Color.Black,
    background = theme.background,
    onBackground = ZyroTextPrimary,
    surface = theme.surface,
    onSurface = ZyroTextPrimary,
    surfaceVariant = theme.card,
    onSurfaceVariant = ZyroTextSecondary,
    outline = ZyroTextMuted
)
