package br.gov.caixa.loterias.feature.cart.data.datasource.local

internal class RecentPurchaseLocalDataSourceImpl(private val access: SecureCartAccess) : RecentPurchaseLocalDataSource {
    override suspend fun datesForAmount(amount: String): List<String> = access.use { it.read().purchases[amount].orEmpty() }
    override suspend fun replaceForAmount(amount: String, date: String) = access.use { document ->
        document.transaction { replacePurchase(amount, date) }
    }
}
