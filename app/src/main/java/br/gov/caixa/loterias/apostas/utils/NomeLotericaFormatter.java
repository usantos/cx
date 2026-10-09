package br.gov.caixa.loterias.apostas.utils;

public final class NomeLotericaFormatter {
    private NomeLotericaFormatter() {}

    public static String formatar(String nome) {
        return formatar(nome, 32);
    }

    public static String formatar(String nome, int limiteCaracteres) {
        if (limiteCaracteres < 1) throw new IllegalArgumentException("O limite deve ser positivo");
        if (nome == null || nome.trim().isEmpty()) return "";
        StringBuilder resultado = new StringBuilder();
        int tamanhoLinha = 0;
        for (String palavra : nome.trim().split("\\s+")) {
            int tamanhoPalavra = palavra.codePointCount(0, palavra.length());
            if (tamanhoLinha > 0) {
                if (tamanhoLinha + 1 + tamanhoPalavra > limiteCaracteres) {
                    resultado.append('\n');
                    tamanhoLinha = 0;
                } else {
                    resultado.append(' ');
                    tamanhoLinha++;
                }
            }
            resultado.append(palavra);
            tamanhoLinha += tamanhoPalavra;
        }
        return resultado.toString();
    }
}
