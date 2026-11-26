package com.atasoyyazilim.kurankerim.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf

enum class TemaSecimi { ACIK, GECE, OTOMATIK }

/** Ekranlar renkleri buradan alır: `val r = LocalTemaRenkleri.current` */
val LocalTemaRenkleri = staticCompositionLocalOf { AcikRenkler }

@Composable
fun KuranTema(
    secim: TemaSecimi = TemaSecimi.OTOMATIK,
    content: @Composable () -> Unit
) {
    val gece = when (secim) {
        TemaSecimi.ACIK -> false
        TemaSecimi.GECE -> true
        TemaSecimi.OTOMATIK -> isSystemInDarkTheme()
    }
    val r = if (gece) GeceRenkleri else AcikRenkler
    // Material bileşenleri kullanılırsa bizim renklerimizi alsın diye eşleme (Material görünümü KULLANILMAZ).
    val sema = if (gece) {
        darkColorScheme(
            primary = Renk.Altin, onPrimary = Renk.YesilKoyu,
            background = r.zemin, onBackground = r.metin,
            surface = r.kart, onSurface = r.metin, outline = r.kenar
        )
    } else {
        lightColorScheme(
            primary = Renk.YesilKoyu, onPrimary = Renk.Beyaz,
            background = r.zemin, onBackground = r.metin,
            surface = r.kart, onSurface = r.metin, outline = r.kenar
        )
    }
    CompositionLocalProvider(LocalTemaRenkleri provides r) {
        MaterialTheme(colorScheme = sema, typography = KuranTipografi, content = content)
    }
}
