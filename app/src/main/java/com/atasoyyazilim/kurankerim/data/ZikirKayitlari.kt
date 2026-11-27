package com.atasoyyazilim.kurankerim.data

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import java.time.LocalDate

private val Context.zikirDepo by preferencesDataStore(name="zikirmatik")
private val secKey=intPreferencesKey("secili")
private val hedefKey=intPreferencesKey("hedef")
private val sayacKey=List(5){longPreferencesKey("sayac_$it")}
private val gunKey=stringPreferencesKey("gun")
private val toplamKey=longPreferencesKey("toplam")
private val turKey=longPreferencesKey("tur")

/** İşlemler tek kuyrukta, her dokunuş ayrı atomik DataStore işlemi. */
object ZikirKayitlari {
    private val scope=CoroutineScope(SupervisorJob()+Dispatchers.IO)
    private data class Is(val c:Context,val eylem:(ZikirDurumu)->Pair<ZikirDurumu,Long>)
    private val kuyruk=Channel<Is>(Channel.UNLIMITED)
    init {
        scope.launch {
            for(islem in kuyruk){
                try {
                    var titre=0L
                    islem.c.zikirDepo.edit{p->
                        val (d,ms)=islem.eylem(oku(p).gun(LocalDate.now().toString()))
                        p[secKey]=d.secili;p[hedefKey]=d.hedef
                        sayacKey.forEachIndexed{i,k->p[k]=d.sayaclar[i]}
                        p[gunKey]=d.tarih;p[toplamKey]=d.toplam;p[turKey]=d.tur
                        titre=ms
                    }
                    if(titre>0)titret(islem.c,titre)
                }catch(e:CancellationException){throw e}
                catch(e:Exception){android.util.Log.e("ZikirKayitlari","Sayaç kaydedilemedi",e)}
            }
        }
    }
    private fun oku(p:Preferences)=ZikirDurumu(
        secili=(p[secKey]?:0).coerceIn(0,4),
        hedef=(p[hedefKey]?:33).takeIf{it in ZikirDurumu.HEDEFLER}?:33,
        sayaclar=sayacKey.map{(p[it]?:0L).coerceAtLeast(0)},
        tarih=p[gunKey].orEmpty(),toplam=(p[toplamKey]?:0L).coerceAtLeast(0),tur=(p[turKey]?:0L).coerceAtLeast(0))
    fun izle(c:Context):Flow<ZikirDurumu> = combine(
        c.applicationContext.zikirDepo.data.map(::oku),
        flow{while(currentCoroutineContext().isActive){emit(LocalDate.now().toString());delay(1000)}}
    ){d,gun->d.gun(gun)}
    fun say(c:Context)=gonder(c){it.ekle(LocalDate.now().toString())}
    fun sec(c:Context,id:Int)=gonder(c){it.sec(id) to 0L}
    fun hedef(c:Context,n:Int)=gonder(c){it.hedefSec(n) to 0L}
    fun sifirla(c:Context)=gonder(c){it.sifirla() to 0L}
    private fun gonder(c:Context,f:(ZikirDurumu)->Pair<ZikirDurumu,Long>){kuyruk.trySend(Is(c.applicationContext,f))}
    @Suppress("DEPRECATION")
    private fun titret(c:Context,ms:Long){
        runCatching{
            val v=if(Build.VERSION.SDK_INT>=31)c.getSystemService(VibratorManager::class.java).defaultVibrator else c.getSystemService(Vibrator::class.java)
            if(v.hasVibrator())v.vibrate(VibrationEffect.createOneShot(ms,VibrationEffect.DEFAULT_AMPLITUDE))
        }
    }
}
