package br.gov.caixa.loterias.feature.marketplace.presentation.delivery

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.feature.marketplace.presentation.state.MarketplaceDetailUiState
import java.math.BigDecimal

interface MarketplaceMarketplaceDetailScreenEntry {
    @Composable
    fun MarketplaceDetailScreen(
        state: MarketplaceDetailUiState,
        onBack: () -> Unit,
        onRetry: () -> Unit,
        onDecrease: () -> Unit,
        onIncrease: () -> Unit,
        onAddToCart: () -> Unit,
        cartTotal: BigDecimal = BigDecimal.ZERO,
        cartBetCount: Int = 0,
        onCart: (() -> Unit)? = null,
        onFavorite: () -> Unit = {},
        specialName: String? = null,
    )
}
