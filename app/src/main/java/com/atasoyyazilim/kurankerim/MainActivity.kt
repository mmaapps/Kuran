package com.atasoyyazilim.kurankerim

import android.os.Bundle
import android.content.Intent
import androidx.compose.runtime.mutableLongStateOf
import com.atasoyyazilim.kurankerim.ses.EzanServisi
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.atasoyyazilim.kurankerim.ui.nav.GezinmeGrafigi
import com.atasoyyazilim.kurankerim.guncelleme.GuncellemeKontrolu
import com.atasoyyazilim.kurankerim.ui.theme.KuranTema

class MainActivity : ComponentActivity() {
    private val ezanAcIstegi=mutableLongStateOf(0L)
    override fun onNewIntent(intent:Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        if(intent.action==EzanServisi.EYLEM_AC)ezanAcIstegi.longValue++
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Kenardan kenara çizim: içerik WindowInsets ile kamera deliği ve gezinme tuşlarından korunur.
        enableEdgeToEdge()
        if(intent.action==EzanServisi.EYLEM_AC)ezanAcIstegi.longValue++
        setContent {
            // TODO(Ayarlar.dc.html): tema seçimi DataStore'dan okunacak (Açık / Gece / Otomatik)
            KuranTema {
                GuncellemeKontrolu { kontrol -> GezinmeGrafigi(guncellemeKontrol = kontrol, ezanAcIstegi=ezanAcIstegi.longValue) }
            }
        }
    }
}
