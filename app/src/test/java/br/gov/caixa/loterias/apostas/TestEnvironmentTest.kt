package br.gov.caixa.loterias.apostas

import br.gov.caixa.loterias.core.domain.environment.model.TestEnvironment
import br.gov.caixa.loterias.feature.home.presentation.mapper.homeEnvironmentLabel
import br.gov.caixa.loterias.feature.home.presentation.mapper.name
import org.junit.Assert.*
import org.junit.Test

class TestEnvironmentTest {
    @Test fun `legacy identities map to the exact configured endpoints`() {
        val expected = linkedMapOf(
            "EXTERNO" to "https://simlo.tqs.caixa.gov.br/silce-servico-rest/rest/v1/",
            "INTERNO" to "https://simlo.tqs.caixa/silce-servico-rest/rest/v1/",
            "INTERNO_ESTEIRA" to "https://silce-simlo.tqs.caixa/silce-servico-rest/rest/v1/",
            "EXTERNO_ESTEIRA" to "https://silce.simlo.tqs.caixa.gov.br/silce-servico-rest/rest/v1/",
            "HMP" to "https://nbcfvgbhnjmkasdef-lce.caixa.gov.br/",
            "PILOTO" to "https://pilotoloterias.caixa.gov.br/silce-servico-rest/rest/v1/",
        )
        assertEquals(expected, TestEnvironment.entries.associate { it.name to it.apiBaseUrl })
        expected.forEach { (name, url) ->
            assertEquals(name, homeEnvironmentLabel("tqs", url))
            assertEquals(name, homeEnvironmentLabel("automacao", " ${url.trimEnd('/')} "))
            assertNull(homeEnvironmentLabel("prd", url))
        }
    }

    @Test fun `unrecognized endpoint or flavor never invents an environment label`() {
        assertNull(homeEnvironmentLabel("unknown", TestEnvironment.EXTERNO.apiBaseUrl))
        assertNull(homeEnvironmentLabel("tqs", "https://unlisted.caixa.gov.br/"))
        assertNull(homeEnvironmentLabel("tqs", "https://simlo.tqs.caixa.gov.br.evil.test/silce-servico-rest/rest/v1/"))
        assertNull(homeEnvironmentLabel("tqs", "http://simlo.tqs.caixa.gov.br/silce-servico-rest/rest/v1/"))
    }
}
