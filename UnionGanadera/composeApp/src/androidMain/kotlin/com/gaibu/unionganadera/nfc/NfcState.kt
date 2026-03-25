package com.gaibu.unionganadera.nfc

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object NfcState {
    private val _estado = MutableStateFlow("Buscando...")
    val estado: StateFlow<String> = _estado

    private val _uid = MutableStateFlow("")
    val uid: StateFlow<String> = _uid

    private val _techList = MutableStateFlow<List<String>>(emptyList())
    val techList: StateFlow<List<String>> = _techList

    fun onTagDetectado(data: NfcTagData) {
        when (data) {
            is NfcTagData.Success -> {
                _estado.value = "Tag detectado ✅"
                _uid.value = data.uid
                _techList.value = data.techList
            }
            is NfcTagData.Error -> {
                _estado.value = "Error: ${data.message}"
                _uid.value = ""
                _techList.value = emptyList()
            }
        }
    }

    fun reset() {
        _estado.value = "Buscando..."
        _uid.value = ""
        _techList.value = emptyList()
    }
}