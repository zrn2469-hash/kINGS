package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp

private val KingsEarnsColorScheme = darkColorScheme(
    primary = RoyalGold,
    onPrimary = MidnightBlack,
    primaryContainer = MetallicGold,
    onPrimaryContainer = MidnightBlack,
    secondary = RoyalPurple,
    onSecondary = PureWhite,
    secondaryContainer = DeepRoyalPurple,
    onSecondaryContainer = PureWhite,
    tertiary = SuccessGreen,
    onTertiary = MidnightBlack,
    background = MidnightBlack,
    onBackground = PureWhite,
    surface = DarkNavy,
    onSurface = PureWhite,
    surfaceVariant = ElevatedNavy,
    onSurfaceVariant = CoolSilver,
    outline = BorderSlate,
    outlineVariant = SoftNavySurface,
    error = DangerRed,
    onError = PureWhite
)

val KingsEarnsShapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(22.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

object RoyalBrushes {
    val GoldGradient = Brush.linearGradient(
        colors = listOf(WarmGoldTint, RoyalGold, MetallicGold)
    )
    val PurpleGoldGradient = Brush.linearGradient(
        colors = listOf(RoyalPurple, DeepRoyalPurple, DarkNavy)
    )
    val MidnightRadialGlow = Brush.verticalGradient(
        colors = listOf(ColorGlowTop, MidnightBlack, MidnightBlack)
    )
    val CardSurfaceGradient = Brush.linearGradient(
        colors = listOf(ElevatedNavy, DarkNavy)
    )
}

private val ColorGlowTop = androidx.compose.ui.graphics.Color(0xFF171333)

@Composable
fun KingsEarnsTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = KingsEarnsColorScheme,
        typography = KingsEarnsTypography,
        shapes = KingsEarnsShapes,
        content = content
    )
}
