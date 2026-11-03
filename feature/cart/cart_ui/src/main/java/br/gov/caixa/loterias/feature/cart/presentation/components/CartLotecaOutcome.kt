package br.gov.caixa.loterias.feature.cart.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.components.dialog.LoteriasCaixaDSDialog
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.cart.presentation.mapper.cartText
import br.gov.caixa.loterias.feature.cart.ui.R as DeliveryR

@Composable
internal fun CartLotecaOutcome(
    label: String,
    chosen: Boolean,
    description: String,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
) {
    Row(
        modifier.heightIn(
            min = if (compact) LOTECA_TEAM_CELL_HEIGHT.dp else 0.dp
        ).padding(vertical = 4.dp).semantics(mergeDescendants = true) {
            contentDescription = description
            selected = chosen
        },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Box(Modifier.size(16.dp).border(1.dp, LoteriasCaixaDSColorTokens.SecondaryText, CircleShape), contentAlignment = Alignment.Center) {
            if (chosen) Box(Modifier.size(10.dp).background(LoteriasCaixaDSColorTokens.Blue700, CircleShape))
        }
        Text(
            label,
            modifier = (if (compact) Modifier.weight(1f) else Modifier).uiId("cart.0047"),
            color = LoteriasCaixaDSColorTokens.SecondaryText,
            maxLines = if (compact) 1 else Int.MAX_VALUE,
            softWrap = !compact,
            overflow = if (compact) TextOverflow.Ellipsis else TextOverflow.Clip,
            autoSize = if (compact) {
                TextAutoSize.StepBased(
                    minFontSize = LOTECA_TEAM_MIN_TEXT_SIZE.sp,
                    maxFontSize = LOTECA_TEAM_TEXT_SIZE.sp
                )
            } else {
                null
            },
            style = cartText(if (compact) LOTECA_TEAM_TEXT_SIZE else LOTECA_TEXT_SIZE, if (chosen) FontWeight.Bold else FontWeight.Normal)
        )
    }
}

private const val LOTECA_TEXT_SIZE = 14

private const val LOTECA_TEAM_TEXT_SIZE = 12
private const val LOTECA_TEAM_MIN_TEXT_SIZE = 10
private const val LOTECA_TEAM_CELL_HEIGHT = 28

@Composable
internal fun CartLotecaLegend(onDismiss: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    LoteriasCaixaDSDialog(
        title = uiStrings.getString(DeliveryR.string.ui_legenda_61154f),
        message = "",
        positiveLabel = uiStrings.getString(DeliveryR.string.ui_fechar_0f2bd8),
        onPositive = onDismiss,
        onDismiss = onDismiss,
        extraContent = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                CartLotecaOutcome(
                    uiStrings.getString(DeliveryR.string.ui_palpite_escolhido_fea20f),
                    true,
                    uiStrings.getString(DeliveryR.string.ui_palpite_escolhido_fea20f),
                    modifier = Modifier.uiId("cart.0045")
                )
                CartLotecaOutcome(
                    uiStrings.getString(DeliveryR.string.ui_palpite_nao_escolhido_9dfb91),
                    false,
                    uiStrings.getString(DeliveryR.string.ui_palpite_nao_escolhido_9dfb91),
                    modifier = Modifier.uiId("cart.0046")
                )
            }
        }
    )
}
