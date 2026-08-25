package com.negm.egyptology.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import com.negm.egyptology.data.Catalog
import com.negm.egyptology.data.EgyptRepository
import com.negm.egyptology.ui.components.CurvedBottomNavigation
import com.negm.egyptology.ui.components.NavTab
import com.negm.egyptology.ui.screens.ArticleDetailScreen
import com.negm.egyptology.ui.screens.FavoritesScreen
import com.negm.egyptology.ui.screens.HomeScreen
import com.negm.egyptology.ui.screens.MapScreen
import com.negm.egyptology.ui.screens.MoreScreen
import com.negm.egyptology.ui.screens.QuizScreen
import com.negm.egyptology.ui.screens.ViewAllScreen
import com.negm.egyptology.ui.theme.DarkPageBg

/**
 * Root state holder: active tab, "view all" category, selected article and bookmarks.
 */
class EgyptAppState(catalog: Catalog) {
  var tab by mutableStateOf(NavTab.HOME)
  var viewAllCategory by mutableStateOf<String?>(null)
  var selectedArticleId by mutableStateOf<String?>(null)

  val bookmarkedIds = mutableStateListOf("king_ramses2", "pyr_khufu", "art_mask")

  fun toggleBookmark(id: String) {
    if (bookmarkedIds.contains(id)) bookmarkedIds.remove(id) else bookmarkedIds.add(id)
  }
}

@Composable
fun EgyptApp() {
  val context = LocalContext.current
  val catalog = remember { EgyptRepository.catalog(context.applicationContext) }
  val state = remember { EgyptAppState(catalog) }

  val selectedArticle =
    state.selectedArticleId?.let { id -> catalog.items.firstOrNull { it.id == id } }

  // System back navigation through the screen stack.
  BackHandler(
    enabled = selectedArticle != null || state.viewAllCategory != null || state.tab != NavTab.HOME
  ) {
    when {
      selectedArticle != null -> state.selectedArticleId = null
      state.viewAllCategory != null -> state.viewAllCategory = null
      else -> state.tab = NavTab.HOME
    }
  }

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    containerColor = DarkPageBg,
    bottomBar = {
      if (selectedArticle == null) {
        CurvedBottomNavigation(
          selectedTab = if (state.viewAllCategory != null) NavTab.HOME else state.tab,
          onTabSelected = { tab ->
            state.viewAllCategory = null
            state.selectedArticleId = null
            state.tab = tab
          }
        )
      }
    }
  ) { innerPadding ->
    // Stable key for animated navigation between screens
    val navKey = when {
      selectedArticle != null -> "detail:${selectedArticle.id}"
      state.viewAllCategory != null -> "view_all:${state.viewAllCategory}"
      else -> state.tab.name
    }

    AnimatedContent(
      targetState = navKey,
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding),
      transitionSpec = {
        if (targetState.startsWith("detail")) {
          // Detail screen slides up from the bottom edge
          slideInVertically(
            initialOffsetY = { it / 8 },
            animationSpec = tween(280, easing = FastOutSlowInEasing)
          ) + fadeIn(tween(240)) togetherWith fadeOut(tween(160))
        } else {
          // Tabs cross-fade with a gentle RTL horizontal slide
          fun orderOf(key: String): Int {
            val base = key.substringBefore(":")
            val tab = when {
              base.startsWith("view_all") -> NavTab.HOME
              base.startsWith("detail") -> NavTab.HOME
              else -> NavTab.entries.firstOrNull { it.name == base } ?: NavTab.HOME
            }
            return tabsOrder.indexOf(tab).let { if (it < 0) 0 else it }
          }
          val direction = if (orderOf(targetState) >= orderOf(initialState)) -1 else 1
          (
            slideInHorizontally(
              initialOffsetX = { direction * it / 14 },
              animationSpec = tween(260, easing = FastOutSlowInEasing)
            ) + fadeIn(tween(240))
            ) togetherWith (
            slideOutHorizontally(
              targetOffsetX = { -direction * it / 14 },
              animationSpec = tween(220)
            ) + fadeOut(tween(180))
            )
        }
      },
      label = "nav_screen_transition"
    ) { key ->
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(DarkPageBg)
      ) {
        when {
          key.startsWith("detail:") -> {
            selectedArticle?.let { articleItem ->
              ArticleDetailScreen(
                item = articleItem,
                article = EgyptRepository.articleFor(context, articleItem.id),
                isBookmarked = state.bookmarkedIds.contains(articleItem.id),
                onBookmarkToggle = { state.toggleBookmark(articleItem.id) },
                onShareClick = {
                  com.negm.egyptology.ui.components.shareEgyptArticle(context, articleItem)
                },
                onBack = { state.selectedArticleId = null }
              )
            }
          }
          key.startsWith("view_all") -> {
            ViewAllScreen(
              catalog = catalog,
              initialCategory = state.viewAllCategory ?: "الكل",
              bookmarkedIds = state.bookmarkedIds.toSet(),
              onBookmarkToggle = { state.toggleBookmark(it) },
              onItemClick = { item -> state.selectedArticleId = item.id },
              onBack = { state.viewAllCategory = null }
            )
          }
          state.tab == NavTab.HOME -> {
            HomeScreen(
              catalog = catalog,
              bookmarkedIds = state.bookmarkedIds.toSet(),
              onBookmarkToggle = { state.toggleBookmark(it) },
              onItemClick = { item -> state.selectedArticleId = item.id },
              onViewAllClick = { category -> state.viewAllCategory = category }
            )
          }
          state.tab == NavTab.FAVORITES -> {
            FavoritesScreen(
              catalog = catalog,
              bookmarkedIds = state.bookmarkedIds.toSet(),
              onBookmarkToggle = { state.toggleBookmark(it) },
              onItemClick = { item -> state.selectedArticleId = item.id },
              onExploreClick = { state.tab = NavTab.HOME }
            )
          }
          state.tab == NavTab.MAP -> {
            MapScreen(
              catalog = catalog,
              onItemClick = { item -> state.selectedArticleId = item.id }
            )
          }
          state.tab == NavTab.QUIZ -> QuizScreen(catalog)
          state.tab == NavTab.MORE -> MoreScreen(catalog)
        }
      }
    }
  }
}

private val tabsOrder = listOf(NavTab.MAP, NavTab.FAVORITES, NavTab.HOME, NavTab.QUIZ, NavTab.MORE)
