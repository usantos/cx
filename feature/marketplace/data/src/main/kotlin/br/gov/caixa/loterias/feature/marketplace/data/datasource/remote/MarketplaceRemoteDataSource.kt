package br.gov.caixa.loterias.feature.marketplace.data.datasource.remote

import br.gov.caixa.loterias.feature.marketplace.data.dto.MarketplacePageDto
import br.gov.caixa.loterias.feature.marketplace.data.dto.MarketplacePoolDetailDto
import br.gov.caixa.loterias.feature.cart.data.dto.CartDto
import br.gov.caixa.loterias.feature.betting.data.dto.AddPoolShareRequestDto

interface MarketplaceRemoteDataSource {
    suspend fun pools(query: Map<String, String>): MarketplacePageDto?
    suspend fun poolDetail(poolCode: String): MarketplacePoolDetailDto?
    suspend fun addPoolShare(request: AddPoolShareRequestDto): CartDto?
}
