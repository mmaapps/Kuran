package com.atasoyyazilim.kurankerim.data
import org.junit.Assert.*
import org.junit.Test
class ZikirDurumuTest {
    @Test fun sifirdanBaslar(){val d=ZikirDurumu();assertEquals(0L,d.sayi);assertEquals(0L,d.toplam);assertEquals(0f,d.ilerleme,0f)}
    @Test fun hedefTitreSim(){var d=ZikirDurumu();repeat(32){val (y,ms)=d.ekle("2026-11-27");assertEquals(15L,ms);d=y};val (y,ms)=d.ekle("2026-11-27");assertEquals(200L,ms);assertEquals(1f,y.ilerleme,0f);assertEquals(1L,y.tur);assertEquals(33L,y.toplam)}
    @Test fun sifirlaVeSec(){val d=ZikirDurumu().ekle("a").first.sec(1).ekle("a").first;val y=d.sifirla();assertEquals(0L,y.sayi);assertEquals(1L,y.sec(0).sayi);assertEquals(2L,y.toplam)}
    @Test fun yeniGun(){val d=ZikirDurumu().ekle("a").first.gun("b");assertEquals(1L,d.sayi);assertEquals(0L,d.toplam);assertEquals(0L,d.tur)}
    @Test fun hedefDegisimi(){val d=ZikirDurumu().ekle("a").first.hedefSec(99);assertEquals(99,d.hedef);assertEquals(1L,d.sayi);assertEquals(99,d.hedefSec(7).hedef)}
}
