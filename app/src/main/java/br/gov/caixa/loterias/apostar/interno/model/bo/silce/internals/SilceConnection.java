package br.gov.caixa.loterias.apostar.interno.model.bo.silce.internals;

import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.ParameterizedType;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static com.android.volley.Request.Method.DELETE;
import static com.android.volley.Request.Method.GET;
import static com.android.volley.Request.Method.POST;
import static com.android.volley.Request.Method.PUT;

public class SilceConnection {
    private static final Bin KEY = Bin.fromBase64("QEm06ZAThg1E314zziahsg");
    private static final Bin IV = Bin.fromBase64("8hQLtNV44Y1bIeONmlr+Dg==");
    private static final String TAG = SilceConnection.class.getName();
    private static final int TIMEOUT = 30000;
    private static final String TAG_REQUEST = "TAG_REQUEST";

    private RequestQueue requestQueue;
    private SilceCrypto silceCrypto = SilceCrypto.create(KEY, IV);
    private String baseUrl;

    private SilceConnection(RequestQueue requestQueue, String baseUrl) {
        this.requestQueue = requestQueue;
        this.baseUrl = baseUrl;

        if ((this.baseUrl.length() > 0) && (this.baseUrl.charAt(this.baseUrl.length() - 1) != '/')) {
            this.baseUrl += '/';
        }
    }

    public void request(GsonRequest req) {
        try {
            if (req.getHeaders() == null) {
                req.setHeaders(new HashMap<String, String>());
            }
            req.getHeaders().put("cookie", "security=true");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (req.getHeaders() == null) {
                req.setHeaders(new HashMap<String, String>());
            }
            req.getHeaders().put("cookie", "security=true");
        } catch (Exception e) {
            e.printStackTrace();
        }
        requestQueue.add(req);
        requestQueue.getCache().clear();
    }

    private RetryPolicy createRetryPolicy() {
        return new DefaultRetryPolicy(
                TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
    }

    public <T> GsonRequest buildGetRequest(String path, Map<String, String> queryParams,
                                           Class<T> responseDataClass,
                                           Response.Listener<T> listener,
                                           Response.ErrorListener errorListener, Map<String, String> headers) {
        String url = fillUrlWithQueryParams(path, queryParams);
        GsonRequest request = new GsonRequest<String, T>(GET, url, responseDataClass,
                headers, listener, errorListener);
        request.setRetryPolicy(createRetryPolicy());
        Log.i(TAG_REQUEST, getPath(path, queryParams));
        return request;
    }

    public <REQ, RSP> GsonRequest buildPostRequest(String path, Map<String, String> queryParams,
                                                   REQ body, Class<RSP> responseDataClass,
                                                   Response.Listener<RSP> listener,
                                                   Response.ErrorListener errorListener, Map<String, String> headers) {
        String url = fillUrlWithQueryParams(path, queryParams);
        GsonRequest request = new GsonRequest<>(POST, url,
                prepareBody(body), responseDataClass,
                headers, listener, errorListener);

        request.setRetryPolicy(createRetryPolicy());
        Log.i(TAG_REQUEST, getPath(path, queryParams));
        return request;
    }

    public <REQ, RSP> GsonRequest buildPutRequest(String path, Map<String, String> queryParams,
                                                  REQ body, Class<RSP> responseDataClass,
                                                  Response.Listener<RSP> listener,
                                                  Response.ErrorListener errorListener, Map<String, String> headers) {
        String url = fillUrlWithQueryParams(path, queryParams);
        GsonRequest request = new GsonRequest<>(PUT, url,
                prepareBody(body), responseDataClass,
                headers, listener, errorListener);
        request.setRetryPolicy(createRetryPolicy());
        Log.i(TAG_REQUEST, getPath(path, queryParams));
        return request;
    }

    public <T> GsonRequest buildDeleteRequest(String path, String param,
                                              Class<T> responseDataClass,
                                              Response.Listener<T> listener,
                                              Response.ErrorListener errorListener, Map<String, String> headers) {
        String url = (path + param);
        url = fillUrl(url);
        GsonRequest request = new GsonRequest<String, T>(DELETE, url, responseDataClass,
                headers, listener, errorListener);
        request.setRetryPolicy(createRetryPolicy());
        Log.i(TAG_REQUEST, url);
        return request;
    }

    public <T> void get(String path, Map<String, String> queryParams,
                        Class<T> responseDataClass,
                        Response.Listener<T> listener,
                        Response.ErrorListener errorListener, Map<String, String> headers) {
        request(buildGetRequest(path, queryParams, responseDataClass, listener, errorListener, headers));
    }

    public <T> void delete(String path, String param,
                           Class<T> responseDataClass,
                           Response.Listener<T> listener,
                           Response.ErrorListener errorListener, LinkedHashMap<String, String> headers) {
        request(buildDeleteRequest(path, param, responseDataClass, listener, errorListener, headers));
    }

    public <REQ, RSP> void post(String path, Map<String, String> queryParams,
                                REQ body, Class<RSP> responseDataClass,
                                Response.Listener<RSP> listener,
                                Response.ErrorListener errorListener, LinkedHashMap<String, String> headers) {
        request(buildPostRequest(path, queryParams, body, responseDataClass, listener, errorListener, headers));
    }

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

    private <T> String prepareBody(T body) {
        try {
            String teste = (new Gson()).toJson(body);

            //{"apostas":[]}

            String bodyString = "";
            if (body == null) {
                bodyString = "{}";
            } else {
                bodyString = (new Gson()).toJson(body);
            }

            Bin encryptedBody = silceCrypto.encryptBody(bodyString);
            String bodyStr = encryptedBody.toIso8859_1()
                    + "!#!#!" + KEY.toIso8859_1()
                    + "!#!#!" + IV.toIso8859_1();
            return Bin.fromIso8859_1(bodyStr).toBase64();
        } catch (Exception e) {
            throw new RuntimeException("Exceção ao preparar o body da requisição ao SILCE", e);
        }
    }

    private String fillParamStr(Map<String, String> queryParams) {
        if ((queryParams == null) || (queryParams.isEmpty())) {
            return "";
        }

        try {
            Bin encryptedParams = silceCrypto.encryptParams(queryParams);
            String paramStr = encryptedParams.toIso8859_1()
                    + "!#!#!" + KEY.toIso8859_1()
                    + "!#!#!" + IV.toIso8859_1();
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
        if (error.networkResponse != null) {
            return "Network error: " +
                    error.networkResponse.statusCode + " - " +
                    Bin.fromBytes(error.networkResponse.data).toUtf8();
        } else {
            return "Exception when trying to reach the server: "
                    + error.toString();
        }
    }

    private String getPath(String path, Map<String, String> param) {
        StringBuilder sb = new StringBuilder();
        sb.append(path);
        if (param != null && !param.isEmpty()) {
            for (Map.Entry<String, String> entry : param.entrySet()) {
                sb.append("/");
                sb.append(entry.getKey());
                sb.append("=");
                sb.append(entry.getValue());
            }
        }
        return sb.toString();
    }
}
