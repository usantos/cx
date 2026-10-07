package br.gov.caixa.loterias.apostas.controllers;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.gov.caixa.loterias.apostas.LoteriasBaseAppActivity;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.utils.DialogUtils;
import br.gov.caixa.loterias.apostas.utils.ModalidadePreferences;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.ModalidadesFavoritasAdapter;

public class ModalidadesFavoritasActivity extends LoteriasBaseAppActivity {
    public static final String EXTRA_SELECTED_MODALIDADE = "selected_favorite_modalidade";
    private ModalidadePreferences preferences;
    private ModalidadesFavoritasAdapter adapter;
    private RecyclerView list;
    private TextView empty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modalidades_favoritas);
        preferences = new ModalidadePreferences(this);
        setSupportActionBar(findViewById(R.id.toolbar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        setTitle(ViewUtils.textCaixaSTDBoldTitle(this, getString(R.string.modalidades_favoritas_titulo)));
        list = findViewById(R.id.listaModalidadesFavoritas);
        empty = findViewById(R.id.semModalidadesFavoritas);
        adapter = new ModalidadesFavoritasAdapter(this::confirmRemoval, modalidade -> {
            setResult(RESULT_OK, new Intent().putExtra(EXTRA_SELECTED_MODALIDADE, modalidade));
            finish();
        });
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshFavorites();
    }

    private void confirmRemoval(ModalidadeEnum modalidade) {
        DialogUtils.dialogConfirmar(this,
                getString(R.string.msg_excluir_favorita, ModalidadeEnum.getDescricao(modalidade)),
                (dialog, which) -> {
                    preferences.setFavorite(modalidade, false);
                    refreshFavorites();
                });
    }

    private void refreshFavorites() {
        List<ModalidadeEnum> favorites = preferences.getFavorites();
        adapter.setFavorites(favorites);
        list.setVisibility(favorites.isEmpty() ? View.GONE : View.VISIBLE);
        empty.setVisibility(favorites.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
