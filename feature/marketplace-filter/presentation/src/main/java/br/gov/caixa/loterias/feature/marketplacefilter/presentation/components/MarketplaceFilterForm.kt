package br.gov.caixa.loterias.feature.marketplacefilter.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.legacyAutomationId
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSMarketplaceFilterButton as ActionButton
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSMarketplaceFilterLabel as Label
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSMarketplaceFilterStyle
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSScaffold
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSMaterialTheme as MaterialTheme
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.components.cart.CaixaDSCartSummary
import br.gov.caixa.loterias.feature.marketplacefilter.R as LocalizedR
import br.gov.caixa.loterias.feature.marketplacefilter.presentation.mapper.allModalities
import br.gov.caixa.loterias.feature.marketplacefilter.presentation.screen.MarketplaceFilterScreen
import br.gov.caixa.loterias.feature.marketplacefilter.presentation.state.MarketplaceFilterAction
import br.gov.caixa.loterias.feature.marketplacefilter.presentation.state.MarketplaceFilterModalityUi
import br.gov.caixa.loterias.feature.marketplacefilter.presentation.state.MarketplaceFilterUiState
import java.math.BigDecimal

@Composable
internal fun MarketplaceFilterForm(
    state: MarketplaceFilterUiState,
    onAction: (MarketplaceFilterAction) -> Unit,
    modifier: Modifier,
    cartTotal: BigDecimal,
    cartBetCount: Int,
    onCart: (() -> Unit)?,
    showWantedFilters: Boolean,
    onOpenNumberDialog: (NumberDialog) -> Unit,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    CaixaDSScaffold(
        title = uiStrings.getString(LocalizedR.string.ui_filtros_57ac13),
        onBack = { onAction(MarketplaceFilterAction.Back) },
        modifier = (modifier.fillMaxSize().legacyAutomationId("marketplace_filter_screen")).uiId("filter.0008"),
        gradientTopBar = false,
        containerColor = CaixaDSMarketplaceFilterStyle.canvas,
        bottomBar = { CaixaDSCartSummary(cartTotal, cartBetCount, onClick = onCart, modifier = Modifier.uiId("filter.0009")) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).uiId("filter.0010").verticalScroll(rememberScrollState())) {
            Label(
                text = uiStrings.getString(LocalizedR.string.ui_filtrar_06b52c),
                modifier = (Modifier.padding(start = 10.dp, top = 10.dp)).uiId("filter.0011"),
                color = CaixaDSMarketplaceFilterStyle.heading,
                fontSize = 14
            )
            MarketplaceFilterModalities(state, onAction)

            if (state.lotteryEnabled) {
                MarketplaceRetailerSearch(state, onAction)
            }

            MarketplaceFilterValueFields(state, onAction)

            MarketplaceFilterBetQuantity(state, onAction)

            MarketplaceFilterNumberSelectors(state, onAction, showWantedFilters, onOpenNumberDialog)

            (state.configurationError ?: state.validationMessage)?.let {
                Text(
                    it,
                    modifier = (Modifier.padding(horizontal = 10.dp, vertical = 8.dp)).uiId("filter.0012"),
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 20.dp, bottom = CaixaDSMarketplaceFilterStyle.bottomInset),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ActionButton(
                    text = uiStrings.getString(LocalizedR.string.ui_limpar_filtros_60a791),
                    outlined = true,
                    onClick = { onAction(MarketplaceFilterAction.Clear) },
                    modifier = (Modifier.padding(start = 20.dp, end = 16.dp).weight(1f)).uiId("filter.0013")
                )
                ActionButton(
                    text = uiStrings.getString(LocalizedR.string.ui_aplicar_3a0489),
                    outlined = false,
                    enabled = state.canApply,
                    onClick = { onAction(MarketplaceFilterAction.Apply) },
                    modifier = (Modifier.padding(start = 16.dp, end = 20.dp).weight(1f)).uiId("filter.0014")
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MarketplaceFilterPreview() {
    val mega = MarketplaceFilterModalityUi(
        id = 2,
        name = "Mega-Sena",
        contestType = 1,
        numberOptions = (6..20).toList(),
        maximumNumber = 60
    )
    MarketplaceFilterScreen(
        state = MarketplaceFilterUiState(
            modalities = listOf(allModalities, mega),
            selectedModality = mega,
            betOptions = (3..8).toList(),
            numberOptions = (6..20).toList(),
            selectedBetQuantity = 4,
            selectedNumberQuantity = 7,
            wantedNumbers = listOf(7, 12)
        ),
        onAction = {},
        modifier = Modifier.uiId("filter.0015")
    )
}
