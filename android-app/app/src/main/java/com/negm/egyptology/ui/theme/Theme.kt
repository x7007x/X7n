package com.negm.egyptology.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val EgyptColorScheme =
  darkColorScheme(
    primary = GoldMain,
    onPrimary = DarkPageBg,
    primaryContainer = SecondarySurface,
    onPrimaryContainer = BodyTextLight,
    secondary = GoldLightAccent,
    onSecondary = DarkPageBg,
    tertiary = TextMutedColor,
    onTertiary = DarkPageBg,
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
