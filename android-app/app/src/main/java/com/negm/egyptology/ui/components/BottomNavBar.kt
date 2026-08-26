package com.negm.egyptology.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.core.animateDpAsState
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.negm.egyptology.ui.theme.GlassBorderColor
import com.negm.egyptology.ui.theme.GoldMain
import com.negm.egyptology.ui.theme.TextMutedColor

enum class NavTab(val label: String, val activeIcon: ImageVector, val inactiveIcon: ImageVector) {
  MAP("خريطة مصر", Icons.Outlined.Explore, Icons.Outlined.Explore),
  FAVORITES("المفضلة", Icons.Filled.Favorite, Icons.Outlined.FavoriteBorder),
  HOME("الرئيسية", Icons.Filled.Home, Icons.Outlined.Home),
  QUIZ("الاختبارات", Icons.Filled.Assignment, Icons.Outlined.Assignment),
  MORE("المزيد", Icons.Filled.MoreHoriz, Icons.Outlined.MoreHoriz)
}

@Composable
fun FlatBottomNavigation(selectedTab: NavTab, onTabSelected: (NavTab) -> Unit) {
  val tabs = NavTab.entries
  val haptics = LocalHapticFeedback.current
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .navigationBarsPadding()
      .padding(horizontal = 14.dp, vertical = 12.dp)
      .height(70.dp),
    contentAlignment = Alignment.Center
  ) {
    Canvas(modifier = Modifier.matchParentSize()) {
      drawRoundRect(
        color = Color.Transparent,
        cornerRadius = CornerRadius(35.dp.toPx(), 35.dp.toPx()),
        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.4.dp.toPx())
      )
      drawRoundRect(
        color = GoldMain.copy(alpha = 0.66f),
        cornerRadius = CornerRadius(35.dp.toPx(), 35.dp.toPx()),
        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 0.55.dp.toPx())
      )
    }

    Row(
      modifier = Modifier.fillMaxWidth().fillMaxHeight(),
      horizontalArrangement = Arrangement.SpaceEvenly,
      verticalAlignment = Alignment.CenterVertically
    ) {
      tabs.forEachIndexed { index, tab ->
        val selected = tab == selectedTab
        val tint by animateColorAsState(
          targetValue = if (selected) GoldMain else TextMutedColor.copy(alpha = 0.76f),
          animationSpec = tween(220),
          label = "navTint$index"
        )
        val iconSize by animateDpAsState(
          targetValue = if (selected) 26.dp else 22.dp,
          animationSpec = tween(240),
          label = "navIconSize$index"
        )
        Column(
          modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .clickable(
              interactionSource = remember { MutableInteractionSource() },
              indication = null
            ) {
              if (!selected) {
                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onTabSelected(tab)
              }
            },
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = if (selected) Arrangement.Center else Arrangement.Center
        ) {
          Icon(
            imageVector = if (selected) tab.activeIcon else tab.inactiveIcon,
            contentDescription = tab.label,
            tint = tint,
            modifier = Modifier.size(iconSize)
          )
          AnimatedVisibility(
            visible = !selected,
            enter = androidx.compose.animation.fadeIn(tween(180)) + expandVertically(tween(180)),
            exit = androidx.compose.animation.fadeOut(tween(140)) + shrinkVertically(tween(140)),
            label = "navLabelVisibility$index"
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              androidx.compose.foundation.layout.Spacer(Modifier.height(4.dp))
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
  }
}
