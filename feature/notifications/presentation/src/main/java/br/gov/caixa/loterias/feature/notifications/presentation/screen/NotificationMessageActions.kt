package br.gov.caixa.loterias.feature.notifications.presentation.screen

import br.gov.caixa.loterias.feature.notifications.domain.model.AppNotification
import br.gov.caixa.loterias.feature.notifications.presentation.state.NotificationFilter

data class NotificationMessageActions(
    val updateQuery: (String) -> Unit,
    val requestMarkAllRead: () -> Unit,
    val requestDeleteAll: () -> Unit,
    val selectFilter: (NotificationFilter) -> Unit,
    val toggleExpanded: (AppNotification) -> Unit,
    val requestMarkRead: (Long) -> Unit,
    val requestDelete: (Long) -> Unit,
    val share: (AppNotification) -> Unit,
    val performCategoryAction: (AppNotification) -> Unit,
)
