package br.gov.caixa.loterias.apostas.model.bean;

/**
 * Created by cedesbr450 on 05/04/18.
 */

public class MeioPagamento {
    private int meioPagamento;
    private ParametrosPagamento parametrosPagamento;

    public MeioPagamento(int meioPagamento, ParametrosPagamento parametrosPagamento){
        this.parametrosPagamento = parametrosPagamento;
        this.meioPagamento = meioPagamento;
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



}
