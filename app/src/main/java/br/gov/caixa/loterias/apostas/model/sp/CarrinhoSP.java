package br.gov.caixa.loterias.apostas.model.sp;

import android.content.Context;
import android.content.SharedPreferences;

import br.gov.caixa.loterias.apostas.utils.Aplicacao;

public class CarrinhoSP {

    private static final String CARRINHO_KEY = "CARRINHO_KEY_1";
    private static final String QUANTIDADE = "QUANTIDADE";

    public static void salvarQuantidadeApostas(int quantidade) {
        Context context = Aplicacao.application.getApplicationContext();
        if(context != null){
            SharedPreferences sharedPreferences = context.getSharedPreferences(CARRINHO_KEY, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(QUANTIDADE, String.valueOf(quantidade));
            editor.apply();
        }
    }

    public static String obterQuantidadeApostas() {
         Context context = Aplicacao.application.getApplicationContext();
         if(context != null){
             SharedPreferences sharedPreferences = context.getSharedPreferences(CARRINHO_KEY, Context.MODE_PRIVATE);
             return sharedPreferences.getString(QUANTIDADE, "0");
         }
         return "0";
    }
}
