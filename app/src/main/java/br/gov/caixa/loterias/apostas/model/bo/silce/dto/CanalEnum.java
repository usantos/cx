package br.gov.caixa.loterias.apostas.model.bo.silce.dto;

import com.google.gson.annotations.SerializedName;

import io.swagger.annotations.ApiModel;

@ApiModel(description = "")
public enum CanalEnum {
    @SerializedName("SILCE") SILCE,
    @SerializedName("SISPL") SISPL,
    @SerializedName("IBC") IBC,
    @SerializedName("SIGEL") SIGEL;
}
