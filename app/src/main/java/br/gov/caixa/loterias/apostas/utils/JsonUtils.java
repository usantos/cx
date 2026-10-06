package br.gov.caixa.loterias.apostas.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;

/**
 * Created by lsimas on 05/04/2017.
 */

public class JsonUtils {

    private static Gson gson;

    public static Gson getGson() {
        if ( gson == null ) {
            gson = new GsonBuilder()
                    .setDateFormat("dd/MM/yyyy")
                    .create();
        }

        return gson;
    }

    public static HashMap<String, Object> parseJson(Object src){
        String jsonString = JsonUtils.getGson().toJson(src);

        return new Gson().fromJson(
                jsonString, new TypeToken<HashMap<String, Object>>() {}.getType()
        );
    }

}
