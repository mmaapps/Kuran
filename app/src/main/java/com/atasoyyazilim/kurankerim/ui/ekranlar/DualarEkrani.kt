package com.atasoyyazilim.kurankerim.ui.ekranlar

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.atasoyyazilim.kurankerim.R
import com.atasoyyazilim.kurankerim.data.Dua
import com.atasoyyazilim.kurankerim.data.DuaKurallari
import com.atasoyyazilim.kurankerim.data.IcerikDeposu
import com.atasoyyazilim.kurankerim.ses.SesliOkuyucu
import com.atasoyyazilim.kurankerim.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

/** Dualar.dc.html: seçili dua kartı ve 58 dp liste satırları. */
@Composable
fun DualarEkrani(geri:()->Unit){
    val c=LocalContext.current
    val r=LocalTemaRenkleri.current
    val dualar by produceState<List<Dua>?>(null,c){
        value=withContext(Dispatchers.IO){
            runCatching{
                val depo=IcerikDeposu(c)
                val kaynak=depo.dualar()
                require(kaynak.map{it.id}.distinct().size==kaynak.size)
                val mushaf=if(kaynak.any{DuaKurallari.ayet(it.id)!=null})depo.mushaf()else null
                kaynak.mapNotNull{d->
                    val ref=DuaKurallari.ayet(d.id)
                    if(ref!=null){
                        // Kur'an metni yalnız Tanzil; âyet anlamı ekranda veya TTS'de yok.
                        val ayet=mushaf?.ayet(ref.first,ref.second) ?: return@mapNotNull null
                        d.copy(arapca=ayet.arapca,anlam="")
                    }else if(DuaKurallari.anlamGoster(d.id)&&d.kaynak.isNotBlank()&&d.arapca.isNotBlank())d
                    else null // kaynağı/sınıflandırması doğrulanmamış yeni kayıt gösterilmez
                }
            }.getOrDefault(emptyList())
        }
    }
    val liste=dualar.orEmpty()
    var secili by rememberSaveable{mutableStateOf("d001")}
    val d=liste.firstOrNull{it.id==secili}?:liste.firstOrNull()
    var sesHazir by remember{mutableStateOf(false)}
    var sesEksik by remember{mutableStateOf(false)}
    var okuyucu by remember{mutableStateOf<SesliOkuyucu?>(null)}
    val lifecycle=LocalLifecycleOwner.current.lifecycle
    DisposableEffect(c,lifecycle){
        val o=SesliOkuyucu(c){sesHazir=it}
        okuyucu=o
        val observer=LifecycleEventObserver{_,e->if(e==Lifecycle.Event.ON_STOP)o.durdur()}
        lifecycle.addObserver(observer)
        onDispose{lifecycle.removeObserver(observer);o.durdur();o.kapat();okuyucu=null}
    }
    if(sesEksik)AlertDialog(onDismissRequest={sesEksik=false},
        text={Text(stringResource(R.string.namaz_turkce_ses_yok))},
        confirmButton={TextButton(onClick={sesEksik=false}){Text(stringResource(R.string.kapat))}})
    Column(Modifier.fillMaxSize().background(r.zemin).windowInsetsPadding(WindowInsets.safeDrawing)){
        Row(Modifier.fillMaxWidth().height(60.dp).padding(horizontal=8.dp),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(4.dp)){
            Box(Modifier.size(44.dp).clickable(role=Role.Button,onClick=geri).semantics{contentDescription=c.getString(R.string.mushaf_geri)},contentAlignment=Alignment.Center){
                Canvas(Modifier.size(22.dp)){
                    val p=Path().apply{moveTo(size.width*.625f,size.height*.208f);lineTo(size.width*.333f,size.height*.5f);lineTo(size.width*.625f,size.height*.792f)}
                    drawPath(p,r.baslik,style=Stroke(2.dp.toPx(),cap=StrokeCap.Round))
                }
            }
            Text(stringResource(R.string.dualar_baslik),fontFamily=Manrope,fontSize=19.sp,fontWeight=FontWeight.ExtraBold,color=r.baslik)
        }
        if(d!=null){
            LazyColumn(Modifier.fillMaxWidth().weight(1f),contentPadding=PaddingValues(start=16.dp,end=16.dp,bottom=24.dp)){
                item(key="dua_kart"){
                    Column(Modifier.fillMaxWidth().background(Renk.YesilKoyu,RoundedCornerShape(22.dp)).padding(horizontal=18.dp,vertical=16.dp)){
                        Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.SpaceBetween){
                            Text(d.kategori.uppercase(Locale("tr","TR")),Modifier.weight(1f),fontFamily=Manrope,fontSize=11.5.sp,fontWeight=FontWeight.ExtraBold,letterSpacing=1.15.sp,color=Renk.Altin)
                            Box(Modifier.size(44.dp).clip(CircleShape).background(Renk.Altin)
                                .clickable(role=Role.Button){
                                    if(sesHazir)okuyucu?.oku(DuaKurallari.sesliMetin(d))else sesEksik=true
                                }.semantics{contentDescription=c.getString(R.string.dualar_dinle)},contentAlignment=Alignment.Center){
                                Canvas(Modifier.size(15.dp)){
                                    val p=Path().apply{moveTo(size.width*7/24,size.height*5/24);lineTo(size.width*19/24,size.height/2);lineTo(size.width*7/24,size.height*19/24);close()}
                                    drawPath(p,Renk.YesilKoyu)
                                }
                            }
                        }
                        Text(d.ad,Modifier.padding(top=2.dp),fontFamily=Manrope,fontSize=20.sp,fontWeight=FontWeight.ExtraBold,color=Renk.Beyaz)
                        Text(d.arapca,Modifier.fillMaxWidth().padding(top=10.dp),style=TextStyle(
                            fontFamily=if(DuaKurallari.ayet(d.id)!=null)MushafYazi else AmiriQuran,
                            fontSize=23.sp,lineHeight=46.sp,textDirection=TextDirection.Rtl,
                            textAlign=TextAlign.End,color=Renk.AltinAcik))
                        if(d.okunus.isNotBlank())Text(d.okunus,Modifier.padding(top=8.dp),fontFamily=Manrope,fontSize=14.sp,lineHeight=21.7.sp,fontStyle=FontStyle.Italic,color=Renk.KoyuMetin)
                        if(DuaKurallari.anlamGoster(d.id)&&d.anlam.isNotBlank())Text(d.anlam,Modifier.padding(top=8.dp),fontFamily=Lora,fontSize=15.5.sp,lineHeight=24.8.sp,color=Renk.Beyaz)
                        Text(d.kaynak,Modifier.padding(top=8.dp),fontFamily=Manrope,fontSize=12.sp,fontWeight=FontWeight.SemiBold,color=Renk.KoyuMetinSoluk)
                    }
                }
                item(key="dua_liste_baslik"){
                    Text(stringResource(R.string.dualar_tumu),Modifier.padding(top=18.dp,bottom=8.dp),fontFamily=Manrope,fontSize=16.sp,fontWeight=FontWeight.ExtraBold,color=r.baslik)
                }
                itemsIndexed(liste,key={_,x->x.id}){i,x->
                    val aktif=d.id==x.id
                    val sekil=RoundedCornerShape(topStart=if(i==0)18.dp else 0.dp,topEnd=if(i==0)18.dp else 0.dp,
                        bottomStart=if(i==liste.lastIndex)18.dp else 0.dp,bottomEnd=if(i==liste.lastIndex)18.dp else 0.dp)
                    Column(Modifier.fillMaxWidth().clip(sekil).background(if(aktif)Renk.VurguAcik else r.kart)){
                        Row(Modifier.fillMaxWidth().heightIn(min=58.dp).selectable(aktif,role=Role.Button){
                            okuyucu?.durdur();secili=x.id
                        }.padding(horizontal=16.dp,vertical=8.dp),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(12.dp)){
                            Column(Modifier.weight(1f)){
                                Text(x.ad,fontFamily=Manrope,fontSize=15.sp,fontWeight=FontWeight.ExtraBold,color=if(aktif)Renk.Metin else r.metin)
                                Text(x.kategori,fontFamily=Manrope,fontSize=12.sp,color=if(aktif)Renk.MetinIkincil else r.ikincil)
                            }
                            Canvas(Modifier.size(16.dp)){
                                val p=Path().apply{moveTo(size.width*.375f,size.height*.208f);lineTo(size.width*.667f,size.height*.5f);lineTo(size.width*.375f,size.height*.792f)}
                                drawPath(p,if(aktif)Renk.MetinIkincil else r.ikincil,style=Stroke(2.dp.toPx(),cap=StrokeCap.Round))
                            }
                        }
                        if(i!=liste.lastIndex)Box(Modifier.fillMaxWidth().height(1.dp).background(if(r.gece)r.kenar else Renk.Ayirici))
                    }
                }
            }
        }else if(dualar!=null){
            Text(stringResource(R.string.bos_durum),Modifier.fillMaxWidth().padding(16.dp),fontFamily=Manrope,fontSize=15.sp,color=r.ikincil,textAlign=TextAlign.Center)
        }
    }
}
