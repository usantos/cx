package br.gov.caixa.loterias.apostar.interno.view;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import br.gov.caixa.loterias.apostar.interno.R;
import br.gov.caixa.loterias.apostar.interno.view.adapter.DetalhesApostasCarrinhoFavoritoAdapter;

public class DetalhesCarrinhoFavoritoActivity extends SettingToolbarActivity {

    private RecyclerView rvApostas;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_carrinho_favorito);
        setaViews();
        setaMetodos();
    }

    private void setaMetodos(){
        configuraRecyclerView();
    }

    private void setaViews(){
        rvApostas = findViewById(R.id.rv_apostas);

        setTitle(getString(R.string.title_activity_favoritas));
    }

    public void configuraRecyclerView(){
        rvApostas.setAdapter( new DetalhesApostasCarrinhoFavoritoAdapter());
        RecyclerView.LayoutManager layout = new LinearLayoutManager(DetalhesCarrinhoFavoritoActivity.this, LinearLayoutManager.VERTICAL, false);
        rvApostas.setLayoutManager(layout);
    }

}
