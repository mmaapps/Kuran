package com.atasoyyazilim.kurankerim.vakit

import com.batoulapps.adhan.CalculationMethod
import com.batoulapps.adhan.CalculationParameters
import com.batoulapps.adhan.Coordinates
import com.batoulapps.adhan.Madhab
import com.batoulapps.adhan.PrayerTimes
import com.batoulapps.adhan.Qibla
import com.batoulapps.adhan.data.DateComponents
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.chrono.HijrahDate
import java.time.temporal.ChronoField

/**
 * Namaz vakitleri — internetsiz, cihazda hesaplanır (Adhan kütüphanesi, MIT).
 * Türkiye yöntemi: imsak 18°, yatsı 17°, ikindi = asr-ı evvel (Şâfiî gölge),
 * temkin düzeltmeleri: güneş −7, öğle +5, ikindi +4, akşam +7 dakika.
 * Sonuçlar Diyanet'in yayımladığı takvimden birkaç dakika farklı olabilir (uygulamada yazılı olmalı).
 */
object VakitHesaplayici {

    private fun turkiyeParametreleri(): CalculationParameters =
        CalculationMethod.OTHER.parameters.apply {
            fajrAngle = 18.0
            ishaAngle = 17.0
            madhab = Madhab.SHAFI
            methodAdjustments.sunrise = -7
            methodAdjustments.dhuhr = 5
            methodAdjustments.asr = 4
            methodAdjustments.maghrib = 7
        }

    fun gunluk(enlem: Double, boylam: Double, tarih: LocalDate): Map<Vakit, Instant> {
        val pt = PrayerTimes(
            Coordinates(enlem, boylam),
            DateComponents(tarih.year, tarih.monthValue, tarih.dayOfMonth),
            turkiyeParametreleri()
        )
        return linkedMapOf(
            Vakit.IMSAK to pt.fajr.toInstant(),
            Vakit.GUNES to pt.sunrise.toInstant(),
            Vakit.OGLE to pt.dhuhr.toInstant(),
            Vakit.IKINDI to pt.asr.toInstant(),
            Vakit.AKSAM to pt.maghrib.toInstant(),
            Vakit.YATSI to pt.isha.toInstant()
        )
    }

    /** Bugün ve yarının vakitleri, zaman sırasıyla. */
    fun ikiGunluk(enlem: Double, boylam: Double, bolge: ZoneId = ZoneId.systemDefault()): List<Pair<Vakit, Instant>> {
        val bugun = LocalDate.now(bolge)
        return (gunluk(enlem, boylam, bugun).toList() + gunluk(enlem, boylam, bugun.plusDays(1)).toList())
    }

    // Sıradaki ezan için EzanZamanlayici.siradakiEzan(context) kullanılır (sabah ezanı gecikmesini hesaba katar).

    /** Kıble açısı (kuzeyden saat yönünde derece). */
    fun kibleAcisi(enlem: Double, boylam: Double): Double = Qibla(Coordinates(enlem, boylam)).direction
}

/** Hicri tarih — Ayarlar'dan ±1 gün düzeltme verilebilir (Diyanet takvimiyle eşlemek için). */
object HicriTakvim {
    private val aylar = listOf(
        "Muharrem", "Safer", "Rebiülevvel", "Rebiülâhir", "Cemâziyelevvel", "Cemâziyelâhir",
        "Recep", "Şâban", "Ramazan", "Şevval", "Zilkade", "Zilhicce"
    )

    fun metin(tarih: LocalDate, duzeltmeGun: Long = 0L): String {
        val h = HijrahDate.from(tarih.plusDays(duzeltmeGun))
        val gun = h.get(ChronoField.DAY_OF_MONTH)
        val ay = h.get(ChronoField.MONTH_OF_YEAR)
        val yil = h.get(ChronoField.YEAR)
        return "$gun ${aylar[ay - 1]} $yil"
    }
}
