/**
 * Created by cedesbr450 on 29/12/17.
 */


package caixa.gov.br.loterias.model.bo.silce.dto;

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
    @SerializedName("Dia de Sorte")DIA_DE_SORTE;

    public static String fromString(ModalidadeEnumSelecaoModalidades modalidade) {
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


    public static ModalidadeEnumSelecaoModalidades toString(String modalidade) {
        switch (modalidade) {
            case "Quina":
                return ModalidadeEnumSelecaoModalidades.QUINA;
            case "Loteca":
                return ModalidadeEnumSelecaoModalidades.LOTECA;
            case "Lotogol":
                return ModalidadeEnumSelecaoModalidades.LOTOGOL;
            case "Lotofácil":
                return ModalidadeEnumSelecaoModalidades.LOTOFACIL;
            case "Lotomania":
                return ModalidadeEnumSelecaoModalidades.LOTOMANIA;
            case "Mega-Sena":
                return ModalidadeEnumSelecaoModalidades.MEGA_SENA;
            case "Timemania":
                return ModalidadeEnumSelecaoModalidades.TIMEMANIA;
            case "Dupla Sena":
                return ModalidadeEnumSelecaoModalidades.DUPLA_SENA;
            case "Federal":
                return ModalidadeEnumSelecaoModalidades.FEDERAL;
            case "Dia de Sorte":
                return ModalidadeEnumSelecaoModalidades.DIA_DE_SORTE;
            default:
                return null;
        }
    }
}
