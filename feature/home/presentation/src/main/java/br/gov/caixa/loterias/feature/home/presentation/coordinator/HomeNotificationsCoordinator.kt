package br.gov.caixa.loterias.feature.home.presentation.coordinator

import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.network.NetworkAvailability
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.domain.service.model.ServiceFailure
import br.gov.caixa.loterias.core.domain.service.model.ServiceNotice
import br.gov.caixa.loterias.core.domain.service.model.ServiceNoticeReason
import br.gov.caixa.loterias.core.domain.service.model.ServiceRedirect
import br.gov.caixa.loterias.core.presentation.formatting.LEGACY_OPERATION_FAILED
import br.gov.caixa.loterias.core.presentation.formatting.LEGACY_UPDATED_TERMS
import br.gov.caixa.loterias.core.presentation.rule.ServiceNoticeUiState
import br.gov.caixa.loterias.core.presentation.rule.connectionFailureNotice
import br.gov.caixa.loterias.core.presentation.rule.loginConnectivityNotice
import br.gov.caixa.loterias.core.presentation.rule.serviceNoticeUi
import br.gov.caixa.loterias.core.presentation.state.ReduxStateStore
import br.gov.caixa.loterias.feature.account.domain.model.NotificationReadResult
import br.gov.caixa.loterias.feature.account.domain.usecase.ReadLatestNotificationUseCase
import br.gov.caixa.loterias.feature.home.presentation.formatting.localizedMessage as messageText
import br.gov.caixa.loterias.feature.home.presentation.mapper.notificationBrowserLink
import br.gov.caixa.loterias.feature.home.presentation.state.HomeNotificationAction
import br.gov.caixa.loterias.feature.home.presentation.state.HomeNotificationRedirect
import br.gov.caixa.loterias.feature.home.presentation.state.HomeStateAction
import br.gov.caixa.loterias.feature.home.presentation.state.HomeUiState
import java.time.Clock
import java.time.LocalDateTime
import java.util.concurrent.atomic.AtomicLong
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal class HomeNotificationsCoordinator(
    private val mutableState: ReduxStateStore<HomeUiState, HomeStateAction>,
    private val scope: CoroutineScope,
    private val dispatchers: CoroutineDispatchers,
    private val notificationVersion: AtomicLong,
    private val unreadRequestVersion: AtomicLong,
    private val readLatestNotification: ReadLatestNotificationUseCase,
    private val networkAvailability: NetworkAvailability,
    private val localClock: Clock,
    private val getUnreadNotificationCount: br.gov.caixa.loterias.feature.account.domain.usecase.GetUnreadNotificationCountUseCase?,
) {
    fun refreshUnreadNotificationCount() {
        val useCase = getUnreadNotificationCount ?: return
        val request = unreadRequestVersion.incrementAndGet()
        scope.launch(dispatchers.io) {
            val count = useCase()
            synchronized(mutableState) {
                if (unreadRequestVersion.get() == request) mutableState.dispatch(HomeNotificationAction.UnreadCountReceived(count))
            }
        }
    }

    fun closeNotification(): Unit = synchronized(mutableState) {
        val previous = mutableState.value
        val notification = previous.latestNotification ?: return
        if (previous.unidentifiedPayment || previous.notificationDismissed) return
        val version = notificationVersion.get()
        mutableState.dispatch(HomeNotificationAction.Closed(notification))
        scope.launch(dispatchers.io) {
            val result = readLatestNotification(notification)
            val failure = when (result) {
                is AppResult.Success -> (result.value as? NotificationReadResult.Rejected)?.failure
                is AppResult.Failure -> ServiceFailure.NoticeRequired(ServiceNotice(ServiceNoticeReason.OPERATION_FAILED))
            } ?: return@launch
            val notice = notificationFailureNotice(failure)
            val redirect = (failure as? ServiceFailure.RedirectRequired)?.takeIf { notice == null }
                ?.let { HomeNotificationRedirect(it.destination) }
            synchronized(mutableState) {
                if (version ==
                    notificationVersion.get()
                ) {
                    mutableState.dispatch(HomeNotificationAction.ReadRejected(notification, notice, redirect))
                }
            }
        }
    }

    fun notificationLink(): String? {
        if (mutableState.value.unidentifiedPayment || mutableState.value.notificationDismissed) return null
        val notification = mutableState.value.latestNotification ?: return null
        return notificationBrowserLink(notification.link).also { if (it == null) notificationLinkFailed() }
    }

    fun notificationLinkFailed() = mutableState.dispatch(
        HomeNotificationAction.LinkFailed(
            messageText("nao_foi_possivel_abrir_o_link_da_af56cc")
        )
    )

    fun dismissNotificationError() = mutableState.dispatch(HomeNotificationAction.ErrorDismissed)

    /** Reconhecer um erro nunca reabre o aviso dispensado nem envia outro PUT. */
    fun dismissNotificationNotice(expected: ServiceNoticeUiState) = mutableState.dispatch(HomeNotificationAction.NoticeDismissed(expected))

    fun acceptNotificationNotice(expected: ServiceNoticeUiState) {
        val destination = expected.destination
        val offline = destination?.let { loginConnectivityNotice(it, networkAvailability) }
        mutableState.dispatch(
            HomeNotificationAction.NoticeAccepted(
                expected,
                offline,
                destination?.takeIf { offline == null }?.let(::HomeNotificationRedirect)
            )
        )
    }

    fun consumeNotificationRedirect(expected: HomeNotificationRedirect): Boolean = synchronized(mutableState) {
        if (mutableState.value.notificationRedirect !== expected) return@synchronized false
        mutableState.dispatch(HomeNotificationAction.RedirectConsumed(expected))
        true
    }
    private fun notificationFailureNotice(failure: ServiceFailure): ServiceNoticeUiState? = when (failure) {
        is ServiceFailure.ConnectionFailed -> connectionFailureNotice(failure.reason, networkAvailability.current())

        is ServiceFailure.NoticeRequired -> serviceNoticeUi(failure.notice, LocalDateTime.now(localClock))

        is ServiceFailure.RegistrationRequired -> ServiceNoticeUiState(LEGACY_OPERATION_FAILED)

        is ServiceFailure.RedirectRequired -> when (failure.destination) {
            ServiceRedirect.ACCEPT_TERMS -> ServiceNoticeUiState(LEGACY_UPDATED_TERMS, failure.destination)
            else -> loginConnectivityNotice(failure.destination, networkAvailability)
        }

        ServiceFailure.Unchanged -> null
    }
}
