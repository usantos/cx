package br.gov.caixa.loterias.feature.cart.presentation.coordinator

import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.presentation.state.ReduxStateStore
import br.gov.caixa.loterias.feature.cart.domain.model.FavoriteCartBet
import br.gov.caixa.loterias.feature.cart.domain.usecase.DeleteFavoriteCartBetUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.DeleteFavoriteCartUseCase
import br.gov.caixa.loterias.feature.cart.presentation.state.FavoriteCartsAction
import br.gov.caixa.loterias.feature.cart.presentation.state.FavoriteCartsUiState
import kotlinx.coroutines.launch

internal class FavoriteCartsRemoval(
    private val mutableState: ReduxStateStore<FavoriteCartsUiState, FavoriteCartsAction>,
    private val viewModelScope: kotlinx.coroutines.CoroutineScope,
    private val dispatchers: CoroutineDispatchers,
    private val cartId: Long?,
    private val failures: FavoriteCartsFailures,
    private val deleteFavoriteCart: DeleteFavoriteCartUseCase,
    private val deleteFavoriteCartBet: DeleteFavoriteCartBetUseCase,
    private val betsCache: MutableMap<Long, List<FavoriteCartBet>>,
    private val loading: FavoriteCartsLoading
) {

    fun confirmCartRemoval() {
        val id = mutableState.value.pendingCartRemoval ?: return
        viewModelScope.launch(dispatchers.io) {
            mutableState.dispatch(FavoriteCartsAction.CartRemovalStarted)
            when (val result = deleteFavoriteCart(id)) {
                is AppResult.Success -> {
                    betsCache.remove(id)
                    mutableState.dispatch(FavoriteCartsAction.CartRemoved(id, result.value))
                }

                is AppResult.Failure -> failures.applyFailure(result.error)
            }
        }
    }

    fun confirmBetRemoval() {
        val id = mutableState.value.pendingBetRemoval ?: return
        val favoriteId = cartId ?: return
        viewModelScope.launch(dispatchers.io) {
            mutableState.dispatch(FavoriteCartsAction.BetRemovalStarted)
            if (mutableState.value.bets.size <= 1) {
                when (val result = deleteFavoriteCart(favoriteId)) {
                    is AppResult.Success -> mutableState.dispatch(FavoriteCartsAction.LastBetCartRemoved)
                    is AppResult.Failure -> failures.applyFailure(result.error)
                }
            } else {
                loading.applyBets(deleteFavoriteCartBet(favoriteId, id))
            }
        }
    }
}
