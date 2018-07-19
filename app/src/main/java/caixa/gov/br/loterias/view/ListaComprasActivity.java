package caixa.gov.br.loterias.view;

import android.support.v7.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.List;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.model.bo.ApostaBO;
import caixa.gov.br.loterias.model.bo.ApostaBO_;
import caixa.gov.br.loterias.model.bo.RedirectNetwork;
import caixa.gov.br.loterias.model.bo.RequestListener;
import caixa.gov.br.loterias.model.bo.silce.dto.CompraDTO;
import caixa.gov.br.loterias.model.bo.silce.dto.MesAnoDTO;
import caixa.gov.br.loterias.model.bo.silce.dto.MesAnoDTOResponse;
import caixa.gov.br.loterias.model.bo.silce.dto.ResultadoPesquisaPaginadaDTOCompraDTOResponse;
import caixa.gov.br.loterias.model.bo.MensagensNetwork;
import caixa.gov.br.loterias.utils.ViewUtils;
import caixa.gov.br.loterias.view.adapter.ListaComprasRecyclerViewAdapter;
import caixa.gov.br.loterias.view.custom.LoadingViewLoterias;

public class ListaComprasActivity extends AppCompatActivity {



    List<MesAnoDTO> listaMesAnoDTO;
    List<CompraDTO> listaComprasDto;
    Boolean isMaisCompras = true;
    Boolean bloquearRequestScroll = false;
    int offsetItem = 0;
    int mes;
    int ano;

    ListaComprasRecyclerViewAdapter listaComprasRecyclerViewAdapter;

    LinearLayoutManager layoutManager;
    public RecyclerView recyclerViewCompras;
    public ProgressBar progressLoadingMore;
    NestedScrollView scrollViewContentViewCompras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_compras);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView textBottomInformacaoTextView = (TextView) findViewById(R.id.textBottomInformacaoTextView);
        textBottomInformacaoTextView.setText("Compras mais antigas\npodem ser consultadas no site");
        scrollViewContentViewCompras = (NestedScrollView) findViewById(R.id.scrollViewContentViewCompras);

        progressLoadingMore = (ProgressBar) findViewById(R.id.compras_loading_more);
        recyclerViewCompras = (RecyclerView)findViewById(R.id.recyclerCompras);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        callWebServiceMesCompras();


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1) {
            callWebServiceMesCompras();
        }
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

    private void callWebServiceCompras(){
        offsetItem = 0;
        isMaisCompras = true;
        bloquearRequestScroll = false;
        final AlertDialog loadViewProgress = LoadingViewLoterias.show(this);
        final ApostaBO apostaBO = ApostaBO_.getInstance_(ListaComprasActivity.this);

        apostaBO.getCompras(mes, ano, offsetItem ,new RequestListener<ResultadoPesquisaPaginadaDTOCompraDTOResponse>() {
            @Override
            public void onResponse(ResultadoPesquisaPaginadaDTOCompraDTOResponse response) {
                loadViewProgress.dismiss();
                listaComprasDto = response.getPayload().getLista();
                offsetItem = 5;
                initRecyclerView();
                if (response.getRedirect() != null){
                    RedirectNetwork.checkRedirectSucesso( response.getRedirect(), ListaComprasActivity.this);
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                loadViewProgress.dismiss();
                RedirectNetwork.checkRedirect( error, ListaComprasActivity.this );
            }
        });
    }


    private void callWebServiceMesCompras(){

        final AlertDialog loadViewProgress = LoadingViewLoterias.show(this);
        final ApostaBO apostaBO = ApostaBO_.getInstance_(ListaComprasActivity.this);

        apostaBO.getMesesCompras(new RequestListener<MesAnoDTOResponse>() {
            @Override
            public void onResponse(MesAnoDTOResponse response) {
                loadViewProgress.dismiss();
                listaMesAnoDTO = response.getPayload();

                addSpinner();
                mes = listaMesAnoDTO.get(0).getMes().getValor().intValue();
                ano = listaMesAnoDTO.get(0).getAno().intValue();

                callWebServiceCompras();
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                loadViewProgress.dismiss();
                RedirectNetwork.checkRedirect( error, ListaComprasActivity.this );

            }
        });
    }




    private void addSpinner(){
        Spinner SelecaoMesSpinner;
        SelecaoMesSpinner = (Spinner) findViewById(R.id.SelecaoMesSpinner);
        List<String> list = new ArrayList<String>();
        for(MesAnoDTO item : listaMesAnoDTO){
            list.add(item.getMes().getDescricao() + " de " + item.getAno());
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item,list);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SelecaoMesSpinner.setAdapter(dataAdapter);
        SelecaoMesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // Your code here
                mes = listaMesAnoDTO.get(i).getMes().getValor().intValue();
                ano = listaMesAnoDTO.get(i).getAno().intValue();
                callWebServiceCompras();

            }

            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });
    }



    private void initRecyclerView() {
        layoutManager = new LinearLayoutManager(this);
        recyclerViewCompras.setHasFixedSize(true);

        listaComprasRecyclerViewAdapter = new ListaComprasRecyclerViewAdapter(this, listaComprasDto);
        recyclerViewCompras.setAdapter(listaComprasRecyclerViewAdapter);
        recyclerViewCompras.setLayoutManager(layoutManager);


        scrollViewContentViewCompras.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                View view = (View) v.getChildAt(v.getChildCount() - 1);
                int diff = (view.getBottom() - (v.getHeight() + v.getScrollY()));

                // if diff is zero, then the bottom has been reached
                if (diff == 0 && bloquearRequestScroll == false) {
                    // do stuff
                    getComprasScroll();
                    bloquearRequestScroll = true;
                }
            }
        });




    }

    private void getComprasScroll(){
        if (isMaisCompras == false) {
            return;
        }
        final ApostaBO apostaBO = ApostaBO_.getInstance_(ListaComprasActivity.this);
        progressLoadingMore.setVisibility(View.VISIBLE);

        apostaBO.getCompras(mes, ano, offsetItem, new RequestListener<ResultadoPesquisaPaginadaDTOCompraDTOResponse>() {
            @Override
            public void onResponse(ResultadoPesquisaPaginadaDTOCompraDTOResponse response) {
                progressLoadingMore.setVisibility(View.GONE);
                if(response.getPayload().getLista().size() == 0){
                    isMaisCompras = false;
                }else{
                    listaComprasDto.addAll(response.getPayload().getLista());
                    listaComprasRecyclerViewAdapter.notifyDataSetChanged();
                    offsetItem = offsetItem +5;
                }
                bloquearRequestScroll = false;
                if (response.getRedirect() != null){
                    RedirectNetwork.checkRedirectSucesso( response.getRedirect(), ListaComprasActivity.this);
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                progressLoadingMore.setVisibility(View.GONE);
                bloquearRequestScroll = false;
                RedirectNetwork.checkRedirect( error, ListaComprasActivity.this );
            }
        });
    }







}
