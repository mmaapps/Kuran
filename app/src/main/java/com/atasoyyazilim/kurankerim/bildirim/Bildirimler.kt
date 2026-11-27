package com.atasoyyazilim.kurankerim.bildirim

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.atasoyyazilim.kurankerim.MainActivity
import com.atasoyyazilim.kurankerim.R
import com.atasoyyazilim.kurankerim.ses.EzanServisi
import com.atasoyyazilim.kurankerim.vakit.Vakit
import com.atasoyyazilim.kurankerim.vakit.VakitAyarlari
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/** Bildirim.dc.html'deki üç bildirim: geri sayım, 5 dakika kaldı, ezan okunuyor. */
object Bildirimler {
    const val KANAL_EZAN = "ezan"
    const val KANAL_HATIRLATMA = "hatirlatma"
    const val KANAL_GERI_SAYIM = "geri_sayim"

    const val ID_EZAN = 1
    const val ID_HATIRLATMA = 2
    const val ID_GERI_SAYIM = 3

    fun kanallariOlustur(context: Context) {
        val nm = context.getSystemService(NotificationManager::class.java)
        nm.createNotificationChannels(
            listOf(
                NotificationChannel(
                    KANAL_EZAN, context.getString(R.string.kanal_ezan), NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = context.getString(R.string.kanal_ezan_aciklama)
                    setSound(null, null) // ezan sesini EzanServisi çalar
                },
                NotificationChannel(
                    KANAL_HATIRLATMA, context.getString(R.string.kanal_hatirlatma), NotificationManager.IMPORTANCE_HIGH
                ).apply { description = context.getString(R.string.kanal_hatirlatma_aciklama) },
                NotificationChannel(
                    KANAL_GERI_SAYIM, context.getString(R.string.kanal_geri_sayim), NotificationManager.IMPORTANCE_LOW
                ).apply {
                    description = context.getString(R.string.kanal_geri_sayim_aciklama)
                    setShowBadge(false)
                }
            )
        )
    }

    private fun izinVar(context: Context): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED

    private fun uygulamayiAc(context: Context): PendingIntent = PendingIntent.getActivity(
        context, 0,
        Intent(context, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP),
        PendingIntent.FLAG_IMMUTABLE
    )

    private fun saat(zaman: Instant): String =
        DateTimeFormatter.ofPattern("HH:mm").withZone(ZoneId.systemDefault()).format(zaman)

    fun geriSayimKapat(context: Context) = context.getSystemService(NotificationManager::class.java).cancel(ID_GERI_SAYIM)

    /** "Sıradaki: Akşam · 16:52" + canlı geri sayım (sistem kronometresi; servis gerekmez). */
    @SuppressLint("MissingPermission")
    fun geriSayimGoster(context: Context, vakit: Vakit, zaman: Instant) {
        if (!izinVar(context) || !VakitAyarlari.geriSayimAcik(context)) return
        val b = NotificationCompat.Builder(context, KANAL_GERI_SAYIM)
            .setSmallIcon(R.drawable.ic_launcher_monochrome)
            .setContentTitle(context.getString(R.string.geri_sayim_baslik, context.getString(vakit.adRes), saat(zaman)))
            .setWhen(zaman.toEpochMilli())
            .setShowWhen(true)
            .setUsesChronometer(true)
            .setChronometerCountDown(true)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setSilent(true)
            .setContentIntent(uygulamayiAc(context))
            .build()
        NotificationManagerCompat.from(context).notify(ID_GERI_SAYIM, b)
    }

    /** "Akşam ezanına 5 dakika kaldı" */
    @SuppressLint("MissingPermission")
    fun hatirlatmaGoster(context: Context, vakit: Vakit) {
        if (!izinVar(context)) return
        val b = NotificationCompat.Builder(context, KANAL_HATIRLATMA)
            .setSmallIcon(R.drawable.ic_launcher_monochrome)
            .setContentTitle(context.getString(R.string.hatirlatma_baslik, context.getString(vakit.adRes)))
            .setContentText(context.getString(R.string.hatirlatma_metin))
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .setContentIntent(uygulamayiAc(context))
            .build()
        NotificationManagerCompat.from(context).notify(ID_HATIRLATMA, b)
    }

    /** EzanServisi'nin ön plan bildirimi: "Akşam ezanı okunuyor" + Sustur. */
    fun ezanBildirimi(context: Context, vakit: Vakit): Notification {
        val sustur = PendingIntent.getService(
            context, 1,
            Intent(context, EzanServisi::class.java).setAction(EzanServisi.EYLEM_SUSTUR),
            PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(context, KANAL_EZAN)
            .setSmallIcon(R.drawable.ic_launcher_monochrome)
            .setContentTitle(context.getString(R.string.ezan_okunuyor, context.getString(vakit.adRes)))
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setOngoing(true)
            .setContentIntent(PendingIntent.getActivity(context,4,
                Intent(context,MainActivity::class.java).setAction(EzanServisi.EYLEM_AC)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP),
                PendingIntent.FLAG_IMMUTABLE))
            .addAction(0, context.getString(R.string.ezan_sustur), sustur)
            .build()
    }
}
