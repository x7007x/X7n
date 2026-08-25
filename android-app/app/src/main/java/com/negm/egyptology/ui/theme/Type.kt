package com.negm.egyptology.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.negm.egyptology.R

val OmnesFontFamily = FontFamily(
  Font(R.font.omnes, FontWeight.Normal),
  Font(R.font.omnes, FontWeight.Medium),
  Font(R.font.omnes, FontWeight.SemiBold),
  Font(R.font.omnes, FontWeight.Bold)
)

// Consistent type scale (sp) applied on top of the bundled Omnes font so every
// screen follows one rhythm instead of scattered inline sizes.
private val Base = Typography(
  displayLarge = Typography().displayLarge.copy(fontSize = 52.sp, lineHeight = 58.sp),
  displayMedium = Typography().displayMedium.copy(fontSize = 42.sp, lineHeight = 48.sp),
  displaySmall = Typography().displaySmall.copy(fontSize = 34.sp, lineHeight = 40.sp),
  headlineLarge = Typography().headlineLarge.copy(fontSize = 30.sp, lineHeight = 36.sp),
  headlineMedium = Typography().headlineMedium.copy(fontSize = 25.sp, lineHeight = 31.sp),
  headlineSmall = Typography().headlineSmall.copy(fontSize = 21.sp, lineHeight = 27.sp),
  titleLarge = Typography().titleLarge.copy(fontSize = 20.sp, lineHeight = 26.sp, fontWeight = FontWeight.Bold),
  titleMedium = Typography().titleMedium.copy(fontSize = 16.5.sp, lineHeight = 22.sp, fontWeight = FontWeight.Bold),
  titleSmall = Typography().titleSmall.copy(fontSize = 14.sp, lineHeight = 19.sp, fontWeight = FontWeight.SemiBold),
  bodyLarge = Typography().bodyLarge.copy(fontSize = 15.5.sp, lineHeight = 23.sp),
  bodyMedium = Typography().bodyMedium.copy(fontSize = 13.5.sp, lineHeight = 20.sp),
  bodySmall = Typography().bodySmall.copy(fontSize = 12.sp, lineHeight = 17.sp),
  labelLarge = Typography().labelLarge.copy(fontSize = 13.5.sp, lineHeight = 18.sp),
  labelMedium = Typography().labelMedium.copy(fontSize = 12.sp, lineHeight = 16.sp),
  labelSmall = Typography().labelSmall.copy(fontSize = 10.5.sp, lineHeight = 14.sp)
)

val Typography = Base.copy(
  displayLarge = Base.displayLarge.copy(fontFamily = OmnesFontFamily),
  displayMedium = Base.displayMedium.copy(fontFamily = OmnesFontFamily),
  displaySmall = Base.displaySmall.copy(fontFamily = OmnesFontFamily),
  headlineLarge = Base.headlineLarge.copy(fontFamily = OmnesFontFamily),
  headlineMedium = Base.headlineMedium.copy(fontFamily = OmnesFontFamily),
  headlineSmall = Base.headlineSmall.copy(fontFamily = OmnesFontFamily),
  titleLarge = Base.titleLarge.copy(fontFamily = OmnesFontFamily),
  titleMedium = Base.titleMedium.copy(fontFamily = OmnesFontFamily),
  titleSmall = Base.titleSmall.copy(fontFamily = OmnesFontFamily),
  bodyLarge = Base.bodyLarge.copy(fontFamily = OmnesFontFamily),
  bodyMedium = Base.bodyMedium.copy(fontFamily = OmnesFontFamily),
  bodySmall = Base.bodySmall.copy(fontFamily = OmnesFontFamily),
  labelLarge = Base.labelLarge.copy(fontFamily = OmnesFontFamily),
  labelMedium = Base.labelMedium.copy(fontFamily = OmnesFontFamily),
  labelSmall = Base.labelSmall.copy(fontFamily = OmnesFontFamily)
)
