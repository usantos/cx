package br.gov.caixa.loterias.feature.marketplacefilter.presentation.delivery

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import br.gov.caixa.loterias.feature.marketplacefilter.presentation.state.MarketplaceFilterAction
import br.gov.caixa.loterias.feature.marketplacefilter.presentation.state.MarketplaceFilterUiState
import java.math.BigDecimal

interface MarketplaceFilterMarketplaceFilterScreenEntry {
    @Composable
    fun MarketplaceFilterScreen(
        state: MarketplaceFilterUiState,
        onAction: (MarketplaceFilterAction) -> Unit,
        modifier: Modifier = Modifier,
        cartTotal: BigDecimal = BigDecimal.ZERO,
        cartBetCount: Int = 0,
        onCart: (() -> Unit)? = null,
    )
}
