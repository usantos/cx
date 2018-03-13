package caixa.gov.br.loterias.model.bo.silce.dto;

import com.google.gson.annotations.SerializedName;

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
            case "quina":
                return ModalidadeEnum.QUINA;
            case "loteca":
                return ModalidadeEnum.LOTECA;
            case "lotogol":
                return ModalidadeEnum.LOTOGOL;
            case "lotofácil":
                return ModalidadeEnum.LOTOFACIL;
            case "lotomania":
                return ModalidadeEnum.LOTOMANIA;
            case "mega-sena":
                return ModalidadeEnum.MEGA_SENA;
            case "timemania":
                return ModalidadeEnum.TIMEMANIA;
            case "dupla-sena":
                return ModalidadeEnum.DUPLA_SENA;
            case "federal":
                return ModalidadeEnum.FEDERAL;
            default:
                return null;
        }
    }
}
