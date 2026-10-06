package br.gov.caixa.loterias.apostas.model.bean;

import java.util.List;

public class ResultadoAposta {
    private String concurso;
    private List<Premio> premios;

    public ResultadoAposta(String contest, List<Premio> rewards) {
        this.concurso = contest;
        this.premios = rewards;
    }

    public String getConcurso() {
        return concurso;
    }

    public void setConcurso(String concurso) {
        this.concurso = concurso;
    }

    public List<Premio> getPremios() {
        return premios;
    }

    public void setPremios(List<Premio> premios) {
        this.premios = premios;
    }
}
