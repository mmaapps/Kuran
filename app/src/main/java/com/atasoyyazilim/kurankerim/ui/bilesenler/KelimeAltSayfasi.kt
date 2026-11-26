package com.atasoyyazilim.kurankerim.ui.bilesenler

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.atasoyyazilim.kurankerim.R
import com.atasoyyazilim.kurankerim.ui.theme.Manrope
import com.atasoyyazilim.kurankerim.ui.theme.MushafYazi
import com.atasoyyazilim.kurankerim.ui.theme.Renk

/** Yalnız doğrulanmış içerik deposundan gelen değerler. Meal veya anlam alanı yoktur. */
data class KelimeHarf(val arapca: String, val ad: String, val sesHazir: Boolean)

/** Mushaf.dc.html: oynatıcı üstündeki 22 dp köşeli panel. Yerleşimi çağıran ekran yapar. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun KelimeAltSayfasi(
    ayet: Int,
    arapca: String,
    okunus: String?,
    harfler: List<KelimeHarf>,
    seciliHarf: Int?,
    kelimeSesiHazir: Boolean,
    durum: String,
    kapat: () -> Unit,
    kelimeyiDinle: () -> Unit,
    harfiDinle: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val sekil = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp)
    Column(modifier.fillMaxWidth().shadow(10.dp, sekil).clip(sekil)
        .background(Renk.Beyaz).padding(start = 18.dp, end = 18.dp, top = 10.dp, bottom = 14.dp)) {
        Box(Modifier.align(Alignment.CenterHorizontally).size(40.dp, 4.dp)
            .background(Renk.KelimeTutamac, RoundedCornerShape(2.dp)))
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(stringResource(R.string.secili_kelime_ayet, ayet), color = Renk.AltinMetin,
                    fontFamily = Manrope, fontWeight = FontWeight.ExtraBold, fontSize = 11.sp,
                    letterSpacing = 0.88.sp)
                if (!okunus.isNullOrEmpty()) {
                    Spacer(Modifier.height(2.dp))
                    Text(okunus, color = Renk.YesilKoyu, fontFamily = Manrope,
                        fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                }
            }
            Text(arapca, modifier = Modifier.weight(1f, fill = false), style = TextStyle(
                fontFamily = MushafYazi, fontSize = 40.sp, lineHeight = 64.sp,
                textDirection = TextDirection.Rtl, color = Renk.YesilKoyu))
            Box(Modifier.align(Alignment.Top).size(44.dp).clip(CircleShape)
                .background(Renk.KelimeKapatZemin).clickable(role = Role.Button, onClick = kapat),
                contentAlignment = Alignment.Center) {
                Icon(KapatIkon, stringResource(R.string.kapat), tint = Renk.Metin, modifier = Modifier.size(18.dp))
            }
        }
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth().height(44.dp).clip(RoundedCornerShape(12.dp))
            .background(Renk.YesilKoyu).clickable(enabled = kelimeSesiHazir, role = Role.Button, onClick = kelimeyiDinle),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically) {
            Icon(KelimeOynatIkon, null, tint = Renk.Beyaz, modifier = Modifier.size(16.dp))
            Text(stringResource(R.string.kelimeyi_dinle), color = Renk.Beyaz,
                fontFamily = Manrope, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
        }
        Spacer(Modifier.height(10.dp))
        Text(stringResource(R.string.harfe_dokun), color = Renk.MetinIkincil, fontFamily = Manrope,
            fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(6.dp))
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)) {
                harfler.forEachIndexed { i, h ->
                    val aktif = i == seciliHarf
                    val cipSekil = RoundedCornerShape(12.dp)
                    Column(Modifier.size(52.dp, 58.dp).clip(cipSekil)
                        .background(if (aktif) Renk.KelimeSecili else Renk.Krem)
                        .border(1.5.dp, if (aktif) Renk.AltinKoyu else Renk.Kenar, cipSekil)
                        .clickable(enabled = h.sesHazir, role = Role.Button) { harfiDinle(i) },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center) {
                        Text(h.arapca, style = TextStyle(fontFamily = MushafYazi,
                            fontSize = 24.sp, lineHeight = 28.8.sp, color = Renk.YesilKoyu,
                            textDirection = TextDirection.Rtl))
                        Text(h.ad, fontFamily = Manrope, fontSize = 10.sp, fontWeight = FontWeight.Bold,
                            color = Renk.MetinIkincil)
                    }
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(durum, Modifier.heightIn(min = 18.dp), fontFamily = Manrope, fontSize = 12.5.sp,
            fontWeight = FontWeight.Bold, color = Renk.Zumrut)
    }
}

private val KapatIkon = ImageVector.Builder("Kapat", 18.dp, 18.dp, 24f, 24f).apply {
    addPath(pathData = addPathNodes("M6 6l12 12M18 6L6 18"), stroke = SolidColor(Color.Black),
        strokeLineWidth = 2.2f, strokeLineCap = StrokeCap.Round)
}.build()

private val KelimeOynatIkon = ImageVector.Builder("KelimeyiDinle", 16.dp, 16.dp, 24f, 24f).apply {
    addPath(pathData = addPathNodes("M7 5l12 7-12 7z"), fill = SolidColor(Color.Black))
}.build()
