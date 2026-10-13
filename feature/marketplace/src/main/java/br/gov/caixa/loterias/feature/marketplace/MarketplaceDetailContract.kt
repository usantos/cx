package br.gov.caixa.loterias.feature.marketplace

import br.gov.caixa.loterias.core.domain.marketplace.MarketplacePoolDetail
import java.math.BigDecimal

data class MarketplaceDetailUiState(
    val detail: MarketplacePoolDetail? = null,
    val selectedShares: Int = 1,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val isAddingToCart: Boolean = false,
    val addToCartError: String? = null,
    val confirmation: MarketplacePoolConfirmation? = null,
) {
    val canDecrease: Boolean get() = !isAddingToCart && selectedShares > 1
    val canIncrease: Boolean get() = !isAddingToCart && selectedShares < (detail?.availableShares ?: 0)
}

data class MarketplacePoolConfirmation(
    val poolCode: String,
    val modality: String,
    val expiresAt: String,
    val contest: Int = 0,
    val drawDate: String = "",
    val amount: BigDecimal = BigDecimal.ZERO,
)

sealed interface MarketplaceDetailAction {
    data object Retry : MarketplaceDetailAction
    data object IncreaseShares : MarketplaceDetailAction
    data object DecreaseShares : MarketplaceDetailAction
    data object AddToCart : MarketplaceDetailAction
}
