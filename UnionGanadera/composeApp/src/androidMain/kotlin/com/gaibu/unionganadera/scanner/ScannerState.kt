package com.gaibu.unionganadera.scanner

import android.net.Uri
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object ScannerState {
    private val _paginas = MutableStateFlow<List<Uri>>(emptyList())
    val paginasEscaneadas: StateFlow<List<Uri>> = _paginas

    private val _estado = MutableStateFlow("Listo para escanear")
    val estado: StateFlow<String> = _estado

    fun agregarPaginas(uris: List<Uri>) {
        _paginas.value = _paginas.value + uris
        actualizarEstado()
    }

    fun eliminarPagina(uri: Uri) {
        _paginas.value = _paginas.value.filter { it != uri }
        actualizarEstado()
    }

    fun reordenar(desde: Int, hasta: Int) {
        val lista = _paginas.value.toMutableList()
        val item = lista.removeAt(desde)
        lista.add(hasta, item)
        _paginas.value = lista
        actualizarEstado()
    }

    private fun actualizarEstado() {
        _estado.value = "${_paginas.value.size} página(s) escaneada(s)"
    }

    fun reset() {
        _paginas.value = emptyList()
        _estado.value = "Listo para escanear"
    }
}