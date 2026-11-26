package com.atasoyyazilim.kurankerim.ui.ekranlar

import android.content.Context
import android.util.Xml
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.atasoyyazilim.kurankerim.R
import com.atasoyyazilim.kurankerim.data.KaldigimYer
import com.atasoyyazilim.kurankerim.data.MushafKonumu
import com.atasoyyazilim.kurankerim.ui.bilesenler.AltSekmeCubugu
import com.atasoyyazilim.kurankerim.ui.bilesenler.HatMadalyonu
import com.atasoyyazilim.kurankerim.ui.nav.Ekran
import com.atasoyyazilim.kurankerim.ui.theme.Amiri
import com.atasoyyazilim.kurankerim.ui.theme.LocalTemaRenkleri
import com.atasoyyazilim.kurankerim.ui.theme.Manrope
import com.atasoyyazilim.kurankerim.ui.theme.Renk
import com.atasoyyazilim.kurankerim.vakit.*
import kotlinx.coroutines.delay
import org.xmlpull.v1.XmlPullParser
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private val tr = Locale("tr", "TR")
private fun simge(ad: String, d: String, cizgi: Float = 1.8f) =
    ImageVector.Builder(ad, 24.dp, 24.dp, 24f, 24f).apply {
        addPath(pathData = addPathNodes(d), stroke = SolidColor(Color.Black),
            strokeLineWidth = cizgi, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round)
    }.build()

private data class Bolum(val ekran: Ekran, val etiket: Int, val ikon: ImageVector?)
private val kitap = simge("kitap", "M4 4h6a2 2 0 0 1 2 2v14a2 2 0 0 0-2-2H4z M20 4h-6a2 2 0 0 0-2 2v14a2 2 0 0 1 2-2h6z")
private val ok = simge("ok", "M9 5l7 7-7 7", 2f)
private val yer = simge("konum", "M12 21s7-6 7-12a7 7 0 0 0-14 0c0 6 7 12 7 12z M12 6.5a2.5 2.5 0 1 1 0 5a2.5 2.5 0 1 1 0-5z", 2f)
private val bolumler = listOf(
    Bolum(Ekran.MUSHAF, R.string.ana_kuran, kitap),
    Bolum(Ekran.HATIM, R.string.ana_hatim, simge("hatim", "M5 4h11a3 3 0 0 1 3 3v13H8a3 3 0 0 1-3-3z M9 11l2 2 4-4")),
    Bolum(Ekran.HADIS, R.string.ana_hadis, simge("hadis", "M7 4h10a2 2 0 0 1 2 2v12a2 2 0 0 1-2 2H7 M7 4a2 2 0 0 0-2 2v1h4V6a2 2 0 0 0-2-2z M11 10h5 M11 14h5")),
    Bolum(Ekran.ILMIHAL, R.string.ana_ilmihal, simge("ilmihal", "M12 3l8 4v5c0 5-3.5 8-8 9-4.5-1-8-4-8-9V7z M9 12l2 2 4-4")),
    Bolum(Ekran.NAMAZ, R.string.ana_namaz, simge("namaz", "M5 21V12a7 7 0 0 1 14 0v9 M12 5V2 M2 21h20 M9.5 21v-4a2.5 2.5 0 0 1 5 0v4")),
    Bolum(Ekran.VAKITLER, R.string.ana_vakitler, simge("vakitler", "M12 5a8 8 0 1 1 0 16a8 8 0 1 1 0-16z M12 9v4l2.5 2 M9 2h6")),
    Bolum(Ekran.KIBLE, R.string.ana_kible, simge("kible", "M12 3a9 9 0 1 1 0 18a9 9 0 1 1 0-18z M15.5 8.5l-2 5-5 2 2-5z")),
    Bolum(Ekran.ZIKIRMATIK, R.string.ana_zikirmatik, simge("zikirmatik", "M12 3a2 2 0 1 1 0 4a2 2 0 1 1 0-4z M6.5 7a2 2 0 1 1 0 4a2 2 0 1 1 0-4z M17.5 7a2 2 0 1 1 0 4a2 2 0 1 1 0-4z M8 13a2 2 0 1 1 0 4a2 2 0 1 1 0-4z M16 13a2 2 0 1 1 0 4a2 2 0 1 1 0-4z M12 18v4")),
    Bolum(Ekran.ESMA, R.string.ana_esma, simge("esma", "M12 2l2.5 5.5L20 10l-5.5 2.5L12 18l-2.5-5.5L4 10l5.5-2.5z M12 18v4")),
    Bolum(Ekran.ELIFBA, R.string.ana_elifba, null),
    Bolum(Ekran.DUALAR, R.string.ana_dualar, simge("dualar", "M8 21v-6l-3-5V5a1.5 1.5 0 0 1 3 0v5 M16 21v-6l3-5V5a1.5 1.5 0 0 0-3 0v5")),
    Bolum(Ekran.KAZA, R.string.ana_kaza, simge("kaza", "M10 6h10 M10 12h10 M10 18h10 M4 6l1 1 2-2 M4 12l1 1 2-2 M4 18l1 1 2-2"))
)

/** Yalnız özgün Tanzil metadata'sındaki sûre etiketi; dini içerik oluşturulmaz. */
private fun sureAdi(c: Context, sure: Int): String? {
    return try {
        c.assets.open("content/quran/quran-data.xml").use { stream ->
            val p = Xml.newPullParser()
            p.setInput(stream, "UTF-8")
            while (p.eventType != XmlPullParser.END_DOCUMENT) {
                if (p.eventType == XmlPullParser.START_TAG && p.name == "sura" &&
                    p.getAttributeValue(null, "index") == sure.toString()) {
                    return if (sure == 1) c.getString(R.string.ana_fatiha) else p.getAttributeValue(null, "tname")
                }
                p.next()
            }
            null
        }
    } catch (_: Exception) { null }
}

@Composable
private fun AnaYazi(metin: String, boyut: Float, renk: Color, kalinlik: FontWeight = FontWeight.Normal,
                    modifier: Modifier = Modifier, ortala: Boolean = false) {
    Text(metin, modifier = modifier, color = renk, fontFamily = Manrope, fontSize = boyut.sp,
        fontWeight = kalinlik, textAlign = if (ortala) TextAlign.Center else TextAlign.Start)
}

@Composable
private fun Desen(modifier: Modifier) {
    Canvas(modifier) {
        val a = 48.dp.toPx()
        val renk = Renk.Altin.copy(alpha = 0.09f)
        val cizgi = Stroke(1.dp.toPx())
        val points = listOf(24f to 2f,30f to 18f,46f to 24f,30f to 30f,24f to 46f,18f to 30f,2f to 24f,18f to 18f)
        for (x in 0..(size.width/a).toInt()) for (y in 0..(size.height/a).toInt()) {
            fun path(points: List<Pair<Float, Float>>) = Path().apply {
                points.forEachIndexed { i, (px,py) ->
                    val xx = x*a + px*a/48f
                    val yy = y*a + py*a/48f
                    if (i == 0) moveTo(xx,yy) else lineTo(xx,yy)
                }
                close()
            }
            drawPath(path(points), renk, style = cizgi)
            drawPath(path(listOf(24f to 9.858f,38.142f to 24f,24f to 38.142f,9.858f to 24f)), renk, style = cizgi)
        }
    }
}

/** Main.dc.html; dinamik değerler cihazdan, örnek HTML saatleri çalışma zamanına taşınmaz. */
@Composable
fun AnaSayfaEkrani(git: (Ekran) -> Unit) {
    val c = LocalContext.current
    val r = LocalTemaRenkleri.current
    val depo = remember(c) { KaldigimYer(c.applicationContext) }
    val konum by depo.mushaf.collectAsState(initial = MushafKonumu())
    val sure = remember(c, konum.sure) { sureAdi(c, konum.sure) }
    val simdi by produceState(Instant.now()) {
        while (true) { value = Instant.now(); delay(1000) }
    }
    val bolge = ZoneId.systemDefault()
    val tarih = simdi.atZone(bolge).toLocalDate()
    val sehir = VakitAyarlari.konum(c)
    val gecikme = VakitAyarlari.sabahEzaniGecikmeDk(c)
    val vakitler = remember(tarih, sehir) {
        VakitHesaplayici.gunluk(sehir.enlem, sehir.boylam, tarih)
    }
    val ezanlar = remember(tarih, sehir, gecikme) {
        (vakitler.toList() + VakitHesaplayici.gunluk(sehir.enlem, sehir.boylam, tarih.plusDays(1)).toList())
            .filter { it.first != Vakit.GUNES }
            .map { (v,t) -> v to EzanZamanlayici.ezanZamani(c, v, t) }.sortedBy { it.second }
    }
    val sonraki = ezanlar.firstOrNull { it.second.isAfter(simdi) }
    val saniye = sonraki?.let { Duration.between(simdi, it.second).seconds.coerceAtLeast(0) }
    val saatFmt = remember { DateTimeFormatter.ofPattern("HH:mm", tr) }
    fun saat(z: Instant?) = z?.atZone(bolge)?.format(saatFmt) ?: "—"
    val vakitAdi = sonraki?.let { stringResource(if (it.first == Vakit.IMSAK) R.string.ana_sabah else it.first.adRes) } ?: "—"
    val geriSayim = saniye?.let { String.format(Locale.ROOT,"%02d:%02d:%02d",it/3600,(it%3600)/60,it%60) } ?: "--:--:--"
    Column(Modifier.fillMaxSize().background(r.zemin)
        .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal))) {
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState())) {
            Box(Modifier.fillMaxWidth()) {
                Box(Modifier.fillMaxWidth().height(336.dp).background(Renk.YesilKoyu).clip(RoundedCornerShape(0.dp))) {
                    Desen(Modifier.matchParentSize())
                    Column(Modifier.fillMaxWidth().padding(start=18.dp,end=18.dp,top=22.dp),
                        horizontalAlignment=Alignment.CenterHorizontally) {
                        Row(Modifier.fillMaxWidth(), verticalAlignment=Alignment.CenterVertically,
                            horizontalArrangement=Arrangement.SpaceBetween) {
                            HatMadalyonu("اللّٰه", stringResource(R.string.ana_allah), yaziBoyutu=30.sp)
                            Column(Modifier.weight(1f).padding(horizontal=4.dp), horizontalAlignment=Alignment.CenterHorizontally) {
                                Text(stringResource(R.string.app_name).uppercase(tr), color=Renk.Altin,
                                    fontFamily=Manrope,fontSize=12.sp,fontWeight=FontWeight.ExtraBold,
                                    letterSpacing=1.92.sp,textAlign=TextAlign.Center)
                                Spacer(Modifier.height(8.dp))
                                AnaYazi(tarih.format(DateTimeFormatter.ofPattern("d MMMM yyyy, EEEE",tr)),13f,Renk.KoyuMetin,FontWeight.SemiBold,ortala=true)
                                Spacer(Modifier.height(2.dp))
                                AnaYazi(runCatching { HicriTakvim.metin(tarih) }.getOrDefault("—"),12.5f,Renk.KoyuMetinSoluk,ortala=true)
                            }
                            HatMadalyonu("مُحَمَّد", stringResource(R.string.ana_muhammed), yaziBoyutu=26.sp)
                        }
                        Spacer(Modifier.height(20.dp))
                        AnaYazi(stringResource(R.string.ana_ezan_kalan,vakitAdi),14f,Renk.KoyuMetin,FontWeight.SemiBold)
                        Spacer(Modifier.height(4.dp))
                        Text(geriSayim,style=TextStyle(fontFamily=Manrope,fontSize=52.sp,lineHeight=57.2.sp,
                            fontWeight=FontWeight.ExtraBold,color=Renk.Beyaz,letterSpacing=1.04.sp,
                            fontFeatureSettings="tnum"),maxLines=1)
                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement=Arrangement.spacedBy(6.dp),verticalAlignment=Alignment.CenterVertically) {
                            Icon(yer,null,tint=Renk.Altin,modifier=Modifier.size(15.dp))
                            AnaYazi(stringResource(R.string.ana_konum_vakit,sehir.ad,vakitAdi,saat(sonraki?.second)),13f,Renk.Altin,FontWeight.Bold)
                        }
                        Spacer(Modifier.height(18.dp))
                        Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(4.dp)) {
                            Vakit.entries.forEach { v ->
                                val aktif = sonraki?.first == v
                                Column(Modifier.weight(1f).clip(RoundedCornerShape(10.dp))
                                    .background(if(aktif) Renk.Altin else Renk.Beyaz.copy(alpha=.06f))
                                    .padding(vertical=8.dp),horizontalAlignment=Alignment.CenterHorizontally) {
                                    Text(stringResource(v.adRes),fontFamily=Manrope,fontSize=10.5.sp,
                                        fontWeight=FontWeight.Bold,color=if(aktif)Renk.YesilKoyu else Renk.KoyuMetinSoluk,maxLines=1)
                                    Spacer(Modifier.height(2.dp))
                                    Text(saat(vakitler[v]),fontFamily=Manrope,fontSize=13.sp,
                                        fontWeight=FontWeight.ExtraBold,color=if(aktif)Renk.YesilKoyu else Renk.Beyaz,maxLines=1)
                                }
                            }
                        }
                    }
                }
                val sekil=RoundedCornerShape(18.dp)
                Column(Modifier.padding(top=314.dp,start=16.dp,end=16.dp).fillMaxWidth()
                    .shadow(8.dp,sekil).clip(sekil).background(r.kart).padding(start=16.dp,end=16.dp,top=8.dp,bottom=10.dp)) {
                    Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween,verticalAlignment=Alignment.CenterVertically) {
                        Text(stringResource(R.string.ana_devam).uppercase(tr),fontFamily=Manrope,fontSize=11.5.sp,
                            fontWeight=FontWeight.ExtraBold,letterSpacing=1.15.sp,color=r.altinMetin)
                        Row(Modifier.heightIn(min=44.dp).clickable(role=Role.Button){git(Ekran.HATIM)},
                            verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(4.dp)) {
                            // Hatim takibi henüz uygulanmadı: tasarımdaki örnek %14 gösterilmez.
                            AnaYazi(stringResource(R.string.ana_hatim),12.5f,r.aktif,FontWeight.ExtraBold)
                            Icon(ok,null,tint=r.aktif,modifier=Modifier.size(14.dp))
                        }
                    }
                    Row(Modifier.fillMaxWidth().heightIn(min=52.dp).clickable(role=Role.Button){git(Ekran.MUSHAF)},
                        horizontalArrangement=Arrangement.spacedBy(12.dp),verticalAlignment=Alignment.CenterVertically) {
                        Box(Modifier.size(42.dp).background(Renk.YesilAcik,RoundedCornerShape(12.dp)),contentAlignment=Alignment.Center) {
                            Icon(kitap,null,tint=Renk.Zumrut,modifier=Modifier.size(22.dp))
                        }
                        Column(Modifier.weight(1f)) {
                            AnaYazi(stringResource(R.string.app_name),15f,r.metin,FontWeight.ExtraBold)
                            Spacer(Modifier.height(1.dp))
                            AnaYazi(stringResource(R.string.ana_okuma_konumu,sure ?: "—",konum.sayfa,konum.ayet),12.5f,r.ikincil)
                        }
                        Icon(ok,null,tint=r.ikincil,modifier=Modifier.size(18.dp))
                    }
                }
            }
            Column(Modifier.fillMaxWidth().padding(start=16.dp,end=16.dp,top=10.dp,bottom=8.dp)) {
                AnaYazi(stringResource(R.string.ana_bolumler),16f,r.baslik,FontWeight.ExtraBold)
                Spacer(Modifier.height(8.dp))
                bolumler.chunked(4).forEachIndexed { i, satir ->
                    if(i>0) Spacer(Modifier.height(8.dp))
                    Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                        satir.forEach { b ->
                            val sekil=RoundedCornerShape(14.dp)
                            Column(Modifier.weight(1f).height(82.dp).clip(sekil).background(r.kart)
                                .border(1.dp,r.kenar,sekil).clickable(role=Role.Button){git(b.ekran)},
                                horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.Center) {
                                Box(Modifier.size(34.dp).background(Renk.YesilKoyu,RoundedCornerShape(10.dp)),contentAlignment=Alignment.Center) {
                                    if(b.ikon!=null) Icon(b.ikon,null,tint=Renk.Altin,modifier=Modifier.size(19.dp))
                                    else Text("ا ب",fontFamily=Amiri,fontSize=17.sp,color=Renk.Altin)
                                }
                                Spacer(Modifier.height(5.dp))
                                Text(stringResource(b.etiket),fontFamily=Manrope,fontSize=11.5.sp,fontWeight=FontWeight.ExtraBold,
                                    color=r.baslik,maxLines=1,overflow=TextOverflow.Ellipsis,textAlign=TextAlign.Center)
                            }
                        }
                    }
                }
            }
        }
        AltSekmeCubugu(Ekran.ANA,git)
    }
}
