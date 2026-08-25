package com.negm.egyptology.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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

@Composable
fun CurvedBottomNavigation(
  selectedTab: NavTab,
  onTabSelected: (NavTab) -> Unit
) {
  val tabs = NavTab.entries.toList()
  val haptics = LocalHapticFeedback.current
  val selectedIndex = tabs.indexOf(selectedTab).coerceAtLeast(0)
  val barHeight = 70.dp
  val archRoom = 18.dp
  val selectionSpec = tween<Float>(
    durationMillis = 280,
    easing = FastOutSlowInEasing
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
    Canvas(
      modifier = Modifier
        .fillMaxWidth()
        .height(barHeight + archRoom)
    ) {
      val width = size.width
      val height = barHeight.toPx()
      val room = archRoom.toPx()
      val itemWidth = width / tabs.size
      val cornerRadius = height / 2f
      val bumpHalfWidth = (itemWidth * 0.32f).coerceAtMost((width - cornerRadius * 2f) / 2f)
      val requestedCenter = width - itemWidth * (animatedIndex + 0.5f)
      val center = requestedCenter.coerceIn(
        cornerRadius + bumpHalfWidth,
        width - cornerRadius - bumpHalfWidth
      )
      val start = center - bumpHalfWidth
      val end = center + bumpHalfWidth
      val bumpHeight = 10.dp.toPx()
      val quarterControl = bumpHalfWidth * 0.5522848f
      val path = Path().apply {
        moveTo(0f, height - cornerRadius)
        lineTo(0f, cornerRadius)
        arcTo(Rect(0f, 0f, cornerRadius * 2f, cornerRadius * 2f), 180f, 90f, false)
        lineTo(start, 0f)
        cubicTo(start + quarterControl, 0f, center - quarterControl, -bumpHeight, center, -bumpHeight)
        cubicTo(center + quarterControl, -bumpHeight, end - quarterControl, 0f, end, 0f)
        lineTo(width - cornerRadius, 0f)
        arcTo(Rect(width - cornerRadius * 2f, 0f, width, cornerRadius * 2f), 270f, 90f, false)
        lineTo(width, height - cornerRadius)
        arcTo(Rect(width - cornerRadius * 2f, height - cornerRadius * 2f, width, height), 0f, 90f, false)
        lineTo(cornerRadius, height)
        arcTo(Rect(0f, height - cornerRadius * 2f, cornerRadius * 2f, height), 90f, 90f, false)
        close()
      }

      translate(top = room) {
        drawPath(path, color = Color(0xF214161E))
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
        val iconScale by animateFloatAsState(
          targetValue = if (isSelected) 1.08f else 1f,
          animationSpec = if (isSelected) selectionSpec else tween(200),
          label = "nav_icon_scale_$index"
        )
        val iconLift by animateFloatAsState(
          targetValue = if (isSelected) 0.35f else 0f,
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
          Icon(
            imageVector = if (isSelected) tab.activeIcon else tab.inactiveIcon,
            contentDescription = tab.label,
            tint = tint,
            modifier = Modifier
              .size(24.dp)
              .graphicsLayer {
                scaleX = iconScale
                scaleY = iconScale
                translationY = -iconLift * 7.dp.toPx()
              }
          )
          androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(5.dp))
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
