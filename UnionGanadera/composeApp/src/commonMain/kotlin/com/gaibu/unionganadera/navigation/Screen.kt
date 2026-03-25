package com.gaibu.unionganadera.navigation


sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Nfc : Screen("nfc")
    object Reportes : Screen("reportes")
    object Configuracion : Screen("configuracion")
    object EscanerDocumentos : Screen("escaner_documentos")
    object CalculadoraAlimentos : Screen("calculadora_alimentos")
    object SubirDocumentosAnimales : Screen("subir_documentos_animales")
}