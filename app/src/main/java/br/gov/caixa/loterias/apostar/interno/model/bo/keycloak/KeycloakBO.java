package br.gov.caixa.loterias.apostar.interno.model.bo.keycloak;

/**
 * Created by igorvilar on 06/09/17.
 */

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.util.Pair;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EBean;
import org.jboss.aerogear.android.authorization.AuthorizationManager;
import org.jboss.aerogear.android.authorization.AuthzModule;
import org.jboss.aerogear.android.authorization.oauth2.OAuth2AuthorizationConfiguration;
import org.jboss.aerogear.android.pipe.module.AuthorizationFields;

import java.net.URL;
import java.util.List;

import br.gov.caixa.loterias.apostar.interno.BuildConfig;
import br.gov.caixa.loterias.apostar.interno.model.bo.DadosUsuarioBO;

@EBean(scope = EBean.Scope.Singleton)
public class KeycloakBO {
    private static final String TAG = KeycloakBO.class.getName();

    private static final String SERVER_URL = BuildConfig.URL_SSO;
    //private static final String SERVER_URL = "http://10.130.9.22:81";
    private static final String AUTH_URL = SERVER_URL +"/auth";
    private static final String AUTH_ENDPOINT = "/realms/internet/protocol/openid-connect/auth";
    private static final String ACCESS_TOKEN_ENDPOINT = "/realms/internet/protocol/openid-connect/token";
    private static final String REFRESH_TOKEN_ENDPOINT = "/realms/internet/protocol/openid-connect/token";
    private static final String AUTHZ_ACCOOUNT_ID = "keycloak-token";
    private static final String AUTH_CLIENT_ID = "cli-mob-lce";
//    private static final String AUTH_REDIRECT_URL = "http://localhost";
    private static final String AUTH_REDIRECT_URL = BuildConfig.SSO_REDIRECT;
    private static final String MODULE_NAME = "";
    private static final String CLIENT_SECRET = "b54d171d-03de-41f3-b1b1-64e98672cbb8";

    private String accessToken;

    public String getAccessToken(){
        return accessToken;
    }

    public void setAccessToken(String accessToken){
        this.accessToken = accessToken;
    }

    @AfterInject
    protected void init(){
        try {
            AuthorizationManager.config(MODULE_NAME, OAuth2AuthorizationConfiguration.class)
                    .setBaseURL(new URL(AUTH_URL))
                    .setAuthzEndpoint(AUTH_ENDPOINT)
                    .setAccessTokenEndpoint(ACCESS_TOKEN_ENDPOINT)
                    .setRefreshEndpoint(REFRESH_TOKEN_ENDPOINT)
                    .setAccountId(AUTHZ_ACCOOUNT_ID)
                    .setClientId(AUTH_CLIENT_ID)
                    .setClientSecret(CLIENT_SECRET)
                    .setRedirectURL(AUTH_REDIRECT_URL)
                    .asModule();
        } catch (Exception e) {
            throw new RuntimeException("Não foi possível inicializar os serviços de " +
                    "autenticação OAUTH2 por um erro de configuração", e);
        }
    }

    public void refreshAccess(OnRefreshAccess listener){
        new RefreshAccessTask(this, listener).execute();
    }

    private String refreshAccess(){

        return "";
    }

    public void logout(Activity activity){
        DadosUsuarioBO.updateUsuarioLogado( activity, false );
        final AuthzModule authzModule = AuthorizationManager.getModule(MODULE_NAME);
        authzModule.deleteAccount();
        accessToken = null;
        clearCookies(activity);
        WebView webView = new WebView(activity);
        webView.clearCache(true);
    }

    public void limparSessao(Activity activity){
        DadosUsuarioBO.updateUsuarioLogado( activity, false );
        accessToken = null;
        clearCookies(activity);
        WebView webView = new WebView(activity);
        webView.clearCache(true);
    }

    @SuppressWarnings("deprecation")
    private void clearCookies(Context context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            Log.d(TAG, "Using clearCookies code for API >=" + String.valueOf(Build.VERSION_CODES.LOLLIPOP_MR1));
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
        } else {
            Log.d(TAG, "Using clearCookies code for API <" + String.valueOf(Build.VERSION_CODES.LOLLIPOP_MR1));
            CookieSyncManager cookieSyncMngr = CookieSyncManager.createInstance(context);
            cookieSyncMngr.startSync();
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
            cookieSyncMngr.stopSync();
            cookieSyncMngr.sync();
        }
    }

    public interface OnRefreshAccess{
        void onRefreshAccess(String token);
    }

    public interface OnLogin{
        void onSuccess(String token);
        void onFailure(Exception e);
    }

    private static class RefreshAccessTask extends AsyncTask<Void, Void, String>{
        private KeycloakBO keycloakBO;
        private OnRefreshAccess listener;

        private RefreshAccessTask(KeycloakBO keycloakBO, OnRefreshAccess listener){
            this.keycloakBO = keycloakBO;
            this.listener = listener;
        }

        @Override
        protected String doInBackground(Void... voids) {
            return keycloakBO.refreshAccess();
        }

        @Override
        protected void onPostExecute(String token) {
            super.onPostExecute(token);
            if(listener != null){
                listener.onRefreshAccess(token);
            }
        }
    }



}

