package br.gov.caixa.loterias.apostas.utils;

import java.util.ArrayList;
import java.util.List;

public class RowUtils {
    public static <T> List<List<T>> chunkBySpan(List<T> list, int spanCount) {
        List<List<T>> result = new ArrayList<>();

        if (list == null || list.isEmpty() || spanCount <= 0) {
            return result;
        }

        for (int i = 0; i < list.size(); i += spanCount) {
            int end = Math.min(i + spanCount, list.size());
            result.add(new ArrayList<>(list.subList(i, end)));
        }

        return result;
    }
}
