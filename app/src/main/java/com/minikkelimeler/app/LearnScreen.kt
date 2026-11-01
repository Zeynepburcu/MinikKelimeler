package com.minikkelimeler.app

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * One learn round: a few big picture cards the child swipes through.
 * Each card's word is read aloud when it appears and whenever the child taps it.
 * After the last card comes a "well done" page: if the game is open for this
 * category it starts by itself, otherwise the child can do one more round.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LearnScreen(
    lang: Lang,
    category: Category,
    speaker: Speaker,
    progress: Progress,
    onBack: () -> Unit,
    onAgain: () -> Unit,
    onPlay: () -> Unit
) {
    val cards = remember { progress.nextRound(lang, category) }
    val pageCount = cards.size + 1 // the extra page is the "round finished" page
    val pager = rememberPagerState(pageCount = { pageCount })
    val scope = rememberCoroutineScope()
    // Cards already counted in this round (each counts once per round).
    val counted = remember { mutableSetOf<Word>() }

    LaunchedEffect(pager.currentPage) {
        val page = pager.currentPage
        if (page < cards.size) {
            val word = cards[page]
            if (counted.add(word)) progress.markSeen(lang, category, word)
            delay(250)
            speaker.speak(category.label(word, lang), lang)
        } else if (progress.canPlay(lang, category)) {
            speaker.speak(Words.roundDone(lang), lang)
            delay(2800)
            onPlay()
        } else {
            speaker.speak(Words.oneMore(lang), lang)
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(category.color)
    ) {
        TopBar(onBack = onBack, stars = progress.stars)

        HorizontalPager(
            state = pager,
            contentPadding = PaddingValues(horizontal = 36.dp),
            pageSpacing = 16.dp,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) { page ->
            if (page < cards.size) {
                CardPage(
                    category = category,
                    word = cards[page],
                    lang = lang,
                    onTap = { speaker.speak(category.label(cards[page], lang), lang) }
                )
            } else {
                RoundFinishedPage(
                    canPlay = progress.canPlay(lang, category),
                    learned = progress.learnedWords(lang, category).size,
                    total = category.words.size,
                    onAgain = onAgain,
                    onPlay = onPlay
                )
            }
        }

        // Progress dots.
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 16.dp)
        ) {
            for (i in 0 until pageCount) {
                val current = i == pager.currentPage
                Box(
                    Modifier
                        .size(if (current) 14.dp else 10.dp)
                        .clip(CircleShape)
                        .background(if (current) Color(0xFF5D4037) else Color.White)
                )
            }
        }

        // Buttons for children who tap instead of swiping.
        val onCard = pager.currentPage < cards.size
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .fillMaxWidth()
                .height(112.dp)
                .padding(bottom = 32.dp)
        ) {
            if (onCard) {
                RoundButton(
                    R.drawable.ui_left,
                    onClick = {
                        scope.launch { pager.animateScrollToPage((pager.currentPage - 1).coerceAtLeast(0)) }
                    },
                    size = 72.dp
                )
                RoundButton(
                    R.drawable.ui_speaker,
                    onClick = { speaker.speak(category.label(cards[pager.currentPage], lang), lang) },
                    size = 80.dp
                )
                RoundButton(
                    R.drawable.ui_right,
                    onClick = { scope.launch { pager.animateScrollToPage(pager.currentPage + 1) } },
                    size = 72.dp
                )
            }
        }
    }
}

@Composable
private fun CardPage(category: Category, word: Word, lang: Lang, onTap: () -> Unit) {
    val scale = remember { Animatable(1f) }
    val scope = rememberCoroutineScope()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .graphicsLayer {
                    scaleX = scale.value
                    scaleY = scale.value
                }
                .shadow(8.dp, RoundedCornerShape(40.dp))
                .clip(RoundedCornerShape(40.dp))
                .background(Color.White)
                .clickable {
                    onTap()
                    scope.launch {
                        scale.snapTo(0.85f)
                        scale.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                    }
                }
        ) {
            Emoji(word.image, Modifier.fillMaxSize(0.75f))
        }
        Spacer(Modifier.height(24.dp))
        WordLabel(category, word, lang)
    }
}

@Composable
private fun RoundFinishedPage(
    canPlay: Boolean,
    learned: Int,
    total: Int,
    onAgain: () -> Unit,
    onPlay: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(40.dp))
            .background(Color.White.copy(alpha = 0.6f))
            .padding(24.dp)
    ) {
        Row(verticalAlignment = Alignment.Bottom) {
            Emoji(R.drawable.ui_party, Modifier.size(72.dp))
            Emoji(R.drawable.ui_star, Modifier.size(110.dp))
            Emoji(R.drawable.ui_party, Modifier.size(72.dp).graphicsLayer { scaleX = -1f })
        }
        Spacer(Modifier.height(24.dp))
        if (canPlay) {
            RoundButton(R.drawable.ui_play, onClick = onPlay, size = 112.dp)
            Spacer(Modifier.height(8.dp))
            Text(Texts.play, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF3E2723))
        } else {
            RoundButton(R.drawable.ui_learn, onClick = onAgain, size = 112.dp)
            Spacer(Modifier.height(8.dp))
            Text(Texts.oneMoreRound, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF3E2723))
            Spacer(Modifier.height(8.dp))
            Text(
                Texts.gameOpensSoon,
                fontSize = 15.sp,
                color = Color(0xFF6D4C41),
                textAlign = TextAlign.Center
            )
        }
        Spacer(Modifier.height(16.dp))
        Text(Texts.learnedCount(learned, total), fontSize = 15.sp, color = Color(0xFF6D4C41))
    }
}

/**
 * The written word. In German the article is coloured (der = blue, die = red,
 * das = green) so children start to feel the gender of the noun.
 */
@Composable
fun WordLabel(category: Category, word: Word, lang: Lang) {
    val text = buildAnnotatedString {
        if (lang == Lang.DE && category.kind == Kind.NOUN) {
            val article = if (word.dePlural) "die" else word.article.orEmpty()
            withStyle(SpanStyle(color = word.labelArticleColor)) { append(article) }
            append(" ")
            append(word.de)
        } else {
            append(category.label(word, lang))
        }
    }
    Text(
        text,
        fontSize = 40.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF3E2723),
        textAlign = TextAlign.Center
    )
}
