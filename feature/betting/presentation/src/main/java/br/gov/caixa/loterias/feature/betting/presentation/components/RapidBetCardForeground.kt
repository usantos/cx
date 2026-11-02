package br.gov.caixa.loterias.feature.betting.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSDropdownMenu as DropdownMenu
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSDropdownMenuItem as DropdownMenuItem
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSLotteryStyle
import br.gov.caixa.loterias.feature.betting.R as LocalizedR
import br.gov.caixa.loterias.feature.cart.domain.model.CartItem

@Composable
internal fun RapidBetCardForeground(
    item: CartItem,
    style: LoteriasCaixaDSLotteryStyle,
    offset: Float,
    contextMenu: Boolean,
    onCloseDetails: () -> Unit,
    onShowMenu: () -> Unit,
    onDismissMenu: () -> Unit,
    onRemove: () -> Unit,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Box(
        Modifier.fillMaxWidth().offset { IntOffset(offset.toInt(), 0) }
            .background(LoteriasCaixaDSColorTokens.White)
            .uiId("bet.0203").combinedClickable(
                onClick = onCloseDetails,
                onLongClick = onShowMenu,
                onLongClickLabel = uiStrings.getString(LocalizedR.string.ui_acoes_da_aposta_134037)
            )
    ) {
        RapidBetSummary(item, style)
        DropdownMenu(expanded = contextMenu, onDismissRequest = onDismissMenu, modifier = Modifier.uiId("bet.0209")) {
            DropdownMenuItem(
                text = {
                    Text(
                        uiStrings.getString(LocalizedR.string.ui_excluir_aposta_86e1ac),
                        fontFamily = LoteriasCaixaDSFontFamily,
                        modifier = Modifier.uiId("bet.0210")
                    )
                },
                onClick = onRemove,
                modifier = Modifier.uiId("bet.0211")
            )
        }
    }
}
