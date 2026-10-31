package com.minikkelimeler.app

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val ROUNDS = 8

private data class Round(val target: Word, val options: List<Word>)

/** Builds the rounds: each target word appears once, with two other pictures. */
private fun makeRounds(words: List<Word>): List<Round> =
    words.shuffled().take(ROUNDS).map { target ->
        val others = (words - target).shuffled().take(2)
        Round(target, (others + target).shuffled())
    }

/**
 * "Where is the dog?" The app asks out loud and the child taps the right picture
 * out of three. A star is earned for every round.
 */
@Composable
fun GameScreen(
    lang: Lang,
    category: Category,
    speaker: Speaker,
    stars: Int,
    onStar: () -> Unit,
    onBack: () -> Unit
) {
    var rounds by remember { mutableStateOf(makeRounds(category.words)) }
    var roundIndex by remember { mutableIntStateOf(0) }

    // Ask the question at the start of every round; cheer at the end.
    LaunchedEffect(roundIndex, rounds) {
        if (roundIndex < rounds.size) {
            delay(400)
            speaker.speak(rounds[roundIndex].target.question(lang), lang)
        } else {
            speaker.speak(Words.finished(lang), lang)
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(category.color)
    ) {
        TopBar(onBack = onBack, stars = stars)

        if (roundIndex >= rounds.size) {
            FinishedView(
                earned = rounds.size,
                onAgain = {
                    rounds = makeRounds(category.words)
                    roundIndex = 0
                },
                onBack = onBack
            )
        } else {
            RoundView(
                lang = lang,
                speaker = speaker,
                round = rounds[roundIndex],
                roundIndex = roundIndex,
                roundCount = rounds.size,
                onStar = onStar,
                onNext = { roundIndex += 1 }
            )
        }
    }
}

@Composable
private fun ColumnScope.RoundView(
    lang: Lang,
    speaker: Speaker,
    round: Round,
    roundIndex: Int,
    roundCount: Int,
    onStar: () -> Unit,
    onNext: () -> Unit
) {
    // Reset per-round state whenever a new round starts.
    var wrong by remember(round) { mutableStateOf(setOf<Int>()) }
    var solved by remember(round) { mutableStateOf(false) }
    var shakingIndex by remember(round) { mutableIntStateOf(-1) }
    val shake = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    // Progress: one star per round.
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        for (i in 0 until roundCount) {
            val done = i < roundIndex || (i == roundIndex && solved)
            Emoji(R.drawable.ui_star, Modifier.size(30.dp).alpha(if (done) 1f else 0.25f))
        }
    }
    Spacer(Modifier.height(12.dp))

    // Tap the speaker to hear the question again.
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 24.dp)
    ) {
        RoundButton(
            R.drawable.ui_speaker,
            onClick = { speaker.speak(round.target.question(lang), lang) },
            size = 72.dp
        )
        Spacer(Modifier.width(16.dp))
        Text(
            round.target.question(lang),
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF3E2723)
        )
    }
    Spacer(Modifier.height(16.dp))

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
            .padding(start = 32.dp, end = 32.dp, bottom = 32.dp)
    ) {
        round.options.forEachIndexed { i, option ->
            val isTarget = option == round.target
            val isWrong = i in wrong
            val scale by animateFloatAsState(
                targetValue = if (solved && isTarget) 1.08f else 1f,
                label = "scale"
            )
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .offset {
                        IntOffset(
                            if (i == shakingIndex) shake.value.dp.roundToPx() else 0,
                            0
                        )
                    }
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    }
                    .alpha(if (isWrong) 0.3f else 1f)
                    .shadow(6.dp, RoundedCornerShape(32.dp))
                    .clip(RoundedCornerShape(32.dp))
                    .background(if (solved && isTarget) Color(0xFFC8E6C9) else Color.White)
                    .clickable {
                        if (solved || isWrong) return@clickable
                        if (isTarget) {
                            solved = true
                            onStar()
                            speaker.speak(Words.praise(lang).random(), lang)
                            scope.launch {
                                delay(1800)
                                onNext()
                            }
                        } else {
                            wrong = wrong + i
                            speaker.speak(
                                Words.tryAgain(lang) + " " + round.target.question(lang),
                                lang
                            )
                            shakingIndex = i
                            scope.launch {
                                for (x in listOf(-20f, 20f, -14f, 14f, -6f, 0f)) {
                                    shake.animateTo(x, tween(50))
                                }
                            }
                        }
                    }
            ) {
                Emoji(option.image, Modifier.fillMaxHeight(0.8f))
            }
        }
    }
}

@Composable
private fun FinishedView(earned: Int, onAgain: () -> Unit, onBack: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Row(verticalAlignment = Alignment.Bottom) {
            Emoji(R.drawable.ui_party, Modifier.size(80.dp))
            Emoji(R.drawable.ui_trophy, Modifier.size(160.dp))
            Emoji(R.drawable.ui_party, Modifier.size(80.dp).graphicsLayer { scaleX = -1f })
        }
        Spacer(Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("+$earned", fontSize = 48.sp, fontWeight = FontWeight.Bold, color = Color(0xFF3E2723))
            Spacer(Modifier.width(8.dp))
            Emoji(R.drawable.ui_star, Modifier.size(56.dp))
        }
        Spacer(Modifier.height(40.dp))
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(40.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                RoundButton(R.drawable.ui_play, onClick = onAgain, size = 96.dp)
                Spacer(Modifier.height(8.dp))
                Text(Texts.again, fontSize = 20.sp, color = Color(0xFF3E2723))
            }
            RoundButton(R.drawable.ui_back, onClick = onBack, size = 96.dp)
        }
    }
}
