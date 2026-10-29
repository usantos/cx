package br.gov.caixa.loterias.feature.home.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.coroutines.DefaultCoroutineDispatchers
import br.gov.caixa.loterias.core.common.network.ConnectionAvailability
import br.gov.caixa.loterias.core.common.network.NetworkAvailability
import br.gov.caixa.loterias.core.domain.configuration.usecase.GetHomeFeatureConfigurationUseCase
import br.gov.caixa.loterias.core.presentation.rule.ServiceNoticeUiState
import br.gov.caixa.loterias.core.presentation.state.reduxStateStore
import br.gov.caixa.loterias.feature.account.domain.usecase.ObserveAuthenticatedSessionContentUseCase
import br.gov.caixa.loterias.feature.account.domain.usecase.ReadLatestNotificationUseCase
import br.gov.caixa.loterias.feature.betting.domain.model.HomeBettingAction
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetBetCombosUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetBettingParametersUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.PrepareBetEntryUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.PrepareHomeBettingActionUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.PreparePoolEntryUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.GetCurrentCartUseCase
import br.gov.caixa.loterias.feature.home.presentation.coordinator.HomeCartCoordinator
import br.gov.caixa.loterias.feature.home.presentation.coordinator.HomeEntryCoordinator
import br.gov.caixa.loterias.feature.home.presentation.coordinator.HomeLoadingCoordinator
import br.gov.caixa.loterias.feature.home.presentation.coordinator.HomeNotificationsCoordinator
import br.gov.caixa.loterias.feature.home.presentation.coordinator.HomeSessionCoordinator
import br.gov.caixa.loterias.feature.home.presentation.coordinator.HomeSocialTransfersCoordinator
import br.gov.caixa.loterias.feature.home.presentation.state.HomeNotificationRedirect
import br.gov.caixa.loterias.feature.home.presentation.state.HomeStateAction
import br.gov.caixa.loterias.feature.home.presentation.state.HomeUiState
import br.gov.caixa.loterias.feature.home.presentation.state.reduceHomeState
import br.gov.caixa.loterias.feature.responsiblegaming.domain.usecase.GetSocialTransfersUseCase
import java.time.Clock

class HomeViewModel(
    private val getBettingParameters: GetBettingParametersUseCase,
    private val getHomeFeatureConfiguration: GetHomeFeatureConfigurationUseCase,
    private val getBetCombos: GetBetCombosUseCase,
    private val getSocialTransfers: GetSocialTransfersUseCase,
    private val getCurrentCart: GetCurrentCartUseCase,
    private val prepareBetEntry: PrepareBetEntryUseCase,
    private val preparePoolEntry: PreparePoolEntryUseCase,
    private val prepareHomeBettingAction: PrepareHomeBettingActionUseCase,
    private val observeSessionContent: ObserveAuthenticatedSessionContentUseCase,
    private val readLatestNotification: ReadLatestNotificationUseCase,
    private val dispatchers: CoroutineDispatchers = DefaultCoroutineDispatchers,
    private val networkAvailability: NetworkAvailability = NetworkAvailability { ConnectionAvailability.UNKNOWN },
    private val localClock: Clock = Clock.systemDefaultZone(),
    private val getUnreadNotificationCount: br.gov.caixa.loterias.feature.account.domain.usecase.GetUnreadNotificationCountUseCase? = null,
    private val selectHomeResaleOffers: br.gov.caixa.loterias.feature.home.domain.usecase.SelectHomeResaleOffersUseCase,
    private val getLatestResult: br.gov.caixa.loterias.feature.results.domain.usecase.GetLatestResultUseCase? = null,
) : ViewModel() {
    private val cartRequestVersion = java.util.concurrent.atomic.AtomicLong()
    private val notificationVersion = java.util.concurrent.atomic.AtomicLong()
    private val unreadRequestVersion = java.util.concurrent.atomic.AtomicLong()
    private val mutableState = reduxStateStore<HomeUiState, HomeStateAction>(
        HomeUiState(),
        ::reduceHomeState
    ).also { store ->
        addCloseable(
            java.io.Closeable {
                // O cancelamento da corrotina agenda finally após o ViewModel fechar seus recursos.
                // Publica o mesmo estado final de carregamento antes do fechamento da assinatura Redux.
                try {
                    store.dispatch(HomeStateAction.ParametersLoadingFinished)
                } finally {
                    store.close()
                }
            }
        )
    }
    val state = mutableState.state
    private val results = br.gov.caixa.loterias.feature.home.presentation.coordinator.HomeResultsCoordinator(
        mutableState,
        viewModelScope,
        dispatchers,
        getLatestResult
    )

    val resultActions: br.gov.caixa.loterias.feature.home.presentation.coordinator.HomeResultActions = results

    private val loading = HomeLoadingCoordinator(
        mutableState,
        viewModelScope,
        dispatchers,
        getBettingParameters,
        getHomeFeatureConfiguration,
        getBetCombos,
        selectHomeResaleOffers
    )
    private val notifications = HomeNotificationsCoordinator(
        mutableState, viewModelScope, dispatchers, notificationVersion, unreadRequestVersion,
        readLatestNotification, networkAvailability, localClock, getUnreadNotificationCount
    )
    private val cart = HomeCartCoordinator(mutableState, viewModelScope, dispatchers, cartRequestVersion, getCurrentCart)
    private val socialTransfers = HomeSocialTransfersCoordinator(mutableState, viewModelScope, dispatchers, getSocialTransfers)
    private val entry =
        HomeEntryCoordinator(mutableState, viewModelScope, dispatchers, prepareBetEntry, preparePoolEntry, prepareHomeBettingAction)
    private val coordinator = HomeSessionCoordinator(
        mutableState,
        viewModelScope,
        dispatchers,
        cartRequestVersion,
        notificationVersion,
        unreadRequestVersion,
        observeSessionContent,
        notifications::refreshUnreadNotificationCount
    )

    init {
        coordinator.start()
        loading.load()
    }

    fun refreshUnreadNotificationCount() = notifications.refreshUnreadNotificationCount()

    fun acknowledgeLocalCartReview() = coordinator.acknowledgeLocalCartReview()

    fun acknowledgeUnidentifiedPayment(): Boolean = coordinator.acknowledgeUnidentifiedPayment()

    fun closeNotification() = notifications.closeNotification()

    fun notificationLink(): String? = notifications.notificationLink()

    fun notificationLinkFailed() = notifications.notificationLinkFailed()

    fun dismissNotificationError() = notifications.dismissNotificationError()

    fun dismissNotificationNotice(expected: ServiceNoticeUiState) = notifications.dismissNotificationNotice(expected)

    fun acceptNotificationNotice(expected: ServiceNoticeUiState) = notifications.acceptNotificationNotice(expected)

    fun consumeNotificationRedirect(expected: HomeNotificationRedirect): Boolean = notifications.consumeNotificationRedirect(expected)

    fun startBet(modalityId: Int, contestType: String, contestNumber: Int) = entry.startBet(modalityId, contestType, contestNumber)

    fun consumeBetEntryDecision() = entry.consumeBetEntryDecision()

    fun dismissBetEntryError() = entry.dismissBetEntryError()

    fun startPools(modalityId: Int, contestType: String) = entry.startPools(modalityId, contestType)

    fun consumePoolEntryDecision() = entry.consumePoolEntryDecision()

    fun startOtherBetting(action: HomeBettingAction) = entry.startOtherBetting(action)

    fun consumeOtherBettingDecision() = entry.consumeOtherBettingDecision()

    fun externalBettingFailed() = entry.externalBettingFailed()

    fun dismissParameterRetryMessage() = loading.dismissParameterRetryMessage()

    fun dismissParametersUnavailable() = loading.dismissParametersUnavailable()

    // A entrada da Home na pilha de navegação controla esta trava; assim, atualizações e o retorno de
    // outra tela mantêm o cabeçalho visível. Um novo login cria uma nova entrada.
    fun revealHeaderActions() = mutableState.dispatch(HomeStateAction.HeaderActionsRevealed)

    val visibility: br.gov.caixa.loterias.feature.home.presentation.coordinator.HomeVisibilityActions =
        br.gov.caixa.loterias.feature.home.presentation.coordinator.HomeResaleRefreshCoordinator(viewModelScope, ::load)

    fun load() {
        results.invalidate()
        loading.load(refreshConfiguration = true)
    }

    fun loadSocialTransfers() = socialTransfers.loadSocialTransfers()

    fun refreshCart() = cart.refreshCart()
}
