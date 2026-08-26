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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.negm.egyptology.ui.theme.GlassBorderColor
import com.negm.egyptology.ui.theme.GoldMain
import com.negm.egyptology.ui.theme.TextMutedColor
import kotlin.math.min

enum class NavTab(val label: String, val activeIcon: ImageVector, val inactiveIcon: ImageVector) {
  MAP("خريطة مصر", Icons.Outlined.Explore, Icons.Outlined.Explore),
  FAVORITES("المفضلة", Icons.Filled.Favorite, Icons.Outlined.FavoriteBorder),
  HOME("الرئيسية", Icons.Filled.Home, Icons.Outlined.Home),
  QUIZ("الاختبارات", Icons.Filled.Assignment, Icons.Outlined.Assignment),
  MORE("المزيد", Icons.Filled.MoreHoriz, Icons.Outlined.MoreHoriz)
}

@Composable
fun CurvedBottomNavigation(selectedTab: NavTab, onTabSelected: (NavTab) -> Unit) {
  val tabs = NavTab.entries
  val haptics = LocalHapticFeedback.current
  val selectedIndex = tabs.indexOf(selectedTab).coerceAtLeast(0)
  val barHeight = 70.dp
  val archRoom = 18.dp
  val animation = tween<Float>(280, easing = FastOutSlowInEasing)
  val animatedIndex by animateFloatAsState(selectedIndex.toFloat(), animation, label = "navIndex")

  Box(
    modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(horizontal = 14.dp, vertical = 12.dp),
    contentAlignment = Alignment.BottomCenter
  ) {
    Canvas(Modifier.fillMaxWidth().height(barHeight + archRoom)) {
      val width = size.width
      val height = barHeight.toPx()
      val room = archRoom.toPx()
      val slot = width / tabs.size
      val corner = min(height / 2f, width / 14f)
      val bumpRadius = min(slot * 0.22f, room * 0.92f)
      val center = (width - slot * (animatedIndex + 0.5f)).coerceIn(
        corner + bumpRadius + 1.dp.toPx(),
        width - corner - bumpRadius - 1.dp.toPx()
      )
      val left = center - bumpRadius
      val right = center + bumpRadius
      val bumpHeight = bumpRadius * 0.72f
      val circleControl = bumpRadius * 0.5522848f
      val path = Path().apply {
        moveTo(0f, height - corner)
        lineTo(0f, corner)
        arcTo(Rect(0f, 0f, corner * 2f, corner * 2f), 180f, 90f, false)
        lineTo(left, 0f)
        cubicTo(left + circleControl, 0f, center - circleControl, -bumpHeight, center, -bumpHeight)
        cubicTo(center + circleControl, -bumpHeight, right - circleControl, 0f, right, 0f)
        lineTo(width - corner, 0f)
        arcTo(Rect(width - corner * 2f, 0f, width, corner * 2f), 270f, 90f, false)
        lineTo(width, height - corner)
        arcTo(Rect(width - corner * 2f, height - corner * 2f, width, height), 0f, 90f, false)
        lineTo(corner, height)
        arcTo(Rect(0f, height - corner * 2f, corner * 2f, height), 90f, 90f, false)
        close()
      }
      translate(top = room) {
        drawPath(path, color = Color(0xF214161E))
        drawPath(
          path,
          brush = Brush.linearGradient(listOf(GoldMain.copy(alpha = 0.72f), GlassBorderColor, GoldMain.copy(alpha = 0.72f))),
          style = Stroke(width = 1.35.dp.toPx())
        )
      }
    }

    Row(
      modifier = Modifier.fillMaxWidth().height(barHeight).align(Alignment.BottomCenter),
      horizontalArrangement = Arrangement.SpaceEvenly,
      verticalAlignment = Alignment.CenterVertically
    ) {
      tabs.forEachIndexed { index, tab ->
        val selected = tab == selectedTab
        val tint by animateColorAsState(if (selected) GoldMain else TextMutedColor.copy(alpha = 0.75f), tween(220), label = "navTint$index")
        Column(
          modifier = Modifier.weight(1f).fillMaxHeight().clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null
          ) {
            if (!selected) {
              haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
              onTabSelected(tab)
            }
          },
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center
        ) {
          Icon(
            imageVector = if (selected) tab.activeIcon else tab.inactiveIcon,
            contentDescription = tab.label,
            tint = tint,
            modifier = Modifier.size(22.dp)
          )
          androidx.compose.foundation.layout.Spacer(Modifier.height(5.dp))
          Text(tab.label, style = MaterialTheme.typography.labelSmall, color = tint, fontWeight = FontWeight.Medium, maxLines = 1)
        }
      }
    }
  }
}
