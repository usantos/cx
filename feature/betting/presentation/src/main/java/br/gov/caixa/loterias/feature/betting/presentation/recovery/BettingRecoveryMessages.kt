package br.gov.caixa.loterias.feature.betting.presentation.recovery

enum class BettingRecoveryMessage {
    WRITE_FAILED,
    RECONCILE_REQUIRED,
    RULES_UNAVAILABLE,
    CONTEST_CHANGED,
    READ_FAILED,
    INTENT_FAILED,
}

fun interface BettingRecoveryMessages {
    fun text(message: BettingRecoveryMessage): String

    companion object {
        /** Chamadores e testes sem Android possuem identificadores estáveis; a produção injeta recursos Android. */
        val Identifiers = BettingRecoveryMessages { it.name }
    }
}
