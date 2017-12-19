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

public class RapidaoConfigActivity extends AppCompatActivity {

    TipoAposta[] tipoApostaLista;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rapidao_config);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TipoAposta tipoAposta = new TipoAposta("Mega Sena", R.drawable.trevo_megasena, R.color.verdesenatrevo);
        tipoApostaLista = new TipoAposta[4];
        tipoApostaLista[0] = tipoAposta;
        tipoApostaLista[1] = tipoAposta;
        tipoApostaLista[2] = tipoAposta;
        tipoApostaLista[3] = tipoAposta;

        GridView gridView = (GridView)findViewById(R.id.gridviewTiposApostasRapidao);
        TiposApostasAdapter tiposApostasAdapter = new TiposApostasAdapter(this, tipoApostaLista);
        gridView.setAdapter(tiposApostasAdapter);
    }

}
