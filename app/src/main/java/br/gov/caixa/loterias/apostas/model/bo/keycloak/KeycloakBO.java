package br.gov.caixa.loterias.apostas.model.bo.keycloak;

/**
 * Created by igorvilar on 06/09/17.
 */

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
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
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.Objects;

import br.gov.caixa.loterias.apostas.BuildConfig;
import br.gov.caixa.loterias.apostas.model.bo.BiometriaBO;
import br.gov.caixa.loterias.apostas.model.bo.DadosBiometriaBO;
import br.gov.caixa.loterias.apostas.model.bo.DadosUsuarioBO;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnRefreshTokenListener;
import br.gov.caixa.loterias.apostas.utils.Aplicacao;
import br.gov.caixa.loterias.apostas.utils.AuthorizationServiceFactory;
import br.gov.caixa.loterias.apostas.utils.BuildConfigManager;

public class KeycloakBO {
    private static final String TAG = KeycloakBO.class.getName();

    private static KeycloakBO instance;
    protected Context context;
    private String accessToken;
    private String refreshToken;

    private KeycloakBO(){
        context = Aplicacao.application.getApplicationContext();
    }

    public static KeycloakBO getInstance(){
        if (instance == null){
            instance = new KeycloakBO();
        }
        return instance;
    }

    public String getAccessToken(){
        return accessToken;
    }
    public void setAccessToken(String accessToken){
        this.accessToken = accessToken;
    }
    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }

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
        try {
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
        } catch (Exception e){
            Log.d("ERRO_LIMPAR_COOKIES", Objects.requireNonNull(e.getLocalizedMessage()));
        }
    }

    public boolean isPrecisaRefreshToken() {
        if (getAccessToken() != null) {
            Long exp = Long.parseLong(getExpToken());
            Long tsLong = System.currentTimeMillis()/1000;
            return (exp - tsLong) < 30;
        }
        return false;
    }

    public boolean isAccessTokenExpirado() {
        if (getAccessToken() == null) return true;

        try {
            String expStr = getExpToken();
            if (expStr == null || expStr.isEmpty()) return true;

            long exp = Long.parseLong(expStr);
            long now = System.currentTimeMillis() / 1000L;

            return exp <= now;
        } catch (Exception e) {
            return true;
        }
    }


    public String getExpToken() {
        return getFromToken("exp");
    }

    public String getFromToken(String attr){
        String attrBuscado = "";
        try {
            String[] separar =  getAccessToken().split("\\.");
            String tokenCorpo = separar[1];
            byte[] bytes = Base64.decode(tokenCorpo, Base64.URL_SAFE);
            tokenCorpo = new String(bytes, Charset.forName("UTF-8"));
            JSONObject jsonObject;
            jsonObject = new JSONObject(tokenCorpo);
            attrBuscado = jsonObject.getString(attr);
        } catch (Exception e) {
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
                    Uri.parse(BuildConfigManager.getVariavel("URL_SSO_AUTH")),
                    Uri.parse(BuildConfigManager.getVariavel("URL_SSO_TOKEN"))
            );

            AuthorizationService authorizationService = AuthorizationServiceFactory.getAuthorizationService(context);
            TokenRequest.Builder tokenBuilder = new TokenRequest.Builder(authorizationServiceConfiguration, BuildConfigManager.getVariavel("SSO_CLIENT_ID"));
            tokenBuilder.setRefreshToken(getRefreshToken());
            tokenBuilder.setGrantType("refresh_token");

            TokenRequest tokenRequest = tokenBuilder.build();

            authorizationService.performTokenRequest(tokenRequest, (response, ex) -> {
                if (response != null) {
                    if (response.accessToken == null || response.refreshToken == null) {
                        listener.errorRefresh(setErrorUnauthorized());
                    } else {
                        if (BiometriaBO.getInstance().getBiometriaHabilitada()) {
                            DadosBiometriaBO.salvarBiometria(response.refreshToken, DadosUsuarioBO.obterNome(), DadosUsuarioBO.obterCpf());
                        }
                        KeycloakBO.getInstance().setAccessToken(response.accessToken);
                        KeycloakBO.getInstance().setRefreshToken(response.refreshToken);

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

