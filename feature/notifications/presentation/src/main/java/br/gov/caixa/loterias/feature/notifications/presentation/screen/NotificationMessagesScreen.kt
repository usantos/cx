package br.gov.caixa.loterias.feature.notifications.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSScaffold
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSBasicTextField as BasicTextField
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSIconButton as IconButton
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.icon.CaixaDSIcons
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.feature.notifications.R
import br.gov.caixa.loterias.feature.notifications.domain.model.AppNotification
import br.gov.caixa.loterias.feature.notifications.presentation.components.SwipeableNotificationItem
import br.gov.caixa.loterias.feature.notifications.presentation.mapper.notificationLegacyTextStyle
import br.gov.caixa.loterias.feature.notifications.presentation.state.NotificationFilter
import br.gov.caixa.loterias.feature.notifications.presentation.state.NotificationsUiState

@Composable
internal fun MessagesScreen(state: NotificationsUiState, actions: NotificationMessageActions, onBack: () -> Unit) {
    CaixaDSScaffold(
        title = stringResource(R.string.notifications_notificacoes),
        onBack = onBack,
        modifier = Modifier.uiId("inbox.0028")

    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).background(CaixaDSColorTokens.NotificationList)) {
            NotificationSearchActions(state.query, actions)

            Row(Modifier.fillMaxWidth().height(56.dp).background(CaixaDSColorTokens.NotificationSearch)) {
                NotificationTab(
                    label = stringResource(R.string.notifications_todas),
                    selected = state.filter == NotificationFilter.ALL,
                    onClick = { actions.selectFilter(NotificationFilter.ALL) },
                    modifier = (Modifier.weight(1f)).uiId("inbox.0029")
                )
                NotificationTab(
                    label = stringResource(R.string.notifications_nao_lidas),
                    selected = state.filter == NotificationFilter.UNREAD,
                    onClick = { actions.selectFilter(NotificationFilter.UNREAD) },
                    modifier = (Modifier.weight(1f)).uiId("inbox.0030")
                )
            }

            if (!state.loading && state.visibleMessages.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        stringResource(state.emptyMessage),
                        color = CaixaDS.colors.text,
                        style = notificationLegacyTextStyle(16.sp, FontWeight.Bold),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.uiId("inbox.0031")
                    )
                }
            } else {
                LazyColumn((Modifier.fillMaxSize().background(CaixaDSColorTokens.NotificationList)).uiId("inbox.0032")) {
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
    }
}

@Composable
private fun NotificationTab(label: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier.fillMaxHeight().uiId("inbox.0033").selectable(selected = selected, role = Role.Tab, onClick = onClick)
            .background(if (selected) CaixaDS.colors.surface else CaixaDSColorTokens.NotificationSearch),
        contentAlignment = Alignment.Center
    ) {
        Text(label, color = CaixaDS.colors.text, style = notificationLegacyTextStyle(14.sp), modifier = Modifier.uiId("inbox.0034"))
    }
}

internal data class NotificationMessageActions(
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

@Composable
private fun NotificationSearchActions(query: String, actions: NotificationMessageActions) {
    Row(
        Modifier.fillMaxWidth().background(CaixaDSColorTokens.NotificationSearch)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(20.dp))
        NotificationSearchField(query, actions.updateQuery)
        Spacer(Modifier.width(32.dp))
        IconButton(
            onClick = actions.requestMarkAllRead,
            modifier = (Modifier.size(36.dp)).uiId("inbox.0035")
        ) {
            Icon(
                CaixaDSIcons.NotificationRead,
                stringResource(R.string.notifications_marcar_todas_as_mensagens_como_lidas),
                tint = Color.Unspecified,
                modifier = (Modifier.size(36.dp)).uiId("inbox.0036")
            )
        }
        Spacer(Modifier.width(16.dp))
        IconButton(
            onClick = actions.requestDeleteAll,
            modifier = (Modifier.size(36.dp)).uiId("inbox.0037")
        ) {
            Icon(
                CaixaDSIcons.NotificationDelete,
                stringResource(R.string.notifications_excluir_todas_as_mensagens),
                tint = Color.Unspecified,
                modifier = (Modifier.size(36.dp)).uiId("inbox.0038")
            )
        }
        Spacer(Modifier.width(16.dp))
    }
}

@Composable
private fun RowScope.NotificationSearchField(query: String, onQueryChanged: (String) -> Unit) {
    BasicTextField(
        value = query,
        onValueChange = onQueryChanged,
        modifier = (Modifier.weight(1f).height(40.dp)).uiId("inbox.0039"),
        singleLine = true,
        textStyle = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontSize = 14.sp,
            letterSpacing = 0.56.sp,
            color = CaixaDSColorTokens.NotificationText,
            platformStyle = PlatformTextStyle(includeFontPadding = true)
        ),
        decorationBox = { field ->
            Row(
                Modifier.fillMaxSize().background(CaixaDSColorTokens.NotificationSearchField, RoundedCornerShape(12.dp))
                    .border(1.dp, CaixaDSColorTokens.NotificationSearchOutline, RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    CaixaDSIcons.Search,
                    contentDescription = null,
                    tint = CaixaDSColorTokens.NotificationText,
                    modifier = (Modifier.size(20.dp)).uiId("inbox.0040")
                )
                Box(Modifier.padding(start = 8.dp).weight(1f)) {
                    if (query.isBlank()) {
                        Text(
                            stringResource(R.string.notifications_pesquisar),
                            color = CaixaDSColorTokens.NotificationText,
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 14.sp,
                            letterSpacing = 0.56.sp,
                            modifier = Modifier.uiId("inbox.0041")
                        )
                    }
                    field()
                }
            }
        }
    )
}
