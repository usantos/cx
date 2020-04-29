package br.gov.caixa.loterias.apostas.mock;

/**
 * Created by joafilho on 16/03/2018.
 */

public class ItemListaFaixaPremiacaoMock {

    private String concurso;
    private String faixaPremiacao;
    private String sorteio;
    private String premiacao;

    public ItemListaFaixaPremiacaoMock() {
    }

    public String getConcurso() {
        return concurso;
    }

    public void setConcurso(String concurso) {
        this.concurso = concurso;
    }

    public String getFaixaPremiacao() {
        return faixaPremiacao;
    }

    public void setFaixaPremiacao(String faixaPremiacao) {
        this.faixaPremiacao = faixaPremiacao;
    }

    public String getSorteio() {
        return sorteio;
    }

    public void setSorteio(String sorteio) {
        this.sorteio = sorteio;
    }

    public String getPremiacao() {
        return premiacao;
    }

    public void setPremiacao(String premiacao) {
        this.premiacao = premiacao;
    }
}
