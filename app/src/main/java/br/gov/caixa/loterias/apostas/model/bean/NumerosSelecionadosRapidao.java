package br.gov.caixa.loterias.apostas.model.bean;

/**
 * Created by cedesbr450 on 21/12/17.
 */

public class NumerosSelecionadosRapidao {

    private int numero;
    private Boolean selecionado;


    public NumerosSelecionadosRapidao(int numero, Boolean selecionado){
        this.numero = numero;
        this.selecionado = selecionado;
    }


    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public Boolean getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(Boolean selecionado) {
        this.selecionado = selecionado;
    }
}
