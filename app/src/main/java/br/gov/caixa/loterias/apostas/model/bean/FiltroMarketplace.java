package br.gov.caixa.loterias.apostas.model.bean;

import java.io.Serializable;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.LotericaDTO;

public class FiltroMarketplace implements Serializable {
    private String valorMinimoAposta;
    private String valorMaximoAposta;
    private Float qtdMinCotas;
    private Float qtdMaxCotas;
    private LotericaDTO lotericaDTO;

    public FiltroMarketplace(){
    }

    public FiltroMarketplace(String valorMinimoAposta, String valorMaximoAposta, Float qtdMinCotas, Float qtdMaxCotas, LotericaDTO lotericaDTO) {
        this.valorMinimoAposta = valorMinimoAposta;
        this.valorMaximoAposta = valorMaximoAposta;
        this.qtdMinCotas = qtdMinCotas;
        this.qtdMaxCotas = qtdMaxCotas;
        this.lotericaDTO = lotericaDTO;
    }

    public String getValorMinimoAposta() {
        return valorMinimoAposta;
    }

    public void setValorMinimoAposta(String valorMinimoAposta) {
        this.valorMinimoAposta = valorMinimoAposta;
    }

    public String getValorMaximoAposta() {
        return valorMaximoAposta;
    }

    public void setValorMaximoAposta(String valorMaximoAposta) {
        this.valorMaximoAposta = valorMaximoAposta;
    }

    public Float getQtdMinCotas() {
        return qtdMinCotas;
    }

    public void setQtdMinCotas(Float qtdMinCotas) {
        this.qtdMinCotas = qtdMinCotas;
    }

    public Float getQtdMaxCotas() {
        return qtdMaxCotas;
    }

    public void setQtdMaxCotas(Float qtdMaxCotas) {
        this.qtdMaxCotas = qtdMaxCotas;
    }

    public LotericaDTO getLotericaDTO() {
        return lotericaDTO;
    }

    public void setLotericaDTO(LotericaDTO lotericaDTO) {
        this.lotericaDTO = lotericaDTO;
    }
}