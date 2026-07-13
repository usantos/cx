package br.gov.caixa.loterias.apostas.controllers;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.android.volley.VolleyError;
import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;
import com.scottyab.rootbeer.RootBeer;

import java.io.File;

import br.gov.caixa.loterias.apostas.BuildConfig;
import br.gov.caixa.loterias.apostas.LoteriasBaseAppActivity;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.AcessoBO;
import br.gov.caixa.loterias.apostas.model.bo.ApostaSilceBO;
import br.gov.caixa.loterias.apostas.model.bo.DadosUsuarioBO;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametrosConfiguraveisDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.VersionResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.ConfiguracaoAppSingleton;
import br.gov.caixa.loterias.apostas.model.sp.LoginSP;
import br.gov.caixa.loterias.apostas.utils.BuildConfigManager;
import br.gov.caixa.loterias.apostas.utils.BuildVersionUtil;
import br.gov.caixa.loterias.apostas.utils.EspecialUtils;
import br.gov.caixa.loterias.apostas.utils.MessagingUtils;
import br.gov.caixa.loterias.apostas.utils.SharedPreferencesUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.utils.FilaBRAManager;
import br.gov.caixa.loterias.apostas.view.listener.FilaBRAListener;

public class SplashScreenActivity extends LoteriasBaseAppActivity {

    public static final int SPLASH_TIME = 2000;

    private VersionResponse responseVersao;

    private FilaBRAManager filaBRAManager;

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

        filaBRAManager = new FilaBRAManager();
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
        //filaBRAManager.resetaFila(this);
        filaBRAManager.iniciaFila(SplashScreenActivity.this, new FilaBRAListener(this));
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
                if(!DadosUsuarioBO.checarFecharIntroducao()) {
                    startActivity(new Intent(SplashScreenActivity.this, IntroducaoActivity.class));
                }
                else{
                    startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
                }
                    //}
                finish();
            }, SPLASH_TIME);
        }
    }

    private void callConfiguracoesApp() {
        try {
            ApostaSilceBO.getInstance().getApresentaHistorico(new RequestListener<ParametrosConfiguraveisDTOResponse>() {
                @Override
                public void onResponse(ParametrosConfiguraveisDTOResponse result) {

                    if (temInformacaoBolao(result)){
                        ConfiguracaoAppSingleton.getInstance().setBolaoHabilitado(result.getPayload().getMapa().getBolaoHabilitado());
                        SharedPreferencesUtils.setValor("bolaoHabilitado",result.getPayload().getMapa().getBolaoHabilitado());
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
                        if (temDadosComboApostasHabilitado(result)) {
                            ConfiguracaoAppSingleton.getInstance().setComboApostasHabilitado(result.getPayload().getMapa().getComboApostasHabilitado());
                        }

                        ConfiguracaoAppSingleton.getInstance().setUrlBaseCarrinhoPiloto(result.getPayload().getMapa().getUrlBaseCarrinhoPiloto());
                        ConfiguracaoAppSingleton.getInstance().setUrlBaseCarrinhoProducao(result.getPayload().getMapa().getUrlBaseCarrinhoProducao());
                        ConfiguracaoAppSingleton.getInstance().setGrupoDeCpfsQueAcessamOServicoDaAzure(result.getPayload().getMapa().getGrupoDeCpfsQueAcessamOServicoDaAzure());
                        ConfiguracaoAppSingleton.getInstance().setRapidaoHabilitado(false);
                        ConfiguracaoAppSingleton.getInstance().setUrlBaseApostador(result.getPayload().getMapa().getUrlBaseApostador());
                        ConfiguracaoAppSingleton.getInstance().setGrupoDeCpfsApostador(result.getPayload().getMapa().getGrupoDeCpfsApostador());

                        if(result.getPayload().getMapa().getConcursoMega30Anos() != null && !result.getPayload().getMapa().getConcursoMega30Anos().toString().isEmpty()){
                            SharedPreferencesUtils.setValor(EspecialUtils.MEGA_VALOR,result.getPayload().getMapa().getConcursoMega30Anos());
                            SharedPreferencesUtils.setValor(EspecialUtils.MEGA_LINHA,getString(R.string.mega_sena_30_anos));
                            SharedPreferencesUtils.setValor(EspecialUtils.MEGA_DUAS_LINHAS,getString(R.string.mega_sena_30_anos_duas_linhas));
                        }

                        //TODO: LOTECA PAIS//
                        //if(result.getPayload().getMapa().getConcursoLotecaPais() != null && !result.getPayload().getMapa().getConcursoLotecaPais().toString().isEmpty()){
                        //MOCK
                        if(result.getPayload().getMapa().getConcursoMega30Anos() != null && !result.getPayload().getMapa().getConcursoMega30Anos().toString().isEmpty()){
                            //SharedPreferencesUtils.setValor(EspecialUtils.PAIS_VALOR, result.getPayload().getMapa().getConcursoLotecaPais());
                            SharedPreferencesUtils.setValor(EspecialUtils.LOTECA_PAIS_VALOR, 4459);
                            SharedPreferencesUtils.setValor(EspecialUtils.LOTECA_PAIS_LINHA, getString(R.string.loteca_pais));
                            SharedPreferencesUtils.setValor(EspecialUtils.LOTECA_PAIS_DUAS_LINHAS, getString(R.string.loteca_pais_duas_linhas));
                        }
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
    private Boolean temDadosComboApostasHabilitado(ParametrosConfiguraveisDTOResponse result) {
        return result.getPayload().getMapa().getComboApostasHabilitado() != null;
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
