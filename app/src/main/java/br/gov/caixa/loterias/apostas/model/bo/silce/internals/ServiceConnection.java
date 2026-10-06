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

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.gov.caixa.loterias.apostas.BuildConfig;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.keycloak.KeycloakBO;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnRefreshTokenListener;
import br.gov.caixa.loterias.apostas.utils.ServicoBffUtil;
import br.gov.caixa.loterias.apostas.utils.ServicoNuvemUtil;

public class ServiceConnection {
    private static final Bin KEY = Bin.fromBase64("QEm06ZAThg1E314zziahsg");
    private static final Bin IV = Bin.fromBase64("8hQLtNV44Y1bIeONmlr+Dg==");
    private static final int TIMEOUT = 30000;
    private static final String TAG_REQUEST = "TAG_REQUEST";

    private final RequestQueue requestQueue;
    private final SilceCrypto silceCrypto = SilceCrypto.create(KEY, IV);

    private String baseUrl;
    private Boolean isEncoding = false;

    public ServiceConnection(RequestQueue requestQueue, String baseUrl, Boolean isEncoding) {
        this.requestQueue = requestQueue;
        this.baseUrl = baseUrl;
        this.isEncoding = isEncoding;

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
                addDefaultHeaders(req);
            }

        } catch (Exception e) {
        }
        if (!precisaRefreshToken) {
            addToQueue(req);
        }
    }

    private <T> OnRefreshTokenListener onRefreshTokenListener(final GsonRequest req,
                                                              final RequestListener<T> listenerService) {
        return new OnRefreshTokenListener() {
            @Override
            public void successRefresh() {
                try {
                    KeycloakBO keycloakBO = KeycloakBO.getInstance();
                    req.getHeaders().put("Authorization", "Bearer " + keycloakBO.getAccessToken());
                    addDefaultHeaders(req);
                } catch (Exception e) {
                }

                addToQueue(req);
            }

            @Override
            public void errorRefresh(VolleyError volleyError) {
                if (listenerService != null) {
                    listenerService.onErrorResponse(volleyError);
                }
            }
        };
    }
    private void addDefaultHeaders(GsonRequest req) throws Exception {
        req.getHeaders().put("cookie", "security=false");
        req.getHeaders().put("Subcanal", "3");
        req.getHeaders().put(ServicoNuvemUtil.APIKEY_DESCRICAO, BuildConfig.APIKEY_CHAVE);
        req.getHeaders().put("content-type", "application/json");
    }
    private void addToQueue(GsonRequest req) {
        requestQueue.add(req);
    }

    private RetryPolicy createRetryPolicy() {
        return new DefaultRetryPolicy(
                TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        );
    }

    public <T> GsonRequest buildGetRequest(String path,
                                           Map<String, String> queryParams,
                                           Class<T> responseDataClass,
                                           Response.Listener<T> listener,
                                           Response.ErrorListener errorListener,
                                           Map<String, String> headers) {

        final String method = "GET";
        final String url = fillUrlWithQueryParams(path, queryParams);
        String inspectorUrl = baseUrl +
                (path.startsWith("/") ? path.substring(1) : path);

        final InspectorContext inspectorContext = createInspectorContext(
                method,
                inspectorUrl,
                path,
                queryParams,
                null,
                null
        );

        GsonRequest request = new GsonRequest<String, T>(
                GET,
                url,
                responseDataClass,
                headers,
                wrapSuccess(inspectorContext, listener),
                wrapError(inspectorContext, errorListener)
        );

        configureRequest(request, inspectorContext);

        Log.i(TAG_REQUEST, getPath(path, queryParams));
        return request;
    }

    public <T> GsonRequest buildGetRequest(String path,
                                           List<Map.Entry<String, String>> queryParams,
                                           Class<T> responseDataClass,
                                           Response.Listener<T> listener,
                                           Response.ErrorListener errorListener,
                                           Map<String, String> headers) {

        final String method = "GET";
        final String url = fillUrlWithQueryParams(path, queryParams);
        String inspectorUrl = baseUrl +
                (path.startsWith("/") ? path.substring(1) : path);

        final InspectorContext inspectorContext = createInspectorContext(
                method,
                inspectorUrl,
                path,
                null,
                null,
                null
        );

        GsonRequest request = new GsonRequest<String, T>(
                GET,
                url,
                responseDataClass,
                headers,
                wrapSuccess(inspectorContext, listener),
                wrapError(inspectorContext, errorListener)
        );

        configureRequest(request, inspectorContext);

        return request;
    }

    public <REQ, RSP> GsonRequest buildPostRequest(String path,
                                                   Map<String, String> queryParams,
                                                   REQ body,
                                                   Class<RSP> responseDataClass,
                                                   Response.Listener<RSP> listener,
                                                   Response.ErrorListener errorListener,
                                                   Map<String, String> headers) {

        final String method = "POST";
        final String url = fillUrlWithQueryParams(path, queryParams);
        final String preparedBody = prepareBody(body, path);
        String inspectorUrl = baseUrl +
                (path.startsWith("/") ? path.substring(1) : path);

        final InspectorContext inspectorContext = createInspectorContext(
                method,
                inspectorUrl,
                path,
                queryParams,
                body,
                preparedBody
        );

        GsonRequest request = new GsonRequest<>(
                POST,
                url,
                preparedBody,
                responseDataClass,
                headers,
                wrapSuccess(inspectorContext, listener),
                wrapError(inspectorContext, errorListener)
        );
        configureRequest(request, inspectorContext);
        Log.i(TAG_REQUEST, getPath(path, queryParams));
        return request;
    }

    public <REQ, RSP> GsonRequest buildPutRequest(String path,
                                                  Map<String, String> queryParams,
                                                  REQ body,
                                                  Class<RSP> responseDataClass,
                                                  Response.Listener<RSP> listener,
                                                  Response.ErrorListener errorListener,
                                                  Map<String, String> headers) {

        final String method = "PUT";
        final String url = fillUrlWithQueryParams(path, queryParams);
        final String preparedBody = prepareBody(body, path);
        String inspectorUrl = baseUrl +
                (path.startsWith("/") ? path.substring(1) : path);

        final InspectorContext inspectorContext = createInspectorContext(
                method,
                inspectorUrl,
                path,
                queryParams,
                body,
                preparedBody
        );

        GsonRequest request = new GsonRequest<>(
                PUT,
                url,
                preparedBody,
                responseDataClass,
                headers,
                wrapSuccess(inspectorContext, listener),
                wrapError(inspectorContext, errorListener)
        );
        configureRequest(request, inspectorContext);
        Log.i(TAG_REQUEST, getPath(path, queryParams));
        return request;
    }

    public <T> GsonRequest buildDeleteRequest(String path,
                                              Map<String, String> queryParams,
                                              Class<T> responseDataClass,
                                              Response.Listener<T> listener,
                                              Response.ErrorListener errorListener,
                                              Map<String, String> headers) {

        final String method = "DELETE";
        final String url = fillUrlWithQueryParams(path, queryParams);
        String inspectorUrl = baseUrl +
                (path.startsWith("/") ? path.substring(1) : path);

        final InspectorContext inspectorContext = createInspectorContext(
                method,
                inspectorUrl,
                path,
                queryParams,
                null,
                null
        );

        GsonRequest request = new GsonRequest<String, T>(
                DELETE,
                url,
                responseDataClass,
                headers,
                wrapSuccess(inspectorContext, listener),
                wrapError(inspectorContext, errorListener)
        );
        configureRequest(request, inspectorContext);
        Log.i(TAG_REQUEST, url);
        return request;
    }

    private void configureRequest(GsonRequest request, final InspectorContext inspectorContext) {
        request.setRetryPolicy(createRetryPolicy());
        configureStatusCodeCapture(request, inspectorContext);
        markRequestSent(request, inspectorContext);
    }

    private void configureStatusCodeCapture(GsonRequest request, final InspectorContext inspectorContext) {
        if (!BuildConfig.ENABLE_NETWORK_INSPECTOR || request == null || inspectorContext == null) {
            return;
        }

        request.setOnHttpStatusListener(statusCode -> {
            if (statusCode > 0) {
                inspectorContext.statusCode = statusCode;
                updateInspectorStatusCode(inspectorContext);
            }
        });
    }


    private void markRequestSent(GsonRequest request, InspectorContext inspectorContext) {
        if (!BuildConfig.ENABLE_NETWORK_INSPECTOR || request == null || inspectorContext == null) {
            return;
        }

        try {
            NetworkInspector.markRequestSent(
                    inspectorContext.entryKey,
                    inspectorContext.method,
                    inspectorContext.url,
                    safeHeaders(request)
            );
        } catch (Exception ignored) {
        }
    }

    private InspectorContext createInspectorContext(String method,
                                                    String url,
                                                    String path,
                                                    Map<String, String> queryParams,
                                                    Object originalBody,
                                                    String preparedBody) {
        if (!BuildConfig.ENABLE_NETWORK_INSPECTOR) {
            return null;
        }

        try {
            String entryKey = NetworkInspector.createEntry(
                    method,
                    url,
                    path,
                    queryParams,
                    originalBody,
                    preparedBody
            );

            return new InspectorContext(entryKey, method, url);

        } catch (Exception e) {
            return null;
        }
    }

    private void updateInspectorStatusCode(InspectorContext inspectorContext) {
        if (!BuildConfig.ENABLE_NETWORK_INSPECTOR || inspectorContext == null) {
            return;
        }

        if (inspectorContext.entryKey == null || inspectorContext.statusCode <= 0) {
            return;
        }

        try {
            NetworkInspector.setStatusCode(
                    inspectorContext.entryKey,
                    inspectorContext.statusCode
            );
        } catch (Exception ignored) {
        }
    }

    private Map<String, String> safeHeaders(GsonRequest req) {
        try {
            Map<String, String> headers = req.getHeaders();

            if (headers == null) {
                return new HashMap<>();
            }

            return new HashMap<>(headers);

        } catch (Exception e) {
            return new HashMap<>();
        }
    }

    private <T> Response.Listener<T> wrapSuccess(final InspectorContext inspectorContext,
                                                 final Response.Listener<T> original) {
        return new Response.Listener<T>() {
            @Override
            public void onResponse(T response) {
                if (BuildConfig.ENABLE_NETWORK_INSPECTOR) {
                    try {
                        updateInspectorStatusCode(inspectorContext);

                        NetworkInspector.markSuccess(
                                getEntryKey(inspectorContext),
                                getMethod(inspectorContext),
                                getUrl(inspectorContext),
                                response
                        );

                    } catch (Exception ignored) {
                    }
                }

                if (original != null) {
                    original.onResponse(response);
                }
            }
        };
    }

    private Response.ErrorListener wrapError(final InspectorContext inspectorContext,
                                             final Response.ErrorListener original) {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (BuildConfig.ENABLE_NETWORK_INSPECTOR) {
                    try {
                        int statusCode = getStatusCodeFromError(error);
                        String errorBody = getErrorBodyFromError(error);

                        if (inspectorContext != null && statusCode > 0) {
                            inspectorContext.statusCode = statusCode;
                        }

                        NetworkInspector.markError(
                                getEntryKey(inspectorContext),
                                getMethod(inspectorContext),
                                getUrl(inspectorContext),
                                error,
                                statusCode,
                                errorBody
                        );
                    } catch (Exception ignored) {
                    }
                }
                if (original != null) {
                    original.onErrorResponse(error);
                }
            }
        };
    }
    private int getStatusCodeFromError(VolleyError error) {
        if (error == null || error.networkResponse == null) {
            return -1;
        }
        return error.networkResponse.statusCode;
    }
    private String getErrorBodyFromError(VolleyError error) {
        if (error == null || error.networkResponse == null || error.networkResponse.data == null) {
            return "";
        }
        try {
            return new String(error.networkResponse.data, "UTF-8");
        } catch (Exception e) {
            return "";
        }
    }
    private String getEntryKey(InspectorContext inspectorContext) {
        return inspectorContext != null ? inspectorContext.entryKey : null;
    }
    private String getMethod(InspectorContext inspectorContext) {
        return inspectorContext != null ? inspectorContext.method : "";
    }
    private String getUrl(InspectorContext inspectorContext) {
        return inspectorContext != null ? inspectorContext.url : "";
    }
    private String fillUrl(String path) {
        if (isEncoding) {
            String pathSemBarra = path;
            if (path.startsWith("/")) {
                pathSemBarra = path.substring(1);
            }
            return baseUrl + Bin.fromUtf8(pathSemBarra).toBase64();
        } else {
            return baseUrl + path;
        }
    }

    private String fillUrlWithQueryParamsNuvem(String path, Map<String, String> queryParams) {
        String baseSemBarra = baseUrl;
        if (baseUrl.endsWith("/")) {
            baseSemBarra = baseUrl.substring(0, baseUrl.length() - 1);
        }
        return baseSemBarra + path + fillParamToString(queryParams);
    }

    private String fillUrlWithQueryParams(String path, Map<String, String> queryParams) {
        if (ServicoNuvemUtil.isEndpointNuvem(path)) {
            if (ServicoNuvemUtil.isAmbienteTQS() && baseUrl != null && ServicoBffUtil.getBaseUrlBff() != null &&
                    ServicoBffUtil.getBaseUrlBff().equalsIgnoreCase(baseUrl)) {
                ServicoNuvemUtil.apresentaCaminhoTQS(baseUrl + path);
            }
            return fillUrlWithQueryParamsNuvem(path, queryParams);
        } else {
            return fillUrl(path) + fillParamStr(queryParams);
        }
    }
    private String fillUrlWithQueryParams(String path, List<Map.Entry<String, String>> queryParams) {
        return fillUrl(path) + fillParamStr(queryParams);
    }
    private <T> String prepareBody(T body, String endPoint) {
        if (ServicoNuvemUtil.isEndpointNuvem(endPoint)) {
            return new Gson().toJson(body);
        } else {
            try {
                String bodyString;
                if (body == null) {
                    bodyString = "{}";
                } else {
                    bodyString = new Gson().toJson(body);
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
    private String fillParamStr(List<Map.Entry<String, String>> queryParams) {
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

    private String fillParamToString(Map<String, String> queryParams) {
        if ((queryParams == null) || (queryParams.isEmpty())) {
            return "";
        }
        StringBuilder singleParam = new StringBuilder();
        for (Map.Entry<String, String> param : queryParams.entrySet()) {
            if (param.getValue() != null) {
                singleParam.append(param.getKey())
                        .append("=")
                        .append(param.getValue())
                        .append("&");
            }
        }
        return "/?" + singleParam.toString().substring(0, singleParam.toString().length() - 1);
    }

    private String getMethod(int method) {
        switch (method) {
            case GET:
                return "GET";
            case POST:
                return "POST";
            case PUT:
                return "PUT";
            case DELETE:
                return "DELETE";
            default:
                return "UNKNOWN";
        }
    }

    private static class InspectorContext {
        final String entryKey;
        final String method;
        final String url;
        int statusCode = -1;

        InspectorContext(String entryKey, String method, String url) {
            this.entryKey = entryKey;
            this.method = method;
            this.url = url;
        }
    }
}