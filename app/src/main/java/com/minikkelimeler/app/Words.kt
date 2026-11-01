package com.minikkelimeler.app

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import java.util.Locale

/** The language the child is learning. */
enum class Lang(val locale: Locale, val title: String) {
    EN(Locale.UK, "English"),
    DE(Locale.GERMANY, "Deutsch")
}

/** How the words of a category behave in sentences. */
enum class Kind { NOUN, COLOUR, NUMBER }

/**
 * One word with its picture.
 *
 * @param en       English word, e.g. "dog".
 * @param de       German word without article, e.g. "Hund".
 * @param article  German article ("der", "die", "das") for nouns.
 * @param enPlural true for English words that are plural ("glasses", "socks").
 * @param dePlural true for German words that are plural ("Socken").
 */
data class Word(
    @DrawableRes val image: Int,
    val en: String,
    val de: String,
    val article: String? = null,
    val enPlural: Boolean = false,
    val dePlural: Boolean = false
) {
    /** Stable key used to save learning progress. */
    val key: String get() = en

    // ---- German forms ----
    // Nominative: der Hund   / die Katze / das Pferd / die Socken
    val deNom: String get() = if (dePlural) "die $de" else "$article $de"

    // Accusative: den Hund   / die Katze / das Pferd / die Socken
    val deAcc: String get() = when {
        dePlural -> "die $de"
        article == "der" -> "den $de"
        else -> "$article $de"
    }

    // Dative: dem Hund       / der Katze / dem Pferd / den Socken
    val deDat: String get() = when {
        dePlural -> "den $de"
        article == "die" -> "der $de"
        else -> "dem $de"
    }

    // Possessive: dein Mund  / deine Nase / dein Auge
    val deYour: String get() = if (dePlural || article == "die") "deine $de" else "dein $de"

    val deVerbIs: String get() = if (dePlural) "sind" else "ist"
    val enVerbIs: String get() = if (enPlural) "are" else "is"

    /** Article colour for the German label. Plurals stay neutral. */
    val labelArticleColor: Color get() = articleColor(if (dePlural) null else article)
}

data class Category(
    val id: String,
    val en: String,
    val de: String,
    @DrawableRes val cover: Int,
    val color: Color,
    val kind: Kind = Kind.NOUN,
    /** A little sentence for the game, e.g. "Let's take the helicopter!" */
    val sentence: (Word, Lang) -> String,
    val words: List<Word>
) {
    fun name(lang: Lang): String = if (lang == Lang.EN) en else de

    /** What is shown and spoken on a learn card. */
    fun label(w: Word, lang: Lang): String = when {
        lang == Lang.EN -> w.en
        kind == Kind.NOUN -> w.deNom
        else -> w.de
    }

    /** The simple game question: "Where is the dog?" */
    fun ask(w: Word, lang: Lang): String = when (kind) {
        Kind.NOUN -> when (lang) {
            Lang.EN -> "Where ${w.enVerbIs} the ${w.en}?"
            Lang.DE -> "Wo ${w.deVerbIs} ${w.deNom}?"
        }
        Kind.COLOUR -> when (lang) {
            Lang.EN -> "Where is ${w.en}?"
            Lang.DE -> "Wo ist ${w.de.cap()}?"
        }
        Kind.NUMBER -> when (lang) {
            Lang.EN -> "Where is number ${w.en}?"
            Lang.DE -> "Wo ist die ${w.de.cap()}?"
        }
    }
}

private fun String.cap(): String = replaceFirstChar { it.uppercase() }

/** Picks the English or German version. */
private fun t(lang: Lang, en: String, de: String): String = if (lang == Lang.EN) en else de

object Words {

    val categories: List<Category> = listOf(
        Category(
            id = "animals", en = "Animals", de = "Tiere",
            cover = R.drawable.w_dog, color = Color(0xFFFFE0B2),
            sentence = { w, l -> t(l, "Let's feed the ${w.en}!", "Komm, wir füttern ${w.deAcc}!") },
            words = listOf(
                Word(R.drawable.w_dog, "dog", "Hund", "der"),
                Word(R.drawable.w_cat, "cat", "Katze", "die"),
                Word(R.drawable.w_cow, "cow", "Kuh", "die"),
                Word(R.drawable.w_pig, "pig", "Schwein", "das"),
                Word(R.drawable.w_horse, "horse", "Pferd", "das"),
                Word(R.drawable.w_duck, "duck", "Ente", "die"),
                Word(R.drawable.w_fish, "fish", "Fisch", "der"),
                Word(R.drawable.w_sheep, "sheep", "Schaf", "das"),
                Word(R.drawable.w_rabbit, "rabbit", "Hase", "der"),
                Word(R.drawable.w_frog, "frog", "Frosch", "der"),
                Word(R.drawable.w_lion, "lion", "Löwe", "der"),
                Word(R.drawable.w_elephant, "elephant", "Elefant", "der"),
                Word(R.drawable.w_bear, "bear", "Bär", "der"),
                Word(R.drawable.w_monkey, "monkey", "Affe", "der"),
                Word(R.drawable.w_giraffe, "giraffe", "Giraffe", "die"),
                Word(R.drawable.w_penguin, "penguin", "Pinguin", "der")
            )
        ),
        Category(
            id = "food", en = "Food", de = "Essen",
            cover = R.drawable.w_apple, color = Color(0xFFFFCDD2),
            sentence = { w, l -> t(l, "Can I have the ${w.en}, please?", "Kann ich bitte ${w.deAcc} haben?") },
            words = listOf(
                Word(R.drawable.w_apple, "apple", "Apfel", "der"),
                Word(R.drawable.w_banana, "banana", "Banane", "die"),
                Word(R.drawable.w_strawberry, "strawberry", "Erdbeere", "die"),
                Word(R.drawable.w_pear, "pear", "Birne", "die"),
                Word(R.drawable.w_orangefruit, "orange", "Orange", "die"),
                Word(R.drawable.w_carrot, "carrot", "Karotte", "die"),
                Word(R.drawable.w_tomato, "tomato", "Tomate", "die"),
                Word(R.drawable.w_bread, "bread", "Brot", "das"),
                Word(R.drawable.w_milk, "milk", "Milch", "die"),
                Word(R.drawable.w_cheese, "cheese", "Käse", "der"),
                Word(R.drawable.w_egg, "egg", "Ei", "das"),
                Word(R.drawable.w_watermelon, "watermelon", "Wassermelone", "die"),
                Word(R.drawable.w_pizza, "pizza", "Pizza", "die"),
                Word(R.drawable.w_cake, "cake", "Kuchen", "der"),
                Word(R.drawable.w_cookie, "cookie", "Keks", "der"),
                Word(R.drawable.w_icecream, "ice cream", "Eis", "das")
            )
        ),
        Category(
            id = "vehicles", en = "Vehicles", de = "Fahrzeuge",
            cover = R.drawable.w_car, color = Color(0xFFBBDEFB),
            sentence = { w, l -> t(l, "Let's take the ${w.en}!", "Komm, wir nehmen ${w.deAcc}!") },
            words = listOf(
                Word(R.drawable.w_car, "car", "Auto", "das"),
                Word(R.drawable.w_bus, "bus", "Bus", "der"),
                Word(R.drawable.w_train, "train", "Zug", "der"),
                Word(R.drawable.w_plane, "plane", "Flugzeug", "das"),
                Word(R.drawable.w_boat, "boat", "Boot", "das"),
                Word(R.drawable.w_bicycle, "bicycle", "Fahrrad", "das"),
                Word(R.drawable.w_taxi, "taxi", "Taxi", "das"),
                Word(R.drawable.w_truck, "truck", "Lastwagen", "der"),
                Word(R.drawable.w_helicopter, "helicopter", "Hubschrauber", "der"),
                Word(R.drawable.w_ship, "ship", "Schiff", "das"),
                Word(R.drawable.w_motorbike, "motorbike", "Motorrad", "das"),
                Word(R.drawable.w_tractor, "tractor", "Traktor", "der"),
                Word(R.drawable.w_fireengine, "fire engine", "Feuerwehrauto", "das"),
                Word(R.drawable.w_policecar, "police car", "Polizeiauto", "das"),
                Word(R.drawable.w_ambulance, "ambulance", "Krankenwagen", "der"),
                Word(R.drawable.w_rocket, "rocket", "Rakete", "die")
            )
        ),
        Category(
            id = "body", en = "My body", de = "Mein Körper",
            cover = R.drawable.w_hand, color = Color(0xFFFFF59D),
            sentence = { w, l -> t(l, "Where is your ${w.en}?", "Wo ist ${w.deYour}?") },
            words = listOf(
                Word(R.drawable.w_eye, "eye", "Auge", "das"),
                Word(R.drawable.w_ear, "ear", "Ohr", "das"),
                Word(R.drawable.w_nose, "nose", "Nase", "die"),
                Word(R.drawable.w_mouth, "mouth", "Mund", "der"),
                Word(R.drawable.w_tooth, "tooth", "Zahn", "der"),
                Word(R.drawable.w_tongue, "tongue", "Zunge", "die"),
                Word(R.drawable.w_hand, "hand", "Hand", "die"),
                Word(R.drawable.w_arm, "arm", "Arm", "der"),
                Word(R.drawable.w_leg, "leg", "Bein", "das"),
                Word(R.drawable.w_foot, "foot", "Fuß", "der")
            )
        ),
        Category(
            id = "clothes", en = "Clothes", de = "Kleidung",
            cover = R.drawable.w_tshirt, color = Color(0xFFB2EBF2),
            sentence = { w, l -> t(l, "I need the ${w.en}!", "Ich brauche ${w.deAcc}!") },
            words = listOf(
                Word(R.drawable.w_tshirt, "T-shirt", "T-Shirt", "das"),
                Word(R.drawable.w_dress, "dress", "Kleid", "das"),
                Word(R.drawable.w_jeans, "jeans", "Jeans", "die", enPlural = true),
                Word(R.drawable.w_coat, "coat", "Mantel", "der"),
                Word(R.drawable.w_scarf, "scarf", "Schal", "der"),
                Word(R.drawable.w_cap, "cap", "Mütze", "die"),
                Word(R.drawable.w_hat, "hat", "Hut", "der"),
                Word(R.drawable.w_shoe, "shoe", "Schuh", "der"),
                Word(R.drawable.w_boot, "boot", "Stiefel", "der"),
                Word(R.drawable.w_socks, "socks", "Socken", "die", enPlural = true, dePlural = true),
                Word(R.drawable.w_gloves, "gloves", "Handschuhe", "die", enPlural = true, dePlural = true),
                Word(R.drawable.w_glasses, "glasses", "Brille", "die", enPlural = true)
            )
        ),
        Category(
            id = "home", en = "At home", de = "Zu Hause",
            cover = R.drawable.w_sofa, color = Color(0xFFD7CCC8),
            sentence = { w, l -> t(l, "Show me the ${w.en}!", "Zeig mir ${w.deAcc}!") },
            words = listOf(
                Word(R.drawable.w_house, "house", "Haus", "das"),
                Word(R.drawable.w_door, "door", "Tür", "die"),
                Word(R.drawable.w_window, "window", "Fenster", "das"),
                Word(R.drawable.w_bed, "bed", "Bett", "das"),
                Word(R.drawable.w_chair, "chair", "Stuhl", "der"),
                Word(R.drawable.w_sofa, "sofa", "Sofa", "das"),
                Word(R.drawable.w_bathtub, "bathtub", "Badewanne", "die"),
                Word(R.drawable.w_toilet, "toilet", "Toilette", "die"),
                Word(R.drawable.w_toothbrush, "toothbrush", "Zahnbürste", "die"),
                Word(R.drawable.w_spoon, "spoon", "Löffel", "der"),
                Word(R.drawable.w_book, "book", "Buch", "das"),
                Word(R.drawable.w_clock, "clock", "Uhr", "die"),
                Word(R.drawable.w_key, "key", "Schlüssel", "der")
            )
        ),
        Category(
            id = "toys", en = "Toys", de = "Spielzeug",
            cover = R.drawable.w_teddy, color = Color(0xFFF8BBD0),
            sentence = { w, l -> t(l, "Let's play with the ${w.en}!", "Komm, wir spielen mit ${w.deDat}!") },
            words = listOf(
                Word(R.drawable.w_teddy, "teddy bear", "Teddybär", "der"),
                Word(R.drawable.w_ball, "ball", "Ball", "der"),
                Word(R.drawable.w_balloon, "balloon", "Luftballon", "der"),
                Word(R.drawable.w_kite, "kite", "Drachen", "der"),
                Word(R.drawable.w_robot, "robot", "Roboter", "der"),
                Word(R.drawable.w_puzzle, "puzzle", "Puzzle", "das"),
                Word(R.drawable.w_yoyo, "yo-yo", "Jo-Jo", "das"),
                Word(R.drawable.w_drum, "drum", "Trommel", "die"),
                Word(R.drawable.w_guitar, "guitar", "Gitarre", "die"),
                Word(R.drawable.w_trumpet, "trumpet", "Trompete", "die"),
                Word(R.drawable.w_piano, "piano", "Klavier", "das")
            )
        ),
        Category(
            id = "outside", en = "Outside", de = "Draußen",
            cover = R.drawable.w_sun, color = Color(0xFFC8E6C9),
            sentence = { w, l -> t(l, "Look at the ${w.en}!", "Schau mal, ${w.deNom}!") },
            words = listOf(
                Word(R.drawable.w_sun, "sun", "Sonne", "die"),
                Word(R.drawable.w_moon, "moon", "Mond", "der"),
                Word(R.drawable.w_star, "star", "Stern", "der"),
                Word(R.drawable.w_cloud, "cloud", "Wolke", "die"),
                Word(R.drawable.w_rainbow, "rainbow", "Regenbogen", "der"),
                Word(R.drawable.w_tree, "tree", "Baum", "der"),
                Word(R.drawable.w_flower, "flower", "Blume", "die"),
                Word(R.drawable.w_leaf, "leaf", "Blatt", "das"),
                Word(R.drawable.w_mountain, "mountain", "Berg", "der"),
                Word(R.drawable.w_umbrella, "umbrella", "Regenschirm", "der"),
                Word(R.drawable.w_snowflake, "snowflake", "Schneeflocke", "die"),
                Word(R.drawable.w_snowman, "snowman", "Schneemann", "der")
            )
        ),
        Category(
            id = "colours", en = "Colours", de = "Farben",
            cover = R.drawable.w_red, color = Color(0xFFE1BEE7), kind = Kind.COLOUR,
            sentence = { w, l -> t(l, "Which heart is ${w.en}?", "Welches Herz ist ${w.de}?") },
            words = listOf(
                Word(R.drawable.w_red, "red", "rot"),
                Word(R.drawable.w_blue, "blue", "blau"),
                Word(R.drawable.w_green, "green", "grün"),
                Word(R.drawable.w_yellow, "yellow", "gelb"),
                Word(R.drawable.w_orange, "orange", "orange"),
                Word(R.drawable.w_purple, "purple", "lila"),
                Word(R.drawable.w_pink, "pink", "rosa"),
                Word(R.drawable.w_brown, "brown", "braun"),
                Word(R.drawable.w_black, "black", "schwarz"),
                Word(R.drawable.w_white, "white", "weiß")
            )
        ),
        Category(
            id = "numbers", en = "Numbers", de = "Zahlen",
            cover = R.drawable.w_n3, color = Color(0xFFC5CAE9), kind = Kind.NUMBER,
            sentence = { w, l -> t(l, "Show me number ${w.en}!", "Zeig mir die ${w.de.cap()}!") },
            words = listOf(
                Word(R.drawable.w_n1, "one", "eins"),
                Word(R.drawable.w_n2, "two", "zwei"),
                Word(R.drawable.w_n3, "three", "drei"),
                Word(R.drawable.w_n4, "four", "vier"),
                Word(R.drawable.w_n5, "five", "fünf"),
                Word(R.drawable.w_n6, "six", "sechs"),
                Word(R.drawable.w_n7, "seven", "sieben"),
                Word(R.drawable.w_n8, "eight", "acht"),
                Word(R.drawable.w_n9, "nine", "neun"),
                Word(R.drawable.w_n10, "ten", "zehn")
            )
        )
    )

    /** Praise spoken after a correct answer. */
    fun praise(lang: Lang): List<String> = when (lang) {
        Lang.EN -> listOf("Great job!", "Super!", "Well done!", "Yes, that's right!")
        Lang.DE -> listOf("Super!", "Toll gemacht!", "Prima!", "Ja, richtig!")
    }

    fun tryAgain(lang: Lang): String = t(lang, "Oops! Try again.", "Hoppla! Versuch es noch einmal.")

    fun finished(lang: Lang): String = t(lang, "Hooray! You did it!", "Hurra! Du hast es geschafft!")

    fun roundDone(lang: Lang): String = t(lang, "Well done! Let's play!", "Toll! Jetzt spielen wir!")

    fun oneMore(lang: Lang): String = t(lang, "Great! One more time?", "Toll! Noch einmal?")

    fun hello(lang: Lang): String = t(lang, "Hello! Let's go!", "Hallo! Los geht's!")
}

/** Colour for a German article: der = blue, die = red, das = green (a common teaching aid). */
fun articleColor(article: String?): Color = when (article) {
    "der" -> Color(0xFF1E88E5)
    "die" -> Color(0xFFE53935)
    "das" -> Color(0xFF43A047)
    else -> Color(0xFF37474F)
}
