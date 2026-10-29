package br.gov.caixa.loterias.feature.cart.presentation.state

import br.gov.caixa.loterias.feature.cart.domain.model.FavoriteCart
import br.gov.caixa.loterias.feature.cart.domain.model.FavoriteCartBet

sealed interface FavoriteCartsAction {
    data class CartRemovalRequested(val id: Long) : FavoriteCartsAction
    data class BetRemovalRequested(val id: Long) : FavoriteCartsAction
    data object ConfirmationDismissed : FavoriteCartsAction
    data object ImportedConsumed : FavoriteCartsAction
    data class Failed(val message: String, val itemOnly: Boolean) : FavoriteCartsAction
    data object LoadStarted : FavoriteCartsAction
    data object ExpansionCleared : FavoriteCartsAction
    data class CachedCartExpanded(val id: Long, val bets: List<FavoriteCartBet>) : FavoriteCartsAction
    data class CartLoadStarted(val id: Long) : FavoriteCartsAction
    data class CartExpanded(val id: Long, val bets: List<FavoriteCartBet>) : FavoriteCartsAction
    data class CartsLoaded(val carts: List<FavoriteCart>) : FavoriteCartsAction
    data class BetsLoaded(val bets: List<FavoriteCartBet>) : FavoriteCartsAction
    data object CartRemovalStarted : FavoriteCartsAction
    data class CartRemoved(val id: Long, val carts: List<FavoriteCart>) : FavoriteCartsAction
    data object BetRemovalStarted : FavoriteCartsAction
    data object LastBetCartRemoved : FavoriteCartsAction
    data class ExistingImportDetected(val id: Long) : FavoriteCartsAction
    data object ExistingImportConfirmed : FavoriteCartsAction
    data object ImportBetsLoaded : FavoriteCartsAction
    data class SpecialSelectionRequested(val id: Long, val regularName: String, val specialName: String) : FavoriteCartsAction
    data object ImportStarted : FavoriteCartsAction
    data object Imported : FavoriteCartsAction
}

fun reduceFavoriteCartsState(state: FavoriteCartsUiState, action: FavoriteCartsAction): FavoriteCartsUiState = when (action) {
    is FavoriteCartsAction.CartRemovalRequested -> state.copy(pendingCartRemoval = action.id)

    is FavoriteCartsAction.BetRemovalRequested -> state.copy(pendingBetRemoval = action.id)

    FavoriteCartsAction.ConfirmationDismissed -> state.copy(
        pendingCartRemoval = null,
        pendingBetRemoval = null,
        pendingImportCartId = null,
        specialSelectionRequired = false,
        existingCartConfirmationRequired = false
    )

    FavoriteCartsAction.ImportedConsumed -> state.copy(imported = false)

    is FavoriteCartsAction.Failed -> state.copy(
        loading = if (action.itemOnly) state.loading else false,
        loadingCartId = null,
        errorMessage = action.message
    )

    FavoriteCartsAction.CartRemovalStarted -> state.copy(loading = true, pendingCartRemoval = null, errorMessage = null)

    is FavoriteCartsAction.CartRemoved -> state.copy(
        loading = false,
        carts = action.carts,
        expandedCartId = if (state.expandedCartId == action.id) null else state.expandedCartId,
        expandedBets = if (state.expandedCartId == action.id) emptyList() else state.expandedBets,
        errorMessage = null
    )

    FavoriteCartsAction.BetRemovalStarted -> state.copy(loading = true, pendingBetRemoval = null, errorMessage = null)

    FavoriteCartsAction.LastBetCartRemoved -> state.copy(loading = false, bets = emptyList(), cartDeleted = true, errorMessage = null)

    else -> reduceFavoriteCartsLoadingState(state, action)
}

private fun reduceFavoriteCartsLoadingState(state: FavoriteCartsUiState, action: FavoriteCartsAction): FavoriteCartsUiState =
    when (action) {
        FavoriteCartsAction.LoadStarted -> state.copy(loading = true, errorMessage = null)

        FavoriteCartsAction.ExpansionCleared -> state.copy(expandedCartId = null, expandedBets = emptyList())

        is FavoriteCartsAction.CachedCartExpanded -> state.copy(expandedCartId = action.id, expandedBets = action.bets, errorMessage = null)

        is FavoriteCartsAction.CartLoadStarted -> state.copy(loadingCartId = action.id, errorMessage = null)

        is FavoriteCartsAction.CartExpanded -> state.copy(
            loadingCartId = null,
            expandedCartId = action.id,
            expandedBets = action.bets,
            errorMessage = null
        )

        is FavoriteCartsAction.CartsLoaded -> state.copy(loading = false, carts = action.carts, errorMessage = null)

        is FavoriteCartsAction.BetsLoaded -> state.copy(loading = false, bets = action.bets, errorMessage = null)

        else -> reduceFavoriteCartsImportState(state, action)
    }

private fun reduceFavoriteCartsImportState(state: FavoriteCartsUiState, action: FavoriteCartsAction): FavoriteCartsUiState = when (action) {
    is FavoriteCartsAction.ExistingImportDetected -> state.copy(pendingImportCartId = action.id, existingCartConfirmationRequired = true)

    FavoriteCartsAction.ExistingImportConfirmed -> state.copy(existingCartConfirmationRequired = false)

    FavoriteCartsAction.ImportBetsLoaded -> state.copy(loadingCartId = null)

    is FavoriteCartsAction.SpecialSelectionRequested -> state.copy(
        pendingImportCartId = action.id,
        regularModalityName = action.regularName,
        specialModalityName = action.specialName,
        specialSelectionRequired = true
    )

    FavoriteCartsAction.ImportStarted -> state.copy(
        loading = true,
        specialSelectionRequired = false,
        existingCartConfirmationRequired = false,
        errorMessage = null
    )

    FavoriteCartsAction.Imported -> state.copy(loading = false, pendingImportCartId = null, imported = true)

    else -> state
}
