package caixa.gov.br.loterias.bo.silce.dto;

import com.google.gson.annotations.SerializedName;

import io.swagger.annotations.ApiModel;

@ApiModel(description = "")
public enum SituacaoApostadorEnum {
    @SerializedName("ATIVO") ATIVO,
    @SerializedName("BLOQUEADO") BLOQUEADO;
}
