package br.gov.caixa.loterias.feature.notifications.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSButton
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSButtonVariant
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSLoading
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSIconButton as IconButton
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.icon.LoteriasCaixaDSIcons
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.notifications.R
import br.gov.caixa.loterias.feature.notifications.domain.model.InboxNotification
import br.gov.caixa.loterias.feature.notifications.presentation.state.CentralNotificationsState

@Composable
internal fun InboxMessages(state: CentralNotificationsState, onRemove: (InboxNotification) -> Unit, onRetry: () -> Unit) {
    when {
        state.loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            LoteriasCaixaDSLoading(
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

@Composable
internal fun InboxRow(notification: InboxNotification, onRemove: (InboxNotification) -> Unit) {
    Column {
        Row(Modifier.fillMaxWidth().padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.padding(end = 8.dp).size(8.dp).alpha(if (notification.read) 0f else 1f)
                    .background(LoteriasCaixaDSColorTokens.Blue700, CircleShape)
            )
            Column(Modifier.weight(1f)) {
                Text(
                    notification.title,
                    fontFamily = LoteriasCaixaDSFontFamily,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = LoteriasCaixaDSColorTokens.NotificationText,
                    modifier = Modifier.uiId("inbox.0019")
                )
                Text(
                    notification.content,
                    (Modifier.padding(top = 14.dp)).uiId("inbox.0020"),
                    fontFamily = LoteriasCaixaDSFontFamily,
                    fontSize = 14.sp,
                    color = LoteriasCaixaDSColorTokens.NotificationText
                )
            }
            IconButton(
                onClick = { onRemove(notification) },
                modifier = (Modifier.align(Alignment.Top).padding(start = 8.dp))
                    .uiId("inbox.0021")
            ) {
                Icon(
                    LoteriasCaixaDSIcons.Delete,
                    stringResource(R.string.notifications_excluir_notificacao_1_s, notification.title),
                    tint = LoteriasCaixaDSColorTokens.Blue700,
                    modifier = Modifier.uiId("inbox.0022")
                )
            }
        }
        Box(Modifier.fillMaxWidth().height(1.dp).background(LoteriasCaixaDSColorTokens.NotificationSearchOutline))
    }
}

@Composable
internal fun InboxEmptyState(state: CentralNotificationsState, onRetry: () -> Unit) {
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
            fontFamily = LoteriasCaixaDSFontFamily,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = LoteriasCaixaDSColorTokens.NotificationText,
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
            fontFamily = LoteriasCaixaDSFontFamily,
            fontSize = 16.sp,
            color = LoteriasCaixaDSColorTokens.NotificationText
        )
        if (state.error != null) {
            LoteriasCaixaDSButton(
                stringResource(R.string.notifications_tentar_novamente),
                onRetry,
                (Modifier.padding(top = 16.dp)).uiId("inbox.0027"),
                variant = LoteriasCaixaDSButtonVariant.Secondary
            )
        }
    }
}
