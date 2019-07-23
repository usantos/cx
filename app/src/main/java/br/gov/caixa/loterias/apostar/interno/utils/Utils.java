package br.gov.caixa.loterias.apostar.interno.utils;

import android.os.Handler;
import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Random;

import br.gov.caixa.loterias.apostar.interno.model.bean.DadosUsuarioToken;

/**
 * Created by cedesbr450 on 12/04/18.
 */

public class Utils {

    private static Random random;

    public interface DelayCallback{
        void afterDelay();
    }

    public static void delay(int secs, final DelayCallback delayCallback){
        Handler handler = new Handler();
        handler.postDelayed(() -> delayCallback.afterDelay(), secs * 1000);
    }

    public static DadosUsuarioToken decodedToken(String JWTEncoded) throws Exception {
        if (JWTEncoded != null) {
            DadosUsuarioToken dadosUsuarioToken = new DadosUsuarioToken();
            try {
                String[] split = JWTEncoded.split("\\.");
                String json = getJson(split[1]);
                Gson gson = new Gson();
                dadosUsuarioToken = gson.fromJson(json, DadosUsuarioToken.class);
            } catch (UnsupportedEncodingException e) {

            }
            return dadosUsuarioToken;
        } else {
            return new DadosUsuarioToken();
        }
    }

    private static String getJson(String strEncoded) throws UnsupportedEncodingException{
        byte[] decodedBytes = Base64.decode(strEncoded, Base64.URL_SAFE);
        return new String(decodedBytes, "UTF-8");
    }

    public static Random getRandom() {
        if(random == null){
            random = new Random();
        }
        return random;
    }
}
