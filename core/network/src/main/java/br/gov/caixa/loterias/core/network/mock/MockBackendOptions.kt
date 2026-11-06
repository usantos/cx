package br.gov.caixa.loterias.core.network.mock

data class MockBackendOptions(
    val enabled: Boolean = false,
    val scenario: MockBackendScenario = MockBackendScenario.HAPPY_PATH,
    val slowDelayMillis: Long = 2_500,
)

