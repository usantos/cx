package br.gov.caixa.loterias.feature.cart.data.datasource.local

internal interface RecentPurchaseLocalDataSource {
    suspend fun datesForAmount(amount: String): List<String>
    suspend fun replaceForAmount(amount: String, date: String)
}
