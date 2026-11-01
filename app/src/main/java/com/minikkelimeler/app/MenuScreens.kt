package com.minikkelimeler.app

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val Background = Color(0xFFFFF8E1)
private val TextDark = Color(0xFF3E2723)
private val TextSoft = Color(0xFF8D6E63)

/**
 * Start screen: one big "Start" button for the child.
 * The language switch sits small at the bottom and only opens on a long press,
 * so a child cannot change it by accident.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    lang: Lang,
    stars: Int,
    onStart: () -> Unit,
    onLanguage: (Lang) -> Unit
) {
    var showPicker by remember { mutableStateOf(false) }
    var showHint by remember { mutableStateOf(false) }

    LaunchedEffect(showHint) {
        if (showHint) {
            delay(3000)
            showHint = false
        }
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                StarCount(stars)
            }
            Spacer(Modifier.weight(1f))
            Emoji(R.drawable.ui_chick, Modifier.size(140.dp))
            Text("Minik Kelimeler", fontSize = 34.sp, fontWeight = FontWeight.Bold, color = TextDark)
            Spacer(Modifier.height(48.dp))

            // The big start button.
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .shadow(10.dp, RoundedCornerShape(48.dp))
                    .clip(RoundedCornerShape(48.dp))
                    .background(Color(0xFFFFB300))
                    .clickable(onClick = onStart)
                    .padding(horizontal = 48.dp, vertical = 20.dp)
            ) {
                Emoji(R.drawable.ui_start, Modifier.size(96.dp))
                Text(Texts.start, fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
            Spacer(Modifier.weight(1f))

            if (showHint) {
                Text(Texts.holdToChange, fontSize = 14.sp, color = TextSoft, textAlign = TextAlign.Center)
                Spacer(Modifier.height(8.dp))
            }

            // Small, quiet language switch (long press).
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .combinedClickable(
                        onClick = { showHint = true },
                        onLongClick = { showPicker = true }
                    )
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .alpha(0.7f)
            ) {
                Flag(lang, Modifier.width(30.dp).height(20.dp))
                Spacer(Modifier.width(8.dp))
                Text(lang.title, fontSize = 15.sp, color = TextSoft)
            }
        }

        if (showPicker) {
            LanguagePicker(
                current = lang,
                onPick = {
                    onLanguage(it)
                    showPicker = false
                },
                onClose = { showPicker = false }
            )
        }
    }
}

@Composable
private fun LanguagePicker(current: Lang, onPick: (Lang) -> Unit, onClose: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x99000000))
            .clickable(onClick = onClose)
            .padding(24.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(Color.White)
                .clickable { /* keep taps inside the card */ }
                .padding(20.dp)
        ) {
            Text(Texts.chooseLanguage, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextDark)
            Lang.entries.forEach { l ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (l == current) Color(0xFFFFECB3) else Color(0xFFF5F5F5))
                        .clickable { onPick(l) }
                        .padding(14.dp)
                ) {
                    Flag(l, Modifier.width(60.dp).height(40.dp))
                    Spacer(Modifier.width(16.dp))
                    Text(l.title, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = TextDark)
                }
            }
        }
    }
}

/** Grid of topics with a small "learned words" counter on each. */
@Composable
fun CategoriesScreen(
    lang: Lang,
    progress: Progress,
    missingVoice: Boolean,
    onBack: () -> Unit,
    onCategory: (Category) -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        TopBar(onBack = onBack, stars = progress.stars)
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
                val learned = progress.learnedWords(lang, category).size
                PictureCard(
                    image = category.cover,
                    caption = category.name(lang),
                    color = category.color,
                    onClick = { onCategory(category) },
                    badge = "$learned / ${category.words.size}",
                    modifier = Modifier.aspectRatio(0.85f)
                )
            }
        }
    }
}

/** Two big choices: learn with cards (book) or play the game (controller, locked at first). */
@Composable
fun ModeScreen(
    lang: Lang,
    category: Category,
    progress: Progress,
    onBack: () -> Unit,
    onLearn: () -> Unit,
    onPlay: () -> Unit
) {
    val canPlay = progress.canPlay(lang, category)
    val learned = progress.learnedWords(lang, category).size
    val shake = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(category.color)
    ) {
        TopBar(onBack = onBack, stars = progress.stars)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Emoji(category.cover, Modifier.size(56.dp))
            Spacer(Modifier.width(12.dp))
            Text(category.name(lang), fontSize = 32.sp, fontWeight = FontWeight.Bold, color = TextDark)
        }
        Spacer(Modifier.height(4.dp))
        Text(Texts.learnedCount(learned, category.words.size), fontSize = 15.sp, color = TextSoft)

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
            Box(
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .offset { IntOffset(shake.value.dp.roundToPx(), 0) }
            ) {
                PictureCard(
                    image = R.drawable.ui_play,
                    caption = Texts.play,
                    color = Color.White,
                    onClick = {
                        if (canPlay) {
                            onPlay()
                        } else {
                            scope.launch {
                                for (x in listOf(-16f, 16f, -10f, 10f, -4f, 0f)) {
                                    shake.animateTo(x, tween(50))
                                }
                            }
                        }
                    },
                    imageSize = 120.dp,
                    badge = if (canPlay) null else Texts.gameOpensSoon,
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(if (canPlay) 1f else 0.55f)
                )
                if (!canPlay) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(12.dp)
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                    ) {
                        Emoji(R.drawable.ui_lock, Modifier.size(40.dp))
                    }
                }
            }
        }
    }
}

/** Texts for parents, in Turkish. Change them here if needed. */
object Texts {
    const val start = "Başla"
    const val learn = "Öğren"
    const val play = "Oyna"
    const val again = "Tekrar"
    const val newWords = "Yeni kelimeler"
    const val oneMoreRound = "Bir tur daha"
    const val learnFirst = "Önce kartlara bakalım"
    const val gameOpensSoon = "Oyun, kartlara biraz daha bakınca açılır"
    const val chooseLanguage = "Öğrenilecek dil"
    const val holdToChange = "Dili değiştirmek için basılı tutun"
    const val missingVoice =
        "Bu dil için telefonda ses yok. Ayarlar → Metin okuma (Text-to-speech) bölümünden " +
            "Google ses motoruna Almanca / İngilizce sesi indirin."

    fun learnedCount(learned: Int, total: Int) = "Öğrenilen kelime: $learned / $total"
}
