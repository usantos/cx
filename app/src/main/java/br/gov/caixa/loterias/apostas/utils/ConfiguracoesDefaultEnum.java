package br.gov.caixa.loterias.apostas.utils;

public enum ConfiguracoesDefaultEnum {

    MOSTRA_MENU_APOSTAS(true),
    IS_MICRO_SERVICO(false),
    BOLAO_HABILITADO(false),
    COMBO_APOSTAS(false),
    IS_ATUALIZA_PUBLIC_KEY(false),
    IS_NOVA_API(false),
    IS_RAPIDAO(false),

    GRUPO_CPF_NOVA_API(0),
    QTDE_MINIMA_APOSTA_BOLAO_FILTRO(0),
    QTDE_MAXIMA_APOSTA_BOLAO_FILTRO(0),

    URL_BASE_BUSCA_APOSTAS(""),
    URL_BASE_CARRINHO_PILOTO(""),
    URL_BASE_CARRINHO_PRODUCAO(""),
    URL_BASE_APOSTADOR(""),
    GRUPO_CPFS_APOSTADOR(""),
    URL_BASE_BFF(""),
    GRUPO_CPFS_BFF("0;");

    private final Boolean boolValue;
    private final Integer intValue;
    private final String stringValue;

    // boolean
    ConfiguracoesDefaultEnum(Boolean value) {
        this.boolValue = value;
        this.intValue = null;
        this.stringValue = null;
    }

    // int
    ConfiguracoesDefaultEnum(Integer value) {
        this.boolValue = null;
        this.intValue = value;
        this.stringValue = null;
    }

    // string
    ConfiguracoesDefaultEnum(String value) {
        this.boolValue = null;
        this.intValue = null;
        this.stringValue = value;
    }

    public Boolean asBoolean() {
        return boolValue;
    }

    public Integer asInt() {
        return intValue;
    }

    public String asString() {
        return stringValue;
    }
}
