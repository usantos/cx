package caixa.gov.br.loterias.model.bo;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.ResponseTypeValues;

import org.androidannotations.annotations.EBean;

import caixa.gov.br.loterias.BuildConfig;
import caixa.gov.br.loterias.utils.Aplicacao;
import caixa.gov.br.loterias.view.TokenActivity;

import static org.androidannotations.annotations.EBean.Scope.Singleton;

@EBean(scope = Singleton)
public class AcessoBO {

    public static Boolean PRIMEIRO_LOGIN = false;

    public void iniciarLoginSSO(){
        Context context = Aplicacao.application.getApplicationContext();
        if(context != null) {
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
        }
    }

    public void iniciarLogoutSSO(){
        Context context = Aplicacao.application.getApplicationContext();
        if(context != null) {
            AuthorizationServiceConfiguration authorizationServiceConfiguration = new AuthorizationServiceConfiguration(
                    Uri.parse(BuildConfig.URL_SSO_LOGOUT),
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
        }
    }

}
