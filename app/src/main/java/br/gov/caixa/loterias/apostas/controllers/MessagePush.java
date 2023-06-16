package br.gov.caixa.loterias.apostas.controllers;

public class MessagePush {
    private int icon;
    private String titulo;
    private final String conteudo;
    private String data;

    public MessagePush(int icon, String titulo, String conteudo, String data) {
        this.icon = icon;
        this.titulo = titulo;
        this.conteudo = conteudo;
        this.data = data;
    }
    public int getIcon() { return icon; }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getConteudo() {
        return conteudo;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

}
