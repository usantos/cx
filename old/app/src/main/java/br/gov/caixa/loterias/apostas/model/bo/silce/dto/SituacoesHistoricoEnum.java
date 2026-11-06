package br.gov.caixa.loterias.apostas.model.bo.silce.dto;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;

/**
 * Created by cedesbr450 on 26/04/18.
 */

@ApiModel(description = "")
public enum SituacoesHistoricoEnum implements Serializable {
    @SerializedName("TODAS")TODAS,
    @SerializedName("CONCURSO_PRESCRITO")CONCURSO_PRESCRITO,
    @SerializedName("PREMIO_PAGO")PREMIO_PAGO;




    public static Long toLong(SituacoesHistoricoEnum situacoesEnum) {
        switch (situacoesEnum){
            case TODAS:
                return Long.parseLong( "1" );
            case CONCURSO_PRESCRITO:
                return Long.parseLong( "2" );
            case PREMIO_PAGO:
                return Long.parseLong( "3" );
            default:
                return null;
        }
    }

}