package br.gov.caixa.loterias.apostar.interno.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.gov.caixa.loterias.apostar.interno.R;
import br.gov.caixa.loterias.apostar.interno.model.bean.NumerosSelecionadosRapidao;
import br.gov.caixa.loterias.apostar.interno.utils.FirebaseManager;
import br.gov.caixa.loterias.apostar.interno.utils.ViewUtils;
import br.gov.caixa.loterias.apostar.interno.view.adapter.NumeroSelecionadoRapidaoAdapter;
import br.gov.caixa.loterias.apostar.interno.view.custom.ExpandableHeightGridView;

public class NumerosRapidaoActivity extends AppCompatActivity {
    NumerosSelecionadosRapidao[] listaNumerosSelecionadosRapidao;
    List<Integer> numerosLista = new ArrayList<>();
    List<Integer> listaNumerosContrariosRapidao = new ArrayList<>();
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
        setTitle(ViewUtils.textFuturaAndFuturaBoldTitle(this, getString(R.string.title_activity_lista_rapidao)));
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        salvarNumerosApostaRapidaoSelecao = findViewById(R.id.salvarNumerosApostaRapidaoSelecao);
        titleNumerosRapidaoObrigatorios  = findViewById(R.id.titleNumerosRapidao);
        textoSelecaoNumerosRapidao  = findViewById(R.id.textoSelecaoNumerosRapidao);

        isTipoNrObrigatorios =  getIntent().getExtras().getBoolean("isTipoNrObrigatorios");

        String arrayContrarios =  getIntent().getExtras().getString("numerosContrarios");
        listaNumerosContrariosRapidao = Arrays.asList(new Gson().fromJson(arrayContrarios, Integer[].class));

        if(isTipoNrObrigatorios == true){
            FirebaseManager.registraEvento(this ,"ENTROU_SELECIONA_NUMEROS_OBRIGATORIOS_CONFIG_RAPIDAO");

            List<Integer> numerosObrigadoriosSalvos = new ArrayList<>();
            String arrayObrigarios =  getIntent().getExtras().getString("numerosobrigatorios");
            numerosObrigadoriosSalvos = Arrays.asList(new Gson().fromJson(arrayObrigarios, Integer[].class));
            numerosLista = numerosObrigadoriosSalvos;
            gridViewNumerosRapidaoSlc = findViewById(R.id.gridviewNumerosRapidaoSelecao);

        }else{
            FirebaseManager.registraEvento(this ,"ENTROU_SELECIONA_NUMEROS_PROIBIDOS_CONFIG_RAPIDAO");

            List<Integer> numerosProibidosSalvos = new ArrayList<>();
            String arrayProibidos =  getIntent().getExtras().getString("numerosProibidos");
            numerosProibidosSalvos = Arrays.asList(new Gson().fromJson(arrayProibidos, Integer[].class));
            numerosLista = numerosProibidosSalvos;
            titleNumerosRapidaoObrigatorios.setText("Números proíbidos");
            textoSelecaoNumerosRapidao.setText("Selecione os números que não deverão aparecer em suas apostas.");
            titleNumerosRapidaoObrigatorios.setTextColor(ContextCompat.getColor(this, R.color.vermelhoBilhetes));
            salvarNumerosApostaRapidaoSelecao.setBackgroundColor(ContextCompat.getColor(this, R.color.vermelhoBilhetes));
            gridViewNumerosRapidaoSlc = findViewById(R.id.gridviewNumerosRapidaoSelecao);
        }



        salvarNumerosApostaRapidaoSelecao.setOnClickListener(view -> {
            Intent intent = new Intent();

            List<Integer> salvarNumero = new ArrayList<>();
            for (NumerosSelecionadosRapidao numero : listaNumerosSelecionadosRapidao){
                if (numero.getSelecionado() == true){
                    salvarNumero.add(numero.getNumero());
                }
            }
            String arrayNumeros = new Gson().toJson(salvarNumero);
            intent.putExtra("numerosRapidao", arrayNumeros);
            setResult(RESULT_OK, intent);
            finish();
        });


        limparButtonRapidaoSelecionados = findViewById(R.id.botaoLimparSelecaoApostaRapidaSelecao);
        limparButtonRapidaoSelecionados.setOnClickListener(view -> {
            for (NumerosSelecionadosRapidao numero : listaNumerosSelecionadosRapidao){
                numero.setSelecionado(false);
            }
            reloadGridView();
        });

        gridViewNumeros();
    }

    @Override
    protected void onStart() {
        super.onStart();
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

        final ScrollView scrollNumerosRapidao = findViewById(R.id.scrollNumerosRapidao);
        scrollNumerosRapidao.post(() -> scrollNumerosRapidao.fullScroll(ScrollView.FOCUS_UP));
    }



    public void mudarLayoutBotaoLimparSelecao(Boolean ativo) {
        final RelativeLayout limpaselecaorelativeLayout = findViewById(R.id.layoutLimparSelecaoApostaRapidaSelecao);
        if (ativo) {
            limpaselecaorelativeLayout.setVisibility(View.VISIBLE);
        }else {
            limpaselecaorelativeLayout.setVisibility(View.GONE);

        }
    }

}
