package br.gov.caixa.loterias.apostar.interno.model.bo;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;

import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.ResponseTypeValues;

import org.androidannotations.annotations.EBean;

import java.util.LinkedHashMap;

import br.gov.caixa.loterias.apostar.interno.BuildConfig;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.VersionResponse;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.internals.GsonRequest;
import br.gov.caixa.loterias.apostar.interno.model.sp.LoginSP;
import br.gov.caixa.loterias.apostar.interno.view.LoginActivity;
import br.gov.caixa.loterias.apostar.interno.view.TokenActivity;

import static org.androidannotations.annotations.EBean.Scope.Singleton;

@EBean(scope = Singleton)
public class AcessoBO extends BaseBO {

    public static Boolean PRIMEIRO_LOGIN = false;
    AuthorizationService authorizationService;

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

            authorizationService = new AuthorizationService(activity);
            authorizationService.performAuthorizationRequest(authorizationRequest, pendingIntent);
        }
    }

    public void iniciarLogoutSSO(Activity activity){
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
        LoginSP.loginRealizado(false);
        Intent intent = new Intent(activity, LoginActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(activity, authorizationRequest.hashCode(), intent, 0);

        AuthorizationService authorizationService = new AuthorizationService(activity);
        authorizationService.performAuthorizationRequest(authorizationRequest, pendingIntent);

        DadosUsuarioBO.limparRegistros();
    }

    public void getVerificaVersao(String versao, final RequestListener<VersionResponse> listener) {
        LinkedHashMap<String, String> queryParams = new LinkedHashMap<>();
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();

        String path = ServerMethods.APP_VERSAO_ATUALIZACAO_PATH.replace("{versao-app}", String.valueOf( versao ));

        GsonRequest request = silceConnection.buildGetRequest(path, queryParams, VersionResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(), headers);

        request.setErrorListener(interceptError(request, headers, listener));
        silceConnection.request(request);
    }
}
