package caixa.gov.br.loterias.bo.silce.dto;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;

import caixa.gov.br.loterias.bo.silce.gson.EnumByNameTypeAdapterFactory;
import io.swagger.annotations.ApiModel;

@ApiModel(description = "")
public enum ModalidadeEnum {
    @SerializedName("DUPLA_SENA")DUPLA_SENA,
    @SerializedName("LOTECA")LOTECA,
    @SerializedName("LOTOFACIL")LOTOFACIL,
    @SerializedName("LOTOGOL")LOTOGOL,
    @SerializedName("LOTOMANIA")LOTOMANIA,
    @SerializedName("MEGA_SENA")MEGA_SENA,
    @SerializedName("QUINA")QUINA,
    @SerializedName("TIMEMANIA")TIMEMANIA,
}
