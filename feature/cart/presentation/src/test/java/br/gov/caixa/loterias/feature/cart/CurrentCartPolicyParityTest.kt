package br.gov.caixa.loterias.feature.cart

import br.gov.caixa.loterias.feature.cart.domain.model.CartItem
import br.gov.caixa.loterias.feature.cart.domain.model.CartItemKind
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
import java.math.BigDecimal
import org.junit.Assert.*
import org.junit.Test
import br.gov.caixa.loterias.feature.cart.presentation.mapper.canClearCart
import br.gov.caixa.loterias.feature.cart.presentation.mapper.canFavoriteCart
import br.gov.caixa.loterias.feature.cart.presentation.mapper.currentCartModalityName

/** CarrinhoActivity novo da CX usa os cartões exibidos, sem usar a contagem achatada legada. */
class CurrentCartPolicyParityTest {
    private val individual = CartItem(1, CartItemKind.INDIVIDUAL_BET, "Mega-Sena", "", BigDecimal.ONE)
    private val combo = CartItem(2, CartItemKind.COMBO, "Combo", "", BigDecimal.TEN, comboBetCount = 4)
    private val pool = CartItem(3, CartItemKind.POOL_SHARE, "Bolão", "", BigDecimal.TEN)

    @Test fun currentCxCanonicalNamesPreserveAccentsAndSpecialContestDescriptions() {
        assertEquals("lotofácil", currentCartModalityName(individual.copy(title = "Lotofacil", modalityCode = "LOTOFACIL")))
        assertEquals("mega-sena", currentCartModalityName(individual.copy(title = "MEGA_SENA", modalityCode = "MEGA_SENA")))
        assertEquals("mega da virada", currentCartModalityName(individual.copy(title = "Mega da Virada", modalityCode = "MEGA_SENA")))
        assertEquals("+milionária", currentCartModalityName(individual.copy(title = "MAIS_MILIONARIA", modalityCode = "MAIS_MILIONARIA")))
    }

    @Test fun displayedIndividualCardCanBeClearedEvenWhenFlattenedCountIsZero() {
        assertTrue(canClearCart(ShoppingCart(1, BigDecimal.ONE, listOf(individual), totalBetCount = 0)))
        assertFalse(canClearCart(ShoppingCart(1, BigDecimal.ZERO, emptyList(), totalBetCount = 5)))
    }

    @Test fun authenticatedCartCanFavoriteCombosButNeverPoolShares() {
        assertTrue(canFavoriteCart(ShoppingCart(1, BigDecimal.TEN, listOf(combo), totalBetCount = 4), true))
        assertTrue(canFavoriteCart(ShoppingCart(1, BigDecimal.ONE, listOf(individual), totalBetCount = 0), true))
        assertFalse(canFavoriteCart(ShoppingCart(1, BigDecimal.TEN, listOf(pool)), true))
        assertFalse(canFavoriteCart(ShoppingCart(1, BigDecimal.TEN, listOf(combo)), false))
        assertFalse(canFavoriteCart(ShoppingCart(1, BigDecimal.ZERO, emptyList(), totalBetCount = 5), true))
    }
}
