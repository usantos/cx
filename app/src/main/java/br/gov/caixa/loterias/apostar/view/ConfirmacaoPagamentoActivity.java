package br.gov.caixa.loterias.apostar.view;

import android.content.Intent;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import br.gov.caixa.loterias.apostar.R;
import br.gov.caixa.loterias.apostar.utils.ViewUtils;

public class ConfirmacaoPagamentoActivity extends SettingToolbarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ViewUtils.configuraStatusBarGradientLayout(this, getWindow());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmacao_pagamento);
        setTitle(ViewUtils.textFuturaAndFuturaBoldTitle(this, getString(R.string.title_activity_confirmacao_pagamento)));

        configuraBotoes();

    }

    private void configuraBotoes(){
        ((Button) findViewById(R.id.botaoVoltarInicio)).setText(ViewUtils.textFuturaAndFuturaBold(this, getString(R.string.confirmacaoPagamentoBotaoVoltarInicio)));

        findViewById(R.id.botaoVoltarInicio).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PrincipalActivity_.intent(ConfirmacaoPagamentoActivity.this).flags(Intent.FLAG_ACTIVITY_CLEAR_TOP |  Intent.FLAG_ACTIVITY_SINGLE_TOP).start();
            }
        });
    }
}
