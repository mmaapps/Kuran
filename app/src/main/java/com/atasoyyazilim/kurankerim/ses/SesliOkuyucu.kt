package com.atasoyyazilim.kurankerim.ses

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

/**
 * Hadis, ilmihal, dua anlamı ve abdest/namaz "Sesli anlat" için Türkçe sesli okuma.
 * Telefonun kendi ses motorunu kullanır: dosya yok, internet yok.
 *
 * KURAL: Buraya YALNIZCA TÜRKÇE metin verilir. Kur'an'ın ve duaların Arapçası TTS ile okunmaz.
 */
class SesliOkuyucu(context: Context, private val hazir: (turkceVar: Boolean) -> Unit = {}) :
    TextToSpeech.OnInitListener {

    /** false ise ekranda: "Telefon ayarlarından Türkçe ses verisini açın" (uygulama bir şey indirmez). */
    var turkceVar: Boolean = false
        private set

    private val tts = TextToSpeech(context.applicationContext, this)

    override fun onInit(durum: Int) {
        if (durum == TextToSpeech.SUCCESS) {
            val sonuc = tts.setLanguage(Locale.forLanguageTag("tr-TR"))
            turkceVar = sonuc != TextToSpeech.LANG_MISSING_DATA && sonuc != TextToSpeech.LANG_NOT_SUPPORTED
        }
        hazir(turkceVar)
    }

    fun oku(metin: String, kimlik: String = "okuma") {
        if (turkceVar) tts.speak(metin, TextToSpeech.QUEUE_FLUSH, null, kimlik)
    }

    fun siraya(metin: String, kimlik: String) {
        if (turkceVar) tts.speak(metin, TextToSpeech.QUEUE_ADD, null, kimlik)
    }

    fun hiz(oran: Float) {
        tts.setSpeechRate(oran)
    }

    fun durdur() {
        tts.stop()
    }

    fun kapat() {
        tts.shutdown()
    }
}
