package com.negm.egyptology.ui.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material.icons.outlined.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
  onBack: () -> Unit
) {
  val context = LocalContext.current
  var selectedCategory by remember { mutableStateOf(initialCategory) }
  var searchQuery by remember { mutableStateOf("") }

  val allCategoriesFilter = remember(catalog) {
    listOf("الكل") + catalog.categories.map { it.title }
  }

  val displayedItems = remember(selectedCategory, searchQuery, catalog) {
    val q = searchQuery.trim()
    val base =
      if (selectedCategory == "الكل") catalog.items
      else catalog.items.filter { it.category == selectedCategory }
    if (searchQuery.isBlank()) {
      base
    } else {
      base.filter {
        it.title.contains(q, ignoreCase = true) ||
          it.tag1.contains(q, ignoreCase = true) ||
          it.tag2.contains(q, ignoreCase = true) ||
          it.subtitle.contains(q, ignoreCase = true)
      }
    }
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(DarkPageBg)
  ) {
    // Top Bar
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 18.dp, vertical = 12.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(
          onClick = onBack,
          modifier = Modifier
            .size(38.dp)
            .border(1.dp, GlassBorderColor, CircleShape)
            .background(GlassDarkBg, CircleShape)
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = GoldMain,
            modifier = Modifier.size(20.dp)
          )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
          Text(
            text = "سجل الآثار المصرية",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = GoldMain,
            fontSize = 18.sp
          )
          Text(
            text = "مجموع ${displayedItems.size} عنصراً مسجلاً",
            style = MaterialTheme.typography.bodySmall,
            color = TextMutedColor,
            fontSize = 11.sp
          )
        }
      }

      Icon(
        imageVector = Icons.Outlined.WorkspacePremium,
        contentDescription = "Egypt Emblem",
        tint = GoldMain,
        modifier = Modifier.size(26.dp)
      )
    }

    SearchBarSection(query = searchQuery, onQueryChange = { searchQuery = it })

    Spacer(modifier = Modifier.height(4.dp))

    LazyRow(
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 8.dp),
      contentPadding = PaddingValues(horizontal = 18.dp),
      horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
    ) {
      items(allCategoriesFilter) { cat ->
        FilterChip(label = cat, isSelected = cat == selectedCategory, onClick = { selectedCategory = cat })
      }
    }

    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .weight(1f)
    ) {
      if (displayedItems.isEmpty()) {
        item {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(top = 60.dp),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Icon(
                imageVector = Icons.Outlined.SearchOff,
                contentDescription = "Empty",
                tint = TextMutedColor,
                modifier = Modifier.size(48.dp)
              )
              Spacer(modifier = Modifier.height(10.dp))
              Text(
                text = "لا توجد نتائج مطابقة لبحثك",
                style = MaterialTheme.typography.bodyMedium,
                color = TextMutedColor
              )
            }
          }
        }
      } else {
        items(displayedItems, key = { it.id }) { item ->
          EgyptItemCard(
            item = item,
            isBookmarked = bookmarkedIds.contains(item.id),
            onBookmarkToggle = { onBookmarkToggle(item.id) },
            onShareClick = { shareEgyptArticle(context, item) },
            onClick = { onItemClick(item) }
          )
        }
      }
      item {
        Spacer(modifier = Modifier.height(115.dp))
      }
    }
  }
}
