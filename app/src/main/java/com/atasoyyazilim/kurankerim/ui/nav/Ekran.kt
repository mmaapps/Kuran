package com.atasoyyazilim.kurankerim.ui.nav

/**
 * Uygulamanın TÜM ekranları. Her ekranın tasarımı 02_TASARIM/ekranlar/<tasarimDosyasi> dosyasındadır.
 * Bu listeye tasarımda olmayan ekran eklenmez.
 */
enum class Ekran(val rota: String, val baslik: String, val tasarimDosyasi: String) {
    ANA("ana", "Ana Sayfa", "Main.dc.html"),
    MUSHAF("mushaf", "Kur'an-ı Kerim", "Mushaf.dc.html"),
    HADIS("hadis", "Hadis-i Şerifler", "Hadis.dc.html"),
    ILMIHAL("ilmihal", "İslam'ı Öğrenelim", "Ilmihal.dc.html"),
    NAMAZ("namaz", "Namaz Rehberi", "Namaz.dc.html"),
    VAKITLER("vakitler", "Ezan Vakitleri", "EzanVakti.dc.html"),
    EZAN_ANI("ezan_ani", "Ezan", "EzanAni.dc.html"),
    KIBLE("kible", "Kıble Pusulası", "Kible.dc.html"),
    ZIKIRMATIK("zikirmatik", "Zikirmatik", "Zikirmatik.dc.html"),
    ESMA("esma", "Esmâü'l-Hüsnâ", "Esma.dc.html"),
    ELIFBA("elifba", "Elif-Ba", "ElifBa.dc.html"),
    DUALAR("dualar", "Dualar", "Dualar.dc.html"),
    HATIM("hatim", "Hatim Takibi", "Hatim.dc.html"),
    KAZA("kaza", "Kaza Namazı Takibi", "Kaza.dc.html"),
    AYARLAR("ayarlar", "Ayarlar", "Ayarlar.dc.html"),
    HAKKINDA("hakkinda", "Hakkında ve Kaynaklar", "Hakkinda.dc.html")
}
