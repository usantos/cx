package br.gov.caixa.loterias.core.network.mock.response

import br.gov.caixa.loterias.core.network.mock.MockBackendScenario

internal object MockResponseErrors {
    fun errorResponse(path: String, scenario: MockBackendScenario): MockedHttpResponse? {
        // Erros HTTP de autenticação/negócio não podem impedir a inicialização do app simulado.
        if (path.startsWith("app/valida-versao/") || path == "parametros/configuraveis-app") return null
        return when (scenario) {
        MockBackendScenario.UNAUTHENTICATED -> error(401, "Não autenticado")
        MockBackendScenario.FORBIDDEN -> error(403, "Acesso negado")
        MockBackendScenario.VALIDATION_ERROR -> error(422, "Dados inválidos")
        MockBackendScenario.SERVER_ERROR -> error(500, "Serviço indisponível")
        else -> null
        }
    }

    fun error(code: Int, message: String) = MockedHttpResponse(
        code,
        message,
        "{\"codigo\":\"MOCK_$code\",\"mensagem\":\"$message\",\"payload\":null}",
    )
}
