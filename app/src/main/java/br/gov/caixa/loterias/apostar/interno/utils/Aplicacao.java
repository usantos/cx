package br.gov.caixa.loterias.apostar.interno.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.support.v7.app.AlertDialog;

import com.android.volley.VolleyError;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;

import br.gov.caixa.loterias.apostar.interno.model.bean.DadosUsuarioToken;
import br.gov.caixa.loterias.apostar.interno.model.bean.SessaoUsuario;
import br.gov.caixa.loterias.apostar.interno.model.bo.DadosCorporativosBO;
import br.gov.caixa.loterias.apostar.interno.model.bo.DadosCorporativosBO_;
import br.gov.caixa.loterias.apostar.interno.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostar.interno.model.bo.RequestListener;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ParametrosSimulacao;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ParametrosSimulacaoResponse;
import br.gov.caixa.loterias.apostar.interno.controllers.AppIndisponivelActivity;
import br.gov.caixa.loterias.apostar.interno.controllers.CarrinhoActivity;
import br.gov.caixa.loterias.apostar.interno.view.PrincipalActivity_;
import br.gov.caixa.loterias.apostar.interno.controllers.SplashScreenActivity;
import br.gov.caixa.loterias.apostar.interno.view.custom.LoadingViewLoterias;

public class Aplicacao extends MultiDexApplication {

    public static Application application;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        registerActivityLifecycleCallbacks(new br.gov.caixa.loterias.apostar.interno.utils.ActivityLifecycleCallbacks());
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
            if(!isRecreated) {
                atualizaBuscaParametros(activity);
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
            if (activity.getClass().toString().equalsIgnoreCase(SplashScreenActivity.class.toString())) {
                loadViewProgress.dismiss();
            }
        }

        final DadosCorporativosBO dadosCorporativosBO = DadosCorporativosBO_.getInstance_(Aplicacao.application.getApplicationContext());
        dadosCorporativosBO.parametrosSimulacao(new RequestListener<ParametrosSimulacaoResponse>() {
            @Override
            public void onResponse(ParametrosSimulacaoResponse response) {
                if(activity != null && !activity.isDestroyed()){
                    loadViewProgress.dismiss();
                }
                if (response.getPayload().getParametros().isEmpty()) {
                    Intent appIndis = new Intent(activity, AppIndisponivelActivity.class);
                    appIndis.putExtra("IS_BUSCA_PARAM", true);
                    activity.startActivityForResult(appIndis, 1);
                } else {
                    if (response.getPayload().getValorLimiteDiario() == null) {
                        response.getPayload().setValorLimiteDiario(BigDecimal.ZERO);
                    }
                    parametroSimulacaoSuccess(response, activity);
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                if(activity != null && !activity.isDestroyed()){
                    loadViewProgress.dismiss();
                }
                RedirectNetwork.checkRedirect(error, activity);
            }
        });
    }

    private void parametroSimulacaoSuccess(ParametrosSimulacaoResponse response, Activity activity) {
        SessaoUsuario sessaoUser = SessaoUsuario.getInstance();
        ParametrosSimulacao oldParams = sessaoUser.getParametrosSimulacao();
        ParametrosSimulacao newParams = response.getPayload();
        if (oldParams != null && newParams != null) {
            try {
                DadosUsuarioToken dadosUsuarioToken = Utils.decodedToken(sessaoUser.getToken());
                sessaoUser.setEmail(dadosUsuarioToken.getEmail());
                sessaoUser.setNome(dadosUsuarioToken.getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            sessaoUser.setValorMinimimoAposta(response.getPayload().getValorMinimoCarrinho());
            sessaoUser.setValorMaximoAposta(response.getPayload().getValorLimiteDiario());
            sessaoUser.setParametrosSimulacao(response.getPayload());
            atualizaLayout(activity);
        }
    }
    private void atualizaLayout(Activity activity){
        ArrayList<String> permitidas = new ArrayList<>((Arrays.asList(PrincipalActivity_.class.toString().toUpperCase(), CarrinhoActivity.class.toString().toUpperCase())));
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

