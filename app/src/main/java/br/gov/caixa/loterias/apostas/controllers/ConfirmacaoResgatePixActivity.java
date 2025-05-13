package br.gov.caixa.loterias.apostas.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import br.gov.caixa.loterias.apostas.LoteriasAppActivity;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.utils.IntentUtil;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;

public class ConfirmacaoResgatePixActivity extends LoteriasAppActivity {
    private Button btnMinhasApostas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirma_resgate_pix);
        configToolbar(R.id.toolbar);
        hideHomeAsUpButton();

        setaViews();
        setaMetodos();
    }

    private void setaViews() {
        setTitle(ViewUtils.textCaixaSTDBoldTitle(this, getString(R.string.title_activity_resgate_pix)));
        btnMinhasApostas = findViewById(R.id.btn_minhas_apostas);
        btnMinhasApostas.setText(ViewUtils.textCaixaSTDBoldTitle(this, getString(R.string.acesse_minhas_apostas)));
    }

    private void setaMetodos() {
        btnMinhasApostas.setOnClickListener(v -> goToMinhasApostas());
    }

    private void goToMinhasApostas() {
        Intent it = IntentUtil.getIntentLimpandoPilhaActivities(ConfirmacaoResgatePixActivity.this, PrincipalActivity.class);
        it.putExtra(getString(R.string.arg_goto_minhas_apostas), true);
        startActivity(it);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        goToMinhasApostas();
    }
}