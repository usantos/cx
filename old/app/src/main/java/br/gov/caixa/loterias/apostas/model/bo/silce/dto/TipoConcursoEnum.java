package br.gov.caixa.loterias.apostas.model.bo.silce.dto;

import com.google.gson.annotations.SerializedName;

import io.swagger.annotations.ApiModel;

@ApiModel(description = "")
public enum TipoConcursoEnum {
    @SerializedName("NORMAL") NORMAL,
    @SerializedName("ESPECIAL") ESPECIAL;

    public static Integer fromStringToIdTipoConcurso(TipoConcursoEnum tipoConcursoEnum) {
        if (tipoConcursoEnum != null){
            switch (tipoConcursoEnum) {
                case NORMAL:
                    return 1;
                case ESPECIAL:
                    return 2;
            }
        }

        return null;
    }

    public Integer fromStringToIdTipoConcurso() {
        switch (this) {
            case NORMAL:
                return 1;
            case ESPECIAL:
                return 2;
        }
        return null;
    }

    public String getValor() {
        switch (this) {
            case NORMAL:
                return "1";
            case ESPECIAL:
                return "2";
        }
        return null;
    }

    public static TipoConcursoEnum fromInteger( Integer valor) {
        switch (valor) {
            case 1:
                return TipoConcursoEnum.NORMAL;
            case 2:
                return TipoConcursoEnum.ESPECIAL;

            default:
                return null;
        }
    }
}
