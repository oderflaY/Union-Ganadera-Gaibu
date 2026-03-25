package com.gaibu.unionganadera

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.gaibu.unionganadera.nfc.NfcReaderManager
import com.gaibu.unionganadera.nfc.NfcState

class MainActivity : ComponentActivity() {

    private lateinit var nfcReaderManager: NfcReaderManager

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        nfcReaderManager = NfcReaderManager(this) { tagData ->
            NfcState.onTagDetectado(tagData)
        }

        setContent {
            App()
        }
    }

    override fun onResume() {
        super.onResume()
        NfcState.reset()
        nfcReaderManager.enableReading()
    }

    override fun onPause() {
        super.onPause()
        nfcReaderManager.disableReading()
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}