package br.gov.caixa.loterias.apostas.model.enums;

public enum TipoConsultaFiltroApostasEnum {

    /* Para surpresinha/teimosinha/combo -
    Se passar 1 quer dizer que vc que ver todas as apostas com ou sem surpresinha/teimosinha/combo
    se passar 2 quer dizer que ver apostas sem surpresinha/teimosinha/combo
    se passar 3 quer dizer que ver apostas com surpresinha/teimosinha/combo
    por ser um botão usamos so o 1 ou 3 mas ja deixamos ali o 2 para casos futuros se gestor mudar a forma como deve funcionar ali o filtro.*/


    TODAS(1),
    NAO_SELECIONADO(2),
    SELECIONADO(3);

    private int valor;

    TipoConsultaFiltroApostasEnum(int valor){
        this.valor = valor;
    }

    public int getValor() {
        return valor;
    }
}
