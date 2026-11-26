package com.atasoyyazilim.kurankerim.ses

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import com.atasoyyazilim.kurankerim.bildirim.Bildirimler
import com.atasoyyazilim.kurankerim.vakit.Vakit
import java.io.IOException

/**
 * Ezanı ekran kapalıyken çalan ön plan hizmeti (EzanAni.dc.html, Bildirim.dc.html "Ezan okunurken").
 * Ses: assets/ses/ezan/<vakit>.m4a — dosya yoksa (izin bekleniyor) sadece bildirim gösterilir.
 */
class EzanServisi : Service() {

    private var oynatici: MediaPlayer? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == EYLEM_SUSTUR) {
            durdur()
            return START_NOT_STICKY
        }
        val vakit = intent?.getStringExtra(EK_VAKIT)
            ?.let { ad -> Vakit.entries.firstOrNull { it.name == ad } }
            ?: Vakit.AKSAM

        val bildirim = Bildirimler.ezanBildirimi(this, vakit)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(Bildirimler.ID_EZAN, bildirim, ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK)
        } else {
            startForeground(Bildirimler.ID_EZAN, bildirim)
        }
        cal(vakit)
        return START_NOT_STICKY
    }

    private fun cal(vakit: Vakit) {
        val dosya = vakit.sesDosyasi ?: run { durdur(); return }
        oynatici?.release()
        val mp = MediaPlayer()
        try {
            assets.openFd("ses/ezan/$dosya.m4a").use { fd ->
                mp.setDataSource(fd.fileDescriptor, fd.startOffset, fd.length)
            }
            mp.setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build()
            )
            mp.setOnCompletionListener { ezanBitti() }
            mp.prepare()
            mp.start()
            oynatici = mp
        } catch (e: IOException) {
            // Ezan kaydı henüz eklenmedi (IZIN_TAKIP_TABLOSU satır 12). Bildirim kalır, ses çalınmaz.
            mp.release()
            durdur()
        }
    }

    private fun ezanBitti() {
        // TODO(EzanAni.dc.html — 2 · Kısa âyet):
        //  VakitAyarlari.ezanSonrasiAyetAcik ise IcerikDeposu.ezanSonrasiAyetler() listesinden sıradaki âyetin
        //  tilâveti çalınır: asset:///tilavet/SSSAAA.opus (izinli hâfız kaydı). Arapça ASLA TTS ile okunmaz.
        //  MEAL OKUNMAZ (uygulamada meal yok).
        durdur()
    }

    private fun durdur() {
        oynatici?.release()
        oynatici = null
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        oynatici?.release()
        oynatici = null
        super.onDestroy()
    }

    companion object {
        const val EYLEM_SUSTUR = "com.atasoyyazilim.kurankerim.EZAN_SUSTUR"
        private const val EK_VAKIT = "vakit"

        fun baslat(context: Context, vakit: Vakit) {
            val niyet = Intent(context, EzanServisi::class.java).putExtra(EK_VAKIT, vakit.name)
            context.startForegroundService(niyet)
        }
    }
}
