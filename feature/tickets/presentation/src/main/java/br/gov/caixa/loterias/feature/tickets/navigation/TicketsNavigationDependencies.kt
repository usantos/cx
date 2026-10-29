package br.gov.caixa.loterias.feature.tickets.navigation

import br.gov.caixa.loterias.feature.betting.domain.usecase.AddBetToCartUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetBettingParametersUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetLotteryModalitiesUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.SaveFavoriteBetUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.ValidateFavoriteBetUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.GetCurrentCartUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.ValidateFavoriteCartNameUseCase
import br.gov.caixa.loterias.feature.payment.domain.usecase.GetPurchaseProgressUseCase
import br.gov.caixa.loterias.feature.payment.domain.usecase.ValidateDamUseCase
import br.gov.caixa.loterias.feature.retail.domain.usecase.DownloadBetReceiptUseCase
import br.gov.caixa.loterias.feature.retail.domain.usecase.DownloadPrizeReceiptUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.CheckTicketUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetConfirmedBetsConfigurationUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetConfirmedBetsUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetPurchaseBetsUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetPurchaseDetailsUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetPurchaseMonthsUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetPurchasePaymentMethodsUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetPurchaseStatusesUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetPurchasesUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.RecoverConfirmedBetUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.SavePurchaseAsFavoriteUseCase

/** Dependências deste grafo de destinos, fornecidas pela raiz de composição do aplicativo. */
interface TicketsNavigationDependencies : br.gov.caixa.loterias.feature.payment.navigation.MercadoPagoRegistrationDependencies, br.gov.caixa.loterias.core.navigation.fixture.VisualBetNavigationFixtures {
    val addBetToCart: AddBetToCartUseCase
    val authenticatedSessionIdentity: br.gov.caixa.loterias.feature.account.domain.port.AuthenticatedSessionIdentityProvider
    val bettingParameters: GetBettingParametersUseCase
    val checkTicket: CheckTicketUseCase
    val confirmedBetResults: br.gov.caixa.loterias.feature.results.domain.usecase.GetConfirmedBetResultsUseCase
    val confirmedBets: GetConfirmedBetsUseCase
    val confirmedBetsConfiguration: GetConfirmedBetsConfigurationUseCase
    val currentCart: GetCurrentCartUseCase
    val downloadBetReceipt: DownloadBetReceiptUseCase
    val downloadPrizeReceipt: DownloadPrizeReceiptUseCase
    val loadConfirmedBet: br.gov.caixa.loterias.feature.tickets.domain.usecase.LoadConfirmedBetUseCase
    val lotteryModalities: GetLotteryModalitiesUseCase
    val purchaseBets: GetPurchaseBetsUseCase
    val purchaseDetails: GetPurchaseDetailsUseCase
    val purchaseMonths: GetPurchaseMonthsUseCase
    val purchasePaymentMethods: GetPurchasePaymentMethodsUseCase
    val purchaseProgress: GetPurchaseProgressUseCase
    val purchaseStatuses: GetPurchaseStatusesUseCase
    val purchases: GetPurchasesUseCase
    val recoverConfirmedBet: RecoverConfirmedBetUseCase
    val saveFavoriteBet: SaveFavoriteBetUseCase
    val savePurchaseAsFavorite: SavePurchaseAsFavoriteUseCase
    val validateDam: ValidateDamUseCase
    val validateFavoriteBet: ValidateFavoriteBetUseCase
    val validateFavoriteCartName: ValidateFavoriteCartNameUseCase

}
