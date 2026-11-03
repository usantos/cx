package br.gov.caixa.loterias.feature.marketplace.ui

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.feature.marketplace.presentation.delivery.MarketplaceMarketplaceScreenEntry
import br.gov.caixa.loterias.feature.marketplace.presentation.delivery.MarketplaceScreenActions
import br.gov.caixa.loterias.feature.marketplace.presentation.delivery.MarketplaceScreenNavigation
import br.gov.caixa.loterias.feature.marketplace.presentation.state.MarketplaceUiState
import java.math.BigDecimal

internal class MarketplaceMarketplaceScreenRenderer : MarketplaceMarketplaceScreenEntry {
    @Composable
    override fun MarketplaceScreen(
        state: MarketplaceUiState,
        navigation: MarketplaceScreenNavigation,
        actions: MarketplaceScreenActions,
        retailerView: Boolean,
        cartTotal: BigDecimal,
        cartBetCount: Int,
    ) {
        br.gov.caixa.loterias.feature.marketplace.presentation.screen.MarketplaceScreenDelivered(
            state,
            navigation,
            actions,
            retailerView,
            cartTotal,
            cartBetCount
        )
    }
}
