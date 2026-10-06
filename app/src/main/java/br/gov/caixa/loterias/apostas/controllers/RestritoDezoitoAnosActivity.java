package br.gov.caixa.loterias.apostas.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import br.gov.caixa.loterias.apostas.LoteriasBaseAppActivity;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.utils.AppCenterManager;
import br.gov.caixa.loterias.apostas.utils.Utils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;

public class RestritoDezoitoAnosActivity extends LoteriasBaseAppActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCenterManager.registraEvento(getResources().getString(R.string.evento_entrou_validacao_dezoito_anos));

        setContentView(R.layout.activity_restrito_dezoito_anos);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView textViewTitulo = findViewById(R.id.textLoterias);
        textViewTitulo.setHint("Título");

        TextView textViewTextoRestrito = findViewById(R.id.textViewTextoRestrito);
        textViewTextoRestrito.setText(ViewUtils.textCaixaSTDBold(this, getResources().getString(R.string.app_para_maior_de_18)));

        Button buttonDigitarAposta = findViewById(R.id.entendiButton);
        buttonDigitarAposta.setOnClickListener(v -> {
            Intent activity = new Intent(RestritoDezoitoAnosActivity.this, IntroducaoActivity.class);
            startActivity(activity);
        });

        TextView saibaMais = findViewById(R.id.textViewSaibaMais);
        saibaMais.setHint("Link");
        saibaMais.setOnClickListener(v -> {
            Utils.abreUrl(RestritoDezoitoAnosActivity.this,R.string.url_jogo_responsavel);
        });
    }

}
