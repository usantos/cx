package caixa.gov.br.loterias.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.android.volley.VolleyError;
import com.github.pinball83.maskededittext.MaskedEditText;
import com.mercadopago.callbacks.Callback;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.Card;
import com.mercadopago.model.CardToken;
import com.mercadopago.model.Token;


import java.util.ArrayList;
import java.util.Collections;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.model.bean.MeioPagamento;
import caixa.gov.br.loterias.model.bean.ParametrosPagamento;
import caixa.gov.br.loterias.model.bo.ApostaBO;
import caixa.gov.br.loterias.model.bo.ApostaBO_;
import caixa.gov.br.loterias.model.bo.MensagensNetwork;
import caixa.gov.br.loterias.model.bo.RequestListener;
import caixa.gov.br.loterias.model.bo.silce.dto.RetornoPadraoDTO;
import caixa.gov.br.loterias.utils.MaskEditUtil;
import caixa.gov.br.loterias.utils.ViewUtils;
import caixa.gov.br.loterias.view.custom.LoadingViewLoterias;

public class NovaFormaPagamentoActivity extends SettingToolbarActivity {

    private EditText editTextNumeroCartaoContent, editTextNomeClienteCartaoContent, editTextCodigoSegurancaContent, editTextMesValidadeCartaooContent,editTextAnoValidadeCartaooContent;

    private AlertDialog alertDialog;

    private CheckBox checkBoxSalvarCartao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ViewUtils.configuraStatusBarGradientLayout(this, getWindow());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nova_forma_pagamento);

        editTextNumeroCartaoContent = (EditText) findViewById(R.id.editTextNumeroCartaoContent);
        editTextNomeClienteCartaoContent = (EditText) findViewById(R.id.editTextNomeClienteCartaoContent);
        editTextCodigoSegurancaContent = (EditText) findViewById(R.id.editTextCodigoSegurancaContent);
        editTextMesValidadeCartaooContent = (EditText) findViewById(R.id.editTextMesValidadeCartaooContent);
        editTextAnoValidadeCartaooContent = (EditText) findViewById(R.id.editTextAnoValidadeCartaooContent);
        checkBoxSalvarCartao = (CheckBox) findViewById(R.id.checkBoxSalvarCartao);

        setTitle(ViewUtils.textFuturaAndFuturaBold(this, getString(R.string.title_activity_forma_pagamento)));

        String[] values = new String[]{"01", "02", "02", "02", "02", "02", "02", "02", "02"};

//        RecyclerView recyclerView = findViewById(R.id.novaFormaPagamentoRecyclerView);
//        LinearLayoutManager layoutManager
//                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
//
//
//        recyclerView.setLayoutManager(layoutManager);
//
//        recyclerView.setAdapter(new NovaFormaPagamentoBandeiraAdapter(montaCarrinhoApostaValores(values), this));

        configuraBotoesListener();

        editTextNumeroCartaoContent.addTextChangedListener(MaskEditUtil.mask(editTextNumeroCartaoContent, MaskEditUtil.FORMAT_CRED_CARD));
        addRightImg((EditText) findViewById(R.id.editTextNumeroCartaoContent));
        addRightImg((EditText) findViewById(R.id.editTextNomeClienteCartaoContent));
        addRightImg((EditText) findViewById(R.id.editTextCodigoSegurancaContent));
    }

    private void addRightImg(EditText textField) {
        textField.setOnFocusChangeListener(new EditText.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
            /* When focus is lost check that the text field
            * has valid values.
            */
                if (!hasFocus) {
                    if (v.getClass() == MaskedEditText.class) {
                        if (((MaskedEditText) v).getUnmaskedText().toString().equals("")) {
                            ((MaskedEditText) v).setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                        } else {
                            ((MaskedEditText) v).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_input_ok, 0);
                        }
                    } else {
                        if (((EditText) v).getText().toString().equals("")) {
                            ((EditText) v).setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                        } else {
                            ((EditText) v).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_input_ok, 0);
                        }
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

        findViewById(R.id.botaoAvancarPagamento).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editTextAnoValidadeCartaooContent.getText().toString().equals("") ||
                        editTextMesValidadeCartaooContent.getText().toString().equals("") ||
                                        editTextNumeroCartaoContent.getText().toString().equals("")||
                                        editTextCodigoSegurancaContent.getText().toString().equals("") ||
                                        editTextNomeClienteCartaoContent.getText().toString().equals("")){
                                ViewUtils.alert(NovaFormaPagamentoActivity.this, "Informe os dados do cartão.");
                                return;
                }
                CardToken cardToken = new CardToken(editTextNumeroCartaoContent.getText().toString(),
                        Integer.valueOf(editTextMesValidadeCartaooContent.getText().toString()),
                        Integer.valueOf(editTextAnoValidadeCartaooContent.getText().toString()),
                        editTextCodigoSegurancaContent.getText().toString(),
                        editTextNomeClienteCartaoContent.getText().toString(),
                        "",
                        "");



                    createTokenAsync(cardToken);
//                }
            }
        });
    }

    private void createTokenAsync(CardToken cardToken) {
        alertDialog =  LoadingViewLoterias.show(this);
        MercadoPago mMercadoPago = new MercadoPago.Builder()
                .setContext(this)
                .setPublicKey("TEST-946a3077-5cf1-446c-bab3-e3a266357e9f")
                .build();
        mMercadoPago.createToken(cardToken, new Callback<Token>() {
            @Override
            public void success(Token token) {
//                Log.d("token", token.getId());
                callWebserviceRegistraAposta(token.getId());

            }

            @Override
            public void failure(ApiException apiException) {
                //TODO: Manage API Failure
                alertDialog.dismiss();
                ViewUtils.alert(NovaFormaPagamentoActivity.this, apiException.getMessage());
            }
        });
    }

    private void callWebserviceRegistraAposta(String tokenPagamento) {

        ParametrosPagamento parametroPagamento = new ParametrosPagamento(tokenPagamento, String.valueOf(checkBoxSalvarCartao.isChecked()), "visa");
        MeioPagamento meioPagamento = new MeioPagamento(1, parametroPagamento);

        ApostaBO apostaBO = ApostaBO_.getInstance_(NovaFormaPagamentoActivity.this);
        apostaBO.postRegistrarApostaCarrinho(meioPagamento, new RequestListener<RetornoPadraoDTO>() {
            @Override
            public void onResponse(RetornoPadraoDTO response) {
                alertDialog.dismiss();
                Intent activity = new Intent(NovaFormaPagamentoActivity.this, ConfirmacaoPagamentoActivity.class);
                startActivity(activity);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                alertDialog.dismiss();
                ViewUtils.alert(NovaFormaPagamentoActivity.this, MensagensNetwork.setMensagem(error, getApplicationContext()));
            }
        });
    }

}
