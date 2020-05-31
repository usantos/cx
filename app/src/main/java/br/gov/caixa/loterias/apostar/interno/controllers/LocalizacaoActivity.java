package br.gov.caixa.loterias.apostar.interno.controllers;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import br.gov.caixa.loterias.apostar.interno.BuildConfig;
import br.gov.caixa.loterias.apostar.interno.R;
import br.gov.caixa.loterias.apostar.interno.utils.LocalizacaoUtils;

public class LocalizacaoActivity extends AppCompatActivity {

    private TextView tvVersao, tvMsgGenerica;
    private Button btnGenerico;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_localizacao);
        setaViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        trataPermissao();
    }

    private void setaViews() {
        tvVersao = findViewById(R.id.tv_versao);
        btnGenerico = findViewById(R.id.btn_generico);
        tvMsgGenerica = findViewById(R.id.tv_msg_generica);

        tvVersao.setText(TextUtils.concat(getResources().getString(R.string.v), BuildConfig.VERSION_NAME));
    }

    private void trataPermissao() {
        btnGenerico.setVisibility(View.VISIBLE);

        switch (LocalizacaoUtils.checaStatusPermissao(this)) {
            case LocalizacaoUtils.LOC_NAO_PERMITIDA:
                LocalizacaoUtils.solicitaPermissao(LocalizacaoActivity.this);
                break;
            case LocalizacaoUtils.LOC_NEG_PERMANENTEMENTE:
                tvMsgGenerica.setText(getResources().getString(R.string.bloq_local_desativada_msg));
                btnGenerico.setText(getResources().getString(R.string.bloq_local_permitir));
                btnGenerico.setOnClickListener(view -> LocalizacaoUtils.intentConfigPermissao(this));
                break;
            case LocalizacaoUtils.LOC_DESATIVADA:
                tvMsgGenerica.setText(getResources().getString(R.string.bloq_local_desativada_msg));
                btnGenerico.setText(getResources().getString(R.string.bloq_local_ir_config));
                btnGenerico.setOnClickListener(view -> LocalizacaoUtils.intentConfigGps(this));
                break;
            case LocalizacaoUtils.LOC_FORA_DO_BRASIL:
                configLocForaBrasil();
                break;
            case LocalizacaoUtils.LOC_NO_BRASIL:
                setResult(RESULT_OK);
                finish();
                break;
            case LocalizacaoUtils.LOC_ERRO:
                tvMsgGenerica.setText("ERRO_LOC");
                btnGenerico.setText("ERRO_LOC");
                break;
        }
    }

    private void configLocForaBrasil(){
        tvMsgGenerica.setText(getResources().getString(R.string.bloq_local_fora_brasil));
        btnGenerico.setVisibility(View.GONE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (requestCode == LocalizacaoUtils.PERMISSAO_LOCALIZACAO_COD) {
            if (grantResults.length > 0) {
                trataPermissao();
            }
        }
    }

    @Override
    public void onBackPressed() { }
}
