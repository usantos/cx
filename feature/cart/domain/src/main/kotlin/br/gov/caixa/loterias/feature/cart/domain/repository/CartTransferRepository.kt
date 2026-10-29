package br.gov.caixa.loterias.feature.cart.domain.repository

import br.gov.caixa.loterias.feature.cart.domain.model.CartReview
import br.gov.caixa.loterias.feature.cart.domain.model.CartCleanupResult
import br.gov.caixa.loterias.feature.cart.domain.model.CartTransferPreparation

/** Protocolo local persistente. ownerKey é um hash do emissor e sujeito verificados, nunca um token ou CPF. */
interface CartTransferRepository {
    /** Confirma um recibo pendente ANTES de retornar Ready. Uma segunda chamada nunca retorna esse lote. */
    suspend fun prepare(ownerKey: String): CartTransferPreparation
    /** Chama somente após validar que a resposta remota confirma exatamente este lote. */
    suspend fun confirmReceived(ownerKey: String, transferId: String): Boolean
    /** Exclui somente linhas inalteradas de um lote confirmado, de forma atômica com seu recibo local. */
    suspend fun finishConfirmed(ownerKey: String, transferId: String): CartCleanupResult
    /** Somente este titular pode inspecionar e descartar explicitamente um lote não resolvido. */
    suspend fun review(ownerKey: String): CartReview? = null
    /** Recusa um retrato alterado; nunca toca em apostas novas fora deste recibo. */
    suspend fun discardReviewed(ownerKey: String, review: CartReview): Boolean = false
}
