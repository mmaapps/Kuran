package com.atasoyyazilim.kurankerim.vakit

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.location.Geocoder
import android.os.Looper
import androidx.core.content.ContextCompat
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.coroutineScope
import java.util.Locale
import kotlin.math.round

/** Yalnız ön planda; izin verilmeyen konum ağa gönderilmez. */
object KonumTakip {
    fun izin(c:Context)=ContextCompat.checkSelfPermission(c,Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED
    private fun konumlar(c:Context)=callbackFlow<Location>{
        val lm=c.getSystemService(LocationManager::class.java)
        val listener=LocationListener{trySend(it)}
        listOf(LocationManager.NETWORK_PROVIDER,LocationManager.GPS_PROVIDER).forEach{p->
            runCatching{
                if(lm.isProviderEnabled(p)){
                    lm.requestLocationUpdates(p,300_000L,5000f,listener,Looper.getMainLooper())
                    lm.getLastKnownLocation(p)?.takeIf{System.currentTimeMillis()-it.time in 0..1_200_000L}?.let{trySend(it)}
                }
            }
        }
        awaitClose{lm.removeUpdates(listener)}
    }.conflate()

    suspend fun kaydet(c:Context,l:Location){
        if(!izin(c)||!l.latitude.isFinite()||!l.longitude.isFinite()||l.latitude !in -90.0..90.0||l.longitude !in -180.0..180.0)return
        // 0.1 derece: yaklaşık bölgesel koordinat; kesin konum sunucuya gönderilmez.
        val enlem=round(l.latitude*10)/10;val boylam=round(l.longitude*10)/10
        val once=VakitAyarlari.konum(c)
        if(once.enlem!=enlem||once.boylam!=boylam||!VakitAyarlari.konumBelirlendi(c)){
            val ad=withContext(Dispatchers.IO){
                @Suppress("DEPRECATION")
                runCatching{Geocoder(c,Locale("tr","TR")).getFromLocation(enlem,boylam,1)?.firstOrNull()
                    ?.let{listOfNotNull(it.subAdminArea?:it.locality,it.adminArea).distinct().joinToString(", ")}}
                    .getOrNull()?.takeIf{it.isNotBlank()} ?: c.getString(com.atasoyyazilim.kurankerim.R.string.vakit_otomatik_konum)
            }
            if(!izin(c))return
            VakitAyarlari.konumKaydet(c,Konum(enlem,boylam,ad))
            EzanZamanlayici.kur(c,enlem,boylam)
        }
    }
    suspend fun izle(c:Context)=coroutineScope {
        if(!izin(c))return@coroutineScope
        launch { konumlar(c).collect { kaydet(c,it) } }
        launch {
            while(true){
                if(izin(c)&&VakitAyarlari.konumBelirlendi(c)){
                    val k=VakitAyarlari.konum(c)
                    if(CevrimiciVakitler.yenile(c,k)){
                        VakitAyarlari.yenilendi()
                        EzanZamanlayici.kur(c,k.enlem,k.boylam)
                    }
                }
                delay(60_000)
            }
        }
    }
}

@Composable
fun KonumIzleme(){
    val c=LocalContext.current.applicationContext
    val lifecycle=LocalLifecycleOwner.current.lifecycle
    val rev by VakitAyarlari.izinDegisimi.collectAsState()
    LaunchedEffect(lifecycle,rev){
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
            if(KonumTakip.izin(c))KonumTakip.izle(c)
        }
    }
}
