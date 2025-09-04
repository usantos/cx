package br.gov.caixa.loterias.apostas.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import br.gov.caixa.loterias.apostas.LoteriasBaseAppActivity;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bean.SessaoUsuario;
import br.gov.caixa.loterias.apostas.utils.IntentUtil;

public class AutoavaliacaoStartActivity extends LoteriasBaseAppActivity {

    private TextView tvIniciar;
    private Button btnIniciar;
    private Button btnFechar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autoavaliacao_start);

        configurar();
    }

    @Override
    public void onBackPressed() {
    }

    private void configurar() {

        tvIniciar = findViewById(R.id.tvAutoIniciar);

        btnIniciar = findViewById(R.id.btnAutoavaliacaoStart);
        btnIniciar.setOnClickListener(v -> {
            Intent intent = IntentUtil.getIntentOrigemDestino(this, AutoavaliacaoFormActivity.class);
            startActivity(intent);
            finish();
        });

        if (!SessaoUsuario.getInstance().getResponderAutoavaliacao()) {
            btnFechar = findViewById(R.id.btnAutoFecharStart);
            btnFechar.setVisibility(View.VISIBLE);
            btnFechar.setOnClickListener(v -> {
                finish();
            });
        }
    }
}
