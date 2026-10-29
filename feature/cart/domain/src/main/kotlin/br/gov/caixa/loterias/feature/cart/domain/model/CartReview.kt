package br.gov.caixa.loterias.feature.cart.domain.model

data class CartReview(val transferId: String, val bets: List<LocalCartBet>) {
    override fun toString() = "CartReview(redacted)"
}
