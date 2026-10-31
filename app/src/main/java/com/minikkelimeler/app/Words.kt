package com.minikkelimeler.app

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import java.util.Locale

/** The language the child is learning. */
enum class Lang(val locale: Locale) {
    EN(Locale.UK),
    DE(Locale.GERMANY)
}

/**
 * One word with its picture.
 *
 * @param en      English word, e.g. "dog".
 * @param de      German word without article, e.g. "Hund".
 * @param article German article ("der", "die", "das"); null for colours.
 */
data class Word(
    @DrawableRes val image: Int,
    val en: String,
    val de: String,
    val article: String? = null
) {
    val isNoun: Boolean get() = article != null

    /** What is shown and spoken in learn mode. */
    fun label(lang: Lang): String = when (lang) {
        Lang.EN -> en
        Lang.DE -> if (article != null) "$article $de" else de
    }

    /** The question asked in the game. */
    fun question(lang: Lang): String = when (lang) {
        Lang.EN -> if (isNoun) "Where is the $en?" else "Where is $en?"
        Lang.DE -> if (article != null) {
            "Wo ist $article $de?"
        } else {
            "Wo ist ${de.replaceFirstChar { it.uppercase() }}?"
        }
    }
}

data class Category(
    val en: String,
    val de: String,
    @DrawableRes val cover: Int,
    val color: Color,
    val words: List<Word>
) {
    fun name(lang: Lang): String = if (lang == Lang.EN) en else de
}

object Words {

    val categories: List<Category> = listOf(
        Category(
            en = "Animals", de = "Tiere", cover = R.drawable.w_dog, color = Color(0xFFFFE0B2),
            words = listOf(
                Word(R.drawable.w_dog, "dog", "Hund", "der"),
                Word(R.drawable.w_cat, "cat", "Katze", "die"),
                Word(R.drawable.w_cow, "cow", "Kuh", "die"),
                Word(R.drawable.w_pig, "pig", "Schwein", "das"),
                Word(R.drawable.w_horse, "horse", "Pferd", "das"),
                Word(R.drawable.w_duck, "duck", "Ente", "die"),
                Word(R.drawable.w_fish, "fish", "Fisch", "der"),
                Word(R.drawable.w_lion, "lion", "Löwe", "der"),
                Word(R.drawable.w_elephant, "elephant", "Elefant", "der"),
                Word(R.drawable.w_rabbit, "rabbit", "Hase", "der")
            )
        ),
        Category(
            en = "Food", de = "Essen", cover = R.drawable.w_apple, color = Color(0xFFFFCDD2),
            words = listOf(
                Word(R.drawable.w_apple, "apple", "Apfel", "der"),
                Word(R.drawable.w_banana, "banana", "Banane", "die"),
                Word(R.drawable.w_strawberry, "strawberry", "Erdbeere", "die"),
                Word(R.drawable.w_pear, "pear", "Birne", "die"),
                Word(R.drawable.w_carrot, "carrot", "Karotte", "die"),
                Word(R.drawable.w_bread, "bread", "Brot", "das"),
                Word(R.drawable.w_milk, "milk", "Milch", "die"),
                Word(R.drawable.w_cheese, "cheese", "Käse", "der"),
                Word(R.drawable.w_egg, "egg", "Ei", "das"),
                Word(R.drawable.w_icecream, "ice cream", "Eis", "das")
            )
        ),
        Category(
            en = "Vehicles", de = "Fahrzeuge", cover = R.drawable.w_car, color = Color(0xFFBBDEFB),
            words = listOf(
                Word(R.drawable.w_car, "car", "Auto", "das"),
                Word(R.drawable.w_bus, "bus", "Bus", "der"),
                Word(R.drawable.w_train, "train", "Zug", "der"),
                Word(R.drawable.w_plane, "plane", "Flugzeug", "das"),
                Word(R.drawable.w_boat, "boat", "Boot", "das"),
                Word(R.drawable.w_bicycle, "bicycle", "Fahrrad", "das"),
                Word(R.drawable.w_fireengine, "fire engine", "Feuerwehrauto", "das"),
                Word(R.drawable.w_policecar, "police car", "Polizeiauto", "das"),
                Word(R.drawable.w_tractor, "tractor", "Traktor", "der"),
                Word(R.drawable.w_rocket, "rocket", "Rakete", "die")
            )
        ),
        Category(
            en = "Outside", de = "Draußen", cover = R.drawable.w_sun, color = Color(0xFFC8E6C9),
            words = listOf(
                Word(R.drawable.w_sun, "sun", "Sonne", "die"),
                Word(R.drawable.w_moon, "moon", "Mond", "der"),
                Word(R.drawable.w_star, "star", "Stern", "der"),
                Word(R.drawable.w_tree, "tree", "Baum", "der"),
                Word(R.drawable.w_flower, "flower", "Blume", "die"),
                Word(R.drawable.w_rainbow, "rainbow", "Regenbogen", "der"),
                Word(R.drawable.w_snowman, "snowman", "Schneemann", "der"),
                Word(R.drawable.w_house, "house", "Haus", "das"),
                Word(R.drawable.w_ball, "ball", "Ball", "der"),
                Word(R.drawable.w_cloud, "cloud", "Wolke", "die")
            )
        ),
        Category(
            en = "Colours", de = "Farben", cover = R.drawable.w_red, color = Color(0xFFE1BEE7),
            words = listOf(
                Word(R.drawable.w_red, "red", "rot"),
                Word(R.drawable.w_blue, "blue", "blau"),
                Word(R.drawable.w_green, "green", "grün"),
                Word(R.drawable.w_yellow, "yellow", "gelb"),
                Word(R.drawable.w_orange, "orange", "orange"),
                Word(R.drawable.w_purple, "purple", "lila"),
                Word(R.drawable.w_brown, "brown", "braun"),
                Word(R.drawable.w_black, "black", "schwarz"),
                Word(R.drawable.w_white, "white", "weiß"),
                Word(R.drawable.w_pink, "pink", "rosa")
            )
        )
    )

    /** Praise spoken after a correct answer. */
    fun praise(lang: Lang): List<String> = when (lang) {
        Lang.EN -> listOf("Great job!", "Super!", "Well done!", "Yes, that's right!")
        Lang.DE -> listOf("Super!", "Toll gemacht!", "Prima!", "Ja, richtig!")
    }

    fun tryAgain(lang: Lang): String = when (lang) {
        Lang.EN -> "Oops! Try again."
        Lang.DE -> "Hoppla! Versuch es noch einmal."
    }

    fun finished(lang: Lang): String = when (lang) {
        Lang.EN -> "Hooray! You did it!"
        Lang.DE -> "Hurra! Du hast es geschafft!"
    }
}

/** Colour for a German article: der = blue, die = red, das = green (a common teaching aid). */
fun articleColor(article: String?): Color = when (article) {
    "der" -> Color(0xFF1E88E5)
    "die" -> Color(0xFFE53935)
    "das" -> Color(0xFF43A047)
    else -> Color(0xFF37474F)
}
