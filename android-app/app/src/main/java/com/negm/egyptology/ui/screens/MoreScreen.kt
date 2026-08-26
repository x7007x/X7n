package com.negm.egyptology.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.negm.egyptology.data.Catalog
import com.negm.egyptology.ui.theme.CardTextTone
import com.negm.egyptology.ui.theme.DarkPageBg
import com.negm.egyptology.ui.theme.GlassBorderColor
import com.negm.egyptology.ui.theme.GlassCardBg
import com.negm.egyptology.ui.theme.GoldMain
import com.negm.egyptology.ui.theme.TextMutedColor

@Composable
fun MoreScreen(catalog: Catalog) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(DarkPageBg)
      .padding(18.dp)
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
          imageVector = Icons.Outlined.MenuBook,
          contentDescription = "More",
          tint = GoldMain,
          modifier = Modifier.size(22.dp)
        )
      }
      Spacer(modifier = Modifier.width(12.dp))
      Column {
        Text(
          text = "المصادر والمراجع",
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.Bold,
          color = GoldMain
        )
        Text(
          text = "مراجع علم المصريات",
          style = MaterialTheme.typography.bodySmall,
          color = TextMutedColor
        )
      }
    }

    Spacer(modifier = Modifier.height(18.dp))

    LazyColumn {
      items(catalog.moreSources) { source ->
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = GlassCardBg),
          border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorderColor),
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Text(
              text = source.title,
              style = MaterialTheme.typography.titleSmall,
              fontWeight = FontWeight.Bold,
              color = GoldMain
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = source.description,
              style = MaterialTheme.typography.bodySmall,
              color = CardTextTone
            )
          }
        }
      }
    }
  }
}
