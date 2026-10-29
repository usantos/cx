// Literais numéricos descrevem conteúdos sintéticos, níveis da API da plataforma e limites esperados das massas de teste.
@file:Suppress("MagicNumber")

package br.gov.caixa.loterias.feature.cart.data.repository

import android.app.Application
import br.gov.caixa.loterias.feature.betting.domain.model.BetDraft
import br.gov.caixa.loterias.feature.betting.domain.model.BetPrice
import br.gov.caixa.loterias.feature.betting.domain.model.BettingParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryGameParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality
import br.gov.caixa.loterias.feature.cart.data.datasource.local.CartMemorySecureStore
import br.gov.caixa.loterias.feature.cart.data.datasource.local.CartReceipt
import br.gov.caixa.loterias.feature.cart.data.datasource.local.CartReceiptPhase
import br.gov.caixa.loterias.feature.cart.data.datasource.local.SecureCartDocument
import br.gov.caixa.loterias.feature.cart.data.datasource.local.VisitorCartGraphStorage
import br.gov.caixa.loterias.feature.cart.data.mapper.LEGACY_CART_TABLE
import br.gov.caixa.loterias.feature.cart.domain.model.CartItemBadge
import java.math.BigDecimal
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class VisitorCartSQLiteIntegrationTest {
    private var disk = CartMemorySecureStore()
    private val context get() = RuntimeEnvironment.getApplication()
    private fun game(id: Int, name: String, count: Int = 6) = LotteryGameParameters(
        LotteryModality(id, name, ""), 3000, "", 100, count, 100, 10,
        emptyList(), listOf(BetPrice(count, 0, BigDecimal("6.00")))
    )
    private fun parameters(vararg games: LotteryGameParameters, maximum: Int = 100) =
        BettingParameters(BigDecimal("1000"), BigDecimal.ZERO, maximum, games.toList())

    @Before fun clean() {
        context.deleteDatabase("Loterias.db")
        disk = CartMemorySecureStore()
    }

    @Test fun `visitor add survives recreation duplicate rejection is atomic and ids never reuse removed bets`() = runBlocking {
        val parameters = parameters(game(2, "Mega-Sena"))
        val draft = BetDraft(2, numbers = (1..6).toList())
        val repository = VisitorCartRepositoryImpl(SecureCartDocument(disk))
        assertTrue(repository.current().items.isEmpty())
        val first = repository.add(draft, parameters)
        assertEquals(1L, first.items.single().id)
        assertEquals(BigDecimal("6.00"), first.total)
        assertEquals("Mega-Sena", first.items.single().title)
        assertEquals(listOf((1..6).toList()), first.items.single().selectedNumbers)
        assertEquals(first, VisitorCartRepositoryImpl(SecureCartDocument(disk)).current())
        assertTrue(runCatching { repository.add(draft, parameters) }.isFailure)
        assertEquals(first, repository.current())
        assertTrue(
            runCatching {
                repository.add(draft.copy(numbers = (2..7).toList()), parameters(game(2, "Mega-Sena"), maximum = 1))
            }.isFailure
        )
        assertTrue(repository.remove(first.items.single()).items.isEmpty())
        val second = repository.add(draft, parameters)
        assertEquals(2L, second.items.single().id)
        assertTrue(repository.clear().items.isEmpty())
        assertTrue(VisitorCartRepositoryImpl(SecureCartDocument(disk)).current().items.isEmpty())
        assertTrue(runCatching { repository.remove(first.items.single()) }.isFailure)
    }

    @Test fun `lotomania mirror pair is linked and removed together`() = runBlocking {
        val repository = VisitorCartRepositoryImpl(SecureCartDocument(disk))
        val draft = BetDraft(16, numbers = (1..50).toList(), generateMirror = true, recurringDraws = 2)
        val cart = repository.add(draft, parameters(game(16, "Lotomania", 50)))
        assertEquals(2, cart.items.size)
        assertEquals(BigDecimal("24.00"), cart.total)
        assertEquals((51..100).toList(), cart.items[1].selectedNumbers.single())
        assertTrue(CartItemBadge.MIRROR in cart.items[1].badges)
        assertTrue(CartItemBadge.RECURRING in cart.items[0].badges)
        assertTrue(repository.remove(cart.items[1]).items.isEmpty())
    }

    @Test fun `surprise quantity uses distinct durable ids and keeps unknown numbers redacted`() = runBlocking {
        val repository = VisitorCartRepositoryImpl(SecureCartDocument(disk))
        val cart = repository.add(
            BetDraft(2, numbers = emptyList(), numberQuantity = 6, surprise = true, quantity = 2),
            parameters(game(2, "Mega-Sena"))
        )
        assertEquals(listOf(1L, 2L), cart.items.map { it.id })
        assertTrue(cart.items.all { CartItemBadge.SURPRISE in it.badges })
        assertEquals("XX - XX - XX - XX - XX - XX", cart.items.first().description)
        assertEquals(BigDecimal("12.00"), cart.total)
    }

    @Test fun `graph allocator accounts for reserved transfer ids and rolls back failed writes`() = runBlocking {
        val storage = VisitorCartGraphStorage(SecureCartDocument(disk))
        storage.transaction {
            assertEquals(1L, nextId(0))
            saveLastId(10)
            assertEquals(11L, nextId(2))
            assertEquals(21L, nextId(20))
            assertEquals(1L, nextTeamId(emptyMap()))
            assertEquals(
                10L,
                nextTeamId(
                    mapOf(
                        "tb_equipe" to listOf(mapOf("_id" to "9")),
                        "tb_parametro_equipe" to listOf(mapOf("_id" to "8"))
                    )
                )
            )
            writeGraph(mapOf("tb_mes_sorte" to listOf(mapOf("numero" to "12", "nome" to "Dezembro"))))
            writeGraph(mapOf("tb_mes_sorte" to listOf(mapOf("numero" to "12", "nome" to "Atualizado"))))
            assertEquals("Atualizado", tables().getValue("tb_mes_sorte").single()["nome"])
        }
        SecureCartDocument(disk).transaction {
            save(CartReceipt("pending", "a".repeat(64), CartReceiptPhase.PENDING, mapOf(30L to "b".repeat(64))))
        }
        storage.transaction { assertEquals(31L, nextId(2)) }
        assertTrue(
            runCatching {
                storage.transaction {
                    saveLastId(99)
                    error("crash")
                }
            }.isFailure
        )
        storage.transaction { assertEquals(31L, nextId(2)) }
        assertTrue(
            runCatching {
                storage.transaction {
                    writeGraph(mapOf(LEGACY_CART_TABLE to listOf(mapOf("_id" to "1", "nullable" to null))))
                    error("rollback graph")
                }
            }.isFailure
        )
        storage.transaction { assertFalse(LEGACY_CART_TABLE in tables()) }
    }
}
