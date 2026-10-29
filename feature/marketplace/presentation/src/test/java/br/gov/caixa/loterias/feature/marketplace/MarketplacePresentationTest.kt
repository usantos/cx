package br.gov.caixa.loterias.feature.marketplace

import br.gov.caixa.loterias.feature.marketplace.presentation.screen.marketplaceListRetailerName
import br.gov.caixa.loterias.feature.marketplace.presentation.screen.marketplaceFavoriteRetailerName
import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Test
import br.gov.caixa.loterias.feature.marketplace.presentation.screen.marketplaceDrawDate
import br.gov.caixa.loterias.feature.marketplace.presentation.screen.marketplacePrizeWords
import br.gov.caixa.loterias.feature.marketplace.presentation.screen.marketplaceRetailerName
import br.gov.caixa.loterias.feature.marketplace.presentation.mapper.marketplacePoolModalityLabel
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplacePool

class MarketplacePresentationTest {
    @Test fun `retailer card labels use modality descriptions instead of backend enums`() {
        val names = mapOf(
            "SUPER_7" to "super sete",
            "MAIS_MILIONARIA" to "+milionária",
            "LOTOFACIL" to "lotofácil",
            "MEGA_SENA" to "mega sena"
        )
        names.forEach { (code, expected) ->
            assertEquals(expected, marketplacePoolModalityLabel(pool(code), emptyMap()))
        }
    }

    @Test fun `special retailer labels preserve standard line breaks and configured campaign names`() {
        val names = mapOf(
            "MEGA_SENA" to "mega da \nvirada",
            "QUINA" to "quina de \nsão joão",
            "LOTOFACIL" to "lotofácil da \nindependência",
            "DUPLA_SENA" to "dupla de \npáscoa",
            "LOTECA" to "loteca \nespecial"
        )
        names.forEach { (code, expected) ->
            assertEquals(expected, marketplacePoolModalityLabel(pool(code).copy(contestType = 2), emptyMap()))
        }
        assertEquals(
            "mega 30 anos",
            marketplacePoolModalityLabel(
                pool("MEGA_SENA").copy(contestType = 2),
                mapOf(("MEGA_SENA" to 2) to "mega 30 anos")
            )
        )
    }

    @Test fun `retailer and city names reproduce CX prepositions and corporate capitalization`() {
        assertEquals("Lotérica S.A. na Praça", marketplaceRetailerName("LOTÉRICA S.A. NA PRAÇA"))
        assertEquals("De Olho no Prêmio", marketplaceRetailerName("DE OLHO NO PRÊMIO"))
        assertEquals("Para Todos em Brasília", marketplaceRetailerName("PARA TODOS EM BRASÍLIA"))
    }

    @Test fun `estimated prize words use the legacy magnitude and truncate one decimal`() {
        assertEquals("(R$ 150 Milhões)", marketplacePrizeWords(BigDecimal("150000000")))
        assertEquals("(R$ 7,6 Milhões)", marketplacePrizeWords(BigDecimal("7654321")))
        assertEquals("(R$ 1,9 Milhão)", marketplacePrizeWords(BigDecimal("1999999")))
        assertEquals("", marketplacePrizeWords(BigDecimal("999")))
    }

    @Test fun `draw date accepts backend Brazilian and ISO formats without inventing a date`() {
        assertEquals("14/10", marketplaceDrawDate("14/10/2026 21:00:00"))
        assertEquals("14/10", marketplaceDrawDate("2026-10-14T21:00:00"))
        assertEquals("", marketplaceDrawDate(""))
    }

    @Test fun `retailer titles move whole words beyond thirty characters to next line`() {
        val firstWord = "A".repeat(26)
        assertEquals("${"A" + "a".repeat(25)}\nCasa", marketplaceListRetailerName("$firstWord CASA"))
        assertEquals("${"A" + "a".repeat(24)} Casa", marketplaceListRetailerName("${"A".repeat(25)} CASA"))
    }

    @Test fun `retailer title wrapping preserves long words and all following words`() {
        val longWord = "Superloterica".repeat(3)
        val formatted = longWord.lowercase().replaceFirstChar { it.uppercase() }
        assertEquals("$formatted\nCentro", marketplaceListRetailerName("$longWord CENTRO"))
        assertEquals("", marketplaceListRetailerName("   "))
    }

    @Test fun `favorite retailer names wrap at twenty seven while pool titles retain thirty characters`() {
        assertEquals("Lotérica\nAbcdefghijklmnopqrs Casa", marketplaceFavoriteRetailerName("LOTÉRICA ABCDEFGHIJKLMNOPQRS CASA"))
        assertEquals("Lotérica Abcdefghijklmnopqrs\nCasa", marketplaceListRetailerName("LOTÉRICA ABCDEFGHIJKLMNOPQRS CASA"))
        assertEquals("Lotérica Central de Goiania", marketplaceFavoriteRetailerName("LOTÉRICA CENTRAL DE GOIANIA"))
        assertEquals("", marketplaceFavoriteRetailerName("  "))
    }

    private fun pool(modality: String) = MarketplacePool(
        "B1", modality, 1, "Centro", "31/12/2026", "10",
        10, 2, 6, false
    )
}
