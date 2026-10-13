package br.gov.caixa.loterias.feature.cart

import br.gov.caixa.loterias.core.domain.cart.CartItem
import br.gov.caixa.loterias.core.domain.cart.CartItemKind
import br.gov.caixa.loterias.core.domain.cart.ShoppingCart
import java.math.BigDecimal
import java.time.Instant
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CartRulesTest {
    @Test fun `total considers price and quantity`() {
        assertEquals(2_500, totalCartCents(listOf(CartLine(500, 2), CartLine(1_500))))
        assertEquals(0, totalCartCents(emptyList()))
    }

    @Test fun `invalid negative values cannot reduce total`() {
        assertEquals(500, totalCartCents(listOf(CartLine(-100, 5), CartLine(500), CartLine(100, -1))))
    }

    @Test fun `currency uses Brazilian representation`() {
        assertEquals("R$ 25,00", formatCurrency(2_500))
        assertEquals("R$ 0,00", formatCurrency(-1))
    }

    @Test fun `sections follow legacy order and hide empty kinds`() {
        val items = listOf(item(3, CartItemKind.COMBO), item(1, CartItemKind.INDIVIDUAL_BET))
        val sections = visibleCartSections(items)
        assertEquals(listOf(CartItemKind.INDIVIDUAL_BET, CartItemKind.COMBO), sections.map { it.kind })
        assertEquals(listOf(1L), sections.first().items.map { it.id })
    }

    @Test fun `clear and favorite visibility preserve legacy restrictions`() {
        val individual = cart(listOf(item(1, CartItemKind.INDIVIDUAL_BET)))
        assertTrue(canClearCart(individual))
        assertFalse(canFavoriteCart(individual, authenticated = false))
        assertTrue(canFavoriteCart(individual, authenticated = true))
        assertFalse(canFavoriteCart(cart(individual.items + item(2, CartItemKind.POOL_SHARE)), true))
        assertFalse(canFavoriteCart(cart(individual.items + item(3, CartItemKind.COMBO)), true))
        assertFalse(canClearCart(null))
        assertFalse(canFavoriteCart(null, true))
    }

    @Test fun `toolbar uses flat presence independently of grouped card projections`() {
        val flatOnly = ShoppingCart(1, BigDecimal.TEN, emptyList(), totalBetCount = 2)
        assertTrue(canClearCart(flatOnly))
        assertTrue(canFavoriteCart(flatOnly, true))
        assertFalse(canFavoriteCart(flatOnly, false))
        assertFalse(canClearCart(flatOnly.copy(totalBetCount = 0)))
        assertFalse(canFavoriteCart(flatOnly.copy(totalBetCount = 0), true))
        assertFalse(canClearCart(cart(emptyList())))
        assertFalse(canFavoriteCart(cart(emptyList()), true))

        // Stale individual projections must not override an explicitly empty flat list.
        val individual = flatOnly.copy(items = listOf(item(1, CartItemKind.INDIVIDUAL_BET)), totalBetCount = 0)
        assertFalse(canClearCart(individual))
        assertFalse(canFavoriteCart(individual, true))
        // Pools and combo groups independently allow clearing, but never favoriting.
        for (kind in listOf(CartItemKind.POOL_SHARE, CartItemKind.COMBO)) {
            val grouped = individual.copy(items = listOf(item(2, kind)))
            assertTrue(canClearCart(grouped))
            assertFalse(canFavoriteCart(grouped, true))
            assertFalse(canFavoriteCart(grouped.copy(totalBetCount = 2), true))
        }
    }

    @Test fun `quantity labels use singular only for one`() {
        assertEquals("1 número", quantityLabel(1, "número", "números"))
        assertEquals("0 números", quantityLabel(0, "número", "números"))
        assertEquals("2 sorteios", quantityLabel(2, "sorteio", "sorteios"))
    }

    @Test fun `pool expiry handles active warning expired and malformed dates`() {
        val now = Instant.parse("2026-09-04T12:00:00Z")
        assertEquals(PoolExpiry(PoolExpiryState.ACTIVE, "61:01"), poolExpiry("2026-09-04T13:01:01Z", now))
        assertEquals(PoolExpiry(PoolExpiryState.ACTIVE, "01:00"), poolExpiry("2026-09-04T12:01:00Z", now))
        assertEquals(PoolExpiry(PoolExpiryState.WARNING, "00:59"), poolExpiry("2026-09-04T12:00:59Z", now))
        assertEquals(PoolExpiry(PoolExpiryState.EXPIRED, "00:00"), poolExpiry("2026-09-04T11:59:59Z", now))
        assertEquals(PoolExpiry(PoolExpiryState.EXPIRED, "00:00"), poolExpiry("2026-09-04T12:00:00.900Z", now))
        assertEquals(PoolExpiry(PoolExpiryState.ACTIVE, "15:00"), poolExpiry("2026-09-04T09:15:00", now))
        assertEquals(PoolExpiry(PoolExpiryState.UNKNOWN, "--:--"), poolExpiry("inválida", now))
    }

    private fun item(id: Long, kind: CartItemKind) = CartItem(id, kind, "Item", "", BigDecimal.ONE)
    private fun cart(items: List<CartItem>) = ShoppingCart(1, BigDecimal.ONE, items)
}
