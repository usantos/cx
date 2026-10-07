package br.gov.caixa.loterias.apostas.utils;
import org.junit.Test;
import java.util.*;
import static org.junit.Assert.*;

public class ModalidadeOrderingTest {
    @Test public void ignoresUsageBelowConfiguredThreshold() {
        List<String> backend = Arrays.asList("A", "B", "C");
        assertEquals(backend, ModalidadeOrdering.sorted(backend, s -> false,
                s -> false, s -> s.equals("C") ? 9L : 0L, 10));
    }

    @Test public void promotesUsageAtThresholdAndKeepsSpecialAndFavoritePriority() {
        List<String> backend = Arrays.asList("A", "used", "favorite", "special", "below");
        assertEquals(Arrays.asList("special", "favorite", "used", "A", "below"),
                ModalidadeOrdering.sorted(backend, s -> s.equals("special"),
                        s -> s.equals("favorite"),
                        s -> s.equals("used") ? 10L : s.equals("below") ? 9L : 0L, 10));
    }

    @Test public void configurableThresholdChangesEligibility() {
        List<String> backend = Arrays.asList("A", "B");
        assertEquals(Arrays.asList("B", "A"), ModalidadeOrdering.sorted(backend,
                s -> false, s -> false, s -> s.equals("B") ? 5L : 0L, 5));
        assertEquals(backend, ModalidadeOrdering.sorted(backend,
                s -> false, s -> false, s -> s.equals("B") ? 5L : 0L, 10));
    }
    @Test public void prioritizesSpecialThenFavoriteThenUsageWithStableBackendTies() {
        List<String> backend = Arrays.asList("zeroA", "used", "favorite", "special", "zeroB", "moreUsed");
        List<String> sorted = ModalidadeOrdering.sorted(backend,
            s -> s.equals("special"), s -> s.equals("favorite"),
            s -> s.equals("used") ? 2L : s.equals("moreUsed") ? 10L : 0L);
        assertEquals(Arrays.asList("special", "favorite", "moreUsed", "used", "zeroA", "zeroB"), sorted);
        assertEquals("zeroA", backend.get(0));
    }
    @Test public void removingFavoriteRestoresBackendOrder() {
        List<String> backend = Arrays.asList("A", "B", "C");
        assertEquals(Arrays.asList("C", "A", "B"), ModalidadeOrdering.sorted(backend, s -> false, s -> s.equals("C"), s -> 0L));
        assertEquals(backend, ModalidadeOrdering.sorted(backend, s -> false, s -> false, s -> 0L));
    }
}
