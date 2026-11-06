package br.gov.caixa.loterias.apostas.model.bo.silce.dto;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "")
public class AutoavaliacaoPerguntaDTO {

    @SerializedName("id")
    private Integer id = null;
    @SerializedName("descricaoPergunta")
    private String descricaoPergunta = null;
    @SerializedName("ordemPergunta")
    private Integer ordemPergunta = null;
    @SerializedName("ativo")
    private Boolean ativo = null;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescricaoPergunta() {
        return descricaoPergunta;
    }

    public void setDescricaoPergunta(String descricaoPergunta) {
        this.descricaoPergunta = descricaoPergunta;
    }

    public Integer getOrdemPergunta() {
        return ordemPergunta;
    }

    public void setOrdemPergunta(Integer ordemPergunta) {
        this.ordemPergunta = ordemPergunta;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AutoavaliacaoPerguntaDTO that = (AutoavaliacaoPerguntaDTO) o;
        return Objects.equals(id, that.id) && Objects.equals(descricaoPergunta, that.descricaoPergunta) && Objects.equals(ordemPergunta, that.ordemPergunta) && Objects.equals(ativo, that.ativo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, descricaoPergunta, ordemPergunta, ativo);
    }

    @Override
    public String toString() {
        return "AutoavaliacaoPerguntaDTO{" +
                "id=" + id +
                ", descricaoPergunta='" + descricaoPergunta + '\'' +
                ", ordemPergunta=" + ordemPergunta +
                ", ativo=" + ativo +
                '}';
    }
}