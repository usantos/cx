package caixa.gov.br.loterias.ui;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
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
import android.widget.ScrollView;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.ui.Inteface.FormaDePagamentoListener;
import caixa.gov.br.loterias.ui.adapter.FormaPagamentoRecyclerViewAdapter;
import caixa.gov.br.loterias.utils.ViewUtils;

public class FormaPagamentoActivity extends AppCompatActivity implements FormaDePagamentoListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ViewUtils.configuraStatusBarGradientLayout(this, getWindow());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forma_pagamento);

        configuraActivityCarrinho();
        configuraClickBotoes();
        configuraRecyclerViewCarrinhoAposta();

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
        String[] values = new String[] {"01", "02", "03", "04"};

        RecyclerView recyclerView = findViewById(R.id.formasPagamentoRecyclerView);
        RecyclerView.LayoutManager layout = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);

        FormaPagamentoRecyclerViewAdapter formaPagamentoRecyclerViewAdapter = new FormaPagamentoRecyclerViewAdapter(montaCarrinhoApostaValores(values), this);
        formaPagamentoRecyclerViewAdapter.formaDePagamentoListener = this;

        recyclerView.setAdapter(formaPagamentoRecyclerViewAdapter);
        recyclerView.setLayoutManager(layout);
        recyclerView.setNestedScrollingEnabled(false);
    }

    private ArrayList<String> montaCarrinhoApostaValores(String valores[]){
        ArrayList<String> valoresTextoDialog = new ArrayList<>();

        Collections.addAll(valoresTextoDialog, valores);

        return valoresTextoDialog;
    }

    @Override
    public void itemRemoved() {
        ViewUtils.organizaTabLayout(this, R.id.formaPagamentoMainLayout, R.id.formasPagamentoRecyclerView, R.id.formaPagamentoScrollView);
    }
}
