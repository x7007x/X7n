package com.negm.egyptology.data

import androidx.compose.ui.graphics.vector.ImageVector

data class Category(
  val id: String,
  val title: String,
  val icon: ImageVector
)

data class CategoryMeta(
  val title: String,
  val subtitle: String,
  val icon: ImageVector
)

data class EgyptItem(
  val id: String,
  val category: String,
  val title: String,
  val tag1: String,
  val tag2: String,
  val subtitle: String,
  val imageRes: Int,
  val subtitleIcon: ImageVector
)

data class ArticleDetailData(
  val cartoucheName: String,
  val location: String,
  val tableRows: List<Pair<String, String>>,
  val overviewParagraph: String,
  val hieroglyphicQuote: String,
  val quoteSource: String,
  val highlights: List<String>
)

data class QuizQuestion(
  val question: String,
  val options: List<String>,
  val correctIndex: Int
)

data class MoreSource(
  val title: String,
  val description: String
)

data class Catalog(
  val categories: List<Category>,
  val categoryMeta: List<CategoryMeta>,
  val items: List<EgyptItem>,
  val articles: Map<String, ArticleDetailData>,
  val defaultArticle: ArticleDetailData,
  val quiz: List<QuizQuestion>,
  val moreSources: List<MoreSource>
)
