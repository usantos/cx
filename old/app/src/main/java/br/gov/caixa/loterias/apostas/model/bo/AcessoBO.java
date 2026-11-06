package br.gov.caixa.loterias.apostas.model.bo;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.biometric.BiometricManager;

import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.ResponseTypeValues;

import java.util.LinkedHashMap;

import br.gov.caixa.loterias.apostas.controllers.LoginActivity;
import br.gov.caixa.loterias.apostas.controllers.TokenActivity;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.VersionResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.GsonRequest;
import br.gov.caixa.loterias.apostas.utils.AuthorizationServiceFactory;
import br.gov.caixa.loterias.apostas.utils.BuildConfigManager;
import br.gov.caixa.loterias.apostas.utils.TopazUtil;

public class AcessoBO extends SilceBO {
    private static String OPENID  = "openid";
    private static String PROFILE = "profile";
    private static String OFFLINE = "offline_access";

    public static Boolean PRIMEIRO_LOGIN = false;
    private static AcessoBO instance;
    private AuthorizationService authorizationService;

    private AcessoBO(){
        super();
    }

    public static AcessoBO getInstance(){
        if (instance == null){
            instance = new AcessoBO();
        }
        return instance;
    }

    public void iniciarLoginSSO(Activity activity, Bundle bolao){
        if(activity != null) {
            try {
                AuthorizationServiceConfiguration authorizationServiceConfiguration = new AuthorizationServiceConfiguration(
                        Uri.parse(BuildConfigManager.getVariavel("URL_SSO_AUTH")),
                        Uri.parse(BuildConfigManager.getVariavel("URL_SSO_TOKEN"))
                );

                AuthorizationRequest.Builder builder = getAuthorizationRequestBuilder(activity, authorizationServiceConfiguration);

                AuthorizationService service = new AuthorizationService(activity);
                if (service.getBrowserDescriptor() != null) {
                    AuthorizationRequest authorizationRequest = builder.build();
                    Intent intent = new Intent(activity, TokenActivity.class);
                    if (bolao != null){
                        intent.putExtras(bolao);
                    }
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    PendingIntent pendingIntent = getPendingIntent(intent);

                    authorizationService = AuthorizationServiceFactory.getAuthorizationService(activity);
                    authorizationService.performAuthorizationRequest(authorizationRequest, pendingIntent);
                } else {
                    Log.d("ERRO_SSO", "Sem navegador compativel");
                }

            } catch (ActivityNotFoundException e){
                Log.d("ERRO_SSO", e.getLocalizedMessage());
            }

        }
    }

    private PendingIntent getPendingIntent(Intent intent) {
        PendingIntent pendingIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getActivity(getContext(),
                                                      0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
        } else {
            pendingIntent = PendingIntent.getActivity(getContext(),
                                                      0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        return pendingIntent;
    }

    public void iniciarLogoutSSO(Class classDestino, Activity activity){
        AuthorizationServiceConfiguration authorizationServiceConfiguration = new AuthorizationServiceConfiguration(
                Uri.parse(BuildConfigManager.getVariavel("URL_SSO_LOGOUT")),
                Uri.parse(BuildConfigManager.getVariavel("URL_SSO_TOKEN"))
        );

        AuthorizationRequest.Builder builder = getAuthorizationRequestBuilder(activity, authorizationServiceConfiguration);

        AuthorizationRequest authorizationRequest = builder.build();
        Intent intent = new Intent(activity, classDestino);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = getPendingIntent(intent);

        AuthorizationService authorizationService = AuthorizationServiceFactory.getAuthorizationService(activity);
        authorizationService.performAuthorizationRequest(authorizationRequest, pendingIntent);

        DadosUsuarioBO.limparRegistros();
        DadosBiometriaBO.limparRegistros();
    }

    private AuthorizationRequest.Builder getAuthorizationRequestBuilder(Activity activity, AuthorizationServiceConfiguration authorizationServiceConfiguration) {
        AuthorizationRequest.Builder builder = new AuthorizationRequest.Builder(
                authorizationServiceConfiguration,
                BuildConfigManager.getVariavel("SSO_CLIENT_ID"),
                ResponseTypeValues.CODE,
                Uri.parse(BuildConfigManager.getVariavel("SSO_REDIRECT"))
        );

        //Verifica se tem biometria para passar os parametros de RefreshToken com tempo Estendido
        BiometricManager biometricManager = BiometricManager.from(activity);
        if (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK)
                == BiometricManager.BIOMETRIC_SUCCESS) {
            builder.setScopes(OPENID,PROFILE,OFFLINE);
        }

        String deviceId = TopazUtil.getDevieId(activity);
        if (deviceId != null && !deviceId.isEmpty()){
            deviceId = deviceId.replace("|", "-");
            builder.setAdditionalParameters(TopazUtil.getParams(deviceId));
        }

        return builder;
    }

    public void getVerificaVersao(String versao, final RequestListener<VersionResponse> listener) {
        LinkedHashMap<String, String> queryParams = new LinkedHashMap<>();
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();

        String path = ServerMethods.APP_VERSAO_ATUALIZACAO_PATH.replace("{versao-app}", String.valueOf( versao ));

        GsonRequest request = getServiceConnection().buildGetRequest(path, queryParams, VersionResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(), headers);

        request.setErrorListener(interceptError(request, headers, listener));
        getServiceConnection().request(request, listener);
    }
}
