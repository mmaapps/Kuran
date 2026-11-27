package com.atasoyyazilim.kurankerim.ui.bilesenler

import android.provider.Settings
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.atasoyyazilim.kurankerim.ui.theme.Renk
import kotlin.math.cos
import kotlin.math.PI

/** Namaz.dc.html: özgün SVG viewBox 220×180; yüzsüz figürler. */
@Composable
fun NamazCizimi(k:String,etiket:String,modifier:Modifier=Modifier){
    val c=LocalContext.current
    val hareket=remember(c){Settings.Global.getFloat(c.contentResolver,Settings.Global.ANIMATOR_DURATION_SCALE,1f)!=0f}
    var ms by remember(k){mutableLongStateOf(0L)}
    LaunchedEffect(k,hareket){
        if(hareket&&k.startsWith("a")||hareket&&k in listOf("n8","n10")){
            val bas=withFrameNanos{it}
            while(true)ms=withFrameNanos{(it-bas)/1_000_000}
        }
    }
    Canvas(modifier.semantics{contentDescription=etiket}){
        val f=minOf(size.width/220f,size.height/180f)
        translate((size.width-220*f)/2,(size.height-180*f)/2){
            scale(f,f,pivot=Offset.Zero){ namazCiz(k,ms,hareket) }
        }
    }
}
private fun DrawScope.namazCiz(k:String,ms:Long,hareket:Boolean){
    val g=Renk.Altin;val su=Renk.Su;val acik=Renk.KoyuMetin
    fun gitgel(sure:Long):Float {
        if(!hareket)return .5f
        return ((1-cos(PI*(ms%(sure*2)).toDouble()/sure))/2).toFloat()
    }
    val rub=-6+12*gitgel(700)
    val wipe=-8+20*gitgel(1800)
    val lift=16*(1-gitgel(1600))
    val slide=74*gitgel(2400)
    val pulse=if(hareket)(.35f+.65f*((1-cos(2*PI*(ms%1600)/1600.0))/2).toFloat())else 1f
    fun p(d:String,renk:Color=g,w:Float=3f,dolgu:Color?=null,efekt:PathEffect?=null){
        val path=PathParser().parsePathString(d).toPath()
        dolgu?.let{drawPath(path,it)}
        if(w>0)drawPath(path,renk,style=Stroke(w,cap=StrokeCap.Round,join=StrokeJoin.Round,pathEffect=efekt))
    }
    fun rr(x:Float,y:Float,w:Float,h:Float,r:Float,fill:Color=g.copy(alpha=.18f),stroke:Color=g,sw:Float=3f){
        val o=Offset(x,y);val s=Size(w,h);val cr=CornerRadius(r)
        drawRoundRect(fill,o,s,cr)
        if(sw>0)drawRoundRect(stroke,o,s,cr,style=Stroke(sw))
    }
    fun el(x:Float,y:Float,rx:Float,ry:Float,fill:Color=Color.Transparent,stroke:Color=g,sw:Float=3f,efekt:PathEffect?=null){
        val o=Offset(x-rx,y-ry);val s=Size(rx*2,ry*2)
        if(fill!=Color.Transparent)drawOval(fill,o,s)
        if(sw>0)drawOval(stroke,o,s,style=Stroke(sw,pathEffect=efekt))
    }
    val flow=PathEffect.dashPathEffect(floatArrayOf(6f,8f),if(hareket)-28f*(ms%700)/700 else 0f)
    fun damla(x:Float,y:Float,gecikme:Long=0){
        val t=if(hareket)((ms-gecikme+1400)%1400).coerceAtLeast(0)/1400f else .3f
        val a=if(!hareket)1f else if(t<.15f)t/.15f else (1-t)/.85f
        drawCircle(su.copy(alpha=a.coerceIn(0f,1f)),3f,Offset(x,y+30*t*t))
    }
    fun musluk(son:Float){p("M60 22 H118 a12 12 0 0 1 12 12 V44",acik,9f);rr(50f,12f,16f,22f,4f,acik,sw=0f);p("M130 52 V$son",su,5f,efekt=flow)}
    fun yuz(){p("M52 180 Q60 146 110 144 Q160 146 168 180 M98 124 V144 M122 124 V144");el(110f,76f,38f,48f,g.copy(alpha=.14f))}
    fun avuc(y:Float){translate(84f,y+lift){p("M0 4 Q26 30 52 4",w=4f,dolgu=g.copy(alpha=.25f));el(26f,12f,14f,4f,su,sw=0f)}}
    fun kol(){musluk(94f);rr(16f,102f,44f,20f,10f);rr(44f,96f,132f,30f,15f);rr(56+slide,82f,36f,18f,9f)
        el(172f,111f,22f,22f,stroke=su.copy(alpha=pulse),efekt=PathEffect.dashPathEffect(floatArrayOf(5f,5f)))
        damla(126f,134f);damla(136f,136f,450)}
    fun ayak(){musluk(128f);rr(48f,44f,32f,96f,14f);p("M50 118 H80 L150 136 Q170 141 170 154 V162 H44 Q40 132 50 118 Z",dolgu=g.copy(alpha=.18f));rr(146+rub,140f,30f,10f,5f);el(54f,150f,18f,18f,stroke=su.copy(alpha=pulse),efekt=PathEffect.dashPathEffect(floatArrayOf(5f,5f)));damla(120f,140f);damla(140f,142f,450)}
    if(k in listOf("a3","a4","a5","a8","a9"))yuz()
    if(k.startsWith("n"))rr(30f,166f,160f,7f,3.5f,g.copy(alpha=.35f),sw=0f)
    if(k in listOf("n1","n2","n3","n5","n8")){
        drawCircle(g,13f,Offset(110f,32f));p("M110 50 V106 M110 106 L102 162 M110 106 L118 162",w=9f)
        p(when(k){"n2"->"M102 58 L90 54 L94 30 M118 58 L130 54 L126 30";"n3","n8"->"M102 58 L98 82 L114 88 M118 58 L122 82 L106 88";else->"M102 58 L96 104 M118 58 L124 104"},w=9f)
    }
    if(k in listOf("n7","n9","n10")){drawCircle(g,13f,Offset(118f,68f));p("M76 160 L128 162 M76 158 L120 140 M120 140 L118 86 M118 94 L106 120 L88 146",w=9f)}
    when(k){
        "a1"->{
            p("M56 176 V88 a54 54 0 0 1 108 0 V176",g.copy(alpha=.35f))
            translate(72f,76f){rotate(-12f,Offset.Zero){rr(0f,0f,30f,62f,15f);rr(-12f,28f,16f,28f,8f)}}
            translate(148f,76f){rotate(12f,Offset.Zero){scale(-1f,1f,Offset.Zero){rr(0f,0f,30f,62f,15f);rr(-12f,28f,16f,28f,8f)}}}
            p("M110 34 l4 10 10 4 -10 4 -4 10 -4 -10 -10 -4 10 -4z",w=0f,dolgu=g.copy(alpha=pulse))
        }
        "a2"->{musluk(96f)
            translate(66+rub,96f){rr(0f,0f,64f,22f,11f);rr(10f,-9f,24f,12f,6f)}
            translate(196-rub,114f){scale(-1f,1f,Offset.Zero){rr(0f,0f,64f,22f,11f);rr(10f,19f,24f,12f,6f)}}
            damla(122f,140f);damla(134f,142f,450);damla(128f,138f,900)}
        "a3"->{el(110f,102f,12f,5f,su.copy(alpha=pulse),sw=0f);avuc(116f)}
        "a4"->{el(110f,84f,5f,10f,su.copy(alpha=pulse),sw=0f);avuc(100f)}
        "a5"->{el(110f,76f,44f,54f,stroke=su,efekt=flow);rr(54f,54+wipe,18f,46f,9f,g.copy(alpha=.22f));rr(148f,54+wipe,18f,46f,9f,g.copy(alpha=.22f));damla(96f,128f);damla(124f,130f,450)}
        "a6"->kol()
        "a7"->scale(-1f,1f,Offset(110f,0f)){kol()}
        "a8"->{p("M74 62 Q76 28 110 28 Q144 28 146 62 Z",w=0f,dolgu=su.copy(alpha=.5f*pulse));rr(82f,6+wipe,56f,20f,10f,g.copy(alpha=.25f))}
        "a9"->{el(70f,80f,8f,15f,su.copy(alpha=pulse),sw=0f);el(150f,80f,8f,15f,su.copy(alpha=pulse),sw=0f);rr(30+rub,74f,34f,11f,5.5f,g.copy(alpha=.25f));rr(156-rub,74f,34f,11f,5.5f,g.copy(alpha=.25f))}
        "a10"->{p("M44 180 Q52 132 110 130 Q168 132 176 180");rr(94f,94f,32f,40f,8f,g.copy(alpha=.14f));el(110f,60f,38f,38f,g.copy(alpha=.14f));rr(94f,104f,32f,18f,6f,su.copy(alpha=.6f*pulse),sw=0f);rr(54+rub,104f,40f,16f,8f,g.copy(alpha=.25f));rr(126-rub,104f,40f,16f,8f,g.copy(alpha=.25f))}
        "a11"->ayak()
        "a12"->scale(-1f,1f,Offset(110f,0f)){ayak()}
        "n1"->p("M64 166 V80 a46 46 0 0 1 92 0 V166",g.copy(alpha=.3f))
        "n4"->{drawCircle(g,13f,Offset(58f,104f));p("M130 108 L126 162 M130 108 L136 162 M130 108 L74 106 M82 110 L104 128 L126 136",w=9f)}
        "n6"->{drawCircle(g,12f,Offset(64f,152f));p("M112 160 L150 162 M112 160 L126 120 M126 120 L78 144 M88 142 L94 158 L80 164",w=9f)}
        "n8"->p("M164 124 V70 M152 84 L164 70 L176 84",su.copy(alpha=pulse),5f)
        "n10"->p("M140 52 Q156 66 140 82 M134 76 L140 83 L148 78 M96 52 Q80 66 96 82 M102 76 L96 83 L88 78",su.copy(alpha=pulse),4f)
    }
}
