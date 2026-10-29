package br.gov.caixa.loterias.feature.retail.navigation

import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.network.NetworkAvailability
import br.gov.caixa.loterias.core.navigation.configuration.HostFeatureConfiguration
import br.gov.caixa.loterias.feature.account.domain.port.CurrentUserIdentityProvider
import br.gov.caixa.loterias.feature.account.domain.usecase.GetAccountProfileUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetBettingParametersUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.PrepareBetEntryUseCase
import br.gov.caixa.loterias.feature.payment.domain.usecase.PrepareMercadoPagoRegistrationUseCase
import br.gov.caixa.loterias.feature.retail.domain.usecase.CancelPixRedemptionUseCase
import br.gov.caixa.loterias.feature.retail.domain.usecase.CheckRetailLocationUseCase
import br.gov.caixa.loterias.feature.retail.domain.usecase.ConsultPixRedemptionKeyUseCase
import br.gov.caixa.loterias.feature.retail.domain.usecase.DownloadBetReceiptUseCase
import br.gov.caixa.loterias.feature.retail.domain.usecase.DownloadPrizeReceiptUseCase
import br.gov.caixa.loterias.feature.retail.domain.usecase.GenerateLotteryRedemptionCodeUseCase
import br.gov.caixa.loterias.feature.retail.domain.usecase.GetPrizeDetailsUseCase
import br.gov.caixa.loterias.feature.retail.domain.usecase.RedeemPrizeUseCase
import br.gov.caixa.loterias.feature.retail.domain.usecase.SearchRetailersUseCase
import br.gov.caixa.loterias.feature.retail.domain.usecase.SetRetailerFavoriteUseCase
import java.time.Clock
import javax.inject.Inject

class RetailNavigationDependenciesAdapter @Inject constructor(
    override val checkRetailLocation: CheckRetailLocationUseCase,
    override val bettingParameters: GetBettingParametersUseCase,
    override val downloadBetReceipt: DownloadBetReceiptUseCase,
    override val prepareBetEntry: PrepareBetEntryUseCase,
    override val searchRetailers: SearchRetailersUseCase,
    override val setRetailerFavorite: SetRetailerFavoriteUseCase,
    private val hostConfiguration: HostFeatureConfiguration,
    private val retailRedemptionDependencies: RetailRedemptionDependencies,
    private val retailAccountDependencies: RetailAccountDependencies,
    private val runtime: RetailRuntimeDependencies,
) : RetailNavigationDependencies {
    override val dispatchers: br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers get() = runtime.dispatchers
    override val localClock: Clock get() = runtime.localClock
    override val networkAvailability: NetworkAvailability get() = runtime.networkAvailability

    override val cancelPixRedemption: CancelPixRedemptionUseCase get() = retailRedemptionDependencies.cancelPixRedemption
    override val consultPixRedemptionKey: ConsultPixRedemptionKeyUseCase get() = retailRedemptionDependencies.consultPixRedemptionKey
    override val downloadPrizeReceipt: DownloadPrizeReceiptUseCase get() = retailRedemptionDependencies.downloadPrizeReceipt
    override val lotteryRedemptionCode: GenerateLotteryRedemptionCodeUseCase get() = retailRedemptionDependencies.lotteryRedemptionCode
    override val prizeDetails: GetPrizeDetailsUseCase get() = retailRedemptionDependencies.prizeDetails
    override val redeemPrize: RedeemPrizeUseCase get() = retailRedemptionDependencies.redeemPrize
    override val accountProfile: GetAccountProfileUseCase get() = retailAccountDependencies.accountProfile
    override val prepareMercadoPagoRegistration: PrepareMercadoPagoRegistrationUseCase get() =
        retailAccountDependencies.prepareMercadoPagoRegistration
    override val currentUserIdentity: CurrentUserIdentityProvider get() = retailAccountDependencies.currentUserIdentity

    override val appVersionName: String get() = hostConfiguration.appVersionName
    override val mockBackendEnabled: Boolean get() = hostConfiguration.mockBackendEnabled
}

class RetailRedemptionDependencies @Inject constructor(
    val cancelPixRedemption: CancelPixRedemptionUseCase,
    val consultPixRedemptionKey: ConsultPixRedemptionKeyUseCase,
    val downloadPrizeReceipt: DownloadPrizeReceiptUseCase,
    val lotteryRedemptionCode: GenerateLotteryRedemptionCodeUseCase,
    val prizeDetails: GetPrizeDetailsUseCase,
    val redeemPrize: RedeemPrizeUseCase,
)

class RetailAccountDependencies @Inject constructor(
    val accountProfile: GetAccountProfileUseCase,
    val prepareMercadoPagoRegistration: PrepareMercadoPagoRegistrationUseCase,
    val currentUserIdentity: CurrentUserIdentityProvider,
)

class RetailRuntimeDependencies @Inject constructor(
    val dispatchers: br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers,
    val localClock: Clock,
    val networkAvailability: NetworkAvailability,
)
