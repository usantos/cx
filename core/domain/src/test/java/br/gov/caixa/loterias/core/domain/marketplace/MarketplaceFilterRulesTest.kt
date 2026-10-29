package br.gov.caixa.loterias.core.domain.marketplace

import org.junit.Test
import org.junit.Assert.*
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceFilter
import br.gov.caixa.loterias.feature.marketplace.domain.rule.MarketplaceFilterRules

class MarketplaceFilterRulesTest {
    @Test
    fun inclusiveRange_buildsConfiguredBackendRange() {
        assertEquals(listOf(2, 3, 4), MarketplaceFilterRules.inclusiveRange(2, 4))
        assertTrue(MarketplaceFilterRules.inclusiveRange(0, 4).isEmpty())
        assertTrue(MarketplaceFilterRules.inclusiveRange(5, 4).isEmpty())
    }

    @Test(expected = UnsupportedOperationException::class)
    fun inclusiveRange_isImmutable() {
        (MarketplaceFilterRules.inclusiveRange(1, 1) as MutableList<Int>).add(2)
    }

    @Test
    fun toggleSingle_selectsAndUnselectsOneValue() {
        assertEquals(4, MarketplaceFilterRules.toggleSingle(null, 4))
        assertNull(MarketplaceFilterRules.toggleSingle(4, 4))
        assertEquals(5, MarketplaceFilterRules.toggleSingle(4, 5))
        assertEquals(4, MarketplaceFilterRules.toggleSingle(4, 0))
    }

    @Test
    fun validate_rejectsNullFilter() {
        assertInvalid(MarketplaceFilterRules.validate(null), "Filtro não informado")
    }

    @Test
    fun validate_leavesMoneyRangeToBackendAsLegacyFilterDoes() {
        val filter = filter("500.00", "10.00", 2, 100,
                emptyList(), emptyList())
        assertTrue(MarketplaceFilterRules.validate(filter).isValid)
        assertEquals("500.00", MarketplaceFilterRules.toQuery(filter).get("valorMinimoCota"))
        assertEquals("10.00", MarketplaceFilterRules.toQuery(filter).get("valorMaximoCota"))
        assertInvalid(MarketplaceFilterRules.validate(filter("500.00", "10.00", 1, 2,
                emptyList(), emptyList())), "menor que 2")
    }

    @Test
    fun validate_rejectsInvertedShareRange() {
        val filter = filter("inválido", "", 5, 2,
                emptyList(), emptyList())
        assertInvalid(MarketplaceFilterRules.validate(filter), "quantidade mínima")
    }

    @Test
    fun validate_enforcesLegacyShareLimits() {
        assertInvalid(MarketplaceFilterRules.validate(filter("", "", 1, null,
                emptyList(), emptyList())), "menor que 2")
        assertInvalid(MarketplaceFilterRules.validate(filter("", "", 101, null,
                emptyList(), emptyList())), "maior que a máxima")
        assertInvalid(MarketplaceFilterRules.validate(filter("", "", null, 1,
                emptyList(), emptyList())), "máxima não pode ser menor")
        assertInvalid(MarketplaceFilterRules.validate(filter("", "", null, 101,
                emptyList(), emptyList())), "maior que 100")
    }

    @Test
    fun validate_rejectsOverlappingNumberLists() {
        val filter = filter("", "", null, null,
                listOf(1, 2), listOf(2, 3))
        assertInvalid(MarketplaceFilterRules.validate(filter), "duas listas")
    }

    @Test
    fun validate_acceptsEmptyAndConsistentFilters() {
        assertTrue(MarketplaceFilterRules.validate(MarketplaceFilter.empty()).isValid)
        val filter = filter("1.000,50", "2.000,00", 2, 3,
                listOf(1), listOf(2))
        val result = MarketplaceFilterRules.validate(filter)
        assertTrue(result.isValid)
        assertEquals("", result.message)
    }

    @Test
    fun toQuery_usesBackendContractAndOmitsEmptyValues() {
        val filter = MarketplaceFilter(
                "1,50", "2,50", 3, 4, 5, 6, "L", "M",
                listOf(7, 8), listOf(9, 10)
        )
        val query = MarketplaceFilterRules.toQuery(filter)
        assertEquals("5", query.get("quantidadeAposta"))
        assertEquals("6", query.get("quantidadeDezena"))
        assertEquals("3", query.get("qtdMinimaCota"))
        assertEquals("4", query.get("qtdMaximaCota"))
        assertEquals("1,50", query.get("valorMinimoCota"))
        assertEquals("2,50", query.get("valorMaximoCota"))
        assertEquals("7;8", query.get("numerosEscolhidos"))
        assertEquals("9;10", query.get("numerosNaoEscolhidos"))
        assertEquals("L", query.get("numeroLoterico"))
    }

    @Test
    fun toQuery_emptyFilterCreatesEmptyImmutableMap() {
        val query = MarketplaceFilterRules.toQuery(MarketplaceFilter.empty())
        assertTrue(query.isEmpty())
        try {
            (query as MutableMap<String, String>)["x"] = "y"
            fail("Expected immutable map")
        } catch (expected: UnsupportedOperationException) {
            assertNotNull(expected)
        }
    }

    @Test
    fun toQuery_includesHomeModalityAndContestType() {
        val query = MarketplaceFilterRules.toQuery(
                MarketplaceFilter.forModality(2, 1)
        )

        assertEquals("2", query.get("idModalidade"))
        assertEquals("1", query.get("tipoConcurso"))
    }

    @Test
    fun withLottery_preservesTheOtherFiltersAndUsesLegacyQueryName() {
        val original = MarketplaceFilter.forModality(2, 1).withBetQuantity(4)
        val retailer = original.withLottery("12345")

        assertEquals(4, retailer.betQuantity)
        assertEquals(2, retailer.modalityId)
        assertEquals("12345", retailer.lottery)
        assertEquals("12345", MarketplaceFilterRules.toQuery(retailer).get("numeroLoterico"))
    }

    private fun filter(
            minValue: String,
            maxValue: String,
            minShares: Int?,
            maxShares: Int?,
            wanted: List<Int>,
            unwanted: List<Int>,
    ): MarketplaceFilter = MarketplaceFilter(
        minValue, maxValue, minShares, maxShares,
        null, null, "", "", wanted, unwanted
    )

    private fun assertInvalid(result: MarketplaceFilterRules.ValidationResult, messagePart: String) {
        assertFalse(result.isValid)
        assertTrue(result.message.contains(messagePart))
    }
}
