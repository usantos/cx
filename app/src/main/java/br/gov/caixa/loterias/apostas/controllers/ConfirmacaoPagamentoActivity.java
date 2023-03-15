package br.gov.caixa.loterias.apostas.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.utils.RateUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;

public class ConfirmacaoPagamentoActivity extends SettingToolbarActivity {

    private int origem;
    private TextView titulo;
    private TextView infoApostas;
    private Button btnVoltarInicio;
    private Button btnAcompanheCompras;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ViewUtils.configuraStatusBarGradientLayout(this, getWindow());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmacao_pagamento);
        setTitle(ViewUtils.textFuturaAndFuturaBoldTitle(this, getString(R.string.title_activity_confirmacao_pagamento)));

        configViews();

        getExtras();
        configLayout();
        configuraBotoes();
        RateUtils.avaliarAplicativo(this,RateUtils.getQtdCompras());
    }

    private void configViews() {
        titulo = findViewById(R.id.textTitulo);
        infoApostas = findViewById(R.id.textInfoApostas);
        btnVoltarInicio = findViewById(R.id.botaoVoltarInicio);
        btnAcompanheCompras = findViewById(R.id.botaoAcompanheSuasCompras);
    }

    private void configLayout() {
        if (origem == 6) {
            titulo.setText(R.string.confirmacaopagamentoParabensPix);
            infoApostas.setText(R.string.confirmacaoPagamentoInfoApostasPIX);
            btnVoltarInicio.setBackgroundColor(getResources().getColor(R.color.branco));
            btnAcompanheCompras.setBackgroundColor(getResources().getColor(R.color.verde_acompanha_compras));
         }
    }

    private void getExtras() {
        if (getIntent() != null && getIntent().getExtras() != null)  {
            origem = getIntent().getExtras().getInt("ARG_ORIGEM");
        }
    }

    @Override
    public void onBackPressed() { }

    private void configuraBotoes(){

        btnVoltarInicio.setText(ViewUtils.textFuturaAndFuturaBold(this, getString(R.string.confirmacaoPagamentoBotaoVoltarInicio)));
        btnAcompanheCompras.setText(ViewUtils.textFuturaAndFuturaBold(this, getString(R.string.label_acompanhe_suas_compras_pix)));

        btnVoltarInicio.setOnClickListener(view -> PrincipalActivity_.intent(ConfirmacaoPagamentoActivity.this).flags(Intent.FLAG_ACTIVITY_CLEAR_TOP |  Intent.FLAG_ACTIVITY_SINGLE_TOP).start());

        btnAcompanheCompras.setOnClickListener(view ->
                startActivity(new Intent(ConfirmacaoPagamentoActivity.this, ListaComprasActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |  Intent.FLAG_ACTIVITY_SINGLE_TOP)));

        findViewById(R.id.botao_termo).setOnClickListener(v -> {
            Intent activity = new Intent(this, TermosUsoActivity.class);
            startActivity(activity);
        });
    }
}
