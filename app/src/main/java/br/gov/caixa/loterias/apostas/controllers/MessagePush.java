package br.gov.caixa.loterias.apostas.controllers;

import org.w3c.dom.CDATASection;

public class MessagePush {
    private String titulo;
    private final String conteudo;
    private boolean isRead;

    public MessagePush(String titulo, String conteudo, boolean isRead, CDATASection dia) {
        this.titulo = titulo;
        this.conteudo = conteudo;
        this.isRead = isRead;
        this.dia = dia;
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

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

}
