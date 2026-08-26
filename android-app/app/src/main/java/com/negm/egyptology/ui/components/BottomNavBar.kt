package com.negm.egyptology.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.negm.egyptology.ui.theme.BodyTextLight
import com.negm.egyptology.ui.theme.GlassBorderSubtle
import com.negm.egyptology.ui.theme.SecondarySurface
import com.negm.egyptology.ui.theme.TextMutedColor

enum class NavTab(val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
  EXPLORE("استكشاف", Icons.Outlined.Explore),
  HOME("الرئيسية", Icons.Outlined.Home),
  FAVORITES("المفضلة", Icons.Outlined.FavoriteBorder)
}

@Composable
fun FlatBottomNavigation(selectedTab: NavTab, onTabSelected: (NavTab) -> Unit) {
  val haptics = LocalHapticFeedback.current
  val shape = RoundedCornerShape(22.dp)

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .navigationBarsPadding()
      .padding(horizontal = 18.dp, vertical = 10.dp)
      .background(SecondarySurface, shape)
      .border(1.dp, GlassBorderSubtle, shape)
      .padding(horizontal = 8.dp, vertical = 6.dp),
    horizontalArrangement = Arrangement.SpaceEvenly,
    verticalAlignment = Alignment.CenterVertically
  ) {
    NavTab.entries.forEach { tab ->
      val selected = tab == selectedTab
      val tint by animateColorAsState(
        targetValue = if (selected) BodyTextLight else TextMutedColor,
        animationSpec = tween(220),
        label = "navColor_${tab.name}"
      )
      Box(
        modifier = Modifier
          .weight(1f)
          .height(48.dp)
          .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null
          ) {
            if (!selected) {
              haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
              onTabSelected(tab)
            }
          },
        contentAlignment = Alignment.Center
      ) {
        AnimatedContent(
          targetState = selected,
          transitionSpec = { androidx.compose.animation.fadeIn(tween(180)) togetherWith androidx.compose.animation.fadeOut(tween(140)) },
          label = "navContent_${tab.name}"
        ) { isSelected ->
          if (isSelected) {
            Icon(
              imageVector = tab.icon,
              contentDescription = tab.label,
              tint = tint,
              modifier = Modifier.height(30.dp)
            )
          } else {
            androidx.compose.foundation.layout.Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.Center
            ) {
              Icon(
                imageVector = tab.icon,
                contentDescription = tab.label,
                tint = tint,
                modifier = Modifier.height(23.dp)
              )
              Text(
                text = tab.label,
                color = tint,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.padding(top = 2.dp)
              )
            }
          }
        }
      }
    }
  }
}
