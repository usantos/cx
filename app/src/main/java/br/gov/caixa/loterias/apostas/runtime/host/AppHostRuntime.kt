package br.gov.caixa.loterias.apostas.runtime.host

import br.gov.caixa.loterias.apostas.environment.runtime.ProcessEnvironmentSettings
import br.gov.caixa.loterias.core.network.config.NetworkClientCoordinator
import br.gov.caixa.loterias.core.auth.gateway.AuthenticationGateway
import br.gov.caixa.loterias.core.auth.session.SessionTokenStore
import java.util.concurrent.atomic.AtomicBoolean
import kotlinx.coroutines.withContext
import br.gov.caixa.loterias.core.domain.environment.usecase.applyEnvironmentChange

internal class AppHostRuntimeInitialization(
    private val initializeNetworkClients: () -> Unit,
) {
    private val initialized = AtomicBoolean(false)

    fun initialize() {
        if (initialized.compareAndSet(false, true)) initializeNetworkClients()
    }
}

internal class AppHostRuntime(private val settings: ProcessEnvironmentSettings,
    private val authentication: AuthenticationGateway,
    private val tokens: SessionTokenStore,
    private val networkClients: dagger.Lazy<NetworkClientCoordinator>,
    dispatchers: br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers,
) {
    val environmentChange: br.gov.caixa.loterias.feature.account.domain.environment.ChangeEnvironment =
        br.gov.caixa.loterias.feature.account.domain.environment.ChangeEnvironment { selected ->
            withContext(dispatchers.io) {
                if (settings.persistedSelection == selected.name) {
                    true
                } else {
                    applyEnvironmentChange(settings.selection, selected) {
                        authentication.cancelAuthorization()
                        tokens.clear()
                    }
                }
            }
        }
    private val initialization = AppHostRuntimeInitialization { networkClients.get().initialize() }
    fun initialize() = initialization.initialize()
}
