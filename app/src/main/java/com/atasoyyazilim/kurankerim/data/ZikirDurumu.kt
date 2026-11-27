package com.atasoyyazilim.kurankerim.data

/** Zikirmatik.dc.html: sayaç toplamı korunur, halka tamamlanan turda dolu kalır. */
data class ZikirDurumu(
    val secili:Int=0,
    val hedef:Int=33,
    val sayaclar:List<Long> = List(5){0L},
    val tarih:String="",
    val toplam:Long=0,
    val tur:Long=0
) {
    val sayi:Long get()=sayaclar[secili]
    val ilerleme:Float get()=if(sayi==0L)0f else if(sayi%hedef==0L)1f else (sayi%hedef).toFloat()/hedef
    fun gun(gun:String)=if(tarih==gun)this else copy(tarih=gun,toplam=0,tur=0)
    fun sec(id:Int)=if(id in sayaclar.indices)copy(secili=id)else this
    fun hedefSec(n:Int)=if(n in HEDEFLER)copy(hedef=n)else this
    fun sifirla()=copy(sayaclar=sayaclar.mapIndexed{i,s->if(i==secili)0L else s})
    fun ekle(gun:String):Pair<ZikirDurumu,Long>{
        val d=gun(gun)
        if(d.sayi==Long.MAX_VALUE||d.toplam==Long.MAX_VALUE)return d to 0L
        val yeni=d.sayi+1
        val tamam=yeni%hedef==0L
        return d.copy(sayaclar=d.sayaclar.mapIndexed{i,s->if(i==d.secili)yeni else s},
            toplam=d.toplam+1,tur=if(tamam&&d.tur<Long.MAX_VALUE)d.tur+1 else d.tur) to if(tamam)200L else 15L
    }
    companion object { val HEDEFLER=listOf(33,99,100) }
}
