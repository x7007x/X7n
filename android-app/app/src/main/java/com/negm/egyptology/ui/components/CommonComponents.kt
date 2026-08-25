package com.negm.egyptology.ui.components

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.negm.egyptology.data.Category
import com.negm.egyptology.data.CategoryMeta
import com.negm.egyptology.data.EgyptItem
import com.negm.egyptology.data.EgyptRepository
import com.negm.egyptology.ui.theme.BodyTextLight
import com.negm.egyptology.ui.theme.DarkPageBg
import com.negm.egyptology.ui.theme.GlassBorderSubtle
import com.negm.egyptology.ui.theme.GlassCardBg
import com.negm.egyptology.ui.theme.GlassBorderColor
import com.negm.egyptology.ui.theme.GlassDarkBg
import com.negm.egyptology.ui.theme.GoldLightAccent
import com.negm.egyptology.ui.theme.GoldMain
import com.negm.egyptology.ui.theme.GoldMuted
import com.negm.egyptology.ui.theme.TextMutedColor

// -------------------------------------------------------------
// SHARED GLASS UI PIECES
// -------------------------------------------------------------

@Composable
fun GlassBadge(text: String) {
  Box(
    modifier = Modifier
      .border(1.dp, GlassBorderSubtle, RoundedCornerShape(12.dp))
      .background(Color(0x66161820), RoundedCornerShape(12.dp))
      .padding(horizontal = 8.dp, vertical = 3.dp)
  ) {
    Text(
      text = text,
      style = MaterialTheme.typography.labelSmall,
      color = TextMutedColor,
      maxLines = 1
    )
  }
}

@Composable
fun SectionTitle(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
  Row(verticalAlignment = Alignment.CenterVertically) {
    Icon(
      imageVector = icon,
      contentDescription = title,
      tint = GoldMain,
      modifier = Modifier.size(20.dp)
    )
    Spacer(modifier = Modifier.width(8.dp))
    Text(
      text = title,
      style = MaterialTheme.typography.titleMedium,
      fontWeight = FontWeight.Bold,
      color = Color(0xFFF3D58C)
    )
  }
}

@Composable
fun SearchBarSection(
  query: String,
  onQueryChange: (String) -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 18.dp, vertical = 6.dp)
      .height(50.dp)
      .clip(RoundedCornerShape(26.dp))
      .background(GlassDarkBg)
      .border(1.dp, GlassBorderColor, RoundedCornerShape(26.dp)),
    contentAlignment = Alignment.CenterStart
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Icon(
        imageVector = Icons.Outlined.Search,
        contentDescription = "Search",
        tint = GoldMain,
        modifier = Modifier.size(21.dp)
      )
      Spacer(modifier = Modifier.width(10.dp))

      val fieldStyle = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Start)

      Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
        if (query.isEmpty()) {
          Text(
            "...ابحث عن ملك، متحف، مقبرة، مجموعة",
            style = fieldStyle,
            color = TextMutedColor.copy(alpha = 0.75f),
            maxLines = 1
          )
        }
        BasicTextField(
          value = query,
          onValueChange = onQueryChange,
          singleLine = true,
          textStyle = fieldStyle.copy(color = BodyTextLight),
          cursorBrush = SolidColor(GoldMain),
          modifier = Modifier.fillMaxWidth()
        )
      }

      if (query.isNotEmpty()) {
        IconButton(onClick = { onQueryChange("") }, modifier = Modifier.size(28.dp)) {
          Icon(
            imageVector = Icons.Outlined.Close,
            contentDescription = "Clear",
            tint = TextMutedColor,
            modifier = Modifier.size(18.dp)
          )
        }
      }
    }
  }
}

@Composable
fun CategoriesSection(
  categories: List<Category>,
  selectedCategory: String,
  onCategorySelect: (String) -> Unit
) {
  LazyRow(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 12.dp),
    contentPadding = PaddingValues(horizontal = 18.dp),
    horizontalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    items(categories) { category ->
      val isSelected = category.title == selectedCategory
      CategoryItem(
        category = category,
        isSelected = isSelected,
        onClick = { onCategorySelect(category.title) }
      )
    }
  }
}

@Composable
private fun CategoryItem(
  category: Category,
  isSelected: Boolean,
  onClick: () -> Unit
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center,
    modifier = Modifier
      .width(72.dp)
      .height(82.dp)
      .clip(RoundedCornerShape(16.dp))
      .background(if (isSelected) Color(0x33DFB24C) else Color(0x66181A22))
      .border(
        width = if (isSelected) 1.5.dp else 1.dp,
        color = if (isSelected) GoldMain else GlassBorderSubtle,
        shape = RoundedCornerShape(16.dp)
      )
      .clickable(onClick = onClick)
      .padding(4.dp)
  ) {
    Icon(
      imageVector = category.icon,
      contentDescription = category.title,
      tint = if (isSelected) GoldMain else GoldMuted.copy(alpha = 0.85f),
      modifier = Modifier.size(26.dp)
    )
    Spacer(modifier = Modifier.height(6.dp))
    Text(
      text = category.title,
      style = MaterialTheme.typography.labelSmall,
      color = if (isSelected) GoldMain else TextMutedColor,
      textAlign = TextAlign.Center,
      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis
    )
  }
}

@Composable
fun SectionHeader(
  meta: CategoryMeta,
  onViewAllClick: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 18.dp, vertical = 10.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Icon(
        imageVector = meta.icon,
        contentDescription = meta.title,
        tint = GoldMain,
        modifier = Modifier.size(24.dp)
      )
      Spacer(modifier = Modifier.width(8.dp))
      Column {
        Text(
          text = meta.title,
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = Color(0xFFF3D58C)
        )
        Text(
          text = meta.subtitle,
          style = MaterialTheme.typography.bodySmall,
          color = TextMutedColor
        )
      }
    }

    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier
        .clip(RoundedCornerShape(10.dp))
        .clickable(onClick = onViewAllClick)
        .padding(horizontal = 6.dp, vertical = 4.dp)
    ) {
      Icon(
        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
        contentDescription = "View All",
        tint = GoldMain,
        modifier = Modifier.size(16.dp)
      )
      Spacer(modifier = Modifier.width(3.dp))
      Text(
        text = "عرض الكل",
        style = MaterialTheme.typography.labelMedium,
        color = GoldMain
      )
    }
  }
}

// -------------------------------------------------------------
// ITEM CARD (PHARAOH / ARTIFACT / SITE ...)
// -------------------------------------------------------------
@Composable
fun EgyptItemCard(
  item: EgyptItem,
  isBookmarked: Boolean,
  onBookmarkToggle: () -> Unit,
  onShareClick: () -> Unit,
  onClick: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = GlassCardBg),
    border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorderColor),
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 18.dp, vertical = 6.dp)
      .height(138.dp)
      .clickable(onClick = onClick)
  ) {
    Row(
      modifier = Modifier.fillMaxSize(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .weight(0.35f)
          .fillMaxHeight(),
        contentAlignment = Alignment.Center
      ) {
        Image(
          painter = painterResource(id = item.imageRes),
          contentDescription = item.title,
          contentScale = ContentScale.Crop,
          modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp))
        )
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(
              Brush.horizontalGradient(
                colors = listOf(Color.Transparent, GlassCardBg.copy(alpha = 0.75f))
              )
            )
        )
      }

      Column(
        modifier = Modifier
          .weight(0.51f)
          .fillMaxHeight()
          .padding(vertical = 12.dp, horizontal = 10.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
      ) {
        Text(
          text = item.title,
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = GoldMain,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          GlassBadge(text = item.tag1)
          if (item.tag2.isNotBlank()) GlassBadge(text = item.tag2)
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = item.subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = TextMutedColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
          Spacer(modifier = Modifier.width(5.dp))
          Icon(
            imageVector = item.subtitleIcon,
            contentDescription = "Info",
            tint = GoldMuted,
            modifier = Modifier.size(13.dp)
          )
        }
      }

      Box(
        modifier = Modifier
          .width(1.dp)
          .fillMaxHeight()
          .background(GlassBorderColor.copy(alpha = 0.4f))
      )

      Column(
        modifier = Modifier
          .weight(0.14f)
          .fillMaxHeight()
          .background(Color(0x400C0D10)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
      ) {
        IconButton(onClick = onBookmarkToggle, modifier = Modifier.size(32.dp)) {
          Icon(
            imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
            contentDescription = "Bookmark",
            tint = if (isBookmarked) GoldMain else GoldMuted.copy(alpha = 0.85f),
            modifier = Modifier.size(21.dp)
          )
        }

        IconButton(onClick = onShareClick, modifier = Modifier.size(32.dp)) {
          Icon(
            imageVector = Icons.Outlined.Share,
            contentDescription = "Share",
            tint = GoldMuted.copy(alpha = 0.85f),
            modifier = Modifier.size(20.dp)
          )
        }
      }
    }
  }
}

// -------------------------------------------------------------
// ARTICLE DETAIL HELPERS
// -------------------------------------------------------------
@Composable
fun QuickStatCard(title: String, value: String, modifier: Modifier = Modifier) {
  Card(
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = GlassCardBg),
    border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorderColor),
    modifier = modifier
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      Text(
        text = title,
        style = MaterialTheme.typography.labelSmall,
        color = TextMutedColor
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = value,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = GoldMain,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
    }
  }
}

@Composable
fun TableRowItem(field: String, value: String, isEven: Boolean) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .background(if (isEven) Color(0x221F222C) else Color.Transparent, RoundedCornerShape(8.dp))
      .padding(horizontal = 10.dp, vertical = 8.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      text = field,
      style = MaterialTheme.typography.bodyMedium,
      fontWeight = FontWeight.Bold,
      color = GoldMain,
      modifier = Modifier.weight(0.42f)
    )
    Text(
      text = value,
      style = MaterialTheme.typography.bodyMedium,
      color = BodyTextLight,
      modifier = Modifier.weight(0.58f)
    )
  }
}

@Composable
fun HorizontalDividerThin() {
  HorizontalDivider(
    color = GlassBorderSubtle,
    thickness = 0.8.dp,
    modifier = Modifier.padding(vertical = 4.dp)
  )
}

// -------------------------------------------------------------
// EYE OF HORUS CUSTOM EGYPTIAN VECTOR
// -------------------------------------------------------------
@Composable
fun EyeOfHorusIcon(modifier: Modifier = Modifier) {
  Canvas(modifier = modifier) {
    val w = size.width
    val h = size.height
    val strokeWidth = 2.4.dp.toPx()
    val gold = GoldMain

    val eyePath = Path().apply {
      moveTo(w * 0.12f, h * 0.45f)
      quadraticTo(w * 0.5f, h * 0.15f, w * 0.88f, h * 0.45f)
      quadraticTo(w * 0.5f, h * 0.75f, w * 0.12f, h * 0.45f)
      close()
    }
    drawPath(path = eyePath, color = gold, style = Stroke(width = strokeWidth, cap = StrokeCap.Round))

    drawCircle(color = gold, radius = w * 0.13f, center = Offset(w * 0.5f, h * 0.45f), style = Fill)
    drawCircle(
      color = DarkPageBg,
      radius = w * 0.05f,
      center = Offset(w * 0.5f, h * 0.45f),
      style = Fill
    )

    val eyebrowPath = Path().apply {
      moveTo(w * 0.15f, h * 0.28f)
      quadraticTo(w * 0.5f, h * 0.04f, w * 0.85f, h * 0.28f)
    }
    drawPath(path = eyebrowPath, color = gold, style = Stroke(width = strokeWidth, cap = StrokeCap.Round))

    val tearPath = Path().apply {
      moveTo(w * 0.5f, h * 0.75f)
      lineTo(w * 0.5f, h * 0.95f)
    }
    drawPath(path = tearPath, color = gold, style = Stroke(width = strokeWidth, cap = StrokeCap.Round))

    val spiralPath = Path().apply {
      moveTo(w * 0.62f, h * 0.7f)
      quadraticTo(w * 0.8f, h * 0.85f, w * 0.7f, h * 0.95f)
    }
    drawPath(path = spiralPath, color = gold, style = Stroke(width = strokeWidth, cap = StrokeCap.Round))
  }
}

// -------------------------------------------------------------
// SHARE INTENT
// -------------------------------------------------------------
fun shareEgyptArticle(context: Context, item: EgyptItem) {
  val article = EgyptRepository.articleFor(context, item.id)
  val shareText = """
    📜 ${item.title} - موسوعة مصر القديمة
    ━━━━━━━━━━━━━━━━━━━━━━
    🏷️ التصنيف: ${item.category} (${item.tag1})
    ⏳ الحقبة: ${item.subtitle}
    📍 الموقع: ${article.location}
    
    🏛️ نبذة تاريخية:
    ${article.overviewParagraph}
    
    ✨ أبرز الإنجازات:
    ${article.highlights.take(3).joinToString("\n") { "• $it" }}
    
    مشارك من تطبيق egyptology 🇪🇬
  """.trimIndent()

  val sendIntent = Intent().apply {
    action = Intent.ACTION_SEND
    putExtra(Intent.EXTRA_TEXT, shareText)
    putExtra(Intent.EXTRA_TITLE, item.title)
    type = "text/plain"
  }
  context.startActivity(Intent.createChooser(sendIntent, "مشاركة سجل ${item.title}"))
}
