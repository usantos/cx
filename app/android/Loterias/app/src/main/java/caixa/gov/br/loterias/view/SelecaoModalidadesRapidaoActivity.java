package caixa.gov.br.loterias.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.model.TipoAposta;
import caixa.gov.br.loterias.view.adapter.TiposApostasMultiplaSelecaoAdapter;
import caixa.gov.br.loterias.view.custom.ExpandableHeightGridView;

public class SelecaoModalidadesRapidaoActivity extends AppCompatActivity {

    TipoAposta[] tipoApostaLista;
    Button salvarButtonRapidaoSelecionados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selecao_modalidades_rapidao);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        salvarButtonRapidaoSelecionados = (Button)findViewById(R.id.salvarModalidadesApostaRapidaoSelececao);
        gridViewTipoAposta();
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
        Intent activity = new Intent(SelecaoModalidadesRapidaoActivity.this, TermosUsoActivity.class);
        startActivity(activity);
    }


    private void gridViewTipoAposta(){
        //mock tipo aposta

        TipoAposta tipoAposta = new TipoAposta("mega-sena", R.drawable.trevo_megasena, R.color.megasenaclaro, false);
        TipoAposta tipoAposta2 = new TipoAposta("quina", R.drawable.trevo_quina, R.color.quinaclaro, false);
        TipoAposta tipoAposta3 = new TipoAposta("loteca", R.drawable.trevo_loteca, R.color.lotecaclaro, false);
        TipoAposta tipoAposta4 = new TipoAposta("timemania", R.drawable.trevo_timemania, R.color.timemaniaclaro, false);
        TipoAposta tipoAposta5 = new TipoAposta("duplasena", R.drawable.trevo_duplasena, R.color.duplasenaclaro, false);
        TipoAposta tipoAposta6 = new TipoAposta("lotofácil", R.drawable.trevo_lotofacil, R.color.lotofacilclaro, false);
        TipoAposta tipoAposta7 = new TipoAposta("lotogol", R.drawable.trevo_lotogol, R.color.lotogolclaro, false);
        TipoAposta tipoAposta8 = new TipoAposta("lotomania", R.drawable.trevo_lotomania, R.color.lotomaniaclaro, false);
        TipoAposta tipoAposta9 = new TipoAposta("lotomania", R.drawable.trevo_lotomania, R.color.lotomaniaclaro, false);
        TipoAposta tipoAposta10 = new TipoAposta("lotomania", R.drawable.trevo_lotomania, R.color.lotomaniaclaro, false);
        TipoAposta tipoAposta11 = new TipoAposta("lotomania", R.drawable.trevo_lotomania, R.color.lotomaniaclaro, false);
        TipoAposta tipoAposta12 = new TipoAposta("lotomania", R.drawable.trevo_lotomania, R.color.lotomaniaclaro, false);
        TipoAposta tipoAposta13 = new TipoAposta("lotomania", R.drawable.trevo_lotomania, R.color.lotomaniaclaro, false);
        tipoApostaLista = new TipoAposta[13];
        tipoApostaLista[0] = tipoAposta;
        tipoApostaLista[1] = tipoAposta2;
        tipoApostaLista[2] = tipoAposta3;
        tipoApostaLista[3] = tipoAposta4;
        tipoApostaLista[4] = tipoAposta5;
        tipoApostaLista[5] = tipoAposta6;
        tipoApostaLista[6] = tipoAposta7;
        tipoApostaLista[7] = tipoAposta8;
        tipoApostaLista[8] = tipoAposta9;
        tipoApostaLista[9] = tipoAposta10;
        tipoApostaLista[10] = tipoAposta11;
        tipoApostaLista[11] = tipoAposta12;
        tipoApostaLista[12] = tipoAposta13;

        //gridview

        ExpandableHeightGridView gridViewTiposAposta = (ExpandableHeightGridView)findViewById(R.id.gridviewTiposApostasRapidaoSelecionar);
        TiposApostasMultiplaSelecaoAdapter tiposApostasAdapter = new TiposApostasMultiplaSelecaoAdapter(this, this, tipoApostaLista);
        gridViewTiposAposta.setAdapter(tiposApostasAdapter);
        gridViewTiposAposta.setExpanded(true);
    }

    public void mudarLayoutBotaoSalvar(Boolean ativo) {
        if (ativo) {
            salvarButtonRapidaoSelecionados.setBackgroundColor(getResources().getColor(R.color.verdeazul));
            salvarButtonRapidaoSelecionados.setClickable(true);
        }else {
            salvarButtonRapidaoSelecionados.setBackgroundColor(getResources().getColor(R.color.branco));
            salvarButtonRapidaoSelecionados.setClickable(false);
        }
    }

}
