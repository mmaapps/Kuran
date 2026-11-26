package com.atasoyyazilim.kurankerim.ui.bilesenler

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.atasoyyazilim.kurankerim.ui.nav.Ekran
import com.atasoyyazilim.kurankerim.ui.theme.LocalTemaRenkleri
import com.atasoyyazilim.kurankerim.ui.theme.Manrope
import com.atasoyyazilim.kurankerim.ui.theme.Renk

/** Tasarımdaki çizgi ikonlar (24×24 görünüm, 1.8 çizgi, yuvarlak uç). Yol verileri .dc.html dosyalarından. */
private fun cizgiIkon(ad: String, vararg yollar: String): ImageVector =
    ImageVector.Builder(
        name = ad,
        defaultWidth = 23.dp,
        defaultHeight = 23.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        yollar.forEach { d ->
            addPath(
                pathData = addPathNodes(d),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.8f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            )
        }
    }.build()

private data class Sekme(val ekran: Ekran, val etiket: String, val ikon: ImageVector)

private val sekmeler = listOf(
    Sekme(Ekran.ANA, "Ana Sayfa", cizgiIkon("ana", "M3 11l9-7 9 7v9a1 1 0 0 1-1 1h-5v-6h-6v6H4a1 1 0 0 1-1-1z")),
    Sekme(
        Ekran.MUSHAF, "Kur'an",
        cizgiIkon(
            "kuran",
            "M4 4h6a2 2 0 0 1 2 2v14a2 2 0 0 0-2-2H4z",
            "M20 4h-6a2 2 0 0 0-2 2v14a2 2 0 0 1 2-2h6z"
        )
    ),
    Sekme(
        Ekran.VAKITLER, "Vakitler",
        cizgiIkon("vakitler", "M12 5a8 8 0 1 1 0 16a8 8 0 1 1 0-16z", "M12 9v4l2.5 2", "M9 2h6")
    ),
    Sekme(
        Ekran.HADIS, "Hadis",
        cizgiIkon(
            "hadis",
            "M7 4h10a2 2 0 0 1 2 2v12a2 2 0 0 1-2 2H7",
            "M7 4a2 2 0 0 0-2 2v1h4V6a2 2 0 0 0-2-2z",
            "M7 20a2 2 0 0 1-2-2v-1h4v1a2 2 0 0 1-2 2z"
        )
    ),
    Sekme(
        Ekran.AYARLAR, "Ayarlar",
        cizgiIkon(
            "ayarlar",
            "M4 6h9M17 6h3M4 12h3M11 12h9M4 18h11M19 18h1",
            "M15 4a2 2 0 1 1 0 4a2 2 0 1 1 0-4z",
            "M9 10a2 2 0 1 1 0 4a2 2 0 1 1 0-4z",
            "M17 16a2 2 0 1 1 0 4a2 2 0 1 1 0-4z"
        )
    )
)

/**
 * Alt sekme çubuğu — Main, Hadis, EzanVakti, Ayarlar ekranlarında.
 * Tasarım: 78 dp yükseklik, üstte 1 dp çizgi, 5 sekme, aktif sekme Zümrüt + ExtraBold.
 */
@Composable
fun AltSekmeCubugu(aktif: Ekran, git: (Ekran) -> Unit, modifier: Modifier = Modifier) {
    val r = LocalTemaRenkleri.current
    Column(modifier.fillMaxWidth().background(r.kart)) {
        Box(Modifier.fillMaxWidth().height(1.dp).background(if (r.gece) r.kenar else Renk.Cizgi))
        Row(
            Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(start = 6.dp, end = 6.dp, top = 8.dp, bottom = 10.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            sekmeler.forEach { s ->
                val secili = s.ekran == aktif
                val renk = if (secili) r.aktif else r.ikincil
                Column(
                    Modifier
                        .widthIn(min = 64.dp)
                        .heightIn(min = 48.dp)
                        .selectable(selected = secili, role = Role.Tab, onClick = { git(s.ekran) }),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(s.ikon, contentDescription = null, tint = renk, modifier = Modifier.size(23.dp))
                    Spacer(Modifier.height(3.dp))
                    Text(
                        s.etiket,
                        color = renk,
                        fontFamily = Manrope,
                        fontSize = 11.sp,
                        fontWeight = if (secili) FontWeight.ExtraBold else FontWeight.Bold
                    )
                }
            }
        }
    }
}
