package br.gov.caixa.loterias.feature.betting.presentation.recovery

import br.gov.caixa.loterias.feature.betting.domain.recovery.BettingCheckpointSession
import java.util.UUID

/** Não há repetição automática: qualquer interrupção mantém sua intenção persistente para conciliação do carrinho. */
class BettingMutationBarrier(private val session: BettingCheckpointSession?) {
    suspend fun <T> run(kind: String, confirmed: (T) -> Boolean, remote: suspend () -> T): T {
        val checkpoint = session ?: return remote()
        check(checkpoint.isCurrent())
        val id = UUID.randomUUID().toString()
        checkpoint.beginOperation(id, kind)
        check(checkpoint.isCurrent())
        val result = remote()
        check(checkpoint.isCurrent())
        if (confirmed(result)) checkpoint.completeOperation(id)
        check(checkpoint.isCurrent())
        return result
    }
}
