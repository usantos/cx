package br.gov.caixa.loterias.apostas

import br.gov.caixa.loterias.feature.home.HomeDrawerItem

/** Internal destinations only; browser actions require a separate explicit confirmation. */
internal fun homeDrawerRoute(item: HomeDrawerItem): String? = when (item) {
    HomeDrawerItem.PERSONAL_DATA -> "account/personal-data"
    HomeDrawerItem.MY_BETS -> "tickets/my-bets"
    HomeDrawerItem.PURCHASES -> "tickets/purchases"
    HomeDrawerItem.FAVORITE_BETS -> "betting/favorites"
    HomeDrawerItem.FAVORITE_CARTS -> "cart/favorite-carts"
    HomeDrawerItem.CARDS -> "payment/cards"
    HomeDrawerItem.RESULTS -> "results/modalities"
    HomeDrawerItem.RAPID -> "betting/rapid"
    HomeDrawerItem.TICKETS -> "tickets/read"
    HomeDrawerItem.ASSESSMENT -> "responsible/start"
    HomeDrawerItem.SUSPENSION -> "responsible/suspension"
    HomeDrawerItem.SOCIAL -> "responsible/social"
    HomeDrawerItem.TERMS -> "support/terms-view"
    HomeDrawerItem.QUESTIONS -> "support"
    HomeDrawerItem.MY_AREA, HomeDrawerItem.RESPONSIBLE, HomeDrawerItem.POLICY, HomeDrawerItem.ABOUT -> null
}

internal enum class HomeDrawerConfirmation(val title: String, val message: String, val url: String?) {
    POLICY("Jogo responsável", "Você será redirecionado para o portal do Jogo Responsável",
        "https://www.caixa.gov.br/jogo-responsavel/Paginas/default.aspx"),
    ABOUT("Atenção", "Você será direcionado para o site da CAIXA Loterias e sairá do aplicativo. Deseja continuar?",
        "https://www.caixa.gov.br/sobre-a-caixa/governanca-corporativa/caixa-loterias/Paginas/default.aspx"),
    LOGOUT("Atenção", "Tem certeza que deseja sair?", null),
}
