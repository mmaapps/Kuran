package com.atasoyyazilim.kurankerim.ui.ekranlar

import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
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
import com.atasoyyazilim.kurankerim.data.Harf
import com.atasoyyazilim.kurankerim.data.IcerikDeposu
import com.atasoyyazilim.kurankerim.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private data class HarfVerisi(val harfler:List<Harf>,val birlesmeyen:String,val sesler:Set<Int>)

/** Kaynak JSON'un 28 harfi ve birleştirme bilgisi; yalnız izinli, paket içi kayıt çalınır. */
@Composable
fun ElifBaEkrani(geri:()->Unit){
    val c=LocalContext.current
    val r=LocalTemaRenkleri.current
    val veri by produceState<HarfVerisi?>(null,c){
        value=withContext(Dispatchers.IO){
            runCatching{
                val d=IcerikDeposu(c)
                val liste=d.harfler().sortedBy{it.sira}
                require(liste.map{it.sira}==(1..28).toList()&&liste.map{it.harf}.distinct().size==28)
                val birlesmeyen=d.jsonOku("elifba/harfler.json")?.getString("birlesmeyenler")?:error("birleşim bilgisi eksik")
                val ses=liste.filter{h->h.sesDosyasi.matches(Regex("[0-9]{2}_[a-z]+\\.m4a"))&&
                    runCatching{c.assets.openFd("ses/harf/${h.sesDosyasi}").use{}}.isSuccess}.map{it.sira}.toSet()
                HarfVerisi(liste,birlesmeyen,ses)
            }.getOrElse{HarfVerisi(emptyList(),"",emptySet())}
        }
    }
    val liste=veri?.harfler.orEmpty()
    var secili by rememberSaveable{mutableIntStateOf(2)}
    val harf=liste.firstOrNull{it.sira==secili}?:liste.firstOrNull()
    var sesDurumu by remember{mutableIntStateOf(0)}
    var havuz by remember{mutableStateOf<SoundPool?>(null)}
    var bekleyen by remember{mutableIntStateOf(0)}
    var yuklenen by remember{mutableIntStateOf(0)}
    var akis by remember{mutableIntStateOf(0)}
    val lifecycle=LocalLifecycleOwner.current.lifecycle
    DisposableEffect(c,lifecycle){
        val p=SoundPool.Builder().setMaxStreams(1).setAudioAttributes(AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA).setContentType(AudioAttributes.CONTENT_TYPE_SPEECH).build()).build()
        val handler=Handler(Looper.getMainLooper())
        havuz=p
        p.setOnLoadCompleteListener{_,id,status->handler.post{
            if(havuz===p&&id==bekleyen){
                bekleyen=0
                if(status==0){akis=p.play(id,1f,1f,1,0,1f);sesDurumu=if(akis!=0)2 else 3}
                else sesDurumu=3
            }
        }}
        val observer=LifecycleEventObserver{_,e->if(e==Lifecycle.Event.ON_STOP){
            bekleyen=0
            if(akis!=0)p.stop(akis)
            akis=0
            if(yuklenen!=0)p.unload(yuklenen)
            yuklenen=0;sesDurumu=0
        }}
        lifecycle.addObserver(observer)
        onDispose{lifecycle.removeObserver(observer);havuz=null;handler.removeCallbacksAndMessages(null);p.release()}
    }
    fun dinle(h:Harf){
        val p=havuz?:return
        bekleyen=0
        if(akis!=0)p.stop(akis)
        akis=0
        if(yuklenen!=0)p.unload(yuklenen)
        yuklenen=0;sesDurumu=0
        if(h.sira !in veri?.sesler.orEmpty())return
        runCatching{
            c.assets.openFd("ses/harf/${h.sesDosyasi}").use{fd->yuklenen=p.load(fd,1)}
            bekleyen=yuklenen;sesDurumu=if(yuklenen!=0)1 else 3
        }.onFailure{sesDurumu=3}
    }
    fun sec(h:Harf){secili=h.sira;dinle(h)}
    Column(Modifier.fillMaxSize().background(r.zemin).windowInsetsPadding(WindowInsets.safeDrawing)){
        Row(Modifier.fillMaxWidth().height(60.dp).padding(horizontal=8.dp),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(4.dp)){
            Box(Modifier.size(44.dp).clickable(role=Role.Button,onClick=geri).semantics{contentDescription=c.getString(R.string.mushaf_geri)},contentAlignment=Alignment.Center){
                Canvas(Modifier.size(22.dp)){
                    val p=Path().apply{moveTo(size.width*.625f,size.height*.208f);lineTo(size.width*.333f,size.height*.5f);lineTo(size.width*.625f,size.height*.792f)}
                    drawPath(p,r.baslik,style=Stroke(2.dp.toPx(),cap=StrokeCap.Round))
                }
            }
            Text(stringResource(R.string.elifba_baslik),Modifier.weight(1f),fontFamily=Manrope,fontSize=19.sp,fontWeight=FontWeight.ExtraBold,color=r.baslik)
            Text(stringResource(R.string.elifba_harfler,liste.size),Modifier.padding(end=10.dp),fontFamily=Manrope,fontSize=12.5.sp,fontWeight=FontWeight.Bold,color=r.ikincil)
        }
        if(harf!=null){
            val ton=OgrenmeRenkleri.kart(harf.sira)
            val birlesir=harf.harf !in (veri?.birlesmeyen.orEmpty())
            val sesVar=harf.sira in veri?.sesler.orEmpty()
            Row(Modifier.fillMaxWidth().padding(horizontal=16.dp).clip(RoundedCornerShape(22.dp))
                .background(Brush.linearGradient(listOf(ton.zemin,Renk.Kagit))).border(1.dp,ton.kenar,RoundedCornerShape(22.dp)).padding(14.dp),
                verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(10.dp)){
                Box(Modifier.size(96.dp).clip(RoundedCornerShape(20.dp)).background(ton.kenar)
                    .border(1.5.dp,ton.vurgu,RoundedCornerShape(20.dp)),contentAlignment=Alignment.Center){
                    Text(harf.harf,style=TextStyle(fontFamily=AmiriQuran,fontSize=54.sp,lineHeight=70.sp,color=ton.vurgu,textDirection=TextDirection.Rtl))
                }
                Column(Modifier.weight(1f)){
                    Text(harf.ad,fontFamily=Manrope,fontSize=22.sp,fontWeight=FontWeight.ExtraBold,color=Renk.YesilKoyu)
                    Text(harf.not.ifBlank{stringResource(if(birlesir)R.string.elifba_birlesir else R.string.elifba_birlesmez)},fontFamily=Manrope,fontSize=12.5.sp,color=Renk.MetinIkincil)
                    if(birlesir){
                        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl){
                            Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){
                                listOf(harf.harf+"ـ","ـ"+harf.harf+"ـ","ـ"+harf.harf).forEach{metin->
                                    Text(metin,style=TextStyle(fontFamily=Amiri,fontSize=24.sp,color=ton.vurgu,textDirection=TextDirection.Rtl))
                                }
                            }
                        }
                        Text(stringResource(R.string.elifba_bicimler),fontFamily=Manrope,fontSize=10.5.sp,fontWeight=FontWeight.Bold,color=Renk.MetinIkincil)
                    }
                }
                Box(Modifier.size(52.dp).clip(CircleShape).background(if(sesVar)ton.vurgu else ton.kenar)
                    .clickable(enabled=sesVar,role=Role.Button){dinle(harf)}.semantics{contentDescription=c.getString(R.string.elifba_dinle)},contentAlignment=Alignment.Center){
                    Canvas(Modifier.size(20.dp)){
                        val f=size.width/24f
                        val p=Path().apply{moveTo(4*f,9*f);lineTo(4*f,15*f);lineTo(8*f,15*f);lineTo(13*f,19*f);lineTo(13*f,5*f);lineTo(8*f,9*f);close()}
                        val renk=if(sesVar)Renk.Beyaz else ton.vurgu.copy(alpha=.5f)
                        drawPath(p,renk,style=Stroke(2.2f*f,cap=StrokeCap.Round))
                        drawArc(renk,-45f,90f,false,Offset(10*f,6*f),androidx.compose.ui.geometry.Size(12*f,12*f),style=Stroke(2.2f*f,cap=StrokeCap.Round))
                    }
                }
            }
            Text(stringResource(when{
                !sesVar->R.string.elifba_ses_yok
                sesDurumu==1->R.string.elifba_yukleniyor
                sesDurumu==2->R.string.elifba_baslatildi
                sesDurumu==3->R.string.elifba_ses_hata
                else->R.string.elifba_dokun
            }),Modifier.fillMaxWidth().padding(start=16.dp,end=16.dp,top=6.dp,bottom=6.dp),fontFamily=Manrope,fontSize=12.5.sp,fontWeight=FontWeight.Bold,color=r.aktif)
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl){
                LazyVerticalGrid(columns=GridCells.Fixed(4),modifier=Modifier.weight(1f).fillMaxWidth(),
                    contentPadding=PaddingValues(horizontal=16.dp,vertical=4.dp),horizontalArrangement=Arrangement.spacedBy(6.dp),verticalArrangement=Arrangement.spacedBy(6.dp)){
                    items(liste,key={it.sira}){item->
                        val aktif=item.sira==harf.sira
                        val renk=OgrenmeRenkleri.kart(item.sira)
                        val shape=RoundedCornerShape(14.dp)
                        Column(Modifier.heightIn(min=70.dp).clip(shape).background(renk.zemin)
                            .border(if(aktif)2.5.dp else 1.dp,if(aktif)renk.vurgu else renk.kenar,shape)
                            .selectable(aktif,role=Role.Button){sec(item)}.padding(4.dp),
                            horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.Center){
                            Text(item.harf,style=TextStyle(fontFamily=AmiriQuran,fontSize=30.sp,lineHeight=39.sp,color=renk.vurgu,textDirection=TextDirection.Rtl,textAlign=TextAlign.Center))
                            Text(item.ad,fontFamily=Manrope,fontSize=11.5.sp,fontWeight=FontWeight.ExtraBold,color=Renk.Metin)
                        }
                    }
                }
            }
            Row(Modifier.fillMaxWidth().padding(16.dp),horizontalArrangement=Arrangement.spacedBy(10.dp)){
                listOf(-1,1).forEach{yon->
                    val hedef=liste.firstOrNull{it.sira==harf.sira+yon}
                    val shape=RoundedCornerShape(14.dp)
                    Box(Modifier.weight(1f).height(48.dp).clip(shape).background(if(yon==1)ton.vurgu else ton.zemin)
                        .border(1.dp,ton.kenar,shape).clickable(enabled=hedef!=null,role=Role.Button){hedef?.let{sec(it)}},contentAlignment=Alignment.Center){
                        Text(stringResource(if(yon<0)R.string.namaz_onceki else R.string.namaz_sonraki),fontFamily=Manrope,fontSize=14.sp,fontWeight=FontWeight.ExtraBold,color=(if(yon==1)Renk.Beyaz else ton.vurgu).copy(alpha=if(hedef!=null)1f else .45f))
                    }
                }
            }
        }else if(veri!=null){
            Text(stringResource(R.string.bos_durum),Modifier.fillMaxWidth().padding(16.dp),fontFamily=Manrope,fontSize=15.sp,color=r.ikincil,textAlign=TextAlign.Center)
        }
    }
}
