package com.gaibu.unionganadera.nfc


import android.nfc.Tag
import android.nfc.tech.Ndef

object NfcTagParser {

    fun parse(tag: Tag): NfcTagData {
        return try {
            val uid = tag.id.toHexString()
            val techList = tag.techList.map { it.substringAfterLast('.') }
            val ndefMessages = readNdefMessages(tag)

            NfcTagData.Success(
                uid = uid,
                techList = techList,
                ndefMessages = ndefMessages
            )
        } catch (e: Exception) {
            NfcTagData.Error("Error al leer tag: ${e.message}")
        }
    }

    private fun readNdefMessages(tag: Tag): List<String> {
        val ndef = Ndef.get(tag) ?: return emptyList()
        return try {
            ndef.connect()
            val ndefMessage = ndef.ndefMessage
            ndefMessage?.records?.map { record ->
                String(record.payload.drop(3).toByteArray())
            } ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        } finally {
            try { ndef.close() } catch (_: Exception) {}
        }
    }

    private fun ByteArray.toHexString(): String =
        joinToString("") { "%02x".format(it) }
}