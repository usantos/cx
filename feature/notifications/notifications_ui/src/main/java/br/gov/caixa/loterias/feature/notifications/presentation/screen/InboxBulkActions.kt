package br.gov.caixa.loterias.feature.notifications.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSButton
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSButtonVariant
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.notifications.R
import br.gov.caixa.loterias.feature.notifications.presentation.state.CentralNotificationsState
import br.gov.caixa.loterias.feature.notifications.ui.R as DeliveryR

@Composable
internal fun InboxBulkActions(state: CentralNotificationsState, onMarkAll: () -> Unit, onClear: () -> Unit) {
    Column(Modifier.fillMaxWidth().background(LoteriasCaixaDSColorTokens.White).navigationBarsPadding()) {
        Box(Modifier.fillMaxWidth().height(1.dp).background(LoteriasCaixaDSColorTokens.NotificationSearchOutline))
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            LoteriasCaixaDSButton(
                stringResource(R.string.notifications_marcar_todas_como_lidas),
                onMarkAll,
                (Modifier.fillMaxWidth()).uiId("inbox.0023"),
                enabled = state.canMarkAll
            )
            if (!state.unreadOnly) {
                LoteriasCaixaDSButton(
                    stringResource(DeliveryR.string.notifications_excluir_todas_as_notificacoes),
                    onClear,
                    (Modifier.fillMaxWidth()).uiId("inbox.0024"),
                    enabled = state.canDeleteAll,
                    variant = LoteriasCaixaDSButtonVariant.Secondary
                )
            }
        }
    }
}
