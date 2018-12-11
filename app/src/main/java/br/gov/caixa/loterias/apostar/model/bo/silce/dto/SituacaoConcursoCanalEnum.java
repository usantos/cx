package br.gov.caixa.loterias.apostar.model.bo.silce.dto;

import com.google.gson.annotations.SerializedName;

import io.swagger.annotations.ApiModel;

@ApiModel(description = "")
public enum SituacaoConcursoCanalEnum {
    @SerializedName("ABERTO") ABERTO,
    @SerializedName("FECHADO") FECHADO,
}
