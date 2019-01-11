package br.gov.caixa.loterias.apostar.interno.model.bo;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.ResponseTypeValues;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import java.util.HashMap;

import br.gov.caixa.loterias.apostar.interno.BuildConfig;
import br.gov.caixa.loterias.apostar.interno.model.bo.keycloak.KeycloakBO;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.internals.GsonRequest;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.internals.SilceConnection;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.internals.SilceSingleton;
import br.gov.caixa.loterias.apostar.interno.utils.AppUtils;
import br.gov.caixa.loterias.apostar.interno.view.TokenActivity;

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
                            public void onRefreshAccess(String token) {
                                //Se retornar o token, tenta fazer a mesma requisição

                                AuthorizationServiceConfiguration authorizationServiceConfiguration = new AuthorizationServiceConfiguration(
                                        Uri.parse(BuildConfig.URL_SSO_AUTH),
                                        Uri.parse(BuildConfig.URL_SSO_TOKEN)
                                );

                                AuthorizationRequest.Builder builder = new AuthorizationRequest.Builder(
                                        authorizationServiceConfiguration,
                                        BuildConfig.SSO_CLIENT_ID,
                                        ResponseTypeValues.CODE,
                                        Uri.parse(BuildConfig.SSO_REDIRECT)
                                );

                                AuthorizationRequest authorizationRequest = builder.build();
                                Intent intent = new Intent(context, TokenActivity.class);
                                PendingIntent pendingIntent = PendingIntent.getActivity(context, authorizationRequest.hashCode(), intent, 0);

                                AuthorizationService authorizationService = new AuthorizationService(context);
                                authorizationService.performAuthorizationRequest(authorizationRequest, pendingIntent);

                                //TO DO
//                                if (keycloakBO.getAccessToken() != null) {
//                                    //Atualiza o token com os headers
//                                    headers.put("Authorization", "Bearer " + keycloakBO.getAccessToken());
//                                    request.setHeaders(headers);
//                                    //Refaz a conexão
//                                    silceConnection.request(request);
//                                } else {
//                                    listener.onErrorResponse(error);
//                                }
                            }
                        });
                    }else{
                        listener.onErrorResponse(error);
                    }
                }else{
                    listener.onErrorResponse(error);
                }
            }
        };
    }
}
