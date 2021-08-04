package br.gov.caixa.loterias.apostas.controllers;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.mercadolibre.android.device.sdk.DeviceSDK;

import org.json.JSONObject;

import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bean.MeioPagamento;
import br.gov.caixa.loterias.apostas.model.bean.ParametrosPagamento;
import br.gov.caixa.loterias.apostas.model.bo.ApostaBO;
import br.gov.caixa.loterias.apostas.model.bo.ApostaBO_;
import br.gov.caixa.loterias.apostas.model.bo.DadosUsuarioBO;
import br.gov.caixa.loterias.apostas.model.bo.DadosUsuarioBO_;
import br.gov.caixa.loterias.apostas.model.bo.MercadoPagoConnection;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ListCartaoDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RetornoCartao;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RetornoPadraoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.Bin;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.CarrinhoSingleton;
import br.gov.caixa.loterias.apostas.model.dao.crud.CarrinhoFavoritoCRUD;
import br.gov.caixa.loterias.apostas.utils.DialogUtils;
import br.gov.caixa.loterias.apostas.utils.FirebaseManager;
import br.gov.caixa.loterias.apostas.utils.FragmentUtils;
import br.gov.caixa.loterias.apostas.utils.MercadoPagoUtil;
import br.gov.caixa.loterias.apostas.utils.RateUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.adapter.CartoesRecyclerViewAdapter;
import br.gov.caixa.loterias.apostas.view.fragment.SomadorCarrinhoFragment;
import br.gov.caixa.loterias.apostas.view.listener.FormaDePagamentoListener;

import static br.gov.caixa.loterias.apostas.controllers.PrincipalActivity.MENU_LATERAL;

public class CartoesActivity extends AppCompatActivity implements FormaDePagamentoListener, MercadoPagoConnection.MercadoPagoListener {

    private RecyclerView rvCartoes;
    private CartoesRecyclerViewAdapter adapter;
    private Toolbar toolbar;
    private List<RetornoCartao> listaCartoes = null;
    private View itemToolbarSettings;
    private Boolean flagEntrouAddCartao = false, flagVeioMenu = false;
    private Button btnAdicionarCartao, btnAvancarPagamento;
    private TextView tvSubtitulo, tvDescricao, tvValorTotalCarrinho, tvSemCartao;
    private ConstraintLayout clSemCartao, clTab;
    private NestedScrollView scrollCartoes;
    private MercadoPagoConnection.MercadoPagoListener listener;
    private String requestBody, bin;
    private EditText userInput;

    //fragments
    private SomadorCarrinhoFragment somadorCarrinhoFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cartoes);
        pegaExtras();
        setaViews();
        setaMetodos();
        listener = this;
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
        tvSemCartao = findViewById(R.id.tv_sem_cartao);
        clSemCartao = findViewById(R.id.cl_sem_cartao);
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

        tvSemCartao.setText(getResources().getString(R.string.cartoes_sem_cartao));

        btnAvancarPagamento.setText(ViewUtils.textFuturaAndFuturaBold(this, getString(R.string.apostarAutorizarCobranca)));
        btnAdicionarCartao.setText(ViewUtils.textFuturaAndFuturaBold(this, getString(R.string.adicionarFormaPagamento)));


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
                userInput = promptsView
                        .findViewById(R.id.cvvCardNumberEditText);
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton(getResources().getString(R.string.confirmar),
                                           (DialogInterface dialog, int which) -> {
                                               int tamanhoCVV = userInput.getText().toString().length();
                                               if (tamanhoCVV == 3 || tamanhoCVV == 4){
                                                   preencheDadosPagamento();
                                                   showLoading();
                                                   MercadoPagoConnection.callWebServicePayment_method(bin ,requestBody, this, listener);
                                               }else{
                                                   ViewUtils.alertTitleButtonPositiveListener(
                                                           CartoesActivity.this,
                                                           R.string.label_atencao, getResources().getString( R.string.label_informa_cvv ),
                                                           R.string.ok, (dialog1, which1) -> btnAvancarPagamento.performClick()
                                                                                             );
                                               }
                                           })
                        .setNegativeButton(getResources().getString(R.string.cancelar),
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
    }

    private void preencheDadosPagamento(){

        RetornoCartao cartao = adapter.valoresList.get(adapter.selected_position);
        JSONObject jsonBody = new JSONObject();
        JSONObject jsonCardHolder = new JSONObject();
        JSONObject jsonIdentification = new JSONObject();
        try {
            jsonIdentification.put("type", "CPF");
            jsonIdentification.put("number", DadosUsuarioBO.obterCpf());
            jsonCardHolder.put("identification",jsonIdentification);

            jsonBody.put("card_id",cartao.getIdCartao());
            jsonBody.put("expiration_year",cartao.getAnoValidade());
            jsonBody.put("expiration_month", cartao.getMesValidade());
            jsonBody.put("security_code", userInput.getText());
            jsonBody.put("cardholder", jsonCardHolder);
            jsonBody.put("device", DeviceSDK.getInstance().getInfoAsJsonString());
        }catch (Exception e) {
            e.printStackTrace();
        }

        bin = cartao.getPrimeirosDigitos();
        requestBody = jsonBody.toString();
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
        FragmentUtils.startSomadorCarrinhoFragment(getSupportFragmentManager(), somadorCarrinhoFragment, R.id.id_fg_somador_carrinho, "TELA ESCOLHE CARTAO");
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
       showLoading();

        DadosUsuarioBO dadosUsuarioBO = DadosUsuarioBO_.getInstance_(CartoesActivity.this);
        dadosUsuarioBO.cartoes(new RequestListener<ListCartaoDTOResponse>() {
            @Override
            public void onResponse(ListCartaoDTOResponse response) {
                FirebaseManager.registraEvento(CartoesActivity.this ,getResources().getString(R.string.evento_entrou_formas_pagamento));
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
                DialogUtils.dismiss();
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                RedirectNetwork.checkRedirect( error, CartoesActivity.this );
                DialogUtils.dismiss();
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
            clSemCartao.setVisibility(View.VISIBLE);
        }else{
            scrollCartoes.setVisibility(View.VISIBLE);
            clSemCartao.setVisibility(View.GONE);
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

    private void callWebserviceRegistraAposta(String tokenPagamento, String payment_method_id) {
        if(!DialogUtils.isShow()){
            showLoading();
        }

        ParametrosPagamento parametroPagamento = new ParametrosPagamento( tokenPagamento, getResources().getString(R.string.string_true), payment_method_id, getResources().getString(R.string.string_false) );
        MeioPagamento meioPagamento = new MeioPagamento(1, parametroPagamento);

        ApostaBO apostaBO = ApostaBO_.getInstance_(CartoesActivity.this);
        apostaBO.postRegistrarApostaCarrinho(meioPagamento, new RequestListener<RetornoPadraoDTO>() {
            @Override
            public void onResponse(RetornoPadraoDTO response) {
                if (response.getRedirect() != null){
                    RedirectNetwork.checkRedirectSucesso( response.getRedirect(), CartoesActivity.this);
                }else{
                    FirebaseManager.registraEvento(CartoesActivity.this ,getResources().getString(R.string.evento_pagamento_sucesso));
                    RateUtils.setQtdCompras(RateUtils.getQtdCompras() + 1);
                    Intent activity = new Intent(CartoesActivity.this, ConfirmacaoPagamentoActivity.class);
                    startActivity(activity);
                    CarrinhoSingleton.getInstance().setCarrinho(new CarrinhoDTO());
                    limpaCarrinhoFavorito();
                }
                DialogUtils.dismiss();
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                RedirectNetwork.checkRedirect( error, CartoesActivity.this );
                DialogUtils.dismiss();
            }
        });
    }

    private void limpaCarrinhoFavorito() {
        CarrinhoFavoritoCRUD crud = new CarrinhoFavoritoCRUD(CartoesActivity.this);
        crud.deletaTodos();
    }

    private void showLoading(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                DialogUtils.show(CartoesActivity.this);
            }
        });
    }

    @Override
    public void onSucesso(String cardToken, String payment_method_id) {
        callWebserviceRegistraAposta(cardToken, payment_method_id);
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
        ViewUtils.alertTitleButton(CartoesActivity.this, R.string.label_atencao, msg,
                CartoesActivity.this.getResources().getString( R.string.ok ) );
    }
}