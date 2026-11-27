package com.atasoyyazilim.kurankerim.vakit

import android.content.Context
import org.json.JSONObject
import java.net.URL
import java.net.HttpURLConnection
import java.net.URLEncoder
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext

/** AlAdhan Türkiye hesap yöntemi; resmî Diyanet takvimi değildir. Yaklaşık koordinatlar. */
object CevrimiciVakitler {
    private fun prefs(c:Context)=c.getSharedPreferences("cevrimici_vakitler",Context.MODE_PRIVATE)
    private fun anahtar(k:Konum,zone:ZoneId)="${k.enlem},${k.boylam}|${zone.id}"
    private val alanlar=listOf("Fajr","Sunrise","Dhuhr","Asr","Maghrib","Isha")
    private val format=DateTimeFormatter.ofPattern("dd-MM-uuuu",Locale.ROOT)
    fun oku(c:Context,k:Konum,tarih:LocalDate,zone:ZoneId=ZoneId.systemDefault()):Map<Vakit,Instant>? = runCatching {
        val p=prefs(c)
        if(p.getString("konum",null)!=anahtar(k,zone))return@runCatching null
        val kayit=JSONObject(p.getString("takvim","{}") ?: "{}").optJSONObject(tarih.toString()) ?: return@runCatching null
        Vakit.entries.associateWith { Instant.ofEpochMilli(kayit.getLong(it.name)) }
    }.getOrNull()

    suspend fun yenile(c:Context,k:Konum):Boolean = withContext(Dispatchers.IO) {
        val zone=ZoneId.systemDefault()
        val tarih=LocalDate.now(zone)
        val id=anahtar(k,zone)
        val p=prefs(c)
        if(p.getString("konum",null)==id && p.getLong("son",0)>System.currentTimeMillis()-21_600_000L
            && (-3L..3L).all { oku(c,k,tarih.plusDays(it),zone)!=null })return@withContext false
        val takvim=JSONObject()
        val aylar=(-3L..3L).map { YearMonth.from(tarih.plusDays(it)) }.distinct()
        try {
            for(ay in aylar) {
                if(!KonumTakip.izin(c))return@withContext false
                kotlinx.coroutines.currentCoroutineContext().ensureActive()
                val tz=URLEncoder.encode(zone.id,"UTF-8")
                val url="https://api.aladhan.com/v1/calendar/${ay.year}/${ay.monthValue}?latitude=${k.enlem}&longitude=${k.boylam}&method=13&school=0&timezonestring=$tz"
                val conn=URL(url).openConnection() as HttpURLConnection
                conn.instanceFollowRedirects=false
                conn.connectTimeout=10000;conn.readTimeout=15000
                try {
                    check(conn.responseCode==200)
                    val raw=conn.inputStream.use { input ->
                        val out=java.io.ByteArrayOutputStream()
                        val buf=ByteArray(8192)
                        while(true){val n=input.read(buf);if(n<0)break;check(out.size()+n<=1_048_576);out.write(buf,0,n)}
                        out.toString("UTF-8")
                    }
                    val root=JSONObject(raw);check(root.getInt("code")==200)
                    val data=root.getJSONArray("data")
                    for(i in 0 until data.length()){
                        val item=data.getJSONObject(i)
                        val gun=LocalDate.parse(item.getJSONObject("date").getJSONObject("gregorian").getString("date"),format)
                        check(YearMonth.from(gun)==ay)
                        val timings=item.getJSONObject("timings")
                        val satir=JSONObject()
                        val saatler=alanlar.map { ad ->
                            val t=timings.getString(ad).take(5)
                            check(t.matches(Regex("[0-9]{2}:[0-9]{2}")))
                            gun.atTime(LocalTime.parse(t)).atZone(zone).toInstant()
                        }
                        check(saatler.zipWithNext().all{(a,b)->a.isBefore(b)})
                        Vakit.entries.forEachIndexed{j,v->satir.put(v.name,saatler[j].toEpochMilli())}
                        takvim.put(gun.toString(),satir)
                    }
                } finally {conn.disconnect()}
            }
            check((-3L..3L).all { takvim.has(tarih.plusDays(it).toString()) })
            val simdiki=VakitAyarlari.konum(c)
            if(!KonumTakip.izin(c)||anahtar(simdiki,ZoneId.systemDefault())!=id)return@withContext false
            p.edit().putString("konum",id).putString("takvim",takvim.toString()).putLong("son",System.currentTimeMillis()).apply()
            true
        } catch(e:Exception) {
            if(e is kotlinx.coroutines.CancellationException)throw e
            android.util.Log.w("CevrimiciVakitler","Çevrimiçi takvim alınamadı; cihaz hesabı kullanılabilir",e)
            false
        }
    }
}
