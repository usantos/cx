package br.gov.caixa.loterias.core.network.config

import br.gov.caixa.loterias.core.keymanagement.model.HostPublicKeyPins
import br.gov.caixa.loterias.core.keymanagement.model.PublicKeySet
import br.gov.caixa.loterias.core.network.provider.BackendApiProvider
import br.gov.caixa.loterias.core.network.provider.BetsHistoryApiProvider
import br.gov.caixa.loterias.core.network.provider.LoteriasApiProvider
import br.gov.caixa.loterias.core.network.provider.PaymentApiProvider

/** Instala clientes com pins nos providers estáveis, preservando a ordem original de troca. */
class NetworkClientCoordinator(
    private val factory: BackendClientFactory,
    private val providers: NetworkApiProviders,
    private val localFallback: PublicKeySet,
    private val mockBackend: Boolean,
    private val resolveKeySet: suspend () -> br.gov.caixa.loterias.core.keymanagement.model.ResolvedPublicKeySet?,
    private val onKeySetResolved: (br.gov.caixa.loterias.core.keymanagement.model.ResolvedPublicKeySet?) -> Unit,
) {
    fun initialize() {
        providers.apiProvider.onParametersLoaded(factory::updateParameters)
        replaceMainBackendApis(localFallback)
    }

    val networkSecurity: br.gov.caixa.loterias.core.domain.configuration.usecase.PrepareNetworkSecurityUseCase =
        NetworkSecurityPreparation { updateNetworkSecurity() }

    private suspend fun updateNetworkSecurity() {
        if (mockBackend) {
            replaceMainBackendApis(localFallback)
            return
        }
        val resolved = resolveKeySet()
        replaceMainBackendApis(resolved?.keySet ?: localFallback)
        onKeySetResolved(resolved)
    }

    private fun replaceMainBackendApis(keySet: PublicKeySet) {
        providers.apiProvider.replace(factory.createApi(keySet))
        providers.notificationReadProvider.replace(factory.createNotificationRead(keySet))
        providers.notificationInboxProvider?.replace(factory.createNotificationInbox(keySet))
        providers.sessionLogoutProvider.replace(factory.createSessionLogout(keySet))
        providers.accountVerificationProvider.replace(factory.createAccountVerification(keySet))
        providers.prizeApiProvider.replace(factory.createPrizeRedemption(keySet))
        providers.purchaseApiProvider.replace(factory.createPurchaseBackend(keySet))
        providers.legacyApiProvider.replace(factory.createLegacy(keySet))
        providers.betsHistoryApiProvider.replace(factory.createBetsHistory(keySet))
        providers.betsHistoryApiProvider.configureDynamicClients(
            verifiedPinnedHosts = keySet.hosts.map(HostPublicKeyPins::hostname).toSet()
        ) { baseUrl -> factory.createDynamicBetsHistory(baseUrl, keySet) }
        providers.paymentApiProvider.replace(factory.createPaymentApis(keySet))
    }
}

class NetworkApiProviders(
    val apiProvider: LoteriasApiProvider,
    val legacyApiProvider: BackendApiProvider,
    val betsHistoryApiProvider: BetsHistoryApiProvider,
    val paymentApiProvider: PaymentApiProvider,
    val purchaseApiProvider: br.gov.caixa.loterias.core.network.api.PurchaseBackendApiProvider,
    val prizeApiProvider: br.gov.caixa.loterias.core.network.provider.PrizeBackendApiProvider,
    val accountVerificationProvider: br.gov.caixa.loterias.core.network.api.AccountVerificationBackendProvider,
    val notificationReadProvider: br.gov.caixa.loterias.core.network.api.NotificationReadApiProvider,
    val sessionLogoutProvider: br.gov.caixa.loterias.core.network.api.SessionLogoutApiProvider,
    val notificationInboxProvider: br.gov.caixa.loterias.core.network.api.NotificationInboxApiProvider? = null,
)
