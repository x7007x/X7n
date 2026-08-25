package com.negm.egyptology.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.negm.egyptology.ui.theme.GlassBorderColor
import com.negm.egyptology.ui.theme.GoldMain
import com.negm.egyptology.ui.theme.TextMutedColor

/** Bottom destinations, ordered right-to-left for the RTL layout. */
enum class NavTab(
  val label: String,
  val activeIcon: ImageVector,
  val inactiveIcon: ImageVector
) {
  MAP("خريطة مصر", Icons.Outlined.Explore, Icons.Outlined.Explore),
  FAVORITES("المفضلة", Icons.Filled.Favorite, Icons.Outlined.FavoriteBorder),
  HOME("الرئيسية", Icons.Filled.Home, Icons.Outlined.Home),
  QUIZ("الاختبارات", Icons.Filled.Assignment, Icons.Outlined.Assignment),
  MORE("المزيد", Icons.Filled.MoreHoriz, Icons.Outlined.MoreHoriz);

  companion object {
    fun fromLabel(label: String): NavTab =
      entries.firstOrNull { it.label == label } ?: HOME
  }
}

// -------------------------------------------------------------
// DYNAMIC CURVED BOTTOM NAVIGATION (SPRING ANIMATION + GLOW)
// -------------------------------------------------------------
@Composable
fun CurvedBottomNavigation(
  selectedTab: NavTab,
  onTabSelected: (NavTab) -> Unit
) {
  val tabs = NavTab.entries.toList()
  val haptics = LocalHapticFeedback.current
  val selectedIndex = tabs.indexOf(selectedTab).coerceAtLeast(0)

  // Bouncy spring drives the arch so it glides between tabs.
  val springSpec = spring<Float>(
    dampingRatio = Spring.DampingRatioMediumBouncy,
    stiffness = Spring.StiffnessLow
  )
  val animatedIndex by animateFloatAsState(
    targetValue = selectedIndex.toFloat(),
    animationSpec = springSpec,
    label = "nav_active_index"
  )

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 14.dp, vertical = 12.dp),
    contentAlignment = Alignment.BottomCenter
  ) {
    // Custom Canvas drawing the navbar with a curved arch that follows the active tab
    Canvas(
      modifier = Modifier
        .fillMaxWidth()
        .height(70.dp)
    ) {
      val w = size.width
      val h = size.height
      val cornerRadius = 35.dp.toPx()
      val itemCount = tabs.size
      val itemWidth = w / itemCount

      // Center X of active arch (RTL: index 0 sits on the rightmost edge)
      val activeCenterX = w - itemWidth * (animatedIndex + 0.5f)
      val archHalfWidth = 32.dp.toPx()
      val archPeakHeight = 12.dp.toPx()

      val path = Path().apply {
        moveTo(0f, h - cornerRadius)
        lineTo(0f, cornerRadius)
        arcTo(
          rect = Rect(0f, 0f, cornerRadius * 2, cornerRadius * 2),
          startAngleDegrees = 180f,
          sweepAngleDegrees = 90f,
          forceMoveTo = false
        )
        lineTo(activeCenterX - archHalfWidth, 0f)
        cubicTo(
          activeCenterX - archHalfWidth * 0.5f, 0f,
          activeCenterX - archHalfWidth * 0.35f, -archPeakHeight,
          activeCenterX, -archPeakHeight
        )
        cubicTo(
          activeCenterX + archHalfWidth * 0.35f, -archPeakHeight,
          activeCenterX + archHalfWidth * 0.5f, 0f,
          activeCenterX + archHalfWidth, 0f
        )
        lineTo(w - cornerRadius, 0f)
        arcTo(
          rect = Rect(w - cornerRadius * 2, 0f, w, cornerRadius * 2),
          startAngleDegrees = 270f,
          sweepAngleDegrees = 90f,
          forceMoveTo = false
        )
        lineTo(w, h - cornerRadius)
        arcTo(
          rect = Rect(w - cornerRadius * 2, h - cornerRadius * 2, w, h),
          startAngleDegrees = 0f,
          sweepAngleDegrees = 90f,
          forceMoveTo = false
        )
        lineTo(cornerRadius, h)
        arcTo(
          rect = Rect(0f, h - cornerRadius * 2, cornerRadius * 2, h),
          startAngleDegrees = 90f,
          sweepAngleDegrees = 90f,
          forceMoveTo = false
        )
        close()
      }

      // Soft outer glow behind the glass bar
      drawPath(
        path = path,
        brush = Brush.verticalGradient(
          colors = listOf(GoldMain.copy(alpha = 0.10f), Color.Transparent)
        ),
        style = Stroke(width = 6.dp.toPx())
      )

      // Glass dark fill
      drawPath(path = path, color = Color(0xF214161E))

      // Golden gradient border
      drawPath(
        path = path,
        brush = Brush.linearGradient(
          colors = listOf(
            GoldMain.copy(alpha = 0.55f),
            GlassBorderColor,
            GoldMain.copy(alpha = 0.55f)
          )
        ),
        style = Stroke(width = 1.4.dp.toPx())
      )
    }

    // Interactive items row
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .height(70.dp),
      horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceEvenly,
      verticalAlignment = Alignment.CenterVertically
    ) {
      tabs.forEachIndexed { index, tab ->
        val isSelected = tab == selectedTab

        val iconScale by animateFloatAsState(
          targetValue = if (isSelected) 1f else 0.86f,
          animationSpec = if (isSelected) springSpec else tween(200),
          label = "nav_icon_scale_$index"
        )
        val tint by animateColorAsState(
          targetValue = if (isSelected) GoldMain else TextMutedColor.copy(alpha = 0.75f),
          animationSpec = tween(220),
          label = "nav_tint_$index"
        )
        val labelAlpha by animateFloatAsState(
          targetValue = if (isSelected) 1f else 0.65f,
          animationSpec = tween(220),
          label = "nav_label_alpha_$index"
        )

        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
          modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .clickable(
              interactionSource = remember { MutableInteractionSource() },
              indication = null
            ) {
              if (!isSelected) {
                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onTabSelected(tab)
              }
            }
            .padding(vertical = 4.dp)
        ) {
          Box(contentAlignment = Alignment.Center) {
            if (isSelected) {
              // Golden halo behind the active icon
              Box(
                modifier = Modifier
                  .size(46.dp)
                  .clip(CircleShape)
                  .background(
                    Brush.radialGradient(
                      listOf(GoldMain.copy(alpha = 0.45f), Color.Transparent)
                    )
                  )
              )
            }
            Icon(
              imageVector = tab.activeIcon,
              contentDescription = tab.label,
              tint = tint,
              modifier = Modifier
                .size(24.dp)
                .graphicsLayer {
                  scaleX = iconScale
                  scaleY = iconScale
                  translationY = if (isSelected) (-12).dp.toPx() else 0f
                }
            )
          }

          Spacer(modifier = Modifier.height(if (isSelected) 16.dp else 4.dp))

          Text(
            text = tab.label,
            style = MaterialTheme.typography.labelSmall,
            color = tint,
            fontSize = if (isSelected) 10.5.sp else 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            maxLines = 1,
            modifier = Modifier.graphicsLayer { alpha = labelAlpha }
          )
        }
      }
    }
  }
}
