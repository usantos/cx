package caixa.gov.br.loterias.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.model.NumerosSelecionadosRapidao;
import caixa.gov.br.loterias.view.adapter.NumeroSelecionadoRapidaoAdapter;
import caixa.gov.br.loterias.view.adapter.NumerosRapidaoAdapter;
import caixa.gov.br.loterias.view.custom.ExpandableHeightGridView;

public class NumerosRapidaoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_numeros_rapidao);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        gridViewNumeros();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.settings, menu);

        return true;
    }

    // Mudando o texto dos MenuItem de acordo com os dados
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            abrirTermosUso();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void abrirTermosUso(){
        Intent activity = new Intent(NumerosRapidaoActivity.this, TermosUsoActivity.class);
        startActivity(activity);
    }


    private void gridViewNumeros(){
        NumerosSelecionadosRapidao[] listaNumerosSelecionadosRapidao = new NumerosSelecionadosRapidao[100];
        for (int i =0 ; i<100; i++){
            NumerosSelecionadosRapidao numeSel = new NumerosSelecionadosRapidao(i+1, false);
            listaNumerosSelecionadosRapidao[i] = numeSel;
        }

        ExpandableHeightGridView gridViewNumerosRapidaoSlc = (ExpandableHeightGridView)findViewById(R.id.gridviewNumerosRapidaoSelecao);
        NumeroSelecionadoRapidaoAdapter numerosSlcRapidaoAdapter = new NumeroSelecionadoRapidaoAdapter(this, this, listaNumerosSelecionadosRapidao);
        gridViewNumerosRapidaoSlc.setAdapter(numerosSlcRapidaoAdapter);
        gridViewNumerosRapidaoSlc.setExpanded(true);

    }



    public void mudarLayoutBotaoLimparSelecao(Boolean ativo) {
        final RelativeLayout limpaselecaorelativeLayout = (RelativeLayout)findViewById(R.id.layoutLimparSelecaoApostaRapidaSelecao);
        if (ativo) {
            limpaselecaorelativeLayout.setVisibility(View.VISIBLE);
        }else {
            limpaselecaorelativeLayout.setVisibility(View.GONE);

        }
    }

}
