package br.gov.caixa.loterias.feature.marketplace.presentation.coordinator

import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.presentation.state.ReduxStateStore
import br.gov.caixa.loterias.feature.cart.domain.model.CartItemKind
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplacePoolOrder
import br.gov.caixa.loterias.feature.marketplace.domain.usecase.AddMarketplacePoolToCartUseCase
import br.gov.caixa.loterias.feature.marketplace.presentation.state.MarketplaceStateAction
import br.gov.caixa.loterias.feature.marketplace.presentation.state.MarketplaceUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal class MarketplaceCartCoordinator(
    private val mutableState: ReduxStateStore<MarketplaceUiState, MarketplaceStateAction>,
    private val scope: CoroutineScope,
    private val dispatchers: CoroutineDispatchers,
    private val addMarketplacePoolToCart: AddMarketplacePoolToCartUseCase,
) {
    fun addToCart(poolCode: String) {
        val state = synchronized(mutableState) {
            val previous = mutableState.value
            mutableState.dispatch(MarketplaceStateAction.CartAdditionClaimed(poolCode))
            previous
        }
        if (poolCode in state.addingPoolCodes) return
        val pool = state.pools.firstOrNull { it.code == poolCode } ?: return
        val shares = state.selectedShares[poolCode] ?: 1
        if (shares !in 1..pool.availableShares) return
        scope.launch(dispatchers.io) {
            val order = MarketplacePoolOrder(
                code = pool.code,
                modality = pool.modality,
                contest = pool.contest,
                contestType = pool.contestType,
                drawDate = pool.drawDate,
                availableShares = pool.availableShares,
                requestedShares = shares,
                serviceFeePerShare = pool.serviceFeePerShare,
                firstSharePriceWithFee = pool.lastSharePriceWithFeeValue,
                regularSharePriceWithFee = pool.regularSharePriceWithFee
            )
            when (val outcome = addMarketplacePoolToCart(order)) {
                is AppResult.Success -> {
                    val reservation = outcome.value.items.firstOrNull { item ->
                        item.kind == CartItemKind.POOL_SHARE && item.poolShare?.code == pool.code
                    }?.poolShare
                    mutableState.dispatch(MarketplaceStateAction.CartReservationReceived(reservation, poolCode, shares, pool, order))
                }

                is AppResult.Failure -> mutableState.dispatch(MarketplaceStateAction.CartAdditionFailed(poolCode))
            }
        }
    }

    fun consumeConfirmation() {
        mutableState.dispatch(MarketplaceStateAction.ConfirmationConsumed)
    }
}
