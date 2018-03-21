package caixa.gov.br.loterias.model.bo;

import com.android.volley.Response;
import com.android.volley.VolleyError;

/**
 * Created by pmotta on 15/03/2018.
 */

public abstract class RequestListener<T> {

    private Response.Listener<T> silceListener;
    private Response.ErrorListener errorListener;

    public RequestListener(){
        silceListener = new Response.Listener<T>(){
            @Override
            public void onResponse(T result) {
                RequestListener.this.onResponse(result);
            }
        };
        errorListener = new Response.ErrorListener(){
            @Override
            public void onErrorResponse(final VolleyError error) {
                RequestListener.this.onErrorResponse(error);
            }
        };
    }

    public abstract void onResponse(T result);
    public abstract void onErrorResponse(VolleyError error);

    public Response.Listener<T> getSilceListener() {
        return silceListener;
    }

    public Response.ErrorListener getErrorListener() {
        return errorListener;
    }
}
