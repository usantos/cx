package caixa.gov.br.loterias.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.bo.silce.dto.DetalhesPremioDTO;
import caixa.gov.br.loterias.view.adapter.ListaComprasAdapter;
import caixa.gov.br.loterias.view.adapter.ListaComprasDetalhesAdapter;

public class DetalhesComprasActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_compras);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        montarLayout();
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }


    private void montarLayout(){
        List<DetalhesPremioDTO> listaPremio = new ArrayList<DetalhesPremioDTO>();
        DetalhesPremioDTO det = new DetalhesPremioDTO();
        listaPremio.add(det);
        listaPremio.add(det);
        listaPremio.add(det);
        listaPremio.add(det);
        listaPremio.add(det);

        ListView listView = (ListView)findViewById(R.id.gridviewDetalhesCompras);
        ListaComprasDetalhesAdapter listaAdapter = new ListaComprasDetalhesAdapter(this, listaPremio);
        listView.setAdapter(listaAdapter);

    }

}
