package caixa.gov.br.loterias.model.sp;

import android.content.Context;
import android.content.SharedPreferences;

import caixa.gov.br.loterias.utils.Aplicacao;

public class InformacoesUsuarioSP {

    private static final String INFORMACOES_USUARIO_KEY = "INFORMACOES_USUARIO_KEY";
    private static final String NOME = "NOME";
    private static final String CPF = "CPF";

    public static void salvarNomeUsuairo(String name) {
        Context context = Aplicacao.application.getApplicationContext();
        if(context != null){
            SharedPreferences sharedPreferences = context.getSharedPreferences(INFORMACOES_USUARIO_KEY,  Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(NOME, name);
            editor.apply();
        }
    }

    public static String obterNome() {
        Context context = Aplicacao.application.getApplicationContext();
        if(context != null){
            SharedPreferences sharedPreferences = context.getSharedPreferences(INFORMACOES_USUARIO_KEY, Context.MODE_PRIVATE);
            return sharedPreferences.getString(NOME, "");
        }
        return "";
    }

    public static void salvarCpfUsuario(String cpf) {
        Context context = Aplicacao.application.getApplicationContext();
        if(context != null){
            SharedPreferences sharedPreferences = context.getSharedPreferences(INFORMACOES_USUARIO_KEY, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(CPF, cpf);
            editor.apply();
        }
    }

    public static String obterCpf() {
        Context context = Aplicacao.application.getApplicationContext();
        if(context != null){
            SharedPreferences sharedPreferences = context.getSharedPreferences(INFORMACOES_USUARIO_KEY, Context.MODE_PRIVATE);
            return sharedPreferences.getString(CPF, "");
        }
        return "";
    }

    public static boolean existeUsuarioLogado() {
        if(!"".equals(obterNome())){
            return true;
        }
        return false;
    }

    public static void limparRegistros() {
        salvarCpfUsuario("");
        salvarNomeUsuairo("");
    }
}
