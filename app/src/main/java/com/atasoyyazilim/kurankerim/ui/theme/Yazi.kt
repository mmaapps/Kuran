package com.atasoyyazilim.kurankerim.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.atasoyyazilim.kurankerim.R

/** Arayüz yazısı (değişken yazı tipi; ağırlık otomatik ayarlanır). Lisans: assets/lisanslar/manrope_OFL.txt */
val Manrope = FontFamily(
    Font(R.font.manrope_variable, FontWeight.Normal),
    Font(R.font.manrope_variable, FontWeight.Medium),
    Font(R.font.manrope_variable, FontWeight.SemiBold),
    Font(R.font.manrope_variable, FontWeight.Bold),
    Font(R.font.manrope_variable, FontWeight.ExtraBold)
)

/** Hadis ve dua metni (kitap hissi). Lisans: assets/lisanslar/lora_OFL.txt */
val Lora = FontFamily(
    Font(R.font.lora_variable, FontWeight.Normal),
    Font(R.font.lora_variable, FontWeight.Medium),
    Font(R.font.lora_variable, FontWeight.SemiBold),
    Font(R.font.lora_italic_variable, FontWeight.Normal, FontStyle.Italic)
)

/** Arapça başlık, hat madalyonu, dua Arapçası. Lisans: assets/lisanslar/amiri_*_OFL.txt */
val AmiriQuran = FontFamily(Font(R.font.amiri_quran))
val Amiri = FontFamily(
    Font(R.font.amiri_regular, FontWeight.Normal),
    Font(R.font.amiri_bold, FontWeight.Bold)
)

/**
 * MUSHAF YAZISI.
 * KFGQPC Hafs dosyası (res/font/kfgqpc_hafs.ttf) resmî siteden eklendiğinde bu satır
 * `FontFamily(Font(R.font.kfgqpc_hafs))` yapılacak. O zamana kadar önizleme için Amiri Quran.
 */
val MushafYazi: FontFamily = AmiriQuran

/** Tasarımdaki yazı ölçeği (TASARIM_KURALLARI.md §2). */
val KuranTipografi = Typography(
    headlineMedium = TextStyle(fontFamily = Manrope, fontWeight = FontWeight.ExtraBold, fontSize = 26.sp),
    titleLarge = TextStyle(fontFamily = Manrope, fontWeight = FontWeight.ExtraBold, fontSize = 19.sp),
    titleMedium = TextStyle(fontFamily = Manrope, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp),
    bodyLarge = TextStyle(fontFamily = Manrope, fontWeight = FontWeight.Normal, fontSize = 15.sp, lineHeight = 24.sp),
    bodyMedium = TextStyle(fontFamily = Manrope, fontWeight = FontWeight.SemiBold, fontSize = 13.sp),
    bodySmall = TextStyle(fontFamily = Manrope, fontWeight = FontWeight.SemiBold, fontSize = 12.sp),
    labelMedium = TextStyle(
        fontFamily = Manrope,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 11.5.sp,
        letterSpacing = 0.1.em
    ),
    labelSmall = TextStyle(fontFamily = Manrope, fontWeight = FontWeight.Bold, fontSize = 11.sp)
)

/** Kitap metinleri (Hadis.dc.html, Dualar.dc.html). */
object KitapYazi {
    val hadis = TextStyle(fontFamily = Lora, fontSize = 15.5.sp, lineHeight = 25.sp)
    val gununHadisi = TextStyle(fontFamily = Lora, fontSize = 17.sp, lineHeight = 27.sp)
}
