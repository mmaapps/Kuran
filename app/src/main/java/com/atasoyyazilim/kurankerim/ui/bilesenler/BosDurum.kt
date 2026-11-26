package com.atasoyyazilim.kurankerim.ui.bilesenler

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.atasoyyazilim.kurankerim.R
import com.atasoyyazilim.kurankerim.ui.theme.Manrope
import com.atasoyyazilim.kurankerim.ui.theme.Renk

/** TASARIM_KURALLARI §4: ikon + tek cümle. İkonu çağıran ekranın özgün tasarımı belirler. */
@Composable
fun BosDurum(
    ikon: ImageVector,
    modifier: Modifier = Modifier,
    metin: String = stringResource(R.string.bos_durum)
) {
    Column(modifier.fillMaxWidth().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Icon(ikon, null, tint = Renk.AltinMetin)
        Text(metin, fontFamily = Manrope, fontSize = 15.sp, fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center, color = Renk.MetinIkincil)
    }
}
