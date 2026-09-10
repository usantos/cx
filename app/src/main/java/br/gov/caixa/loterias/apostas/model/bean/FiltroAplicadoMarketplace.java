package br.gov.caixa.loterias.apostas.model.bean;

import java.io.Serializable;
import java.util.Objects;

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
    /***
     * null = não recebeu param (PADRÃO)
     * true = recebeu param, são todas as modalidades
     * false = recebeu param, modalidade específica
    ***/
    private Boolean todasModalidades = null;
    private TipoConcursoEnum tipoConcurso;

    public FiltroAplicadoMarketplace(){
    }

    public void setTodasModalidades(boolean todasModalidades) {
        this.todasModalidades = todasModalidades;
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

    public Integer getQtdDezenas() {
        return qtdDezenas;
    }

    public Integer getQtdApostas() {
        return qtdApostas;
    }

    public void setQtdApostas(Integer qtdApostas) {
        this.qtdApostas = qtdApostas;
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

    public boolean isEqualsTo(FiltroAplicadoMarketplace other) {
        return this.equals(other);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FiltroAplicadoMarketplace)) return false;
        FiltroAplicadoMarketplace that = (FiltroAplicadoMarketplace) o;

        return Objects.equals(valorMinimoAposta, that.valorMinimoAposta)
                && Objects.equals(valorMaximoAposta, that.valorMaximoAposta)
                && Objects.equals(qtdMinCotas, that.qtdMinCotas)
                && Objects.equals(qtdMaxCotas, that.qtdMaxCotas)
                && Objects.equals(qtdApostas, that.qtdApostas)
                && Objects.equals(qtdDezenas, that.qtdDezenas)
                && Objects.equals(numerosQuero, that.numerosQuero)
                && Objects.equals(numerosNaoQuero, that.numerosNaoQuero)
                && Objects.equals(lotericaDTO, that.lotericaDTO)
                && modalidade == that.modalidade
                && tipoConcurso == that.tipoConcurso;
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                valorMinimoAposta,
                valorMaximoAposta,
                qtdMinCotas,
                qtdMaxCotas,
                qtdApostas,
                qtdDezenas,
                numerosQuero,
                numerosNaoQuero,
                lotericaDTO,
                modalidade,
                tipoConcurso
        );
    }

    public Boolean isTodasAsModalidades() {
        return todasModalidades;
    }
}
