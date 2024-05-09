package br.gov.caixa.loterias.apostas.model.bo.silce.dto;


import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.io.Serializable;
import java.util.Objects;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * SituacaoReservaCotaBolao
 */
@ApiModel(description = "")
public class SituacaoReservaCotaBolao implements Serializable {
    @SerializedName("id")
    private Long id = null;
    @SerializedName("valor")
    private Integer valor = null;
    @SerializedName("descricao")
    private String descricao = null;

    @ApiModelProperty(value = "")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ApiModelProperty(value = "")
    public Integer getValor() {
        return valor;
    }

    public void setValor(Integer valor) {
        this.valor = valor;
    }

    @ApiModelProperty(value = "")
    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SituacaoReservaCotaBolao situacaoReservaCotaBolao = (SituacaoReservaCotaBolao) o;
        return Objects.equals(this.id, situacaoReservaCotaBolao.id) &&
                Objects.equals(this.valor, situacaoReservaCotaBolao.valor) &&
                Objects.equals(this.descricao, situacaoReservaCotaBolao.descricao);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, valor, descricao);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class SituacaoReservaCotaBolao {\n");

        sb.append("    id: ").append(toIndentedString(id)).append("\n");
        sb.append("    valor: ").append(toIndentedString(valor)).append("\n");
        sb.append("    descricao: ").append(toIndentedString(descricao)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(java.lang.Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }

}

