package br.gov.caixa.loterias.feature.betting.data.recovery

import br.gov.caixa.loterias.core.domain.persistence.SecureRecordStore
import br.gov.caixa.loterias.feature.betting.domain.recovery.BettingCheckpoint
import br.gov.caixa.loterias.feature.betting.domain.recovery.BettingCheckpointCodec
import br.gov.caixa.loterias.feature.betting.domain.recovery.BettingCheckpointSession
import br.gov.caixa.loterias.feature.betting.domain.recovery.BettingRecoveryRecord
import br.gov.caixa.loterias.feature.betting.domain.recovery.BettingUncertainOperation
/** Uma transação criptografada controla o rascunho, a rota e o marcador de incerteza remota. */
class SecureBettingCheckpointSession(private val store: SecureRecordStore, private val current: () -> Boolean) : BettingCheckpointSession {
    override fun isCurrent() = current()

    override suspend fun read(): BettingRecoveryRecord? {
        checkCurrent()
        val record = decode(store.read().values)
        checkCurrent()
        return record
    }

    override suspend fun save(checkpoint: BettingCheckpoint) {
        checkpoint.validate()
        checkCurrent()
        store.update { existing ->
            checkCurrent()
            val previous = decode(existing)
            check(previous?.uncertainOperation == null || previous.checkpoint == checkpoint)
            encode(BettingRecoveryRecord(checkpoint, previous?.uncertainOperation))
        }
        checkCurrent()
    }

    override suspend fun beginOperation(id: String, kind: String, checkpoint: BettingCheckpoint) = begin(id, kind, checkpoint)

    override suspend fun beginOperation(id: String, kind: String) = begin(id, kind, null)

    private suspend fun begin(id: String, kind: String, checkpoint: BettingCheckpoint?) {
        require(id.matches(Regex("[A-Za-z0-9_-]{1,64}")))
        require(kind in setOf("ADD_BET", "SAVE_FAVORITE", "GENERATE_RAPID", "REMOVE_CART", "ADD_COMBO", "ADD_FAVORITE"))
        checkpoint?.validate()
        checkCurrent()
        store.update { existing ->
            checkCurrent()
            val previous = decode(existing)
            check(previous?.uncertainOperation == null) { "Operation requires reconciliation" }
            encode(BettingRecoveryRecord(checkpoint ?: previous?.checkpoint, BettingUncertainOperation(id, kind)))
        }
        checkCurrent()
    }

    override suspend fun completeOperation(id: String) {
        checkCurrent()
        store.update { existing ->
            checkCurrent()
            check(decode(existing)?.uncertainOperation?.id == id)
            encode(BettingRecoveryRecord(null, reconcileCart = true))
        }
        checkCurrent()
    }

    private fun checkCurrent() = check(current()) { "Checkpoint session is no longer current" }

    private fun encode(record: BettingRecoveryRecord): Map<String, String> = buildMap {
        put("schema", "1")
        put("reconcileCart", record.reconcileCart.toString())
        record.checkpoint?.let { checkpoint ->
            BettingCheckpointCodec.encode(checkpoint).forEach { (key, value) -> put("draft.$key", value) }
        }
        record.uncertainOperation?.let {
            put("operation.id", it.id)
            put("operation.kind", it.kind)
        }
    }

    private fun decode(values: Map<String, String>): BettingRecoveryRecord? {
        if (values.isEmpty()) return null
        require(values["schema"] == "1")
        require(values["reconcileCart"] in setOf("true", "false"))
        require(values.keys.all { it in setOf("schema", "reconcileCart", "operation.id", "operation.kind") || it.startsWith("draft.") })
        val draft = values.filterKeys { it.startsWith("draft.") }.mapKeys { it.key.removePrefix("draft.") }
            .takeIf { it.isNotEmpty() }?.let(BettingCheckpointCodec::decode)
        val id = values["operation.id"]
        val kind = values["operation.kind"]
        require((id == null) == (kind == null))
        val operation = id?.let {
            require(
                it.matches(Regex("[A-Za-z0-9_-]{1,64}")) &&
                    kind in setOf("ADD_BET", "SAVE_FAVORITE", "GENERATE_RAPID", "REMOVE_CART", "ADD_COMBO", "ADD_FAVORITE")
            )
            BettingUncertainOperation(it, requireNotNull(kind))
        }
        return BettingRecoveryRecord(draft, operation, values["reconcileCart"] == "true")
    }
}
