package com.negm.egyptology.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val EgyptColorScheme =
  darkColorScheme(
    primary = GoldMain,
    secondary = GoldLightAccent,
    tertiary = GoldMuted,
    background = DarkPageBg,
    surface = GlassCardBg,
    onPrimary = DarkPageBg,
    onSecondary = DarkPageBg,
    onBackground = BodyTextLight,
    onSurface = BodyTextLight,
    onSurfaceVariant = TextMutedColor
  )

@Composable
fun EgyptologyTheme(
  darkTheme: Boolean = true, // Force dark theme for this design
  content: @Composable () -> Unit,
) {
  MaterialTheme(colorScheme = EgyptColorScheme, typography = Typography, content = content)
}
