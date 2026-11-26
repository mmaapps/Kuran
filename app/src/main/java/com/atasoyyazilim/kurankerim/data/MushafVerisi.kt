package com.atasoyyazilim.kurankerim.data

import org.w3c.dom.Element
import org.xml.sax.SAXException
import java.io.ByteArrayInputStream
import java.io.InputStream
import javax.xml.parsers.DocumentBuilderFactory

data class KuranAyet(val sure: Int, val no: Int, val arapca: String, val besmele: String?)
data class KuranSure(val no: Int, val arapcaAd: String, val latinAd: String, val ayetSayisi: Int, val tur: String)
data class KuranSayfa(val no: Int, val cuz: Int, val ayetler: List<KuranAyet>)

/** Yalnız orijinal Tanzil Uthmani XML ve metadata. Kaynak metin normalize edilmez. */
class MushafVerisi private constructor(
    val sureler: List<KuranSure>,
    val sayfalar: List<KuranSayfa>,
    private val ayetler: List<KuranAyet>,
    private val sayfaBaslari: List<Int>,
    private val cuzBaslari: List<Int>
) {
    fun ayet(sure: Int, no: Int): KuranAyet? =
        if (sure in 1..sureler.size && no in 1..sureler[sure - 1].ayetSayisi)
            ayetler[sureler.take(sure - 1).sumOf { it.ayetSayisi } + no - 1] else null

    fun sayfa(no: Int): KuranSayfa? = sayfalar.getOrNull(no - 1)

    fun sayfaNo(sure: Int, no: Int): Int? {
        val i = ayetIndeksi(sure, no) ?: return null
        return sayfaBaslari.indexOfLast { it <= i } + 1
    }

    fun cuzNo(sure: Int, no: Int): Int? {
        val i = ayetIndeksi(sure, no) ?: return null
        return cuzBaslari.indexOfLast { it <= i } + 1
    }

    private fun ayetIndeksi(sure: Int, no: Int): Int? =
        if (sure in 1..sureler.size && no in 1..sureler[sure - 1].ayetSayisi)
            sureler.take(sure - 1).sumOf { it.ayetSayisi } + no - 1 else null

    companion object {
        fun oku(metin: InputStream, metadata: InputStream): MushafVerisi {
            val meta = xmlKok(metadata)
            val quran = xmlKok(metin)
            val metaSureler = meta.alt("suras").single().alt("sura")
            val quranSureler = quran.alt("sura")
            require(metaSureler.size == 114 && quranSureler.size == 114)
            var toplam = 0
            val sureler = metaSureler.mapIndexed { i, e ->
                require(e.sayi("index") == i + 1 && e.sayi("start") == toplam)
                val sayi = e.sayi("ayas")
                require(sayi > 0)
                toplam += sayi
                KuranSure(i + 1, e.getAttribute("name"), e.getAttribute("tname"), sayi, e.getAttribute("type"))
            }
            require(toplam == 6236)
            val ayetler = quranSureler.flatMapIndexed { i, e ->
                require(e.sayi("index") == i + 1)
                val a = e.alt("aya")
                require(a.size == sureler[i].ayetSayisi)
                a.mapIndexed { j, x ->
                    require(x.sayi("index") == j + 1 && x.hasAttribute("text"))
                    val arapca = x.getAttribute("text")
                    require(arapca.isNotBlank())
                    KuranAyet(i + 1, j + 1, arapca,
                        if (x.hasAttribute("bismillah")) x.getAttribute("bismillah") else null)
                }
            }
            val sureBaslari = metaSureler.map { it.sayi("start") }
            fun baslar(bolum: String, etiket: String, adet: Int): List<Int> {
                val liste = meta.alt(bolum).single().alt(etiket)
                require(liste.size == adet)
                val indeksler = liste.mapIndexed { i, x ->
                    require(x.sayi("index") == i + 1)
                    val s = x.sayi("sura")
                    val a = x.sayi("aya")
                    require(s in 1..114 && a in 1..sureler[s - 1].ayetSayisi)
                    sureBaslari[s - 1] + a - 1
                }
                require(indeksler.first() == 0 && indeksler.zipWithNext().all { (a, b) -> a < b })
                return indeksler
            }
            val sayfaBaslari = baslar("pages", "page", 604)
            val cuzBaslari = baslar("juzs", "juz", 30)
            val sayfalar = sayfaBaslari.mapIndexed { i, bas ->
                val son = sayfaBaslari.getOrNull(i + 1) ?: ayetler.size
                KuranSayfa(i + 1, cuzBaslari.indexOfLast { it <= bas } + 1, ayetler.subList(bas, son).toList())
            }
            return MushafVerisi(sureler, sayfalar, ayetler, sayfaBaslari, cuzBaslari)
        }

        private fun xmlKok(stream: InputStream): Element {
            // Yalnız paket içi XML; haricî varlıklar ve DOCTYPE kabul edilmez.
            val raw = stream.readBytes()
            require(!raw.toString(Charsets.UTF_8).contains("<!DOCTYPE", ignoreCase = true))
            val builder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
            builder.setEntityResolver { _, _ -> throw SAXException("Haricî XML varlığı yasak") }
            val kok = builder.parse(ByteArrayInputStream(raw)).documentElement
            require(kok.tagName == "quran")
            return kok
        }

        private fun Element.alt(ad: String): List<Element> =
            (0 until childNodes.length).mapNotNull { childNodes.item(it) as? Element }.filter { it.tagName == ad }

        private fun Element.sayi(ad: String): Int = getAttribute(ad).toInt()
    }
}
