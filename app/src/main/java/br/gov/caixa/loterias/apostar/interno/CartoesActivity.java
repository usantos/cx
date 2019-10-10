package br.gov.caixa.loterias.apostar.interno;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mercadopago.callbacks.Callback;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.SavedCardToken;
import com.mercadopago.model.Token;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import br.gov.caixa.loterias.apostar.interno.model.bean.MeioPagamento;
import br.gov.caixa.loterias.apostar.interno.model.bean.ParametrosPagamento;
import br.gov.caixa.loterias.apostar.interno.model.bo.ApostaBO;
import br.gov.caixa.loterias.apostar.interno.model.bo.ApostaBO_;
import br.gov.caixa.loterias.apostar.interno.model.bo.DadosUsuarioBO;
import br.gov.caixa.loterias.apostar.interno.model.bo.DadosUsuarioBO_;
import br.gov.caixa.loterias.apostar.interno.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostar.interno.model.bo.RequestListener;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ListCartaoDTOResponse;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.RetornoCartao;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.RetornoPadraoDTO;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.internals.CarrinhoSingleton;
import br.gov.caixa.loterias.apostar.interno.utils.FirebaseManager;
import br.gov.caixa.loterias.apostar.interno.utils.MercadoPagoUtil;
import br.gov.caixa.loterias.apostar.interno.utils.ViewUtils;
import br.gov.caixa.loterias.apostar.interno.view.ConfirmacaoPagamentoActivity;
import br.gov.caixa.loterias.apostar.interno.view.ListaRapidaoActivity;
import br.gov.caixa.loterias.apostar.interno.view.NovaFormaPagamentoActivity;
import br.gov.caixa.loterias.apostar.interno.view.adapter.CartoesRecyclerViewAdapter;
import br.gov.caixa.loterias.apostar.interno.view.custom.LoadingViewLoterias;
import br.gov.caixa.loterias.apostar.interno.view.listener.FormaDePagamentoListener;

import static br.gov.caixa.loterias.apostar.interno.view.PrincipalActivity.MENU_LATERAL;

public class CartoesActivity extends AppCompatActivity implements FormaDePagamentoListener {

    private RecyclerView rvCartoes;
    private CartoesRecyclerViewAdapter adapter;
    private Toolbar toolbar;
    private List<RetornoCartao> listaCartoes = null;
    private View itemToolbarSettings;
    private AlertDialog loadViewProgress;
    private Boolean flagEntrouAddCartao = false, flagVeioMenu = false;
    private Button btnAdicionarCartao, btnAvancarPagamento, btnIrRapidao;
    private TextView tvSubtitulo, tvDescricao, tvValorTotalCarrinho, tvIrRapidao;
    private ConstraintLayout clIrRapidao, clTab;
    private NestedScrollView scrollCartoes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cartoes);
        pegaExtras();
        setaViews();
        setaMetodos();
    }

    private void pegaExtras(){
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            flagVeioMenu = bundle.getBoolean(MENU_LATERAL,false);
        }
    }

    private void setaViews(){
        rvCartoes = findViewById(R.id.rv_cartoes);
        toolbar = findViewById(R.id.toolbar);
        btnAdicionarCartao = findViewById(R.id.btn_adicionar_cartao);
        btnAvancarPagamento = findViewById(R.id.btn_avancar_pagamento);
        tvSubtitulo = findViewById(R.id.tv_subtitulo);
        tvDescricao = findViewById(R.id.tv_descricao);
        tvValorTotalCarrinho = findViewById(R.id.tv_valor_total_carrinho);
        tvIrRapidao = findViewById(R.id.tv_ir_rapidao);
        btnIrRapidao = findViewById(R.id.btn_ir_rapidao);
        clIrRapidao = findViewById(R.id.cl_ir_rapidao);
        scrollCartoes = findViewById(R.id.nsv_conteudo);
        clTab = findViewById(R.id.cl_tab);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(flagVeioMenu){
            btnAdicionarCartao.setVisibility(View.GONE);
            btnAvancarPagamento.setVisibility(View.GONE);
            setTitle(getString(R.string.cartoes_titulo_menu_lat));
            tvSubtitulo.setText(getString(R.string.cartoes_subt_menu_lat));
            tvDescricao.setText(getString(R.string.cartoes_desc_menu_lat));
            clTab.setVisibility(View.GONE);
        }else {
            setTitle(getString(R.string.cartoes_titulo));
        }

        configuraTelaSemCartao();

        btnAvancarPagamento.setText(ViewUtils.textFuturaAndFuturaBold(this, getString(R.string.apostarAutorizarCobranca)));
        btnAdicionarCartao.setText(ViewUtils.textFuturaAndFuturaBold(this, getString(R.string.adicionarFormaPagamento)));


    }

    private void configuraTelaSemCartao(){
        if (DadosUsuarioBO.checarTutorialRapidao()){
            tvIrRapidao.setText(ViewUtils.textColor(this,getResources().getString(R.string.cartoes_ir_rapidao), R.color.amareloQueimado));
            btnIrRapidao.setText(getString(R.string.partiu));

        } else {
            tvIrRapidao.setText(ViewUtils.textColor(this,getResources().getString(R.string.cartoes_ir_rapidao_tutorial), R.color.amareloQueimado));
            btnIrRapidao.setText(getString(R.string.so_bora));
        }
    }

    private void setaMetodos(){
        btnAvancarPagamento.setOnClickListener(view -> {
            if (adapter != null) {
                if (adapter.selected_position < 0){
                    ViewUtils.alertTitleButton(
                            CartoesActivity.this,
                            R.string.label_atencao, getResources().getString( R.string.label_selecione_cartao ),
                            getResources().getString( R.string.ok ));
                    return;
                }
                LayoutInflater li = LayoutInflater.from(CartoesActivity.this);
                View promptsView = li.inflate(R.layout.dialog_security_code, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder( CartoesActivity.this);
                alertDialogBuilder.setView(promptsView);
                final EditText userInput = promptsView
                        .findViewById(R.id.cvvCardNumberEditText);
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("Confirmar",
                                (DialogInterface dialog, int which) -> {
                                    if (userInput.getText().toString().length() == 3){
                                        String cardId = adapter.valoresList.get(adapter.selected_position).getIdCartao().toString();
                                        createToken( cardId,  userInput.getText().toString());
                                    }else{
                                        ViewUtils.alertTitleButtonPositiveListener(
                                                CartoesActivity.this,
                                                R.string.label_atencao, getResources().getString( R.string.label_informa_cvv ),
                                                R.string.ok, (dialog1, which1) -> btnAvancarPagamento.performClick()
                                        );
                                    }
                                })
                        .setNegativeButton("Cancelar",
                                (DialogInterface dialog, int which) -> {
                                    dialog.cancel();
                                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            } else {
                ViewUtils.alertTitleButton(
                        CartoesActivity.this,
                        R.string.label_atencao, getResources().getString( R.string.label_adicione_um_cartao ),
                        getResources().getString( R.string.ok ));
                return;
            }
        });

        btnAdicionarCartao.setOnClickListener(view -> startActivity(new Intent(CartoesActivity.this, NovaFormaPagamentoActivity.class)));

        btnIrRapidao.setOnClickListener(V -> {
            if (DadosUsuarioBO.checarTutorialRapidao()) {
                startActivity(new Intent(CartoesActivity.this, ListaRapidaoActivity.class));
            } else {
                startActivity(new Intent(CartoesActivity.this, TutorialRapidaoActivity.class));
            }
        });
    }


    private void configuraRecycler(){
        adapter = new CartoesRecyclerViewAdapter( listaCartoes,CartoesActivity.this, flagVeioMenu);
        adapter.formaDePagamentoListener = this;
        rvCartoes.setAdapter(adapter);
        RecyclerView.LayoutManager layout = new LinearLayoutManager(CartoesActivity.this, LinearLayoutManager.VERTICAL, false);
        rvCartoes.setLayoutManager(layout);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        callWebservice();
        atualizaValorTotal();
    }

    private void atualizaValorTotal() {
        try {
            String valor = ViewUtils.getMoedaFormat(CarrinhoSingleton.getInstance().getCarrinho().getValorTotal());
            tvValorTotalCarrinho.setText(valor);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void callWebservice() {
        loadViewProgress = LoadingViewLoterias.show(this);

        DadosUsuarioBO dadosUsuarioBO = DadosUsuarioBO_.getInstance_(CartoesActivity.this);
        dadosUsuarioBO.cartoes(new RequestListener<ListCartaoDTOResponse>() {
            @Override
            public void onResponse(ListCartaoDTOResponse response) {
                FirebaseManager.registraEvento(CartoesActivity.this ,"ENTROU_FORMAS_DE_PAGAMENTO");
                loadViewProgress.dismiss();
                if (response.getRedirect() != null){
                    RedirectNetwork.checkRedirectSucesso( response.getRedirect(), CartoesActivity.this);
                }
                listaCartoes = response.getPayload();
                if (listaCartoes != null && listaCartoes.size() > 0) {
                    configuraRecycler();
                    configuraTelaSemCartoes(false);
                }else {
                    if (!flagVeioMenu) {
                        if (!flagEntrouAddCartao) {
                            startActivityForResult(new Intent(CartoesActivity.this, NovaFormaPagamentoActivity.class), 600);
                            flagEntrouAddCartao = true;
                        }
                    } else {
                        configuraTelaSemCartoes(true);
                    }
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                loadViewProgress.dismiss();
                RedirectNetwork.checkRedirect( error, CartoesActivity.this );

            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
       if (requestCode == 600) {
            if (listaCartoes == null || listaCartoes.size() == 0){
                finish();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void configuraTelaSemCartoes(Boolean isSemCartoes){
        if(isSemCartoes){
            scrollCartoes.setVisibility(View.GONE);
            clIrRapidao.setVisibility(View.VISIBLE);
        }else{
            scrollCartoes.setVisibility(View.VISIBLE);
            clIrRapidao.setVisibility(View.GONE);
        }
    }

    @Override
    public void itemRemoved(Boolean lastItem){
        if(lastItem){
            if(flagVeioMenu){
                recreate();
            }else {
                startActivityForResult(new Intent(CartoesActivity.this, NovaFormaPagamentoActivity.class), 600);
            }
        }
    }

    private void createToken(String cardId, String securityCode) {
        SavedCardToken savedCardToken = new SavedCardToken(cardId, securityCode);

        loadViewProgress = LoadingViewLoterias.show(this);
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
                ViewUtils.alert(CartoesActivity.this, MercadoPagoUtil.getMessageFromCode(apiException.getCause().get(0).getCode()));
            }
        });
    }


    private void callWebserviceRegistraAposta(String tokenPagamento, String payment_method_id) {
        ParametrosPagamento parametroPagamento = new ParametrosPagamento( tokenPagamento, "true", payment_method_id, "false" );
        MeioPagamento meioPagamento = new MeioPagamento(1, parametroPagamento);

        ApostaBO apostaBO = ApostaBO_.getInstance_(CartoesActivity.this);
        apostaBO.postRegistrarApostaCarrinho(meioPagamento, new RequestListener<RetornoPadraoDTO>() {
            @Override
            public void onResponse(RetornoPadraoDTO response) {
                loadViewProgress.dismiss();
                if (response.getRedirect() != null){
                    RedirectNetwork.checkRedirectSucesso( response.getRedirect(), CartoesActivity.this);
                }else{
                    FirebaseManager.registraEvento(CartoesActivity.this ,"EFETUOU_PAGAMENTO_SUCESSO");
                    Intent activity = new Intent(CartoesActivity.this, ConfirmacaoPagamentoActivity.class);
                    startActivity(activity);
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                loadViewProgress.dismiss();
                RedirectNetwork.checkRedirect( error, CartoesActivity.this );
            }
        });
    }

    private void callWebServicePayment_method(final String token){
        String bin = adapter.valoresList.get( adapter.selected_position ).getPrimeirosDigitos();
        String url = BuildConfig.MP_PAYMENT_URL+"bin="+ bin +"&public_key="+ BuildConfig.PUBLIC_KEY_MP;
        StringRequest request = new StringRequest( Request.Method.GET, url, response -> {
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
        }, volleyError -> {
            loadViewProgress.dismiss();
            Log.d("volleyError", "volleyError: "+ volleyError.toString());
        });

        RequestQueue rQueue = Volley.newRequestQueue(CartoesActivity.this);
        rQueue.add(request);
    }
}
