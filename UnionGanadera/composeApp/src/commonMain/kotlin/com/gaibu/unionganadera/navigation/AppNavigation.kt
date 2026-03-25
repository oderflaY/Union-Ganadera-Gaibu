package com.gaibu.unionganadera.navigation



import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gaibu.unionganadera.ui.HomeScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }
        composable(Screen.Nfc.route) {
            NfcScreen(navController = navController)
        }
        composable(Screen.Reportes.route) {
            // ReportesScreen(navController)
        }
        composable(Screen.Configuracion.route) {
            // ConfiguracionScreen(navController)
        }
        composable(Screen.EscanerDocumentos.route) {
            // EscanerScreen(navController)
        }
        composable(Screen.CalculadoraAlimentos.route) {
            // CalculadoraScreen(navController)
        }
        composable(Screen.SubirDocumentosAnimales.route) {
            // SubirDocumentosScreen(navController)
        }
    }
}