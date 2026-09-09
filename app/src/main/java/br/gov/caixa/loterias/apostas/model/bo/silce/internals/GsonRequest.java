package br.gov.caixa.loterias.apostas.model.bo.silce.internals;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public class GsonRequest<REQ, RSP> extends JsonRequest<RSP> {
    private static final String TAG = "GsonRequest";
    private final Gson gson = new Gson();
    private final Class<RSP> responseKlazz;
    private Map<String, String> headers;
    private Response.Listener<RSP> listener;
    private Response.ErrorListener errorListener;
    private OnHttpStatusListener httpStatusListener;

    public GsonRequest(
            int method,
            String url,
            Class<RSP> responseKlazz,
            Map<String, String> headers,
            Response.Listener<RSP> listener,
            Response.ErrorListener errorListener) {

        this(
                method,
                url,
                null,
                responseKlazz,
                headers,
                listener,
                errorListener
        );
    }

    public GsonRequest(
            int method,
            String url,
            REQ requestBody,
            Class<RSP> responseKlazz,
            Map<String, String> headers,
            Response.Listener<RSP> listener,
            Response.ErrorListener errorListener) {

        super(
                method,
                url,
                bodyToJson(requestBody),
                listener,
                errorListener
        );

        Log.i(TAG, "URL: " + url);

        this.responseKlazz = responseKlazz;
        this.headers = headers;
        this.listener = listener;
        this.errorListener = errorListener;
    }

    private static <T> String bodyToJson(T requestBody) {
        if (requestBody == null) {
            return null;
        }

        if (requestBody instanceof String) {
            return (String) requestBody;
        }

        return new Gson().toJson(requestBody);
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public void setListener(Response.Listener<RSP> listener) {
        this.listener = listener;
    }

    public void setErrorListener(Response.ErrorListener listener) {
        this.errorListener = listener;
    }

    public void setOnHttpStatusListener(OnHttpStatusListener listener) {
        this.httpStatusListener = listener;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers != null
                ? headers
                : super.getHeaders();
    }

    @Override
    protected void deliverResponse(RSP response) {
        if (listener != null) {
            listener.onResponse(response);
        }
    }

    @Override
    public void deliverError(VolleyError error) {

        logError(error);

        if (errorListener != null) {
            errorListener.onErrorResponse(error);
        }
    }

    @Override
    protected Response<RSP> parseNetworkResponse(NetworkResponse response) {
        try {

            notifyStatusCode(response);

            String json = new String(
                    response.data,
                    StandardCharsets.UTF_8
            );

            Log.i(TAG, json);

            RSP result = gson.fromJson(
                    json,
                    responseKlazz
            );

            return Response.success(
                    result,
                    HttpHeaderParser.parseCacheHeaders(response)
            );

        } catch (JsonSyntaxException e) {

            return Response.error(
                    new ParseError(e)
            );
        }
    }

    @Override
    protected VolleyError parseNetworkError(VolleyError volleyError) {

        try {

            if (volleyError != null
                    && volleyError.networkResponse != null) {

                notifyStatusCode(
                        volleyError.networkResponse
                );

                Log.d(
                        TAG,
                        "HTTP ERROR: "
                                + volleyError.networkResponse.statusCode
                );
            }

        } catch (Exception ignored) {
        }

        return super.parseNetworkError(volleyError);
    }

    private void notifyStatusCode(NetworkResponse response) {

        if (response == null) {
            return;
        }

        if (httpStatusListener != null) {
            httpStatusListener.onStatusCode(
                    response.statusCode
            );
        }
    }

    private void logError(VolleyError error) {

        if (error == null
                || error.networkResponse == null
                || error.networkResponse.data == null) {
            return;
        }

        try {

            String errorBody = new String(
                    error.networkResponse.data,
                    StandardCharsets.UTF_8
            );

            Log.d(
                    "ERROR_RESPONSE",
                    errorBody
            );

        } catch (Exception e) {
            Log.e(
                    TAG,
                    "Erro ao logar body de erro",
                    e
            );
        }
    }

    public interface OnHttpStatusListener {
        void onStatusCode(int statusCode);
    }
}