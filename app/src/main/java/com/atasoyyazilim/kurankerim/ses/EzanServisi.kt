package com.atasoyyazilim.kurankerim.ses

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import com.atasoyyazilim.kurankerim.bildirim.Bildirimler
import com.atasoyyazilim.kurankerim.data.IcerikDeposu
import com.atasoyyazilim.kurankerim.vakit.Vakit
import com.atasoyyazilim.kurankerim.vakit.VakitAyarlari
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.Instant
import java.util.Locale

data class EzanOynatma(val oturum:Long, val vakit:Vakit, val zaman:Instant, val sehir:String,
    val caliyor:Boolean, val sure:Int?=null, val ayet:Int?=null)

/** Yalnız paket içindeki gerçek kayıtlar. Önizleme metni/sesi oynatma durumu olarak kullanılmaz. */
class EzanServisi : Service() {
    private var oynatici:MediaPlayer?=null
    private var kuyruk:List<Pair<Int,Int>> = emptyList()
    private var sira=0
    private var oturum: EzanOynatma?=null

    override fun onBind(intent:Intent?):IBinder?=null

    override fun onStartCommand(intent:Intent?,flags:Int,startId:Int):Int {
        when(intent?.action) {
            EYLEM_SUSTUR -> { durdur();return START_NOT_STICKY }
            EYLEM_DURAKLAT -> { degistir();return START_NOT_STICKY }
        }
        val v=intent?.getStringExtra(EK_VAKIT)?.let { ad -> Vakit.entries.firstOrNull { it.name==ad } }
            ?: run { durdur();return START_NOT_STICKY }
        val dosya=v.sesDosyasi ?: run { durdur();return START_NOT_STICKY }
        val n=Bildirimler.ezanBildirimi(this,v)
        if(Build.VERSION.SDK_INT>=29) startForeground(Bildirimler.ID_EZAN,n,ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK)
        else startForeground(Bildirimler.ID_EZAN,n)
        kuyruk=emptyList();sira=0
        oturum=EzanOynatma(System.nanoTime(),v,Instant.now(),VakitAyarlari.konum(this).ad,false)
        if(!oynat("ses/ezan/$dosya.m4a",null,null)) durdur()
        return START_NOT_STICKY
    }

    private fun oynat(yol:String,sure:Int?,ayet:Int?):Boolean {
        if(getSystemService(AudioManager::class.java).ringerMode!=AudioManager.RINGER_MODE_NORMAL) return false
        oynatici?.release();oynatici=null
        val mp=MediaPlayer()
        return try {
            assets.openFd(yol).use { fd -> mp.setDataSource(fd.fileDescriptor,fd.startOffset,fd.length) }
            mp.setAudioAttributes(AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH).build())
            mp.setOnErrorListener { _,_,_ -> durdur();true }
            mp.setOnCompletionListener { if(sure==null) ezanBitti() else sonrakiAyet() }
            mp.prepare()
            oynatici=mp
            mp.start()
            oturum=oturum?.copy(caliyor=true,sure=sure,ayet=ayet)
            _durum.value=oturum
            true
        } catch(e:Exception) {
            if(oynatici===mp)oynatici=null
            mp.release()
            android.util.Log.w("EzanServisi","Paket içi ses oynatılamadı",e)
            false
        }
    }

    private fun ayetDosyasi(s:Int,a:Int)=String.format(Locale.ROOT,"tilavet/%03d%03d.opus",s,a)
    private fun ezanBitti() {
        if(!VakitAyarlari.ezanSonrasiAyetAcik(this)){durdur();return}
        // Dosyası eksik olan aralık asla yarım oynatılmaz. Yalnız mevcut içerik listesinden.
        val liste=runCatching { IcerikDeposu(this).ezanSonrasiAyetler() }.getOrDefault(emptyList())
        val secim=liste.firstOrNull { ar ->
            ar.ayetler.isNotEmpty() && ar.ayetler.all { a ->
                runCatching { assets.openFd(ayetDosyasi(ar.sure,a)).use {} }.isSuccess
            }
        } ?: run { durdur();return }
        kuyruk=secim.ayetler.map { secim.sure to it };sira=0
        sonrakiAyet()
    }
    private fun sonrakiAyet() {
        val (s,a)=kuyruk.getOrNull(sira++) ?: run { durdur();return }
        if(!oynat(ayetDosyasi(s,a),s,a))durdur()
    }
    private fun degistir() {
        val mp=oynatici ?: return
        try {
            if(mp.isPlaying)mp.pause()
            else if(getSystemService(AudioManager::class.java).ringerMode==AudioManager.RINGER_MODE_NORMAL)mp.start()
            oturum=oturum?.copy(caliyor=mp.isPlaying);_durum.value=oturum
        } catch(e:IllegalStateException){durdur()}
    }
    private fun temizle() {
        oynatici?.release();oynatici=null;oturum=null;_durum.value=null
        kuyruk=emptyList();sira=0
    }
    private fun durdur(){temizle();stopForeground(STOP_FOREGROUND_REMOVE);stopSelf()}
    override fun onDestroy(){temizle();super.onDestroy()}

    companion object {
        const val EYLEM_SUSTUR="com.atasoyyazilim.kurankerim.EZAN_SUSTUR"
        const val EYLEM_DURAKLAT="com.atasoyyazilim.kurankerim.EZAN_DURAKLAT"
        const val EYLEM_AC="com.atasoyyazilim.kurankerim.EZAN_AC"
        private const val EK_VAKIT="vakit"
        private val _durum=MutableStateFlow<EzanOynatma?>(null)
        val durum=_durum.asStateFlow()
        fun baslat(context:Context,vakit:Vakit) {
            context.startForegroundService(Intent(context,EzanServisi::class.java).putExtra(EK_VAKIT,vakit.name))
        }
        fun komut(context:Context,eylem:String) {
            if(durum.value!=null)context.startService(Intent(context,EzanServisi::class.java).setAction(eylem))
        }
    }
}
