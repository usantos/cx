package br.gov.caixa.loterias.apostas.utils;

public enum ChavePixEnum {
    CPF("CPF"),
    EMAIL("E-mail"),
    CELULAR("Celular"),
    CHAVE_ALEATORIA("Chave Aleatória");

    private final String texto;

    ChavePixEnum(String texto) {
        this.texto = texto;
    }

    public String getTexto() {
        return texto;
    }
}
