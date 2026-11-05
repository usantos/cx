package br.gov.caixa.loterias.feature.cart.presentation.state

import br.gov.caixa.loterias.core.domain.contract.cart.model.ShoppingCart
import br.gov.caixa.loterias.feature.cart.domain.model.CartReview

data class LocalCartReviewState(
    val loading: Boolean = false,
    val local: ShoppingCart? = null,
    val remote: ShoppingCart? = null,
    val review: CartReview? = null,
    val error: String? = null,
    val discarded: Boolean = false,
)
