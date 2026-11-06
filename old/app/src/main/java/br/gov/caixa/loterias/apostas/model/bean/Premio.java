package br.gov.caixa.loterias.apostas.model.bean;

public class Premio {
    private String descricao;
    private String valor;

    public Premio(String descricao, String valor) {
        this.descricao = descricao;
        this.valor = valor;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }
}
