package br.gov.caixa.loterias.apostas.controllers;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.VolleyError;
import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;
import com.scottyab.rootbeer.RootBeer;

import java.io.File;

import br.gov.caixa.loterias.apostas.BuildConfig;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bean.SessaoUsuario;
import br.gov.caixa.loterias.apostas.model.bo.AcessoBO;
import br.gov.caixa.loterias.apostas.model.bo.ApostaBO;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametrosConfiguraveisDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.VersionResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.ConfiguracaoAppSingleton;
import br.gov.caixa.loterias.apostas.model.sp.LoginSP;
import br.gov.caixa.loterias.apostas.utils.BuildConfigManager;
import br.gov.caixa.loterias.apostas.utils.BuildVersionUtil;
import br.gov.caixa.loterias.apostas.utils.MessagingUtils;
import br.gov.caixa.loterias.apostas.utils.UtilQueueIT;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.listener.ICallbackQueueIT;


public class SplashScreenActivity extends AppCompatActivity {

    public static final int SPLASH_TIME = 2000;

    private VersionResponse responseVersao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        if (BuildVersionUtil.isPRD()) {
            RootBeer rootBeer = new RootBeer(this);
            if (rootBeer.isRooted() || isRootAvailable()) {
                ViewUtils.alertTitleButton(this, R.string.label_atencao, "Aplicativo incompatível com as configurações atuais do dispositivo. Código AR001.",
                                           getString(R.string.ok), (DialogInterface.OnDismissListener) dialog -> {
                    finish();
                });
                return;
            }
        }
        
        AppCenter.start(getApplication(), BuildConfigManager.getVariavel("APP_CENTER_KEY"), Analytics.class, Crashes.class);

        //Cria canal de Notificacao
        MessagingUtils.criaCanalNotificacaoApp(this);

        startaFila();
    }

    public static boolean isRootAvailable(){
        for(String pathDir : System.getenv("PATH").split(":")){
            if(new File(pathDir, "su").exists()) {
                return true;
            }
        }
        return false;
    }

    private void startaFila() {
        new UtilQueueIT(SplashScreenActivity.this, onQueueITListener());
    }

    private ICallbackQueueIT onQueueITListener() {
        return new ICallbackQueueIT() {
            @Override
            public void callbackFila(boolean passouNaFila) {
                Log.e("QUEUE-IT", "callbackFila -> passouNaFila: " + passouNaFila);
                SessaoUsuario sessaoUser = SessaoUsuario.getInstance();
                sessaoUser.setPassouPelaQueueIT(true);
                verificaVersao();
            }

            @Override
            public void callbackAbrirFila() {
                Log.e("QUEUE-IT", "callbackFila -> callbackAbrirFila");
            }

            @Override
            public void callbackSaiuFila() {
                Log.e("QUEUE-IT", "callbackFila -> callbackSaiuFila");
            }
        };
    }

    public void verificaVersao() {
        AcessoBO.getInstance().getVerificaVersao(BuildConfig.VERSION_NAME, new RequestListener<VersionResponse>() {
            @Override
            public void onResponse(VersionResponse response) {
                responseVersao = response;
                trataSucesso(responseVersao);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                startActivityForResult(new Intent(SplashScreenActivity.this, AppIndisponivelActivity.class), 1);
            }
        });
    }

    private void trataSucesso (VersionResponse response) {
        if (response == null || response.getPayload() == null || !response.getPayload()) {
            startActivity(new Intent(SplashScreenActivity.this, AtualizacaoActivity.class));
        } else {
            // chama configuracoes do app
            callConfiguracoesApp();
            new Handler().postDelayed(() -> {
                LoginSP.limpar();
                //if (!redirectDeepLink()) {
                    startActivity(new Intent(SplashScreenActivity.this, IntroducaoActivity.class));
                //}
                finish();
            }, SPLASH_TIME);
        }
    }

    private void callConfiguracoesApp() {
        try {
            ApostaBO.getInstance().getApresentaHistorico(new RequestListener<ParametrosConfiguraveisDTOResponse>() {
                @Override
                public void onResponse(ParametrosConfiguraveisDTOResponse result) {

                    if (temInformacaoBolao(result)){
                        ConfiguracaoAppSingleton.getInstance().setBolaoHabilitado(result.getPayload().getMapa().getBolaoHabilitado());
                    }

                    if (temDadosResposta(result)){
                        setMostraMenuApostas(result.getPayload().getMapa().getHabilitaMenuApostas());
                    }else {
                        setMenuApostasDefault();
                    }

                    if (precisaAtualizarPublicKey(result)){
                        ConfiguracaoAppSingleton.getInstance().setAtualizaPublicKey(result.getPayload().getMapa().isAtualizaPublicKey());
                    }

                    if (temDadosPayloadMapa(result)) {
                        ConfiguracaoAppSingleton.getInstance().setUrlBaseCarrinhoPiloto(result.getPayload().getMapa().getUrlBaseCarrinhoPiloto());
                        ConfiguracaoAppSingleton.getInstance().setUrlBaseCarrinhoProducao(result.getPayload().getMapa().getUrlBaseCarrinhoProducao());
                        ConfiguracaoAppSingleton.getInstance().setGrupoDeCpfsQueAcessamOServicoDaAzure(result.getPayload().getMapa().getGrupoDeCpfsQueAcessamOServicoDaAzure());
                    }

                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    setMenuApostasDefault();
                }
            });
        }catch (Exception e){
            setMenuApostasDefault();
        }
    }

    private boolean temInformacaoBolao(ParametrosConfiguraveisDTOResponse result) {
        return temDadosPayloadMapa(result) && result.getPayload().getMapa().getBolaoHabilitado() != null;
    }

    private boolean precisaAtualizarPublicKey(ParametrosConfiguraveisDTOResponse result) {
        return temDadosPayloadMapa(result) && result.getPayload().getMapa().isAtualizaPublicKey() != null;
    }

    private void setMenuApostasDefault() {
        setMostraMenuApostas(ConfiguracaoAppSingleton.MOSTRA_MENU_APOSTAS_DEFAULT);
    }

    private void setMostraMenuApostas(Boolean habilitaMenuApostas) {
        ConfiguracaoAppSingleton.getInstance().setMostraMenuAposta(habilitaMenuApostas);
    }

    private boolean temDadosResposta(ParametrosConfiguraveisDTOResponse result) {
        return temDadosPayloadMapa(result) && result.getPayload().getMapa().getHabilitaMenuApostas() != null;
    }

    private Boolean temDadosPayloadMapa(ParametrosConfiguraveisDTOResponse result) {
        return result != null && result.getPayload() != null && result.getPayload().getMapa() != null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1) {
            verificaVersao();
        }
    }

    //Enviado para PrincipalActivity
//    private boolean redirectDeepLink() {
//        Intent appLinkIntent = getIntent();
//        String appLinkAction = appLinkIntent.getAction();
//        Uri appLinkData = appLinkIntent.getData();
//        if (appLinkData != null && appLinkData.toString().length() >0) {
//            Intent intent = IntentUtil.getIntentDeepLink(this, appLinkData.toString());
//            if (intent != null) {
//                startActivity(intent);
//                return true;
//            }
//        }
//        return false;
//    }
}
