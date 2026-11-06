package br.gov.caixa.loterias.core.presentation.delivery

import br.gov.caixa.loterias.core.domain.configuration.model.AppModulesConfiguration
import br.gov.caixa.loterias.core.domain.configuration.repository.AppModulesConfigurationRepository
import br.gov.caixa.loterias.core.domain.configuration.usecase.AppModulesConfigurationUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FeatureModuleForegroundRefreshTest {
    @Test fun foregroundRefreshRecoversWithoutConcurrentLoopsAndPauses() = runTest {
        val config = Configuration()
        val model = FeatureModuleForegroundRefresh(this, AppModulesConfigurationUseCase(config))
        model.foreground(true)
        model.foreground(true)
        runCurrent()
        assertEquals(1, config.refreshes)
        advanceTimeBy(REFRESH_INTERVAL)
        runCurrent()
        assertEquals(2, config.refreshes)
        model.foreground(false)
        advanceTimeBy(PAUSED_INTERVAL)
        runCurrent()
        assertEquals(2, config.refreshes)
        model.foreground(true)
        runCurrent()
        assertEquals(REFRESHES_AFTER_RESUME, config.refreshes)
        model.close()
    }

    private class Configuration : AppModulesConfigurationRepository {
        override val configuration = MutableStateFlow(AppModulesConfiguration())
        var refreshes = 0
        override suspend fun refresh() {
            refreshes++
        }
    }
}

private const val REFRESH_INTERVAL = 30_000L
private const val PAUSED_INTERVAL = 60_000L
private const val REFRESHES_AFTER_RESUME = 3
