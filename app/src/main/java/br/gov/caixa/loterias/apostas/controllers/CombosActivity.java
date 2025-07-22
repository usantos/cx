package br.gov.caixa.loterias.apostas.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CombosDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.CarrinhoSingleton;
import br.gov.caixa.loterias.apostas.utils.FragmentUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.ListaComboAdapter;
import br.gov.caixa.loterias.apostas.view.fragment.SomadorCarrinhoFragment;
import br.gov.caixa.loterias.apostas.view.holder.HomeViewHolder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CombosActivity extends SettingToolbarActivity {
    //public static final String ARG_COMBOS = "ARG_COMBOS";
    private static final int QUANTIDADE_COLUNA_LISTA_COMBOS = 2;
    private List<CombosDTO> combos = new ArrayList<>();

    RecyclerView listaCombosRcv;
    ListaComboAdapter listaComboAdapter;
    Toolbar toolbar;

    private SomadorCarrinhoFragment somadorCarrinhoFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_combos);

        getExtras();
        setViews();
        configuraListaCombosRcv();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fragmentSomadorCarrinho();
    }

    private void setViews(){
        listaCombosRcv = findViewById(R.id.rcv_lista_combos);
        View view_id_fg_somador_carrinho = findViewById(R.id.id_fg_somador_carrinho);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle(ViewUtils.textCaixaSTDBoldTitle(this, getString(R.string.titulo_combos)));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        view_id_fg_somador_carrinho.setOnClickListener(view -> vaiProCarrinho());
    }


    private void getExtras(){
        if(getIntent() != null && getIntent().hasExtra(HomeViewHolder.ARG_COMBOS)){
            Serializable extra = getIntent().getSerializableExtra(HomeViewHolder.ARG_COMBOS);
            if (extra instanceof ArrayList<?>) {
                try {
                    combos = (ArrayList<CombosDTO>) extra;
                } catch (ClassCastException e) {
                    Log.e("CombosActivity", "Erro ao converter combos: " + e.getMessage());
                }
            }
            Log.d("CombosActivity", "Combos recebidos: " + combos);
        }else{
            Log.d("CombosActivity", "Nenhum combo recebido");
        }
    }

    private void configuraListaCombosRcv(){

        if(combos != null && !combos.isEmpty()){

            listaComboAdapter = new ListaComboAdapter(combos, CombosActivity.this);
            listaCombosRcv.setAdapter(listaComboAdapter);
            RecyclerView.LayoutManager layoutListacombos = new GridLayoutManager(this, QUANTIDADE_COLUNA_LISTA_COMBOS);
            listaCombosRcv.setLayoutManager(layoutListacombos);
            listaCombosRcv.setNestedScrollingEnabled(true);

        }else{
            ViewUtils.alertTitleButton(CombosActivity.this, R.string.label_atencao, getString(R.string.nao_concluiu_operacao), "Ok", dialogInterface -> finish());
        }
    }

    private void fragmentSomadorCarrinho() {
        if(somadorCarrinhoFragment == null){
            somadorCarrinhoFragment = FragmentUtils.startSomadorCarrinhoFragment(getSupportFragmentManager(), somadorCarrinhoFragment, R.id.id_fg_somador_carrinho,"TELA LISTA COMBOS" );
        }else {
            somadorCarrinhoFragment.atualizaValorTotal(CarrinhoSingleton.getInstance().getCarrinho());
        }
    }

    protected void vaiProCarrinho() {
        if (somadorCarrinhoFragment != null && somadorCarrinhoFragment.isVisible()) {
            startActivity(new Intent(CombosActivity.this, CarrinhoActivity.class));
        }
    }

}