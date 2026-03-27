package com.gaibu.unionganadera.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.gaibu.unionganadera.nfc.NfcState

private val Rojo = Color(0xFFE3000F)
private val RojoOscuro = Color(0xFFB0000A)
private val RojoMuyOscuro = Color(0xFF6B0000)
private val Blanco = Color(0xFFFFFFFF)
private val Verde = Color(0xFF00E676)
private val VerdeOscuro = Color(0xFF00C853)
private val Amarillo = Color(0xFFFFD600)
private val Negro = Color(0xFF0A0A0A)

@Composable
actual fun NfcScreen(navController: NavController, nfcEstado: String) {

    val estado by NfcState.estado.collectAsState()
    val uid by NfcState.uid.collectAsState()
    val techList by NfcState.techList.collectAsState()
    val tagDetectado = uid.isNotEmpty()

    // Animación pulso principal
    val pulseAnim = rememberInfiniteTransition(label = "pulse")
    val scale1 by pulseAnim.animateFloat(
        initialValue = 0.92f, targetValue = 1.08f,
        animationSpec = infiniteRepeatable(tween(1000, easing = EaseInOut), RepeatMode.Reverse),
        label = "s1"
    )
    val scale2 by pulseAnim.animateFloat(
        initialValue = 0.95f, targetValue = 1.05f,
        animationSpec = infiniteRepeatable(tween(1200, easing = EaseInOut, delayMillis = 200), RepeatMode.Reverse),
        label = "s2"
    )
    val scale3 by pulseAnim.animateFloat(
        initialValue = 0.97f, targetValue = 1.03f,
        animationSpec = infiniteRepeatable(tween(1400, easing = EaseInOut, delayMillis = 400), RepeatMode.Reverse),
        label = "s3"
    )

    // Animación éxito
    val successScale by animateFloatAsState(
        targetValue = if (tagDetectado) 1f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "success"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    if (tagDetectado)
                        listOf(Color(0xFF003300), Color(0xFF001A00), Negro)
                    else
                        listOf(Rojo, Color(0xFFCC0000), RojoOscuro, Color(0xFF5A0000))
                )
            )
            .statusBarsPadding()
    ) {

        // Glow de fondo animado
        if (!tagDetectado) {
            Box(
                modifier = Modifier
                    .size(400.dp)
                    .align(Alignment.Center)
                    .offset(y = (-40).dp)
                    .scale(scale1)
                    .blur(80.dp)
                    .background(
                        Brush.radialGradient(listOf(Blanco.copy(alpha = 0.08f), Color.Transparent)),
                        CircleShape
                    )
            )
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // Título
            AnimatedContent(
                targetState = tagDetectado,
                transitionSpec = {
                    (fadeIn(tween(400)) + slideInVertically { -20 }) togetherWith
                            (fadeOut(tween(200)) + slideOutVertically { 20 })
                },
                label = "titulo"
            ) { detectado ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (detectado) "Tag Detectado" else "NFC Activo",
                        color = Blanco,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.5).sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (detectado) "Lectura exitosa" else "Acerca tu dispositivo para escanear",
                        color = Blanco.copy(alpha = 0.7f),
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(56.dp))

            // Círculos concéntricos
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(300.dp)
            ) {
                // Círculo 1 — más exterior
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .scale(if (tagDetectado) 1f else scale1)
                        .clip(CircleShape)
                        .background(
                            if (tagDetectado)
                                Verde.copy(alpha = 0.08f)
                            else
                                Blanco.copy(alpha = 0.07f)
                        )
                )
                // Círculo 2
                Box(
                    modifier = Modifier
                        .size(220.dp)
                        .scale(if (tagDetectado) 1f else scale2)
                        .clip(CircleShape)
                        .background(
                            if (tagDetectado)
                                Verde.copy(alpha = 0.12f)
                            else
                                Blanco.copy(alpha = 0.1f)
                        )
                )
                // Círculo 3
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .scale(if (tagDetectado) 1f else scale3)
                        .clip(CircleShape)
                        .background(
                            if (tagDetectado)
                                Verde.copy(alpha = 0.18f)
                            else
                                Blanco.copy(alpha = 0.14f)
                        )
                )
                // Círculo blanco central
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Blanco)
                        .border(
                            3.dp,
                            if (tagDetectado) Verde else Blanco,
                            CircleShape
                        )
                ) {
                    AnimatedContent(
                        targetState = tagDetectado,
                        transitionSpec = {
                            scaleIn(spring(Spring.DampingRatioMediumBouncy)) +
                                    fadeIn() togetherWith scaleOut() + fadeOut()
                        },
                        label = "icono"
                    ) { detectado ->
                        Icon(
                            imageVector = if (detectado) Icons.Filled.CheckCircle else Icons.Filled.Nfc,
                            contentDescription = null,
                            tint = if (detectado) VerdeOscuro else Rojo,
                            modifier = Modifier.size(52.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Panel inferior
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                    .background(
                        Brush.verticalGradient(
                            if (tagDetectado)
                                listOf(Color(0xFF0D2B0D), Color(0xFF071507))
                            else
                                listOf(RojoMuyOscuro, Color(0xFF3D0000))
                        )
                    )
                    .border(
                        width = 1.dp,
                        brush = Brush.horizontalGradient(
                            if (tagDetectado)
                                listOf(Verde.copy(alpha = 0.3f), Verde.copy(alpha = 0.1f))
                            else
                                listOf(Blanco.copy(alpha = 0.15f), Blanco.copy(alpha = 0.05f))
                        ),
                        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                    )
                    .navigationBarsPadding()
                    .padding(horizontal = 28.dp, vertical = 28.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Handle
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Blanco.copy(alpha = 0.2f))
                        .align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(4.dp))

                InfoFila(
                    label = "Estado",
                    valor = estado,
                    valorColor = if (tagDetectado) Verde else Amarillo
                )
                HorizontalDivider(color = Blanco.copy(alpha = 0.08f))
                InfoFila(
                    label = "Tipo de conexión",
                    valor = if (techList.isNotEmpty()) techList.first() else "—",
                    valorColor = Blanco
                )
                if (uid.isNotEmpty()) {
                    HorizontalDivider(color = Blanco.copy(alpha = 0.08f))
                    InfoFila(
                        label = "UID",
                        valor = uid.uppercase(),
                        valorColor = Blanco
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoFila(label: String, valor: String, valorColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = Blanco.copy(alpha = 0.55f),
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