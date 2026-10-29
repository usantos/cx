package br.gov.caixa.loterias.feature.marketplace.presentation.coordinator

import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.presentation.state.ReduxStateStore
import br.gov.caixa.loterias.feature.marketplace.presentation.state.MarketplaceStateAction
import br.gov.caixa.loterias.feature.marketplace.presentation.state.MarketplaceUiState
import br.gov.caixa.loterias.feature.retail.domain.usecase.SetRetailerFavoriteUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal class MarketplacePoolFavoriteCoordinator(
    private val mutableState: ReduxStateStore<MarketplaceUiState, MarketplaceStateAction>,
    private val scope: CoroutineScope,
    private val dispatchers: CoroutineDispatchers,
    private val setRetailerFavorite: SetRetailerFavoriteUseCase,
    private val onFavoriteChanged: (Long, Boolean) -> Unit,
) {
    fun requestFavorite(poolCode: String) {
        mutableState.dispatch(MarketplaceStateAction.PoolFavoriteRequested(poolCode))
    }

    fun cancelFavorite() {
        mutableState.dispatch(MarketplaceStateAction.PoolFavoriteCancelled)
    }

    fun confirmFavorite() {
        val state = synchronized(mutableState) {
            val previous = mutableState.value
            mutableState.dispatch(MarketplaceStateAction.PoolFavoriteClaimed)
            previous
        }
        val code = state.pendingFavoritePoolCode ?: return
        val pool = state.pools.firstOrNull { it.code == code } ?: return
        val newFavorite = !pool.favoriteLottery
        scope.launch(dispatchers.io) {
            when (setRetailerFavorite(pool.lotteryId, newFavorite)) {
                is AppResult.Success -> {
                    mutableState.dispatch(MarketplaceStateAction.PoolFavoriteSaved(pool.lotteryId, newFavorite))
                    onFavoriteChanged(pool.lotteryId, newFavorite)
                }

                is AppResult.Failure -> mutableState.dispatch(MarketplaceStateAction.PoolFavoriteFailed(pool.lotteryId))
            }
        }
    }
}
