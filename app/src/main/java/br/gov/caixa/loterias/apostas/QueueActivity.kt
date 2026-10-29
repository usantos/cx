package br.gov.caixa.loterias.apostas

import android.os.Bundle
import br.gov.caixa.loterias.apostas.composition.startup.QueueWebConnectionGate
import br.gov.caixa.loterias.core.common.network.NetworkAvailability
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.core.queue.android.FilaBraQueueWebView

/** Entrada exclusiva do SDK. O manifesto impede deliberadamente a abertura por outros aplicativos. */
@AndroidEntryPoint
class QueueActivity : ComponentActivity() {
    @Inject lateinit var networkAvailability: NetworkAvailability
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val location = intent.getStringExtra("location") ?: run { finish(); return }
        setContent { CaixaDSTheme(darkTheme = false) {
            QueueWebConnectionGate(networkAvailability) { onConnectionError ->
                FilaBraQueueWebView(location, this, onConnectionError)
            }
        } }
    }
}
