package com.atasoyyazilim.kurankerim.ui.ekranlar

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
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
import com.atasoyyazilim.kurankerim.data.Esma
import com.atasoyyazilim.kurankerim.data.IcerikDeposu
import com.atasoyyazilim.kurankerim.ses.SesliOkuyucu
import com.atasoyyazilim.kurankerim.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Esma.dc.html — kart 22 dp, 18 dp iç boşluk, üç sütun/8 dp ara/92 dp isim kartı.
 * İsimler yalnız onay bekleyen mevcut içerik dosyasından okunur. Eksik isim üretilmez.
 */
@Composable
fun EsmaEkrani(geri:()->Unit){
    val c=LocalContext.current
    val r=LocalTemaRenkleri.current
    val isimler by produceState<List<Esma>?>(null,c){
        value=withContext(Dispatchers.IO){
            runCatching{IcerikDeposu(c).esma().also{liste->
                require(liste.size<=99 && liste.map{it.no}.distinct().size==liste.size)
                require(liste.all{it.no in 1..99 && it.arapca.isNotBlank() && it.okunus.isNotBlank() && it.anlam.isNotBlank()})
            }.sortedBy{it.no}}.getOrDefault(emptyList())
        }
    }
    val liste=isimler.orEmpty()
    var secili by rememberSaveable{mutableIntStateOf(2)}
    val isim=liste.firstOrNull{it.no==secili}?:liste.firstOrNull()
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
    if(sesEksik)AlertDialog(onDismissRequest={sesEksik=false},
        text={Text(stringResource(R.string.namaz_turkce_ses_yok))},
        confirmButton={TextButton(onClick={sesEksik=false}){Text(stringResource(R.string.kapat))}})
    Column(Modifier.fillMaxSize().background(r.zemin).windowInsetsPadding(WindowInsets.safeDrawing)){
        Row(Modifier.fillMaxWidth().height(60.dp).padding(horizontal=8.dp),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(4.dp)){
            Box(Modifier.size(44.dp).clickable(role=Role.Button,onClick=geri)
                .semantics{contentDescription=c.getString(R.string.mushaf_geri)},contentAlignment=Alignment.Center){
                Canvas(Modifier.size(22.dp)){
                    val p=Path().apply{moveTo(size.width*.625f,size.height*.208f);lineTo(size.width*.333f,size.height*.5f);lineTo(size.width*.625f,size.height*.792f)}
                    drawPath(p,r.baslik,style=Stroke(2.dp.toPx(),cap=StrokeCap.Round))
                }
            }
            Text(stringResource(R.string.esma_baslik),Modifier.weight(1f),fontFamily=Manrope,fontSize=19.sp,fontWeight=FontWeight.ExtraBold,color=r.baslik)
            Text(stringResource(if(liste.size==99)R.string.esma_tam_sayi else R.string.esma_mevcut_sayi,liste.size),
                Modifier.padding(end=10.dp),fontFamily=Manrope,fontSize=12.5.sp,fontWeight=FontWeight.Bold,color=r.ikincil)
        }
        if(isim!=null){
            val ton=OgrenmeRenkleri.kart(isim.no)
            Box(Modifier.fillMaxWidth().padding(start=16.dp,end=16.dp,top=4.dp)
                .clip(RoundedCornerShape(22.dp)).background(Brush.linearGradient(listOf(ton.zemin,Renk.Kagit))).border(1.dp,ton.kenar,RoundedCornerShape(22.dp))){
                Canvas(Modifier.matchParentSize()){
                    val f=150.dp.toPx()/48f
                    translate(-40.dp.toPx(),size.height-100.dp.toPx()){
                        val p=Path().apply{
                            moveTo(24*f,2*f)
                            listOf(30f to 18f,46f to 24f,30f to 30f,24f to 46f,18f to 30f,2f to 24f,18f to 18f).forEach{(a,b)->lineTo(a*f,b*f)}
                            close()
                        }
                        drawPath(p,ton.vurgu.copy(alpha=.13f),style=Stroke(f))
                        rotate(45f,Offset(24*f,24*f)){
                            drawRect(ton.vurgu.copy(alpha=.13f),Offset(14*f,14*f),Size(20*f,20*f),style=Stroke(f))
                        }
                    }
                }
                Column(Modifier.fillMaxWidth().padding(18.dp),horizontalAlignment=Alignment.CenterHorizontally){
                    Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween,verticalAlignment=Alignment.CenterVertically){
                        Text(stringResource(R.string.esma_isim_no,isim.no),fontFamily=Manrope,fontSize=12.sp,letterSpacing=1.2.sp,fontWeight=FontWeight.ExtraBold,color=ton.vurgu)
                        Box(Modifier.size(44.dp).clip(CircleShape).background(ton.vurgu)
                            .clickable(role=Role.Button){
                                // Arapça metin TTS'ye verilmez. Yalnız Türkçe okunuş ve anlam.
                                if(sesHazir)okuyucu?.oku(isim.okunus+". "+isim.anlam)else sesEksik=true
                            }.semantics{contentDescription=c.getString(R.string.esma_dinle)},contentAlignment=Alignment.Center){
                            Canvas(Modifier.size(15.dp)){
                                val p=Path().apply{moveTo(size.width*7/24,size.height*5/24);lineTo(size.width*19/24,size.height/2);lineTo(size.width*7/24,size.height*19/24);close()}
                                drawPath(p,Renk.Beyaz)
                            }
                        }
                    }
                    Text(isim.arapca,Modifier.fillMaxWidth(),style=TextStyle(fontFamily=AmiriQuran,fontSize=56.sp,lineHeight=86.sp,textDirection=TextDirection.Rtl,textAlign=TextAlign.Center,color=ton.vurgu))
                    Text(isim.okunus,fontFamily=Manrope,fontSize=22.sp,fontWeight=FontWeight.ExtraBold,color=Renk.YesilKoyu,textAlign=TextAlign.Center)
                    Spacer(Modifier.height(4.dp))
                    Text(isim.anlam,fontFamily=Manrope,fontSize=14.sp,lineHeight=21.sp,color=Renk.MetinIkincil,textAlign=TextAlign.Center)
                }
            }
            Spacer(Modifier.height(14.dp))
            LazyVerticalGrid(columns=GridCells.Fixed(3),modifier=Modifier.fillMaxWidth().weight(1f),
                contentPadding=PaddingValues(start=16.dp,end=16.dp,bottom=24.dp),
                horizontalArrangement=Arrangement.spacedBy(8.dp),verticalArrangement=Arrangement.spacedBy(8.dp)){
                items(liste,key={it.no}){n->
                    val aktif=n.no==isim.no
                    val renk=OgrenmeRenkleri.kart(n.no)
                    val shape=RoundedCornerShape(16.dp)
                    Column(Modifier.heightIn(min=132.dp).clip(shape)
                        .background(renk.zemin)
                        .border(if(aktif)2.5.dp else 1.dp,if(aktif)renk.vurgu else renk.kenar,shape)
                        .selectable(aktif,role=Role.Button){okuyucu?.durdur();secili=n.no}
                        .padding(4.dp),horizontalAlignment=Alignment.CenterHorizontally,
                        verticalArrangement=Arrangement.spacedBy(2.dp,Alignment.CenterVertically)){
                        Box(Modifier.sizeIn(minWidth=24.dp,minHeight=22.dp).background(renk.kenar,CircleShape).padding(horizontal=4.dp),contentAlignment=Alignment.Center){
                            Text(n.no.toString(),fontFamily=Manrope,fontSize=10.5.sp,fontWeight=FontWeight.ExtraBold,color=renk.vurgu)
                        }
                        Text(n.arapca,style=TextStyle(fontFamily=AmiriQuran,fontSize=25.sp,lineHeight=38.sp,color=renk.vurgu,textDirection=TextDirection.Rtl,textAlign=TextAlign.Center))
                        Text(n.okunus,fontFamily=Manrope,fontSize=12.sp,fontWeight=FontWeight.ExtraBold,color=Renk.Metin,textAlign=TextAlign.Center)
                        Text(n.anlam,fontFamily=Manrope,fontSize=10.5.sp,lineHeight=14.sp,color=Renk.MetinIkincil,maxLines=2,overflow=androidx.compose.ui.text.style.TextOverflow.Ellipsis,textAlign=TextAlign.Center)
                    }
                }
                item(span={GridItemSpan(maxLineSpan)}){
                    Text(stringResource(if(liste.size==99)R.string.esma_tam_liste else R.string.esma_eksik_liste,liste.size),
                        Modifier.fillMaxWidth().padding(top=4.dp),fontFamily=Manrope,fontSize=12.sp,color=r.ikincil,textAlign=TextAlign.Center)
                }
            }
        }else if(isimler!=null){
            Text(stringResource(R.string.bos_durum),Modifier.fillMaxWidth().padding(16.dp),fontFamily=Manrope,fontSize=15.sp,color=r.ikincil,textAlign=TextAlign.Center)
        }
    }
}
