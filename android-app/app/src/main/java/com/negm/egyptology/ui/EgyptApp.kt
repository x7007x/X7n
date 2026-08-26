package com.negm.egyptology.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.negm.egyptology.data.EgyptItem
import com.negm.egyptology.data.EgyptRepository
import com.negm.egyptology.ui.components.FlatBottomNavigation
import com.negm.egyptology.ui.components.NavTab
import com.negm.egyptology.ui.screens.ArticleDetailScreen
import com.negm.egyptology.ui.screens.HomeScreen
import com.negm.egyptology.ui.screens.ViewAllScreen
import com.negm.egyptology.ui.theme.DarkPageBg

class EgyptAppState {
  var tab by mutableStateOf(NavTab.HOME)
  var exploreCategory by mutableStateOf("الكل")
  var selectedArticleId by mutableStateOf<String?>(null)
  val tabHistory = mutableStateListOf(NavTab.HOME)
  val bookmarkedIds = mutableStateListOf("king_ramses2", "pyr_khufu", "art_mask")

  fun selectTab(nextTab: NavTab) {
    if (nextTab == tab) return
    tabHistory.add(nextTab)
    tab = nextTab
    selectedArticleId = null
  }

  fun goBack() {
    when {
      selectedArticleId != null -> selectedArticleId = null
      tabHistory.size > 1 -> {
        tabHistory.removeAt(tabHistory.lastIndex)
        tab = tabHistory.last()
      }
      tab != NavTab.HOME -> tab = NavTab.HOME
    }
  }

  fun toggleBookmark(id: String) {
    if (bookmarkedIds.contains(id)) bookmarkedIds.remove(id) else bookmarkedIds.add(id)
  }
}

@Composable
fun EgyptApp() {
  val context = LocalContext.current
  val catalog = remember { EgyptRepository.catalog(context.applicationContext) }
  val state = remember { EgyptAppState() }
  val selectedArticle = state.selectedArticleId?.let { id -> catalog.items.firstOrNull { it.id == id } }

  var lastArticle by remember { mutableStateOf<EgyptItem?>(null) }
  selectedArticle?.let { lastArticle = it }

  BackHandler(
    enabled = selectedArticle != null || state.tabHistory.size > 1 || state.tab != NavTab.HOME
  ) {
    state.goBack()
  }

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    containerColor = DarkPageBg,
    bottomBar = {
      FlatBottomNavigation(
        selectedTab = state.tab,
        onTabSelected = { state.selectTab(it) }
      )
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .background(DarkPageBg)
    ) {
      AnimatedContent(
        targetState = state.tab,
        transitionSpec = {
          val from = tabsOrder.indexOf(initialState).let { if (it < 0) 0 else it }
          val to = tabsOrder.indexOf(targetState).let { if (it < 0) 0 else it }
          val direction = if (to >= from) -1 else 1
          (
            slideInHorizontally(
              initialOffsetX = { direction * it / 10 },
              animationSpec = tween(260, easing = FastOutSlowInEasing)
            ) + fadeIn(tween(220))
          ) togetherWith (
            slideOutHorizontally(
              targetOffsetX = { -direction * it / 10 },
              animationSpec = tween(220)
            ) + fadeOut(tween(170))
          )
        },
        label = "tab_transition"
      ) { currentTab ->
        when (currentTab) {
          NavTab.HOME -> HomeScreen(
            catalog = catalog,
            bookmarkedIds = state.bookmarkedIds.toSet(),
            onBookmarkToggle = state::toggleBookmark,
            onItemClick = { item -> state.selectedArticleId = item.id },
            onViewAllClick = { category ->
              state.exploreCategory = category
              state.selectTab(NavTab.EXPLORE)
            }
          )
          NavTab.EXPLORE, NavTab.FAVORITES -> ViewAllScreen(
            catalog = catalog,
            initialCategory = state.exploreCategory,
            bookmarkedIds = state.bookmarkedIds.toSet(),
            onBookmarkToggle = state::toggleBookmark,
            onItemClick = { item -> state.selectedArticleId = item.id },
            onBack = { state.goBack() },
            onShowFavorites = {
              state.selectTab(if (currentTab == NavTab.EXPLORE) NavTab.FAVORITES else NavTab.EXPLORE)
            },
            favoritesOnly = currentTab == NavTab.FAVORITES
          )
        }
      }

      AnimatedVisibility(
        visible = selectedArticle != null && lastArticle != null,
        enter = slideInVertically(
          initialOffsetY = { it / 8 },
          animationSpec = tween(280, easing = FastOutSlowInEasing)
        ) + fadeIn(tween(240)),
        exit = slideOutVertically(
          targetOffsetY = { it / 10 },
          animationSpec = tween(200)
        ) + fadeOut(tween(160)),
        label = "detail_overlay"
      ) {
        lastArticle?.let { article ->
          ArticleDetailScreen(
            item = article,
            article = EgyptRepository.articleFor(context, article.id),
            isBookmarked = state.bookmarkedIds.contains(article.id),
            onBookmarkToggle = { state.toggleBookmark(article.id) },
            onShareClick = {
              com.negm.egyptology.ui.components.shareEgyptArticle(context, article)
            },
            onBack = state::goBack
          )
        }
      }
    }
  }
}

private val tabsOrder = listOf(NavTab.EXPLORE, NavTab.HOME, NavTab.FAVORITES)
