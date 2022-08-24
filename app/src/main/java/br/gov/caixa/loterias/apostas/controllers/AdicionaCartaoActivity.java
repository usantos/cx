package br.gov.caixa.loterias.apostas.controllers;

import android.content.Intent;
import android.os.Bundle;
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
import java.util.List;

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
import br.gov.caixa.loterias.apostas.model.model.CartoesModel;
import br.gov.caixa.loterias.apostas.model.model.CompraModel;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.AppCenterManager;
import br.gov.caixa.loterias.apostas.utils.DataUtil;
import br.gov.caixa.loterias.apostas.utils.MaskEditUtil;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.config.MeioPagamentoConfig;
import br.gov.caixa.loterias.apostas.view.listener.OnCompraAsync;

public class AdicionaCartaoActivity extends SettingToolbarActivity{

    private EditText editTextNumeroCartaoContent, editTextNomeClienteCartaoContent, editTextCodigoSegurancaContent, editTextMesValidadeCartaoContent, editTextAnoValidadeCartaoContent;
    private TextView textoInfoSelecioneFormaPagamento;
    private CheckBox checkBoxSalvarCartao;
    private AppCompatImageView imagemMeioPagamento, voltar;
    private ConstraintLayout backColor;
    private View margem;
    private TextView alterarMeioPagamento;
    private Button btnAvancar;

    private ArrayList<String> listaMeses;

    private Boolean primeiraSelecao = false;

    private CartoesModel model;
    private MeioPagamentoConfig config;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ViewUtils.configuraStatusBarGradientLayout(this, getWindow());
        AppCenterManager.registraEvento(getResources().getString(R.string.evento_entrou_adicao_forma_pagamento));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adiciona_cartao);

        model = new CartoesModel(this);

        bindView();

        pegaExtras();
        configAparencia();

        setTitle(ViewUtils.textFuturaAndFuturaBoldTitle(this, getString(R.string.title_activity_forma_pagamento)));

        configuraBotoesListener();

        editTextNumeroCartaoContent.addTextChangedListener(MaskEditUtil.mask(editTextNumeroCartaoContent, MaskEditUtil.FORMAT_CRED_CARD));
        addRightImg(findViewById(R.id.editTextNumeroCartaoContent));
        addRightImg(findViewById(R.id.editTextNomeClienteCartaoContent));
        addRightImg(findViewById(R.id.editTextCodigoSegurancaContent));

        configuraSpin();
        apresentaTextCartoes();

        if (model.isRecargaPay(config)){
            findViewById(R.id.containerComboSalvar).setVisibility(View.INVISIBLE);
        }
    }

    private void bindView() {
        editTextNumeroCartaoContent      =  findViewById(R.id.editTextNumeroCartaoContent);
        editTextNomeClienteCartaoContent = findViewById(R.id.editTextNomeClienteCartaoContent);
        editTextCodigoSegurancaContent   = findViewById(R.id.editTextCodigoSegurancaContent);
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
            margem.setBackgroundColor(getResources().getColor(config.getIdCorBarra()));
            backColor.setBackgroundColor(getResources().getColor(config.getIdCorBarra()));
            imagemMeioPagamento.setBackground(getDrawable(config.getIdImagem()));
        }
    }

    private void pegaExtras() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            config = (MeioPagamentoConfig) bundle.getSerializable(getResources().getString(R.string.bundle_key_config));
        }
    }

    private void configuraSpin(){
        Spinner spinner = findViewById(R.id.spin_meses);
        listaMeses = new ArrayList<>();
        listaMeses.add(getString(R.string.title_mesDeValidade));
        for(int x = 1; x <= 12; x++){
            listaMeses.add(x < 10 ? "0" + x: String.valueOf(x));
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, R.layout.custom_spinner_item, listaMeses);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (!primeiraSelecao ){
                    editTextMesValidadeCartaoContent.setText("");
                } else if(i == 0){
                    editTextMesValidadeCartaoContent.setText("");
                } else {
                    editTextMesValidadeCartaoContent.setText(listaMeses.get(i));
                }
                primeiraSelecao = true;
            }

            public void onNothingSelected(AdapterView<?> adapterView) {return;}
        });
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
        btnAvancar.setText(ViewUtils.textFuturaAndFuturaBold(this, getString(R.string.apostarAutorizarCobranca)));
        btnAvancar.setOnClickListener(view -> {
            if(!camposInvalidos()){
                showLoading();
                model.buscaMeioPagamento(config.getMeioPagamento().getValor(), onMeioPagamentoListener());
            }
        });

        alterarMeioPagamento.setOnClickListener( v -> finish());
        voltar.setOnClickListener(v -> finish());
    }

    private OnSilceListener<MeioPagamentoDTO> onMeioPagamentoListener() {
        return new OnSilceListener<MeioPagamentoDTO>() {
            @Override
            public void success(MeioPagamentoDTO payload) {
                if (model.isMercadoPago(config)){
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
        model.recuperaTokenApostadorRecargaPay(onRecuperaTokenListener());
    }

    private OnSilceListener<String> onRecuperaTokenListener() {
        return new OnSilceListener<String>() {
            @Override
            public void success(String token) {
                // chama servico para recuperar informacoes pro recargapay
                model.buscaDadosPessoais(onDadosPessoaisListener(token));
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
                model.realizaCompraRecargaPay(token, getCartaoRecargaPay(payload), onRecargaPayListener());
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
        cartao.setBrand(getBrand());
        cartao.setNumber(editTextNumeroCartaoContent.getText().toString());

        CardHolderRecargaPay cardHolder = getCardHolderRecargaPay(dadosPessoais);
        cartao.setCardHolder(cardHolder);

        DataExpiracao dataExpiracao = getDataExpiracao();
        cartao.setDataExpiracao(dataExpiracao);

        cartao.setSecurityCode(editTextCodigoSegurancaContent.getText().toString());
        return cartao;
    }

    @NonNull
    private DataExpiracao getDataExpiracao() {
        DataExpiracao dataExpiracao = new DataExpiracao();
        dataExpiracao.setMes(Integer.valueOf(editTextMesValidadeCartaoContent.getText().toString()));
        dataExpiracao.setAno(Integer.valueOf(editTextAnoValidadeCartaoContent.getText().toString()));
        return dataExpiracao;
    }

    @NonNull
    private CardHolderRecargaPay getCardHolderRecargaPay(ApostadorDTO dadosPessoais) {
        CardHolderRecargaPay cardHolder = new CardHolderRecargaPay();
        cardHolder.setName(dadosPessoais.getNome());
        cardHolder.setType(RecargaPayConnection.CARDHOLDER_TYPE);
        cardHolder.setEmail(dadosPessoais.getEmail());
        cardHolder.setDocument(dadosPessoais.getCpf());
        cardHolder.setBirthdate(DataUtil.getStringDataFormatted(DataUtil.YYYY_MM_DD, dadosPessoais.getDataNascimento()));
        List<String> phones = new ArrayList<>();
        phones.add("+55999999999");
        cardHolder.setPhones(phones);
        return cardHolder;
    }

    @NonNull
    private String getBrand() {
        String numeroCartao = editTextNumeroCartaoContent.getText().toString();
        if (numeroCartao.startsWith("4")){
            return "VISA";
        } else if (numeroCartao.startsWith("51") || numeroCartao.startsWith("52") ||
                    numeroCartao.startsWith("53") || numeroCartao.startsWith("54") ||
                numeroCartao.startsWith("55")){
            return "MASTERCARD";
        }
        return "";
    }

    private void realizaCompraMercadoPago() {
        CardToken cardToken = new CardToken(editTextNumeroCartaoContent.getText().toString(),
                                            Integer.valueOf(editTextMesValidadeCartaoContent.getText().toString()),
                                            Integer.valueOf(editTextAnoValidadeCartaoContent.getText().toString()),
                                            editTextCodigoSegurancaContent.getText().toString(),
                                            editTextNomeClienteCartaoContent.getText().toString(),
                                            getResources().getString(R.string.cpf),
                                            DadosUsuarioBO.obterCpf());
        String requestBody = model.preencheDadosPagamento(cardToken);
        String bin = editTextNumeroCartaoContent.getText().toString().replace(getResources().getString(R.string.espaco_em_branco),
                                                                              getResources().getString(R.string.string_vazia))
                                                .substring(0, 7);

        AppCenterManager.registraEventoDeviceMP(getResources().getString(R.string.app_center_mp_sem_cartao), requestBody);
        model.realizaCompraMercadoPago(bin, requestBody, true, onMercadoPagoListener());
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

    private boolean camposInvalidos(){
        String errorMessage = getString(R.string.msg_campos_em_branco);
        boolean invalido;
        invalido = (editTextAnoValidadeCartaoContent.getText().toString().trim().equals(getResources().getString(R.string.string_vazia)) ||
                editTextMesValidadeCartaoContent.getText().toString().trim().equals(getResources().getString(R.string.string_vazia)) ||
                editTextNumeroCartaoContent.getText().toString().trim().equals(getResources().getString(R.string.string_vazia)) ||
                editTextCodigoSegurancaContent.getText().toString().trim().equals(getResources().getString(R.string.string_vazia)) ||
                editTextNomeClienteCartaoContent.getText().toString().trim().equals(getResources().getString(R.string.string_vazia)));
        String textNumeroCartao = editTextNumeroCartaoContent.getText().toString().replace(getResources().getString(R.string.espaco_em_branco), getResources().getString(R.string.string_vazia));
        if(textNumeroCartao.length() < 15){
            invalido =  true ;
            errorMessage = getString(R.string.mercado_pago_numero_cartao_Incorreto);
        }
        if(invalido){
            ViewUtils.alertTitleButton(AdicionaCartaoActivity.this,
									   R.string.label_atencao, errorMessage,
									   AdicionaCartaoActivity.this.getResources().getString(R.string.ok));
        }
        return invalido;
    }

    private void apresentaTextCartoes() {
        Bundle bundle = getIntent().getExtras();
        Boolean isPossiuCartao = bundle.getBoolean(getResources().getString(R.string.textoInfoSelecioneFormaPagamento));
        if (isPossiuCartao) {
            textoInfoSelecioneFormaPagamento.setVisibility(View.GONE);
        } else {
            apresentaAlertaPreCompra();
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