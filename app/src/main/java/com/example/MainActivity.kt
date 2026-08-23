package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
              CustomBottomNavigation(
                selectedTab = if (isViewingAll) "الرئيسية" else currentNavTab,
                onTabSelected = { tab ->
                  isViewingAll = false
                  currentNavTab = tab
                }
              )
            },
            containerColor = MaterialTheme.colorScheme.background
          ) { innerPadding ->
            Box(
              modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
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
    subtitle = "استكشف كنوز وتاريخ مصر القديمة",
    icon = Icons.Outlined.WorkspacePremium
  )

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
  ) {
    item {
      CompactHeaderSection()
    }
    item {
      SearchBarSection(
        query = searchQuery,
        onQueryChange = { searchQuery = it }
      )
    }
    item {
      CategoriesSection(
        selectedCategory = selectedCategory,
        onCategorySelect = { selectedCategory = it }
      )
    }
    item {
      SectionHeader(
        meta = categoryMeta,
        itemCount = filteredItems.size,
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
              tint = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "لم يتم العثور على نتائج",
              style = MaterialTheme.typography.bodyLarge,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }
    } else {
      items(filteredItems, key = { it.id }) { item ->
        EgyptCard(
          item = item,
          isBookmarked = bookmarkedIds.contains(item.id),
          onBookmarkToggle = { onBookmarkToggle(item.id) }
        )
      }
    }
    item {
      Spacer(modifier = Modifier.height(110.dp)) // padding for floating bottom nav
    }
  }
}

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
      .background(MaterialTheme.colorScheme.background)
  ) {
    // Top Bar
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 12.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(
          onClick = onBack,
          modifier = Modifier
            .size(40.dp)
            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), CircleShape)
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(22.dp)
          )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
          Text(
            text = "عرض كافة السجلات",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 18.sp
          )
          Text(
            text = "مجموع ${displayedItems.size} عنصراً مسجلاً",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary,
            fontSize = 11.sp
          )
        }
      }

      Icon(
        imageVector = Icons.Outlined.HistoryEdu,
        contentDescription = "Egypt Emblem",
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(28.dp)
      )
    }

    // Search bar
    SearchBarSection(
      query = searchQuery,
      onQueryChange = { searchQuery = it }
    )

    Spacer(modifier = Modifier.height(4.dp))

    // Filter Chips
    LazyRow(
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 8.dp),
      contentPadding = PaddingValues(horizontal = 20.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      items(allCategoriesFilter) { cat ->
        val isSelected = cat == selectedCategory
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(
              if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
              else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            )
            .border(
              1.dp,
              if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f),
              RoundedCornerShape(20.dp)
            )
            .clickable { selectedCategory = cat }
            .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
          Text(
            text = cat,
            style = MaterialTheme.typography.labelMedium,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
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
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(48.dp)
              )
              Spacer(modifier = Modifier.height(10.dp))
              Text(
                text = "لا توجد نتائج مطابقة لبحثك",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }
      } else {
        items(displayedItems, key = { it.id }) { item ->
          EgyptCard(
            item = item,
            isBookmarked = bookmarkedIds.contains(item.id),
            onBookmarkToggle = { onBookmarkToggle(item.id) }
          )
        }
      }
      item {
        Spacer(modifier = Modifier.height(110.dp))
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
      .background(MaterialTheme.colorScheme.background)
  ) {
    // Header
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp, vertical = 14.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(42.dp)
            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), CircleShape)
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Filled.Bookmark,
            contentDescription = "Favorites",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
          )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
          Text(
            text = "قائمة المفضلة",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 20.sp
          )
          Text(
            text = "العناصر المحفوظة للرجوع السريع",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp
          )
        }
      }

      Box(
        modifier = Modifier
          .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
          .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
          .padding(horizontal = 10.dp, vertical = 4.dp)
      ) {
        Text(
          text = "${bookmarkedItems.size} عنصر",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.primary,
          fontWeight = FontWeight.Bold,
          fontSize = 11.sp
        )
      }
    }

    if (bookmarkedItems.isEmpty()) {
      // Empty state
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
              .size(90.dp)
              .border(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), CircleShape)
              .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Outlined.BookmarkBorder,
              contentDescription = "Empty Favorites",
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(46.dp)
            )
          }
          Spacer(modifier = Modifier.height(18.dp))
          Text(
            text = "لا توجد عناصر في المفضلة",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 18.sp
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "اضغط على أيقونة الإشارة المرجعية بجانب أي بطاقة لحفظها هنا",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            fontSize = 13.sp
          )
          Spacer(modifier = Modifier.height(24.dp))
          Button(
            onClick = onExploreClick,
            colors = ButtonDefaults.buttonColors(
              containerColor = MaterialTheme.colorScheme.primary,
              contentColor = MaterialTheme.colorScheme.background
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
      // Search
      SearchBarSection(
        query = searchQuery,
        onQueryChange = { searchQuery = it }
      )

      // Category filters
      if (availableCategories.size > 2) {
        LazyRow(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
          contentPadding = PaddingValues(horizontal = 20.dp),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          items(availableCategories) { cat ->
            val isSelected = cat == selectedFilter
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(
                  if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                  else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                )
                .border(
                  1.dp,
                  if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f),
                  RoundedCornerShape(20.dp)
                )
                .clickable { selectedFilter = cat }
                .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
              Text(
                text = cat,
                style = MaterialTheme.typography.labelMedium,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
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
          EgyptCard(
            item = item,
            isBookmarked = true,
            onBookmarkToggle = { onBookmarkToggle(item.id) }
          )
        }
        item {
          Spacer(modifier = Modifier.height(110.dp))
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
      .background(MaterialTheme.colorScheme.background)
      .padding(20.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Icon(
      imageVector = Icons.Outlined.Map,
      contentDescription = "Map",
      tint = MaterialTheme.colorScheme.primary,
      modifier = Modifier.size(56.dp)
    )
    Spacer(modifier = Modifier.height(14.dp))
    Text(
      text = "خريطة المواقع الأثرية",
      style = MaterialTheme.typography.titleLarge,
      fontWeight = FontWeight.Bold,
      color = MaterialTheme.colorScheme.onBackground
    )
    Spacer(modifier = Modifier.height(6.dp))
    Text(
      text = "استكشف توزيع الآثار والمعابد والمتاحف على طول وادي النيل",
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      textAlign = TextAlign.Center
    )
  }
}

@Composable
fun QuizScreen() {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .padding(20.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Icon(
      imageVector = Icons.Outlined.Assignment,
      contentDescription = "Quiz",
      tint = MaterialTheme.colorScheme.primary,
      modifier = Modifier.size(56.dp)
    )
    Spacer(modifier = Modifier.height(14.dp))
    Text(
      text = "اختبارات علم المصريات",
      style = MaterialTheme.typography.titleLarge,
      fontWeight = FontWeight.Bold,
      color = MaterialTheme.colorScheme.onBackground
    )
    Spacer(modifier = Modifier.height(6.dp))
    Text(
      text = "اختبر معلوماتك عن الفراعنة والأسرات وتاريخ مصر القديم",
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      textAlign = TextAlign.Center
    )
  }
}

@Composable
fun MoreScreen() {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .padding(20.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Icon(
      imageVector = Icons.Outlined.MoreHoriz,
      contentDescription = "More",
      tint = MaterialTheme.colorScheme.primary,
      modifier = Modifier.size(56.dp)
    )
    Spacer(modifier = Modifier.height(14.dp))
    Text(
      text = "المزيد من المصادر",
      style = MaterialTheme.typography.titleLarge,
      fontWeight = FontWeight.Bold,
      color = MaterialTheme.colorScheme.onBackground
    )
    Spacer(modifier = Modifier.height(6.dp))
    Text(
      text = "المكتبة العلمية والمراجع المعتمدة لعلم الآثار المصرية",
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      textAlign = TextAlign.Center
    )
  }
}

@Composable
fun CompactHeaderSection() {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .height(210.dp) // Compact header height
  ) {
    // Background Image
    Image(
      painter = painterResource(id = R.drawable.hero_egypt_hieroglyphics_1787518543829),
      contentDescription = "Hero Image",
      contentScale = ContentScale.Crop,
      modifier = Modifier.fillMaxSize()
    )

    // Gradient Overlay
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(
          Brush.verticalGradient(
            colors = listOf(
              Color.Transparent,
              MaterialTheme.colorScheme.background.copy(alpha = 0.7f),
              MaterialTheme.colorScheme.background
            )
          )
        )
    )

    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
      Spacer(modifier = Modifier.height(28.dp))
      
      // Top Navigation Bar
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Menu Button (Right)
        IconButton(
          onClick = { /* Menu action */ },
          modifier = Modifier.size(38.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Menu,
            contentDescription = "Menu",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(28.dp)
          )
        }

        // Actions (Notifications & Profile) (Left)
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          // Bell with red notification badge
          Box(contentAlignment = Alignment.TopEnd) {
            IconButton(
              onClick = { /* Notifications */ },
              modifier = Modifier.size(38.dp)
            ) {
              Icon(
                imageVector = Icons.Outlined.NotificationsNone,
                contentDescription = "Notifications",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
              )
            }
            Box(
              modifier = Modifier
                .padding(top = 6.dp, end = 6.dp)
                .size(7.dp)
                .background(Color(0xFFE53935), CircleShape)
            )
          }

          // Profile Button
          IconButton(
            onClick = { /* Profile */ },
            modifier = Modifier
              .size(36.dp)
              .border(1.2.dp, MaterialTheme.colorScheme.primary, CircleShape)
          ) {
            Icon(
              imageVector = Icons.Outlined.PersonOutline,
              contentDescription = "Profile",
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(20.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      // Title & Emblem
      Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Icon(
          imageVector = Icons.Outlined.Visibility, // Eye of Horus
          contentDescription = "Eye of Horus",
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(34.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = "مصر القديمة",
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.primary,
          fontSize = 24.sp
        )
        Text(
          text = "موسوعة علمية لعلم المصريات",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.primary.copy(alpha = 0.85f),
          fontSize = 12.sp
        )
      }
    }
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
      .padding(horizontal = 20.dp, vertical = 8.dp)
  ) {
    TextField(
      value = query,
      onValueChange = onQueryChange,
      singleLine = true,
      placeholder = {
        Text(
          "ابحث عن ملك، متحف، مقبرة، أثر...",
          color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f),
          fontSize = 13.sp
        )
      },
      leadingIcon = {
        Icon(
          imageVector = Icons.Outlined.Search,
          contentDescription = "Search",
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(20.dp)
        )
      },
      trailingIcon = {
        if (query.isNotEmpty()) {
          IconButton(onClick = { onQueryChange("") }) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Clear search",
              tint = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.size(18.dp)
            )
          }
        }
      },
      colors = TextFieldDefaults.colors(
        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        cursorColor = MaterialTheme.colorScheme.primary
      ),
      shape = RoundedCornerShape(26.dp),
      modifier = Modifier
        .fillMaxWidth()
        .height(52.dp)
        .border(
          width = 1.dp,
          color = MaterialTheme.colorScheme.primary.copy(alpha = 0.85f),
          shape = RoundedCornerShape(26.dp)
        )
    )
  }
}

@Composable
fun CategoriesSection(
  selectedCategory: String,
  onCategorySelect: (String) -> Unit
) {
  LazyRow(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 18.dp),
    contentPadding = PaddingValues(horizontal = 20.dp),
    horizontalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    items(categoriesList) { category ->
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
fun CategoryItem(
  category: Category,
  isSelected: Boolean,
  onClick: () -> Unit
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center,
    modifier = Modifier
      .width(74.dp)
      .height(84.dp)
      .clip(RoundedCornerShape(16.dp))
      .background(
        if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
      )
      .border(
        width = if (isSelected) 1.5.dp else 1.dp,
        color = if (isSelected) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f),
        shape = RoundedCornerShape(16.dp)
      )
      .clickable(onClick = onClick)
      .padding(6.dp)
  ) {
    Icon(
      imageVector = category.icon,
      contentDescription = category.title,
      tint = if (isSelected) MaterialTheme.colorScheme.primary
      else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
      modifier = Modifier.size(28.dp)
    )
    Spacer(modifier = Modifier.height(6.dp))
    Text(
      text = category.title,
      style = MaterialTheme.typography.labelSmall,
      color = if (isSelected) MaterialTheme.colorScheme.primary
      else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
      textAlign = TextAlign.Center,
      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis,
      fontSize = 10.5.sp
    )
  }
}

@Composable
fun SectionHeader(
  meta: CategoryMeta,
  itemCount: Int,
  onViewAllClick: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 20.dp, vertical = 12.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Column {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = meta.icon,
          contentDescription = meta.title,
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = meta.title,
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onBackground,
          fontSize = 19.sp
        )
        Spacer(modifier = Modifier.width(6.dp))
        Box(
          modifier = Modifier
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), CircleShape)
            .padding(horizontal = 8.dp, vertical = 2.dp)
        ) {
          Text(
            text = "$itemCount",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp
          )
        }
      }
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = meta.subtitle,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontSize = 12.sp
      )
    }

    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier
        .clip(RoundedCornerShape(12.dp))
        .clickable(onClick = onViewAllClick)
        .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
      Text(
        text = "عرض الكل",
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.primary,
        fontSize = 12.sp
      )
      Icon(
        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
        contentDescription = "View All",
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(16.dp)
      )
    }
  }
}

@Composable
fun EgyptCard(
  item: EgyptItem,
  isBookmarked: Boolean,
  onBookmarkToggle: () -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 20.dp, vertical = 8.dp)
  ) {
    Card(
      shape = RoundedCornerShape(22.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)),
      border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.18f)),
      modifier = Modifier
        .fillMaxWidth()
        .height(150.dp)
        .clickable { /* Open details */ }
    ) {
      Row(modifier = Modifier.fillMaxSize()) {
        Box(
          modifier = Modifier
            .weight(0.38f)
            .fillMaxHeight()
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
                Brush.horizontalGradient(
                  colors = listOf(
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                    Color.Transparent
                  )
                )
              )
          )
          Box(
            modifier = Modifier
              .align(Alignment.TopStart)
              .padding(10.dp)
              .size(32.dp)
              .clip(CircleShape)
              .background(MaterialTheme.colorScheme.background.copy(alpha = 0.55f))
              .clickable(onClick = onBookmarkToggle),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
              contentDescription = "Bookmark",
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(18.dp)
            )
          }
        }

        Column(
          modifier = Modifier
            .weight(0.62f)
            .fillMaxHeight()
            .padding(horizontal = 16.dp, vertical = 16.dp),
          verticalArrangement = Arrangement.Center
        ) {
          Text(
            text = item.title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontSize = 18.sp
          )

          Spacer(modifier = Modifier.height(10.dp))

          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Badge(text = item.tag1)
            if (item.tag2.isNotBlank()) {
              Badge(text = item.tag2)
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = item.subtitleIcon,
              contentDescription = "Info",
              tint = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = item.subtitle,
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
              fontSize = 12.sp
            )
          }
        }
      }
    }

    Box(
      modifier = Modifier
        .align(Alignment.CenterEnd)
        .offset(x = 10.dp)
        .size(30.dp)
        .clip(CircleShape)
        .background(MaterialTheme.colorScheme.background)
        .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), CircleShape)
        .clickable { /* Open details */ },
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
        contentDescription = "Open",
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(18.dp)
      )
    }
  }
}

@Composable
fun Badge(text: String) {
  Box(
    modifier = Modifier
      .border(
        width = 1.dp,
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
        shape = RoundedCornerShape(12.dp)
      )
      .background(MaterialTheme.colorScheme.background.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
      .padding(horizontal = 8.dp, vertical = 3.dp)
  ) {
    Text(
      text = text,
      style = MaterialTheme.typography.labelSmall,
      color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.85f),
      fontSize = 9.5.sp,
      maxLines = 1
    )
  }
}

@Composable
fun CustomBottomNavigation(
  selectedTab: String,
  onTabSelected: (String) -> Unit
) {
  val navItems = listOf(
    NavItemData("خريطة", "خريطة", Icons.Filled.Map, Icons.Outlined.Map),
    NavItemData("المفضلة", "المفضلة", Icons.Filled.Bookmark, Icons.Outlined.BookmarkBorder),
    NavItemData("الرئيسية", "الرئيسية", Icons.Filled.Home, Icons.Outlined.Home),
    NavItemData("الاختبارات", "الاختبارات", Icons.Filled.Assignment, Icons.Outlined.Assignment),
    NavItemData("المزيد", "المزيد", Icons.Filled.MoreHoriz, Icons.Outlined.MoreHoriz)
  )

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 14.dp, vertical = 10.dp),
    contentAlignment = Alignment.BottomCenter
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .height(68.dp)
        .background(
          MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.96f),
          RoundedCornerShape(34.dp)
        )
        .border(
          1.dp,
          MaterialTheme.colorScheme.primary.copy(alpha = 0.28f),
          RoundedCornerShape(34.dp)
        )
        .padding(horizontal = 6.dp),
      horizontalArrangement = Arrangement.SpaceEvenly,
      verticalAlignment = Alignment.CenterVertically
    ) {
      navItems.forEach { item ->
        val isSelected = selectedTab == item.id
        val offsetY by animateDpAsState(
          targetValue = if (isSelected) (-8).dp else 0.dp,
          animationSpec = tween(durationMillis = 220),
          label = "nav_offset_${item.id}"
        )

        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center,
          modifier = Modifier
            .weight(1f)
            .offset(y = offsetY)
            .clip(RoundedCornerShape(24.dp))
            .clickable { onTabSelected(item.id) }
            .padding(vertical = 3.dp)
        ) {
          if (isSelected) {
            Box(
              modifier = Modifier
                .size(48.dp)
                .background(
                  Brush.linearGradient(
                    colors = listOf(
                      MaterialTheme.colorScheme.primary,
                      MaterialTheme.colorScheme.primary.copy(alpha = 0.85f)
                    )
                  ),
                  CircleShape
                )
                .border(2.dp, MaterialTheme.colorScheme.background, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = item.activeIcon,
                contentDescription = item.title,
                tint = MaterialTheme.colorScheme.background,
                modifier = Modifier.size(24.dp)
              )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = item.title,
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.primary,
              fontSize = 10.5.sp,
              fontWeight = FontWeight.Bold,
              maxLines = 1
            )
          } else {
            Box(
              modifier = Modifier.size(32.dp),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = item.inactiveIcon,
                contentDescription = item.title,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f),
                modifier = Modifier.size(22.dp)
              )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = item.title,
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f),
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
  val activeIcon: androidx.compose.ui.graphics.vector.ImageVector,
  val inactiveIcon: androidx.compose.ui.graphics.vector.ImageVector
)

// Data Models & Comprehensive Lists

data class Category(
  val title: String,
  val icon: androidx.compose.ui.graphics.vector.ImageVector
)

data class CategoryMeta(
  val title: String,
  val subtitle: String,
  val icon: androidx.compose.ui.graphics.vector.ImageVector
)

val categoryMetaMap = mapOf(
  "الملوك" to CategoryMeta("الملوك والفراعنة", "اكتشف ملوك مصر وحكامها عبر العصور", Icons.Outlined.WorkspacePremium),
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
  val subtitleIcon: androidx.compose.ui.graphics.vector.ImageVector = Icons.Outlined.DateRange
)

val allEgyptItems = listOf(
  // 1. الملوك
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
    subtitle = "يضم كنوز توت عنخ آمون كاملة",
    imageRes = R.drawable.museum_egypt_1787518889790,
    subtitleIcon = Icons.Outlined.LocationOn
  ),
  EgyptItem(
    id = "museum_nmec",
    category = "المتاحف",
    title = "متحف الحضارة (NMEC)",
    tag1 = "الفسطاط",
    tag2 = "متحف قومي شامل",
    subtitle = "قاعة المومياوات الملكية الفريدة",
    imageRes = R.drawable.museum_egypt_1787518889790,
    subtitleIcon = Icons.Outlined.LocationOn
  ),
  EgyptItem(
    id = "museum_tahrir",
    category = "المتاحف",
    title = "المتحف المصري بالتحرير",
    tag1 = "القاهرة",
    tag2 = "تأسس 1902 م",
    subtitle = "أعرق وأقدم صرح أثري بالشرق",
    imageRes = R.drawable.museum_egypt_1787518889790,
    subtitleIcon = Icons.Outlined.LocationOn
  ),
  EgyptItem(
    id = "museum_luxor",
    category = "المتاحف",
    title = "متحف الأقصر للفن القديم",
    tag1 = "الأقصر",
    tag2 = "البر الشرقي",
    subtitle = "روائع الفن وطيبة القديمة",
    imageRes = R.drawable.museum_egypt_1787518889790,
    subtitleIcon = Icons.Outlined.LocationOn
  ),

  // 3. المقابر
  EgyptItem(
    id = "tomb_tut",
    category = "المقابر",
    title = "مقبرة توت عنخ آمون (KV62)",
    tag1 = "وادي الملوك",
    tag2 = "الأسرة 18",
    subtitle = "اكتشفت كاملة عام 1922 م",
    imageRes = R.drawable.tomb_egypt_1787518916344,
    subtitleIcon = Icons.Outlined.MeetingRoom
  ),
  EgyptItem(
    id = "tomb_nefertari",
    category = "المقابر",
    title = "مقبرة نفرتاري (QV66)",
    tag1 = "وادي الملكات",
    tag2 = "الأسرة 19",
    subtitle = "أجمل جداريات ملونة بالتاريخ",
    imageRes = R.drawable.tomb_egypt_1787518916344,
    subtitleIcon = Icons.Outlined.MeetingRoom
  ),
  EgyptItem(
    id = "tomb_seti1",
    category = "المقابر",
    title = "مقبرة سيتي الأول (KV17)",
    tag1 = "وادي الملوك",
    tag2 = "الأسرة 19",
    subtitle = "أعمق وأطول مقابر وادي الملوك",
    imageRes = R.drawable.tomb_egypt_1787518916344,
    subtitleIcon = Icons.Outlined.MeetingRoom
  ),
  EgyptItem(
    id = "tomb_ramses6",
    category = "المقابر",
    title = "مقبرة رمسيس السادس (KV9)",
    tag1 = "وادي الملوك",
    tag2 = "الأسرة 20",
    subtitle = "سقف فلكي استثنائي لكتاب الليل",
    imageRes = R.drawable.tomb_egypt_1787518916344,
    subtitleIcon = Icons.Outlined.MeetingRoom
  ),

  // 4. المجموعات الهرمية
  EgyptItem(
    id = "pyr_khufu",
    category = "المجموعات الهرمية",
    title = "هرم خوفو الأكبر",
    tag1 = "الجيزة",
    tag2 = "عجائب الدنيا السبع",
    subtitle = "شيد حوالي 2560 ق.م",
    imageRes = R.drawable.pyramids_egypt_1787518903667,
    subtitleIcon = Icons.Outlined.ChangeHistory
  ),
  EgyptItem(
    id = "pyr_khafre",
    category = "المجموعات الهرمية",
    title = "هرم خفرع وأبو الهول",
    tag1 = "الجيزة",
    tag2 = "الأسرة 4",
    subtitle = "يحتفظ بكسوته الحجرية العلوية",
    imageRes = R.drawable.pyramids_egypt_1787518903667,
    subtitleIcon = Icons.Outlined.ChangeHistory
  ),
  EgyptItem(
    id = "pyr_djoser",
    category = "المجموعات الهرمية",
    title = "هرم زوسر المدرج",
    tag1 = "سقارة",
    tag2 = "الأسرة 3",
    subtitle = "أول بناء حجري متكامل بالعالم",
    imageRes = R.drawable.pyramids_egypt_1787518903667,
    subtitleIcon = Icons.Outlined.ChangeHistory
  ),
  EgyptItem(
    id = "pyr_sneferu",
    category = "المجموعات الهرمية",
    title = "هرم سنفرو المنحني",
    tag1 = "دهشور",
    tag2 = "الأسرة 4",
    subtitle = "أبرز نماذج تطور بناء الأهرام",
    imageRes = R.drawable.pyramids_egypt_1787518903667,
    subtitleIcon = Icons.Outlined.ChangeHistory
  ),

  // 5. الآثار
  EgyptItem(
    id = "art_mask",
    category = "الآثار",
    title = "قناع توت عنخ آمون الذهبي",
    tag1 = "ذهب وأحجار كريمة",
    tag2 = "الأسرة 18",
    subtitle = "أيقونة الآثار المصرية عالمياً",
    imageRes = R.drawable.hero_egypt_hieroglyphics_1787518543829,
    subtitleIcon = Icons.Outlined.AutoAwesome
  ),
  EgyptItem(
    id = "art_scribe",
    category = "الآثار",
    title = "تمثال الكاتب الجالس",
    tag1 = "حجر جيري ملون",
    tag2 = "الأسرة 5",
    subtitle = "قمة الإتقان والواقعية الفنية",
    imageRes = R.drawable.hero_pharaoh_1787516913716,
    subtitleIcon = Icons.Outlined.AutoAwesome
  ),
  EgyptItem(
    id = "art_nefertiti",
    category = "الآثار",
    title = "تمثال نفرتيتي النصفي",
    tag1 = "حجر جيري وجص",
    tag2 = "الأسرة 18",
    subtitle = "رمز الجمال الخالد في الفن القديم",
    imageRes = R.drawable.pharaoh_3_1787516935888,
    subtitleIcon = Icons.Outlined.AutoAwesome
  ),

  // 6. المواقع الأثرية
  EgyptItem(
    id = "site_karnak",
    category = "المواقع الأثرية",
    title = "مجمع معابد الكرنك",
    tag1 = "الأقصر",
    tag2 = "البر الشرقي",
    subtitle = "أكبر صرح ديني في العالم القديم",
    imageRes = R.drawable.museum_egypt_1787518889790,
    subtitleIcon = Icons.Outlined.LocationOn
  ),
  EgyptItem(
    id = "site_abusimbel",
    category = "المواقع الأثرية",
    title = "معبد أبو سمبل الكبير",
    tag1 = "أسوان",
    tag2 = "الأسرة 19",
    subtitle = "منحوت في قلب الجبل لرمسيس الثاني",
    imageRes = R.drawable.pyramids_egypt_1787518903667,
    subtitleIcon = Icons.Outlined.LocationOn
  ),
  EgyptItem(
    id = "site_hatshepsut",
    category = "المواقع الأثرية",
    title = "معبد حتشبسوت (الدير البحري)",
    tag1 = "الأقصر",
    tag2 = "البر الغربي",
    subtitle = "تحفة معمارية في حضن الجبل",
    imageRes = R.drawable.tomb_egypt_1787518916344,
    subtitleIcon = Icons.Outlined.LocationOn
  ),

  // 7. المزيد
  EgyptItem(
    id = "more_hieroglyphs",
    category = "المزيد",
    title = "الكتابة الهيروغليفية ونصوص الأهرام",
    tag1 = "لغة ونقوش",
    tag2 = "مصر القديمة",
    subtitle = "نظام كتابة وتدوين الحضارة المصرية",
    imageRes = R.drawable.hero_egypt_hieroglyphics_1787518543829,
    subtitleIcon = Icons.Outlined.AutoStories
  ),
  EgyptItem(
    id = "more_rosetta",
    category = "المزيد",
    title = "حجر رشيد ومفتاح الحضارة",
    tag1 = "العصر البطلمي",
    tag2 = "196 ق.م",
    subtitle = "حل لغز قراءة الهيروغليفية القديمة",
    imageRes = R.drawable.hero_pharaoh_1787516913716,
    subtitleIcon = Icons.Outlined.AutoStories
  )
)
