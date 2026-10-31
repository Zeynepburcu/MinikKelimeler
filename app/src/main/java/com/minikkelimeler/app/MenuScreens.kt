package com.minikkelimeler.app

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Background = Color(0xFFFFF8E1)
private val TextDark = Color(0xFF3E2723)

/** Start screen: the parent or child picks English or German. */
@Composable
fun HomeScreen(stars: Int, onLanguage: (Lang) -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(24.dp)
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            StarCount(stars)
        }
        Spacer(Modifier.height(16.dp))
        Emoji(R.drawable.ui_chick, Modifier.size(120.dp))
        Text(
            "Minik Kelimeler",
            fontSize = 34.sp,
            fontWeight = FontWeight.Bold,
            color = TextDark
        )
        Spacer(Modifier.height(40.dp))
        LanguageButton(Lang.EN, "English") { onLanguage(Lang.EN) }
        Spacer(Modifier.height(24.dp))
        LanguageButton(Lang.DE, "Deutsch") { onLanguage(Lang.DE) }
    }
}

@Composable
private fun LanguageButton(lang: Lang, title: String, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(28.dp))
            .clip(RoundedCornerShape(28.dp))
            .background(Color.White)
            .clickable(onClick = onClick)
            .padding(20.dp)
    ) {
        Flag(lang, Modifier.width(96.dp).height(64.dp))
        Spacer(Modifier.width(24.dp))
        Text(title, fontSize = 32.sp, fontWeight = FontWeight.Bold, color = TextDark)
    }
}

/** Grid of topics: animals, food, vehicles, outside, colours. */
@Composable
fun CategoriesScreen(
    lang: Lang,
    stars: Int,
    missingVoice: Boolean,
    onBack: () -> Unit,
    onCategory: (Category) -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        TopBar(onBack = onBack, stars = stars)
        if (missingVoice) {
            Text(
                Texts.missingVoice,
                fontSize = 16.sp,
                color = Color(0xFFB71C1C),
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFFFEBEE))
                    .padding(12.dp)
            )
        }
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(Words.categories) { category ->
                PictureCard(
                    image = category.cover,
                    caption = category.name(lang),
                    color = category.color,
                    onClick = { onCategory(category) },
                    modifier = Modifier.aspectRatio(0.9f)
                )
            }
        }
    }
}

/** Two big choices: look and listen (book) or play the game (controller). */
@Composable
fun ModeScreen(
    lang: Lang,
    category: Category,
    stars: Int,
    onBack: () -> Unit,
    onLearn: () -> Unit,
    onPlay: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(category.color)
    ) {
        TopBar(onBack = onBack, stars = stars)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Emoji(category.cover, Modifier.size(56.dp))
            Spacer(Modifier.width(12.dp))
            Text(category.name(lang), fontSize = 32.sp, fontWeight = FontWeight.Bold, color = TextDark)
        }
        Spacer(Modifier.height(16.dp))
        Column(
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(24.dp)
        ) {
            PictureCard(
                image = R.drawable.ui_learn,
                caption = Texts.learn,
                color = Color.White,
                onClick = onLearn,
                imageSize = 120.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
            PictureCard(
                image = R.drawable.ui_play,
                caption = Texts.play,
                color = Color.White,
                onClick = onPlay,
                imageSize = 120.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
        }
    }
}

/** Texts for parents, in Turkish. Change them here if needed. */
object Texts {
    const val learn = "Öğren"
    const val play = "Oyna"
    const val again = "Tekrar"
    const val missingVoice =
        "Bu dil için telefonda ses yok. Ayarlar → Metin okuma (Text-to-speech) bölümünden " +
            "Google ses motoruna Almanca / İngilizce sesi indirin."
}
