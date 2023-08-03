package br.gov.caixa.loterias.apostas.controllers;

public class MessagePush {
    private Long id;
    private int icon;
    private int categoria;
    private String titulo;
    private final String conteudo;
    private String data;
    private boolean read;
    private boolean expanded;
    private boolean selected;

    public MessagePush(Long id, int icon, int categoria, String titulo, String conteudo, String data, boolean read) {
        this.id = id;
        this.icon = icon;
        this.categoria = categoria;
        this.titulo = titulo;
        this.conteudo = conteudo;
        this.data = data;
        this.read = read;
        this.expanded = false;
        this.selected = false;
    }

    public Long getId() {
        return id;
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

    public boolean getRead() {
        return read;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isSelected() {
        return selected;
    }
}



