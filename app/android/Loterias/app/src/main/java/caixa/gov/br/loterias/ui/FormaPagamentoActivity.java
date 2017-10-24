package caixa.gov.br.loterias.ui;

import android.content.Intent;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.ui.adapter.FormaPagamentoRecyclerViewAdapter;
import caixa.gov.br.loterias.utils.TipoApostaEnum;

public class FormaPagamentoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forma_pagamento);

        configuraActivityCarrinho();
        configuraRecyclerViewCarrinhoAposta();
        configuraClickBotoes();
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
        findViewById(R.id.botaoAvancarPagamento).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent activity = new Intent(FormaPagamentoActivity.this, ConfirmacaoPagamentoActivity.class);
                startActivity(activity);
            }
        });
    }

    private void configuraRecyclerViewCarrinhoAposta(){
        String[] values = new String[] { "01", "02", "02", "02", "02"};

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.formasPagamentoRecyclerView);
        RecyclerView.LayoutManager layout = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);

        recyclerView.setAdapter(new FormaPagamentoRecyclerViewAdapter(montaCarrinhoApostaValores(values), this));
        recyclerView.setLayoutManager(layout);
        recyclerView.setNestedScrollingEnabled(false);
    }

    private ArrayList<String> montaCarrinhoApostaValores(String valores[]){
        ArrayList<String> valoresTextoDialog = new ArrayList<>();

        Collections.addAll(valoresTextoDialog, valores);

        return valoresTextoDialog;
    }
}
