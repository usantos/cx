package br.gov.caixa.loterias.feature.betting.data.datasource.remote

import br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality
import br.gov.caixa.loterias.feature.betting.data.mapper.toBettingModality

internal class BettingModalitiesLoader(private val remote: BettingRemoteDataSource) {
    suspend fun modalities(): List<LotteryModality> =
        remote.modalities().orEmpty().map { it.toBettingModality() }
}
