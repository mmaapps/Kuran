package com.atasoyyazilim.kurankerim.ui.ekranlar

import android.hardware.GeomagneticField
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.atasoyyazilim.kurankerim.R
import com.atasoyyazilim.kurankerim.ui.theme.*
import com.atasoyyazilim.kurankerim.vakit.*
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.roundToInt
import kotlin.math.abs

@Composable
fun KibleEkrani(geri:()->Unit){
    val c=LocalContext.current
    val lifecycle=LocalLifecycleOwner.current.lifecycle
    val rev by VakitAyarlari.degisim.collectAsState()
    val konum=remember(rev){VakitAyarlari.konum(c)}
    val konumVar=remember(rev){VakitAyarlari.konumBelirlendi(c)}
    val q=remember(konum){VakitHesaplayici.kibleAcisi(konum.enlem,konum.boylam)}
    val km=remember(konum){KibleYon.mesafeKm(konum.enlem,konum.boylam)}
    val sapma=remember(konum){GeomagneticField(konum.enlem.toFloat(),konum.boylam.toFloat(),0f,System.currentTimeMillis()).declination.toDouble()}
    var sensor by remember{mutableStateOf(PusulaOlcumu(null,false,false,true))}
    LaunchedEffect(lifecycle){
        lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED){
            try { KibleSensoru.izle(c).collect{sensor=it} }
            finally {sensor=PusulaOlcumu(null,false,false,true)}
        }
    }
    val yon=sensor.manyetikYon?.let{KibleYon.normalize(it+sapma)}
    val fark=yon?.let{KibleYon.fark(it,q)}
    val hazir=konumVar&&sensor.sensorVar&&sensor.guvenilir&&sensor.duz&&fark!=null
    val dogru=hazir&&abs(fark!!)<=4
    var onceDogru by remember{mutableStateOf(false)}
    LaunchedEffect(dogru){
        if(dogru&&!onceDogru){
            @Suppress("DEPRECATION")
            val v=if(Build.VERSION.SDK_INT>=31)c.getSystemService(VibratorManager::class.java).defaultVibrator else c.getSystemService(Vibrator::class.java)
            if(v.hasVibrator())runCatching{v.vibrate(VibrationEffect.createOneShot(100,VibrationEffect.DEFAULT_AMPLITUDE))}
        }
        onceDogru=dogru
    }
    var hedef by remember{mutableDoubleStateOf(0.0)}
    var once by remember{mutableStateOf<Double?>(null)}
    LaunchedEffect(yon){yon?.let{y->hedef=once?.let{hedef-KibleYon.fark(it,y)}?:-y;once=y}}
    val donus by animateFloatAsState(hedef.toFloat(),tween(250),label="kible")
    val yonler=listOf(R.string.kible_k,R.string.kible_kd,R.string.kible_d,R.string.kible_gd,R.string.kible_g,R.string.kible_gb,R.string.kible_b,R.string.kible_kb)
    val durum=when{
        !konumVar->stringResource(R.string.kible_konum_yok)
        !sensor.sensorVar->stringResource(R.string.kible_sensor_yok)
        !sensor.duz&&yon!=null->stringResource(R.string.kible_duz)
        !sensor.guvenilir||fark==null->stringResource(R.string.kible_kalibre)
        dogru->stringResource(R.string.kible_dogru)
        fark>=0->stringResource(R.string.kible_saga,abs(fark).roundToInt())
        else->stringResource(R.string.kible_sola,abs(fark).roundToInt())
    }
    Column(Modifier.fillMaxSize().background(Renk.YesilKoyu).windowInsetsPadding(WindowInsets.safeDrawing).verticalScroll(rememberScrollState())){
        Row(Modifier.fillMaxWidth().height(60.dp).padding(horizontal=8.dp),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(4.dp)){
            Box(Modifier.size(44.dp).clickable(role=Role.Button,onClick=geri).semantics{contentDescription=c.getString(R.string.mushaf_geri)},contentAlignment=Alignment.Center){
                Canvas(Modifier.size(22.dp)){val p=Path().apply{moveTo(size.width*.625f,size.height*.208f);lineTo(size.width*.333f,size.height*.5f);lineTo(size.width*.625f,size.height*.792f)};drawPath(p,Renk.Beyaz,style=Stroke(2.dp.toPx(),cap=androidx.compose.ui.graphics.StrokeCap.Round))}
            }
            Text(stringResource(R.string.kible_baslik),fontFamily=Manrope,fontSize=19.sp,fontWeight=FontWeight.ExtraBold,color=Renk.Beyaz)
        }
        Spacer(Modifier.height(8.dp))
        Column(Modifier.fillMaxWidth(),horizontalAlignment=Alignment.CenterHorizontally){
            Text(stringResource(R.string.kible_konum_acisi,konum.ad),fontFamily=Manrope,fontSize=13.sp,fontWeight=FontWeight.SemiBold,color=Renk.KoyuMetinSoluk)
            Text(stringResource(R.string.kible_derece,q.roundToInt()),fontFamily=Manrope,fontSize=44.sp,fontWeight=FontWeight.ExtraBold,color=Renk.AltinAcik)
            Text(stringResource(R.string.kible_mesafe,stringResource(yonler[KibleYon.yonIndeksi(q)]),NumberFormat.getIntegerInstance(Locale("tr","TR")).format(km.roundToInt())),fontFamily=Manrope,fontSize=13.sp,color=Renk.KoyuMetin,textAlign=TextAlign.Center)
        }
        Spacer(Modifier.height(26.dp))
        BoxWithConstraints(Modifier.fillMaxWidth().padding(horizontal=20.dp),contentAlignment=Alignment.Center){
            val d=minOf(300.dp,maxWidth)
            Box(Modifier.size(d)){
                Box(Modifier.fillMaxSize().graphicsLayer{rotationZ=donus}){
                    Canvas(Modifier.fillMaxSize().semantics{contentDescription=c.getString(R.string.kible_kabe_yonu)}){
                        val f=size.width/300f
                        val center=Offset(size.width/2,size.height/2)
                        drawCircle(Renk.YesilMadalyon,149*f,center)
                        drawCircle(Renk.Altin.copy(alpha=.5f),149*f,center,style=Stroke(2*f))
                        for(i in 0 until 72)rotate(i*5f,center){
                            val major=i%6==0
                            drawLine(if(major)Renk.Altin else Renk.KoyuMetin.copy(alpha=.45f),Offset(center.x,(if(major)40 else 32)*f),Offset(center.x,24*f), (if(major)2.5f else 1.2f)*f)
                        }
                        rotate(q.toFloat(),center){
                            drawRoundRect(Renk.Altin,Offset(center.x-3*f,center.y-96*f),Size(6*f,96*f),CornerRadius(3*f))
                            val pos=Offset(center.x-20*f,center.y-128*f)
                            drawRoundRect(Renk.KabeSiyah,pos,Size(40*f,40*f),CornerRadius(8*f))
                            drawRoundRect(Renk.Altin,pos+Offset(f,f),Size(38*f,38*f),CornerRadius(7*f),style=Stroke(2*f))
                            drawRect(Renk.Altin,pos+Offset(2*f,11*f),Size(36*f,6*f))
                        }
                        drawCircle(Renk.Altin,12*f,center);drawCircle(Renk.YesilKoyu,10*f,center,style=Stroke(4*f))
                    }
                    val inset=d*(32f/300f)
                    Text(stringResource(R.string.kible_k_harf),Modifier.align(Alignment.TopCenter).padding(top=inset),fontFamily=Manrope,fontSize=18.sp,fontWeight=FontWeight.ExtraBold,color=Renk.AltinAcik)
                    Text(stringResource(R.string.kible_g_harf),Modifier.align(Alignment.BottomCenter).padding(bottom=inset),fontFamily=Manrope,fontSize=16.sp,fontWeight=FontWeight.ExtraBold,color=Renk.KoyuMetin)
                    Text(stringResource(R.string.kible_d_harf),Modifier.align(Alignment.CenterEnd).padding(end=inset),fontFamily=Manrope,fontSize=16.sp,fontWeight=FontWeight.ExtraBold,color=Renk.KoyuMetin)
                    Text(stringResource(R.string.kible_b_harf),Modifier.align(Alignment.CenterStart).padding(start=inset),fontFamily=Manrope,fontSize=16.sp,fontWeight=FontWeight.ExtraBold,color=Renk.KoyuMetin)
                }
                Canvas(Modifier.align(Alignment.TopCenter).offset(y=(-16).dp).size(24.dp,20.dp)){
                    val p=Path().apply{moveTo(size.width/2,size.height);lineTo(size.width/12,size.height/10);lineTo(size.width*11/12,size.height/10);close()}
                    drawPath(p,if(dogru)Renk.Basari else Renk.Altin)
                }
            }
        }
        Spacer(Modifier.height(26.dp))
        Box(Modifier.fillMaxWidth().padding(horizontal=20.dp).heightIn(min=56.dp).background(if(dogru)Renk.Basari else Renk.Beyaz.copy(alpha=.08f),RoundedCornerShape(16.dp)).semantics{liveRegion=LiveRegionMode.Polite}.padding(horizontal=10.dp),contentAlignment=Alignment.Center){
            Text(durum,fontFamily=Manrope,fontSize=15.sp,fontWeight=FontWeight.ExtraBold,color=if(dogru)Renk.YesilKoyu else Renk.Beyaz,textAlign=TextAlign.Center)
        }
        Text(stringResource(R.string.kible_tavsiye),Modifier.padding(start=20.dp,end=20.dp,top=14.dp,bottom=20.dp),fontFamily=Manrope,fontSize=12.sp,lineHeight=18.sp,color=Renk.KoyuMetinSoluk)
    }
}
