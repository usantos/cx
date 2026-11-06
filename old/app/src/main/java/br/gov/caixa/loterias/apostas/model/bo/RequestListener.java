package br.gov.caixa.loterias.apostas.model.bo;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;

/**
 * Created by pmotta on 15/03/2018.
 */

public abstract class RequestListener<T> {

    private Response.Listener<T> silceListener;
    private Response.ErrorListener errorListener;

    public RequestListener(){
        silceListener = result -> {
            if (result != null) {
                Log.d("RESPONSE_BODY", result.toString());
            }
            RequestListener.this.onResponse(result);
        };
        errorListener = error -> {
            if (error != null) {
                Log.d("ERROR_BODY", error.toString());
            }
            RequestListener.this.onErrorResponse(error);
        };
    }

    public abstract void onResponse(T response);
    public abstract void onErrorResponse(VolleyError error);

    public Response.Listener<T> getSilceListener() {
        return silceListener;
    }

    public Response.ErrorListener getErrorListener() {
        return errorListener;
    }
}
