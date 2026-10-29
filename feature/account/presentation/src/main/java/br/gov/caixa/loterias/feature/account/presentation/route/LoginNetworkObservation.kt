package br.gov.caixa.loterias.feature.account.presentation.route

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.LocalLifecycleOwner
import br.gov.caixa.loterias.core.common.network.ConnectionAvailability
import br.gov.caixa.loterias.core.common.network.NetworkAvailability
import java.util.concurrent.atomic.AtomicBoolean

/** O conteúdo do navegador pertence ao provedor SSO; observa a conectividade do dispositivo, e não seu DOM ou credenciais. */
@Composable
internal fun ObserveLoginConnection(network: NetworkAvailability?, authorizationPending: () -> Boolean, onDisconnected: () -> Unit) {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val pending = rememberUpdatedState(authorizationPending)
    val disconnected = rememberUpdatedState(onDisconnected)
    val check = rememberUpdatedState {
        // A rede pode ser bloqueada apenas para o app em segundo plano enquanto o navegador SSO permanece online.
        // A retomada do hospedeiro confirma uma desconexão real antes de invalidar a autorização em curso.
        if (lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED) &&
            pending.value() && network?.current() == ConnectionAvailability.DISCONNECTED
        ) {
            disconnected.value()
        }
    }
    LifecycleResumeEffect(network) {
        check.value()
        onPauseOrDispose { }
    }
    DisposableEffect(context, network) {
        val manager = context.getSystemService(ConnectivityManager::class.java)
        val alive = AtomicBoolean(true)
        val handler = Handler(Looper.getMainLooper())
        val callback = object : ConnectivityManager.NetworkCallback() {
            private fun changed() {
                handler.post { if (alive.get()) check.value() }
            }
            override fun onLost(network: Network) = changed()
            override fun onCapabilitiesChanged(network: Network, capabilities: NetworkCapabilities) = changed()
        }
        val registered = try {
            if (network != null && manager != null) {
                manager.registerDefaultNetworkCallback(callback)
                true
            } else {
                false
            }
        } catch (_: SecurityException) {
            false
        }
        onDispose {
            alive.set(false)
            if (registered) manager?.unregisterNetworkCallback(callback)
        }
    }
}
