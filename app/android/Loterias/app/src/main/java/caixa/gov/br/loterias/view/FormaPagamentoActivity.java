package caixa.gov.br.loterias.view;

import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.Menu;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import com.android.volley.VolleyError;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.bo.DadosUsuarioBO;
import caixa.gov.br.loterias.bo.silce.SilceClient;
import caixa.gov.br.loterias.bo.silce.dto.ListCartaoDTOResponse;
import caixa.gov.br.loterias.bo.silce.dto.RetornoCartao;
import caixa.gov.br.loterias.utils.LoadingViewLoterias;
import caixa.gov.br.loterias.utils.MensagensNetwork;
import caixa.gov.br.loterias.view.Inteface.FormaDePagamentoListener;
import caixa.gov.br.loterias.view.adapter.FormaPagamentoRecyclerViewAdapter;
import caixa.gov.br.loterias.utils.ViewUtils;

public class FormaPagamentoActivity extends AppCompatActivity implements FormaDePagamentoListener {
    private List<RetornoCartao> listaCartoes = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ViewUtils.configuraStatusBarGradientLayout(this, getWindow());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forma_pagamento);

        configuraActivityCarrinho();
        configuraClickBotoes();
        callWebservice();

        ViewUtils.organizaTabLayout(this, R.id.formaPagamentoMainLayout, R.id.formasPagamentoRecyclerView, R.id.formaPagamentoScrollView);
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        return true;
    }

    private void callWebservice () {
        final View loadViewProgress = LoadingViewLoterias.init(this);

        DadosUsuarioBO dadosUsuarioBO = new DadosUsuarioBO(FormaPagamentoActivity.this);
        dadosUsuarioBO.cartoes(new SilceClient.Listener<ListCartaoDTOResponse>() {
            @Override
            public void onResponse(ListCartaoDTOResponse response) {
                LoadingViewLoterias.stop(loadViewProgress);
                listaCartoes = response.getPayload();
                if (listaCartoes != null && listaCartoes.size() > 0){
                    configuraRecyclerViewCarrinhoAposta();
                }
            }
        }, new SilceClient.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LoadingViewLoterias.stop(loadViewProgress);
                ViewUtils.alert(FormaPagamentoActivity.this, MensagensNetwork.setMensagem(error, getApplicationContext()));
            }
        });
    }

    private void configuraActivityCarrinho(){
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        getSupportActionBar().setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.navigation_gradient, null));
    }

    private void configuraClickBotoes(){
        ((Button) findViewById(R.id.botaoAvancarPagamento)).setText(ViewUtils.configuraButtonText(this, getString(R.string.apostarAutorizarCobranca), 7));
        ((Button) findViewById(R.id.botaoAdicionarFormaPagamento)).setText(ViewUtils.configuraButtonText(this, getString(R.string.adicionarFormaPagamento), 9));

        findViewById(R.id.botaoAvancarPagamento).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent activity = new Intent(FormaPagamentoActivity.this, ConfirmacaoPagamentoActivity.class);
                startActivity(activity);
            }
        });

        final Button buttonAdicionarFormaPagamento = (findViewById(R.id.botaoAdicionarFormaPagamento));
        buttonAdicionarFormaPagamento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent activity = new Intent(FormaPagamentoActivity.this, NovaFormaPagamentoActivity.class);
                startActivity(activity);
            }
        });
    }

    private void configuraRecyclerViewCarrinhoAposta(){
        RecyclerView recyclerView = findViewById(R.id.formasPagamentoRecyclerView);
        RecyclerView.LayoutManager layout = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);

        int sizeitem = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 75 * listaCartoes.size(), this.getResources().getDisplayMetrics());


        ConstraintLayout.LayoutParams layoutFormasPagamento = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, sizeitem);
        layoutFormasPagamento.topToBottom = R.id.textoInfoSelecioneFormaPagamento;
        recyclerView.setLayoutParams(layoutFormasPagamento);

        FormaPagamentoRecyclerViewAdapter formaPagamentoRecyclerViewAdapter = new FormaPagamentoRecyclerViewAdapter(listaCartoes, this);
        formaPagamentoRecyclerViewAdapter.formaDePagamentoListener = this;

        recyclerView.setAdapter(formaPagamentoRecyclerViewAdapter);
        recyclerView.setLayoutManager(layout);
        recyclerView.setNestedScrollingEnabled(false);
    }

    @Override
    public void itemRemoved() {
        ViewUtils.organizaTabLayout(this, R.id.formaPagamentoMainLayout, R.id.formasPagamentoRecyclerView, R.id.formaPagamentoScrollView);
    }
}
