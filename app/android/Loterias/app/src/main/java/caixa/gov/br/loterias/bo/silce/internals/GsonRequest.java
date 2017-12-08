package caixa.gov.br.loterias.bo.silce.internals;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public class GsonRequest<REQ, RSP> extends JsonRequest<RSP> {
    private final Gson gson = new Gson();
    private final Class<RSP> responseKlazz;
    private final Map<String, String> headers;
    private final Response.Listener<RSP> listener;
    private Response.ErrorListener mErrorListener;

    /**
     * Make a GET request and return a parsed object from JSON.
     *
     * @param url URL of the request to make
     * @param responseKlazz Relevant class object, for Gson's reflection
     * @param headers Map of request headers
     */
    public GsonRequest(int method, String url, Class<RSP> responseKlazz, Map<String, String> headers,
                       Response.Listener<RSP> listener, Response.ErrorListener mErrorListener) {
        this(method, url, null, responseKlazz, headers, listener, mErrorListener);
    }

    public GsonRequest(int method, String url, REQ requestBody, Class<RSP> responseKlazz,
                       Map<String, String> headers, Response.Listener<RSP> listener,
                       Response.ErrorListener mErrorListener) {
        super(method, url, bodyToJson(requestBody), listener, mErrorListener);
        this.responseKlazz = responseKlazz;
        this.headers = headers;
        this.listener = listener;
        this.mErrorListener = mErrorListener;
    }

    private static <T> String bodyToJson(T requestBody) {
        if (requestBody == null) {
            return null;
        } else if (String.class.isAssignableFrom(requestBody.getClass())) {
            return (String) requestBody;
        }

        return (new Gson()).toJson(requestBody);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers != null ? headers : super.getHeaders();
    }

    @Override
    protected void deliverResponse(RSP response) {
        listener.onResponse(response);
    }

    @Override
    public void deliverError(VolleyError error) {
        if (error.networkResponse.statusCode == 400){

        }else if(error.networkResponse.statusCode == 401) {

        }else{

        }
        mErrorListener.onErrorResponse(error);
    }

    @Override
    protected Response<RSP> parseNetworkResponse(NetworkResponse response) {

        try {
            String json = new String(response.data, "UTF-8");

            return Response.success(
                    gson.fromJson(json, responseKlazz),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        }
    }


}