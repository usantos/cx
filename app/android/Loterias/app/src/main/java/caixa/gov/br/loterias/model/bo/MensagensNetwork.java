package caixa.gov.br.loterias.model.bo;

import android.content.Context;
import android.util.Log;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import caixa.gov.br.loterias.R;

/**
 * Created by cedesbr450 on 08/12/17.
 */

public class MensagensNetwork {
    private MensagensNetwork(){}

    public static String setMensagem(VolleyError error, Context context){
        if (error.networkResponse == null){
            return context.getString(R.string.seminternet);
        }
        String msgErro = "";

        byte[] textBytes = error.networkResponse.data;
        String object = new String(textBytes);
        String msgErroResponse = "";
        try {
            String json = new String(error.networkResponse.data, "UTF-8");
            JSONObject jsonObj = new JSONObject(json);
            msgErroResponse = jsonObj.getString("mensagem");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        switch (error.networkResponse.statusCode){
            case 400:
                msgErro = msgErroResponse;
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
                msgErro = msgErroResponse;
                break;
        }
        return msgErro;
    }
}
