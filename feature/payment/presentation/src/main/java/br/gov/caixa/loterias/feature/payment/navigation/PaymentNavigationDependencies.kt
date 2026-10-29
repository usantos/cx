package br.gov.caixa.loterias.feature.payment.navigation

import br.gov.caixa.loterias.feature.cart.domain.usecase.GetCurrentCartUseCase
import br.gov.caixa.loterias.feature.payment.domain.usecase.DeleteSavedCardUseCase
import br.gov.caixa.loterias.feature.payment.domain.usecase.GeneratePixChargeUseCase
import br.gov.caixa.loterias.feature.payment.domain.usecase.GetPaymentMethodsUseCase
import br.gov.caixa.loterias.feature.payment.domain.usecase.GetPurchaseProgressUseCase
import br.gov.caixa.loterias.feature.payment.domain.usecase.GetSavedCardsUseCase
import br.gov.caixa.loterias.feature.payment.domain.usecase.SubmitCardPaymentUseCase
import br.gov.caixa.loterias.feature.payment.domain.usecase.ValidateDamUseCase

/** Dependências deste grafo de destinos, fornecidas pela raiz de composição do aplicativo. */
interface PaymentNavigationDependencies : br.gov.caixa.loterias.feature.payment.navigation.MercadoPagoRegistrationDependencies {
    val authenticatedSessionIdentity: br.gov.caixa.loterias.feature.account.domain.port.AuthenticatedSessionIdentityProvider
    val currentCart: GetCurrentCartUseCase
    val deleteSavedCard: DeleteSavedCardUseCase
    val generatePixCharge: GeneratePixChargeUseCase
    val paymentMethods: GetPaymentMethodsUseCase
    val purchaseProgress: GetPurchaseProgressUseCase
    val recordRecentPurchase: br.gov.caixa.loterias.feature.cart.domain.usecase.RecordRecentPurchaseUseCase
    val savedCards: GetSavedCardsUseCase
    val submitCardPayment: SubmitCardPaymentUseCase
    val validateDam: ValidateDamUseCase
}
