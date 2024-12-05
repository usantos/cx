package br.gov.caixa.loterias.apostas.utils;

import br.gov.caixa.loterias.apostas.model.bo.DadosUsuarioBO;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.ConfiguracaoAppSingleton;

public class NovaApiUtils {

    public static boolean isNovaApiHabilitada(){
        return ConfiguracaoAppSingleton.getInstance().isNovaApi() != null && ConfiguracaoAppSingleton.getInstance().isNovaApi();
    }

    public static boolean isFinalCPFAcessaNovaApi() {

        int finalCpf = ConfiguracaoAppSingleton.getInstance().getGrupoCpfNovaApi();

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
