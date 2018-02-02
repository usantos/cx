package caixa.gov.br.loterias.view;

import android.content.Intent;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.bo.ApostaBO;
import caixa.gov.br.loterias.bo.silce.SilceClient;
import caixa.gov.br.loterias.bo.silce.dto.CarrinhoDTOResponse;
import caixa.gov.br.loterias.bo.silce.dto.DetalhesPremioDTO;
import caixa.gov.br.loterias.bo.silce.dto.ResultadoPesquisaPaginadaCompraDTO;
import caixa.gov.br.loterias.bo.silce.dto.ResultadoPesquisaPaginadaDTOCompraDTOResponse;
import caixa.gov.br.loterias.utils.ExpandableListDataPump;
import caixa.gov.br.loterias.utils.MensagensNetwork;
import caixa.gov.br.loterias.utils.ViewUtils;
import caixa.gov.br.loterias.view.adapter.ListaComprasAdapter;
import caixa.gov.br.loterias.view.adapter.ListaComprasExpandleAdapter;
import caixa.gov.br.loterias.view.adapter.TiposApostasMultiplaSelecaoAdapter;
import caixa.gov.br.loterias.view.custom.ExpandableHeightGridView;
import caixa.gov.br.loterias.view.custom.LoadingViewLoterias;

public class ListaComprasActivity extends AppCompatActivity {

//    ExpandableListView expandableListView;
//    ExpandableListAdapter expandableListAdapter;
//    List<String> expandableListTitle;
//    HashMap<String, List<String>> expandableListDetail;

    ResultadoPesquisaPaginadaCompraDTO resultadoPesquisaPaginadaCompraDTO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_compras);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        callWebService();

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

    private void callWebService(){

        final View loadViewProgress = LoadingViewLoterias.init(this);
        final ApostaBO apostaBO = new ApostaBO(ListaComprasActivity.this);

        apostaBO.getCompras(new SilceClient.Listener<ResultadoPesquisaPaginadaDTOCompraDTOResponse>() {
            @Override
            public void onResponse(ResultadoPesquisaPaginadaDTOCompraDTOResponse response) {
                LoadingViewLoterias.stop(loadViewProgress);
                resultadoPesquisaPaginadaCompraDTO = response.getPayload();

                montarLayout();
            }
        }, new SilceClient.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LoadingViewLoterias.stop(loadViewProgress);
                ViewUtils.alert(ListaComprasActivity.this, MensagensNetwork.setMensagem(error, getApplicationContext()));
            }
        });
    }

    private void montarLayout(){
        ListView listView = (ListView)findViewById(R.id.gridviewCompras);
        ListaComprasAdapter listaAdapter = new ListaComprasAdapter(this, resultadoPesquisaPaginadaCompraDTO.getLista());
        listView.setAdapter(listaAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent activity = new Intent(ListaComprasActivity.this, DetalhesComprasActivity.class);
                String arrayAsString = new Gson().toJson(resultadoPesquisaPaginadaCompraDTO.getLista().get(position));
                activity.putExtra("CompraDTO", arrayAsString);
                startActivity(activity);
            }
        });
    }


//    private void layoutListView(){
//        expandableListView = (ExpandableListView) findViewById(R.id.comprasExpandableListView);
//        expandableListDetail = ExpandableListDataPump.getData();
//        expandableListTitle = new ArrayList<String>(expandableListDetail.keySet());
//        expandableListAdapter = new ListaComprasExpandleAdapter(this, expandableListTitle, expandableListDetail);
//        expandableListView.setAdapter(expandableListAdapter);
//        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
//
//            @Override
//            public void onGroupExpand(int groupPosition) {
//                Toast.makeText(getApplicationContext(),
//                        expandableListTitle.get(groupPosition) + " List Expanded.",
//                        Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
//
//            @Override
//            public void onGroupCollapse(int groupPosition) {
//                Toast.makeText(getApplicationContext(),
//                        expandableListTitle.get(groupPosition) + " List Collapsed.",
//                        Toast.LENGTH_SHORT).show();
//
//            }
//        });
//
//        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
//            @Override
//            public boolean onChildClick(ExpandableListView parent, View v,
//                                        int groupPosition, int childPosition, long id) {
//                Toast.makeText(
//                        getApplicationContext(),
//                        expandableListTitle.get(groupPosition)
//                                + " -> "
//                                + expandableListDetail.get(
//                                expandableListTitle.get(groupPosition)).get(
//                                childPosition), Toast.LENGTH_SHORT
//                ).show();
//                return false;
//            }
//        });
//    }

}
