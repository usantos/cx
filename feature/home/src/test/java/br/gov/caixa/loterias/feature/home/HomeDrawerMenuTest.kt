package br.gov.caixa.loterias.feature.home

import br.gov.caixa.loterias.core.domain.configuration.HomeFeatureConfiguration
import org.junit.Assert.*
import org.junit.Test

class HomeDrawerMenuTest {
    @Test fun `groups and children preserve legacy order for all remote flag combinations`() {
        for (rapid in listOf(false, true)) for (history in listOf(false, true)) {
            val menu = homeDrawerMenu(HomeFeatureConfiguration(rapidBettingEnabled = rapid, myBetsVisible = history))
            val top = mutableListOf("Minha área", "Resultados")
            if (rapid) top += "Rapidão"
            top += listOf("Conferir bilhetes", "Jogo responsável", "Repasses sociais", "Termo de uso", "Dúvidas", "Sobre a CAIXA Loterias")
            assertEquals(top, menu.map { it.item.label })
            val children = mutableListOf("Dados pessoais")
            if (history) children += "Minhas apostas"
            children += listOf("Minhas compras", "Apostas favoritas", "Carrinhos favoritos", "Meus cartões")
            assertEquals(children, menu.first().children.map { it.label })
            assertEquals(listOf("Política de jogo responsável", "Autoavaliação do apostador", "Suspensão temporária"),
                menu.single { it.item == HomeDrawerItem.RESPONSIBLE }.children.map { it.label })
            assertEquals(2, menu.count { it.children.isNotEmpty() })
            val items = menu.flatMap { listOf(it.item) + it.children }
            assertEquals(items.size, items.distinct().size)
        }
        assertTrue(homeDrawerMenu(HomeFeatureConfiguration()).first().children.contains(HomeDrawerItem.MY_BETS))
    }

    @Test fun `greeting uses first and last names with safe empty identity fallback`() {
        assertEquals("Maria Santos", homeDrawerUserName("  MARIA DA SILVA SANTOS  "))
        assertEquals("João Souza", homeDrawerUserName("JOÃO\tDE\nSOUZA"))
        assertEquals("Ana Ana", homeDrawerUserName("ANA"))
        assertEquals("", homeDrawerUserName(null))
        assertEquals("", homeDrawerUserName(" \t "))
    }
}
