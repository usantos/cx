package br.gov.caixa.loterias.feature.marketplace.ui

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.feature.marketplace.presentation.delivery.MarketplaceMarketplaceDetailScreenEntry
import br.gov.caixa.loterias.feature.marketplace.presentation.state.MarketplaceDetailUiState
import java.math.BigDecimal

internal class MarketplaceMarketplaceDetailScreenRenderer : MarketplaceMarketplaceDetailScreenEntry {
    @Composable
    override fun MarketplaceDetailScreen(
        state: MarketplaceDetailUiState,
        onBack: () -> Unit,
        onRetry: () -> Unit,
        onDecrease: () -> Unit,
        onIncrease: () -> Unit,
        onAddToCart: () -> Unit,
        cartTotal: BigDecimal,
        cartBetCount: Int,
        onCart: (() -> Unit)?,
        onFavorite: () -> Unit,
        specialName: String?,
    ) {
        br.gov.caixa.loterias.feature.marketplace.presentation.screen.MarketplaceDetailScreenDelivered(
            state,
            onBack,
            onRetry,
            onDecrease,
            onIncrease,
            onAddToCart,
            cartTotal,
            cartBetCount,
            onCart,
            onFavorite,
            specialName
        )
    }
}
