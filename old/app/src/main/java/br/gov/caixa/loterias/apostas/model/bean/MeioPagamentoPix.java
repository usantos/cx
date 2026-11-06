package br.gov.caixa.loterias.apostas.model.bean;

/**
 * Created by cedesbr450 on 05/04/18.
 */

public class MeioPagamentoPix {
    private int meioPagamento;
    private ParametrosPagamento parametrosPagamento;
    private Boolean salvarCarrinhoFavorito;
    private String nomeCarrinhoFavorito;

    public MeioPagamentoPix(int meioPagamento, ParametrosPagamento parametrosPagamento, Boolean salvarCarrinhoFavorito, String nomeCarrinhoFavorito){
        this.parametrosPagamento = parametrosPagamento;
        this.meioPagamento = meioPagamento;
        this.salvarCarrinhoFavorito = salvarCarrinhoFavorito;
        this.nomeCarrinhoFavorito = nomeCarrinhoFavorito;
    }

    public int getMeioPagamento() {
        return meioPagamento;
    }

    public void setMeioPagamento(int meioPagamento) {
        this.meioPagamento = meioPagamento;
    }

    public ParametrosPagamento getParametrosPagamento() {
        return parametrosPagamento;
    }

    public void setParametrosPagamento(ParametrosPagamento parametroPagamento) {
        this.parametrosPagamento = parametroPagamento;
    }

    public Boolean getSalvarCarrinhoFavorito() {
        return salvarCarrinhoFavorito;
    }

    public void setSalvarCarrinhoFavorito(Boolean salvarCarrinhoFavorito) {
        this.salvarCarrinhoFavorito = salvarCarrinhoFavorito;
    }

    public String getNomeCarrinhoFavorito() {
        return nomeCarrinhoFavorito;
    }

    public void setNomeCarrinhoFavorito(String nomeCarrinhoFavorito) {
        this.nomeCarrinhoFavorito = nomeCarrinhoFavorito;
    }
}
