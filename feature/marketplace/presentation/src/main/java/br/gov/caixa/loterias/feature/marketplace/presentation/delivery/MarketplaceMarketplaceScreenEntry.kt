package br.gov.caixa.loterias.feature.marketplace.presentation.delivery

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.feature.marketplace.presentation.state.MarketplaceUiState
import java.math.BigDecimal

interface MarketplaceMarketplaceScreenEntry {
    @Composable
    fun MarketplaceScreen(
        state: MarketplaceUiState,
        navigation: MarketplaceScreenNavigation,
        actions: MarketplaceScreenActions,
        retailerView: Boolean,
        cartTotal: BigDecimal,
        cartBetCount: Int,
    )
}
