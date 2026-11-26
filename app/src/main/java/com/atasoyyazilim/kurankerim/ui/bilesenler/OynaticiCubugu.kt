package com.atasoyyazilim.kurankerim.ui.bilesenler

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.atasoyyazilim.kurankerim.R
import com.atasoyyazilim.kurankerim.ui.theme.Manrope
import com.atasoyyazilim.kurankerim.ui.theme.Renk

private fun dolguIkon(ad: String, yol: String) = ImageVector.Builder(
    name = ad, defaultWidth = 20.dp, defaultHeight = 20.dp,
    viewportWidth = 24f, viewportHeight = 24f
).apply {
    addPath(pathData = addPathNodes(yol), fill = SolidColor(Color.Black))
}.build()

private val Onceki = dolguIkon("onceki", "M18 5v14l-9-7zM6 5h2v14H6z")
private val Sonraki = dolguIkon("sonraki", "M6 5v14l9-7zM16 5h2v14h-2z")
private val Oynat = dolguIkon("oynat", "M8 5l11 7-11 7z")
private val Duraklat = dolguIkon("duraklat", "M7 5h3v14H7zM14 5h3v14h-3z")

/** Mushaf.dc.html: yalnız arayüz. İzinli ses kaynağı yoksa düğmeler etkinleştirilmez. */
@Composable
fun OynaticiCubugu(
    baslik: String,
    altBaslik: String,
    ilerleme: Float,
    oynuyor: Boolean,
    sesHazir: Boolean,
    onceki: () -> Unit,
    oynatDuraklat: () -> Unit,
    sonraki: () -> Unit,
    modifier: Modifier = Modifier
) {
    val oran = if (ilerleme.isFinite()) ilerleme.coerceIn(0f, 1f) else 0f
    Column(modifier.fillMaxWidth().height(86.dp).background(Renk.YesilKoyu)
        .padding(start = 16.dp, end = 16.dp, top = 10.dp)) {
        Box(Modifier.fillMaxWidth().height(3.dp).clip(RoundedCornerShape(2.dp))
            .background(Renk.Beyaz.copy(alpha = 0.15f))) {
            Box(Modifier.fillMaxWidth(oran).fillMaxHeight().background(Renk.Altin, RoundedCornerShape(2.dp)))
        }
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Column(Modifier.weight(1f)) {
                Text(baslik, fontFamily = Manrope, fontSize = 13.5.sp, fontWeight = FontWeight.ExtraBold,
                    color = Renk.Beyaz, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(altBaslik, fontFamily = Manrope, fontSize = 11.5.sp,
                    color = Renk.KoyuMetinSoluk, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Box(Modifier.size(44.dp).clickable(enabled = sesHazir, role = Role.Button, onClick = onceki),
                contentAlignment = Alignment.Center) {
                Icon(Onceki, stringResource(R.string.onceki_ayet), tint = Renk.Beyaz, modifier = Modifier.size(20.dp))
            }
            Box(Modifier.size(48.dp).clip(CircleShape).background(Renk.Altin)
                .clickable(enabled = sesHazir, role = Role.Button, onClick = oynatDuraklat),
                contentAlignment = Alignment.Center) {
                Icon(if (oynuyor) Duraklat else Oynat,
                    stringResource(if (oynuyor) R.string.duraklat else R.string.oynat),
                    tint = Renk.YesilKoyu, modifier = Modifier.size(20.dp))
            }
            Box(Modifier.size(44.dp).clickable(enabled = sesHazir, role = Role.Button, onClick = sonraki),
                contentAlignment = Alignment.Center) {
                Icon(Sonraki, stringResource(R.string.sonraki_ayet), tint = Renk.Beyaz, modifier = Modifier.size(20.dp))
            }
        }
    }
}
