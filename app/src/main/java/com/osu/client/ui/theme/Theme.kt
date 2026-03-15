package com.osu.client.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ── osu! brand colors ──────────────────────────────────────────────────────────
val OsuPink = Color(0xFFFF66AA)
val OsuPinkDark = Color(0xFFCC3377)
val OsuPinkContainer = Color(0xFF5C0032)
val OsuPinkLight = Color(0xFFFFB3D1)

val OsuPurple = Color(0xFF9966FF)
val OsuPurpleContainer = Color(0xFF2A0080)

val OsuBackground = Color(0xFF121218)
val OsuSurface = Color(0xFF1E1E2A)
val OsuSurfaceVariant = Color(0xFF2A2A3A)
val OsuOnSurface = Color(0xFFE8E8F0)
val OsuOutline = Color(0xFF44445A)

// Grade colors
val GradeSSH = Color(0xFFDDCCFF)
val GradeSS = Color(0xFFFFD700)
val GradeSH = Color(0xFFCCCCFF)
val GradeS = Color(0xFFFFAA00)
val GradeA = Color(0xFF00CC66)
val GradeB = Color(0xFF0099FF)
val GradeC = Color(0xFF9933FF)
val GradeD = Color(0xFFFF3300)

private val DarkColorScheme = darkColorScheme(
    primary = OsuPink,
    onPrimary = Color(0xFF1A0012),
    primaryContainer = OsuPinkContainer,
    onPrimaryContainer = OsuPinkLight,
    secondary = OsuPurple,
    onSecondary = Color(0xFF0D0033),
    secondaryContainer = OsuPurpleContainer,
    onSecondaryContainer = Color(0xFFD9BBFF),
    tertiary = Color(0xFF66DDFF),
    onTertiary = Color(0xFF003344),
    background = OsuBackground,
    onBackground = OsuOnSurface,
    surface = OsuSurface,
    onSurface = OsuOnSurface,
    surfaceVariant = OsuSurfaceVariant,
    onSurfaceVariant = Color(0xFFAAAAAC),
    outline = OsuOutline,
    outlineVariant = Color(0xFF333344),
    error = Color(0xFFFF6B6B),
    onError = Color(0xFF1A0000),
    surfaceTint = OsuPink,
)

private val LightColorScheme = lightColorScheme(
    primary = OsuPinkDark,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFD9E8),
    onPrimaryContainer = Color(0xFF3E0022),
    secondary = Color(0xFF7744DD),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE8DDFF),
    onSecondaryContainer = Color(0xFF2A0066),
    background = Color(0xFFFFF8FA),
    onBackground = Color(0xFF1A0010),
    surface = Color.White,
    onSurface = Color(0xFF1A0010),
    surfaceVariant = Color(0xFFF5E0EA),
    onSurfaceVariant = Color(0xFF4A3040),
    outline = Color(0xFFAA8899),
)

@Composable
fun OsuClientTheme(
    darkTheme: Boolean = true, // osu! is dark by default
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = OsuTypography,
        content = content,
    )
}
