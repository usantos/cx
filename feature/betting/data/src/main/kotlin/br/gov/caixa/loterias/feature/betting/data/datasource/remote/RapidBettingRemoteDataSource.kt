package br.gov.caixa.loterias.feature.betting.data.datasource.remote

import br.gov.caixa.loterias.feature.cart.data.dto.CartDto
import br.gov.caixa.loterias.feature.betting.data.dto.RapidConfigurationDto

interface RapidBettingRemoteDataSource {
    suspend fun configuration(): RapidConfigurationDto?
    suspend fun save(configuration: RapidConfigurationDto): RapidConfigurationDto?
    suspend fun generate(quantity: Int): CartDto?
}
