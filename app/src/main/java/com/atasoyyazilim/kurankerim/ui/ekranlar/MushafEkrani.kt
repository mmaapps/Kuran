package com.atasoyyazilim.kurankerim.ui.ekranlar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.atasoyyazilim.kurankerim.R
import com.atasoyyazilim.kurankerim.data.*
import com.atasoyyazilim.kurankerim.ui.bilesenler.*
import com.atasoyyazilim.kurankerim.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private val GeriIkon = ImageVector.Builder("Geri",22.dp,22.dp,24f,24f).apply {
    addPath(pathData=addPathNodes("M15 5l-7 7 7 7"),stroke=SolidColor(Color.Black),
        strokeLineWidth=2f,strokeLineCap=StrokeCap.Round,strokeLineJoin=StrokeJoin.Round)
}.build()
private fun imIkon(dolu:Boolean) = ImageVector.Builder("Yer imi",22.dp,22.dp,24f,24f).apply {
    addPath(pathData=addPathNodes("M6 3h12v18l-6-4-6 4z"),
        fill=if(dolu) SolidColor(Color.Black) else null,stroke=SolidColor(Color.Black),
        strokeLineWidth=1.6f,strokeLineJoin=StrokeJoin.Round)
}.build()
private val ImDolu=imIkon(true)
private val ImBos=imIkon(false)
private val KitapIkon = ImageVector.Builder("Kitap",24.dp,24.dp,24f,24f).apply {
    addPath(pathData=addPathNodes("M4 4h6l2 2 2-2h6v16h-6l-2 2-2-2H4z M12 6v16"),
        stroke=SolidColor(Color.Black),strokeLineWidth=1.8f)
}.build()

private data class YuklenenMushaf(val veri:MushafVerisi?,val konum:MushafKonumu,val harfler:List<Harf>)
private data class SeciliKelime(val sayfa:Int,val sure:Int,val ayet:Int,val sira:Int,val metin:String)
private val kelimeDeseni=Regex("\\S+")
private fun arapRakam(n:Int)=n.toString().map { "٠١٢٣٤٥٦٧٨٩"[it-'0'] }.joinToString("")
private fun KuranSure.etiket()=latinAd

@Composable
fun MushafEkrani(geri:()->Unit) {
    val c=LocalContext.current.applicationContext
    val depo=remember(c){KaldigimYer(c)}
    val yuklenen by produceState<YuklenenMushaf?>(null,c) {
        value=withContext(Dispatchers.IO) {
            val kaynak=IcerikDeposu(c)
            YuklenenMushaf(kaynak.mushaf(),depo.mushaf.first(),kaynak.harfler())
        }
    }
    val y=yuklenen
    if(y==null || y.veri==null) {
        Column(Modifier.fillMaxSize().background(Renk.MushafZemin).windowInsetsPadding(WindowInsets.safeDrawing)) {
            Box(Modifier.size(44.dp).clickable(role=Role.Button,onClick=geri),contentAlignment=Alignment.Center) {
                Icon(GeriIkon,stringResource(R.string.mushaf_geri),tint=Renk.YesilKoyu)
            }
            if(y!=null) BosDurum(KitapIkon)
        }
    } else MushafOkuma(y.veri,y.konum,y.harfler,depo,geri)
}

@Composable
private fun MushafOkuma(veri:MushafVerisi,ilk:MushafKonumu,harfler:List<Harf>,depo:KaldigimYer,geri:()->Unit) {
    val r=LocalTemaRenkleri.current
    val zemin=if(r.gece)r.zemin else Renk.MushafZemin
    val ilkSayfa=ilk.sayfa.coerceIn(1,604)
    val pager=rememberPagerState(initialPage=ilkSayfa-1){veri.sayfalar.size}
    val sayfa=veri.sayfalar[pager.settledPage]
    val ilkAyet=sayfa.ayetler.first()
    var secili by remember { mutableStateOf<SeciliKelime?>(null) }
    val kelime=secili?.takeIf { it.sayfa==sayfa.no }
    val konum=if(kelime!=null) MushafKonumu(sayfa.no,kelime.sure,kelime.ayet)
        else if(sayfa.no==ilkSayfa && veri.sayfaNo(ilk.sure,ilk.ayet)==ilkSayfa)
            MushafKonumu(sayfa.no,ilk.sure,ilk.ayet)
        else MushafKonumu(sayfa.no,ilkAyet.sure,ilkAyet.no)
    // Kaydedilen âyet, panel kapatılınca kaybolmaz.
    var sonKonum by remember { mutableStateOf(konum) }
    LaunchedEffect(sayfa.no,kelime) {
        if(sonKonum.sayfa!=sayfa.no || kelime!=null) sonKonum=konum
    }
    val scope=rememberCoroutineScope()
    val lifecycle=LocalLifecycleOwner.current.lifecycle
    val son by rememberUpdatedState(sonKonum)
    var kaydedilen by remember { mutableStateOf<MushafKonumu?>(null) }
    LaunchedEffect(sonKonum) {
        val k=sonKonum
        withContext(NonCancellable){depo.mushafKaydet(k)}
        kaydedilen=k
    }
    DisposableEffect(lifecycle) {
        val observer=LifecycleEventObserver { _,event ->
            if(event==Lifecycle.Event.ON_STOP) {
                val k=son
                scope.launch { withContext(NonCancellable){depo.mushafKaydet(k)} }
            }
        }
        lifecycle.addObserver(observer)
        onDispose { lifecycle.removeObserver(observer) }
    }
    val imler by depo.mushafYerImleri.collectAsState(initial=emptySet())
    val boyut by depo.mushafYaziBoyutu.collectAsState(initial=28)
    val kayitli=sayfa.no.toString() in imler
    val sure=veri.sureler[ilkAyet.sure-1]
    val sureAdi=if(sure.no==1)stringResource(R.string.ana_fatiha)else sure.etiket()
    Column(Modifier.fillMaxSize().background(zemin).windowInsetsPadding(WindowInsets.safeDrawing)) {
        Row(Modifier.fillMaxWidth().height(64.dp).padding(horizontal=10.dp),
            verticalAlignment=Alignment.CenterVertically) {
            Box(Modifier.size(44.dp).clickable(role=Role.Button,onClick=geri),contentAlignment=Alignment.Center) {
                Icon(GeriIkon,stringResource(R.string.mushaf_geri),tint=r.baslik)
            }
            Column(Modifier.weight(1f),horizontalAlignment=Alignment.CenterHorizontally) {
                Text(stringResource(R.string.mushaf_sure,sureAdi),fontFamily=Manrope,fontSize=15.sp,
                    fontWeight=FontWeight.ExtraBold,color=r.baslik,textAlign=TextAlign.Center,maxLines=1)
                Text(stringResource(R.string.mushaf_cuz_sayfa,sayfa.cuz,sayfa.no),fontFamily=Manrope,
                    fontSize=11.5.sp,fontWeight=FontWeight.SemiBold,color=r.ikincil,textAlign=TextAlign.Center)
            }
            Box(Modifier.size(44.dp).semantics { contentDescription="Yazı boyutu" }
                .clickable(role=Role.Button){scope.launch { depo.mushafYaziBoyutuKaydet(if(boyut>=40)22 else (boyut+2).coerceAtMost(40)) }},
                contentAlignment=Alignment.Center) {
                Text("Aa",fontFamily=Manrope,fontSize=15.sp,fontWeight=FontWeight.ExtraBold,color=r.baslik)
            }
            Spacer(Modifier.width(2.dp))
            Box(Modifier.size(44.dp).clickable(role=Role.Button){scope.launch { depo.mushafYerImiDegistir(sayfa.no) }},
                contentAlignment=Alignment.Center) {
                Icon(if(kayitli)ImDolu else ImBos,stringResource(if(kayitli)R.string.mushaf_im_kayitli else R.string.mushaf_im),
                    tint=Renk.AltinKoyu,modifier=Modifier.size(22.dp))
            }
        }
        Box(Modifier.weight(1f).fillMaxWidth()) {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                HorizontalPager(state=pager,modifier=Modifier.fillMaxSize()) { i ->
                    val p=veri.sayfalar[i]
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(bottom=12.dp)) {
                            MushafKagit(p,veri,boyut,kelime,kaydedilen?.sayfa==p.no) { secili=it }
                        }
                    }
                }
            }
            if(kelime!=null) {
                // Harf adları yalnız izinli Elif-Ba listesinden; kelimenin aslı değişmez.
                val h=remember(kelime,harfler) {
                    kelime.metin.mapNotNull { ch ->
                        val ar=if(ch in "ٱأإآ")'ا' else ch
                        harfler.firstOrNull { it.harf==ar.toString() }?.let { KelimeHarf(ch.toString(),it.ad,false) }
                    }
                }
                KelimeAltSayfasi(kelime.ayet,kelime.metin,null,h,null,false,"",
                    kapat={secili=null},kelimeyiDinle={},harfiDinle={},
                    modifier=Modifier.align(Alignment.BottomCenter))
            }
        }
        OynaticiCubugu(stringResource(R.string.mushaf_tilavet,sonKonum.ayet),
            stringResource(R.string.mushaf_ses_yok),0f,false,false,{},{},{})
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MushafKagit(p:KuranSayfa,veri:MushafVerisi,boyut:Int,secili:SeciliKelime?,kayitli:Boolean,sec:(SeciliKelime)->Unit) {
    val r=LocalTemaRenkleri.current
    val kagit=r.kagit
    val dis=RoundedCornerShape(6.dp)
    val ic=RoundedCornerShape(3.dp)
    Column(Modifier.fillMaxWidth().padding(horizontal=12.dp).shadow(6.dp,dis).clip(dis)
        .background(kagit).border(2.dp,Renk.AltinKoyu,dis).padding(7.dp)) {
        Column(Modifier.fillMaxWidth().border(1.dp,Renk.MushafIcCerceve,ic)
            .padding(start=10.dp,end=10.dp,top=12.dp,bottom=14.dp),
            horizontalAlignment=Alignment.CenterHorizontally) {
            p.ayetler.groupBy { it.sure }.forEach { (s,ayetler) ->
                val sure=veri.sureler[s-1]
                Row(Modifier.fillMaxWidth().background(Renk.YesilKoyu,RoundedCornerShape(30.dp))
                    .border(1.dp,Renk.AltinKoyu,RoundedCornerShape(30.dp)).padding(horizontal=14.dp,vertical=6.dp),
                    horizontalArrangement=Arrangement.spacedBy(10.dp,Alignment.CenterHorizontally),
                    verticalAlignment=Alignment.CenterVertically) {
                    Box(Modifier.size(8.dp).rotate(45f).background(Renk.Altin))
                    Text(sure.arapcaAd,fontFamily=AmiriQuran,fontSize=20.sp,lineHeight=30.sp,color=Renk.AltinAcik,
                        modifier=Modifier.padding(top=4.dp),textAlign=TextAlign.Center)
                    Box(Modifier.size(8.dp).rotate(45f).background(Renk.Altin))
                }
                Spacer(Modifier.height(6.dp))
                Text(stringResource(R.string.mushaf_tur_ayet,stringResource(if(sure.tur=="Meccan")R.string.mushaf_mekki else R.string.mushaf_medeni),sure.ayetSayisi),
                    fontFamily=Manrope,fontSize=11.sp,fontWeight=FontWeight.Bold,color=r.altinMetin,letterSpacing=.66.sp)
                // Tanzil'in ayrı bismillah niteliği yalnız sûre başlangıcında gösterilir.
                ayetler.first().besmele?.takeIf { it.isNotEmpty() }?.let {
                    Text(it,style=TextStyle(fontFamily=MushafYazi,fontSize=boyut.sp,lineHeight=(boyut*1.9f).sp,
                        textDirection=TextDirection.Rtl,color=r.metin),textAlign=TextAlign.Center)
                }
                Spacer(Modifier.height(8.dp))
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    FlowRow(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(1.dp,Alignment.CenterHorizontally),
                        verticalArrangement=Arrangement.spacedBy(2.dp)) {
                        ayetler.forEach { a ->
                            // Yalnız görünüm için kelime sınırları: kaynak XML ve tam âyet metni korunur.
                            kelimeDeseni.findAll(a.arapca).forEachIndexed { j,m ->
                                val k=SeciliKelime(p.no,a.sure,a.no,j,m.value)
                                Box(Modifier.heightIn(min=44.dp).clip(RoundedCornerShape(8.dp))
                                    .background(if(k==secili)Renk.KelimeSecili else Color.Transparent)
                                    .clickable(role=Role.Button){sec(k)}.padding(horizontal=3.dp),contentAlignment=Alignment.Center) {
                                    Text(m.value,style=TextStyle(fontFamily=MushafYazi,fontSize=boyut.sp,
                                        lineHeight=(boyut*1.9f).sp,textDirection=TextDirection.Rtl,
                                        color=if(k==secili)Renk.YesilKoyu else r.metin))
                                }
                            }
                            Box(Modifier.heightIn(min=44.dp).padding(horizontal=3.dp),contentAlignment=Alignment.Center) {
                                Box(Modifier.size(28.dp).border(1.5.dp,Renk.AltinKoyu,CircleShape)
                                    .padding(3.dp).border(1.dp,Renk.MushafIcCerceve,CircleShape)
                                    .semantics { contentDescription="${a.no}. âyetin sonu" },contentAlignment=Alignment.Center) {
                                    Text(arapRakam(a.no),fontFamily=Amiri,fontSize=15.sp,color=r.altinMetin)
                                }
                            }
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
            if(kayitli) {
                Spacer(Modifier.height(2.dp))
                Row(Modifier.background(Renk.YesilAcik,RoundedCornerShape(20.dp)).padding(horizontal=10.dp,vertical=5.dp),
                    horizontalArrangement=Arrangement.spacedBy(6.dp),verticalAlignment=Alignment.CenterVertically) {
                    Icon(ImDolu,null,tint=Renk.Zumrut,modifier=Modifier.size(13.dp))
                    Text(stringResource(R.string.mushaf_kaydedildi),fontFamily=Manrope,fontSize=11.5.sp,
                        fontWeight=FontWeight.Bold,color=Renk.Zumrut)
                }
            }
            Spacer(Modifier.height(6.dp))
            Text(arapRakam(p.no),fontFamily=Amiri,fontSize=14.sp,color=r.altinMetin)
        }
    }
}
