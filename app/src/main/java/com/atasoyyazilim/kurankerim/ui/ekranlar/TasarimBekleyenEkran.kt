package com.atasoyyazilim.kurankerim.ui.ekranlar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.atasoyyazilim.kurankerim.ui.bilesenler.HatMadalyonu
import com.atasoyyazilim.kurankerim.ui.nav.Ekran
import com.atasoyyazilim.kurankerim.ui.theme.LocalTemaRenkleri
import com.atasoyyazilim.kurankerim.ui.theme.Renk

/**
 * GEÇİCİ yer tutucu. Gerçek ekran yazılınca GezinmeGrafigi'nden çıkarılır.
 * Ana sayfada tüm ekranlara geçiş listesi vardır (sadece geliştirme sırasında deneme için).
 */
@Composable
fun TasarimBekleyenEkran(ekran: Ekran, git: (Ekran) -> Unit, geri: () -> Unit) {
    val r = LocalTemaRenkleri.current
    Column(
        Modifier
            .fillMaxSize()
            .background(r.zemin)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(16.dp)
    ) {
        if (ekran == Ekran.ANA) {
            Box(Modifier.fillMaxWidth()) {
                HatMadalyonu(yazi = "اللّٰه", aciklama = "Allah lafzı – hat levhası")
                HatMadalyonu(
                    yazi = "مُحَمَّد",
                    aciklama = "Muhammed lafzı – hat levhası",
                    modifier = Modifier.align(androidx.compose.ui.Alignment.CenterEnd)
                )
            }
            Spacer(Modifier.height(16.dp))
        }
        Text(ekran.baslik, style = MaterialTheme.typography.headlineMedium, color = r.baslik)
        Text(
            "Tasarım: 02_TASARIM/ekranlar/${ekran.tasarimDosyasi}",
            style = MaterialTheme.typography.bodySmall,
            color = r.ikincil
        )
        Spacer(Modifier.height(16.dp))
        if (ekran == Ekran.ANA) {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                items(Ekran.entries.filter { it != Ekran.ANA }) { hedef ->
                    Text(
                        hedef.baslik,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 48.dp)
                            .clickable { git(hedef) }
                            .padding(vertical = 12.dp),
                        style = MaterialTheme.typography.titleMedium,
                        color = r.metin
                    )
                }
            }
        } else {
            Text(
                "‹ Geri",
                modifier = Modifier
                    .heightIn(min = 48.dp)
                    .clickable { geri() }
                    .padding(vertical = 12.dp),
                style = MaterialTheme.typography.titleMedium,
                color = Renk.Zumrut
            )
        }
    }
}
