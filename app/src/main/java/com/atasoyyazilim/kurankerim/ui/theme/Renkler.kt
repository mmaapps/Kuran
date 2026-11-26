package com.atasoyyazilim.kurankerim.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Tasarımın renkleri — 02_TASARIM/TASARIM_KURALLARI.md §1 ile BİREBİR aynı.
 * Yeni renk eklenmez; ekranlarda Color(0x...) yazılmaz, buradan kullanılır.
 */
object Renk {
    val YesilKoyu = Color(0xFF0B2E26)
    val YesilMadalyon = Color(0xFF0F3D33)
    val Zumrut = Color(0xFF0F5C4A)
    val Altin = Color(0xFFD4AF5A)
    val AltinAcik = Color(0xFFE2C277)
    val AltinKoyu = Color(0xFFB8923A)
    val AltinMetin = Color(0xFF7A5C14)
    val Krem = Color(0xFFFBF6EA)
    val Kagit = Color(0xFFFFFBF0)
    val MushafIcCerceve = Color(0xFFD9C28A) // Mushaf.dc.html
    val MushafZemin = Color(0xFFEFE5CC)
    val Kenar = Color(0xFFEDE5D1)
    val Cizgi = Color(0xFFE7DFCB)
    val Ayirici = Color(0xFFF3EDDD)
    val Metin = Color(0xFF1B2421)
    val MetinIkincil = Color(0xFF5B6560)
    val KoyuMetin = Color(0xFFCFE0D8)
    val KoyuMetinSoluk = Color(0xFFA9C2B8)
    val VurguAcik = Color(0xFFF6EDD6)
    val YesilAcik = Color(0xFFE6F0EC)
    val KelimeSecili = Color(0xFFEBD592)
    val OkunanAyet = Color(0xFFF5E8C4)
    val Su = Color(0xFF8ED1E6)
    val Basari = Color(0xFF7FD6A8)
    // Namaz.dc.html: segment kontrolünün özgün zemin rengi.
    val SegmentZemin = Color(0xFFEFE6D0)
    // Mushaf.dc.html: kelime alt sayfasındaki özgün renkler.
    val KelimeTutamac = Color(0xFFE0D8C4)
    val KelimeKapatZemin = Color(0xFFF4EFE3)
    val Beyaz = Color(0xFFFFFFFF)
}

/** Açık ve gece temasında değişen renkler (Ayarlar.dc.html). */
data class TemaRenkleri(
    val zemin: Color,
    val kart: Color,
    val kenar: Color,
    val metin: Color,
    val baslik: Color,
    val ikincil: Color,
    val altinMetin: Color,
    val kagit: Color,
    val aktif: Color,
    val gece: Boolean
)

val AcikRenkler = TemaRenkleri(
    zemin = Renk.Krem,
    kart = Renk.Beyaz,
    kenar = Renk.Kenar,
    metin = Renk.Metin,
    baslik = Renk.YesilKoyu,
    ikincil = Renk.MetinIkincil,
    altinMetin = Renk.AltinMetin,
    kagit = Renk.Kagit,
    aktif = Renk.Zumrut,
    gece = false
)

val GeceRenkleri = TemaRenkleri(
    zemin = Color(0xFF0E1A17),
    kart = Color(0xFF16261F),
    kenar = Color(0xFF24382F),
    metin = Color(0xFFE8EFEB),
    baslik = Color(0xFFFFFFFF),
    ikincil = Renk.KoyuMetinSoluk,
    altinMetin = Renk.Altin,
    kagit = Color(0xFF1C2E26),
    aktif = Renk.AltinAcik,
    gece = true
)
