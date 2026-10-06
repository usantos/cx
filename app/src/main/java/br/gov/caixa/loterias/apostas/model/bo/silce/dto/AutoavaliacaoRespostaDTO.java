package br.gov.caixa.loterias.apostas.model.bo.silce.dto;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

import io.swagger.annotations.ApiModel;

@ApiModel(description = "")
public class AutoavaliacaoRespostaDTO {

    @SerializedName("idPergunta")
    private Integer idPergunta = null;
    @SerializedName("descricaoResposta")
    private String descricaoResposta = null;
    @SerializedName("indicadorResposta")
    private Boolean indicadorResposta = null;

    public AutoavaliacaoRespostaDTO(Integer idPergunta, String descricaoResposta, Boolean indicadorResposta) {
        this.idPergunta = idPergunta;
        this.descricaoResposta = descricaoResposta;
        this.indicadorResposta = indicadorResposta;
    }

    public Integer getIdPergunta() {
        return idPergunta;
    }

    public void setIdPergunta(Integer idPergunta) {
        this.idPergunta = idPergunta;
    }

    public String getDescricaoResposta() {
        return descricaoResposta;
    }

    public void setDescricaoResposta(String descricaoResposta) {
        this.descricaoResposta = descricaoResposta;
    }

    public Boolean getIndicadorResposta() {
        return indicadorResposta;
    }

    public void setIndicadorResposta(Boolean indicadorResposta) {
        this.indicadorResposta = indicadorResposta;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AutoavaliacaoRespostaDTO that = (AutoavaliacaoRespostaDTO) o;
        return Objects.equals(idPergunta, that.idPergunta) && Objects.equals(descricaoResposta, that.descricaoResposta) && Objects.equals(indicadorResposta, that.indicadorResposta);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idPergunta, descricaoResposta, indicadorResposta);
    }

    @Override
    public String toString() {
        return "AutoavaliacaoRespostaDTO{" +
                "idPergunta=" + idPergunta +
                ", descricaoResposta='" + descricaoResposta + '\'' +
                ", indicadorResposta=" + indicadorResposta +
                '}';
    }
}