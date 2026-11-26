package com.atasoyyazilim.kurankerim.vakit
import org.junit.Assert.assertEquals
import org.junit.Test
class EzanDavranisiTest {
    @Test fun izinliSesVeNormalMod() {
        assertEquals(EzanDavranisi.Eylem.SES,EzanDavranisi.karar(true,true,false,true,0))
        assertEquals(EzanDavranisi.Eylem.YOK,EzanDavranisi.karar(true,false,false,true,0))
    }
    @Test fun sessizdeSesCalmaz() {
        assertEquals(EzanDavranisi.Eylem.TITRET,EzanDavranisi.karar(true,true,true,true,0))
        assertEquals(EzanDavranisi.Eylem.TITRET,EzanDavranisi.karar(true,false,true,true,0))
        assertEquals(EzanDavranisi.Eylem.YOK,EzanDavranisi.karar(true,true,true,false,0))
    }
    @Test fun kapaliGecikmisVeyaErkenAlarmCalmaz() {
        assertEquals(EzanDavranisi.Eylem.YOK,EzanDavranisi.karar(false,true,true,true,0))
        assertEquals(EzanDavranisi.Eylem.YOK,EzanDavranisi.karar(true,true,false,true,60_000))
        assertEquals(EzanDavranisi.Eylem.YOK,EzanDavranisi.karar(true,true,false,true,-1))
    }
}
