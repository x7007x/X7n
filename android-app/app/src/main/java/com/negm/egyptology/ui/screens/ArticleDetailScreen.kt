package com.negm.egyptology.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.CollectionsBookmark
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.BorderStroke
import com.negm.egyptology.data.EgyptItem
import com.negm.egyptology.ui.components.EyeOfHorusIcon
import com.negm.egyptology.ui.components.GlassBadge
import com.negm.egyptology.ui.components.HorizontalDividerThin
import com.negm.egyptology.ui.components.QuickStatCard
import com.negm.egyptology.ui.components.SectionTitle
import com.negm.egyptology.ui.components.TableRowItem
import com.negm.egyptology.ui.theme.BodyTextLight
import com.negm.egyptology.ui.theme.DarkPageBg
import com.negm.egyptology.ui.theme.GlassBorderColor
import com.negm.egyptology.ui.theme.GlassBorderSubtle
import com.negm.egyptology.ui.theme.GlassCardBg
import com.negm.egyptology.ui.theme.GlassDarkBg
import com.negm.egyptology.ui.theme.GoldLightAccent
import com.negm.egyptology.ui.theme.GoldMain
import com.negm.egyptology.ui.theme.TextMutedColor

@Composable
fun ArticleDetailScreen(
  article: com.negm.egyptology.data.ArticleDetailData,
  item: EgyptItem,
  isBookmarked: Boolean,
  onBookmarkToggle: () -> Unit,
  onShareClick: () -> Unit,
  onBack: () -> Unit
) {
  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(DarkPageBg)
  ) {
    // 1. Top Hero Image with Glass Action Buttons
    item {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(280.dp)
      ) {
        Image(
          painter = painterResource(id = item.imageRes),
          contentDescription = item.title,
          contentScale = ContentScale.Crop,
          modifier = Modifier.fillMaxSize()
        )

        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(
              Brush.verticalGradient(
                colors = listOf(Color(0x990C0D10), Color(0x660C0D10), DarkPageBg)
              )
            )
        )

        // Top Actions (Back, Share, Bookmark)
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 14.dp)
            .align(Alignment.TopCenter),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          IconButton(
            onClick = onBack,
            modifier = Modifier
              .size(34.dp)
              .border(1.dp, GlassBorderColor, CircleShape)
              .background(GlassDarkBg, CircleShape)
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Back",
              tint = GoldMain,
              modifier = Modifier.size(18.dp)
            )
          }

          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            IconButton(
              onClick = onShareClick,
              modifier = Modifier
                .size(34.dp)
                .border(1.dp, GlassBorderColor, CircleShape)
                .background(GlassDarkBg, CircleShape)
            ) {
              Icon(
                imageVector = Icons.Outlined.Share,
                contentDescription = "Share",
                tint = GoldMain,
                modifier = Modifier.size(18.dp)
              )
            }

            IconButton(
              onClick = onBookmarkToggle,
              modifier = Modifier
                .size(34.dp)
                .border(1.dp, GlassBorderColor, CircleShape)
                .background(GlassDarkBg, CircleShape)
            ) {
              Icon(
                imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                contentDescription = "Bookmark",
                tint = if (isBookmarked) GoldMain else GoldLightAccent.copy(alpha = 0.7f),
                modifier = Modifier.size(18.dp)
              )
            }
          }
        }

        // Bottom Hero Info Badges
        Column(
          modifier = Modifier
            .align(Alignment.BottomStart)
            .padding(horizontal = 18.dp, vertical = 14.dp)
        ) {
          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            GlassBadge(text = item.category)
            GlassBadge(text = item.tag1)
            if (item.tag2.isNotBlank()) GlassBadge(text = item.tag2)
          }
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = item.title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = GoldMain
          )
          Text(
            text = item.subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = GoldLightAccent.copy(alpha = 0.9f)
          )
        }
      }
    }

    // 2. Quick Summary Cards
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 18.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        QuickStatCard(
          title = "الاسم الملكي / الكارتوش",
          value = article.cartoucheName,
          modifier = Modifier.weight(1f)
        )
        QuickStatCard(
          title = "الموقع الجغرافي",
          value = article.location,
          modifier = Modifier.weight(1f)
        )
      }
    }

    // 3. Archaeological Data Table
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 18.dp, vertical = 12.dp)
      ) {
        SectionTitle(title = "جدول التوثيق والبيانات الأثرية", icon = Icons.Outlined.CollectionsBookmark)

        Spacer(modifier = Modifier.height(10.dp))

        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = GlassCardBg),
          border = BorderStroke(1.dp, GlassBorderColor),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            article.tableRows.forEachIndexed { index, row ->
              TableRowItem(field = row.first, value = row.second, isEven = index % 2 == 0)
              if (index < article.tableRows.size - 1) {
                HorizontalDividerThin()
              }
            }
          }
        }
      }
    }

    // 4. Overview Paragraph
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 18.dp, vertical = 8.dp)
      ) {
        SectionTitle(title = "نظرة عامة وتاريخية", icon = Icons.Outlined.MenuBook)

        Spacer(modifier = Modifier.height(8.dp))

        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = GlassCardBg),
          border = BorderStroke(1.dp, GlassBorderSubtle),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Text(
              text = article.overviewParagraph,
              style = MaterialTheme.typography.bodyLarge,
              color = BodyTextLight,
              lineHeight = 26.sp
            )
          }
        }
      }
    }

    // 5. Hieroglyphic Quote Callout
    if (article.hieroglyphicQuote.isNotBlank()) {
      item {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 10.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0x33DFB24C))
            .border(1.2.dp, GoldMain, RoundedCornerShape(16.dp))
            .padding(16.dp)
        ) {
          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              EyeOfHorusIcon(modifier = Modifier.size(24.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "نقش فرعوني مقدس",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = GoldMain
              )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "« ${article.hieroglyphicQuote} »",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.SemiBold,
              color = GoldLightAccent,
              lineHeight = 24.sp
            )
            if (article.quoteSource.isNotBlank()) {
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = "— ${article.quoteSource}",
                style = MaterialTheme.typography.bodySmall,
                color = TextMutedColor
              )
            }
          }
        }
      }
    }

    // 6. Highlights Bullets
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 18.dp, vertical = 10.dp)
      ) {
        SectionTitle(title = "أبرز الإنجازات والوقائع التاريخية", icon = Icons.Outlined.AutoAwesome)

        Spacer(modifier = Modifier.height(10.dp))

        article.highlights.forEach { highlight ->
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 4.dp),
            verticalAlignment = Alignment.Top
          ) {
            Box(
              modifier = Modifier
                .padding(top = 6.dp)
                .size(8.dp)
                .background(GoldMain, CircleShape)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
              text = highlight,
              style = MaterialTheme.typography.bodyMedium,
              color = BodyTextLight,
              lineHeight = 22.sp
            )
          }
        }
      }
    }

    // 7. Academic References
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 18.dp, vertical = 12.dp)
      ) {
        SectionTitle(title = "المراجع والتوثيق العلمي", icon = Icons.Outlined.CollectionsBookmark)

        Spacer(modifier = Modifier.height(8.dp))

        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = GlassDarkBg),
          border = BorderStroke(1.dp, GlassBorderSubtle),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Text(
              text = "• السجل العام للآثار المصرية - وزارة السياحة والآثار\n• أبحاث المجلس الأعلى للآثار وهيئة المتحف المصري\n• دراسات البروفيسور سليم حسن (موسوعة مصر القديمة)",
              style = MaterialTheme.typography.bodySmall,
              color = TextMutedColor,
              lineHeight = 22.sp
            )
          }
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(30.dp))
    }
  }
}
