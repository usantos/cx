package caixa.gov.br.loterias.model;

import java.math.BigDecimal;

import caixa.gov.br.loterias.bo.silce.dto.ConcursoDTO;
import caixa.gov.br.loterias.utils.TipoModalidadeEnum;

/**
 * Created by brfernan on 27/10/2017.
 */

public class Modalidade {
    private String nome;
    private BigDecimal valor;
    private String descricao;
    private String descricaoEspecial;
    private TipoModalidadeEnum tipoModalidade;
    private ConcursoDTO concurso;

    public Modalidade (String nome, BigDecimal valor, String descricao, String descricaoEspecial, TipoModalidadeEnum tipoModalidade, ConcursoDTO concurso) {
        this.nome = nome;
        this.valor = valor;
        this.descricao = descricao;
        this.descricaoEspecial = descricaoEspecial;
        this.tipoModalidade = tipoModalidade;
        this.concurso = concurso;
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

    public void setTipoModalidade(TipoModalidadeEnum tipoModalidade) {
        this.tipoModalidade = tipoModalidade;
    }

    public TipoModalidadeEnum getTipoModalidade() {
        return tipoModalidade;
    }

    public ConcursoDTO getConcurso() {
        return concurso;
    }

    public void setConcurso(ConcursoDTO concurso) {
        this.concurso = concurso;
    }
}
