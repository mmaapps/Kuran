package com.atasoyyazilim.kurankerim.guncelleme
import org.junit.Test
import org.junit.Assert.*
class GuncellemeKurallariTest {
    @Test fun surumVeDosya() {
        assertEquals(2L,GuncellemeKurallari.kod("KuranKerim-2.apk"))
        assertNull(GuncellemeKurallari.kod("KuranKerim-debug.apk"))
        assertNull(GuncellemeKurallari.kod("KuranKerim-0.apk"))
        assertEquals("1.0.1",GuncellemeKurallari.surum("v1.0.1"))
        assertNull(GuncellemeKurallari.surum("v1.0.1-beta"))
    }
    @Test fun kaynakVeYonlendirme() {
        assertTrue(GuncellemeKurallari.kaynakGecerli("https://github.com/mmaapps/Kuran/releases/download/v1.0.1/KuranKerim-2.apk","v1.0.1","KuranKerim-2.apk"))
        assertFalse(GuncellemeKurallari.kaynakGecerli("https://github.com/other/Kuran/releases/download/v1.0.1/KuranKerim-2.apk","v1.0.1","KuranKerim-2.apk"))
        assertTrue(GuncellemeKurallari.yonlendirmeGecerli("https://release-assets.githubusercontent.com/a"))
        assertFalse(GuncellemeKurallari.yonlendirmeGecerli("http://github.com/a"))
        assertFalse(GuncellemeKurallari.yonlendirmeGecerli("https://github.com.evil.test/a"))
        assertFalse(GuncellemeKurallari.yonlendirmeGecerli("https://evil.test@github.com/a"))
    }
    @Test fun ozet() {
        assertTrue(GuncellemeKurallari.gecerliOzet("sha256:"+"a".repeat(64)))
        assertFalse(GuncellemeKurallari.gecerliOzet("sha256:bad"))
    }
}
