package com.atasoyyazilim.kurankerim.data

import org.junit.Assert.*
import org.junit.Test
import org.w3c.dom.Element
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

class MushafVerisiTest {
    private fun kaynak(ad: String): File {
        val relative = "src/main/assets/content/quran/$ad"
        return listOf(File(relative), File("app/$relative")).first { it.isFile }
    }
    private fun oku(): MushafVerisi = kaynak("quran-uthmani.xml").inputStream().use { m ->
        kaynak("quran-data.xml").inputStream().use { d -> MushafVerisi.oku(m, d) }
    }

    @Test fun butunSayfalarVeAyetler() {
        val v = oku()
        assertEquals(114, v.sureler.size)
        assertEquals(604, v.sayfalar.size)
        assertEquals((1..604).toList(), v.sayfalar.map { it.no })
        assertEquals(6236, v.sayfalar.sumOf { it.ayetler.size })
        assertEquals(6236, v.sayfalar.flatMap { it.ayetler }.map { it.sure to it.no }.distinct().size)
        assertEquals((1..30).toSet(), v.sayfalar.flatMap { it.ayetler }.map { v.cuzNo(it.sure, it.no)!! }.toSet())
    }

    @Test fun kaynakMetniHarfHarekeBoslukDahilAynidir() {
        val v = oku()
        val nodes = DocumentBuilderFactory.newInstance().newDocumentBuilder()
            .parse(kaynak("quran-uthmani.xml")).getElementsByTagName("aya")
        assertEquals(6236, nodes.length)
        v.sayfalar.flatMap { it.ayetler }.forEachIndexed { i, a ->
            val e = nodes.item(i) as Element
            assertEquals(e.getAttribute("text"), a.arapca)
            assertEquals(if (e.hasAttribute("bismillah")) e.getAttribute("bismillah") else null, a.besmele)
            assertEquals(a, v.ayet(a.sure, a.no))
        }
    }

    @Test fun sayfaVeCuzSinirlari() {
        val v = oku()
        assertEquals(1, v.sayfaNo(1, 1))
        assertEquals(2, v.sayfaNo(2, 1))
        assertEquals(2, v.cuzNo(2, 142))
        assertEquals(604, v.sayfaNo(114, 6))
        assertEquals(30, v.cuzNo(114, 6))
        v.sayfalar.forEach { p -> p.ayetler.forEach { a -> assertEquals(p.no, v.sayfaNo(a.sure, a.no)) } }
        assertNull(v.ayet(0, 1))
        assertNull(v.ayet(114, 7))
        assertNull(v.sayfa(605))
    }

    @Test(expected = IllegalArgumentException::class)
    fun eksikKaynakKabulEdilmez() {
        kaynak("quran-data.xml").inputStream().use { d ->
            MushafVerisi.oku("<quran/>".byteInputStream(), d)
        }
    }
}
