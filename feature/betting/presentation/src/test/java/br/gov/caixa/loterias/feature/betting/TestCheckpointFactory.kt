package br.gov.caixa.loterias.feature.betting

import br.gov.caixa.loterias.feature.betting.domain.recovery.BettingCheckpoint
import br.gov.caixa.loterias.feature.betting.domain.recovery.BettingCheckpointSession
import br.gov.caixa.loterias.feature.betting.domain.recovery.BettingCheckpointSessionFactory
import br.gov.caixa.loterias.feature.betting.domain.recovery.BettingRecoveryRecord
import br.gov.caixa.loterias.feature.betting.domain.recovery.BettingUncertainOperation
internal fun testCheckpointFactory() = BettingCheckpointSessionFactory {
    object : BettingCheckpointSession {
        var value: BettingRecoveryRecord? = null
        override fun isCurrent() = true
        override suspend fun read() = value
        override suspend fun save(checkpoint: BettingCheckpoint) {
            value = BettingRecoveryRecord(checkpoint)
        }
        override suspend fun beginOperation(id: String, kind: String, checkpoint: BettingCheckpoint) {
            value = BettingRecoveryRecord(checkpoint, BettingUncertainOperation(id, kind))
        }
        override suspend fun beginOperation(id: String, kind: String) {
            value =
                BettingRecoveryRecord(null, BettingUncertainOperation(id, kind))
        }
        override suspend fun completeOperation(id: String) {
            value = BettingRecoveryRecord(null, reconcileCart = true)
        }
    }
}
