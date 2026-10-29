package br.gov.caixa.loterias.feature.cart.data.datasource.local

import br.gov.caixa.loterias.feature.cart.data.mapper.decodeLegacyCart

internal class VisitorCartGraphStorage(private val document: SecureCartDocument, private val beforeAccess: suspend () -> Unit = {}) {
    suspend fun snapshot() = beforeAccess().let { decodeLegacyCart(document.read().tables) }
    suspend fun <T> transaction(action: SecureCartTransaction.() -> T): T {
        beforeAccess()
        return document.transaction(action)
    }
}
