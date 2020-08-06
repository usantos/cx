package br.gov.caixa.loterias.apostas.model.sp;

import android.content.Context;
import android.content.SharedPreferences;

import br.gov.caixa.loterias.apostas.utils.Aplicacao;

public class LoginSP {

    private final static String LOGIN_KEY = "LOGIN_KEY";
    private final static String PRIMEIRO = "PRIMEIRO";
    private final static String LOGIN_SUCESSO = "LOGIN_SUCESSO";

    public static void limpar() {
        loginRealizado(false);
        primeiraInicializacao(true);
    }

    public static void loginRealizado(boolean sucesso){
        Context context = Aplicacao.application.getApplicationContext();
        if(context != null){
            SharedPreferences sharedPreferences = context.getSharedPreferences(LOGIN_KEY, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(LOGIN_SUCESSO, sucesso);
            editor.apply();
        }
    }

    public static boolean isLoginRealizado() {
        Context context = Aplicacao.application.getApplicationContext();
        if(context != null){
            SharedPreferences sharedPreferences = context.getSharedPreferences(LOGIN_KEY, Context.MODE_PRIVATE);
            return sharedPreferences.getBoolean(LOGIN_SUCESSO, false);
        }
        return false;
    }

    public static void primeiraInicializacao(boolean primeiro) {
        Context context = Aplicacao.application.getApplicationContext();
        if(context != null){
            SharedPreferences sharedPreferences = context.getSharedPreferences(LOGIN_KEY, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(PRIMEIRO, primeiro);
            editor.apply();
        }
    }

    public static boolean isPrimeiraInicializacao() {
        Context context = Aplicacao.application.getApplicationContext();
        if(context != null){
            SharedPreferences sharedPreferences = context.getSharedPreferences(LOGIN_KEY, Context.MODE_PRIVATE);
            return sharedPreferences.getBoolean(PRIMEIRO, false);
        }
        return false;
    }
}
