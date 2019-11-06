package br.gov.caixa.loterias.apostar.interno.controllers;


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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import br.gov.caixa.loterias.apostar.interno.BuildConfig;
import br.gov.caixa.loterias.apostar.interno.R;
import br.gov.caixa.loterias.apostar.interno.model.bean.DadosUsuarioToken;
import br.gov.caixa.loterias.apostar.interno.model.bean.Modalidade;
import br.gov.caixa.loterias.apostar.interno.model.bean.SessaoUsuario;
import br.gov.caixa.loterias.apostar.interno.model.bo.AcessoBO_;
import br.gov.caixa.loterias.apostar.interno.model.bo.ApostaBO;
import br.gov.caixa.loterias.apostar.interno.model.bo.DadosCorporativosBO;
import br.gov.caixa.loterias.apostar.interno.model.bo.DadosCorporativosBO_;
import br.gov.caixa.loterias.apostar.interno.model.bo.DadosUsuarioBO;
import br.gov.caixa.loterias.apostar.interno.model.bo.DadosUsuarioBO_;
import br.gov.caixa.loterias.apostar.interno.model.bo.MensagensNetwork;
import br.gov.caixa.loterias.apostar.interno.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostar.interno.model.bo.RequestListener;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ParametroSimulacao;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ParametrosSimulacaoResponse;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.RepassometroDTO;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.RepassometroDTOResponse;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.RetornoPadraoDTO;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.internals.UltimaNotificacaoSingleton;
import br.gov.caixa.loterias.apostar.interno.model.dao.DBLoteriasHelper;
import br.gov.caixa.loterias.apostar.interno.model.sp.CarrinhoSP;
import br.gov.caixa.loterias.apostar.interno.model.sp.LoginSP;
import br.gov.caixa.loterias.apostar.interno.utils.EstiloModalidade;
import br.gov.caixa.loterias.apostar.interno.utils.ExpandableListDataSideMenu;
import br.gov.caixa.loterias.apostar.interno.utils.FirebaseManager;
import br.gov.caixa.loterias.apostar.interno.utils.OnSwipeTouchListener;
import br.gov.caixa.loterias.apostar.interno.utils.RateUtils;
import br.gov.caixa.loterias.apostar.interno.utils.StringUtils;
import br.gov.caixa.loterias.apostar.interno.utils.Utils;
import br.gov.caixa.loterias.apostar.interno.utils.ViewUtils;
import br.gov.caixa.loterias.apostar.interno.view.adapter.HomeAdapter;
import br.gov.caixa.loterias.apostar.interno.view.adapter.SideMenuExpandleAdapter;
import br.gov.caixa.loterias.apostar.interno.view.animation.AnimacaoCalculoRepassesSociais;
import br.gov.caixa.loterias.apostar.interno.view.animation.AnimacoesAcessoRapido;
import br.gov.caixa.loterias.apostar.interno.view.custom.LoadingViewLoterias;

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
    protected RelativeLayout relativeContentPrincipal,
            relativeBlankView, relativeLayoutContentRapidao, relativeLayoutContentLerBilhetes, relativeLayoutContentFavoritas, relativeLayoutContentRepassados,
            relativeLayoutCarrinho;
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
            if (extras.containsKey("MODALIDADE_APOSTAR_AGORA")) {
                ModalidadeEnum modalidade = (ModalidadeEnum) extras.getSerializable("MODALIDADE_APOSTAR_AGORA");
                int position =  retornaPosicaoPorModalidade(modalidade);
                EstiloModalidade estilo;
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
        for (int i = 0; i < data.size(); i++) {
            if (data !=null && data.get(i).getTipoModalidade().equals(modalidade)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    protected void onResume() {
        super.onResume();
        String separarNome[] = DadosUsuarioBO.obterNome().toLowerCase(new Locale("pt", "BR")).split(" ");
        String nomeUsuario = StringUtils.capitalizer(separarNome[0]+" "+separarNome[separarNome.length - 1]);

        if(!" ".equals(nomeUsuario)) {
            String texto = "Olá, _%_!";
            texto = texto.replace("%", nomeUsuario);
            nav_user.setText(ViewUtils.textFuturaAndFuturaBold(this, texto));
        }else {
            nav_user.setText("Olá, seja bem-vindo ao\n App Loterias.");
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
                FirebaseManager.registraEvento(PrincipalActivity.this ,"ENTROU_LISTA_MODALIDADES");

                if(response.getPayload().getValorLimiteDiario() == null){
                    response.getPayload().setValorLimiteDiario(BigDecimal.ZERO);
                }

                modalidadesParaListar = response.getPayload().getParametros();
                if (modalidadesParaListar == null || modalidadesParaListar.isEmpty()) {
                    recarregarBuscaParam = true;
                    Intent appIndis = new Intent(PrincipalActivity.this, AppIndisponivelActivity.class);
                    appIndis.putExtra("IS_BUSCA_PARAM", true);

                    startActivityForResult(appIndis, 1);
                }

                paramatroSimulacaoSuccess(response);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                recarregarBuscaParam = true;
                loadViewProgress.dismiss();
                Intent appIndis = new Intent(PrincipalActivity.this, AppIndisponivelActivity.class);
                appIndis.putExtra("IS_BUSCA_PARAM", true);

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

        textViewHomeMinimumPurchaseValue.setText(String.format("valor mínimo da compra: %s", ViewUtils.getMoedaFormat(response.getPayload().getValorMinimoCarrinho())));

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
                        parametro.getParametroJogo().getConcurso().getValorApostaMinima(), "", "", parametro.getParametroJogo().getConcurso().getModalidade(),
                        parametro.getParametroJogo().getConcurso(), false, null));
            }
        }

        return listaModalidades;
    }

    private void layoutTrevoAcessoRapido() {
        valorRepassesPrincipal.setText("0");

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
        FirebaseManager.registraEvento(PrincipalActivity.this ,"ENTROU_MENU_TREVO");

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
                        FirebaseManager.registraEvento(PrincipalActivity.this ,"EFETUOU_LOGOUT_SUCESSO");

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
                FirebaseManager.registraEvento(PrincipalActivity.this ,"EFETUOU_LOGOUT_OFFLINE_SUCESSO");
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
            @Nullable HomeAdapter.ViewHolder newCurrent) {

        if (currentHolder != null && newCurrent != null) {
            float position = Math.abs(currentPosition);
            Log.d("position", "position: " + position);
            Log.d("currentIndex", "currentIndex: " + currentIndex);
            Log.d("newIndex", "newIndex: " + newIndex);
        }

    }

    @Override
    public void onCurrentItemChanged(@Nullable HomeAdapter.ViewHolder viewHolder, int adapterPosition) {
        adapterPosition  = infiniteAdapter.getRealCurrentPosition();
        if (viewHolder != null) {
            EstiloModalidade estilo = new EstiloModalidade(data.get(adapterPosition).getTipoModalidade());
            buttonHomeBet.setBackgroundColor(ContextCompat.getColor(this, estilo.getCorClara()));
            buttonHomeBet.setTag(adapterPosition);
            buttonHomeBet.setText(ViewUtils.textFuturaAndFuturaBold(this, "_Aposte_\npor " + ViewUtils.getMoedaFormat(modalidadesParaListar.get(adapterPosition).getParametroJogo().getConcurso().getValorApostaMinima())));
        }
    }

    @Click(R.id.buttonHomeBet)
    protected void clickButtonHomeBet() {
        FirebaseManager.registraEvento(PrincipalActivity.this ,"ENTROU_SIMULAR_APOSTA" + modalidadesParaListar.get(Integer.parseInt(buttonHomeBet.getTag().toString())).getParametroJogo().getConcurso().getModalidade());

        Intent activity = new Intent(PrincipalActivity.this, SimularApostaActivity_.class);
        activity.putExtra("tipoAposta", modalidadesParaListar.get(Integer.parseInt(buttonHomeBet.getTag().toString())).getParametroJogo().getConcurso().getModalidade());
        activity.putExtra("modalidade", (new Gson()).toJson(modalidadesParaListar.get(Integer.parseInt(buttonHomeBet.getTag().toString())).getParametroJogo()));
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
            drawerLayout.closeDrawer(Gravity.START, false);
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
            case 4: startActivity(new Intent(PrincipalActivity.this, RepassesSociaisActivity.class));
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
            case 1:
                startActivity(new Intent(PrincipalActivity.this, AlterarDadosLoginCaixaActivity.class));
                break;
            case 2:
                startActivity(new Intent(PrincipalActivity.this, MinhasApostasActivity_.class));
                break;
            case 3:
                startActivity(new Intent(PrincipalActivity.this, ListaComprasActivity.class));
                break;
            case 4:
                startActivity(new Intent(PrincipalActivity.this, FavoritasActivity.class));
                break;
            case 5:
                startActivity(new Intent(PrincipalActivity.this, CarrinhosFavoritosActivity.class));
                break;
            case 6:
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
            startActivity(new Intent(PrincipalActivity.this, TutorialRapidaoActivity.class));
        }
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