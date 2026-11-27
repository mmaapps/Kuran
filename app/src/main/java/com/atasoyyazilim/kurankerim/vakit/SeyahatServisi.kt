package com.atasoyyazilim.kurankerim.vakit

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.atasoyyazilim.kurankerim.MainActivity
import com.atasoyyazilim.kurankerim.R
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Kullanıcının görünür ekrandan başlattığı, durdurulabilir seyahat modu.
 * Arka plan konum izni istemez; Android'in konum türünde ön plan hizmetini kullanır.
 * Yeniden başlatma, boot veya güncelleme sonrasında kendiliğinden başlamaz.
 */
class SeyahatServisi : Service() {
    private val scope=CoroutineScope(SupervisorJob()+Dispatchers.Main.immediate)
    private var islem:Job?=null
    override fun onBind(intent:Intent?):IBinder?=null
    override fun onStartCommand(intent:Intent?,flags:Int,startId:Int):Int {
        if(intent?.action!=BASLAT){durdur();return START_NOT_STICKY}
        if(islem?.isActive==true)return START_NOT_STICKY
        if(!KonumTakip.izin(this)||!bildirimIzni(this)){_hata.value=true;durdur();return START_NOT_STICKY}
        try {
            val nm=getSystemService(NotificationManager::class.java)
            nm.createNotificationChannel(NotificationChannel(KANAL,getString(R.string.seyahat_baslik),NotificationManager.IMPORTANCE_LOW).apply{
                setShowBadge(false);setSound(null,null)
            })
            if(nm.getNotificationChannel(KANAL).importance==NotificationManager.IMPORTANCE_NONE){
                _hata.value=true;durdur();return START_NOT_STICKY
            }
            val ac=PendingIntent.getActivity(this,40,Intent(this,MainActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP),PendingIntent.FLAG_IMMUTABLE)
            val stop=PendingIntent.getService(this,41,Intent(this,SeyahatServisi::class.java).setAction(DURDUR),PendingIntent.FLAG_IMMUTABLE)
            val n=NotificationCompat.Builder(this,KANAL)
                .setSmallIcon(R.drawable.ic_launcher_monochrome)
                .setContentTitle(getString(R.string.seyahat_aktif))
                .setContentText(getString(R.string.seyahat_bildirim))
                .setOngoing(true).setOnlyAlertOnce(true).setSilent(true).setContentIntent(ac)
                .addAction(0,getString(R.string.seyahat_kapat),stop).build()
            if(Build.VERSION.SDK_INT>=29)startForeground(ID,n,ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION)
            else startForeground(ID,n)
        } catch(e:RuntimeException){
            android.util.Log.w("SeyahatServisi","Konum hizmeti başlatılamadı",e)
            _hata.value=true;durdur();return START_NOT_STICKY
        }
        _hata.value=false;_aktif.value=true
        islem=scope.launch {
            try {
                coroutineScope {
                    launch { KonumTakip.izle(applicationContext) }
                    launch {
                        while(isActive){
                            if(!KonumTakip.izin(applicationContext)||!bildirimIzni(applicationContext)){
                                this@SeyahatServisi.durdur();return@launch
                            }
                            delay(15_000L)
                        }
                    }
                }
            }catch(e:CancellationException){throw e}
            catch(e:Exception){_hata.value=true;durdur()}
        }
        return START_NOT_STICKY
    }
    private fun durdur(){
        islem?.cancel();islem=null;_aktif.value=false
        stopForeground(STOP_FOREGROUND_REMOVE);stopSelf()
    }
    override fun onDestroy(){
        scope.cancel();_aktif.value=false
        super.onDestroy()
    }
    companion object {
        private const val BASLAT="com.atasoyyazilim.kurankerim.SEYAHAT_BASLAT"
        private const val DURDUR="com.atasoyyazilim.kurankerim.SEYAHAT_DURDUR"
        private const val KANAL="seyahat_konum"
        private const val ID=4
        private val _aktif=MutableStateFlow(false)
        val aktif=_aktif.asStateFlow()
        private val _hata=MutableStateFlow(false)
        val hata=_hata.asStateFlow()
        fun bildirimIzni(c:Context):Boolean {
            val nm=c.getSystemService(NotificationManager::class.java)
            return (Build.VERSION.SDK_INT<33||ContextCompat.checkSelfPermission(c,Manifest.permission.POST_NOTIFICATIONS)==PackageManager.PERMISSION_GRANTED)
                && nm.areNotificationsEnabled()
                && nm.getNotificationChannel(KANAL)?.importance!=NotificationManager.IMPORTANCE_NONE
        }
        fun baslat(c:Context){
            _hata.value=false
            try {
                if(!KonumTakip.izin(c)||!bildirimIzni(c)){_hata.value=true;return}
                ContextCompat.startForegroundService(c,Intent(c,SeyahatServisi::class.java).setAction(BASLAT))
            }catch(e:RuntimeException){_hata.value=true}
        }
        fun kapat(c:Context){c.stopService(Intent(c,SeyahatServisi::class.java));_hata.value=false}
    }
}
