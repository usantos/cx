package br.gov.caixa.loterias.feature.cart.presentation.coordinator

import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.presentation.state.ReduxStateStore
import br.gov.caixa.loterias.feature.cart.domain.model.FavoriteCart
import br.gov.caixa.loterias.feature.cart.domain.model.FavoriteCartBet
import br.gov.caixa.loterias.feature.cart.domain.usecase.GetFavoriteCartBetsUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.GetFavoriteCartsUseCase
import br.gov.caixa.loterias.feature.cart.presentation.state.FavoriteCartsAction
import br.gov.caixa.loterias.feature.cart.presentation.state.FavoriteCartsUiState
import kotlinx.coroutines.launch

internal class FavoriteCartsLoading(
    private val mutableState: ReduxStateStore<FavoriteCartsUiState, FavoriteCartsAction>,
    private val viewModelScope: kotlinx.coroutines.CoroutineScope,
    private val dispatchers: CoroutineDispatchers,
    private val cartId: Long?,
    private val failures: FavoriteCartsFailures,
    private val getFavoriteCarts: GetFavoriteCartsUseCase,
    private val getFavoriteCartBets: GetFavoriteCartBetsUseCase,
    private val isSessionCurrent: () -> Boolean,
    private val betsCache: MutableMap<Long, List<FavoriteCartBet>>,
) {
    private var expansionRequest: Any? = null

    fun load() = viewModelScope.launch(dispatchers.io) {
        if (!isSessionCurrent()) return@launch
        mutableState.dispatch(FavoriteCartsAction.LoadStarted)
        if (cartId == null) applyCarts(getFavoriteCarts()) else applyBets(getFavoriteCartBets(cartId))
    }

    fun refreshAfterDetail() {
        synchronized(mutableState) {
            if (!isSessionCurrent()) return
            expansionRequest = null
            betsCache.clear()
            mutableState.dispatch(FavoriteCartsAction.ExpansionCleared)
        }
        load()
    }

    fun toggleCart(id: Long) {
        val request = synchronized(mutableState) {
            if (!isSessionCurrent()) return
            val request = Any()
            expansionRequest = request
            if (mutableState.value.expandedCartId == id) {
                mutableState.dispatch(FavoriteCartsAction.ExpansionCleared)
                return
            }
            betsCache[id]?.let { bets ->
                mutableState.dispatch(FavoriteCartsAction.CachedCartExpanded(id, bets))
                return
            }
            mutableState.dispatch(FavoriteCartsAction.CartLoadStarted(id))
            request
        }
        viewModelScope.launch(dispatchers.io) {
            if (!isCurrentExpansion(request)) return@launch
            val result = getFavoriteCartBets(id)
            synchronized(mutableState) {
                if (!isCurrentExpansion(request)) return@launch
                when (result) {
                    is AppResult.Success -> {
                        betsCache[id] = result.value
                        mutableState.dispatch(FavoriteCartsAction.CartExpanded(id, result.value))
                    }

                    is AppResult.Failure -> failures.applyFailure(result.error, itemOnly = true)
                }
            }
        }
    }

    private fun isCurrentExpansion(request: Any): Boolean = synchronized(mutableState) {
        isSessionCurrent() && expansionRequest === request
    }

    private fun applyCarts(result: AppResult<List<FavoriteCart>>) = if (!isSessionCurrent()) {
        Unit
    } else {
        when (result) {
            is AppResult.Success -> mutableState.dispatch(FavoriteCartsAction.CartsLoaded(result.value))
            is AppResult.Failure -> failures.applyFailure(result.error)
        }
    }

    fun applyBets(result: AppResult<List<FavoriteCartBet>>) = if (!isSessionCurrent()) {
        Unit
    } else {
        when (result) {
            is AppResult.Success -> mutableState.dispatch(FavoriteCartsAction.BetsLoaded(result.value))
            is AppResult.Failure -> failures.applyFailure(result.error)
        }
    }
}
