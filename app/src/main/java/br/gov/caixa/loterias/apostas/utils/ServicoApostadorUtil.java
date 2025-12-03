package br.gov.caixa.loterias.apostas.utils;

import br.gov.caixa.loterias.apostas.model.bo.DadosUsuarioBO;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.ConfiguracaoAppSingleton;

public class ServicoApostadorUtil {


    private static boolean cpfAcessamApostador() {


        String cpfAcessam[] = ConfiguracaoAppSingleton.getInstance().getGrupoDeCpfsApostador().split(";");

        int finalCpfAcessamApostador = new Integer(cpfAcessam[0]);

        if (finalCpfAcessamApostador == 100) {
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

            //Verifica se o final do CPF < finalCpfAcessamApostador
            if (Integer.parseInt(cpf.substring(cpf.length() -2)) < finalCpfAcessamApostador) {
                return true;
            }
        }

        return false;
    }


    public static String getBaseUrlApostador() {

        return ConfiguracaoAppSingleton.getInstance().getUrlBaseApostador();
    }

    public static boolean isServicoApostadorAtivo() {
        String baseUrlApostador = getBaseUrlApostador();

        if (baseUrlApostador == null || baseUrlApostador.isEmpty() || baseUrlApostador.equals("-")) {
            return false;
        }

        return true;
    }

    public static boolean isServicoApostador() {
        if (!isServicoApostadorAtivo()) {
            return false;
        }

        if (cpfAcessamApostador()) {
            return true;
        }

        return false;
    }
}