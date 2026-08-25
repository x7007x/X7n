package com.negm.egyptology

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
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
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.negm.egyptology.ui.theme.MyApplicationTheme

// Gold & Glass Palette matching Egyptian Royal aesthetics
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
          val context = LocalContext.current
          var currentNavTab by remember { mutableStateOf("الرئيسية") }
          var bookmarkedIds by remember { 
            mutableStateOf(setOf("king_ramses2", "pyr_khufu", "art_mask")) 
          }
          var isViewingAll by remember { mutableStateOf(false) }
          var viewAllCategory by remember { mutableStateOf("الكل") }
          var selectedArticleItem by remember { mutableStateOf<EgyptItem?>(null) }

          // System Back Handler
          BackHandler(enabled = selectedArticleItem != null || isViewingAll || currentNavTab != "الرئيسية") {
            if (selectedArticleItem != null) {
              selectedArticleItem = null
            } else if (isViewingAll) {
              isViewingAll = false
            } else if (currentNavTab != "الرئيسية") {
              currentNavTab = "الرئيسية"
            }
          }

          Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
              if (selectedArticleItem == null) {
                CustomCurvedBottomNavigation(
                  selectedTab = if (isViewingAll) "الرئيسية" else currentNavTab,
                  onTabSelected = { tab ->
                    isViewingAll = false
                    selectedArticleItem = null
                    currentNavTab = tab
                  }
                )
              }
            },
            containerColor = DarkPageBg
          ) { innerPadding ->
            // Stable key for animated navigation between screens
            val navKey = when {
              selectedArticleItem != null -> "detail"
              isViewingAll -> "view_all"
              else -> currentNavTab
            }

            val tabOrder = listOf("خريطة", "المفضلة", "الرئيسية", "الاختبارات", "المزيد")

            AnimatedContent(
              targetState = navKey,
              modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
              transitionSpec = {
                if (targetState == "detail") {
                  // Detail screen slides up from the bottom edge
                  slideInVertically(
                    initialOffsetY = { it / 8 },
                    animationSpec = tween(280, easing = FastOutSlowInEasing)
                  ) + fadeIn(tween(240)) togetherWith
                    fadeOut(tween(160))
                } else {
                  // Tabs cross-fade with a gentle RTL horizontal slide
                  fun orderOf(key: String) =
                    tabOrder.indexOf(if (key == "view_all") "الرئيسية" else key).let { if (it < 0) 0 else it }
                  val direction = if (orderOf(targetState) >= orderOf(initialState)) -1 else 1
                  (
                    slideInHorizontally(
                      initialOffsetX = { direction * it / 14 },
                      animationSpec = tween(260, easing = FastOutSlowInEasing)
                    ) + fadeIn(tween(240))
                    ) togetherWith (
                    slideOutHorizontally(
                      targetOffsetX = { -direction * it / 14 },
                      animationSpec = tween(220)
                    ) + fadeOut(tween(180))
                    )
                }
              },
              label = "nav_screen_transition"
            ) { key ->
              Box(
                modifier = Modifier
                  .fillMaxSize()
                  .background(DarkPageBg)
              ) {
                when {
                  key == "detail" && selectedArticleItem != null -> {
                    ArticleDetailScreen(
                      item = selectedArticleItem!!,
                      isBookmarked = bookmarkedIds.contains(selectedArticleItem!!.id),
                      onBookmarkToggle = {
                        val id = selectedArticleItem!!.id
                        bookmarkedIds = if (bookmarkedIds.contains(id)) bookmarkedIds - id else bookmarkedIds + id
                      },
                      onShareClick = {
                        shareEgyptArticle(context, selectedArticleItem!!)
                      },
                      onBack = { selectedArticleItem = null }
                    )
                  }
                  key == "view_all" -> {
                    ViewAllScreen(
                      initialCategory = viewAllCategory,
                      bookmarkedIds = bookmarkedIds,
                      onBookmarkToggle = { id ->
                        bookmarkedIds = if (bookmarkedIds.contains(id)) bookmarkedIds - id else bookmarkedIds + id
                      },
                      onItemClick = { item ->
                        selectedArticleItem = item
                      },
                      onBack = { isViewingAll = false }
                    )
                  }
                  key == "الرئيسية" -> {
                    HomeScreen(
                      bookmarkedIds = bookmarkedIds,
                      onBookmarkToggle = { id ->
                        bookmarkedIds = if (bookmarkedIds.contains(id)) bookmarkedIds - id else bookmarkedIds + id
                      },
                      onItemClick = { item ->
                        selectedArticleItem = item
                      },
                      onViewAllClick = { category ->
                        viewAllCategory = category
                        isViewingAll = true
                      }
                    )
                  }
                  key == "المفضلة" -> {
                    FavoritesScreen(
                      bookmarkedIds = bookmarkedIds,
                      onBookmarkToggle = { id ->
                        bookmarkedIds = if (bookmarkedIds.contains(id)) bookmarkedIds - id else bookmarkedIds + id
                      },
                      onItemClick = { item ->
                        selectedArticleItem = item
                      },
                      onExploreClick = {
                        currentNavTab = "الرئيسية"
                      }
                    )
                  }
                  key == "خريطة" -> {
                    MapScreen(onItemClick = { selectedArticleItem = it })
                  }
                  key == "الاختبارات" -> {
                    QuizScreen()
                  }
                  key == "المزيد" -> {
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
}

// -------------------------------------------------------------
// HOME SCREEN
// -------------------------------------------------------------
@Composable
fun HomeScreen(
  bookmarkedIds: Set<String>,
  onBookmarkToggle: (String) -> Unit,
  onItemClick: (EgyptItem) -> Unit,
  onViewAllClick: (String) -> Unit
) {
  val context = LocalContext.current
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

// -------------------------------------------------------------
// REDESIGNED HERO HEADER (GLASS TOP BAR + GLOWING EMBLEM)
// -------------------------------------------------------------
@Composable
fun ExactHeroHeaderSection() {
  // Slow breathing glow for the royal emblem
  val infiniteTransition = rememberInfiniteTransition(label = "emblem_glow")
  val emblemGlow by infiniteTransition.animateFloat(
    initialValue = 0.35f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(2200, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "emblem_glow_alpha"
  )

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .height(250.dp)
  ) {
    // Hieroglyphic stone dark background
    Image(
      painter = painterResource(id = R.drawable.hero_egypt_hieroglyphics_1787518543829),
      contentDescription = "Hieroglyphic Wall",
      contentScale = ContentScale.Crop,
      modifier = Modifier.fillMaxSize()
    )

    // Moody gradient overlay
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

    // Pharaoh statue blended into the background (RTL: start = right edge)
    Box(
      modifier = Modifier
        .fillMaxHeight()
        .width(180.dp)
        .align(Alignment.CenterStart)
        .offset(x = 10.dp, y = (-10).dp)
    ) {
      Image(
        painter = painterResource(id = R.drawable.ramses_pharaoh_1787516924600),
        contentDescription = "Pharaoh Bust",
        contentScale = ContentScale.Fit,
        modifier = Modifier.fillMaxSize()
      )
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

    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 18.dp, vertical = 8.dp)
    ) {
      Spacer(modifier = Modifier.height(14.dp))

      // Glass top action bar
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(20.dp))
          .background(Color(0x3314161E))
          .border(1.dp, GlassBorderSubtle, RoundedCornerShape(20.dp))
          .padding(horizontal = 6.dp, vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Menu button (three gold bars in a bordered circle)
        Box(
          modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .border(1.dp, GlassBorderColor, CircleShape)
            .clickable { /* Menu */ },
          contentAlignment = Alignment.Center
        ) {
          Column(
            verticalArrangement = Arrangement.spacedBy(3.5.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Box(modifier = Modifier.width(17.dp).height(2.dp).background(GoldMain, RoundedCornerShape(2.dp)))
            Box(modifier = Modifier.width(12.dp).height(2.dp).background(GoldMuted, RoundedCornerShape(2.dp)))
            Box(modifier = Modifier.width(17.dp).height(2.dp).background(GoldMain, RoundedCornerShape(2.dp)))
          }
        }

        // App wordmark
        Text(
          text = "EGYPTOLOGY",
          style = MaterialTheme.typography.labelMedium,
          fontWeight = FontWeight.Bold,
          color = GoldLightAccent.copy(alpha = 0.9f),
          fontSize = 11.sp,
          letterSpacing = 2.sp
        )

        // Bell with red dot + profile avatar
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(contentAlignment = Alignment.TopEnd) {
            IconButton(onClick = { /* Notifications */ }, modifier = Modifier.size(36.dp)) {
              Icon(
                imageVector = Icons.Outlined.Notifications,
                contentDescription = "Notifications",
                tint = GoldMain,
                modifier = Modifier.size(21.dp)
              )
            }
            Box(
              modifier = Modifier
                .padding(top = 5.dp, end = 5.dp)
                .size(6.5.dp)
                .background(Color(0xFFE53935), CircleShape)
            )
          }
          Spacer(modifier = Modifier.width(2.dp))
          Box(
            modifier = Modifier
              .size(34.dp)
              .clip(CircleShape)
              .background(Brush.linearGradient(listOf(GoldMain.copy(alpha = 0.25f), Color(0xFF14161E))))
              .border(1.2.dp, GoldMain.copy(alpha = 0.85f), CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Outlined.PersonOutline,
              contentDescription = "Profile",
              tint = GoldMain,
              modifier = Modifier.size(19.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.weight(1f))

      // Centered glowing emblem + titles anchored near the bottom
      Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Box(contentAlignment = Alignment.Center) {
          // Radial halo behind the Eye of Horus
          Box(
            modifier = Modifier
              .size(86.dp)
              .graphicsLayer { alpha = emblemGlow * 0.55f }
              .clip(CircleShape)
              .background(
                Brush.radialGradient(
                  listOf(GoldMain.copy(alpha = 0.55f), Color.Transparent)
                )
              )
          )
          EyeOfHorusIcon(modifier = Modifier.size(48.dp))
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
          text = "egyptology",
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.Bold,
          color = GoldMain,
          fontSize = 26.sp,
          letterSpacing = 2.sp
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Subtitle inside a subtle golden pill
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0x33DFB24C))
            .border(1.dp, GlassBorderColor, RoundedCornerShape(20.dp))
            .padding(horizontal = 14.dp, vertical = 5.dp)
        ) {
          Text(
            text = "موسوعة علمية لعلم المصريات",
            style = MaterialTheme.typography.bodySmall,
            color = GoldLightAccent,
            fontSize = 11.5.sp
          )
        }

        Spacer(modifier = Modifier.height(16.dp))
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
        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
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
// EXACT PHARAOH CARD (CLEAN: NO LEFT ARROW, BOOKMARK & SHARE ON RIGHT)
// -------------------------------------------------------------
@Composable
fun ExactPharaohCard(
  item: EgyptItem,
  isBookmarked: Boolean,
  onBookmarkToggle: () -> Unit,
  onShareClick: () -> Unit,
  onClick: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = GlassCardBg),
    border = BorderStroke(1.dp, GlassBorderColor),
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
      // 1. LEFT SIDE: Pharaoh Statue Image directly (NO arrow next to image)
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
        // Soft gradient mask so statue blends seamlessly into the card
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(
              Brush.horizontalGradient(
                colors = listOf(
                  Color.Transparent,
                  GlassCardBg.copy(alpha = 0.75f)
                )
              )
            )
        )
      }

      // 2. CENTER CONTENT: Title + Badges + Date
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

        // Date Row: Calendar/Location Icon + Date Text
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
            imageVector = item.subtitleIcon,
            contentDescription = "Info",
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

      // 3. FAR RIGHT: Vertical Sidebar with Bookmark & Share Actions
      Column(
        modifier = Modifier
          .weight(0.14f)
          .fillMaxHeight()
          .background(Color(0x400C0D10)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
      ) {
        // Bookmark Button at Top
        IconButton(
          onClick = onBookmarkToggle,
          modifier = Modifier.size(32.dp)
        ) {
          Icon(
            imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
            contentDescription = "Bookmark",
            tint = if (isBookmarked) GoldMain else GoldMuted.copy(alpha = 0.85f),
            modifier = Modifier.size(21.dp)
          )
        }

        // Share Button at Bottom
        IconButton(
          onClick = onShareClick,
          modifier = Modifier.size(32.dp)
        ) {
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
// DYNAMIC CURVED BOTTOM NAVIGATION (SPRING ANIMATION + GLOW)
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

  val haptics = LocalHapticFeedback.current
  val selectedIndex = navItems.indexOfFirst { it.id == selectedTab }.coerceAtLeast(0)

  // Bouncy spring drives the arch so it glides between tabs.
  val springSpec = spring<Float>(
    dampingRatio = Spring.DampingRatioMediumBouncy,
    stiffness = Spring.StiffnessLow
  )
  val animatedIndex by animateFloatAsState(
    targetValue = selectedIndex.toFloat(),
    animationSpec = springSpec,
    label = "nav_active_index"
  )

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 14.dp, vertical = 12.dp),
    contentAlignment = Alignment.BottomCenter
  ) {
    // Custom Canvas drawing the navbar with a curved arch that follows the active tab
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

      // Center X of active arch (RTL from right to left: Index 0 is on rightmost edge)
      val activeCenterX = w - itemWidth * (animatedIndex + 0.5f)
      val archHalfWidth = 32.dp.toPx()
      val archPeakHeight = 12.dp.toPx()

      val path = Path().apply {
        moveTo(0f, h - cornerRadius)
        lineTo(0f, cornerRadius)
        arcTo(
          rect = Rect(0f, 0f, cornerRadius * 2, cornerRadius * 2),
          startAngleDegrees = 180f,
          sweepAngleDegrees = 90f,
          forceMoveTo = false
        )
        lineTo(activeCenterX - archHalfWidth, 0f)
        cubicTo(
          activeCenterX - archHalfWidth * 0.5f, 0f,
          activeCenterX - archHalfWidth * 0.35f, -archPeakHeight,
          activeCenterX, -archPeakHeight
        )
        cubicTo(
          activeCenterX + archHalfWidth * 0.35f, -archPeakHeight,
          activeCenterX + archHalfWidth * 0.5f, 0f,
          activeCenterX + archHalfWidth, 0f
        )
        lineTo(w - cornerRadius, 0f)
        arcTo(
          rect = Rect(w - cornerRadius * 2, 0f, w, cornerRadius * 2),
          startAngleDegrees = 270f,
          sweepAngleDegrees = 90f,
          forceMoveTo = false
        )
        lineTo(w, h - cornerRadius)
        arcTo(
          rect = Rect(w - cornerRadius * 2, h - cornerRadius * 2, w, h),
          startAngleDegrees = 0f,
          sweepAngleDegrees = 90f,
          forceMoveTo = false
        )
        lineTo(cornerRadius, h)
        arcTo(
          rect = Rect(0f, h - cornerRadius * 2, cornerRadius * 2, h),
          startAngleDegrees = 90f,
          sweepAngleDegrees = 90f,
          forceMoveTo = false
        )
        close()
      }

      // Soft outer glow behind the glass bar
      drawPath(
        path = path,
        brush = Brush.verticalGradient(
          colors = listOf(GoldMain.copy(alpha = 0.10f), Color.Transparent)
        ),
        style = Stroke(width = 6.dp.toPx())
      )

      // Glass dark fill
      drawPath(path = path, color = Color(0xF214161E))

      // Golden gradient border
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

    // Interactive items row
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .height(70.dp),
      horizontalArrangement = Arrangement.SpaceEvenly,
      verticalAlignment = Alignment.CenterVertically
    ) {
      navItems.forEachIndexed { index, item ->
        val isSelected = selectedIndex == index

        // Per-item animated values (icon scale, icon color, label alpha)
        val iconScale by animateFloatAsState(
          targetValue = if (isSelected) 1f else 0.86f,
          animationSpec = if (isSelected) springSpec else tween(200),
          label = "nav_icon_scale_$index"
        )
        val tint by animateColorAsState(
          targetValue = if (isSelected) GoldMain else TextMutedColor.copy(alpha = 0.75f),
          animationSpec = tween(220),
          label = "nav_tint_$index"
        )
        val labelAlpha by animateFloatAsState(
          targetValue = if (isSelected) 1f else 0.65f,
          animationSpec = tween(220),
          label = "nav_label_alpha_$index"
        )
        val elevation by animateFloatAsState(
          targetValue = if (isSelected) 14.dp.value else 0f,
          animationSpec = springSpec,
          label = "nav_glow_$index"
        )

        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center,
          modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .clickable(
              interactionSource = remember { MutableInteractionSource() },
              indication = null
            ) {
              if (!isSelected) {
                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onTabSelected(item.id)
              }
            }
            .padding(vertical = 4.dp)
        ) {
          Box(contentAlignment = Alignment.Center) {
            if (isSelected) {
              // Golden halo behind the active icon
              Box(
                modifier = Modifier
                  .size(46.dp)
                  .graphicsLayer {
                    scaleX = 1f + (elevation / 40f)
                    scaleY = 1f + (elevation / 40f)
                    alpha = 0.35f
                  }
                  .clip(CircleShape)
                  .background(Brush.radialGradient(listOf(GoldMain.copy(alpha = 0.9f), Color.Transparent)))
              )
            }
            Icon(
              imageVector = item.activeIcon,
              contentDescription = item.title,
              tint = tint,
              modifier = Modifier
                .size(24.dp)
                .graphicsLayer {
                  scaleX = iconScale
                  scaleY = iconScale
                  translationY = if (isSelected) (-12).dp.toPx() else 0f
                }
            )
          }

          Spacer(modifier = Modifier.height(if (isSelected) 16.dp else 4.dp))

          Text(
            text = item.title,
            style = MaterialTheme.typography.labelSmall,
            color = tint,
            fontSize = if (isSelected) 10.5.sp else 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            maxLines = 1,
            modifier = Modifier.graphicsLayer { alpha = labelAlpha }
          )
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
// COMPREHENSIVE ARTICLE DETAIL SCREEN (MARKDOWN, TABLES & RICH DATA)
// -------------------------------------------------------------
@Composable
fun ArticleDetailScreen(
  item: EgyptItem,
  isBookmarked: Boolean,
  onBookmarkToggle: () -> Unit,
  onShareClick: () -> Unit,
  onBack: () -> Unit
) {
  val articleData = remember(item.id) { getDetailedArticle(item.id) }

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

        // Dark gradient overlay
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(
              Brush.verticalGradient(
                colors = listOf(
                  Color(0x990C0D10),
                  Color(0x660C0D10),
                  DarkPageBg
                )
              )
            )
        )

        // Top Actions (Back, Share, Bookmark)
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 18.dp)
            .align(Alignment.TopCenter),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Back Button
          IconButton(
            onClick = onBack,
            modifier = Modifier
              .size(42.dp)
              .border(1.2.dp, GlassBorderColor, CircleShape)
              .background(GlassDarkBg, CircleShape)
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Back",
              tint = GoldMain,
              modifier = Modifier.size(22.dp)
            )
          }

          // Right Actions: Bookmark + Share
          Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            IconButton(
              onClick = onShareClick,
              modifier = Modifier
                .size(42.dp)
                .border(1.2.dp, GlassBorderColor, CircleShape)
                .background(GlassDarkBg, CircleShape)
            ) {
              Icon(
                imageVector = Icons.Outlined.Share,
                contentDescription = "Share",
                tint = GoldMain,
                modifier = Modifier.size(20.dp)
              )
            }

            IconButton(
              onClick = onBookmarkToggle,
              modifier = Modifier
                .size(42.dp)
                .border(1.2.dp, GlassBorderColor, CircleShape)
                .background(GlassDarkBg, CircleShape)
            ) {
              Icon(
                imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                contentDescription = "Bookmark",
                tint = if (isBookmarked) GoldMain else GoldMuted,
                modifier = Modifier.size(22.dp)
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
            if (item.tag2.isNotBlank()) {
              GlassBadge(text = item.tag2)
            }
          }
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = item.title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = GoldMain,
            fontSize = 26.sp
          )
          Text(
            text = item.subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = GoldLightAccent.copy(alpha = 0.9f),
            fontSize = 13.5.sp
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
          value = articleData.cartoucheName,
          modifier = Modifier.weight(1f)
        )
        QuickStatCard(
          title = "الموقع الجغرافي",
          value = articleData.location,
          modifier = Modifier.weight(1f)
        )
      }
    }

    // 3. Archaeological Data Table (جدول التوثيق والبيانات الأثرية)
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 18.dp, vertical = 12.dp)
      ) {
        SectionTitle(title = "جدول التوثيق والبيانات الأثرية", icon = Icons.Outlined.TableChart)

        Spacer(modifier = Modifier.height(10.dp))

        // Glass Table Container
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = GlassCardBg),
          border = BorderStroke(1.dp, GlassBorderColor),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            articleData.tableRows.forEachIndexed { index, row ->
              TableRowItem(
                field = row.first,
                value = row.second,
                isEven = index % 2 == 0
              )
              if (index < articleData.tableRows.size - 1) {
                HorizontalDivider(
                  color = GlassBorderSubtle,
                  thickness = 0.8.dp,
                  modifier = Modifier.padding(vertical = 4.dp)
                )
              }
            }
          }
        }
      }
    }

    // 4. Markdown Formatted Sections (المقالة التاريخية والتحليل)
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
              text = articleData.overviewParagraph,
              style = MaterialTheme.typography.bodyLarge,
              color = Color(0xFFE2DDD3),
              lineHeight = 26.sp,
              fontSize = 14.5.sp
            )
          }
        }
      }
    }

    // 5. Hieroglyphic Quote / Royal Text Callout
    if (articleData.hieroglyphicQuote.isNotBlank()) {
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
              text = "« ${articleData.hieroglyphicQuote} »",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.SemiBold,
              color = GoldLightAccent,
              lineHeight = 24.sp,
              fontSize = 15.sp
            )
            if (articleData.quoteSource.isNotBlank()) {
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = "— ${articleData.quoteSource}",
                style = MaterialTheme.typography.bodySmall,
                color = TextMutedColor,
                fontSize = 11.5.sp
              )
            }
          }
        }
      }
    }

    // 6. Key Archaeological Highlights (Markdown Bullet Points)
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 18.dp, vertical = 10.dp)
      ) {
        SectionTitle(title = "أبرز الإنجازات والوقائع التاريخية", icon = Icons.Outlined.AutoAwesome)

        Spacer(modifier = Modifier.height(10.dp))

        articleData.highlights.forEach { highlight ->
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
              color = Color(0xFFDED8CD),
              lineHeight = 22.sp,
              fontSize = 13.5.sp
            )
          }
        }
      }
    }

    // 7. Academic References & Museum Documentation
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
              lineHeight = 22.sp,
              fontSize = 12.sp
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

@Composable
fun QuickStatCard(title: String, value: String, modifier: Modifier = Modifier) {
  Card(
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = GlassCardBg),
    border = BorderStroke(1.dp, GlassBorderColor),
    modifier = modifier
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      Text(
        text = title,
        style = MaterialTheme.typography.labelSmall,
        color = TextMutedColor,
        fontSize = 11.sp
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = value,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = GoldMain,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        fontSize = 14.sp
      )
    }
  }
}

@Composable
fun SectionTitle(title: String, icon: ImageVector) {
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
      color = Color(0xFFF3D58C),
      fontSize = 17.sp
    )
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
      style = MaterialTheme.typography.bodySmall,
      fontWeight = FontWeight.Bold,
      color = GoldMain,
      modifier = Modifier.weight(0.42f),
      fontSize = 12.5.sp
    )
    Text(
      text = value,
      style = MaterialTheme.typography.bodyMedium,
      color = Color(0xFFE5DFD4),
      modifier = Modifier.weight(0.58f),
      fontSize = 12.5.sp
    )
  }
}

// Share Intent function
fun shareEgyptArticle(context: Context, item: EgyptItem) {
  val article = getDetailedArticle(item.id)
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
    
    مشارك من تطبيق موسوعة مصر القديمة 🇪🇬
  """.trimIndent()

  val sendIntent = Intent().apply {
    action = Intent.ACTION_SEND
    putExtra(Intent.EXTRA_TEXT, shareText)
    putExtra(Intent.EXTRA_TITLE, item.title)
    type = "text/plain"
  }
  val shareIntent = Intent.createChooser(sendIntent, "مشاركة سجل ${item.title}")
  context.startActivity(shareIntent)
}

// -------------------------------------------------------------
// VIEW ALL SCREEN WITH CLEAN SEARCH & FILTERS
// -------------------------------------------------------------
@Composable
fun ViewAllScreen(
  initialCategory: String,
  bookmarkedIds: Set<String>,
  onBookmarkToggle: (String) -> Unit,
  onItemClick: (EgyptItem) -> Unit,
  onBack: () -> Unit
) {
  val context = LocalContext.current
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

// -------------------------------------------------------------
// FAVORITES SCREEN
// -------------------------------------------------------------
@Composable
fun FavoritesScreen(
  bookmarkedIds: Set<String>,
  onBookmarkToggle: (String) -> Unit,
  onItemClick: (EgyptItem) -> Unit,
  onExploreClick: () -> Unit
) {
  val context = LocalContext.current
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

// -------------------------------------------------------------
// MAP, QUIZ & MORE SCREENS
// -------------------------------------------------------------
@Composable
fun MapScreen(onItemClick: (EgyptItem) -> Unit) {
  val sites = remember { allEgyptItems.filter { it.category == "المواقع الأثرية" || it.category == "المجموعات الهرمية" } }

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
          imageVector = Icons.Outlined.Explore,
          contentDescription = "Map",
          tint = GoldMain,
          modifier = Modifier.size(22.dp)
        )
      }
      Spacer(modifier = Modifier.width(12.dp))
      Column {
        Text(
          text = "خريطة المعالم الأثرية",
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.Bold,
          color = GoldMain,
          fontSize = 20.sp
        )
        Text(
          text = "المعالم والمواقع الجغرافية على طول النيل",
          style = MaterialTheme.typography.bodySmall,
          color = TextMutedColor,
          fontSize = 11.5.sp
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
              color = Color(0xFFDCD6CB),
              fontSize = 13.sp,
              lineHeight = 20.sp
            )
          }
        }
      }

      item {
        Spacer(modifier = Modifier.height(10.dp))
        SectionTitle(title = "المعالم والمواقع الموثقة", icon = Icons.Outlined.Place)
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
            Image(
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
                color = TextMutedColor,
                fontSize = 11.5.sp
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

@Composable
fun QuizScreen() {
  var selectedAnswer by remember { mutableStateOf<Int?>(null) }
  var currentQuestionIndex by remember { mutableStateOf(0) }
  var score by remember { mutableStateOf(0) }
  var isSubmitted by remember { mutableStateOf(false) }

  val questions = remember {
    listOf(
      QuizQuestion(
        question = "من هو الملك الفرعوني صاحب أطول فترة حكم في تاريخ مصر القديمة (نحو 66 عاماً)؟",
        options = listOf("تحتمس الثالث", "رمسيس الثاني", "أمنحتب الثالث", "توت عنخ آمون"),
        correctIndex = 1
      ),
      QuizQuestion(
        question = "ما هو المعلم الأثري الوحيد الباقي حتى اليوم من عجائب الدنيا السبع القديمة؟",
        options = listOf("هرم زوسر", "الهرم الأكبر (خوفو)", "منارة الإسكندرية", "معبد أبو سمبل"),
        correctIndex = 1
      ),
      QuizQuestion(
        question = "في أي عام تم اكتشاف مقبرة الملك الذهبي توت عنخ آمون كاملة بوادي الملوك؟",
        options = listOf("1898 م", "1905 م", "1922 م", "1954 م"),
        correctIndex = 2
      )
    )
  }

  val q = questions[currentQuestionIndex]

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
          imageVector = Icons.Outlined.Assignment,
          contentDescription = "Quiz",
          tint = GoldMain,
          modifier = Modifier.size(22.dp)
        )
      }
      Spacer(modifier = Modifier.width(12.dp))
      Column {
        Text(
          text = "اختبارات الحضارة المصرية",
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.Bold,
          color = GoldMain,
          fontSize = 20.sp
        )
        Text(
          text = "السؤال ${currentQuestionIndex + 1} من ${questions.size}",
          style = MaterialTheme.typography.bodySmall,
          color = TextMutedColor,
          fontSize = 11.5.sp
        )
      }
    }

    Spacer(modifier = Modifier.height(18.dp))

    Card(
      shape = RoundedCornerShape(18.dp),
      colors = CardDefaults.cardColors(containerColor = GlassCardBg),
      border = BorderStroke(1.dp, GlassBorderColor),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(18.dp)) {
        Text(
          text = q.question,
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = Color(0xFFF3D58C),
          fontSize = 16.sp,
          lineHeight = 24.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        q.options.forEachIndexed { index, option ->
          val isCurrentChoice = selectedAnswer == index
          val isCorrect = index == q.correctIndex
          val borderColor = when {
            isSubmitted && isCorrect -> Color(0xFF4CAF50)
            isSubmitted && isCurrentChoice && !isCorrect -> Color(0xFFE53935)
            isCurrentChoice -> GoldMain
            else -> GlassBorderSubtle
          }
          val bgColor = when {
            isSubmitted && isCorrect -> Color(0x334CAF50)
            isSubmitted && isCurrentChoice && !isCorrect -> Color(0x33E53935)
            isCurrentChoice -> Color(0x33DFB24C)
            else -> GlassDarkBg
          }

          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 5.dp)
              .clip(RoundedCornerShape(12.dp))
              .background(bgColor)
              .border(1.2.dp, borderColor, RoundedCornerShape(12.dp))
              .clickable(enabled = !isSubmitted) { selectedAnswer = index }
              .padding(horizontal = 14.dp, vertical = 12.dp)
          ) {
            Text(
              text = option,
              style = MaterialTheme.typography.bodyMedium,
              color = if (isCurrentChoice) GoldMain else Color(0xFFE0DBD1),
              fontWeight = if (isCurrentChoice) FontWeight.Bold else FontWeight.Normal,
              fontSize = 14.sp
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (!isSubmitted) {
          Button(
            onClick = {
              if (selectedAnswer != null) {
                isSubmitted = true
                if (selectedAnswer == q.correctIndex) {
                  score++
                }
              }
            },
            enabled = selectedAnswer != null,
            colors = ButtonDefaults.buttonColors(
              containerColor = GoldMain,
              contentColor = DarkPageBg
            ),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Text("تأكيد الإجابة", fontWeight = FontWeight.Bold)
          }
        } else {
          Button(
            onClick = {
              if (currentQuestionIndex < questions.size - 1) {
                currentQuestionIndex++
                selectedAnswer = null
                isSubmitted = false
              } else {
                currentQuestionIndex = 0
                selectedAnswer = null
                isSubmitted = false
                score = 0
              }
            },
            colors = ButtonDefaults.buttonColors(
              containerColor = GoldMain,
              contentColor = DarkPageBg
            ),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Text(
              if (currentQuestionIndex < questions.size - 1) "السؤال التالي" else "إعادة الاختبار",
              fontWeight = FontWeight.Bold
            )
          }
        }
      }
    }
  }
}

data class QuizQuestion(
  val question: String,
  val options: List<String>,
  val correctIndex: Int
)

@Composable
fun MoreScreen() {
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
          imageVector = Icons.Outlined.MoreHoriz,
          contentDescription = "More",
          tint = GoldMain,
          modifier = Modifier.size(22.dp)
        )
      }
      Spacer(modifier = Modifier.width(12.dp))
      Column {
        Text(
          text = "المزيد من المصادر والمراجع",
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.Bold,
          color = GoldMain,
          fontSize = 20.sp
        )
        Text(
          text = "المكتبة العلمية الرقمية لعلم المصريات",
          style = MaterialTheme.typography.bodySmall,
          color = TextMutedColor,
          fontSize = 11.5.sp
        )
      }
    }

    Spacer(modifier = Modifier.height(18.dp))

    val sources = listOf(
      "موسوعة مصر القديمة - د. سليم حسن (18 مجلداً)" to "التأريخ الشامل للحياة السياسية والدينية والاجتماعية",
      "قواعد اللغة المصرية القديمة (الهيروغليفية) - جاردنر" to "المرجع العالمي لفك وفهم النقوش الجدارية",
      "أطلس الآثار والمقابر الملكية بطيبة" to "التوثيق المعماري لوادي الملوك والكرنك والأقصر",
      "دليل المتاحف الكبرى العالمية للآثار المصرية" to "المتحف المصري الكبير، متحف اللوفر، المتحف البريطاني"
    )

    LazyColumn {
      items(sources) { (title, desc) ->
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = GlassCardBg),
          border = BorderStroke(1.dp, GlassBorderColor),
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Text(
              text = title,
              style = MaterialTheme.typography.titleSmall,
              fontWeight = FontWeight.Bold,
              color = GoldMain
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = desc,
              style = MaterialTheme.typography.bodySmall,
              color = Color(0xFFC7C1B5),
              fontSize = 12.sp
            )
          }
        }
      }
    }
  }
}

// -------------------------------------------------------------
// DETAILED ARCHAEOLOGICAL ARTICLE DATA MODEL
// -------------------------------------------------------------
data class ArticleDetailData(
  val cartoucheName: String,
  val location: String,
  val tableRows: List<Pair<String, String>>,
  val overviewParagraph: String,
  val hieroglyphicQuote: String,
  val quoteSource: String,
  val highlights: List<String>
)

fun getDetailedArticle(itemId: String): ArticleDetailData {
  return when (itemId) {
    "king_ramses2" -> ArticleDetailData(
      cartoucheName = "أوسر ماعت رع ستب إن رع",
      location = "طيبة / بر-رعمسيس / أبو سمبل",
      tableRows = listOf(
        "الاسم الكامل" to "رمسيس الثاني (رعمسيس)",
        "الأسرة الحاكمة" to "الأسرة التاسعة عشرة",
        "العصر التاريخي" to "المملكة الحديثة (العصر الذهبي)",
        "فترة الحكم" to "1279 – 1213 ق.م (66 عاماً)",
        "المقبرة الملكية" to "KV7 - وادي الملوك",
        "أهم المعارك" to "معركة قادش ضد الحيثيين (1274 ق.م)",
        "أعظم المنشآت" to "معبد أبو سمبل، الرمسيوم، صالة أعمدة الكرنك"
      ),
      overviewParagraph = "يُعد رمسيس الثاني، الملقب بـ «رمسيس الأكبر»، ثالث ملوك الأسرة التاسعة عشرة وأحد أعظم وأقوى حكام الإمبراطورية المصرية على مر العصور. قاد عدة حملات عسكرية لتأمين حدود الإمبراطورية في بلاد الشام والنوبة، وشهد عهده أول معاهدة سلام موثقة في تاريخ البشرية مع الملك الحيثي خاتوشيلي الثالث. تميز عهده بنهضة معمارية غير مسبوقة عمت أرجاء مصر من الشمال إلى الجنوب.",
      hieroglyphicQuote = "أنا سيد الأرضين، حامي مصر وقاهر الأعداء، بنى لي الخلود في صخر الجبال",
      quoteSource = "نقوش واجهة معبد أبو سمبل الكبير",
      highlights = listOf(
        "توقيع أول معاهدة سلام مكتوبة في التاريخ الإنساني مع الحيثيين عام 1259 ق.م.",
        "تشييد معبدي أبو سمبل بنحت الجبل الصخري بعبقرية فلكية تُحقق تعامد الشمس مرتين سنوياً.",
        "توسعة صالة الأعمدة الكبرى بمعبد الكرنك وبناء معبد الرمسيوم الجنائزي الفخم بالأقصر.",
        "تأسيس عاصمة جديدة في الدلتا «بر-رعمسيس» لتعزيز التجارة والسيطرة الاستراتيجية."
      )
    )
    "pyr_khufu" -> ArticleDetailData(
      cartoucheName = "خنوم خوفوي (خنوم يحميني)",
      location = "هضبة الجيزة - محافظة الجيزة",
      tableRows = listOf(
        "اسم الأثر" to "الهرم الأكبر (أفق خوفو - أخت خوفو)",
        "الملك الباني" to "خوفو (ثاني ملوك الأسرة الرابعة)",
        "العصر التاريخي" to "المملكة القديمة (عصر بناة الأهرام)",
        "تاريخ التشييد" to "نحو 2560 ق.م",
        "الارتفاع الأصلي" to "146.6 متراً (الحالي 138.8 م)",
        "عدد الأحجار المقدر" to "نحو 2.3 مليون كتلة حجرية جيرية وجرانيتية",
        "المهندس المعماري" to "الوزير حم إيونو (Hemiunu)"
      ),
      overviewParagraph = "الهرم الأكبر بالجيزة هو أضخم بناء حجري شيدته يد الإنسان في التاريخ القديم، والعجيبة الوحيدة الباقية من عجائب الدنيا السبع القديمة. بُني ليكون مقبرة للملك خوفو على مدار أكثر من عشرين عاماً. يُجسد الهرم ذروة التطور الهندسي والفلكي في مصر القديمة، حيث تتطابق أضلاعه الأربعة بدقة متناهية مع الاتجاهات الأصلية الأربعة للبوصلة.",
      hieroglyphicQuote = "أفق خوفو مشرق إلى الأبد، منارة الخلود التي لا تفنى",
      quoteSource = "نصوص الأهرام ونقوش مقابر كبار رجال الدولة بالجيزة",
      highlights = listOf(
        "تطابق الاتجاهات الأربعة للهرم بدقة فلكية تقل عن 0.05 درجة من الشمال الحقيقي.",
        "استخدام كتل جرانيتية ضخمة تزن الواحدة حتى 50 طناً في سقف حجرة الملك.",
        "وجود نظام تهوية وقنوات داخلية مصممة بمحاذاة مجموعات نجمية مقدسة كحزام أوريون وسيريوس.",
        "بناء مراكب الشمس الخشبية المكتشفة سليمة بجوار الهرم لدعم رحلة الملك السماوية."
      )
    )
    "art_mask" -> ArticleDetailData(
      cartoucheName = "نب خبرو رع (توت عنخ آمون)",
      location = "المتحف المصري الكبير - الجيزة",
      tableRows = listOf(
        "اسم القطعة" to "القناع الذهبي للملك توت عنخ آمون",
        "تاريخ الصنع" to "1323 ق.م (الأسرة الثامنة عشرة)",
        "المادة المصنوع منها" to "ذهب خالص عيار 22، لازورد، عقيق، فيروز، زجاج ملون",
        "الوزن الإجمالي" to "10.23 كيلوغراماً من الذهب الخالص",
        "الارتفاع" to "54 سنتيمتراً",
        "تاريخ الاكتشاف" to "4 نوفمبر 1922 م (هوارد كارتر)",
        "الموقع الأصلي" to "المقبرة KV62 - وادي الملوك"
      ),
      overviewParagraph = "القناع الجنائزي الذهبي للملك توت عنخ آمون هو الرمز البصري الأشهر عالمياً للحضارة المصرية القديمة وروعة الصياغة الفنية الفرعونية. عُثر عليه مستقراً فوق رأس وكتفي مومياء الملك الشاب داخل تابوته الذهبي الخالص. يُمثل القناع ملامح الملك الشاب مع لحية أوزيرية مستعارة، وعلى الجبهة شارات الملكية المقدسة: أنثى النسر نخبت وصل الكوبرا واجيت.",
      hieroglyphicQuote = "عينك اليمنى هي مركب المساء، وعينك اليسرى هي مركب الصباح، وجمالك يملأ القلوب نوراً",
      quoteSource = "نصوص كتاب الموتى المنقوشة على ظهر القناع الجنائزي",
      highlights = listOf(
        "صُنع من طبقتين من الذهب فائق النقاوة مرصعاً بالأحجار الكريمة الطبيعية النادرة.",
        "ظهره منقوش بالكامل بنصوص تعاويذ سحرية من الفصل 151 من كتاب الموتى لحماية حواس الملك.",
        "تم حفظ القناع كاملاً دون أدنى تلف لأكثر من 3300 عام داخل المقبرة الملكية المحكمة."
      )
    )
    else -> ArticleDetailData(
      cartoucheName = "سجل ملكي موثق",
      location = "جمهورية مصر العربية",
      tableRows = listOf(
        "التصنيف الأثري" to "أثر مصري خالد",
        "الحقبة التاريخية" to "مصر القديمة عبر العصور",
        "المصدر التوثيقي" to "هيئة الآثار والمتاحف المصرية",
        "الحالة الأثرية" to "مسجل وموثق رسمياً"
      ),
      overviewParagraph = "يُمثل هذا الأثر جزءاً أصيلاً من التراث الإنساني الفريد للحضارة المصرية القديمة، حيث وثق المصريون القدماء عقائدهم الدينية، وإنجازاتهم المعمارية، وعلومهم المتقدمة في الفلك والهندسة على جدران المعابد والبرديات والمقتنيات الأثرية الخالدة.",
      hieroglyphicQuote = "عاشت مصر خالدة ما دام النيل يجري وما دامت الشمس تشرق في سمائها",
      quoteSource = "نقوش البرديات المصرية القديمة",
      highlights = listOf(
        "توثيق علمي شامل لكافة المقتنيات والنقوش المرتبطة بهذا المعلم الأثري.",
        "حفظ دقيق لجميع الاكتشافات والأبحاث الأثرية المعتمدة من علماء المصريات."
      )
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
  val subtitleIcon: ImageVector = Icons.Outlined.CalendarToday
)

val allEgyptItems = listOf(
  // 1. الملوك (Kings)
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
