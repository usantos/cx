package br.gov.caixa.loterias.apostas.model.bo.silce.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import io.swagger.annotations.ApiModel;

@ApiModel(description = "")
public class PremiacaoMesDeSorteDTO implements Serializable {

    protected Integer quantidadeGanhadores;
    protected BigDecimal valor;
    protected ParametroMesDeSorte mesDeSorte;

    public PremiacaoMesDeSorteDTO() {
    }

    public Integer getQuantidadeGanhadores() {
        return quantidadeGanhadores;
    }

    public void setQuantidadeGanhadores(Integer quantidadeGanhadores) {
        this.quantidadeGanhadores = quantidadeGanhadores;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public ParametroMesDeSorte getMesDeSorte() {
        return mesDeSorte;
    }

    public void setMesDeSorte(ParametroMesDeSorte mesDeSorte) {
        this.mesDeSorte = mesDeSorte;
    }
}
