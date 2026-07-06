package br.gov.caixa.loterias.apostas.utils;

public enum ConfiguracoesEnum {

    URL_BASE_BUSCA_APOSTAS("urlBaseBuscaApostas"),
    IS_MICRO_SERVICO("isMicroServico"),
    MOSTRA_MENU_APOSTA("mostraMenuAposta"),
    IS_ATUALIZA_PUBLIC_KEY("isAtualizaPublicKey"),
    BOLAO("isBolaoHabilitado"),
    COMBO_APOSTAS("isComboApostasHabilitado"),
    GRUPO_CPF_NOVA_API("grupoCpfNovaApi"),
    IS_NOVA_API("isNovaApi"),
    IS_RAPIDAO("isRapidaoHabilitado"),
    URL_BASE_CARRINHO_PILOTO("urlBaseCarrinhoPiloto"),
    URL_BASE_CARRINHO_PRODUCAO("urlBaseCarrinhoProducao"),
    GRUPO_CPFS_AZURE("grupoDeCpfsQueAcessamOServicoDaAzure"),
    URL_BASE_APOSTADOR("urlBaseApostador"),
    GRUPO_CPFS_APOSTADOR("grupoDeCpfsApostador");

    private final String key;

    ConfiguracoesEnum(String key) {
        this.key = key;
    }

    public String get() {
        return key;
    }

}
