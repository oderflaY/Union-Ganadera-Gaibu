package com.gaibu.unionganadera.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Nfc
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.gaibu.unionganadera.nfc.NfcState

private val RojoNfc = Color(0xFFE3000F)
private val RojoDark = Color(0xFFB0000A)
private val BlancoPuro = Color(0xFFFFFFFF)
private val VerdeExito = Color(0xFF00C853)
private val AmarilloEstado = Color(0xFFFFD600)

@Composable
actual fun NfcScreen(navController: NavController, nfcEstado: String) {

    val estado by NfcState.estado.collectAsState()
    val uid by NfcState.uid.collectAsState()
    val techList by NfcState.techList.collectAsState()
    val tagDetectado = uid.isNotEmpty()

    val pulseAnim = rememberInfiniteTransition(label = "pulse")
    val scale by pulseAnim.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RojoNfc)
            .statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        AnimatedContent(
            targetState = tagDetectado,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "titulo"
        ) { detectado ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = if (detectado) "Tag Detectado" else "NFC Activo",
                    color = BlancoPuro,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.3.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (detectado) "Lectura exitosa" else "Acerca tu dispositivo para escanear",
                    color = BlancoPuro.copy(alpha = 0.8f),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        // Tres círculos concéntricos
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(240.dp)
                .scale(if (tagDetectado) 1f else scale)
                .clip(CircleShape)
                .background(
                    if (tagDetectado) VerdeExito.copy(alpha = 0.25f)
                    else BlancoPuro.copy(alpha = 0.12f)
                )
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(170.dp)
                    .clip(CircleShape)
                    .background(
                        if (tagDetectado) VerdeExito.copy(alpha = 0.2f)
                        else BlancoPuro.copy(alpha = 0.15f)
                    )
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        .background(BlancoPuro)
                ) {
                    AnimatedContent(
                        targetState = tagDetectado,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = "icono"
                    ) { detectado ->
                        if (detectado) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = "Tag detectado",
                                tint = VerdeExito,
                                modifier = Modifier.size(56.dp)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Filled.Nfc,
                                contentDescription = "NFC",
                                tint = RojoNfc,
                                modifier = Modifier.size(56.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Panel inferior
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .background(RojoDark)
                .padding(horizontal = 24.dp, vertical = 28.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            InfoRow(
                label = "Estado",
                valor = estado,
                valorColor = if (tagDetectado) VerdeExito else AmarilloEstado
            )
            HorizontalDivider(color = BlancoPuro.copy(alpha = 0.1f), thickness = 1.dp)
            InfoRow(
                label = "Tipo de conexión",
                valor = if (techList.isNotEmpty()) techList.first() else "—",
                valorColor = BlancoPuro
            )
            if (uid.isNotEmpty()) {
                HorizontalDivider(color = BlancoPuro.copy(alpha = 0.1f), thickness = 1.dp)
                InfoRow(
                    label = "UID",
                    valor = uid.uppercase(),
                    valorColor = BlancoPuro
                )
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, valor: String, valorColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = BlancoPuro.copy(alpha = 0.7f),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = valor,
            color = valorColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}