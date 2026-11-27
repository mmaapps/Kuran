package com.atasoyyazilim.kurankerim.ui.theme

import androidx.compose.ui.graphics.Color

/** 27 Kasım kullanıcı onaylı yumuşak renkli modern Esmâ ve Elif-Ba paleti.
 * Açık kartlarda koyu metin, gece temasında da kontrast korunur.
 * Renkler yalnız görsel gruplama içindir; dini sınıflandırma değildir.
 */
data class OgrenmeRengi(val zemin:Color,val kenar:Color,val vurgu:Color)
object OgrenmeRenkleri {
    private val renkler=listOf(
        OgrenmeRengi(Color(0xFFE4F3EC),Color(0xFFB4D9C7),Color(0xFF175F48)),
        OgrenmeRengi(Color(0xFFFFF0CE),Color(0xFFE5C782),Color(0xFF79520E)),
        OgrenmeRengi(Color(0xFFF1E9F9),Color(0xFFD8C7E9),Color(0xFF62447C)),
        OgrenmeRengi(Color(0xFFFBE8DB),Color(0xFFEAC1AC),Color(0xFF874B36)),
        OgrenmeRengi(Color(0xFFF8E7EE),Color(0xFFE8C0D1),Color(0xFF88435E)),
        OgrenmeRengi(Color(0xFFF1F3DC),Color(0xFFD6DDA6),Color(0xFF535F21))
    )
    fun kart(sira:Int)=renkler[(sira-1).coerceAtLeast(0)%renkler.size]
}
