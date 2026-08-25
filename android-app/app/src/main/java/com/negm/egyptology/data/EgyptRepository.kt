package com.negm.egyptology.data

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.ChangeHistory
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.MeetingRoom
import androidx.compose.material.icons.outlined.VerticalAlignTop
import androidx.compose.material.icons.outlined.ViewColumn
import androidx.compose.material.icons.outlined.WorkspacePremium
import com.negm.egyptology.R

/**
 * Static catalog loaded once from assets/data/catalog.json.
 */
object EgyptRepository {

  @Volatile
  private var cached: Catalog? = null

  fun catalog(context: Context): Catalog =
    cached ?: synchronized(this) {
      cached ?: parse(context.applicationContext).also { cached = it }
    }

  fun articleFor(context: Context, itemId: String): ArticleDetailData {
    val catalog = catalog(context)
    return catalog.articles[itemId] ?: catalog.defaultArticle
  }

  private fun parse(context: Context): Catalog {
    val json = context.assets.open("data/catalog.json")
      .bufferedReader(Charsets.UTF_8).use { it.readText() }
    val root = org.json.JSONObject(json)

    val categories = root.getJSONArray("categories").let { arr ->
      (0 until arr.length()).map { i ->
        val o = arr.getJSONObject(i)
        Category(
          id = o.getString("id"),
          title = o.getString("title"),
          icon = iconFor(o.getString("icon"))
        )
      }
    }

    val categoryMeta = root.getJSONArray("categoryMeta").let { arr ->
      (0 until arr.length()).map { i ->
        val o = arr.getJSONObject(i)
        CategoryMeta(
          title = o.getString("title"),
          subtitle = o.getString("subtitle"),
          icon = iconFor(o.getString("icon"))
        )
      }
    }

    val items = root.getJSONArray("items").let { arr ->
      (0 until arr.length()).map { i ->
        val o = arr.getJSONObject(i)
        EgyptItem(
          id = o.getString("id"),
          category = o.getString("category"),
          title = o.getString("title"),
          tag1 = o.optString("tag1", ""),
          tag2 = o.optString("tag2", ""),
          subtitle = o.optString("subtitle", ""),
          imageRes = imageFor(o.getString("image")),
          subtitleIcon = iconFor(o.optString("icon", "calendar"))
        )
      }
    }

    val articlesObj = root.getJSONObject("articles")
    val articles = buildMap {
      for (key in articlesObj.keys()) {
        put(key, readArticle(articlesObj.getJSONObject(key)))
      }
    }

    val defaultArticle = readArticle(root.getJSONObject("defaultArticle"))

    val quiz = root.getJSONArray("quiz").let { arr ->
      (0 until arr.length()).map { i ->
        val o = arr.getJSONObject(i)
        QuizQuestion(
          question = o.getString("question"),
          options = o.getJSONArray("options").let { opts ->
            (0 until opts.length()).map { opts.getString(it) }
          },
          correctIndex = o.getInt("correctIndex")
        )
      }
    }

    val moreSources = root.getJSONArray("moreSources").let { arr ->
      (0 until arr.length()).map { i ->
        val o = arr.getJSONObject(i)
        MoreSource(title = o.getString("title"), description = o.getString("description"))
      }
    }

    return Catalog(categories, categoryMeta, items, articles, defaultArticle, quiz, moreSources)
  }

  private fun readArticle(o: org.json.JSONObject): ArticleDetailData = ArticleDetailData(
    cartoucheName = o.getString("cartoucheName"),
    location = o.getString("location"),
    tableRows = o.getJSONArray("tableRows").let { rows ->
      (0 until rows.length()).map { r ->
        val pair = rows.getJSONArray(r)
        pair.getString(0) to pair.getString(1)
      }
    },
    overviewParagraph = o.getString("overviewParagraph"),
    hieroglyphicQuote = o.optString("hieroglyphicQuote", ""),
    quoteSource = o.optString("quoteSource", ""),
    highlights = o.getJSONArray("highlights").let { hs ->
      (0 until hs.length()).map { hs.getString(it) }
    }
  )

  private fun iconFor(name: String) = when (name) {
    "crown" -> Icons.Outlined.WorkspacePremium
    "museum" -> Icons.Outlined.AccountBalance
    "tomb" -> Icons.Outlined.MeetingRoom
    "pyramid" -> Icons.Outlined.ChangeHistory
    "artifact" -> Icons.Outlined.VerticalAlignTop
    "site" -> Icons.Outlined.ViewColumn
    "grid" -> Icons.Outlined.GridView
    "location" -> Icons.Outlined.LocationOn
    "premium" -> Icons.Outlined.WorkspacePremium
    "book" -> Icons.Outlined.AutoStories
    else -> Icons.Outlined.CalendarToday
  }

  private fun imageFor(name: String) = when (name) {
    "ramses" -> R.drawable.ramses_pharaoh_1787516924600
    "pharaoh" -> R.drawable.pharaoh_3_1787516935888
    "heroPharaoh" -> R.drawable.hero_pharaoh_1787516913716
    "hieroglyphs" -> R.drawable.hero_egypt_hieroglyphics_1787518543829
    "museum" -> R.drawable.museum_egypt_1787518889790
    "tomb" -> R.drawable.tomb_egypt_1787518916344
    "pyramids" -> R.drawable.pyramids_egypt_1787518903667
    else -> R.drawable.app_logo
  }
}
