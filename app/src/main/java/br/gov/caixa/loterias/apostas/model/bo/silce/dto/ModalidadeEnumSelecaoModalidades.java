/**
 * Created by cedesbr450 on 29/12/17.
 */


package br.gov.caixa.loterias.apostas.model.bo.silce.dto;

        import com.google.gson.annotations.SerializedName;
        import io.swagger.annotations.ApiModel;

@ApiModel(description = "")
public enum ModalidadeEnumSelecaoModalidades {
    @SerializedName("Dupla Sena")DUPLA_SENA,
    @SerializedName("Loteca")LOTECA,
    @SerializedName("Lotofácil")LOTOFACIL,
    @SerializedName("Lotogol")LOTOGOL,
    @SerializedName("Lotomania")LOTOMANIA,
    @SerializedName("Mega-Sena")MEGA_SENA,
    @SerializedName("Quina")QUINA,
    @SerializedName("Timemania")TIMEMANIA,
    @SerializedName("Federal")FEDERAL,
    @SerializedName("Dia de Sorte")DIA_DE_SORTE,
    @SerializedName("Super Sete")SUPER_SETE,
    @SerializedName("Mais Milionária")MAIS_MILIONALIA;

    public static ModalidadeEnumSelecaoModalidades toString(String modalidade) {
        switch (modalidade.toLowerCase()) {
            case "quina":
                return ModalidadeEnumSelecaoModalidades.QUINA;
            case "loteca":
                return ModalidadeEnumSelecaoModalidades.LOTECA;
            case "lotogol":
                return ModalidadeEnumSelecaoModalidades.LOTOGOL;
            case "lotofácil":
                return ModalidadeEnumSelecaoModalidades.LOTOFACIL;
            case "lotomania":
                return ModalidadeEnumSelecaoModalidades.LOTOMANIA;
            case "mega-sena":
                return ModalidadeEnumSelecaoModalidades.MEGA_SENA;
            case "timemania":
                return ModalidadeEnumSelecaoModalidades.TIMEMANIA;
            case "dupla sena":
                return ModalidadeEnumSelecaoModalidades.DUPLA_SENA;
            case "federal":
                return ModalidadeEnumSelecaoModalidades.FEDERAL;
            case "dia de sorte":
                return ModalidadeEnumSelecaoModalidades.DIA_DE_SORTE;
            case "super sete":
                return ModalidadeEnumSelecaoModalidades.SUPER_SETE;
            case "mais milionária":
            case "+milionária":
                return ModalidadeEnumSelecaoModalidades.MAIS_MILIONALIA;
            default:
                return null;
        }
    }
}
