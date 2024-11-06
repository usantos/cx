package br.gov.caixa.loterias.apostas.model.bo.silce.internals;

import static com.android.volley.Request.Method.DELETE;
import static com.android.volley.Request.Method.GET;
import static com.android.volley.Request.Method.POST;
import static com.android.volley.Request.Method.PUT;

import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.keycloak.KeycloakBO;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnRefreshTokenListener;

public class ServiceConnection {
    private static final Bin KEY = Bin.fromBase64("QEm06ZAThg1E314zziahsg");
    private static final Bin IV = Bin.fromBase64("8hQLtNV44Y1bIeONmlr+Dg==");
    private static final int TIMEOUT = 30000;
    private static final String TAG_REQUEST = "TAG_REQUEST";

    private final RequestQueue requestQueue;
    private final SilceCrypto silceCrypto = SilceCrypto.create(KEY, IV);
    private String baseUrl;

    public ServiceConnection(RequestQueue requestQueue, String baseUrl) {
        this.requestQueue = requestQueue;
        this.baseUrl = baseUrl;

        if ((this.baseUrl.length() > 0) && (this.baseUrl.charAt(this.baseUrl.length() - 1) != '/')) {
            this.baseUrl += '/';
        }
    }

    public <T> void request(GsonRequest req, final RequestListener<T> listenerService) {
        boolean precisaRefreshToken = false;
        try {
            KeycloakBO keycloakBO = KeycloakBO.getInstance();
            if (req.getHeaders() == null) {
                req.setHeaders(new HashMap<>());
            } else if (req.getHeaders().get("Authorization") != null && keycloakBO.isPrecisaRefreshToken()) {
                precisaRefreshToken = true;
                keycloakBO.realizaRefreshToken(onRefreshTokenListener(req, listenerService));
            }
            if (!precisaRefreshToken) {
                req.getHeaders().put("cookie", "security=false");
                req.getHeaders().put("Subcanal", "3");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!precisaRefreshToken) {
            requestQueue.add(req);
            requestQueue.getCache().clear();
        }
    }

    private <T> OnRefreshTokenListener onRefreshTokenListener(GsonRequest req, final RequestListener<T> listenerService) {
        return new OnRefreshTokenListener() {
            @Override
            public void successRefresh() {
                try {
                    KeycloakBO keycloakBO = KeycloakBO.getInstance();
                    req.getHeaders().put("Authorization", "Bearer " + keycloakBO.getAccessToken());
                    req.getHeaders().put("cookie", "security=false");
                    req.getHeaders().put("Subcanal", "3");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                requestQueue.add(req);
                requestQueue.getCache().clear();
            }

            @Override
            public void errorRefresh(VolleyError volleyError) {
                listenerService.onErrorResponse(volleyError);
            }
        };
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
        GsonRequest request = new GsonRequest<String, T>(GET, url, responseDataClass, headers, listener, errorListener);
        request.setRetryPolicy(createRetryPolicy());
        Log.i(TAG_REQUEST, getPath(path, queryParams));
        return request;
    }

    public <REQ, RSP> GsonRequest buildPostRequest(String path, Map<String, String> queryParams,
                                                   REQ body, Class<RSP> responseDataClass,
                                                   Response.Listener<RSP> listener,
                                                   Response.ErrorListener errorListener, Map<String, String> headers) {

        String url = fillUrlWithQueryParams(path, queryParams);
        GsonRequest request = new GsonRequest<>(POST, url, prepareBody(body), responseDataClass, headers, listener, errorListener);

        request.setRetryPolicy(createRetryPolicy());
        Log.i(TAG_REQUEST, getPath(path, queryParams));
        return request;
    }

    public <REQ, RSP> GsonRequest buildPutRequest(String path, Map<String, String> queryParams,
                                                  REQ body, Class<RSP> responseDataClass,
                                                  Response.Listener<RSP> listener,
                                                  Response.ErrorListener errorListener, Map<String, String> headers) {

        String url = fillUrlWithQueryParams(path, queryParams);
        GsonRequest request = new GsonRequest<>(PUT, url, prepareBody(body), responseDataClass, headers, listener, errorListener);

        request.setRetryPolicy(createRetryPolicy());
        Log.i(TAG_REQUEST, getPath(path, queryParams));
        return request;
    }

    public <T> GsonRequest buildDeleteRequest(String path, Map<String, String> queryParams,
                                              Class<T> responseDataClass,
                                              Response.Listener<T> listener,
                                              Response.ErrorListener errorListener, Map<String, String> headers) {

        String url = fillUrlWithQueryParams(path, queryParams);
        GsonRequest request = new GsonRequest<String, T>(DELETE, url, responseDataClass, headers, listener, errorListener);
        request.setRetryPolicy(createRetryPolicy());
        Log.i(TAG_REQUEST, url);
        return request;
    }

    public <T> void get(String path, Map<String, String> queryParams,
                        Class<T> responseDataClass,
                        Response.Listener<T> listener,
                        Response.ErrorListener errorListener, Map<String, String> headers, final RequestListener<T> listenerService) {
        request(buildGetRequest(path, queryParams, responseDataClass, listener, errorListener, headers), listenerService);
    }

    public <REQ, RSP, T> void post(String path, Map<String, String> queryParams,
                                   REQ body, Class<RSP> responseDataClass,
                                   Response.Listener<RSP> listener,
                                   Response.ErrorListener errorListener, LinkedHashMap<String, String> headers, final RequestListener<T> listenerService) {
        request(buildPostRequest(path, queryParams, body, responseDataClass, listener, errorListener, headers), listenerService);
    }

    private String fillUrl(String path) {
        return baseUrl + Bin.fromUtf8(path).toBase64();
    }

    private String fillUrlWithQueryParams(String path, Map<String, String> queryParams) {
        return fillUrl(path) + fillParamStr(queryParams);
    }

    private <T> String prepareBody(T body) {
        try {
            String bodyString;
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

    public static ServiceConnection create(RequestQueue requestQueue, String baseUrl) {
        return new ServiceConnection(requestQueue, baseUrl);
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
