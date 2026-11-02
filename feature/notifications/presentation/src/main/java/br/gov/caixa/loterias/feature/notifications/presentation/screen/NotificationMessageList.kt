package br.gov.caixa.loterias.feature.notifications.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.notifications.domain.model.AppNotification
import br.gov.caixa.loterias.feature.notifications.presentation.components.SwipeableNotificationItem
import br.gov.caixa.loterias.feature.notifications.presentation.mapper.notificationLegacyTextStyle
import br.gov.caixa.loterias.feature.notifications.presentation.state.NotificationsUiState

@Composable
internal fun NotificationMessageList(state: NotificationsUiState, actions: NotificationMessageActions) {
    if (!state.loading && state.visibleMessages.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                stringResource(state.emptyMessage),
                color = LoteriasCaixaDS.colors.text,
                style = notificationLegacyTextStyle(16.sp, FontWeight.Bold),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.uiId("inbox.0031")
            )
        }
    } else {
        LazyColumn((Modifier.fillMaxSize().background(LoteriasCaixaDSColorTokens.NotificationList)).uiId("inbox.0032")) {
            items(state.visibleMessages, key = AppNotification::id) { message ->
                SwipeableNotificationItem(
                    message = message,
                    expanded = state.expandedMessageId == message.id,
                    onToggle = { actions.toggleExpanded(message) },
                    onRead = { actions.requestMarkRead(message.id) },
                    onDelete = { actions.requestDelete(message.id) },
                    onShare = { actions.share(message) },
                    onCategoryAction = { actions.performCategoryAction(message) }
                )
            }
        }
    }
}
