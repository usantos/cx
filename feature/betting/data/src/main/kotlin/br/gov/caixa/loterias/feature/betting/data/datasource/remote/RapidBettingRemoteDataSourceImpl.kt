package br.gov.caixa.loterias.feature.betting.data.datasource.remote

import br.gov.caixa.loterias.core.data.mapper.checkedPayload
import br.gov.caixa.loterias.core.network.provider.BackendApiProvider
import br.gov.caixa.loterias.feature.betting.data.dto.RapidConfigurationDto

class RapidBettingRemoteDataSourceImpl(
    private val provider: BackendApiProvider,
) : RapidBettingRemoteDataSource {
    override suspend fun configuration() = provider.current().featureBettingApi().rapidConfiguration().checkedPayload()
    override suspend fun save(configuration: RapidConfigurationDto) =
        provider.current().featureBettingApi().saveRapidConfiguration(configuration).checkedPayload()
    override suspend fun generate(quantity: Int) = provider.current().featureBettingApi().generateRapidBets(quantity).checkedPayload()
}
