package com.osu.client.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// ── Brand ─────────────────────────────────────────────────────────────────────
val OsuPink    = Color(0xFFFF66AA)
val OsuPinkDim = Color(0xFF99335A)
val OsuPurple  = Color(0xFF9966FF)
val OsuBlue    = Color(0xFF66CCFF)
val OsuGold    = Color(0xFFFFCC44)

// ── Grades ────────────────────────────────────────────────────────────────────
val GradeSSH = Color(0xFFE8E8F8)
val GradeSS  = Color(0xFFFFDD44)
val GradeSH  = Color(0xFFCCEEFF)
val GradeS   = Color(0xFF44DDFF)
val GradeA   = Color(0xFF66EE66)
val GradeB   = Color(0xFF6699FF)
val GradeC   = Color(0xFFAA66FF)
val GradeD   = Color(0xFFFF4444)

// ── Surfaces ──────────────────────────────────────────────────────────────────
val Surface0 = Color(0xFF0A0A0C)
val Surface1 = Color(0xFF111114)
val Surface2 = Color(0xFF1A1A1F)
val Surface3 = Color(0xFF222228)
val Surface4 = Color(0xFF2A2A32)

private val DarkColorScheme = darkColorScheme(
    primary              = OsuPink,
    onPrimary            = Color(0xFF1A0010),
    primaryContainer     = Color(0xFF4A0028),
    onPrimaryContainer   = Color(0xFFFFD9E6),
    secondary            = OsuPurple,
    onSecondary          = Color(0xFF150040),
    secondaryContainer   = Color(0xFF2D0070),
    onSecondaryContainer = Color(0xFFE8DAFF),
    tertiary             = OsuBlue,
    onTertiary           = Color(0xFF003040),
    background           = Surface0,
    onBackground         = Color(0xFFF2F2F6),
    surface              = Surface1,
    onSurface            = Color(0xFFEEEEF2),
    surfaceVariant       = Surface2,
    onSurfaceVariant     = Color(0xFF9A9AA4),
    outline              = Color(0xFF33333A),
    outlineVariant       = Color(0xFF28282E),
    error                = Color(0xFFFF4455),
    onError              = Color(0xFF200000),
    inverseSurface       = Color(0xFFEEEEF2),
    inverseOnSurface     = Surface0,
)

val Typography = Typography(
    displayLarge  = TextStyle(fontWeight = FontWeight.Black,  fontSize = 57.sp, letterSpacing = (-0.25).sp),
    displayMedium = TextStyle(fontWeight = FontWeight.Bold,   fontSize = 45.sp),
    displaySmall  = TextStyle(fontWeight = FontWeight.Bold,   fontSize = 36.sp),
    headlineLarge = TextStyle(fontWeight = FontWeight.Bold,   fontSize = 32.sp, letterSpacing = (-0.5).sp),
    headlineMedium= TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 28.sp),
    headlineSmall = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 24.sp),
    titleLarge    = TextStyle(fontWeight = FontWeight.Bold,   fontSize = 22.sp),
    titleMedium   = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 16.sp, letterSpacing = 0.15.sp),
    titleSmall    = TextStyle(fontWeight = FontWeight.Medium, fontSize = 14.sp, letterSpacing = 0.1.sp),
    bodyLarge     = TextStyle(fontWeight = FontWeight.Normal, fontSize = 16.sp),
    bodyMedium    = TextStyle(fontWeight = FontWeight.Normal, fontSize = 14.sp),
    bodySmall     = TextStyle(fontWeight = FontWeight.Normal, fontSize = 12.sp),
    labelLarge    = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 14.sp, letterSpacing = 0.1.sp),
    labelMedium   = TextStyle(fontWeight = FontWeight.Medium, fontSize = 12.sp, letterSpacing = 0.5.sp),
    labelSmall    = TextStyle(fontWeight = FontWeight.Medium, fontSize = 11.sp, letterSpacing = 0.5.sp),
)

@Composable
fun OsuClientTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography  = Typography,
        content     = content,
    )
}
