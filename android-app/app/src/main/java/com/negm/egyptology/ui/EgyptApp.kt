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
class EgyptAppState {
  var tab by mutableStateOf(NavTab.HOME)
  var viewAllCategory by mutableStateOf<String?>(null)
  var selectedArticleId by mutableStateOf<String?>(null)
  val tabHistory = mutableStateListOf(NavTab.HOME)

  fun selectTab(nextTab: NavTab) {
    if (nextTab == tab) return
    tabHistory.add(nextTab)
    tab = nextTab
    viewAllCategory = null
    selectedArticleId = null
  }

  fun goBack() {
    when {
      selectedArticleId != null -> selectedArticleId = null
      viewAllCategory != null -> viewAllCategory = null
      tabHistory.size > 1 -> {
        tabHistory.removeAt(tabHistory.lastIndex)
        tab = tabHistory.last()
      }
      tab != NavTab.HOME -> tab = NavTab.HOME
    }
  }

  val bookmarkedIds = mutableStateListOf("king_ramses2", "pyr_khufu", "art_mask")

  fun toggleBookmark(id: String) {
    if (bookmarkedIds.contains(id)) bookmarkedIds.remove(id) else bookmarkedIds.add(id)
  }
}

@Composable
fun EgyptApp() {
  val context = LocalContext.current
  val catalog = remember { EgyptRepository.catalog(context.applicationContext) }
  val state = remember { EgyptAppState() }

  val selectedArticle =
    state.selectedArticleId?.let { id -> catalog.items.firstOrNull { it.id == id } }

  // Keep the last opened article composed so the exit animation has content.
  var lastArticle by remember { mutableStateOf<EgyptItem?>(null) }
  selectedArticle?.let { lastArticle = it }

  BackHandler(
    enabled = selectedArticle != null || state.viewAllCategory != null || state.tabHistory.size > 1
  ) {
    state.goBack()
  }

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    containerColor = DarkPageBg,
    bottomBar = {
      CurvedBottomNavigation(
        selectedTab = if (state.viewAllCategory != null) NavTab.HOME else state.tab,
        onTabSelected = { tab -> state.selectTab(tab) }
      )
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .background(DarkPageBg)
    ) {
      // Animated movement between the main pages (RTL-aware slide + fade).
      AnimatedContent(
        targetState = state.tab,
        transitionSpec = {
          val from = tabsOrder.indexOf(initialState).let { if (it < 0) 0 else it }
          val to = tabsOrder.indexOf(targetState).let { if (it < 0) 0 else it }
          // RTL: forward movement enters from the left edge.
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
            onBookmarkToggle = { state.toggleBookmark(it) },
            onItemClick = { item -> state.selectedArticleId = item.id },
            onViewAllClick = { category -> state.viewAllCategory = category }
          )
          NavTab.FAVORITES -> FavoritesScreen(
            catalog = catalog,
            bookmarkedIds = state.bookmarkedIds.toSet(),
            onBookmarkToggle = { state.toggleBookmark(it) },
            onItemClick = { item -> state.selectedArticleId = item.id },
            onExploreClick = { state.selectTab(NavTab.HOME) },
            onShowAllClick = {
              state.selectTab(NavTab.HOME)
              state.viewAllCategory = "الكل"
            }
          )
          NavTab.MAP -> MapScreen(
            catalog = catalog,
            onItemClick = { item -> state.selectedArticleId = item.id }
          )
          NavTab.QUIZ -> QuizScreen(catalog)
          NavTab.MORE -> MoreScreen(catalog)
        }
      }

      // "View all" replaces the home list instantly while staying under the nav bar
      state.viewAllCategory?.let { category ->
        ViewAllScreen(
          catalog = catalog,
          initialCategory = category,
          bookmarkedIds = state.bookmarkedIds.toSet(),
          onBookmarkToggle = { state.toggleBookmark(it) },
          onItemClick = { item -> state.selectedArticleId = item.id },
          onBack = { state.viewAllCategory = null },
          onShowFavorites = {
            state.viewAllCategory = null
            state.selectTab(NavTab.FAVORITES)
          }
        )
      }

      // Detail screen rises from the bottom as an overlay layer
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
            onBack = { state.goBack() }
          )
        }
      }
    }
  }
}

private val tabsOrder =
  listOf(NavTab.MAP, NavTab.FAVORITES, NavTab.HOME, NavTab.QUIZ, NavTab.MORE)
