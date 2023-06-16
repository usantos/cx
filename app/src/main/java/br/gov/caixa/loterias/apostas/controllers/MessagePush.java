package br.gov.caixa.loterias.apostas.controllers;

public class MessagePush {
    private String titulo;
    private final String conteudo;
    private String data;
    private boolean isRead;

    public MessagePush(String titulo, String conteudo, String data, boolean isRead) {
        this.titulo = titulo;
        this.conteudo = conteudo;
        this.isRead = isRead;
        this.data = data;
    }

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

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

}
