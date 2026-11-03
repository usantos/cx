package br.gov.caixa.loterias.feature.notifications.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSBasicTextField as BasicTextField
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSIconButton as IconButton
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.icon.LoteriasCaixaDSIcons
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.notifications.R
import br.gov.caixa.loterias.feature.notifications.ui.R as DeliveryR

@Composable
internal fun NotificationSearchActions(query: String, actions: NotificationMessageActions) {
    Row(
        Modifier.fillMaxWidth().background(LoteriasCaixaDSColorTokens.NotificationSearch)
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
                LoteriasCaixaDSIcons.NotificationRead,
                stringResource(DeliveryR.string.notifications_marcar_todas_as_mensagens_como_lidas),
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
                LoteriasCaixaDSIcons.NotificationDelete,
                stringResource(DeliveryR.string.notifications_excluir_todas_as_mensagens),
                tint = Color.Unspecified,
                modifier = (Modifier.size(36.dp)).uiId("inbox.0038")
            )
        }
        Spacer(Modifier.width(16.dp))
    }
}

@Composable
internal fun RowScope.NotificationSearchField(query: String, onQueryChanged: (String) -> Unit) {
    BasicTextField(
        value = query,
        onValueChange = onQueryChanged,
        modifier = (Modifier.weight(1f).height(40.dp)).uiId("inbox.0039"),
        singleLine = true,
        textStyle = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontSize = 14.sp,
            letterSpacing = 0.56.sp,
            color = LoteriasCaixaDSColorTokens.NotificationText,
            platformStyle = PlatformTextStyle(includeFontPadding = true)
        ),
        decorationBox = { field ->
            Row(
                Modifier.fillMaxSize().background(LoteriasCaixaDSColorTokens.NotificationSearchField, RoundedCornerShape(12.dp))
                    .border(1.dp, LoteriasCaixaDSColorTokens.NotificationSearchOutline, RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    LoteriasCaixaDSIcons.Search,
                    contentDescription = null,
                    tint = LoteriasCaixaDSColorTokens.NotificationText,
                    modifier = (Modifier.size(20.dp)).uiId("inbox.0040")
                )
                Box(Modifier.padding(start = 8.dp).weight(1f)) {
                    if (query.isBlank()) {
                        Text(
                            stringResource(DeliveryR.string.notifications_pesquisar),
                            color = LoteriasCaixaDSColorTokens.NotificationText,
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
