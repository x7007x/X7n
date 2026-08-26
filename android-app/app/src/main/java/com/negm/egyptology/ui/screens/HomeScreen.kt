package com.negm.egyptology.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.negm.egyptology.R
import com.negm.egyptology.data.Catalog
import com.negm.egyptology.ui.components.CategoriesSection
import com.negm.egyptology.ui.components.EgyptItemCard
import com.negm.egyptology.ui.components.SearchBarSection
import com.negm.egyptology.ui.components.shareEgyptArticle
import com.negm.egyptology.ui.theme.DarkPageBg
import com.negm.egyptology.ui.theme.GlassCardBg
import com.negm.egyptology.ui.theme.TextMutedColor

@Composable
fun HomeScreen(
  catalog: Catalog,
  bookmarkedIds: Set<String>,
  onBookmarkToggle: (String) -> Unit,
  onItemClick: (com.negm.egyptology.data.EgyptItem) -> Unit,
  onViewAllClick: (String) -> Unit
) {
  val context = LocalContext.current
  var searchQuery by remember { mutableStateOf("") }
  val categoryImages = remember(catalog) {
    buildMap {
      catalog.categories.forEach { category ->
        catalog.items.firstOrNull { it.category == category.title }?.imageRes?.let { imageRes ->
          put(category.title, imageRes)
        }
      }
    }
  }
  val searchResults = remember(searchQuery, catalog) {
    if (searchQuery.isBlank()) {
      emptyList()
    } else {
      val query = searchQuery.trim()
      catalog.items.filter {
        it.title.contains(query, ignoreCase = true) ||
          it.category.contains(query, ignoreCase = true) ||
          it.tag1.contains(query, ignoreCase = true) ||
          it.tag2.contains(query, ignoreCase = true) ||
          it.subtitle.contains(query, ignoreCase = true)
      }
    }
  }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(DarkPageBg)
  ) {
    item {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(210.dp)
      ) {
        Image(
          painter = painterResource(id = R.drawable.section_kings_background),
          contentDescription = "Egyptology header",
          contentScale = ContentScale.Crop,
          modifier = Modifier.fillMaxSize()
        )
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(
              Brush.verticalGradient(
                colors = listOf(
                  Color(0x66000000),
                  Color(0x22000000),
                  DarkPageBg
                )
              )
            ),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "ايچبتتولوجي",
            style = MaterialTheme.typography.headlineLarge,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.4.sp,
            maxLines = 1
          )
        }
      }
    }
    item {
      SearchBarSection(
        query = searchQuery,
        onQueryChange = { searchQuery = it }
      )
    }
    item {
      Text(
        text = "استكشف مصر القديمة",
        style = MaterialTheme.typography.titleLarge,
        color = Color.White,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp)
      )
    }
    item {
      CategoriesSection(
        categories = catalog.categories,
        categoryImages = categoryImages,
        selectedCategory = null,
        onCategorySelect = onViewAllClick
      )
    }
    if (searchQuery.isNotBlank()) {
      item {
        Text(
          text = "نتائج البحث (${searchResults.size})",
          style = MaterialTheme.typography.titleMedium,
          color = TextMutedColor,
          fontWeight = FontWeight.Bold,
          modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp)
        )
      }
      item {
        if (searchResults.isEmpty()) {
          Text(
            text = "لم يتم العثور على نتائج",
            style = MaterialTheme.typography.bodyLarge,
            color = TextMutedColor,
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 18.dp, vertical = 28.dp)
          )
        } else {
          AnimatedContent(
            targetState = searchResults,
            transitionSpec = { fadeIn(tween(260)) togetherWith fadeOut(tween(160)) },
            label = "home_search_results_transition"
          ) { itemsForSearch ->
            Column {
              itemsForSearch.forEach { item ->
                EgyptItemCard(
                  item = item,
                  isBookmarked = bookmarkedIds.contains(item.id),
                  onBookmarkToggle = { onBookmarkToggle(item.id) },
                  onShareClick = { shareEgyptArticle(context, item) },
                  onClick = { onItemClick(item) }
                )
              }
            }
          }
        }
      }
    }
    item {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 18.dp, vertical = 6.dp)
          .height(1.dp)
          .background(GlassCardBg)
      )
    }
    item { Spacer(modifier = Modifier.height(115.dp)) }
  }
}
