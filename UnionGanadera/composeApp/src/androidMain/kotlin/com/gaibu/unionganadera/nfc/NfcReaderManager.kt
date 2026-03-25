package com.gaibu.unionganadera.nfc


import android.app.Activity
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle

class NfcReaderManager(
    private val activity: Activity,
    private val onTagDiscovered: (NfcTagData) -> Unit
) : NfcAdapter.ReaderCallback {

    private var nfcAdapter: NfcAdapter? = null

    init {
        nfcAdapter = NfcAdapter.getDefaultAdapter(activity)
    }

    fun enableReading() {
        val adapter = nfcAdapter ?: return

        if (!adapter.isEnabled) {
            onTagDiscovered(NfcTagData.Error("NFC desactivado. Actívalo en Ajustes."))
            return
        }

        val options = Bundle().apply {
            putInt(NfcAdapter.EXTRA_READER_PRESENCE_CHECK_DELAY, 250)
        }

        adapter.enableReaderMode(
            activity,
            this,
            NfcAdapter.FLAG_READER_NFC_A or
                    NfcAdapter.FLAG_READER_NFC_B or
                    NfcAdapter.FLAG_READER_NFC_F or
                    NfcAdapter.FLAG_READER_NFC_V or
                    NfcAdapter.FLAG_READER_NFC_BARCODE or
                    NfcAdapter.FLAG_READER_NO_PLATFORM_SOUNDS,
            options
        )
    }

    fun disableReading() {
        nfcAdapter?.disableReaderMode(activity)
    }

    override fun onTagDiscovered(tag: Tag?) {
        tag ?: return
        val parsed = NfcTagParser.parse(tag)
        activity.runOnUiThread {
            onTagDiscovered(parsed)
        }
    }

    fun isNfcAvailable(): Boolean = nfcAdapter != null

    fun isNfcEnabled(): Boolean = nfcAdapter?.isEnabled == true
}