package br.gov.caixa.loterias.core.queue.android

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import br.gov.caixa.loterias.core.designsystem.components.state.CaixaDSGateMessage as GateMessage
import br.gov.caixa.loterias.core.queue.parser.QueueConsoleEvent
import br.gov.caixa.loterias.core.queue.parser.QueueConsoleEventParser
import br.gov.caixa.loterias.core.designsystem.web.CaixaDSWebPage
import kotlinx.coroutines.delay
import br.gov.caixa.loterias.core.queue.security.QueueOriginPolicy
import br.gov.caixa.loterias.core.queue.R

@Composable
fun FilaBraQueueWebView(location: String, activity: Activity, onConnectionError: (() -> Unit)? = null) {
    val secureLocation = remember(location) { location.takeIf(QueueOriginPolicy::isQueuePage) }
    var pendingPass by remember { mutableStateOf<QueueConsoleEvent.Pass?>(null) }
    var rejected by remember { mutableStateOf(false) }
    var connectionFailed by remember { mutableStateOf(false) }

    fun broadcast(event: QueueConsoleEvent) = broadcastQueueEvent(activity, event)

    val currentBroadcast = rememberUpdatedState<(QueueConsoleEvent) -> Unit>(::broadcast)
    val currentPendingPassHandler = rememberUpdatedState<(QueueConsoleEvent.Pass) -> Unit> {
        pendingPass = it
    }

    BackHandler {
        activity.sendBroadcast(
            Intent("${activity.packageName}.com.filabra.adapter.FILABRA_BROADCAST")
                .setPackage(activity.packageName)
                .putExtra("result", "ABANDON")
                .putExtra("cause", "Back"),
        )
        activity.finish()
    }

    if (secureLocation == null) {
        LaunchedEffect(Unit) {
            broadcast(QueueConsoleEvent.Error(activity.getString(R.string.queue_invalid_address)))
            activity.finish()
        }
        GateMessage(activity.getString(R.string.queue_open_failed))
        return
    }

    pendingPass?.let { pass ->
        LaunchedEffect(pass) {
            broadcast(pass)
            delay(pass.delayMillis.coerceAtMost(60_000))
            activity.finish()
        }
    }

    val queueUrl = remember(secureLocation) {
        secureLocation + if ('?' in secureLocation) "&qfnoredirect=true" else "?qfnoredirect=true"
    }
    CaixaDSWebPage(
        url = queueUrl,
        modifier = Modifier.fillMaxSize(),
        javaScriptEnabled = true,
        captureBackPresses = false,
        isUrlAllowed = { QueueOriginPolicy.isSameQueueOrigin(secureLocation, it) },
        isConsoleSourceAllowed = { !rejected && !connectionFailed && QueueOriginPolicy.isSameQueueOrigin(secureLocation, it) },
        onNavigationBlocked = {
            if (!rejected) {
                rejected = true
                pendingPass = null
                broadcast(QueueConsoleEvent.Error(activity.getString(R.string.queue_invalid_address)))
                activity.finish()
            }
        },
        onConnectionError = onConnectionError?.let { callback -> {
            if (!rejected && !connectionFailed && pendingPass == null) {
                connectionFailed = true
                callback()
            }
        } },
        onConsoleMessage = { message ->
            if (rejected || connectionFailed) return@CaixaDSWebPage
            when (val event = QueueConsoleEventParser.parse(message)) {
                is QueueConsoleEvent.Pass -> currentPendingPassHandler.value(event)
                else -> currentBroadcast.value(event)
            }
        },
    )
}
