package br.gov.caixa.loterias.apostas.model.bo;

import android.content.Context;
import android.net.Uri;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;

import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.TokenRequest;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import br.gov.caixa.loterias.apostas.BuildConfig;
import br.gov.caixa.loterias.apostas.model.bean.ErrorResponse;
import br.gov.caixa.loterias.apostas.model.bo.keycloak.KeycloakBO;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.Bin;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.CertificadoMicroServicoSingleton;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.ConfiguracaoAppSingleton;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.GsonRequest;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.MicroServicoConnection;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.MicroServicoSingleton;
import br.gov.caixa.loterias.apostas.utils.AppCenterManager;
import br.gov.caixa.loterias.apostas.utils.AppUtils;
import br.gov.caixa.loterias.apostas.utils.AuthorizationServiceFactory;

@EBean
public class BaseMicroServicoBO {

    protected MicroServicoConnection connection;

    @RootContext
    protected Context context;

    @AfterInject
    protected void init(){
    }

    protected <T> Response.ErrorListener interceptError(final GsonRequest request, final Map<String, String> headers, final RequestListener<T> listener){
        return error -> {
            if(AppUtils.isNetworkAvailable(context)){
                registraErro(error, request);

                if (MensagensNetwork.isCertificadoInvalido(error)){
                    if (CertificadoMicroServicoSingleton.getInstance().trocaCertificado()){
                        MicroServicoSingleton.getInstance().initQueue();
                        connection = new MicroServicoConnection(MicroServicoSingleton.getInstance().getRequestQueue(), ConfiguracaoAppSingleton.getInstance().getUrlBaseBuscaApostas());
                        connection.request(request, listener);
                        return;
                    } else {
                        listener.onErrorResponse(error);
                    }
                }

                //Caso não esteja autorizado tenta atualizar o token de acesso
                if((MensagensNetwork.isUnauthorizedError(error) || error instanceof AuthFailureError) && KeycloakBO.getInstance().getAccessToken() != null) {
                    KeycloakBO.getInstance().refreshAccess(() -> {
                        //Se retornar o token, tenta fazer a mesma requisição

                        AuthorizationServiceConfiguration authorizationServiceConfiguration = new AuthorizationServiceConfiguration(
                                Uri.parse(BuildConfig.URL_SSO_AUTH),
                                Uri.parse(BuildConfig.URL_SSO_TOKEN)
                        );

                        AuthorizationService authorizationService = AuthorizationServiceFactory.getAuthorizationService(context);
                        TokenRequest.Builder tokenBuilder = new TokenRequest.Builder(authorizationServiceConfiguration, BuildConfig.SSO_CLIENT_ID);
                        tokenBuilder.setRefreshToken(KeycloakBO.getInstance().getRefreshToken());
                        tokenBuilder.setGrantType("refresh_token");

                        TokenRequest tokenRequest = tokenBuilder.build();

                        authorizationService.performTokenRequest(tokenRequest, (response, ex) -> {
                            if (response != null) {
                                if (response.accessToken == null || response.refreshToken == null) {
                                    listener.onErrorResponse(error);
                                } else {
                                    KeycloakBO.getInstance().setAccessToken(response.accessToken);
                                    KeycloakBO.getInstance().setRefreshToken(response.refreshToken);

                                    if (KeycloakBO.getInstance().getAccessToken() != null) {
                                        //Atualiza o token com os headers
                                        headers.put("Authorization", "Bearer " + KeycloakBO.getInstance().getAccessToken());
                                        request.setHeaders(headers);
                                        //Refaz a conexão
                                        connection.request(request, listener);
                                    } else {
                                        listener.onErrorResponse(error);
                                    }
                                }
                            }else {
                                listener.onErrorResponse(error);
                            }
                        });
                    });
                } else{
                    listener.onErrorResponse(error);
                }
            } else{
                listener.onErrorResponse(error);
            }
        };
    }


    private void registraErro(VolleyError error, GsonRequest request){
        ErrorResponse   errorResponse = transformaErroEmErrorResponse(error);
        NetworkResponse response      = error.networkResponse;
        if(response != null && response.statusCode == 500){
           AppCenterManager.registraErro(errorResponse.getCodigo(), getEndpoint(request));
        }
    }

    private String getEndpoint(GsonRequest request){
        if (request.getUrl().contains("/?")) {
            return "URL crashando app";
        }
        return Bin.fromBase64(request.getUrl().replaceAll(BuildConfig.CAIXA_BASE_URL_SILCE,"")).toUtf8();
    }


    public static ErrorResponse transformaErroEmErrorResponse(VolleyError error){
        ErrorResponse errorResponse = new ErrorResponse();
        Gson          gsonResponse  = new Gson();
        String        body;
        if(error != null && error.networkResponse != null && error.networkResponse.data != null) {
            try {
                body = new String(error.networkResponse.data,"UTF-8");
                errorResponse = gsonResponse.fromJson(body, ErrorResponse.class);

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        return errorResponse;
    }
}
