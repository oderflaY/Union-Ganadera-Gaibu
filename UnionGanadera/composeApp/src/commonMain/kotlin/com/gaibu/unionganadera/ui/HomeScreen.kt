package com.gaibu.unionganadera.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.Nfc
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.gaibu.unionganadera.navigation.Screen

val RojoUnion = Color(0xFFE3000F)
val GrisFondo = Color(0xFFF2F2F2)
val GrisTarjeta = Color(0xFFFFFFFF)

data class MenuOption(
    val titulo: String,
    val subtitulo: String,
    val icono: ImageVector,
    val route: String,
    val destacado: Boolean = false
)

@Composable
fun HomeScreen(navController: NavController) {

    val opciones = listOf(
        MenuOption("NFC", "Escanear tag", Icons.Filled.Nfc, Screen.Nfc.route, destacado = true),
        MenuOption("Escáner", "Digitalizar docs", Icons.Filled.DocumentScanner, Screen.EscanerDocumentos.route),
        MenuOption("Reportes", "Ver informes", Icons.Filled.BarChart, Screen.Reportes.route),
        MenuOption("Calculadora", "Alimentos animales", Icons.Filled.Calculate, Screen.CalculadoraAlimentos.route),
        MenuOption("Documentos", "Subir archivos", Icons.Filled.UploadFile, Screen.SubirDocumentosAnimales.route),
        MenuOption("Configuración", "Ajustes", Icons.Filled.Settings, Screen.Configuracion.route),
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GrisFondo)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(RojoUnion)
                .statusBarsPadding()
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            Column {
                Text(
                    text = "Unión Ganadera",
                    color = Color.White,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Selecciona una opción",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal
                )
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(opciones) { opcion ->
                MenuCard(opcion = opcion) {
                    navController.navigate(opcion.route)
                }
            }
        }
    }
}

@Composable
fun MenuCard(opcion: MenuOption, onClick: () -> Unit) {
    val bgColor = if (opcion.destacado) RojoUnion else GrisTarjeta
    val textColor = if (opcion.destacado) Color.White else Color(0xFF1A1A1A)
    val subTextColor = if (opcion.destacado) Color.White.copy(alpha = 0.8f) else Color(0xFF888888)
    val iconColor = if (opcion.destacado) Color.White else RojoUnion

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .shadow(6.dp, RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        if (opcion.destacado) Color.White.copy(alpha = 0.2f)
                        else RojoUnion.copy(alpha = 0.08f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = opcion.icono,
                    contentDescription = opcion.titulo,
                    tint = iconColor,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = opcion.titulo,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = opcion.subtitulo,
                fontSize = 11.sp,
                color = subTextColor
            )
        }
    }
}