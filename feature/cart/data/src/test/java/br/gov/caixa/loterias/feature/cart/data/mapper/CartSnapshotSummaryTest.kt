package br.gov.caixa.loterias.feature.cart.data.mapper

import br.gov.caixa.loterias.feature.cart.data.datasource.remote.CartRemoteDataSource
import br.gov.caixa.loterias.feature.cart.data.dto.CartBetDto
import br.gov.caixa.loterias.feature.cart.data.dto.CartComboDto
import br.gov.caixa.loterias.feature.cart.data.dto.CartDto
import br.gov.caixa.loterias.feature.cart.data.dto.CartPoolReservationDto
import br.gov.caixa.loterias.feature.cart.data.repository.CartRepositoryImpl
import br.gov.caixa.loterias.feature.cart.data.repository.toDomainCart
import br.gov.caixa.loterias.feature.cart.domain.model.CartItemKind
import com.google.gson.Gson
import com.google.gson.JsonParser
import java.math.BigDecimal
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CartSnapshotSummaryTest {
    private val individual = CartBetDto(
        id = 1,
        modality = JsonParser.parseString("\"MEGA_SENA\""),
        amount = BigDecimal("3.50"),
        betQuantity = 7,
        recurringDraws = 5
    )
    private val pool = CartBetDto(
        id = 2,
        amount = BigDecimal("999"),
        poolReservation = CartPoolReservationDto(total = BigDecimal("12.75"))
    )
    private val comboBets = listOf(CartBetDto(id = 3, amount = BigDecimal("4.25")), CartBetDto(id = 4, amount = BigDecimal("5.00")))
    private fun fullCart() = CartDto(
        id = 21,
        total = BigDecimal("9999"),
        individualBets = listOf(individual),
        poolShares = listOf(pool),
        combos = listOf(CartComboDto(id = 8, bets = comboBets)),
        bets = listOf(individual, pool) + comboBets
    )

    @Test fun `flat snapshot supplies exact summary without double counting grouped cards or recurring draws`() {
        val dto = fullCart()
        val cart = dto.toDomainCart()
        assertEquals(BigDecimal("25.50"), cart.total)
        assertEquals(4, cart.betCount)
        assertEquals(4, cart.totalBetCount)
        assertEquals(3, cart.items.size)
        assertEquals(listOf(CartItemKind.POOL_SHARE, CartItemKind.INDIVIDUAL_BET, CartItemKind.COMBO), cart.items.map { it.kind })
        assertEquals(2, cart.items.last().comboBetCount)
        assertEquals(BigDecimal("9.25"), cart.items.last().amount)
    }

    @Test fun `absent flat field retains grouped compatibility while explicit empty snapshot is authoritative`() {
        val dto = fullCart()
        val empty = dto.copy(bets = emptyList()).toDomainCart()
        assertEquals(BigDecimal.ZERO, empty.total)
        assertEquals(0, empty.betCount)
        assertEquals(3, empty.items.size)
        val fallback = dto.copy(bets = null).toDomainCart()
        assertEquals(BigDecimal("9999"), fallback.total)
        assertNull(fallback.totalBetCount)
        assertEquals(4, fallback.betCount)
        assertEquals(BigDecimal("25.50"), dto.copy(bets = null, total = null).toDomainCart().total)
        assertEquals(0, CartDto().toDomainCart().betCount)
        assertEquals(BigDecimal.ZERO, CartDto().toDomainCart().total)
    }

    @Test fun `flat only snapshot is retained for summary without inventing grouped combo removal ids`() {
        val cart = CartDto(bets = listOf(individual, pool) + comboBets).toDomainCart()
        assertEquals(BigDecimal("25.50"), cart.total)
        assertEquals(4, cart.betCount)
        assertEquals(listOf(individual.id), cart.items.map { it.id })
        assertEquals(CartItemKind.INDIVIDUAL_BET, cart.items.single().kind)
    }

    @Test fun `summary preserves decimal precision ignores absent standalone amounts and rejects incomplete reservation`() {
        val precise = BigDecimal("12345678901234567890.123456789")
        val cart = CartDto(
            bets = listOf(
                CartBetDto(),
                CartBetDto(amount = precise),
                CartBetDto(poolReservation = CartPoolReservationDto(total = BigDecimal.ZERO)),
                CartBetDto(amount = BigDecimal("0.000000001"))
            )
        ).toDomainCart()
        assertEquals(BigDecimal("12345678901234567890.123456790"), cart.total)
        assertEquals(4, cart.betCount)
        val error = runCatching {
            CartDto(bets = listOf(pool.copy(poolReservation = CartPoolReservationDto()))).toDomainCart()
        }.exceptionOrNull()
        assertTrue(error is IllegalArgumentException)
        assertEquals("Cota de bolão sem valor total", error!!.message)
    }

    @Test fun `json keeps flat list separate from grouped projections and null from empty`() {
        val gson = Gson()
        val raw = gson.toJson(fullCart())
        val decoded = gson.fromJson(raw, CartDto::class.java)
        assertEquals(4, decoded.bets!!.size)
        assertEquals(1, decoded.individualBets!!.size)
        assertEquals(fullCart(), decoded)
        for (rawCart in listOf("{}", "{\"apostas\":null}")) assertNull(gson.fromJson(rawCart, CartDto::class.java).bets)
        assertEquals(emptyList<CartBetDto>(), gson.fromJson("{\"apostas\":[]}", CartDto::class.java).bets)
    }

    @Test fun `mutation responses replace summary and still use grouped item ids on their original endpoints`() = runTest {
        var snapshot = fullCart()
        val calls = mutableListOf<String>()
        val source = object : CartRemoteDataSource {
            override suspend fun currentCart() = snapshot
            override suspend fun removeCartBet(id: Long): CartDto {
                calls += "bet:$id"
                return snapshot
            }
            override suspend fun removeCartCombo(id: Long): CartDto {
                calls += "combo:$id"
                return snapshot
            }
            override suspend fun clearCart() = snapshot
            override suspend fun validateCart() {}
            override suspend fun validateFavoriteCartName(name: String) {}
            override suspend fun saveCurrentCartFavorite(name: String) {}
        }
        val repo = CartRepositoryImpl(source)
        val initial = repo.current()
        snapshot = snapshot.copy(bets = listOf(individual), poolShares = emptyList(), combos = emptyList())
        assertEquals(1, repo.remove(initial.items.first()).betCount)
        assertEquals(BigDecimal("3.50"), repo.remove(initial.items.last()).total)
        assertEquals(listOf("bet:2", "combo:8"), calls)
        snapshot = snapshot.copy(bets = emptyList(), individualBets = emptyList())
        assertEquals(0, repo.clear().betCount)
        assertEquals(BigDecimal.ZERO, repo.current().total)
    }

    @Test fun `flat individual bets remain visible with empty or incomplete grouped projections`() {
        val extra = individual.copy(id = Long.MAX_VALUE)
        for (grouped in listOf(emptyList(), listOf(individual))) {
            val cart = fullCart().copy(individualBets = grouped, bets = listOf(individual, extra, pool) + comboBets).toDomainCart()
            assertEquals(listOf(individual.id, extra.id), cart.items.filter { it.kind == CartItemKind.INDIVIDUAL_BET }.map { it.id })
            assertEquals(listOf(pool.id), cart.items.filter { it.kind == CartItemKind.POOL_SHARE }.map { it.id })
            assertEquals(
                listOf(CartItemKind.POOL_SHARE, CartItemKind.INDIVIDUAL_BET, CartItemKind.INDIVIDUAL_BET, CartItemKind.COMBO),
                cart.items.map { it.kind }
            )
        }
    }

    @Test fun `flat combo bets and reserved shares are not presented as individual bets`() {
        val rows = listOf(individual, individual.copy(id = Long.MAX_VALUE, combo = true), pool)
        val cart = CartDto(bets = rows).toDomainCart()
        assertEquals(listOf(individual.id), cart.items.map { it.id })
        assertTrue(cart.items.all { it.kind == CartItemKind.INDIVIDUAL_BET })
    }
}
