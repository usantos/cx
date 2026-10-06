package br.gov.caixa.loterias.apostas.model.bo.silce.dto;

import com.google.gson.annotations.SerializedName;

import io.swagger.annotations.ApiModel;

@ApiModel(description = "")
public enum SituacaoEnum {
    @SerializedName("ABERTO") ABERTO,
    @SerializedName("ENCERRADO") ENCERRADO,
    @SerializedName("APURADO") APURADO,
    @SerializedName("HOMOLOGADO") HOMOLOGADO,
    @SerializedName("CONTABILIZADO") CONTABILIZADO,
    @SerializedName("PRESCRITO") PRESCRITO,
    @SerializedName("EM_APURACAO") EM_APURACAO,
    @SerializedName("PRESCRITO_CONTABILIZADO") PRESCRITO_CONTABILIZADO,
    @SerializedName("CANCELADO") CANCELADO,
    @SerializedName("NAO_INICIALIZADO") NAO_INICIALIZADO;
}
