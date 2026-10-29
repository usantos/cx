package br.gov.caixa.loterias.core.data

import br.gov.caixa.loterias.core.domain.configuration.model.CampaignArea
import br.gov.caixa.loterias.core.domain.configuration.model.CommemorativeCampaign
import br.gov.caixa.loterias.core.network.model.AppParametersDto
import br.gov.caixa.loterias.core.network.model.AppParametersMapDto
import com.google.gson.Gson
import java.lang.reflect.Proxy
import java.time.LocalDate
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test
import br.gov.caixa.loterias.core.data.repository.AppConfigurationRepositoryImpl
import br.gov.caixa.loterias.core.data.mapper.toCommemorativeCampaigns
import br.gov.caixa.loterias.core.data.datasource.remote.AppConfigurationRemoteDataSource

class CommemorativeCampaignConfigurationTest {
    private val date = LocalDate.of(2026, 10, 15)
    private fun settings(json: String) = Gson().fromJson(json, AppParametersDto::class.java).toCommemorativeCampaigns()

    @Test fun legacyGeneralAndMegaSenaFlagsAreIndependentAndFailClosed() {
        for (general in listOf(false, true)) for (mega in listOf(false, true)) {
            val settings = settings("""{"vigenciaMesOutubroRosa":$general,"vigenciaOutubroRosaMegaSena":$mega}""")
            assertEquals(if (general) CommemorativeCampaign.OUTUBRO_ROSA else null, settings.active(CampaignArea.DRAWER, date))
            assertEquals(if (general && mega) CommemorativeCampaign.OUTUBRO_ROSA else null, settings.active(CampaignArea.MEGA_SENA, date))
        }
        assertNull(settings("{}").active(CampaignArea.HOME, date))
    }

    @Test fun explicitCampaignListOverridesLegacyAndHonorsScopeDatesAndPriority() {
        val settings = settings("""{"vigenciaMesOutubroRosa":true,"campanhasComemorativas":[
          {"codigo":"SETEMBRO_AZUL","ativa":true,"areas":["DRAWER"],"inicio":"2026-10-01","fim":"2026-10-31","prioridade":2},
          {"codigo":"OUTUBRO_ROSA","ativa":true,"areas":["HOME","DRAWER"],"prioridade":1}
        ]}""")
        assertEquals(CommemorativeCampaign.SETEMBRO_AZUL, settings.active(CampaignArea.DRAWER, date))
        assertEquals(CommemorativeCampaign.OUTUBRO_ROSA, settings.active(CampaignArea.HOME, date))
        assertNull(settings.active(CampaignArea.MEGA_SENA, date))
        assertEquals(CommemorativeCampaign.OUTUBRO_ROSA, settings.active(CampaignArea.DRAWER, date.plusMonths(1)))
        assertNull(settings("""{"vigenciaMesOutubroRosa":true,"campanhasComemorativas":[]}""").active(CampaignArea.DRAWER, date))
    }

    @Test fun unknownDisabledAndInvalidRulesDoNotActivateFallback() {
        val rules = listOf("null", """{"codigo":"FUTURA","ativa":true}""",
            """{"codigo":"OUTUBRO_ROSA","ativa":false}""",
            """{"codigo":"OUTUBRO_ROSA","ativa":true,"inicio":"invalid"}""",
            """{"codigo":"OUTUBRO_ROSA","ativa":true,"areas":["UNKNOWN"]}""",
            """{"codigo":"OUTUBRO_ROSA","ativa":true,"inicio":"2026-11-01","fim":"2026-09-01"}""")
        rules.forEach { rule ->
            val settings = settings("""{"vigenciaMesOutubroRosa":true,"campanhasComemorativas":[$rule]}""")
            CampaignArea.entries.forEach { assertNull(rule, settings.active(it, date)) }
        }
    }

    @Test fun homeRefreshAppliesBackendChangesAndKeepsLastGoodConfigurationOnFailure() = runTest {
        var payload = """{"vigenciaMesOutubroRosa":true}"""
        var fail = false
        val source = Proxy.newProxyInstance(AppConfigurationRemoteDataSource::class.java.classLoader,
            arrayOf(AppConfigurationRemoteDataSource::class.java)) { _, method, _ ->
            check(method.name == "appParameters")
            if (fail) throw java.io.IOException("offline")
            AppParametersMapDto(Gson().fromJson(payload, AppParametersDto::class.java))
        } as AppConfigurationRemoteDataSource
        val repository = AppConfigurationRepositoryImpl(source)
        assertEquals(CommemorativeCampaign.OUTUBRO_ROSA, repository.refreshHomeFeatures().campaigns.active(CampaignArea.DRAWER, date))
        assertEquals(CommemorativeCampaign.OUTUBRO_ROSA, repository.campaignSettings.value.active(CampaignArea.DRAWER, date))
        fail = true
        assertEquals(CommemorativeCampaign.OUTUBRO_ROSA, repository.refreshHomeFeatures().campaigns.active(CampaignArea.DRAWER, date))
        fail = false
        payload = """{"vigenciaMesOutubroRosa":false}"""
        assertNull(repository.refreshHomeFeatures().campaigns.active(CampaignArea.DRAWER, date))
        assertNull(repository.campaignSettings.value.active(CampaignArea.DRAWER, date))
    }
}
