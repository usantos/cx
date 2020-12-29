package br.gov.caixa.loterias.apostas.controllers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.pinball83.maskededittext.MaskedEditText;
import com.google.gson.Gson;
import com.mercadopago.callbacks.Callback;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.CardToken;
import com.mercadopago.model.Token;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;

import br.gov.caixa.loterias.apostas.BuildConfig;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bean.MeioPagamento;
import br.gov.caixa.loterias.apostas.model.bean.ParametrosPagamento;
import br.gov.caixa.loterias.apostas.model.bo.ApostaBO;
import br.gov.caixa.loterias.apostas.model.bo.ApostaBO_;
import br.gov.caixa.loterias.apostas.model.bo.CardTokenResponse;
import br.gov.caixa.loterias.apostas.model.bo.DadosUsuarioBO;
import br.gov.caixa.loterias.apostas.model.bo.DadosUsuarioBO_;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ListCartaoDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RetornoCartao;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RetornoPadraoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.Bin;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.CarrinhoSingleton;
import br.gov.caixa.loterias.apostas.utils.FirebaseManager;
import br.gov.caixa.loterias.apostas.utils.MaskEditUtil;
import br.gov.caixa.loterias.apostas.utils.MercadoPagoUtil;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.custom.LoadingViewLoterias;

public class NovaFormaPagamentoActivity extends SettingToolbarActivity {

    private EditText editTextNumeroCartaoContent, editTextNomeClienteCartaoContent, editTextCodigoSegurancaContent, editTextMesValidadeCartaoContent, editTextAnoValidadeCartaoContent;

    private TextView textoInfoSelecioneFormaPagamento;

    private AlertDialog alertDialog;

    private CheckBox checkBoxSalvarCartao;

    private ArrayList<RetornoCartao> listaCartoes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ViewUtils.configuraStatusBarGradientLayout(this, getWindow());
        FirebaseManager.registraEvento(this ,getResources().getString(R.string.evento_entrou_adicao_forma_pagamento));

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

        buscaCartoes();

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
                CardToken cardToken = new CardToken(editTextNumeroCartaoContent.getText().toString(),
                        Integer.valueOf(editTextMesValidadeCartaoContent.getText().toString()),
                        Integer.valueOf(editTextAnoValidadeCartaoContent.getText().toString()),
                        editTextCodigoSegurancaContent.getText().toString(),
                        editTextNomeClienteCartaoContent.getText().toString(),
                        getResources().getString(R.string.cpf),
                        DadosUsuarioBO.obterCpf());

                RequestQueue requestQueue = Volley.newRequestQueue(this);
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
                }catch (Exception e) {
                    e.printStackTrace();
                }

                String bin = editTextNumeroCartaoContent.getText().toString().substring(0, 7);
                bin = bin.replace(getResources().getString(R.string.espaco_em_branco), getResources().getString(R.string.string_vazia));
                final String requestBody = jsonBody.toString();
                callWebServicePayment_method(bin, requestBody, getApplicationContext());
               // createTokenAsync(cardToken);
            }
        });
    }

    private boolean camposInvalidos(){
        String errorMessage = getString(R.string.msg_campos_em_branco);
        boolean invalido;
        invalido = (editTextAnoValidadeCartaoContent.getText().toString().trim().equals(getResources().getString(R.string.string_vazia)) ||
                editTextMesValidadeCartaoContent.getText().toString().trim().equals(getResources().getString(R.string.string_vazia)) ||
                editTextNumeroCartaoContent.getText().toString().trim().equals(getResources().getString(R.string.string_vazia)) ||
                editTextCodigoSegurancaContent.getText().toString().trim().equals(getResources().getString(R.string.string_vazia)) ||
                editTextNomeClienteCartaoContent.getText().toString().trim().equals(getResources().getString(R.string.string_vazia)));
        if(editTextNumeroCartaoContent.getText().toString().length() < 16){
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
           //    callWebServicePayment_method(token.getId());
            }

            @Override
            public void failure(ApiException apiException) {
                //TODO: Manage API Failure
                alertDialog.dismiss();
                ViewUtils.alert(NovaFormaPagamentoActivity.this, MercadoPagoUtil.getMessageFromCode(apiException.getCause().get(0).getCode()));
            }
        });
    }

    public void callWebServicePayment_method(final String bin, String jsonObject, Context ctx) {
        String url = BuildConfig.MP_PAYMENT_URL + ctx.getResources().getString(R.string.bin) + bin +  ctx.getResources().getString(R.string.public_key) + BuildConfig.PUBLIC_KEY_MP;
        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            try {
                RequestQueue rQueue = Volley.newRequestQueue(ctx);
                JSONArray  jsonArray   = new JSONArray(response);
                JSONObject responseObj = new JSONObject();
                responseObj = jsonArray.getJSONObject(0);
                String payment_method_id = responseObj.getString(ctx.getResources().getString(R.string.payment_method_id));
                String thumbnail = (responseObj.getJSONObject(ctx.getResources().getString(R.string.issuer))).getString(ctx.getResources().getString(R.string.thumbnail));

                String url2 = BuildConfig.MP_CREATE_TOKEN_URL +  ctx.getResources().getString(R.string.public_key) + BuildConfig.PUBLIC_KEY_MP;

                StringRequest stringRequest = new StringRequest(Request.Method.POST, url2, response1 -> Log.i("VOLLEY", response1), error -> Log.e("VOLLEY", error.toString())) {
                    @Override
                    public String getBodyContentType() {
                        return "application/json; charset=utf-8";
                    }

                    @Override
                    public byte[] getBody() throws AuthFailureError {
                        try {
                            return jsonObject == null ? null : jsonObject.getBytes("utf-8");
                        } catch (UnsupportedEncodingException uee) {
                            VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", jsonObject, "utf-8");
                            return null;
                        }
                    }

                    @Override
                    protected Response<String> parseNetworkResponse(NetworkResponse response) {
                        String responseString = "";
                        if (response != null) {
                            String body = null;
                            try {
                                body = new String(response.data,"UTF-8");
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            CardTokenResponse cardResponse = new Gson().fromJson(body, CardTokenResponse.class);
                            cardResponse.getId();
                            callWebserviceRegistraAposta(cardResponse.getId() , payment_method_id);
                            // can get more details such as response.headers
                        }
                        return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                    }
                };
                rQueue.add(stringRequest);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, volleyError -> {
            //alertDialog.dismiss();
            Log.d(ctx.getResources().getString(R.string.volley_error), ctx.getResources().getString(R.string.volleyError_dois_pontos) + volleyError.toString());
            if(Bin.fromBytes(volleyError.networkResponse.data).toUtf8().contains(ctx.getResources().getString(R.string.not_found))){
                ViewUtils.alert(ctx, ctx.getString(R.string.mercado_pago_numero_cartao_Incorreto));
            }
        });

        RequestQueue rQueue = Volley.newRequestQueue(ctx);
        rQueue.add(request);
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
                    FirebaseManager.registraEvento(NovaFormaPagamentoActivity.this ,getResources().getString(R.string.evento_efetuou_adicao_forma_pagamento_sucesso));
                }
                FirebaseManager.registraEvento(NovaFormaPagamentoActivity.this ,getResources().getString(R.string.evento_efetuou_pagamento_sucesso));

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
                RedirectNetwork.checkRedirect(error, NovaFormaPagamentoActivity.this);
                /*try {
                    String mensagem = new String(error.networkResponse.data);
                    if (mensagem.contains(getResources().getString(R.string.chave_abrindo))) {
                        JSONObject jsonObject = new JSONObject(mensagem);
                        ViewUtils.alert(NovaFormaPagamentoActivity.this, jsonObject.getString(getResources().getString(R.string.mensagem)));
                    } else {
                        RedirectNetwork.checkRedirect(error, NovaFormaPagamentoActivity.this);
                    }
                } catch (Exception e) {
                    RedirectNetwork.checkRedirect(error, NovaFormaPagamentoActivity.this);
                }*/

            }
        });
    }

//    private void callWebServicePayment_method(final String token) {
//        String bin = editTextNumeroCartaoContent.getText().toString().substring(0, 7);
//        bin = bin.replace(getResources().getString(R.string.espaco_em_branco), getResources().getString(R.string.string_vazia));
//        String url = BuildConfig.MP_PAYMENT_URL + getResources().getString(R.string.bin) + bin +  getResources().getString(R.string.public_key) + BuildConfig.PUBLIC_KEY_MP;
//        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
//            Log.d( getResources().getString(R.string.response_dois_pontos), response.toString());
//            try {
//                //Do it with this it will work
//                JSONArray jsonArray = new JSONArray(response);
//                JSONObject responseObj = new JSONObject();
//                responseObj = jsonArray.getJSONObject(0);
//                String payment_method_id = responseObj.getString(getResources().getString(R.string.payment_method_id));
//                String thumbnail = (responseObj.getJSONObject(getResources().getString(R.string.issuer))).getString(getResources().getString(R.string.thumbnail));
//                callWebserviceRegistraAposta(token, payment_method_id);
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//                Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_dois_pontos) + e.getMessage(), Toast.LENGTH_LONG).show();
//            }
//        }, volleyError -> {
//            alertDialog.dismiss();
//
//            Log.d(getResources().getString(R.string.volley_error), getResources().getString(R.string.volleyError_dois_pontos) + volleyError.toString());
//            if(Bin.fromBytes(volleyError.networkResponse.data).toUtf8().contains(getResources().getString(R.string.not_found))){
//                ViewUtils.alert(NovaFormaPagamentoActivity.this, getString(R.string.mercado_pago_numero_cartao_Incorreto));
//            }
//        });
//
//        RequestQueue rQueue = Volley.newRequestQueue(NovaFormaPagamentoActivity.this);
//        rQueue.add(request);
//    }

    private void buscaCartoes() {
        alertDialog = LoadingViewLoterias.show(this);

        DadosUsuarioBO dadosUsuarioBO = DadosUsuarioBO_.getInstance_(NovaFormaPagamentoActivity.this);
        dadosUsuarioBO.cartoes(new RequestListener<ListCartaoDTOResponse>() {

            @Override
            public void onResponse(ListCartaoDTOResponse response) {
                alertDialog.dismiss();
                if (response.getRedirect() != null){
                    RedirectNetwork.checkRedirectSucesso( response.getRedirect(), NovaFormaPagamentoActivity.this);
                }
                listaCartoes = (ArrayList<RetornoCartao>) response.getPayload();
                if (listaCartoes != null && listaCartoes.size() > 0){
                    textoInfoSelecioneFormaPagamento.setVisibility(View.GONE);
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                alertDialog.dismiss();
                RedirectNetwork.checkRedirect( error, NovaFormaPagamentoActivity.this );

            }
        });
    }
}

