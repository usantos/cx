package br.gov.caixa.loterias.feature.home.presentation.state

import br.gov.caixa.loterias.core.presentation.rule.ServiceNoticeUiState
import br.gov.caixa.loterias.feature.account.domain.model.AccountNotification

internal sealed interface HomeNotificationAction : HomeStateAction {
    data class UnreadCountReceived(val count: Int) : HomeNotificationAction
    data class Closed(val expected: AccountNotification) : HomeNotificationAction
    data class ReadRejected(val expected: AccountNotification, val notice: ServiceNoticeUiState?, val redirect: HomeNotificationRedirect?) :
        HomeNotificationAction
    data class LinkFailed(val message: String) : HomeNotificationAction
    data object ErrorDismissed : HomeNotificationAction
    data class NoticeDismissed(val expected: ServiceNoticeUiState) : HomeNotificationAction
    data class NoticeAccepted(
        val expected: ServiceNoticeUiState,
        val offline: ServiceNoticeUiState?,
        val redirect: HomeNotificationRedirect?,
    ) : HomeNotificationAction
    data class RedirectConsumed(val expected: HomeNotificationRedirect) : HomeNotificationAction
}

internal fun reduceHomeNotificationState(state: HomeUiState, action: HomeNotificationAction): HomeUiState = when (action) {
    is HomeNotificationAction.UnreadCountReceived -> state.copy(unreadNotificationCount = action.count)

    is HomeNotificationAction.Closed -> reduceHomeNotificationStateClosed(state, action)

    is HomeNotificationAction.ReadRejected -> if (state.latestNotification !== action.expected) {
        state
    } else {
        state.copy(notificationNotice = action.notice, notificationRedirect = action.redirect)
    }

    is HomeNotificationAction.LinkFailed -> state.copy(notificationReadError = action.message)

    HomeNotificationAction.ErrorDismissed -> state.copy(notificationReadError = null)

    is HomeNotificationAction.NoticeDismissed -> if (state.notificationNotice !==
        action.expected
    ) {
        state
    } else {
        state.copy(notificationNotice = null)
    }

    is HomeNotificationAction.NoticeAccepted -> if (state.notificationNotice !== action.expected) {
        state
    } else {
        state.copy(notificationNotice = action.offline, notificationRedirect = action.redirect)
    }

    is HomeNotificationAction.RedirectConsumed -> if (state.notificationRedirect !==
        action.expected
    ) {
        state
    } else {
        state.copy(notificationRedirect = null)
    }
}

private fun reduceHomeNotificationStateClosed(state: HomeUiState, action: HomeNotificationAction.Closed): HomeUiState =
    if (state.latestNotification !== action.expected || state.notificationDismissed ||
        state.unidentifiedPayment
    ) {
        state
    } else {
        state.copy(notificationDismissed = true, notificationReadError = null)
    }
