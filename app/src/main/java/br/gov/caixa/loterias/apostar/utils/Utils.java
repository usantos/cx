package br.gov.caixa.loterias.apostar.utils;

import android.os.Handler;
import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import br.gov.caixa.loterias.apostar.model.bean.DadosUsuarioToken;

/**
 * Created by cedesbr450 on 12/04/18.
 */

public class Utils {


    public interface DelayCallback{
        void afterDelay();
    }

    public static void delay(int secs, final DelayCallback delayCallback){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                delayCallback.afterDelay();
            }
        }, secs * 1000);
    }

    public static DadosUsuarioToken decodedToken(String JWTEncoded) throws Exception {
        DadosUsuarioToken dadosUsuarioToken = new DadosUsuarioToken();
        try {
            String[] split = JWTEncoded.split("\\.");
//            Log.d("JWT_DECODED", "Header: " + getJson(split[0]));
            String json = getJson(split[1]);
            Gson gson = new Gson();
            dadosUsuarioToken = gson.fromJson(json, DadosUsuarioToken.class);
            //new JSONObject(json);
            Log.d( "retorno", json.toString() );
        } catch (UnsupportedEncodingException e) {
            //Error
        }
        return dadosUsuarioToken;
    }

    private static String getJson(String strEncoded) throws UnsupportedEncodingException{
        byte[] decodedBytes = Base64.decode(strEncoded, Base64.URL_SAFE);
        return new String(decodedBytes, "UTF-8");
    }
}
