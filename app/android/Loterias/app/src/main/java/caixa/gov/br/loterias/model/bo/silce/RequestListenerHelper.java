package caixa.gov.br.loterias.model.bo.silce;

import com.android.volley.VolleyError;

/**
 * Created by pmotta on 15/03/2018.
 */

public class RequestListenerHelper <T>{

    private SilceClient.Listener<T> silceListener;
    private SilceClient.ErrorListener errorListener;

    public RequestListenerHelper(final RequestListener<T> listener){
        silceListener = new SilceClient.Listener<T>(){
            @Override
            public void onResponse(T result) {
                if(listener != null){
                    listener.onResponse(result);
                }
            }
        };
        errorListener = new SilceClient.ErrorListener(){
            @Override
            public void onErrorResponse(final VolleyError error) {
                if(listener != null) {
                    listener.onErrorResponse(error);
                }
            }
        };
    }

    public SilceClient.Listener<T> getSilceListener() {
        return silceListener;
    }

    public SilceClient.ErrorListener getErrorListener() {
        return errorListener;
    }
}
