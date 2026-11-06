package br.gov.caixa.loterias.core.data.datasource.remote

import br.gov.caixa.loterias.core.network.model.AppParametersMapDto

interface AppConfigurationRemoteDataSource {
    suspend fun isVersionSupported(versionName: String): Boolean?
    suspend fun appParameters(): AppParametersMapDto?
}

