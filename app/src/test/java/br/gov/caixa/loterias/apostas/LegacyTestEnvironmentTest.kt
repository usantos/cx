package br.gov.caixa.loterias.apostas

import org.junit.Assert.*
import org.junit.Test

class LegacyTestEnvironmentTest {
    @Test fun `legacy identities map to the exact configured endpoints`() {
        val expected = linkedMapOf(
            "EXTERNO" to "https://simlo.tqs.caixa.gov.br/silce-servico-rest/rest/v1/",
            "INTERNO" to "https://simlo.tqs.caixa/silce-servico-rest/rest/v1/",
            "INTERNO_ESTEIRA" to "https://silce-simlo.tqs.caixa/silce-servico-rest/rest/v1/",
            "EXTERNO_ESTEIRA" to "https://silce.simlo.tqs.caixa.gov.br/silce-servico-rest/rest/v1/",
            "HMP" to "https://nbcfvgbhnjmkasdef-lce.caixa.gov.br/",
            "PILOTO" to "https://pilotoloterias.caixa.gov.br/silce-servico-rest/rest/v1/",
        )
        assertEquals(expected, LegacyTestEnvironment.entries.associate { it.name to it.apiBaseUrl })
        expected.forEach { (name, url) ->
            assertEquals(name, homeEnvironmentLabel("tqs", url))
            assertEquals(name, homeEnvironmentLabel("automacao", " ${url.trimEnd('/')} "))
            assertNull(homeEnvironmentLabel("prd", url))
        }
    }

    @Test fun `unrecognized endpoint or flavor never invents an environment label`() {
        assertNull(homeEnvironmentLabel("unknown", LegacyTestEnvironment.EXTERNO.apiBaseUrl))
        assertNull(homeEnvironmentLabel("tqs", "https://unlisted.caixa.gov.br/"))
        assertNull(homeEnvironmentLabel("tqs", "https://simlo.tqs.caixa.gov.br.evil.test/silce-servico-rest/rest/v1/"))
        assertNull(homeEnvironmentLabel("tqs", "http://simlo.tqs.caixa.gov.br/silce-servico-rest/rest/v1/"))
    }
}
