package br.gov.caixa.loterias.feature.cart.presentation.state

import kotlinx.coroutines.flow.*
import br.gov.caixa.loterias.feature.cart.domain.model.CartReview
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart

data class LocalCartReviewState(
    val loading: Boolean = false,
    val local: ShoppingCart? = null,
    val remote: ShoppingCart? = null,
    val review: CartReview? = null,
    val error: String? = null,
    val discarded: Boolean = false,
)
