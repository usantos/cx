package br.gov.caixa.loterias.apostas.model.bean;

import java.io.Serializable;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.LotericaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.TipoConcursoEnum;

public class FiltroAplicadoMarketplace implements Serializable {
    private String valorMinimoAposta;
    private String valorMaximoAposta;
    private Integer qtdMinCotas;
    private Integer qtdMaxCotas;
    private Integer qtdApostas;
    private Integer qtdDezenas;
    private String numerosQuero;
    private String numerosNaoQuero;
    private LotericaDTO lotericaDTO;
    private ModalidadeEnum modalidade;
    private TipoConcursoEnum tipoConcurso;

    public FiltroAplicadoMarketplace(){
    }

    public FiltroAplicadoMarketplace(LotericaDTO lotericaDTO, String valorMinimoAposta, String valorMaximoAposta, Integer qtdMinCotas, Integer qtdMaxCotas, Integer qtdApostas, Integer qtdDezenas, String numerosQuero, String numerosNaoQuero, ModalidadeEnum modalidade, TipoConcursoEnum tipoConcurso) {
        this.valorMinimoAposta = valorMinimoAposta;
        this.valorMaximoAposta = valorMaximoAposta;
        this.qtdMinCotas = qtdMinCotas;
        this.qtdMaxCotas = qtdMaxCotas;
        this.qtdApostas = qtdApostas;
        this.qtdDezenas = qtdDezenas;
        this.numerosQuero = numerosQuero;
        this.numerosNaoQuero = numerosNaoQuero;
        this.lotericaDTO = lotericaDTO;
        this.modalidade = modalidade;
        this.tipoConcurso = tipoConcurso;
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

    public LotericaDTO getLotericaDTO() {
        return lotericaDTO;
    }

    public void setLotericaDTO(LotericaDTO lotericaDTO) {
        this.lotericaDTO = lotericaDTO;
    }

    public Integer getQtdMinCotas() {
        return qtdMinCotas;
    }

    public void setQtdMinCotas(Integer qtdMinCotas) {
        this.qtdMinCotas = qtdMinCotas;
    }

    public Integer getQtdMaxCotas() {
        return qtdMaxCotas;
    }

    public void setQtdMaxCotas(Integer qtdMaxCotas) {
        this.qtdMaxCotas = qtdMaxCotas;
    }

    public String getNumerosQuero() {
        return numerosQuero;
    }

    public void setNumerosQuero(String numerosQuero) {
        this.numerosQuero = numerosQuero;
    }

    public String getNumerosNaoQuero() {
        return numerosNaoQuero;
    }

    public void setNumerosNaoQuero(String numerosNaoQuero) {
        this.numerosNaoQuero = numerosNaoQuero;
    }

    public Integer getQtdApostas() {
        return qtdApostas;
    }

    public void setQtdApostas(Integer qtdApostas) {
        this.qtdApostas = qtdApostas;
    }

    public Integer getQtdDezenas() {
        return qtdDezenas;
    }

    public void setQtdDezenas(Integer qtdDezenas) {
        this.qtdDezenas = qtdDezenas;
    }

    public ModalidadeEnum getModalidade() {
        return modalidade;
    }

    public void setModalidade(ModalidadeEnum modalidade) {
        this.modalidade = modalidade;
    }

    public TipoConcursoEnum getTipoConcurso() {
        return tipoConcurso;
    }

    public void setTipoConcurso(TipoConcursoEnum tipoConcurso) {
        this.tipoConcurso = tipoConcurso;
    }
}
