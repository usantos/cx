package br.gov.caixa.loterias.feature.cart.data.mapper

import br.gov.caixa.loterias.feature.cart.data.dto.CartBetDto
import br.gov.caixa.loterias.feature.cart.data.dto.CartDto
import br.gov.caixa.loterias.feature.cart.domain.model.CartItemKind
import com.google.gson.JsonParser
import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Test

class CartSprint015ParityTest {
    @Test fun `remote Loteca preserves independent home draw and away selections`() {
        val matches = JsonParser.parseString(
            """[
            {"equipe1":{"nome":"Casa","uf":"SP","vitoria":true},"equipe2":{"nome":"Fora","vitoria":false},"empate":true},
            {"equipe1":{"nome":"A","vitoria":false},"equipe2":{"nome":"B","vitoria":true},"empate":false},
            {"equipe1":{"nome":"C","vitoria":true},"equipe2":{"nome":"D","vitoria":true},"empate":true}
        ]"""
        )
        val item = CartBetDto(lotecaMatches = matches).cartToCartItem(CartItemKind.INDIVIDUAL_BET)
        assertEquals(listOf(true, false, true), item.lotecaSelections.map { it.homeSelected })
        assertEquals(listOf(true, false, true), item.lotecaSelections.map { it.drawSelected })
        assertEquals(listOf(false, true, true), item.lotecaSelections.map { it.awaySelected })
        assertEquals("Casa/SP", item.lotecaSelections.first().homeLabel)
        assertEquals((1..LOTECA_MATCH_COUNT).toList(), item.lotecaSelections.map { it.number })
    }

    @Test fun `null modality entries are hidden without altering backend summary or unknown modalities`() {
        val visible = CartBetDto(id = 1, modality = JsonParser.parseString("\"FUTURE_GAME\""), amount = BigDecimal("4"))
        val missing = CartBetDto(id = 2, amount = BigDecimal("8"))
        val jsonNull = CartBetDto(id = 3, modality = JsonParser.parseString("null"), amount = BigDecimal("16"))
        val cart = CartDto(individualBets = listOf(visible, missing, jsonNull), bets = listOf(visible, missing, jsonNull)).mapCartPayload()
        assertEquals(listOf(1L), cart.items.map { it.id })
        assertEquals(BigDecimal("28"), cart.total)
        assertEquals(BACKEND_BET_COUNT, cart.betCount)
        val fallback = CartDto(individualBets = listOf(visible, missing, jsonNull)).mapCartPayload()
        assertEquals(BigDecimal("28"), fallback.total)
        val explicit = CartDto(total = BigDecimal("99"), individualBets = listOf(visible, missing)).mapCartPayload()
        assertEquals(BigDecimal("99"), explicit.total)
    }
}

private const val LOTECA_MATCH_COUNT = 3
private const val BACKEND_BET_COUNT = 3
