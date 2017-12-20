package caixa.gov.br.loterias.view;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.GridView;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.model.TipoAposta;
import caixa.gov.br.loterias.view.adapter.TiposApostasAdapter;
import caixa.gov.br.loterias.view.custom.ExpandableHeightGridView;

public class RapidaoConfigActivity extends AppCompatActivity {

    TipoAposta[] tipoApostaLista;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rapidao_config);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TipoAposta tipoAposta = new TipoAposta("Mega Sena", R.drawable.trevo, R.color.duplasenaclaro);
        TipoAposta tipoAposta2 = new TipoAposta("Quina", R.drawable.trevo, R.color.quinaclaro);
        TipoAposta tipoAposta3 = new TipoAposta("Loteca", R.drawable.trevo, R.color.lotecaclaro);
        TipoAposta tipoAposta4 = new TipoAposta("Time Mania", R.drawable.trevo, R.color.timemaniaclaro);
        tipoApostaLista = new TipoAposta[4];
        tipoApostaLista[0] = tipoAposta;
        tipoApostaLista[1] = tipoAposta2;
        tipoApostaLista[2] = tipoAposta3;
        tipoApostaLista[3] = tipoAposta4;

        ExpandableHeightGridView gridView = (ExpandableHeightGridView)findViewById(R.id.gridviewTiposApostasRapidao);
        TiposApostasAdapter tiposApostasAdapter = new TiposApostasAdapter(this, tipoApostaLista);
        gridView.setAdapter(tiposApostasAdapter);
        gridView.setExpanded(true);
    }

}
