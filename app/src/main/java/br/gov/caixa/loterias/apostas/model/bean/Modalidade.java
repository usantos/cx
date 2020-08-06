package br.gov.caixa.loterias.apostas.model.bean;

import java.math.BigDecimal;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ConcursoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResultadoConcursoDTO;

/**
 * Created by brfernan on 27/10/2017.
 */

public class Modalidade {
    private String nome;
    private BigDecimal valor;
    private String descricao;
    private String descricaoEspecial;
    private ModalidadeEnum tipoModalidade;
    private ConcursoDTO concurso;
    private Boolean resultadosAberto;
    private ResultadoConcursoDTO resultadoConcursoDTO;

    public Modalidade (String nome, BigDecimal valor, String descricao, String descricaoEspecial, ModalidadeEnum tipoModalidade, ConcursoDTO concurso, Boolean resultadosAberto, ResultadoConcursoDTO resultadoConcursoDTO) {
        this.nome = nome;
        this.valor = valor;
        this.descricao = descricao;
        this.descricaoEspecial = descricaoEspecial;
        this.tipoModalidade = tipoModalidade;
        this.concurso = concurso;
        this.resultadosAberto = resultadosAberto;
        this.resultadoConcursoDTO = resultadoConcursoDTO;

    }
    public Modalidade (ModalidadeEnum modalidadeEnum) {
        this.nome = modalidadeEnum.name();

    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricaoEspecial(String descricaoEspecial) {
        this.descricaoEspecial = descricaoEspecial;
    }

    public String getDescricaoEspecial() {
        return descricaoEspecial;
    }

    public void setTipoModalidade(ModalidadeEnum tipoModalidade) {
        this.tipoModalidade = tipoModalidade;
    }

    public ModalidadeEnum getTipoModalidade() {
        return tipoModalidade;
    }

    public ConcursoDTO getConcurso() {
        return concurso;
    }

    public void setConcurso(ConcursoDTO concurso) {
        this.concurso = concurso;
    }

    public ResultadoConcursoDTO getResultadoConcursoDTO() {
        return resultadoConcursoDTO;
    }

    public void setResultadoConcursoDTO(ResultadoConcursoDTO resultadoConcursoDTO) {
        this.resultadoConcursoDTO = resultadoConcursoDTO;
    }

    public Boolean getResultadosAberto() {
        return resultadosAberto;
    }

    public void setResultadosAberto(Boolean resultadosAberto) {
        this.resultadosAberto = resultadosAberto;
    }

}
