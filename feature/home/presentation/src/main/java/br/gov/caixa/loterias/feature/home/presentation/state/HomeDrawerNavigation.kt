package br.gov.caixa.loterias.feature.home.presentation.state

import br.gov.caixa.loterias.feature.home.presentation.formatting.localizedMessage as messageText

enum class HomeDrawerConfirmation(val title: String, val message: String, val url: String?) {
    POLICY(
        messageText("jogo_responsavel_01eb13"),
        messageText("voce_sera_redirecionado_para_o_p_ddf63b"),
        "https://www.caixa.gov.br/jogo-responsavel/Paginas/default.aspx"
    ),
    ABOUT(
        messageText("atencao_b0f8a4"),
        messageText("voce_sera_direcionado_para_o_sit_79e7bc"),
        "https://www.caixa.gov.br/sobre-a-caixa/governanca-corporativa/caixa-loterias/Paginas/default.aspx"
    ),
    LOGOUT(messageText("atencao_b0f8a4"), "Tem certeza que deseja sair?", null),
}
