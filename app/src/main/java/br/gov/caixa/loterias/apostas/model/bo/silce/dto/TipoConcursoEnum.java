package br.gov.caixa.loterias.apostas.model.bo.silce.dto;

import com.google.gson.annotations.SerializedName;

import io.swagger.annotations.ApiModel;

@ApiModel(description = "")
public enum TipoConcursoEnum {
    @SerializedName("NORMAL") NORMAL,
    @SerializedName("ESPECIAL") ESPECIAL;

    public static Integer fromStringToIdTipoConcurso(TipoConcursoEnum tipoConcursoEnum) {
        switch (tipoConcursoEnum) {
            case NORMAL:
                return 1;
            case ESPECIAL:
                return 2;
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
}
