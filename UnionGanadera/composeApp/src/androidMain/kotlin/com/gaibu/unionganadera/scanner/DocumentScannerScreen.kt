package com.gaibu.unionganadera.navigation

import android.app.Activity
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.gaibu.unionganadera.scanner.ScannerState
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Image
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

private val Rojo = Color(0xFFE3000F)
private val RojoDark = Color(0xFF8B0000)
private val Blanco = Color(0xFFFFFFFF)
private val Gris = Color(0xFF121212)
private val GrisTarjeta = Color(0xFF1E1E1E)
private val GrisBorde = Color(0xFF2E2E2E)
private val Verde = Color(0xFF00C853)

@Composable
actual fun DocumentScannerScreen(navController: NavController) {

    val context = LocalContext.current
    val activity = context as Activity

    val paginas by ScannerState.paginasEscaneadas.collectAsState()
    var escaneadorListo by remember { mutableStateOf(false) }
    var guardadoExitoso by remember { mutableStateOf(false) }
    var nombreArchivo by remember { mutableStateOf("") }
    var paginaAEliminar by remember { mutableStateOf<Uri?>(null) }
    var mostrarDialogoNombre by remember { mutableStateOf(false) }
    var nombrePdf by remember { mutableStateOf("") }

    val lazyListState = rememberLazyListState()
    val reorderableState = rememberReorderableLazyListState(lazyListState) { from, to ->
        ScannerState.reordenar(from.index, to.index)
    }

    val options = remember {
        GmsDocumentScannerOptions.Builder()
            .setGalleryImportAllowed(true)
            .setPageLimit(10)
            .setResultFormats(
                GmsDocumentScannerOptions.RESULT_FORMAT_JPEG,
                GmsDocumentScannerOptions.RESULT_FORMAT_PDF
            )
            .setScannerMode(GmsDocumentScannerOptions.SCANNER_MODE_FULL)
            .build()
    }

    val scanner = remember { GmsDocumentScanning.getClient(options) }

    val scannerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val scanResult = GmsDocumentScanningResult.fromActivityResultIntent(result.data)
            val uris = scanResult?.pages?.map { it.imageUri } ?: emptyList()
            ScannerState.agregarPaginas(uris)
        } else {
            if (paginas.isEmpty()) navController.popBackStack()
        }
        escaneadorListo = true
    }

    fun guardarPDF(nombre: String) {
        try {
            val carpeta = File(context.filesDir, "documentos").apply { mkdirs() }
            val nombreFinal = nombre.trim().ifBlank {
                SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            }
            val archivo = File(carpeta, "$nombreFinal.pdf")
            val pdfWriter = PdfWriter(archivo)
            val pdfDoc = PdfDocument(pdfWriter)
            val document = Document(pdfDoc)
            paginas.forEach { uri ->
                val stream = context.contentResolver.openInputStream(uri)
                val bytes = stream?.readBytes() ?: return@forEach
                stream.close()
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                val pageSize = com.itextpdf.kernel.geom.PageSize(
                    bitmap.width.toFloat(), bitmap.height.toFloat()
                )
                pdfDoc.addNewPage(pageSize)
                val image = Image(ImageDataFactory.create(bytes))
                image.setFixedPosition(pdfDoc.numberOfPages, 0f, 0f)
                image.scaleToFit(bitmap.width.toFloat(), bitmap.height.toFloat())
                document.add(image)
            }
            document.close()
            nombreArchivo = "$nombreFinal.pdf"
            guardadoExitoso = true
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun iniciarEscaneo() {
        scanner.getStartScanIntent(activity)
            .addOnSuccessListener { intentSender ->
                scannerLauncher.launch(IntentSenderRequest.Builder(intentSender).build())
            }
            .addOnFailureListener { navController.popBackStack() }
    }

    LaunchedEffect(Unit) { iniciarEscaneo() }

    // ── Diálogo nombre PDF ──────────────────────────────────────────
    if (mostrarDialogoNombre) {
        Dialog(
            onDismissRequest = { mostrarDialogoNombre = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(GrisTarjeta)
                    .padding(24.dp)
            ) {
                Column {
                    // Icono PDF
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Rojo.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Filled.PictureAsPdf,
                            contentDescription = null,
                            tint = Rojo,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Nombre del documento",
                        color = Blanco,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Escribe cómo quieres guardar tu PDF",
                        color = Blanco.copy(alpha = 0.5f),
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    OutlinedTextField(
                        value = nombrePdf,
                        onValueChange = { nombrePdf = it },
                        placeholder = {
                            Text("Ej: Contrato_Enero", color = Blanco.copy(alpha = 0.3f))
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Blanco,
                            unfocusedTextColor = Blanco,
                            focusedBorderColor = Rojo,
                            unfocusedBorderColor = GrisBorde,
                            cursorColor = Rojo,
                            focusedContainerColor = Color(0xFF2A2A2A),
                            unfocusedContainerColor = Color(0xFF2A2A2A)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "📄 ${nombrePdf.trim().ifBlank { "documento_fecha" }}.pdf",
                        color = Blanco.copy(alpha = 0.35f),
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = { mostrarDialogoNombre = false },
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp, GrisBorde
                            )
                        ) {
                            Text("Cancelar", color = Blanco.copy(alpha = 0.7f))
                        }
                        Button(
                            onClick = {
                                mostrarDialogoNombre = false
                                guardarPDF(nombrePdf)
                            },
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Rojo)
                        ) {
                            Text("Guardar", color = Blanco, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // ── Diálogo eliminar ───────────────────────────────────────────
    paginaAEliminar?.let { uri ->
        Dialog(onDismissRequest = { paginaAEliminar = null }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(GrisTarjeta)
                    .padding(24.dp)
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color.Red.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Filled.DeleteForever,
                            contentDescription = null,
                            tint = Color.Red,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Eliminar página",
                        color = Blanco,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Esta acción no se puede deshacer.",
                        color = Blanco.copy(alpha = 0.5f),
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = { paginaAEliminar = null },
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, GrisBorde)
                        ) {
                            Text("Cancelar", color = Blanco.copy(alpha = 0.7f))
                        }
                        Button(
                            onClick = {
                                ScannerState.eliminarPagina(uri)
                                paginaAEliminar = null
                            },
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                        ) {
                            Text("Eliminar", color = Blanco, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // ── Diálogo éxito ──────────────────────────────────────────────
    if (guardadoExitoso) {
        Dialog(onDismissRequest = { }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(GrisTarjeta)
                    .padding(24.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Círculo verde animado
                    val scale by rememberInfiniteTransition(label = "").animateFloat(
                        initialValue = 0.95f,
                        targetValue = 1.05f,
                        animationSpec = infiniteRepeatable(
                            tween(800, easing = EaseInOut),
                            RepeatMode.Reverse
                        ),
                        label = "scale"
                    )
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .scale(scale)
                            .clip(CircleShape)
                            .background(Verde.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = Verde,
                            modifier = Modifier.size(44.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "¡PDF Guardado!",
                        color = Blanco,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Tu documento fue guardado exitosamente",
                        color = Blanco.copy(alpha = 0.6f),
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    // Chip con nombre del archivo
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Verde.copy(alpha = 0.1f))
                            .border(1.dp, Verde.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Filled.PictureAsPdf,
                                contentDescription = null,
                                tint = Verde,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = nombreArchivo,
                                color = Verde,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = {
                            guardadoExitoso = false
                            ScannerState.reset()
                            navController.popBackStack()
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Rojo)
                    ) {
                        Text("Listo", color = Blanco, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // ── Pantalla principal ─────────────────────────────────────────
    AnimatedVisibility(
        visible = escaneadorListo && paginas.isNotEmpty(),
        enter = fadeIn() + slideInVertically { it / 4 }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Gris)
        ) {
            // Header con gradiente
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(listOf(Rojo, Color(0xFFCC0000)))
                    )
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        ScannerState.reset()
                        navController.popBackStack()
                    }) {
                        Icon(Icons.Filled.Close, contentDescription = "Cerrar", tint = Blanco)
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Documento Escaneado",
                            color = Blanco,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${paginas.size} página${if (paginas.size != 1) "s" else ""}",
                            color = Blanco.copy(alpha = 0.75f),
                            fontSize = 12.sp
                        )
                    }
                    // Icono PDF
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Blanco.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Filled.PictureAsPdf,
                            contentDescription = null,
                            tint = Blanco,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            // Instrucción
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(GrisTarjeta)
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Filled.SwapHoriz,
                    contentDescription = null,
                    tint = Blanco.copy(alpha = 0.4f),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Mantén presionado para reordenar  •  X para eliminar",
                    color = Blanco.copy(alpha = 0.4f),
                    fontSize = 11.sp
                )
            }

            // Lista reordenable — más grande
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Gris),
                contentAlignment = Alignment.Center
            ) {
                LazyRow(
                    state = lazyListState,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(horizontal = 24.dp),
                    modifier = Modifier.fillMaxHeight()
                ) {
                    itemsIndexed(paginas, key = { _, uri -> uri.toString() }) { index, uri ->
                        ReorderableItem(reorderableState, key = uri.toString()) { isDragging ->
                            val elevation by animateDpAsState(
                                if (isDragging) 16.dp else 4.dp,
                                label = "elevation"
                            )
                            Box(
                                modifier = Modifier
                                    .scale(if (isDragging) 1.04f else 1f)
                                    .shadow(elevation, RoundedCornerShape(16.dp))
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .longPressDraggableHandle()
                                        .padding(vertical = 16.dp)
                                ) {
                                    Box {
                                        AsyncImage(
                                            model = uri,
                                            contentDescription = "Página ${index + 1}",
                                            contentScale = ContentScale.Fit,
                                            modifier = Modifier
                                                .width(200.dp)
                                                .fillMaxHeight(0.75f)
                                                .clip(RoundedCornerShape(16.dp))
                                                .border(
                                                    2.dp,
                                                    if (isDragging) Blanco else Rojo,
                                                    RoundedCornerShape(16.dp)
                                                )
                                        )
                                        // Botón eliminar
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .padding(6.dp)
                                                .size(30.dp)
                                                .clip(CircleShape)
                                                .background(Color.Black.copy(alpha = 0.7f))
                                                .border(1.dp, GrisBorde, CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            IconButton(
                                                onClick = { paginaAEliminar = uri },
                                                modifier = Modifier.size(30.dp)
                                            ) {
                                                Icon(
                                                    Icons.Filled.Close,
                                                    contentDescription = "Eliminar",
                                                    tint = Blanco,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                            }
                                        }
                                        // Badge número
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.BottomStart)
                                                .padding(6.dp)
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(Color.Black.copy(alpha = 0.6f))
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = "${index + 1}",
                                                color = Blanco,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Barra inferior
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                    .background(GrisTarjeta)
                    .border(
                        width = 1.dp,
                        color = GrisBorde,
                        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
                    )
                    .navigationBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { iniciarEscaneo() },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2A2A2A),
                        contentColor = Blanco
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GrisBorde)
                ) {
                    Icon(Icons.Filled.Add, contentDescription = null, tint = Rojo)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Escanear otra página", fontWeight = FontWeight.SemiBold)
                }

                Button(
                    onClick = {
                        nombrePdf = ""
                        mostrarDialogoNombre = true
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Rojo)
                ) {
                    Icon(Icons.Filled.PictureAsPdf, contentDescription = null, tint = Blanco)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Guardar como PDF", fontWeight = FontWeight.Bold, color = Blanco)
                }
            }
        }
    }
}