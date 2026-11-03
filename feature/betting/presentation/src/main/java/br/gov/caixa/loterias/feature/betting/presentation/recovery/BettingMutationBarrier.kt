package br.gov.caixa.loterias.feature.betting.presentation.recovery

import br.gov.caixa.loterias.feature.betting.domain.recovery.BettingCheckpointSession
import java.util.UUID
import java.util.concurrent.atomic.AtomicBoolean

/** Não há repetição automática: qualquer interrupção mantém sua intenção persistente para conciliação do carrinho. */
class BettingMutationBarrier(private val session: BettingCheckpointSession?, private val canStartOperation: () -> Boolean = { true }) {
    private val revokedPreparation = AtomicBoolean(false)
    suspend fun <T> run(kind: String, confirmed: (T) -> Boolean, remote: suspend () -> T): T {
        if (revokedPreparation.get()) throw BettingWriteNotAdmitted(checkpointed = true)
        if (!canStartOperation()) throw BettingWriteNotAdmitted(checkpointed = false)
        val checkpoint = session ?: return remote()
        check(checkpoint.isCurrent())
        val id = UUID.randomUUID().toString()
        checkpoint.beginOperation(id, kind)
        check(checkpoint.isCurrent())
        if (!canStartOperation()) {
            revokedPreparation.set(true)
            throw BettingWriteNotAdmitted(checkpointed = true)
        }
        val result = remote()
        check(checkpoint.isCurrent())
        if (confirmed(result)) checkpoint.completeOperation(id)
        check(checkpoint.isCurrent())
        return result
    }
}

internal class BettingWriteNotAdmitted(val checkpointed: Boolean) : IllegalStateException("Feature write unavailable")
