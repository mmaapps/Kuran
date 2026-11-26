package com.atasoyyazilim.kurankerim

import android.app.Application
import com.atasoyyazilim.kurankerim.bildirim.Bildirimler
import com.atasoyyazilim.kurankerim.vakit.EzanZamanlayici
import com.atasoyyazilim.kurankerim.vakit.VakitAyarlari

class KuranUygulamasi : Application() {
    override fun onCreate() {
        super.onCreate()
        Bildirimler.kanallariOlustur(this)
        // Uygulama her açıldığında sıradaki ezan alarmları ve geri sayım bildirimi yenilenir.
        val konum = VakitAyarlari.konum(this)
        EzanZamanlayici.kur(this, konum.enlem, konum.boylam)
    }
}
