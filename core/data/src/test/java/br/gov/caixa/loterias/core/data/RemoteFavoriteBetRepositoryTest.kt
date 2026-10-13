package br.gov.caixa.loterias.core.data

import br.gov.caixa.loterias.core.domain.betting.FavoriteBetCartOptions
import br.gov.caixa.loterias.core.domain.betting.FavoriteBetDraft
import br.gov.caixa.loterias.core.network.ApiEnvelope
import br.gov.caixa.loterias.core.network.CartDto
import br.gov.caixa.loterias.core.network.FavoriteBetCloversDto
import br.gov.caixa.loterias.core.network.FavoriteBetDto
import br.gov.caixa.loterias.core.network.FavoriteBetEnumDto
import br.gov.caixa.loterias.core.network.FavoriteBetNamedParameterDto
import br.gov.caixa.loterias.core.network.FavoriteBetPageDto
import br.gov.caixa.loterias.core.network.GroupedFavoriteBetsDto
import br.gov.caixa.loterias.core.network.LegacyBackendApi
import br.gov.caixa.loterias.core.network.LegacyBackendApiProvider
import com.google.gson.JsonParser
import java.math.BigDecimal
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RemoteFavoriteBetRepositoryTest {
    private val flat = FavoriteBetDto(
        id = 1,
        modality = FavoriteBetEnumDto(2, "Mega-Sena"),
        name = "Minha Mega",
        selectedNumbers = JsonParser.parseString("[1,2,3,4,5,6]"),
        heartTeam = FavoriteBetNamedParameterDto(name = "Time", state = "DF"),
        luckyMonth = FavoriteBetNamedParameterDto(name = "Janeiro"),
        clovers = FavoriteBetCloversDto(JsonParser.parseString("[1,2]")),
    )
    private val columns = FavoriteBetDto(
        id = 2,
        modality = FavoriteBetEnumDto(7, "Super Sete"),
        name = "",
        selectedNumbers = JsonParser.parseString("[[1],[2],null,[4]]"),
    )

    @Test fun `maps grouped and modality favorites including optional fields`() = runTest {
        val source = FakeSource()
        val repository = RemoteFavoriteBetRepository(source)
        val grouped = repository.favorites()
        assertEquals(listOf(1, 2), grouped.map { it.id.toInt() })
        with(grouped.first()) {
            assertEquals("Time/DF", heartTeam)
            assertEquals("Janeiro", luckyMonth)
            assertEquals(listOf(1, 2), clovers)
            assertEquals(listOf(1, 2, 3, 4, 5, 6), numbers)
            assertFalse(isSuperSeven)
        }
        with(grouped.last()) {
            assertEquals("Aposta favorita", name)
            assertEquals(listOf(listOf(1), listOf(2), emptyList(), listOf(4)), numberColumns)
            assertTrue(isSuperSeven)
        }

        assertEquals(listOf(1L), repository.favorites(2, 8).map { it.id })
        assertEquals(2, source.modality)
        assertEquals(8, source.offset)
    }

    @Test fun `delete and add delegate and missing payload is rejected`() = runTest {
        val source = FakeSource()
        val repository = RemoteFavoriteBetRepository(source)
        repository.delete(3)
        assertEquals(3L, source.deleted)
        val options = FavoriteBetCartOptions(2, 4, true)
        assertEquals(12L, repository.addToCart(4, options).id)
        assertEquals(options, source.options)

        source.cart = null
        var failed = false
        try { repository.addToCart(4, options) } catch (_: IllegalArgumentException) { failed = true }
        assertTrue(failed)
    }

    @Test fun `null malformed and primitive numbers map safely`() = runTest {
        val malformed = listOf(
            FavoriteBetDto(id = 3, selectedNumbers = null),
            FavoriteBetDto(id = 4, selectedNumbers = JsonParser.parseString("5")),
            FavoriteBetDto(id = 5, selectedNumbers = JsonParser.parseString("{}")),
        )
        val repository = RemoteFavoriteBetRepository(FakeSource(grouped = GroupedFavoriteBetsDto(mapOf("x" to FavoriteBetPageDto(items = malformed)))))
        val result = repository.favorites()
        assertTrue(result[0].numbers.isEmpty())
        assertEquals(listOf(5), result[1].numbers)
        assertTrue(result[2].numbers.isEmpty())
        assertEquals("", result[0].heartTeam)
    }

    @Test fun `retrofit source delegates grouped filtered delete and cart operations`() = runTest {
        val provider = LegacyBackendApiProvider()
        var methodNames = mutableListOf<String>()
        var filter: Map<String, String>? = null
        var options: List<Any?>? = null
        val favoriteBodies = mutableListOf<com.google.gson.JsonElement>()
        provider.replace(java.lang.reflect.Proxy.newProxyInstance(
            LegacyBackendApi::class.java.classLoader,
            arrayOf(LegacyBackendApi::class.java),
        ) { _, method, arguments ->
            methodNames += method.name
            when (method.name) {
                "groupedFavoriteBets" -> ApiEnvelope(GroupedFavoriteBetsDto())
                "favoriteBetsByModality" -> {
                    @Suppress("UNCHECKED_CAST")
                    filter = arguments?.first() as Map<String, String>
                    ApiEnvelope(FavoriteBetPageDto())
                }
                "deleteFavoriteBet" -> ApiEnvelope(com.google.gson.JsonNull.INSTANCE)
                "addFavoriteBetToCart" -> {
                    options = arguments?.take(4)
                    ApiEnvelope(CartDto(id = 7, total = BigDecimal.ZERO))
                }
                "validateFavoriteBet", "saveFavoriteBet" -> {
                    favoriteBodies += requireNotNull(arguments?.first() as? com.google.gson.JsonElement)
                    ApiEnvelope(com.google.gson.JsonNull.INSTANCE)
                }
                else -> error("Unexpected ${method.name}")
            }
        } as LegacyBackendApi)
        val source = RetrofitFavoriteBetRemoteDataSource(provider)
        assertEquals(GroupedFavoriteBetsDto(), source.grouped())
        assertEquals(FavoriteBetPageDto(), source.byModality(2, 8))
        assertEquals(mapOf("modalidade" to "2", "offset" to "8"), filter)
        source.delete(9)
        assertEquals(7L, source.addToCart(9, FavoriteBetCartOptions(2, 3, true))?.id)
        assertEquals(listOf(9L, 2, 3, true), options)
        val draft = FavoriteBetDraft(
            2,
            "Mega-Sena",
            "Nome que será mantido",
            listOf(listOf(1, 2, 3)),
            clovers = listOf(2, 5),
            favoriteTeam = br.gov.caixa.loterias.core.domain.betting.SportsTeam(101, "Time Azul", "DF"),
            luckyMonth = br.gov.caixa.loterias.core.domain.betting.LuckyMonth(9, "Setembro", "SET"),
        )
        source.validate(draft)
        source.save(draft.copy(numberColumns = listOf(listOf(1), listOf(2))))
        assertEquals(2, favoriteBodies[0].asJsonObject["modalidade"].asJsonObject["valor"].asInt)
        assertEquals("Nome que será mantido", favoriteBodies[0].asJsonObject["nome"].asString)
        assertEquals("[1,2,3]", favoriteBodies[0].asJsonObject["numerosSelecionados"].toString())
        assertEquals("[2,5]", favoriteBodies[0].asJsonObject["trevos"].asJsonObject["trevosSelecionados"].toString())
        assertEquals("Time Azul", favoriteBodies[0].asJsonObject["timeDoCoracao"].asJsonObject["nome"].asString)
        assertEquals("Setembro", favoriteBodies[0].asJsonObject["mesDeSorte"].asJsonObject["nome"].asString)
        assertEquals("[[1],[2]]", favoriteBodies[1].asJsonObject["numerosSelecionados"].toString())
        assertEquals(
            listOf(
                "groupedFavoriteBets", "favoriteBetsByModality", "deleteFavoriteBet",
                "addFavoriteBetToCart", "validateFavoriteBet", "saveFavoriteBet",
            ),
            methodNames,
        )
    }

    private inner class FakeSource(
        private val grouped: GroupedFavoriteBetsDto? = GroupedFavoriteBetsDto(
            linkedMapOf("mega" to FavoriteBetPageDto(items = listOf(flat)), "super" to FavoriteBetPageDto(items = listOf(columns))),
        ),
    ) : FavoriteBetRemoteDataSource {
        var modality: Int? = null
        var offset: Int? = null
        var deleted: Long? = null
        var options: FavoriteBetCartOptions? = null
        var cart: CartDto? = CartDto(id = 12, total = BigDecimal.ZERO)
        override suspend fun grouped() = grouped
        override suspend fun byModality(modalityId: Int, offset: Int): FavoriteBetPageDto {
            modality = modalityId
            this.offset = offset
            return FavoriteBetPageDto(items = listOf(flat))
        }
        override suspend fun delete(favoriteBetId: Long) { deleted = favoriteBetId }
        override suspend fun addToCart(favoriteBetId: Long, options: FavoriteBetCartOptions): CartDto? {
            this.options = options
            return cart
        }
        override suspend fun validate(draft: br.gov.caixa.loterias.core.domain.betting.FavoriteBetDraft) = Unit
        override suspend fun save(draft: br.gov.caixa.loterias.core.domain.betting.FavoriteBetDraft) = Unit
    }
}
