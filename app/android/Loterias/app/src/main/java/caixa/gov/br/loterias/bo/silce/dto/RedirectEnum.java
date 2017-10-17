package caixa.gov.br.loterias.bo.silce.dto;

import com.google.gson.annotations.SerializedName;

import io.swagger.annotations.ApiModel;

@ApiModel(description = "")
public enum RedirectEnum {
    NULL,
    @SerializedName("home")HOME,
    @SerializedName("ativar-cadastro")ATIVAR_CADASTRO,
    @SerializedName("mega-sena")MEGA_SENA,
    @SerializedName("surpresinhaMegaSena")SURPRESINHAMEGASENA,
    @SerializedName("quina")QUINA,
    @SerializedName("surpresinhaQuina")SURPRESINHAQUINA,
    @SerializedName("dupla-sena")DUPLA_SENA,
    @SerializedName("surpresinhaDuplaSena")SURPRESINHADUPLASENA,
    @SerializedName("lotomania")LOTOMANIA,
    @SerializedName("surpresinhaLotomania")SURPRESINHALOTOMANIA,
    @SerializedName("lotofacil")LOTOFACIL,
    @SerializedName("surpresinhaLotofacil")SURPRESINHALOTOFACIL,
    @SerializedName("timemania")TIMEMANIA,
    @SerializedName("surpresinhaTimemania")SURPRESINHATIMEMANIA,
    @SerializedName("lotogol")LOTOGOL,
    @SerializedName("loteca")LOTECA,
    @SerializedName("aposta")APOSTA,
    @SerializedName("carrinho")CARRINHO,
    @SerializedName("validar-cpf")VALIDAR_CPF,
    @SerializedName("cadastrar-apostador")CADASTRAR_APOSTADOR,
    @SerializedName("selecionar-loterica")SELECIONAR_LOTERICA,
    @SerializedName("aceitar-termo-de-uso")ACEITAR_TERMO_DE_USO,
    @SerializedName("login")LOGIN;

    @Override
    public String toString() {
        return name();
    }
}
