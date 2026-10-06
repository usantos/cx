package br.gov.caixa.loterias.apostas.controllers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import br.gov.caixa.loterias.apostas.BuildConfig;
import br.gov.caixa.loterias.apostas.LoteriasBaseAppActivity;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.LocalizacaoUtils;

public class LocalizacaoActivity extends LoteriasBaseAppActivity {

    private TextView tvVersao, tvMsgGenerica;
    private Button btnGenerico;
    public BroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_localizacao);
        setaViews();
        setaMetodos();
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

    private void setaMetodos() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.matches("android.location.PROVIDERS_CHANGED")) {
                    trataPermissaoDelay();
                }
            }
        };
        registerReceiver(receiver, new IntentFilter("android.location.PROVIDERS_CHANGED"));
    }

    private void trataPermissaoDelay() {
        AlertDialogUtils.show(LocalizacaoActivity.this);
        new Handler().postDelayed(() -> {
            AlertDialogUtils.dismiss();
            trataPermissao();
        }, 1000);

    }

    private void trataPermissao() {
        btnGenerico.setVisibility(View.VISIBLE);
        LocalizacaoUtils.checaStatusPermissaoAsync(this, result ->{
            switch (result) {

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
                    tvMsgGenerica.setText(getResources().getString(R.string.bloq_local_erro));
                    btnGenerico.setText(getResources().getString(R.string.bloq_local_tente_nvm));
                    btnGenerico.setOnClickListener(view -> trataPermissaoDelay());
                    break;
            }
        });
    }

    private void configLocForaBrasil() {
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
    public void onBackPressed() {
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }
}
