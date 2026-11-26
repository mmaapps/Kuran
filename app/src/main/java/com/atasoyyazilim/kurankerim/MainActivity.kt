package com.atasoyyazilim.kurankerim

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.atasoyyazilim.kurankerim.ui.nav.GezinmeGrafigi
import com.atasoyyazilim.kurankerim.guncelleme.GuncellemeKontrolu
import com.atasoyyazilim.kurankerim.ui.theme.KuranTema

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Kenardan kenara çizim: içerik WindowInsets ile kamera deliği ve gezinme tuşlarından korunur.
        enableEdgeToEdge()
        setContent {
            // TODO(Ayarlar.dc.html): tema seçimi DataStore'dan okunacak (Açık / Gece / Otomatik)
            KuranTema {
                GuncellemeKontrolu { kontrol -> GezinmeGrafigi(guncellemeKontrol = kontrol) }
            }
        }
    }
}
