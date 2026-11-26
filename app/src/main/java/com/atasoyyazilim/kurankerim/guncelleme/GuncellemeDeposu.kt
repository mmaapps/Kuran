package com.atasoyyazilim.kurankerim.guncelleme

import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.security.MessageDigest
import java.io.IOException
import kotlin.coroutines.coroutineContext

data class YeniSurum(val ad: String, val kod: Long, val url: String, val sha256: String, val boyut: Long)

class GuncellemeDeposu(private val c: Context) {
    private val pm = c.packageManager
    @Suppress("DEPRECATION")
    private val imzaBayragi = if (Build.VERSION.SDK_INT >= 28) PackageManager.GET_SIGNING_CERTIFICATES else PackageManager.GET_SIGNATURES
    @Suppress("DEPRECATION")
    private fun kod(p: PackageInfo): Long = if (Build.VERSION.SDK_INT >= 28) p.longVersionCode else p.versionCode.toLong()
    @Suppress("DEPRECATION")
    private fun kurulu(): PackageInfo = pm.getPackageInfo(c.packageName, imzaBayragi)
    @Suppress("DEPRECATION")
    private fun imzalar(p: PackageInfo): Set<String> =
        (if (Build.VERSION.SDK_INT >= 28) p.signingInfo?.apkContentsSigners else p.signatures)
            ?.map { sha(it.toByteArray()) }?.toSet() ?: emptySet()
    private fun sha(b: ByteArray): String = hex(MessageDigest.getInstance("SHA-256").digest(b))
    private fun hex(b: ByteArray) = b.joinToString("") { "%02x".format(it.toInt() and 255) }
    private val klasor get() = File(c.cacheDir, "guncelleme").apply { mkdirs() }
    val kuruluSurum get() = kurulu().versionName ?: "—"

    private fun baglan(url: String, json: Boolean = false): HttpURLConnection {
        var hedef = url
        repeat(6) {
            require(GuncellemeKurallari.yonlendirmeGecerli(hedef))
            val conn = URL(hedef).openConnection() as HttpURLConnection
            conn.instanceFollowRedirects = false
            conn.connectTimeout = 12_000
            conn.readTimeout = 25_000
            conn.setRequestProperty("User-Agent","KuranKerim-Update")
            conn.setRequestProperty("Accept",if(json)"application/vnd.github+json" else "application/octet-stream")
            val status = conn.responseCode
            if(status in 300..399) {
                val next = conn.getHeaderField("Location")
                conn.disconnect()
                hedef = URL(URL(hedef),next ?: throw IOException("Redirect")).toString()
            } else return conn
        }
        throw IOException("Too many redirects")
    }

    suspend fun kontrol(): YeniSurum? = withContext(Dispatchers.IO) {
        val conn = baglan(GuncellemeKurallari.API,true)
        try {
            if(conn.responseCode == 404) return@withContext null
            check(conn.responseCode == 200)
            val raw = conn.inputStream.use { input ->
                val out = java.io.ByteArrayOutputStream()
                val buf = ByteArray(8192)
                while(true) {
                    coroutineContext.ensureActive()
                    val n = input.read(buf)
                    if(n < 0) break
                    check(out.size()+n <= 2*1024*1024)
                    out.write(buf,0,n)
                }
                out.toString("UTF-8")
            }
            val j = JSONObject(raw)
            if(j.optBoolean("draft") || j.optBoolean("prerelease")) return@withContext null
            val tag = j.getString("tag_name")
            val version = GuncellemeKurallari.surum(tag) ?: return@withContext null
            val assets = j.getJSONArray("assets")
            val current = kod(kurulu())
            val matches = (0 until assets.length()).mapNotNull { i ->
                val a = assets.getJSONObject(i)
                val name = a.optString("name")
                val code = GuncellemeKurallari.kod(name) ?: return@mapNotNull null
                val url = a.optString("browser_download_url")
                val digest = a.optString("digest")
                val size = a.optLong("size")
                if(code <= current || !GuncellemeKurallari.kaynakGecerli(url,tag,name) ||
                    !GuncellemeKurallari.gecerliOzet(digest) || size !in 1..GuncellemeKurallari.MAX_APK ||
                    a.optString("state") != "uploaded") null
                else YeniSurum(version,code,url,digest.substringAfter(':').lowercase(),size)
            }
            matches.singleOrNull()
        } finally { conn.disconnect() }
    }

    suspend fun indir(s: YeniSurum, ilerleme: (Float)->Unit): File = withContext(Dispatchers.IO) {
        val hedef = File(klasor,"KuranKerim-${s.kod}.apk")
        val tmp = File(klasor,"KuranKerim-${s.kod}.part")
        // Önceki yarım veya eski indirmeleri temizle.
        klasor.listFiles()?.forEach { it.delete() }
        try {
            val conn = baglan(s.url)
            try {
                check(conn.responseCode == 200)
                val md = MessageDigest.getInstance("SHA-256")
                var total = 0L
                conn.inputStream.use { input ->
                    tmp.outputStream().use { out ->
                        val buf = ByteArray(64*1024)
                        while(true) {
                            coroutineContext.ensureActive()
                            val n = input.read(buf)
                            if(n < 0) break
                            total += n
                            check(total <= s.boyut && total <= GuncellemeKurallari.MAX_APK)
                            out.write(buf,0,n);md.update(buf,0,n)
                            ilerleme(total.toFloat()/s.boyut)
                        }
                    }
                }
                check(total == s.boyut && hex(md.digest()) == s.sha256)
                check(tmp.renameTo(hedef))
            } finally { conn.disconnect() }
            dogrula(hedef,s)
            hedef
        } catch(t: Throwable) {
            tmp.delete();hedef.delete();throw t
        }
    }

    @Suppress("DEPRECATION")
    private fun dogrula(f:File,s:YeniSurum) {
        check(f.canonicalFile.parentFile == klasor.canonicalFile && f.isFile && f.length()==s.boyut)
        val md=MessageDigest.getInstance("SHA-256")
        f.inputStream().use { input ->
            val buf=ByteArray(65536)
            while(true){val n=input.read(buf);if(n<0)break;md.update(buf,0,n)}
        }
        check(hex(md.digest())==s.sha256)
        val eski=kurulu()
        val yeni=pm.getPackageArchiveInfo(f.absolutePath,imzaBayragi) ?: error("APK")
        check(yeni.packageName==c.packageName && kod(yeni)==s.kod && kod(yeni)>kod(eski))
        check(yeni.versionName==s.ad)
        val imza=imzalar(eski)
        check(imza.isNotEmpty() && imza==imzalar(yeni))
    }

    suspend fun kurulumNiyeti(f:File,s:YeniSurum):Intent = withContext(Dispatchers.IO) {
        dogrula(f,s)
        val uri=FileProvider.getUriForFile(c,"${c.packageName}.guncelleme",f)
        Intent(Intent.ACTION_VIEW).setDataAndType(uri,"application/vnd.android.package-archive")
            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
}
