package com.negm.egyptology.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.negm.egyptology.ui.theme.GoldMain
import com.negm.egyptology.ui.theme.TextMutedColor


enum class NavTab(val label: String, val activeIcon: androidx.compose.ui.graphics.vector.ImageVector, val inactiveIcon: androidx.compose.ui.graphics.vector.ImageVector) {
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

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .navigationBarsPadding()
      .padding(horizontal = 24.dp, vertical = 12.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    tabs.forEachIndexed { index, tab ->
      val selected = selectedTab == tab
      val tint by animateColorAsState(
        targetValue = if (selected) GoldMain else TextMutedColor.copy(alpha = 0.76f),
        animationSpec = tween(220),
        label = "bottomNavColor$index"
      )
      Column(
        modifier = Modifier
          .size(44.dp)
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
        verticalArrangement = Arrangement.Center
      ) {
        Icon(
          imageVector = if (selected) tab.activeIcon else tab.inactiveIcon,
          contentDescription = tab.label,
          tint = tint,
          modifier = Modifier.size(25.dp)
        )
      }
    }
  }
}
