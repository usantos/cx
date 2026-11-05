package br.gov.caixa.loterias.feature.betting.data.datasource.remote

import br.gov.caixa.loterias.core.network.dto.betting.RapidConfigurationDto
import br.gov.caixa.loterias.core.network.dto.cart.CartDto

interface RapidBettingRemoteDataSource {
    suspend fun configuration(): RapidConfigurationDto?
    suspend fun save(configuration: RapidConfigurationDto): RapidConfigurationDto?
    suspend fun generate(quantity: Int): CartDto?
}
