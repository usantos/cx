
package br.gov.caixa.loterias.apostas.model.enums;

public enum TipoConsultaBolaoEnum {
    MUNICIPIO(0),
    UF(1),
    NACIONAL(2),
    LOTERICO(3);

    private int valor;

    TipoConsultaBolaoEnum(int valor){
        this.valor = valor;
    }

    public int getValor() {
        return valor;
    }
}
