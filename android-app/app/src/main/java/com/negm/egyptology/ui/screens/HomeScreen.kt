package com.negm.egyptology.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.dp
import androidx.compose.animation.core.tween
import com.negm.egyptology.R
import com.negm.egyptology.data.Catalog
import com.negm.egyptology.data.EgyptRepository
import com.negm.egyptology.ui.components.CategoriesSection
import com.negm.egyptology.ui.components.EgyptItemCard
import com.negm.egyptology.ui.components.SectionHeader
import com.negm.egyptology.ui.components.SearchBarSection
import com.negm.egyptology.ui.components.shareEgyptArticle
import com.negm.egyptology.ui.theme.DarkPageBg
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
  var selectedCategory by remember { mutableStateOf(catalog.categories.first().title) }
  var searchQuery by remember { mutableStateOf("") }

  val filteredItems = remember(selectedCategory, searchQuery, catalog) {
    val itemsInCategory = catalog.items.filter { it.category == selectedCategory }
    if (searchQuery.isBlank()) {
      itemsInCategory
    } else {
      val q = searchQuery.trim()
      itemsInCategory.filter {
        it.title.contains(q, ignoreCase = true) ||
          it.tag1.contains(q, ignoreCase = true) ||
          it.tag2.contains(q, ignoreCase = true) ||
          it.subtitle.contains(q, ignoreCase = true)
      }
    }
  }

  val categoryMeta = catalog.categoryMeta.firstOrNull { it.title == selectedCategory }
  val sectionBackground = when (selectedCategory) {
    "الملوك" -> R.drawable.section_kings_background
    "المتاحف" -> R.drawable.section_museums_background
    "المقابر" -> R.drawable.section_museums_background
    "المجموعات الهرمية" -> R.drawable.section_map_background
    "الآثار" -> R.drawable.section_collections_background
    "المواقع الأثرية" -> R.drawable.section_map_background
    else -> R.drawable.section_sources_background
  }
  val sectionIllustration = when (selectedCategory) {
    "الملوك" -> R.drawable.section_kings_illustration
    "المتاحف" -> R.drawable.section_museums_illustration
    "المقابر" -> R.drawable.section_museums_illustration
    "المجموعات الهرمية" -> R.drawable.section_map_illustration
    "الآثار" -> R.drawable.section_collections_illustration
    "المواقع الأثرية" -> R.drawable.section_map_illustration
    else -> R.drawable.section_sources_illustration
  }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(DarkPageBg)
  ) {
    item {
      Box(modifier = Modifier.fillMaxWidth().height(200.dp)) {
        AnimatedContent(
          targetState = sectionBackground,
          transitionSpec = { fadeIn(tween(320)) togetherWith fadeOut(tween(220)) },
          label = "section_background_transition"
        ) { backgroundRes ->
          Image(
            painter = painterResource(id = backgroundRes),
            contentDescription = "Header",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
          )
        }
        AnimatedContent(
          targetState = sectionIllustration,
          transitionSpec = { fadeIn(tween(320)) togetherWith fadeOut(tween(220)) },
          label = "section_illustration_transition"
        ) { illustrationRes ->
          Image(
            painter = painterResource(id = illustrationRes),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
              .align(Alignment.TopEnd)
              .padding(top = 18.dp, end = 18.dp)
              .size(112.dp)
          )
        }
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(
              Brush.verticalGradient(
                0f to Color(0x33000000),
                0.42f to Color.Transparent,
                1f to DarkPageBg
              )
            )
        )
        categoryMeta?.let { meta ->
          Column(
            modifier = Modifier
              .align(Alignment.BottomEnd)
              .padding(horizontal = 18.dp, vertical = 18.dp),
            horizontalAlignment = Alignment.End
          ) {
            Text(
              text = meta.title,
              style = MaterialTheme.typography.headlineSmall,
              color = com.negm.egyptology.ui.theme.GoldMain,
              maxLines = 1
            )
            Text(
              text = meta.subtitle,
              style = MaterialTheme.typography.bodySmall,
              color = Color.White.copy(alpha = 0.86f),
              maxLines = 1
            )
          }
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
      CategoriesSection(
        categories = catalog.categories,
        selectedCategory = selectedCategory,
        onCategorySelect = { selectedCategory = it }
      )
    }
    categoryMeta?.let { meta ->
      item {
        SectionHeader(
          meta = meta,
          onViewAllClick = { onViewAllClick(selectedCategory) }
        )
      }
    }
    if (filteredItems.isEmpty()) {
      item {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp, horizontal = 24.dp),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
              imageVector = Icons.Outlined.SearchOff,
              contentDescription = "No results",
              tint = TextMutedColor,
              modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "لم يتم العثور على نتائج",
              style = MaterialTheme.typography.bodyLarge,
              color = TextMutedColor
            )
          }
        }
      }
    } else {
      item {
        AnimatedContent(
          targetState = filteredItems,
          transitionSpec = { fadeIn(tween(260)) togetherWith fadeOut(tween(160)) },
          label = "section_items_transition"
        ) { itemsForCategory ->
          Column {
            itemsForCategory.forEach { item ->
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
    item {
      Spacer(modifier = Modifier.height(115.dp))
    }
  }
}
