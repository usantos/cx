package caixa.gov.br.loterias.utils;

import android.content.Context;

import com.android.volley.VolleyError;

import caixa.gov.br.loterias.R;

/**
 * Created by cedesbr450 on 08/12/17.
 */

public class MensagensNetwork {

    public static String setMensagem(VolleyError error, Context context){
        String msgErro = "";
        switch (error.networkResponse.statusCode){
            case 400:
                msgErro = error.getClass().getSimpleName();
                break;
            case 401:
                msgErro = context.getString(R.string.erro_401);
                break;
            case 408:
                msgErro = context.getString(R.string.erro_408);
                break;
            case 500:
                msgErro = context.getString(R.string.erro_500);
                break;
            default:
                msgErro = error.getClass().getSimpleName();
                break;
        }
        return msgErro;
    }
}
