package br.gov.caixa.loterias.apostar.interno.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.mercadopago.callbacks.Callback;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.SavedCardToken;
import com.mercadopago.model.Token;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import br.gov.caixa.loterias.apostar.interno.BuildConfig;
import br.gov.caixa.loterias.apostar.interno.R;
import br.gov.caixa.loterias.apostar.interno.model.bean.MeioPagamento;
import br.gov.caixa.loterias.apostar.interno.model.bean.ParametrosPagamento;
import br.gov.caixa.loterias.apostar.interno.model.bo.ApostaBO;
import br.gov.caixa.loterias.apostar.interno.model.bo.ApostaBO_;
import br.gov.caixa.loterias.apostar.interno.model.bo.DadosUsuarioBO;
import br.gov.caixa.loterias.apostar.interno.model.bo.DadosUsuarioBO_;
import br.gov.caixa.loterias.apostar.interno.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostar.interno.model.bo.RequestListener;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.CarrinhoDTO;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ListCartaoDTOResponse;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.RetornoCartao;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.RetornoPadraoDTO;
import br.gov.caixa.loterias.apostar.interno.utils.ViewUtils;
import br.gov.caixa.loterias.apostar.interno.view.adapter.FormaPagamentoRecyclerViewAdapter;
import br.gov.caixa.loterias.apostar.interno.view.custom.LoadingViewLoterias;
import br.gov.caixa.loterias.apostar.interno.view.listener.FormaDePagamentoListener;

public class FormaPagamentoActivity extends SettingToolbarActivity implements FormaDePagamentoListener {

    private List<RetornoCartao> listaCartoes = null;
    private View itemToolbarSettings;
    private AlertDialog loadViewProgress;
    private FormaPagamentoRecyclerViewAdapter formaPagamentoRecyclerViewAdapter;
    private CarrinhoDTO carrinhoDTO;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ViewUtils.configuraStatusBarGradientLayout(this, getWindow());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forma_pagamento);
        setTitle(ViewUtils.textFuturaAndFuturaBoldTitle(this, getString(R.string.title_activity_forma_pagamento)));

        gson = new Gson();

        configuraClickBotoes();
        callWebservice();
        recebendoInformacoes();

        ViewUtils.organizaTabLayout(this, R.id.formaPagamentoMainLayout, R.id.formasPagamentoRecyclerView, R.id.formaPagamentoScrollView);
    }

    private void recebendoInformacoes() {
        try {
            String json = getIntent().getExtras().getString("CARRINHO");
            carrinhoDTO = gson.fromJson(json, CarrinhoDTO.class);
            TextView valorTotalCarrinho = findViewById(R.id.valorTotalCarrinho);
            String valor = ViewUtils.getMoedaFormat(carrinhoDTO.getValorTotal());
            valorTotalCarrinho.setText(valor);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void callWebservice() {
        loadViewProgress = LoadingViewLoterias.show(this);

        DadosUsuarioBO dadosUsuarioBO = DadosUsuarioBO_.getInstance_(FormaPagamentoActivity.this);
        dadosUsuarioBO.cartoes(new RequestListener<ListCartaoDTOResponse>() {
            @Override
            public void onResponse(ListCartaoDTOResponse response) {
                loadViewProgress.dismiss();
                listaCartoes = response.getPayload();
                if (listaCartoes != null && listaCartoes.size() > 0) {
                    configuraRecyclerViewCarrinhoAposta();
                    ((Button) findViewById(R.id.botaoAvancarPagamento)).setClickable( true );
                    ((Button) findViewById(R.id.botaoAvancarPagamento)).setBackgroundColor( getResources().getColor(R.color.verdeazul ));
                }else {
                    ((Button) findViewById(R.id.botaoAvancarPagamento)).setClickable( false );
                    ((Button) findViewById(R.id.botaoAvancarPagamento)).setBackgroundColor( getResources().getColor(R.color.cinzaescuro ));

                }
                if (response.getRedirect() != null){
                    RedirectNetwork.checkRedirectSucesso( response.getRedirect(), FormaPagamentoActivity.this);
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                loadViewProgress.dismiss();
                RedirectNetwork.checkRedirect( error, FormaPagamentoActivity.this );

            }
        });
    }

    private void configuraRecyclerViewCarrinhoAposta() {
        RecyclerView recyclerView = findViewById(R.id.formasPagamentoRecyclerView);
        RecyclerView.LayoutManager layout = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);

        int sizeitem = ViewUtils.setDisplayMetric(75 * listaCartoes.size(), this);
        ConstraintLayout.LayoutParams layoutFormasPagamento = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, sizeitem);
        layoutFormasPagamento.topToBottom = R.id.textoInfoSelecioneFormaPagamento;
        layoutFormasPagamento.setMargins(0, ViewUtils.setDisplayMetric(40, this), 0, 0);
        recyclerView.setLayoutParams(layoutFormasPagamento);

        formaPagamentoRecyclerViewAdapter = new FormaPagamentoRecyclerViewAdapter(listaCartoes, this);
        formaPagamentoRecyclerViewAdapter.formaDePagamentoListener = this;

        recyclerView.setAdapter(formaPagamentoRecyclerViewAdapter);
        recyclerView.setLayoutManager(layout);
        recyclerView.setNestedScrollingEnabled(false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1) {
            callWebservice();
        }
    }

    @Override
    public void itemRemoved() {
        ViewUtils.organizaTabLayout(this, R.id.formaPagamentoMainLayout, R.id.formasPagamentoRecyclerView, R.id.formaPagamentoScrollView);
    }

    private void configuraClickBotoes() {
        ((Button) findViewById(R.id.botaoAvancarPagamento)).setText(ViewUtils.textFuturaAndFuturaBold(this, getString(R.string.apostarAutorizarCobranca)));
        ((Button) findViewById(R.id.botaoAdicionarFormaPagamento)).setText(ViewUtils.textFuturaAndFuturaBold(this, getString(R.string.adicionarFormaPagamento)));
        findViewById(R.id.botaoAvancarPagamento).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (formaPagamentoRecyclerViewAdapter.selected_position < 0){
                    ViewUtils.alertTitleButton(
                            FormaPagamentoActivity.this,
                            R.string.label_atencao, getResources().getString( R.string.label_selecione_cartao ),
                            getResources().getString( R.string.ok ));
                    return;
                }
                LayoutInflater li = LayoutInflater.from(FormaPagamentoActivity.this);
                View promptsView = li.inflate(R.layout.dialog_security_code, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder( FormaPagamentoActivity.this);
                alertDialogBuilder.setView(promptsView);
                final EditText userInput = (EditText) promptsView
                        .findViewById(R.id.cvvCardNumberEditText);
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("Confirmar",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        if (userInput.getText().toString().length() == 3){
                                            String cardId = formaPagamentoRecyclerViewAdapter.valoresList.get(formaPagamentoRecyclerViewAdapter.selected_position).getIdCartao().toString();
                                            createToken( cardId,  userInput.getText().toString());
                                        }else{
                                            ViewUtils.alertTitleButtonPositiveListener(
                                                    FormaPagamentoActivity.this,
                                                    R.string.label_atencao, getResources().getString( R.string.label_informa_cvv ),
                                                    R.string.ok, new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            ((Button) findViewById(R.id.botaoAvancarPagamento)).performClick();
                                                        }
                                                    }
                                            );
                                        }

                                    }
                                })
                        .setNegativeButton("Cancelar",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        dialog.cancel();
                                    }
                                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

        final Button buttonAdicionarFormaPagamento = (findViewById(R.id.botaoAdicionarFormaPagamento));
        buttonAdicionarFormaPagamento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent activity = new Intent(FormaPagamentoActivity.this, NovaFormaPagamentoActivity.class);
                if(carrinhoDTO != null) {
                    String carrinho = gson.toJson(carrinhoDTO);
                    activity.putExtra("CARRINHO", carrinho);
                }
                startActivity(activity);
            }
        });
    }

    private void createToken(String cardId, String securityCode) {
        SavedCardToken savedCardToken = new SavedCardToken(cardId, securityCode);

        loadViewProgress.show();
        MercadoPago mMercadoPago = new MercadoPago.Builder()
                .setContext(this)
                .setPublicKey( BuildConfig.PUBLIC_KEY_MP )
                .build();
        mMercadoPago.createToken(savedCardToken, new Callback<Token>() {
            @Override
            public void success(Token token) {
                callWebServicePayment_method(token.getId());
            }

            @Override
            public void failure(ApiException apiException) {
                //TODO: Manage API Failure
                loadViewProgress.dismiss();
                ViewUtils.alert(FormaPagamentoActivity.this, apiException.getMessage());
            }
        });
    }


    private void callWebserviceRegistraAposta(String tokenPagamento, String payment_method_id) {
        ParametrosPagamento parametroPagamento = new ParametrosPagamento( tokenPagamento, "true", payment_method_id, "false" );
        MeioPagamento meioPagamento = new MeioPagamento(1, parametroPagamento);

        ApostaBO apostaBO = ApostaBO_.getInstance_(FormaPagamentoActivity.this);
        apostaBO.postRegistrarApostaCarrinho(meioPagamento, new RequestListener<RetornoPadraoDTO>() {
            @Override
            public void onResponse(RetornoPadraoDTO response) {
                loadViewProgress.dismiss();
                if (response.getRedirect() != null){
                    RedirectNetwork.checkRedirectSucesso( response.getRedirect(), FormaPagamentoActivity.this);
                }else{
                    Intent activity = new Intent(FormaPagamentoActivity.this, ConfirmacaoPagamentoActivity.class);
                    startActivity(activity);
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                loadViewProgress.dismiss();
                RedirectNetwork.checkRedirect( error, FormaPagamentoActivity.this );
            }
        });
    }

    private void callWebServicePayment_method(final String token){
        String bin = formaPagamentoRecyclerViewAdapter.valoresList.get( formaPagamentoRecyclerViewAdapter.selected_position ).getPrimeirosDigitos();
        String url = BuildConfig.MP_PAYMENT_URL+"bin="+ bin +"&public_key="+ BuildConfig.PUBLIC_KEY_MP;
        StringRequest request = new StringRequest( Request.Method.GET, url, new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {
                Log.d("Response: ", response.toString());
                try {
                    //Do it with this it will work
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject responseObj = new JSONObject();
                    responseObj = jsonArray.getJSONObject(0);
                    String payment_method_id = responseObj.getString("payment_method_id");
                    String thumbnail = (responseObj.getJSONObject("issuer")).getString( "thumbnail" );
                    callWebserviceRegistraAposta(token, payment_method_id);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.d("volleyError", "volleyError: "+ volleyError.toString());
            }
        });

        RequestQueue rQueue = Volley.newRequestQueue(FormaPagamentoActivity.this);
        rQueue.add(request);
    }




}
