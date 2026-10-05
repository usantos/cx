package br.gov.caixa.loterias.apostas.utils;

public final class NomeLotericaFormatter {
    private NomeLotericaFormatter() {}

    public static String formatar(String nome) {
        if (nome == null || nome.trim().isEmpty()) return "";
        StringBuilder resultado = new StringBuilder();
        int tamanhoLinha = 0;
        for (String palavra : nome.trim().split("\\s+")) {
            int tamanhoPalavra = palavra.codePointCount(0, palavra.length());
            if (tamanhoLinha > 0) {
                if (tamanhoLinha + 1 + tamanhoPalavra > 32) {
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
