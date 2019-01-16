package br.gov.caixa.loterias.apostar.interno.model.bo.keycloak.internals;

import org.androidannotations.annotations.EBean;
import org.jboss.aerogear.android.authorization.AuthorizationManager;
import org.jboss.aerogear.android.authorization.oauth2.OAuth2AuthorizationConfiguration;
import org.jboss.aerogear.android.authorization.oauth2.OAuth2Properties;
import org.jboss.aerogear.android.authorization.oauth2.intent.OAuth2IntentAuthzModule;
import org.jboss.aerogear.android.authorization.oauth2.webview.OAuth2WebViewAuthzModule;

import java.net.URL;

import br.gov.caixa.loterias.apostar.interno.model.bo.keycloak.KeycloakBO;

import static org.androidannotations.annotations.EBean.Scope.Singleton;

@EBean(scope = Singleton)
public class KeycloakFactoryBO {
    private static final String TAG = KeycloakBO.class.getName();
    //private static final String SERVER_URL = "https://logindes.caixa.gov.br";
    private static final String SERVER_URL = "http://10.130.9.22:81";
    private static final String AUTH_URL = SERVER_URL + "/auth";
    private static final String AUTH_ENDPOINT = "/auth/realms/r_inter_auth_basic/protocol/openid-connect/auth";
    private static final String ACCESS_TOKEN_ENDPOINT = "/auth/realms/r_inter_auth_basic/protocol/openid-connect/token";
    private static final String REFRESH_TOKEN_ENDPOINT = "/auth/realms/r_inter_auth_basic/protocol/openid-connect/token";
    private static final String AUTHZ_ACCOOUNT_ID = "keycloak-token";
    private static final String AUTH_CLIENT_ID = "cli-mob-mlo";
    private static final String AUTH_REDIRECT_URL = "http://localhost";
    private static final String MODULE_NAME = "";
    private static final String CLIENT_SECRET = "b54d171d-03de-41f3-b1b1-64e98672cbb8";

    /*
    public OAuth2AuthzService createAuthService() {

    }
    */

    public OAuth2WebViewAuthzModule createDialogModule() {
        return createDialogModule("dialog-" + AUTH_CLIENT_ID);
    }

    public OAuth2WebViewAuthzModule createDialogModule(String moduleName) {
        return (OAuth2WebViewAuthzModule) newModuleConfig(moduleName).setWithIntent(false).asModule();
    }

    public OAuth2IntentAuthzModule createBrowseIntentModule() {
        return createBrowseIntentModule("intent-" + AUTH_CLIENT_ID);
    }

    public OAuth2IntentAuthzModule createBrowseIntentModule(String moduleName) {
        return (OAuth2IntentAuthzModule) newModuleConfig(moduleName).setWithIntent(true).asModule();
    }

    private OAuth2AuthorizationConfiguration newModuleConfig(String moduleName) {
        try {
            return AuthorizationManager.config(moduleName, OAuth2AuthorizationConfiguration.class)
                .setBaseURL(new URL(AUTH_URL))
                .setAuthzEndpoint(AUTH_ENDPOINT)
                .setAccessTokenEndpoint(ACCESS_TOKEN_ENDPOINT)
                .setRefreshEndpoint(REFRESH_TOKEN_ENDPOINT)
                .setAccountId(AUTHZ_ACCOOUNT_ID)
                .setClientId(AUTH_CLIENT_ID)
                .setClientSecret(CLIENT_SECRET)
                .setRedirectURL(AUTH_REDIRECT_URL);
        } catch (Exception e) {
            throw new RuntimeException("Não foi possível inicializar os serviços de " +
                    "autenticação OAUTH2 por um erro de configuração", e);
        }
    }

    private OAuth2Properties propertiesFor(String name) {
        try {
            OAuth2Properties properties = new OAuth2Properties(new URL(SERVER_URL), name);
            properties.setAccessTokenEndpoint(ACCESS_TOKEN_ENDPOINT);
            properties.setAuthzEndpoint(AUTH_URL);
            properties.setClientId(AUTH_CLIENT_ID);
            properties.setClientSecret(CLIENT_SECRET);
            properties.setRedirectURL(AUTH_REDIRECT_URL);
            properties.setRefreshEndpoint(SERVER_URL + REFRESH_TOKEN_ENDPOINT);
            // properties.setScopes();
            // properties.setAccountId();

            return properties;
        } catch (Exception e) {
            throw new RuntimeException("Não foi possível inicializar os serviços de " +
                    "autenticação OAUTH2 por um erro de configuração", e);
        }
    }
}

