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

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.DadosUsuarioBO;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostadorDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CompraAsyncResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.MeioPagamentoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.CarrinhoSingleton;
import br.gov.caixa.loterias.apostas.model.model.CartoesModel;
import br.gov.caixa.loterias.apostas.model.model.CompraModel;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.AppCenterManager;
import br.gov.caixa.loterias.apostas.utils.MaskEditUtil;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.config.MeioPagamentoConfig;
import br.gov.caixa.loterias.apostas.view.listener.OnCompraAsync;

public class AdicionaCartaoActivity extends SettingToolbarActivity{

    private EditText editTextNumeroCartaoContent, editTextNomeClienteCartaoContent, editTextCodigoSegurancaContent, editTextMesValidadeCartaoContent,  editTextAnoValidadeCartaoContent;
    private TextView textoInfoSelecioneFormaPagamento, alterarMeioPagamento;
    private CheckBox checkBoxSalvarCartao, checkBoxCiente;
    private AppCompatImageView imagemMeioPagamento, voltar;
    private ConstraintLayout backColor;
    private View margem;
    private Button btnAvancar;

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

        configuraSpin(R.id.spin_meses, model.getListaMeses(), onSpinListener(editTextMesValidadeCartaoContent, model.getListaMeses()));
        configuraSpin(R.id.spin_anos, model.getListaAnos(), onSpinListener(editTextAnoValidadeCartaoContent, model.getListaAnos()));
        apresentaTextCartoes();
    }

    private void bindView() {
        editTextNumeroCartaoContent      =  findViewById(R.id.editTextNumeroCartaoContent);
        editTextNomeClienteCartaoContent = findViewById(R.id.editTextNomeClienteCartaoContent);
        editTextCodigoSegurancaContent   = findViewById(R.id.editTextCodigoSegurancaContent);
        editTextMesValidadeCartaoContent = findViewById(R.id.editTextMesValidadeCartaooContent);
        editTextAnoValidadeCartaoContent = findViewById(R.id.editTextAnoValidadeCartaooContent);
        checkBoxSalvarCartao             = findViewById(R.id.checkBoxSalvarCartao);
        checkBoxCiente                   = findViewById(R.id.checkBoxCiente);
        textoInfoSelecioneFormaPagamento = findViewById(R.id.textoInfoSelecioneFormaPagamento);
        imagemMeioPagamento              = findViewById(R.id.imagemMeioPagamento);
        backColor                        = findViewById(R.id.background_dados_cartao);
        margem                           = findViewById(R.id.margem_cabecalho);
        btnAvancar                       = findViewById(R.id.botaoAvancarPagamento);
        alterarMeioPagamento             = findViewById(R.id.alterarMeioPagamento);
        voltar                           = findViewById(R.id.iv_voltar);

        btnAvancar.setEnabled(false);
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

    private void configuraSpin(int idSprin, ArrayList<String> lista, AdapterView.OnItemSelectedListener listener){
        Spinner spinner = findViewById(idSprin);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, R.layout.custom_spinner_item, lista);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
        spinner.setOnItemSelectedListener(listener);
    }

    private AdapterView.OnItemSelectedListener onSpinListener(EditText editText, ArrayList<String> lista){
        return new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i == 0){
                    editText.setText("");
                } else {
                    editText.setText(lista.get(i));
                }
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
        btnAvancar.setText(ViewUtils.textFuturaAndFuturaBold(this, "_Pagar agora_"));
        btnAvancar.setOnClickListener(view -> {
            if (checkBoxCiente.isChecked()){
                if(!camposInvalidos()){
                    showLoading();
                    model.buscaMeioPagamento(config.getMeioPagamento().getValor(), onMeioPagamentoListener());
                }
            }
        });

        alterarMeioPagamento.setOnClickListener( v -> finish());
        voltar.setOnClickListener(v -> finish());
        checkBoxCiente.setOnClickListener(v -> {
            if (checkBoxCiente.isChecked()){
                btnAvancar.setEnabled(true);
                btnAvancar.setBackgroundColor(getResources().getColor(R.color.verdeazul));
            } else {
                btnAvancar.setEnabled(false);
                btnAvancar.setBackgroundColor(getResources().getColor(R.color.cinza_salvar_cartao_desabilitado));
            }
        });
    }

    private OnSilceListener<MeioPagamentoDTO> onMeioPagamentoListener() {
        return new OnSilceListener<MeioPagamentoDTO>() {
            @Override
            public void success(MeioPagamentoDTO payload) {
                if (model.isMercadoPago(config.getMeioPagamento().getValor())){
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
                model.realizaCompraRecargaPayNovoCartao(token, true,
                                                        model.getCartaoRecargaPay(payload,
                                                                                  editTextNumeroCartaoContent.getText().toString(),
                                                                                  editTextMesValidadeCartaoContent.getText().toString(),
                                                                                  editTextAnoValidadeCartaoContent.getText().toString(),
                                                                                  editTextCodigoSegurancaContent.getText().toString(),
                                                                                  checkBoxSalvarCartao.isChecked()),
                                                        onRecargaPayListener());
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
        String salvarCartao = getSalvarCartao();
        model.realizaCompraMercadoPago(bin, requestBody, salvarCartao,true, onMercadoPagoListener());
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
        }
    }

    private void showLoading(){
        runOnUiThread(() -> AlertDialogUtils.show(AdicionaCartaoActivity.this));
    }
}