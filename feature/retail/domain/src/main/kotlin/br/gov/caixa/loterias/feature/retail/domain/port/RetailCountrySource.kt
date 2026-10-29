package br.gov.caixa.loterias.feature.retail.domain.port

fun interface RetailCountrySource {
    suspend fun countryCode(): String?
}
