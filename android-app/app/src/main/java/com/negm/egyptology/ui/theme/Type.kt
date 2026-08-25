package com.negm.egyptology.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.negm.egyptology.R

val OmnesFontFamily = FontFamily(
  Font(R.font.omnes, FontWeight.Normal),
  Font(R.font.omnes, FontWeight.Medium),
  Font(R.font.omnes, FontWeight.SemiBold),
  Font(R.font.omnes, FontWeight.Bold)
)

private val BaseTypography = Typography()

val Typography = BaseTypography.copy(
  displayLarge = BaseTypography.displayLarge.copy(fontFamily = OmnesFontFamily),
  displayMedium = BaseTypography.displayMedium.copy(fontFamily = OmnesFontFamily),
  displaySmall = BaseTypography.displaySmall.copy(fontFamily = OmnesFontFamily),
  headlineLarge = BaseTypography.headlineLarge.copy(fontFamily = OmnesFontFamily),
  headlineMedium = BaseTypography.headlineMedium.copy(fontFamily = OmnesFontFamily),
  headlineSmall = BaseTypography.headlineSmall.copy(fontFamily = OmnesFontFamily),
  titleLarge = BaseTypography.titleLarge.copy(fontFamily = OmnesFontFamily, fontWeight = FontWeight.Bold),
  titleMedium = BaseTypography.titleMedium.copy(fontFamily = OmnesFontFamily, fontWeight = FontWeight.Bold),
  titleSmall = BaseTypography.titleSmall.copy(fontFamily = OmnesFontFamily),
  bodyLarge = BaseTypography.bodyLarge.copy(fontFamily = OmnesFontFamily),
  bodyMedium = BaseTypography.bodyMedium.copy(fontFamily = OmnesFontFamily),
  bodySmall = BaseTypography.bodySmall.copy(fontFamily = OmnesFontFamily),
  labelLarge = BaseTypography.labelLarge.copy(fontFamily = OmnesFontFamily),
  labelMedium = BaseTypography.labelMedium.copy(fontFamily = OmnesFontFamily),
  labelSmall = BaseTypography.labelSmall.copy(fontFamily = OmnesFontFamily)
)
