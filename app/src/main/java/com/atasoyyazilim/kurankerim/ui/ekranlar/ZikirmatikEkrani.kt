package com.atasoyyazilim.kurankerim.ui.ekranlar

import android.provider.Settings
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.StrokeCap
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
import com.atasoyyazilim.kurankerim.R
import com.atasoyyazilim.kurankerim.data.*
import com.atasoyyazilim.kurankerim.ui.bilesenler.IlerlemeHalkasi
import com.atasoyyazilim.kurankerim.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun ZikirmatikEkrani(geri:()->Unit){
    val c=LocalContext.current
    val r=LocalTemaRenkleri.current
    val akis=remember(c){ZikirKayitlari.izle(c)}
    val durum by akis.collectAsState(initial=null)
    val zikirler by produceState<List<Zikir>?>(null,c){
        value=withContext(Dispatchers.IO){runCatching{IcerikDeposu(c).zikirler()}.getOrDefault(emptyList())}
    }
    val d=durum
    val liste=zikirler
    val z=liste?.firstOrNull{it.id==d?.secili}
    val dolu=d!=null&&z!=null
    Column(Modifier.fillMaxSize().background(r.zemin).windowInsetsPadding(WindowInsets.safeDrawing)){
        Row(Modifier.fillMaxWidth().height(60.dp).padding(horizontal=8.dp),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(4.dp)){
            Box(Modifier.size(44.dp).clickable(role=Role.Button,onClick=geri).semantics{contentDescription=c.getString(R.string.mushaf_geri)},contentAlignment=Alignment.Center){
                Canvas(Modifier.size(22.dp)){
                    val p=Path().apply{moveTo(size.width*.625f,size.height*.208f);lineTo(size.width*.333f,size.height*.5f);lineTo(size.width*.625f,size.height*.792f)}
                    drawPath(p,r.baslik,style=Stroke(2.dp.toPx(),cap=StrokeCap.Round))
                }
            }
            Text(stringResource(R.string.zikir_baslik),Modifier.weight(1f),fontFamily=Manrope,fontSize=19.sp,fontWeight=FontWeight.ExtraBold,color=r.baslik)
            Box(Modifier.height(44.dp).padding(end=4.dp).clickable(enabled=dolu,role=Role.Button){ZikirKayitlari.sifirla(c)},contentAlignment=Alignment.Center){
                Box(Modifier.height(40.dp).background(r.kart,RoundedCornerShape(20.dp)).border(1.dp,if(r.gece)r.kenar else Renk.Cizgi,RoundedCornerShape(20.dp)).padding(horizontal=14.dp),contentAlignment=Alignment.Center){
                    Text(stringResource(R.string.zikir_sifirla),fontFamily=Manrope,fontSize=13.sp,fontWeight=FontWeight.ExtraBold,color=r.baslik)
                }
            }
        }
        Column(Modifier.fillMaxWidth().weight(1f).verticalScroll(rememberScrollState()),horizontalAlignment=Alignment.CenterHorizontally){
            if(dolu&&d!=null&&z!=null&&liste!=null){
                Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(start=16.dp,end=16.dp,top=4.dp).selectableGroup(),horizontalArrangement=Arrangement.spacedBy(8.dp)){
                    liste.forEach{item->
                        val aktif=item.id==d.secili
                        Box(Modifier.height(44.dp).selectable(aktif,role=Role.Button){ZikirKayitlari.sec(c,item.id)},contentAlignment=Alignment.Center){
                            val shape=RoundedCornerShape(20.dp)
                            Box(Modifier.height(40.dp).background(if(aktif)Renk.YesilKoyu else r.kart,shape)
                                .border(1.dp,if(aktif)Renk.YesilKoyu else if(r.gece)r.kenar else Renk.Cizgi,shape).padding(horizontal=14.dp),contentAlignment=Alignment.Center){
                                Text(item.ad,fontFamily=Manrope,fontSize=13.sp,fontWeight=FontWeight.ExtraBold,color=if(aktif)Renk.Beyaz else r.metin)
                            }
                        }
                    }
                }
                Spacer(Modifier.height(22.dp))
                Text(z.arapca,Modifier.fillMaxWidth().padding(horizontal=16.dp),style=TextStyle(fontFamily=AmiriQuran,fontSize=34.sp,lineHeight=57.8.sp,color=r.baslik,textDirection=TextDirection.Rtl,textAlign=TextAlign.Center))
                Text(z.anlam,Modifier.padding(horizontal=16.dp),fontFamily=Manrope,fontSize=14.sp,fontWeight=FontWeight.SemiBold,color=r.ikincil,textAlign=TextAlign.Center)
                Spacer(Modifier.height(22.dp))
                BoxWithConstraints(Modifier.fillMaxWidth().padding(horizontal=16.dp),contentAlignment=Alignment.Center){
                    val boyut=minOf(maxWidth,280.dp)
                    val kaynak=remember{MutableInteractionSource()}
                    val basili by kaynak.collectIsPressedAsState()
                    val hareket=remember(c){Settings.Global.getFloat(c.contentResolver,Settings.Global.ANIMATOR_DURATION_SCALE,1f)!=0f}
                    val olcek by animateFloatAsState(if(basili&&hareket).97f else 1f,tween(if(hareket)80 else 0),label="zikir_basma")
                    val etiket=stringResource(R.string.zikir_say_etiket,z.ad,d.sayi)
                    Box(Modifier.size(boyut).graphicsLayer{scaleX=olcek;scaleY=olcek}.clip(CircleShape).background(Renk.YesilKoyu)
                        .clickable(interactionSource=kaynak,indication=null,role=Role.Button){ZikirKayitlari.say(c)}
                        .semantics{contentDescription=etiket},contentAlignment=Alignment.Center){
                        IlerlemeHalkasi(d.ilerleme,boyut,boyut*(124f/280),boyut*(12f/280)){
                            Column(horizontalAlignment=Alignment.CenterHorizontally){
                                val font=if(d.sayi.toString().length>4)40.sp else if(d.sayi.toString().length>3)54.sp else 72.sp
                                Text(d.sayi.toString(),style=TextStyle(fontFamily=Manrope,fontSize=font,lineHeight=font,fontWeight=FontWeight.ExtraBold,color=Renk.Beyaz,fontFeatureSettings="tnum"))
                                Spacer(Modifier.height(6.dp))
                                Text(stringResource(R.string.zikir_dokun,d.hedef),fontFamily=Manrope,fontSize=14.sp,fontWeight=FontWeight.Bold,color=Renk.KoyuMetinSoluk)
                            }
                        }
                    }
                }
                Spacer(Modifier.height(22.dp))
                Row(Modifier.selectableGroup(),horizontalArrangement=Arrangement.spacedBy(8.dp)){
                    ZikirDurumu.HEDEFLER.forEach{n->
                        val aktif=n==d.hedef
                        val shape=RoundedCornerShape(14.dp)
                        Box(Modifier.size(72.dp,44.dp).clip(shape).background(if(aktif)Renk.Altin else r.kart)
                            .border(1.dp,if(aktif)Renk.Altin else if(r.gece)r.kenar else Renk.Cizgi,shape)
                            .selectable(aktif,role=Role.Button){ZikirKayitlari.hedef(c,n)},contentAlignment=Alignment.Center){
                            Text(n.toString(),fontFamily=Manrope,fontSize=15.sp,fontWeight=FontWeight.ExtraBold,color=if(aktif)Renk.YesilKoyu else r.baslik)
                        }
                    }
                }
                Spacer(Modifier.height(18.dp))
                Row(Modifier.fillMaxWidth().padding(horizontal=16.dp).background(r.kart,RoundedCornerShape(16.dp))
                    .border(1.dp,r.kenar,RoundedCornerShape(16.dp)).padding(horizontal=16.dp,vertical=12.dp),
                    horizontalArrangement=Arrangement.SpaceBetween,verticalAlignment=Alignment.CenterVertically){
                    Text(stringResource(R.string.zikir_bugun),fontFamily=Manrope,fontSize=13.sp,fontWeight=FontWeight.SemiBold,color=r.ikincil)
                    Text(stringResource(R.string.zikir_toplam,d.toplam,d.tur),fontFamily=Manrope,fontSize=13.sp,fontWeight=FontWeight.ExtraBold,color=r.baslik)
                }
                Text(stringResource(R.string.zikir_titresim),Modifier.padding(start=16.dp,end=16.dp,top=10.dp,bottom=16.dp),
                    fontFamily=Manrope,fontSize=12.sp,color=r.ikincil,textAlign=TextAlign.Center)
            }else if(liste!=null&&liste.isEmpty()){
                Text(stringResource(R.string.bos_durum),Modifier.padding(16.dp),fontFamily=Manrope,fontSize=15.sp,color=r.ikincil)
            }
        }
    }
}
