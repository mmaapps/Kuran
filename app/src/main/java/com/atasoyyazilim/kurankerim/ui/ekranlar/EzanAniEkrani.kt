package com.atasoyyazilim.kurankerim.ui.ekranlar

import android.provider.Settings
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.atasoyyazilim.kurankerim.R
import com.atasoyyazilim.kurankerim.data.IcerikDeposu
import com.atasoyyazilim.kurankerim.ses.EzanServisi
import com.atasoyyazilim.kurankerim.ui.bilesenler.HatMadalyonu
import com.atasoyyazilim.kurankerim.ui.bilesenler.BosDurum
import com.atasoyyazilim.kurankerim.ui.theme.*
import com.atasoyyazilim.kurankerim.vakit.Vakit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.cos
import kotlin.math.PI

private val Oynat=ImageVector.Builder("Oynat",22.dp,22.dp,24f,24f).apply {
    addPath(pathData=addPathNodes("M8 5l11 7-11 7z"),fill=SolidColor(Color.Black))
}.build()
private val Duraklat=ImageVector.Builder("Duraklat",22.dp,22.dp,24f,24f).apply {
    addPath(pathData=addPathNodes("M7 5h3v14H7zM14 5h3v14h-3z"),fill=SolidColor(Color.Black))
}.build()
private val yukseklikler=listOf(18,30,44,26,52,36,22,48,56,40,30,46,54,34,24,42,50,28,38,22,16)

@Composable
private fun EzanDeseni(modifier:Modifier) {
    Canvas(modifier) {
        val t=56.dp.toPx();val k=t/56f
        for(y in 0..(size.height/t).toInt()) for(x in 0..(size.width/t).toInt()){
            val ox=x*t;val oy=y*t
            fun c(a:Float,b:Float)=Offset(ox+a*k,oy+b*k)
            val p=Path().apply{
                moveTo(c(28f,2f).x,c(28f,2f).y)
                listOf(35f to 21f,54f to 28f,35f to 35f,28f to 54f,21f to 35f,2f to 28f,21f to 21f).forEach{(a,b)->lineTo(c(a,b).x,c(a,b).y)}
                close()
            }
            drawPath(p,Renk.Altin.copy(alpha=.07f),style=Stroke(1.dp.toPx()))
            val d=12f*k*1.41421356f
            val diamond=Path().apply{moveTo(ox+28*k,oy+28*k-d);lineTo(ox+28*k+d,oy+28*k);lineTo(ox+28*k,oy+28*k+d);lineTo(ox+28*k-d,oy+28*k);close()}
            drawPath(diamond,Renk.Altin.copy(alpha=.07f),style=Stroke(1.dp.toPx()))
        }
    }
}
@Composable
private fun Dalga(caliyor:Boolean) {
    val c=LocalContext.current
    val hareket=remember(c){Settings.Global.getFloat(c.contentResolver,Settings.Global.ANIMATOR_DURATION_SCALE,1f)!=0f}
    var ms by remember{mutableLongStateOf(0L)}
    LaunchedEffect(caliyor,hareket) {
        if(caliyor&&hareket) {val bas=withFrameNanos{it};while(true){ms=withFrameNanos{(it-bas)/1_000_000}}}
    }
    Canvas(Modifier.fillMaxWidth().height(56.dp)) {
        val w=5.dp.toPx();val gap=5.dp.toPx();val left=(size.width-(21*w+20*gap))/2
        yukseklikler.forEachIndexed{i,h->
            val oran=if(!hareket)1f else if(!caliyor)0.35f else (0.35+0.65*(1-cos(2*PI*(ms-i*70)/1100.0))/2).toFloat()
            val hh=h.dp.toPx()*oran
            drawRoundRect(Renk.Altin,Offset(left+i*(w+gap),(size.height-hh)/2),Size(w,hh),CornerRadius(3.dp.toPx()))
        }
    }
}

@Composable
fun EzanAniEkrani(kapat:()->Unit,susturSonrasi:()->Unit) {
    val c=LocalContext.current
    val durum by EzanServisi.durum.collectAsState()
    val d=durum
    val ayet by produceState<Pair<String,String>?>(null,d?.sure,d?.ayet) {
        value=null
        val s=d?.sure;val a=d?.ayet
        if(s!=null&&a!=null) value=withContext(Dispatchers.IO){
            val v=IcerikDeposu(c).mushaf()
            v?.ayet(s,a)?.let { it.arapca to (v.sureler[s-1].latinAd+" · "+a) }
        }
    }
    val ad=d?.let{stringResource(if(it.vakit==Vakit.IMSAK)R.string.ana_sabah else it.vakit.adRes)}
    val baslik=if(ad==null)stringResource(R.string.kanal_ezan)else stringResource(R.string.ezan_ani_baslik,ad)
    val ayetAsamasi=d?.sure!=null
    Box(Modifier.fillMaxSize().background(Renk.YesilKoyu)) {
        EzanDeseni(Modifier.matchParentSize())
        Column(Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {
            Column(Modifier.weight(1f).verticalScroll(rememberScrollState())) {
                Row(Modifier.fillMaxWidth().padding(start=22.dp,end=22.dp,top=26.dp),
                    horizontalArrangement=Arrangement.SpaceBetween,verticalAlignment=Alignment.CenterVertically){
                    HatMadalyonu("اللّٰه",stringResource(R.string.ana_allah),boyut=92.dp,yaziBoyutu=36.sp)
                    Box(Modifier.sizeIn(minWidth=44.dp,minHeight=44.dp).clickable(role=Role.Button,onClick=kapat).padding(12.dp),contentAlignment=Alignment.Center){
                        Text(stringResource(R.string.kapat),fontFamily=Manrope,fontSize=13.sp,fontWeight=FontWeight.Bold,color=Renk.AltinAcik)
                    }
                    HatMadalyonu("مُحَمَّد",stringResource(R.string.ana_muhammed),boyut=92.dp,yaziBoyutu=30.sp)
                }
                Spacer(Modifier.height(26.dp))
                Column(Modifier.fillMaxWidth(),horizontalAlignment=Alignment.CenterHorizontally){
                    Text(stringResource(R.string.ezan_ani_etiket),fontFamily=Manrope,fontSize=12.sp,letterSpacing=1.92.sp,fontWeight=FontWeight.ExtraBold,color=Renk.Altin)
                    Spacer(Modifier.height(6.dp))
                    Text(baslik,fontFamily=Manrope,fontSize=38.sp,fontWeight=FontWeight.ExtraBold,color=Renk.Beyaz,textAlign=TextAlign.Center)
                    d?.let { Text(it.sehir+" · "+DateTimeFormatter.ofPattern("HH:mm").withZone(ZoneId.systemDefault()).format(it.zaman),
                        fontFamily=Manrope,fontSize=14.sp,fontWeight=FontWeight.SemiBold,color=Renk.KoyuMetin) }
                }
                Spacer(Modifier.height(18.dp));Dalga(d?.caliyor==true);Spacer(Modifier.height(18.dp))
                Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(8.dp,Alignment.CenterHorizontally)){
                    listOf(R.string.ezan_ani_asama_ezan,R.string.ezan_ani_asama_ayet).forEachIndexed{i,etiket->
                        val aktif=d!=null && (i==1)==ayetAsamasi
                        val sekil=RoundedCornerShape(18.dp)
                        Box(Modifier.height(44.dp),contentAlignment=Alignment.Center){
                            Box(Modifier.height(36.dp).clip(sekil).background(if(aktif)Renk.Altin else Color.Transparent)
                                .border(1.dp,if(aktif)Renk.Altin else Renk.Beyaz.copy(alpha=.3f),sekil).padding(horizontal=14.dp),contentAlignment=Alignment.Center){
                                Text(stringResource(etiket),fontFamily=Manrope,fontSize=12.5.sp,fontWeight=FontWeight.ExtraBold,color=if(aktif)Renk.YesilKoyu else Renk.Beyaz)
                            }
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
                Column(Modifier.fillMaxWidth().padding(horizontal=18.dp).heightIn(min=190.dp)
                    .background(Renk.Beyaz.copy(alpha=.06f),RoundedCornerShape(22.dp))
                    .border(1.dp,Renk.Altin.copy(alpha=.35f),RoundedCornerShape(22.dp)).padding(18.dp)){
                    Text(stringResource(if(ayetAsamasi)R.string.ezan_ani_ayet else R.string.ezan_ani_okunan),fontFamily=Manrope,fontSize=11.5.sp,letterSpacing=1.15.sp,fontWeight=FontWeight.ExtraBold,color=Renk.Altin)
                    val a=ayet
                    if(ayetAsamasi&&a!=null){
                        Spacer(Modifier.height(10.dp))
                        Text(a.first,Modifier.fillMaxWidth(),style=TextStyle(fontFamily=MushafYazi,fontSize=28.sp,lineHeight=53.2.sp,textDirection=TextDirection.Rtl,color=Renk.Beyaz,textAlign=TextAlign.Center))
                        Text(a.second,Modifier.fillMaxWidth(),fontFamily=Manrope,fontSize=13.sp,color=Renk.KoyuMetinSoluk,textAlign=TextAlign.Center)
                    } else BosDurum(Oynat,metin=stringResource(R.string.bos_durum),renk=Renk.KoyuMetin)
                }
            }
            Row(Modifier.fillMaxWidth().padding(start=18.dp,end=18.dp,bottom=28.dp,top=12.dp),
                horizontalArrangement=Arrangement.spacedBy(10.dp)){
                val sekil=RoundedCornerShape(16.dp)
                Box(Modifier.size(60.dp,56.dp).clip(sekil).background(Renk.Altin.copy(alpha=if(d==null).45f else 1f))
                    .clickable(enabled=d!=null,role=Role.Button){EzanServisi.komut(c,EzanServisi.EYLEM_DURAKLAT)},contentAlignment=Alignment.Center){
                    Icon(if(d?.caliyor==true)Duraklat else Oynat,stringResource(if(d?.caliyor==true)R.string.duraklat else R.string.oynat),tint=Renk.YesilKoyu)
                }
                Box(Modifier.weight(1f).height(56.dp).clip(sekil).border(1.dp,Renk.Beyaz.copy(alpha=.3f),sekil)
                    .clickable(role=Role.Button){EzanServisi.komut(c,EzanServisi.EYLEM_SUSTUR);susturSonrasi()},contentAlignment=Alignment.Center){
                    Text(stringResource(R.string.ezan_ani_sustur),fontFamily=Manrope,fontSize=15.sp,fontWeight=FontWeight.ExtraBold,color=Renk.Beyaz)
                }
            }
        }
    }
}
