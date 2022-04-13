package br.gov.caixa.loterias.apostas.controllers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.VolleyError;
import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;

import br.gov.caixa.loterias.apostas.BuildConfig;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bean.SessaoUsuario;
import br.gov.caixa.loterias.apostas.model.bo.AcessoBO_;
import br.gov.caixa.loterias.apostas.model.bo.ApostaBO_;
import br.gov.caixa.loterias.apostas.model.bo.DadosCorporativosBO_;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.MeioPagamentoResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametrosConfiguraveisDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.VersionResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.ConfiguracaoAppSingleton;
import br.gov.caixa.loterias.apostas.model.dao.crud.MercadoPagoCRUD;
import br.gov.caixa.loterias.apostas.model.sp.LoginSP;
import br.gov.caixa.loterias.apostas.utils.Aplicacao;
import br.gov.caixa.loterias.apostas.utils.LocalizacaoUtils;
import br.gov.caixa.loterias.apostas.utils.UtilQueueIT;
import br.gov.caixa.loterias.apostas.view.listener.ICallbackQueueIT;


public class SplashScreenActivity extends AppCompatActivity {

    public static final int SPLASH_TIME = 2000;
    public static final int REQ_LOC = 2;
    private VersionResponse responseVersao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        
        AppCenter.start(getApplication(), BuildConfig.APP_CENTER_KEY, Analytics.class, Crashes.class);

        startaFila();
    }

    private void startaFila() {
        UtilQueueIT queueIT = new UtilQueueIT(SplashScreenActivity.this, onQueueITListener());
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

    private void trataPermissao() {
        int statusPermissao = LocalizacaoUtils.checaStatusPermissao(this);
        if (statusPermissao == LocalizacaoUtils.LOC_NAO_PERMITIDA) {
            LocalizacaoUtils.solicitaPermissao(this);
        } else {
            if (statusPermissao == LocalizacaoUtils.LOC_NO_BRASIL) {
                trataSucesso(responseVersao);
            } else {
                startActivityForResult(new Intent(SplashScreenActivity.this, LocalizacaoActivity.class), REQ_LOC);
            }
        }
    }

    public void verificaVersao() {
        AcessoBO_.getInstance_(this).getVerificaVersao(BuildConfig.VERSION_NAME, new RequestListener<VersionResponse>() {
            @Override
            public void onResponse(VersionResponse response) {
                responseVersao = response;
                trataPermissao();
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                startActivityForResult(new Intent(SplashScreenActivity.this, AppIndisponivelActivity.class), 1);
            }
        });
    }


    private void trataSucesso (VersionResponse response) {
        if (!response.getPayload()) {
            startActivity(new Intent(SplashScreenActivity.this, AtualizacaoActivity.class));
        } else {
            // chama configuracoes do app
            callConfiguracoesApp();
            new Handler().postDelayed(() -> {
                LoginSP.limpar();
                startActivity(new Intent(SplashScreenActivity.this, IntroducaoActivity.class));
                finish();
            }, SPLASH_TIME);
        }
    }

    private void callConfiguracoesApp() {
        try {
            ApostaBO_.getInstance_(SplashScreenActivity.this).getApresentaHistorico(new RequestListener<ParametrosConfiguraveisDTOResponse>() {
                @Override
                public void onResponse(ParametrosConfiguraveisDTOResponse result) {

                    if (temDadosResposta(result)){
                        setMostraMenuApostas(result.getPayload().getMapa().getHabilitaMenuApostas());
                    }else {
                        setMenuApostasDefault();
                    }

                    if (precisaAtualizarPublicKey(result)){
                        ConfiguracaoAppSingleton.getInstance().setAtualizaPublicKey(result.getPayload().getMapa().isAtualizaPublicKey());
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
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (requestCode == LocalizacaoUtils.PERMISSAO_LOCALIZACAO_COD) {
            if (grantResults.length > 0) {
                trataPermissao();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQ_LOC) {
            trataSucesso(responseVersao);
        } else if (resultCode == RESULT_OK && requestCode == 1) {
            verificaVersao();
        }
    }
}
