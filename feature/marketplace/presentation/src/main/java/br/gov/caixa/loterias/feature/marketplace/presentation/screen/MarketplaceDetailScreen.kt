package br.gov.caixa.loterias.feature.marketplace.presentation.screen

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.domain.configuration.model.AppModule
import br.gov.caixa.loterias.core.presentation.delivery.FeatureModuleContent
import br.gov.caixa.loterias.feature.marketplace.presentation.delivery.MarketplaceUiEntry
import br.gov.caixa.loterias.feature.marketplace.presentation.state.MarketplaceDetailUiState
import java.math.BigDecimal

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
) {
    FeatureModuleContent(
        module = AppModule.MARKETPLACE,
        entryType = MarketplaceUiEntry::class.java,
        onBack = onBack,
        preserveActive = false
    ) { entry ->
        entry.MarketplaceDetailScreen(
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
