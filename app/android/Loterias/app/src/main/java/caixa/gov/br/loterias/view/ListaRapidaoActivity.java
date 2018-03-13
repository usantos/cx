package caixa.gov.br.loterias.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.VolleyError;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.model.bo.ApostaBO;
import caixa.gov.br.loterias.model.bo.silce.SilceClient;
import caixa.gov.br.loterias.model.bo.silce.dto.CarrinhoDTO;
import caixa.gov.br.loterias.model.bo.silce.dto.CarrinhoDTOResponse;
import caixa.gov.br.loterias.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import caixa.gov.br.loterias.model.bo.MensagensNetwork;
import caixa.gov.br.loterias.utils.ViewUtils;
import caixa.gov.br.loterias.view.adapter.CarrinhoApostasListViewRapidaoAdapter;
import caixa.gov.br.loterias.view.custom.ExpandableHeightRecyclerView;
import caixa.gov.br.loterias.view.custom.LoadingViewLoterias;

public class ListaRapidaoActivity extends AppCompatActivity {


    public CarrinhoDTO carrinho;
    private ExpandableHeightRecyclerView recyclerView;
    public List<IdentificaoDeUmaApostaDas8Modalidades> apostas;
    private TextView valorTotalCarrinhoRapidao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_rapidao);
        setTitle(ViewUtils.textFuturaAndFuturaBold(this, getString(R.string.title_activity_lista_rapidao)));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        recyclerView = findViewById(R.id.apostasListaRecycleViewRapidao);
        valorTotalCarrinhoRapidao = findViewById(R.id.valorTotalCarrinhoRapidao);


        callWebservice(0);

        ViewUtils.organizaTabLayout(this, R.id.carrinhoRapidaoMainLayout, R.id.apostasListaRecycleViewRapidao, R.id.carrinhoRapidaoScrollView);
    }





    public void atualizaLayout() {
        configuraActivityCarrinho();
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
            return true;
        }
        if (id == R.id.action_config_rapidao) {
            abrirConfigRapidao();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void abrirConfigRapidao() {
        Intent activity = new Intent(this, RapidaoConfigActivity.class);
        startActivityForResult(activity, 1);
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


        findViewById(R.id.botaoFazerNovaApostaRapidao).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callWebservice(1);

            }
        });

//        findViewById(R.id.imagemLixeiraCarrinhoRapidao).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                limpaCarrinho();
//            }
//        });
//
//        findViewById(R.id.textoLimparCarrinhoRapidao).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                limpaCarrinho();
//            }
//        });

        findViewById(R.id.botaoAvancarPagamentoRapidao).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent activity = new Intent(ListaRapidaoActivity.this, FormaPagamentoActivity.class);

                startActivity(activity);

            }
        });
    }

    private void configuraRecyclerViewCarrinhoAposta() {
        RecyclerView.LayoutManager layout = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);

        recyclerView.setAdapter(new CarrinhoApostasListViewRapidaoAdapter(this, (ArrayList<IdentificaoDeUmaApostaDas8Modalidades>) apostas, this));
        recyclerView.setLayoutManager(layout);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setExpanded(true);
        valorTotal();
    }

//    private void limpaCarrinho(){
//        ViewUtils.alert(this, "LIMPAR CARRINHO");
//    }


    private void callWebservice(int quantidade) {

        final View loadViewProgress = LoadingViewLoterias.init(this);
        final ApostaBO apostaBO = new ApostaBO(ListaRapidaoActivity.this);

        apostaBO.gerarApostas(quantidade, new SilceClient.Listener<CarrinhoDTOResponse>() {
            @Override
            public void onResponse(CarrinhoDTOResponse response) {
                LoadingViewLoterias.stop(loadViewProgress);
                carrinho = response.getPayload();
                apostas = carrinho.getApostas();
                atualizaLayout();
            }
        }, new SilceClient.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LoadingViewLoterias.stop(loadViewProgress);
                ViewUtils.alert(ListaRapidaoActivity.this, MensagensNetwork.setMensagem(error, getApplicationContext()));
            }
        });
    }

    private void valorTotal(){
        BigDecimal valorTotal = BigDecimal.ZERO;

        for (IdentificaoDeUmaApostaDas8Modalidades aposta: apostas){
            valorTotal = valorTotal.add(aposta.getValor());
        }

        ViewUtils.setMoedaFormatHtml(valorTotal, valorTotalCarrinhoRapidao);
    }

}
