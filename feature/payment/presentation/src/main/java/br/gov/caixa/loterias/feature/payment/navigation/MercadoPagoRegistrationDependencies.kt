package br.gov.caixa.loterias.feature.payment.navigation

/** Dependências compartilhadas do fluxo de registro; cada destino preserva seu próprio responsável pelo ViewModel. */
interface MercadoPagoRegistrationDependencies {
    val accountProfile: br.gov.caixa.loterias.feature.account.domain.usecase.GetAccountProfileUseCase
    val prepareMercadoPagoRegistration: br.gov.caixa.loterias.feature.payment.domain.usecase.PrepareMercadoPagoRegistrationUseCase
    val dispatchers: br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
    val localClock: java.time.Clock
    val networkAvailability: br.gov.caixa.loterias.core.common.network.NetworkAvailability
    val mockBackendEnabled: Boolean
}
