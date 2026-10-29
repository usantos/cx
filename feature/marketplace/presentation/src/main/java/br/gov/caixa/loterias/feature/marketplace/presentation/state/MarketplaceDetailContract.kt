package br.gov.caixa.loterias.feature.marketplace.presentation.state

import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplacePoolDetail
import java.math.BigDecimal

data class MarketplaceDetailUiState(
    val detail: MarketplacePoolDetail? = null,
    val selectedShares: Int = 1,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val isAddingToCart: Boolean = false,
    val addToCartError: String? = null,
    val confirmation: MarketplacePoolConfirmation? = null,
    val favoriteLottery: Boolean? = null,
    val pendingFavorite: Boolean? = null,
    val isUpdatingFavorite: Boolean = false,
    val favoriteError: String? = null,
    val soldOutNotice: String? = null,
    val specialName: String? = null,
) {
    val purchaseAmount: BigDecimal get() {
        val current = detail ?: return BigDecimal.ZERO
        if (selectedShares <= 0) return BigDecimal.ZERO
        return current.lastSharePriceWithFeeValue + current.regularSharePriceWithFee.multiply((selectedShares - 1).toBigDecimal())
    }
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

