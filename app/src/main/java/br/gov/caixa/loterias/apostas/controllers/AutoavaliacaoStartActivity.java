package br.gov.caixa.loterias.apostas.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.utils.IntentUtil;
import br.gov.caixa.loterias.apostas.view.activity.DetalhesBolaoActivity;

public class AutoavaliacaoStartActivity extends AppCompatActivity {

    private Button btnIniciar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autoavaliacao_start);


    }

    private void configButton() {
        btnIniciar = findViewById(R.id.btnAutoavaliacaoStart);
        btnIniciar.setOnClickListener(v -> {
            Intent intent = IntentUtil.getIntentOrigemDestino(AutoavaliacaoStartActivity.this, AutoavaliacaoFormActivity.class);
            startActivity(intent);
        });
    }
}
