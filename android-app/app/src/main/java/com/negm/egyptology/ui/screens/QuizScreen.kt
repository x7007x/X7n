package com.negm.egyptology.ui.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.negm.egyptology.data.Catalog
import com.negm.egyptology.ui.theme.DarkPageBg
import com.negm.egyptology.ui.theme.GlassBorderColor
import com.negm.egyptology.ui.theme.GlassBorderSubtle
import com.negm.egyptology.ui.theme.GlassCardBg
import com.negm.egyptology.ui.theme.GlassDarkBg
import com.negm.egyptology.ui.theme.GoldMain
import com.negm.egyptology.ui.theme.TextMutedColor

@Composable
fun QuizScreen(catalog: Catalog) {
  var selectedAnswer by remember { mutableStateOf<Int?>(null) }
  var currentQuestionIndex by remember { mutableIntStateOf(0) }
  var score by remember { mutableIntStateOf(0) }
  var isSubmitted by remember { mutableStateOf(false) }

  val questions = catalog.quiz
  if (questions.isEmpty()) return
  val q = questions[currentQuestionIndex]

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(DarkPageBg)
      .verticalScroll(rememberScrollState())
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
                if (selectedAnswer == q.correctIndex) score++
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
