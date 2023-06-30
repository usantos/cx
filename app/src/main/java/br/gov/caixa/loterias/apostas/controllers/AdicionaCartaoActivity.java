package br.gov.caixa.loterias.apostas.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.android.volley.VolleyError;
import com.github.pinball83.maskededittext.MaskedEditText;
import com.mercadopago.model.CardToken;

import java.util.ArrayList;
import java.util.Date;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.DadosUsuarioBO;
import br.gov.caixa.loterias.apostas.model.bo.RecargaPayConnection;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostadorDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CardHolderRecargaPay;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CartaoRecargaPayDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CompraAsyncResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DataExpiracao;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.MeioPagamentoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.CarrinhoSingleton;
import br.gov.caixa.loterias.apostas.model.model.AdicionaCartaoCreditoModel;
import br.gov.caixa.loterias.apostas.model.model.CartoesModel;
import br.gov.caixa.loterias.apostas.model.model.CompraModel;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.AppCenterManager;
import br.gov.caixa.loterias.apostas.utils.FragmentUtils;
import br.gov.caixa.loterias.apostas.utils.IntentUtil;
import br.gov.caixa.loterias.apostas.utils.MaskEditUtil;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.activity.DigitarCvvActivity;
import br.gov.caixa.loterias.apostas.view.config.CartaoCreditoConfig;
import br.gov.caixa.loterias.apostas.view.config.MeioPagamentoConfig;
import br.gov.caixa.loterias.apostas.view.fragment.CartaoCreditoFragment;
import br.gov.caixa.loterias.apostas.view.listener.OnCompraAsync;

public class AdicionaCartaoActivity extends SettingToolbarActivity{

    private EditText editTextNumeroCartaoContent, editTextNomeClienteCartaoContent, editTextMesValidadeCartaoContent,  editTextAnoValidadeCartaoContent;
    private TextView textoInfoSelecioneFormaPagamento;
    private CheckBox checkBoxSalvarCartao;
    private AppCompatImageView imagemMeioPagamento, voltar;
    private ConstraintLayout backColor;
    private View margem;
    private TextView alterarMeioPagamento;
    private Button btnAvancar;

    private Boolean primeiraSelecao = false;

    private CartoesModel modelVeio;
    private AdicionaCartaoCreditoModel model;
    private MeioPagamentoConfig config;

    private CartaoCreditoFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ViewUtils.configuraStatusBarGradientLayout(this, getWindow());
        AppCenterManager.registraEvento(getResources().getString(R.string.evento_entrou_adicao_forma_pagamento));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adiciona_cartao);

        modelVeio = new CartoesModel(this);
        model = new AdicionaCartaoCreditoModel(this);

        bindView();
        startFragment();

        pegaExtras();
        configAparencia();

        setTitle(ViewUtils.textFuturaAndFuturaBoldTitle(this, getString(R.string.title_activity_forma_pagamento)));

        configBandeiras();

        configuraBotoesListener();
        configTextWatchers();

        editTextNumeroCartaoContent.addTextChangedListener(MaskEditUtil.mask(editTextNumeroCartaoContent, MaskEditUtil.FORMAT_CRED_CARD, text -> {
            if (fragment != null){
                if (text.length() > 19){
                    text = text.substring(0,19);
                }
                fragment.atualizaNumero(text);
                if (text.length() == 19 || (!text.contains(" ") && text.length() == 16)){
                    fragment.atualizaBandeira(new CartoesModel(AdicionaCartaoActivity.this).getBrand(text));
                } else {
                    fragment.atualizaBandeira("");
                }
            }
            checkEnableButton();
        }));
        addRightImg(findViewById(R.id.editTextNumeroCartaoContent));
        addRightImg(findViewById(R.id.editTextNomeClienteCartaoContent));

        configuraSpin(R.id.spin_meses, model.getListaMeses(), onSpinListener(editTextMesValidadeCartaoContent, model.getListaMeses()));
        configuraSpin(R.id.spin_anos, model.getListaAnos(), onSpinListener(editTextAnoValidadeCartaoContent, model.getListaAnos()));
        apresentaTextCartoes();
    }

    private void configTextWatchers() {
        editTextNomeClienteCartaoContent.addTextChangedListener(MaskEditUtil.textWatcherAfterChangedListener(text -> {
            if (fragment != null){
                fragment.atualizaNome(text);
            }
            checkEnableButton();
        }));
        editTextMesValidadeCartaoContent.addTextChangedListener(onValidadeTextListener());
        editTextAnoValidadeCartaoContent.addTextChangedListener(onValidadeTextListener());
    }

    @NonNull
    private TextWatcher onValidadeTextListener() {
        return MaskEditUtil.textWatcherAfterChangedListener(text -> {
            String validade = "";
            if (!editTextMesValidadeCartaoContent.getText().toString().isEmpty()
                    && !editTextAnoValidadeCartaoContent.getText().toString().isEmpty()){
                validade = editTextMesValidadeCartaoContent.getText().toString() + "\\" + editTextAnoValidadeCartaoContent.getText().toString();
            } else if (!editTextMesValidadeCartaoContent.getText().toString().isEmpty()){
                validade = editTextMesValidadeCartaoContent.getText().toString() + "\\";
            } else if (!editTextAnoValidadeCartaoContent.getText().toString().isEmpty()){
                validade = "\\" + editTextAnoValidadeCartaoContent.getText().toString();
            }

            if (fragment != null) {
                fragment.atualizaValidade(validade);
            }
            checkEnableButton();
        });
    }

    private void configBandeiras() {
        if (!model.isMercadoPago(config.getMeioPagamento().getValor())){
            findViewById(R.id.imgBandeira1).setVisibility(View.GONE);
            findViewById(R.id.imgBandeira2).setVisibility(View.GONE);
            findViewById(R.id.imgBandeira4).setVisibility(View.GONE);
        }
    }

    private void startFragment() {
        fragment = FragmentUtils.startCartaoCredito(getSupportFragmentManager(), R.id.containerCartaoCredito, null);
    }

    private void bindView() {
        editTextNumeroCartaoContent      =  findViewById(R.id.editTextNumeroCartaoContent);
        editTextNomeClienteCartaoContent = findViewById(R.id.editTextNomeClienteCartaoContent);
        editTextMesValidadeCartaoContent = findViewById(R.id.editTextMesValidadeCartaooContent);
        editTextAnoValidadeCartaoContent = findViewById(R.id.editTextAnoValidadeCartaooContent);
        checkBoxSalvarCartao             = findViewById(R.id.checkBoxSalvarCartao);
        textoInfoSelecioneFormaPagamento = findViewById(R.id.textoInfoSelecioneFormaPagamento);
        imagemMeioPagamento              = findViewById(R.id.imagemMeioPagamento);
        backColor                        = findViewById(R.id.background_dados_cartao);
        margem                           = findViewById(R.id.margem_cabecalho);
        btnAvancar                           = findViewById(R.id.botaoAvancarPagamento);
        alterarMeioPagamento                           = findViewById(R.id.alterarMeioPagamento);
        voltar                           = findViewById(R.id.iv_voltar);
    }

    private void configAparencia() {
        if (config != null){
            //margem.setBackgroundColor(getResources().getColor(config.getIdCorBarra()));
            //backColor.setBackgroundColor(getResources().getColor(config.getIdCorBarra()));
            imagemMeioPagamento.setBackground(getDrawable(config.getIdImagem()));
        }
    }

    private void pegaExtras() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            config = (MeioPagamentoConfig) bundle.getSerializable(getResources().getString(R.string.bundle_key_config));
        }
    }

    private void configuraSpin(int idSprin, ArrayList<String> lista, AdapterView.OnItemSelectedListener listener){
        Spinner spinner = findViewById(idSprin);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, R.layout.custom_spinner_item, lista);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
        spinner.setOnItemSelectedListener(listener);
    }

    private AdapterView.OnItemSelectedListener onSpinListener(EditText editText, ArrayList<String> lista) {
        return new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //                if (!primeiraSelecao ){
                //                    editTextMesValidadeCartaoContent.setText("");
                //                } else
                if (i == 0) {
                    editText.setText("");
                } else {
                    editText.setText(lista.get(i));
                }
                //                primeiraSelecao = true;
            }

            public void onNothingSelected(AdapterView<?> adapterView) {return;}
        };
    }

    private void addRightImg(EditText textField) {
        textField.setOnFocusChangeListener((v, hasFocus) -> {
            /* When focus is lost check that the text field
             * has valid values.
             */
            if (!hasFocus) {
                if (v.getClass() == MaskedEditText.class) {
                    if (((MaskedEditText) v).getUnmaskedText().toString().equals(getResources().getString(R.string.string_vazia))) {
                        ((MaskedEditText) v).setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    } else {
                        ((MaskedEditText) v).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_input_ok, 0);
                    }
                } else {
                    if (((EditText) v).getText().toString().equals(getResources().getString(R.string.string_vazia))) {
                        ((EditText) v).setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    } else {
                        ((EditText) v).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_input_ok, 0);
                    }
                }
            }
        });
    }

    private void configuraBotoesListener() {
        btnAvancar.setOnClickListener(view -> {
            if(!camposInvalidos()){
//                showLoading();
//                model.buscaMeioPagamento(config.getMeioPagamento().getValor(), onMeioPagamentoListener());

                Intent intent = IntentUtil.getIntentOrigemDestino(AdicionaCartaoActivity.this, DigitarCvvActivity.class);
                intent.putExtra(DigitarCvvActivity.CARTAO_NOVO, getCartaoCredito());
                intent.putExtra(DigitarCvvActivity.MEIO, config);
                startActivity(intent);
            } else {
                handleMsgError();
            }
        });

        alterarMeioPagamento.setOnClickListener( v -> finish());
        voltar.setOnClickListener(v -> finish());

        btnAvancar.setEnabled(camposInvalidos());
    }

    private CartaoCreditoConfig getCartaoCredito() {
        return new CartaoCreditoConfig(modelVeio.getBrand(editTextNumeroCartaoContent.getText().toString()),
                                       editTextNumeroCartaoContent.getText().toString(),
                                       editTextNomeClienteCartaoContent.getText().toString(),
                                       editTextMesValidadeCartaoContent.getText().toString() + "//"+ editTextAnoValidadeCartaoContent.getText().toString(),
                                       checkBoxSalvarCartao.isChecked());
    }

    private OnSilceListener<MeioPagamentoDTO> onMeioPagamentoListener() {
        return new OnSilceListener<MeioPagamentoDTO>() {
            @Override
            public void success(MeioPagamentoDTO payload) {
                if (modelVeio.isMercadoPago(config.getMeioPagamento().getValor())){
                    realizaCompraMercadoPago();
                } else {
                    realizaCompraRecargaPay();
                }
            }

            @Override
            public void error(VolleyError error) {
                AlertDialogUtils.dismiss();
            }
        };
    }

    private void realizaCompraRecargaPay() {
        modelVeio.recuperaTokenApostadorRecargaPay(onRecuperaTokenListener());
    }

    private OnSilceListener<String> onRecuperaTokenListener() {
        return new OnSilceListener<String>() {
            @Override
            public void success(String token) {
                // chama servico para recuperar informacoes pro recargapay
                modelVeio.buscaDadosPessoais(onDadosPessoaisListener(token));
            }

            @Override
            public void error(VolleyError error) {
                AlertDialogUtils.dismiss();
            }
        };
    }

    private OnSilceListener<ApostadorDTO> onDadosPessoaisListener(String token) {
        return new OnSilceListener<ApostadorDTO>() {
            @Override
            public void success(ApostadorDTO payload) {
                modelVeio.realizaCompraRecargaPayNovoCartao(token, true, getCartaoRecargaPay(payload), onRecargaPayListener());
            }

            @Override
            public void error(VolleyError error) {
                AlertDialogUtils.dismiss();
            }
        };
    }

    private OnCompraAsync onRecargaPayListener() {
        return new OnCompraAsync() {
            @Override
            public void successCompra() {
                onCompraSuccess();
            }

            @Override
            public void redirectCompra(CompraAsyncResponse compraAsyncResponse) {
                onCompraRedirect(compraAsyncResponse);
            }

            @Override
            public void errorCompra(VolleyError volleyError) {
                onCompraError(volleyError);
            }
        };
    }

    private CartaoRecargaPayDTO getCartaoRecargaPay(ApostadorDTO dadosPessoais) {
        CartaoRecargaPayDTO cartao = new CartaoRecargaPayDTO();
        cartao.setType(RecargaPayConnection.CARTAO_TYPE);
        cartao.setBrand(modelVeio.getBrand(editTextNumeroCartaoContent.getText().toString()));
        cartao.setNumber(editTextNumeroCartaoContent.getText().toString());

        CardHolderRecargaPay cardHolder = modelVeio.getCardHolderRecargaPay(dadosPessoais);
        cartao.setCardHolder(cardHolder);

        DataExpiracao dataExpiracao = modelVeio.getDataExpiracao(editTextMesValidadeCartaoContent.getText().toString(), editTextAnoValidadeCartaoContent.getText().toString());
        cartao.setDataExpiracao(dataExpiracao);

        cartao.setShouldSave(checkBoxSalvarCartao.isChecked());
        return cartao;
    }

    private void realizaCompraMercadoPago() {
        CardToken cardToken = new CardToken(editTextNumeroCartaoContent.getText().toString(),
                                            Integer.valueOf(editTextMesValidadeCartaoContent.getText().toString()),
                                            Integer.valueOf(editTextAnoValidadeCartaoContent.getText().toString()),
                                            "",
                                            editTextNomeClienteCartaoContent.getText().toString(),
                                            getResources().getString(R.string.cpf),
                                            DadosUsuarioBO.obterCpf());
        String requestBody = modelVeio.preencheDadosPagamento(cardToken);
        String bin = editTextNumeroCartaoContent.getText().toString().replace(getResources().getString(R.string.espaco_em_branco),
                                                                              getResources().getString(R.string.string_vazia))
                                                .substring(0, 7);

        AppCenterManager.registraEventoDeviceMP(getResources().getString(R.string.app_center_mp_sem_cartao), requestBody);
        String salvarCartao = getSalvarCartao();
        modelVeio.realizaCompraMercadoPago(bin, requestBody, salvarCartao, true, onMercadoPagoListener());
    }

    @NonNull
    private String getSalvarCartao() {
        return checkBoxSalvarCartao.isChecked() ? "true" : "false";
    }

    private OnCompraAsync onMercadoPagoListener() {
        return new OnCompraAsync() {
            @Override
            public void successCompra() {
                onCompraSuccess();
            }

            @Override
            public void redirectCompra(CompraAsyncResponse compraAsyncResponse) {
                onCompraRedirect(compraAsyncResponse);
            }

            @Override
            public void errorCompra(VolleyError volleyError) {
                onCompraError(volleyError);
            }
        };
    }

    private void onCompraError(VolleyError volleyError) {
        RedirectNetwork.checkRedirectCompraAsync(volleyError, AdicionaCartaoActivity.this);
        AlertDialogUtils.dismiss();
    }

    private void onCompraRedirect(CompraAsyncResponse compraAsyncResponse) {
        RedirectNetwork.checkRedirectSucesso(compraAsyncResponse.getRedirect(), AdicionaCartaoActivity.this);
        AlertDialogUtils.dismiss();
    }

    private void onCompraSuccess() {
        new CompraModel(AdicionaCartaoActivity.this).salvaUltimaCompra(CarrinhoSingleton.getInstance().getCarrinho().getValorTotal(),new Date());
        AlertDialogUtils.dismiss();
        if (checkBoxSalvarCartao.isChecked()) {
            AppCenterManager.registraEvento(getResources().getString(R.string.evento_efetuou_adicao_forma_pagamento_sucesso));
        }
        AppCenterManager.registraEvento(getResources().getString(R.string.evento_efetuou_pagamento_sucesso));
        CarrinhoSingleton.getInstance().setCarrinho(new CarrinhoDTO());
        Intent activity = new Intent(AdicionaCartaoActivity.this, ConfirmacaoPagamentoActivity.class);
        startActivity(activity);
    }

    private void checkEnableButton(){
        if (camposInvalidos()){
            btnAvancar.setEnabled(false);
            btnAvancar.setBackgroundColor(getResources().getColor(R.color.cinzaDisable));
        } else {
            btnAvancar.setEnabled(true);
            btnAvancar.setBackgroundColor(getResources().getColor(R.color.verdeazul));
        }
    }

    private boolean camposInvalidos(){
        boolean invalido;
        invalido = (editTextAnoValidadeCartaoContent.getText().toString().trim().equals(getResources().getString(R.string.string_vazia)) ||
                editTextMesValidadeCartaoContent.getText().toString().trim().equals(getResources().getString(R.string.string_vazia)) ||
                editTextNumeroCartaoContent.getText().toString().trim().equals(getResources().getString(R.string.string_vazia)) ||
                editTextNomeClienteCartaoContent.getText().toString().trim().equals(getResources().getString(R.string.string_vazia)));
        String textNumeroCartao = editTextNumeroCartaoContent.getText().toString().replace(getResources().getString(R.string.espaco_em_branco), getResources().getString(R.string.string_vazia));
        if(textNumeroCartao.length() < 16){
            invalido =  true ;
        }

        return invalido;
    }

    private void handleMsgError(){
        String errorMessage = getString(R.string.msg_campos_em_branco);
        boolean invalido = camposInvalidos();

        String textNumeroCartao = editTextNumeroCartaoContent.getText().toString().replace(getResources().getString(R.string.espaco_em_branco), getResources().getString(R.string.string_vazia));
        if(textNumeroCartao.length() < 15){
            invalido = true;
            errorMessage = getString(R.string.mercado_pago_numero_cartao_Incorreto);
        }


        if(invalido){
            ViewUtils.alertTitleButton(AdicionaCartaoActivity.this,
                                       R.string.label_atencao, errorMessage,
                                       AdicionaCartaoActivity.this.getResources().getString(R.string.ok));
        }
    }

    private void apresentaTextCartoes() {
        Bundle bundle = getIntent().getExtras();
        Boolean isPossiuCartao = bundle.getBoolean(getResources().getString(R.string.textoInfoSelecioneFormaPagamento));
        if (isPossiuCartao) {
            textoInfoSelecioneFormaPagamento.setVisibility(View.GONE);
        } else {
            //apresentaAlertaPreCompra();
        }
    }

    private void apresentaAlertaPreCompra() {
        ViewUtils.alertTitleCustomPositiveListener(AdicionaCartaoActivity.this,
                                                   "Cancelar", "Confirmar",
                                                   AdicionaCartaoActivity.this.getResources()
                                                                              .getString(R.string.label_pre_pagamento)
                                                                              .replace("{valorCompra}",
                                                                                       ViewUtils.getMoedaFormat(CarrinhoSingleton.getInstance().getCarrinho().getValorTotal())),
                                                   (dialog, which) -> {
            AlertDialogUtils.dismiss();
            dialog.dismiss();
        }, (dialog, which) -> {
            finish();
        } );
    }

    private void showLoading(){
        runOnUiThread(() -> AlertDialogUtils.show(AdicionaCartaoActivity.this));
    }
}