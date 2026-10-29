package br.gov.caixa.loterias.apostas.composition.startup

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LifecycleResumeEffect
import br.gov.caixa.loterias.core.common.network.ConnectionAvailability
import br.gov.caixa.loterias.core.common.network.NetworkAvailability
import br.gov.caixa.loterias.feature.onboarding.presentation.screen.StartupStatusKind
import java.util.concurrent.atomic.AtomicBoolean

/** Tenta novamente apenas a página atual da fila: nunca limpa seus cookies nem solicita uma nova entrada na fila. */
@Composable
internal fun QueueWebConnectionGate(
    networkAvailability: NetworkAvailability,
    page: @Composable (onConnectionError: () -> Unit) -> Unit,
) {
    var offline by rememberSaveable { mutableStateOf(networkAvailability.current() == ConnectionAvailability.DISCONNECTED) }
    var attempt by rememberSaveable { mutableIntStateOf(0) }
    val context = LocalContext.current
    val connectionLost = rememberUpdatedState {
        if (networkAvailability.current() == ConnectionAvailability.DISCONNECTED) offline = true
    }
    LifecycleResumeEffect(networkAvailability) {
        connectionLost.value()
        onPauseOrDispose { }
    }
    DisposableEffect(context, networkAvailability) {
        val connectivity = context.getSystemService(ConnectivityManager::class.java)
        val active = AtomicBoolean(true)
        val handler = Handler(Looper.getMainLooper())
        val callback = object : ConnectivityManager.NetworkCallback() {
            private fun changed() { handler.post { if (active.get()) connectionLost.value() } }
            override fun onLost(network: Network) = changed()
            override fun onCapabilitiesChanged(network: Network, capabilities: NetworkCapabilities) = changed()
        }
        val registered = try {
            connectivity?.registerDefaultNetworkCallback(callback)
            connectivity != null
        } catch (_: SecurityException) { false }
        onDispose {
            active.set(false)
            if (registered) connectivity?.unregisterNetworkCallback(callback)
        }
    }
    if (offline) {
        AppStartupStatusPanel(
            kind = StartupStatusKind.OFFLINE,
            networkAvailability = networkAvailability,
            connectionFailure = true,
            onRetry = {
                offline = networkAvailability.current() == ConnectionAvailability.DISCONNECTED
                if (!offline) attempt++
            },
        )
    } else key(attempt) {
        page { offline = true }
    }
}
