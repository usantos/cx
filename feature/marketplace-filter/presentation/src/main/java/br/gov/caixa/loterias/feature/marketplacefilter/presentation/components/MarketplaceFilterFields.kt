package br.gov.caixa.loterias.feature.marketplacefilter.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSMarketplaceFilterBetGrid as NumberGrid
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSMarketplaceFilterInput as Input
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSMarketplaceFilterLabel as Label
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSMaterialTheme as MaterialTheme
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.components.caixaDSMarketplaceFilterText as filterText
import br.gov.caixa.loterias.feature.marketplacefilter.R as LocalizedR
import br.gov.caixa.loterias.feature.marketplacefilter.presentation.state.MarketplaceFilterAction
import br.gov.caixa.loterias.feature.marketplacefilter.presentation.state.MarketplaceFilterUiState

@Composable
internal fun MarketplaceFilterValueFields(state: MarketplaceFilterUiState, onAction: (MarketplaceFilterAction) -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    TwoFieldRow(
        leftLabel = uiStrings.getString(LocalizedR.string.ui_valor_minimo_cota_ff3499),
        rightLabel = uiStrings.getString(LocalizedR.string.ui_valor_maximo_cota_9a12ae),
        leftValue = state.minimumValue,
        rightValue = state.maximumValue,
        leftPlaceholder = "0,00",
        rightPlaceholder = "0,00",
        keyboardType = KeyboardType.Decimal,
        currency = true,
        onLeftChange = { onAction(MarketplaceFilterAction.ChangeMinimumValue(it)) },
        onRightChange = { onAction(MarketplaceFilterAction.ChangeMaximumValue(it)) }
    )
    TwoFieldRow(
        leftLabel = uiStrings.getString(LocalizedR.string.ui_quant_de_cotas_minima_c081e3),
        rightLabel = uiStrings.getString(LocalizedR.string.ui_quant_de_cotas_maxima_31401a),
        leftValue = state.minimumShares,
        rightValue = state.maximumShares,
        leftPlaceholder = "2",
        rightPlaceholder = "100",
        keyboardType = KeyboardType.Number,
        leftError = state.minimumSharesError,
        rightError = state.maximumSharesError,
        leftInputInset = 2.dp,
        onLeftChange = { onAction(MarketplaceFilterAction.ChangeMinimumShares(it)) },
        onRightChange = { onAction(MarketplaceFilterAction.ChangeMaximumShares(it)) }
    )
}

@Composable
internal fun MarketplaceFilterBetQuantity(state: MarketplaceFilterUiState, onAction: (MarketplaceFilterAction) -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Label(
        if (state.selectedModality?.baseName.equals("Loteca", ignoreCase = true)) {
            uiStrings.getString(LocalizedR.string.ui_quantidade_de_palpites_ef7895)
        } else {
            uiStrings.getString(LocalizedR.string.ui_quantidade_de_apostas_43c4d2)
        },
        (Modifier.padding(start = 10.dp, top = 10.dp)).uiId("filter.0001")
    )
    NumberGrid(
        options = state.betOptions,
        selected = setOfNotNull(state.selectedBetQuantity),
        modifier = (Modifier.padding(start = 12.dp, end = 15.dp)).uiId("filter.0002")
    ) { onAction(MarketplaceFilterAction.SelectBetQuantity(it)) }
}

@Composable
private fun TwoFieldRow(
    leftLabel: String,
    rightLabel: String,
    leftValue: String,
    rightValue: String,
    leftPlaceholder: String,
    rightPlaceholder: String,
    keyboardType: KeyboardType,
    onLeftChange: (String) -> Unit,
    onRightChange: (String) -> Unit,
    currency: Boolean = false,
    leftError: String? = null,
    rightError: String? = null,
    leftInputInset: Dp = 0.dp,
) {
    Row(Modifier.fillMaxWidth().padding(top = 10.dp)) {
        Column(Modifier.weight(1f).padding(start = 10.dp, end = 15.dp)) {
            Label(leftLabel, modifier = Modifier.uiId("filter.0003"))
            Input(
                leftValue,
                onLeftChange,
                leftPlaceholder,
                keyboardType,
                currency = currency,
                modifier = (Modifier.padding(start = leftInputInset, top = 10.dp)).uiId("filter.0004"),
                accessibilityLabel = leftLabel,
                errorMessage = leftError
            )
            LegacyFieldError(leftError)
        }
        Column(Modifier.weight(1f).padding(end = 20.dp)) {
            Label(rightLabel, modifier = Modifier.uiId("filter.0005"))
            Input(
                rightValue,
                onRightChange,
                rightPlaceholder,
                keyboardType,
                currency = currency,
                modifier = (Modifier.padding(top = 10.dp)).uiId("filter.0006"),
                accessibilityLabel = rightLabel,
                errorMessage = rightError
            )
            LegacyFieldError(rightError)
        }
    }
}

@Composable
private fun LegacyFieldError(message: String?) {
    message?.let {
        Text(
            it,
            modifier = (Modifier.padding(top = 10.dp)).uiId("filter.0007"),
            color = MaterialTheme.colorScheme.error,
            style = filterText(12, FontWeight.Bold)
        )
    }
}
