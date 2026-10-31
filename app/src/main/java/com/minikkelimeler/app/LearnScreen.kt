package com.minikkelimeler.app

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

/**
 * Flash cards: one big picture at a time. The word is read aloud when the card
 * appears and again whenever the child taps the picture.
 */
@Composable
fun LearnScreen(
    lang: Lang,
    category: Category,
    speaker: Speaker,
    onBack: () -> Unit,
    onPlay: () -> Unit
) {
    val words = category.words
    var index by remember { mutableIntStateOf(0) }
    val word = words[index]
    val scale = remember { Animatable(1f) }
    val scope = rememberCoroutineScope()

    fun sayAndBounce() {
        speaker.speak(word.label(lang), lang)
        scope.launch {
            scale.snapTo(0.85f)
            scale.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
        }
    }

    // Read the word every time a new card is shown.
    LaunchedEffect(index) {
        speaker.speak(word.label(lang), lang)
        scale.snapTo(0.6f)
        scale.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(category.color)
    ) {
        TopBar(onBack = onBack)

        // The picture card.
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(horizontal = 32.dp)
                .fillMaxWidth()
                .aspectRatio(1f)
                .graphicsLayer {
                    scaleX = scale.value
                    scaleY = scale.value
                }
                .shadow(8.dp, RoundedCornerShape(40.dp))
                .clip(RoundedCornerShape(40.dp))
                .background(Color.White)
                .clickable { sayAndBounce() }
        ) {
            Emoji(word.image, Modifier.fillMaxSize(0.75f))
        }

        Spacer(Modifier.height(28.dp))
        WordLabel(word, lang)
        Spacer(Modifier.weight(1f))

        // Progress dots.
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            words.indices.forEach { i ->
                Box(
                    Modifier
                        .size(if (i == index) 14.dp else 10.dp)
                        .clip(CircleShape)
                        .background(if (i == index) Color(0xFF5D4037) else Color.White)
                )
            }
        }
        Spacer(Modifier.height(20.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        ) {
            RoundButton(
                R.drawable.ui_left,
                onClick = { index = (index - 1 + words.size) % words.size },
                size = 80.dp
            )
            RoundButton(R.drawable.ui_speaker, onClick = { sayAndBounce() }, size = 80.dp)
            if (index == words.lastIndex) {
                // After the last card, offer the game.
                RoundButton(R.drawable.ui_play, onClick = onPlay, size = 80.dp)
            } else {
                RoundButton(R.drawable.ui_right, onClick = { index += 1 }, size = 80.dp)
            }
        }
    }
}

/**
 * The written word. In German the article is coloured (der = blue, die = red,
 * das = green) so children start to feel the gender of the noun.
 */
@Composable
fun WordLabel(word: Word, lang: Lang) {
    val text = buildAnnotatedString {
        if (lang == Lang.DE && word.article != null) {
            withStyle(SpanStyle(color = articleColor(word.article))) { append(word.article) }
            append(" ")
            append(word.de)
        } else {
            append(word.label(lang))
        }
    }
    Text(
        text,
        fontSize = 44.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF3E2723)
    )
}
