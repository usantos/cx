package br.gov.caixa.loterias.apostas.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.VolleyError;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import br.gov.caixa.loterias.apostas.BuildConfig;
import br.gov.caixa.loterias.apostas.model.bo.AcessoBO_;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.VersionResponse;
import br.gov.caixa.loterias.apostas.model.sp.LoginSP;
import br.gov.caixa.loterias.apostas.utils.LocalizacaoUtils;


public class SplashScreenActivity extends AppCompatActivity {

    public static final int SPLASH_TIME = 2000;
    public static final int REQ_LOC = 2;
    private VersionResponse responseVersao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        verificaVersao();
    }

    private void trataPermissao() {
        int statusPermissao = LocalizacaoUtils.checaStatusPermissao(this);
        if (statusPermissao == LocalizacaoUtils.LOC_NAO_PERMITIDA) {
            LocalizacaoUtils.solicitaPermissao(this);
        } else {
            if (statusPermissao == LocalizacaoUtils.LOC_NO_BRASIL) {
                trataSucesso(responseVersao);
            } else {
                startActivityForResult(new Intent(SplashScreenActivity.this, LocalizacaoActivity.class), REQ_LOC);
            }
        }
    }

    public void verificaVersao() {
        AcessoBO_.getInstance_(this).getVerificaVersao(BuildConfig.VERSION_NAME, new RequestListener<VersionResponse>() {
            @Override
            public void onResponse(VersionResponse response) {
                responseVersao = response;
                trataPermissao();
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                startActivityForResult(new Intent(SplashScreenActivity.this, AppIndisponivelActivity.class), 1);
            }
        });
    }


    private void trataSucesso (VersionResponse response) {
        if (!response.getPayload()) {
            startActivity(new Intent(SplashScreenActivity.this, AtualizacaoActivity.class));
        } else {
            new Handler().postDelayed(() -> {
                LoginSP.limpar();
                startActivity(new Intent(SplashScreenActivity.this, IntroducaoActivity.class));
                finish();
            }, SPLASH_TIME);
        }
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQ_LOC) {
            trataSucesso(responseVersao);
        } else if (resultCode == RESULT_OK && requestCode == 1) {
            verificaVersao();
        }
    }
}
