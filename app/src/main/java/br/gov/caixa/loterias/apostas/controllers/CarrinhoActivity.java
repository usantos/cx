package br.gov.caixa.loterias.apostas.controllers;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import br.gov.caixa.loterias.apostas.LoteriasBaseAppActivity;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bean.SessaoUsuario;
import br.gov.caixa.loterias.apostas.model.bo.DadosUsuarioBO;
import br.gov.caixa.loterias.apostas.model.bo.MensagensNetwork;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ComboApostaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroSimulacao;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametrosSimulacao;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.CarrinhoSingleton;
import br.gov.caixa.loterias.apostas.model.model.CarrinhoModel;
import br.gov.caixa.loterias.apostas.model.sp.LoginSP;
import br.gov.caixa.loterias.apostas.utils.AlertDialogExperimenteLogarSingleton;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.AnalyticsHelper;
import br.gov.caixa.loterias.apostas.utils.AppCenterManager;
import br.gov.caixa.loterias.apostas.utils.DateUtils;
import br.gov.caixa.loterias.apostas.utils.DialogUtils;
import br.gov.caixa.loterias.apostas.utils.FragmentUtils;
import br.gov.caixa.loterias.apostas.utils.IntentUtil;
import br.gov.caixa.loterias.apostas.utils.LocalizacaoUtils;
import br.gov.caixa.loterias.apostas.utils.MessagingUtils;
import br.gov.caixa.loterias.apostas.utils.PermissaoUtil;
import br.gov.caixa.loterias.apostas.utils.RedirectUtils;
import br.gov.caixa.loterias.apostas.utils.ResultadoNavegacaoAposta;
import br.gov.caixa.loterias.apostas.utils.SessaoUsuarioUtil;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.utils.helper.AdicionarApostaCarrinhoHelper;
import br.gov.caixa.loterias.apostas.view.activity.FormaPagamentoActivity;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.CarrinhoApostasListViewAdapter;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.CarrinhoApostasListViewComboAdapter;
import br.gov.caixa.loterias.apostas.view.custom.ExpandableHeightRecyclerView;
import br.gov.caixa.loterias.apostas.view.fragment.SomadorCarrinhoFragment;
import br.gov.caixa.loterias.apostas.view.listener.OnDialogBotaoListener;
import br.gov.caixa.loterias.apostas.view.listener.OnDialogDoisBotoesListener;
import br.gov.caixa.loterias.apostas.view.listener.OnDialogFavoritarListener;


public class CarrinhoActivity extends LoteriasBaseAppActivity {

    public static final String ORIGEM = "ORIGEM";
    public static String CARRINHO = "CARRINHO";
    public static String LER_CARRINHO_LOCAL = "LER_CARRINHO_LOCAL";
    public static int FIM_SCROLL = 0;
    public static int PAGE_SIZE = 10;
    public int offSet = 0;

    private Toolbar toolbar;
    private Dialog dialogFavoritar;
    private ExpandableHeightRecyclerView rvListaApostas, rvListaBoloes, rvListaCombos;
    private TextView infoValorMinimo;
    protected View vLoading;
    private EditText etCarrinho;
    private Button btnAvancaPagamento, btnNovaAposta;
    private ImageButton btnLimpar, btnFavoritar;
    private CarrinhoDTO carrinho;
    private boolean lerCarrinhoLocal = false;
    private boolean ocorrendoValidacao = false;
    private boolean onResume = false;
    private boolean bloqScroll = false;
    private List<IdentificaoDeUmaApostaDas8Modalidades> listApostas = new ArrayList<>();
    private List<IdentificaoDeUmaApostaDas8Modalidades> listBoloes  = new ArrayList<>();
    private List<ComboApostaDTO> listCombos = new ArrayList<>();
    private LinearLayoutManager layoutManager, layoutManagerBoloes, layoutManagerCombos;
    private ScrollView svCarrinho;
    private TextView textCotasBolao, textApostasIndividuais, textComboApostas;
    private View viewDivBottomBolao, viewDivBottom, viewDivBottomCombo;

    private Timer timer;
    private Handler handler = new Handler();

    private String origem;

    private CarrinhoModel model;

    //fragments
    private SomadorCarrinhoFragment somadorCarrinhoFragment;
    private TextView titleToolbar;
    private ImageButton customButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrinho);
        model = new CarrinhoModel(CarrinhoActivity.this);
        carrinho = CarrinhoSingleton.getInstance().getCarrinho();
        pegaExtras();
        timerExcluiCotasExpiradas();
        setaViews();
        setaMetodos();
    }

    @Override
    protected void onResume() {
        super.onResume();
        desabilitaBoloes();
        desabilitaApostas();
        desabilitaCombos();
        limparCarrinhoInicial();
        atualizaParametros();
    }

    private void limparCarrinhoInicial() {
        offSet = 0;
        synchronized (rvListaApostas) {
            listApostas.removeAll(listApostas);
            rvListaApostas.getAdapter().notifyDataSetChanged();

            //Importante pro refresh dos Boloes
            listBoloes.clear();
            rvListaBoloes.getAdapter().notifyDataSetChanged();

            listCombos.clear();
            rvListaCombos.getAdapter().notifyDataSetChanged();
        }
    }

    private void postResume(){
        if (DadosUsuarioBO.checarUsuarioLogado(this)) {
            lerCarrinhoLocal = false;
        }
        if (ocorrendoValidacao && !onResume) {
            onResume = true;
            DialogUtils.dialogEntendi(CarrinhoActivity.this, CarrinhoActivity.this.getResources().getString( R.string.label_verificar_valor_carrinho ));
        }
        lerCarrinho();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1) {
            callWebservice();
        }
    }

    private void lerCarrinho() {
        if (getIntent().getComponent().getClassName().equals(CarrinhoActivity.class.getName())) {
            lerCarrinhoLocal = false;
            carrinho = null;
        }
        if (lerCarrinhoLocal) {
            AppCenterManager.registraEvento( getResources().getString(R.string.evento_carrinho_offline));
            lerCarrinhoLocal();
        } else {
            atualizaLayout(offSet);
        }
        handleBtnFavoritar();
    }

    public void lerCarrinhoLocal(){
        carrinho = model.lerCarrinhoLocal();
        atualizaLayout(offSet);
    }

    private void handleBtnFavoritar() {
        if (!DadosUsuarioBO.checarUsuarioLogado(this)) {
            btnFavoritar.setVisibility(View.GONE);
        } else {
            if (podeHabilitarFavoritar()) {
                btnFavoritar.setVisibility(View.VISIBLE);
            }
        }
    }

    private boolean podeHabilitarFavoritar(){
        return (carrinho != null && carrinho.getApostas() != null && !carrinho.getApostas().isEmpty()) &&
                (carrinho.getBoloes() == null || (carrinho.getBoloes() != null && carrinho.getBoloes().isEmpty())) &&
                        (carrinho.getCombos() == null || (carrinho.getCombos() != null && carrinho.getCombos().isEmpty()));
    }

    private void setaDialogFavoritar() {
        dialogFavoritar = new Dialog(this);
        dialogFavoritar.setContentView(R.layout.custom_dialog_edit_text);

        TextView tituloDialog = dialogFavoritar.findViewById(R.id.tv_titulo);
        tituloDialog.setText("Carrinho Favorito");

        etCarrinho = dialogFavoritar.findViewById(R.id.et_carrinho);

        Button btnOk = dialogFavoritar.findViewById(R.id.btn_ok);
        Button btnCancelar = dialogFavoritar.findViewById(R.id.btn_cancelar);

        btnOk.setOnClickListener(v -> {
            String text = etCarrinho.getText().toString();
            if (text != null && !text.isEmpty()) {
                validarCarrinhoFavorito(text);
            }
        });
        dialogFavoritar.setOnDismissListener(dialogInterface -> {
            etCarrinho.setText("");
        });
        btnCancelar.setOnClickListener(v -> dialogFavoritar.dismiss());

        if(dialogFavoritar.getWindow() != null){

            int screenWidth = getResources().getDisplayMetrics().widthPixels;
            int dialogWidth = (int) (screenWidth * 0.85); // Define a largura da dialog como 85% da largura da tela

            dialogFavoritar.getWindow().setLayout(dialogWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialogFavoritar.getWindow().setBackgroundDrawableResource(R.drawable.rounded_dialog_background);

        }

    }

    private void pegaExtras() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            lerCarrinhoLocal = bundle.getBoolean(LER_CARRINHO_LOCAL);
            origem = bundle.getString(ORIGEM);
        }
    }

    private void setaViews() {
        textCotasBolao = findViewById(R.id.tv_cotas_bolao);
        textApostasIndividuais = findViewById(R.id.tv_apostas_individuais);
        textComboApostas = findViewById(R.id.tv_combo_apostas);

        viewDivBottomBolao = findViewById(R.id.v_div_bottom_cotas);
        viewDivBottom = findViewById(R.id.v_div_bottom);
        viewDivBottomCombo = findViewById(R.id.v_div_bottom_combos);

        rvListaBoloes  = findViewById(R.id.erv_boloes_listas);
        rvListaApostas = findViewById(R.id.erv_apostas_listas);
        rvListaCombos = findViewById(R.id.erv_combos_listas);

        infoValorMinimo = findViewById(R.id.tv_info_valor_minimo);
        btnAvancaPagamento = findViewById(R.id.btn_avancar_pagamento);
        btnNovaAposta = findViewById(R.id.btn_nova_aposta);
        toolbar = findViewById(R.id.toolbar);
        btnLimpar = findViewById(R.id.ib_excluir);
        btnFavoritar = findViewById(R.id.ib_favoritar);
        vLoading = findViewById(R.id.pb_loading);
        svCarrinho = findViewById(R.id.sv_carrinho);
        configurarToolbar();
        setaDialogFavoritar();
    }

    private void configurarToolbar(){
        titleToolbar = findViewById(R.id.textCustom);
        titleToolbar.setText(R.string.carrinho_nde_apostas);
        ViewCompat.setAccessibilityHeading(titleToolbar,true);
        customButton = findViewById(R.id.customButton);
        customButton.setContentDescription(getString(R.string.voltar));
        customButton.setFocusable(true);
        customButton.setFocusableInTouchMode(true);
        customButton.requestFocus();
        btnLimpar.setContentDescription(getString(R.string.excluir));
        btnFavoritar.setContentDescription(getString(R.string.favoritar_carrinho));
        configurarBack();
    }
    private void configurarBack() {

        getOnBackPressedDispatcher().addCallback(
                this,
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {

                        setResult(ResultadoNavegacaoAposta.RESULT_RESET);
                        finish();
                    }
                }
        );

        customButton.setOnClickListener(
                v -> getOnBackPressedDispatcher().onBackPressed()
        );
    }

    private void setaMetodos() {
        btnAvancaPagamento.setOnClickListener(v -> onClickAvancaPagamento());
        btnNovaAposta.setOnClickListener(v -> clickBotaoFazerNovaAposta());
        btnLimpar.setOnClickListener(v -> clickLimparCarrinhoLinearLayout());
        btnFavoritar.setOnClickListener(v -> onClickFavoritar());

        layoutManagerBoloes = new LinearLayoutManager(this);
        rvListaBoloes.setLayoutManager(layoutManagerBoloes);
        rvListaBoloes.setAdapter(new CarrinhoApostasListViewAdapter(this, (ArrayList<IdentificaoDeUmaApostaDas8Modalidades>) listBoloes, this, false, model, true));
        rvListaBoloes.setExpanded(Boolean.TRUE);
        rvListaBoloes.setItemViewCacheSize(PAGE_SIZE);

        layoutManager = new LinearLayoutManager(this);
        rvListaApostas.setLayoutManager(layoutManager);
        rvListaApostas.setAdapter(new CarrinhoApostasListViewAdapter(this, (ArrayList<IdentificaoDeUmaApostaDas8Modalidades>) listApostas, this, true, model, false));
        rvListaApostas.setExpanded(Boolean.TRUE);
        rvListaApostas.setItemViewCacheSize(PAGE_SIZE);
        configuraSwipe(rvListaApostas);

        layoutManagerCombos = new LinearLayoutManager(this);
        rvListaCombos.setLayoutManager(layoutManagerCombos);
        rvListaCombos.setAdapter(new CarrinhoApostasListViewComboAdapter(this, (ArrayList<ComboApostaDTO>) listCombos, this, model));
        rvListaCombos.setExpanded(Boolean.TRUE);
        rvListaCombos.setItemViewCacheSize(PAGE_SIZE);

        svCarrinho.getViewTreeObserver().addOnScrollChangedListener(() -> {
            int scrollY = svCarrinho.getScrollY();
            View view = svCarrinho.getChildAt(svCarrinho.getChildCount() - 1);
            int diff = (view.getBottom() - (svCarrinho.getHeight() + scrollY));
            if (diff == FIM_SCROLL && !bloqScroll) {
                vLoading.setVisibility(View.VISIBLE);
                bloqScroll = true;
                new Handler().postDelayed(() -> {
                    atualizaLayout(offSet);
                }, 1);
            }
        });
    }

    private void onClickFavoritar() {
        DialogUtils.dialogEntendiListener(CarrinhoActivity.this,
            getString(R.string.loteca_nao_favorita),
                new OnDialogBotaoListener() {
                    @Override
                    public void onButtonClick(DialogInterface dialog, int which) {
                        dialogFavoritar.show();
                    }
                }
        );
    }

    private void onClickAvancaPagamento() {
        logCart();
        if (LoginSP.isLoginRealizado() && RedirectUtils.temRedirectCadastrarApostador()){
            startActivity(IntentUtil.getIntentOrigemDestino(CarrinhoActivity.this, CadastrarActivity.class));
            finish();
        } else if (!AdicionarApostaCarrinhoHelper.usuarioEstaSuspenso(this, 2)) {
            if (temCotasExpiradas()) {
                AlertDialogUtils.show(CarrinhoActivity.this);
                vLoading.setVisibility(View.VISIBLE);
                model.buscaCarrinhoSilce(new OnSilceListener<CarrinhoDTO>() {
                    @Override
                    public void success(CarrinhoDTO payload) {
                        vLoading.setVisibility(View.GONE);
                        AlertDialogUtils.dismiss();
                        setCarrinho(payload);
                        if (payload.getMsgCotaExcluida() != null && payload.getMsgCotaExcluida()) {
                            notificationCotasExcluidas();
                        }
                        trataPermissao();
                    }

                    @Override
                    public void error(VolleyError error) {
                        vLoading.setVisibility(View.GONE);
                        AlertDialogUtils.dismiss();
                        trataPermissao();
                    }
                });
            } else {
                trataPermissao();
            }
        }
    }

    private void logCart() {
        if (carrinho == null) {
            return;
        }
        String total = ViewUtils.getMoedaFormat(carrinho.getValorTotal());
        int apostas = carrinho.getApostas() != null ? carrinho.getApostas().size() : 0;
        int apostasIndividuais = carrinho.getApostasIndividuais() != null ? carrinho.getApostasIndividuais().size() : 0;
        int boloes = carrinho.getBoloes() != null ? carrinho.getBoloes().size() : 0;
        AnalyticsHelper.getInstance().logInteraction(
                AnalyticsHelper.EventCategoryParams.CTA,
                AnalyticsHelper.EventActionParams.CLICK,
                AnalyticsHelper.EventLabelParams.PAYMENT,
                AnalyticsHelper.JourneyParams.APOSTAR,
                AnalyticsHelper.SubJourneyParams.CARRINHO,
                AnalyticsHelper.Tela.BETS_CART,
                total,
                String.valueOf(apostas),
                String.valueOf(apostasIndividuais),
                String.valueOf(boloes)
        );
    }

    private void trataPermissao() {
        LocalizacaoUtils.checaStatusPermissaoAsync(this, result -> {
            if (result == LocalizacaoUtils.LOC_NAO_PERMITIDA ||
                    result == LocalizacaoUtils.LOC_NEG_PERMANENTEMENTE ||
                    result == LocalizacaoUtils.LOC_DESATIVADA) {
                DialogUtils.dialogSimNao(CarrinhoActivity.this,
                        getString(R.string.posso_consultar_sua_localizacao),
                        new OnDialogDoisBotoesListener() {
                            @Override
                            public void PositiveButton(DialogInterface dialog, int which) {
                                if (result == LocalizacaoUtils.LOC_NAO_PERMITIDA) {
                                    PermissaoUtil.checkList(CarrinhoActivity.this, PermissaoUtil.getListaLocalizacaoPermissoes(), 123);
                                    LocalizacaoUtils.incrementContLocation();
                                } else if (result == LocalizacaoUtils.LOC_NEG_PERMANENTEMENTE) {
                                    LocalizacaoUtils.intentConfigPermissao(CarrinhoActivity.this);
                                } else if (result == LocalizacaoUtils.LOC_DESATIVADA) {
                                    LocalizacaoUtils.intentConfigGps(CarrinhoActivity.this);
                                }
                            }

                            @Override
                            public void NegativeButton(DialogInterface dialog, int which) {
                                showAlertLocalizacao();
                            }
                        });
            } else {
                if (result == LocalizacaoUtils.LOC_NO_BRASIL) {
                    onFluxoLocalizacaoNoBrasil();
                } else {
                    showAlertLocalizacao();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LocalizacaoUtils.PERMISSAO_LOCALIZACAO_COD) {
            if (grantResults.length > 0 && grantResults[0] >= 0) {
                trataPermissao();
            } else {
                if(LocalizacaoUtils.checaNegadaDefinitivamente(CarrinhoActivity.this)){
                    LocalizacaoUtils.intentConfigPermissao(CarrinhoActivity.this);
                } else {
                    showAlertLocalizacao();
                }
            }
        }
    }

    private void showAlertLocalizacao() {
        DialogUtils.dialogEntendiListener(CarrinhoActivity.this,
                getString(R.string.alerta_precisa_estar_no_brasil),
                new OnDialogBotaoListener() {
                    @Override
                    public void onButtonClick(DialogInterface dialog, int which) {
                        //PrincipalActivity_.intent(CarrinhoActivity.this).flags(Intent.FLAG_ACTIVITY_CLEAR_TOP).start();
                        Intent intent = IntentUtil.getIntentOrigemDestino(CarrinhoActivity.this, PrincipalActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                }
        );
    }

    private void onFluxoLocalizacaoNoBrasil(){
        try {
            if (model.existeCompraMesmoValorEm24horas(carrinho.getValorTotal())){
                //modal
                DialogUtils.dialogSimNao(CarrinhoActivity.this,
                        getString(R.string.compra_existente_24_horas),
                        new OnDialogDoisBotoesListener() {
                            @Override
                            public void PositiveButton(DialogInterface dialog, int which) {
                                // sem ação, apenas fecha o dialog
                            }

                            @Override
                            public void NegativeButton(DialogInterface dialog, int which) {
                                if (DadosUsuarioBO.checarUsuarioLogado(CarrinhoActivity.this)) {
                                    validarCarrinho();
                                } else {
                                    AlertDialogExperimenteLogarSingleton.show(CarrinhoActivity.this, true, null);
                                }
                            }
                        });
            } else {
                if (DadosUsuarioBO.checarUsuarioLogado(this)) {
                    validarCarrinho();
                } else{
                    AlertDialogExperimenteLogarSingleton.show(this, true, null);
                }
            }
        } catch (Exception e){}
    }

    public void deletaCombo(int position, ComboApostaDTO comboApostaDTO) {
        synchronized (rvListaCombos) {
            listCombos.remove(position);
            rvListaCombos.getAdapter().notifyItemRemoved(position);
            setValorTotalCarrinho();
            configuraBotoesSuperiores();
        }
        if (listCombos.size() == 0) {
            desabilitaCombos();
        }
    }

    public void deletaAposta(int position, IdentificaoDeUmaApostaDas8Modalidades aposta, boolean isBolao) {
        if (isBolao) {
            synchronized (rvListaBoloes) {
                listBoloes.remove(position);
                rvListaBoloes.getAdapter().notifyItemRemoved(position);
                setValorTotalCarrinho();
                configuraBotoesSuperiores();
            }
            if (listBoloes.size() == 0) {
                desabilitaBoloes();
            }
            return;
        }
        if (listApostas != null) {
            synchronized (rvListaApostas) {
                if (apagouLotomaniaGerarEspelho(listApostas.get(position))){
                    listApostas.remove(position);
                    rvListaApostas.getAdapter().notifyDataSetChanged();
                } else {
                    listApostas.remove(position);
                    rvListaApostas.getAdapter().notifyItemRemoved(position);
                }
                offSet--;
                setValorTotalCarrinho();
                configuraBotoesSuperiores();
            }
            if (listApostas.size() == 0) {
                desabilitaApostas();
            }
        }
    }

    private boolean apagouLotomaniaGerarEspelho(IdentificaoDeUmaApostaDas8Modalidades aposta) {
        return aposta.getModalidade() == ModalidadeEnum.LOTOMANIA && aposta.getGerarApostaEspelho();
    }

    public void atualizaLayout(int offSet) {
        try {
            if (carrinho != null) {
                if (temBolaoCarrinho()) {
                    configuraRecyclerViewCarrinhoBoloes();;
                    fragmentSomadorCarrinho();
                    habilitaBoloes();
                }
                if (temApostasIndividuaisCarrinho()) {
                    configuraRecyclerViewCarrinhoAposta(offSet);
                    fragmentSomadorCarrinho();
                    habilitaApostas();
                }
                if (temCombosCarrinho()) {
                    configuraRecyclerViewCarrinhoCombos();
                    fragmentSomadorCarrinho();
                    habilitaCombos();
                }
                configuraBotoesSuperiores();
            } else {
                callWebservice();
            }
        } catch (Exception e) {
        }
        montarLayout();
    }

    private void callWebservice() {
        AlertDialogUtils.show(CarrinhoActivity.this);
        vLoading.setVisibility(View.VISIBLE);
        model.buscaCarrinhoSilce(onCarrinhoListener());
    }

    private OnSilceListener<CarrinhoDTO> onCarrinhoListener() {
        return new OnSilceListener<CarrinhoDTO>() {
            @Override
            public void success(CarrinhoDTO payload) {
                vLoading.setVisibility(View.GONE);
                AlertDialogUtils.dismiss();

                setCarrinho(payload);
                atualizaLayout(offSet);

                if (payload != null) {
                    AnalyticsHelper.getInstance().logViewCartScreen(
                            AnalyticsHelper.JourneyParams.APOSTAR,
                            AnalyticsHelper.SubJourneyParams.CARRINHO,
                            AnalyticsHelper.Tela.BETS_CART,
                            payload
                    );
                }

                if (payload.getMsgCotaExcluida() != null && payload.getMsgCotaExcluida()) {
                    notificationCotasExcluidas();
                }
            }

            @Override
            public void error(VolleyError error) {
                vLoading.setVisibility(View.GONE);
                AlertDialogUtils.dismiss();
            }
        };
    }

    private void fragmentSomadorCarrinho() {
        if(somadorCarrinhoFragment == null) {
           somadorCarrinhoFragment = FragmentUtils.startSomadorCarrinhoFragment(getSupportFragmentManager(), somadorCarrinhoFragment, R.id.id_fg_somador_carrinho, "TELA CARRINHO");
        } else {
            setValorTotalCarrinho();
        }
    }

    public void setCarrinho(CarrinhoDTO carrinho) {
        this.carrinho = carrinho;
        CarrinhoSingleton.getInstance().setCarrinho(this.carrinho);
    }

    private void validarCarrinho() {
        ocorrendoValidacao = true;
        AlertDialogUtils.show(CarrinhoActivity.this);
        model.validarCarrinho(onValidarCarrinhoListener());
    }

    private OnSilceListener onValidarCarrinhoListener() {
        return new OnSilceListener() {
            @Override
            public void success(Object payload) {
                ocorrendoValidacao = false;
                AlertDialogUtils.dismiss();
                CarrinhoSingleton.getInstance().setCarrinho(carrinho);
                if (carrinho != null) {
                    AnalyticsHelper.getInstance().logFlowEnd(
                            AnalyticsHelper.JourneyParams.APOSTAR,
                            AnalyticsHelper.JourneyParams.APOSTAR,
                            AnalyticsHelper.SubJourneyParams.CARRINHO,
                            AnalyticsHelper.Tela.BETS_CART,
                            carrinho
                    );
                }
                startActivity(new Intent(CarrinhoActivity.this, FormaPagamentoActivity.class));
            }

            @Override
            public void error(VolleyError error) {
                AlertDialogUtils.dismiss();
                if (error != null && error.networkResponse != null && error.networkResponse.statusCode != 401) {
                    ocorrendoValidacao = false;
                }
            }
        };
    }

    private void validarCarrinhoFavorito(String nome) {
        AlertDialogUtils.show(CarrinhoActivity.this);
        model.validarCarrinhoFavorito(nome, onValidarCarrinhoFavorito(nome));
    }

    private OnSilceListener onValidarCarrinhoFavorito(String nome) {
        return new OnSilceListener() {
            @Override
            public void success(Object payload) {
                AlertDialogUtils.dismiss();
                favoritarCarrinho(nome);
            }

            @Override
            public void error(VolleyError error) {
                AlertDialogUtils.dismiss();

              DialogUtils.dialogSim(CarrinhoActivity.this,
                        MensagensNetwork.getErrorMessage(error),
                        new OnDialogBotaoListener() {
                            @Override
                            public void onButtonClick(DialogInterface dialog, int which) {
                                favoritarCarrinho(nome);
                            }
                        }
                );
            }
        };
    }

    private void favoritarCarrinho(String nome) {
        AlertDialogUtils.show(CarrinhoActivity.this);
        model.favoritarCarrinho(nome, onFavoritarCarrinhoListener());
    }

    private OnSilceListener onFavoritarCarrinhoListener() {
        return new OnSilceListener() {
            @Override
            public void success(Object payload) {
                AlertDialogUtils.dismiss();

               DialogUtils.dialogEntendiListener(CarrinhoActivity.this,
                        getString(R.string.carrinho_fav_lbl_msg),

                        new OnDialogBotaoListener() {
                            @Override
                            public void onButtonClick(DialogInterface dialog, int which) {
                                dialogFavoritar.dismiss();
                            }
                        }
                );
            }

            @Override
            public void error(VolleyError error) {
                AlertDialogUtils.dismiss();
            }
        };
    }

    protected void clickBotaoFazerNovaAposta() {
        if (origem != null && origem.equalsIgnoreCase(DetalhesComprasActivity.DETALHES_COMPRA_ACTIVITY)){
            //PrincipalActivity_.intent(CarrinhoActivity.this).flags(Intent.FLAG_ACTIVITY_CLEAR_TOP).start();
            Intent intent = IntentUtil.getIntentOrigemDestino(CarrinhoActivity.this, PrincipalActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else {
            setResult(ResultadoNavegacaoAposta.RESULT_RESET);
            finish();
        }
    }

    protected void clickLimparCarrinhoLinearLayout() {
        DialogUtils.dialogSim(CarrinhoActivity.this,
                getString(R.string.MA011),

                new OnDialogBotaoListener() {
                    @Override
                    public void onButtonClick(DialogInterface dialog, int which) {
                        AlertDialogUtils.show(CarrinhoActivity.this);
                        model.limparCarrinho(onLimparCarrinhoListener());
                    }
                }
        );
    }

    private OnSilceListener onLimparCarrinhoListener() {
        return new OnSilceListener() {
            @Override
            public void success(Object payload) {
                AlertDialogUtils.dismiss();
                limparCarrinhoAtualizarTela();
                limparCarrinhosFavoritos();
                setValorTotalCarrinho();
            }

            @Override
            public void error(VolleyError error) {
                AlertDialogUtils.dismiss();
                deleteCarrinhoLocal();
            }
        };
    }

    private void limparCarrinhosFavoritos() {
        model.limparCarrinhosFavoritosLocal();
    }

    private void deleteCarrinhoLocal() {
        model.deletaCarrinhoLocal();
        limparCarrinhoAtualizarTela();
        setValorTotalCarrinho();
    }

    private void limparCarrinhoAtualizarTela() {
        offSet = 0;

        carrinho.getApostas().removeAll(carrinho.getApostas());
        carrinho.getApostasIndividuais().removeAll(carrinho.getApostasIndividuais());
        if (temBolaoCarrinho()){
            carrinho.getBoloes().removeAll(carrinho.getBoloes());
        }
        if(temCombosCarrinho()){
            carrinho.getCombos().removeAll(carrinho.getCombos());
        }
        carrinho.setValorTotal(BigDecimal.ZERO);
        CarrinhoSingleton.getInstance().setCarrinho(carrinho);
        synchronized (rvListaApostas) {
            listApostas.removeAll(listApostas);
            rvListaApostas.getAdapter().notifyDataSetChanged();
            listBoloes.clear();
            rvListaBoloes.getAdapter().notifyDataSetChanged();
            desabilitaApostas();
            desabilitaBoloes();
            desabilitaCombos();
        }
        atualizaLayout(offSet);
        configuraBotoesSuperiores();
    }

    private boolean temBolaoCarrinho(){
        return carrinho != null && carrinho.getBoloes() != null && carrinho.getBoloes().size() > 0;
    }
    private boolean temApostasIndividuaisCarrinho(){
        return carrinho != null && carrinho.getApostasIndividuais() != null && carrinho.getApostasIndividuais().size() > 0;
    }
    private boolean temCombosCarrinho(){
        return carrinho != null && carrinho.getCombos() != null && carrinho.getCombos().size() > 0;
    }

    private void montarLayout() {
        SessaoUsuario sessaoUsuario = SessaoUsuario.getInstance();
        BigDecimal valorCarrinho = sessaoUsuario.getValorMinimimoAposta() == null ? BigDecimal.ZERO : sessaoUsuario.getValorMinimimoAposta();
        if(valorCarrinho != null && valorCarrinho.compareTo(BigDecimal.ZERO) > 0){
            ViewUtils.setMoedaFormatHtml(valorCarrinho, infoValorMinimo, getResources().getString(R.string.label_lembrar_valor_minimo).replace(getResources().getString(R.string.underline), getResources().getString(R.string.quebra_html)) + getResources().getString(R.string.espaco_em_branco));
            infoValorMinimo.setVisibility(View.VISIBLE);
        }else{
            infoValorMinimo.setVisibility(View.GONE);
        }
        btnAvancaPagamento.setText(ViewUtils.textCaixaSTDBold(this, getString(R.string.avancarFormaPagamento)));
    }

    public void configuraRecyclerViewCarrinhoBoloes() {
        if (listBoloes != null && listBoloes.size() == 0) {
            if (temBolaoCarrinho()) {
                listBoloes.addAll(carrinho.getBoloes());
            }
        }
        vLoading.setVisibility(View.GONE);
    }
    public void configuraRecyclerViewCarrinhoCombos() {
        if (listCombos != null && listCombos.size() == 0) {
            if (temCombosCarrinho()) {
                listCombos.addAll(carrinho.getCombos());

            }
        }
        vLoading.setVisibility(View.GONE);
    }
    public void configuraRecyclerViewCarrinhoAposta(int offSet) {
        int cont;
        int maximoLista = 0;

        List<IdentificaoDeUmaApostaDas8Modalidades> apostas = new ArrayList<>();
        if (temApostasIndividuaisCarrinho()) {
            if (offSet > carrinho.getApostasIndividuais().size()) {
                vLoading.setVisibility(View.GONE);
                return;
            }
            maximoLista = pegaMaximoLista();
            for (cont = offSet; cont < maximoLista; cont++) {
                IdentificaoDeUmaApostaDas8Modalidades aposta = carrinho.getApostasIndividuais().get(cont) == null ? null : carrinho.getApostasIndividuais().get(cont);
                if (aposta.getModalidade() != null) {
                    apostas.add(aposta);
                }
            }
        }

        if (apostas != null) {
            listApostas.addAll(apostas);
        }

        if (listApostas != null) {
            synchronized (rvListaApostas) {
                rvListaApostas.getAdapter().notifyItemRangeInserted(offSet, maximoLista);
                vLoading.setVisibility(View.GONE);
            }

            this.offSet += PAGE_SIZE;
        }
        bloqScroll = false;
    }

    private void desabilitaBoloes() {
        textCotasBolao.setVisibility(View.GONE);
        viewDivBottomBolao.setVisibility(View.GONE);
        rvListaBoloes.setVisibility(View.GONE);
    }

    private void habilitaBoloes() {
        if (temBolaoCarrinho()) {
            textCotasBolao.setVisibility(View.VISIBLE);
            viewDivBottomBolao.setVisibility(View.VISIBLE);
            rvListaBoloes.setVisibility(View.VISIBLE);
        }
    }

    private void desabilitaApostas() {
        textApostasIndividuais.setVisibility(View.GONE);
        viewDivBottom.setVisibility(View.GONE);
        rvListaApostas.setVisibility(View.GONE);
    }

    private void habilitaApostas() {
        if (carrinho != null && carrinho.getApostasIndividuais() != null && carrinho.getApostasIndividuais().size() > 0) {
            textApostasIndividuais.setVisibility(View.VISIBLE);
            viewDivBottom.setVisibility(View.VISIBLE);
            rvListaApostas.setVisibility(View.VISIBLE);
        }
    }

    private void desabilitaCombos() {
        textComboApostas.setVisibility(View.GONE);
        viewDivBottomCombo.setVisibility(View.GONE);
        rvListaCombos.setVisibility(View.GONE);
    }

    private void habilitaCombos() {
        if (temCombosCarrinho()) {
            textComboApostas.setVisibility(View.VISIBLE);
            viewDivBottomCombo.setVisibility(View.VISIBLE);
            rvListaCombos.setVisibility(View.VISIBLE);
        }
    }

    private void configuraBotoesSuperiores() {
        if ((carrinho.getApostas() != null && !carrinho.getApostas().isEmpty()) ||
                (carrinho.getBoloes() != null && !carrinho.getBoloes().isEmpty()) ||
                (carrinho.getCombos() != null && !carrinho.getCombos().isEmpty())) {
            btnLimpar.setVisibility(View.VISIBLE);
            if (DadosUsuarioBO.checarUsuarioLogado(this)) {
                if (podeHabilitarFavoritar()) {
                    btnFavoritar.setVisibility(View.VISIBLE);
                }
            }
        } else {
            btnLimpar.setVisibility(View.GONE);
            btnFavoritar.setVisibility(View.GONE);
        }
    }

    private int pegaMaximoLista() {
        return offSet + PAGE_SIZE < carrinho.getApostasIndividuais().size() ? offSet + PAGE_SIZE : carrinho.getApostasIndividuais().size();
    }

    private void setValorTotalCarrinho() {
        carrinho.setValorTotal(carrinho.getValorTotal());
        if(somadorCarrinhoFragment == null){
            somadorCarrinhoFragment = FragmentUtils.startSomadorCarrinhoFragment(getSupportFragmentManager(), somadorCarrinhoFragment, R.id.id_fg_somador_carrinho, "TELA CARRINHO");
        }
        somadorCarrinhoFragment.atualizaValorTotal(carrinho);
    }

    private void atualizaParametros() {
        AlertDialogUtils.show(this);
        if(SessaoUsuarioUtil.precisaAtualizar()) {
            model.buscaParametroSiumulacao(onParametroSimulacaoListener());
        }else {
            postResume();
            AlertDialogUtils.dismiss();
        }
    }

    private OnSilceListener<ParametrosSimulacao> onParametroSimulacaoListener() {
        return new OnSilceListener<ParametrosSimulacao>() {
            @Override
            public void success(ParametrosSimulacao payload) {
                AlertDialogUtils.dismiss();

                List<ParametroSimulacao> parametros = payload.getParametros();
                if (parametros == null || parametros.isEmpty()) {
                    Intent appIndis = new Intent(CarrinhoActivity.this, AppIndisponivelActivity.class);
                    appIndis.putExtra(getResources().getString(R.string.extra_is_busca_param), true);

                    startActivityForResult(appIndis, 1);
                }

                SessaoUsuarioUtil.atualizaParametrosSingleton(payload);
                postResume();
            }

            @Override
            public void error(VolleyError error) {
                AlertDialogUtils.dismiss();
                postResume();
            }
        };
    }

    public void timerExcluiCotasExpiradas() {

        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        //Tarefa
                        if (temCotasExpiradas()) {
                            onResume();
                        }
                    }
                });
            }
        };
        timer.schedule(task, 0, 60000);; //1 minuto
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
    }

    private boolean temCotasExpiradas() {

        CarrinhoDTO carrinhoDTO = CarrinhoSingleton.getInstance().getCarrinho();
        if (carrinhoDTO == null || carrinhoDTO.getBoloes() == null ||
                carrinhoDTO.getBoloes().size() == 0) {
            return false;
        }

        List<IdentificaoDeUmaApostaDas8Modalidades> listBolao = carrinhoDTO.getBoloes();

        for (IdentificaoDeUmaApostaDas8Modalidades bolao : listBolao) {
            String dataHoraExpiracao = bolao.getReservaCotaBolao().getDataHoraExpiracaoReserva();
            if (dataHoraExpiracao != null || dataHoraExpiracao.length() > 0) {
                long tempo_restante_milesec = DateUtils.diffMillisSecondsTimerZone(dataHoraExpiracao);
                if (tempo_restante_milesec <= 0) {
                    return true;
                }
            }
        }
        return false;
    }

    private void notificationCotasExcluidas() {
        //Toast.makeText(this, getString(R.string.cotas_expiradas_excluidas_carrinho),
        //        Toast.LENGTH_SHORT).show();
        MessagingUtils.criaNotificacaoNova(this, "", this.getString(R.string.cotas_expiradas_excluidas_carrinho));
    }


    public void configuraSwipe(RecyclerView recyclerView){
        ItemTouchHelper.SimpleCallback callback =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

                    @Override
                    public boolean onMove(
                            RecyclerView recyclerView,
                            RecyclerView.ViewHolder viewHolder,
                            RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public int getSwipeDirs(
                            @NonNull RecyclerView recyclerView,
                            @NonNull RecyclerView.ViewHolder viewHolder) {

                        // bloqueia swipe do usuário ✅
                        return 0;
                    }

                    @Override
                    public void onSwiped(
                            @NonNull RecyclerView.ViewHolder viewHolder,
                            int direction) {
                        // nunca chamado
                    }
                };

        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(recyclerView);
    }
}
