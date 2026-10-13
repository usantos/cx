package br.gov.caixa.loterias.apostas

import br.gov.caixa.loterias.core.common.appResultOf
import br.gov.caixa.loterias.core.domain.configuration.PrepareNetworkSecurityUseCase
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/** Serializes resolution AND installation, so an older caller cannot install after a newer one. */
internal class NetworkSecurityPreparation(
    private val prepare: suspend () -> Unit,
) : PrepareNetworkSecurityUseCase {
    private val mutex = Mutex()

    override suspend fun invoke() = appResultOf {
        mutex.withLock { prepare() }
    }
}
