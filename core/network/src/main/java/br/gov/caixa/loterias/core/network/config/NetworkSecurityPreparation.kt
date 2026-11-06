package br.gov.caixa.loterias.core.network.config

import br.gov.caixa.loterias.core.common.result.appResultOf
import br.gov.caixa.loterias.core.domain.configuration.usecase.PrepareNetworkSecurityUseCase
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/** Serializa resolução E instalação para impedir que um chamador antigo instale após um mais recente. */
class NetworkSecurityPreparation(
    private val prepare: suspend () -> Unit,
) : PrepareNetworkSecurityUseCase {
    private val mutex = Mutex()

    override suspend fun invoke() = appResultOf {
        mutex.withLock { prepare() }
    }
}
