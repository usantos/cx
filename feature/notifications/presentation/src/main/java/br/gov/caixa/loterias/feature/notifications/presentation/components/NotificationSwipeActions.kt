package br.gov.caixa.loterias.feature.notifications.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.feature.notifications.R
import br.gov.caixa.loterias.feature.notifications.domain.model.AppNotification
import kotlinx.coroutines.launch

@Composable
internal fun SwipeableNotificationItem(
    message: AppNotification,
    expanded: Boolean,
    onToggle: () -> Unit,
    onRead: () -> Unit,
    onDelete: () -> Unit,
    onShare: () -> Unit,
    onCategoryAction: () -> Unit,
) {
    BoxWithConstraints(Modifier.fillMaxWidth()) {
        val itemWidth = maxWidth
        val scrollState = rememberScrollState()
        val scope = rememberCoroutineScope()
        Row(Modifier.uiId("inbox.0011").horizontalScroll(scrollState)) {
            NotificationItem(
                message = message,
                expanded = expanded,
                onToggle = {
                    if (scrollState.value > 0) {
                        scope.launch { scrollState.animateScrollTo(0) }
                    } else {
                        onToggle()
                    }
                },
                onShare = onShare,
                onCategoryAction = onCategoryAction,
                modifier = Modifier.width(itemWidth)
            )
            SwipeAction(
                icon = br.gov.caixa.loterias.core.designsystem.icon.CaixaDSNotificationSwipeRead,
                description = stringResource(R.string.notifications_marcar_mensagem_como_lida),
                background = CaixaDSColorTokens.NotificationReadAction,
                onClick = onRead
            )
            SwipeAction(
                icon = br.gov.caixa.loterias.core.designsystem.icon.CaixaDSNotificationSwipeDelete,
                description = stringResource(R.string.notifications_excluir_mensagem),
                background = CaixaDSColorTokens.NotificationDeleteAction,
                onClick = onDelete
            )
        }
    }
}

@Composable
private fun SwipeAction(icon: ImageVector, description: String, background: Color, onClick: () -> Unit) {
    Box(
        Modifier.width(60.dp).fillMaxHeight().background(background).uiId("inbox.0012").clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            icon,
            description,
            tint = Color.Unspecified,
            modifier = (Modifier.size(icon.defaultWidth, icon.defaultHeight))
                .uiId("inbox.0013")
        )
    }
}
