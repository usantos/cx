package br.gov.caixa.loterias.feature.marketplace.data.datasource.remote

import br.gov.caixa.loterias.core.network.dto.betting.AddPoolShareRequestDto
import br.gov.caixa.loterias.core.network.dto.cart.CartDto
import br.gov.caixa.loterias.core.network.dto.marketplace.MarketplacePageDto
import br.gov.caixa.loterias.core.network.dto.marketplace.MarketplacePoolDetailDto

interface MarketplaceRemoteDataSource {
    suspend fun pools(query: Map<String, String>): MarketplacePageDto?
    suspend fun poolDetail(poolCode: String): MarketplacePoolDetailDto?
    suspend fun addPoolShare(request: AddPoolShareRequestDto): CartDto?
}
