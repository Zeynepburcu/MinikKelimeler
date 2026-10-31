package com.minikkelimeler.app

import android.content.Context
import android.speech.tts.TextToSpeech
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.util.Locale

/**
 * Reads words aloud with the phone's built-in text-to-speech engine.
 * Works offline once the German / English voices are installed on the phone.
 */
class Speaker(context: Context) {

    /** True when the phone has no voice for the requested language. */
    var missingVoice by mutableStateOf(false)
        private set

    private var ready = false
    private var pending: Pair<String, Lang>? = null

    private val tts: TextToSpeech = TextToSpeech(context.applicationContext) { status ->
        if (status == TextToSpeech.SUCCESS) {
            ready = true
            pending?.let { (text, lang) -> speak(text, lang) }
            pending = null
        }
    }

    fun speak(text: String, lang: Lang) {
        if (!ready) {
            pending = text to lang
            return
        }
        if (!setLanguage(lang)) {
            missingVoice = true
            return
        }
        missingVoice = false
        // A little slower than normal, easier for small children.
        tts.setSpeechRate(0.8f)
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "word")
    }

    fun stop() {
        if (ready) tts.stop()
    }

    fun shutdown() {
        tts.stop()
        tts.shutdown()
    }

    private fun setLanguage(lang: Lang): Boolean {
        if (tts.setLanguage(lang.locale) >= TextToSpeech.LANG_AVAILABLE) return true
        // Fall back to American English if no British voice is installed.
        if (lang == Lang.EN && tts.setLanguage(Locale.US) >= TextToSpeech.LANG_AVAILABLE) return true
        return false
    }
}
