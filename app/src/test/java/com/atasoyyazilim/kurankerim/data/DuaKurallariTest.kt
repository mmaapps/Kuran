package com.atasoyyazilim.kurankerim.data
import org.junit.Assert.*
import org.junit.Test

class DuaKurallariTest {
    private fun dua(id:String)=Dua(id,"ad","kategori","ع","okunus","anlam","kaynak")
    @Test fun ayetReferanslari(){
        assertEquals(2 to 201,DuaKurallari.ayet("d001"))
        assertEquals(14 to 41,DuaKurallari.ayet("d002"))
        assertNull(DuaKurallari.ayet("d003"))
    }
    @Test fun ayetMealiSeslendirilmez(){
        for(id in listOf("d001","d002","yeni")){
            assertFalse(DuaKurallari.anlamGoster(id))
            assertEquals("okunus",DuaKurallari.sesliMetin(dua(id)))
        }
    }
    @Test fun duaAnlamiKorunur(){
        for(id in listOf("d003","d004","d005")){
            assertTrue(DuaKurallari.anlamGoster(id))
            assertEquals("okunus anlam",DuaKurallari.sesliMetin(dua(id)))
        }
    }
}
