package br.gov.caixa.loterias.apostas.model.bo.keycloak;

/**
 * Created by igorvilar on 06/09/17.
 */

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;

import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.TokenRequest;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import br.gov.caixa.loterias.apostas.BuildConfig;
import br.gov.caixa.loterias.apostas.model.bo.DadosUsuarioBO;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnRefreshTokenListener;
import br.gov.caixa.loterias.apostas.utils.AuthorizationServiceFactory;

@EBean(scope = EBean.Scope.Singleton)
public class KeycloakBO {

    @RootContext
    protected Context context;

    private static final String TAG = KeycloakBO.class.getName();

    private String accessToken;
    private String refreshToken;

    public String getAccessToken(){
        return accessToken;
    }
    public void setAccessToken(String accessToken){
        this.accessToken = accessToken;
    }
    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }

    @AfterInject
    protected void init(){
        try {

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

    public void limparSessao(Activity activity){
        DadosUsuarioBO.updateUsuarioLogado( activity, false );
        accessToken = null;
        refreshToken = null;
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

    public boolean isPrecisaRefreshToken() {
        if (getAccessToken() != null) {
            Long exp = Long.parseLong(getExpToken());
            Long tsLong = System.currentTimeMillis()/1000;
            return (exp - tsLong) < 0;
        }
        return false;
    }

    public String getExpToken() {
        return getFromToken("exp");
    }

    public String getFromToken(String attr){
        String separar[] =  getAccessToken().split("\\.");
        String tokenCorpo = separar[1];
        byte bytes[] = Base64.decode(tokenCorpo, Base64.URL_SAFE);
        tokenCorpo = new String(bytes, Charset.forName("UTF-8"));
        JSONObject jsonObject;
        String attrBuscado = "";
        try {
            jsonObject = new JSONObject(tokenCorpo);
            attrBuscado = jsonObject.getString(attr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return attrBuscado;
    }

    public interface OnRefreshAccess{
        void onRefreshAccess();
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
                listener.onRefreshAccess();
            }
        }
    }

    public void realizaRefreshToken(final OnRefreshTokenListener listener) {
        refreshAccess(() -> {
            //Se retornar o token, tenta fazer a mesma requisição

            AuthorizationServiceConfiguration authorizationServiceConfiguration = new AuthorizationServiceConfiguration(
                    Uri.parse(BuildConfig.URL_SSO_AUTH),
                    Uri.parse(BuildConfig.URL_SSO_TOKEN)
            );

            AuthorizationService authorizationService = AuthorizationServiceFactory.getAuthorizationService(context);
            TokenRequest.Builder tokenBuilder = new TokenRequest.Builder(authorizationServiceConfiguration, BuildConfig.SSO_CLIENT_ID);
            tokenBuilder.setRefreshToken(getRefreshToken());
            tokenBuilder.setGrantType("refresh_token");

            TokenRequest tokenRequest = tokenBuilder.build();

            authorizationService.performTokenRequest(tokenRequest, (response, ex) -> {
                if (response != null) {
                    if (response.accessToken == null || response.refreshToken == null) {
                        listener.errorRefresh(setErrorUnauthorized());
                    } else {
                        KeycloakBO_.getInstance_(context).setAccessToken(response.accessToken);
                        KeycloakBO_.getInstance_(context).setRefreshToken(response.refreshToken);

                        if (getAccessToken() != null) {
                            listener.successRefresh();
                        } else {
                            listener.errorRefresh(setErrorUnauthorized());
                        }
                    }
                }else {
                    listener.errorRefresh(setErrorUnauthorized());
                }
            });
        });
    }

    private VolleyError setErrorUnauthorized() {
        return new VolleyError(new NetworkResponse(401, null, false, 0L, null));
    }
}

