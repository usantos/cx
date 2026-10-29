package br.gov.caixa.loterias.feature.betting.domain.repository

import br.gov.caixa.loterias.core.domain.service.model.ServiceResult
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
import br.gov.caixa.loterias.feature.betting.domain.model.RapidBetConfiguration

interface RapidBettingRepository {
    suspend fun configuration(): ServiceResult<RapidBetConfiguration>
    suspend fun save(configuration: RapidBetConfiguration): ServiceResult<RapidBetConfiguration>
    suspend fun generate(quantity: Int): ServiceResult<ShoppingCart>
}
