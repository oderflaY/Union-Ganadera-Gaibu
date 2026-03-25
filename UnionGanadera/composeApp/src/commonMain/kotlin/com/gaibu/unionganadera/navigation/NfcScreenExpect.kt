package com.gaibu.unionganadera.navigation


import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
expect fun NfcScreen(navController: NavController, nfcEstado: String = "Buscando...")