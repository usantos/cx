package caixa.gov.br.loterias.view;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.TextView;



import com.android.volley.VolleyError;


import java.util.ArrayList;
import java.util.List;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.bo.ApostaBO;
import caixa.gov.br.loterias.bo.silce.SilceClient;
import caixa.gov.br.loterias.bo.silce.dto.AgrupadorDTOApostaFavoritaDTO;
import caixa.gov.br.loterias.bo.silce.dto.AgrupadorDTOApostaFavoritaDTOResponse;
import caixa.gov.br.loterias.bo.silce.dto.ApostaFavoritaDTO;
import caixa.gov.br.loterias.bo.silce.dto.DTOEnumInteger;
import caixa.gov.br.loterias.bo.silce.dto.ParametroEquipe;
import caixa.gov.br.loterias.utils.LoadingViewLoterias;
import caixa.gov.br.loterias.utils.MensagensNetwork;
import caixa.gov.br.loterias.utils.ViewUtils;
import caixa.gov.br.loterias.view.adapter.ApostaFavoritaAdapter;
import caixa.gov.br.loterias.view.adapter.FavoritasAdapter;
import caixa.gov.br.loterias.view.custom.ExpandableHeightGridView;


public class FavoritasActivity extends AppCompatActivity {

    AgrupadorDTOApostaFavoritaDTO agrupadorDTOApostaFavoritaDTO;

    List<ApostaFavoritaDTO> apostaFavoritaLista = new ArrayList<ApostaFavoritaDTO>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favoritas);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        callWebservice();


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
        Intent activity = new Intent(this, TermosUsoActivity.class);
        startActivity(activity);
    }

    private void atualizaLayout(){
        TextView tituloFavoritas = (TextView) findViewById(R.id.tituloFavoritas);
        TextView subTituloFavoritas = (TextView) findViewById(R.id.subTituloFavoritas);
        tituloFavoritas.setText("Os seus números da sorte\nno mesmo lugar");
        subTituloFavoritas.setText("Nesta área estão todas as suas apostas da sorte.\nÉ só escolher e apostar. Boa Sorte!");

        montarListaFavoritas();
    }

    private void montarListaFavoritas(){

    }

    private void callWebservice() {

        final View loadViewProgress = LoadingViewLoterias.init(this);
        final ApostaBO apostaBO = new ApostaBO(this);

        apostaBO.getApostasFavoritas(new SilceClient.Listener<AgrupadorDTOApostaFavoritaDTOResponse>() {
            @Override
            public void onResponse(AgrupadorDTOApostaFavoritaDTOResponse response) {
                LoadingViewLoterias.stop(loadViewProgress);

//                agrupadorDTOApostaFavoritaDTO = response.getPayload();

                ApostaFavoritaDTO apostaFavoritaDTO = new ApostaFavoritaDTO();
                apostaFavoritaDTO.setId(new Long(1));
                apostaFavoritaDTO.setNome("Favorita xxx");
                apostaFavoritaDTO.setModalidade(new DTOEnumInteger());
                apostaFavoritaDTO.setTimeDoCoracao(new ParametroEquipe());
                List<Integer> numerosLista = new ArrayList<Integer>();
                numerosLista.add(1);
                numerosLista.add(10);
                numerosLista.add(50);
                apostaFavoritaDTO.setNumerosSelecionados(numerosLista);

                apostaFavoritaLista.add(apostaFavoritaDTO);
                apostaFavoritaLista.add(apostaFavoritaDTO);
                apostaFavoritaLista.add(apostaFavoritaDTO);
                apostaFavoritaLista.add(apostaFavoritaDTO);

                //gridview
                ExpandableHeightGridView gridViewApostaFavorita = (ExpandableHeightGridView)findViewById(R.id.gridViewFavoritas);
                FavoritasAdapter favoritasAdapter = new FavoritasAdapter(FavoritasActivity.this, apostaFavoritaLista);
                gridViewApostaFavorita.setAdapter(favoritasAdapter);
                gridViewApostaFavorita.setExpanded(true);

                atualizaLayout();
            }
        }, new SilceClient.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LoadingViewLoterias.stop(loadViewProgress);
                ViewUtils.alert(FavoritasActivity.this, MensagensNetwork.setMensagem(error, getApplicationContext()));
            }
        });
    }




    //


}
