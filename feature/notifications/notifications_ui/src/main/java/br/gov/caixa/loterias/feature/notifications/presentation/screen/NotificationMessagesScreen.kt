package br.gov.caixa.loterias.feature.notifications.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSScaffold
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.notifications.R
import br.gov.caixa.loterias.feature.notifications.domain.model.AppNotification
import br.gov.caixa.loterias.feature.notifications.presentation.state.NotificationFilter
import br.gov.caixa.loterias.feature.notifications.presentation.state.NotificationsUiState
import br.gov.caixa.loterias.feature.notifications.ui.R as DeliveryR

@Composable
internal fun DeliveredMessagesScreen(state: NotificationsUiState, actions: NotificationMessageActions, onBack: () -> Unit) {
    LoteriasCaixaDSScaffold(
        title = stringResource(DeliveryR.string.notifications_notificacoes),
        onBack = onBack,
        modifier = Modifier.uiId("inbox.0028")

    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).background(LoteriasCaixaDSColorTokens.NotificationList)) {
            NotificationSearchActions(state.query, actions)

            NotificationMessageFilters(state.filter, actions.selectFilter)

            NotificationMessageList(state, actions)
        }
    }
}
