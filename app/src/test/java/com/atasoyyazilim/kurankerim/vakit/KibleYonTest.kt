package com.atasoyyazilim.kurankerim.vakit
import org.junit.Assert.*
import org.junit.Test
class KibleYonTest {
    @Test fun kuzeySiniri(){assertEquals(2.0,KibleYon.fark(359.0,1.0),.0001);assertEquals(-2.0,KibleYon.fark(1.0,359.0),.0001)}
    @Test fun dortDerece(){assertTrue(KibleYon.dogru(148.0,152.0));assertFalse(KibleYon.dogru(147.9,152.0))}
    @Test fun mesafe(){assertEquals(0.0,KibleYon.mesafeKm(21.4225,39.8262),.001);assertTrue(KibleYon.mesafeKm(41.0082,28.9784) in 2300.0..2500.0)}
    @Test fun yonler(){assertEquals(0,KibleYon.yonIndeksi(359.0));assertEquals(3,KibleYon.yonIndeksi(152.0))}
}
