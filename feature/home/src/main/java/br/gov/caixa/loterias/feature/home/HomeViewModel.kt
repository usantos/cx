package br.gov.caixa.loterias.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.gov.caixa.loterias.core.common.AppResult
import br.gov.caixa.loterias.core.common.AppError
import br.gov.caixa.loterias.core.domain.betting.BetEntryDecision
import br.gov.caixa.loterias.core.domain.betting.BetEntryRequest
import br.gov.caixa.loterias.core.domain.betting.PrepareBetEntryUseCase
import br.gov.caixa.loterias.core.domain.betting.PreparePoolEntryUseCase
import br.gov.caixa.loterias.core.domain.betting.PoolEntryDecision
import br.gov.caixa.loterias.core.domain.betting.PoolEntryRequest
import br.gov.caixa.loterias.core.domain.betting.HomeBettingAction
import br.gov.caixa.loterias.core.domain.betting.HomeBettingDecision
import br.gov.caixa.loterias.core.domain.betting.PrepareHomeBettingActionUseCase
import br.gov.caixa.loterias.core.common.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.DefaultCoroutineDispatchers
import br.gov.caixa.loterias.core.domain.betting.BettingParameters
import br.gov.caixa.loterias.core.domain.betting.BetCombo
import br.gov.caixa.loterias.core.domain.betting.GetBetCombosUseCase
import br.gov.caixa.loterias.core.domain.betting.GetBettingParametersUseCase
import br.gov.caixa.loterias.core.domain.cart.GetCurrentCartUseCase
import br.gov.caixa.loterias.core.domain.configuration.GetHomeFeatureConfigurationUseCase
import br.gov.caixa.loterias.core.domain.configuration.HomeFeatureConfiguration
import br.gov.caixa.loterias.core.domain.responsiblegaming.GetSocialTransfersUseCase
import br.gov.caixa.loterias.core.domain.responsiblegaming.SocialTransfers
import java.math.BigDecimal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import br.gov.caixa.loterias.core.domain.account.AccountNotification
import br.gov.caixa.loterias.core.domain.account.ObserveAuthenticatedSessionContentUseCase
import br.gov.caixa.loterias.core.domain.account.ReadLatestNotificationUseCase
import br.gov.caixa.loterias.core.domain.account.NotificationReadResult
import br.gov.caixa.loterias.core.domain.service.*
import br.gov.caixa.loterias.core.presentation.*
import br.gov.caixa.loterias.core.common.NetworkAvailability
import br.gov.caixa.loterias.core.common.ConnectionAvailability
import java.time.Clock
import java.time.LocalDateTime

class HomeNotificationRedirect(val destination: ServiceRedirect)

data class HomeUiState(
    val loading: Boolean = true,
    val parameters: BettingParameters? = null,
    val features: HomeFeatureConfiguration = HomeFeatureConfiguration(),
    val combos: List<BetCombo> = emptyList(),
    val socialTransfers: SocialTransfers? = null,
    val loadingSocialTransfers: Boolean = false,
    val cartTotal: BigDecimal = BigDecimal.ZERO,
    val cartItemCount: Int = 0,
    val loadingCart: Boolean = false,
    val errorMessage: String? = null,
    val parametersUnavailable: Boolean = false,
    val parameterRetryMessage: String? = null,
    val preparingBet: Boolean = false,
    val betEntryDecision: BetEntryDecision? = null,
    val betEntryError: String? = null,
    val poolEntryDecision: PoolEntryDecision? = null,
    val otherBettingDecision: HomeBettingDecision? = null,
    val localCartReviewRequired: Boolean = false,
    val localCartReviewAcknowledged: Boolean = false,
    val unidentifiedPayment: Boolean = false,
    val paymentNoticeAcknowledged: Boolean = false,
    val latestNotification: AccountNotification? = null,
    val notificationDismissed: Boolean = false,
    val notificationReadError: String? = null,
    val notificationNotice: ServiceNoticeUiState? = null,
    val notificationRedirect: HomeNotificationRedirect? = null,
    val unreadNotificationCount: Int = 0,
)

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
    private val getUnreadNotificationCount: br.gov.caixa.loterias.core.domain.account.GetUnreadNotificationCountUseCase? = null,
) : ViewModel() {
    private var parameterRequestRunning = false
    private var socialTransfersRequested = false
    private val cartRequestVersion = java.util.concurrent.atomic.AtomicLong()
    private val notificationVersion = java.util.concurrent.atomic.AtomicLong()
    private val unreadRequestVersion = java.util.concurrent.atomic.AtomicLong()
    private val mutableState = MutableStateFlow(HomeUiState())
    val state = mutableState.asStateFlow()

    init {
        viewModelScope.launch(dispatchers.io) {
            observeSessionContent().collect { content ->
                cartRequestVersion.incrementAndGet()
                notificationVersion.incrementAndGet()
                unreadRequestVersion.incrementAndGet()
                mutableState.update {
                    it.copy(localCartReviewRequired = content?.localCartReviewRequired == true,
                        localCartReviewAcknowledged = if (content?.localCartReviewRequired == true) it.localCartReviewAcknowledged else false,
                        unidentifiedPayment = content?.unidentifiedPayment == true,
                        paymentNoticeAcknowledged = false, latestNotification = content?.latestNotification,
                        notificationDismissed = if (it.latestNotification === content?.latestNotification) it.notificationDismissed else false,
                        notificationReadError = null,
                        notificationNotice = null, notificationRedirect = null,
                        unreadNotificationCount = 0,
                        cartTotal = content?.cart?.total ?: BigDecimal.ZERO,
                        cartItemCount = content?.cart?.betCount ?: 0, loadingCart = false)
                }
                refreshUnreadNotificationCount()
            }
        }
        load()
    }

    fun refreshUnreadNotificationCount() {
        val useCase = getUnreadNotificationCount ?: return
        val request = unreadRequestVersion.incrementAndGet()
        viewModelScope.launch(dispatchers.io) {
            val count = useCase()
            if (unreadRequestVersion.get() == request) mutableState.update { it.copy(unreadNotificationCount = count) }
        }
    }

    fun acknowledgeLocalCartReview() = mutableState.update { it.copy(localCartReviewAcknowledged = true) }

    fun acknowledgeUnidentifiedPayment(): Boolean {
        if (!state.value.unidentifiedPayment || state.value.paymentNoticeAcknowledged) return false
        mutableState.update { it.copy(paymentNoticeAcknowledged = true) }
        return true
    }

    fun closeNotification() {
        val previous = state.value
        val notification = previous.latestNotification ?: return
        if (previous.unidentifiedPayment || previous.notificationDismissed) return
        val version = notificationVersion.get()
        if (!mutableState.compareAndSet(previous, previous.copy(notificationDismissed = true, notificationReadError = null))) return
        viewModelScope.launch(dispatchers.io) {
            val result = readLatestNotification(notification)
            val failure = when (result) {
                is AppResult.Success -> (result.value as? NotificationReadResult.Rejected)?.failure
                is AppResult.Failure -> ServiceFailure.NoticeRequired(ServiceNotice(ServiceNoticeReason.OPERATION_FAILED))
            } ?: return@launch
            val notice = when (failure) {
                is ServiceFailure.ConnectionFailed -> connectionFailureNotice(failure.reason, networkAvailability.current())
                is ServiceFailure.NoticeRequired -> serviceNoticeUi(failure.notice, LocalDateTime.now(localClock))
                is ServiceFailure.RegistrationRequired -> ServiceNoticeUiState(LEGACY_OPERATION_FAILED)
                is ServiceFailure.RedirectRequired -> when (failure.destination) {
                    ServiceRedirect.ACCEPT_TERMS -> ServiceNoticeUiState(LEGACY_UPDATED_TERMS, failure.destination)
                    else -> loginConnectivityNotice(failure.destination, networkAvailability)
                }
                ServiceFailure.Unchanged -> null
            }
            val redirect = (failure as? ServiceFailure.RedirectRequired)?.takeIf { notice == null }
                ?.let { HomeNotificationRedirect(it.destination) }
            mutableState.update {
                if (version != notificationVersion.get() || it.latestNotification !== notification) it
                else it.copy(notificationNotice = notice, notificationRedirect = redirect)
            }
        }
    }

    fun notificationLink(): String? {
        if (state.value.unidentifiedPayment || state.value.notificationDismissed) return null
        val notification = state.value.latestNotification ?: return null
        return notificationBrowserLink(notification.link).also { if (it == null) notificationLinkFailed() }
    }

    fun notificationLinkFailed() = mutableState.update { it.copy(notificationReadError = "Não foi possível abrir o link da notificação.") }
    fun dismissNotificationError() = mutableState.update { it.copy(notificationReadError = null) }

    /** Recognizing an error never reopens the dismissed popup or sends another PUT. */
    fun dismissNotificationNotice(expected: ServiceNoticeUiState) = mutableState.update {
        if (it.notificationNotice !== expected) it else it.copy(notificationNotice = null)
    }

    fun acceptNotificationNotice(expected: ServiceNoticeUiState) {
        val destination = expected.destination
        val offline = destination?.let { loginConnectivityNotice(it, networkAvailability) }
        mutableState.update {
            if (it.notificationNotice !== expected) it else it.copy(notificationNotice = offline,
                notificationRedirect = destination?.takeIf { offline == null }?.let(::HomeNotificationRedirect))
        }
    }

    fun consumeNotificationRedirect(expected: HomeNotificationRedirect): Boolean {
        val previous = state.value
        return previous.notificationRedirect === expected &&
            mutableState.compareAndSet(previous, previous.copy(notificationRedirect = null))
    }

    fun startBet(modalityId: Int, contestType: String, contestNumber: Int) {
        if (entryInProgress()) return
        mutableState.update { it.copy(preparingBet = true, betEntryError = null) }
        viewModelScope.launch(dispatchers.io) {
            when (val result = prepareBetEntry(BetEntryRequest(modalityId, contestType, contestNumber))) {
                is AppResult.Success -> mutableState.update {
                    it.copy(preparingBet = false, betEntryDecision = result.value)
                }
                is AppResult.Failure -> failEntry(result.error)
            }
        }
    }

    fun consumeBetEntryDecision() = mutableState.update { it.copy(betEntryDecision = null) }
    fun dismissBetEntryError() = mutableState.update { it.copy(betEntryError = null) }

    fun startPools(modalityId: Int, contestType: String) {
        if (entryInProgress()) return
        mutableState.update { it.copy(preparingBet = true, betEntryError = null) }
        viewModelScope.launch(dispatchers.io) {
            when (val result = preparePoolEntry(PoolEntryRequest(modalityId, contestType))) {
                is AppResult.Success -> mutableState.update { it.copy(preparingBet = false, poolEntryDecision = result.value) }
                is AppResult.Failure -> failEntry(result.error)
            }
        }
    }

    fun consumePoolEntryDecision() = mutableState.update { it.copy(poolEntryDecision = null) }

    fun startOtherBetting(action: HomeBettingAction) {
        if (entryInProgress()) return
        mutableState.update { it.copy(preparingBet = true, betEntryError = null) }
        viewModelScope.launch(dispatchers.io) {
            when (val result = prepareHomeBettingAction(action)) {
                is AppResult.Success -> mutableState.update { it.copy(preparingBet = false, otherBettingDecision = result.value) }
                is AppResult.Failure -> failEntry(result.error)
            }
        }
    }

    fun consumeOtherBettingDecision() = mutableState.update { it.copy(otherBettingDecision = null) }
    fun externalBettingFailed() = mutableState.update { it.copy(betEntryError = "Não foi possível abrir o navegador. Tente novamente.") }

    private fun entryInProgress(): Boolean = state.value.let {
        it.parametersUnavailable || it.loading || it.preparingBet || it.betEntryDecision != null || it.poolEntryDecision != null || it.otherBettingDecision != null
    }

    private fun failEntry(error: AppError) = mutableState.update {
        it.copy(preparingBet = false, betEntryError = when (error) {
            AppError.Network -> "Não foi possível acessar o serviço. Verifique sua conexão."
            is AppError.Validation -> error.reason.ifBlank { "Não foi possível concluir a operação." }
            AppError.Unexpected -> "Não foi possível concluir a operação. Tente novamente."
        })
    }

    fun dismissParameterRetryMessage() = mutableState.update { it.copy(parameterRetryMessage = null) }
    fun dismissParametersUnavailable() {
        if (!state.value.loading) mutableState.update { it.copy(parametersUnavailable = false, parameterRetryMessage = null) }
    }

    fun load() {
        if (parameterRequestRunning) return
        parameterRequestRunning = true
        val recovering = state.value.parametersUnavailable
        mutableState.update { it.copy(loading = true, errorMessage = null, parameterRetryMessage = null) }
        viewModelScope.launch(dispatchers.io) {
            try {
                when (val result = getBettingParameters()) {
                    is AppResult.Success -> {
                        if (result.value.games.isEmpty()) {
                            mutableState.update {
                                it.copy(parametersUnavailable = true,
                                    errorMessage = "Nenhuma modalidade disponível no momento.",
                                    parameterRetryMessage = if (recovering) PARAMETER_RETRY_FAILURE else null)
                            }
                            return@launch
                        }
                        val features = when (val featureResult = getHomeFeatureConfiguration()) {
                            is AppResult.Success -> featureResult.value
                            is AppResult.Failure -> HomeFeatureConfiguration()
                        }
                        val combos = if (features.comboBettingEnabled) {
                            when (val comboResult = getBetCombos()) {
                                is AppResult.Success -> comboResult.value.filter { combo -> combo.id > 0 }
                                is AppResult.Failure -> emptyList()
                            }
                        } else emptyList()
                        mutableState.update {
                            it.copy(parameters = result.value, features = features, combos = combos,
                                parametersUnavailable = false, errorMessage = null)
                        }
                    }
                    is AppResult.Failure -> mutableState.update {
                        it.copy(parametersUnavailable = true,
                            errorMessage = "Não foi possível carregar as modalidades. Tente novamente.",
                            parameterRetryMessage = if (recovering) PARAMETER_RETRY_FAILURE else null)
                    }
                }
            } finally {
                parameterRequestRunning = false
                mutableState.update { it.copy(loading = false) }
            }
        }
    }

    fun loadSocialTransfers() {
        if (socialTransfersRequested || state.value.socialTransfers != null) return
        socialTransfersRequested = true
        viewModelScope.launch(dispatchers.io) {
            mutableState.update { it.copy(loadingSocialTransfers = true) }
            when (val result = getSocialTransfers()) {
                is AppResult.Success -> mutableState.update {
                    it.copy(loadingSocialTransfers = false, socialTransfers = result.value)
                }
                is AppResult.Failure -> {
                    socialTransfersRequested = false
                    mutableState.update { it.copy(loadingSocialTransfers = false) }
                }
            }
        }
    }

    fun refreshCart() {
        if (state.value.loadingCart) return
        viewModelScope.launch(dispatchers.io) {
            val requestVersion = cartRequestVersion.incrementAndGet()
            mutableState.update { it.copy(loadingCart = true) }
            when (val result = getCurrentCart()) {
                is AppResult.Success -> mutableState.update {
                    if (requestVersion != cartRequestVersion.get()) it else it.copy(
                        loadingCart = false,
                        cartTotal = result.value.total,
                        cartItemCount = result.value.betCount,
                    )
                }
                is AppResult.Failure -> mutableState.update {
                    if (requestVersion != cartRequestVersion.get()) it else it.copy(loadingCart = false)
                }
            }
        }
    }
}

private const val PARAMETER_RETRY_FAILURE = "Não foi possível concluir esta operação. Por favor, tente novamente."
