package br.gov.caixa.loterias.apostas.utils;

import com.google.firebase.messaging.FirebaseMessaging;

public class MessagingToken {
    private static final String TOKEN_ENVIADO = "TOKEN_ENVIADO";

    public boolean isTokenEnviado(String cpf, String token) {
        String tokenEnviado = SharedPreferencesUtils.getValorString(TOKEN_ENVIADO, "");
        String tokenEsperado = cpf.substring(cpf.length() -2) +
                                token.substring(token.length() -5);
        return tokenEsperado.equals(tokenEnviado);
    }

    //Salva Digito CPF + Final do Token
    public void setTokenEnviado(String cpf, String token) {
        SharedPreferencesUtils.setValor(TOKEN_ENVIADO,
                cpf.substring(cpf.length() -2) +
                       token.substring(token.length() -5));
    }

    public void tokenFirebase(OnTokenListener listner) {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {

            if (!task.isSuccessful()) {
                listner.onErro(task.getException());
                return;
            }

            String token = task.getResult();
            //saveToken(token);
            listner.onSucesso(token);
        });
    }

    public interface OnTokenListener {
        void onSucesso(String token);
        void onErro(Exception e);
    }
}
