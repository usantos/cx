package br.gov.caixa.loterias.feature.cart.domain.model

sealed interface CartTransferPreparation {
    data object Empty : CartTransferPreparation
    data class Ready(val transferId: String, val bets: List<LocalCartBet>) : CartTransferPreparation {
        override fun toString() = "CartTransferPreparation.Ready(redacted)"
    }
    /** Nenhuma repetição automática é segura: um processo anterior pode já ter enviado a requisição. */
    data object RequiresReview : CartTransferPreparation
    data class CleanupPending(val transferId: String) : CartTransferPreparation
}
