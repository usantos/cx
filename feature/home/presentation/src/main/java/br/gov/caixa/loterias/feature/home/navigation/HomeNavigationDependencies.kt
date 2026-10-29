package br.gov.caixa.loterias.feature.home.navigation

import br.gov.caixa.loterias.feature.account.domain.port.CurrentUserIdentityProvider
import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetBetCombosUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetBettingParametersUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.GetCurrentCartUseCase
import br.gov.caixa.loterias.core.domain.configuration.usecase.GetHomeFeatureConfigurationUseCase
import br.gov.caixa.loterias.feature.responsiblegaming.domain.usecase.GetSocialTransfersUseCase
import br.gov.caixa.loterias.feature.results.domain.usecase.GetLatestResultUseCase
import br.gov.caixa.loterias.feature.results.domain.usecase.GetResultByContestUseCase

/** Dependências deste grafo de destinos, fornecidas pela raiz de composição do aplicativo. */
interface HomeNavigationDependencies {
    val serverTime: br.gov.caixa.loterias.core.common.time.ServerTimeSource
    val selectHomeResaleOffers: br.gov.caixa.loterias.feature.home.domain.usecase.SelectHomeResaleOffersUseCase
    val authenticatedSessionIdentity: br.gov.caixa.loterias.feature.account.domain.port.AuthenticatedSessionIdentityProvider
    val betCombos: GetBetCombosUseCase
    val bettingParameters: GetBettingParametersUseCase
    val currentCart: GetCurrentCartUseCase
    val currentUserIdentity: CurrentUserIdentityProvider
    val dispatchers: CoroutineDispatchers
    val homeFeatureConfiguration: GetHomeFeatureConfigurationUseCase
    val homeNavigationEnvironmentLabel: String?
    val homePreferences: br.gov.caixa.loterias.feature.home.domain.usecase.HomePreferencesUseCases
    val latestResult: GetLatestResultUseCase
    val localClock: java.time.Clock
    val networkAvailability: br.gov.caixa.loterias.core.common.network.NetworkAvailability
    val observeSessionContent: br.gov.caixa.loterias.feature.account.domain.usecase.ObserveAuthenticatedSessionContentUseCase
    val prepareBetEntry: br.gov.caixa.loterias.feature.betting.domain.usecase.PrepareBetEntryUseCase
    val prepareHomeBettingAction: br.gov.caixa.loterias.feature.betting.domain.usecase.PrepareHomeBettingActionUseCase
    val preparePoolEntry: br.gov.caixa.loterias.feature.betting.domain.usecase.PreparePoolEntryUseCase
    val readLatestNotification: br.gov.caixa.loterias.feature.account.domain.usecase.ReadLatestNotificationUseCase
    val resultByContest: GetResultByContestUseCase
    val socialTransfers: GetSocialTransfersUseCase
    val unreadNotificationCount: br.gov.caixa.loterias.feature.account.domain.usecase.GetUnreadNotificationCountUseCase
}
