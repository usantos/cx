package br.gov.caixa.loterias.feature.marketplace.domain

import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.domain.configuration.model.HomeFeatureConfiguration
import br.gov.caixa.loterias.core.domain.configuration.model.HomeSpecialContests
import br.gov.caixa.loterias.core.domain.configuration.repository.AppConfigurationRepository
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceFilterParameters
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceSpecialContests
import br.gov.caixa.loterias.feature.marketplace.domain.usecase.GetMarketplaceSpecialContests
import java.io.IOException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test

class GetMarketplaceSpecialContestsTest {
    @Test fun `preserva os numeros dos concursos configurados`() = runTest {
        assertEquals(AppResult.Success(MarketplaceSpecialContests(MEGA_ANNIVERSARY_CONTEST, LOTECA_FATHERS_DAY_CONTEST)), useCase()())
    }

    @Test fun `preserva concursos ausentes`() = runTest {
        assertEquals(AppResult.Success(MarketplaceSpecialContests()), useCase { HomeFeatureConfiguration() }())
    }

    @Test fun `falhas de configuracao preservam o resultado inesperado do contrato existente`() = runTest {
        listOf(IllegalStateException("indisponivel"), IOException("indisponivel")).forEach { failure ->
            assertEquals(AppResult.Failure(AppError.Unexpected), useCase { throw failure }())
        }
    }

    @Test fun `cancelamento continua cancelando o consumidor`() = runTest {
        val cancellation = CancellationException("cancelado")
        assertSame(cancellation, runCatching { useCase { throw cancellation }() }.exceptionOrNull())
    }

    private fun useCase(
        read: suspend () -> HomeFeatureConfiguration = {
            HomeFeatureConfiguration(specialContests = HomeSpecialContests(MEGA_ANNIVERSARY_CONTEST, LOTECA_FATHERS_DAY_CONTEST))
        },
    ) = GetMarketplaceSpecialContests(object : AppConfigurationRepository {
        override suspend fun isVersionSupported(versionName: String) = error("Nao utilizado")
        override suspend fun marketplaceFilterParameters(): MarketplaceFilterParameters = error("Nao utilizado")
        override suspend fun homeFeatures() = read()
    })
    private companion object {
        const val MEGA_ANNIVERSARY_CONTEST = 3000
        const val LOTECA_FATHERS_DAY_CONTEST = 1234
    }
}
