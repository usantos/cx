package br.gov.caixa.loterias.apostas.utils;

public enum DrawerEnum {
    MENU_MINHA_AREA("Minha área"),
    MENU_DADOS_PESSOAIS("Dados pessoais"),
    MENU_MINHAS_APOSTAS("Minhas apostas"),
    MENU_COMPRAS("Minhas compras"),
    MENU_FAVORITAS("Apostas favoritas"),
    MENU_CARRINHOS_FAVORITOS("Carrinhos favoritos"),
    MENU_MEUS_CARTOES("Meus cartões"),
    MENU_RESULTADOS("Resultados"),
    MENU_RAPIDAO("Rapidão"),
    MENU_COFERIR_BILHETE("Conferir bilhetes"),
    MENU_JOGO_RESPONSAVEL("Jogo responsável"),
    MENU_JOGO_RESPONSAVEL_POLITICA("Política de jogo responsável"),
    MENU_JOGO_RESPONSAVEL_AVALIACAO("Autoavaliação do apostador"),
    MENU_JOGO_RESPONSAVEL_SUSPENSAO("Suspensão temporária"),
    MENU_REPASSES_SOCIAIS("Repasses sociais"),
    MENU_TERMO_USO("Termo de uso"),
    MENU_DUVIDAS("Dúvidas"),
    MENU_SOBRE_CAIXA("Sobre a CAIXA Loterias");

    private final String texto;

    DrawerEnum(String texto) {
        this.texto = texto;
    }

    public String toString() {
        return texto;
    }
}
