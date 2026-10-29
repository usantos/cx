package br.gov.caixa.loterias.feature.marketplace.presentation.coordinator

import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.presentation.state.ReduxStateStore
import br.gov.caixa.loterias.feature.marketplace.presentation.state.MarketplaceStateAction
import br.gov.caixa.loterias.feature.marketplace.presentation.state.MarketplaceUiState
import br.gov.caixa.loterias.feature.retail.domain.usecase.GetFavoriteRetailersUseCase
import br.gov.caixa.loterias.feature.retail.domain.usecase.SetRetailerFavoriteUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch

internal class MarketplaceFavoriteRetailersCoordinator(
    private val mutableState: ReduxStateStore<MarketplaceUiState, MarketplaceStateAction>,
    private val scope: CoroutineScope,
    private val dispatchers: CoroutineDispatchers,
    private val getFavoriteRetailers: GetFavoriteRetailersUseCase,
    private val setRetailerFavorite: SetRetailerFavoriteUseCase,
) {
    private var loadJob: Job? = null
    private var loadGeneration = 0L

    private fun invalidateLoad() = synchronized(mutableState) {
        loadJob?.cancel()
        loadGeneration++
    }

    fun onPoolFavoriteChanged(retailerId: Long, favorite: Boolean) {
        invalidateLoad()
        if (favorite) {
            loadFavoriteRetailers(openAfterLoad = true)
        } else {
            mutableState.dispatch(MarketplaceStateAction.PoolFavoriteRemoved(retailerId))
        }
    }

    fun requestFavoriteRetailerRemoval(retailerId: Long) {
        mutableState.dispatch(MarketplaceStateAction.RetailerRemovalRequested(retailerId))
    }

    fun cancelFavoriteRetailerRemoval() {
        mutableState.dispatch(MarketplaceStateAction.RetailerRemovalCancelled)
    }

    fun confirmFavoriteRetailerRemoval() {
        val snapshot = synchronized(mutableState) {
            val previous = mutableState.value
            mutableState.dispatch(MarketplaceStateAction.RetailerRemovalClaimed)
            previous
        }
        val retailerId = snapshot.pendingFavoriteRetailerRemoval ?: return
        if (retailerId in snapshot.updatingFavoriteLotteryIds) return
        scope.launch(dispatchers.io) {
            when (setRetailerFavorite(retailerId, false)) {
                is AppResult.Success -> {
                    invalidateLoad()
                    mutableState.dispatch(MarketplaceStateAction.RetailerRemoved(retailerId))
                }

                is AppResult.Failure -> mutableState.dispatch(MarketplaceStateAction.RetailerRemovalFailed(retailerId))
            }
        }
    }

    fun loadFavoriteRetailers(openAfterLoad: Boolean = false, synchronizePools: Boolean = false) {
        invalidateLoad()
        val generation = loadGeneration
        mutableState.dispatch(MarketplaceStateAction.FavoriteRetailersLoading(openAfterLoad))
        loadJob = scope.launch(dispatchers.io) {
            val result = getFavoriteRetailers()
            currentCoroutineContext().ensureActive()
            if (generation != loadGeneration) return@launch
            synchronized(mutableState) {
                mutableState.dispatch(
                    MarketplaceStateAction.FavoriteRetailersReceived(generation != loadGeneration, result, synchronizePools, openAfterLoad)
                )
            }
        }
    }
}
