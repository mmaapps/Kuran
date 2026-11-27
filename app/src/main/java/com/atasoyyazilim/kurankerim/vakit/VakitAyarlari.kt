package com.atasoyyazilim.kurankerim.vakit

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class Konum(val enlem: Double, val boylam: Double, val ad: String)

/**
 * Alarm alıcılarının da senkron okuyabilmesi için SharedPreferences.
 * Konum: kullanıcı EzanVakti ekranındaki şehir düğmesinden seçer (assets/content/sehirler/sehirler.json)
 * veya yaklaşık konum izni verir. Seçim yapılana kadar İstanbul kullanılır.
 */
object VakitAyarlari {
    private val _degisim=MutableStateFlow(0L)
    val degisim=_degisim.asStateFlow()
    private val _izinDegisimi=MutableStateFlow(0L)
    val izinDegisimi=_izinDegisimi.asStateFlow()
    fun yenilendi(){_degisim.update{it+1}}
    fun izinYenilendi(){_izinDegisimi.update{it+1}}
    fun konumBelirlendi(c:Context)=sp(c).contains("enlem")
    private fun sp(c: Context) = c.getSharedPreferences("vakit_ayarlari", Context.MODE_PRIVATE)

    fun konum(c: Context): Konum {
        val p = sp(c)
        return Konum(
            enlem = p.getString("enlem", null)?.toDoubleOrNull() ?: 41.0082,
            boylam = p.getString("boylam", null)?.toDoubleOrNull() ?: 28.9784,
            ad = p.getString("ad", null) ?: "İstanbul"
        )
    }

    fun konumKaydet(c: Context, k: Konum) {
        sp(c).edit()
            .putString("enlem", k.enlem.toString())
            .putString("boylam", k.boylam.toString())
            .putString("ad", k.ad)
            .apply()
        yenilendi()
    }

    /** EzanVakti.dc.html: her vakit satırındaki zil düğmesi. */
    fun ezanAcik(c: Context, v: Vakit): Boolean = v != Vakit.GUNES && sp(c).getBoolean("ezan_${v.name}", true)
    fun ezanAyarla(c: Context, v: Vakit, acik: Boolean) = sp(c).edit().putBoolean("ezan_${v.name}", acik).apply()

    /**
     * Sabah ezanı imsaktan kaç dakika SONRA okunsun (EzanVakti.dc.html > "Sabah ezanı" seçimi).
     * Kullanıcı kararı: imsaktan sonra. Varsayılan 30 dk; seçenekler 0 (imsakta), 15, 30, 45, 60.
     */
    val SABAH_GECIKME_SECENEKLERI = listOf(0, 15, 30, 45, 60)
    fun sabahEzaniGecikmeDk(c: Context): Int = sp(c).getInt("sabah_gecikme_dk", 30)
    fun sabahEzaniGecikmeAyarla(c: Context, dk: Int) = sp(c).edit().putInt("sabah_gecikme_dk", dk).apply()

    /** EzanVakti.dc.html > Ezan ayarları */
    fun hatirlatmaAcik(c: Context): Boolean = sp(c).getBoolean("hatirlatma", true)
    fun ezanSonrasiAyetAcik(c: Context): Boolean = sp(c).getBoolean("ezan_sonrasi_ayet", true)
    fun geriSayimAcik(c: Context): Boolean = sp(c).getBoolean("geri_sayim", true)
    fun sessizdeTitret(c: Context): Boolean = sp(c).getBoolean("sessizde_titret", true)

    fun ayarla(c: Context, anahtar: String, deger: Boolean) = sp(c).edit().putBoolean(anahtar, deger).apply()
}
