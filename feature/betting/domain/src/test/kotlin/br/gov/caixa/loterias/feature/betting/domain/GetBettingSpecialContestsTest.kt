package br.gov.caixa.loterias.feature.betting.domain

import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.domain.configuration.model.HomeFeatureConfiguration
import br.gov.caixa.loterias.core.domain.configuration.model.HomeSpecialContests
import br.gov.caixa.loterias.core.domain.configuration.repository.AppConfigurationRepository
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetBettingSpecialContests
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceFilterParameters
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GetBettingSpecialContestsTest {
    @Test fun `header reads shared cached configuration and preserves absent contests`() = runTest {
        listOf(HomeSpecialContests(3000, 1234), HomeSpecialContests()).forEach { expected ->
            val result = GetBettingSpecialContests(repository { HomeFeatureConfiguration(specialContests = expected) })()
            assertEquals(expected, (result as AppResult.Success).value)
        }
    }

    @Test fun `configuration failure becomes a result rather than throwing into the screen`() = runTest {
        val result = GetBettingSpecialContests(repository { error("Unavailable configuration") })()
        assertTrue(result is AppResult.Failure)
    }

    private fun repository(read: () -> HomeFeatureConfiguration) = object : AppConfigurationRepository {
        override suspend fun isVersionSupported(versionName: String): Boolean = error("Not a header request")
        override suspend fun marketplaceFilterParameters(): MarketplaceFilterParameters = error("Not a header request")
        override suspend fun refreshHomeFeatures(): HomeFeatureConfiguration = error("Header must use the shared cache")
        override suspend fun homeFeatures(): HomeFeatureConfiguration = read()
    }
}
