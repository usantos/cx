package br.gov.caixa.loterias.apostas.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.gov.caixa.loterias.apostas.LoteriasBaseAppActivity;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bean.NumerosSelecionadosRapidao;
import br.gov.caixa.loterias.apostas.utils.AppCenterManager;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.adapter.baseadapter.NumeroSelecionadoRapidaoAdapter;
import br.gov.caixa.loterias.apostas.view.custom.ExpandableHeightGridView;

public class NumerosRapidaoActivity extends LoteriasBaseAppActivity implements NumeroSelecionadoRapidaoAdapter.NumeroSelecionadoListener{
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

        isTipoNrObrigatorios =  getIntent().getExtras().getBoolean(getResources().getString(R.string.extra_is_tipo_nr_obrigatorios));

        String arrayContrarios =  getIntent().getExtras().getString(getResources().getString(R.string.extra_numeros_contrarios));
        listaNumerosContrariosRapidao = Arrays.asList(new Gson().fromJson(arrayContrarios, Integer[].class));

        if(isTipoNrObrigatorios == true){
            AppCenterManager.registraEvento(getResources().getString(R.string.evento_seleciona_numeros_obrigatorios_config_rapidao));

            List<Integer> numerosObrigadoriosSalvos = new ArrayList<>();
            String arrayObrigarios =  getIntent().getExtras().getString(getResources().getString(R.string.extra_numeros_obrigatorios));
            numerosObrigadoriosSalvos = Arrays.asList(new Gson().fromJson(arrayObrigarios, Integer[].class));
            numerosLista = numerosObrigadoriosSalvos;
            gridViewNumerosRapidaoSlc = findViewById(R.id.gridviewNumerosRapidaoSelecao);

        }else{
            AppCenterManager.registraEvento(getResources().getString(R.string.evento_entrou_seleciona_numeros_proibidos_config_rapidao));

            List<Integer> numerosProibidosSalvos = new ArrayList<>();
            String arrayProibidos =  getIntent().getExtras().getString(getResources().getString(R.string.extra_numeros_proibidos));
            numerosProibidosSalvos = Arrays.asList(new Gson().fromJson(arrayProibidos, Integer[].class));
            numerosLista = numerosProibidosSalvos;
            titleNumerosRapidaoObrigatorios.setText(getResources().getString(R.string.numeros_proibidos));
            textoSelecaoNumerosRapidao.setText(getResources().getString(R.string.numeros_nao_devem_aparecer));
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
            intent.putExtra(getResources().getString(R.string.extra_numeros_rapidao), arrayNumeros);
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
        NumeroSelecionadoRapidaoAdapter numerosSlcRapidaoAdapter = new NumeroSelecionadoRapidaoAdapter(this,
                                                                                                       this,
                                                                                                       listaNumerosSelecionadosRapidao,
                                                                                                       isTipoNrObrigatorios,
                                                                                                       listaNumerosContrariosRapidao,
                                                                                                       this);
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


        NumeroSelecionadoRapidaoAdapter numerosSlcRapidaoAdapter = new NumeroSelecionadoRapidaoAdapter(this,
                                                                                                       this,
                                                                                                       listaNumerosSelecionadosRapidao,
                                                                                                       isTipoNrObrigatorios,
                                                                                                       listaNumerosContrariosRapidao,
                                                                                                       this);
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
