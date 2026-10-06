package br.gov.caixa.loterias.apostas.utils.shake;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

public final class ApostaShakeRandom {
    private ApostaShakeRandom() { }

    public static List<Integer> numeros(int quantidade, List<Integer> atual,
            int minimo, int maximo, boolean renovar, Random random) {
        if (quantidade <= 0 || quantidade > maximo - minimo + 1) {
            throw new IllegalArgumentException("Quantidade de números inválida");
        }
        List<Integer> resultado = new ArrayList<>();
        if (!renovar && atual != null) resultado.addAll(atual);
        List<Integer> livres = new ArrayList<>();
        for (int n = minimo; n <= maximo; n++) {
            if (!resultado.contains(n)) livres.add(n);
        }
        Collections.shuffle(livres, random);
        for (Integer numero : livres) {
            if (resultado.size() >= quantidade) break;
            resultado.add(numero);
        }
        if (renovar && mesmosNumeros(resultado, atual) && quantidade < maximo - minimo + 1) {
            List<Integer> alternativas = new ArrayList<>();
            for (int n = minimo; n <= maximo; n++) {
                if (!resultado.contains(n)) alternativas.add(n);
            }
            resultado.set(random.nextInt(resultado.size()), alternativas.get(random.nextInt(alternativas.size())));
        }
        Collections.sort(resultado);
        return resultado;
    }

    public static boolean mesmosNumeros(List<Integer> a, List<Integer> b) {
        return a != null && b != null && new HashSet<>(a).equals(new HashSet<>(b));
    }

    /** Planeja a configuração inteira antes de alterar qualquer partida. */
    public static int[] quantidadesLoteca(int[] atuais, int duplas, int triplas, Random random) {
        if (duplas < 0 || triplas < 0 || duplas + triplas > atuais.length) return null;
        List<Integer> disponiveis = new ArrayList<>();
        for (int i = 0; i < triplas; i++) disponiveis.add(3);
        for (int i = 0; i < duplas; i++) disponiveis.add(2);
        while (disponiveis.size() < atuais.length) disponiveis.add(1);
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < atuais.length; i++) indices.add(i);
        Collections.shuffle(indices, random);
        Collections.sort(indices, (a, b) -> Integer.compare(Integer.bitCount(atuais[b]), Integer.bitCount(atuais[a])));
        int[] resultado = new int[atuais.length];
        for (Integer indice : indices) {
            List<Integer> viaveis = new ArrayList<>();
            for (Integer quantidade : disponiveis) {
                if (quantidade >= Integer.bitCount(atuais[indice])) viaveis.add(quantidade);
            }
            if (viaveis.isEmpty()) return null;
            Integer quantidade = viaveis.get(random.nextInt(viaveis.size()));
            resultado[indice] = quantidade;
            disponiveis.remove(quantidade);
        }
        return resultado;
    }

    /** Troca os resultados de uma partida sem alterar simples/duplo/triplo. */
    public static int outraMascaraLoteca(int atual, Random random) {
        List<Integer> alternativas = new ArrayList<>();
        for (int m = 1; m <= 7; m++) {
            if (m != atual && Integer.bitCount(m) == Integer.bitCount(atual)) alternativas.add(m);
        }
        return alternativas.isEmpty() ? atual : alternativas.get(random.nextInt(alternativas.size()));
    }
}
