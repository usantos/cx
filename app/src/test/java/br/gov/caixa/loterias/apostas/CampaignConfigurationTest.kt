package br.gov.caixa.loterias.apostas

import br.gov.caixa.loterias.core.domain.configuration.usecase.CampaignConfigurationUseCasesImpl
import br.gov.caixa.loterias.core.presentation.campaign.viewmodel.CampaignViewModel
import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.domain.configuration.model.CampaignActivation
import br.gov.caixa.loterias.core.domain.configuration.model.CampaignArea
import br.gov.caixa.loterias.core.domain.configuration.model.CommemorativeCampaign
import br.gov.caixa.loterias.core.domain.configuration.model.CommemorativeCampaignSettings
import br.gov.caixa.loterias.core.domain.configuration.model.HomeFeatureConfiguration
import br.gov.caixa.loterias.core.domain.configuration.repository.AppConfigurationRepository
import br.gov.caixa.loterias.core.domain.configuration.usecase.GetHomeFeatureConfiguration
import br.gov.caixa.loterias.core.testing.coroutines.MainDispatcherRule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class CampaignConfigurationTest {
    @get:Rule val main = MainDispatcherRule()
    private class Configuration : AppConfigurationRepository {
        override val campaignSettings = MutableStateFlow(CommemorativeCampaignSettings())
        val events = mutableListOf<String>()
        override suspend fun isVersionSupported(versionName: String) = error("unused")
        override suspend fun marketplaceFilterParameters() = error("unused")
        override suspend fun refreshHomeFeatures(): HomeFeatureConfiguration {
            events += "configuration"; return HomeFeatureConfiguration()
        }
    }
    @Test fun `security failure prevents refresh and success always prepares before configuration`() = runTest {
        val repository = Configuration(); var ready = false
        val adapter = CampaignConfigurationUseCasesImpl(repository, {
            repository.events += "security"
            if (ready) AppResult.Success(Unit) else AppResult.Failure(AppError.Network)
        }, GetHomeFeatureConfiguration(repository))
        adapter.refresh(); assertEquals(listOf("security"), repository.events)
        ready = true; adapter.refresh()
        assertEquals(listOf("security", "security", "configuration"), repository.events)
    }
    @Test fun `campaign state starts with defaults and observes repository updates while the screen is started`() = runTest {
        val repository = Configuration()
        val model = CampaignViewModel(CampaignConfigurationUseCasesImpl(repository, { AppResult.Success(Unit) },
            GetHomeFeatureConfiguration(repository)), FixedCoroutineDispatchers(main.dispatcher))
        assertNull(model.state.value.active(CampaignArea.HOME))
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { model.state.collect {} }
        runCurrent()
        repository.campaignSettings.value = CommemorativeCampaignSettings(listOf(
            CampaignActivation(CommemorativeCampaign.SETEMBRO_AZUL, setOf(CampaignArea.HOME))))
        runCurrent()
        assertEquals(CommemorativeCampaign.SETEMBRO_AZUL, model.state.value.active(CampaignArea.HOME))
        assertNull(model.state.value.active(CampaignArea.MEGA_SENA))
    }
}
