package com.negm.egyptology.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChangeHistory
import androidx.compose.material.icons.outlined.ChevronLeft
import androidx.compose.material.icons.outlined.Navigation
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.negm.egyptology.data.Catalog
import com.negm.egyptology.data.EgyptItem
import com.negm.egyptology.ui.components.SectionTitle
import com.negm.egyptology.ui.theme.CardTextTone
import com.negm.egyptology.ui.theme.DarkPageBg
import com.negm.egyptology.ui.theme.GlassBorderColor
import com.negm.egyptology.ui.theme.GlassBorderSubtle
import com.negm.egyptology.ui.theme.GlassCardBg
import com.negm.egyptology.ui.theme.GlassDarkBg
import com.negm.egyptology.ui.theme.GoldMain
import com.negm.egyptology.ui.theme.TextMutedColor

@Composable
fun MapScreen(
  catalog: Catalog,
  onItemClick: (EgyptItem) -> Unit
) {
  val sites = remember(catalog) {
    catalog.items.filter {
      it.category == "المواقع الأثرية" || it.category == "المجموعات الهرمية"
    }
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(DarkPageBg)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 18.dp, vertical = 14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(40.dp)
          .border(1.dp, GlassBorderColor, CircleShape)
          .background(Color(0x33DFB24C), CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Outlined.Place,
          contentDescription = "Map",
          tint = GoldMain,
          modifier = Modifier.size(22.dp)
        )
      }
      Spacer(modifier = Modifier.width(12.dp))
      Column {
        Text(
          text = "خريطة مصر الأثرية",
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.Bold,
          color = GoldMain
        )
        Text(
          text = "المعالم والمواقع على طول النيل",
          style = MaterialTheme.typography.bodySmall,
          color = TextMutedColor
        )
      }
    }

    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 18.dp)
    ) {
      item {
        Card(
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(containerColor = GlassCardBg),
          border = BorderStroke(1.dp, GlassBorderColor),
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Outlined.Navigation,
                contentDescription = "Map Guide",
                tint = GoldMain,
                modifier = Modifier.size(22.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "المسار الأثري لوادي النيل",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = GoldMain
              )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "يمتد العمران المصري القديم من الدلتا شمالاً (منف والإسكندرية) مروراً بآثار الجيزة وسقارة حتى قلب الصعيد في طيبة (الأقصر) وأسوان وأبو سمبل جنوباً.",
              style = MaterialTheme.typography.bodyMedium,
              color = CardTextTone,
              lineHeight = 20.sp
            )
          }
        }
      }

      item {
        Spacer(modifier = Modifier.height(10.dp))
        SectionTitle(title = "المعالم والمواقع الموثقة", icon = Icons.Outlined.ChangeHistory)
        Spacer(modifier = Modifier.height(8.dp))
      }

      items(sites, key = { it.id }) { item ->
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = GlassDarkBg),
          border = BorderStroke(1.dp, GlassBorderSubtle),
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onItemClick(item) }
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            androidx.compose.foundation.Image(
              painter = painterResource(id = item.imageRes),
              contentDescription = item.title,
              contentScale = ContentScale.Crop,
              modifier = Modifier
                .size(54.dp)
                .clip(RoundedCornerShape(12.dp))
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = item.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = GoldMain
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = "${item.tag1} • ${item.subtitle}",
                style = MaterialTheme.typography.bodySmall,
                color = TextMutedColor
              )
            }
            Icon(
              imageVector = Icons.Outlined.ChevronLeft,
              contentDescription = "Open",
              tint = GoldMain,
              modifier = Modifier.size(20.dp)
            )
          }
        }
      }

      item {
        Spacer(modifier = Modifier.height(115.dp))
      }
    }
  }
}
