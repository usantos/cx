package br.gov.caixa.loterias.feature.betting.domain.recovery

/** A composição fornece o vínculo de sessão em execução, que nunca é serializado com o rascunho criptografado. */
interface BettingCheckpointSession {
    fun isCurrent(): Boolean
    suspend fun read(): BettingRecoveryRecord?
    suspend fun save(checkpoint: BettingCheckpoint)
    suspend fun beginOperation(id: String, kind: String, checkpoint: BettingCheckpoint)
    suspend fun beginOperation(id: String, kind: String) {
        error("This session does not support flow operations")
    }
    suspend fun completeOperation(id: String)
}

fun interface BettingCheckpointSessionFactory {
    /** Captura o ambiente atual e o vínculo autenticado de sessão, ou o vínculo explícito de visitante. */
    fun open(): BettingCheckpointSession
}

data class BettingUncertainOperation(val id: String, val kind: String)
data class BettingRecoveryRecord(
    val checkpoint: BettingCheckpoint?,
    val uncertainOperation: BettingUncertainOperation? = null,
    val reconcileCart: Boolean = false,
)
