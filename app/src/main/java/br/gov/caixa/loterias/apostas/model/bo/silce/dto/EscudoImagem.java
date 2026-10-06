package br.gov.caixa.loterias.apostas.model.bo.silce.dto;

import android.graphics.Bitmap;

public class EscudoImagem {

    private Integer id;
    private Integer equipeId;
    private Bitmap imagem;

    public EscudoImagem() {
    }

    public EscudoImagem(Integer id, Integer equipeId, Bitmap imagem) {
        this.id = id;
        this.equipeId = equipeId;
        this.imagem = imagem;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getEquipeId() {
        return equipeId;
    }

    public void setEquipeId(Integer equipeId) {
        this.equipeId = equipeId;
    }

    public Bitmap getImagem() {
        return imagem;
    }

    public void setImagem(Bitmap imagem) {
        this.imagem = imagem;
    }
}
