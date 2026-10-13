package br.gov.caixa.loterias.apostas

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import br.gov.caixa.loterias.core.domain.configuration.PrepareNetworkSecurityUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/** Re-entry refresh has no navigation effect; the initial entry remains guarded by startup. */
internal class NetworkSecurityLifecycleObserver(
    private val prepare: PrepareNetworkSecurityUseCase,
    private val scope: CoroutineScope,
    private val dispatcher: CoroutineDispatcher,
) : DefaultLifecycleObserver {
    private var hasStopped = false

    override fun onStop(owner: LifecycleOwner) { hasStopped = true }

    override fun onStart(owner: LifecycleOwner) {
        if (hasStopped) scope.launch(dispatcher) { prepare() }
    }
}
