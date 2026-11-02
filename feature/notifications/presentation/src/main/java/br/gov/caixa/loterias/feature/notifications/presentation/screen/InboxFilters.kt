package br.gov.caixa.loterias.feature.notifications.presentation.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.notifications.R
import br.gov.caixa.loterias.feature.notifications.presentation.state.CentralNotificationsState

@Composable
internal fun InboxFilters(state: CentralNotificationsState, onSelectUnread: (Boolean) -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(start = 15.dp, end = 15.dp, top = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        InboxTab(stringResource(R.string.notifications_todas), !state.unreadOnly) { onSelectUnread(false) }
        InboxTab(stringResource(R.string.notifications_nao_lidas_1_d, state.unread.size), state.unreadOnly) { onSelectUnread(true) }
    }
}

@Composable
internal fun InboxTab(label: String, active: Boolean, onClick: () -> Unit) {
    val blue = LoteriasCaixaDSColorTokens.Blue700
    Box(
        Modifier.semantics {
            role = Role.Tab
            selected = active
        }
            .background(if (active) blue else LoteriasCaixaDSColorTokens.White, RoundedCornerShape(8.dp))
            .border(BorderStroke(1.dp, blue), RoundedCornerShape(8.dp))
            .uiId("inbox.0017").clickable(onClick = onClick).padding(horizontal = 15.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            label,
            fontFamily = LoteriasCaixaDSFontFamily,
            fontSize = 16.sp,
            color = if (active) LoteriasCaixaDSColorTokens.White else LoteriasCaixaDSColorTokens.NotificationText,
            modifier = Modifier.uiId("inbox.0018")
        )
    }
}
