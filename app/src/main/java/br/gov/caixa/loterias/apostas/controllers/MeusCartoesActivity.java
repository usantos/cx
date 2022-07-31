package br.gov.caixa.loterias.apostas.controllers;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.model.CompraModel;

public class MeusCartoesActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private CompraModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cartoes);
        pegaExtras();
        setaViews();
        model = new CompraModel(MeusCartoesActivity.this);
    }

    private void pegaExtras(){
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

        }
    }

    private void setaViews(){
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle(getString(R.string.meus_cartoes));
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

}