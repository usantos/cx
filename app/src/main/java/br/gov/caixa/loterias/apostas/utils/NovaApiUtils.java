package br.gov.caixa.loterias.apostas.utils;

import br.gov.caixa.loterias.apostas.model.bo.DadosUsuarioBO;

public class NovaApiUtils {

    public static boolean isNovaApiHabilitada(){
        return SharedPreferencesUtils.getValorBoolean(ConfiguracoesEnum.IS_NOVA_API.get(), ConfiguracoesDefaultEnum.IS_NOVA_API.asBoolean());
    }

    public static boolean isFinalCPFAcessaNovaApi() {
        int finalCpf = SharedPreferencesUtils.getValorInt(ConfiguracoesEnum.GRUPO_CPF_NOVA_API.get(), ConfiguracoesDefaultEnum.GRUPO_CPF_NOVA_API.asInt());

        if (finalCpf == 0) {
            return false;
        }

        if (finalCpf == 100) {
            return true;
        }

        String cpf = DadosUsuarioBO.obterCpf();
        if (cpf != null && !cpf.isEmpty() && cpf.length() >= 2) {
            //Verifica se o final do CPF < finalCpfAcessamNuvem
            if (Integer.parseInt(cpf.substring(cpf.length() -2)) < finalCpf) {
                return true;
            }
        }

        return false;
    }

    public static boolean isPossoBuscarNovaAPI(){
        return isNovaApiHabilitada() && isFinalCPFAcessaNovaApi();
    }

}
