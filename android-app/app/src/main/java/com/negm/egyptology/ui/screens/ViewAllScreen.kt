package com.negm.egyptology.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material.icons.outlined.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.negm.egyptology.data.Catalog
import com.negm.egyptology.data.EgyptItem
import com.negm.egyptology.ui.components.EgyptItemCard
import com.negm.egyptology.ui.components.SearchBarSection
import com.negm.egyptology.ui.components.shareEgyptArticle
import com.negm.egyptology.ui.theme.DarkPageBg
import com.negm.egyptology.ui.theme.GlassBorderColor
import com.negm.egyptology.ui.theme.GlassDarkBg
import com.negm.egyptology.ui.theme.GoldMain
import com.negm.egyptology.ui.theme.TextMutedColor

@Composable
fun ViewAllScreen(
  catalog: Catalog,
  initialCategory: String,
  bookmarkedIds: Set<String>,
  onBookmarkToggle: (String) -> Unit,
  onItemClick: (EgyptItem) -> Unit,
  onBack: () -> Unit,
  onShowFavorites: () -> Unit,
  favoritesOnly: Boolean = false,
  onShowAll: () -> Unit = {}
) {
  val context = LocalContext.current
  var selectedCategory by remember(initialCategory, favoritesOnly) { mutableStateOf(initialCategory) }
  var searchQuery by remember(favoritesOnly) { mutableStateOf("") }
  val sourceItems = remember(catalog, bookmarkedIds, favoritesOnly) {
    if (favoritesOnly) catalog.items.filter { it.id in bookmarkedIds } else catalog.items
  }
  val categories = remember(sourceItems) { listOf("الكل") + sourceItems.map { it.category }.distinct() }
  if (selectedCategory !in categories) selectedCategory = "الكل"
  val displayedItems = remember(sourceItems, selectedCategory, searchQuery) {
    val q = searchQuery.trim()
    val categoryItems = if (selectedCategory == "الكل") sourceItems else sourceItems.filter { it.category == selectedCategory }
    if (q.isBlank()) categoryItems else categoryItems.filter {
      it.title.contains(q, true) || it.tag1.contains(q, true) || it.tag2.contains(q, true) || it.subtitle.contains(q, true)
    }
  }

  Column(modifier = Modifier.fillMaxSize().background(DarkPageBg)) {
    Row(
      modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 12.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(
          onClick = onBack,
          modifier = Modifier.size(36.dp).clip(CircleShape).background(GlassDarkBg).border(1.dp, GlassBorderColor, CircleShape)
        ) {
          Icon(Icons.AutoMirrored.Filled.ArrowBack, "رجوع", GoldMain, Modifier.size(18.dp))
        }
        Spacer(Modifier.width(10.dp))
        Column {
          Text(if (favoritesOnly) "المفضلة" else "كل العناصر", MaterialTheme.typography.titleLarge, FontWeight.Bold, GoldMain)
          Text("${displayedItems.size} عنصر", MaterialTheme.typography.bodySmall, color = TextMutedColor)
        }
      }
      Row(verticalAlignment = Alignment.CenterVertically) {
        TextButton(onClick = if (favoritesOnly) onShowAll else onShowFavorites) {
          Text(if (favoritesOnly) "الكل" else "المفضلة", style = MaterialTheme.typography.labelLarge, color = GoldMain)
        }
        Icon(
          imageVector = if (favoritesOnly) Icons.Outlined.Bookmark else Icons.Outlined.WorkspacePremium,
          contentDescription = null,
          tint = GoldMain,
          modifier = Modifier.size(22.dp)
        )
      }
    }

    SearchBarSection(query = searchQuery, onQueryChange = { searchQuery = it })
    LazyRow(
      modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
      contentPadding = PaddingValues(horizontal = 18.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      items(categories) { category ->
        FilterChip(category, category == selectedCategory) { selectedCategory = category }
      }
    }

    LazyColumn(modifier = Modifier.fillMaxWidth().weight(1f)) {
      if (displayedItems.isEmpty()) {
        item {
          Box(Modifier.fillMaxWidth().padding(top = 60.dp), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Icon(Icons.Outlined.SearchOff, "لا توجد نتائج", TextMutedColor, Modifier.size(40.dp))
              Spacer(Modifier.height(10.dp))
              Text(if (favoritesOnly) "لا توجد عناصر محفوظة" else "لا توجد نتائج مطابقة", color = TextMutedColor)
            }
          }
        }
      } else {
        item {
          AnimatedContent(
            targetState = displayedItems,
            transitionSpec = { fadeIn(tween(220)) togetherWith fadeOut(tween(140)) },
            label = "collectionItemsTransition"
          ) { itemsForFilter ->
            Column {
              itemsForFilter.forEach { item ->
                EgyptItemCard(
                  item = item,
                  isBookmarked = item.id in bookmarkedIds,
                  onBookmarkToggle = { onBookmarkToggle(item.id) },
                  onShareClick = { shareEgyptArticle(context, item) },
                  onClick = { onItemClick(item) }
                )
              }
            }
          }
        }
      }
      item { Spacer(Modifier.height(115.dp)) }
    }
  }
}

@Composable
fun FilterChip(label: String, isSelected: Boolean, onClick: () -> Unit) {
  Box(
    modifier = Modifier
      .clip(RoundedCornerShape(20.dp))
      .background(if (isSelected) Color(0x33DFB24C) else GlassDarkBg)
      .clickable(onClick = onClick)
      .padding(horizontal = 14.dp, vertical = 6.dp)
  ) {
    Text(label, style = MaterialTheme.typography.labelMedium, color = if (isSelected) GoldMain else TextMutedColor, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
  }
}
