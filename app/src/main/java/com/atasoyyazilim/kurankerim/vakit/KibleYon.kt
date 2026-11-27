package com.atasoyyazilim.kurankerim.vakit

import kotlin.math.*

object KibleYon {
    fun normalize(a:Double):Double = ((a%360)+360)%360
    fun fark(yon:Double,kible:Double):Double = normalize(kible-yon+180)-180
    fun dogru(yon:Double,kible:Double):Boolean = abs(fark(yon,kible))<=4.0
    fun mesafeKm(enlem:Double,boylam:Double):Double {
        val a=Math.toRadians(enlem)
        val b=Math.toRadians(21.4225)
        val x=sin((b-a)/2).pow(2)+cos(a)*cos(b)*sin(Math.toRadians(39.8262-boylam)/2).pow(2)
        return 6371.0088*2*asin(sqrt(x.coerceIn(0.0,1.0)))
    }
    fun yonIndeksi(ac:Double):Int = ((normalize(ac)+22.5)/45).toInt()%8
}
