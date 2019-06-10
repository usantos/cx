package br.gov.caixa.loterias.apostar.interno.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.VolleyError;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostar.interno.R;
import br.gov.caixa.loterias.apostar.interno.model.bo.ApostaBO;
import br.gov.caixa.loterias.apostar.interno.model.bo.ApostaBO_;
import br.gov.caixa.loterias.apostar.interno.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostar.interno.model.bo.RequestListener;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.CarrinhoDTOResponse;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ResourceResponse;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.internals.CarrinhoSingleton;
import br.gov.caixa.loterias.apostar.interno.utils.DialogUtils;
import br.gov.caixa.loterias.apostar.interno.utils.FirebaseManager;
import br.gov.caixa.loterias.apostar.interno.utils.ViewUtils;
import br.gov.caixa.loterias.apostar.interno.view.adapter.CarrinhoApostasListViewRapidaoAdapter;
import br.gov.caixa.loterias.apostar.interno.view.custom.ExpandableHeightRecyclerView;
import br.gov.caixa.loterias.apostar.interno.view.custom.LoadingViewLoterias;

public class ListaRapidaoActivity extends AppCompatActivity {

    private ExpandableHeightRecyclerView recyclerView;
    public List<IdentificaoDeUmaApostaDas8Modalidades> apostas;
    private TextView valorTotalCarrinhoRapidao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_rapidao);
        setTitle(ViewUtils.textFuturaAndFuturaBoldTitle(this, getString(R.string.title_activity_lista_rapidao)));
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        configuraActivityCarrinho();

        recyclerView = findViewById(R.id.apostasListaRecycleViewRapidao);

        valorTotalCarrinhoRapidao = findViewById(R.id.valorTotalCarrinhoRapidao);
        FirebaseManager.registraEvento(ListaRapidaoActivity.this ,"ENTROU_LISTA_APOSTAS_RAPIDAO");

        ViewUtils.organizaTabLayout(this, R.id.carrinhoRapidaoMainLayout, R.id.apostasListaRecycleViewRapidao, R.id.carrinhoRapidaoScrollView);
    }

    @Override
    protected void onResume() {
        super.onResume();

        callWebservice(0);
    }

    public void atualizaLayout() {
        if (apostas != null) {
            configuraRecyclerViewCarrinhoAposta();
        }
        configuraListeners();
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
        getMenuInflater().inflate(R.menu.rapidao, menu);

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

        if (id == R.id.action_duvida) {
            abrirTermosUso();
            return true;
        }
        if (id == R.id.action_config_rapidao) {
            abrirConfigRapidao();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void abrirTermosUso(){
        Intent activity = new Intent(ListaRapidaoActivity.this, TermosUsoActivity.class);
        startActivity(activity);
    }

    private void abrirConfigRapidao() {
        RapidaoConfigActivity_.intent(this).startForResult(1);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1) {
            callWebservice(0);
        }
    }

    private void configuraActivityCarrinho() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        getSupportActionBar().setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.navigation_gradient, null));
    }

    private void configuraListeners() {
        ((Button) findViewById(R.id.botaoAvancarPagamentoRapidao)).setText(ViewUtils.textFuturaAndFuturaBold(this, getString(R.string.avancarFormaPagamento)));


        findViewById(R.id.botaoFazerNovaApostaRapidao).setOnClickListener(view -> callWebservice(1));


        findViewById(R.id.botaoAvancarPagamentoRapidao).setOnClickListener(view -> validarCarrinho());
    }
    private void validarCarrinho() {
        DialogUtils.show(ListaRapidaoActivity.this);
        ApostaBO_.getInstance_(ListaRapidaoActivity.this).validarCarrinho(new RequestListener<ResourceResponse>() {
            @Override
            public void onResponse(ResourceResponse result) {
                DialogUtils.dismiss();
                startActivity(new Intent(ListaRapidaoActivity.this, FormaPagamentoActivity.class));
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                DialogUtils.dismiss();
                RedirectNetwork.checkRedirect(error, ListaRapidaoActivity.this);

            }
        });
    }

    private void configuraRecyclerViewCarrinhoAposta() {
        RecyclerView.LayoutManager layout = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        CarrinhoApostasListViewRapidaoAdapter carrinhoApostasListViewRapidaoAdapter =
                new CarrinhoApostasListViewRapidaoAdapter(this, (ArrayList<IdentificaoDeUmaApostaDas8Modalidades>) apostas, this);
        recyclerView.setAdapter(carrinhoApostasListViewRapidaoAdapter);
        carrinhoApostasListViewRapidaoAdapter.setOnCarrinhoApostasRapidaoListener(apostaDas8Modalidades -> callWebserviceRemoveItem(apostaDas8Modalidades));
        recyclerView.setLayoutManager(layout);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setExpanded(true);
        valorTotal();
    }


    private void callWebserviceRemoveItem(IdentificaoDeUmaApostaDas8Modalidades identificaoDeUmaApostaDas8Modalidades){
        final AlertDialog loadViewProgress = LoadingViewLoterias.show(this);
        final ApostaBO apostaBO = ApostaBO_.getInstance_(ListaRapidaoActivity.this);

        apostaBO.deleteApostaCarrinho(identificaoDeUmaApostaDas8Modalidades.getId().toString(), new RequestListener<CarrinhoDTOResponse>() {
            @Override
            public void onResponse(CarrinhoDTOResponse response) {
                loadViewProgress.dismiss();
                CarrinhoSingleton.getInstance().setCarrinho(response.getPayload());
                apostas = null;
                apostas = CarrinhoSingleton.getInstance().getCarrinho().getApostas();
                atualizaLayout();
                if (response.getRedirect() != null){
                    RedirectNetwork.checkRedirectSucesso( response.getRedirect(), ListaRapidaoActivity.this);
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                loadViewProgress.dismiss();
                RedirectNetwork.checkRedirect( error, ListaRapidaoActivity.this );
            }
        });

    }


    private void callWebservice(int quantidade) {

        final AlertDialog loadViewProgress = LoadingViewLoterias.show(this);
        final ApostaBO apostaBO = ApostaBO_.getInstance_(ListaRapidaoActivity.this);

        apostaBO.gerarApostas(quantidade, new RequestListener<CarrinhoDTOResponse>() {
            @Override
            public void onResponse(CarrinhoDTOResponse response) {
                loadViewProgress.dismiss();
                FirebaseManager.registraEvento(ListaRapidaoActivity.this ,"GEROU_LISTA_APOSTAS_RAPIDAO");

                CarrinhoSingleton.getInstance().setCarrinho(response.getPayload());
                apostas = null;
                apostas = CarrinhoSingleton.getInstance().getCarrinho().getApostas();
                atualizaLayout();
                if (response.getRedirect() != null){
                    RedirectNetwork.checkRedirectSucesso( response.getRedirect(), ListaRapidaoActivity.this);
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                loadViewProgress.dismiss();
                RedirectNetwork.checkRedirect( error, ListaRapidaoActivity.this );
            }
        });
    }

    private void valorTotal(){
        BigDecimal valorTotal = BigDecimal.ZERO;

        for (IdentificaoDeUmaApostaDas8Modalidades aposta: apostas){
            valorTotal = valorTotal.add(aposta.getValor());
        }

        valorTotalCarrinhoRapidao.setText("Total: "+ ViewUtils.getMoedaFormat(valorTotal));
    }
}
