package com.atasoyyazilim.kurankerim.ui.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.atasoyyazilim.kurankerim.ui.ekranlar.TasarimBekleyenEkran

/**
 * Gezinme. Codex her ekranı yazdıkça ilgili satırdaki TasarimBekleyenEkran'ı gerçek ekranla değiştirir,
 * örn. `composable(Ekran.MUSHAF.rota) { MushafEkrani(...) }`.
 */
@Composable
fun GezinmeGrafigi(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Ekran.ANA.rota) {
        Ekran.entries.forEach { ekran ->
            composable(ekran.rota) {
                TasarimBekleyenEkran(
                    ekran = ekran,
                    git = { hedef -> navController.navigate(hedef.rota) { launchSingleTop = true } },
                    geri = { navController.popBackStack() }
                )
            }
        }
    }
}
