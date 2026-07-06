package br.gov.caixa.loterias.apostas.model.bo.silce.dto;

import android.content.Context;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.utils.Aplicacao;
import io.swagger.annotations.ApiModel;

@ApiModel(description = "")
public enum ModalidadeEnum implements Serializable {

    @SerializedName("DIA_DE_SORTE")DIA_DE_SORTE,
    @SerializedName("DUPLA_SENA")DUPLA_SENA,
    @SerializedName("LOTECA")LOTECA,
    @SerializedName("LOTOFACIL")LOTOFACIL,
    @SerializedName("LOTOGOL")LOTOGOL,
    @SerializedName("LOTOMANIA")LOTOMANIA,
    @SerializedName("MEGA_SENA")MEGA_SENA,
    @SerializedName("QUINA")QUINA,
    @SerializedName("TIMEMANIA")TIMEMANIA,
    @SerializedName("SUPER_7") SUPER_7,
    @SerializedName("MAIS_MILIONARIA") MAIS_MILIONARIA,
    @SerializedName("INSTANTANEA") INSTANTANEA,
    @SerializedName("COMBO") COMBO,
    @SerializedName("BOLAO") BOLAO;

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
                return "dupla sena";
            case DIA_DE_SORTE:
                return "dia de sorte";
            case SUPER_7:
                return "super sete";
            case MAIS_MILIONARIA:
                return Aplicacao.application.getBaseContext().getString(R.string.label_mais_milionaria);
            case INSTANTANEA:
                return "instantânea";
            case COMBO:
                return "Combos de Apostas";
            case BOLAO:
                return "bolão";
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
            case "super sete":
                return ModalidadeEnum.SUPER_7;
            case "+Milionária":
            case "mais milionária":
            case "maismilionária":
            case "+milionária":
                return MAIS_MILIONARIA;
            case "instantânea":
            case "Instantânea":
                return ModalidadeEnum.INSTANTANEA;
            case "combo":
                return ModalidadeEnum.COMBO;
            case "bolao":
            case "bolão":
                return BOLAO;
            default:
                return null;
        }
    }

    public static ModalidadeEnum fromModalidadeDTO(ModalidadeDTO modalidade) {
        switch (modalidade.getValor()) {
            case 2:
                return ModalidadeEnum.MEGA_SENA;
            case 3:
                return ModalidadeEnum.QUINA;
            case 8:
                return ModalidadeEnum.LOTOFACIL;
            case 9:
                return ModalidadeEnum.MAIS_MILIONARIA;
            case 7:
                return ModalidadeEnum.SUPER_7;
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
            case 1502:
                return ModalidadeEnum.INSTANTANEA;

        }
        return null;
    }

    public static ModalidadeEnum fromInteger( Integer valor) {
        if (valor == null) return null;
        switch (valor) {
            case 2:
                return ModalidadeEnum.MEGA_SENA;
            case 3:
                return ModalidadeEnum.QUINA;
            case 7:
                return ModalidadeEnum.SUPER_7;
            case 8:
                return ModalidadeEnum.LOTOFACIL;
            case 9:
                return ModalidadeEnum.MAIS_MILIONARIA;
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
            case 1502:
                return ModalidadeEnum.INSTANTANEA;

            default:
                return null;
        }
    }

    public static String getDescricao(ModalidadeEnum modalidade) {
        Context context = Aplicacao.application.getBaseContext();
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
            case SUPER_7:
                return "Super Sete";
            case MAIS_MILIONARIA:
                return context.getString(R.string.label_mais_milionaria);
            case INSTANTANEA:
                return "Instantânea";
            case COMBO:
                return "Combo de Apostas";
        }
        return "";
    }

    public static String getDescricaoEspecial(ModalidadeEnum modalidade) {
        switch (modalidade) {
            case QUINA:
                return "Quina de São João";
            case LOTOFACIL:
                return "Lotofácil da Independência";
            case MEGA_SENA:
                return "Mega da Virada";
            case DUPLA_SENA:
                return "Dupla de Páscoa";
            case LOTECA:
                return "Loteca Especial";
            default:
                return getDescricao(modalidade);
        }
    }

    public static String getDescricaoEspecialDuasLinhas(ModalidadeEnum modalidade) {
        switch (modalidade) {
            case QUINA:
                return "Quina de \nSão João";
            case LOTOFACIL:
                return "Lotofácil da \nIndependência";
            case MEGA_SENA:
                return "Mega da \nVirada";
            case DUPLA_SENA:
                return "Dupla de \nPáscoa";
            case LOTECA:
                return "Loteca \nEspecial";
        }
        return "";
    }

    public static Integer fromStringToIdModalidade(ModalidadeEnum modalidade) {
        if (modalidade != null){
            switch (modalidade) {
                case MEGA_SENA:
                    return 2;
                case QUINA:
                    return 3;
                case SUPER_7:
                    return 7;
                case LOTOFACIL:
                    return 8;
                case MAIS_MILIONARIA:
                    return 9;
                case DIA_DE_SORTE:
                    return 11;
                case LOTOGOL:
                    return 14;
                case LOTOMANIA:
                    return 16;
                case DUPLA_SENA:
                    return 18;
                case LOTECA:
                    return 19;
                case TIMEMANIA:
                    return 20;
                case INSTANTANEA:
                    return 1502;
                case COMBO:
                    return 0;
                case BOLAO:
                    return 0;
            }
        }

        return null;
    }
}
