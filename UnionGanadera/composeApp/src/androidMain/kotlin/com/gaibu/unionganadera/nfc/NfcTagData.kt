package com.gaibu.unionganadera.nfc


sealed class NfcTagData {

    data class Success(
        val uid: String,
        val techList: List<String>,
        val ndefMessages: List<String> = emptyList()
    ) : NfcTagData()

    data class Error(val message: String) : NfcTagData()
}