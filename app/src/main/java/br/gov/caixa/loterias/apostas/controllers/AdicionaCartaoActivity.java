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

import androidx.appcompat.widget.AppCompatImageView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.android.volley.VolleyError;
import com.github.pinball83.maskededittext.MaskedEditText;
import com.mercadolibre.android.device.sdk.DeviceSDK;
import com.mercadopago.model.CardToken;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.DadosUsuarioBO;
import br.gov.caixa.loterias.apostas.model.bo.MercadoPagoConnection;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CompraAsyncResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.CarrinhoSingleton;
import br.gov.caixa.loterias.apostas.model.model.CompraModel;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.AppCenterManager;
import br.gov.caixa.loterias.apostas.utils.CompraUtil;
import br.gov.caixa.loterias.apostas.utils.MaskEditUtil;
import br.gov.caixa.loterias.apostas.utils.MeioPagamentoUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.config.MeioPagamentoConfig;
import br.gov.caixa.loterias.apostas.view.listener.OnCompraAsync;

public class AdicionaCartaoActivity extends SettingToolbarActivity implements MercadoPagoConnection.MercadoPagoListener {

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

    private MercadoPagoConnection.MercadoPagoListener listener;

    private String requestBody, bin;

    private CompraModel model;
    private MeioPagamentoConfig config;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ViewUtils.configuraStatusBarGradientLayout(this, getWindow());
        AppCenterManager.registraEvento(getResources().getString(R.string.evento_entrou_adicao_forma_pagamento));
        listener = this;
        model = new CompraModel(AdicionaCartaoActivity.this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adiciona_cartao);
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

        if (MeioPagamentoUtils.isRecargaPay(config.getMeioPagamento().getValor())){
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
                CardToken cardToken = new CardToken(editTextNumeroCartaoContent.getText().toString(),
                                                    Integer.valueOf(editTextMesValidadeCartaoContent.getText().toString()),
                                                    Integer.valueOf(editTextAnoValidadeCartaoContent.getText().toString()),
                                                    editTextCodigoSegurancaContent.getText().toString(),
                                                    editTextNomeClienteCartaoContent.getText().toString(),
                                                    getResources().getString(R.string.cpf),
                                                    DadosUsuarioBO.obterCpf());
                preencheDadosPagamento();
                AppCenterManager.registraEventoDeviceMP(getResources().getString(R.string.app_center_mp_sem_cartao), requestBody);
                MercadoPagoConnection.callWebServicePayment_method(bin, requestBody, this, listener);
            }
        });

        alterarMeioPagamento.setOnClickListener( v -> finish());
        voltar.setOnClickListener( v -> finish());
    }

    private void preencheDadosPagamento(){
        JSONObject jsonBody = new JSONObject();
        JSONObject jsonCardHolder = new JSONObject();
        JSONObject jsonIdentification = new JSONObject();
        try {
            jsonIdentification.put("type", "CPF");
            jsonIdentification.put("number", DadosUsuarioBO.obterCpf());
            jsonCardHolder.put("identification",jsonIdentification);
            jsonCardHolder.put("name",editTextNomeClienteCartaoContent.getText().toString());

            jsonBody.put("card_number", editTextNumeroCartaoContent.getText().toString().replace(getResources().getString(R.string.espaco_em_branco), getResources().getString(R.string.string_vazia)));
            jsonBody.put("expiration_year",Integer.valueOf(editTextAnoValidadeCartaoContent.getText().toString()));
            jsonBody.put("expiration_month", Integer.valueOf(editTextMesValidadeCartaoContent.getText().toString()));
            jsonBody.put("security_code", editTextCodigoSegurancaContent.getText().toString());
            jsonBody.put("cardholder", jsonCardHolder);
            jsonBody.put("device", DeviceSDK.getInstance().getInfoAsJsonString());
        }catch (Exception e) {
            e.printStackTrace();
        }

        bin = editTextNumeroCartaoContent.getText().toString().replace(getResources().getString(R.string.espaco_em_branco), getResources().getString(R.string.string_vazia)).substring(0, 7);
        requestBody = jsonBody.toString();
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

    private void callWebserviceRegistraApostaAsync(String tokenPagamento, String payment_method_id) {
        if(!AlertDialogUtils.isShow()){
            showLoading();
        }

        CompraUtil.registraApostaAsync(tokenPagamento, payment_method_id, getResources().getString(R.string.string_vazia) ,String.valueOf(checkBoxSalvarCartao.isChecked()),onCompraAsyncListener());
    }

    private OnCompraAsync onCompraAsyncListener() {
        return new OnCompraAsync() {

            @Override
            public void successCompra() {
                model.salvaUltimaCompra(CarrinhoSingleton.getInstance().getCarrinho().getValorTotal(),new Date());
                sucessoCompraAsync();
            }

            @Override
            public void redirectCompra(CompraAsyncResponse compraAsyncResponse) {
                redirectCompraAsync(compraAsyncResponse);
            }

            @Override
            public void errorCompra(VolleyError volleyError) {
                errorCompraAsync(volleyError);
            }
        };
    }

    private void sucessoCompraAsync() {
        AlertDialogUtils.dismiss();
        if (checkBoxSalvarCartao.isChecked()) {
            AppCenterManager.registraEvento(getResources().getString(R.string.evento_efetuou_adicao_forma_pagamento_sucesso));
        }
        AppCenterManager.registraEvento(getResources().getString(R.string.evento_efetuou_pagamento_sucesso));
        CarrinhoSingleton.getInstance().setCarrinho(new CarrinhoDTO());
        Intent activity = new Intent(AdicionaCartaoActivity.this, ConfirmacaoPagamentoActivity.class);
        startActivity(activity);
    }

    private void redirectCompraAsync(CompraAsyncResponse compraAsyncResponse) {
        RedirectNetwork.checkRedirectSucesso( compraAsyncResponse.getRedirect(), AdicionaCartaoActivity.this);
        AlertDialogUtils.dismiss();
    }

    private void errorCompraAsync(VolleyError error) {
        RedirectNetwork.checkRedirectCompraAsync( error, AdicionaCartaoActivity.this);
        AlertDialogUtils.dismiss();
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

    @Override
    public void onSucesso(String cardId, String payment_method_id, boolean luhn_validation) {
        if (luhn_validation){
            callWebserviceRegistraApostaAsync(cardId, payment_method_id);
        } else {
            AlertDialogUtils.dismiss();
            apresentaModal(getString(R.string.numero_cartao_invalido));
        }
    }

    @Override
    public void onTokenFailure(String msgError) {
        AlertDialogUtils.dismiss();
        apresentaModal(msgError);
    }

    @Override
    public void onPaymentFailure(String msgError) {
        AlertDialogUtils.dismiss();
        apresentaModal(msgError);
    }

    private void apresentaModal(String msg) {
        ViewUtils.alertTitleButton(AdicionaCartaoActivity.this, R.string.label_atencao, msg,
								   AdicionaCartaoActivity.this.getResources().getString(R.string.ok));
    }
}