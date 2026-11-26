package com.atasoyyazilim.kurankerim.ui.bilesenler

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.atasoyyazilim.kurankerim.ui.theme.AmiriQuran
import com.atasoyyazilim.kurankerim.ui.theme.Renk

/**
 * Camilerdeki levha gibi yuvarlak hat madalyonu (Main.dc.html: 78 dp, EzanAni.dc.html: 92 dp).
 * Yapı: 2 dp altın halka → 4 dp boşluk → 1 dp yarı saydam altın iç halka → koyu yeşil zemin → altın Arapça yazı.
 * Solda "اللّٰه" (30 sp), sağda "مُحَمَّد" (26 sp).
 */
@Composable
fun HatMadalyonu(
    yazi: String,
    aciklama: String,
    modifier: Modifier = Modifier,
    boyut: Dp = 78.dp,
    yaziBoyutu: TextUnit = if (yazi.length <= 6) 30.sp else 26.sp
) {
    Box(
        modifier
            .size(boyut)
            .border(2.dp, Renk.Altin, CircleShape)
            .padding(4.dp)
            .semantics { contentDescription = aciklama },
        contentAlignment = Alignment.Center
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .background(Renk.YesilMadalyon)
                .border(1.dp, Renk.Altin.copy(alpha = 0.55f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = yazi,
                fontFamily = AmiriQuran,
                fontSize = yaziBoyutu,
                color = Renk.AltinAcik,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
