package br.gov.caixa.loterias.apostas.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.android.volley.VolleyError;
import com.mercadolibre.android.device.sdk.DeviceSDK;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import br.gov.caixa.loterias.apostas.controllers.AppIndisponivelActivity;
import br.gov.caixa.loterias.apostas.controllers.CarrinhoActivity;
import br.gov.caixa.loterias.apostas.controllers.LocalizacaoActivity;
import br.gov.caixa.loterias.apostas.controllers.PrincipalActivity;
import br.gov.caixa.loterias.apostas.controllers.SplashScreenActivity;
import br.gov.caixa.loterias.apostas.model.bean.DadosUsuarioToken;
import br.gov.caixa.loterias.apostas.model.bean.SessaoUsuario;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametrosSimulacao;
import br.gov.caixa.loterias.apostas.model.model.ParametrosSimulacaoModel;
import br.gov.caixa.loterias.apostas.view.custom.LoadingViewLoterias;


public class Aplicacao extends MultiDexApplication {

    public static Application application;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        registerActivityLifecycleCallbacks(new br.gov.caixa.loterias.apostas.utils.ActivityLifecycleCallbacks());
        DeviceSDK.getInstance().execute(this);

    }

    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

}

class ActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {

    private AlertDialog loadViewProgress;
    private int numStarted = 0;
    private boolean isRecreated = false;

    @Override
    public void onActivityStarted(Activity activity) {
        if (numStarted == 0) {
            SessaoUsuario sessaoUser = SessaoUsuario.getInstance();
            if(!isRecreated && SessaoUsuarioUtil.precisaAtualizar() && sessaoUser.isPassouPelaQueueIT()) {
                try{
                    atualizaBuscaParametros(activity);
                }catch (Exception e){
                    Log.e("ERRO", e.getMessage());
                }
            }
        }
        numStarted++;
        isRecreated = false;
    }

    @Override
    public void onActivityStopped(Activity activity) {
        numStarted--;
    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }


    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    public void atualizaBuscaParametros(Activity activity) {
        if(activity != null && !activity.isDestroyed()) {
            loadViewProgress = LoadingViewLoterias.show(activity);
            if (activity.getClass().toString().equalsIgnoreCase(SplashScreenActivity.class.toString()) ||
                    activity.getClass().toString().equalsIgnoreCase(LocalizacaoActivity.class.toString())) {
                loadViewProgress.dismiss();
            }
        }

        new ParametrosSimulacaoModel(activity)
                .buscaParametroSiumulacao(new OnSilceListener<ParametrosSimulacao>() {
                    @Override
                    public void success(ParametrosSimulacao payload) {
                        if(activity != null && !activity.isDestroyed()){
                            loadViewProgress.dismiss();
                        }
                        if (payload.getParametros().isEmpty()) {
                            Intent appIndis = new Intent(activity, AppIndisponivelActivity.class);
                            appIndis.putExtra("IS_BUSCA_PARAM", true);
                            activity.startActivityForResult(appIndis, 1);
                        } else {
                            parametroSimulacaoSuccess(payload, activity);
                        }
                    }

                    @Override
                    public void error(VolleyError error) {
                        if(activity != null && !activity.isDestroyed()){
                            loadViewProgress.dismiss();
                        }
                        RedirectNetwork.checkRedirect(error, activity);
                    }
                });
    }

    private void parametroSimulacaoSuccess(ParametrosSimulacao payload, Activity activity) {
        SessaoUsuario sessaoUser = SessaoUsuario.getInstance();
        ParametrosSimulacao oldParams = sessaoUser.getParametrosSimulacao();
        ParametrosSimulacao newParams = payload;

        if ( oldParams == null || (newParams != null && oldParams!= null && !newParams.equals(oldParams))) {
                try {
                    DadosUsuarioToken dadosUsuarioToken = Utils.decodedToken(sessaoUser.getToken());
                    sessaoUser.setEmail(dadosUsuarioToken.getEmail());
                    sessaoUser.setNome(dadosUsuarioToken.getName());
                    sessaoUser.setDataUltimaRequisicao(new Date());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                sessaoUser.setValorMinimimoAposta(payload.getValorMinimoCarrinho());
                sessaoUser.setValorMaximoAposta(payload.getValorLimiteDiario());
                sessaoUser.setParametrosSimulacao(payload);
                atualizaLayout(activity);
        }
    }
    
    private void atualizaLayout(Activity activity){
        ArrayList<String> permitidas = new ArrayList<>((Arrays.asList(PrincipalActivity.class.toString().toUpperCase(), CarrinhoActivity.class.toString().toUpperCase())));
        if(permitidas.contains(activity.getClass().toString().toUpperCase())){
            isRecreated = true;
            if (android.os.Build.VERSION.SDK_INT >= 11){
                activity.recreate();
            }else{
                Intent intent = new Intent(activity, activity.getClass());
                if (activity.getIntent().getExtras() != null) {
                    intent.putExtras(activity.getIntent().getExtras());
                }
                activity.startActivity(intent);
                activity.overridePendingTransition(0, 0);
                activity.finish();
            }
        }
    }
}

