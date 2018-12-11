package br.gov.caixa.loterias.apostar.model.bo.silce.dto;

import com.google.gson.annotations.SerializedName;

import io.swagger.annotations.ApiModel;

@ApiModel(description = "")
public enum TipoConcursoEnum {
    @SerializedName("NORMAL") NORMAL,
    @SerializedName("ESPECIAL") ESPECIAL;
}
