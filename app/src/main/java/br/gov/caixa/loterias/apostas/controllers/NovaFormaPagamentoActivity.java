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

import com.android.volley.VolleyError;
import com.github.pinball83.maskededittext.MaskedEditText;
import com.mercadolibre.android.device.sdk.DeviceSDK;
import com.mercadopago.model.CardToken;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.DadosUsuarioBO;
import br.gov.caixa.loterias.apostas.model.bo.MercadoPagoConnection;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CompraAsyncResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RetornoCartao;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.CarrinhoSingleton;
import br.gov.caixa.loterias.apostas.utils.AppCenterManager;
import br.gov.caixa.loterias.apostas.utils.CompraUtil;
import br.gov.caixa.loterias.apostas.utils.DialogUtils;
import br.gov.caixa.loterias.apostas.utils.FragmentUtils;
import br.gov.caixa.loterias.apostas.utils.MaskEditUtil;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.fragment.SomadorCarrinhoFragment;
import br.gov.caixa.loterias.apostas.view.listener.OnCompraAsync;

public class NovaFormaPagamentoActivity extends SettingToolbarActivity implements MercadoPagoConnection.MercadoPagoListener {

    private EditText editTextNumeroCartaoContent, editTextNomeClienteCartaoContent, editTextCodigoSegurancaContent, editTextMesValidadeCartaoContent, editTextAnoValidadeCartaoContent;

    private TextView textoInfoSelecioneFormaPagamento;

    private CheckBox checkBoxSalvarCartao;

    private ArrayList<RetornoCartao> listaCartoes;

    private ArrayList<String> listaMeses;

    private Boolean primeiraSelecao = false;

    private MercadoPagoConnection.MercadoPagoListener listener;

    private String requestBody, bin;

    private TextView valorTotalCarrinho;

    //fragments
    private SomadorCarrinhoFragment somadorCarrinhoFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ViewUtils.configuraStatusBarGradientLayout(this, getWindow());
        AppCenterManager.registraEvento(getResources().getString(R.string.evento_entrou_adicao_forma_pagamento));
        listener = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nova_forma_pagamento);

        recebendoInformacoes();


        editTextNumeroCartaoContent =  findViewById(R.id.editTextNumeroCartaoContent);
        editTextNomeClienteCartaoContent = findViewById(R.id.editTextNomeClienteCartaoContent);
        editTextCodigoSegurancaContent = findViewById(R.id.editTextCodigoSegurancaContent);
        editTextMesValidadeCartaoContent = findViewById(R.id.editTextMesValidadeCartaooContent);
        editTextAnoValidadeCartaoContent = findViewById(R.id.editTextAnoValidadeCartaooContent);
        checkBoxSalvarCartao = findViewById(R.id.checkBoxSalvarCartao);
        textoInfoSelecioneFormaPagamento = findViewById(R.id.textoInfoSelecioneFormaPagamento);


        setTitle(ViewUtils.textFuturaAndFuturaBoldTitle(this, getString(R.string.title_activity_forma_pagamento)));

        configuraBotoesListener();

        editTextNumeroCartaoContent.addTextChangedListener(MaskEditUtil.mask(editTextNumeroCartaoContent, MaskEditUtil.FORMAT_CRED_CARD));
        addRightImg(findViewById(R.id.editTextNumeroCartaoContent));
        addRightImg(findViewById(R.id.editTextNomeClienteCartaoContent));
        addRightImg(findViewById(R.id.editTextCodigoSegurancaContent));

        configuraSpin();
        apresentaTextCartoes();
        FragmentUtils.startSomadorCarrinhoFragment(getSupportFragmentManager(), somadorCarrinhoFragment, R.id.id_fg_somador_carrinho,"TELA ADD CARTAO");

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

    private void recebendoInformacoes() {
        try {
            String valor = ViewUtils.getMoedaFormat(CarrinhoSingleton.getInstance().getCarrinho().getValorTotal());
            valorTotalCarrinho = findViewById(R.id.valorTotalCarrinho);
            valorTotalCarrinho.setText(valor);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    private ArrayList<String> montaCarrinhoApostaValores(String valores[]) {
        ArrayList<String> valoresTextoDialog = new ArrayList<>();

        Collections.addAll(valoresTextoDialog, valores);

        return valoresTextoDialog;
    }

    private void configuraBotoesListener() {
        ((Button) findViewById(R.id.botaoAvancarPagamento)).setText(ViewUtils.textFuturaAndFuturaBold(this, getString(R.string.apostarAutorizarCobranca)));

        findViewById(R.id.botaoAvancarPagamento).setOnClickListener(view -> {
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
            ViewUtils.alertTitleButton(NovaFormaPagamentoActivity.this,
                                       R.string.label_atencao,errorMessage,
                                       NovaFormaPagamentoActivity.this.getResources().getString(R.string.ok));
        }
        return invalido;
    }

    private void callWebserviceRegistraApostaAsync(String tokenPagamento, String payment_method_id) {
        if(!DialogUtils.isShow()){
            showLoading();
        }
        // novo cartao com opcao de salvar desmarcada ou marcada

        // (SAVE_CARD: self.salvarCartao ? "true" : "false", TOKEN: token, SAVED_CARD: nil, PAYMENT_METHOD_ID: paymentId)) novo cartao

        // public static void registraApostaAsync(final Context context, String tokenPagamento, String payment_method_id, String savedCard, String saveCard, final OnCompraAsync listener) {

        CompraUtil.registraApostaAsync(tokenPagamento, payment_method_id, getResources().getString(R.string.string_vazia) ,String.valueOf(checkBoxSalvarCartao.isChecked()),onCompraAsyncListener());
    }

    private OnCompraAsync onCompraAsyncListener() {
        return new OnCompraAsync() {

            @Override
            public void successCompra() {
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
        DialogUtils.dismiss();
        if (checkBoxSalvarCartao.isChecked()) {
            AppCenterManager.registraEvento(getResources().getString(R.string.evento_efetuou_adicao_forma_pagamento_sucesso));
        }
        AppCenterManager.registraEvento(getResources().getString(R.string.evento_efetuou_pagamento_sucesso));
        CarrinhoSingleton.getInstance().setCarrinho(new CarrinhoDTO());
        Intent activity = new Intent(NovaFormaPagamentoActivity.this, ConfirmacaoPagamentoActivity.class);
        startActivity(activity);
    }

    private void redirectCompraAsync(CompraAsyncResponse compraAsyncResponse) {
        RedirectNetwork.checkRedirectSucesso( compraAsyncResponse.getRedirect(), NovaFormaPagamentoActivity.this);
        DialogUtils.dismiss();
    }

    private void errorCompraAsync(VolleyError error) {
        RedirectNetwork.checkRedirect( error, NovaFormaPagamentoActivity.this );
        DialogUtils.dismiss();
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
        ViewUtils.alertTitleCustomPositiveListener(NovaFormaPagamentoActivity.this, "Cancelar", "Confirmar", NovaFormaPagamentoActivity.this.getResources().getString(R.string.label_pre_pagamento).replace("{valorCompra}", valorTotalCarrinho.getText()), (dialog, which) -> {
            DialogUtils.dismiss();
            dialog.dismiss();
        },(dialog, which) -> {
            finish();
        } );
    }

    private void showLoading(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                DialogUtils.show(NovaFormaPagamentoActivity.this);
            }
        });
    }


    @Override
    public void onSucesso(String cardId, String payment_method_id) {
        callWebserviceRegistraApostaAsync(cardId, payment_method_id);
    }

    @Override
    public void onTokenFailure(String msgError) {
        DialogUtils.dismiss();
        apresentaModal(msgError);
    }

    @Override
    public void onPaymentFailure(String msgError) {
        DialogUtils.dismiss();
        apresentaModal(msgError);
    }

    private void apresentaModal(String msg) {
        ViewUtils.alertTitleButton(NovaFormaPagamentoActivity.this, R.string.label_atencao, msg,
                NovaFormaPagamentoActivity.this.getResources().getString( R.string.ok ) );
    }
}