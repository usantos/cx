package br.gov.caixa.loterias.apostas.controllers;


import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.InfiniteScrollAdapter;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import br.gov.caixa.loterias.apostas.BuildConfig;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bean.DadosUsuarioToken;
import br.gov.caixa.loterias.apostas.model.bean.Modalidade;
import br.gov.caixa.loterias.apostas.model.bean.SessaoUsuario;
import br.gov.caixa.loterias.apostas.model.bo.AcessoBO_;
import br.gov.caixa.loterias.apostas.model.bo.ApostaBO;
import br.gov.caixa.loterias.apostas.model.bo.DadosCorporativosBO;
import br.gov.caixa.loterias.apostas.model.bo.DadosCorporativosBO_;
import br.gov.caixa.loterias.apostas.model.bo.DadosUsuarioBO;
import br.gov.caixa.loterias.apostas.model.bo.DadosUsuarioBO_;
import br.gov.caixa.loterias.apostas.model.bo.MensagensNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroJogoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroSimulacao;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametrosSimulacaoResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RepassometroDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RepassometroDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RetornoPadraoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.UltimaNotificacaoSingleton;
import br.gov.caixa.loterias.apostas.model.dao.DBLoteriasHelper;
import br.gov.caixa.loterias.apostas.model.sp.CarrinhoSP;
import br.gov.caixa.loterias.apostas.model.sp.LoginSP;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidade;
import br.gov.caixa.loterias.apostas.utils.ExpandableListDataSideMenu;
import br.gov.caixa.loterias.apostas.utils.FirebaseManager;
import br.gov.caixa.loterias.apostas.utils.OnSwipeTouchListener;
import br.gov.caixa.loterias.apostas.utils.RateUtils;
import br.gov.caixa.loterias.apostas.utils.StringUtils;
import br.gov.caixa.loterias.apostas.utils.Utils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.adapter.HomeAdapter;
import br.gov.caixa.loterias.apostas.view.adapter.SideMenuExpandleAdapter;
import br.gov.caixa.loterias.apostas.view.animation.AnimacaoCalculoRepassesSociais;
import br.gov.caixa.loterias.apostas.view.animation.AnimacoesAcessoRapido;
import br.gov.caixa.loterias.apostas.view.custom.LoadingViewLoterias;

@OptionsMenu(R.menu.principal)
@EActivity(R.layout.activity_principal)
public class PrincipalActivity extends AppCompatActivity
        implements DiscreteScrollView.ScrollListener<HomeAdapter.ViewHolder>,
        DiscreteScrollView.OnItemChangedListener<HomeAdapter.ViewHolder> {

    public static final String MENU_LATERAL = "MENU_LATERAL";
    private static final int ZXING_CAMERA_PERMISSION = 1;

    @ViewById
    public AppBarLayout navigationBarListaModalidade;
    @ViewById
    public DiscreteScrollView homeCarousel;
    @ViewById
    protected Toolbar toolbar;
    @ViewById
    protected DrawerLayout drawerLayout;
    @ViewById
    protected Button buttonHomeBet;
    @ViewById
    protected TextView textViewHomeCountBetsCart, textViewHomeMinimumPurchaseValue, tituloLerBilhetes, textoRepassados, valorRepassesPrincipal, txtVersaoMenu, nav_user;
    @ViewById
    protected RelativeLayout relativeContentPrincipal,rlJogoResponsavel,
            relativeBlankView, relativeLayoutContentRapidao,
            relativeLayoutContentLerBilhetes, relativeLayoutContentFavoritas,
            relativeLayoutContentRepassados, relativeLayoutCarrinho;
    @ViewById
    protected LinearLayout atalhoRapidoLoterias;
    @ViewById
    protected ExpandableListView sideMenuExpandableListView;
    @ViewById
    protected NavigationView nav_view;
    @ViewById
    protected ConstraintLayout viewSair;

    @Bean
    protected ApostaBO apostaBO;

    @Bean
    protected DadosUsuarioBO dadosUsuarioBO;

    private List<Modalidade> data;
    private List<ParametroSimulacao> modalidadesParaListar;
    private RepassometroDTO repassesSociais;
    private DBLoteriasHelper dbLoteriasHelper;
    private View actionApresentarTrevo, actionSettings;
    private AlertDialog loadViewProgress;
    private InfiniteScrollAdapter infiniteAdapter;
    private HomeAdapter homeAdapter;
    private View viewNotification;
    private PopupWindow popupNotification;
    private AnimatorSet anim;
    private boolean realizandoLogin = false;
    private boolean recarregarBuscaParam = false;
    private Handler handlerDelayNotificacao;

    @AfterViews
    public void init() {
        setSupportActionBar(toolbar);

        txtVersaoMenu.setText(BuildConfig.VERSION_NAME);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        toolbarListener();
        layoutSideMenu();
        listenerSair();
        callWebservice();
        iniciarBancoLocal();
        RateUtils.avaliarAplicativo(this);
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

        if(!" ".equals(nomeUsuario)) {
            String texto = getResources().getString(R.string.ola_usuario);
            texto = texto.replace(getResources().getString(R.string.percent), nomeUsuario);
            nav_user.setText(ViewUtils.textFuturaAndFuturaBold(this, texto));
        }else {
            nav_user.setText(getResources().getString(R.string.bem_vindo_ao_app));
        }

        if (recarregarBuscaParam) {
            callWebservice();
        }
        layoutSideMenu();
    }

    private void listenerSair() {
        viewSair.setOnClickListener((View v) -> {
            sairLogin(this);
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
    }


    @Override
    protected void onStart() {
        super.onStart();
        textViewHomeCountBetsCart.setText(CarrinhoSP.obterQuantidadeApostas());
        if(realizandoLogin){
            DadosUsuarioBO.updateUsuarioLogado(PrincipalActivity.this, false);
            DadosUsuarioBO.limparRegistros();
            startActivity(new Intent(PrincipalActivity.this, LoginActivity.class));
            finish();
        }
    }

    private void callWebservice() {
        loadViewProgress
                = LoadingViewLoterias.show(this);

        final DadosCorporativosBO dadosCorporativosBO = DadosCorporativosBO_.getInstance_(PrincipalActivity.this);

        dadosCorporativosBO.parametrosSimulacao(new RequestListener<ParametrosSimulacaoResponse>() {
            @Override
            public void onResponse(ParametrosSimulacaoResponse response) {
                loadViewProgress.dismiss();
                FirebaseManager.registraEvento(PrincipalActivity.this ,getResources().getString(R.string.evento_entrou_lista_modalidades));

                modalidadesParaListar = response.getPayload().getParametros();
                if (modalidadesParaListar == null || modalidadesParaListar.isEmpty()) {
                    recarregarBuscaParam = true;
                    Intent appIndis = new Intent(PrincipalActivity.this, AppIndisponivelActivity.class);
                    appIndis.putExtra(getResources().getString(R.string.extra_is_busca_param), true);

                    startActivityForResult(appIndis, 1);
                }

                paramatroSimulacaoSuccess(response);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                recarregarBuscaParam = true;
                loadViewProgress.dismiss();
                Intent appIndis = new Intent(PrincipalActivity.this, AppIndisponivelActivity.class);
                appIndis.putExtra(getResources().getString(R.string.extra_is_busca_param), true);

                startActivityForResult(appIndis, 1);
            }
        });
    }

    private void paramatroSimulacaoSuccess(ParametrosSimulacaoResponse response) {
        SessaoUsuario sessaoUser = SessaoUsuario.getInstance();
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

        textViewHomeMinimumPurchaseValue.setText(String.format(getResources().getString(R.string.valor_minimo_compra), ViewUtils.getMoedaFormat(response.getPayload().getValorMinimoCarrinho())));

        montaListaModalidades();
        montarCarrossel();

        callRepassesSociais();

        if (UltimaNotificacaoSingleton.getInstance().getUltimaNoficacao() != null) {
            trataPopupDeNotificacao();
        }
    }

    private void trataPopupDeNotificacao() {
        LayoutInflater li = LayoutInflater.from(PrincipalActivity.this);
        viewNotification = li.inflate(R.layout.popup_notificacao, null);
        TextView tituloNotificacao = viewNotification.findViewById(R.id.tituloPopup);
        TextView corpoNotificacao = viewNotification.findViewById(R.id.corpoPopup);

        tituloNotificacao.setText(UltimaNotificacaoSingleton.getInstance().getUltimaNoficacao().getTitulo());
        corpoNotificacao.setText(UltimaNotificacaoSingleton.getInstance().getUltimaNoficacao().getConteudo());

        viewNotification.setOnTouchListener(new OnSwipeTouchListener(PrincipalActivity.this){
            @Override
            public void onSwipeTop() {
                anim = (AnimatorSet) AnimatorInflater.loadAnimator(PrincipalActivity.this, R.animator.swipe_top_anim);
                anim.setTarget(viewNotification);
                anim.start();
                removePopup();
            }
            @Override
            public void onSwipeRight() {
                anim = (AnimatorSet) AnimatorInflater.loadAnimator(PrincipalActivity.this, R.animator.swipe_left_anim);
                anim.setTarget(viewNotification);
                anim.start();
                removePopup();
            }
            @Override
            public void onSwipeLeft() {
                anim = (AnimatorSet) AnimatorInflater.loadAnimator(PrincipalActivity.this, R.animator.swipe_right_anim);
                anim.setTarget(viewNotification);
                anim.start();
                removePopup();
            }
        });

        handlerDelayNotificacao = new Handler();
        handlerDelayNotificacao.postDelayed(new Runnable() {
            @Override
            public void run() {
                anim = (AnimatorSet) AnimatorInflater.loadAnimator(PrincipalActivity.this, R.animator.swipe_top_anim);
                anim.setTarget(viewNotification);
                anim.start();
                removePopup();
            }
        }, 10000);

        popupNotification = new PopupWindow(viewNotification, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        popupNotification.showAtLocation(navigationBarListaModalidade, Gravity.TOP, 0, 0);
    }

    private void removePopup() {
        handlerDelayNotificacao.removeCallbacksAndMessages(null);
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                popupNotification.dismiss();
                dadosUsuarioBO.putLerNotificacao(new RequestListener<RetornoPadraoDTO>() {
                    @Override
                    public void onResponse(RetornoPadraoDTO result) {
                        UltimaNotificacaoSingleton.getInstance().setUltimaNoficacao(null);
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        RedirectNetwork.checkRedirect(error, PrincipalActivity.this);
                    }
                });
            }
        });
    }

    private void callRepassesSociais() {
        final DadosCorporativosBO dadosCorporativosBO = DadosCorporativosBO_.getInstance_(PrincipalActivity.this);
        dadosCorporativosBO.repasses(new RequestListener<RepassometroDTOResponse>() {
            @Override
            public void onResponse(RepassometroDTOResponse response) {
                repassesSociais = response.getPayload();
                layoutTrevoAcessoRapido();
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                ViewUtils.alert(PrincipalActivity.this, MensagensNetwork.setMensagem(error, getApplicationContext()));
            }
        });
    }

    private List<Modalidade> montaListaModalidades
            () {
        List<Modalidade> listaModalidades = new ArrayList<>();

        for (ParametroSimulacao parametro : modalidadesParaListar) {
            if (parametro.getParametroJogo().getConcurso().getModalidade() != null) {
                listaModalidades.add(new Modalidade(parametro.getParametroJogo().getConcurso().getModalidadeDetalhada().getDescricao(),
                        parametro.getParametroJogo().getConcurso().getValorApostaMinima(), getResources().getString(R.string.string_vazia), getResources().getString(R.string.string_vazia), parametro.getParametroJogo().getConcurso().getModalidade(),
                        parametro.getParametroJogo().getConcurso(), false, null));
            }
        }

        return listaModalidades;
    }

    private void layoutTrevoAcessoRapido() {
        valorRepassesPrincipal.setText(getResources().getString(R.string.zero));

        tituloLerBilhetes.setText(ViewUtils.textFuturaAndFuturaBold(this, getString(R.string.label_ler_bilhestes_bold)));
        textoRepassados.setText(ViewUtils.textFuturaAndFuturaBold(this, getString(R.string.label_repassados_boas_causas_bold)));
        AnimacaoCalculoRepassesSociais.start(valorRepassesPrincipal, 0, repassesSociais);
    }

    private void toolbarListener() {
        toolbar.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {

                actionApresentarTrevo = toolbar.findViewById(R.id.action_apresentar_trevo);
                if (actionApresentarTrevo != null) {
                    toolbar.removeOnLayoutChangeListener(this);
                    actionApresentarTrevo.setOnClickListener(view -> {
                        if (modalidadesParaListar != null && modalidadesParaListar.size() > 0) {
                            if (relativeContentPrincipal.isEnabled()) {
                                AnimacaoAbrirMenuRapido(actionApresentarTrevo);
                            } else {
                                AnimacaoFecharMenuRapido(actionApresentarTrevo);
                            }
                        }
                    });
                }
                actionSettings = toolbar.findViewById(R.id.action_settings);
                if (actionSettings != null) {
                    toolbar.removeOnLayoutChangeListener(this);
                    actionSettings.setOnClickListener(view -> {
                        Intent activity = new Intent(PrincipalActivity.this, TermosUsoActivity.class);
                        startActivity(activity);
                    });
                }
            }
        });
    }

    private void AnimacaoAbrirMenuRapido(View v) {
        FirebaseManager.registraEvento(PrincipalActivity.this ,getResources().getString(R.string.evento_entrou_menu_trevo));

        relativeContentPrincipal.setEnabled(false);
        actionApresentarTrevo.setEnabled(false);
        atalhoRapidoLoterias.setVisibility(View.VISIBLE);
        EstiloModalidade estilo = new EstiloModalidade(data.get(infiniteAdapter.getRealCurrentPosition()).getTipoModalidade());
        AnimacoesAcessoRapido animacoesAcessoRapido = new AnimacoesAcessoRapido(
                v,
                relativeContentPrincipal,
                relativeLayoutContentRapidao,
                relativeLayoutContentLerBilhetes,
                relativeLayoutContentFavoritas,
                rlJogoResponsavel,
                relativeLayoutContentRepassados,
                relativeBlankView,
                atalhoRapidoLoterias,
                buttonHomeBet,
                actionApresentarTrevo,
                PrincipalActivity.this,
                estilo
        );
        int positionTopRodape = (int) relativeBlankView.getY();

        int itemSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 105, this.getResources().getDisplayMetrics());

        animacoesAcessoRapido.AnimacaoDescerContent(positionTopRodape - itemSize);
    }

    private void AnimacaoFecharMenuRapido(View v) {
        relativeContentPrincipal.setEnabled(true);
        actionApresentarTrevo.setEnabled(false);
        EstiloModalidade estilo = new EstiloModalidade(data.get(infiniteAdapter.getRealCurrentPosition()).getTipoModalidade());
        AnimacoesAcessoRapido animacoesAcessoRapido = new AnimacoesAcessoRapido(
                v,
                relativeContentPrincipal,
                relativeLayoutContentRapidao,
                relativeLayoutContentLerBilhetes,
                relativeLayoutContentFavoritas,
                rlJogoResponsavel,
                relativeLayoutContentRepassados,
                relativeBlankView,
                atalhoRapidoLoterias,
                buttonHomeBet,
                actionApresentarTrevo,
                PrincipalActivity.this,
                estilo
        );
        animacoesAcessoRapido.AnimacaoSubirContent();

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
        if (resultCode == RESULT_OK && requestCode == 1) {
            callWebservice();
        }
    }

    private void sairLogin(Activity activity) {

        ViewUtils.alertTitleButtonYesOrNotListener(this, getResources().getString(R.string.MA008), (dialog, which) -> {
            if (DadosUsuarioBO.checarUsuarioLogado(PrincipalActivity.this)) {
                final AlertDialog loadViewProgress = LoadingViewLoterias.show(PrincipalActivity.this);

                DadosUsuarioBO dadosUsuarioBO = DadosUsuarioBO_.getInstance_(PrincipalActivity.this);
                dadosUsuarioBO.sair(new RequestListener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        FirebaseManager.registraEvento(PrincipalActivity.this ,getResources().getString(R.string.evento_logout_sucesso));

                        AcessoBO_.getInstance_(PrincipalActivity.this).iniciarLogoutSSO(activity);
                        LoginSP.loginRealizado(false);
                        realizandoLogin = true;
                        loadViewProgress.dismiss();
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loadViewProgress.dismiss();
                        RedirectNetwork.checkRedirect(error, PrincipalActivity.this);
                    }
                });
            } else {
                FirebaseManager.registraEvento(PrincipalActivity.this ,getResources().getString(R.string.evento_logout_offline_sucesso));
                startActivity(new Intent(PrincipalActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

    @Override
    public void onScroll(
            float currentPosition,
            int currentIndex, int newIndex,
            @Nullable HomeAdapter.ViewHolder currentHolder,
            @Nullable HomeAdapter.ViewHolder newCurrent) { }

    @Override
    public void onCurrentItemChanged(@Nullable HomeAdapter.ViewHolder viewHolder, int adapterPosition) {
        adapterPosition  = infiniteAdapter.getRealCurrentPosition();
        if (viewHolder != null) {
            EstiloModalidade estilo = new EstiloModalidade(data.get(adapterPosition).getTipoModalidade());
            buttonHomeBet.setBackgroundColor(ContextCompat.getColor(this, estilo.getCorClara()));
            buttonHomeBet.setTag(adapterPosition);
            buttonHomeBet.setText(ViewUtils.textFuturaAndFuturaBold(this, getResources().getString(R.string.aposte_por) +
                    getResources().getString(R.string.espaco_em_branco) +
                    ViewUtils.getMoedaFormat(modalidadesParaListar.get(adapterPosition).getParametroJogo().getConcurso().getValorApostaMinima())));
        }
    }

    @Click(R.id.buttonHomeBet)
    protected void clickButtonHomeBet() {
        int selecionada = Integer.parseInt(buttonHomeBet.getTag().toString());
        ModalidadeEnum modalidade = modalidadesParaListar.get(selecionada).getParametroJogo().getConcurso().getModalidade();
        FirebaseManager.registraEvento(PrincipalActivity.this ,getResources().getString(R.string.evento_entrou_simular_aposta_modalidade) + modalidade);
        ParametroJogoDTO parametroJogo = modalidadesParaListar.get(selecionada).getParametroJogo();

        Intent activity = new Intent(PrincipalActivity.this, SimularApostaActivity_.class);
        activity.putExtra(getString(R.string.extra_tipo_aposta), parametroJogo.getConcurso().getModalidade());
        activity.putExtra(getString(R.string.extra_modalidade), (new Gson()).toJson(parametroJogo));
        activity.putExtra(getString(R.string.extra_especial),parametroJogo.getConcurso().getTipoConcurso().toString().equals(getResources().getString(R.string.especial_maiusculo)));
        startActivity(activity);
    }

    @Click(R.id.relativeLayoutContentRepassados)
    protected void clickRelativeLayoutContentRepassados() {
        AnimacaoFecharMenuRapido(relativeContentPrincipal);
        Intent activity = new Intent(PrincipalActivity.this, RepassesSociaisActivity.class);
        startActivity(activity);
    }

    @Click(R.id.relativeLayoutContentRapidao)
    protected void clickRelativeLayoutContentRapidao() {
        AnimacaoFecharMenuRapido(relativeContentPrincipal);
        redirecionaRapidao();
    }

    @Click(R.id.rlJogoResponsavel)
    protected void clickrlJogoResponsavel() {
        AnimacaoFecharMenuRapido(relativeContentPrincipal);
        redirecionaJogoResponsavel();
    }

    @Click(R.id.relativeLayoutCarrinho)
    protected void clickRelativeLayoutCarrinho() {
        startActivity(new Intent(PrincipalActivity.this, CarrinhoActivity.class));
    }

    @Click(R.id.relativeLayoutContentFavoritas)
    protected void clickRelativeLayoutContentFavoritas() {
        AnimacaoFecharMenuRapido(relativeContentPrincipal);
        startActivity(new Intent(PrincipalActivity.this, FavoritasActivity.class));
    }

    @Click(R.id.relativeLayoutContentLerBilhetes)
    protected void clickRelativeLayoutContentLerBilhetes() {
        if (ContextCompat.checkSelfPermission(PrincipalActivity.this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(PrincipalActivity.this,
                    new String[]{Manifest.permission.CAMERA}, ZXING_CAMERA_PERMISSION);
        } else {
            AnimacaoFecharMenuRapido(relativeContentPrincipal);
            startActivity(new Intent(PrincipalActivity.this, LerBilhetesActivity.class));
        }
    }

    @Click(R.id.apostasConfirmadasBtn)
    protected void clickApostasConfirmadasBtn() {
        MinhasApostasActivity_.intent(PrincipalActivity.this).start();
    }

    private void AbrirMenusGroup(int groupPosition) {
        if (groupPosition != 0) {
            drawerLayout.closeDrawer(GravityCompat.START, false);
        }
        switch (groupPosition) {
            case 1: startActivity(new Intent(PrincipalActivity.this, SelecaoModalidadesResultados.class));
                break;
            case 2: redirecionaRapidao();
                break;

            case 3: if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA}, ZXING_CAMERA_PERMISSION);
            } else {
                startActivity(new Intent(PrincipalActivity.this, LerBilhetesActivity.class));
            }
                break;
            case 4: startActivity(new Intent(PrincipalActivity.this, JogoResponsavelActivity.class));
                break;
            case 5: startActivity(new Intent(PrincipalActivity.this, RepassesSociaisActivity.class));
                break;

            default:
                break;
        }
    }

    private void AbrirMenusItens(int itemPosition) {
        switch (itemPosition) {
            case 0:
                startActivity(new Intent(PrincipalActivity.this, DadosPessoaisActivity.class));
                break;
            /*case 1:
                startActivity(new Intent(PrincipalActivity.this, AlterarDadosLoginCaixaActivity.class));
                break;*/
            case 1:
                startActivity(new Intent(PrincipalActivity.this, MinhasApostasActivity_.class));
                break;
            case 2:
                startActivity(new Intent(PrincipalActivity.this, ListaComprasActivity.class));
                break;
            case 3:
                startActivity(new Intent(PrincipalActivity.this, FavoritasActivity.class));
                break;
            case 4:
                startActivity(new Intent(PrincipalActivity.this, CarrinhosFavoritosActivity.class));
                break;
            case 5:
                startActivity(new Intent(PrincipalActivity.this, CartoesActivity.class).putExtra(MENU_LATERAL, true));
                break;
            default:
                break;
        }
        sideMenuExpandableListView.collapseGroup(0);
        drawerLayout.closeDrawer(Gravity.START, false);
    }

    private void layoutSideMenu() {

        final ExpandableListAdapter expandableListAdapter;
        final List<String> expandableListTitle;
        final HashMap<String, List<String>> expandableListDetail;

        sideMenuExpandableListView.setGroupIndicator(null);
        expandableListDetail = ExpandableListDataSideMenu.getData();
        expandableListTitle = new ArrayList<>(expandableListDetail.keySet());
        expandableListAdapter = new SideMenuExpandleAdapter(this, expandableListTitle, expandableListDetail);
        sideMenuExpandableListView.setAdapter(expandableListAdapter);
        sideMenuExpandableListView.setOnGroupExpandListener(groupPosition ->
                AbrirMenusGroup(groupPosition));

        sideMenuExpandableListView.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {
            AbrirMenusItens(childPosition);
            return false;
        });
    }

    private void redirecionaRapidao(){
        if (DadosUsuarioBO.checarTutorialRapidao()){
            startActivity(new Intent(PrincipalActivity.this, ListaRapidaoActivity.class));
        }else {
            startActivity(new Intent(PrincipalActivity.this, ConfiguracaoRapidaoActivity.class));
        }
    }
    private void redirecionaJogoResponsavel(){
        startActivity(new Intent(PrincipalActivity.this, JogoResponsavelActivity.class));
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case ZXING_CAMERA_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    AnimacaoFecharMenuRapido(relativeContentPrincipal);
                    startActivity(new Intent(PrincipalActivity.this, LerBilhetesActivity.class));
                } else {
                    Toast.makeText(this, R.string.label_conceda_permissao_camera, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void dismissProgressDialog() {
        if (loadViewProgress != null && loadViewProgress.isShowing()) {
            loadViewProgress.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        dbLoteriasHelper.close();
        dismissProgressDialog();
        super.onDestroy();
    }

}