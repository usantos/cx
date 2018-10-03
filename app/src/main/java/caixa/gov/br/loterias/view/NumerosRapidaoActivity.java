package caixa.gov.br.loterias.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.jboss.aerogear.android.authorization.oauth2.OAuth2AuthorizationException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.model.bean.NumerosSelecionadosRapidao;

import caixa.gov.br.loterias.model.bo.DadosUsuarioBO;
import caixa.gov.br.loterias.model.bo.DadosUsuarioBO_;
import caixa.gov.br.loterias.model.bo.RedirectNetwork;
import caixa.gov.br.loterias.model.bo.RequestListener;
import caixa.gov.br.loterias.model.bo.keycloak.KeycloakBO;
import caixa.gov.br.loterias.model.bo.keycloak.KeycloakBO_;
import caixa.gov.br.loterias.model.bo.silce.dto.ListCartaoDTOResponse;
import caixa.gov.br.loterias.utils.AppUtils;
import caixa.gov.br.loterias.utils.ViewUtils;
import caixa.gov.br.loterias.view.adapter.NumeroSelecionadoRapidaoAdapter;
import caixa.gov.br.loterias.view.custom.ExpandableHeightGridView;
import caixa.gov.br.loterias.view.custom.LoadingViewLoterias;

public class NumerosRapidaoActivity extends AppCompatActivity {
    NumerosSelecionadosRapidao[] listaNumerosSelecionadosRapidao;
    List<Integer> numerosLista =  new ArrayList<Integer>();
    List<Integer> listaNumerosContrariosRapidao =  new ArrayList<Integer>();
    Button salvarNumerosApostaRapidaoSelecao;
    Button limparButtonRapidaoSelecionados;
    TextView titleNumerosRapidaoObrigatorios;
    TextView textoSelecaoNumerosRapidao;
    ExpandableHeightGridView gridViewNumerosRapidaoSlc;
    Boolean isTipoNrObrigatorios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_numeros_rapidao);
        setTitle(ViewUtils.textFuturaAndFuturaBold(this, getString(R.string.title_activity_lista_rapidao)));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        salvarNumerosApostaRapidaoSelecao = (Button)findViewById(R.id.salvarNumerosApostaRapidaoSelecao);
        titleNumerosRapidaoObrigatorios  = (TextView)findViewById(R.id.titleNumerosRapidao);
        textoSelecaoNumerosRapidao  = (TextView)findViewById(R.id.textoSelecaoNumerosRapidao);

        isTipoNrObrigatorios =  getIntent().getExtras().getBoolean("isTipoNrObrigatorios");

        String arrayContrarios =  getIntent().getExtras().getString("numerosContrarios");
        listaNumerosContrariosRapidao = Arrays.asList(new Gson().fromJson(arrayContrarios, Integer[].class));

        if(isTipoNrObrigatorios == true){
            List<Integer> numerosObrigadoriosSalvos =  new ArrayList<Integer>();
            String arrayObrigarios =  getIntent().getExtras().getString("numerosobrigatorios");
            numerosObrigadoriosSalvos = Arrays.asList(new Gson().fromJson(arrayObrigarios, Integer[].class));
            numerosLista = numerosObrigadoriosSalvos;
            gridViewNumerosRapidaoSlc = (ExpandableHeightGridView)findViewById(R.id.gridviewNumerosRapidaoSelecao);

        }else{
            List<Integer> numerosProibidosSalvos =  new ArrayList<Integer>();
            String arrayProibidos =  getIntent().getExtras().getString("numerosProibidos");
            numerosProibidosSalvos = Arrays.asList(new Gson().fromJson(arrayProibidos, Integer[].class));
            numerosLista = numerosProibidosSalvos;
            titleNumerosRapidaoObrigatorios.setText("Números proíbidos");
            textoSelecaoNumerosRapidao.setText("Selecione os números que não deverão aparecer em suas apostas.");
            titleNumerosRapidaoObrigatorios.setTextColor(ContextCompat.getColor(this, R.color.vermelhoBilhetes));
            salvarNumerosApostaRapidaoSelecao.setBackgroundColor(ContextCompat.getColor(this, R.color.vermelhoBilhetes));
            gridViewNumerosRapidaoSlc = (ExpandableHeightGridView)findViewById(R.id.gridviewNumerosRapidaoSelecao);
        }



        salvarNumerosApostaRapidaoSelecao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();

                List<Integer> salvarNumero = new ArrayList<Integer>();
                for (NumerosSelecionadosRapidao numero : listaNumerosSelecionadosRapidao){
                    if (numero.getSelecionado() == true){
                        salvarNumero.add(numero.getNumero());
                    }
                }
                String arrayNumeros = new Gson().toJson(salvarNumero);
                intent.putExtra("numerosRapidao", arrayNumeros);
                setResult(RESULT_OK, intent);
                finish();
            }
        });


        limparButtonRapidaoSelecionados = (Button)findViewById(R.id.botaoLimparSelecaoApostaRapidaSelecao);
        limparButtonRapidaoSelecionados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (NumerosSelecionadosRapidao numero : listaNumerosSelecionadosRapidao){
                    numero.setSelecionado(false);
                }
                reloadGridView();
            }
        });

        gridViewNumeros();

        connectKeycloak();
    }

    private void connectKeycloak() {
        if(AppUtils.isNetworkAvailable(NumerosRapidaoActivity.this)){
            KeycloakBO_.getInstance_(NumerosRapidaoActivity.this).login(NumerosRapidaoActivity.this, new KeycloakBO.OnLogin() {
                        @Override
                        public void onSuccess(String token) {
                            if(NumerosRapidaoActivity.this != null){
                                DadosUsuarioBO.updateUsuarioLogado( NumerosRapidaoActivity.this, true );
                                //loginSucess();
                            }
                        }

                        @Override
                        public void onFailure(Exception e) {
                            if(NumerosRapidaoActivity.this != null){
//                            OAuth2AuthorizationException.Error error = ((OAuth2AuthorizationException)e).type;
                                if(!((OAuth2AuthorizationException) e).error.equals( "dialog_dismissed" )){
                                    ViewUtils.alert(NumerosRapidaoActivity.this, getString(R.string.sistema_indisponivel));
                                }
                            }
                        }
                    }
            );
        }else{
            ViewUtils.alert(NumerosRapidaoActivity.this, getString(R.string.seminternet));
        }
    }

    private void reloadGridView(){
        NumeroSelecionadoRapidaoAdapter numerosSlcRapidaoAdapter = new NumeroSelecionadoRapidaoAdapter(this, this, listaNumerosSelecionadosRapidao, isTipoNrObrigatorios, listaNumerosContrariosRapidao);
        gridViewNumerosRapidaoSlc.setAdapter(numerosSlcRapidaoAdapter);
        gridViewNumerosRapidaoSlc.setExpanded(true);
        mudarLayoutBotaoLimparSelecao(false);
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
        Intent activity = new Intent(NumerosRapidaoActivity.this, TermosUsoActivity.class);
        startActivity(activity);
    }


    private void gridViewNumeros(){
        listaNumerosSelecionadosRapidao = new NumerosSelecionadosRapidao[100];
        for (int i =0 ; i<100; i++){
            NumerosSelecionadosRapidao numeSel = new NumerosSelecionadosRapidao(i+1, false);
            for(Integer numerosalvo : numerosLista){
                if(numerosalvo == i+1){
                    numeSel.setSelecionado(true);
                }
            }
            listaNumerosSelecionadosRapidao[i] = numeSel;
        }


        NumeroSelecionadoRapidaoAdapter numerosSlcRapidaoAdapter = new NumeroSelecionadoRapidaoAdapter(this, this, listaNumerosSelecionadosRapidao, isTipoNrObrigatorios, listaNumerosContrariosRapidao);
        gridViewNumerosRapidaoSlc.setAdapter(numerosSlcRapidaoAdapter);
        gridViewNumerosRapidaoSlc.setExpanded(true);

        final ScrollView scrollNumerosRapidao = (ScrollView)findViewById(R.id.scrollNumerosRapidao);
        scrollNumerosRapidao.post(new Runnable()
        {
            public void run() {
                scrollNumerosRapidao.fullScroll(ScrollView.FOCUS_UP);
            }
        });
    }



    public void mudarLayoutBotaoLimparSelecao(Boolean ativo) {
        final RelativeLayout limpaselecaorelativeLayout = (RelativeLayout)findViewById(R.id.layoutLimparSelecaoApostaRapidaSelecao);
        if (ativo) {
            limpaselecaorelativeLayout.setVisibility(View.VISIBLE);
        }else {
            limpaselecaorelativeLayout.setVisibility(View.GONE);

        }
    }

}
