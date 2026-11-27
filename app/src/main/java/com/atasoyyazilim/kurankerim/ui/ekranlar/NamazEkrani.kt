package com.atasoyyazilim.kurankerim.ui.ekranlar

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.atasoyyazilim.kurankerim.R
import com.atasoyyazilim.kurankerim.data.IcerikDeposu
import com.atasoyyazilim.kurankerim.data.NamazAdimi
import com.atasoyyazilim.kurankerim.ses.SesliOkuyucu
import com.atasoyyazilim.kurankerim.ui.bilesenler.*
import com.atasoyyazilim.kurankerim.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private fun ikon(ad:String,d:String)=ImageVector.Builder(ad,22.dp,22.dp,24f,24f).apply{
    addPath(pathData=addPathNodes(d),stroke=SolidColor(Color.Black),strokeLineWidth=2f,strokeLineCap=StrokeCap.Round,strokeLineJoin=StrokeJoin.Round)
}.build()
private val Geri=ikon("geri","M15 5l-7 7 7 7")
private val Ses=ikon("ses","M4 9v6h4l5 4V5L8 9z M16 9a4 4 0 0 1 0 6")
@Composable private fun NY(m:String,s:Float,r:Color,k:FontWeight=FontWeight.Normal){
    Text(m,fontFamily=Manrope,fontSize=s.sp,color=r,fontWeight=k)
}

@Composable
fun NamazEkrani(geri:()->Unit,kaza:()->Unit){
    val c=LocalContext.current
    val r=LocalTemaRenkleri.current
    var mod by rememberSaveable{mutableIntStateOf(0)}
    var sira by rememberSaveable{mutableIntStateOf(0)}
    val veri by produceState<Pair<List<NamazAdimi>,List<NamazAdimi>>?>(null,c){
        value=withContext(Dispatchers.IO){
            val d=IcerikDeposu(c)
            runCatching{d.abdestAdimlari() to d.namazAdimlari()}.getOrDefault(emptyList<NamazAdimi>() to emptyList())
        }
    }
    val liste=if(mod==0)veri?.first.orEmpty() else veri?.second.orEmpty()
    val adim=liste.getOrNull(sira.coerceIn(0,(liste.size-1).coerceAtLeast(0)))
    var sesHazir by remember{mutableStateOf(false)}
    var sesEksik by remember{mutableStateOf(false)}
    var okuyucu by remember{mutableStateOf<SesliOkuyucu?>(null)}
    val lifecycle=LocalLifecycleOwner.current.lifecycle
    DisposableEffect(c,lifecycle){
        val o=SesliOkuyucu(c){sesHazir=it}
        okuyucu=o
        val observer=LifecycleEventObserver{_,event->if(event==Lifecycle.Event.ON_STOP)o.durdur()}
        lifecycle.addObserver(observer)
        onDispose{lifecycle.removeObserver(observer);o.durdur();o.kapat();okuyucu=null}
    }
    LaunchedEffect(mod,sira){okuyucu?.durdur()}
    if(sesEksik)AlertDialog(onDismissRequest={sesEksik=false},
        text={Text(stringResource(R.string.namaz_turkce_ses_yok))},
        confirmButton={TextButton(onClick={sesEksik=false}){Text(stringResource(R.string.kapat))}})
    Column(Modifier.fillMaxSize().background(r.zemin).windowInsetsPadding(WindowInsets.safeDrawing)){
        Row(Modifier.fillMaxWidth().height(60.dp).padding(horizontal=8.dp),verticalAlignment=Alignment.CenterVertically){
            Box(Modifier.size(44.dp).clickable(role=Role.Button,onClick=geri),contentAlignment=Alignment.Center){Icon(Geri,stringResource(R.string.mushaf_geri),tint=r.baslik)}
            Text(stringResource(R.string.namaz_baslik),Modifier.weight(1f),fontFamily=Manrope,fontSize=19.sp,fontWeight=FontWeight.ExtraBold,color=r.baslik)
            Box(Modifier.heightIn(min=44.dp).clickable(role=Role.Button,onClick=kaza).padding(end=4.dp),contentAlignment=Alignment.Center){
                Box(Modifier.height(40.dp).background(r.kart,RoundedCornerShape(20.dp)).border(1.dp,r.kenar,RoundedCornerShape(20.dp)).padding(horizontal=12.dp),contentAlignment=Alignment.Center){
                    NY(stringResource(R.string.namaz_kaza),12.5f,r.baslik,FontWeight.ExtraBold)
                }
            }
        }
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState())){
            SegmentKontrol(listOf(stringResource(R.string.namaz_abdest),stringResource(R.string.namaz_iki_rekat)),mod,{mod=it;sira=0},Modifier.padding(horizontal=16.dp))
            Spacer(Modifier.height(14.dp))
            if(adim==null){
                if(veri!=null)BosDurum(Ses)
            }else{
                Box(Modifier.fillMaxWidth().padding(horizontal=16.dp).height(236.dp).clip(RoundedCornerShape(20.dp)).background(Renk.YesilKoyu)){
                    Canvas(Modifier.matchParentSize()){
                        val t=40.dp.toPx();val f=t/40
                        for(y in 0..(size.height/t).toInt())for(x in 0..(size.width/t).toInt()){
                            translate(x*t,y*t){
                                val p=Path().apply{
                                    moveTo(20*f,2*f);listOf(25f to 15f,38f to 20f,25f to 25f,20f to 38f,15f to 25f,2f to 20f,15f to 15f).forEach{(a,b)->lineTo(a*f,b*f)};close()
                                }
                                drawPath(p,Renk.Altin.copy(alpha=.08f),style=Stroke(1.dp.toPx()))
                            }
                        }
                    }
                    Column(Modifier.align(Alignment.Center),horizontalAlignment=Alignment.CenterHorizontally){
                        NamazCizimi(adim.cizim,stringResource(R.string.namaz_cizim,adim.gorselAciklama),Modifier.size(220.dp,180.dp))
                        Spacer(Modifier.height(4.dp))
                        NY(adim.gorselAciklama,12f,Renk.KoyuMetinSoluk,FontWeight.Bold)
                    }
                    Box(Modifier.align(Alignment.TopEnd).padding(top=10.dp,end=12.dp).heightIn(min=44.dp).clickable(role=Role.Button){
                        if(sesHazir)okuyucu?.oku(adim.aciklama) else sesEksik=true
                    },contentAlignment=Alignment.Center){
                        Row(Modifier.height(40.dp).background(Renk.Altin,RoundedCornerShape(20.dp)).padding(horizontal=12.dp),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(6.dp)){
                            Icon(Ses,null,Modifier.size(14.dp),tint=Renk.YesilKoyu)
                            NY(stringResource(R.string.namaz_sesli_anlat),12.5f,Renk.YesilKoyu,FontWeight.ExtraBold)
                        }
                    }
                }
                Spacer(Modifier.height(14.dp))
                Row(Modifier.fillMaxWidth().padding(horizontal=16.dp),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(10.dp)){
                    Text(stringResource(R.string.namaz_adim,sira+1,liste.size),fontFamily=Manrope,fontSize=12.sp,fontWeight=FontWeight.ExtraBold,letterSpacing=.72.sp,color=r.altinMetin)
                    Box(Modifier.weight(1f).height(5.dp).clip(RoundedCornerShape(3.dp)).background(Renk.SegmentZemin)){
                        Box(Modifier.fillMaxWidth((sira+1f)/liste.size).fillMaxHeight().background(Renk.Zumrut,RoundedCornerShape(3.dp)))
                    }
                    if(adim.hukum.isNotBlank())Box(Modifier.background(Renk.YesilAcik,RoundedCornerShape(10.dp)).padding(horizontal=9.dp,vertical=3.dp)){
                        NY(adim.hukum,11f,Renk.Zumrut,FontWeight.ExtraBold)
                    }
                }
                Text(adim.baslik,Modifier.padding(start=16.dp,end=16.dp,top=8.dp),fontFamily=Manrope,fontSize=21.sp,fontWeight=FontWeight.ExtraBold,color=r.baslik)
                Text(adim.aciklama,Modifier.padding(start=16.dp,end=16.dp,top=6.dp),fontFamily=Manrope,fontSize=15.sp,lineHeight=24.sp,color=r.metin)
                if(adim.arapca.isNotBlank()){
                    Row(Modifier.fillMaxWidth().padding(start=16.dp,end=16.dp,top=12.dp).background(r.kart,RoundedCornerShape(16.dp))
                        .border(1.dp,r.kenar,RoundedCornerShape(16.dp)).padding(horizontal=14.dp,vertical=12.dp),
                        verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(10.dp)){
                        Column(Modifier.weight(1f)){
                            Text(stringResource(R.string.namaz_okunur),fontFamily=Manrope,fontSize=11.sp,fontWeight=FontWeight.ExtraBold,letterSpacing=.66.sp,color=r.altinMetin)
                            Spacer(Modifier.height(2.dp));NY(adim.okunus,14.5f,r.baslik,FontWeight.ExtraBold)
                        }
                        Text(adim.arapca,Modifier.weight(1f),style=TextStyle(fontFamily=AmiriQuran,fontSize=22.sp,lineHeight=39.6.sp,textDirection=TextDirection.Rtl,color=r.baslik,textAlign=TextAlign.End))
                        // İzinli dua ses kaydı bulunmadığından Arapça seslendirme yapılmaz.
                        Box(Modifier.size(44.dp).background(Renk.YesilKoyu.copy(alpha=.35f),RoundedCornerShape(22.dp)),contentAlignment=Alignment.Center){
                            NY("▶",15f,Renk.Altin)
                        }
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
        }
        Row(Modifier.fillMaxWidth().padding(start=16.dp,end=16.dp,bottom=20.dp,top=8.dp),horizontalArrangement=Arrangement.spacedBy(10.dp)){
            val shape=RoundedCornerShape(14.dp)
            Box(Modifier.weight(1f).height(52.dp).clip(shape).background(r.kart).border(1.dp,r.kenar,shape).clickable(enabled=adim!=null&&sira>0,role=Role.Button){sira--},contentAlignment=Alignment.Center){
                NY(stringResource(R.string.namaz_onceki),15f,r.metin.copy(alpha=if(sira>0)1f else .45f),FontWeight.ExtraBold)
            }
            Box(Modifier.weight(1f).height(52.dp).clip(shape).background(Renk.YesilKoyu).clickable(enabled=adim!=null,role=Role.Button){sira=if(sira>=liste.lastIndex)0 else sira+1},contentAlignment=Alignment.Center){
                NY(stringResource(if(adim!=null&&sira==liste.lastIndex)R.string.namaz_bastan else R.string.namaz_sonraki),15f,Renk.Beyaz,FontWeight.ExtraBold)
            }
        }
    }
}
