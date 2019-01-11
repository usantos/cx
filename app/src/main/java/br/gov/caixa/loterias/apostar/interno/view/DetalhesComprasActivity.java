package br.gov.caixa.loterias.apostar.interno.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostar.interno.R;
import br.gov.caixa.loterias.apostar.interno.model.bo.ApostaBO;
import br.gov.caixa.loterias.apostar.interno.model.bo.ApostaBO_;
import br.gov.caixa.loterias.apostar.interno.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostar.interno.model.bo.RequestListener;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.AgrupadorApostaCompraDTOResponse;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ApostaDTO;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.CompraDTO;
import br.gov.caixa.loterias.apostar.interno.utils.ViewUtils;
import br.gov.caixa.loterias.apostar.interno.view.adapter.ListaComprasDetalhesAdapter;
import br.gov.caixa.loterias.apostar.interno.view.custom.LoadingViewLoterias;

public class DetalhesComprasActivity extends AppCompatActivity {

    CompraDTO compraDTO;
    List<ApostaDTO> listaAposta = new ArrayList<>();
    private ArrayList<String> tiposApostas = new ArrayList<>();
    int ordemCompra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_compras);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(ViewUtils.textFuturaAndFuturaBoldTitle(this, getString(R.string.texto_futura_bold, getString(R.string.title_activity_detalhes_compras))));


        String compraDTOJson = getIntent().getExtras().getString("CompraDTO");
        ordemCompra = getIntent().getExtras().getInt("ordem");
        compraDTO = new Gson().fromJson(compraDTOJson, CompraDTO.class);

        criarCabecalho();
        callWebService();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1) {
            callWebService();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
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

    private void criarCabecalho() {
        FrameLayout setaIndicador = findViewById(R.id.setaComprasGroup);
        setaIndicador.setVisibility(View.INVISIBLE);

        TextView dataCompra = findViewById(R.id.dataCompra);
        dataCompra.setText(compraDTO.getDataEfetivacaoDebito());
        TextView valorCompra = findViewById(R.id.valorCompra);
        valorCompra.setText(br.gov.caixa.loterias.apostar.interno.utils.ViewUtils.getMoedaFormat(compraDTO.getValorTotal()));

        TextView itemCompraOrdemTextView = findViewById(R.id.itemCompraOrdemTextView);
        itemCompraOrdemTextView.setText(getString(R.string.label_compra_valor, ordemCompra));
    }

    private void abrirTermosUso() {
        Intent activity = new Intent(this, TermosUsoActivity.class);
        startActivity(activity);
    }


    private void montarLayout() {
        RecyclerView listView = findViewById(R.id.gridviewDetalhesCompras);
        listView.setLayoutManager(new LinearLayoutManager(this));
        ListaComprasDetalhesAdapter listaAdapter = new ListaComprasDetalhesAdapter(this, this, listaAposta);
        listView.setAdapter(listaAdapter);

    }

    private void callWebService() {

        final AlertDialog loadViewProgress = LoadingViewLoterias.show(this);
        final ApostaBO apostaBO = ApostaBO_.getInstance_(DetalhesComprasActivity.this);

        apostaBO.getDetalhesCompras(compraDTO.getId().toString(), new RequestListener<AgrupadorApostaCompraDTOResponse>() {
            @Override
            public void onResponse(AgrupadorApostaCompraDTOResponse response) {
                loadViewProgress.dismiss();
                listaAposta = response.getPayload().getApostas();

                tiposApostas = new ArrayList<>();
                for(ApostaDTO aposta : listaAposta){
                    tiposApostas.add(aposta.getModalidade().toString());
                }

                montarLayout();
                if (response.getRedirect() != null) {
                    RedirectNetwork.checkRedirectSucesso(response.getRedirect(), DetalhesComprasActivity.this);
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                loadViewProgress.dismiss();
                montarLayout();
                RedirectNetwork.checkRedirect(error, DetalhesComprasActivity.this);
            }
        });
    }

}
