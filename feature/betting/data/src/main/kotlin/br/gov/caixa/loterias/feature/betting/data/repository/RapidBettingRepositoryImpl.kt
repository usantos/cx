package br.gov.caixa.loterias.feature.betting.data.repository

import br.gov.caixa.loterias.feature.betting.data.localizedMessage as messageText

import br.gov.caixa.loterias.core.data.mapper.serviceResultOf
import br.gov.caixa.loterias.feature.betting.domain.model.RapidBetConfiguration
import br.gov.caixa.loterias.feature.betting.domain.repository.RapidBettingRepository
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
import br.gov.caixa.loterias.feature.cart.data.repository.toDomainCart
import br.gov.caixa.loterias.feature.betting.data.mapper.toDomain
import br.gov.caixa.loterias.feature.betting.data.mapper.toDto
import br.gov.caixa.loterias.feature.betting.data.datasource.remote.RapidBettingRemoteDataSource

class RapidBettingRepositoryImpl(private val remote: RapidBettingRemoteDataSource) : RapidBettingRepository {
    override suspend fun configuration() = serviceResultOf {
        requireNotNull(remote.configuration()) { messageText("configuracao_do_rapidao_sem_payl_a7a2d6") }.toDomain()
    }

    override suspend fun save(configuration: RapidBetConfiguration) = serviceResultOf {
        (remote.save(configuration.toDto()) ?: configuration.toDto()).toDomain()
    }

    override suspend fun generate(quantity: Int) = serviceResultOf { remote.generate(quantity).toDomainCart() }
}
