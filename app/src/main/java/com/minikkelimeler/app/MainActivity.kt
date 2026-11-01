package com.minikkelimeler.app

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

class MainActivity : ComponentActivity() {

    private lateinit var speaker: Speaker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        speaker = Speaker(this)
        val progress = Progress(getSharedPreferences("progress", Context.MODE_PRIVATE))

        setContent {
            MaterialTheme(
                colorScheme = lightColorScheme(
                    primary = Color(0xFFFF8F00),
                    background = Color(0xFFFFF8E1),
                    surface = Color.White
                )
            ) {
                App(speaker = speaker, progress = progress)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        speaker.stop()
    }

    override fun onDestroy() {
        speaker.shutdown()
        super.onDestroy()
    }
}

/** The screens of the app. */
sealed interface Screen {
    data object Home : Screen
    data object Categories : Screen
    data class Mode(val category: Category) : Screen
    /** [round] makes every learn round a fresh screen. */
    data class Learn(val category: Category, val round: Int) : Screen
    data class Game(val category: Category) : Screen
}

@Composable
fun App(speaker: Speaker, progress: Progress) {
    var screen by remember { mutableStateOf<Screen>(Screen.Home) }
    var learnRound by remember { mutableIntStateOf(0) }
    val lang = progress.lang

    fun learn(category: Category) {
        learnRound += 1
        screen = Screen.Learn(category, learnRound)
    }

    fun goBack() {
        speaker.stop()
        screen = when (val s = screen) {
            Screen.Home -> Screen.Home
            Screen.Categories -> Screen.Home
            is Screen.Mode -> Screen.Categories
            is Screen.Learn -> Screen.Mode(s.category)
            is Screen.Game -> Screen.Mode(s.category)
        }
    }

    BackHandler(enabled = screen != Screen.Home) { goBack() }

    when (val s = screen) {
        Screen.Home -> HomeScreen(
            lang = lang,
            stars = progress.stars,
            onStart = {
                speaker.speak(Words.hello(lang), lang)
                screen = Screen.Categories
            },
            onLanguage = { progress.lang = it }
        )

        Screen.Categories -> CategoriesScreen(
            lang = lang,
            progress = progress,
            missingVoice = speaker.missingVoice,
            onBack = { goBack() },
            onCategory = { category ->
                speaker.speak(category.name(lang), lang)
                screen = Screen.Mode(category)
            }
        )

        is Screen.Mode -> ModeScreen(
            lang = lang,
            category = s.category,
            progress = progress,
            onBack = { goBack() },
            onLearn = { learn(s.category) },
            onPlay = { screen = Screen.Game(s.category) }
        )

        is Screen.Learn -> key(s.round) {
            LearnScreen(
                lang = lang,
                category = s.category,
                speaker = speaker,
                progress = progress,
                onBack = { goBack() },
                onAgain = { learn(s.category) },
                onPlay = { screen = Screen.Game(s.category) }
            )
        }

        is Screen.Game -> GameScreen(
            lang = lang,
            category = s.category,
            speaker = speaker,
            progress = progress,
            onLearnMore = { learn(s.category) },
            onBack = { goBack() }
        )
    }
}
