package br.gov.caixa.loterias.feature.marketplacefilter.presentation.components

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSMarketplaceFilterAccordionHeader
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSMarketplaceFilterAccordionKind
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSMarketplaceFilterSelection
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSMarketplaceFilterStyle
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSHorizontalDivider as HorizontalDivider
import br.gov.caixa.loterias.feature.marketplacefilter.presentation.state.MarketplaceFilterAction
import br.gov.caixa.loterias.feature.marketplacefilter.presentation.state.MarketplaceFilterUiState
import br.gov.caixa.loterias.feature.marketplacefilter.ui.R as DeliveryR

@Composable
fun MarketplaceFilterNumberSelectors(
    state: MarketplaceFilterUiState,
    onAction: (MarketplaceFilterAction) -> Unit,
    showWantedFilters: Boolean,
    onOpenNumberDialog: (NumberDialog) -> Unit,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    LegacyDivider()
    LegacyAccordionSelector(
        title = uiStrings.getString(DeliveryR.string.ui_quantidade_de_dezenas_daa269),
        testTag = "number_quantity_selector",
        selected = state.selectedNumberQuantity?.let(::listOf).orEmpty(),
        onOpen = { onOpenNumberDialog(NumberDialog.Quantity) },
        onRemove = { onAction(MarketplaceFilterAction.SelectNumberQuantity(null)) },
        onClear = { onAction(MarketplaceFilterAction.SelectNumberQuantity(null)) },
        clearLabel = uiStrings.getString(DeliveryR.string.ui_excluir_quantidade_a75886)
    )
    LegacyDivider()

    if (showWantedFilters) {
        LegacyAccordionSelector(
            title = uiStrings.getString(DeliveryR.string.ui_numeros_que_quero_b83043),
            testTag = "wanted_numbers_selector",
            selected = state.wantedNumbers,
            onOpen = { onOpenNumberDialog(NumberDialog.Wanted) },
            onRemove = { value ->
                onAction(MarketplaceFilterAction.ChangeWantedNumbers(state.wantedNumbers - value))
            },
            onClear = { onAction(MarketplaceFilterAction.ChangeWantedNumbers(emptyList())) },
            clearLabel = uiStrings.getString(DeliveryR.string.ui_excluir_numeros_4115c4)
        )
        LegacyDivider()
        LegacyAccordionSelector(
            title = uiStrings.getString(DeliveryR.string.ui_numeros_que_nao_quero_417744),
            testTag = "unwanted_numbers_selector",
            selected = state.unwantedNumbers,
            onOpen = { onOpenNumberDialog(NumberDialog.Unwanted) },
            onRemove = { value ->
                onAction(MarketplaceFilterAction.ChangeUnwantedNumbers(state.unwantedNumbers - value))
            },
            onClear = { onAction(MarketplaceFilterAction.ChangeUnwantedNumbers(emptyList())) },
            clearLabel = uiStrings.getString(DeliveryR.string.ui_excluir_numeros_4115c4)
        )
        LegacyDivider()
    }
}

@Composable
private fun LegacyAccordionSelector(
    title: String,
    testTag: String,
    selected: List<Int>,
    onOpen: () -> Unit,
    onRemove: (Int) -> Unit,
    onClear: () -> Unit,
    clearLabel: String,
) {
    LoteriasCaixaDSMarketplaceFilterAccordionHeader(
        title,
        testTag,
        onOpen,
        kind = if (testTag == "number_quantity_selector") {
            LoteriasCaixaDSMarketplaceFilterAccordionKind.QUANTITY
        } else {
            LoteriasCaixaDSMarketplaceFilterAccordionKind.NUMBERS
        }
    )
    if (selected.isNotEmpty()) {
        LoteriasCaixaDSMarketplaceFilterSelection(selected, clearLabel, onRemove, onClear)
    }
}

@Composable
private fun LegacyDivider() {
    HorizontalDivider(
        modifier = (Modifier.padding(start = 10.dp, top = 10.dp, end = 10.dp)).uiId("filter.0023"),
        thickness = 2.dp,
        color = LoteriasCaixaDSMarketplaceFilterStyle.line
    )
}
