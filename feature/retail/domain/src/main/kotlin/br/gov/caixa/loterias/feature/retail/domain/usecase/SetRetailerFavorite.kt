package br.gov.caixa.loterias.feature.retail.domain.usecase

import br.gov.caixa.loterias.core.common.result.appResultOf
import br.gov.caixa.loterias.feature.retail.domain.model.localizedMessage as messageText
import br.gov.caixa.loterias.feature.retail.domain.repository.RetailRepository

class SetRetailerFavorite(private val repository: RetailRepository) : SetRetailerFavoriteUseCase {
    override suspend fun invoke(retailerId: Long, favorite: Boolean) = appResultOf {
        require(retailerId > 0) { messageText("loterica_invalida_d34015") }
        repository.setFavorite(retailerId, favorite)
    }
}
