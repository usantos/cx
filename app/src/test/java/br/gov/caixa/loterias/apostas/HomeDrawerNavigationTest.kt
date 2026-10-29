package br.gov.caixa.loterias.apostas

import br.gov.caixa.loterias.feature.home.presentation.mapper.homeDrawerRoute
import br.gov.caixa.loterias.feature.home.presentation.mapper.name
import br.gov.caixa.loterias.feature.home.presentation.state.HomeDrawerConfirmation
import br.gov.caixa.loterias.feature.home.presentation.state.HomeDrawerItem
import org.junit.Assert.*
import org.junit.Test

class HomeDrawerNavigationTest {
    @Test fun `each internal legacy action has its dedicated destination`() {
        val expected = mapOf(
            HomeDrawerItem.PERSONAL_DATA to "account/personal-data", HomeDrawerItem.MY_BETS to "tickets/my-bets",
            HomeDrawerItem.PURCHASES to "tickets/purchases", HomeDrawerItem.FAVORITE_BETS to "betting/favorites",
            HomeDrawerItem.FAVORITE_CARTS to "cart/favorite-carts",
            HomeDrawerItem.FAVORITE_RETAILERS to "betting/favorite-retailers", HomeDrawerItem.CARDS to "payment/cards",
            HomeDrawerItem.RESULTS to "results/modalities", HomeDrawerItem.RAPID to "betting/rapid",
            HomeDrawerItem.TICKETS to "tickets/read", HomeDrawerItem.ASSESSMENT to "responsible/start",
            HomeDrawerItem.SUSPENSION to "responsible/suspension", HomeDrawerItem.SOCIAL to "responsible/social",
            HomeDrawerItem.TERMS to "support/terms-view", HomeDrawerItem.QUESTIONS to "support"
        )
        HomeDrawerItem.entries.forEach { assertEquals(it.name, expected[it], homeDrawerRoute(it)) }
    }

    @Test fun `browser and logout confirmations keep original texts and exact destinations`() {
        assertEquals("Você será redirecionado para o portal do Jogo Responsável", HomeDrawerConfirmation.POLICY.message)
        assertEquals("Jogo responsável", HomeDrawerConfirmation.POLICY.title)
        assertEquals("https://www.caixa.gov.br/jogo-responsavel/Paginas/default.aspx", HomeDrawerConfirmation.POLICY.url)
        assertEquals("Atenção", HomeDrawerConfirmation.ABOUT.title)
        assertEquals(
            "Você será direcionado para o site da CAIXA Loterias e sairá do aplicativo. Deseja continuar?",
            HomeDrawerConfirmation.ABOUT.message
        )
        assertEquals(
            "https://www.caixa.gov.br/sobre-a-caixa/governanca-corporativa/caixa-loterias/Paginas/default.aspx",
            HomeDrawerConfirmation.ABOUT.url
        )
        assertEquals("Tem certeza que deseja sair?", HomeDrawerConfirmation.LOGOUT.message)
        assertEquals("Atenção", HomeDrawerConfirmation.LOGOUT.title)
        assertNull(HomeDrawerConfirmation.LOGOUT.url)
    }
}
