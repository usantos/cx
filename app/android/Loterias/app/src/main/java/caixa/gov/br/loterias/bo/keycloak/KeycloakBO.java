package caixa.gov.br.loterias.bo.keycloak;


/**
 * Created by igorvilar on 06/09/17.
 */

import android.app.Activity;
import android.util.Log;

import org.jboss.aerogear.android.authorization.AuthorizationManager;
import org.jboss.aerogear.android.authorization.AuthzModule;
import org.jboss.aerogear.android.authorization.oauth2.OAuth2AuthorizationConfiguration;
import org.jboss.aerogear.android.authorization.oauth2.OAuthWebViewDialog;
import org.jboss.aerogear.android.core.Callback;
import java.net.URL;

    public class KeycloakBO {
        private static final String TAG = KeycloakBO.class.getName();

          private static final String SERVER_URL = "https://logindes.caixa.gov.br";
//        private static final String SERVER_URL = "http://10.130.9.22:81";
        private static final String AUTH_URL = SERVER_URL +"/auth";
        private static final String AUTH_ENDPOINT = "/realms/r_inter_auth_basic/protocol/openid-connect/auth";
        private static final String ACCESS_TOKEN_ENDPOINT = "/realms/r_inter_auth_basic/protocol/openid-connect/token";
        private static final String REFRESH_TOKEN_ENDPOINT = "/realms/r_inter_auth_basic/protocol/openid-connect/token";
        private static final String AUTHZ_ACCOOUNT_ID = "keycloak-token";
        private static final String AUTH_CLIENT_ID = "cli-mob-mlo";
        private static final String AUTH_REDIRECT_URL = "http://localhost";
        private static final String MODULE_NAME = "";
        private static final String CLIENT_SECRET = "b54d171d-03de-41f3-b1b1-64e98672cbb8";


        static {
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
                throw new RuntimeException(e);
            }
        }

        public static OAuth2AuthorizationConfiguration oauth2Configuration() {
            try {
                return AuthorizationManager.config(MODULE_NAME, OAuth2AuthorizationConfiguration.class)
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

        public static void connect(final Activity activity, final Callback callback) {
            try {
                final AuthzModule authzModule = AuthorizationManager.getModule(MODULE_NAME);

                Log.i(TAG, "authzModule = " + authzModule.getClass().getName());

                authzModule.requestAccess(activity, new Callback<String>() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public void onSuccess(String s) {
                        Log.i(TAG, "KeycloakBO.connect.onSuccess: " + s);

                        callback.onSuccess(s);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.i(TAG, "KeycloakBO.connect.onFailure: " + e);

                        if (!e.getMessage().matches(OAuthWebViewDialog.OAuthReceiver.DISMISS_ERROR)) {
                            authzModule.deleteAccount();
                        }
                        callback.onFailure(e);
                    }
                });


            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }

        public static boolean isConnected() {
            return AuthorizationManager.getModule(MODULE_NAME).isAuthorized();
        }

    }

