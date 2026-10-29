package br.gov.caixa.loterias.core.data

import br.gov.caixa.loterias.core.data.datasource.remote.AppConfigurationRemoteDataSource
import br.gov.caixa.loterias.core.data.repository.AppConfigurationRepositoryImpl
import br.gov.caixa.loterias.core.domain.configuration.usecase.GetHomeFeatureConfiguration
import br.gov.caixa.loterias.core.domain.configuration.usecase.GetMarketplaceFilterParameters
import br.gov.caixa.loterias.core.network.model.AppParametersDto
import br.gov.caixa.loterias.core.network.model.AppParametersMapDto
import com.google.gson.Gson
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class AppConfigurationRequestSharingTest {
    @Test fun startupFilterThemeAndHomeReuseTheSameResponse() = runTest {
        var calls = 0
        val repository = AppConfigurationRepositoryImpl(
            source {
                calls++
                payload()
            },
            backgroundScope
        )
        GetMarketplaceFilterParameters(repository)()
        GetHomeFeatureConfiguration(repository)()
        GetHomeFeatureConfiguration(repository)()
        assertEquals(1, calls)
    }

    @Test fun simultaneousStartupReadersShareTheInFlightRequest() = runTest {
        var calls = 0
        val response = CompletableDeferred<AppParametersMapDto?>()
        val repository = AppConfigurationRepositoryImpl(
            source {
                calls++
                response.await()
            },
            backgroundScope
        )
        val filter = async { repository.marketplaceFilterParameters() }
        val theme = async { repository.homeFeatures() }
        runCurrent()
        val observed = calls
        response.complete(payload())
        filter.await()
        theme.await()
        assertEquals(1, observed)
    }

    @Test fun foregroundAndManualRefreshFetchNewDataWhileNormalReadsUseCache() = runTest {
        var calls = 0
        val repository = AppConfigurationRepositoryImpl(
            source {
                calls++
                payload()
            },
            backgroundScope
        )
        val home = GetHomeFeatureConfiguration(repository)
        val campaign = br.gov.caixa.loterias.core.domain.configuration.usecase.CampaignConfigurationUseCasesImpl(
            repository,
            br.gov.caixa.loterias.core.domain.configuration.usecase.PrepareNetworkSecurityUseCase {
                br.gov.caixa.loterias.core.common.result.AppResult.Success(Unit)
            },
            home
        )
        GetMarketplaceFilterParameters(repository)()
        campaign.load()
        home()
        assertEquals(1, calls)
        campaign.refresh()
        assertEquals(2, calls)
        home()
        assertEquals(2, calls)
        home.refresh()
        assertEquals(REQUESTS_AFTER_BOTH_REFRESHES, calls)
    }

    @Test fun simultaneousRefreshesShareARequestAndCachedReadersReceiveTheUpdatedData() = runTest {
        var calls = 0
        val response = CompletableDeferred<AppParametersMapDto?>()
        val repository = AppConfigurationRepositoryImpl(
            source {
                calls++
                if (calls == 1) payload() else response.await()
            },
            backgroundScope
        )
        repository.homeFeatures()
        val first = async { repository.refreshHomeFeatures() }
        val second = async { repository.refreshHomeFeatures() }
        val reader = async { repository.homeFeatures() }
        runCurrent()
        assertEquals(2, calls)
        response.complete(AppParametersMapDto(Gson().fromJson("""{"habilitaRevendaBolaoApp":false}""", AppParametersDto::class.java)))
        org.junit.Assert.assertFalse(first.await().poolSalesEnabled)
        org.junit.Assert.assertFalse(second.await().poolSalesEnabled)
        org.junit.Assert.assertFalse(reader.await().poolSalesEnabled)
        org.junit.Assert.assertFalse(repository.homeFeatures().poolSalesEnabled)
        assertEquals(2, calls)
    }

    @Test fun cancellingOneStartupConsumerDoesNotCancelOtherReadersOrTheSharedRequest() = runTest {
        var calls = 0
        val response = CompletableDeferred<AppParametersMapDto?>()
        val repository = AppConfigurationRepositoryImpl(
            source {
                calls++
                response.await()
            },
            backgroundScope
        )
        val theme = async { repository.homeFeatures() }
        val startup = async { repository.marketplaceFilterParameters() }
        runCurrent()
        theme.cancel()
        runCurrent()
        response.complete(payload())
        startup.await()
        org.junit.Assert.assertTrue(repository.homeFeatures().poolSalesEnabled)
        assertEquals(1, calls)
    }

    @Test fun firstLoadFailureIsSharedAndLaterRetryCanSucceed() = runTest {
        var calls = 0
        val response = CompletableDeferred<Unit>()
        val requestScope = CoroutineScope(
            backgroundScope.coroutineContext +
                SupervisorJob(backgroundScope.coroutineContext[Job])
        )
        val repository = AppConfigurationRepositoryImpl(
            source {
                calls++
                if (calls == 1) {
                    response.await()
                    throw java.io.IOException("offline")
                }
                payload()
            },
            requestScope
        )
        val first = async { runCatching { repository.homeFeatures() } }
        val second = async { runCatching { repository.marketplaceFilterParameters() } }
        runCurrent()
        response.complete(Unit)
        org.junit.Assert.assertTrue(first.await().isFailure)
        org.junit.Assert.assertTrue(second.await().isFailure)
        assertEquals(1, calls)
        org.junit.Assert.assertTrue(repository.homeFeatures().poolSalesEnabled)
        assertEquals(2, calls)
    }

    private fun payload() = AppParametersMapDto(Gson().fromJson("""{"habilitaRevendaBolaoApp":true}""", AppParametersDto::class.java))
    private fun source(read: suspend () -> AppParametersMapDto?) = object : AppConfigurationRemoteDataSource {
        override suspend fun isVersionSupported(versionName: String) = true
        override suspend fun appParameters() = read()
    }
}

private const val REQUESTS_AFTER_BOTH_REFRESHES = 3
