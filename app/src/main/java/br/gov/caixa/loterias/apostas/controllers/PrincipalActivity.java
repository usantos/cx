package br.gov.caixa.loterias.apostas.controllers;

import android.Manifest;
import android.animation.AnimatorSet;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.AccessibilityDelegateCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Lifecycle;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.InfiniteScrollAdapter;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import br.gov.caixa.loterias.apostas.BuildConfig;
import br.gov.caixa.loterias.apostas.LoteriasBaseAppActivity;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.TutorialMaisMilionariaActivity;
import br.gov.caixa.loterias.apostas.model.bean.Modalidade;
import br.gov.caixa.loterias.apostas.model.bean.SessaoUsuario;
import br.gov.caixa.loterias.apostas.model.bo.DadosCorporativosSilceBO;
import br.gov.caixa.loterias.apostas.model.bo.DadosUsuarioBO;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnAnimacaoViewListener;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CombosDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ConcursoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeDisponivelCota;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.NotificacaoResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroJogoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroSimulacao;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametrosSimulacao;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.PushRegistrarDispositivoResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RepassometroDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RepassometroDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RetornoPadraoResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.TipoConcursoEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.CarrinhoSingleton;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.TimerNotificarCotasExpiradasSingleton;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.UltimaNotificacaoSingleton;
import br.gov.caixa.loterias.apostas.model.dao.DBLoteriasHelper;
import br.gov.caixa.loterias.apostas.model.model.CarrinhoModel;
import br.gov.caixa.loterias.apostas.model.model.ComboModel;
import br.gov.caixa.loterias.apostas.model.model.LoginSilceModel;
import br.gov.caixa.loterias.apostas.model.model.ParametrosSimulacaoModel;
import br.gov.caixa.loterias.apostas.novo.features.carrinho.CarrinhoActivity;
import br.gov.caixa.loterias.apostas.novo.features.dadospessoais.DadosPessoaisActivity;
import br.gov.caixa.loterias.apostas.utils.AlertDialogExperimenteLogarSingleton;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.AmbienteEnum;
import br.gov.caixa.loterias.apostas.utils.AnalyticsHelper;
import br.gov.caixa.loterias.apostas.utils.ApostaIdentityKey;
import br.gov.caixa.loterias.apostas.utils.ApostasIdenticasSingleton;
import br.gov.caixa.loterias.apostas.utils.AppCenterManager;
import br.gov.caixa.loterias.apostas.utils.BuildVersionUtil;
import br.gov.caixa.loterias.apostas.utils.ComboUtil;
import br.gov.caixa.loterias.apostas.utils.ConfiguracoesDefaultEnum;
import br.gov.caixa.loterias.apostas.utils.ConfiguracoesEnum;
import br.gov.caixa.loterias.apostas.utils.DialogUtils;
import br.gov.caixa.loterias.apostas.utils.DrawerEnum;
import br.gov.caixa.loterias.apostas.utils.EncerramentoUtil;
import br.gov.caixa.loterias.apostas.utils.EspecialUtils;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidadeMKP;
import br.gov.caixa.loterias.apostas.utils.ExpandableListDataSideMenu;
import br.gov.caixa.loterias.apostas.utils.FragmentUtils;
import br.gov.caixa.loterias.apostas.utils.IntentUtil;
import br.gov.caixa.loterias.apostas.utils.MessagingToken;
import br.gov.caixa.loterias.apostas.utils.NotificacaoUtils;
import br.gov.caixa.loterias.apostas.utils.RateUtils;
import br.gov.caixa.loterias.apostas.utils.SessaoUsuarioUtil;
import br.gov.caixa.loterias.apostas.utils.SharedPreferencesUtils;
import br.gov.caixa.loterias.apostas.utils.StringUtils;
import br.gov.caixa.loterias.apostas.utils.Utils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.utils.helper.AdicionarApostaCarrinhoHelper;
import br.gov.caixa.loterias.apostas.view.activity.BolaoActivity;
import br.gov.caixa.loterias.apostas.view.features.volantenovo.activity.LotecaActivity;
import br.gov.caixa.loterias.apostas.view.features.volantenovo.activity.SimulaActivity;
import br.gov.caixa.loterias.apostas.view.adapter.baseadapter.SideMenuExpandleAdapter;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.HomeAdapter;
import br.gov.caixa.loterias.apostas.view.animation.AnimacaoCalculoRepassesSociais;
import br.gov.caixa.loterias.apostas.view.animation.AnimacoesAcessoRapido;
import br.gov.caixa.loterias.apostas.view.fragment.SomadorCarrinhoFragment;
import br.gov.caixa.loterias.apostas.view.fragment.VerificaMsgPush;
import br.gov.caixa.loterias.apostas.view.holder.HomeViewHolder;
import br.gov.caixa.loterias.apostas.view.listener.OnDialogBotaoListener;
import br.gov.caixa.loterias.apostas.view.listener.OnNotificacaoListener;
import br.gov.caixa.loterias.apostas.utils.shake.ApostaShakePreferences;
import br.gov.caixa.loterias.apostas.utils.shake.ApostaShakeMenuController;
import android.view.MotionEvent;

public class PrincipalActivity extends LoteriasBaseAppActivity
        implements DiscreteScrollView.ScrollListener<HomeViewHolder>,
        DiscreteScrollView.OnItemChangedListener<HomeViewHolder> {

    public static final String MENU_LATERAL = "MENU_LATERAL";
    private static final int ZXING_CAMERA_PERMISSION = 1;
    public static final String ENDERECO_INSTANTANEA = "https://instantanea.caixa.gov.br";
    private static final String TOKEN_DISPOSITIVO = "TOKEN_DISPOSITIVO";

    public AppBarLayout navigationBarListaModalidade;
    public DiscreteScrollView homeCarousel;
    private Toolbar toolbar;
    private ImageView imgBadgeNaoLida;
    private DrawerLayout drawerLayout;
    private MaterialButton buttonHomeBet, buttonHomeBolao;
    private TextView textViewHomeMinimumPurchaseValue, tituloLerBilhetes,
            textoRepassados, valorRepassesPrincipal, txtVersaoMenu, nav_user, txtAmbiente;
    private RelativeLayout relativeContentPrincipal,
            rlJogoResponsavel,
            relativeBlankView,
            relativeLayoutContentRapidao,
            relativeLayoutContentLerBilhetes,
            relativeLayoutContentFavoritas,
            relativeLayoutContentRepassados,
            rlMsgDisque180;
    private LinearLayout atalhoRapidoLoterias;
    private ExpandableListView sideMenuExpandableListView;
    private ConstraintLayout viewSair, atalhoOutubroRosa, containerDescricaoOutubroRosa;
    private ConstraintLayout recolherMenu;

    //fragments
    private SomadorCarrinhoFragment somadorCarrinhoFragment;
    private VerificaMsgPush contadorMsgpush;

    private List<Modalidade> data;
    private List<ParametroSimulacao> modalidadesParaListar;
    private RepassometroDTO repassesSociais;
    private DBLoteriasHelper dbLoteriasHelper;
    private View actionApresentarTrevo, actionSettings, actionCentralNotificacao, actionOutubroRosa;
    private InfiniteScrollAdapter infiniteAdapter;
    private HomeAdapter homeAdapter;
    private View viewNotification;
    private PopupWindow popupNotification;
    private AnimatorSet anim;
    private boolean realizandoLogin = false;
    private Handler handlerDelayNotificacao;
    private boolean precisaBuscarRepasses = true;
    private static int contRegressivo = 1;
    private int anInt;
    private int ano;
    private Boolean expandido = false;
    private static final int REQ_CARRINHO = 1001;
    private static final int REQ_CENTRAL_NOTIFICACAO = 2001;
    private Integer numeroConcurso = null;
    private ModalidadeEnum tipoModalidade = null;
    private TextView textoInformativoCaixa, nomeCaixa;
    private AppCompatImageView arrow, iconeTrevoDrawer;
    private AnimacoesAcessoRapido animacoesAcessoRapido, animacoesOutubroRosa;

    private CarrinhoModel model;
    private static final int REQ_TUTORIAL_SHAKE = 6287;
    private boolean tutorialShakeAberto;
    private boolean tutorialShakeExibido;
    private String tutorialShakeUsuario;
    private ApostaShakeMenuController shakeMenuController;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.principal, menu);
        if (EspecialUtils.isOutubroRosa()){
            menu.findItem(R.id.action_apresentar_trevo).setIcon(AppCompatResources.getDrawable(this, R.drawable.icon_trevo_outubro_rosa));
            menu.findItem(R.id.action_outubro_rosa).setVisible(true);
        }
        return super.onCreateOptionsMenu(menu);
    }

    public void init() {
        //getExtras();
        setViews();

        registrarDispositivo();

        setSupportActionBar(toolbar);

        if (!(BuildConfig.FLAVOR.equals("prd"))) {
            String ambienteSalvo = SharedPreferencesUtils.getValorString("AMBIENTE_SELECIONADO", AmbienteEnum.EXTERNO.name());
            txtAmbiente.setText(ambienteSalvo);
            txtAmbiente.setVisibility(View.VISIBLE);
        }

        txtVersaoMenu.setText(BuildConfig.VERSION_NAME);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        if (EspecialUtils.isOutubroRosa()){
            toggle.getDrawerArrowDrawable().setColor(ContextCompat.getColor(this, R.color.outubro_rosa_secundario));
        }
        drawerLayout.addDrawerListener(toggle);
        shakeMenuController = new ApostaShakeMenuController(this, () -> {
            if (sideMenuExpandableListView.getExpandableListAdapter() instanceof SideMenuExpandleAdapter) {
                ((SideMenuExpandleAdapter) sideMenuExpandableListView.getExpandableListAdapter()).notifyDataSetChanged();
            }
        });
        toggle.syncState();
        toolbarListener();
        layoutSideMenu();
        listenerSair();
        //callWebservice();
        iniciarBancoLocal();
        if(veioDoCarrinho() || veioDoPagamento()) {
            RateUtils.solicitarReview(this);
        }
        SessaoUsuario.getInstance().setPrecisaMontarCarrossel(true);

        if (SessaoUsuario.getInstance().getResponderAutoavaliacao()) {
            startActivity(new Intent(PrincipalActivity.this, AutoavaliacaoStartActivity.class));
        }

        TimerNotificarCotasExpiradasSingleton.getInstance(this).iniciarVerificacao();
        aplicaOutubroRosa();
    }

    private void aplicaOutubroRosa() {
        if (EspecialUtils.isOutubroRosa()){
            viewSair.setBackgroundColor(ContextCompat.getColor(this, R.color.outubro_rosa_secundario));
            ImageViewCompat.setImageTintList(arrow, ColorStateList.valueOf(ContextCompat.getColor(this, R.color.outubro_rosa_secundario)));
            this.nav_user.setTextColor(ContextCompat.getColor(this, R.color.outubro_rosa_secundario));
            textoInformativoCaixa.setTextColor(ContextCompat.getColor(this, R.color.outubro_rosa_secundario));
            nomeCaixa.setTextColor(ContextCompat.getColor(this, R.color.outubro_rosa_secundario));
            iconeTrevoDrawer.setBackground(ContextCompat.getDrawable(this, R.drawable.icon_trevo_rosa));
            ((TextView) findViewById(R.id.paragrafo_1)).setText(ViewUtils.textCaixaSTDBoldTitle(this, getString(R.string.outubro_rosa_paragrafo_1), R.color.outubro_rosa_secundario));
            ((TextView) findViewById(R.id.paragrafo_2)).setText(ViewUtils.textCaixaSTDBoldTitle(this, getString(R.string.outubro_rosa_paragrafo_2), R.color.outubro_rosa_secundario));
            drawerLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.outubro_rosa_secundario));
        }
    }

    private boolean veioDoCarrinho() {
        String origem = getIntent() != null
                ? getIntent().getStringExtra("ORIGEM_TELA")
                : null;

        return "CARRINHO_INC".equals(origem) || "CARRINHO_BOLAO".equals(origem) || "CARRINHO".equals(origem);
    }

    private boolean veioDoPagamento() {
        String origem = getIntent() != null
                ? getIntent().getStringExtra("ORIGEM_TELA")
                : null;

        return "MERCADO".equals(origem);
    }

    private void setViews() {
        this.navigationBarListaModalidade = findViewById(R.id.navigationBarListaModalidade);
        this.homeCarousel = findViewById(R.id.home_carousel);
        this.toolbar = findViewById(R.id.toolbar);
        this.drawerLayout = findViewById(R.id.drawerLayout);
        this.buttonHomeBet = findViewById(R.id.buttonHomeBet);
        this.buttonHomeBolao = findViewById(R.id.buttonHomeBolao);
        this.textViewHomeMinimumPurchaseValue = findViewById(R.id.textViewHomeMinimumPurchaseValue);
        this.tituloLerBilhetes = findViewById(R.id.tituloLerBilhetes);
        this.textoRepassados = findViewById(R.id.textoRepassados);
        this.valorRepassesPrincipal = findViewById(R.id.valorRepassesPrincipal);
        this.txtVersaoMenu = findViewById(R.id.txtVersaoMenu);
        this.nav_user = findViewById(R.id.nav_user);
        this.recolherMenu = findViewById(R.id.layout_rodape);
        this.txtAmbiente = findViewById(R.id.txtAmbiente);
        this.relativeContentPrincipal = findViewById(R.id.relativeContentPrincipal);
        this.rlJogoResponsavel = findViewById(R.id.rlJogoResponsavel);
        this.relativeBlankView = findViewById(R.id.relativeBlankView);
        this.relativeLayoutContentRapidao = findViewById(R.id.relativeLayoutContentRapidao);
        this.relativeLayoutContentLerBilhetes = findViewById(R.id.relativeLayoutContentLerBilhetes);
        this.relativeLayoutContentFavoritas = findViewById(R.id.relativeLayoutContentFavoritas);
        this.relativeLayoutContentRepassados = findViewById(R.id.relativeLayoutContentRepassados);
        this.rlMsgDisque180 = findViewById(R.id.rlMsgDisque180);
        this.containerDescricaoOutubroRosa = findViewById(R.id.container_descricao);
        this.rlMsgDisque180 = findViewById(R.id.rlMsgDisque180);
        this.atalhoRapidoLoterias = findViewById(R.id.atalhoRapidoLoterias);
        this.atalhoOutubroRosa = findViewById(R.id.atalhoRapidoOutubroRosa);
        this.sideMenuExpandableListView = findViewById(R.id.sideMenuExpandableListView);
        this.viewSair = findViewById(R.id.viewSair);
        View view_id_fg_somador_carrinho = findViewById(R.id.id_somador_carrinho);
        this.textoInformativoCaixa = recolherMenu.findViewById(R.id.nav_user_text);
        this.arrow = recolherMenu.findViewById(R.id.imagemMaisOpcoes);
        this.iconeTrevoDrawer = findViewById(R.id.iconTrevo);
        this.nomeCaixa = recolherMenu.findViewById(R.id.nav_user);
        this.nomeCaixa.setHint("Título, Botão, Recolhido");
        this.nomeCaixa.setContentDescription(getString(R.string.cef_id_tit));
        arrow.setRotation(expandido ? 0f : 180f);

        buttonHomeBet.setOnClickListener(view ->
                {
                    if(AdicionarApostaCarrinhoHelper.usuarioEstaSuspenso(PrincipalActivity.this, 2)) return;
                    clickButtonHomeBet();
                }
        );


        buttonHomeBolao.setOnClickListener(view ->
                {
                    if(AdicionarApostaCarrinhoHelper.usuarioEstaSuspenso(PrincipalActivity.this, 2)) return;
                    clickButtonBolao();
                }
        );

        relativeLayoutContentRepassados.setOnClickListener(view -> clickRelativeLayoutContentRepassados());

        if(SharedPreferencesUtils.getValorBoolean(ConfiguracoesEnum.IS_RAPIDAO.get(), ConfiguracoesDefaultEnum.IS_RAPIDAO.asBoolean())){
            relativeLayoutContentRapidao.setOnClickListener(view -> {clickRelativeLayoutContentRapidao();});
        }else{
            relativeLayoutContentRapidao.setVisibility(View.GONE);
            relativeLayoutContentRapidao.setOnClickListener(view -> {});
        }


        rlJogoResponsavel.setOnClickListener(view -> clickrlJogoResponsavel());

        rlMsgDisque180 .setOnClickListener(view -> clickrlMsgDisque180());

        view_id_fg_somador_carrinho.setOnClickListener(view -> vaiProCarrinho());

        relativeLayoutContentFavoritas.setOnClickListener(view -> clickRelativeLayoutContentFavoritas());

        relativeLayoutContentLerBilhetes.setOnClickListener(view -> clickRelativeLayoutContentLerBilhetes());

        recolherMenu.setOnClickListener(v -> {
            expandido = textoInformativoCaixa.getVisibility() == View.VISIBLE;
            String hint = "Título, Botão, " + (expandido ? "Recolhido" : "Expandido");
            nomeCaixa.setHint(hint);
            nomeCaixa.announceForAccessibility(getString(R.string.cef_id_tit) + hint);
            textoInformativoCaixa.setVisibility(expandido ? View.GONE : View.VISIBLE);
            arrow.setRotation(expandido ? 180f : 0f);
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        abrirModalidadeAposteAgora(intent);
    }

    private void abrirModalidadeAposteAgora(Intent intent){
        Bundle extras = intent.getExtras();
        if (extras!= null) {
            if (extras.containsKey(getResources().getString(R.string.extra_modalidade_apostar_agora))) {

                SessaoUsuario.getInstance().setPrecisaMontarCarrossel(true);

                ModalidadeEnum modalidade = (ModalidadeEnum) extras.getSerializable(getResources().getString(R.string.extra_modalidade_apostar_agora));
                int position =  retornaPosicaoPorModalidade(modalidade);
                if(position != -1){
                    int posicaoAlvo = infiniteAdapter.getClosestPosition(position);
                    homeCarousel.smoothScrollToPosition(posicaoAlvo);
                    buttonHomeBet.setTag(position);
                    buttonHomeBet.performClick();
                }
            }
        }
    }

    public int  retornaPosicaoPorModalidade(ModalidadeEnum modalidade){
        if(data != null){
            for (int i = 0; i < data.size(); i++) {
                if (data.get(i).getTipoModalidade().equals(modalidade)) {
                    return i;
                }
            }
        }
        return -1;
    }

    @Override
    protected void onResume() {
        super.onResume();
        String separarNome[] = DadosUsuarioBO.obterNome().toLowerCase(new Locale(getResources().getString(R.string.pt), getResources().getString(R.string.br))).split(getResources().getString(R.string.espaco_em_branco));
        String nomeUsuario = StringUtils.capitalizer(separarNome[0]+getResources().getString(R.string.espaco_em_branco)+separarNome[separarNome.length - 1]);

        AnalyticsHelper.getInstance().logViewScreenAposta(
                AnalyticsHelper.JourneyParams.APOSTAR,
                AnalyticsHelper.SubJourneyParams.SELECIONAR,
                AnalyticsHelper.Tela.HOME,
                tipoModalidade != null ? ModalidadeEnum.fromString(tipoModalidade) : "",
                numeroConcurso != null ? numeroConcurso.toString() : ""
        );

        if(!" ".equals(nomeUsuario)) {
            String texto = getResources().getString(R.string.ola_usuario);
            texto = texto.replace(getResources().getString(R.string.percent), nomeUsuario);
            //nav_user.setText(ViewUtils.textFuturaAndFuturaBold(this, texto));
            nav_user.setText(ViewUtils.textCaixaSTDBold(this, texto));
            nav_user.setContentDescription("Olá, " + nomeUsuario + "!");
            nav_user.setHint("Título");
        }else {
            nav_user.setText(ViewUtils.textCaixaSTDBold(this, getResources().getString(R.string.bem_vindo_ao_app)));
        }
        nomeCaixa.setContentDescription(getString(R.string.cef_id_tit));
        String hint = "Título, Botão, " + (expandido ? "Recolhido" : "Expandido");
        nomeCaixa.setHint(hint);
        TextView botaoSair = findViewById(R.id.txtSairMenu);
        botaoSair.setContentDescription(getString(R.string.title_sair) + ":Botão");

        callWebservice();

        layoutSideMenu();
        fragmentSomadorCarrinho();
        checkRedirecionamento();

        if (ApostasIdenticasSingleton.getInstance().isValidacaoInicialPendente()) {
            // Evita múltiplas chamadas em onResume enquanto a requisição está em andamento
            ApostasIdenticasSingleton.getInstance().setValidacaoInicialPendente(false);

            if (model == null) model = new CarrinhoModel(this);
            model.buscaCarrinhoSilce(new OnSilceListener<CarrinhoDTO>() {
                @Override
                public void success(CarrinhoDTO payload) {
                    if (possuiApostasDuplicadas(payload)) {
                        DialogUtils.dialogEntendi(PrincipalActivity.this, getString(R.string.txt_apostas_identicas));
                    }
                }

                @Override
                public void error(VolleyError error) {
                    RedirectNetwork.checkRedirect(error, PrincipalActivity.this);
                    // Permite tentar novamente em um próximo onResume
                    ApostasIdenticasSingleton.getInstance().setValidacaoInicialPendente(true);
                }
            });
        }
    }

    private boolean possuiApostasDuplicadas(CarrinhoDTO carrinho) {
        if (carrinho == null || carrinho.getApostas() == null) return false;

        Set<String> apostasExistentes = new HashSet<>();

        for (IdentificaoDeUmaApostaDas8Modalidades<?> aposta : carrinho.getApostas()) {
            String chave = ApostaIdentityKey.gerar(aposta);

            if (!chave.isEmpty() && !apostasExistentes.add(chave)) {
                return true;
            }
        }

        return false;
    }

    private void checkRedirecionamento() {
        if (isGoToMinhasApostas()){
            startActivity(new Intent(PrincipalActivity.this, MinhasApostasActivity.class));
        }
    }

    private boolean isGoToMinhasApostas() {
        return getIntent() != null && getIntent().getBooleanExtra(getString(R.string.arg_goto_minhas_apostas), false);
    }

    private void fragmentSomadorCarrinho() {
        if(somadorCarrinhoFragment == null){
            somadorCarrinhoFragment = FragmentUtils.startSomadorCarrinhoFragment(getSupportFragmentManager(), somadorCarrinhoFragment, R.id.id_somador_carrinho,"TELA PRINCIPAL" );
        }else {
            somadorCarrinhoFragment.atualizaValorTotal(CarrinhoSingleton.getInstance().getCarrinho());
        }

    }

    private void listenerSair() {
        viewSair.setOnClickListener((View v) -> {
            sairLogin();
        });
    }

    private void iniciarBancoLocal() {
        dbLoteriasHelper = new DBLoteriasHelper(getApplicationContext());
        dbLoteriasHelper.getReadableDatabase();
    }

    private void montarCarrossel() {
        data = montaListaModalidades();
        homeAdapter = new HomeAdapter(this, this, data, buttonHomeBet);
        infiniteAdapter = InfiniteScrollAdapter.wrap(homeAdapter);
        homeCarousel.setAdapter(infiniteAdapter);
        homeCarousel.addScrollListener(this);
        homeCarousel.addOnItemChangedListener(this);
        homeCarousel.setItemTransformer(new ScaleTransformer.Builder()
                .setMinScale(0.8f)
                .build());

        SessaoUsuario.getInstance().setPrecisaMontarCarrossel(false);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(realizandoLogin){
            DadosUsuarioBO.updateUsuarioLogado(PrincipalActivity.this, false);
            DadosUsuarioBO.limparRegistros();
            startActivity(new Intent(PrincipalActivity.this, LoginActivity.class));
            finish();
        }
    }

    private void callWebservice() {
        AlertDialogUtils.show(PrincipalActivity.this);

        if(SessaoUsuarioUtil.precisaAtualizar()){
            new ParametrosSimulacaoModel(PrincipalActivity.this)
                    .buscaParametroSiumulacao(new OnSilceListener<ParametrosSimulacao>() {
                        @Override
                        public void success(ParametrosSimulacao payload) {
                            AlertDialogUtils.dismiss();
                            AppCenterManager.registraEvento(getResources().getString(R.string.evento_entrou_lista_modalidades));

                            modalidadesParaListar = payload.getParametros();

                            if (modalidadesParaListar == null || modalidadesParaListar.isEmpty()) {
                                Intent appIndis = new Intent(PrincipalActivity.this, AppIndisponivelActivity.class);
                                appIndis.putExtra(getResources().getString(R.string.extra_is_busca_param), true);

                                startActivityForResult(appIndis, 1);
                            }

                            SessaoUsuarioUtil.atualizaParametrosSingleton(payload);
                            paramatroSimulacaoSuccess(true);
                        }

                        @Override
                        public void error(VolleyError error) {
                            AlertDialogUtils.dismiss();
                            Intent appIndis = new Intent(PrincipalActivity.this, AppIndisponivelActivity.class);
                            appIndis.putExtra(getResources().getString(R.string.extra_is_busca_param), true);

                            startActivityForResult(appIndis, 1);
                        }
                    });
        }else if(SessaoUsuario.getInstance().isPrecisaMontarCarrossel()){
            modalidadesParaListar = SessaoUsuario.getInstance().getParametrosSimulacao().getParametros();
            paramatroSimulacaoSuccess(false);
            AlertDialogUtils.dismiss();
        }else {
            AlertDialogUtils.dismiss();
        }

    }

    private void paramatroSimulacaoSuccess(boolean refezParametrosSimulacao) {

        BigDecimal valorMinimoCarrinho = SessaoUsuario.getInstance().getParametrosSimulacao().getValorMinimoCarrinho();
        textViewHomeMinimumPurchaseValue.setText(String.format(getResources().getString(R.string.valor_minimo_compra), ViewUtils.getMoedaFormat(valorMinimoCarrinho)));

        //Não apagar (utilizado pra testes)
        //EncerramentoUtil.mockApagaModalidadeNosParametroJogo(
        //                        ModalidadeEnum.MEGA_SENA, TipoConcursoEnum.ESPECIAL, 5621);
        EncerramentoUtil.verificaSeCriaCardTemporario();

        if (!refezParametrosSimulacao && !ComboUtil.temComboParametroSimulacao()) {
            refezParametrosSimulacao = true;
        }

        if (SharedPreferencesUtils.getValorBoolean(ConfiguracoesEnum.COMBO_APOSTAS.get(), ConfiguracoesDefaultEnum.COMBO_APOSTAS.asBoolean()) && refezParametrosSimulacao) {
            new ComboModel(this).buscaCombos(new OnSilceListener() {
                @Override
                public void success(Object payload) {
                    SessaoUsuario.getInstance().setListCombosDTO((List<CombosDTO>) payload);
                    if (SessaoUsuario.getInstance().getListCombosDTO() != null &&
                        SessaoUsuario.getInstance().getListCombosDTO().size() > 0) {
                        ComboUtil.atualizaCardCombo();
                    } else {
                        ComboUtil.excluiCardCombo();
                    }
                    paramatroSimulacaoPosCombos();
                }

                @Override
                public void error(VolleyError error) {
                    SessaoUsuario.getInstance().setListCombosDTO(null);
                    ComboUtil.excluiCardCombo();
                    paramatroSimulacaoPosCombos();
                }
            });
        } else {
            paramatroSimulacaoPosCombos();
        }

    }

    private void paramatroSimulacaoPosCombos() {
        montarCarrossel();
        trataPopupDeNotificacaoHandler();
        redirectDeepLink();
    }

    private void trataPopupDeNotificacaoHandler() {
        if (UltimaNotificacaoSingleton.getInstance().isPagamentoNaoIdentificado()){
            DialogUtils.dialogLabelUmBotaoListener(
                    PrincipalActivity.this,
                    getString(R.string.alerta_descricao_pagamento_nao_identificado),
                    getString(R.string.acompanhe_suas_compras),
                    (dialog, which) -> startActivity(IntentUtil.getIntentOrigemDestino(PrincipalActivity.this, ListaComprasActivity.class))
            );
        } else if (UltimaNotificacaoSingleton.getInstance().getUltimaNoficacao() != null) {
            if(getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.RESUMED)){
                trataPopupDeNotificacao();
            } else {
//                if(contRegressivo <= 3) {
                    new Handler().postDelayed(() -> {
                        contRegressivo++;
                        trataPopupDeNotificacaoHandler();
                        }, 3000);
//                }
            }
        }
    }

    private void trataPopupDeNotificacao() {
        popupNotification = NotificacaoUtils.showNotificacao(PrincipalActivity.this,
                                                             viewNotification, navigationBarListaModalidade,
                                                             onRemovePopup());
    }

    private OnNotificacaoListener onRemovePopup() {
        return new OnNotificacaoListener() {
            @Override
            public void removePopup() {
                popupNotification.dismiss();
                if (UltimaNotificacaoSingleton.getInstance().getUltimaNoficacao() != null &&
                        UltimaNotificacaoSingleton.getInstance().getUltimaNoficacao().getId() != null){
                    DadosUsuarioBO.getInstance().putLerNotificacao(new RequestListener<RetornoPadraoResponse>() {
                        @Override
                        public void onResponse(RetornoPadraoResponse result) {
                            UltimaNotificacaoSingleton.getInstance().setUltimaNoficacao(null);
                        }

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            RedirectNetwork.checkRedirect(error, PrincipalActivity.this);
                        }
                    });
                }
            }

            @Override
            public void abrirLink() {
                if (UltimaNotificacaoSingleton.getInstance().getUltimaNoficacao() != null
                        && UltimaNotificacaoSingleton.getInstance().getUltimaNoficacao().getLink() != null
                        && !UltimaNotificacaoSingleton.getInstance().getUltimaNoficacao().getLink().isEmpty()){
                    try {
                        startActivity(IntentUtil.getIntentWeb(UltimaNotificacaoSingleton.getInstance().getUltimaNoficacao().getLink()));
                    }catch (Exception e ){
                        Log.d("",e.getLocalizedMessage());
                    }
                }
            }
        };
    }

    private void callRepassesSociais() {
        AlertDialogUtils.show(PrincipalActivity.this);
        DadosCorporativosSilceBO.getInstance().repasses(new RequestListener<RepassometroDTOResponse>() {
            @Override
            public void onResponse(RepassometroDTOResponse response) {
                repassesSociais = response.getPayload();
                layoutTrevoAcessoRapido();
                AlertDialogUtils.dismiss();
                PrincipalActivity.this.animacoesAcessoRapido = createAnimacaoTrevo(actionApresentarTrevo, animacoesAcessoRapido, atalhoRapidoLoterias,getMenuRapidoViewList());
                fechaAnimacaoSePreciso(animacoesOutubroRosa, onAnimacaoFecharAcessoRapido(animacoesAcessoRapido, atalhoRapidoLoterias));
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                findViewById(R.id.relativeLayoutContentRepassados).setVisibility(View.INVISIBLE);
            }
        });
    }

    private OnAnimacaoViewListener onAnimacaoFecharAcessoRapido(AnimacoesAcessoRapido animacoes, View container) {
        return () -> showMenu(animacoes, container);
    }

    private void fechaAnimacaoSePreciso(AnimacoesAcessoRapido animacoes, OnAnimacaoViewListener listener){
        if (animacoes != null && animacoes.isAberto()){
            animacaoFecharMenuRapido(animacoes, listener);
        } else {
            listener.animacaoFinalizada();
        }
    }
    private void showMenu(AnimacoesAcessoRapido animacoes, View containerView) {
        if (relativeContentPrincipal.isEnabled()) {
            if (infiniteAdapter != null){
                AnimacaoAbrirMenuRapido(animacoes, containerView);
                buttonHomeBet.setVisibility(View.GONE);
                buttonHomeBolao.setVisibility(View.GONE);
            }
        } else {
            animacaoFecharMenuRapido(animacoes);
        }
    }

    private void fecharAnimacoesAbertas() {
        if (animacoesAcessoRapido != null && animacoesAcessoRapido.isAberto()){
            animacaoFecharMenuRapido(animacoesAcessoRapido);
        }
        if (animacoesOutubroRosa != null && animacoesOutubroRosa.isAberto()){
            animacaoFecharMenuRapido(animacoesOutubroRosa);
        }
    }

    private boolean temAnimacoesAbertas(){
        return (animacoesAcessoRapido != null && animacoesAcessoRapido.isAberto()) ||
                (animacoesOutubroRosa != null && animacoesOutubroRosa.isAberto());
    }

    private List<Modalidade> montaListaModalidades() {
        List<Modalidade> listaModalidades = new ArrayList<>();
        for (ParametroSimulacao parametro : modalidadesParaListar) {

            if (parametro.getParametroJogo().getConcurso().getModalidade() != null) {
                String descricao = getDescricaoModalidade(parametro);
                listaModalidades.add(new Modalidade(descricao, parametro.getParametroJogo().getConcurso().getValorApostaMinima(),
                        getResources().getString(R.string.string_vazia),
                        getResources().getString(R.string.string_vazia),
                        parametro.getParametroJogo().getConcurso().getModalidade(),
                        parametro.getParametroJogo().getConcurso(), false, null));
            }
        }

        return listaModalidades;
    }

    @NotNull
    private String getDescricaoModalidade(ParametroSimulacao parametro) {
        if (parametro.getParametroJogo().getConcurso().getTipoConcurso().name().contains(TipoConcursoEnum.NORMAL.name())){
            return parametro.getParametroJogo().getConcurso().getModalidadeDetalhada().getDescricao();
        } else {
            return parametro.getParametroJogo().getConcurso().getModalidadeDetalhada().getDescricaoEspecial();
        }
    }

    private void layoutTrevoAcessoRapido() {
        valorRepassesPrincipal.setText(getResources().getString(R.string.zero));

        //tituloLerBilhetes.setText(ViewUtils.textFuturaAndFuturaBold(this, getString(R.string.label_ler_bilhestes_bold)));
        tituloLerBilhetes.setText(ViewUtils.textCaixaSTDBold(this, getString(R.string.label_ler_bilhestes_bold)));
        Date data = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = null;
        try {
            date = dateFormat.parse(repassesSociais.getDataBase());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        ano = calendar.get(Calendar.YEAR);

        //textoRepassados.setText(ViewUtils.textFuturaAndFuturaBold(this,
        textoRepassados.setText(ViewUtils.textCaixaSTDBold(this,
                String.format(getString(R.string.label_repassados_boas_causas_bold).replace("{ano_repassometro}", String.valueOf(ano)))));
        AnimacaoCalculoRepassesSociais.start(valorRepassesPrincipal, 0, repassesSociais);
    }

    private void toolbarListener() {
        toolbar.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {

                actionApresentarTrevo = toolbar.findViewById(R.id.action_apresentar_trevo);
                actionCentralNotificacao = toolbar.findViewById(R.id.central_notificacao);

                if (actionApresentarTrevo != null) {
                    toolbar.removeOnLayoutChangeListener(this);
                    actionApresentarTrevo.setOnClickListener(view -> {
                        PrincipalActivity.this.animacoesAcessoRapido = createAnimacaoTrevo(actionApresentarTrevo, animacoesAcessoRapido, atalhoRapidoLoterias, getMenuRapidoViewList());
                        if (modalidadesParaListar != null && modalidadesParaListar.size() > 0) {
                            if(precisaBuscarRepasses){
                                precisaBuscarRepasses = false;
                                callRepassesSociais();
                            }else {
                                fechaAnimacaoSePreciso(animacoesOutubroRosa, onAnimacaoFecharAcessoRapido(animacoesAcessoRapido, atalhoRapidoLoterias));
                            }
                        }
                    });
                }
                if (actionCentralNotificacao != null) {
                    NotificacoesNaoLidas();
                    actionCentralNotificacao.setOnClickListener(view -> {
                        startActivityForResult(new Intent(PrincipalActivity.this, CentralNotificacaoActivity.class), REQ_CENTRAL_NOTIFICACAO);
                    });
                }

                actionOutubroRosa = toolbar.findViewById(R.id.action_outubro_rosa);

                if (actionOutubroRosa != null) {
                    toolbar.removeOnLayoutChangeListener(this);
                    actionOutubroRosa.setOnClickListener(view -> {
                        PrincipalActivity.this.animacoesOutubroRosa = createAnimacaoTrevo(actionOutubroRosa, animacoesOutubroRosa, atalhoOutubroRosa, getMenuOutubroRosaViewList());
                        fechaAnimacaoSePreciso(animacoesAcessoRapido, onAnimacaoFecharAcessoRapido(animacoesOutubroRosa, atalhoOutubroRosa));
                    });
                }
            }
        });
    }

    private void AnimacaoAbrirMenuRapido(AnimacoesAcessoRapido animacoes, View containerView) {
        if(infiniteAdapter != null){
            relativeContentPrincipal.setEnabled(false);
            actionApresentarTrevo.setEnabled(false);
            containerView.setVisibility(View.VISIBLE);
            int positionTopRodape = (int) relativeBlankView.getY();

            int itemSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 105, this.getResources().getDisplayMetrics());

            animacoes.animacaoDescerContent(positionTopRodape - itemSize);
            animacoes.setAberto(true);
        }
    }

    private OnAnimacaoViewListener onAnimacaoAcessoRapido() {
        return () -> {
            if (SharedPreferencesUtils.getValorBoolean(ConfiguracoesEnum.BOLAO.get(),ConfiguracoesDefaultEnum.BOLAO_HABILITADO.asBoolean()) &&
                    temBolaoModalidadeDisponivel(data.get(infiniteAdapter.getRealCurrentPosition()).getTipoModalidade())){
                buttonHomeBolao.setVisibility(View.VISIBLE);
            }
            buttonHomeBet.setVisibility(View.VISIBLE);
        };
    }

    private void animacaoFecharMenuRapido(AnimacoesAcessoRapido animacoes) {
        relativeContentPrincipal.setEnabled(true);
        actionApresentarTrevo.setEnabled(false);
        animacoes.animacaoSubirContent(onAnimacaoAcessoRapido());
        animacoes.setAberto(false);
    }

    private void animacaoFecharMenuRapido(AnimacoesAcessoRapido animacoes, OnAnimacaoViewListener listener) {
        relativeContentPrincipal.setEnabled(true);
        actionApresentarTrevo.setEnabled(false);
        animacoes.animacaoSubirContent(listener);
        animacoes.setAberto(false);
    }

    @NonNull
    private AnimacoesAcessoRapido createAnimacaoTrevo(View view, AnimacoesAcessoRapido animacoes, View containerView,List<View> viewList) {
        if (animacoes == null){
            return new AnimacoesAcessoRapido(
                    view,
                    relativeContentPrincipal,
                    viewList,
                    relativeBlankView,
                    containerView,
                    buttonHomeBet,
                    actionApresentarTrevo);
        }
        return animacoes;
    }

    private List<View> getMenuRapidoViewList() {
        ArrayList<View> list = new ArrayList<>();
        list.add(relativeLayoutContentLerBilhetes);
        list.add(rlJogoResponsavel);
        list.add(relativeLayoutContentRepassados);
        list.add(rlMsgDisque180);
        return list;
    }

    private List<View> getMenuOutubroRosaViewList() {
        ArrayList<View> list = new ArrayList<>();
        list.add(containerDescricaoOutubroRosa);

        return list;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_TUTORIAL_SHAKE) tutorialShakeAberto = false;
        if (resultCode == RESULT_OK && requestCode == 1) {
            callWebservice();
        }
        if (requestCode == REQ_CARRINHO && resultCode == 2) {
            RateUtils.solicitarReview(this);
        }
        if (requestCode == REQ_CENTRAL_NOTIFICACAO && resultCode == RESULT_OK && data != null) {
            int qtdNaoLidas = data.getIntExtra("QTD_NAO_LIDAS", 0);
            atualizarBadgeNotificacao(qtdNaoLidas);
        }
    }

    private void sairLogin() {
        DialogUtils.dialogSim(
                PrincipalActivity.this,
                getResources().getString(R.string.MA008),
                (dialog, which) -> {

                    if (DadosUsuarioBO.checarUsuarioLogado(PrincipalActivity.this)) {
                        AlertDialogUtils.show(PrincipalActivity.this);

                        new LoginSilceModel(PrincipalActivity.this).sair(new OnSilceListener<NetworkResponse>() {
                            @Override
                            public void success(NetworkResponse payload) {
                                realizandoLogin = true;
                                AlertDialogUtils.dismiss();
                            }

                            @Override
                            public void error(VolleyError error) {
                                AlertDialogUtils.dismiss();
                            }
                        });
                    } else {
                        AppCenterManager.registraEvento(getResources().getString(R.string.evento_logout_offline_sucesso));
                        startActivity(new Intent(PrincipalActivity.this, LoginActivity.class));
                        finish();
                    }
                }
        );
    }

    @Override
    public void onScroll(
            float currentPosition,
            int currentIndex, int newIndex,
            @Nullable HomeViewHolder currentHolder,
            @Nullable HomeViewHolder newCurrent) { }

    @Override
    public void onCurrentItemChanged(@Nullable HomeViewHolder viewHolder, int adapterPosition) {

        if (viewHolder != null) {
            updateHomeButtonsForPosition(adapterPosition);
        }
    }

    private void updateHomeButtonsForPosition(int adapterPosition) {
        adapterPosition  = infiniteAdapter.getRealCurrentPosition();

        if (data != null
                && adapterPosition >= 0
                && adapterPosition < data.size()
                && Boolean.TRUE.equals(data.get(adapterPosition).getResultadosAberto())) {
            if (buttonHomeBet != null)    buttonHomeBet.setVisibility(View.GONE);
            if (buttonHomeBolao != null)  buttonHomeBolao.setVisibility(View.GONE);
            return;
        }

        if (buttonHomeBet != null && !temAnimacoesAbertas()) buttonHomeBet.setVisibility(View.VISIBLE);

        //TODO: MEGA 30 ANOS//
        //TODO: LOTECA PAIS//
        Modalidade item = (data != null && adapterPosition >= 0 && adapterPosition < data.size())
                ? data.get(adapterPosition)
                : null;

        ConcursoDTO concurso = item != null ? item.getConcurso() : null;

        numeroConcurso = concurso != null ? concurso.getNumero() : null;
        tipoModalidade = item != null ? item.getTipoModalidade() : null;

        AnalyticsHelper.getInstance().logViewScreenAposta(
                AnalyticsHelper.JourneyParams.APOSTAR,
                AnalyticsHelper.SubJourneyParams.SELECIONAR,
                AnalyticsHelper.Tela.HOME,
                tipoModalidade != null ? ModalidadeEnum.fromString(tipoModalidade) : "",
                numeroConcurso != null ? numeroConcurso.toString() : ""
        );

        EstiloModalidadeMKP estilo = EstiloModalidadeMKP.createModalidadeConcursoEsp(data.get(adapterPosition).getTipoModalidade(),
                data.get(adapterPosition).getConcurso().getNumero(),
                data.get(adapterPosition).getConcurso().getTipoConcurso());

        int corText = ContextCompat.getColor(this, estilo.getCorFonteFundoEscuro());
        int cor = ContextCompat.getColor(this, estilo.getCorEscura());

        if (data.get(adapterPosition).getTipoModalidade().equals(ModalidadeEnum.TIMEMANIA)) {
            buttonHomeBet.setStrokeColor(ColorStateList.valueOf(corText));
            buttonHomeBet.setTextColor(corText);
        } else {
            buttonHomeBet.setStrokeColor(ColorStateList.valueOf(cor));
            buttonHomeBet.setTextColor(cor);
        }

        buttonHomeBolao.setBackgroundTintList(ColorStateList.valueOf(cor));
        buttonHomeBet.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.branco)));
        buttonHomeBet.setStrokeWidth((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                2,
                getResources().getDisplayMetrics()));

        buttonHomeBolao.setTextColor(corText);

        if (data.get(adapterPosition).getTipoModalidade().equals(ModalidadeEnum.INSTANTANEA) ||
            data.get(adapterPosition).getTipoModalidade().equals(ModalidadeEnum.COMBO)) {
                buttonHomeBet.setTag(adapterPosition);
                buttonHomeBet.setText(ViewUtils.textCaixaSTDBold(this, getResources().getString(R.string.aposte)));
                buttonHomeBolao.setVisibility(View.GONE);
            } else {
                buttonHomeBet.setTag(adapterPosition);
                if (modalidadesParaListar.get(adapterPosition).getParametroJogo().getConcurso().getValorApostaMinima() == null ||
                    modalidadesParaListar.get(adapterPosition).getParametroJogo().getConcurso().getValorApostaMinima().compareTo(BigDecimal.ZERO) == 0) {
                buttonHomeBet.setText(ViewUtils.textCaixaSTDBold(this, getResources().getString(R.string.aposte)));
            } else {
                buttonHomeBet.setText(ViewUtils.textCaixaSTDBold(this, getResources().getString(R.string.aposte_por) +
                        getResources().getString(R.string.espaco_em_branco) +
                        ViewUtils.getMoedaFormat(modalidadesParaListar.get(adapterPosition).getParametroJogo().getConcurso().getValorApostaMinima())));
            }

            if (SharedPreferencesUtils.getValorBoolean(ConfiguracoesEnum.BOLAO.get(), ConfiguracoesDefaultEnum.BOLAO_HABILITADO.asBoolean()) &&
                    temBolaoModalidadeDisponivel(data.get(adapterPosition).getTipoModalidade())) {
                if (EspecialUtils.isParametrosOutubroRosa() && data.get(adapterPosition).getTipoModalidade() == ModalidadeEnum.MEGA_SENA){
                    buttonHomeBolao.setBackground(AppCompatResources.getDrawable(PrincipalActivity.this, R.drawable.btn_rounded_outubro_rosa_outlined));
                    buttonHomeBolao.setTextColor(ContextCompat.getColor(PrincipalActivity.this, R.color.branco));
                } else {
                    buttonHomeBolao.setTextColor(corText);
                }
                buttonHomeBolao.setText(ViewUtils.textCaixaSTDBold(this, getResources()
                        .getString(R.string.apostas_boloes)));
                if (!temAnimacoesAbertas()){
                    buttonHomeBolao.setVisibility(View.VISIBLE);
                }
            } else {
                buttonHomeBolao.setVisibility(View.GONE);
            }
        }
    }

    public void ocultarBotoesHomeParaResultados() {
        if (buttonHomeBet != null) buttonHomeBet.setVisibility(View.GONE);
        if (buttonHomeBolao != null) buttonHomeBolao.setVisibility(View.GONE);
    }

    public void exibirBotoesHomeAposResultados() {
        if (infiniteAdapter != null) {
            int adapterPosition = infiniteAdapter.getRealCurrentPosition();
            updateHomeButtonsForPosition(adapterPosition); // reaplica regras de apresentação
        }
        // Garante que fiquem visíveis após recalcular (se as regras mandarem ocultar o bolão, ele seguirá GONE)
        if (buttonHomeBet != null) buttonHomeBet.setVisibility(View.VISIBLE);
        // buttonHomeBolao é controlado por updateHomeButtonsForPosition
    }


    private boolean temBolaoModalidadeDisponivel(ModalidadeEnum modalidade) {
        List<ModalidadeDisponivelCota> listModalidadesDisponiveis = SessaoUsuario.getInstance().getParametrosSimulacao().getModalidadesDisponiveisCotas();
        if (listModalidadesDisponiveis != null && !listModalidadesDisponiveis.isEmpty()) {
            for (ModalidadeDisponivelCota modalidadesDisponivel : listModalidadesDisponiveis) {
                if (modalidadesDisponivel.getModalidade() == modalidade) {
                    return true;
                }
            }
        }
        return false;
    }

    protected void clickButtonHomeBet() {
        if(buttonHomeBet != null && buttonHomeBet.getTag() != null){
            nextActivity(TutorialMaisMilionariaActivity.class);
        }
    }

    protected void clickButtonBolao() {
        int selecionada = Integer.parseInt(buttonHomeBet.getTag().toString());
        int idModalidade = modalidadesParaListar.get(selecionada).getParametroJogo().getConcurso().getModalidadeDetalhada().getValor();
        TipoConcursoEnum tipoConcurso = modalidadesParaListar.get(selecionada).getParametroJogo().getConcurso().getTipoConcurso();

        if (modalidadesParaListar != null && selecionada >= 0 && selecionada < modalidadesParaListar.size()
                && modalidadesParaListar.get(selecionada) != null
                && modalidadesParaListar.get(selecionada).getParametroJogo() != null
                && modalidadesParaListar.get(selecionada).getParametroJogo().getConcurso() != null
                && modalidadesParaListar.get(selecionada).getParametroJogo().getConcurso().getNumero() != null) {

            AnalyticsHelper.getInstance().logInteraction(
                    AnalyticsHelper.EventCategoryParams.CTA,
                    AnalyticsHelper.EventActionParams.CLICK,
                    AnalyticsHelper.EventLabelParams.BOLAO,
                    AnalyticsHelper.JourneyParams.APOSTAR,
                    AnalyticsHelper.SubJourneyParams.SELECIONAR,
                    AnalyticsHelper.Tela.HOME,
                    ModalidadeEnum.fromString(ModalidadeEnum.fromInteger(idModalidade)),
                    modalidadesParaListar.get(selecionada).getParametroJogo().getConcurso().getNumero().toString()
            );
        }

        if(DadosUsuarioBO.checarUsuarioLogado(this)){
            if (!DadosUsuarioBO.checarTutorial(DadosUsuarioBO.BOLAO_TUTORIAL)){
                startActivity(IntentUtil.getIntentTutorial(this, ModalidadeEnum.BOLAO, idModalidade, tipoConcurso));
            } else {
                Bundle bundle = new Bundle();
                bundle.putInt(BolaoActivity.ARG_IDMODALIDADE, idModalidade);
                bundle.putInt(BolaoActivity.ARG_TIPO_CONCURSO, tipoConcurso.fromStringToIdTipoConcurso());
                Intent intent = IntentUtil.getIntentOrigemDestino(this, BolaoActivity.class, bundle);
                startActivity(intent);
                //startActivity(IntentUtil.getIntentOrigemDestino(this, BolaoActivity.class));
            }
        } else {

            Bundle bolao = new Bundle();
            bolao.putInt(BolaoActivity.ARG_IDMODALIDADE, idModalidade);
            bolao.putInt(BolaoActivity.ARG_TIPO_CONCURSO, tipoConcurso.fromStringToIdTipoConcurso());
            AlertDialogExperimenteLogarSingleton.show(PrincipalActivity.this, false, bolao );
        }
    }

    private void nextActivity(Class clazz) {

        int              selecionada   = Integer.parseInt(buttonHomeBet.getTag().toString());
        ModalidadeEnum   modalidade    = modalidadesParaListar.get(selecionada).getParametroJogo().getConcurso().getModalidade();
        ParametroJogoDTO parametroJogo = modalidadesParaListar.get(selecionada).getParametroJogo();
        //TODO: MEGA 30 ANOS//
        //TODO: LOTECA PAIS//
        int numeroConcurso = parametroJogo.getConcurso().getNumero();

        if (modalidade != null ) {
            AnalyticsHelper.getInstance().logInteraction(
                    AnalyticsHelper.EventCategoryParams.CTA,
                    AnalyticsHelper.EventActionParams.CLICK,
                    AnalyticsHelper.EventLabelParams.SIMPLES,
                    AnalyticsHelper.JourneyParams.APOSTAR,
                    AnalyticsHelper.SubJourneyParams.SELECIONAR,
                    AnalyticsHelper.Tela.HOME,
                    ModalidadeEnum.fromString(modalidade),
                    String.valueOf(numeroConcurso)
            );
        }

        //
        Long tempoExtraRestanteAposEncerrmento = EncerramentoUtil.
                tempoExtraRestanteAposEncerramento(modalidade,
                        modalidadesParaListar.get(selecionada).getParametroJogo().getConcurso().getTipoConcurso(),
                        modalidadesParaListar.get(selecionada).getParametroJogo().getConcurso().getNumero());
        if (tempoExtraRestanteAposEncerrmento != null) {
             DialogUtils.dialogEntendi(
                     PrincipalActivity.this,
                     getString(R.string.ainda_da_tempo,
                             modalidadesParaListar.get(selecionada).getParametroJogo().getConcurso().getNumero())

             );
            return;
       }

        if (modalidade == ModalidadeEnum.INSTANTANEA) {
            AppCenterManager.registraEvento(getResources().getString(R.string.evento_entrou_instantanea));
            Uri uri = Uri.parse(ENDERECO_INSTANTANEA);
            Intent activity = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(activity);
        } else if (modalidade == ModalidadeEnum.COMBO) {
            if (DadosUsuarioBO.checarUsuarioLogado(this)) {
                AppCenterManager.registraEvento(getResources().getString(R.string.evento_entrou_combo));
                Intent activity = new Intent(PrincipalActivity.this, CombosActivity.class);
                startActivity(activity);
            } else {
                AlertDialogExperimenteLogarSingleton.show(PrincipalActivity.this, false, null);
            }
        }
//        else if (modalidade == ModalidadeEnum.MAIS_MILIONARIA && !DadosUsuarioBO.checarTutorial(DadosUsuarioBO.MAIS_MILIONARIA_TUTORIAL)){
//            AppCenterManager.registraEvento(getResources().getString(R.string.evento_entrou_tutorial_mais_milionaria));
//            Intent activity = new Intent(PrincipalActivity.this, clazz);
//            activity.putExtra(getString(R.string.extra_tipo_aposta), parametroJogo.getConcurso().getModalidade());
//            activity.putExtra(getString(R.string.extra_modalidade), (new Gson()).toJson(parametroJogo));
//            activity.putExtra(getString(R.string.extra_especial),parametroJogo.getConcurso().getTipoConcurso().toString().equals(getResources().getString(R.string.especial_maiusculo)));
//            startActivity(activity);}
//        else if (modalidade == ModalidadeEnum.LOTECA) {
//            Intent activity = new Intent(PrincipalActivity.this, SimularApostaActivity.class);
//            activity.putExtra(getString(R.string.extra_tipo_aposta), parametroJogo.getConcurso().getModalidade());
//            activity.putExtra(getString(R.string.extra_modalidade), (new Gson()).toJson(parametroJogo));
//            //TODO: MEGA 30 ANOS//
//            //TODO: LOTECA PAIS//
//            activity.putExtra(getString(R.string.numero_concurso), numeroConcurso);
//            activity.putExtra(getString(R.string.extra_especial),parametroJogo.getConcurso().getTipoConcurso().toString().equals(getResources().getString(R.string.especial_maiusculo)));
//            startActivity(activity);
//        }
        else if(modalidade == ModalidadeEnum.LOTECA) {
            AppCenterManager.registraEvento(getResources().getString(R.string.evento_entrou_simular_aposta_modalidade) + modalidade);

            Intent activity = new Intent(PrincipalActivity.this, LotecaActivity.class);
            activity.putExtra(getString(R.string.extra_tipo_aposta), parametroJogo.getConcurso().getModalidade());
            activity.putExtra(getString(R.string.extra_modalidade), (new Gson()).toJson(parametroJogo));
            //TODO: MEGA 30 ANOS//
            activity.putExtra(getString(R.string.numero_concurso), numeroConcurso);
            activity.putExtra(getString(R.string.extra_especial),parametroJogo.getConcurso().getTipoConcurso().toString().equals(getResources().getString(R.string.especial_maiusculo)));
            startActivity(activity);
        }
        else {
            AppCenterManager.registraEvento(getResources().getString(R.string.evento_entrou_simular_aposta_modalidade) + modalidade);

            Intent activity = new Intent(PrincipalActivity.this, SimulaActivity.class);
            activity.putExtra(getString(R.string.extra_tipo_aposta), parametroJogo.getConcurso().getModalidade());
            activity.putExtra(getString(R.string.extra_modalidade), (new Gson()).toJson(parametroJogo));
            //TODO: MEGA 30 ANOS//
            activity.putExtra(getString(R.string.numero_concurso), numeroConcurso);
            activity.putExtra(getString(R.string.extra_especial),parametroJogo.getConcurso().getTipoConcurso().toString().equals(getResources().getString(R.string.especial_maiusculo)));
            startActivity(activity);
        }
    }

    protected void clickRelativeLayoutContentRepassados() {
        fecharAnimacoesAbertas();
        Intent activity = new Intent(PrincipalActivity.this, RepassesSociaisActivity.class);
        startActivity(activity);
    }

    protected void clickRelativeLayoutContentRapidao() {
        fecharAnimacoesAbertas();
        if(SharedPreferencesUtils.getValorBoolean(ConfiguracoesEnum.IS_RAPIDAO.get(), ConfiguracoesDefaultEnum.IS_RAPIDAO.asBoolean())){
            redirecionaRapidao();
        }
    }

    protected void clickrlJogoResponsavel() {
        fecharAnimacoesAbertas();
        redirecionaJogoResponsavel();
    }

    protected void clickrlMsgDisque180() {
        fecharAnimacoesAbertas();
        redirecionaMsgDisque180();
    }

    protected void vaiProCarrinho() {
        fecharAnimacoesAbertas();
        if (somadorCarrinhoFragment != null && somadorCarrinhoFragment.isVisible()) {
            startActivityForResult(
                    new Intent(PrincipalActivity.this, CarrinhoActivity.class),
                    REQ_CARRINHO
            );
        }
    }

    protected void clickRelativeLayoutContentFavoritas() {
        fecharAnimacoesAbertas();
        startActivity(new Intent(PrincipalActivity.this, FavoritasActivity.class));
    }

    protected void clickRelativeLayoutContentLerBilhetes() {
        fecharAnimacoesAbertas();
        if (ContextCompat.checkSelfPermission(PrincipalActivity.this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(PrincipalActivity.this,
                    new String[]{Manifest.permission.CAMERA}, ZXING_CAMERA_PERMISSION);
        } else {
            startActivity(new Intent(PrincipalActivity.this, LerBilhetesActivity.class));
        }
    }

    private void AbrirMenusGroup(DrawerEnum item) {
        if (item != DrawerEnum.MENU_MINHA_AREA && item != DrawerEnum.MENU_JOGO_RESPONSAVEL && item != DrawerEnum.MENU_SOBRE_CAIXA) {
            drawerLayout.closeDrawer(GravityCompat.START, false);
        }
        switch (item) {
            case MENU_RESULTADOS:
                fecharAnimacoesAbertas();
                startActivity(new Intent(PrincipalActivity.this, SelecaoModalidadesResultados.class));
                break;
            case MENU_RAPIDAO:
                fecharAnimacoesAbertas();
                if(SharedPreferencesUtils.getValorBoolean(ConfiguracoesEnum.IS_RAPIDAO.get(), ConfiguracoesDefaultEnum.IS_RAPIDAO.asBoolean())){
                    redirecionaRapidao();
                }
                break;
            case MENU_COFERIR_BILHETE:
                fecharAnimacoesAbertas();
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA}, ZXING_CAMERA_PERMISSION);
                } else {
                    startActivity(new Intent(PrincipalActivity.this, LerBilhetesActivity.class));
                }
                break;
            case MENU_REPASSES_SOCIAIS:
                fecharAnimacoesAbertas();
                startActivity(new Intent(PrincipalActivity.this, RepassesSociaisActivity.class));
                break;
            case MENU_TERMO_USO:
                fecharAnimacoesAbertas();
                startActivity(new Intent(PrincipalActivity.this, TermosAceiteActivity.class));
                break;
            case MENU_DUVIDAS:
                fecharAnimacoesAbertas();
                startActivity(new Intent(PrincipalActivity.this, DuvidasActivity.class));
                break;
            default:
                break;
        }
    }

    private void AbrirMenusItens(DrawerEnum drawerEnum, int itemPosition) {
        tratarMenuMinhaArea(drawerEnum);

        sideMenuExpandableListView.collapseGroup(itemPosition);
        drawerLayout.closeDrawer(GravityCompat.START, false);
    }

    private void tratarMenuMinhaArea(DrawerEnum item) {
        switch (item) {
            case MENU_DADOS_PESSOAIS:
                fecharAnimacoesAbertas();
                startActivity(new Intent(PrincipalActivity.this, DadosPessoaisActivity.class));
                break;
            case MENU_MINHAS_APOSTAS:
                fecharAnimacoesAbertas();
                startActivity(new Intent(PrincipalActivity.this, MinhasApostasActivity.class));
                break;
            case MENU_COMPRAS:
                fecharAnimacoesAbertas();
                startActivity(new Intent(PrincipalActivity.this, ListaComprasActivity.class));
                break;
            case MENU_FAVORITAS:
                fecharAnimacoesAbertas();
                startActivity(new Intent(PrincipalActivity.this, FavoritasActivity.class));
                break;
            case MENU_CARRINHOS_FAVORITOS:
                fecharAnimacoesAbertas();
                startActivity(new Intent(PrincipalActivity.this, CarrinhosFavoritosActivity.class));
                break;
            case MENU_MEUS_CARTOES:
                fecharAnimacoesAbertas();
                startActivity(new Intent(PrincipalActivity.this, MeusCartoesActivity.class));
                break;
            default:
                break;
        }
    }

    private void trataMenuJogoResponsavel(DrawerEnum item, int itemPosition) {
        switch (item) {
            case MENU_JOGO_RESPONSAVEL_POLITICA:
                fecharAnimacoesAbertas();
                DialogUtils.dialogTituloConfirmar(
                        PrincipalActivity.this,
                        getString(R.string.jogo_responsavel),
                        getString(R.string.voce_sera_redirecionado_para_o_site_jogo_responsavel),
                        (dialog, which) -> Utils.abreUrl(PrincipalActivity.this, R.string.url_jogo_responsavel)
                );
                break;
            case MENU_JOGO_RESPONSAVEL_AVALIACAO:
                fecharAnimacoesAbertas();
                startActivity(new Intent(PrincipalActivity.this, AutoavaliacaoStartActivity.class));
                break;
            case MENU_JOGO_RESPONSAVEL_SUSPENSAO:
                fecharAnimacoesAbertas();
                startActivity(new Intent(PrincipalActivity.this, AutoSuspensaoActivity.class));
                break;
            default:
                break;
        }

        sideMenuExpandableListView.collapseGroup(itemPosition);
        drawerLayout.closeDrawer(GravityCompat.START, false);
    }

    private void layoutSideMenu() {
        final SideMenuExpandleAdapter expandableListAdapter;
        final List<DrawerEnum> expandableListTitle;
        final HashMap<DrawerEnum, List<DrawerEnum>> expandableListDetail;

        sideMenuExpandableListView.setGroupIndicator(null);
        expandableListDetail = ExpandableListDataSideMenu.getData();
        expandableListTitle = new ArrayList<>(expandableListDetail.keySet());
        expandableListAdapter = new SideMenuExpandleAdapter(this, expandableListTitle, expandableListDetail);
        expandableListAdapter.setShakeController(shakeMenuController);
        sideMenuExpandableListView.setAdapter(expandableListAdapter);
        sideMenuExpandableListView.setOnGroupExpandListener(groupPosition -> AbrirMenusGroup(expandableListTitle.get(groupPosition)));
        sideMenuExpandableListView.setOnGroupClickListener((expandableListView, view, i, l) -> {
            if (expandableListTitle.get(i) == DrawerEnum.MENU_SOBRE_CAIXA) {
//                    ViewUtils.abrirMenuCaixa(PrincipalActivity.this);
            }
            return false;
        });

        sideMenuExpandableListView.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {
            if (expandableListTitle.get(groupPosition) == DrawerEnum.MENU_JOGO_RESPONSAVEL) {
                trataMenuJogoResponsavel(expandableListDetail.get(DrawerEnum.MENU_JOGO_RESPONSAVEL).get(childPosition), childPosition);
            } else if (expandableListTitle.get(groupPosition) == DrawerEnum.MENU_MINHA_AREA){
                AbrirMenusItens(expandableListDetail.get(DrawerEnum.MENU_MINHA_AREA).get(childPosition), childPosition);
            }
            return false;
        });

    }

    @Override protected void onPostResume() {
        super.onPostResume();
        String usuarioAtual = DadosUsuarioBO.obterCpf();
        if (!usuarioAtual.equals(tutorialShakeUsuario)) {
            tutorialShakeUsuario = usuarioAtual;
            tutorialShakeAberto = false;
            tutorialShakeExibido = false;
        }
        if (!isFinishing() && !ApostaShakePreferences.isTutorialConcluido()
                && !tutorialShakeAberto && !tutorialShakeExibido) {
            tutorialShakeAberto = true;
            tutorialShakeExibido = true;
            startActivityForResult(new Intent(this, TutorialApostaShakeActivity.class), REQ_TUTORIAL_SHAKE);
        } else if (!isFinishing() && ApostaShakePreferences.isTooltipPendente()) {
            abrirMenuComTooltipShake();
        }
    }

    private void abrirMenuComTooltipShake() {
        drawerLayout.openDrawer(GravityCompat.START, false);
        android.widget.ExpandableListAdapter adapter = sideMenuExpandableListView.getExpandableListAdapter();
        for (int i = 0; i < adapter.getGroupCount(); i++) {
            if (adapter.getGroup(i) == DrawerEnum.MENU_APOSTA_SHAKE) {
                sideMenuExpandableListView.setSelectedGroup(i);
                break;
            }
        }
        // Aguarda o layout do menu para ancorar o aviso no item visível.
        sideMenuExpandableListView.getViewTreeObserver().addOnPreDrawListener(
                new android.view.ViewTreeObserver.OnPreDrawListener() {
                    @Override public boolean onPreDraw() {
                        sideMenuExpandableListView.getViewTreeObserver().removeOnPreDrawListener(this);
                        View anchor = sideMenuExpandableListView.findViewById(R.id.shakeMenuRow);
                        if (ApostaShakePreferences.isTooltipPendente()
                                && shakeMenuController.mostrarTooltip(anchor, true)) {
                            ApostaShakePreferences.consumirTooltip();
                        }
                        return true;
                    }
                });
    }

    @Override public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN && shakeMenuController != null) shakeMenuController.aoTocarTela();
        return super.dispatchTouchEvent(event);
    }

    private void redirecionaRapidao() {
        if (!AdicionarApostaCarrinhoHelper.usuarioEstaSuspenso(this,2)) {
            fecharAnimacoesAbertas();
            if (DadosUsuarioBO.checarTutorialRapidao()) {
                startActivity(new Intent(PrincipalActivity.this, ListaRapidaoActivity.class));
            } else {
                startActivity(new Intent(PrincipalActivity.this, ConfiguracaoRapidaoActivity.class));
            }
        } else {
            fecharAnimacoesAbertas();
        }
    }

    private void redirecionaJogoResponsavel(){
        //startActivity(new Intent(PrincipalActivity.this, JogoResponsavelActivity.class));
        DialogUtils.dialogTituloConfirmar(
                PrincipalActivity.this,
                getString(R.string.jogo_responsavel),
                getString(R.string.voce_sera_redirecionado_para_o_site_jogo_responsavel),
                new OnDialogBotaoListener() {
                    @Override
                    public void onButtonClick(DialogInterface dialog, int which) {
                        Utils.abreUrl(PrincipalActivity.this, R.string.url_jogo_responsavel);
                    }
                }

        );
    }
    private void redirecionaMsgDisque180(){
        boolean installed = isAppInstalled("com.whatsapp");

        if (installed) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("http://api.whatsapp.com/send?phone=5561996100180&text=Disque180"));
            startActivity(intent);
        }
        else
        {
            //mensagem negativa de Whatsapp
            showDialogNoWhatsapp();
        }

    }
   // private AlertDialog myDialog;
    private void showDialogNoWhatsapp() {
        DialogUtils.dialogEntendi(
                PrincipalActivity.this,
                getResources().getString(R.string.no_whatsapp)
        );
    }
    private boolean isAppInstalled(String s) {
        PackageManager packageManager = getPackageManager();
        boolean is_installed;

        try {
            packageManager.getPackageInfo(s, PackageManager.GET_ACTIVITIES);
            is_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            is_installed = false;
        }
        return is_installed;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case ZXING_CAMERA_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fecharAnimacoesAbertas();
                    startActivity(new Intent(PrincipalActivity.this, LerBilhetesActivity.class));
                } else {
                    Toast.makeText(this, R.string.label_conceda_permissao_camera, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        dbLoteriasHelper.close();
        AlertDialogUtils.dismiss();
        //MessagingUtils.limpaNotificacoesDoApp(this);
        super.onDestroy();
    }

    private boolean redirectDeepLink() {
        Intent appLinkIntent = getIntent();
        String appLinkAction = appLinkIntent.getAction();
        Uri appLinkData = appLinkIntent.getData();
        if (appLinkData != null && appLinkData.toString().length() >0) {
            Intent intent = IntentUtil.getIntentDeepLink(this, appLinkData.toString());
            if (intent != null) {
                startActivity(intent);
                return true;
            }
        }
        return false;
    }

    private void configurarAcessibilidadeCarrossel(){
        ViewCompat.setAccessibilityDelegate(homeCarousel, new AccessibilityDelegateCompat(){
            @Override
            public void onInitializeAccessibilityNodeInfo(@NotNull View host, @NonNull AccessibilityNodeInfoCompat info) {
                super.onInitializeAccessibilityNodeInfo(host, info);

                info.setCollectionInfo(null);
            }
        });
    }

    private void registrarDispositivo() {
        String cpf = DadosUsuarioBO.obterCpf();
        MessagingToken messagingToken = new MessagingToken();
        messagingToken.tokenFirebase(new MessagingToken.OnTokenListener() {
            @Override
            public void onSucesso(String tokenFirebase) {

                if ((cpf != null && cpf.length() > 0) &&
                     !messagingToken.isTokenEnviado(cpf, tokenFirebase)) {
                    DadosUsuarioBO.getInstance().postPushRegistrarDispositivo(cpf, tokenFirebase, new RequestListener<PushRegistrarDispositivoResponse>() {
                        @Override
                        public void onResponse(PushRegistrarDispositivoResponse response) {
                            if (response.getPayload().getCodigo().equals("200")) {
                                if (!BuildVersionUtil.isPRD()) {
                                    DialogUtils.dialogEntendi(
                                            PrincipalActivity.this,
                                            "GRAVOU O TOKEN DO DISPOSITIVO COM SUCESSO\n" + tokenFirebase
                                    );
                                }
                                messagingToken.setTokenEnviado(cpf, tokenFirebase);
                            }
                        }
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            if (!BuildVersionUtil.isPRD()) {
                                DialogUtils.dialogEntendi(
                                        PrincipalActivity.this,
                                        "ERRO GRAVANDO TOKEN DO DISPOSITIVO:\n" + tokenFirebase
                                );
                            }
                        }
                    });
                }
            }

            @Override
            public void onErro(Exception e) {
            }
        });
    }

    private void atualizarBadgeNotificacao(int qtdNaoLidas) {
        imgBadgeNaoLida = findViewById(R.id.imgBadgeNaoLida);
        if (imgBadgeNaoLida == null) {
            return;
        }
        imgBadgeNaoLida.setVisibility(qtdNaoLidas > 0 ? View.VISIBLE : View.GONE);
    }

    private void NotificacoesNaoLidas() {
        DadosUsuarioBO.getInstance().getHistoricoNotificacaoNaoLidas(new RequestListener<NotificacaoResponse>() {
            @Override
            public void onResponse(NotificacaoResponse response) {
                if (response != null && response.getPayload() != null) {
                    atualizarBadgeNotificacao(response.getPayload().size());
                } else {
                    atualizarBadgeNotificacao(0);
                }
            }
            @Override
            public void onErrorResponse(VolleyError error) {
                atualizarBadgeNotificacao(0);
            }
        });

    }


}
