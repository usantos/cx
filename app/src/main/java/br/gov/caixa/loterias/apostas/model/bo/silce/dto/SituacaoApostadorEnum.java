package br.gov.caixa.loterias.apostas.model.bo.silce.dto;

import com.google.gson.annotations.SerializedName;

import io.swagger.annotations.ApiModel;

@ApiModel(description = "")
public enum SituacaoApostadorEnum {
    @SerializedName("ATIVO") ATIVO,
    @SerializedName("BLOQUEADO") BLOQUEADO,
    @SerializedName("BLOQUEADO_TOTAL") BLOQUEADO_TOTAL,
    @SerializedName("BLOQUEADO_PARCIAL") BLOQUEADO_PARCIAL;
}
