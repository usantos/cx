package br.gov.caixa.loterias.core.network.mock.fixture

import br.gov.caixa.loterias.core.network.mock.MockBackendScenario

internal object MockConfigurationFixtures {
    fun appParameters(scenario: MockBackendScenario = MockBackendScenario.HAPPY_PATH): String {
        val campaign = when (scenario) {
            MockBackendScenario.CAMPAIGN_OCTOBER_ROSE, MockBackendScenario.MARKETPLACE_PARITY_OCTOBER ->
                "\"vigenciaMesOutubroRosa\":true,\"vigenciaOutubroRosaMegaSena\":true,"
            MockBackendScenario.MARKETPLACE_PARITY ->
                "\"vigenciaMesOutubroRosa\":false,\"vigenciaOutubroRosaMegaSena\":false,"
            MockBackendScenario.CAMPAIGN_SEPTEMBER_BLUE ->
                "\"campanhasComemorativas\":[{\"codigo\":\"SETEMBRO_AZUL\",\"ativa\":true,\"areas\":[\"HOME\",\"DRAWER\"]}],"
            else -> ""
        }
        return """{
            "mapa":{"qtdeMinimaApostaBolaoFiltro":1,"qtdeMaximaApostaBolaoFiltro":15,
            "habilitaRevendaBolaoApp":true,"comboApostasHabilitado":true,"rapidaoHabilitado":true,
            ${campaign}"microServico":false,"grupoCpfNovaApi":0}}
        """.trimIndent()
    }
}
