package br.gov.caixa.loterias.feature.tickets.navigation

import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.network.NetworkAvailability
import br.gov.caixa.loterias.core.navigation.configuration.HostFeatureConfiguration
import br.gov.caixa.loterias.feature.account.domain.port.AuthenticatedSessionIdentityProvider
import br.gov.caixa.loterias.feature.account.domain.usecase.GetAccountProfileUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.AddBetToCartUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetBettingParametersUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetLotteryModalitiesUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.SaveFavoriteBetUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.ValidateFavoriteBetUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.GetCurrentCartUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.ValidateFavoriteCartNameUseCase
import br.gov.caixa.loterias.feature.payment.domain.usecase.GetPurchaseProgressUseCase
import br.gov.caixa.loterias.feature.payment.domain.usecase.PrepareMercadoPagoRegistrationUseCase
import br.gov.caixa.loterias.feature.payment.domain.usecase.ValidateDamUseCase
import br.gov.caixa.loterias.feature.results.domain.usecase.GetConfirmedBetResultsUseCase
import br.gov.caixa.loterias.feature.retail.domain.usecase.DownloadBetReceiptUseCase
import br.gov.caixa.loterias.feature.retail.domain.usecase.DownloadPrizeReceiptUseCase
import br.gov.caixa.loterias.feature.tickets.data.fixture.mockConfirmedBet
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet
import br.gov.caixa.loterias.feature.tickets.domain.usecase.CheckTicketUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetConfirmedBetsConfigurationUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetConfirmedBetsUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetPurchaseBetsUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetPurchaseDetailsUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetPurchaseMonthsUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetPurchasePaymentMethodsUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetPurchaseStatusesUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetPurchasesUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.LoadConfirmedBetUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.RecoverConfirmedBetUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.SavePurchaseAsFavoriteUseCase
import java.time.Clock
import javax.inject.Inject

class TicketsNavigationDependenciesAdapter @Inject constructor(
    override val checkTicket: CheckTicketUseCase,
    override val currentCart: GetCurrentCartUseCase,
    override val downloadBetReceipt: DownloadBetReceiptUseCase,
    override val downloadPrizeReceipt: DownloadPrizeReceiptUseCase,
    override val validateFavoriteCartName: ValidateFavoriteCartNameUseCase,
    private val hostConfiguration: HostFeatureConfiguration,
    private val ticketsPurchaseDependencies: TicketsPurchaseDependencies,
    private val ticketsBetDependencies: TicketsBetDependencies,
    private val ticketsAccountDependencies: TicketsAccountDependencies,
    private val runtime: TicketsRuntimeDependencies,
) : TicketsNavigationDependencies {
    override val dispatchers: br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers get() = runtime.dispatchers
    override val localClock: Clock get() = runtime.localClock
    override val networkAvailability: NetworkAvailability get() = runtime.networkAvailability

    override val purchaseBets: GetPurchaseBetsUseCase get() = ticketsPurchaseDependencies.purchaseBets
    override val purchaseDetails: GetPurchaseDetailsUseCase get() = ticketsPurchaseDependencies.purchaseDetails
    override val purchaseMonths: GetPurchaseMonthsUseCase get() = ticketsPurchaseDependencies.purchaseMonths
    override val purchasePaymentMethods: GetPurchasePaymentMethodsUseCase get() = ticketsPurchaseDependencies.purchasePaymentMethods
    override val purchaseProgress: GetPurchaseProgressUseCase get() = ticketsPurchaseDependencies.purchaseProgress
    override val purchaseStatuses: GetPurchaseStatusesUseCase get() = ticketsPurchaseDependencies.purchaseStatuses
    override val purchases: GetPurchasesUseCase get() = ticketsPurchaseDependencies.purchases
    override val savePurchaseAsFavorite: SavePurchaseAsFavoriteUseCase get() = ticketsPurchaseDependencies.savePurchaseAsFavorite
    override val validateDam: ValidateDamUseCase get() = ticketsPurchaseDependencies.validateDam
    override val addBetToCart: AddBetToCartUseCase get() = ticketsBetDependencies.addBetToCart
    override val bettingParameters: GetBettingParametersUseCase get() = ticketsBetDependencies.bettingParameters
    override val confirmedBetResults: GetConfirmedBetResultsUseCase get() = ticketsBetDependencies.confirmedBetResults
    override val confirmedBets: GetConfirmedBetsUseCase get() = ticketsBetDependencies.confirmedBets
    override val confirmedBetsConfiguration: GetConfirmedBetsConfigurationUseCase get() = ticketsBetDependencies.confirmedBetsConfiguration
    override val loadConfirmedBet: LoadConfirmedBetUseCase get() = ticketsBetDependencies.loadConfirmedBet
    override val lotteryModalities: GetLotteryModalitiesUseCase get() = ticketsBetDependencies.lotteryModalities
    override val recoverConfirmedBet: RecoverConfirmedBetUseCase get() = ticketsBetDependencies.recoverConfirmedBet
    override val saveFavoriteBet: SaveFavoriteBetUseCase get() = ticketsBetDependencies.saveFavoriteBet
    override val validateFavoriteBet: ValidateFavoriteBetUseCase get() = ticketsBetDependencies.validateFavoriteBet
    override val authenticatedSessionIdentity: AuthenticatedSessionIdentityProvider get() =
        ticketsAccountDependencies.authenticatedSessionIdentity
    override val accountProfile: GetAccountProfileUseCase get() = ticketsAccountDependencies.accountProfile
    override val prepareMercadoPagoRegistration: PrepareMercadoPagoRegistrationUseCase get() =
        ticketsAccountDependencies.prepareMercadoPagoRegistration

    override val mockBackendEnabled: Boolean get() = hostConfiguration.mockBackendEnabled

    override fun visualConfirmedBet(variant: String, originalRecurringBet: Boolean, redeemedPrize: Boolean): ConfirmedBet =
        mockConfirmedBet(
            variant,
            originalRecurringBet,
            redeemedPrize
        )
}

class TicketsPurchaseDependencies @Inject constructor(
    val purchaseBets: GetPurchaseBetsUseCase,
    val purchaseDetails: GetPurchaseDetailsUseCase,
    val purchaseMonths: GetPurchaseMonthsUseCase,
    val purchasePaymentMethods: GetPurchasePaymentMethodsUseCase,
    val purchaseProgress: GetPurchaseProgressUseCase,
    val purchaseStatuses: GetPurchaseStatusesUseCase,
    val purchases: GetPurchasesUseCase,
    val savePurchaseAsFavorite: SavePurchaseAsFavoriteUseCase,
    val validateDam: ValidateDamUseCase,
)

class TicketsBetDependencies @Inject constructor(
    val addBetToCart: AddBetToCartUseCase,
    val bettingParameters: GetBettingParametersUseCase,
    val confirmedBetResults: GetConfirmedBetResultsUseCase,
    val confirmedBets: GetConfirmedBetsUseCase,
    val confirmedBetsConfiguration: GetConfirmedBetsConfigurationUseCase,
    val loadConfirmedBet: LoadConfirmedBetUseCase,
    val lotteryModalities: GetLotteryModalitiesUseCase,
    val recoverConfirmedBet: RecoverConfirmedBetUseCase,
    val saveFavoriteBet: SaveFavoriteBetUseCase,
    val validateFavoriteBet: ValidateFavoriteBetUseCase,
)

class TicketsAccountDependencies @Inject constructor(
    val authenticatedSessionIdentity: AuthenticatedSessionIdentityProvider,
    val accountProfile: GetAccountProfileUseCase,
    val prepareMercadoPagoRegistration: PrepareMercadoPagoRegistrationUseCase,
)

class TicketsRuntimeDependencies @Inject constructor(
    val dispatchers: br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers,
    val localClock: Clock,
    val networkAvailability: NetworkAvailability,
)
