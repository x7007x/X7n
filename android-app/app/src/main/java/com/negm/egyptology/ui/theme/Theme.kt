package com.negm.egyptology.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val EgyptColorScheme =
  darkColorScheme(
    primary = GoldMain,
    onPrimary = Color(0xFF1A1205),
    primaryContainer = Color(0xFF3D2C12),
    onPrimaryContainer = Color(0xFFFFE1A5),
    secondary = GoldLightAccent,
    onSecondary = Color(0xFF1A1205),
    tertiary = Color(0xFFB19A6B),
    onTertiary = Color(0xFF19140C),
    background = DarkPageBg,
    onBackground = BodyTextLight,
    surface = GlassCardBg,
    onSurface = BodyTextLight,
    surfaceVariant = SecondarySurface,
    onSurfaceVariant = TextMutedColor,
    outline = GlassBorderColor,
    outlineVariant = GlassBorderSubtle
  )

@Composable
fun EgyptologyTheme(
  darkTheme: Boolean = true,
  content: @Composable () -> Unit,
) {
  MaterialTheme(colorScheme = EgyptColorScheme, typography = Typography, content = content)
}
