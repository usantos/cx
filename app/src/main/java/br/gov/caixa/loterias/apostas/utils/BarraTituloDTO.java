package br.gov.caixa.loterias.apostas.utils;

import java.io.Serializable;

public class BarraTituloDTO implements Serializable {
    private String numeroConcurso;
    private String dataSorteio;
    private boolean isEspecial;

    @Override
    public String toString() {
        return "BarraTituloModel{" +
                "numeroConcurso='" + numeroConcurso + '\'' +
                ", dataSorteio='" + dataSorteio + '\'' +
                ", isEspecial=" + isEspecial +
                '}';
    }

    public String getNumeroConcurso() {
        return numeroConcurso;
    }

    public void setNumeroConcurso(String numeroConcurso) {
        this.numeroConcurso = numeroConcurso;
    }

    public String getDataSorteio() {
        return dataSorteio;
    }

    public void setDataSorteio(String dataSorteio) {
        this.dataSorteio = dataSorteio;
    }

    public boolean isEspecial() {
        return isEspecial;
    }

    public void setEspecial(boolean especial) {
        isEspecial = especial;
    }
}
