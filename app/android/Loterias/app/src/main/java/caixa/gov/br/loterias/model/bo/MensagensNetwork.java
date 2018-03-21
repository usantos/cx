package caixa.gov.br.loterias.model.bo;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import caixa.gov.br.loterias.R;

/**
 * Created by cedesbr450 on 08/12/17.
 */

public class MensagensNetwork {
    private static final int UNAUTHORIZED = 401;
    private static final int TIMEOUT = 408;
    private static final int INTERNAL_SERVER_ERROR = 500;

    private MensagensNetwork(){}

    public static String setMensagem(VolleyError error, final Context context){
        NetworkResponse response = error.networkResponse;

        if (response == null){
            if (error instanceof TimeoutError) {
                return context.getString(R.string.erro_408);
            }else if(error instanceof NoConnectionError){
                return context.getString(R.string.seminternet);
            } else if (error instanceof AuthFailureError) {
                return context.getString(R.string.erro_401);
            }

            return context.getString(R.string.sistema_indisponivel);
        }

        final byte[] textBytes = response.data;
        String msgErro;
        String msgErroResponse;

        try {
            String json = new String(textBytes, "UTF-8");
            JSONObject jsonObj = new JSONObject(json);
            msgErroResponse = jsonObj.getString("mensagem");
        } catch (UnsupportedEncodingException | JSONException e) {
            msgErroResponse = context.getString(R.string.sistema_indisponivel);
        }

        switch (response.statusCode){
            case UNAUTHORIZED:
                msgErro = context.getString(R.string.erro_401);
                break;
            case TIMEOUT:
                msgErro = context.getString(R.string.erro_408);
                break;
            case INTERNAL_SERVER_ERROR:
                msgErro = context.getString(R.string.erro_500);
                break;
            default:
                msgErro = msgErroResponse;
                break;
        }

        return msgErro;
    }

    public static boolean isUnauthorizedError(VolleyError error){
        NetworkResponse response = error.networkResponse;
        if (response != null){
            return response.statusCode == UNAUTHORIZED;
        }
        return false;
    }
}
