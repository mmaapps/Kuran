package com.atasoyyazilim.kurankerim.vakit

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.atasoyyazilim.kurankerim.bildirim.Bildirimler
import com.atasoyyazilim.kurankerim.ses.EzanServisi
import java.io.IOException

/** Ezan ve "5 dakika kaldı" alarmlarını karşılar. */
class EzanAlarmAlici : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val vakit = intent.getStringExtra(EzanZamanlayici.EK_VAKIT)
            ?.let { ad -> Vakit.entries.firstOrNull { it.name == ad } }
            ?: return

        val zaman = intent.getLongExtra(EzanZamanlayici.EK_ZAMAN, 0L)
        if (zaman <= 0L) return
        when (intent.getStringExtra(EzanZamanlayici.EK_TUR)) {
            EzanZamanlayici.TUR_HATIRLAT -> if (VakitAyarlari.hatirlatmaAcik(context) && System.currentTimeMillis() - zaman < 60_000L) Bildirimler.hatirlatmaGoster(context, vakit)
            EzanZamanlayici.TUR_EZAN -> {
                val dosya = vakit.sesDosyasi
                val hazir = dosya != null && try {
                    context.assets.openFd("ses/ezan/$dosya.m4a").use { true }
                } catch (_: IOException) { false }
                if (hazir && VakitAyarlari.ezanAcik(context, vakit) && System.currentTimeMillis() - zaman < 60_000L) {
                    try { EzanServisi.baslat(context, vakit) } catch (e: IllegalStateException) {
                        android.util.Log.w("EzanAlarmAlici", "Sistem ses hizmetini başlatmadı", e)
                    }
                }
                // Sonraki alarmlar ve geri sayım
                val k = VakitAyarlari.konum(context)
                EzanZamanlayici.kur(context, k.enlem, k.boylam)
            }
        }
    }
}

/** Telefon açılınca veya "Alarmlar ve hatırlatıcılar" izni verilince alarmları yeniden kurar. */
class AcilisAlici : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val k = VakitAyarlari.konum(context)
        EzanZamanlayici.kur(context, k.enlem, k.boylam)
    }
}
