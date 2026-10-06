package br.gov.caixa.loterias.apostas.model.sp;

import android.content.Context;
import android.content.SharedPreferences;

import br.gov.caixa.loterias.apostas.model.bo.DadosUsuarioBO;
import br.gov.caixa.loterias.apostas.utils.Aplicacao;

public class RapidaoSP {

    private static final String RAPIDAO_KEY = "RAPIDAO_KEY";

    public static int obterValorPremioMinimoSalvo(int valor) {
        Context context = Aplicacao.application.getApplicationContext();
        if(context != null){
            SharedPreferences sharedPreferences = context.getSharedPreferences(RAPIDAO_KEY, Context.MODE_PRIVATE);
            return sharedPreferences.getInt(DadosUsuarioBO.obterCpf(), valor);
        }
        return valor;
    }

    public static void salvarValorPremioMinimo(int valor) {
        Context context = Aplicacao.application.getApplicationContext();
        if(context != null){
            SharedPreferences sharedPreferences = context.getSharedPreferences(RAPIDAO_KEY, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(DadosUsuarioBO.obterCpf(), valor);
            editor.apply();
        }
    }
}
