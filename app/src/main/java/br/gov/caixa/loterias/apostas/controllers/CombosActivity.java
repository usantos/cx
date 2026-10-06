package br.gov.caixa.loterias.apostas.controllers;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.List;
import java.util.Objects;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bean.SessaoUsuario;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CombosDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.CarrinhoSingleton;
import br.gov.caixa.loterias.apostas.novo.features.carrinho.CarrinhoActivity;
import br.gov.caixa.loterias.apostas.utils.DialogUtils;
import br.gov.caixa.loterias.apostas.utils.FragmentUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.ListaComboAdapter;
import br.gov.caixa.loterias.apostas.view.fragment.SomadorCarrinhoFragment;
import br.gov.caixa.loterias.apostas.view.listener.OnDialogBotaoListener;
import br.gov.caixa.loterias.apostas.view.listener.onComboClickListener;

public class CombosActivity extends SettingToolbarActivity implements onComboClickListener {
    public static final String ARG_COMBO = "ARG_COMBO";
    private static final int QUANTIDADE_COLUNA_LISTA_COMBOS = 2;

    private Toolbar toolbar;

    private RecyclerView listaCombosRcv;
    private ListaComboAdapter listaComboAdapter;

    private SomadorCarrinhoFragment somadorCarrinhoFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_combos);
        setViews();
        configuraListaCombosRcv();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fragmentSomadorCarrinho();
    }

    private void setViews() {
        listaCombosRcv = findViewById(R.id.rcv_lista_combos);
        View view_id_fg_somador_carrinho = findViewById(R.id.id_somador_carrinho);
        view_id_fg_somador_carrinho.setOnClickListener(view -> vaiProCarrinho());

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle(ViewUtils.textCaixaSTDBoldTitle(this, getString(R.string.titulo_combos)));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }


    private void configuraListaCombosRcv() {
        List<CombosDTO> combos = SessaoUsuario.getInstance().getListCombosDTO();

        if (combos != null && !combos.isEmpty()) {
            listaComboAdapter = new ListaComboAdapter(combos, CombosActivity.this, this);
            listaCombosRcv.setAdapter(listaComboAdapter);
            RecyclerView.LayoutManager layoutListacombos = new GridLayoutManager(this, QUANTIDADE_COLUNA_LISTA_COMBOS);
            listaCombosRcv.setLayoutManager(layoutListacombos);

            listaCombosRcv.setNestedScrollingEnabled(true);

        } else {
            DialogUtils.dialogEntendiListener(CombosActivity.this, getString(R.string.nao_concluiu_operacao),

                    new OnDialogBotaoListener() {
                        @Override
                        public void onButtonClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    }
            );
        }
    }


    @Override
    public void onComboClick(CombosDTO combo) {
        Intent intent = new Intent(this, DetalheComboActivity.class);
        intent.putExtra(ARG_COMBO, (new Gson()).toJson(combo));
        startActivity(intent);
    }


    private void fragmentSomadorCarrinho() {
        if (somadorCarrinhoFragment == null) {
            somadorCarrinhoFragment = FragmentUtils.startSomadorCarrinhoFragment(getSupportFragmentManager(), somadorCarrinhoFragment, R.id.id_somador_carrinho, "TELA LISTA COMBOS");
        } else {
            somadorCarrinhoFragment.atualizaValorTotal(CarrinhoSingleton.getInstance().getCarrinho());
        }

    }

    protected void vaiProCarrinho() {
        if (somadorCarrinhoFragment != null && somadorCarrinhoFragment.isVisible()) {
            startActivity(new Intent(CombosActivity.this, CarrinhoActivity.class));
        }
    }

}