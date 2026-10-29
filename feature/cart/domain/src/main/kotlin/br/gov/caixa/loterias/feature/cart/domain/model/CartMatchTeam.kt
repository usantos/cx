package br.gov.caixa.loterias.feature.cart.domain.model

data class CartMatchTeam(val team: CartTeam, val parameter: CartTeam?, val score: String?, val winner: Boolean)
