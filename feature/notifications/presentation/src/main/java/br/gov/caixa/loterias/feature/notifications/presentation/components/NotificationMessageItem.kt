package br.gov.caixa.loterias.feature.notifications.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSButton as Button
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSButtonDefaults as ButtonDefaults
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSIconButton as IconButton
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.icon.CaixaDSIcons
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFuturaFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.feature.notifications.R
import br.gov.caixa.loterias.feature.notifications.domain.model.AppNotification
import br.gov.caixa.loterias.feature.notifications.domain.model.NotificationCategory
import br.gov.caixa.loterias.feature.notifications.presentation.mapper.canShare
import br.gov.caixa.loterias.feature.notifications.presentation.mapper.notificationIcon
import br.gov.caixa.loterias.feature.notifications.presentation.mapper.notificationLegacyTextStyle
import br.gov.caixa.loterias.feature.notifications.presentation.mapper.primaryAction

@Composable
internal fun NotificationItem(
    message: AppNotification,
    expanded: Boolean,
    onToggle: () -> Unit,
    onShare: () -> Unit,
    onCategoryAction: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val background = if (message.isRead || expanded) {
        CaixaDS.colors.surface
    } else {
        CaixaDSColorTokens.NotificationSearch
    }
    Column(modifier.background(background).padding(8.dp)) {
        NotificationHeader(message, expanded, onToggle)

        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Column(
                Modifier.fillMaxWidth().background(CaixaDS.colors.surface)
                    .padding(bottom = 8.dp)
            ) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                    Text(
                        message.content,
                        modifier = (Modifier.padding(start = 40.dp).width(250.dp).padding(12.dp)).uiId("inbox.0001"),
                        color = CaixaDS.colors.text,
                        style = CaixaDS.typography.bodySmall.copy(fontFamily = CaixaDSFuturaFontFamily),
                        fontWeight = FontWeight.Bold
                    )
                    if (message.category.canShare()) {
                        IconButton(onClick = onShare, modifier = (Modifier.padding(start = 20.dp).size(28.dp)).uiId("inbox.0002")) {
                            Icon(
                                CaixaDSIcons.NotificationShare,
                                stringResource(R.string.notifications_compartilhar_mensagem),
                                tint = Color.Unspecified,
                                modifier = (Modifier.size(28.dp)).uiId("inbox.0003")
                            )
                        }
                    }
                }
                message.category.primaryAction()?.let { action ->
                    Button(
                        onClick = onCategoryAction,
                        modifier = (Modifier.align(Alignment.CenterHorizontally)).uiId("inbox.0004"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (message.category == NotificationCategory.RESULTS_AVAILABLE) {
                                CaixaDSColorTokens.NotificationResultsAction
                            } else {
                                CaixaDS.colors.brand
                            }
                        )
                    ) {
                        Text(
                            stringResource(action),
                            fontFamily = CaixaDSFuturaFontFamily,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.uiId("inbox.0005")
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationHeader(message: AppNotification, expanded: Boolean, onToggle: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().uiId("inbox.0006").clickable(onClick = onToggle).semantics {
            stateDescription = if (expanded) "Expandido" else "Recolhido"
        }.padding(8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            notificationIcon(message.category),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = (Modifier.size(36.dp).align(Alignment.CenterVertically)).uiId("inbox.0007")
        )
        Column(Modifier.weight(1f).padding(horizontal = 16.dp)) {
            Text(
                message.title,
                color = if (message.isRead || expanded) CaixaDSColorTokens.Black else CaixaDSColorTokens.NotificationUnreadTitle,
                style = notificationLegacyTextStyle(16.sp, FontWeight.Bold),
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Clip,
                modifier = Modifier.uiId("inbox.0008")
            )
            // bind() do holder legado mantém a prévia enquanto o corpo completo está expandido.
            Text(
                message.content,
                color = CaixaDSColorTokens.NotificationText,
                style = notificationLegacyTextStyle(16.sp),
                maxLines = 1,
                overflow = TextOverflow.Clip,
                modifier = Modifier.uiId("inbox.0009")
            )
        }
        Text(
            message.dateLabel,
            color = CaixaDSColorTokens.NotificationText,
            style = notificationLegacyTextStyle(14.sp),
            modifier = Modifier.uiId("inbox.0010")
        )
    }
}
