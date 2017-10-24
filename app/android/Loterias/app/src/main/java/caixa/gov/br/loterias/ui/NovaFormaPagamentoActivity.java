package caixa.gov.br.loterias.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
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
//        RecyclerView.LayoutManager layout = new LinearLayoutManager(this,
//                LinearLayoutManager.VERTICAL, false);

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

//        RecyclerView myList = (RecyclerView) findViewById(R.id.formasPagamentoRecyclerView);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(new FormaPagamentoRecyclerViewAdapter(montaCarrinhoApostaValores(values), this));
//        recyclerView.setLayoutManager(layout);
//        recyclerView.setNestedScrollingEnabled(false);
    }

    private ArrayList<String> montaCarrinhoApostaValores(String valores[]){
        ArrayList<String> valoresTextoDialog = new ArrayList<>();

        Collections.addAll(valoresTextoDialog, valores);

        return valoresTextoDialog;
    }

}
