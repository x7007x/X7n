package com.negm.egyptology.ui.screens

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
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.negm.egyptology.data.Catalog
import com.negm.egyptology.data.EgyptItem
import com.negm.egyptology.ui.components.EgyptItemCard
import com.negm.egyptology.ui.components.SearchBarSection
import com.negm.egyptology.ui.components.shareEgyptArticle
import com.negm.egyptology.ui.theme.DarkPageBg
import com.negm.egyptology.ui.theme.GlassBorderColor
import com.negm.egyptology.ui.theme.GlassBorderSubtle
import com.negm.egyptology.ui.theme.GlassDarkBg
import com.negm.egyptology.ui.theme.GoldMain
import com.negm.egyptology.ui.theme.TextMutedColor

@Composable
fun FavoritesScreen(
  catalog: Catalog,
  bookmarkedIds: Set<String>,
  onBookmarkToggle: (String) -> Unit,
  onItemClick: (EgyptItem) -> Unit,
  onExploreClick: () -> Unit
) {
  val context = LocalContext.current
  var selectedFilter by remember { mutableStateOf("الكل") }
  var searchQuery by remember { mutableStateOf("") }

  val bookmarkedItems = remember(bookmarkedIds, catalog) {
    catalog.items.filter { bookmarkedIds.contains(it.id) }
  }

  val availableCategories = remember(bookmarkedItems) {
    listOf("الكل") + bookmarkedItems.map { it.category }.distinct()
  }

  val filteredFavorites = remember(bookmarkedItems, selectedFilter, searchQuery) {
    val q = searchQuery.trim()
    val inCategory =
      if (selectedFilter == "الكل") bookmarkedItems
      else bookmarkedItems.filter { it.category == selectedFilter }
    if (searchQuery.isBlank()) {
      inCategory
    } else {
      inCategory.filter {
        it.title.contains(q, ignoreCase = true) ||
          it.tag1.contains(q, ignoreCase = true) ||
          it.subtitle.contains(q, ignoreCase = true)
      }
    }
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(DarkPageBg)
  ) {
    // Header
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 18.dp, vertical = 14.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(40.dp)
            .border(1.dp, GlassBorderColor, CircleShape)
            .background(Color(0x33DFB24C), CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Filled.Bookmark,
            contentDescription = "Favorites",
            tint = GoldMain,
            modifier = Modifier.size(22.dp)
          )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
          Text(
            text = "قائمة المفضلة",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = GoldMain
          )
          Text(
            text = "السجلات المحفوظة للرجوع السريع",
            style = MaterialTheme.typography.bodySmall,
            color = TextMutedColor
          )
        }
      }

      Box(
        modifier = Modifier
          .background(Color(0x33DFB24C), RoundedCornerShape(12.dp))
          .border(1.dp, GlassBorderColor, RoundedCornerShape(12.dp))
          .padding(horizontal = 10.dp, vertical = 4.dp)
      ) {
        Text(
          text = "${bookmarkedItems.size} عنصر",
          style = MaterialTheme.typography.labelSmall,
          color = GoldMain,
          fontWeight = FontWeight.Bold
        )
      }
    }

    if (bookmarkedItems.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center
        ) {
          Box(
            modifier = Modifier
              .size(86.dp)
              .border(1.5.dp, GlassBorderColor, CircleShape)
              .background(GlassDarkBg, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Outlined.BookmarkBorder,
              contentDescription = "Empty Favorites",
              tint = GoldMain,
              modifier = Modifier.size(44.dp)
            )
          }
          Spacer(modifier = Modifier.height(18.dp))
          Text(
            text = "لا توجد عناصر في المفضلة",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = GoldMain
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "اضغط على أيقونة الإشارة المرجعية داخل أي بطاقة لحفظها هنا",
            style = MaterialTheme.typography.bodySmall,
            color = TextMutedColor,
            textAlign = TextAlign.Center
          )
          Spacer(modifier = Modifier.height(24.dp))
          Button(
            onClick = onExploreClick,
            colors = ButtonDefaults.buttonColors(
              containerColor = GoldMain,
              contentColor = DarkPageBg
            ),
            shape = RoundedCornerShape(24.dp),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 10.dp)
          ) {
            Icon(
              imageVector = Icons.Outlined.Explore,
              contentDescription = "Explore",
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              "استكشف الموسوعة",
              style = MaterialTheme.typography.titleSmall,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }
    } else {
      SearchBarSection(query = searchQuery, onQueryChange = { searchQuery = it })

      if (availableCategories.size > 2) {
        LazyRow(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
          contentPadding = PaddingValues(horizontal = 18.dp),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          items(availableCategories) { cat ->
            val isSelected = cat == selectedFilter
            FilterChip(
              label = cat,
              isSelected = isSelected,
              onClick = { selectedFilter = cat }
            )
          }
        }
      }

      LazyColumn(
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f)
      ) {
        items(filteredFavorites, key = { it.id }) { item ->
          EgyptItemCard(
            item = item,
            isBookmarked = true,
            onBookmarkToggle = { onBookmarkToggle(item.id) },
            onShareClick = { shareEgyptArticle(context, item) },
            onClick = { onItemClick(item) }
          )
        }
        item {
          Spacer(modifier = Modifier.height(115.dp))
        }
      }
    }
  }
}

@Composable
fun FilterChip(label: String, isSelected: Boolean, onClick: () -> Unit) {
  Box(
    modifier = Modifier
      .clip(RoundedCornerShape(20.dp))
      .background(if (isSelected) Color(0x33DFB24C) else GlassDarkBg)
      .border(
        1.dp,
        if (isSelected) GoldMain else GlassBorderSubtle,
        RoundedCornerShape(20.dp)
      )
      .clickable(onClick = onClick)
      .padding(horizontal = 14.dp, vertical = 6.dp)
  ) {
    Text(
      text = label,
      style = MaterialTheme.typography.labelMedium,
      color = if (isSelected) GoldMain else TextMutedColor,
      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
    )
  }
}
