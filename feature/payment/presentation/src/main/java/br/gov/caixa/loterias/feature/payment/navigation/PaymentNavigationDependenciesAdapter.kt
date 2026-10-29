package br.gov.caixa.loterias.feature.payment.navigation

import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.network.NetworkAvailability
import br.gov.caixa.loterias.core.navigation.configuration.HostFeatureConfiguration
import br.gov.caixa.loterias.feature.account.domain.port.AuthenticatedSessionIdentityProvider
import br.gov.caixa.loterias.feature.account.domain.usecase.GetAccountProfileUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.GetCurrentCartUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.RecordRecentPurchaseUseCase
import br.gov.caixa.loterias.feature.payment.domain.usecase.DeleteSavedCardUseCase
import br.gov.caixa.loterias.feature.payment.domain.usecase.GeneratePixChargeUseCase
import br.gov.caixa.loterias.feature.payment.domain.usecase.GetPaymentMethodsUseCase
import br.gov.caixa.loterias.feature.payment.domain.usecase.GetPurchaseProgressUseCase
import br.gov.caixa.loterias.feature.payment.domain.usecase.GetSavedCardsUseCase
import br.gov.caixa.loterias.feature.payment.domain.usecase.PrepareMercadoPagoRegistrationUseCase
import br.gov.caixa.loterias.feature.payment.domain.usecase.SubmitCardPaymentUseCase
import br.gov.caixa.loterias.feature.payment.domain.usecase.ValidateDamUseCase
import java.time.Clock
import javax.inject.Inject

class PaymentNavigationDependenciesAdapter @Inject constructor(
    override val authenticatedSessionIdentity: AuthenticatedSessionIdentityProvider,
    override val currentCart: GetCurrentCartUseCase,
    override val generatePixCharge: GeneratePixChargeUseCase,
    override val paymentMethods: GetPaymentMethodsUseCase,
    override val purchaseProgress: GetPurchaseProgressUseCase,
    override val recordRecentPurchase: RecordRecentPurchaseUseCase,
    override val validateDam: ValidateDamUseCase,
    private val hostConfiguration: HostFeatureConfiguration,
    private val paymentCardDependencies: PaymentCardDependencies,
    private val runtime: PaymentRuntimeDependencies,
) : PaymentNavigationDependencies {
    override val dispatchers: br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers get() = runtime.dispatchers
    override val localClock: Clock get() = runtime.localClock
    override val networkAvailability: NetworkAvailability get() = runtime.networkAvailability

    override val deleteSavedCard: DeleteSavedCardUseCase get() = paymentCardDependencies.deleteSavedCard
    override val savedCards: GetSavedCardsUseCase get() = paymentCardDependencies.savedCards
    override val submitCardPayment: SubmitCardPaymentUseCase get() = paymentCardDependencies.submitCardPayment
    override val prepareMercadoPagoRegistration: PrepareMercadoPagoRegistrationUseCase get() =
        paymentCardDependencies.prepareMercadoPagoRegistration
    override val accountProfile: GetAccountProfileUseCase get() = paymentCardDependencies.accountProfile

    override val mockBackendEnabled: Boolean get() = hostConfiguration.mockBackendEnabled
}

class PaymentCardDependencies @Inject constructor(
    val deleteSavedCard: DeleteSavedCardUseCase,
    val savedCards: GetSavedCardsUseCase,
    val submitCardPayment: SubmitCardPaymentUseCase,
    val prepareMercadoPagoRegistration: PrepareMercadoPagoRegistrationUseCase,
    val accountProfile: GetAccountProfileUseCase,
)

class PaymentRuntimeDependencies @Inject constructor(
    val dispatchers: br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers,
    val localClock: Clock,
    val networkAvailability: NetworkAvailability,
)
