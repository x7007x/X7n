package com.negm.egyptology.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val EgyptColorScheme =
  lightColorScheme(
    primary = GoldMain,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFF1DFC0),
    onPrimaryContainer = Color(0xFF3A270B),
    secondary = GoldLightAccent,
    onSecondary = Color(0xFF2F210A),
    tertiary = Color(0xFF4E7D70),
    onTertiary = Color.White,
    background = DarkPageBg,
    onBackground = BodyTextLight,
    surface = GlassCardBg,
    onSurface = BodyTextLight,
    surfaceVariant = GlassDarkBg,
    onSurfaceVariant = TextMutedColor,
    outline = GlassBorderColor,
    outlineVariant = GlassBorderSubtle
  )

@Composable
fun EgyptologyTheme(
  darkTheme: Boolean = false,
  content: @Composable () -> Unit,
) {
  MaterialTheme(colorScheme = EgyptColorScheme, typography = Typography, content = content)
}
