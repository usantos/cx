package br.gov.caixa.loterias.core.security.lifecycle

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import br.gov.caixa.loterias.core.domain.configuration.usecase.PrepareNetworkSecurityUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/** A atualização ao retornar não afeta a navegação; a entrada inicial permanece protegida pela inicialização. */
class NetworkSecurityLifecycleObserver(
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
