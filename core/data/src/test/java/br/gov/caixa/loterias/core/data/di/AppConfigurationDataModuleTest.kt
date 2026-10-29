package br.gov.caixa.loterias.core.data.di

import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.data.datasource.remote.AppConfigurationRemoteDataSource
import br.gov.caixa.loterias.core.data.repository.AppConfigurationRepositoryImpl
import br.gov.caixa.loterias.core.data.datasource.remote.AppConfigurationRemoteDataSourceImpl
import br.gov.caixa.loterias.core.network.model.AppParametersDto
import br.gov.caixa.loterias.core.network.model.AppParametersMapDto
import br.gov.caixa.loterias.core.network.provider.LoteriasApiProvider
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AppConfigurationDataModuleTest {
    @Test
    fun `data providers preserve repository and use case behavior`() = runTest {
        val apiProvider = LoteriasApiProvider()
        assertTrue(AppConfigurationDataModule.configurationDataSource(apiProvider) is AppConfigurationRemoteDataSourceImpl)

        val source = FakeConfigurationSource()
        val repository = AppConfigurationDataModule.configurationRepository(source)
        assertTrue(repository is AppConfigurationRepositoryImpl)

        assertEquals(AppResult.Success(true), AppConfigurationDataModule.isVersionSupported(repository)("1.24.0"))
        assertEquals(AppResult.Success(br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceFilterParameters(2, 9)),
            AppConfigurationDataModule.filterParameters(repository)())
        val features = AppConfigurationDataModule.homeFeatureConfiguration(repository)()
        assertEquals(AppResult.Success(br.gov.caixa.loterias.core.domain.configuration.model.HomeFeatureConfiguration(
            poolSalesEnabled = true, comboBettingEnabled = true, rapidBettingEnabled = false, myBetsVisible = false,
        )), features)
    }

    private class FakeConfigurationSource : AppConfigurationRemoteDataSource {
        override suspend fun isVersionSupported(versionName: String) = versionName == "1.24.0"
        override suspend fun appParameters() = AppParametersMapDto(
            AppParametersDto(
                minimumMarketplaceBetQuantity = 2,
                maximumMarketplaceBetQuantity = 9,
                poolSalesEnabled = true,
                comboBettingEnabled = true,
                rapidBettingEnabled = false,
                myBetsVisible = false,
            ),
        )
    }
}
