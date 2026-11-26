package com.atasoyyazilim.kurankerim.ui.bilesenler

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.atasoyyazilim.kurankerim.ui.theme.Manrope
import com.atasoyyazilim.kurankerim.ui.theme.Renk

/** Namaz.dc.html: 3 dp iç boşluk, 3 dp ara, 40 dp düğme ve 10 dp köşe. */
@Composable
fun SegmentKontrol(
    etiketler: List<String>,
    secili: Int,
    sec: (Int) -> Unit,
    modifier: Modifier = Modifier,
    yukseklik: Dp = 40.dp
) {
    Row(
        modifier.fillMaxWidth().background(Renk.SegmentZemin, RoundedCornerShape(12.dp))
            .padding(3.dp).selectableGroup(),
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        etiketler.forEachIndexed { i, etiket ->
            val aktif = i == secili
            Box(
                Modifier.weight(1f).heightIn(min = 44.dp)
                    .selectable(aktif, role = Role.Tab, onClick = { sec(i) }),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    Modifier.fillMaxWidth().height(yukseklik)
                        .background(if (aktif) Renk.YesilKoyu else Color.Transparent, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(etiket, fontFamily = Manrope, fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold, color = if (aktif) Renk.Beyaz else Renk.Metin)
                }
            }
        }
    }
}

/** Hadis 36 dp/700, Zikirmatik 40 dp/800; görünüm ve dokunma alanı ayrı. */
@Composable
fun Cip(
    etiket: String,
    secili: Boolean,
    tikla: () -> Unit,
    modifier: Modifier = Modifier,
    yukseklik: Dp = 36.dp,
    kalinlik: FontWeight = FontWeight.Bold,
    seciliZemin: Color = Renk.YesilKoyu,
    seciliMetin: Color = Renk.Beyaz
) {
    val sekil = RoundedCornerShape(yukseklik / 2)
    val kenar = if (secili) seciliZemin else Renk.Cizgi
    Box(modifier.sizeIn(minWidth = 44.dp, minHeight = 44.dp)
        .selectable(secili, role = Role.Button, onClick = tikla),
        contentAlignment = Alignment.Center) {
        Box(Modifier.height(yukseklik)
            .background(if (secili) seciliZemin else Renk.Beyaz, sekil)
            .border(1.dp, kenar, sekil).padding(horizontal = 14.dp),
            contentAlignment = Alignment.Center) {
            Text(etiket, fontFamily = Manrope, fontSize = 13.sp, fontWeight = kalinlik,
                color = if (secili) seciliMetin else Renk.Metin)
        }
    }
}

/** SVG çemberinin özgün yarıçapı ve çizgi kalınlığı, her ekrandan parametre olarak verilir. */
@Composable
fun IlerlemeHalkasi(
    ilerleme: Float,
    boyut: Dp,
    yaricap: Dp,
    kalinlik: Dp,
    modifier: Modifier = Modifier,
    renk: Color = Renk.Altin,
    izRengi: Color = Renk.Beyaz.copy(alpha = 0.12f),
    icerik: @Composable BoxScope.() -> Unit = {}
) {
    val oran = if (ilerleme.isFinite()) ilerleme.coerceIn(0f, 1f) else 0f
    Box(modifier.size(boyut).semantics {
        progressBarRangeInfo = ProgressBarRangeInfo(oran, 0f..1f)
    }, contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val r = yaricap.toPx().coerceIn(0f, size.minDimension / 2f)
            val w = kalinlik.toPx()
            val stroke = Stroke(width = w, cap = StrokeCap.Round)
            drawCircle(izRengi, radius = r, style = stroke)
            if (oran > 0f) {
                drawArc(renk, -90f, oran * 360f, false,
                    topLeft = Offset(center.x - r, center.y - r),
                    size = Size(r * 2f, r * 2f), style = stroke)
            }
        }
        icerik()
    }
}
