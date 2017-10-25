package caixa.gov.br.loterias.ui;

import android.content.Intent;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.ui.adapter.FormaPagamentoRecyclerViewAdapter;

public class NovaFormaPagamentoActivity extends AppCompatActivity {

    private GridView horizontalGridView;
    List<String> list;
    int size;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nova_forma_pagamento);


        String[] values = new String[] { "01", "02", "02", "02", "02"};

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.novaFormaPagamentoRecyclerView);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);


        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(new FormaPagamentoRecyclerViewAdapter(montaCarrinhoApostaValores(values), this));

        configuraActivityCarrinho();
        configuraBotoesListener();
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

    private ArrayList<String> montaCarrinhoApostaValores(String valores[]){
        ArrayList<String> valoresTextoDialog = new ArrayList<>();

        Collections.addAll(valoresTextoDialog, valores);

        return valoresTextoDialog;
    }

    private void configuraBotoesListener(){
        findViewById(R.id.botaoAvancarPagamento).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent activity = new Intent(NovaFormaPagamentoActivity.this, ConfirmacaoPagamentoActivity.class);
                startActivity(activity);
            }
        });
    }

}
