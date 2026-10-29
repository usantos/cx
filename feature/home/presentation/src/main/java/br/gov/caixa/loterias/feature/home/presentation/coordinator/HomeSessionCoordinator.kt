package br.gov.caixa.loterias.feature.home.presentation.coordinator

import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.presentation.state.ReduxStateStore
import br.gov.caixa.loterias.feature.account.domain.usecase.ObserveAuthenticatedSessionContentUseCase
import br.gov.caixa.loterias.feature.home.presentation.state.HomeStateAction
import br.gov.caixa.loterias.feature.home.presentation.state.HomeUiState
import java.util.concurrent.atomic.AtomicLong
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal class HomeSessionCoordinator(
    private val mutableState: ReduxStateStore<HomeUiState, HomeStateAction>,
    private val scope: CoroutineScope,
    private val dispatchers: CoroutineDispatchers,
    private val cartRequestVersion: AtomicLong,
    private val notificationVersion: AtomicLong,
    private val unreadRequestVersion: AtomicLong,
    private val observeSessionContent: ObserveAuthenticatedSessionContentUseCase,
    private val refreshUnreadNotificationCount: () -> Unit,
) {
    fun start() = scope.launch(dispatchers.io) {
        observeSessionContent().collect { content ->
            cartRequestVersion.incrementAndGet()
            notificationVersion.incrementAndGet()
            unreadRequestVersion.incrementAndGet()
            mutableState.dispatch(HomeStateAction.SessionContentObserved(content))
            refreshUnreadNotificationCount()
        }
    }

    fun acknowledgeLocalCartReview() = mutableState.dispatch(HomeStateAction.LocalCartReviewAcknowledged)

    fun acknowledgeUnidentifiedPayment(): Boolean {
        if (!mutableState.value.unidentifiedPayment || mutableState.value.paymentNoticeAcknowledged) return false
        mutableState.dispatch(HomeStateAction.PaymentNoticeAcknowledged)
        return true
    }
}
