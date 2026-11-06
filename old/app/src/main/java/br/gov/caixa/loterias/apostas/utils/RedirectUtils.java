package br.gov.caixa.loterias.apostas.utils;

import br.gov.caixa.loterias.apostas.model.bean.SessaoUsuario;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RedirectEnum;

public class RedirectUtils {

    public static boolean temRedirectCadastrarApostador(){
        return temRedirect() && getRedirect() == RedirectEnum.CADASTRAR_APOSTADOR;
    }

    public static Boolean temRedirect(){
        return getRedirect() != null;
    }

    private static RedirectEnum getRedirect(){
        return SessaoUsuario.getInstance().getRedirectEnum();
    }
}
