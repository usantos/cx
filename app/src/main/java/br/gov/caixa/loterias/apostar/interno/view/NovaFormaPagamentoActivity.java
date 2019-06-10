package br.gov.caixa.loterias.apostar.interno.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.pinball83.maskededittext.MaskedEditText;
import com.mercadopago.callbacks.Callback;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.CardToken;
import com.mercadopago.model.Token;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

import br.gov.caixa.loterias.apostar.interno.BuildConfig;
import br.gov.caixa.loterias.apostar.interno.R;
import br.gov.caixa.loterias.apostar.interno.model.bean.MeioPagamento;
import br.gov.caixa.loterias.apostar.interno.model.bean.ParametrosPagamento;
import br.gov.caixa.loterias.apostar.interno.model.bo.ApostaBO;
import br.gov.caixa.loterias.apostar.interno.model.bo.ApostaBO_;
import br.gov.caixa.loterias.apostar.interno.model.bo.DadosUsuarioBO;
import br.gov.caixa.loterias.apostar.interno.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostar.interno.model.bo.RequestListener;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.RetornoPadraoDTO;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.internals.CarrinhoSingleton;
import br.gov.caixa.loterias.apostar.interno.utils.FirebaseManager;
import br.gov.caixa.loterias.apostar.interno.utils.MaskEditUtil;
import br.gov.caixa.loterias.apostar.interno.utils.ViewUtils;
import br.gov.caixa.loterias.apostar.interno.view.custom.LoadingViewLoterias;

public class NovaFormaPagamentoActivity extends SettingToolbarActivity {

    private EditText editTextNumeroCartaoContent, editTextNomeClienteCartaoContent, editTextCodigoSegurancaContent, editTextMesValidadeCartaooContent, editTextAnoValidadeCartaooContent;

    private AlertDialog alertDialog;

    private CheckBox checkBoxSalvarCartao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ViewUtils.configuraStatusBarGradientLayout(this, getWindow());
        FirebaseManager.registraEvento(this ,"ENTROU_ADICAO_FORMA_DE_PAGAMENTO");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nova_forma_pagamento);

        recebendoInformacoes();

        editTextNumeroCartaoContent = (EditText) findViewById(R.id.editTextNumeroCartaoContent);
        editTextNomeClienteCartaoContent = (EditText) findViewById(R.id.editTextNomeClienteCartaoContent);
        editTextCodigoSegurancaContent = (EditText) findViewById(R.id.editTextCodigoSegurancaContent);
        editTextMesValidadeCartaooContent = (EditText) findViewById(R.id.editTextMesValidadeCartaooContent);
        editTextAnoValidadeCartaooContent = (EditText) findViewById(R.id.editTextAnoValidadeCartaooContent);
        checkBoxSalvarCartao = (CheckBox) findViewById(R.id.checkBoxSalvarCartao);

        setTitle(ViewUtils.textFuturaAndFuturaBoldTitle(this, getString(R.string.title_activity_forma_pagamento)));

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

    private void recebendoInformacoes() {
        try {
            String valor = ViewUtils.getMoedaFormat(CarrinhoSingleton.getInstance().getCarrinho().getValorTotal());
            TextView valorTotalCarrinho = findViewById(R.id.valorTotalCarrinho);
            valorTotalCarrinho.setText(valor);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                        editTextNumeroCartaoContent.getText().toString().equals("") ||
                        editTextCodigoSegurancaContent.getText().toString().equals("") ||
                        editTextNomeClienteCartaoContent.getText().toString().equals("")) {
                    ViewUtils.alertTitleButton(NovaFormaPagamentoActivity.this,
                            R.string.label_atencao,
                            "Informe os dados do cartão.",
                            NovaFormaPagamentoActivity.this.getResources().getString(R.string.ok));
                    return;
                }
                CardToken cardToken = new CardToken(editTextNumeroCartaoContent.getText().toString(),
                        Integer.valueOf(editTextMesValidadeCartaooContent.getText().toString()),
                        Integer.valueOf(editTextAnoValidadeCartaooContent.getText().toString()),
                        editTextCodigoSegurancaContent.getText().toString(),
                        editTextNomeClienteCartaoContent.getText().toString(),
                        "CPF",
                        DadosUsuarioBO.obterCpf());

                    createTokenAsync(cardToken);
            }
        });
    }

    private void createTokenAsync(CardToken cardToken) {
        alertDialog = LoadingViewLoterias.show(this);
        MercadoPago mMercadoPago = new MercadoPago.Builder()
                .setContext(this)
                .setPublicKey(BuildConfig.PUBLIC_KEY_MP)
                .build();

        mMercadoPago.createToken(cardToken, new Callback<Token>() {
            @Override
            public void success(Token token) {
//                Log.d("token", token.getId());
                callWebServicePayment_method(token.getId());
            }

            @Override
            public void failure(ApiException apiException) {
                //TODO: Manage API Failure
                alertDialog.dismiss();
                ViewUtils.alert(NovaFormaPagamentoActivity.this, apiException.getMessage());
            }
        });
    }

    private void callWebserviceRegistraAposta(String tokenPagamento, String payment_method_id) {

        ParametrosPagamento parametroPagamento = new ParametrosPagamento(tokenPagamento, String.valueOf(checkBoxSalvarCartao.isChecked()), payment_method_id);
        MeioPagamento meioPagamento = new MeioPagamento(1, parametroPagamento);

        ApostaBO apostaBO = ApostaBO_.getInstance_(NovaFormaPagamentoActivity.this);
        apostaBO.postRegistrarApostaCarrinho(meioPagamento, new RequestListener<RetornoPadraoDTO>() {
            @Override
            public void onResponse(RetornoPadraoDTO response) {
                alertDialog.dismiss();

                if (checkBoxSalvarCartao.isChecked()) {
                    FirebaseManager.registraEvento(NovaFormaPagamentoActivity.this ,"EFETUOU_ADICAO_FORMA_DE_PAGAMENTO_SUCESSO");
                }
                FirebaseManager.registraEvento(NovaFormaPagamentoActivity.this ,"EFETUOU_PAGAMENTO_SUCESSO");

                if (response.getRedirect() != null) {
                    RedirectNetwork.checkRedirectSucesso(response.getRedirect(), NovaFormaPagamentoActivity.this);
                } else {
                    Intent activity = new Intent(NovaFormaPagamentoActivity.this, ConfirmacaoPagamentoActivity.class);
                    startActivity(activity);
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                alertDialog.dismiss();

                try {
                    String mensagem = new String(error.networkResponse.data);
                    if (mensagem.contains("{")) {
                        JSONObject jsonObject = new JSONObject(mensagem);
                        ViewUtils.alert(NovaFormaPagamentoActivity.this, jsonObject.getString("mensagem"));
                    } else {
                        RedirectNetwork.checkRedirect(error, NovaFormaPagamentoActivity.this);
                    }
                } catch (Exception e) {
                    RedirectNetwork.checkRedirect(error, NovaFormaPagamentoActivity.this);
                }

            }
        });
    }

    private void callWebServicePayment_method(final String token) {
        String bin = editTextNumeroCartaoContent.getText().toString().substring(0, 7);
        bin = bin.replace(" ", "");
        String url = BuildConfig.MP_PAYMENT_URL + "bin=" + bin + "&public_key=" + BuildConfig.PUBLIC_KEY_MP;
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Response: ", response.toString());
                try {
                    //Do it with this it will work
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject responseObj = new JSONObject();
                    responseObj = jsonArray.getJSONObject(0);
                    String payment_method_id = responseObj.getString("payment_method_id");
                    String thumbnail = (responseObj.getJSONObject("issuer")).getString("thumbnail");
                    callWebserviceRegistraAposta(token, payment_method_id);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.d("volleyError", "volleyError: " + volleyError.toString());
            }
        });

        RequestQueue rQueue = Volley.newRequestQueue(NovaFormaPagamentoActivity.this);
        rQueue.add(request);
    }

}

