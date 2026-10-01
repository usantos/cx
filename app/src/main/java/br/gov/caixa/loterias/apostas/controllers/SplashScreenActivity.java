package br.gov.caixa.loterias.apostas.controllers;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

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
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametrosConfiguraveisDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametrosConfiguraveisDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.VersionResponse;
import br.gov.caixa.loterias.apostas.model.sp.LoginSP;
import br.gov.caixa.loterias.apostas.utils.ApostasIdenticasSingleton;
import br.gov.caixa.loterias.apostas.utils.BuildConfigManager;
import br.gov.caixa.loterias.apostas.utils.BuildVersionUtil;
import br.gov.caixa.loterias.apostas.utils.DialogUtils;
import br.gov.caixa.loterias.apostas.utils.EspecialUtils;
import br.gov.caixa.loterias.apostas.utils.FilaBRAManager;
import br.gov.caixa.loterias.apostas.utils.IconAliasManager;
import br.gov.caixa.loterias.apostas.utils.IconeAppEnum;
import br.gov.caixa.loterias.apostas.utils.MessagingUtils;
import br.gov.caixa.loterias.apostas.utils.SharedPreferencesUtils;
import br.gov.caixa.loterias.apostas.utils.ConfiguracoesDefaultEnum;
import br.gov.caixa.loterias.apostas.utils.ConfiguracoesEnum;
import br.gov.caixa.loterias.apostas.view.listener.FilaBRAListener;
import br.gov.caixa.loterias.apostas.view.listener.OnDialogBotaoListener;
import br.gov.caixa.loterias.apostas.view.listener.OnDialogDoisBotoesListener;

public class SplashScreenActivity extends LoteriasBaseAppActivity {

    public static final int SPLASH_TIME = 2000;

    private VersionResponse responseVersao;

    private FilaBRAManager filaBRAManager;
    private ApostasIdenticasSingleton apostasIdenticasSingleton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        String ic = SharedPreferencesUtils.getValorString(ConfiguracoesEnum.LAUNCHER_ICON.get(), "");
        if (IconeAppEnum.DEFAULT.get().equalsIgnoreCase(ic) || ic.isEmpty()) {
            IconAliasManager.applyIcon(this, IconeAppEnum.DEFAULT_DISABLED);
            SharedPreferencesUtils.setValor(ConfiguracoesEnum.LAUNCHER_ICON.get(), IconeAppEnum.DEFAULT_DISABLED.get());
        }

        if (BuildVersionUtil.isPRD()) {
            RootBeer rootBeer = new RootBeer(this);
            if (rootBeer.isRooted() || isRootAvailable()) {
                DialogUtils.dialogEntendiListener(
                        SplashScreenActivity.this,
                        "Aplicativo incompatível com as configurações atuais do dispositivo. Código AR001.",
                        new OnDialogBotaoListener() {
                            @Override
                            public void onButtonClick(DialogInterface dialog, int which) {
                                finish();
                            }                        }
                );
                return;
            }
        }

        apostasIdenticasSingleton = ApostasIdenticasSingleton.getInstance();
        apostasIdenticasSingleton.setValidacaoInicialPendente(true);

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
            LoginSP.limpar();
            setPreferencesDefault();
            callConfiguracoesApp();
        }
    }

    private void redirectApp(){
        if(!DadosUsuarioBO.checarFecharIntroducao()) {
            startActivity(new Intent(SplashScreenActivity.this, IntroducaoActivity.class));
        }
        else{
            startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
        }
        finish();
    }
    private void callConfiguracoesApp() {
        try {
            ApostaSilceBO.getInstance().getApresentaHistorico(new RequestListener<ParametrosConfiguraveisDTOResponse>() {
                @Override
                public void onResponse(ParametrosConfiguraveisDTOResponse result) {

                    if (temInformacaoBolao(result)){
                        SharedPreferencesUtils.setValor(ConfiguracoesEnum.BOLAO.get(),result.getPayload().getMapa().getBolaoHabilitado());
                    }

                    if (temDadosResposta(result)){
                        setMostraMenuApostas(result.getPayload().getMapa().getHabilitaMenuApostas());
                    }else {
                        setMenuApostasDefault();
                    }

                    if (precisaAtualizarPublicKey(result)){
                        SharedPreferencesUtils.setValor(ConfiguracoesEnum.IS_ATUALIZA_PUBLIC_KEY.get(), result.getPayload().getMapa().isAtualizaPublicKey());
                    }

                    if (temDadosPayloadMapa(result)) {
                        if (temDadosComboApostasHabilitado(result)) {
                            SharedPreferencesUtils.setValor(ConfiguracoesEnum.COMBO_APOSTAS.get(), result.getPayload().getMapa().getComboApostasHabilitado());
                        }

                        SharedPreferencesUtils.setValor(ConfiguracoesEnum.URL_BASE_CARRINHO_PILOTO.get(), result.getPayload().getMapa().getUrlBaseCarrinhoPiloto());
                        SharedPreferencesUtils.setValor(ConfiguracoesEnum.URL_BASE_CARRINHO_PRODUCAO.get(), result.getPayload().getMapa().getUrlBaseCarrinhoProducao());
                        SharedPreferencesUtils.setValor(ConfiguracoesEnum.IS_RAPIDAO.get(), result.getPayload().getMapa().isRapidaoHabilitado());
                        SharedPreferencesUtils.setValor(ConfiguracoesEnum.URL_BASE_APOSTADOR.get(), result.getPayload().getMapa().getUrlBaseApostador());
                        SharedPreferencesUtils.setValor(ConfiguracoesEnum.GRUPO_CPFS_APOSTADOR.get(), result.getPayload().getMapa().getGrupoDeCpfsApostador());
                        salvaParametrosFiltroBolao(result.getPayload().getMapa());
                        SharedPreferencesUtils.setValor(ConfiguracoesEnum.OUTUBRO_ROSA.get(), result.getPayload().getMapa().getVigenciaMesOutubroRosa());
                        SharedPreferencesUtils.setValor(ConfiguracoesEnum.OUTUBRO_ROSA_MEGA_SENA.get(), result.getPayload().getMapa().getVigenciaOutubroRosaMegaSena());

                        SharedPreferencesUtils.setValor(ConfiguracoesEnum.URL_BASE_BFF.get(), result.getPayload().getMapa().getUrlBaseBff());
                        SharedPreferencesUtils.setValor(ConfiguracoesEnum.GRUPO_CPFS_BFF.get(), result.getPayload().getMapa().getGrupoDeCpfsQueAcessamOBff());
                        //SharedPreferencesUtils.setValor(ConfiguracoesEnum.URL_BASE_BFF_PILOTO.get(), result.getPayload().getMapa().getUrlBaseBffPiloto());
                        //SharedPreferencesUtils.setValor(ConfiguracoesEnum.GRUPO_CPFS_BFF_PILOTO.get(), result.getPayload().getMapa().getGrupoDeCpfsQueAcessamOBffPiloto());

                        if(result.getPayload().getMapa().getConcursoMega30Anos() != null && !result.getPayload().getMapa().getConcursoMega30Anos().toString().isEmpty()){
                            SharedPreferencesUtils.setValor(EspecialUtils.MEGA_VALOR,result.getPayload().getMapa().getConcursoMega30Anos());
                            SharedPreferencesUtils.setValor(EspecialUtils.MEGA_LINHA,getString(R.string.mega_sena_30_anos));
                            SharedPreferencesUtils.setValor(EspecialUtils.MEGA_DUAS_LINHAS,getString(R.string.mega_sena_30_anos_duas_linhas));
                        }

                        //TODO: LOTECA PAIS//
                        if(result.getPayload().getMapa().getConcursoLotecaPais() != null && !result.getPayload().getMapa().getConcursoLotecaPais().toString().isEmpty()){
                            SharedPreferencesUtils.setValor(EspecialUtils.LOTECA_PAIS_VALOR, result.getPayload().getMapa().getConcursoLotecaPais());
                            SharedPreferencesUtils.setValor(EspecialUtils.LOTECA_PAIS_LINHA, getString(R.string.loteca_pais));
                            SharedPreferencesUtils.setValor(EspecialUtils.LOTECA_PAIS_DUAS_LINHAS, getString(R.string.loteca_pais_duas_linhas));
                        }

                        atualizaIconeApp();
                    }
                    redirectApp();
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    setMenuApostasDefault();
                    redirectApp();
                }
            });
        }catch (Exception e){
            setMenuApostasDefault();
            redirectApp();
        }
    }

    private void atualizaIconeApp() {
        IconeAppEnum icone;
        if (EspecialUtils.isOutubroRosa()) {
            icone = IconeAppEnum.OUTUBRO_ROSA;
        } else {
            icone = IconeAppEnum.DEFAULT_DISABLED;
        }

        if (!icone.get().equalsIgnoreCase(SharedPreferencesUtils.getValorString(ConfiguracoesEnum.LAUNCHER_ICON.get(), ""))){
            IconAliasManager.applyIcon(this, icone);
            SharedPreferencesUtils.setValor(ConfiguracoesEnum.LAUNCHER_ICON.get(), icone.get());
        }
    }

    private boolean isIconeAPP(IconeAppEnum icone) {
        return SharedPreferencesUtils.getValorString(ConfiguracoesEnum.LAUNCHER_ICON.get(), "").equalsIgnoreCase(icone.get());
    }

    private boolean temInformacaoBolao(ParametrosConfiguraveisDTOResponse result) {
        return temDadosPayloadMapa(result) && result.getPayload().getMapa().getBolaoHabilitado() != null;
    }

    private void salvaParametrosFiltroBolao(ParametrosConfiguraveisDTO parametros) {
        if (parametros.getQtdeMinimaApostaBolaoFiltro() != null) {
            SharedPreferencesUtils.setValor(ConfiguracoesEnum.QTDE_MINIMA_APOSTA_BOLAO_FILTRO.get(), parametros.getQtdeMinimaApostaBolaoFiltro());
        }
        if (parametros.getQtdeMaximaApostaBolaoFiltro() != null) {
            SharedPreferencesUtils.setValor(ConfiguracoesEnum.QTDE_MAXIMA_APOSTA_BOLAO_FILTRO.get(), parametros.getQtdeMaximaApostaBolaoFiltro());
        }
    }

    private boolean precisaAtualizarPublicKey(ParametrosConfiguraveisDTOResponse result) {
        return temDadosPayloadMapa(result) && result.getPayload().getMapa().isAtualizaPublicKey() != null;
    }

    private void setMenuApostasDefault() {
        setMostraMenuApostas(ConfiguracoesDefaultEnum.MOSTRA_MENU_APOSTAS.asBoolean());
    }

    private void setPreferencesDefault() {
        SharedPreferencesUtils.setValor(ConfiguracoesEnum.MOSTRA_MENU_APOSTA.get(), ConfiguracoesDefaultEnum.MOSTRA_MENU_APOSTAS.asBoolean());
        SharedPreferencesUtils.setValor(ConfiguracoesEnum.IS_MICRO_SERVICO.get(), ConfiguracoesDefaultEnum.IS_MICRO_SERVICO.asBoolean());
        SharedPreferencesUtils.setValor(ConfiguracoesEnum.BOLAO.get(), ConfiguracoesDefaultEnum.BOLAO_HABILITADO.asBoolean());
        SharedPreferencesUtils.setValor(ConfiguracoesEnum.COMBO_APOSTAS.get(), ConfiguracoesDefaultEnum.COMBO_APOSTAS.asBoolean());
        SharedPreferencesUtils.setValor(ConfiguracoesEnum.IS_ATUALIZA_PUBLIC_KEY.get(), ConfiguracoesDefaultEnum.IS_ATUALIZA_PUBLIC_KEY.asBoolean());
        SharedPreferencesUtils.setValor(ConfiguracoesEnum.IS_NOVA_API.get(), ConfiguracoesDefaultEnum.IS_NOVA_API.asBoolean());
        SharedPreferencesUtils.setValor(ConfiguracoesEnum.IS_RAPIDAO.get(), ConfiguracoesDefaultEnum.IS_RAPIDAO.asBoolean());
        SharedPreferencesUtils.setValor(ConfiguracoesEnum.GRUPO_CPF_NOVA_API.get(), ConfiguracoesDefaultEnum.GRUPO_CPF_NOVA_API.asInt());
        SharedPreferencesUtils.setValor(ConfiguracoesEnum.URL_BASE_BUSCA_APOSTAS.get(), ConfiguracoesDefaultEnum.URL_BASE_BUSCA_APOSTAS.asString());
        SharedPreferencesUtils.setValor(ConfiguracoesEnum.URL_BASE_CARRINHO_PILOTO.get(), ConfiguracoesDefaultEnum.URL_BASE_CARRINHO_PILOTO.asString());
        SharedPreferencesUtils.setValor(ConfiguracoesEnum.URL_BASE_CARRINHO_PRODUCAO.get(), ConfiguracoesDefaultEnum.URL_BASE_CARRINHO_PRODUCAO.asString());
        SharedPreferencesUtils.setValor(ConfiguracoesEnum.URL_BASE_APOSTADOR.get(), ConfiguracoesDefaultEnum.URL_BASE_APOSTADOR.asString());
        SharedPreferencesUtils.setValor(ConfiguracoesEnum.GRUPO_CPFS_APOSTADOR.get(), ConfiguracoesDefaultEnum.GRUPO_CPFS_APOSTADOR.asString());
        SharedPreferencesUtils.setValor(ConfiguracoesEnum.QTDE_MINIMA_APOSTA_BOLAO_FILTRO.get(), ConfiguracoesDefaultEnum.QTDE_MINIMA_APOSTA_BOLAO_FILTRO.asInt());
        SharedPreferencesUtils.setValor(ConfiguracoesEnum.QTDE_MAXIMA_APOSTA_BOLAO_FILTRO.get(), ConfiguracoesDefaultEnum.QTDE_MAXIMA_APOSTA_BOLAO_FILTRO.asInt());
    }
    private void setMostraMenuApostas(Boolean habilitaMenuApostas) {
        SharedPreferencesUtils.setValor(ConfiguracoesEnum.MOSTRA_MENU_APOSTA.get(), habilitaMenuApostas);
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
