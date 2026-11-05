package br.gov.caixa.loterias.feature.cart.domain.model

import br.gov.caixa.loterias.core.domain.contract.cart.model.ShoppingCart

data class LocalCartReviewSnapshot(val local: ShoppingCart, val remote: ShoppingCart, val review: CartReview?)
