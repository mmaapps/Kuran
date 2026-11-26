package com.atasoyyazilim.kurankerim.vakit

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.atasoyyazilim.kurankerim.bildirim.Bildirimler
import com.atasoyyazilim.kurankerim.ses.EzanServisi
import java.io.IOException
import android.media.AudioManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

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
                val audio = context.getSystemService(AudioManager::class.java)
                val karar = EzanDavranisi.karar(
                    VakitAyarlari.ezanAcik(context, vakit),
                    hazir,
                    audio.ringerMode != AudioManager.RINGER_MODE_NORMAL,
                    VakitAyarlari.sessizdeTitret(context),
                    System.currentTimeMillis() - zaman
                )
                when(karar) {
                    EzanDavranisi.Eylem.TITRET -> titret(context)
                    EzanDavranisi.Eylem.SES -> try { EzanServisi.baslat(context, vakit) } catch (e: IllegalStateException) {
                        android.util.Log.w("EzanAlarmAlici", "Sistem ses hizmetini başlatmadı", e)
                    }
                    EzanDavranisi.Eylem.YOK -> Unit
                }
                // Sonraki alarmlar ve geri sayım
                val k = VakitAyarlari.konum(context)
                EzanZamanlayici.kur(context, k.enlem, k.boylam)
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun titret(context: Context) {
        try {
            val vibrator = if(Build.VERSION.SDK_INT >= 31)
                context.getSystemService(VibratorManager::class.java).defaultVibrator
            else context.getSystemService(Vibrator::class.java)
            if(vibrator.hasVibrator()) vibrator.vibrate(VibrationEffect.createOneShot(600L,VibrationEffect.DEFAULT_AMPLITUDE))
        } catch(e: SecurityException) {
            android.util.Log.w("EzanAlarmAlici","Titreşim sistem tarafından engellendi",e)
        }
    }
}

/** Telefon açılınca veya "Alarmlar ve hatırlatıcılar" izni verilince alarmları yeniden kurar. */
class AcilisAlici : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if(intent.action !in setOf(Intent.ACTION_BOOT_COMPLETED,Intent.ACTION_MY_PACKAGE_REPLACED,
            Intent.ACTION_TIME_CHANGED,Intent.ACTION_TIMEZONE_CHANGED,
            "android.app.action.SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED")) return
        val k = VakitAyarlari.konum(context)
        EzanZamanlayici.kur(context, k.enlem, k.boylam)
    }
}
