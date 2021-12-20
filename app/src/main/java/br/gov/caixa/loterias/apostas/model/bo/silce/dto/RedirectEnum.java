package br.gov.caixa.loterias.apostas.model.bo.silce.dto;

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
    @SerializedName("carrinhos-favoritos")CARRINHOS_FAVORITOS,
    @SerializedName("validar-cpf")VALIDAR_CPF,
    @SerializedName("cadastrar-apostador")CADASTRAR_APOSTADOR,
    @SerializedName("selecionar-loterica")SELECIONAR_LOTERICA,
    @SerializedName("aceitar-termo-de-uso")ACEITAR_TERMO_DE_USO,
    @SerializedName("login")LOGIN,
    @SerializedName("indisponibilidade")INDISPONIVEL,
    @SerializedName("/carrinhos/verifica-compra-processamento")VERIFICA_COMPRA_PROCESSAMENTO;

    @Override
    public String toString() {
        return name();
    }

    public static RedirectEnum fromString(String modalidade) {
        switch (modalidade) {
            case "home":
                return RedirectEnum.HOME;
            case "ativar-cadastro":
                return RedirectEnum.ATIVAR_CADASTRO;
            case "mega-sena":
                return RedirectEnum.MEGA_SENA;
            case "surpresinhaMegaSena":
                return RedirectEnum.SURPRESINHAMEGASENA;
            case "quina":
                return RedirectEnum.QUINA;
            case "surpresinhaQuina":
                return RedirectEnum.SURPRESINHAQUINA;
            case "dupla-sena":
                return RedirectEnum.DUPLA_SENA;
            case "surpresinhaDuplaSena":
                return RedirectEnum.SURPRESINHADUPLASENA;
            case "lotomania":
                return RedirectEnum.LOTOMANIA;
            case "surpresinhaLotomania":
                return RedirectEnum.SURPRESINHALOTOMANIA;
            case "lotofacil":
                return RedirectEnum.LOTOFACIL;
            case "surpresinhaLotofacil":
                return RedirectEnum.SURPRESINHALOTOFACIL;
            case "timemania":
                return RedirectEnum.TIMEMANIA;
            case "surpresinhaTimemania":
                return RedirectEnum.SURPRESINHATIMEMANIA;
            case "lotogol":
                return RedirectEnum.LOTOGOL;
            case "loteca":
                return RedirectEnum.LOTECA;
            case "aposta":
                return RedirectEnum.APOSTA;
            case "carrinho":
                return RedirectEnum.CARRINHO;
            case "validar-cpf":
                return RedirectEnum.VALIDAR_CPF;
            case "cadastrar-apostador":
                return RedirectEnum.CADASTRAR_APOSTADOR;
            case "selecionar-loterica":
                return RedirectEnum.SELECIONAR_LOTERICA;
            case "aceitar-termo-de-uso":
                return RedirectEnum.ACEITAR_TERMO_DE_USO;
            case "login":
                return RedirectEnum.LOGIN;
            case "indisponibilidade":
                return RedirectEnum.INDISPONIVEL;
            case "carrinhos-favoritos":
                return RedirectEnum.CARRINHOS_FAVORITOS;
            case "/carrinhos/verifica-compra-processamento":
                return RedirectEnum.VERIFICA_COMPRA_PROCESSAMENTO;
            default:
                return null;
        }
    }

}
