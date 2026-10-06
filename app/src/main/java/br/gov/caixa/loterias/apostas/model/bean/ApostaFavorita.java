package br.gov.caixa.loterias.apostas.model.bean;

/**
 * Created by cedesbr450 on 20/12/17.
 */

public class ApostaFavorita {
    private String titulo;
    private int[] listaNumeros;

    public ApostaFavorita(String titulo, int[] listaNumeros){
        this.titulo = titulo;
        this.listaNumeros = listaNumeros;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public int[] getListaNumeros() {
        return listaNumeros;
    }

    public void setListaNumeros(int[] listaNumeros) {
        this.listaNumeros = listaNumeros;
    }


}
