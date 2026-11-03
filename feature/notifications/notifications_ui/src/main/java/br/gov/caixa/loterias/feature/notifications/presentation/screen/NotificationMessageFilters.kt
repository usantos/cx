package br.gov.caixa.loterias.feature.notifications.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.selection.selectable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.notifications.R
import br.gov.caixa.loterias.feature.notifications.presentation.mapper.notificationLegacyTextStyle
import br.gov.caixa.loterias.feature.notifications.presentation.state.NotificationFilter
import br.gov.caixa.loterias.feature.notifications.ui.R as DeliveryR

@Composable
internal fun NotificationMessageFilters(filter: NotificationFilter, onSelect: (NotificationFilter) -> Unit) {
    Row(Modifier.fillMaxWidth().height(56.dp).background(LoteriasCaixaDSColorTokens.NotificationSearch)) {
        NotificationTab(
            label = stringResource(DeliveryR.string.notifications_todas),
            selected = filter == NotificationFilter.ALL,
            onClick = { onSelect(NotificationFilter.ALL) },
            modifier = (Modifier.weight(1f)).uiId("inbox.0029")
        )
        NotificationTab(
            label = stringResource(DeliveryR.string.notifications_nao_lidas),
            selected = filter == NotificationFilter.UNREAD,
            onClick = { onSelect(NotificationFilter.UNREAD) },
            modifier = (Modifier.weight(1f)).uiId("inbox.0030")
        )
    }
}

@Composable
internal fun NotificationTab(label: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier.fillMaxHeight().uiId("inbox.0033").selectable(selected = selected, role = Role.Tab, onClick = onClick)
            .background(if (selected) LoteriasCaixaDS.colors.surface else LoteriasCaixaDSColorTokens.NotificationSearch),
        contentAlignment = Alignment.Center
    ) {
        Text(label, color = LoteriasCaixaDS.colors.text, style = notificationLegacyTextStyle(14.sp), modifier = Modifier.uiId("inbox.0034"))
    }
}
