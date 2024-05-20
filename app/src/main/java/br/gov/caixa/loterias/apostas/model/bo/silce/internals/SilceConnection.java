package br.gov.caixa.loterias.apostas.model.bo.silce.internals;

import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import br.gov.caixa.loterias.apostas.BuildConfig;
import br.gov.caixa.loterias.apostas.model.bo.DadosUsuarioBO;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.keycloak.KeycloakBO;
import br.gov.caixa.loterias.apostas.model.bo.keycloak.KeycloakBO_;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnRefreshTokenListener;
import br.gov.caixa.loterias.apostas.utils.Aplicacao;

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

    private static final String AZURE_API = "azure-api.net";

    private static final String PUBLICO = "/publico";
    private static final String LOGADO = "/logado";
    private static final List<String> listPublico = Arrays.asList(
                                        "/equipes-esportivas/listaEquipes",
                                        "/parametros-simulacao",
                                        "/combos");
    private static final List<String> listLogadoExato = Arrays.asList(
                                        "/carrinhos",
                                        "/carrinhos/atualiza-teimosinha-aposta",
                                        "/carrinhos/incluir-aposta",
                                        "/carrinhos/incluir-apostas",
                                        "/carrinhos/incluir-combo",
                                        "/carrinhos/incluir-surpresinha",
                                        "/carrinhos/incluir-surpresinhas",
                                        "/carrinhos/iniciar-pagamento-externo",
                                        "/carrinhos/limpar",
                                        "/carrinhos/limpar-carrinho-favorito",
                                        "/carrinhos/registrar-apostas",
                                        "/carrinhos/registrar-apostas-async",
                                        "/carrinhos/registrar-apostas-sem-carrinho",
                                        "/carrinhos/salvar-carrinho-favorito",
                                        "/carrinhos/validar",
                                        "/carrinhos/validar-aposta-identica",
                                        "/carrinhos/verifica-compra-processamento",
                                        "/usuarios/verificar-cadastro");
    private static final List<String> listLogadoParcial = Arrays.asList(
                                        "/apostas-favoritas",
                                        "/carrinhos-favoritos",
                                        "/carrinhos/apostas",
                                        "/carrinhos/combos",
                                        "/carrinhos/incluir-aposta-favorita",
                                        "/carrinhos/incluir-carrinho-favorito");

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

    public <T> void request(GsonRequest req, final RequestListener<T> listenerService) {
        boolean precisaRefreshToken = false;
        try {
            KeycloakBO keycloakBO = KeycloakBO_.getInstance_(Aplicacao.application.getApplicationContext());
            if (req.getHeaders() == null) {
                req.setHeaders(new HashMap<>());
            } else if (req.getHeaders().get("Authorization") != null && keycloakBO.isPrecisaRefreshToken()) {
                precisaRefreshToken = true;
                keycloakBO.realizaRefreshToken(onRefreshTokenListener(req, listenerService));
            }
            if (!precisaRefreshToken) {
                req.getHeaders().put("cookie", "security=false");
                req.getHeaders().put("Subcanal", "3");
                //Apenas para teste
                req.getHeaders().put("ocp-apim-subscription-key", "0e7610943a994c118c171031b54ed4b7");
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
                    KeycloakBO keycloakBO = KeycloakBO_.getInstance_(Aplicacao.application.getApplicationContext());
                    req.getHeaders().put("Authorization", "Bearer " + keycloakBO.getAccessToken());
                    req.getHeaders().put("cookie", "security=false");
                    req.getHeaders().put("Subcanal", "3");
                    //Necessario no Carrinho Nuvem
                    req.getHeaders().put("ocp-apim-subscription-key", "0e7610943a994c118c171031b54ed4b7");
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
        //Apenas para teste
        GsonRequest request;
        if (url.contains(AZURE_API)) {
            request = new GsonRequest<>(POST, url, body, responseDataClass, headers, listener, errorListener);
        } else {
            request = new GsonRequest<>(POST, url, prepareBody(body), responseDataClass, headers, listener, errorListener);
        }

        request.setRetryPolicy(createRetryPolicy());
        Log.i(TAG_REQUEST, getPath(path, queryParams));
        return request;
    }

    public <REQ, RSP> GsonRequest buildPutRequest(String path, Map<String, String> queryParams,
                                                  REQ body, Class<RSP> responseDataClass,
                                                  Response.Listener<RSP> listener,
                                                  Response.ErrorListener errorListener, Map<String, String> headers) {

        String url = fillUrlWithQueryParams(path, queryParams);
        //Apenas para teste
        GsonRequest request;
        if (url.contains(AZURE_API)) {
            request = new GsonRequest<>(PUT, url, body, responseDataClass, headers, listener, errorListener);
        } else {
            request = new GsonRequest<>(PUT, url, prepareBody(body), responseDataClass, headers, listener, errorListener);
        }

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

        String novoCaminho = alteraCaminhoCarrinho(path);
        if (novoCaminho != null) {
            return novoCaminho + fillParamToString(queryParams);
        }

        return fillUrl(path) + fillParamStr(queryParams);
    }

    private <T> String prepareBody(T body) {
        try {
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

//    public void nuke() {
//        SSLPiniingUtil.pinagem();
//    }

    private String fillParamToString(Map<String, String> queryParams) {
        if ((queryParams == null) || (queryParams.isEmpty())) {
            return "";
        }
        StringBuilder singleParam = new StringBuilder();
        for (Map.Entry<String, String> param : queryParams.entrySet()) {
            if (param.getValue() != null) {
                singleParam.append(param.getKey()).append("==").append(param.getValue()).append("&&");
            }
        }
        return "/?q=" + singleParam.toString();
    }

    private String alteraCaminhoCarrinho(String path) {

        String baseUrlCarrinho;

        if(BuildConfig.DEBUG && !BuildConfig.FLAVOR.equalsIgnoreCase("prd")) {
            baseUrlCarrinho = ConfiguracaoAppSingleton.getInstance().getUrlBaseCarrinhoPiloto();

            //Apenas para teste
            baseUrlCarrinho = "https://apim-lce-des.azure-api.net/loterias-web/carrinho/v1";
        } else {
            baseUrlCarrinho = ConfiguracaoAppSingleton.getInstance().getUrlBaseCarrinhoProducao();
        }

        if (baseUrlCarrinho.equals("-")) {
            return null;
        }

        if (listPublico.contains(path)) {
            if (finalCPFAcessaNuvem()) {
                return baseUrlCarrinho + PUBLICO + path;
            }
            return null;
        }

        if (listLogadoExato.contains(path)) {
            if (finalCPFAcessaNuvem()) {
                return baseUrlCarrinho + LOGADO + path;
            }
            return null;
        }

        for(String parcial : listLogadoParcial) {
            if (parcial.startsWith(path)) {
                if (finalCPFAcessaNuvem()) {
                    return baseUrlCarrinho + LOGADO + path;
                }
                return null;
            }
        }

        return null;
    }

    private boolean finalCPFAcessaNuvem() {

        int finalCpfAcessamNuvem = ConfiguracaoAppSingleton.getInstance().getGrupoDeCpfsQueAcessamOServicoDaAzure();

        //Apenas para teste
        finalCpfAcessamNuvem = 100;

        if (finalCpfAcessamNuvem == 0) {
            return false;
        }

        String cpf = DadosUsuarioBO.obterCpf();
        if (cpf != null && !cpf.isEmpty() && cpf.length() >= 2) {
            //Verifica se o final do CPF < finalCpfAcessamNuvem
            if (Integer.parseInt(cpf.substring(cpf.length() -2)) < finalCpfAcessamNuvem) {
                return true;
            }
        }

        if (finalCpfAcessamNuvem == 100) {
            return true;
        }

        return false;
    }
}
