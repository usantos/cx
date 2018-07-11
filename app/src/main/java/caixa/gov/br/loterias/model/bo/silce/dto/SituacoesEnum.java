package caixa.gov.br.loterias.model.bo.silce.dto;

/**
 * Created by cedesbr450 on 25/04/18.
 */


import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import io.swagger.annotations.ApiModel;

@ApiModel(description = "")
@Parcel
public enum SituacoesEnum {
    @SerializedName("NAO_REGISTRADA")NAO_REGISTRADA,
    @SerializedName("EM_PROCESSAMENTO")EM_PROCESSAMENTO,
    @SerializedName("EM_PROCESSAMENTO_ENVIADA_SISPL")EM_PROCESSAMENTO_ENVIADA_SISPL,
    @SerializedName("EFETIVADA")EFETIVADA,
    @SerializedName("NAO_EFETIVADA_DINHEIRO_EM_DEVOLUCAO")NAO_EFETIVADA_DINHEIRO_EM_DEVOLUCAO,
    @SerializedName("NAO_EFETIVADA_DINHEIRO_DEVOLVIDO")NAO_EFETIVADA_DINHEIRO_DEVOLVIDO,
    @SerializedName("PAGAMENTO_EM_PROCESSAMENTO")PAGAMENTO_EM_PROCESSAMENTO,
    @SerializedName("CANCELADA")CANCELADA,
    @SerializedName("NAO_PREMIADA")NAO_PREMIADA,
    @SerializedName("PRESCRITA")PRESCRITA,
    @SerializedName("PREMIO_PAGO")PREMIO_PAGO,
    @SerializedName("PREMIADA")PREMIADA,
    @SerializedName("NAO_SROTEADA")NAO_SROTEADA,
    @SerializedName("NAO_SORTEADA_AINDA_CONCORRENDO")NAO_SORTEADA_AINDA_CONCORRENDO;



    public static Long toLong(SituacoesEnum situacoesEnum) {
        switch (situacoesEnum){
            case NAO_REGISTRADA:
                return Long.parseLong( "1" );
            case EM_PROCESSAMENTO:
                return Long.parseLong( "2" );
            case EM_PROCESSAMENTO_ENVIADA_SISPL:
                return Long.parseLong( "3" );
            case EFETIVADA:
                return Long.parseLong( "4" );
            case NAO_EFETIVADA_DINHEIRO_EM_DEVOLUCAO:
                return Long.parseLong( "5" );
            case NAO_EFETIVADA_DINHEIRO_DEVOLVIDO:
                return Long.parseLong( "6" );
            case PAGAMENTO_EM_PROCESSAMENTO:
                return Long.parseLong( "7" );
            case CANCELADA:
                return Long.parseLong( "8" );
            case NAO_PREMIADA:
                return Long.parseLong( "9" );
            case PRESCRITA:
                return Long.parseLong( "10" );
            case PREMIO_PAGO:
                return Long.parseLong( "11" );
            case PREMIADA:
                return Long.parseLong( "100" );
            case NAO_SROTEADA:
                return Long.parseLong( "101" );
            case NAO_SORTEADA_AINDA_CONCORRENDO:
                return Long.parseLong( "102" );
            default:
                return null;
        }
    }

}
