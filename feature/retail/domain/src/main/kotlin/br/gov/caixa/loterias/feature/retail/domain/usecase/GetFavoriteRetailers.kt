package br.gov.caixa.loterias.feature.retail.domain.usecase

import br.gov.caixa.loterias.core.common.result.appResultOf
import br.gov.caixa.loterias.feature.retail.domain.repository.RetailRepository

class GetFavoriteRetailers(private val repository: RetailRepository) : GetFavoriteRetailersUseCase {
    override suspend fun invoke() = appResultOf(repository::favorites)
}
