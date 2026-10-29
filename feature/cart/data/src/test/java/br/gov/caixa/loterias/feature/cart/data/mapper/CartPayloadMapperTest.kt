package br.gov.caixa.loterias.feature.cart.data.mapper

import br.gov.caixa.loterias.feature.cart.data.mapper.cartDisplayName
import br.gov.caixa.loterias.feature.cart.data.mapper.cartFlattenNumbers
import br.gov.caixa.loterias.feature.cart.data.mapper.cartMatchRows
import br.gov.caixa.loterias.feature.cart.data.mapper.cartModalityCode
import br.gov.caixa.loterias.feature.cart.data.mapper.cartModalityName
import br.gov.caixa.loterias.feature.cart.data.mapper.cartNumberRows
import br.gov.caixa.loterias.feature.cart.data.mapper.cartOrZero
import br.gov.caixa.loterias.feature.cart.data.mapper.cartTextValue
import br.gov.caixa.loterias.feature.cart.data.mapper.cartTeamName
import com.google.gson.JsonParser
import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CartPayloadMapperTest {
    @Test fun `sports mapper keeps team and match labels while ignoring malformed rows`() {
        val match = JsonParser.parseString(
            """[{"numero":4,"equipe1":{"nome":"Time A","uf":"SP"},"equipe2":{"descricaoCurta":"Time B"},"indexPlacarEquipe1":1,"indexPlacarEquipe2":2,"letraLegenda":"Coluna 1"},"inválido",{}]""",
        )

        assertEquals("Time A/SP", JsonParser.parseString("""{"nome":"Time A","uf":"SP"}""").cartTeamName())
        assertEquals("", JsonParser.parseString("null").cartTeamName())
        assertEquals(2, match.cartMatchRows().size)
        assertTrue(match.cartMatchRows().first().contains("4. Time A/SP x Time B"))
        assertTrue(match.cartMatchRows().first().contains("1 x 2 · Coluna 1"))
        assertTrue(match.cartMatchRows().last().contains("3."))
        assertTrue(JsonParser.parseString("{} ").cartMatchRows().isEmpty())
    }

    @Test fun `cart json mapper handles nested numbers modality fallbacks and nullable values`() {
        val numbers = JsonParser.parseString("[[1,[2]],[],[3]]")
        assertEquals(listOf(listOf(1, 2), emptyList(), listOf(3)), numbers.cartNumberRows())
        assertEquals(listOf(listOf(4)), JsonParser.parseString("4").cartNumberRows())
        assertTrue(JsonParser.parseString("[\"x\"]").cartNumberRows().isEmpty())
        assertTrue(JsonParser.parseString("null").cartNumberRows().isEmpty())
        assertEquals(listOf(1, 2, 3), numbers.cartFlattenNumbers())
        assertTrue(JsonParser.parseString("\"x\"").cartFlattenNumbers().isEmpty())

        assertEquals("MEGA_SENA", JsonParser.parseString("\" mega-sena \"").cartModalityCode())
        assertEquals("Loto Facil", JsonParser.parseString("\"loto_facil\"").cartModalityCode().cartDisplayName())
        assertEquals("LOTOFACIL", JsonParser.parseString("""{"nome":"Lotofacil"}""").cartModalityCode())
        assertEquals("", JsonParser.parseString("null").cartModalityCode())
        assertEquals("Especial", JsonParser.parseString("""{"descricao":"Regular","descricaoEspecial":"Especial"}""")
            .cartModalityName(JsonParser.parseString("\"Concurso especial\"")))
        assertEquals("Loto Facil", JsonParser.parseString("\"loto-facil\"").cartModalityName(null))
        assertEquals("", JsonParser.parseString("null").cartModalityName(null))

        assertEquals("valor", JsonParser.parseString("""{"valor":"valor"}""").cartTextValue())
        assertEquals("texto", JsonParser.parseString("\"texto\"").cartTextValue())
        assertEquals("", JsonParser.parseString("[]").cartTextValue())
        assertEquals("Nome", " nome__-".cartDisplayName())
        assertEquals(0, (null as Int?).cartOrZero())
        assertEquals(BigDecimal.ZERO, (null as BigDecimal?).cartOrZero())
    }
}
