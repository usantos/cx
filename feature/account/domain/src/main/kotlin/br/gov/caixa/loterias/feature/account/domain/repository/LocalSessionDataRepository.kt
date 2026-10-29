package br.gov.caixa.loterias.feature.account.domain.repository

fun interface LocalSessionDataRepository {
    /** Limpa somente a identidade e as credenciais de login persistidas; lança erro se a conclusão não puder ser confirmada. */
    suspend fun clear()
}
