package com.atasoyyazilim.kurankerim.vakit

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.atasoyyazilim.kurankerim.bildirim.Bildirimler
import java.time.Instant
import java.time.LocalDate

/**
 * Ezan ve "5 dakika kaldı" alarmlarını kurar.
 * - Tam zamanlı alarm için SCHEDULE_EXACT_ALARM (kullanıcı "Alarmlar ve hatırlatıcılar" iznini verir).
 *   İzin yoksa gecikmesi sistem tarafından belirlenen yaklaşık alarm kurulur; uygulama içinde izin istenir (açıklamayla).
 * - USE_EXACT_ALARM KULLANILMAZ (Google Play sadece alarm/saat/takvim uygulamalarına izin veriyor).
 * - Her ezandan sonra ve telefon açılınca yeniden çağrılır.
 */
object EzanZamanlayici {
    const val TUR_EZAN = "ezan"
    const val TUR_HATIRLAT = "hatirlat"
    const val EK_TUR = "tur"
    const val EK_VAKIT = "vakit"
    const val EK_ZAMAN = "zaman"

    fun tamZamanliIzinVar(context: Context): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.S ||
            context.getSystemService(AlarmManager::class.java).canScheduleExactAlarms()

    /** Vaktin EZAN zamanı: sabah ezanı imsaktan kullanıcının seçtiği dakika kadar sonra, diğerleri vakitte. */
    fun ezanZamani(context: Context, vakit: Vakit, vakitZamani: Instant): Instant =
        if (vakit == Vakit.IMSAK) {
            vakitZamani.plusSeconds(VakitAyarlari.sabahEzaniGecikmeDk(context) * 60L)
        } else {
            vakitZamani
        }

    /** Sıradaki ezanlar (Güneş hariç), ezan zamanına göre sıralı. */
    fun siradakiEzanlar(context: Context, enlem: Double, boylam: Double, simdi: Instant = Instant.now()): List<Pair<Vakit, Instant>> =
        listOf(LocalDate.now().minusDays(1),LocalDate.now(),LocalDate.now().plusDays(1))
            .flatMap{VakitHesaplayici.gunluk(context,enlem,boylam,it).toList()}
            .filter { it.first != Vakit.GUNES }
            .map { (vakit, zaman) -> vakit to ezanZamani(context, vakit, zaman) }
            .filter { it.second.isAfter(simdi) }
            .sortedBy { it.second }

    /** Ana sayfa geri sayımı ve bildirim bunu kullanır (Main.dc.html "… ezanına kalan"). */
    fun siradakiEzan(context: Context): Pair<Vakit, Instant>? {
        val k = VakitAyarlari.konum(context)
        return siradakiEzanlar(context, k.enlem, k.boylam).firstOrNull()
    }

    fun kur(context: Context, enlem: Double, boylam: Double) {
        val am = context.getSystemService(AlarmManager::class.java)
        val simdi = Instant.now()
        val siradakiler = siradakiEzanlar(context, enlem, boylam, simdi).take(5)

        // Önce eski planı iptal et; kapatılan veya taşınan alarmlar kalmasın.
        for (kod in (100..104).toList() + (200..204).toList()) {
            val pi = PendingIntent.getBroadcast(context, kod,
                Intent(context, EzanAlarmAlici::class.java),
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE)
            if (pi != null) { am.cancel(pi); pi.cancel() }
        }
        siradakiler.forEachIndexed { i, (vakit, zaman) ->
            // Ses kapalı olsa da sonraki geri sayım/plan yenilenmeli.
            alarmKur(context, am, zaman.toEpochMilli(), TUR_EZAN, vakit, 100 + i)
            val hatirlat = zaman.minusSeconds(5 * 60)
            if (VakitAyarlari.hatirlatmaAcik(context) && hatirlat.isAfter(simdi)) {
                alarmKur(context, am, hatirlat.toEpochMilli(), TUR_HATIRLAT, vakit, 200 + i)
            }
        }

        // Bildirim çubuğunda sıradaki ezana geri sayım (Bildirim.dc.html)
        if (!VakitAyarlari.geriSayimAcik(context) || siradakiler.isEmpty()) Bildirimler.geriSayimKapat(context)
        else siradakiler.firstOrNull()?.let { (vakit, zaman) -> Bildirimler.geriSayimGoster(context, vakit, zaman) }
    }

    private fun alarmKur(
        context: Context,
        am: AlarmManager,
        zamanMs: Long,
        tur: String,
        vakit: Vakit,
        istekKodu: Int
    ) {
        val niyet = Intent(context, EzanAlarmAlici::class.java)
            .putExtra(EK_TUR, tur)
            .putExtra(EK_VAKIT, vakit.name)
            .putExtra(EK_ZAMAN, zamanMs)
        val pi = PendingIntent.getBroadcast(
            context, istekKodu, niyet,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        if (tamZamanliIzinVar(context)) {
            try {
                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, zamanMs, pi)
                return
            } catch (_: SecurityException) {
                // İzin kontrolü ile kurulum arasında izin kaldırılmış olabilir.
            }
        }
        am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, zamanMs, pi)
    }
}
