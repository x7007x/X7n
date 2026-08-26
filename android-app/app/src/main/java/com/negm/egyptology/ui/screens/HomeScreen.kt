package com.negm.egyptology.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.negm.egyptology.data.Catalog
import com.negm.egyptology.data.EgyptItem
import com.negm.egyptology.ui.components.CategoriesSection
import com.negm.egyptology.ui.components.EgyptItemCard
import com.negm.egyptology.ui.components.SearchBarSection
import com.negm.egyptology.ui.components.SectionHeader
import com.negm.egyptology.ui.components.shareEgyptArticle
import com.negm.egyptology.ui.theme.DarkPageBg
import com.negm.egyptology.ui.theme.GoldMain
import com.negm.egyptology.ui.theme.TextMutedColor

@Composable
fun HomeScreen(
  catalog: Catalog,
  bookmarkedIds: Set<String>,
  onBookmarkToggle: (String) -> Unit,
  onItemClick: (EgyptItem) -> Unit,
  onViewAllClick: (String) -> Unit
) {
  val context = LocalContext.current
  var selectedCategory by remember { mutableStateOf(catalog.categories.firstOrNull()?.title ?: "الملوك") }
  var searchQuery by remember { mutableStateOf("") }
  val filteredItems = remember(selectedCategory, searchQuery, catalog) {
    val categoryItems = catalog.items.filter { it.category == selectedCategory }
    val query = searchQuery.trim()
    if (query.isBlank()) categoryItems else categoryItems.filter {
      it.title.contains(query, true) || it.tag1.contains(query, true) || it.tag2.contains(query, true) || it.subtitle.contains(query, true)
    }
  }
  val featuredItem = filteredItems.firstOrNull() ?: catalog.items.firstOrNull()
  val categoryMeta = catalog.categoryMeta.firstOrNull { it.title == selectedCategory }

  LazyColumn(
    modifier = Modifier.fillMaxSize().background(DarkPageBg),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    item {
      Column(
        modifier = Modifier.fillMaxWidth().padding(top = 34.dp, start = 18.dp, end = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Text(
          text = "موسوعة علم المصريات",
          style = MaterialTheme.typography.headlineLarge,
          fontWeight = FontWeight.Bold,
          color = Color(0xFFF0E8DC),
          textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(6.dp))
        Text(
          text = "اكتشف حضارة مصر القديمة",
          style = MaterialTheme.typography.titleMedium,
          color = Color(0xFFC99A5D),
          textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(22.dp))
      }
    }
    item {
      CategoriesSection(
        categories = catalog.categories,
        selectedCategory = selectedCategory,
        onCategorySelect = { selectedCategory = it }
      )
    }
    item {
      Spacer(Modifier.height(4.dp))
      SearchBarSection(query = searchQuery, onQueryChange = { searchQuery = it })
      Spacer(Modifier.height(14.dp))
    }
    item {
      Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "عرض الكل ‹",
          style = MaterialTheme.typography.labelLarge,
          color = Color(0xFFC99A5D),
          modifier = Modifier.clickable { onViewAllClick(selectedCategory) }
        )
        Text(
          text = "Featured",
          style = MaterialTheme.typography.headlineSmall,
          fontWeight = FontWeight.Bold,
          color = Color(0xFFF0E8DC)
        )
      }
      Spacer(Modifier.height(10.dp))
    }
    featuredItem?.let { featured ->
      item {
        FeaturedEgyptCard(item = featured, onClick = { onItemClick(featured) })
      }
    }
    item {
      Spacer(Modifier.height(18.dp))
      categoryMeta?.let { meta ->
        SectionHeader(meta = meta, onViewAllClick = { onViewAllClick(selectedCategory) })
      }
    }
    if (filteredItems.isEmpty()) {
      item {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(vertical = 34.dp)) {
          Icon(imageVector = Icons.Outlined.SearchOff, contentDescription = "لا توجد نتائج", tint = TextMutedColor, modifier = Modifier.size(42.dp))
          Spacer(Modifier.height(8.dp))
          Text("لم يتم العثور على نتائج", color = TextMutedColor)
        }
      }
    } else {
      item {
        AnimatedContent(
          targetState = filteredItems,
          transitionSpec = { fadeIn(tween(220)) togetherWith fadeOut(tween(140)) },
          label = "homeItemsTransition"
        ) { itemsForCategory ->
          Column {
            itemsForCategory.drop(1).forEach { item ->
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
    item { Spacer(Modifier.height(104.dp)) }
  }
}

@Composable
private fun FeaturedEgyptCard(item: EgyptItem, onClick: () -> Unit) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 18.dp)
      .height(312.dp)
      .clickable(onClick = onClick)
      .border(1.dp, Color(0x332B393A), RoundedCornerShape(18.dp))
  ) {
    Image(
      painter = painterResource(item.imageRes),
      contentDescription = item.title,
      contentScale = ContentScale.Crop,
      modifier = Modifier.fillMaxSize()
    )
    Box(
      modifier = Modifier.fillMaxSize().background(
        Brush.verticalGradient(
          listOf(Color.Transparent, Color.Transparent, Color(0xEE111718))
        )
      )
    )
    Column(
      modifier = Modifier.align(Alignment.BottomEnd).padding(18.dp),
      horizontalAlignment = Alignment.End
    ) {
      Text(
        text = "مقال مصر القديمة",
        style = MaterialTheme.typography.labelMedium,
        color = Color(0xFFD5A76B),
        fontSize = 13.sp
      )
      Spacer(Modifier.height(6.dp))
      Text(
        text = "اكتشف ${item.title}",
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold,
        color = Color(0xFFF0E8DC),
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
      )
    }
  }
}
