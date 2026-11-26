package com.atasoyyazilim.kurankerim.vakit

/** İzinli kayıt yoksa ses çıkmaz; sessizde yalnız kullanıcının seçtiği titreşim. */
object EzanDavranisi {
    enum class Eylem { SES, TITRET, YOK }
    fun karar(acik:Boolean,sesHazir:Boolean,sessiz:Boolean,titret:Boolean,gecikmeMs:Long):Eylem {
        if(!acik || gecikmeMs !in 0L until 60_000L) return Eylem.YOK
        return if(sessiz) {
            if(titret) Eylem.TITRET else Eylem.YOK
        } else if(sesHazir) Eylem.SES else Eylem.YOK
    }
}
