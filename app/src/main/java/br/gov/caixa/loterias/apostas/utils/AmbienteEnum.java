package br.gov.caixa.loterias.apostas.utils;

public enum AmbienteEnum {
    EXTERNO("TQS Externo"),
    INTERNO("TQS Interno"),
    INTERNO_ESTEIRA("TQS Interno Esteira"),
    EXTERNO_ESTEIRA("TQS Externo Esteira"),
    HMP("Hmp"),
    PILOTO("Piloto");

    private final String texto;

    AmbienteEnum(String texto) {
        this.texto = texto;
    }

    public String getTexto() {
        return texto;
    }
}
