package com.atasoyyazilim.kurankerim.data

/** assets/content/hadis/hadisler.json */
data class Hadis(
    val id: String,
    val kategori: String,
    val arapca: String,
    val turkce: String,
    val kaynak: String,
    val derece: String
)

/** assets/content/dualar/dualar.json */
data class Dua(
    val id: String,
    val ad: String,
    val kategori: String,
    val arapca: String,
    val okunus: String,
    val anlam: String,
    val kaynak: String
)

/** assets/content/esma/esma.json */
data class Esma(val no: Int, val arapca: String, val okunus: String, val anlam: String)

/** assets/content/elifba/harfler.json */
data class Harf(val sira: Int, val harf: String, val ad: String, val not: String, val sesDosyasi: String)

/** assets/content/namaz/abdest.json ve namaz.json — `cizim`, Namaz.dc.html'deki çizim kimliğidir (a1..a12, n1..n10). */
data class NamazAdimi(
    val no: Int,
    val cizim: String,
    val baslik: String,
    val hukum: String,
    val aciklama: String,
    val gorselAciklama: String,
    val arapca: String,
    val okunus: String
)

/** assets/content/ilmihal/ilmihal.json */
data class IlmihalBolumu(val id: String, val baslik: String, val altBaslik: String, val maddeler: List<String>)

/** assets/content/ayetler/ezan_sonrasi.json — sadece âyet numaraları; Arapça metin Kur'an dosyasından, ses tilâvetten. Meal yok. */
data class AyetAraligi(val sure: Int, val ayetler: List<Int>)

/** Zikirmatik.dc.html özgün beş zikir metni. Âyet çevirisi değildir. */
data class Zikir(val id:Int,val ad:String,val arapca:String,val anlam:String)
