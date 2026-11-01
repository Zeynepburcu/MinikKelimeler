package com.minikkelimeler.app

import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue

/**
 * Remembers which cards a child has seen, separately for each language.
 *
 * - A learn round ("tur") shows [ROUND_SIZE] cards.
 * - Each round a card appears in counts once.
 * - A word is "learned" after it has appeared in [LEARNED_AFTER] rounds.
 * - The game of a category opens when at least [MIN_TO_PLAY] words are learned,
 *   and it only asks about learned words.
 *
 * With these numbers the game opens after the second learn round.
 */
class Progress(private val prefs: SharedPreferences) {

    companion object {
        const val ROUND_SIZE = 5
        const val LEARNED_AFTER = 2
        const val MIN_TO_PLAY = 3
    }

    /** Bumped on every change so the screens refresh. */
    private var version by mutableIntStateOf(0)

    var stars: Int
        get() {
            version
            return prefs.getInt("stars", 0)
        }
        private set(value) {
            prefs.edit().putInt("stars", value).apply()
            version++
        }

    fun addStar() {
        stars += 1
    }

    var lang: Lang
        get() {
            version
            val saved = prefs.getString("lang", null)
            return Lang.entries.firstOrNull { it.name == saved } ?: Lang.EN
        }
        set(value) {
            prefs.edit().putString("lang", value.name).apply()
            version++
        }

    private fun key(lang: Lang, category: Category, word: Word) =
        "seen:${lang.name}:${category.id}:${word.key}"

    fun timesSeen(lang: Lang, category: Category, word: Word): Int {
        version
        return prefs.getInt(key(lang, category, word), 0)
    }

    fun markSeen(lang: Lang, category: Category, word: Word) {
        val k = key(lang, category, word)
        prefs.edit().putInt(k, prefs.getInt(k, 0) + 1).apply()
        version++
    }

    fun isLearned(lang: Lang, category: Category, word: Word): Boolean =
        timesSeen(lang, category, word) >= LEARNED_AFTER

    fun learnedWords(lang: Lang, category: Category): List<Word> =
        category.words.filter { isLearned(lang, category, it) }

    fun canPlay(lang: Lang, category: Category): Boolean =
        learnedWords(lang, category).size >= MIN_TO_PLAY

    /**
     * The cards for the next learn round: words that were seen once come back
     * first, otherwise new words are introduced. If there is still room, learned
     * words are repeated (the ones seen least first).
     */
    fun nextRound(lang: Lang, category: Category): List<Word> {
        val words = category.words
        val seen = words.associateWith { timesSeen(lang, category, it) }

        val again = words.filter { seen.getValue(it) in 1 until LEARNED_AFTER }.take(ROUND_SIZE)
        val fresh = words.filter { seen.getValue(it) == 0 }.take(ROUND_SIZE - again.size)

        val round = (again + fresh).toMutableList()
        if (round.size < ROUND_SIZE) {
            round += words
                .filter { it !in round }
                .sortedBy { seen.getValue(it) }
                .take(ROUND_SIZE - round.size)
        }
        return round
    }
}
