package br.gov.caixa.loterias.apostas.view.activity;

import static java.lang.String.format;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.VolleyError;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.gson.Gson;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.LoteriasBaseAppActivity;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.controllers.CarrinhoActivity;
import br.gov.caixa.loterias.apostas.controllers.TutorialSuperSeteActivity;
import br.gov.caixa.loterias.apostas.effect.AnalyticsEffect;
import br.gov.caixa.loterias.apostas.effect.AnimacaoEffect;
import br.gov.caixa.loterias.apostas.effect.CarrinhoEffect;
import br.gov.caixa.loterias.apostas.effect.FavoritarApostaEffect;
import br.gov.caixa.loterias.apostas.effect.ScreenSimulaEffect;
import br.gov.caixa.loterias.apostas.event.SimulaUiEvent;
import br.gov.caixa.loterias.apostas.model.bo.DadosCorporativosSilceBO;
import br.gov.caixa.loterias.apostas.model.bo.DadosUsuarioBO;
import br.gov.caixa.loterias.apostas.model.bo.MensagensNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroEquipe;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroJogoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroMesDeSorte;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroValorApostaDTO;
import br.gov.caixa.loterias.apostas.model.model.SimularApostaModel;
import br.gov.caixa.loterias.apostas.model.model.SurpresinhaApostaModel;
import br.gov.caixa.loterias.apostas.state.SimulaUiState;
import br.gov.caixa.loterias.apostas.utils.AccessibilityUtils;
import br.gov.caixa.loterias.apostas.utils.AlertDialogExperimenteLogarSingleton;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.AnalyticsHelper;
import br.gov.caixa.loterias.apostas.utils.AppCenterManager;
import br.gov.caixa.loterias.apostas.utils.BarraTituloDTO;
import br.gov.caixa.loterias.apostas.utils.Constantes;
import br.gov.caixa.loterias.apostas.utils.DialogUtils;
import br.gov.caixa.loterias.apostas.utils.EspecialUtils;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidadeMKP;
import br.gov.caixa.loterias.apostas.utils.EtapaAposta;
import br.gov.caixa.loterias.apostas.utils.FragmentUtils;
import br.gov.caixa.loterias.apostas.utils.InputUtils;
import br.gov.caixa.loterias.apostas.utils.ResultadoNavegacaoAposta;
import br.gov.caixa.loterias.apostas.utils.ServicoFactoryUtil;
import br.gov.caixa.loterias.apostas.utils.SharedPreferencesUtils;
import br.gov.caixa.loterias.apostas.utils.TextViewUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.custom.LottieManager;
import br.gov.caixa.loterias.apostas.view.fragment.EtapaFragment;
import br.gov.caixa.loterias.apostas.view.fragment.SimulaFragment;
import br.gov.caixa.loterias.apostas.view.fragment.SurpresinhaFragment2;
import br.gov.caixa.loterias.apostas.view.fragment.TimesFragment;
import br.gov.caixa.loterias.apostas.view.listener.CartelaFragmentListener;
import br.gov.caixa.loterias.apostas.view.listener.OnDialogFavoritarListener;
import br.gov.caixa.loterias.apostas.view.listener.OnDialogListener;
import br.gov.caixa.loterias.apostas.view.listener.SurpresinhaFragmentListener;
import br.gov.caixa.loterias.apostas.viewModel.SimulaViewModel;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

public class SimulaActivity extends LoteriasBaseAppActivity implements CartelaFragmentListener, SurpresinhaFragmentListener {

    public static String COR_FONTE_FUNDO_BRANCO = "COR_FONTE_FUNDO_BRANCO";
    public static String COR_FONTE_FUNDO_CLARO = "COR_FONTE_FUNDO_CLARO";
    public static String COR_FONTE_FUNDO_ESCURO = "COR_FONTE_FUNDO_ESCURO";

    public final static String APOSTA_EXTRA = "aposta";
    private int numero_concurso;
    private ConstraintLayout toolBarLoteca;
    private Button botaoLimparAposta, botaoCompletar, botaoAdicionar, botaoNextStep;
    private TextView labelValueJogosLoteca, labelValueSimplesLoteca, labelValueDuplasLoteca, labelValueTriplasLoteca;

    protected IdentificaoDeUmaApostaDas8Modalidades aposta;

    // Fragments
    private FragmentManager fragmentManager;
    private SimulaFragment cartelaFragment;
    private SurpresinhaFragment2 surpresinhaFragment;

    private TimesFragment timesFragment;
    private int typeGameColorLight;
    private int typeGameColorDark;
    private List<String> qtdNumerosList;
    private List<String> qtdConcursosList;
    private ModalidadeEnum tipoJogo;
    private String textoBotaoPrognosticosSelecionado;
    private String textoBotaoTeimosinhas;
    private ParametroJogoDTO parametroSimulacao;
    private ParametroMesDeSorte mesDeSorteCartela;
    private ParametroMesDeSorte mesDeSorteSurpresinha;
    private Boolean isEspecial = false;

    //TODO: MEGA 30 ANOS//
    private boolean isMega30;
    public EstiloModalidadeMKP estilo;
    private SimulaViewModel viewModel;
    private LottieManager lottieManager;
    private SimularApostaModel model;
    private TextView tvValorAposta;
    private TextView tvValorCarrinho;
    private LinearLayout containerComoJogar;
    private MaterialButton btnTeimosinha, btnQuantidadeNumeros, btnQuantidadeTrevos, botaoFavoritar;
    private List<Integer> qtdPrognosticosQuantidadeNumerosList;
    private TextView textSelecionados;
    private FrameLayout frameCarrinho;
    private TextView tvQuantidadeApostas;
    private SwitchMaterial switchSurpresinha;
    private LinearLayout opcaoOutrosNumerosLayout;
    private AppCompatCheckBox selecioneOutrosNumeros;
    private boolean mudouTamanhoValor = false;
    String numeroConcurso;
    public BarraTituloDTO barraTituloDTO = new BarraTituloDTO();
    private TextView textInfoEtapa;
    private EtapaAposta etapaRenderizada;
    private boolean renderizacaoSurpresinhaTime;
    private SurpresinhaApostaModel surpresinhaModel = new SurpresinhaApostaModel(this);
    private final ActivityResultLauncher<Intent> fluxoApostaLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode()
                                == ResultadoNavegacaoAposta.RESULT_RESETAR_SIMULA) {

                            resetarTelaAposRetornoDeActivity();
                        }
                    }
            );

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simula_aposta);
        init();
        onBackPressedDispatcher();
    }
    private void onBackPressedDispatcher(){
        getOnBackPressedDispatcher().addCallback(
                this,
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {

                        InputUtils.closeKeyboard(
                                SimulaActivity.this
                        );

                        SimulaUiState state =
                                viewModel.getUiState().getValue();

                        if (state != null
                                && state.isEscolhaTimeCoracaoSurpresinha()) {

                            viewModel.onEvent(
                                    new SimulaUiEvent.VoltarParaSurpresinha()
                            );
                            return;
                        }

                        if (voltarEtapaComplementar()) {
                            return;
                        }

                        if (possuiApostaEmAndamento()) {
                            exibirDialogConfirmacaoSaida();
                            return;
                        }

                        finish();
                    }
                }
        );
    }

    protected void init() {
        model = new SimularApostaModel(this);
        DadosCorporativosSilceBO dadosCorporativosSilceBO = DadosCorporativosSilceBO.getInstance();
        getExtras();
        numeroConcurso = parametroSimulacao.getConcurso().getNumero().toString();

        isMega30 = EspecialUtils.isMega30(numero_concurso, isEspecial);
        estilo = new EstiloModalidadeMKP(tipoJogo, isMega30);

        setViews();
        configurarSwitchSurpresinha();

        viewModel = new ViewModelProvider(this).get(SimulaViewModel.class);
        observeState();
        configurarEventosViewModel();

        estilo = new EstiloModalidadeMKP(tipoJogo);

        zerarApostas();

        if (aposta != null) {
            mesDeSorteCartela = aposta.getMesDeSorte();
        }

        if (tipoJogo == ModalidadeEnum.SUPER_7 && !DadosUsuarioBO.checarTutorial(DadosUsuarioBO.SUPER_SETE_TUTORIAL)) {
            Intent intent = new Intent(this,TutorialSuperSeteActivity.class);
            abrirFluxoApostaComResultado(intent);
        }
        inicializarTela();
    }
    private void inicializarTela(){
        viewModel.onEvent(new SimulaUiEvent.TelaInicializada(tipoJogo, isEspecial, parametroSimulacao));

        viewModel.onEvent(
                new SimulaUiEvent.InicializarParametrosAposta(
                        parametroSimulacao.getQuantidadeMinima() != null
                                ? parametroSimulacao.getQuantidadeMinima()
                                : 0,
                        parametroSimulacao.getTeimosinhas() != null
                                ? parametroSimulacao.getTeimosinhas().get(0)
                                : 0
                ));

        inicializarEstadoTeimosinhas();
        inicializarEstadoQuantidadeNumeros();
        etapaRenderizada = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (lottieManager != null) {
            lottieManager.destroy();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (lottieManager != null) {
            lottieManager.pause();
        }
    }

    private void setViews() {
        this.toolBarLoteca = findViewById(R.id.toolBarLoteca);
        this.botaoLimparAposta = findViewById(R.id.btnLimpar);
        this.botaoCompletar = findViewById(R.id.btnCompletar);
        this.botaoAdicionar = findViewById(R.id.btnAdicionarCarrinho);
        this.labelValueJogosLoteca = findViewById(R.id.labelValueJogosLoteca);
        this.labelValueSimplesLoteca = findViewById(R.id.labelValueSimplesLoteca);
        this.labelValueDuplasLoteca = findViewById(R.id.labelValueDuplasLoteca);
        this.labelValueTriplasLoteca = findViewById(R.id.labelValueTriplasLoteca);
        this.tvValorAposta = findViewById(R.id.tvValorAposta);
        this.tvValorCarrinho = findViewById(R.id.tvTotalCarrinho);
        this.containerComoJogar = findViewById(R.id.containerComoJogar);
        this.btnQuantidadeNumeros = findViewById(R.id.btnQuantidadeNumeros);
        this.btnQuantidadeTrevos = findViewById(R.id.btnQuantidadeTrevos);
        this.btnTeimosinha = findViewById(R.id.btnTeimosinha);
        this.textSelecionados = findViewById(R.id.textSelecionados);
        this.frameCarrinho = findViewById(R.id.frameCarrinho);
        this.tvQuantidadeApostas = findViewById(R.id.tvQuantidadeApostas);
        this.switchSurpresinha = findViewById(R.id.switchSurpresinha);
        this.botaoFavoritar = findViewById(R.id.favoritar_aposta);
        this.botaoNextStep = findViewById(R.id.btnNextStep);
        this.opcaoOutrosNumerosLayout = findViewById(R.id.opcaoOutrosNumerosLayout);
        this.selecioneOutrosNumeros = findViewById(R.id.selcioneOutrosNumeros);
        textInfoEtapa = findViewById(R.id.textInfoEtapa);
        opcaoOutrosNumerosLayout.setVisibility(View.GONE);
    }

    private void renderOpcaoOutrosNumeros(SimulaUiState state) {
        if (opcaoOutrosNumerosLayout == null || selecioneOutrosNumeros == null) {
            return;
        }

        opcaoOutrosNumerosLayout.setVisibility(state.isExibirOpcaoOutrosNumeros() ? View.VISIBLE : View.GONE);

        selecioneOutrosNumeros.setChecked(state.isOpcaoOutrosNumerosSelecionada());
    }

    private void configurarSwitchSurpresinha() {

        if (switchSurpresinha == null || estilo == null) {
            return;
        }

        switchSurpresinha.setUseMaterialThemeColors(false);

        ColorStateList trackTint = new ColorStateList(new int[][]{new int[]{android.R.attr.state_checked}, new int[]{}}, new int[]{ContextCompat.getColor(this, estilo.getCorClara()), ContextCompat.getColor(this, R.color.cinza70)});

        switchSurpresinha.setTrackTintList(trackTint);

        StateListDrawable thumb = new StateListDrawable();

        thumb.addState(new int[]{android.R.attr.state_checked}, criarThumbChecked());

        thumb.addState(new int[]{}, AppCompatResources.getDrawable(this, R.drawable.surpresinha_thumb_unchecked));

        switchSurpresinha.setThumbTintList(null);
        switchSurpresinha.setThumbDrawable(thumb);

        switchSurpresinha.jumpDrawablesToCurrentState();
    }

    private void observeState() {
        viewModel.getUiState().observe(this, this::render);
        viewModel.getUiState().observe(this, this::triggerAnalytics);
    }

    private void renderAnimacao(
            SimulaUiState state
    ) {
        AnimacaoEffect comando =
                state.getComandoAnimacao();

        if (comando == null) {
            return;
        }

        if (comando instanceof
                AnimacaoEffect.Exibir) {

            AnimacaoEffect.Exibir exibir =
                    (AnimacaoEffect.Exibir)
                            comando;

            if (lottieManager != null) {
                lottieManager.destroy();
            }

            lottieManager =
                    new LottieManager(
                            this,
                            exibir.getAnimacaoRes()
                    );

            lottieManager.setSpeed(
                    exibir.getVelocidade()
            );

            lottieManager.start();
        }

        viewModel.onEvent(
                new SimulaUiEvent
                        .ComandoAnimacaoConsumido()
        );
    }

    private void renderFavorito(SimulaUiState state) {

        if (state.isApostaFavoritada()) {

            botaoFavoritar.setIconResource(R.drawable.ic_favoritado_simula);

            botaoFavoritar.setText(getString(R.string.aposta_favoritada));

        } else {

            botaoFavoritar.setIconResource(R.drawable.ic_star_simula);

            botaoFavoritar.setText(getString(R.string.favoritar_aposta));
        }
    }
    private void sincronizarFragmento(
            SimulaUiState state
    ) {

        EtapaAposta etapaAtual =
                state.getEtapaAposta();

        if (etapaAtual == null) {
            etapaAtual = EtapaAposta.NUMEROS;
        }

        boolean escolhaTimeSurpresinha =
                state.isEscolhaTimeCoracaoSurpresinha();

        if (etapaRenderizada == etapaAtual
                && renderizacaoSurpresinhaTime == escolhaTimeSurpresinha) {
            return;
        }

        renderizacaoSurpresinhaTime =
                escolhaTimeSurpresinha;


        etapaRenderizada = etapaAtual;

        renderEtapaAtual(
                etapaAtual,
                state
        );
    }

    private void render(SimulaUiState state) {
        Log.e(
                "TIME_SURP",
                "etapa="
                        + state.getEtapaAposta()
                        + " escolha="
                        + state.isEscolhaTimeCoracaoSurpresinha()
                        + " surpresinha="
                        + state.isSurpresinhaHabilitada()
        );
        sincronizarFragmento(state);
        renderAnimacao(state);
        barraTituloDTO = state.getBarraTituloDTO();
        if (barraTituloDTO != null) {
            renderCabecalho(barraTituloDTO);
        }
        renderParametrosAposta(state);
        renderQuantidadeNumeros(state);
        renderCarrinho(state);

        renderComandoCarrinho(state.getComandoCarrinho());
        renderDialogComoJogar(state);
        renderAbrirCarrinho(state);
        renderComandoTelaSimula(state);
        renderSwitchSurpresinha(state);
        renderOpcaoOutrosNumeros(state);
        renderEstadoBotoes(state);
        renderFavorito(state);
        renderTextoSelecionados(state);
        renderValorAposta(state);
        renderDialogFavoritarAposta(state);
        renderComandoFavoritarAposta(state);
        renderInfoEtapa(state);
        renderQuantidadeTrevos(state);
        renderAvisoQuantidadeTrevos(state);
    }
    private void renderQuantidadeTrevos(
            SimulaUiState state
    ) {

        if (!state.isExibirDialogQuantidadeTrevos()) {
            return;
        }

        DialogUtils.showDialogListItens(
                this,
                getString(R.string.quant_de_trevos),
                state.getSubtituloTrevos(),
                state.getLabelsQuantidadeTrevos(),
                "Confirmar",
                "Cancelar",
                onQuantidadeTrevosDialogListener()
        );

        viewModel.onEvent(
                new SimulaUiEvent.DialogQuantidadeTrevosConsumido()
        );
    }
    private void renderInfoEtapa(
            SimulaUiState state
    ) {
        if (textInfoEtapa == null) {
            return;
        }

        boolean escolhaTimeSurpresinha =
                state.isSurpresinhaHabilitada()
                        && state.isEscolhaTimeCoracaoSurpresinha()
                        && state.getEtapaAposta() == EtapaAposta.TIME_CORACAO;

        if (escolhaTimeSurpresinha) {
            textInfoEtapa.setVisibility(View.GONE);
            return;
        }

        textInfoEtapa.setVisibility(
                state.getExibirInfoEtapa()
                        ? View.VISIBLE
                        : View.GONE
        );

        String texto;
        switch (state.getEtapaAposta()) {
            case TREVOS:
            case MES_SORTE:
            case TIME_CORACAO:
                texto = "2/2";
                break;
            case NUMEROS:
            default:
                texto = "1/2";
                break;
        }

        textInfoEtapa.setText(texto);
    }

    private void triggerAnalytics(
            SimulaUiState state
    ) {

        AnalyticsEffect comando =
                state.getComandoAnalytics();

        if (comando == null) {
            return;
        }

        if (comando
                instanceof AnalyticsEffect
                .QuantidadeNumerosConfirmada) {

            AnalyticsEffect.QuantidadeNumerosConfirmada c =
                    (AnalyticsEffect.QuantidadeNumerosConfirmada)
                            comando;

            AnalyticsHelper.getInstance().logSelectContent(
                    AnalyticsHelper.Tela.MONTAR_APOSTA,
                    AnalyticsHelper.JourneyParams.APOSTAR,
                    AnalyticsHelper.SubJourneyParams.APOSTA_SIMPLES,
                    AnalyticsHelper.ContentCategoryParams.CONFIG,
                    c.getModalidade(),
                    c.getValorSelecionado()
            );
        } else if (comando
                instanceof AnalyticsEffect
                .TeimosinhaConfirmada) {

            AnalyticsEffect.TeimosinhaConfirmada c =
                    (AnalyticsEffect.TeimosinhaConfirmada)
                            comando;

            AnalyticsHelper.getInstance().logSelectContent(
                    AnalyticsHelper.Tela.MONTAR_APOSTA,
                    AnalyticsHelper.JourneyParams.APOSTAR,
                    AnalyticsHelper.SubJourneyParams.APOSTA_SIMPLES,
                    AnalyticsHelper.ContentCategoryParams.CONFIG,
                    c.getModalidade(),
                    c.getValorSelecionado()
            );
        }

        viewModel.onEvent(
                new SimulaUiEvent.ComandoAnalyticsConsumido()
        );
    }

    private void renderComandoFavoritarAposta(SimulaUiState state) {
        FavoritarApostaEffect comando = state.getComandoFavoritarAposta();

        if (comando == null) {
            return;
        }

        if (comando instanceof FavoritarApostaEffect.Sucesso) {
            AppCenterManager.registraEvento(getResources().getString(R.string.evento_efetuou_adicao_aposta_favorita_sucesso));
            viewModel.onEvent(new SimulaUiEvent.ComandoFavoritarApostaConsumido());
            return;
        }

        if (comando instanceof FavoritarApostaEffect.ConfirmarValidacaoNegocial) {
            FavoritarApostaEffect.ConfirmarValidacaoNegocial confirmar = (FavoritarApostaEffect.ConfirmarValidacaoNegocial) comando;

            DialogUtils.dialogSim(this, confirmar.getMensagem(), (dialog, which) -> viewModel.onEvent(new SimulaUiEvent.ConfirmarSalvarApostaFavorita(confirmar.getApostaFavorita())));

            viewModel.onEvent(new SimulaUiEvent.ComandoFavoritarApostaConsumido());
            return;
        }

        if (comando instanceof FavoritarApostaEffect.RedirecionarErro) {
            FavoritarApostaEffect.RedirecionarErro erro = (FavoritarApostaEffect.RedirecionarErro) comando;

            RedirectNetwork.checkRedirect(erro.getError(), this);

            viewModel.onEvent(new SimulaUiEvent.ComandoFavoritarApostaConsumido());
        }
    }

    private void solicitarSalvarFavoritoDoFragmentAtual(
            String nome
    ) {
        EtapaFragment etapa = getEtapaAtual();

        if (etapa != null) {
            etapa.onSalvarFavoritoRodapeConfirmado(nome);
        }
    }

    private void renderDialogFavoritarAposta(SimulaUiState state) {
        if (!state.isExibirDialogFavoritarAposta()) {
            return;
        }

        Dialog dialog = DialogUtils.buildDialogFavoritar(this, getString(R.string.favoritar_aposta), getString(R.string.exemplo_aposta), getString(R.string.informe_favorita), new OnDialogFavoritarListener() {
            @Override
            public void Confirmar(String nome, boolean manterSurpresinhas) {
                if (TextUtils.isEmpty(nome)) {
                    DialogUtils.dialogEntendi(SimulaActivity.this, getString(R.string.msg_favor_inserir_nome_aposta));
                    return;
                }

                solicitarSalvarFavoritoDoFragmentAtual(nome);
            }

            @Override
            public void Cancelar() {
            }
        }, false);

        if (dialog != null) {
            dialog.show();
        }

        viewModel.onEvent(new SimulaUiEvent.DialogFavoritarApostaConsumido());
    }

    private void renderValorAposta(SimulaUiState state) {

        BigDecimal valor = state.getValorAposta() != null ? state.getValorAposta() : BigDecimal.ZERO;

        if (state.isValorGrande()) {

            if (!mudouTamanhoValor) {
                TextViewUtils.mudarTamanhoPorPorcentagem(tvValorAposta, -30);

                mudouTamanhoValor = true;
            }

        } else {

            if (mudouTamanhoValor) {
                TextViewUtils.mudarTamanhoPorPorcentagem(tvValorAposta, 30);

                mudouTamanhoValor = false;
            }
        }

        ViewUtils.setMoedaFormatHtml(valor, tvValorAposta);

    }

    private void renderTextoSelecionados(
            SimulaUiState state
    ) {
        if (textSelecionados == null) {
            return;
        }

        String texto;

        switch (state.getEtapaAposta()) {

            case MES_SORTE:
                texto = "Selecione o mês de sorte:";
                break;

            case TREVOS:
                texto = "Selecione os trevos:";
                break;

            case TIME_CORACAO:
                texto = "Selecione o time do coração:";
                break;

            default:
                texto = state.getTextoSelecionados();
                break;
        }

        textSelecionados.setText(texto);
    }

    private void renderEstadoBotoes(SimulaUiState state) {
        boolean modalidadeComSegundaEtapa = state.getTipoJogo() == ModalidadeEnum.MAIS_MILIONARIA || state.getTipoJogo() == ModalidadeEnum.DIA_DE_SORTE || state.getTipoJogo() == ModalidadeEnum.TIMEMANIA;

        EtapaAposta etapaAtual = state.getEtapaAposta() != null ? state.getEtapaAposta() : EtapaAposta.NUMEROS;

        boolean etapaNumeros = etapaAtual == EtapaAposta.NUMEROS;

        boolean mostrarNextStep =
                modalidadeComSegundaEtapa
                        && etapaNumeros
                        && !state.isSurpresinhaHabilitada();

        boolean mostrarAdicionar = !mostrarNextStep;

        boolean mostrarFavoritar = state.isMostrarSalvarAposta() || state.isApostaFavoritada();


        boolean mostrarBtnQuantidadeTrevos =
                state.getTipoJogo() == ModalidadeEnum.MAIS_MILIONARIA
                        && etapaAtual == EtapaAposta.TREVOS;

        boolean mostrarBtnQuantidadeNumeros =
                !mostrarBtnQuantidadeTrevos;

        int corIcon = ContextCompat.getColor(this, estilo.getCorFonteFundoClaro());

        boolean escolhaTimeSurpresinha =
                state.isSurpresinhaHabilitada()
                        && state.isEscolhaTimeCoracaoSurpresinha()
                        && etapaAtual == EtapaAposta.TIME_CORACAO;

        if (escolhaTimeSurpresinha) {
            if (btnTeimosinha != null) {
                btnTeimosinha.setVisibility(View.GONE);
            }

            if (btnQuantidadeNumeros != null) {
                btnQuantidadeNumeros.setVisibility(View.GONE);
            }

            if (btnQuantidadeTrevos != null) {
                btnQuantidadeTrevos.setVisibility(View.GONE);
            }

            if (textInfoEtapa != null) {
                textInfoEtapa.setVisibility(View.GONE);
            }

            if (botaoNextStep != null) {
                botaoNextStep.setVisibility(View.GONE);
            }

            if (botaoCompletar != null) {
                botaoCompletar.setVisibility(View.GONE);
            }

            if (botaoLimparAposta != null) {
                botaoLimparAposta.setVisibility(View.GONE);
            }

            if (botaoFavoritar != null) {
                botaoFavoritar.setVisibility(View.GONE);
            }

            if (botaoAdicionar != null) {
                botaoAdicionar.setVisibility(View.VISIBLE);
                botaoAdicionar.setEnabled(state.isBotaoAdicionarHabilitado());

                if (botaoAdicionar instanceof MaterialButton) {
                    visualModalidadeBotao(
                            state.isBotaoAdicionarHabilitado(),
                            (MaterialButton) botaoAdicionar
                    );
                }
            }

            return;
        }

        if (btnQuantidadeNumeros != null) {

            btnQuantidadeNumeros.setVisibility(
                    mostrarBtnQuantidadeNumeros
                            ? View.VISIBLE
                            : View.GONE
            );


            visualModalidadeBotao(
                    true,
                    btnQuantidadeNumeros
            );
            btnQuantidadeNumeros.setIconTint(android.content.res.ColorStateList.valueOf(corIcon));
        }

        if (btnQuantidadeTrevos != null) {

            btnQuantidadeTrevos.setVisibility(
                    mostrarBtnQuantidadeTrevos
                            ? View.VISIBLE
                            : View.GONE
            );

            visualModalidadeBotao(
                    true,
                    btnQuantidadeTrevos
            );
            btnQuantidadeTrevos.setIconTint(android.content.res.ColorStateList.valueOf(corIcon));
        }


        if (botaoNextStep != null) {
            botaoNextStep.setVisibility(mostrarNextStep ? View.VISIBLE : View.GONE);

            botaoNextStep.setEnabled(state.isBotaoNextStepHabilitado());
            botaoNextStep.setText(state.getTextoEscolha());

            if (botaoNextStep instanceof MaterialButton) {
                visualModalidadeBotao(state.isBotaoNextStepHabilitado(), (MaterialButton) botaoNextStep);
            }
        }

        if (botaoAdicionar != null) {
            botaoAdicionar.setVisibility(mostrarAdicionar ? View.VISIBLE : View.GONE);

            botaoAdicionar.setEnabled(state.isBotaoAdicionarHabilitado());

            if (botaoAdicionar instanceof MaterialButton) {
                visualModalidadeBotao(state.isBotaoAdicionarHabilitado(), (MaterialButton) botaoAdicionar);
            }
        }

        if (botaoFavoritar != null) {
            botaoFavoritar.setVisibility(mostrarFavoritar ? View.VISIBLE : View.GONE);
        }

        if (botaoCompletar != null) {
            boolean surpresinhaHabilitada = state.isSurpresinhaHabilitada();

            boolean mostrarCompletar = state.isBotaoFinalizarVisivel() && !mostrarFavoritar && !surpresinhaHabilitada;

            botaoCompletar.setVisibility(mostrarCompletar ? View.VISIBLE : View.GONE);

            botaoCompletar.setEnabled(state.isBotaoCompletarHabilitado());
            botaoCompletar.setText(
                    state.getTextoCompletar()
            );
        }

        if (botaoLimparAposta != null) {
            botaoLimparAposta.setEnabled(state.isBotaoLimparHabilitado());
        }
    }

    private void visualModalidadeBotao(boolean habilitado, MaterialButton button) {
        if (habilitado) {

            button.setBackgroundTintList(ContextCompat.getColorStateList(this, estilo.getCorClara()));

            button.setTextColor(ContextCompat.getColor(this, estilo.getCorFonteFundoClaro()));

            button.setStrokeColor(ContextCompat.getColorStateList(this, estilo.getCorEscura()));

        } else {

            // volta para o visual original do XML

            button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.disabled)));

            button.setTextColor(ContextCompat.getColorStateList(this, R.color.text_rodape));

            button.setStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.cinza70)));
        }
    }

    private void renderSwitchSurpresinha(SimulaUiState state) {

        boolean surpresinhaHabilitada = state.isSurpresinhaHabilitada();
        switchSurpresinha.setChecked(state.isSurpresinhaHabilitada());

        if (botaoCompletar != null) {
            botaoCompletar.setVisibility(surpresinhaHabilitada ? View.GONE : View.VISIBLE);
        }

        if (botaoLimparAposta != null) {
            botaoLimparAposta.setVisibility(surpresinhaHabilitada ? View.GONE : View.VISIBLE);
        }

        if (textSelecionados != null) {
            textSelecionados.setVisibility(surpresinhaHabilitada ? View.GONE : View.VISIBLE);
        }
    }

    private Drawable criarThumbChecked() {

        Drawable drawableBase =
                AppCompatResources.getDrawable(
                        this,
                        R.drawable.surpresinha_thumb_checked
                );

        if (!(drawableBase instanceof LayerDrawable)) {
            return new GradientDrawable();
        }

        LayerDrawable drawable =
                (LayerDrawable) drawableBase.mutate();

        Drawable layer = drawable.getDrawable(0);

        if (layer instanceof GradientDrawable) {
            ((GradientDrawable) layer).setColor(
                    ContextCompat.getColor(
                            this,
                            estilo.getCorEscura()
                    )
            );
        }

        return drawable;
    }

    private void renderAvisoQuantidadeTrevos(
            SimulaUiState state
    ) {

        if (!state.isExibirAvisoTrevosMaximo()) {
            return;
        }

        DialogUtils.dialogEntendi(
                this,
                getString(
                        R.string.msg_qtd_max_ultrapassada_milionaria
                )
        );

        viewModel.onEvent(
                new SimulaUiEvent
                        .AvisoTrevosMaximoConsumido()
        );
    }
    private void renderComandoTelaSimula(SimulaUiState state) {
        ScreenSimulaEffect effect = state.getComandoTelaSimula();

        if (effect == null) {
            return;
        }

        if (effect instanceof ScreenSimulaEffect.AbrirSurpresinha) {


            executarAbrirSurpresinha();

            viewModel.onEvent(new SimulaUiEvent.ComandoTelaSimulaConsumido());

            return;
        }
        if (effect instanceof ScreenSimulaEffect.AtualizarQuantidadeTrevos) {

            ScreenSimulaEffect.AtualizarQuantidadeTrevos comando = (ScreenSimulaEffect.AtualizarQuantidadeTrevos) effect;

            EtapaFragment etapa =
                    getEtapaAtual();

            if (etapa != null) {

                etapa.atualizarQuantidadeTrevos(
                        comando.getValorSelecionado()
                );

            }

            viewModel.onEvent(
                    new SimulaUiEvent.ComandoTelaSimulaConsumido()
            );

            return;
        }

        if (effect instanceof ScreenSimulaEffect.VoltarEtapa) {

            etapaRenderizada = null;

            renderEtapaAtual(
                    ((ScreenSimulaEffect.VoltarEtapa) effect)
                            .getEtapa(),
                    state
            );

            viewModel.onEvent(
                    new SimulaUiEvent.ComandoTelaSimulaConsumido()
            );

            return;
        }
    }

    private void renderEtapaAtual(
            EtapaAposta etapa,
            SimulaUiState state
    ) {

        switch (etapa) {

            case TREVOS:
                trevosFragment(state.getDezenasSelecionadas());
                break;

            case MES_SORTE:
                mesDeSorteFragment(state.getDezenasSelecionadas());
                break;

            case TIME_CORACAO:
                if (state.isEscolhaTimeCoracaoSurpresinha()) {
                    timesFragmentSurpresinha();
                } else {
                    timesFragment(state.getDezenasSelecionadas());
                }
                break;

            default:
                timesFragment = null;

                abrirTelaCartela();

                break;
        }
    }

    private void abrirTelaSurpresinha() {
        FragmentUtils.startFragmentAllowingStateLoss(fragmentManager, R.id.fragmentSimularApostas, surpresinhaFragment);
    }
    private void timesFragmentSurpresinha() {
        timesFragment =
                TimesFragment.newInstance(
                        parametroSimulacao,
                        new ArrayList<>(),
                        true
                );

        FragmentUtils.startFragmentAllowingStateLoss(
                fragmentManager,
                R.id.fragmentSimularApostas,
                timesFragment
        );
    }

    private void executarAbrirSurpresinha() {

        if (surpresinhaFragment == null) {
            return;
        }

        surpresinhaFragment.setArguments(new Bundle());

        abrirTelaSurpresinha();

    }

    private void renderDialogComoJogar(SimulaUiState state) {

        if (!state.isExibirDialogComoJogar()) {
            return;
        }

        DialogUtils.dialogHtml(this, state.getTitleComoJogar(), state.getMensagemComoJogar());

        viewModel.onEvent(new SimulaUiEvent.DialogComoJogarConsumido());
    }

    private void renderParametrosAposta(SimulaUiState state) {

        textoBotaoTeimosinhas = state.getTextoBotaoTeimosinhas();

        if (btnTeimosinha != null) {
            if (state.getQtdConcursoSelecionado() == 0) {
                btnTeimosinha.setText(R.string.label_sem_teimosinha);
            } else if (textoBotaoTeimosinhas != null && !textoBotaoTeimosinhas.trim().isEmpty()) {
                btnTeimosinha.setText(textoBotaoTeimosinhas);
            } else {
                btnTeimosinha.setText(R.string.label_sem_teimosinha);
            }
        }

        if (state.isExibirDialogTeimosinha()) {
            abrirDialogTeimosinhas(state);

            viewModel.onEvent(new SimulaUiEvent.ConsumirEventoUnico());
        }

        if (state.isExibirDialogSemTeimosinha()) {
            DialogUtils.dialogEntendi(SimulaActivity.this, getString(R.string.nao_teimosinhas));

            viewModel.onEvent(new SimulaUiEvent.ConsumirEventoUnico());
        }

        if (state.isAtualizarValorApostaPorTeimosinha()) {
            atualizarValorApostaPorTeimosinha();

            viewModel.onEvent(new SimulaUiEvent.ValorApostaAtualizado());
        }
    }

    private void renderQuantidadeNumeros(SimulaUiState state) {
        textoBotaoPrognosticosSelecionado = state.getTextoBotaoPrognosticosSelecionado();

        String textoQuantidadeNumeros;
        String textoQuantidadeTrevos = state.getTextoBotaoQuantidadeTrevos();

        if (textoBotaoPrognosticosSelecionado != null && !textoBotaoPrognosticosSelecionado.trim().isEmpty()) {

            textoQuantidadeNumeros = textoBotaoPrognosticosSelecionado;

        } else {
            textoQuantidadeNumeros = format(getResources().getString(R.string.percent_d_numeros), state.getQtdDezenasPossiveisSelecionado());
        }

        if (btnQuantidadeNumeros != null) {
            btnQuantidadeNumeros.setText(textoQuantidadeNumeros);
        }

        if (btnQuantidadeTrevos != null
                && state.getEtapaAposta() == EtapaAposta.TREVOS) {

            btnQuantidadeTrevos.setText(
                    state.getTextoBotaoQuantidadeTrevos()
            );
        }

        if (state.isExibirDialogQuantidadeNumeros()) {
            abrirDialogQuantidadeNumeros(state);

            viewModel.onEvent(new SimulaUiEvent.DialogQuantidadeNumerosConsumido());
        }

        if (state.isExibirAvisoQuantidadeNumerosMaxima()) {
            DialogUtils.dialogEntendi(SimulaActivity.this, getString(R.string.msg_qtd_max_ultrapassada));

            viewModel.onEvent(new SimulaUiEvent.AvisoQuantidadeNumerosConsumido());
        }

        if (state.isAtualizarValorApostaPorQuantidadeNumeros()) {
            atualizarValorApostaPorQuantidadeNumeros(state);

            viewModel.onEvent(new SimulaUiEvent.ValorApostaQuantidadeNumerosAtualizado());
        }
    }

    private void renderComandoCarrinho(CarrinhoEffect comando) {

        if (comando == null) {
            return;
        }

        if (comando instanceof CarrinhoEffect.MostrarConfirmacaoLimiteDiario) {

            DialogUtils.dialogSim(this, getString(R.string.MA014), (dialog, which) -> viewModel.onEvent(new SimulaUiEvent.ConfirmarAdicionarCarrinho()));

            viewModel.onEvent(new SimulaUiEvent.ComandoCarrinhoConsumido());

            return;
        }

        if (comando instanceof CarrinhoEffect.ExecutarAdicionarCarrinhoEffect) {

            CarrinhoEffect.ExecutarAdicionarCarrinhoEffect add = (CarrinhoEffect.ExecutarAdicionarCarrinhoEffect) comando;

            model.adicionaApostaNoCarrinho(this, add.getAposta(), add.getBarraTituloDTO());

            viewModel.onEvent(new SimulaUiEvent.ComandoCarrinhoConsumido());

            return;
        }
        if (comando instanceof
                CarrinhoEffect.AdicionarSurpresinha) {

            if (surpresinhaModel == null) {
                surpresinhaModel =
                        new SurpresinhaApostaModel(this);
            }

            surpresinhaModel.addSurpresinhaCarrinho(
                    ((CarrinhoEffect.AdicionarSurpresinha)
                            comando)
                            .getDto()
            );

            viewModel.onEvent(
                    new SimulaUiEvent.ComandoCarrinhoConsumido()
            );

            return;
        }


        if (comando instanceof CarrinhoEffect.AdicionarTimeSurpresa) {

            CarrinhoEffect.AdicionarTimeSurpresa timeSurpresa = (CarrinhoEffect.AdicionarTimeSurpresa) comando;

            adicionarTimeSurpresa(timeSurpresa.getEquipeSelecionada());

            viewModel.onEvent(new SimulaUiEvent.ComandoCarrinhoConsumido());
        }
    }

    private void renderCabecalho(BarraTituloDTO cabecalho) {

        FragmentUtils.startCabecalhoSimulacao(getSupportFragmentManager(), R.id.id_fg_barra_titulo, tipoJogo, cabecalho.getNumeroConcurso(), cabecalho.getDataSorteio(), cabecalho.isEspecial(), getCorBackgroundBarraTitulo(), true);
    }

    private int getCorBackgroundBarraTitulo() {
        return estilo.getCorClara();
    }

    private void getExtras() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            //TODO: MEGA 30 ANOS//
            if (extras.containsKey(APOSTA_EXTRA)) {
                this.aposta = ((IdentificaoDeUmaApostaDas8Modalidades) extras.getSerializable(APOSTA_EXTRA));
                System.out.println("teste aposta " + aposta);
            }
            if (extras.containsKey("NUMERO_CONCURSO")) {
                this.numero_concurso = extras.getInt("NUMERO_CONCURSO");
            }
            isEspecial = extras.getBoolean(getResources().getString(R.string.extra_especial), false);
            tipoJogo = (ModalidadeEnum) extras.getSerializable(getResources().getString(R.string.tipoAposta));
            parametroSimulacao = new Gson().fromJson(extras.getString(getResources().getString(R.string.extra_modalidade)), ParametroJogoDTO.class);
        }
    }

    private void configurarEventosViewModel() {

        if (containerComoJogar != null) {
            AccessibilityUtils.setBotao(containerComoJogar);
            containerComoJogar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    viewModel.onEvent(new SimulaUiEvent.ComoJogarClicado());
                }
            });
        }
        if(frameCarrinho != null){
            AccessibilityUtils.setBotao(frameCarrinho);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        AnalyticsHelper.getInstance().logViewScreenAposta(
                AnalyticsHelper.JourneyParams.APOSTAR,
                AnalyticsHelper.SubJourneyParams.APOSTA_SIMPLES,
                AnalyticsHelper.Tela.MONTAR_APOSTA,
                ModalidadeEnum.fromString(tipoJogo),
                numeroConcurso
        );

        viewModel.onEvent(
                new SimulaUiEvent.CarregarCarrinho()
        );
    }

    public void zerarApostas() {
        qtdNumerosList = new ArrayList<>();
        qtdPrognosticosQuantidadeNumerosList = new ArrayList<>();
        qtdConcursosList = new ArrayList<>();
        textoBotaoTeimosinhas = getString(R.string.label_sem_teimosinha);
        mesDeSorteCartela = null;
        mesDeSorteSurpresinha = null;
        configuraParametrosJogo();
        configuraFragments();
        configuraActivity();
        zerarApostasLoteca();
    }

    public void zerarApostasLoteca() {
        this.setLabelValueJogosLoteca(Constantes.ZERO_STRING);
        this.setLabelValueSimplesLoteca(Constantes.ZERO_STRING);
        this.setLabelValueDuplasLoteca(Constantes.ZERO_STRING);
        this.setLabelValueTriplasLoteca(Constantes.ZERO_STRING);
        setColorValuesLoteca();
    }

    private void executarFavoritarRodape() {
        if (!DadosUsuarioBO.checarUsuarioLogado(this)) {
            AlertDialogExperimenteLogarSingleton.show(this, false, null);
            return;
        }

        viewModel.onEvent(new SimulaUiEvent.FavoritarApostaClicado());
    }


    private void renderAbrirCarrinho(SimulaUiState state) {

        if (!state.isAbrirCarrinho()) {
            return;
        }

        abrirCarrinho();

        viewModel.onEvent(new SimulaUiEvent.AbrirCarrinhoConsumido());
    }

    private void abrirCarrinho() {

        AlertDialogUtils.show(this);

        ServicoFactoryUtil.getApostaService().buscaCarrinho(new RequestListener<CarrinhoDTOResponse>() {

            @Override
            public void onResponse(CarrinhoDTOResponse response) {

                AlertDialogUtils.dismiss();

                Bundle args = new Bundle();

                args.putSerializable(CarrinhoActivity.CARRINHO, response.getPayload());
                Intent intent = new Intent(SimulaActivity.this, CarrinhoActivity.class).putExtras(args);
                abrirFluxoApostaComResultado(intent);
            }

            @Override
            public void onErrorResponse(VolleyError error) {

                AlertDialogUtils.dismiss();

                if (MensagensNetwork.isUnauthorizedError(error)) {
                    Intent intent = new Intent(SimulaActivity.this, CarrinhoActivity.class).putExtra(CarrinhoActivity.LER_CARRINHO_LOCAL, true);
                    abrirFluxoApostaComResultado(intent);

                } else {

                    RedirectNetwork.checkRedirect(error, SimulaActivity.this);
                }
            }
        });
    }


    public ParametroJogoDTO getParametroSimulacao() {
        return parametroSimulacao;
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();

        return true;
    }

    @Override
    public int verificaNovoQtdDezenasMax(int qtdDezenasAtual) {

        if (parametroSimulacao == null || parametroSimulacao.getQuantidadeMaxima() == null) {
            return qtdDezenasAtual;
        }

        int quantidadeMaxima = parametroSimulacao.getQuantidadeMaxima();

        int novaQuantidade = Math.min(qtdDezenasAtual + 1, quantidadeMaxima);

        return novaQuantidade;
    }

    @Override
    public void adicionarTimeSurpresa(ParametroEquipe equipeSelecionada) {
        Bundle bundle = new Bundle();
        bundle.putString(getResources().getString(R.string.extra_equipe_selecionada), (new Gson()).toJson(equipeSelecionada));
        surpresinhaFragment.setArguments(bundle);
        abrirTelaSurpresinha();
    }

    @Override
    public void escolherOutroTimeSurpresinha() {
        timesFragment =
                TimesFragment.newInstance(
                        parametroSimulacao,
                        new ArrayList<>(),
                        true
                );

        FragmentUtils.startFragmentAllowingStateLoss(
                fragmentManager,
                R.id.fragmentSimularApostas,
                timesFragment
        );
    }

    public void setColorValuesLoteca() {
        this.labelValueJogosLoteca.setTextColor(ContextCompat.getColor(this,estilo.getCorClara()));
        this.labelValueSimplesLoteca.setTextColor(ContextCompat.getColor(this,estilo.getCorClara()));
        this.labelValueDuplasLoteca.setTextColor(ContextCompat.getColor(this,estilo.getCorClara()));
        this.labelValueTriplasLoteca.setTextColor(ContextCompat.getColor(this,estilo.getCorClara()));
    }

    public void setLabelValueJogosLoteca(String labelValueJogosLoteca) {
        this.labelValueJogosLoteca.setText(labelValueJogosLoteca);
    }

    public void setLabelValueSimplesLoteca(String labelValueSimplesLoteca) {
        this.labelValueSimplesLoteca.setText(labelValueSimplesLoteca);
    }

    public void setLabelValueDuplasLoteca(String labelValueDuplasLoteca) {
        this.labelValueDuplasLoteca.setText(labelValueDuplasLoteca);
    }

    public void setLabelValueTriplasLoteca(String labelValueTriplasLoteca) {
        this.labelValueTriplasLoteca.setText(labelValueTriplasLoteca);
    }

    public TextView getLabelValueSimplesLoteca() {
        return labelValueSimplesLoteca;
    }

    public TextView getLabelValueJogosLoteca() {
        return labelValueJogosLoteca;
    }

    public TextView getLabelValueDuplasLoteca() {
        return labelValueDuplasLoteca;
    }

    public TextView getLabelValueTriplasLoteca() {
        return labelValueTriplasLoteca;
    }

    public Button getBotaoLimparAposta() {
        return botaoLimparAposta;
    }

    public Button getBotaoCompletar() {
        return botaoCompletar;
    }

    public TextView getValorApostaCartela() {
        return tvValorAposta;
    }

    public IdentificaoDeUmaApostaDas8Modalidades getAposta() {
        return aposta;
    }

    public BigDecimal getValorTotalApostaAtual() {

        if (viewModel == null || viewModel.getUiState() == null || viewModel.getUiState().getValue() == null || viewModel.getUiState().getValue().getValorAposta() == null) {

            return BigDecimal.ZERO;
        }

        return viewModel.getUiState().getValue().getValorAposta();
    }

    public void mesDeSorteFragment(List<Integer> dezenasSelecionadas) {
        FragmentUtils.startMesesFragment2(fragmentManager, R.id.fragmentSimularApostas, parametroSimulacao, (ArrayList<Integer>) dezenasSelecionadas);
    }

    public void trevosFragment(List<Integer> dezenasSelecionadas) {
        FragmentUtils.startTrevosFragment2(fragmentManager, R.id.fragmentSimularApostas, parametroSimulacao, (ArrayList<Integer>) dezenasSelecionadas);
    }
    public void timesFragment(List<Integer> dezenasSelecionadas) {
        timesFragment = FragmentUtils.startTimesFragment(fragmentManager, R.id.fragmentSimularApostas, parametroSimulacao, (ArrayList<Integer>) dezenasSelecionadas);
    }


    public ParametroMesDeSorte getMesDeSorteCartela() {
        return mesDeSorteCartela;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ResultadoNavegacaoAposta.RESULT_RESETAR_SIMULA) {
            resetarTelaAposRetornoDeActivity();
        }
    }

    private void configuraParametrosJogo() {
        if (parametroSimulacao != null && parametroSimulacao.getValoresAposta() != null) {
            for (ParametroValorApostaDTO parametro : parametroSimulacao.getValoresAposta()) {

                if (tipoJogo == ModalidadeEnum.MAIS_MILIONARIA) {
                    if (parametro.getNumeroTrevos() != null && parametroSimulacao.getTrevos() != null && parametroSimulacao.getTrevos().getQtdMinima() != null && parametro.getNumeroTrevos().equals(parametroSimulacao.getTrevos().getQtdMinima())) {

                        qtdNumerosList.add(getResources().getString(R.string.string_vazia) + parametro.getNumeroPrognosticos() + getResources().getString(R.string.espaco_numeros_por_espaco) + ViewUtils.getMoedaFormat(parametro.getValor()));

                        qtdPrognosticosQuantidadeNumerosList.add(parametro.getNumeroPrognosticos());
                    }
                } else {
                    qtdNumerosList.add(getResources().getString(R.string.string_vazia) + parametro.getNumeroPrognosticos() + getResources().getString(R.string.espaco_numeros_por_espaco) + ViewUtils.getMoedaFormat(parametro.getValor()));

                    qtdPrognosticosQuantidadeNumerosList.add(parametro.getNumeroPrognosticos());
                }
            }

            if (parametroSimulacao.getTeimosinhas() != null) {
                for (Integer numeroTeimosinha : parametroSimulacao.getTeimosinhas()) {
                    if (numeroTeimosinha == 0) {
                        qtdConcursosList.add("Nenhum concurso");
                    } else if (numeroTeimosinha == 1) {
                        qtdConcursosList.add(numeroTeimosinha + getResources().getString(R.string.espaco_concurso));
                    } else {
                        qtdConcursosList.add(numeroTeimosinha + getResources().getString(R.string.espaco_concursos));
                    }
                }
            }
        }
    }

    private void inicializarEstadoQuantidadeNumeros() {
        viewModel.onEvent(new SimulaUiEvent.InicializarQuantidadeNumeros(qtdNumerosList, qtdPrognosticosQuantidadeNumerosList, getQtdDezenasSelecionadaState()));
    }

    private void inicializarEstadoTeimosinhas() {
        List<Integer> qntConcursos = new ArrayList<>();

        if (parametroSimulacao != null && parametroSimulacao.getTeimosinhas() != null) {
            qntConcursos.addAll(parametroSimulacao.getTeimosinhas());
        }

        viewModel.onEvent(new SimulaUiEvent.InicializarTeimosinhas(qtdConcursosList, qntConcursos, getQtdConcursoState(), isEspecial, getString(R.string.label_sem_teimosinha)));
    }

    private void configuraFragments() {

        fragmentManager = getSupportFragmentManager();

        cartelaFragment = new SimulaFragment();

        Bundle bundle = new Bundle();

        cartelaFragment.setArguments(bundle);

        surpresinhaFragment =
                FragmentUtils.getSurpresinhaFragment2();
    }

    private void configuraActivity() {
        switch (tipoJogo) {
            case MEGA_SENA:
                if (isMega30) {
                    configuraTipoApostaTela(SharedPreferencesUtils.getValorString(EspecialUtils.MEGA_DUAS_LINHAS, ""), R.color.mega_verde_claro_mkp, R.color.mega_verde_escuro_mkp, View.VISIBLE, View.GONE);
                } else {
                    configuraTipoApostaTela(isEspecial ? getResources().getString(R.string.underline_mega_virada_underline) : getResources().getString(R.string.unterline_mega_sena_underline), R.color.mega_verde_claro_mkp, R.color.mega_verde_escuro_mkp, View.VISIBLE, View.GONE);
                }
                break;
            case QUINA:
                configuraTipoApostaTela(isEspecial ? getResources().getString(R.string.underline_quina_sao_joao_underline) : getResources().getString(R.string.underline_quina_underline), R.color.quina_claro_mkp, R.color.quina_escuro_mkp, View.VISIBLE, View.GONE);
                break;
            case LOTOFACIL:
                configuraTipoApostaTela(isEspecial ? getResources().getString(R.string.underline_lotofacil_independencia_underline) : getResources().getString(R.string.underline_lotofacil_underline), R.color.lotofacil_claro_mkp, R.color.lotofacil_escuro_mkp, View.VISIBLE, View.GONE);
                break;
            case DUPLA_SENA:
                configuraTipoApostaTela(isEspecial ? getResources().getString(R.string.underline_dupla_pascoa_underline) : getResources().getString(R.string.underline_dupla_sena_underline), R.color.dupla_sena_claro_mkp, R.color.dupla_sena_escuro_mkp, View.VISIBLE, View.GONE);
                break;
            case TIMEMANIA:
                configuraTipoApostaTela(getResources().getString(R.string.underline_timemania_underline), R.color.timemania_claro_mkp, R.color.timemania_escuro_mkp, View.VISIBLE, View.GONE);
                break;
            case LOTOMANIA:
                configuraTipoApostaTela(getResources().getString(R.string.underline_lotomania_underline), R.color.lotomania_claro_mkp, R.color.lotomania_escuro_mkp, View.VISIBLE, View.GONE);
                break;
            case LOTECA:
                configuraTipoApostaTela(getResources().getString(R.string.underline_loteca_underline), R.color.loteca_claro_mkp, R.color.loteca_escuro_mkp, View.GONE, View.VISIBLE);
                break;
            case DIA_DE_SORTE:
                configuraTipoApostaTela(getResources().getString(R.string.underline_dia_de_Sorte_underline), R.color.dia_sorte_claro_mkp, R.color.dia_sorte_escuro_mkp, View.VISIBLE, View.GONE);
                break;
            case SUPER_7:
                configuraTipoApostaTela(getResources().getString(R.string.underline_super_sete_underline), R.color.super_sete_claro_mkp, R.color.super_sete_escuro_mkp, View.VISIBLE, View.GONE);
                break;
            case MAIS_MILIONARIA:
                configuraTipoApostaTela(getResources().getString(R.string.underline_mais_milionaria_underline).toLowerCase(), R.color.milionaria_claro_mkp, R.color.milionaria_escuro_mkp, View.VISIBLE, View.GONE);
                break;
        }

        String dataSorteioAtual = parametroSimulacao.getConcurso().getDataSorteio();

        // Passando um link
        Bundle bundle = new Bundle();
        bundle.putInt(getResources().getString(R.string.extra_qtd_dezenas_possiveis_selecionado), getQtdDezenasSelecionadaState());
        bundle.putString(getResources().getString(R.string.extra_data_sorteio_atual), dataSorteioAtual);
        bundle.putSerializable(getResources().getString(R.string.extra_type_game_color_light), typeGameColorLight);
        bundle.putSerializable(getResources().getString(R.string.extra_type_game_color_Dark), typeGameColorDark);
        bundle.putInt("typeGameColorLight", typeGameColorLight);
        bundle.putInt("typeGameColorDark", typeGameColorDark);
        bundle.putInt(COR_FONTE_FUNDO_BRANCO, estilo.getCorItemDezena());
        bundle.putInt(COR_FONTE_FUNDO_CLARO, estilo.getCorFonteFundoClaro());
        bundle.putInt(COR_FONTE_FUNDO_ESCURO, estilo.getCorFonteFundoEscuro());

        cartelaFragment.cartelaFragmentBuilder(bundle);
        surpresinhaFragment.surpresinhaFragmentBuilder(bundle);

        configuraBotoesListeners();
    }

    public void configuraBotoesListeners() {
        btnTeimosinha.setOnClickListener(
                v -> viewModel.onEvent(new SimulaUiEvent.ClicarTeimosinhas())
        );

        btnQuantidadeNumeros.setOnClickListener(
                v -> viewModel.onEvent(new SimulaUiEvent.ClicarQuantidadeNumeros())
        );

        frameCarrinho.setOnClickListener(
                v -> viewModel.onEvent(new SimulaUiEvent.CarrinhoClicado())
        );

        if (botaoCompletar != null) {
            botaoCompletar.setOnClickListener(
                    v -> {
                        EtapaFragment etapa = getEtapaAtual();
                        if(etapa != null){
                            etapa.onCompletarRodapeClicado();
                        }
                    }
            );
        }

        if (botaoAdicionar != null) {
            botaoAdicionar.setOnClickListener(
                    v -> {
                        EtapaFragment etapa = getEtapaAtual();
                        if(etapa != null){
                            etapa.onAdicionarRodapeClicado();
                        }
                    }
            );
        }

        if (botaoLimparAposta != null) {
            botaoLimparAposta.setOnClickListener(
                    v -> {
                        EtapaFragment etapa = getEtapaAtual();
                        if(etapa != null){
                            etapa.onLimparRodapeClicado();
                        }
                    }
            );
        }

        if (botaoFavoritar != null) {
            botaoFavoritar.setOnClickListener(
                    v -> executarFavoritarRodape()
            );
        }

        if (botaoNextStep != null) {
            botaoNextStep.setOnClickListener(
                    v -> viewModel.onEvent(new SimulaUiEvent.NextStepClicado(tipoJogo))
            );
        }
        if (btnQuantidadeTrevos != null) {
            btnQuantidadeTrevos.setOnClickListener(
                    v -> {
                        SimulaUiState state = viewModel.getUiState().getValue();

                        if (state != null) {
                            viewModel.onEvent(
                                    new SimulaUiEvent.ClicarQuantidadeTrevos(
                                            parametroSimulacao,
                                            state.getQtdDezenasPossiveisSelecionado()
                                    )
                            );
                        }
                    }
            );
        }

        switchSurpresinha.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {

                    if (!buttonView.isPressed()) {
                        return;
                    }

                    viewModel.onEvent(
                            new SimulaUiEvent.AlterarSurpresinha(
                                    isChecked
                            )
                    );
                }
        );    }

    private void abrirDialogQuantidadeNumeros(SimulaUiState state) {
        AppCenterManager.registraEvento(getResources().getString(R.string.evento_entrou_selecao_prognosticos));

        String subtitulo = null;

        if (parametroSimulacao != null && parametroSimulacao.isTipoJogo(ModalidadeEnum.MAIS_MILIONARIA)) {

            subtitulo = getResources().getString(R.string.valores_para_apostas_com_2_trevos);
        }

        DialogUtils.showDialogListItens(
                SimulaActivity.this,
                getResources().getString(R.string.quant_de_numeros),
                subtitulo,
                state.getLabelsQuantidadeNumeros(),
                "Confirmar",
                "Cancelar",
                onQuantidadeNumerosDialogListener()
        );
    }
    private OnDialogListener onQuantidadeTrevosDialogListener() {

        return new OnDialogListener() {

            @Override
            public void itemSelecionado(
                    int position
            ) {

                viewModel.onEvent(
                        new SimulaUiEvent
                                .SelecionarQuantidadeTrevos(
                                position
                        )
                );
            }

            @Override
            public void ok(
                    int position
            ) {

                viewModel.onEvent(
                        new SimulaUiEvent
                                .ConfirmarQuantidadeTrevos(
                                position
                        )
                );
            }

            @Override
            public void cancelar() {

            }
        };
    }
    private OnDialogListener onQuantidadeNumerosDialogListener() {
        return new OnDialogListener() {
            @Override
            public void itemSelecionado(int position) {
                viewModel.onEvent(new SimulaUiEvent.SelecionarQuantidadeNumeros(position));
            }

            @Override
            public void ok(int position) {
                viewModel.onEvent(new SimulaUiEvent.ConfirmarQuantidadeNumeros(position));
            }

            @Override
            public void cancelar() {
            }
        };
    }

    private void atualizarValorApostaPorQuantidadeNumeros(
            SimulaUiState state
    ) {
        textoBotaoPrognosticosSelecionado =
                state.getTextoBotaoPrognosticosSelecionado();

        if (btnQuantidadeNumeros != null) {
            btnQuantidadeNumeros.setText(
                    textoBotaoPrognosticosSelecionado
            );
        }

        viewModel.onEvent(
                new SimulaUiEvent.ValorApostaQuantidadeNumerosAtualizado()
        );
    }

    //TODO//
    private void abrirDialogTeimosinhas(SimulaUiState state) {
        AppCenterManager.registraEvento(getResources().getString(R.string.evento_entrou_popup_teimosinha));

        DialogUtils.showDialogListItensNovo(SimulaActivity.this, getResources().getString(R.string.teimosinhas_maiusculo), null, getString(R.string.teimosinhas_descricao), state.getLabelsTeimosinhas(), "Confirmar", "Cancelar", onTeimosinhaDialogListener());
    }

    private OnDialogListener onTeimosinhaDialogListener() {
        return new OnDialogListener() {
            @Override
            public void itemSelecionado(int position) {
            }

            @Override
            public void ok(int position) {
                viewModel.onEvent(new SimulaUiEvent.ConfirmarTeimosinha(position));
            }

            @Override
            public void cancelar() {
            }
        };
    }

    private void atualizarValorApostaPorTeimosinha() {
        viewModel.onEvent(
                new SimulaUiEvent.ValorApostaAtualizado()
        );
    }

    private void abrirTelaCartela() {
        fragmentManager.beginTransaction().replace(R.id.fragmentSimularApostas, cartelaFragment).commit();
    }

    private void configuraTipoApostaTela(String titleActivity, int lightColor, int darkColor, int visibilityToolBarSimularAposta, int visibilityToolBarLoteca) {
        getWindow().setStatusBarColor(ContextCompat.getColor(this, lightColor));

        toolBarLoteca.setVisibility(visibilityToolBarLoteca);

        typeGameColorLight = lightColor;
        typeGameColorDark = darkColor;
    }

    private void renderCarrinho(SimulaUiState state) {

        if (tvValorCarrinho != null) {

            ViewUtils.setMoedaFormatHtml(state.getValorCarrinho(), tvValorCarrinho);
        }

        if (tvQuantidadeApostas != null) {

            int quantidade = state.getQuantidadeApostasCarrinho();

            tvQuantidadeApostas.setText(quantidade > 999 ? getString(R.string.mais_999) : String.valueOf(quantidade));
        }
    }

    public BarraTituloDTO getBarraTituloDTO() {
        return barraTituloDTO;
    }
    private int getQtdDezenasSelecionadaState() {
        SimulaUiState state =
                viewModel != null
                        ? viewModel.getUiState().getValue()
                        : null;

        return state != null
                ? state.getQtdDezenasPossiveisSelecionado()
                : 0;
    }

    private int getQtdConcursoState() {
        SimulaUiState state =
                viewModel != null
                        ? viewModel.getUiState().getValue()
                        : null;

        return state != null
                ? state.getQtdConcursoSelecionado()
                : 0;
    }
    private EtapaFragment getEtapaAtual() {

        Fragment fragment =
                getSupportFragmentManager()
                        .findFragmentById(
                                R.id.fragmentSimularApostas
                        );

        if (fragment instanceof EtapaFragment) {
            return (EtapaFragment) fragment;
        }

        return null;
    }
    private boolean voltarEtapaComplementar() {

        SimulaUiState state = viewModel.getUiState().getValue();

        if (state == null) {
            return false;
        }

        switch (state.getEtapaAposta()) {

            case TREVOS:
            case MES_SORTE:
            case TIME_CORACAO:

                viewModel.onEvent(
                        new SimulaUiEvent.VoltarEtapa()
                );

                return true;

            default:
                return false;
        }
    }
    private void exibirDialogConfirmacaoSaida() {

        DialogUtils.dialogConfirmar(
                this,
                getString(R.string.text_descartar),
                (dialog, which) -> {

                    setResult(RESULT_CANCELED);

                    finish();
                }
        );
    }
    private boolean possuiApostaEmAndamento() {
        SimulaUiState state =
                viewModel.getUiState().getValue();

        return state != null
                && state.getPossuiAlteracoesPendentes();
    }

    private void resetarTelaAposRetornoDeActivity() {
        zerarApostas();
        inicializarTela();

        etapaRenderizada = null;
        renderizacaoSurpresinhaTime = false;

        abrirTelaCartela();

        viewModel.onEvent(
                new SimulaUiEvent.CarregarCarrinho()
        );
    }
    public void abrirFluxoApostaComResultado(Intent intent) {
        if (intent == null) {
            return;
        }

        fluxoApostaLauncher.launch(intent);
    }
}