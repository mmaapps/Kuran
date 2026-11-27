package com.atasoyyazilim.kurankerim.data

/** Özgün dua dosyasındaki âyet referansları; yeni dini içerik üretilmez.
 * Yeni kayıt için kaynak sınıflandırması eklenmeden anlam gösterilmez.
 */
object DuaKurallari {
    fun ayet(id:String):Pair<Int,Int>?=when(id){
        "d001"->2 to 201
        "d002"->14 to 41
        else->null
    }
    fun anlamGoster(id:String):Boolean=id in setOf("d003","d004","d005")
    fun sesliMetin(d:Dua):String=listOf(d.okunus,if(anlamGoster(d.id))d.anlam else "")
        .filter{it.isNotBlank()}.joinToString(" ")
}
