package com.atasoyyazilim.kurankerim.ui.ekranlar

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import android.content.pm.PackageManager
import com.atasoyyazilim.kurankerim.R
import com.atasoyyazilim.kurankerim.ui.bilesenler.AltSekmeCubugu
import com.atasoyyazilim.kurankerim.ui.bilesenler.IlerlemeHalkasi
import com.atasoyyazilim.kurankerim.ui.nav.Ekran
import com.atasoyyazilim.kurankerim.ui.theme.*
import com.atasoyyazilim.kurankerim.vakit.*
import kotlinx.coroutines.delay
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private val trVakit = Locale("tr","TR")
private fun vakitIkon(ad: String, d: String, w: Float) =
    ImageVector.Builder(ad,24.dp,24.dp,24f,24f).apply {
        addPath(pathData=addPathNodes(d),stroke=SolidColor(Color.Black),strokeLineWidth=w,
            strokeLineCap=StrokeCap.Round,strokeLineJoin=StrokeJoin.Round)
    }.build()
private val Zil = vakitIkon("zil","M6 8a6 6 0 0 1 12 0c0 7 3 8 3 8H3s3-1 3-8 M10 20a2 2 0 0 0 4 0",1.9f)
private val KonumIkon = vakitIkon("konum","M12 21s7-6 7-12a7 7 0 0 0-14 0c0 6 7 12 7 12z M12 6.5a2.5 2.5 0 1 1 0 5a2.5 2.5 0 1 1 0-5z",2f)
private data class AyarSatiri(val anahtar:String,val baslik:Int,val aciklama:Int)
private val ayarlar = listOf(
    AyarSatiri("hatirlatma",R.string.vakit_hatirlat,R.string.vakit_hatirlat_alt),
    AyarSatiri("ezan_sonrasi_ayet",R.string.vakit_ayet,R.string.vakit_ayet_alt),
    AyarSatiri("geri_sayim",R.string.vakit_geri_sayim,R.string.vakit_geri_sayim_alt),
    AyarSatiri("sessizde_titret",R.string.vakit_titret,R.string.vakit_titret_alt)
)
@Composable
private fun VY(metin:String,boyut:Float,renk:Color,kalin:FontWeight=FontWeight.Normal) {
    Text(metin,fontFamily=Manrope,fontSize=boyut.sp,color=renk,fontWeight=kalin)
}
@Composable
fun EzanVaktiEkrani(git:(Ekran)->Unit) {
    val c=LocalContext.current
    val r=LocalTemaRenkleri.current
    val rev by VakitAyarlari.degisim.collectAsState()
    val konum=remember(rev){VakitAyarlari.konum(c)}
    var konumAciklama by remember { mutableStateOf(false) }
    val konumIzin=rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()){
        VakitAyarlari.izinYenilendi()
    }
    if(konumAciklama) androidx.compose.material3.AlertDialog(
        onDismissRequest={konumAciklama=false},
        title={Text(stringResource(R.string.vakit_konum_baslik))},
        text={Text(stringResource(R.string.vakit_konum_aciklama))},
        confirmButton={androidx.compose.material3.TextButton(onClick={
            konumAciklama=false
            if(!KonumTakip.izin(c))konumIzin.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
            else VakitAyarlari.izinYenilendi()
        }){Text(stringResource(R.string.vakit_konum_onay))}},
        dismissButton={androidx.compose.material3.TextButton(onClick={konumAciklama=false}){Text(stringResource(R.string.kapat))}}
    )
    var gecikme by remember { mutableIntStateOf(VakitAyarlari.sabahEzaniGecikmeDk(c)) }
    val ziller=remember { mutableStateMapOf<Vakit,Boolean>().apply { Vakit.entries.forEach { put(it,VakitAyarlari.ezanAcik(c,it)) } } }
    val durum=remember { mutableStateMapOf(
        "hatirlatma" to VakitAyarlari.hatirlatmaAcik(c),
        "ezan_sonrasi_ayet" to VakitAyarlari.ezanSonrasiAyetAcik(c),
        "geri_sayim" to VakitAyarlari.geriSayimAcik(c),
        "sessizde_titret" to VakitAyarlari.sessizdeTitret(c)
    ) }
    fun yenile() { EzanZamanlayici.kur(c,konum.enlem,konum.boylam) }
    var tamAlarm by remember { mutableStateOf(EzanZamanlayici.tamZamanliIzinVar(c)) }
    val lifecycle=LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle) {
        val observer=LifecycleEventObserver { _, event ->
            if(event==Lifecycle.Event.ON_RESUME) {
                val yeni=EzanZamanlayici.tamZamanliIzinVar(c)
                if(yeni!=tamAlarm) { tamAlarm=yeni; yenile() }
            }
        }
        lifecycle.addObserver(observer)
        onDispose { lifecycle.removeObserver(observer) }
    }
    val bildirimIzin=rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { yenile() }
    fun alarmIste() {
        if(Build.VERSION.SDK_INT>=31 && !EzanZamanlayici.tamZamanliIzinVar(c)) {
            try { c.startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,Uri.parse("package:"+c.packageName))) }
            catch(_:android.content.ActivityNotFoundException) { c.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,Uri.parse("package:"+c.packageName))) }
        }
    }
    fun bildirimIste() {
        if(Build.VERSION.SDK_INT>=33 && ContextCompat.checkSelfPermission(c,Manifest.permission.POST_NOTIFICATIONS)!=PackageManager.PERMISSION_GRANTED)
            bildirimIzin.launch(Manifest.permission.POST_NOTIFICATIONS)
    }
    var simdi by remember { mutableStateOf(Instant.now()) }
    LaunchedEffect(Unit) { while(true) { simdi=Instant.now();delay(1000) } }
    val bolge=ZoneId.systemDefault()
    val bugun=simdi.atZone(bolge).toLocalDate()
    var seciliGun by remember(bugun) { mutableStateOf(bugun) }
    val gunler=remember(bugun) { (-3L..3L).map { bugun.plusDays(it) } }
    val seciliVakitler=remember(seciliGun,konum,rev) { VakitHesaplayici.gunluk(c,konum.enlem,konum.boylam,seciliGun) }
    val ezanlar=remember(bugun,konum,gecikme,rev) {
        (-1L..1L).flatMap { g -> VakitHesaplayici.gunluk(c,konum.enlem,konum.boylam,bugun.plusDays(g)).toList() }
            .filter { it.first!=Vakit.GUNES }.map { (v,t) -> v to EzanZamanlayici.ezanZamani(c,v,t) }.sortedBy { it.second }
    }
    val sonraki=ezanlar.firstOrNull { it.second.isAfter(simdi) }
    val onceki=ezanlar.lastOrNull { !it.second.isAfter(simdi) }
    val kalan=sonraki?.let { Duration.between(simdi,it.second).seconds.coerceAtLeast(0) }?:0
    val aralik=if(sonraki!=null&&onceki!=null) Duration.between(onceki.second,sonraki.second).seconds.coerceAtLeast(1) else 1L
    val oran=(1f-kalan.toFloat()/aralik).coerceIn(0f,1f)
    val fmt=remember { DateTimeFormatter.ofPattern("HH:mm",trVakit) }
    fun saat(t:Instant?)=t?.atZone(bolge)?.format(fmt)?:"—"
    val ad=sonraki?.let { stringResource(if(it.first==Vakit.IMSAK)R.string.ana_sabah else it.first.adRes) }?:"—"
    val geriSayim=if(sonraki==null)"--:--:--" else String.format(Locale.ROOT,"%02d:%02d:%02d",kalan/3600,(kalan%3600)/60,kalan%60)
    val kart=RoundedCornerShape(18.dp)
    Column(Modifier.fillMaxSize().background(r.zemin).windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top+WindowInsetsSides.Horizontal))) {
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(start=16.dp,end=16.dp,top=16.dp,bottom=24.dp)) {
            Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.SpaceBetween) {
                VY(stringResource(R.string.vakit_baslik),26f,r.baslik,FontWeight.ExtraBold)
                Box(Modifier.heightIn(min=44.dp).clickable(role=Role.Button){konumAciklama=true},contentAlignment=Alignment.Center) {
                    Row(Modifier.height(40.dp).background(r.kart,RoundedCornerShape(20.dp)).border(1.dp,r.kenar,RoundedCornerShape(20.dp)).padding(horizontal=12.dp),
                        horizontalArrangement=Arrangement.spacedBy(6.dp),verticalAlignment=Alignment.CenterVertically) {
                        Icon(KonumIkon,null,Modifier.size(15.dp),tint=r.baslik)
                        VY(konum.ad,13f,r.baslik,FontWeight.ExtraBold)
                    }
                }
            }
            Spacer(Modifier.height(2.dp))
            VY(bugun.format(DateTimeFormatter.ofPattern("d MMMM yyyy",trVakit))+" · "+runCatching{HicriTakvim.metin(bugun)}.getOrDefault("—"),13f,r.ikincil,FontWeight.SemiBold)
            Spacer(Modifier.height(14.dp))
            Row(Modifier.fillMaxWidth().background(Renk.YesilKoyu,RoundedCornerShape(22.dp)).padding(18.dp),
                verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(16.dp)) {
                IlerlemeHalkasi(oran,132.dp,58.dp,9.dp) {
                    Column(horizontalAlignment=Alignment.CenterHorizontally) {
                        VY(stringResource(R.string.vakit_kalan,ad),11f,Renk.KoyuMetinSoluk,FontWeight.Bold)
                        Text(geriSayim,style=TextStyle(fontFamily=Manrope,fontSize=22.sp,fontWeight=FontWeight.ExtraBold,color=Renk.Beyaz,fontFeatureSettings="tnum"))
                    }
                }
                Column(Modifier.weight(1f)) {
                    Text(stringResource(R.string.vakit_siradaki),fontFamily=Manrope,fontSize=12.sp,fontWeight=FontWeight.ExtraBold,letterSpacing=1.2.sp,color=Renk.Altin)
                    Spacer(Modifier.height(2.dp))
                    VY(ad,30f,Renk.Beyaz,FontWeight.ExtraBold)
                    VY(saat(sonraki?.second),15f,Renk.KoyuMetin,FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    if(durum["hatirlatma"]==true) VY(stringResource(R.string.vakit_uyari),12f,Renk.KoyuMetinSoluk)
                    // İzinli tilâvet henüz yok; okunacağına dair söz verilmez.
                }
            }
            Spacer(Modifier.height(14.dp))
            Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(6.dp)) {
                gunler.forEach { g ->
                    val aktif=g==seciliGun
                    val s=RoundedCornerShape(14.dp)
                    Column(Modifier.weight(1f).height(62.dp).clip(s).background(if(aktif)Renk.YesilKoyu else r.kart)
                        .border(1.dp,if(aktif)Renk.YesilKoyu else r.kenar,s).selectable(aktif,role=Role.Button){seciliGun=g},
                        horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.Center) {
                        VY(g.format(DateTimeFormatter.ofPattern("EEE",trVakit)).take(3),11f,if(aktif)Renk.Beyaz else r.metin,FontWeight.Bold)
                        Spacer(Modifier.height(2.dp))
                        VY(g.dayOfMonth.toString(),17f,if(aktif)Renk.Beyaz else r.metin,FontWeight.ExtraBold)
                    }
                }
            }
            Spacer(Modifier.height(14.dp))
            Column(Modifier.fillMaxWidth().clip(kart).background(r.kart).border(1.dp,r.kenar,kart)) {
                Vakit.entries.forEach { v ->
                    val t=seciliVakitler[v]
                    val aktif=sonraki?.first==v && sonraki.second.atZone(bolge).toLocalDate()==seciliGun
                    val gecti=t?.isBefore(simdi)==true
                    Row(Modifier.fillMaxWidth().background(if(aktif)Renk.VurguAcik else Color.Transparent).heightIn(min=56.dp).padding(start=16.dp,end=8.dp),
                        horizontalArrangement=Arrangement.spacedBy(12.dp),verticalAlignment=Alignment.CenterVertically) {
                        Box(Modifier.size(8.dp).background(if(aktif)Renk.AltinKoyu else if(gecti)Renk.VakitGecmis else Renk.Zumrut,CircleShape))
                        Column(Modifier.weight(1f)) {
                            VY(stringResource(v.adRes),15.5f,if(aktif)Renk.Metin else r.metin,if(aktif)FontWeight.ExtraBold else FontWeight.SemiBold)
                            if(v==Vakit.IMSAK) VY(stringResource(R.string.vakit_sabah_saat,saat(t?.plusSeconds(gecikme*60L))),12f,if(aktif)Renk.AltinMetin else r.altinMetin,FontWeight.Bold)
                        }
                        VY(saat(t),16f,if(aktif)Renk.YesilKoyu else r.baslik,FontWeight.ExtraBold)
                        val on=ziller[v]==true
                        Box(Modifier.size(44.dp).toggleable(value=on,enabled=v!=Vakit.GUNES,role=Role.Checkbox,onValueChange={
                            ziller[v]=it;VakitAyarlari.ezanAyarla(c,v,it);yenile();if(it)bildirimIste()
                        }),contentAlignment=Alignment.Center) {
                            Icon(Zil,stringResource(if(on)R.string.vakit_zil_acik else R.string.vakit_zil_kapali,stringResource(v.adRes)),Modifier.size(20.dp),tint=if(on)Renk.Zumrut else Renk.VakitZilKapali)
                        }
                    }
                    if(v!=Vakit.YATSI) Box(Modifier.fillMaxWidth().height(1.dp).background(if(r.gece)r.kenar else Renk.Ayirici))
                }
            }
            Spacer(Modifier.height(6.dp))
            VY(stringResource(if(CevrimiciVakitler.oku(c,konum,seciliGun)!=null)R.string.vakit_cevrimici_notu else R.string.vakit_hesap_notu),11.5f,r.ikincil)
            if(!VakitAyarlari.konumBelirlendi(c)) VY(stringResource(R.string.vakit_varsayilan_konum),11.5f,r.ikincil)
            if(!tamAlarm) {
                // Sistem izni açıklaması: kullanıcı onayı olmadan ayar ekranı açılmaz.
                Text(stringResource(R.string.vakit_alarm_izin),modifier=Modifier.fillMaxWidth()
                    .heightIn(min=44.dp).clickable(role=Role.Button){alarmIste()}.padding(vertical=8.dp),
                    color=r.aktif,fontFamily=Manrope,fontSize=12.sp,fontWeight=FontWeight.Bold)
            }
            Spacer(Modifier.height(18.dp))
            VY(stringResource(R.string.vakit_ayarlar),16f,r.baslik,FontWeight.ExtraBold)
            Spacer(Modifier.height(8.dp))
            Column(Modifier.fillMaxWidth().clip(kart).background(r.kart).border(1.dp,r.kenar,kart)) {
                ayarlar.forEachIndexed { i,a ->
                    Row(Modifier.fillMaxWidth().heightIn(min=56.dp).toggleable(value=durum[a.anahtar]==true,role=Role.Checkbox,onValueChange={
                        durum[a.anahtar]=it; VakitAyarlari.ayarla(c,a.anahtar,it);yenile()
                        if(it && a.anahtar in listOf("hatirlatma","geri_sayim"))bildirimIste()
                    }).padding(horizontal=16.dp,vertical=10.dp),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(12.dp)) {
                        Column(Modifier.weight(1f)) {
                            VY(stringResource(a.baslik),14.5f,r.metin,FontWeight.Bold)
                            VY(stringResource(a.aciklama),12f,r.ikincil)
                        }
                        Checkbox(checked=durum[a.anahtar]==true,onCheckedChange=null,modifier=Modifier.size(22.dp),colors=CheckboxDefaults.colors(checkedColor=Renk.Zumrut,checkmarkColor=Renk.Beyaz))
                    }
                    if(i!=ayarlar.lastIndex)Box(Modifier.fillMaxWidth().height(1.dp).background(if(r.gece)r.kenar else Renk.Ayirici))
                }
            }
            Spacer(Modifier.height(10.dp))
            Column(Modifier.fillMaxWidth().background(r.kart,kart).border(1.dp,r.kenar,kart).padding(horizontal=16.dp,vertical=12.dp)) {
                VY(stringResource(R.string.vakit_sabah_ayar),14.5f,r.metin,FontWeight.Bold)
                VY(stringResource(R.string.vakit_sabah_alt),12f,r.ikincil)
                Spacer(Modifier.height(10.dp))
                Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(6.dp)) {
                    VakitAyarlari.SABAH_GECIKME_SECENEKLERI.forEach { dk ->
                        val aktif=gecikme==dk
                        val s=RoundedCornerShape(12.dp)
                        Box(Modifier.weight(1f).height(44.dp).clip(s).background(if(aktif)Renk.YesilKoyu else r.kart).border(1.dp,if(aktif)Renk.YesilKoyu else r.kenar,s)
                            .selectable(aktif,role=Role.Button){gecikme=dk;VakitAyarlari.sabahEzaniGecikmeAyarla(c,dk);yenile()},contentAlignment=Alignment.Center) {
                            Text(if(dk==0)stringResource(R.string.vakit_imsakta)else stringResource(R.string.vakit_dakika,dk),fontFamily=Manrope,fontSize=12.5.sp,fontWeight=FontWeight.ExtraBold,color=if(aktif)Renk.Beyaz else r.metin,textAlign=TextAlign.Center)
                        }
                    }
                }
            }
        }
        AltSekmeCubugu(Ekran.VAKITLER,git)
    }
}
