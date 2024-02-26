package br.gov.caixa.loterias.apostas.controllers;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.VolleyError;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.BuildConfig;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bean.SessaoUsuario;
import br.gov.caixa.loterias.apostas.model.bo.DadosUsuarioBO;
import br.gov.caixa.loterias.apostas.model.bo.MensagensNetwork;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CotaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroSimulacao;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametrosSimulacao;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.CarrinhoSingleton;
import br.gov.caixa.loterias.apostas.model.model.CarrinhoModel;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.AppCenterManager;
import br.gov.caixa.loterias.apostas.utils.BuildVersionUtil;
import br.gov.caixa.loterias.apostas.utils.DateUtils;
import br.gov.caixa.loterias.apostas.utils.DialogUtils;
import br.gov.caixa.loterias.apostas.utils.FragmentUtils;
import br.gov.caixa.loterias.apostas.utils.LocalizacaoUtils;
import br.gov.caixa.loterias.apostas.utils.SessaoUsuarioUtil;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.activity.FormaPagamentoActivity;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.CarrinhoApostasListViewAdapter;
import br.gov.caixa.loterias.apostas.view.custom.ExpandableHeightRecyclerView;
import br.gov.caixa.loterias.apostas.view.fragment.SomadorCarrinhoFragment;
import br.gov.caixa.loterias.apostas.view.listener.OnDialogListenerLeftRight;


public class CarrinhoActivity extends AppCompatActivity {

    public static final String ORIGEM = "ORIGEM";
    public static String CARRINHO = "CARRINHO";
    public static String LER_CARRINHO_LOCAL = "LER_CARRINHO_LOCAL";
    public static int FIM_SCROLL = 0;
    public static int PAGE_SIZE = 10;
    public int offSet = 0;

    private Toolbar toolbar;
    private Dialog dialogFavoritar;
    private ExpandableHeightRecyclerView rvListaApostas, rvListaBoloes;
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
    private LinearLayoutManager layoutManager, layoutManagerBoloes;
    private ScrollView svCarrinho;
    private TextView textCotasBolao, textApostasIndividuais;
    private View viewDivBottomBolao, viewDivBottom;


    private String origem;

    private CarrinhoModel model;

    //fragments
    private SomadorCarrinhoFragment somadorCarrinhoFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrinho);
        model = new CarrinhoModel(CarrinhoActivity.this);
        pegaExtras();
        setaViews();
        setaMetodos();
    }

    @Override
    protected void onResume() {
        super.onResume();
        desabilitaBoloes();
        desabilitaApostas();
        limparCarrinhoInicial();
        atualizaParametros();
    }

    private void limparCarrinhoInicial() {
        offSet = 0;
        synchronized (rvListaApostas) {
            listApostas.removeAll(listApostas);
            rvListaApostas.getAdapter().notifyDataSetChanged();
        }
    }

    private void postResume(){
        if (DadosUsuarioBO.checarUsuarioLogado(this)) {
            lerCarrinhoLocal = false;
        }
        if (ocorrendoValidacao && !onResume) {
            onResume = true;
            ViewUtils.alertTitleButton(CarrinhoActivity.this, R.string.label_atencao, CarrinhoActivity.this.getResources().getString( R.string.label_verificar_valor_carrinho ),
                    CarrinhoActivity.this.getResources().getString( R.string.ok ) );
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

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
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
            if (carrinho != null && carrinho.getApostas() != null && carrinho.getApostas().size() > 0) {
                if (carrinho.getBoloes().size() == 0) {
                    btnFavoritar.setVisibility(View.VISIBLE);
                }
            }
        }
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
    }

    private void pegaExtras() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            carrinho = (CarrinhoDTO) bundle.getSerializable(CARRINHO);
            lerCarrinhoLocal = bundle.getBoolean(LER_CARRINHO_LOCAL);
            origem = bundle.getString(ORIGEM);
        }
    }

    private void setaViews() {

        textCotasBolao = findViewById(R.id.tv_cotas_bolao);
        textApostasIndividuais = findViewById(R.id.tv_apostas_individuais);
        viewDivBottomBolao = findViewById(R.id.v_div_bottom_cotas);
        viewDivBottom = findViewById(R.id.v_div_bottom);

        rvListaBoloes  = findViewById(R.id.erv_boloes_listas);
        rvListaApostas = findViewById(R.id.erv_apostas_listas);
        infoValorMinimo = findViewById(R.id.tv_info_valor_minimo);
        btnAvancaPagamento = findViewById(R.id.btn_avancar_pagamento);
        btnNovaAposta = findViewById(R.id.btn_nova_aposta);
        toolbar = findViewById(R.id.toolbar);
        btnLimpar = findViewById(R.id.ib_excluir);
        btnFavoritar = findViewById(R.id.ib_favoritar);
        vLoading = findViewById(R.id.pb_loading);
        svCarrinho = findViewById(R.id.sv_carrinho);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(ViewUtils.textFuturaAndFuturaBoldTitle(this, getString(R.string.title_activity_carrinho)));
        setaDialogFavoritar();
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
        ViewUtils.alertTitleButton(this, R.string.label_atencao,
            getString(R.string.loteca_nao_favorita),
            getResources().getString(R.string.title_entendi),
            new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    dialogFavoritar.show();
                }
            }
        );
    }

    private void onClickAvancaPagamento() {
        trataPermissao();
    }


    private void trataPermissao() {
        int statusPermissao = LocalizacaoUtils.checaStatusPermissao(this);
        //if (BuildConfig.DEBUG && BuildVersionUtil.isEmulador()) {
        //    statusPermissao = LocalizacaoUtils.LOC_NO_BRASIL;
        //}
        if (//statusPermissao == LocalizacaoUtils.LOC_NAO_PERMITIDA ||
            statusPermissao == LocalizacaoUtils.LOC_DESATIVADA) {
            DialogUtils.showDialogDefault(this, 0,
                                          getResources().getString(R.string.label_atencao),
                                          getString(R.string.posso_consultar_sua_localizacao),
                                          getResources().getString(R.string.label_sim),
                                          getResources().getString(R.string.label_nao),
                                          new OnDialogListenerLeftRight() {
                                              @Override
                                              public void left() {
                                                  //LocalizacaoUtils.solicitaPermissao(CarrinhoActivity.this);
                                                  LocalizacaoUtils.redirecionaConfiguracaoGPS(CarrinhoActivity.this);
                                              }

                                              @Override
                                              public void right() {
                                                  showAlertLocalizacao();
                                              }
                                          });
        } else if (statusPermissao == LocalizacaoUtils.LOC_NEG_PERMANENTEMENTE){
            showAlertLocalizacao();
        } else {
            if (statusPermissao == LocalizacaoUtils.LOC_NO_BRASIL) {
                onFluxoLocalizacaoNoBrasil();
            } else {
                showAlertLocalizacao();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (requestCode == LocalizacaoUtils.PERMISSAO_LOCALIZACAO_COD) {
            if (grantResults.length > 0 && grantResults[0] >= 0) {
                trataPermissao();
            } else {
                showAlertLocalizacao();
            }
        }
    }

    private void showAlertLocalizacao() {
        ViewUtils.alertTitleButton(this, R.string.label_atencao,
                                   getString(R.string.alerta_precisa_estar_no_brasil),
                                   getResources().getString(R.string.title_entendi), new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        PrincipalActivity_.intent(CarrinhoActivity.this).flags(Intent.FLAG_ACTIVITY_CLEAR_TOP).start();
                    }
                });
    }

    private void onFluxoLocalizacaoNoBrasil(){
        try {
            if (model.existeCompraMesmoValorEm24horas(carrinho.getValorTotal())){
                //modal
                ViewUtils.alertTitleButtonYesOrNotListener(CarrinhoActivity.this,
                                                           getString(R.string.compra_existente_24_horas),
                                                           (dialog, which) -> {
                                                               dialog.dismiss();
                                                           },
                                                           (dialog, which) -> {
                                                               validarCarrinho();
                                                           });
            } else {
                validarCarrinho();
            }
        } catch (Exception e){}
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
                listApostas.remove(position);
                rvListaApostas.getAdapter().notifyItemRemoved(position);
                offSet--;
                setValorTotalCarrinho();
                configuraBotoesSuperiores();
            }
            if (listApostas.size() == 0) {
                desabilitaApostas();
            }
        }
    }

    public void atualizaLayout(int offSet) {
        try {
            if (carrinho != null) {
                if (carrinho.getBoloes() != null) {
                    configuraRecyclerViewCarrinhoBoloes();;
                    //fragmentSomadorCarrinho();
                    habilitaBoloes();
                }
                if (carrinho.getApostas() != null) {
                    configuraRecyclerViewCarrinhoAposta(offSet);
                    fragmentSomadorCarrinho();
                    habilitaApostas();
                }
            } else {
                callWebservice();
            }
        } catch (Exception e) {
            e.printStackTrace();
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

                ViewUtils.alertTitleButtonYesOrNotListener(CarrinhoActivity.this,
                                                           MensagensNetwork.getErrorMessage(error),
                                                           (dialogInterface, i) -> favoritarCarrinho(nome));
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

                ViewUtils.alertTitleButton(CarrinhoActivity.this,
                                           R.string.label_atencao, getString(R.string.carrinho_fav_lbl_msg),
                                           getString(R.string.ok), dialogInterface -> { dialogFavoritar.dismiss(); });
            }

            @Override
            public void error(VolleyError error) {
                AlertDialogUtils.dismiss();
            }
        };
    }

    protected void clickBotaoFazerNovaAposta() {
        if (origem != null && origem.equalsIgnoreCase(DetalhesComprasActivity.DETALHES_COMPRA_ACTIVITY)){
            PrincipalActivity_.intent(CarrinhoActivity.this).flags(Intent.FLAG_ACTIVITY_CLEAR_TOP).start();
        } else {
            setResult(2);
            finish();
        }
    }

    protected void clickLimparCarrinhoLinearLayout() {
        ViewUtils.alertTitleButtonYesOrNotListener(this, getString(R.string.MA011), (dialogInterface, i) -> {
            AlertDialogUtils.show(CarrinhoActivity.this);
            model.limparCarrinho(onLimparCarrinhoListener());
        });
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
        carrinho.getBoloes().removeAll(carrinho.getBoloes());
        carrinho.setValorTotal(BigDecimal.ZERO);
        CarrinhoSingleton.getInstance().setCarrinho(carrinho);
        synchronized (rvListaApostas) {
            listApostas.removeAll(listApostas);
            rvListaApostas.getAdapter().notifyDataSetChanged();
            listBoloes.clear();
            rvListaBoloes.getAdapter().notifyDataSetChanged();
            desabilitaApostas();
            desabilitaBoloes();
        }
        atualizaLayout(offSet);
        configuraBotoesSuperiores();
    }

    private void montarLayout() {
        SessaoUsuario sessaoUsuario = SessaoUsuario.getInstance();
        BigDecimal valorCarrinho = sessaoUsuario.getValorMinimimoAposta() == null ? BigDecimal.ZERO : sessaoUsuario.getValorMinimimoAposta();
        ViewUtils.setMoedaFormatHtml(valorCarrinho, infoValorMinimo, getResources().getString(R.string.label_lembrar_valor_minimo).replace(getResources().getString(R.string.underline), getResources().getString(R.string.quebra_html)) + getResources().getString(R.string.espaco_em_branco));
        btnAvancaPagamento.setText(ViewUtils.textFuturaAndFuturaBold(this, getString(R.string.avancarFormaPagamento)));
    }

    public void configuraRecyclerViewCarrinhoBoloes() {
        if (listBoloes.size() == 0) {
            if (carrinho.getBoloes() != null && carrinho.getBoloes().size()>0) {
                listBoloes.addAll(carrinho.getBoloes());
            }
        }
    }

    public void configuraRecyclerViewCarrinhoAposta(int offSet) {
        int cont;
        int maximoLista = 0;

        List<IdentificaoDeUmaApostaDas8Modalidades> apostas = new ArrayList<>();
        if (carrinho != null && carrinho.getApostasIndividuais() != null && carrinho.getApostasIndividuais().size() > 0) {
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

        configuraBotoesSuperiores();

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
        if (carrinho != null && carrinho.getBoloes() != null && carrinho.getBoloes().size() > 0) {
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

    private void configuraBotoesSuperiores() {
        if (carrinho.getApostas().size() > 0) {
            btnLimpar.setVisibility(View.VISIBLE);
            if (DadosUsuarioBO.checarUsuarioLogado(this)) {
                if (carrinho.getBoloes().size() == 0) {
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
}
