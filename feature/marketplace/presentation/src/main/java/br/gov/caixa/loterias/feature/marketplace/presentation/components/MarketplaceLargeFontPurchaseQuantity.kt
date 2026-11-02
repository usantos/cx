package br.gov.caixa.loterias.feature.marketplace.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.marketplace.R
import br.gov.caixa.loterias.feature.marketplace.presentation.state.MarketplaceDetailUiState

@Composable
internal fun LargeFontPurchaseQuantity(state: MarketplaceDetailUiState, decrease: () -> Unit, increase: () -> Unit) {
    Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(
            stringResource(R.string.ui_quantidade_de_cotas_ddb3fd),
            (Modifier.weight(1f)).uiId("pool.0096"),
            style = detailText(PURCHASE_LABEL_SIZE),
            color = LoteriasCaixaDSColorTokens.MarketplaceFooterText
        )
        DetailQuantityButton("-", stringResource(R.string.ui_diminuir_cotas_74fb74), state.canDecrease, decrease)
        Box(Modifier.widthIn(min = 36.dp).heightIn(min = 36.dp), Alignment.Center) {
            Text(
                state.selectedShares.toString(),
                (Modifier.width(IntrinsicSize.Min)).uiId("pool.0097"),
                softWrap = false,
                style = detailText(PURCHASE_QUANTITY_SIZE),
                color = LoteriasCaixaDSColorTokens.MarketplaceFooterText
            )
        }
        DetailQuantityButton("+", stringResource(R.string.ui_aumentar_cotas_203e21), state.canIncrease, increase)
    }
}
