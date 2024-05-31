package br.gov.caixa.loterias.apostas.model.bean;

import java.io.Serializable;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.LotericaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;

public class FiltroAplicadoMarketplace implements Serializable {
    private String valorMinimoAposta;
    private String valorMaximoAposta;
    private Float qtdMinCotas;
    private Float qtdMaxCotas;
    private LotericaDTO lotericaDTO;
    private ModalidadeEnum modalidade;

    public FiltroAplicadoMarketplace(){
    }

    public FiltroAplicadoMarketplace(String valorMinimoAposta, String valorMaximoAposta, Float qtdMinCotas, Float qtdMaxCotas, LotericaDTO lotericaDTO, ModalidadeEnum modalidade) {
        this.valorMinimoAposta = valorMinimoAposta;
        this.valorMaximoAposta = valorMaximoAposta;
        this.qtdMinCotas = qtdMinCotas;
        this.qtdMaxCotas = qtdMaxCotas;
        this.lotericaDTO = lotericaDTO;
        this.modalidade = modalidade;
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

    public ModalidadeEnum getModalidade() {
        return modalidade;
    }

    public void setModalidade(ModalidadeEnum modalidade) {
        this.modalidade = modalidade;
    }
}