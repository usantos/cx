package caixa.gov.br.loterias.view;

import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.model.bo.ApostaBO;
import caixa.gov.br.loterias.model.bo.ApostaBO_;
import caixa.gov.br.loterias.model.bo.RedirectNetwork;
import caixa.gov.br.loterias.model.bo.RequestListener;
import caixa.gov.br.loterias.model.bo.silce.dto.AgrupadorApostaCompraDTOResponse;
import caixa.gov.br.loterias.model.bo.silce.dto.ApostaDTO;
import caixa.gov.br.loterias.model.bo.silce.dto.CompraDTO;
import caixa.gov.br.loterias.model.bo.MensagensNetwork;
import caixa.gov.br.loterias.utils.ViewUtils;
import caixa.gov.br.loterias.view.adapter.ListaComprasDetalhesAdapter;
import caixa.gov.br.loterias.view.custom.LoadingViewLoterias;

public class DetalhesComprasActivity extends AppCompatActivity {

    CompraDTO compraDTO;
    List<ApostaDTO> listaAposta = new ArrayList<>();
    int ordemCompra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_compras);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        String compraDTOJson =  getIntent().getExtras().getString("CompraDTO");
        ordemCompra = getIntent().getExtras().getInt("ordem");
        compraDTO = new Gson().fromJson(compraDTOJson, CompraDTO.class);

        callWebService();

    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }


    private void montarLayout(){
        ListView listView = (ListView)findViewById(R.id.gridviewDetalhesCompras);
        ListaComprasDetalhesAdapter listaAdapter = new ListaComprasDetalhesAdapter(this, this, listaAposta, compraDTO, ordemCompra);
        listView.setAdapter(listaAdapter);

    }

    private void callWebService(){

        final AlertDialog loadViewProgress = LoadingViewLoterias.show(this);
        final ApostaBO apostaBO = ApostaBO_.getInstance_(DetalhesComprasActivity.this);

        apostaBO.getDetalhesCompras(compraDTO.getId().toString() ,new RequestListener<AgrupadorApostaCompraDTOResponse>() {
            @Override
            public void onResponse(AgrupadorApostaCompraDTOResponse response) {
                loadViewProgress.dismiss();
                listaAposta = response.getPayload().getApostas();
                montarLayout();
                if (response.getRedirect() != null){
                    RedirectNetwork.checkRedirectSucesso( response.getRedirect().toString(), DetalhesComprasActivity.this);
                }
            }
            @Override
            public void onErrorResponse(VolleyError error) {
                loadViewProgress.dismiss();
                montarLayout();
                RedirectNetwork.checkRedirect( error, DetalhesComprasActivity.this );
            }
        });
    }

}
