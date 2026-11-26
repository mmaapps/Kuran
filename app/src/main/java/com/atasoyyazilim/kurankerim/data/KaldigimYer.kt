package com.atasoyyazilim.kurankerim.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.kaldigimYerDeposu: DataStore<Preferences> by preferencesDataStore(name = "kaldigim_yer")

data class MushafKonumu(val sayfa: Int = 1, val sure: Int = 1, val ayet: Int = 1)
data class TilavetKonumu(val sure: Int = 1, val ayet: Int = 1, val ms: Long = 0L)

/**
 * "Kaldığın yerden devam" (Main.dc.html kartı, Mushaf ekranı ve tilâvet).
 *
 * KURAL: Ekranlar konumu şu anlarda kaydeder:
 *  - her sayfa/âyet değişiminde,
 *  - uygulama arka plana giderken (Lifecycle ON_STOP),
 *  - oynatıcı duraklatıldığında ve her 5 saniyede bir çalarken (tilâvet ms).
 * Açılışta ekranlar buradan okuyup aynı sayfa/âyet/ms'den başlar.
 */
class KaldigimYer(private val context: Context) {

    private object Anahtar {
        val mushafSayfa = intPreferencesKey("mushaf_sayfa")
        val mushafSure = intPreferencesKey("mushaf_sure")
        val mushafAyet = intPreferencesKey("mushaf_ayet")
        val tilavetSure = intPreferencesKey("tilavet_sure")
        val tilavetAyet = intPreferencesKey("tilavet_ayet")
        val tilavetMs = longPreferencesKey("tilavet_ms")
    }

    val mushaf: Flow<MushafKonumu> = context.kaldigimYerDeposu.data.map { p ->
        MushafKonumu(
            sayfa = p[Anahtar.mushafSayfa] ?: 1,
            sure = p[Anahtar.mushafSure] ?: 1,
            ayet = p[Anahtar.mushafAyet] ?: 1
        )
    }

    val tilavet: Flow<TilavetKonumu> = context.kaldigimYerDeposu.data.map { p ->
        TilavetKonumu(
            sure = p[Anahtar.tilavetSure] ?: 1,
            ayet = p[Anahtar.tilavetAyet] ?: 1,
            ms = p[Anahtar.tilavetMs] ?: 0L
        )
    }

    suspend fun mushafKaydet(k: MushafKonumu) {
        context.kaldigimYerDeposu.edit {
            it[Anahtar.mushafSayfa] = k.sayfa
            it[Anahtar.mushafSure] = k.sure
            it[Anahtar.mushafAyet] = k.ayet
        }
    }

    suspend fun tilavetKaydet(k: TilavetKonumu) {
        context.kaldigimYerDeposu.edit {
            it[Anahtar.tilavetSure] = k.sure
            it[Anahtar.tilavetAyet] = k.ayet
            it[Anahtar.tilavetMs] = k.ms
        }
    }
}
