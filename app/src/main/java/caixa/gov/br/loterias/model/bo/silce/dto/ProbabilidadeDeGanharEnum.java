package caixa.gov.br.loterias.model.bo.silce.dto;

import com.google.gson.annotations.SerializedName;

public enum ProbabilidadeDeGanharEnum {
    NULL,
    @SerializedName("1")UM,
    @SerializedName("2")DOIS,
    @SerializedName("3")TRES,
    @SerializedName("4")QUATRO,
    @SerializedName("5")CINCO
}
