package caixa.gov.br.loterias.bo.silce.internals;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.android.volley.Request.Method.GET;
import static com.android.volley.Request.Method.POST;

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
        GsonRequest<String, T> req = new GsonRequest<>(GET, url, responseDataClass,
                new LinkedHashMap<String, String>(), listener, errorListener);
        requestQueue.add(req);
        requestQueue.start();
    }

    public <REQ, RSP> void post(String path, Map<String, String> queryParams,
                                REQ body, Class<RSP> responseDataClass,
                                Response.Listener<RSP> listener,
                                Response.ErrorListener errorListener) {
        String url = fillUrlWithQueryParams(path, queryParams);
        GsonRequest<String, RSP> req = new GsonRequest<>(POST, url,
                prepareBody(body), responseDataClass,
                new LinkedHashMap<String, String>(), listener, errorListener);
        requestQueue.add(req);
        requestQueue.start();
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
            Bin encryptedBody = silceCrypto.encryptBody((new Gson()).toJson(body));
            String bodyStr = encryptedBody.toIso8859_1()
                    + "!#!#!" + KEY.toIso8859_1()
                    + "!#!#!" +  IV.toIso8859_1();
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
        if (error.networkResponse != null) {
            return "Network error: " +
                    error.networkResponse.statusCode + " - " +
                    Bin.fromBytes(error.networkResponse.data).toUtf8();
        } else {
            return "Exception when trying to reach the server: "
                    + error.toString();
        }
    }
}
