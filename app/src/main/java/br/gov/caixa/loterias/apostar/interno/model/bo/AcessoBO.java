package br.gov.caixa.loterias.apostar.interno.model.bo;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.ResponseTypeValues;

import org.androidannotations.annotations.EBean;

import br.gov.caixa.loterias.apostar.interno.BuildConfig;
import br.gov.caixa.loterias.apostar.interno.model.sp.LoginSP;
import br.gov.caixa.loterias.apostar.interno.utils.Aplicacao;
import br.gov.caixa.loterias.apostar.interno.view.TokenActivity;

import static org.androidannotations.annotations.EBean.Scope.Singleton;

@EBean(scope = Singleton)
public class AcessoBO {

    public static Boolean PRIMEIRO_LOGIN = false;

    public void iniciarLoginSSO(Activity activity){
        if(activity != null) {
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
            Intent intent = new Intent(activity, TokenActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(activity, authorizationRequest.hashCode(), intent, 0);

            AuthorizationService authorizationService = new AuthorizationService(activity);
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

            LoginSP.loginRealizado(false);
        }
    }
}
