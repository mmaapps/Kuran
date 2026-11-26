package com.atasoyyazilim.kurankerim.vakit

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.atasoyyazilim.kurankerim.bildirim.Bildirimler
import com.atasoyyazilim.kurankerim.ses.EzanServisi

/** Ezan ve "5 dakika kaldı" alarmlarını karşılar. */
class EzanAlarmAlici : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val vakit = intent.getStringExtra(EzanZamanlayici.EK_VAKIT)
            ?.let { ad -> Vakit.entries.firstOrNull { it.name == ad } }
            ?: return

        when (intent.getStringExtra(EzanZamanlayici.EK_TUR)) {
            EzanZamanlayici.TUR_HATIRLAT -> Bildirimler.hatirlatmaGoster(context, vakit)
            EzanZamanlayici.TUR_EZAN -> {
                if (VakitAyarlari.ezanAcik(context, vakit)) EzanServisi.baslat(context, vakit)
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
