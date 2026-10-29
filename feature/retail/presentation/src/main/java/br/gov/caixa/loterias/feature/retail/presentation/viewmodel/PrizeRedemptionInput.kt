package br.gov.caixa.loterias.feature.retail.presentation.viewmodel

/** Dados de entrada autenticados para uma única solicitação de resgate. */
data class PrizeRedemptionInput(val betId: Long, val email: String, val cpf: String) {
    override fun toString(): String = "PrizeRedemptionInput(redacted)"
}
