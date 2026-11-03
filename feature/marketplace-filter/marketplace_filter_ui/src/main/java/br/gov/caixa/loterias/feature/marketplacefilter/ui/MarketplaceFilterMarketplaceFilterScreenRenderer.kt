package br.gov.caixa.loterias.feature.marketplacefilter.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import br.gov.caixa.loterias.feature.marketplacefilter.presentation.delivery.MarketplaceFilterMarketplaceFilterScreenEntry
import br.gov.caixa.loterias.feature.marketplacefilter.presentation.state.MarketplaceFilterAction
import br.gov.caixa.loterias.feature.marketplacefilter.presentation.state.MarketplaceFilterUiState
import java.math.BigDecimal

internal class MarketplaceFilterMarketplaceFilterScreenRenderer : MarketplaceFilterMarketplaceFilterScreenEntry {
    @Composable
    override fun MarketplaceFilterScreen(
        state: MarketplaceFilterUiState,
        onAction: (MarketplaceFilterAction) -> Unit,
        modifier: Modifier,
        cartTotal: BigDecimal,
        cartBetCount: Int,
        onCart: (() -> Unit)?,
    ) {
        br.gov.caixa.loterias.feature.marketplacefilter.presentation.screen.MarketplaceFilterScreenDelivered(
            state,
            onAction,
            modifier,
            cartTotal,
            cartBetCount,
            onCart
        )
    }
}
