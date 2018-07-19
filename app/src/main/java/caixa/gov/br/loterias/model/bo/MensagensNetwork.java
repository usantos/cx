package caixa.gov.br.loterias.model.bo;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.model.bean.ErrorResponse;

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

        Gson gsonResponse = new Gson();

        ErrorResponse errorResponse = new ErrorResponse();

        String body;
        //get response body and parse with appropriate encoding
        if(error.networkResponse.data!=null) {
            try {
                body = new String(error.networkResponse.data,"UTF-8");
                errorResponse = gsonResponse.fromJson(body, ErrorResponse.class);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (Exception e){
                e.printStackTrace();
            }
        }

        if (errorResponse.getMensagem() == null){
            errorResponse.setMensagem( context.getString( R.string.sistema_indisponivel ) );
        }

        switch (response.statusCode){
            case UNAUTHORIZED:
                errorResponse.setMensagem(  context.getString(R.string.erro_401) );
                break;
            case TIMEOUT:
                errorResponse.setMensagem(  context.getString(R.string.erro_408) );
                break;
            case INTERNAL_SERVER_ERROR:
                errorResponse.setMensagem(  context.getString(R.string.erro_500) );
                break;
        }

        return errorResponse.getMensagem();
    }

    public static boolean isUnauthorizedError(VolleyError error){
        NetworkResponse response = error.networkResponse;
        if (response != null){
            return response.statusCode == UNAUTHORIZED;
        }
        return false;
    }
}
