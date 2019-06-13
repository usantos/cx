package br.gov.caixa.loterias.apostar.interno.model.bo;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.TokenRequest;
import net.openid.appauth.TokenResponse;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import java.util.HashMap;

import br.gov.caixa.loterias.apostar.interno.BuildConfig;
import br.gov.caixa.loterias.apostar.interno.model.bo.keycloak.KeycloakBO;
import br.gov.caixa.loterias.apostar.interno.model.bo.keycloak.KeycloakBO_;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.internals.GsonRequest;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.internals.SilceConnection;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.internals.SilceSingleton;
import br.gov.caixa.loterias.apostar.interno.utils.AppUtils;

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
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(final VolleyError error) {
                if(AppUtils.isNetworkAvailable(context)){
                    //Caso não esteja autorizado tenta atualizar o token de acesso
                    if(MensagensNetwork.isUnauthorizedError(error) && keycloakBO.getAccessToken() != null) {
                        keycloakBO.refreshAccess(new KeycloakBO.OnRefreshAccess() {
                            @Override
                            public void onRefreshAccess() {
                                //Se retornar o token, tenta fazer a mesma requisição

                                AuthorizationServiceConfiguration authorizationServiceConfiguration = new AuthorizationServiceConfiguration(
                                        Uri.parse(BuildConfig.URL_SSO_AUTH),
                                        Uri.parse(BuildConfig.URL_SSO_TOKEN)
                                );

                                AuthorizationService authorizationService = new AuthorizationService(context);
                                TokenRequest.Builder tokenBuilder = new TokenRequest.Builder(authorizationServiceConfiguration, BuildConfig.SSO_CLIENT_ID);
                                tokenBuilder.setRefreshToken(keycloakBO.getRefreshToken());
                                tokenBuilder.setGrantType("refresh_token");

                                TokenRequest tokenRequest = tokenBuilder.build();

                                authorizationService.performTokenRequest(tokenRequest, new AuthorizationService.TokenResponseCallback() {
                                    @Override
                                    public void onTokenRequestCompleted(@Nullable TokenResponse response, @Nullable AuthorizationException ex) {
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
                                        } else {
                                            listener.onErrorResponse(error);
                                        }
                                    }
                                });
                            }
                        });
                    } else{
                        listener.onErrorResponse(error);
                    }
                } else{
                    listener.onErrorResponse(error);
                }
            }
        };
    }
}
