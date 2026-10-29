package br.gov.caixa.loterias.feature.cart.data.mapper

import br.gov.caixa.loterias.feature.cart.data.dto.CartBetDto
import br.gov.caixa.loterias.feature.cart.domain.model.CartItemBadge
import br.gov.caixa.loterias.feature.cart.domain.model.CartItemKind
import com.google.gson.JsonParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CartSurpriseIndicatorTest {
    @Test fun `native manual enum keeps selected team visible without surprise badge`() {
        val item = CartBetDto(
            modality = JsonParser.parseString("\"TIMEMANIA\""),
            surpriseIndicator = JsonParser.parseString("""{"valor":1,"descricao":""}"""),
            favoriteTeam = JsonParser.parseString("""{"nome":"Time Azul","uf":"DF"}""")
        ).cartToCartItem(CartItemKind.INDIVIDUAL_BET)
        assertFalse(CartItemBadge.SURPRISE in item.badges)
        assertEquals("Time do coração: Time Azul/DF", item.complementaryLabel)
    }

    @Test fun `native ordinary surprise enum keeps its badge and hidden month`() {
        listOf(2).forEach { indicator ->
            val item = CartBetDto(
                modality = JsonParser.parseString("\"DIA_DE_SORTE\""),
                surpriseIndicator = JsonParser.parseString("""{"valor":$indicator,"descricao":""}"""),
                luckyMonth = JsonParser.parseString("\"Janeiro\"")
            ).cartToCartItem(CartItemKind.INDIVIDUAL_BET)
            assertTrue(CartItemBadge.SURPRISE in item.badges)
            assertEquals("Mês da sorte: ???", item.complementaryLabel)
        }
    }

    @Test fun `numeric surprise hides numbers but preserves the chosen team`() {
        val item = CartBetDto(
            modality = JsonParser.parseString("\"TIMEMANIA\""),
            surpriseIndicator = JsonParser.parseString("""{"valor":3,"descricao":"Surpresinha"}"""),
            favoriteTeam = JsonParser.parseString("""{"nome":"Time Azul","uf":"DF"}"""),
            numberQuantity = 10
        ).cartToCartItem(CartItemKind.INDIVIDUAL_BET)
        assertTrue(CartItemBadge.SURPRISE in item.badges)
        assertEquals(List(10) { "XX" }.joinToString(" - "), item.description)
        assertEquals("Time do coração: Time Azul/DF", item.complementaryLabel)
    }

    @Test fun `explicit non surprise flag keeps the selected month visible`() {
        val item = CartBetDto(
            modality = JsonParser.parseString("\"DIA_DE_SORTE\""),
            surprise = false,
            surpriseIndicator = JsonParser.parseString("\"SURPRESINHA\""),
            luckyMonth = JsonParser.parseString("\"Janeiro\"")
        ).cartToCartItem(CartItemKind.INDIVIDUAL_BET)
        assertFalse(CartItemBadge.SURPRISE in item.badges)
        assertEquals("Mês da sorte: Janeiro", item.complementaryLabel)
    }
}
