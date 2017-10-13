package caixa.gov.br.loterias.bo.silce.internals;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.android.volley.Request.Method.GET;

public class SilceConnection {
    public static final Bin KEY = Bin.fromBase64("QEm06ZAThg1E314zziahsg");
    public static final Bin IV = Bin.fromBase64("8hQLtNV44Y1bIeONmlr+Dg==");
    private static final String TAG = SilceConnection.class.getName();

    RequestQueue requestQueue;
    SilceCrypto silceCrypto = SilceCrypto.create(KEY, IV);
    String baseUrl;

    protected SilceConnection(RequestQueue requestQueue, String baseUrl) {
        this.requestQueue = requestQueue;
        this.baseUrl = baseUrl;

        if ((this.baseUrl.length() > 0) && (this.baseUrl.charAt(this.baseUrl.length() - 1) != '/')) {
            this.baseUrl += '/';
        }
    }

    public <T> void get(String path, Map<String, String> queryParams,
                                    Class<T> responseDataClass,
                                    Response.Listener<T> listener,
                                    Response.ErrorListener errorListener) {
        String url = fillUrlWithQueryParams(path, queryParams);
        GsonRequest<T> req = new GsonRequest<>(GET, url, responseDataClass,
                new LinkedHashMap<String, String>(), listener, errorListener);
        requestQueue.add(req);
        requestQueue.start();
    }
/*
    public <R, T> void post(String path, Map<String, String> queryParams,
                        R requestBody,
                        Class<T> responseDataClass,
                        Response.Listener<T> listener,
                        Response.ErrorListener errorListener) {
        JSONObject postBody = new JSONObject();
        JsonObjectRequest req = new JsonObjectRequest(POST, fillUrl(path), postBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                    }
                });
        GsonRequest<T> req = new GsonRequest<>(GET, url, responseDataClass,
                new LinkedHashMap<String, String>(), listener, errorListener);
        requestQueue.add(req);
    }
    */
/*
    <T> Response.Listener<T> volleyJsonListener(
            final SilceClient.Listener<T> listener,
            final Class<T> responseKlazz) {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

            }
        });

    }

    Response.ErrorListener volleyErrorListener(final SilceClient.ErrorListener errorListener,
                                               final Class klass) {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                errorListener.onErrorResponse(error);
            }
        };
    }
*/

    public RequestQueue getRequestQueue() {
        return requestQueue;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    private String fillUrl(String path) {
        return baseUrl + Bin.fromUtf8(path).toBase64();
    }

    private String fillUrlWithQueryParams(String path, Map<String, String> queryParams) {
        return fillUrl(path) + fillParamStr(queryParams);
    }

    private String fillParamStr(Map<String, String> queryParams) {
        if ((queryParams == null) || (queryParams.isEmpty())) {
            return "";
        }

        try {
            Bin encryptedParams = silceCrypto.encryptParams(queryParams);
            String paramStr = encryptedParams.toIso8859_1()
                    + "!#!#!" + KEY.toIso8859_1()
                    + "!#!#!" +  IV.toIso8859_1();
            String urlEncoded = URLEncoder.encode(paramStr, "UTF-8");

            return "/?q=" + urlEncoded;
        } catch (Exception e) {
            throw new RuntimeException("Exceção ao preparar os parâmetros de query da requisição ao SILCE", e);
        }
    }

    private String fillUrl(String path, Bin query) {
        try {
            String queryStr = "";
            if (query != null) {
                queryStr = "/?q=" + URLEncoder.encode(
                        query.toIso8859_1() + "!#!#!" + KEY.toIso8859_1() + "!#!#!" + IV.toIso8859_1(),
                        "UTF-8");
            }
            return fillUrl(path) + queryStr;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Falha ao gerar a URL da requisição ao SILCE", e);
        }
    }

    public static SilceConnection create(RequestQueue requestQueue, String baseUrl) {
        return new SilceConnection(requestQueue, baseUrl);
    }

    public static String errorToString(VolleyError error) {
        return "Network error: " +
                error.networkResponse.statusCode + " - " +
                Bin.fromBytes(error.networkResponse.data).toUtf8();
    }
}
