package br.gov.caixa.loterias.apostas.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Stable ordering: ties retain the original backend order. */
public final class ModalidadeOrdering {
    private ModalidadeOrdering() {}

    public interface Condition<T> { boolean test(T item); }
    public interface Usage<T> { long count(T item); }

    public static <T> List<T> sorted(List<T> backend, Condition<T> special,
                                    Condition<T> favorite, Usage<T> usage) {
        return sorted(backend, special, favorite, usage, 0);
    }

    public static <T> List<T> sorted(List<T> backend, Condition<T> special,
                                    Condition<T> favorite, Usage<T> usage, int minimumUsage) {
        List<T> result = new ArrayList<>(backend);
        Collections.sort(result, (left, right) -> {
            int leftGroup = special.test(left) ? 0 : favorite.test(left) ? 1 : 2;
            int rightGroup = special.test(right) ? 0 : favorite.test(right) ? 1 : 2;
            int priority = Integer.compare(leftGroup, rightGroup);
            long leftCount = usage.count(left);
            long rightCount = usage.count(right);
            long leftUsage = leftCount >= minimumUsage ? leftCount : 0;
            long rightUsage = rightCount >= minimumUsage ? rightCount : 0;
            return priority != 0 ? priority : Long.compare(rightUsage, leftUsage);
        });
        return result;
    }
}
