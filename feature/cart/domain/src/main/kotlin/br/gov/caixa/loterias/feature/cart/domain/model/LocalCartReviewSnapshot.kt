package br.gov.caixa.loterias.feature.cart.domain.model

data class LocalCartReviewSnapshot(val local: ShoppingCart, val remote: ShoppingCart, val review: CartReview?)
