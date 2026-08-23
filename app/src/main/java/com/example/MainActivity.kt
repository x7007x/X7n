package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MyApplicationTheme

// Gold & Glass Palette matching screenshot
val GoldMain = Color(0xFFDFB24C)
val GoldLightAccent = Color(0xFFF5D77F)
val GoldMuted = Color(0xFFB89640)
val GlassDarkBg = Color(0xCC13151A)
val GlassCardBg = Color(0x80181A22)
val GlassBorderColor = Color(0x35DFB24C)
val GlassBorderSubtle = Color(0x22FFFFFF)
val TextMutedColor = Color(0xFFA6A195)
val DarkPageBg = Color(0xFF0C0D10)

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        MyApplicationTheme {
          var currentNavTab by remember { mutableStateOf("الرئيسية") }
          var bookmarkedIds by remember { 
            mutableStateOf(setOf("king_ramses2", "pyr_khufu", "art_mask")) 
          }
          var isViewingAll by remember { mutableStateOf(false) }
          var viewAllCategory by remember { mutableStateOf("الكل") }

          // Handle Android system back press
          BackHandler(enabled = isViewingAll || currentNavTab != "الرئيسية") {
            if (isViewingAll) {
              isViewingAll = false
            } else if (currentNavTab != "الرئيسية") {
              currentNavTab = "الرئيسية"
            }
          }

          Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
              CustomCurvedBottomNavigation(
                selectedTab = if (isViewingAll) "الرئيسية" else currentNavTab,
                onTabSelected = { tab ->
                  isViewingAll = false
                  currentNavTab = tab
                }
              )
            },
            containerColor = DarkPageBg
          ) { innerPadding ->
            Box(
              modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(DarkPageBg)
            ) {
              when {
                isViewingAll -> {
                  ViewAllScreen(
                    initialCategory = viewAllCategory,
                    bookmarkedIds = bookmarkedIds,
                    onBookmarkToggle = { id ->
                      bookmarkedIds = if (bookmarkedIds.contains(id)) {
                        bookmarkedIds - id
                      } else {
                        bookmarkedIds + id
                      }
                    },
                    onBack = { isViewingAll = false }
                  )
                }
                currentNavTab == "الرئيسية" -> {
                  HomeScreen(
                    bookmarkedIds = bookmarkedIds,
                    onBookmarkToggle = { id ->
                      bookmarkedIds = if (bookmarkedIds.contains(id)) {
                        bookmarkedIds - id
                      } else {
                        bookmarkedIds + id
                      }
                    },
                    onViewAllClick = { category ->
                      viewAllCategory = category
                      isViewingAll = true
                    }
                  )
                }
                currentNavTab == "المفضلة" -> {
                  FavoritesScreen(
                    bookmarkedIds = bookmarkedIds,
                    onBookmarkToggle = { id ->
                      bookmarkedIds = if (bookmarkedIds.contains(id)) {
                        bookmarkedIds - id
                      } else {
                        bookmarkedIds + id
                      }
                    },
                    onExploreClick = {
                      currentNavTab = "الرئيسية"
                    }
                  )
                }
                currentNavTab == "خريطة" -> {
                  MapScreen()
                }
                currentNavTab == "الاختبارات" -> {
                  QuizScreen()
                }
                currentNavTab == "المزيد" -> {
                  MoreScreen()
                }
              }
            }
          }
        }
      }
    }
  }
}

@Composable
fun HomeScreen(
  bookmarkedIds: Set<String>,
  onBookmarkToggle: (String) -> Unit,
  onViewAllClick: (String) -> Unit
) {
  var selectedCategory by remember { mutableStateOf("الملوك") }
  var searchQuery by remember { mutableStateOf("") }

  val filteredItems = remember(selectedCategory, searchQuery) {
    val itemsInCategory = allEgyptItems.filter { it.category == selectedCategory }
    if (searchQuery.isBlank()) {
      itemsInCategory
    } else {
      itemsInCategory.filter {
        it.title.contains(searchQuery.trim(), ignoreCase = true) ||
        it.tag1.contains(searchQuery.trim(), ignoreCase = true) ||
        it.tag2.contains(searchQuery.trim(), ignoreCase = true) ||
        it.subtitle.contains(searchQuery.trim(), ignoreCase = true)
      }
    }
  }

  val categoryMeta = categoryMetaMap[selectedCategory] ?: CategoryMeta(
    title = selectedCategory,
    subtitle = "استكشف ملوك مصر عبر العصور",
    icon = Icons.Outlined.WorkspacePremium
  )

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(DarkPageBg)
  ) {
    item {
      ExactHeroHeaderSection()
    }
    item {
      ExactSearchBarSection(
        query = searchQuery,
        onQueryChange = { searchQuery = it }
      )
    }
    item {
      ExactCategoriesSection(
        selectedCategory = selectedCategory,
        onCategorySelect = { selectedCategory = it }
      )
    }
    item {
      ExactSectionHeader(
        meta = categoryMeta,
        onViewAllClick = { onViewAllClick(selectedCategory) }
      )
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
      items(filteredItems, key = { it.id }) { item ->
        ExactPharaohCard(
          item = item,
          isBookmarked = bookmarkedIds.contains(item.id),
          onBookmarkToggle = { onBookmarkToggle(item.id) }
        )
      }
    }
    item {
      Spacer(modifier = Modifier.height(115.dp)) // padding for floating bottom nav
    }
  }
}

// -------------------------------------------------------------
// EXACT HERO HEADER MATCHING UPLOADED PHOTO
// -------------------------------------------------------------
@Composable
fun ExactHeroHeaderSection() {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .height(240.dp)
  ) {
    // Hieroglyphic Stone Dark Background
    Image(
      painter = painterResource(id = R.drawable.hero_egypt_hieroglyphics_1787518543829),
      contentDescription = "Hieroglyphic Wall",
      contentScale = ContentScale.Crop,
      modifier = Modifier.fillMaxSize()
    )

    // Dark Gradient Overlay for moody lighting
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(
          Brush.verticalGradient(
            colors = listOf(
              Color(0xCC0C0D10),
              Color(0x990C0D10),
              DarkPageBg
            )
          )
        )
    )

    // Right Side Pharaoh Statue in background (Matching the photo)
    Box(
      modifier = Modifier
        .fillMaxHeight()
        .width(180.dp)
        .align(Alignment.CenterStart) // In RTL: Start is Right side
        .offset(x = 10.dp, y = (-10).dp)
    ) {
      Image(
        painter = painterResource(id = R.drawable.ramses_pharaoh_1787516924600),
        contentDescription = "Pharaoh Bust",
        contentScale = ContentScale.Fit,
        modifier = Modifier.fillMaxSize()
      )
      // Smooth gradient blending pharaoh into dark background
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(
            Brush.horizontalGradient(
              colors = listOf(
                Color.Transparent,
                DarkPageBg.copy(alpha = 0.85f)
              )
            )
          )
      )
    }

    // Top Header Content
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 18.dp, vertical = 8.dp)
    ) {
      Spacer(modifier = Modifier.height(16.dp))

      // Top Actions Bar (Menu on one side, Bell & Profile on other side)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Left Action: 3 Gold Horizontal Bars (Menu)
        Box(
          modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .clickable { /* Menu */ },
          contentAlignment = Alignment.Center
        ) {
          Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Box(
              modifier = Modifier
                .width(20.dp)
                .height(2.2.dp)
                .background(GoldMain, RoundedCornerShape(2.dp))
            )
            Box(
              modifier = Modifier
                .width(15.dp)
                .height(2.2.dp)
                .background(GoldMain, RoundedCornerShape(2.dp))
            )
            Box(
              modifier = Modifier
                .width(20.dp)
                .height(2.2.dp)
                .background(GoldMain, RoundedCornerShape(2.dp))
            )
          }
        }

        // Right Actions: Bell with red dot + Profile avatar
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          // Bell Icon
          Box(contentAlignment = Alignment.TopEnd) {
            IconButton(
              onClick = { /* Notifications */ },
              modifier = Modifier.size(36.dp)
            ) {
              Icon(
                imageVector = Icons.Outlined.Notifications,
                contentDescription = "Notifications",
                tint = GoldMain,
                modifier = Modifier.size(23.dp)
              )
            }
            Box(
              modifier = Modifier
                .padding(top = 5.dp, end = 5.dp)
                .size(6.5.dp)
                .background(Color(0xFFE53935), CircleShape)
            )
          }

          // Profile Button in gold ring
          Box(
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(GlassDarkBg)
              .border(1.2.dp, GoldMain, CircleShape)
              .clickable { /* Profile */ },
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Outlined.PersonOutline,
              contentDescription = "Profile",
              tint = GoldMain,
              modifier = Modifier.size(20.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Center Title & Eye of Horus Emblem
      Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Eye of Horus Golden Vector Icon
        EyeOfHorusIcon(modifier = Modifier.size(46.dp))

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = "مصر القديمة",
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.Bold,
          color = GoldMain,
          fontSize = 25.sp,
          letterSpacing = 0.5.sp
        )

        Text(
          text = "موسوعة علمية لعلم المصريات",
          style = MaterialTheme.typography.bodySmall,
          color = GoldLightAccent.copy(alpha = 0.85f),
          fontSize = 11.5.sp
        )
      }
    }
  }
}

// Eye of Horus Custom Egyptian Vector Composable
@Composable
fun EyeOfHorusIcon(modifier: Modifier = Modifier) {
  Canvas(modifier = modifier) {
    val w = size.width
    val h = size.height
    val strokeWidth = 2.4.dp.toPx()
    val gold = GoldMain

    // Eye outline path
    val eyePath = Path().apply {
      moveTo(w * 0.12f, h * 0.45f)
      quadraticTo(w * 0.5f, h * 0.15f, w * 0.88f, h * 0.45f)
      quadraticTo(w * 0.5f, h * 0.75f, w * 0.12f, h * 0.45f)
      close()
    }
    drawPath(path = eyePath, color = gold, style = Stroke(width = strokeWidth, cap = StrokeCap.Round))

    // Eyeball circle
    drawCircle(
      color = gold,
      radius = w * 0.13f,
      center = Offset(w * 0.5f, h * 0.45f),
      style = Fill
    )
    drawCircle(
      color = DarkPageBg,
      radius = w * 0.05f,
      center = Offset(w * 0.5f, h * 0.45f),
      style = Fill
    )

    // Eyebrow top arch
    val eyebrowPath = Path().apply {
      moveTo(w * 0.15f, h * 0.28f)
      quadraticTo(w * 0.5f, h * 0.04f, w * 0.85f, h * 0.28f)
    }
    drawPath(path = eyebrowPath, color = gold, style = Stroke(width = strokeWidth, cap = StrokeCap.Round))

    // Teardrop / Falcon marking underneath
    val tearPath = Path().apply {
      moveTo(w * 0.5f, h * 0.75f)
      lineTo(w * 0.5f, h * 0.95f)
    }
    drawPath(path = tearPath, color = gold, style = Stroke(width = strokeWidth, cap = StrokeCap.Round))

    // Spiral curve on left/right
    val spiralPath = Path().apply {
      moveTo(w * 0.62f, h * 0.7f)
      quadraticTo(w * 0.8f, h * 0.85f, w * 0.7f, h * 0.95f)
    }
    drawPath(path = spiralPath, color = gold, style = Stroke(width = strokeWidth, cap = StrokeCap.Round))
  }
}

// -------------------------------------------------------------
// EXACT SEARCH BAR MATCHING UPLOADED PHOTO
// -------------------------------------------------------------
@Composable
fun ExactSearchBarSection(
  query: String,
  onQueryChange: (String) -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 18.dp, vertical = 6.dp)
  ) {
    TextField(
      value = query,
      onValueChange = onQueryChange,
      singleLine = true,
      placeholder = {
        Text(
          "...ابحث عن ملك، متحف، مقبرة، مجموعة",
          color = TextMutedColor.copy(alpha = 0.75f),
          fontSize = 12.5.sp,
          textAlign = TextAlign.Start
        )
      },
      leadingIcon = {
        Icon(
          imageVector = Icons.Outlined.Search,
          contentDescription = "Search",
          tint = GoldMain,
          modifier = Modifier.size(21.dp)
        )
      },
      trailingIcon = {
        if (query.isNotEmpty()) {
          IconButton(onClick = { onQueryChange("") }) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Clear",
              tint = TextMutedColor,
              modifier = Modifier.size(18.dp)
            )
          }
        }
      },
      colors = TextFieldDefaults.colors(
        focusedContainerColor = GlassDarkBg,
        unfocusedContainerColor = GlassDarkBg,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        cursorColor = GoldMain
      ),
      shape = RoundedCornerShape(26.dp),
      modifier = Modifier
        .fillMaxWidth()
        .height(50.dp)
        .border(
          width = 1.dp,
          color = GlassBorderColor,
          shape = RoundedCornerShape(26.dp)
        )
    )
  }
}

// -------------------------------------------------------------
// EXACT CATEGORIES SECTION MATCHING UPLOADED PHOTO
// -------------------------------------------------------------
@Composable
fun ExactCategoriesSection(
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
    items(categoriesList) { category ->
      val isSelected = category.title == selectedCategory
      ExactCategoryItem(
        category = category,
        isSelected = isSelected,
        onClick = { onCategorySelect(category.title) }
      )
    }
  }
}

@Composable
fun ExactCategoryItem(
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
      .background(
        if (isSelected) Color(0x33DFB24C)
        else Color(0x66181A22)
      )
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
      overflow = TextOverflow.Ellipsis,
      fontSize = 10.5.sp
    )
  }
}

// -------------------------------------------------------------
// EXACT SECTION HEADER MATCHING UPLOADED PHOTO
// -------------------------------------------------------------
@Composable
fun ExactSectionHeader(
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
    // Right: Crown + Category Title + Subtitle
    Row(verticalAlignment = Alignment.CenterVertically) {
      Icon(
        imageVector = Icons.Filled.WorkspacePremium, // Crown symbol
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
          color = Color(0xFFF3D58C),
          fontSize = 18.sp
        )
        Text(
          text = meta.subtitle,
          style = MaterialTheme.typography.bodySmall,
          color = TextMutedColor,
          fontSize = 11.5.sp
        )
      }
    }

    // Left: "عرض الكل" + < Arrow
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier
        .clip(RoundedCornerShape(10.dp))
        .clickable(onClick = onViewAllClick)
        .padding(horizontal = 6.dp, vertical = 4.dp)
    ) {
      Icon(
        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
        contentDescription = "View All",
        tint = GoldMain,
        modifier = Modifier.size(16.dp)
      )
      Spacer(modifier = Modifier.width(3.dp))
      Text(
        text = "عرض الكل",
        style = MaterialTheme.typography.labelMedium,
        color = GoldMain,
        fontSize = 12.5.sp
      )
    }
  }
}

// -------------------------------------------------------------
// EXACT PHARAOH CARD (COPIED EXACTLY FROM UPLOADED SCREENSHOT)
// -------------------------------------------------------------
@Composable
fun ExactPharaohCard(
  item: EgyptItem,
  isBookmarked: Boolean,
  onBookmarkToggle: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = GlassCardBg),
    border = BorderStroke(1.dp, GlassBorderColor),
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 18.dp, vertical = 6.dp)
      .height(138.dp)
      .clickable { /* Open Details */ }
  ) {
    Row(
      modifier = Modifier.fillMaxSize(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // 1. LEFT SIDE: Arrow "<" + Pharaoh Statue Image
      Row(
        modifier = Modifier
          .weight(0.36f)
          .fillMaxHeight(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Left Arrow "<"
        Icon(
          imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
          contentDescription = "Open",
          tint = GoldMain.copy(alpha = 0.7f),
          modifier = Modifier
            .padding(start = 10.dp)
            .size(20.dp)
        )

        // Pharaoh Statue
        Box(
          modifier = Modifier
            .fillMaxHeight()
            .weight(1f),
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
          // Soft radial/horizontal mask so statue blends seamlessly into the card
          Box(
            modifier = Modifier
              .fillMaxSize()
              .background(
                Brush.horizontalGradient(
                  colors = listOf(
                    Color.Transparent,
                    GlassCardBg.copy(alpha = 0.7f)
                  )
                )
              )
          )
        }
      }

      // 2. CENTER CONTENT: Title + Badges + Date
      Column(
        modifier = Modifier
          .weight(0.50f)
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
          overflow = TextOverflow.Ellipsis,
          fontSize = 18.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Badges Row
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          GlassBadge(text = item.tag1)
          if (item.tag2.isNotBlank()) {
            GlassBadge(text = item.tag2)
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Date Row: Calendar Icon + Date Text
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = item.subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = TextMutedColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontSize = 12.sp
          )
          Spacer(modifier = Modifier.width(5.dp))
          Icon(
            imageVector = Icons.Outlined.CalendarToday,
            contentDescription = "Date",
            tint = GoldMuted,
            modifier = Modifier.size(13.dp)
          )
        }
      }

      // Thin Vertical Divider
      Box(
        modifier = Modifier
          .width(1.dp)
          .fillMaxHeight()
          .background(GlassBorderColor.copy(alpha = 0.4f))
      )

      // 3. FAR RIGHT: Vertical Sidebar with Bookmark & Stacked Hieroglyphs
      Column(
        modifier = Modifier
          .weight(0.14f)
          .fillMaxHeight()
          .background(Color(0x400C0D10)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
      ) {
        // Bookmark Button at Top
        IconButton(
          onClick = onBookmarkToggle,
          modifier = Modifier.size(30.dp)
        ) {
          Icon(
            imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
            contentDescription = "Bookmark",
            tint = if (isBookmarked) GoldMain else GoldMuted.copy(alpha = 0.85f),
            modifier = Modifier.size(20.dp)
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Stacked Hieroglyphic Symbols
        HieroglyphSymbol1(modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.height(6.dp))
        HieroglyphSymbol2(modifier = Modifier.size(18.dp))
      }
    }
  }
}

// Egyptian Hieroglyphic Vector Assets
@Composable
fun HieroglyphSymbol1(modifier: Modifier = Modifier) {
  Canvas(modifier = modifier) {
    val w = size.width
    val h = size.height
    val gold = GoldMuted.copy(alpha = 0.7f)
    val strokeWidth = 1.6.dp.toPx()

    // Eye/Scribe reed symbol
    drawOval(
      color = gold,
      topLeft = Offset(w * 0.1f, h * 0.3f),
      size = androidx.compose.ui.geometry.Size(w * 0.8f, h * 0.4f),
      style = Stroke(width = strokeWidth)
    )
    drawLine(
      color = gold,
      start = Offset(w * 0.2f, h * 0.5f),
      end = Offset(w * 0.8f, h * 0.5f),
      strokeWidth = strokeWidth
    )
  }
}

@Composable
fun HieroglyphSymbol2(modifier: Modifier = Modifier) {
  Canvas(modifier = modifier) {
    val w = size.width
    val h = size.height
    val gold = GoldMuted.copy(alpha = 0.7f)
    val strokeWidth = 1.6.dp.toPx()

    // Ankh / Key of life simplified outline
    drawCircle(
      color = gold,
      radius = w * 0.25f,
      center = Offset(w * 0.5f, h * 0.28f),
      style = Stroke(width = strokeWidth)
    )
    drawLine(
      color = gold,
      start = Offset(w * 0.5f, h * 0.53f),
      end = Offset(w * 0.5f, h * 0.95f),
      strokeWidth = strokeWidth
    )
    drawLine(
      color = gold,
      start = Offset(w * 0.2f, h * 0.65f),
      end = Offset(w * 0.8f, h * 0.65f),
      strokeWidth = strokeWidth
    )
  }
}

@Composable
fun GlassBadge(text: String) {
  Box(
    modifier = Modifier
      .border(
        width = 1.dp,
        color = GlassBorderSubtle,
        shape = RoundedCornerShape(12.dp)
      )
      .background(Color(0x66161820), RoundedCornerShape(12.dp))
      .padding(horizontal = 8.dp, vertical = 3.dp)
  ) {
    Text(
      text = text,
      style = MaterialTheme.typography.labelSmall,
      color = TextMutedColor,
      fontSize = 10.sp,
      maxLines = 1
    )
  }
}

// -------------------------------------------------------------
// DYNAMIC CURVED BOTTOM NAVIGATION (WITH DYNAMIC CIRCLE & ARCH)
// -------------------------------------------------------------
@Composable
fun CustomCurvedBottomNavigation(
  selectedTab: String,
  onTabSelected: (String) -> Unit
) {
  // Ordered from Right to Left in RTL:
  val navItems = listOf(
    NavItemData("خريطة", "خريطة مصر", Icons.Outlined.Explore, Icons.Outlined.Explore),
    NavItemData("المفضلة", "المفضلة", Icons.Filled.Favorite, Icons.Outlined.FavoriteBorder),
    NavItemData("الرئيسية", "الرئيسية", Icons.Filled.Home, Icons.Outlined.Home),
    NavItemData("الاختبارات", "الاختبارات", Icons.Filled.Assignment, Icons.Outlined.Assignment),
    NavItemData("المزيد", "المزيد", Icons.Filled.MoreHoriz, Icons.Outlined.MoreHoriz)
  )

  val selectedIndex = navItems.indexOfFirst { it.id == selectedTab }.coerceAtLeast(0)
  val animatedIndex by animateFloatAsState(
    targetValue = selectedIndex.toFloat(),
    animationSpec = tween(durationMillis = 280),
    label = "nav_active_index"
  )

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 14.dp, vertical = 12.dp),
    contentAlignment = Alignment.BottomCenter
  ) {
    // Custom Canvas drawing the navbar with dynamic curved arch around active item
    Canvas(
      modifier = Modifier
        .fillMaxWidth()
        .height(70.dp)
    ) {
      val w = size.width
      val h = size.height
      val cornerRadius = 35.dp.toPx()
      val itemCount = 5
      val itemWidth = w / itemCount

      // Center X of active arch (RTL from right to left)
      val activeCenterX = itemWidth * (animatedIndex + 0.5f)
      val archWidth = 66.dp.toPx()
      val archPeakHeight = 14.dp.toPx()

      val path = Path().apply {
        // Start top-left corner
        moveTo(0f, cornerRadius)
        // Top-left arc
        arcTo(
          rect = Rect(0f, 0f, cornerRadius * 2, cornerRadius * 2),
          startAngleDegrees = 180f,
          sweepAngleDegrees = 90f,
          forceMoveTo = false
        )

        // Top line leading to arch
        val archLeft = (activeCenterX - archWidth / 2).coerceAtLeast(cornerRadius)
        val archRight = (activeCenterX + archWidth / 2).coerceAtMost(w - cornerRadius)

        lineTo(archLeft, 0f)

        // Smooth cubic curve rising around active circle
        cubicTo(
          activeCenterX - archWidth * 0.35f, 0f,
          activeCenterX - archWidth * 0.25f, -archPeakHeight,
          activeCenterX, -archPeakHeight
        )
        cubicTo(
          activeCenterX + archWidth * 0.25f, -archPeakHeight,
          activeCenterX + archWidth * 0.35f, 0f,
          archRight, 0f
        )

        // Top line to top-right corner
        lineTo(w - cornerRadius, 0f)
        // Top-right arc
        arcTo(
          rect = Rect(w - cornerRadius * 2, 0f, w, cornerRadius * 2),
          startAngleDegrees = 270f,
          sweepAngleDegrees = 90f,
          forceMoveTo = false
        )

        // Bottom right arc
        lineTo(w, h - cornerRadius)
        arcTo(
          rect = Rect(w - cornerRadius * 2, h - cornerRadius * 2, w, h),
          startAngleDegrees = 0f,
          sweepAngleDegrees = 90f,
          forceMoveTo = false
        )

        // Bottom line to bottom left
        lineTo(cornerRadius, h)
        arcTo(
          rect = Rect(0f, h - cornerRadius * 2, cornerRadius * 2, h),
          startAngleDegrees = 90f,
          sweepAngleDegrees = 90f,
          forceMoveTo = false
        )
        close()
      }

      // Draw Glass Dark Fill
      drawPath(
        path = path,
        color = Color(0xF214161E)
      )

      // Draw Glass Border with Golden Glow
      drawPath(
        path = path,
        brush = Brush.linearGradient(
          colors = listOf(
            GoldMain.copy(alpha = 0.55f),
            GlassBorderColor,
            GoldMain.copy(alpha = 0.55f)
          )
        ),
        style = Stroke(width = 1.4.dp.toPx())
      )
    }

    // Interactive Items Row
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .height(70.dp),
      horizontalArrangement = Arrangement.SpaceEvenly,
      verticalAlignment = Alignment.CenterVertically
    ) {
      navItems.forEachIndexed { index, item ->
        val isSelected = selectedIndex == index

        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center,
          modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .clickable(
              interactionSource = remember { MutableInteractionSource() },
              indication = null
            ) { onTabSelected(item.id) }
            .padding(vertical = 4.dp)
        ) {
          if (isSelected) {
            // Elevated Active Circle with Gold Ring
            Box(
              modifier = Modifier
                .offset(y = (-14).dp)
                .size(50.dp)
                .clip(CircleShape)
                .background(
                  Brush.linearGradient(
                    colors = listOf(
                      Color(0xFF1F222C),
                      Color(0xFF14161E)
                    )
                  )
                )
                .border(1.6.dp, GoldMain, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = item.activeIcon,
                contentDescription = item.title,
                tint = GoldMain,
                modifier = Modifier.size(24.dp)
              )
            }

            // Arabic Title + Gold Indicator
            Text(
              text = item.title,
              style = MaterialTheme.typography.labelSmall,
              color = GoldMain,
              fontSize = 10.5.sp,
              fontWeight = FontWeight.Bold,
              maxLines = 1,
              modifier = Modifier.offset(y = (-10).dp)
            )
          } else {
            // Inactive Tab
            Icon(
              imageVector = item.inactiveIcon,
              contentDescription = item.title,
              tint = TextMutedColor.copy(alpha = 0.75f),
              modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = item.title,
              style = MaterialTheme.typography.labelSmall,
              color = TextMutedColor.copy(alpha = 0.75f),
              fontSize = 10.sp,
              fontWeight = FontWeight.Normal,
              maxLines = 1
            )
          }
        }
      }
    }
  }
}

data class NavItemData(
  val id: String,
  val title: String,
  val activeIcon: ImageVector,
  val inactiveIcon: ImageVector
)

// -------------------------------------------------------------
// VIEW ALL & FAVORITES SCREENS WITH GLASS STYLING
// -------------------------------------------------------------
@Composable
fun ViewAllScreen(
  initialCategory: String,
  bookmarkedIds: Set<String>,
  onBookmarkToggle: (String) -> Unit,
  onBack: () -> Unit
) {
  var selectedCategory by remember { mutableStateOf(initialCategory) }
  var searchQuery by remember { mutableStateOf("") }

  val allCategoriesFilter = remember {
    listOf("الكل") + categoriesList.map { it.title }
  }

  val displayedItems = remember(selectedCategory, searchQuery) {
    val base = if (selectedCategory == "الكل") allEgyptItems else allEgyptItems.filter { it.category == selectedCategory }
    if (searchQuery.isBlank()) {
      base
    } else {
      base.filter {
        it.title.contains(searchQuery.trim(), ignoreCase = true) ||
        it.tag1.contains(searchQuery.trim(), ignoreCase = true) ||
        it.tag2.contains(searchQuery.trim(), ignoreCase = true) ||
        it.subtitle.contains(searchQuery.trim(), ignoreCase = true)
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
      horizontalArrangement = Arrangement.SpaceBetween
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

    // Search bar
    ExactSearchBarSection(
      query = searchQuery,
      onQueryChange = { searchQuery = it }
    )

    Spacer(modifier = Modifier.height(4.dp))

    // Filter Chips
    LazyRow(
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 8.dp),
      contentPadding = PaddingValues(horizontal = 18.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      items(allCategoriesFilter) { cat ->
        val isSelected = cat == selectedCategory
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) Color(0x33DFB24C) else GlassDarkBg)
            .border(
              1.dp,
              if (isSelected) GoldMain else GlassBorderSubtle,
              RoundedCornerShape(20.dp)
            )
            .clickable { selectedCategory = cat }
            .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
          Text(
            text = cat,
            style = MaterialTheme.typography.labelMedium,
            color = if (isSelected) GoldMain else TextMutedColor,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            fontSize = 12.sp
          )
        }
      }
    }

    // Items List
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
          ExactPharaohCard(
            item = item,
            isBookmarked = bookmarkedIds.contains(item.id),
            onBookmarkToggle = { onBookmarkToggle(item.id) }
          )
        }
      }
      item {
        Spacer(modifier = Modifier.height(115.dp))
      }
    }
  }
}

@Composable
fun FavoritesScreen(
  bookmarkedIds: Set<String>,
  onBookmarkToggle: (String) -> Unit,
  onExploreClick: () -> Unit
) {
  var selectedFilter by remember { mutableStateOf("الكل") }
  var searchQuery by remember { mutableStateOf("") }

  val bookmarkedItems = remember(bookmarkedIds) {
    allEgyptItems.filter { bookmarkedIds.contains(it.id) }
  }

  val availableCategories = remember(bookmarkedItems) {
    listOf("الكل") + bookmarkedItems.map { it.category }.distinct()
  }

  val filteredFavorites = remember(bookmarkedItems, selectedFilter, searchQuery) {
    val inCategory = if (selectedFilter == "الكل") bookmarkedItems else bookmarkedItems.filter { it.category == selectedFilter }
    if (searchQuery.isBlank()) {
      inCategory
    } else {
      inCategory.filter {
        it.title.contains(searchQuery.trim(), ignoreCase = true) ||
        it.tag1.contains(searchQuery.trim(), ignoreCase = true) ||
        it.subtitle.contains(searchQuery.trim(), ignoreCase = true)
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
            color = GoldMain,
            fontSize = 20.sp
          )
          Text(
            text = "السجلات المحفوظة للرجوع السريع",
            style = MaterialTheme.typography.bodySmall,
            color = TextMutedColor,
            fontSize = 11.5.sp
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
          fontWeight = FontWeight.Bold,
          fontSize = 11.sp
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
            color = GoldMain,
            fontSize = 18.sp
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "اضغط على أيقونة الإشارة المرجعية داخل أي بطاقة لحفظها هنا",
            style = MaterialTheme.typography.bodySmall,
            color = TextMutedColor,
            textAlign = TextAlign.Center,
            fontSize = 13.sp
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
              text = "استكشف الموسوعة",
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp
            )
          }
        }
      }
    } else {
      ExactSearchBarSection(
        query = searchQuery,
        onQueryChange = { searchQuery = it }
      )

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
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(if (isSelected) Color(0x33DFB24C) else GlassDarkBg)
                .border(
                  1.dp,
                  if (isSelected) GoldMain else GlassBorderSubtle,
                  RoundedCornerShape(20.dp)
                )
                .clickable { selectedFilter = cat }
                .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
              Text(
                text = cat,
                style = MaterialTheme.typography.labelMedium,
                color = if (isSelected) GoldMain else TextMutedColor,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                fontSize = 12.sp
              )
            }
          }
        }
      }

      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .weight(1f)
      ) {
        items(filteredFavorites, key = { it.id }) { item ->
          ExactPharaohCard(
            item = item,
            isBookmarked = true,
            onBookmarkToggle = { onBookmarkToggle(item.id) }
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
fun MapScreen() {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(DarkPageBg)
      .padding(20.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Icon(
      imageVector = Icons.Outlined.Explore,
      contentDescription = "Map",
      tint = GoldMain,
      modifier = Modifier.size(56.dp)
    )
    Spacer(modifier = Modifier.height(14.dp))
    Text(
      text = "خريطة مصر القديمة",
      style = MaterialTheme.typography.titleLarge,
      fontWeight = FontWeight.Bold,
      color = GoldMain
    )
    Spacer(modifier = Modifier.height(6.dp))
    Text(
      text = "استكشف توزيع الآثار والمعابد والمتاحف على امتداد وادي النيل",
      style = MaterialTheme.typography.bodyMedium,
      color = TextMutedColor,
      textAlign = TextAlign.Center
    )
  }
}

@Composable
fun QuizScreen() {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(DarkPageBg)
      .padding(20.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Icon(
      imageVector = Icons.Outlined.Assignment,
      contentDescription = "Quiz",
      tint = GoldMain,
      modifier = Modifier.size(56.dp)
    )
    Spacer(modifier = Modifier.height(14.dp))
    Text(
      text = "اختبارات علم المصريات",
      style = MaterialTheme.typography.titleLarge,
      fontWeight = FontWeight.Bold,
      color = GoldMain
    )
    Spacer(modifier = Modifier.height(6.dp))
    Text(
      text = "اختبر معلوماتك عن الفراعنة والأسرات وتاريخ مصر القديم",
      style = MaterialTheme.typography.bodyMedium,
      color = TextMutedColor,
      textAlign = TextAlign.Center
    )
  }
}

@Composable
fun MoreScreen() {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(DarkPageBg)
      .padding(20.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Icon(
      imageVector = Icons.Outlined.MoreHoriz,
      contentDescription = "More",
      tint = GoldMain,
      modifier = Modifier.size(56.dp)
    )
    Spacer(modifier = Modifier.height(14.dp))
    Text(
      text = "المزيد من المصادر",
      style = MaterialTheme.typography.titleLarge,
      fontWeight = FontWeight.Bold,
      color = GoldMain
    )
    Spacer(modifier = Modifier.height(6.dp))
    Text(
      text = "المكتبة العلمية والمراجع المعتمدة لعلم الآثار المصرية",
      style = MaterialTheme.typography.bodyMedium,
      color = TextMutedColor,
      textAlign = TextAlign.Center
    )
  }
}

// -------------------------------------------------------------
// DATA MODELS & MASTER LISTS
// -------------------------------------------------------------
data class Category(
  val title: String,
  val icon: ImageVector
)

data class CategoryMeta(
  val title: String,
  val subtitle: String,
  val icon: ImageVector
)

val categoryMetaMap = mapOf(
  "الملوك" to CategoryMeta("الملوك والفراعنة", "اكتشف ملوك مصر عبر العصور", Icons.Outlined.WorkspacePremium),
  "المتاحف" to CategoryMeta("المتاحف المصرية", "أشهر متاحف الحضارة وصروح الآثار الخالدة", Icons.Outlined.AccountBalance),
  "المقابر" to CategoryMeta("المقابر الملكية", "أسرار وادي الملوك والمقابر الجدارية المهيبة", Icons.Outlined.MeetingRoom),
  "المجموعات الهرمية" to CategoryMeta("المجموعات الهرمية", "عجائب العمارة وأهرامات ملوك مصر القديمة", Icons.Outlined.ChangeHistory),
  "الآثار" to CategoryMeta("القطع الأثرية", "أعظم مقتنيات وروائع الفن الفرعوني الخالد", Icons.Outlined.VerticalAlignTop),
  "المواقع الأثرية" to CategoryMeta("المواقع الأثرية", "معابد كبرى ومدن تاريخية أثرية على ضفاف النيل", Icons.Outlined.ViewColumn),
  "المزيد" to CategoryMeta("استكشاف شامل", "كل ما يتعلق بالحضارة واللغة والتاريخ المصري", Icons.Outlined.GridView)
)

val categoriesList = listOf(
  Category("الملوك", Icons.Outlined.WorkspacePremium),
  Category("المتاحف", Icons.Outlined.AccountBalance),
  Category("المقابر", Icons.Outlined.MeetingRoom),
  Category("المجموعات الهرمية", Icons.Outlined.ChangeHistory),
  Category("الآثار", Icons.Outlined.VerticalAlignTop),
  Category("المواقع الأثرية", Icons.Outlined.ViewColumn),
  Category("المزيد", Icons.Outlined.GridView)
)

data class EgyptItem(
  val id: String,
  val category: String,
  val title: String,
  val tag1: String,
  val tag2: String,
  val subtitle: String,
  val imageRes: Int,
  val subtitleIcon: ImageVector = Icons.Outlined.DateRange
)

val allEgyptItems = listOf(
  // 1. الملوك (Kings exactly from Screenshot)
  EgyptItem(
    id = "king_ramses2",
    category = "الملوك",
    title = "رمسيس الثاني",
    tag1 = "الأسرة 19",
    tag2 = "العصر الحديث",
    subtitle = "1213 - 1279 ق.م",
    imageRes = R.drawable.ramses_pharaoh_1787516924600
  ),
  EgyptItem(
    id = "king_merneptah",
    category = "الملوك",
    title = "مرنبتاح",
    tag1 = "الأسرة 19",
    tag2 = "العصر الحديث",
    subtitle = "1213 - 1203 ق.م",
    imageRes = R.drawable.ramses_pharaoh_1787516924600
  ),
  EgyptItem(
    id = "king_seti1",
    category = "الملوك",
    title = "سيتي الأول",
    tag1 = "الأسرة 19",
    tag2 = "العصر الحديث",
    subtitle = "1290 - 1279 ق.م",
    imageRes = R.drawable.pharaoh_3_1787516935888
  ),
  EgyptItem(
    id = "king_amenhotep3",
    category = "الملوك",
    title = "أمنحتب الثالث",
    tag1 = "الأسرة 18",
    tag2 = "العصر الحديث",
    subtitle = "1391 - 1353 ق.م",
    imageRes = R.drawable.pharaoh_3_1787516935888
  ),
  EgyptItem(
    id = "king_thutmose3",
    category = "الملوك",
    title = "تحتمس الثالث",
    tag1 = "الأسرة 18",
    tag2 = "العصر الحديث",
    subtitle = "1479 - 1425 ق.م",
    imageRes = R.drawable.hero_pharaoh_1787516913716
  ),
  EgyptItem(
    id = "king_tut",
    category = "الملوك",
    title = "توت عنخ آمون",
    tag1 = "الأسرة 18",
    tag2 = "العصر الحديث",
    subtitle = "1332 - 1323 ق.م",
    imageRes = R.drawable.hero_egypt_hieroglyphics_1787518543829
  ),

  // 2. المتاحف
  EgyptItem(
    id = "museum_gem",
    category = "المتاحف",
    title = "المتحف المصري الكبير",
    tag1 = "الجيزة",
    tag2 = "أكبر متحف بالعالم",
    subtitle = "كنوز الملك الذهبي توت عنخ آمون",
    imageRes = R.drawable.museum_egypt_1787518889790,
    subtitleIcon = Icons.Outlined.LocationOn
  ),
  EgyptItem(
    id = "museum_nmec",
    category = "المتاحف",
    title = "متحف الحضارة (NMEC)",
    tag1 = "الفسطاط",
    tag2 = "متحف شامل",
    subtitle = "قاعة المومياوات الملكية المهيبة",
    imageRes = R.drawable.museum_egypt_1787518889790,
    subtitleIcon = Icons.Outlined.LocationOn
  ),
  EgyptItem(
    id = "museum_tahrir",
    category = "المتاحف",
    title = "المتحف المصري بالتحرير",
    tag1 = "القاهرة",
    tag2 = "تأسس 1902 م",
    subtitle = "أعرق صرح أثري بالشرق الأوسط",
    imageRes = R.drawable.museum_egypt_1787518889790,
    subtitleIcon = Icons.Outlined.LocationOn
  ),

  // 3. المقابر
  EgyptItem(
    id = "tomb_tut",
    category = "المقابر",
    title = "مقبرة توت عنخ آمون (KV62)",
    tag1 = "وادي الملوك",
    tag2 = "اكتشاف 1922",
    subtitle = "المقبرة الوحيدة المكتشفة كاملة",
    imageRes = R.drawable.tomb_egypt_1787518916344,
    subtitleIcon = Icons.Outlined.LocationOn
  ),
  EgyptItem(
    id = "tomb_seti1",
    category = "المقابر",
    title = "مقبرة سيتي الأول (KV17)",
    tag1 = "وادي الملوك",
    tag2 = "أطول مقبرة",
    subtitle = "أعظم النقوش الجدارية الملونة",
    imageRes = R.drawable.tomb_egypt_1787518916344,
    subtitleIcon = Icons.Outlined.LocationOn
  ),

  // 4. المجموعات الهرمية
  EgyptItem(
    id = "pyr_khufu",
    category = "المجموعات الهرمية",
    title = "الهرم الأكبر (خوفو)",
    tag1 = "الجيزة",
    tag2 = "الأسرة 4",
    subtitle = "عجيبة العالم القديم الباقية",
    imageRes = R.drawable.pyramids_egypt_1787518903667,
    subtitleIcon = Icons.Outlined.LocationOn
  ),
  EgyptItem(
    id = "pyr_djoser",
    category = "المجموعات الهرمية",
    title = "هرم زوسر المدرج",
    tag1 = "سقارة",
    tag2 = "الأسرة 3",
    subtitle = "أول بناء حجري ضخم في التاريخ",
    imageRes = R.drawable.pyramids_egypt_1787518903667,
    subtitleIcon = Icons.Outlined.LocationOn
  ),

  // 5. الآثار
  EgyptItem(
    id = "art_mask",
    category = "الآثار",
    title = "قناع توت عنخ آمون الذهبي",
    tag1 = "ذهب خالص",
    tag2 = "11 كجم",
    subtitle = "رمز الحضارة الفرعونية الخالد",
    imageRes = R.drawable.hero_pharaoh_1787516913716,
    subtitleIcon = Icons.Outlined.WorkspacePremium
  ),
  EgyptItem(
    id = "art_rosetta",
    category = "الآثار",
    title = "حجر رشيد",
    tag1 = "196 ق.م",
    tag2 = "حجر البازلت",
    subtitle = "مفتاح فك رموز اللغة الهيروغليفية",
    imageRes = R.drawable.hero_egypt_hieroglyphics_1787518543829,
    subtitleIcon = Icons.Outlined.AutoStories
  ),

  // 6. المواقع الأثرية
  EgyptItem(
    id = "site_karnak",
    category = "المواقع الأثرية",
    title = "مجمع معابد الكرنك",
    tag1 = "الأقصر",
    tag2 = "صرح آمون",
    subtitle = "أكبر دور عبادة بالعالم القديم",
    imageRes = R.drawable.museum_egypt_1787518889790,
    subtitleIcon = Icons.Outlined.LocationOn
  ),
  EgyptItem(
    id = "site_abusi",
    category = "المواقع الأثرية",
    title = "معبد أبو سمبل الكبير",
    tag1 = "أسوان",
    tag2 = "رمسيس الثاني",
    subtitle = "معجزة تعامد الشمس الفريدة",
    imageRes = R.drawable.ramses_pharaoh_1787516924600,
    subtitleIcon = Icons.Outlined.LocationOn
  )
)
