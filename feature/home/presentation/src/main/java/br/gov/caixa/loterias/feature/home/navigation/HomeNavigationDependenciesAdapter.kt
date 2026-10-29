package br.gov.caixa.loterias.feature.home.navigation

import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.network.NetworkAvailability
import br.gov.caixa.loterias.core.common.time.ServerTimeSource
import br.gov.caixa.loterias.core.domain.configuration.usecase.GetHomeFeatureConfigurationUseCase
import br.gov.caixa.loterias.core.navigation.configuration.HostFeatureConfiguration
import br.gov.caixa.loterias.feature.account.domain.port.AuthenticatedSessionIdentityProvider
import br.gov.caixa.loterias.feature.account.domain.port.CurrentUserIdentityProvider
import br.gov.caixa.loterias.feature.account.domain.usecase.GetUnreadNotificationCountUseCase
import br.gov.caixa.loterias.feature.account.domain.usecase.ObserveAuthenticatedSessionContentUseCase
import br.gov.caixa.loterias.feature.account.domain.usecase.ReadLatestNotificationUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetBetCombosUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetBettingParametersUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.PrepareBetEntryUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.PrepareHomeBettingActionUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.PreparePoolEntryUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.GetCurrentCartUseCase
import br.gov.caixa.loterias.feature.home.domain.usecase.HomePreferencesUseCases
import br.gov.caixa.loterias.feature.home.domain.usecase.SelectHomeResaleOffersUseCase
import br.gov.caixa.loterias.feature.responsiblegaming.domain.usecase.GetSocialTransfersUseCase
import br.gov.caixa.loterias.feature.results.domain.usecase.GetLatestResultUseCase
import br.gov.caixa.loterias.feature.results.domain.usecase.GetResultByContestUseCase
import java.time.Clock
import javax.inject.Inject

class HomeNavigationDependenciesAdapter @Inject constructor(

    override val selectHomeResaleOffers: SelectHomeResaleOffersUseCase,
    override val homeFeatureConfiguration: GetHomeFeatureConfigurationUseCase,
    override val homePreferences: HomePreferencesUseCases,
    override val latestResult: GetLatestResultUseCase,
    override val resultByContest: GetResultByContestUseCase,
    override val socialTransfers: GetSocialTransfersUseCase,
    private val hostConfiguration: HostFeatureConfiguration,
    private val homeBettingDependencies: HomeBettingDependencies,
    private val homeSessionDependencies: HomeSessionDependencies,
    private val runtime: HomeRuntimeDependencies,
) : HomeNavigationDependencies {
    override val dispatchers: CoroutineDispatchers get() = runtime.dispatchers
    override val localClock: Clock get() = runtime.localClock
    override val networkAvailability: NetworkAvailability get() = runtime.networkAvailability

    override val serverTime: ServerTimeSource get() = homeSessionDependencies.serverTime
    override val betCombos: GetBetCombosUseCase get() = homeBettingDependencies.betCombos
    override val bettingParameters: GetBettingParametersUseCase get() = homeBettingDependencies.bettingParameters
    override val currentCart: GetCurrentCartUseCase get() = homeBettingDependencies.currentCart
    override val prepareBetEntry: PrepareBetEntryUseCase get() = homeBettingDependencies.prepareBetEntry
    override val prepareHomeBettingAction: PrepareHomeBettingActionUseCase get() = homeBettingDependencies.prepareHomeBettingAction
    override val preparePoolEntry: PreparePoolEntryUseCase get() = homeBettingDependencies.preparePoolEntry
    override val authenticatedSessionIdentity: AuthenticatedSessionIdentityProvider get() =
        homeSessionDependencies.authenticatedSessionIdentity
    override val currentUserIdentity: CurrentUserIdentityProvider get() = homeSessionDependencies.currentUserIdentity
    override val observeSessionContent: ObserveAuthenticatedSessionContentUseCase get() = homeSessionDependencies.observeSessionContent
    override val readLatestNotification: ReadLatestNotificationUseCase get() = homeSessionDependencies.readLatestNotification
    override val unreadNotificationCount: GetUnreadNotificationCountUseCase get() = homeSessionDependencies.unreadNotificationCount

    override val homeNavigationEnvironmentLabel: String? get() = hostConfiguration.homeNavigationEnvironmentLabel
}

class HomeBettingDependencies @Inject constructor(
    val betCombos: GetBetCombosUseCase,
    val bettingParameters: GetBettingParametersUseCase,
    val currentCart: GetCurrentCartUseCase,
    val prepareBetEntry: PrepareBetEntryUseCase,
    val prepareHomeBettingAction: PrepareHomeBettingActionUseCase,
    val preparePoolEntry: PreparePoolEntryUseCase,
)

class HomeSessionDependencies @Inject constructor(
    val serverTime: ServerTimeSource,
    val authenticatedSessionIdentity: AuthenticatedSessionIdentityProvider,
    val currentUserIdentity: CurrentUserIdentityProvider,
    val observeSessionContent: ObserveAuthenticatedSessionContentUseCase,
    val readLatestNotification: ReadLatestNotificationUseCase,
    val unreadNotificationCount: GetUnreadNotificationCountUseCase,
)

class HomeRuntimeDependencies @Inject constructor(
    val dispatchers: CoroutineDispatchers,
    val localClock: Clock,
    val networkAvailability: NetworkAvailability,
)
