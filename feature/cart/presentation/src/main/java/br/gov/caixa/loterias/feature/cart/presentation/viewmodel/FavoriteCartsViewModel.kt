package br.gov.caixa.loterias.feature.cart.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.coroutines.DefaultCoroutineDispatchers
import br.gov.caixa.loterias.core.presentation.state.reduxStateStore
import br.gov.caixa.loterias.feature.cart.domain.model.FavoriteCartBet
import br.gov.caixa.loterias.feature.cart.domain.usecase.AddFavoriteCartToCurrentUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.DeleteFavoriteCartBetUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.DeleteFavoriteCartUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.GetFavoriteCartBetsUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.GetFavoriteCartsUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.MarkFavoriteCartAddedUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.WasFavoriteCartAddedUseCase
import br.gov.caixa.loterias.feature.cart.presentation.coordinator.FavoriteCartsFailures
import br.gov.caixa.loterias.feature.cart.presentation.coordinator.FavoriteCartsImport
import br.gov.caixa.loterias.feature.cart.presentation.coordinator.FavoriteCartsLoading
import br.gov.caixa.loterias.feature.cart.presentation.coordinator.FavoriteCartsRemoval
import br.gov.caixa.loterias.feature.cart.presentation.state.FavoriteCartsAction
import br.gov.caixa.loterias.feature.cart.presentation.state.FavoriteCartsUiState
import br.gov.caixa.loterias.feature.cart.presentation.state.reduceFavoriteCartsState

class FavoriteCartsViewModel(
    private val cartId: Long?,
    private val getFavoriteCarts: GetFavoriteCartsUseCase,
    private val getFavoriteCartBets: GetFavoriteCartBetsUseCase,
    private val deleteFavoriteCart: DeleteFavoriteCartUseCase,
    private val deleteFavoriteCartBet: DeleteFavoriteCartBetUseCase,
    private val addFavoriteCartToCurrent: AddFavoriteCartToCurrentUseCase,
    private val wasFavoriteCartAdded: WasFavoriteCartAddedUseCase = WasFavoriteCartAddedUseCase { false },
    private val markFavoriteCartAdded: MarkFavoriteCartAddedUseCase = MarkFavoriteCartAddedUseCase { },
    private val dispatchers: CoroutineDispatchers = DefaultCoroutineDispatchers,
    autoLoad: Boolean = true,
    private val isSessionCurrent: () -> Boolean = { true }
) : ViewModel() {
    private val mutableState = reduxStateStore<FavoriteCartsUiState, FavoriteCartsAction>(
        FavoriteCartsUiState(loading = autoLoad),
        ::reduceFavoriteCartsState
    ).also {
        addCloseable(it)
    }
    val state = mutableState.state
    private val betsCache = mutableMapOf<Long, List<FavoriteCartBet>>()
    private val importedFavoriteCartIds = mutableSetOf<Long>()
    private val failures = FavoriteCartsFailures(mutableState)
    private val loading = FavoriteCartsLoading(
        mutableState, viewModelScope, dispatchers, cartId, failures,
        getFavoriteCarts, getFavoriteCartBets, isSessionCurrent, betsCache
    )
    private val removal = FavoriteCartsRemoval(
        mutableState, viewModelScope, dispatchers, cartId, failures,
        deleteFavoriteCart, deleteFavoriteCartBet, betsCache, loading
    )
    private val importing = FavoriteCartsImport(
        mutableState, viewModelScope, dispatchers, cartId, failures,
        getFavoriteCartBets, addFavoriteCartToCurrent, wasFavoriteCartAdded, markFavoriteCartAdded,
        isSessionCurrent, betsCache, importedFavoriteCartIds
    )

    init {
        if (autoLoad) load()
    }

    fun load() = loading.load()

    fun refreshAfterDetail() = loading.refreshAfterDetail()

    fun toggleCart(id: Long) = loading.toggleCart(id)

    fun requestCartRemoval(id: Long) = mutableState.dispatch(FavoriteCartsAction.CartRemovalRequested(id))

    fun requestBetRemoval(id: Long) = mutableState.dispatch(FavoriteCartsAction.BetRemovalRequested(id))

    fun dismissConfirmation() = mutableState.dispatch(FavoriteCartsAction.ConfirmationDismissed)

    fun confirmCartRemoval() = removal.confirmCartRemoval()

    fun confirmBetRemoval() = removal.confirmBetRemoval()

    fun requestImport(id: Long? = cartId) = importing.requestImport(id)

    fun confirmExistingCartImport() = importing.confirmExistingCartImport()

    fun selectSpecialMode(selection: Int) = importing.selectSpecialMode(selection)

    fun consumeImported() = mutableState.dispatch(FavoriteCartsAction.ImportedConsumed)
}
