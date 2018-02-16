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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.bo.ApostaBO;
import caixa.gov.br.loterias.bo.silce.SilceClient;
import caixa.gov.br.loterias.bo.silce.dto.CarrinhoDTO;
import caixa.gov.br.loterias.bo.silce.dto.CarrinhoDTOResponse;
import caixa.gov.br.loterias.bo.silce.dto.DTOEnumCharacter;
import caixa.gov.br.loterias.bo.silce.dto.DTOEnumInteger;
import caixa.gov.br.loterias.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import caixa.gov.br.loterias.bo.silce.dto.ModalidadeEnum;
import caixa.gov.br.loterias.utils.MensagensNetwork;
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
//        mockCarrinho();

        ViewUtils.organizaTabLayout(this, R.id.carrinhoRapidaoMainLayout, R.id.apostasListaRecycleViewRapidao, R.id.carrinhoRapidaoScrollView);
    }


    private void mockCarrinho() {
        carrinho = new CarrinhoDTO();

        apostas = new ArrayList<IdentificaoDeUmaApostaDas8Modalidades>();

        IdentificaoDeUmaApostaDas8Modalidades aposta = new IdentificaoDeUmaApostaDas8Modalidades();
        aposta.setId(new Long(0));
        aposta.setModalidade(ModalidadeEnum.MEGA_SENA);

        DTOEnumCharacter tipoConcurso = new DTOEnumCharacter();

        String valorTipoConcurso = "1";
        tipoConcurso.setValor(valorTipoConcurso);
        tipoConcurso.setDescricao("Teste");
        aposta.setTipoConcurso(tipoConcurso);
        aposta.setPartidasLotogol(null);
        aposta.setPartidasLoteca(null);

        List<Integer> thisIsAnIntArray = new ArrayList<Integer>();
        thisIsAnIntArray.add(1);
        thisIsAnIntArray.add(2);
        thisIsAnIntArray.add(3);
        thisIsAnIntArray.add(4);
        thisIsAnIntArray.add(6);
        thisIsAnIntArray.add(5);

        DTOEnumInteger indicadorSurpresinha = new DTOEnumInteger();
        indicadorSurpresinha.setValor(1);
        indicadorSurpresinha.setDescricao("");

        aposta.setIndicadorSurpresinha(indicadorSurpresinha);
        aposta.setValor(new BigDecimal(10.0));
        aposta.setConcursoAlvo(12334);
        aposta.setQuantidadeTeimosinhas(1);
        aposta.setQuantidadeApostas(1);
        aposta.setEspelho(false);

        aposta.setTimeDoCoracao(null);

        aposta.setConcursoInicial(0);
        aposta.setSituacao(null);
        aposta.setTroca(false);

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();

        DateFormat hourFormat = new SimpleDateFormat("HH:mm:ss");
        Date hour = new Date();

        aposta.setDataEfetivacao(dateFormat.format(date));
        aposta.setHoraEfetivacao(hourFormat.format(hour));
        aposta.setVinculoEspelho(new Long(0));
        aposta.setQuantidadeNumeros(6);
        aposta.setSurpresinha(false);
        aposta.setNumerosSelecionados(thisIsAnIntArray);

        apostas.add(aposta);
        apostas.add(aposta);
        apostas.add(aposta);
        apostas.add(aposta);
        apostas.add(aposta);
        apostas.add(aposta);
        apostas.add(aposta);
        apostas.add(aposta);
        apostas.add(aposta);
        apostas.add(aposta);
        apostas.add(aposta);
        apostas.add(aposta);
        apostas.add(aposta);
        apostas.add(aposta);
        apostas.add(aposta);
        apostas.add(aposta);
        apostas.add(aposta);
        apostas.add(aposta);
        apostas.add(aposta);
        apostas.add(aposta);
        apostas.add(aposta);
        apostas.add(aposta);
        apostas.add(aposta);
        apostas.add(aposta);
        apostas.add(aposta);
        apostas.add(aposta);


        carrinho.setApostas(apostas);

        atualizaLayout();
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
        startActivity(activity);
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
