package br.gov.caixa.loterias.core.data.datasource.remote

import br.gov.caixa.loterias.core.network.provider.LoteriasApiProvider

class AppConfigurationRemoteDataSourceImpl(
    private val apiProvider: LoteriasApiProvider,
) : AppConfigurationRemoteDataSource {
    override suspend fun isVersionSupported(versionName: String) = apiProvider.current().isVersionSupported(versionName).payload

    override suspend fun appParameters() = apiProvider.current().appParameters().payload.also {
        it?.values?.let(apiProvider::updateParameters)
    }
}
