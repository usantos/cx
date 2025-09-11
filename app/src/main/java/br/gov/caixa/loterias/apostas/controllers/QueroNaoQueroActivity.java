package br.gov.caixa.loterias.apostas.controllers;


import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import br.gov.caixa.loterias.apostas.LoteriasBaseAppActivity;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.Dezena;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.utils.DialogUtils;

import br.gov.caixa.loterias.apostas.utils.VolanteUtils;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.ListaDezenaRecyclerView;
import br.gov.caixa.loterias.apostas.view.config.DezenaConfig;
import br.gov.caixa.loterias.apostas.view.holder.DezenaHolder;
import br.gov.caixa.loterias.apostas.view.listener.OnItemClickListener;

public class QueroNaoQueroActivity extends LoteriasBaseAppActivity {

    private static final int PROGNOSTICO_MAXIMO = 80;
    private static final int QUANTIDADE_COLUNAS_CARTELA = 6;
    private static final int COR_QUERO_NAO_QUERO = R.color.azulFiltro;
    boolean isQuero = false;
    boolean isNaoQuero = false;
    private List<Dezena> dezenas = new ArrayList<>();
    private List<Integer> dezenasSelecionadas = new ArrayList<>();
    private List<Integer> dezenasIniciais;
    private List<Integer> dezenasQuero = new ArrayList<>();
    private List<Integer> dezenasNaoQuero = new ArrayList<>();
    private TextView title;
    private ConstraintLayout btnFechar;
    private Button btnConfirmar, btnCancelar;
    private RecyclerView cartela;
    public ListaDezenaRecyclerView cartelaAdapter;
    private String titulo = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quero_nao_quero);
        setaViews();
        setaMetodos();
        pegaExtras();
    }

    @Override
    protected void onStart() {
        super.onStart();
        configuraCartela();
    }

    private void pegaExtras(){
        if(getIntent() != null){
              String getTitulo = getIntent().getStringExtra(FiltraMarketplaceActivity.ARG_TITULO_QUERO_NAO_QUERO);
              //dezenasIniciais = getIntent().getIntegerArrayListExtra(FiltraMarketplaceActivity.ARG_DEZENAS_INICIAIS);
              dezenasQuero = getIntent().getIntegerArrayListExtra(FiltraMarketplaceActivity.ARG_DEZENAS_QUERO);
              dezenasNaoQuero = getIntent().getIntegerArrayListExtra(FiltraMarketplaceActivity.ARG_DEZENAS_NAO_QUERO);

            if(getTitulo !=null && getTitulo.equals(FiltraMarketplaceActivity.ARG_QUERO)){
                title.setText(getString(R.string.numeros_quero));
                titulo = getTitulo;
                dezenasIniciais = new ArrayList<>(dezenasQuero);
                dezenasSelecionadas = new ArrayList<>(dezenasIniciais);
                isQuero = true;
            } else if (getTitulo !=null && getTitulo.equals(FiltraMarketplaceActivity.ARG_NAO_QUERO)) {
                title.setText(getString(R.string.numeros_nao_quero));
                titulo = getTitulo;
                dezenasIniciais = new ArrayList<>(dezenasNaoQuero);
                dezenasSelecionadas = new ArrayList<>(dezenasIniciais);
                isNaoQuero = true;
            }else{
                isQuero = false;
                isNaoQuero = false;
                Log.d(" ", "Erro ao definir o titulo da activity quero não quero");
            }
        }

    }

    @Override
    public void finish() {
        super.finish();
    }

    private void setaViews(){
        title = findViewById(R.id.queroNaoQueroTitle);
        btnFechar = findViewById(R.id.btnFecharContainer);
        btnCancelar = findViewById(R.id.btnCancelarQueroNaoQuero);
        btnConfirmar = findViewById(R.id.btnConfirmarQueroNaoQuero);
        cartela = findViewById(R.id.rcvCartelaQueroNaoQuero);
    }

    private void setaMetodos(){

        btnFechar.setOnClickListener(v -> fechar());

        btnCancelar.setOnClickListener(v -> cancelar());

        btnConfirmar.setOnClickListener(v -> confirmar());
    }


    private void configuraCartela(){

        dezenas = VolanteUtils.getDezenas(ModalidadeEnum.QUINA, PROGNOSTICO_MAXIMO);

        DezenaConfig dezenaConfig = new DezenaConfig(true, COR_QUERO_NAO_QUERO, Color.WHITE, true, true);

        cartelaAdapter = new ListaDezenaRecyclerView(dezenas, dezenasIniciais, dezenaConfig, onItemClickListener());

        cartela.setAdapter(cartelaAdapter);
        RecyclerView.LayoutManager layout = new GridLayoutManager(QueroNaoQueroActivity.this, QUANTIDADE_COLUNAS_CARTELA);
        cartela.setLayoutManager(layout);
        cartela.setNestedScrollingEnabled(false);

    }
    @NotNull
    private OnItemClickListener<DezenaHolder> onItemClickListener() {
        return (holder, position) -> {
            try {
                Dezena dezena = dezenas.get(position);
                Integer valorSelecionado = Integer.valueOf(dezena.getValue());

                if(isQuero){
                    adicionaNumeroSelecionado(valorSelecionado, dezenasNaoQuero);

                }else if(isNaoQuero){
                    adicionaNumeroSelecionado(valorSelecionado, dezenasQuero);

                }else{
                    Log.d("Erro","Titulo pagina quero nao quero desconhecido");
                }

                cartelaAdapter.atualizaSelecionados(dezenasSelecionadas);

            } catch (Exception e){
                Log.d("", Objects.requireNonNull(e.getLocalizedMessage()));
            }
        };
    }

    private void adicionaNumeroSelecionado(Integer valorSelecionado, List<Integer> dezenasJaEscolhidasOutroFiltro){
        if(dezenasJaEscolhidasOutroFiltro.contains(valorSelecionado)){
            mostraDialogNumeroJaEscolhido();
        }else{
            if(dezenasSelecionadas.contains(valorSelecionado)){
                dezenasSelecionadas.remove(valorSelecionado);
            }else{
                dezenasSelecionadas.add(valorSelecionado);
            }
        }
    }
    private void mostraDialogNumeroJaEscolhido(){
        DialogUtils.showDialog1Botao(this, getString(R.string.label_atencao),
                getString(R.string.alerta_numero_ja_escolhido_quero_nao_quero),
                getString(R.string.ok),
                () -> new Dialog(this).dismiss()
        );
    }
    private void fechar(){
        fecharPagina(dezenasIniciais);
    }

    private void cancelar(){
      fecharPagina(dezenasIniciais);
    }

    private void confirmar(){
        fecharPagina(dezenasSelecionadas);
    }

    public void fecharPagina(List<Integer> dezenas){

        ArrayList<Integer> lista = new ArrayList<>(dezenas);
        Intent returnIntent = new Intent();

        returnIntent.putExtra(FiltraMarketplaceActivity.ARG_TITULO_QUERO_NAO_QUERO, titulo);
        returnIntent.putIntegerArrayListExtra(FiltraMarketplaceActivity.ARG_DEZENAS_SELECIONADAS, lista);
        setResult(RESULT_OK, returnIntent);
        finish();
    }
}