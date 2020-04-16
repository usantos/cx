package br.gov.caixa.loterias.apostar.interno.controllers;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import br.gov.caixa.loterias.apostar.interno.R;
import br.gov.caixa.loterias.apostar.interno.utils.ViewUtils;

public class JogoResponsavelActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jogo_responsavel);
        setaViews();
        setaMetodos();
    }

    //region Initialization Methods
    private void setaViews() {
        tvSemCarrinhos = findViewById(R.id.tv_sem_carrinhos);
        rvCarrinhosFavoritos = findViewById(R.id.rv_carrinhos_favoritos);

        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(ViewUtils.textFuturaAndFuturaBoldTitle(JogoResponsavelActivity.this, getString(R.string.jr_titulo)));
    }
    //endregion

}
