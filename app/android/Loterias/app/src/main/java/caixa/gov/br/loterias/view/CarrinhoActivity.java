package caixa.gov.br.loterias.view;

import android.content.Intent;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import java.util.Collections;
import java.util.Date;
import java.util.List;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.bo.ApostaBO;
import caixa.gov.br.loterias.bo.DadosCorporativosBO;
import caixa.gov.br.loterias.bo.silce.SilceClient;
import caixa.gov.br.loterias.bo.silce.dto.CarrinhoDTO;
import caixa.gov.br.loterias.bo.silce.dto.CarrinhoDTOResponse;
import caixa.gov.br.loterias.bo.silce.dto.DTOEnumCharacter;
import caixa.gov.br.loterias.bo.silce.dto.DTOEnumInteger;
import caixa.gov.br.loterias.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import caixa.gov.br.loterias.bo.silce.dto.ModalidadeEnum;
import caixa.gov.br.loterias.bo.silce.dto.TipoConcursoEnum;
import caixa.gov.br.loterias.utils.EstiloModalidade;
import caixa.gov.br.loterias.utils.LoadingViewLoterias;
import caixa.gov.br.loterias.utils.MensagensNetwork;
import caixa.gov.br.loterias.view.adapter.CarrinhoApostasListViewAdapter;
import caixa.gov.br.loterias.utils.ViewUtils;

public class CarrinhoActivity extends AppCompatActivity {

    public  CarrinhoDTO carrinho;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ViewUtils.configuraStatusBarGradientLayout(this, getWindow());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrinho);

        callWebservice();
        //mockCarrinho();

        ViewUtils.organizaTabLayout(this, R.id.carrinhoMainLayout, R.id.apostasListaRecycleView, R.id.carrinhoScrollView);
    }

    private void mockCarrinho () {
        carrinho = new CarrinhoDTO();

        List<IdentificaoDeUmaApostaDas8Modalidades> apostas = new ArrayList<IdentificaoDeUmaApostaDas8Modalidades>();

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

        aposta.setNumerosSelecionados(thisIsAnIntArray);

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

    private void montarLayout(){
        TextView infoValorMinimoTextView = (TextView) findViewById(R.id.infoValorMinimo);
        infoValorMinimoTextView.setText(ViewUtils.fromHtml("Lembre-se que o valor mínimo do total de compras nesse aplicativo é <strong>R$24,00</strong>"));
    }

    private void callWebservice () {

        final View loadViewProgress = LoadingViewLoterias.init(this);
        final ApostaBO apostaBO = new ApostaBO(CarrinhoActivity.this);

        apostaBO.buscaCarrinho(new SilceClient.Listener<CarrinhoDTOResponse>() {
            @Override
            public void onResponse(CarrinhoDTOResponse response) {
                LoadingViewLoterias.stop(loadViewProgress);
                carrinho = response.getPayload();

                atualizaLayout();
            }
        }, new SilceClient.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LoadingViewLoterias.stop(loadViewProgress);
                ViewUtils.alert(CarrinhoActivity.this, MensagensNetwork.setMensagem(error, getApplicationContext()));
            }
        });
    }

    public void atualizaLayout () {
        montarLayout();
        configuraActivityCarrinho();
        configuraRecyclerViewCarrinhoAposta();
        configuraListeners();
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
            abrirTermos();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void abrirTermos(){
        Intent activity = new Intent(CarrinhoActivity.this, TermosUsoActivity.class);
        startActivity(activity);
    }

    private void configuraActivityCarrinho(){
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        getSupportActionBar().setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.navigation_gradient, null));
    }

    private void configuraListeners(){
        ((Button) findViewById(R.id.botaoAvancarPagamento)).setText(ViewUtils.configuraButtonText(this, getString(R.string.avancarFormaPagamento), 7));


        findViewById(R.id.botaoFazerNovaAposta).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(2);

                finish();
            }
        });

        findViewById(R.id.imagemLixeiraCarrinho).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                limpaCarrinho();
            }
        });

        findViewById(R.id.textoLimparCarrinho).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                limpaCarrinho();
            }
        });

        findViewById(R.id.botaoAvancarPagamento).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent activity = new Intent(CarrinhoActivity.this, FormaPagamentoActivity.class);

                startActivity(activity);
            }
        });
    }

    private void configuraRecyclerViewCarrinhoAposta(){
        RecyclerView recyclerView = findViewById(R.id.apostasListaRecycleView);
        RecyclerView.LayoutManager layout = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);

        recyclerView.setAdapter(new CarrinhoApostasListViewAdapter(this, (ArrayList<IdentificaoDeUmaApostaDas8Modalidades>) carrinho.getApostas(), this));
        recyclerView.setLayoutManager(layout);
        recyclerView.setNestedScrollingEnabled(false);
    }

    private void limpaCarrinho(){
        ViewUtils.alert(this, "LIMPAR CARRINHO");
    }

}
