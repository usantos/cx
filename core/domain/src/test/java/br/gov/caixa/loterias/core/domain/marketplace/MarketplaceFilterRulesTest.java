package br.gov.caixa.loterias.core.domain.marketplace;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class MarketplaceFilterRulesTest {
    @Test
    public void inclusiveRange_buildsConfiguredBackendRange() {
        assertEquals(Arrays.asList(2, 3, 4), MarketplaceFilterRules.inclusiveRange(2, 4));
        assertTrue(MarketplaceFilterRules.inclusiveRange(0, 4).isEmpty());
        assertTrue(MarketplaceFilterRules.inclusiveRange(5, 4).isEmpty());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void inclusiveRange_isImmutable() {
        MarketplaceFilterRules.inclusiveRange(1, 1).add(2);
    }

    @Test
    public void toggleSingle_selectsAndUnselectsOneValue() {
        assertEquals(Integer.valueOf(4), MarketplaceFilterRules.toggleSingle(null, 4));
        assertNull(MarketplaceFilterRules.toggleSingle(4, 4));
        assertEquals(Integer.valueOf(5), MarketplaceFilterRules.toggleSingle(4, 5));
        assertEquals(Integer.valueOf(4), MarketplaceFilterRules.toggleSingle(4, 0));
    }

    @Test
    public void validate_rejectsNullFilter() {
        assertInvalid(MarketplaceFilterRules.validate(null), "Filtro não informado");
    }

    @Test
    public void validate_leavesMoneyRangeToBackendAsLegacyFilterDoes() {
        MarketplaceFilter filter = filter("500.00", "10.00", 2, 100,
                Collections.emptyList(), Collections.emptyList());
        assertTrue(MarketplaceFilterRules.validate(filter).isValid());
        assertEquals("500.00", MarketplaceFilterRules.toQuery(filter).get("valorMinimoCota"));
        assertEquals("10.00", MarketplaceFilterRules.toQuery(filter).get("valorMaximoCota"));
        assertInvalid(MarketplaceFilterRules.validate(filter("500.00", "10.00", 1, 2,
                Collections.emptyList(), Collections.emptyList())), "menor que 2");
    }

    @Test
    public void validate_rejectsInvertedShareRange() {
        MarketplaceFilter filter = filter("inválido", "", 5, 2,
                Collections.emptyList(), Collections.emptyList());
        assertInvalid(MarketplaceFilterRules.validate(filter), "quantidade mínima");
    }

    @Test
    public void validate_enforcesLegacyShareLimits() {
        assertInvalid(MarketplaceFilterRules.validate(filter("", "", 1, null,
                Collections.emptyList(), Collections.emptyList())), "menor que 2");
        assertInvalid(MarketplaceFilterRules.validate(filter("", "", 101, null,
                Collections.emptyList(), Collections.emptyList())), "maior que a máxima");
        assertInvalid(MarketplaceFilterRules.validate(filter("", "", null, 1,
                Collections.emptyList(), Collections.emptyList())), "máxima não pode ser menor");
        assertInvalid(MarketplaceFilterRules.validate(filter("", "", null, 101,
                Collections.emptyList(), Collections.emptyList())), "maior que 100");
    }

    @Test
    public void validate_rejectsOverlappingNumberLists() {
        MarketplaceFilter filter = filter("", "", null, null,
                Arrays.asList(1, 2), Arrays.asList(2, 3));
        assertInvalid(MarketplaceFilterRules.validate(filter), "duas listas");
    }

    @Test
    public void validate_acceptsEmptyAndConsistentFilters() {
        assertTrue(MarketplaceFilterRules.validate(MarketplaceFilter.empty()).isValid());
        MarketplaceFilter filter = filter("1.000,50", "2.000,00", 2, 3,
                Collections.singletonList(1), Collections.singletonList(2));
        MarketplaceFilterRules.ValidationResult result = MarketplaceFilterRules.validate(filter);
        assertTrue(result.isValid());
        assertEquals("", result.getMessage());
    }

    @Test
    public void toQuery_usesBackendContractAndOmitsEmptyValues() {
        MarketplaceFilter filter = new MarketplaceFilter(
                "1,50", "2,50", 3, 4, 5, 6, "L", "M",
                Arrays.asList(7, 8), Arrays.asList(9, 10)
        );
        Map<String, String> query = MarketplaceFilterRules.toQuery(filter);
        assertEquals("5", query.get("quantidadeAposta"));
        assertEquals("6", query.get("quantidadeDezena"));
        assertEquals("3", query.get("qtdMinimaCota"));
        assertEquals("4", query.get("qtdMaximaCota"));
        assertEquals("1,50", query.get("valorMinimoCota"));
        assertEquals("2,50", query.get("valorMaximoCota"));
        assertEquals("7;8", query.get("numerosEscolhidos"));
        assertEquals("9;10", query.get("numerosNaoEscolhidos"));
        assertEquals("L", query.get("numeroLoterico"));
    }

    @Test
    public void toQuery_emptyFilterCreatesEmptyImmutableMap() {
        Map<String, String> query = MarketplaceFilterRules.toQuery(MarketplaceFilter.empty());
        assertTrue(query.isEmpty());
        try {
            query.put("x", "y");
            fail("Expected immutable map");
        } catch (UnsupportedOperationException expected) {
            assertNotNull(expected);
        }
    }

    @Test
    public void toQuery_includesHomeModalityAndContestType() {
        Map<String, String> query = MarketplaceFilterRules.toQuery(
                MarketplaceFilter.forModality(2, 1)
        );

        assertEquals("2", query.get("idModalidade"));
        assertEquals("1", query.get("tipoConcurso"));
    }

    @Test
    public void withLottery_preservesTheOtherFiltersAndUsesLegacyQueryName() {
        MarketplaceFilter original = MarketplaceFilter.forModality(2, 1).withBetQuantity(4);
        MarketplaceFilter retailer = original.withLottery("12345");

        assertEquals(Integer.valueOf(4), retailer.getBetQuantity());
        assertEquals(Integer.valueOf(2), retailer.getModalityId());
        assertEquals("12345", retailer.getLottery());
        assertEquals("12345", MarketplaceFilterRules.toQuery(retailer).get("numeroLoterico"));
    }

    private MarketplaceFilter filter(
            String minValue,
            String maxValue,
            Integer minShares,
            Integer maxShares,
            List<Integer> wanted,
            List<Integer> unwanted
    ) {
        return new MarketplaceFilter(minValue, maxValue, minShares, maxShares,
                null, null, "", "", wanted, unwanted);
    }

    private void assertInvalid(MarketplaceFilterRules.ValidationResult result, String messagePart) {
        assertFalse(result.isValid());
        assertTrue(result.getMessage().contains(messagePart));
    }
}
