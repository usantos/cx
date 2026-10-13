package br.gov.caixa.loterias.core.domain.home

interface HomeLayoutPreferences {
    fun loadOrder(): String?
    fun saveOrder(order: String)
}
