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
        val prefs = getSharedPreferences("progress", Context.MODE_PRIVATE)

        setContent {
            MaterialTheme(
                colorScheme = lightColorScheme(
                    primary = Color(0xFFFF8F00),
                    background = Color(0xFFFFF8E1),
                    surface = Color.White
                )
            ) {
                App(
                    speaker = speaker,
                    initialStars = prefs.getInt("stars", 0),
                    saveStars = { prefs.edit().putInt("stars", it).apply() }
                )
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
    data class Learn(val category: Category) : Screen
    data class Game(val category: Category) : Screen
}

@Composable
fun App(speaker: Speaker, initialStars: Int, saveStars: (Int) -> Unit) {
    var screen by remember { mutableStateOf<Screen>(Screen.Home) }
    var lang by remember { mutableStateOf(Lang.EN) }
    var stars by remember { mutableIntStateOf(initialStars) }

    val addStar = {
        stars += 1
        saveStars(stars)
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
            stars = stars,
            onLanguage = { chosen ->
                lang = chosen
                speaker.speak(if (chosen == Lang.EN) "Hello!" else "Hallo!", chosen)
                screen = Screen.Categories
            }
        )

        Screen.Categories -> CategoriesScreen(
            lang = lang,
            stars = stars,
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
            stars = stars,
            onBack = { goBack() },
            onLearn = { screen = Screen.Learn(s.category) },
            onPlay = { screen = Screen.Game(s.category) }
        )

        is Screen.Learn -> LearnScreen(
            lang = lang,
            category = s.category,
            speaker = speaker,
            onBack = { goBack() },
            onPlay = { screen = Screen.Game(s.category) }
        )

        is Screen.Game -> GameScreen(
            lang = lang,
            category = s.category,
            speaker = speaker,
            stars = stars,
            onStar = addStar,
            onBack = { goBack() }
        )
    }
}
