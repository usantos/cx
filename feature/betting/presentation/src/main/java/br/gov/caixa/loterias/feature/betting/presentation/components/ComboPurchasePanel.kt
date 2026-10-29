package br.gov.caixa.loterias.feature.betting.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.legacyAutomationId
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSComboColors
import br.gov.caixa.loterias.feature.betting.R
import br.gov.caixa.loterias.feature.betting.presentation.mapper.ComboTextSize
import br.gov.caixa.loterias.feature.betting.presentation.mapper.comboText
import br.gov.caixa.loterias.feature.betting.presentation.mapper.formatBetAmount
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingCatalogUiState
import java.math.BigDecimal

@Composable
internal fun ComboPurchasePanel(
    state: BettingCatalogUiState,
    decrease: () -> Unit,
    increase: () -> Unit,
    add: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val resources = LocalResources.current
    Column(
        modifier.background(CaixaDSColorTokens.White).padding(top = 16.dp, bottom = 16.dp).legacyAutomationId("combo-purchase-panel"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            formatBetAmount(state.selectedCombo?.amount?.multiply(BigDecimal(state.comboQuantity))),
            style = comboText(ComboTextSize.AMOUNT, FontWeight.ExtraBold),
            color = CaixaDSColorTokens.Black,
            modifier = Modifier.uiId("bet.0079")
        )
        Text(
            resources.getString(R.string.combo_quantity_label),
            (Modifier.padding(top = 4.dp)).uiId("bet.0080"),
            style = comboText(ComboTextSize.HEADING),
            color = CaixaDSComboColors.QuantityLabel
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            CatalogCounterButton(false, decrease)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    state.comboQuantity.toString(),
                    style = comboText(ComboTextSize.QUANTITY, FontWeight.Bold),
                    color = CaixaDSColorTokens.ComboDark,
                    modifier = Modifier.uiId("bet.0081")
                )
                Image(painterResource(R.drawable.linha_inferior_qtd_apostas_detalhe_combo), null, Modifier.width(31.dp).height(1.dp))
            }
            CatalogCounterButton(true, increase)
        }
        Box(
            Modifier.padding(top = 15.dp).width(201.dp).height(59.dp).legacyAutomationId("combo-add-to-cart")
                .background(CaixaDSColorTokens.ComboDark, RoundedCornerShape(4.dp))
                .uiId("bet.0082").clickable(enabled = !state.loading && state.selectedCombo != null, role = Role.Button, onClick = add),
            contentAlignment = Alignment.Center
        ) {
            Text(
                buildAnnotatedString {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("Adicionar") }
                    append(resources.getString(R.string.ui_ao_carrinho_de_apostas_ef1eb2))
                },
                style = comboText(ComboTextSize.LABEL),
                color = CaixaDSColorTokens.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.uiId("bet.0083")
            )
        }
    }
}
