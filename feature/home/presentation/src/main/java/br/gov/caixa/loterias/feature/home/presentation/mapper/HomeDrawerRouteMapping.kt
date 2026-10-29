package br.gov.caixa.loterias.feature.home.presentation.mapper

import br.gov.caixa.loterias.feature.home.presentation.state.HomeDrawerItem

/** Somente destinos internos; ações do navegador exigem uma confirmação explícita separada. */
fun homeDrawerRoute(item: HomeDrawerItem): String? = when (item) {
    HomeDrawerItem.PERSONAL_DATA -> "account/personal-data"
    HomeDrawerItem.MY_BETS -> "tickets/my-bets"
    HomeDrawerItem.PURCHASES -> "tickets/purchases"
    HomeDrawerItem.FAVORITE_BETS -> "betting/favorites"
    HomeDrawerItem.FAVORITE_CARTS -> "cart/favorite-carts"
    HomeDrawerItem.FAVORITE_RETAILERS -> "betting/favorite-retailers"
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
