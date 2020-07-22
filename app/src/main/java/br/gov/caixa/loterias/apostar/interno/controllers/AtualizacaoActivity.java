package br.gov.caixa.loterias.apostar.interno.controllers;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;

import br.gov.caixa.loterias.apostar.interno.BuildConfig;
import br.gov.caixa.loterias.apostar.interno.R;
import br.gov.caixa.loterias.apostar.interno.utils.RateUtils;

public class AtualizacaoActivity extends AppCompatActivity {

    public Button buttonAtualizar;

    private TextView textAtualizacaoVersao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_atualizacao);

        textAtualizacaoVersao = findViewById(R.id.txtVersaoAtualizacao );

        textAtualizacaoVersao.setText( TextUtils.concat( getResources().getString(R.string.v) ,BuildConfig.VERSION_NAME) );

        buttonAtualizar = findViewById(R.id.atualizarButton);
        buttonAtualizar.setOnClickListener(view -> {
            RateUtils.redirecionaParaLoja(this);
        });
    }

    @Override
    public void onBackPressed() { }
}
