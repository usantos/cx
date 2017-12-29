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
    @SerializedName("FEDERAL")FEDERAL,
    @SerializedName("TIMEMANIA")TIMEMANIA;

    public static String fromString(ModalidadeEnum modalidade) {
        switch (modalidade) {
            case QUINA:
                return "quina";
            case LOTECA:
                return "loteca";
            case LOTOGOL:
                return "lotogol";
            case LOTOFACIL:
                return "lotofácil";
            case LOTOMANIA:
                return "lotomania";
            case MEGA_SENA:
                return "mega-sena";
            case TIMEMANIA:
                return "timemania";
            case DUPLA_SENA:
                return "dupla-sena";
            case FEDERAL:
                return "federal";

        }
        return "";
    }


    public static ModalidadeEnum toString(String modalidade) {
        switch (modalidade) {
            case "Quina":
                return ModalidadeEnum.QUINA;
            case "Loteca":
                return ModalidadeEnum.LOTECA;
            case "Lotogol":
                return ModalidadeEnum.LOTOGOL;
            case "Lotofácil":
                return ModalidadeEnum.LOTOFACIL;
            case "Lotomania":
                return ModalidadeEnum.LOTOMANIA;
            case "Mega-Sena":
                return ModalidadeEnum.MEGA_SENA;
            case "Timemania":
                return ModalidadeEnum.TIMEMANIA;
            case "Dupla-Sena":
                return ModalidadeEnum.DUPLA_SENA;
            case "Federal":
                return ModalidadeEnum.FEDERAL;
            default:
                return null;
        }
    }
}
