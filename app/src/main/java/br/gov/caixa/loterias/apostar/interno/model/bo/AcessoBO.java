package br.gov.caixa.loterias.apostar.interno.model.bo;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.MainThread;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import net.openid.appauth.AppAuthConfiguration;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.ClientAuthentication;
import net.openid.appauth.ResponseTypeValues;
import net.openid.appauth.TokenRequest;
import net.openid.appauth.TokenResponse;

import org.androidannotations.annotations.EBean;

import br.gov.caixa.loterias.apostar.interno.BuildConfig;
import br.gov.caixa.loterias.apostar.interno.model.bo.AppAuth.AuthStateManager;
import br.gov.caixa.loterias.apostar.interno.model.bo.AppAuth.Configuration;
import br.gov.caixa.loterias.apostar.interno.model.sp.LoginSP;
import br.gov.caixa.loterias.apostar.interno.utils.Aplicacao;
import br.gov.caixa.loterias.apostar.interno.model.bo.AppAuth.TokenActivity;

import static org.androidannotations.annotations.EBean.Scope.Singleton;

@EBean(scope = Singleton)
public class AcessoBO {

    public static Boolean PRIMEIRO_LOGIN = false;
    public AuthorizationService mAuthService;
    public AuthStateManager mStateManager;

    public void iniciarLoginSSO(){
        Context context = Aplicacao.application.getApplicationContext();

        Configuration config = Configuration.getInstance(context);

        mStateManager = AuthStateManager.getInstance(context);
        mAuthService = new AuthorizationService(
                context,
                new AppAuthConfiguration.Builder()
                        .setConnectionBuilder(config.getConnectionBuilder())
                        .build());

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

            LoginSP.loginRealizado(false);
        }
    }

    @MainThread
    public void refreshAccessToken() {
        performTokenRequest(
                mStateManager.getCurrent().createTokenRefreshRequest(),
                this::handleAccessTokenResponse);
    }

    @WorkerThread
    private void handleAccessTokenResponse(
            @Nullable TokenResponse tokenResponse,
            @Nullable AuthorizationException authException) {
        mStateManager.updateAfterTokenResponse(tokenResponse, authException);
    }

    @MainThread
    private void performTokenRequest(
            TokenRequest request,
            AuthorizationService.TokenResponseCallback callback) {
        ClientAuthentication clientAuthentication;
        try {
            clientAuthentication = mStateManager.getCurrent().getClientAuthentication();
        } catch (ClientAuthentication.UnsupportedAuthenticationMethod ex) {
            return;
        }

        mAuthService.performTokenRequest(
                request,
                clientAuthentication,
                callback);
    }
}
