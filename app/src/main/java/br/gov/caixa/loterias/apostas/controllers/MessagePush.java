package br.gov.caixa.loterias.apostas.controllers;

public class MessagePush {
    private int icon;
    private int categoria;
    private String titulo;
    private final String conteudo;
    private String data;
    private boolean isRead;

    public MessagePush(int icon, int categoria, String titulo, String conteudo, String data, boolean isRead) {
        this.icon = icon;
        this.categoria = categoria;
        this.titulo = titulo;
        this.conteudo = conteudo;
        this.data = data;
        this.isRead = false;
    }
    public int getIcon() { return icon; }

    public int getCategoria() {
        return categoria;
    }

    public void setCategoria() { this.categoria = categoria; }

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
