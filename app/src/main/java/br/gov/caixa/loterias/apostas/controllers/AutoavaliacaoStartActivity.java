package br.gov.caixa.loterias.apostas.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.utils.IntentUtil;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.activity.DetalhesBolaoActivity;

public class AutoavaliacaoStartActivity extends AppCompatActivity {

    private TextView tvIniciar;
    private Button btnIniciar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autoavaliacao_start);

        configurar();
    }

    private void configurar() {

        tvIniciar = findViewById(R.id.tvAutoIniciar);
        tvIniciar.setText(ViewUtils.textFuturaAndFuturaBold(this, getString(R.string.autoavaliacao_apostador_bold)));
        //tvIniciar.setText(ViewUtils.textFuturaAndFuturaBoldTitle(this, getString(R.string.autoavaliacao_apostador_bold)));

        btnIniciar = findViewById(R.id.btnAutoavaliacaoStart);
        btnIniciar.setOnClickListener(v -> {
            Intent intent = IntentUtil.getIntentOrigemDestino(this, AutoavaliacaoFormActivity.class);
            startActivity(intent);
        });
    }
}
