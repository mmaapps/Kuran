package com.atasoyyazilim.kurankerim.ui.nav

import androidx.compose.runtime.*
import com.atasoyyazilim.kurankerim.ses.EzanServisi
import com.atasoyyazilim.kurankerim.ui.ekranlar.EzanAniEkrani
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.atasoyyazilim.kurankerim.ui.ekranlar.TasarimBekleyenEkran
import com.atasoyyazilim.kurankerim.ui.ekranlar.AnaSayfaEkrani
import com.atasoyyazilim.kurankerim.ui.ekranlar.MushafEkrani
import com.atasoyyazilim.kurankerim.ui.ekranlar.EzanVaktiEkrani

/**
 * Gezinme. Codex her ekranı yazdıkça ilgili satırdaki TasarimBekleyenEkran'ı gerçek ekranla değiştirir,
 * örn. `composable(Ekran.MUSHAF.rota) { MushafEkrani(...) }`.
 */
@Composable
fun GezinmeGrafigi(navController: NavHostController = rememberNavController(), guncellemeKontrol: () -> Unit = {}, ezanAcIstegi: Long = 0L) {
    val oynatma by EzanServisi.durum.collectAsState()
    LaunchedEffect(oynatma?.oturum, ezanAcIstegi) {
        if(oynatma!=null && navController.currentDestination?.route!=Ekran.EZAN_ANI.rota)
            navController.navigate(Ekran.EZAN_ANI.rota){launchSingleTop=true}
    }
    NavHost(navController = navController, startDestination = Ekran.ANA.rota) {
        Ekran.entries.forEach { ekran ->
            composable(ekran.rota) {
                if (ekran == Ekran.ANA) {
                    AnaSayfaEkrani(git = { hedef -> navController.navigate(hedef.rota) { launchSingleTop = true } })
                } else if (ekran == Ekran.MUSHAF) {
                    MushafEkrani(geri = { navController.popBackStack() })
                } else if (ekran == Ekran.VAKITLER) {
                    EzanVaktiEkrani(git = { hedef -> navController.navigate(hedef.rota) { launchSingleTop = true } })
                } else if(ekran == Ekran.EZAN_ANI) {
                    EzanAniEkrani(
                        kapat={navController.navigate(Ekran.ANA.rota){popUpTo(Ekran.ANA.rota){inclusive=false};launchSingleTop=true}},
                        susturSonrasi={navController.navigate(Ekran.VAKITLER.rota){popUpTo(Ekran.ANA.rota){inclusive=false};launchSingleTop=true}}
                    )
                } else TasarimBekleyenEkran(
                    ekran = ekran,
                    guncellemeKontrol = guncellemeKontrol,
                    git = { hedef -> navController.navigate(hedef.rota) { launchSingleTop = true } },
                    geri = { navController.popBackStack() }
                )
            }
        }
    }
}
