package com.atasoyyazilim.kurankerim.guncelleme

import java.net.URI

internal object GuncellemeKurallari {
    const val API = "https://api.github.com/repos/mmaapps/Kuran/releases/latest"
    const val MAX_APK = 500L * 1024 * 1024
    private val ad = Regex("KuranKerim-([1-9][0-9]{0,8})\\.apk")
    private val etiket = Regex("v[0-9]+\\.[0-9]+\\.[0-9]+")
    private val ozet = Regex("sha256:[0-9a-fA-F]{64}")
    fun kod(dosya: String): Long? = ad.matchEntire(dosya)?.groupValues?.get(1)?.toLongOrNull()
    fun gecerliOzet(digest: String) = ozet.matches(digest)
    fun surum(tag: String): String? = if (etiket.matches(tag)) tag.drop(1) else null
    fun kaynakGecerli(url: String, tag: String, dosya: String): Boolean =
        surum(tag) != null && kod(dosya) != null &&
            url == "https://github.com/mmaapps/Kuran/releases/download/$tag/$dosya"
    fun yonlendirmeGecerli(url: String): Boolean = runCatching {
        val u = URI(url)
        u.scheme == "https" && u.userInfo == null && (u.port == -1 || u.port == 443) &&
            u.host in setOf("api.github.com","github.com","release-assets.githubusercontent.com","objects.githubusercontent.com")
    }.getOrDefault(false)
}
