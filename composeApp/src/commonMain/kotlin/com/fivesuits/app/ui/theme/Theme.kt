package com.fivesuits.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val LightColors = lightColorScheme(
    primary = WhiteLabel.primary,
    onPrimary = WhiteLabel.onPrimary,
    primaryContainer = Color(0xFFDCEBE1),
    onPrimaryContainer = Color(0xFF103D2E),
    secondary = Color(0xFF6A755C),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE9EBD9),
    onSecondaryContainer = Color(0xFF303B26),
    tertiary = Color(0xFF916216),
    onTertiary = Color.White,
    tertiaryContainer = WhiteLabel.accentSoft,
    onTertiaryContainer = Color(0xFF5B3D08),
    background = WhiteLabel.background,
    onBackground = WhiteLabel.ink,
    surface = WhiteLabel.surface,
    onSurface = WhiteLabel.ink,
    surfaceVariant = Color(0xFFEAEDE5),
    onSurfaceVariant = WhiteLabel.muted,
    surfaceTint = WhiteLabel.primary,
    inverseSurface = Color(0xFF243A30),
    inverseOnSurface = Color(0xFFF2F6EF),
    inversePrimary = Color(0xFF9DD8BA),
    outline = Color(0xFF859087),
    outlineVariant = WhiteLabel.border,
    error = Color(0xFFB14337),
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD3),
    onErrorContainer = Color(0xFF410C07),
    scrim = Color.Black,
    surfaceBright = Color(0xFFFFFEFA),
    surfaceDim = Color(0xFFDDDCD5),
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color(0xFFF5F5EE),
    surfaceContainer = Color(0xFFEFEEE7),
    surfaceContainerHigh = Color(0xFFE9E9E2),
    surfaceContainerHighest = Color(0xFFE3E4DC),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF9DD8BA),
    onPrimary = Color(0xFF063826),
    primaryContainer = Color(0xFF20543F),
    onPrimaryContainer = Color(0xFFB9F4D4),
    secondary = Color(0xFFC4CBAF),
    onSecondary = Color(0xFF2D351F),
    secondaryContainer = Color(0xFF434D34),
    onSecondaryContainer = Color(0xFFE0E7CB),
    tertiary = Color(0xFFECC177),
    onTertiary = Color(0xFF4B3100),
    tertiaryContainer = Color(0xFF6C490D),
    onTertiaryContainer = Color(0xFFFFDFAB),
    background = Color(0xFF121D17),
    onBackground = Color(0xFFE0E5DC),
    surface = Color(0xFF152019),
    onSurface = Color(0xFFE0E5DC),
    surfaceVariant = Color(0xFF3D4940),
    onSurfaceVariant = Color(0xFFC0CBC0),
    surfaceTint = Color(0xFF9DD8BA),
    inverseSurface = Color(0xFFE0E5DC),
    inverseOnSurface = Color(0xFF263128),
    inversePrimary = WhiteLabel.primary,
    outline = Color(0xFF8A968B),
    outlineVariant = Color(0xFF3D4940),
    error = Color(0xFFFFB4A8),
    onError = Color(0xFF690E07),
    errorContainer = Color(0xFF8A2920),
    onErrorContainer = Color(0xFFFFDAD3),
    scrim = Color.Black,
    surfaceBright = Color(0xFF38433A),
    surfaceDim = Color(0xFF101B14),
    surfaceContainerLowest = Color(0xFF0B1510),
    surfaceContainerLow = Color(0xFF19251D),
    surfaceContainer = Color(0xFF1E2A22),
    surfaceContainerHigh = Color(0xFF29352C),
    surfaceContainerHighest = Color(0xFF344036),
)

private val AppTypography = Typography(
    displayLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold, fontSize = 56.sp, lineHeight = 60.sp, letterSpacing = (-2).sp),
    displayMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold, fontSize = 44.sp, lineHeight = 48.sp, letterSpacing = (-1.6).sp),
    displaySmall = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold, fontSize = 36.sp, lineHeight = 40.sp, letterSpacing = (-1).sp),
    headlineLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold, fontSize = 32.sp, lineHeight = 38.sp, letterSpacing = (-0.8).sp),
    headlineMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold, fontSize = 28.sp, lineHeight = 34.sp, letterSpacing = (-0.6).sp),
    headlineSmall = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold, fontSize = 24.sp, lineHeight = 30.sp, letterSpacing = (-0.4).sp),
    titleLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.SemiBold, fontSize = 22.sp, lineHeight = 28.sp, letterSpacing = (-0.3).sp),
    titleMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, lineHeight = 23.sp),
    titleSmall = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, lineHeight = 20.sp),
    bodyLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 25.sp),
    bodyMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 21.sp),
    bodySmall = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Normal, fontSize = 12.sp, lineHeight = 18.sp),
    labelLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, lineHeight = 20.sp),
    labelMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, lineHeight = 16.sp),
    labelSmall = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.SemiBold, fontSize = 10.sp, lineHeight = 14.sp, letterSpacing = 0.4.sp),
)

@Composable
fun FiveSuitsTheme(darkTheme: Boolean = false, content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = AppTypography,
        shapes = Shapes(
            extraSmall = RoundedCornerShape(6.dp),
            small = RoundedCornerShape(12.dp),
            medium = RoundedCornerShape(18.dp),
            large = RoundedCornerShape(24.dp),
            extraLarge = RoundedCornerShape(32.dp),
        ),
        content = content,
    )
}
