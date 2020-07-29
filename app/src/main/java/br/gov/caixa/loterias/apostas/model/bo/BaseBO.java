package br.gov.caixa.loterias.apostas.model.bo;

import android.content.Context;
import android.net.Uri;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;

import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.TokenRequest;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import br.gov.caixa.loterias.apostas.BuildConfig;
import br.gov.caixa.loterias.apostas.model.bean.ErrorResponse;
import br.gov.caixa.loterias.apostas.model.bo.keycloak.KeycloakBO;
import br.gov.caixa.loterias.apostas.model.bo.keycloak.KeycloakBO_;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.Bin;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.GsonRequest;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.SilceConnection;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.SilceSingleton;
import br.gov.caixa.loterias.apostas.utils.AppUtils;
import br.gov.caixa.loterias.apostas.utils.AuthorizationServiceFactory;
import br.gov.caixa.loterias.apostas.utils.FirebaseManager;

/**
 * Created by pmotta on 21/03/2018.
 */

@EBean
public class BaseBO {

    protected SilceConnection silceConnection;

    @Bean
    protected SilceSingleton silceSingleton;

    @Bean
    protected KeycloakBO keycloakBO;

    @RootContext
    protected Context context;

    @AfterInject
    protected void init(){
        silceConnection = SilceConnection.create(silceSingleton.getRequestQueue(), BuildConfig.CAIXA_BASE_URL_SILCE);
    }

    protected <T> Response.ErrorListener interceptError(final GsonRequest request, final HashMap<String, String> headers, final RequestListener<T> listener){
        return error -> {
            if(AppUtils.isNetworkAvailable(context)){
                registraErro(error, request);
                //Caso não esteja autorizado tenta atualizar o token de acesso
                if(MensagensNetwork.isUnauthorizedError(error) && keycloakBO.getAccessToken() != null) {
                    keycloakBO.refreshAccess(() -> {
                        //Se retornar o token, tenta fazer a mesma requisição

                        AuthorizationServiceConfiguration authorizationServiceConfiguration = new AuthorizationServiceConfiguration(
                                Uri.parse(BuildConfig.URL_SSO_AUTH),
                                Uri.parse(BuildConfig.URL_SSO_TOKEN)
                        );

                        AuthorizationService authorizationService = AuthorizationServiceFactory.getAuthorizationService(context);
                        TokenRequest.Builder tokenBuilder = new TokenRequest.Builder(authorizationServiceConfiguration, BuildConfig.SSO_CLIENT_ID);
                        tokenBuilder.setRefreshToken(keycloakBO.getRefreshToken());
                        tokenBuilder.setGrantType("refresh_token");

                        TokenRequest tokenRequest = tokenBuilder.build();

                        authorizationService.performTokenRequest(tokenRequest, (response, ex) -> {
                            if (response != null) {
                                if (response.accessToken == null || response.refreshToken == null) {
                                    listener.onErrorResponse(error);
                                } else {
                                    KeycloakBO_.getInstance_(context).setAccessToken(response.accessToken);
                                    KeycloakBO_.getInstance_(context).setRefreshToken(response.refreshToken);

                                    if (keycloakBO.getAccessToken() != null) {
                                        //Atualiza o token com os headers
                                        headers.put("Authorization", "Bearer " + keycloakBO.getAccessToken());
                                        request.setHeaders(headers);
                                        //Refaz a conexão
                                        silceConnection.request(request);
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
            FirebaseManager.registraErro(context,errorResponse.getCodigo(),getEndpoint(request));
        }
    }

    private String getEndpoint(GsonRequest request){
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
