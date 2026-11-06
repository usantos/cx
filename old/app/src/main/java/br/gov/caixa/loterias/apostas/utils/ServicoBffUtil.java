package br.gov.caixa.loterias.apostas.utils;

import br.gov.caixa.loterias.apostas.model.bo.DadosUsuarioBO;

public class ServicoBffUtil {


    private static boolean cpfAcessamBff() {
        String cpfAcessam[] = SharedPreferencesUtils.getValorString(ConfiguracoesEnum.GRUPO_CPFS_BFF.get(), ConfiguracoesDefaultEnum.GRUPO_CPFS_BFF.asString()).split(";");

        int finalCpfAcessamBff = new Integer(cpfAcessam[0]);

        if (finalCpfAcessamBff == 100) {
            return true;
        }

        String cpf = DadosUsuarioBO.obterCpf();
        if (cpf != null && !cpf.isEmpty() && cpf.length() >= 2) {

            //Verifica lista branca
            for (int i = 1; i < cpfAcessam.length; i++) {
                if (cpf.equals(cpfAcessam[i])) {
                    return true;
                }
            }

            //Verifica se o final do CPF < finalCpfAcessamBff
            if (Integer.parseInt(cpf.substring(cpf.length() -2)) < finalCpfAcessamBff) {
                return true;
            }
        }

        return false;
    }


    public static String getBaseUrlBff() {
        return SharedPreferencesUtils.getValorString(ConfiguracoesEnum.URL_BASE_BFF.get(), ConfiguracoesDefaultEnum.URL_BASE_BFF.asString());
    }

    public static boolean isServicoBffAtivo() {
        String baseUrlBff = getBaseUrlBff();

        if (baseUrlBff == null || baseUrlBff.isEmpty() || baseUrlBff.equals("-")) {
            return false;
        }

        return true;
    }

    public static boolean isServicoBff() {
        if (!isServicoBffAtivo()) {
            return false;
        }

        if (cpfAcessamBff()) {
            return true;
        }

        return false;
    }
}