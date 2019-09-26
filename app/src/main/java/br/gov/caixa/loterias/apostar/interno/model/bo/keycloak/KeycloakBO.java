package br.gov.caixa.loterias.apostar.interno.model.bo.keycloak;

/**
 * Created by igorvilar on 06/09/17.
 */

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Base64;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EBean;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;

import br.gov.caixa.loterias.apostar.interno.BuildConfig;
import br.gov.caixa.loterias.apostar.interno.model.bo.DadosUsuarioBO;

@EBean(scope = EBean.Scope.Singleton)
public class KeycloakBO {
    private static final String TAG = KeycloakBO.class.getName();
    private static final String SERVER_URL = BuildConfig.URL_SSO;

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
                listener.onRefreshAccess();
            }
        }
    }
}

