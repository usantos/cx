package br.gov.caixa.loterias.apostar.interno.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.TextView;

import br.gov.caixa.loterias.apostar.interno.R;
import br.gov.caixa.loterias.apostar.interno.utils.FirebaseManager;
import br.gov.caixa.loterias.apostar.interno.utils.ViewUtils;

public class RestritoDezoitoAnosActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseManager.registraEvento(this ,getResources().getString(R.string.evento_entrou_validacao_dezoito_anos));

        setContentView(R.layout.activity_restrito_dezoito_anos);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView textViewTextoRestrito = findViewById(R.id.textViewTextoRestrito);
        textViewTextoRestrito.setText(ViewUtils.textFuturaAndFuturaBold(this, getResources().getString(R.string.app_para_maior_de_18)));

        Button buttonDigitarAposta = findViewById(R.id.entendiButton);
        buttonDigitarAposta.setOnClickListener(v -> {
            Intent activity = new Intent(RestritoDezoitoAnosActivity.this, IntroducaoActivity.class);
            startActivity(activity);
        });
    }

}
