package br.gov.caixa.loterias.apostas.model.bo.silce.dto;

/**
 * Created by joafilho on 23/01/2018.
 */

public class DezenaLotogol {

    private Integer backGround;
    private String valor;

    public DezenaLotogol(Integer backGround, String valor) {
        this.backGround = backGround;
        this.valor = valor;
    }

    public Integer getBackGround() {
        return backGround;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }
}
