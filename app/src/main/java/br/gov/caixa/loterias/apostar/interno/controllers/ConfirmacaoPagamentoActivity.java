package br.gov.caixa.loterias.apostar.interno.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import br.gov.caixa.loterias.apostar.interno.R;
import br.gov.caixa.loterias.apostar.interno.utils.RateUtils;
import br.gov.caixa.loterias.apostar.interno.utils.ViewUtils;
import br.gov.caixa.loterias.apostar.interno.view.PrincipalActivity_;

public class ConfirmacaoPagamentoActivity extends SettingToolbarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ViewUtils.configuraStatusBarGradientLayout(this, getWindow());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmacao_pagamento);
        setTitle(ViewUtils.textFuturaAndFuturaBoldTitle(this, getString(R.string.title_activity_confirmacao_pagamento)));

        configuraBotoes();
        RateUtils.avaliarAplicativo(this,RateUtils.getQtdCompras());
    }

    @Override
    public void onBackPressed() { }

    private void configuraBotoes(){
        ((Button) findViewById(R.id.botaoVoltarInicio)).setText(ViewUtils.textFuturaAndFuturaBold(this, getString(R.string.confirmacaoPagamentoBotaoVoltarInicio)));
        ((Button) findViewById(R.id.botaoAcompanheSuasCompras)).setText(ViewUtils.textFuturaAndFuturaBold(this, getString(R.string.label_acompanhe_suas_compras)));

        findViewById(R.id.botaoVoltarInicio).setOnClickListener(view -> PrincipalActivity_.intent(ConfirmacaoPagamentoActivity.this).flags(Intent.FLAG_ACTIVITY_CLEAR_TOP |  Intent.FLAG_ACTIVITY_SINGLE_TOP).start());

        findViewById(R.id.botaoAcompanheSuasCompras).setOnClickListener(view ->
                startActivity(new Intent(ConfirmacaoPagamentoActivity.this, ListaComprasActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |  Intent.FLAG_ACTIVITY_SINGLE_TOP)));

        findViewById(R.id.botao_termo).setOnClickListener(v -> {
            Intent activity = new Intent(this, TermosUsoActivity.class);
            startActivity(activity);
        });
    }
}
