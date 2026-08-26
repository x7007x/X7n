package com.negm.egyptology.ui.screens

import androidx.compose.runtime.Composable
import com.negm.egyptology.data.Catalog
import com.negm.egyptology.data.EgyptItem

@Composable
fun FavoritesScreen(
  catalog: Catalog,
  bookmarkedIds: Set<String>,
  onBookmarkToggle: (String) -> Unit,
  onItemClick: (EgyptItem) -> Unit,
  onExploreClick: () -> Unit,
  onShowAllClick: () -> Unit
) {
  ViewAllScreen(
    catalog = catalog,
    initialCategory = "الكل",
    bookmarkedIds = bookmarkedIds,
    onBookmarkToggle = onBookmarkToggle,
    onItemClick = onItemClick,
    onBack = onExploreClick,
    onShowFavorites = onShowAllClick,
    favoritesOnly = true
  )
}
