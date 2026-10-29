package br.gov.caixa.loterias.feature.account.domain.repository

import br.gov.caixa.loterias.feature.account.domain.model.MunicipalityId

/** Consulta somente a identidade do município, sem carregar o catálogo de localidades. */
fun interface AccountMunicipalityRepository {
    suspend fun municipality(): MunicipalityId
}
