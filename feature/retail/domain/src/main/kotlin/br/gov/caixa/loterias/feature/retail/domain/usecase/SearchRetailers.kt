package br.gov.caixa.loterias.feature.retail.domain.usecase

import br.gov.caixa.loterias.core.common.result.appResultOf
import br.gov.caixa.loterias.feature.retail.domain.repository.RetailRepository

class SearchRetailers(private val repository: RetailRepository) : SearchRetailersUseCase {
    override suspend fun invoke(query: String) = appResultOf {
        val normalized = query.trim()
        require(normalized.length >= 3) { "Informe pelo menos 3 caracteres" }
        repository.search(normalized)
    }
}
