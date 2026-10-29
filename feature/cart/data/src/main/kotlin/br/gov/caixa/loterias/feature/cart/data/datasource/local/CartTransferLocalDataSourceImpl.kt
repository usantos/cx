package br.gov.caixa.loterias.feature.cart.data.datasource.local

internal class CartTransferLocalDataSourceImpl(
    private val document: SecureCartDocument,
    private val legacyReview: suspend () -> Boolean = { false },
) : CartTransferLocalDataSource {
    override suspend fun requiresReview() = legacyReview()
    override suspend fun <T> transaction(action: (CartTransferTransaction) -> T): T = document.transaction { action(this) }
}
