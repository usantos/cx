package br.gov.caixa.loterias.feature.notifications.presentation.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSButton
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSButtonVariant
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSLoading
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSScaffold
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSIconButton as IconButton
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.icon.CaixaDSIcons
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
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
    CaixaDSScaffold(
        title = stringResource(R.string.notifications_central_de_notificacoes),
        onBack = onBack,
        containerColor = CaixaDSColorTokens.White,
        bottomBar = {
            InboxBulkActions(state, onMarkAll, onClear)
        },
        modifier = Modifier.uiId("inbox.0014")
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            Row(
                Modifier.fillMaxWidth().padding(start = 15.dp, end = 15.dp, top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                InboxTab(stringResource(R.string.notifications_todas), !state.unreadOnly) { onSelectUnread(false) }
                InboxTab(stringResource(R.string.notifications_nao_lidas_1_d, state.unread.size), state.unreadOnly) { onSelectUnread(true) }
            }
            when {
                state.loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CaixaDSLoading(
                        modifier = Modifier
                            .uiId("inbox.0015")
                    )
                }

                state.visible.isEmpty() -> InboxEmptyState(state, onRetry)

                else -> LazyColumn(
                    (Modifier.fillMaxSize().padding(horizontal = 15.dp)).uiId("inbox.0016"),
                    contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp)
                ) {
                    items(state.visible, key = InboxNotification::id) { notification ->
                        InboxRow(notification, onRemove)
                    }
                }
            }
        }
    }
}

@Composable
private fun InboxTab(label: String, active: Boolean, onClick: () -> Unit) {
    val blue = CaixaDSColorTokens.Blue700
    Box(
        Modifier.semantics {
            role = Role.Tab
            selected = active
        }
            .background(if (active) blue else CaixaDSColorTokens.White, RoundedCornerShape(8.dp))
            .border(BorderStroke(1.dp, blue), RoundedCornerShape(8.dp))
            .uiId("inbox.0017").clickable(onClick = onClick).padding(horizontal = 15.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            label,
            fontFamily = CaixaDSFontFamily,
            fontSize = 16.sp,
            color = if (active) CaixaDSColorTokens.White else CaixaDSColorTokens.NotificationText,
            modifier = Modifier.uiId("inbox.0018")
        )
    }
}

@Composable
private fun InboxRow(notification: InboxNotification, onRemove: (InboxNotification) -> Unit) {
    Column {
        Row(Modifier.fillMaxWidth().padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.padding(end = 8.dp).size(8.dp).alpha(if (notification.read) 0f else 1f)
                    .background(CaixaDSColorTokens.Blue700, CircleShape)
            )
            Column(Modifier.weight(1f)) {
                Text(
                    notification.title,
                    fontFamily = CaixaDSFontFamily,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = CaixaDSColorTokens.NotificationText,
                    modifier = Modifier.uiId("inbox.0019")
                )
                Text(
                    notification.content,
                    (Modifier.padding(top = 14.dp)).uiId("inbox.0020"),
                    fontFamily = CaixaDSFontFamily,
                    fontSize = 14.sp,
                    color = CaixaDSColorTokens.NotificationText
                )
            }
            IconButton(
                onClick = { onRemove(notification) },
                modifier = (Modifier.align(Alignment.Top).padding(start = 8.dp))
                    .uiId("inbox.0021")
            ) {
                Icon(
                    CaixaDSIcons.Delete,
                    stringResource(R.string.notifications_excluir_notificacao_1_s, notification.title),
                    tint = CaixaDSColorTokens.Blue700,
                    modifier = Modifier.uiId("inbox.0022")
                )
            }
        }
        Box(Modifier.fillMaxWidth().height(1.dp).background(CaixaDSColorTokens.NotificationSearchOutline))
    }
}

@Composable
private fun InboxBulkActions(state: CentralNotificationsState, onMarkAll: () -> Unit, onClear: () -> Unit) {
    Column(Modifier.fillMaxWidth().background(CaixaDSColorTokens.White).navigationBarsPadding()) {
        Box(Modifier.fillMaxWidth().height(1.dp).background(CaixaDSColorTokens.NotificationSearchOutline))
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            CaixaDSButton(
                stringResource(R.string.notifications_marcar_todas_como_lidas),
                onMarkAll,
                (Modifier.fillMaxWidth()).uiId("inbox.0023"),
                enabled = state.canMarkAll
            )
            if (!state.unreadOnly) {
                CaixaDSButton(
                    stringResource(R.string.notifications_excluir_todas_as_notificacoes),
                    onClear,
                    (Modifier.fillMaxWidth()).uiId("inbox.0024"),
                    enabled = state.canDeleteAll,
                    variant = CaixaDSButtonVariant.Secondary
                )
            }
        }
    }
}

@Composable
private fun InboxEmptyState(state: CentralNotificationsState, onRetry: () -> Unit) {
    Column(
        Modifier.fillMaxWidth().padding(top = 156.dp, start = 15.dp, end = 15.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            if (state.unreadOnly) {
                stringResource(
                    R.string.notifications_nao_ha_notificacoes_nao_lidas
                )
            } else {
                stringResource(R.string.notifications_ainda_nao_ha_notificacoes)
            },
            fontFamily = CaixaDSFontFamily,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = CaixaDSColorTokens.NotificationText,
            modifier = Modifier.uiId("inbox.0025")
        )
        Text(
            if (state.isAnonymous) {
                stringResource(
                    R.string.notifications_faca_login_para_visualizar_suas_notificacoes
                )
            } else {
                stringResource(R.string.notifications_fique_de_olho_nas_novidades_e_atualizacoes)
            },
            (Modifier.padding(top = 4.dp)).uiId("inbox.0026"),
            fontFamily = CaixaDSFontFamily,
            fontSize = 16.sp,
            color = CaixaDSColorTokens.NotificationText
        )
        if (state.error != null) {
            CaixaDSButton(
                stringResource(R.string.notifications_tentar_novamente),
                onRetry,
                (Modifier.padding(top = 16.dp)).uiId("inbox.0027"),
                variant = CaixaDSButtonVariant.Secondary
            )
        }
    }
}
