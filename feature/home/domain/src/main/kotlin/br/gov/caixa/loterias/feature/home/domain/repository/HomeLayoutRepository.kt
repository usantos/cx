package br.gov.caixa.loterias.feature.home.domain.repository

interface HomeLayoutRepository {
    suspend fun loadOrder(): String?
    suspend fun saveOrder(order: String)
}
