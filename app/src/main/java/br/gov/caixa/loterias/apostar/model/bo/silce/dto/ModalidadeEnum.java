package br.gov.caixa.loterias.apostar.model.bo.silce.dto;

import android.content.Intent;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import br.gov.caixa.loterias.apostar.model.bean.Modalidade;
import io.swagger.annotations.ApiModel;

@ApiModel(description = "")
@Parcel
public enum ModalidadeEnum {

    @SerializedName("DIA_DE_SORTE")DIA_DE_SORTE,
    @SerializedName("DUPLA_SENA")DUPLA_SENA,
    @SerializedName("LOTECA")LOTECA,
    @SerializedName("LOTOFACIL")LOTOFACIL,
    @SerializedName("LOTOGOL")LOTOGOL,
    @SerializedName("LOTOMANIA")LOTOMANIA,
    @SerializedName("MEGA_SENA")MEGA_SENA,
    @SerializedName("QUINA")QUINA,
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
            case DIA_DE_SORTE:
                return "dia de sorte";

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
            case "dia de sorte":
                return ModalidadeEnum.DIA_DE_SORTE;
            case "dupla sena":
                return ModalidadeEnum.DUPLA_SENA;
            default:
                return null;
        }
    }

    public static String fromStringComprovante(ModalidadeEnum modalidade) {
        switch (modalidade) {
            case QUINA:
                return "Quina";
            case LOTECA:
                return "Loteca";
            case LOTOGOL:
                return "Lotogol";
            case LOTOFACIL:
                return "Lotofácil";
            case LOTOMANIA:
                return "Lotomania";
            case MEGA_SENA:
                return "Mega Sena";
            case TIMEMANIA:
                return "Timemania";
            case DUPLA_SENA:
                return "Dupla Sena";
            case DIA_DE_SORTE:
                return "Dia de Sorte";

        }
        return "";
    }


    public static ModalidadeEnum fromInteger( Integer valor) {
        switch (valor) {
            case 2:
                return ModalidadeEnum.MEGA_SENA;
            case 3:
                return ModalidadeEnum.QUINA;
            case 8:
                return ModalidadeEnum.LOTOFACIL;
            case 11:
                return ModalidadeEnum.DIA_DE_SORTE;
            case 14:
                return ModalidadeEnum.LOTOGOL;
            case 16:
                return ModalidadeEnum.LOTOMANIA;
            case 18:
                return ModalidadeEnum.DUPLA_SENA;
            case 19:
                return ModalidadeEnum.LOTECA;
            case 20:
                return ModalidadeEnum.TIMEMANIA;
//            case 1234567:
//                return ModalidadeEnum.FEDERAL;
            default:
                return null;
        }
    }
}
