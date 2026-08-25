package com.negm.egyptology.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
// DYNAMIC CURVED BOTTOM NAVIGATION
// - Transparent outside the glass frame (only the arch rises out).
// - Arch glides between tabs with a bouncy spring and lands exactly
//   centered over the selected tab.
// - Only the icon reacts to selection: it grows and lifts upward.
// -------------------------------------------------------------
@Composable
fun CurvedBottomNavigation(
  selectedTab: NavTab,
  onTabSelected: (NavTab) -> Unit
) {
  val tabs = NavTab.entries.toList()
  val haptics = LocalHapticFeedback.current
  val selectedIndex = tabs.indexOf(selectedTab).coerceAtLeast(0)

  val barHeight = 70.dp
  val archRoom = 20.dp // transparent headroom the rising arch lives in

  val selectionSpec = tween<Float>(
    durationMillis = 280,
    easing = androidx.compose.animation.core.FastOutSlowInEasing
  )
  val animatedIndex by animateFloatAsState(
    targetValue = selectedIndex.toFloat(),
    animationSpec = selectionSpec,
    label = "nav_active_index"
  )

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .navigationBarsPadding()
      .padding(horizontal = 14.dp, vertical = 12.dp),
    contentAlignment = Alignment.BottomCenter
  ) {
    // Glass frame + moving arch. Extra top headroom keeps the arch unclipped;
    // nothing is filled outside the path, so the surroundings stay transparent.
    Canvas(
      modifier = Modifier
        .fillMaxWidth()
        .height(barHeight + archRoom)
    ) {
      val w = size.width
      val barH = barHeight.toPx()
      val room = archRoom.toPx()
      val itemCount = tabs.size
      val itemWidth = w / itemCount

      // Exact horizontal center of the selected tab (RTL: index 0 = right edge).
      val activeCenterX = w - itemWidth * (animatedIndex + 0.5f)
      val archHalfWidth = itemWidth * 0.34f
      val archPeakHeight = 10.dp.toPx()

      val cornerR = barH / 2f // fully rounded pill frame
      val path = Path().apply {
        moveTo(0f, barH - cornerR)
        lineTo(0f, cornerR)
        arcTo(
          rect = Rect(0f, 0f, cornerR * 2, cornerR * 2),
          startAngleDegrees = 180f,
          sweepAngleDegrees = 90f,
          forceMoveTo = false
        )
        lineTo(activeCenterX - archHalfWidth, 0f)
        // Smooth cubic arch rising around the active tab
        cubicTo(
          activeCenterX - archHalfWidth * 0.45f, 0f,
          activeCenterX - archHalfWidth * 0.30f, -archPeakHeight,
          activeCenterX, -archPeakHeight
        )
        cubicTo(
          activeCenterX + archHalfWidth * 0.30f, -archPeakHeight,
          activeCenterX + archHalfWidth * 0.45f, 0f,
          activeCenterX + archHalfWidth, 0f
        )
        lineTo(w - cornerR, 0f)
        arcTo(
          rect = Rect(w - cornerR * 2, 0f, w, cornerR * 2),
          startAngleDegrees = 270f,
          sweepAngleDegrees = 90f,
          forceMoveTo = false
        )
        lineTo(w, barH - cornerR)
        arcTo(
          rect = Rect(w - cornerR * 2, barH - cornerR * 2, w, barH),
          startAngleDegrees = 0f,
          sweepAngleDegrees = 90f,
          forceMoveTo = false
        )
        lineTo(cornerR, barH)
        arcTo(
          rect = Rect(0f, barH - cornerR * 2, cornerR * 2, barH),
          startAngleDegrees = 90f,
          sweepAngleDegrees = 90f,
          forceMoveTo = false
        )
        close()
      }

      // Everything is drawn translated down by `room`, so negative-y arch
      // coordinates land inside the canvas instead of being clipped.
      translate(top = room) {
        // Glass dark fill (frame only - transparent everywhere else)
        drawPath(path, color = Color(0xF214161E))

        // Thin golden gradient border following the frame + arch
        drawPath(
          path = path,
          brush = Brush.linearGradient(
            colors = listOf(
              GoldMain.copy(alpha = 0.65f),
              GlassBorderColor,
              GoldMain.copy(alpha = 0.65f)
            )
          ),
          style = Stroke(width = 1.4.dp.toPx())
        )
      }
    }

    // Interactive items row aligned to the glass frame (bottom of the box)
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .height(barHeight)
        .align(Alignment.BottomCenter),
      horizontalArrangement = Arrangement.SpaceEvenly,
      verticalAlignment = Alignment.CenterVertically
    ) {
      tabs.forEachIndexed { index, tab ->
        val isSelected = tab == selectedTab

        // Only the icon reacts: it grows and lifts slightly higher.
        val iconScale by animateFloatAsState(
          targetValue = if (isSelected) 1.12f else 1f,
          animationSpec = if (isSelected) selectionSpec else tween(200),
          label = "nav_icon_scale_$index"
        )
        val iconLift by animateFloatAsState(
          targetValue = if (isSelected) 0.5f else 0f,
          animationSpec = if (isSelected) selectionSpec else tween(220),
          label = "nav_icon_lift_$index"
        )
        val tint by animateColorAsState(
          targetValue = if (isSelected) GoldMain else TextMutedColor.copy(alpha = 0.75f),
          animationSpec = tween(220),
          label = "nav_tint_$index"
        )

        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center,
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
        ) {
          Box(contentAlignment = Alignment.Center) {
            Icon(
              imageVector = if (isSelected) tab.activeIcon else tab.inactiveIcon,
              contentDescription = tab.label,
              tint = tint,
              modifier = Modifier
                .size(24.dp)
                .graphicsLayer {
                  scaleX = iconScale
                  scaleY = iconScale
                  translationY = -iconLift * 8.dp.toPx()
                }
            )
          }

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = tab.label,
            style = MaterialTheme.typography.labelSmall,
            color = tint,
            fontWeight = FontWeight.Medium,
            maxLines = 1
          )
        }
      }
    }
  }
}
