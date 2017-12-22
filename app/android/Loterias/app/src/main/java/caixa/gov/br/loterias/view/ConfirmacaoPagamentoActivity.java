package caixa.gov.br.loterias.view;

import android.content.Intent;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.utils.ViewUtils;

public class ConfirmacaoPagamentoActivity extends SettingToolbarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ViewUtils.configuraStatusBarGradientLayout(this, getWindow());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmacao_pagamento);
        setTitle(ViewUtils.textFuturaAndFuturaBold(this, getString(R.string.title_activity_confirmacao_pagamento)));

        configuraBotoes();

    }

    private void configuraBotoes(){
        ((Button) findViewById(R.id.botaoVoltarInicio)).setText(ViewUtils.textFuturaAndFuturaBold(this, getString(R.string.confirmacaoPagamentoBotaoVoltarInicio)));

        findViewById(R.id.botaoVoltarInicio).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent activity = new Intent(ConfirmacaoPagamentoActivity.this, PrincipalActivity.class);
                activity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |  Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(activity);
            }
        });
    }
}
