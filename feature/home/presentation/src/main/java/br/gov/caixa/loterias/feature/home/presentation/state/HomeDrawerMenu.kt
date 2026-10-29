package br.gov.caixa.loterias.feature.home.presentation.state

import br.gov.caixa.loterias.feature.home.presentation.formatting.localizedMessage as messageText

/** Identidades estáveis para restaurar a expansão; rótulos e ordem reproduzem DrawerEnum do aplicativo legado. */
enum class HomeDrawerItem(val label: String) {
    MY_AREA(messageText("minha_area_f0acc6")),
    PERSONAL_DATA("Dados pessoais"),
    MY_BETS("Minhas apostas"),
    PURCHASES("Minhas compras"),
    FAVORITE_BETS("Apostas favoritas"),
    FAVORITE_CARTS("Carrinhos favoritos"),
    FAVORITE_RETAILERS(messageText("lotericas_favoritas_8a5e86")),
    CARDS(messageText("meus_cartoes_d7f57a")),
    RESULTS(messageText("resultados_310561")),
    RAPID(messageText("rapidao_658c41")),
    TICKETS("Conferir bilhetes"),
    RESPONSIBLE(messageText("jogo_responsavel_01eb13")),
    POLICY(messageText("politica_de_jogo_responsavel_0fb2c8")),
    ASSESSMENT(messageText("autoavaliacao_do_apostador_057b6e")),
    SUSPENSION(messageText("suspensao_temporaria_3a6e3c")),
    SOCIAL("Repasses sociais"),
    TERMS(messageText("termo_de_uso_d57d7b")),
    QUESTIONS(messageText("duvidas_10e026")),
    ABOUT("Sobre a CAIXA Loterias"),
}

data class HomeDrawerGroup(val item: HomeDrawerItem, val children: List<HomeDrawerItem> = emptyList())
