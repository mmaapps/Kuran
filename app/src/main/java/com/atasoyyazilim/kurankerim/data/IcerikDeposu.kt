package com.atasoyyazilim.kurankerim.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

/**
 * Tüm dini içerik SADECE buradan okunur (03_ICERIK_KAYNAKLARI.md).
 * Dosya yoksa veya boşsa boş liste döner → ekran BosDurum gösterir. İçerik ASLA kodda üretilmez.
 */
class IcerikDeposu(private val context: Context) {

    fun jsonOku(yol: String): JSONObject? = try {
        context.assets.open("content/$yol").bufferedReader(Charsets.UTF_8).use { JSONObject(it.readText()) }
    } catch (e: IOException) {
        null
    } catch (e: JSONException) {
        null
    }

    private inline fun <T> liste(yol: String, anahtar: String, donustur: (JSONObject) -> T): List<T> {
        val dizi: JSONArray = jsonOku(yol)?.optJSONArray(anahtar) ?: return emptyList()
        return (0 until dizi.length()).map { donustur(dizi.getJSONObject(it)) }
    }

    private fun JSONObject.metinler(anahtar: String): List<String> {
        val d = optJSONArray(anahtar) ?: return emptyList()
        return (0 until d.length()).map { d.getString(it) }
    }

    /** Diskteki özgün XML değişmeden okunur; eksik/geçersiz veri boş durum üretir. */
    fun mushaf(): MushafVerisi? = try {
        context.assets.open("content/quran/quran-uthmani.xml").use { metin ->
            context.assets.open("content/quran/quran-data.xml").use { metadata ->
                MushafVerisi.oku(metin, metadata)
            }
        }
    } catch (e: Exception) {
        android.util.Log.e("IcerikDeposu", "Mushaf XML okunamadı", e)
        null
    }

    fun hadisler(): List<Hadis> = liste("hadis/hadisler.json", "hadisler") {
        Hadis(
            id = it.getString("id"),
            kategori = it.optString("kategori"),
            arapca = it.optString("arapca"),
            turkce = it.getString("turkce"),
            kaynak = it.getString("kaynak"),
            derece = it.getString("derece")
        )
    }

    fun dualar(): List<Dua> = liste("dualar/dualar.json", "dualar") {
        Dua(
            id = it.getString("id"),
            ad = it.getString("ad"),
            kategori = it.optString("kategori"),
            arapca = it.getString("arapca"),
            okunus = it.optString("okunus"),
            anlam = it.getString("anlam"),
            kaynak = it.getString("kaynak")
        )
    }

    fun esma(): List<Esma> = liste("esma/esma.json", "isimler") {
        Esma(
            no = it.getInt("no"),
            arapca = it.getString("arapca"),
            okunus = it.getString("okunus"),
            anlam = it.getString("anlam")
        )
    }

    fun harfler(): List<Harf> = liste("elifba/harfler.json", "harfler") {
        Harf(
            sira = it.getInt("sira"),
            harf = it.getString("harf"),
            ad = it.getString("ad"),
            not = it.optString("not"),
            sesDosyasi = it.optString("ses")
        )
    }

    fun abdestAdimlari(): List<NamazAdimi> = adimlar("namaz/abdest.json")
    fun namazAdimlari(): List<NamazAdimi> = adimlar("namaz/namaz.json")

    private fun adimlar(yol: String): List<NamazAdimi> = liste(yol, "adimlar") {
        NamazAdimi(
            no = it.getInt("no"),
            cizim = it.getString("cizim"),
            baslik = it.getString("baslik"),
            hukum = it.optString("hukum"),
            aciklama = it.getString("aciklama"),
            gorselAciklama = it.optString("gorsel"),
            arapca = it.optString("arapca"),
            okunus = it.optString("okunus")
        )
    }

    fun ilmihal(): List<IlmihalBolumu> = liste("ilmihal/ilmihal.json", "bolumler") {
        IlmihalBolumu(
            id = it.getString("id"),
            baslik = it.getString("baslik"),
            altBaslik = it.optString("alt_baslik"),
            maddeler = it.metinler("maddeler")
        )
    }

    fun ezanSonrasiAyetler(): List<AyetAraligi> = liste("ayetler/ezan_sonrasi.json", "liste") { o ->
        val d = o.getJSONArray("ayetler")
        AyetAraligi(sure = o.getInt("sure"), ayetler = (0 until d.length()).map { d.getInt(it) })
    }
}
