package br.gov.caixa.loterias.feature.cart.presentation.state

import br.gov.caixa.loterias.feature.cart.domain.model.CartReview
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart

sealed interface LocalCartReviewAction {
    data object LoadStarted : LocalCartReviewAction
    data class Loaded(val local: ShoppingCart?, val remote: ShoppingCart?, val review: CartReview?) : LocalCartReviewAction
    data class Failed(val message: String) : LocalCartReviewAction
    data object Discarded : LocalCartReviewAction
}

fun reduceLocalCartReviewState(state: LocalCartReviewState, action: LocalCartReviewAction): LocalCartReviewState = when (action) {
    LocalCartReviewAction.LoadStarted -> state.copy(loading = true, error = null)
    is LocalCartReviewAction.Loaded -> LocalCartReviewState(local = action.local, remote = action.remote, review = action.review)
    is LocalCartReviewAction.Failed -> state.copy(loading = false, error = action.message)
    LocalCartReviewAction.Discarded -> LocalCartReviewState(discarded = true)
}
