package br.gov.caixa.loterias.feature.notifications.presentation.screen

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
import br.gov.caixa.loterias.feature.notifications.domain.model.InboxNotification
import br.gov.caixa.loterias.feature.notifications.presentation.state.CentralNotificationsState

@Composable
internal fun CentralNotificationsScreen(
    state: CentralNotificationsState,
    onBack: () -> Unit,
    onSelectUnread: (Boolean) -> Unit,
    onMarkAll: () -> Unit,
    onClear: () -> Unit,
    onRemove: (InboxNotification) -> Unit,
    onRetry: () -> Unit,
) {
    LoteriasCaixaDSScaffold(
        title = stringResource(R.string.notifications_central_de_notificacoes),
        onBack = onBack,
        containerColor = LoteriasCaixaDSColorTokens.White,
        bottomBar = {
            InboxBulkActions(state, onMarkAll, onClear)
        },
        modifier = Modifier.uiId("inbox.0014")
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            InboxFilters(state, onSelectUnread)
            InboxMessages(state, onRemove, onRetry)
        }
    }
}
