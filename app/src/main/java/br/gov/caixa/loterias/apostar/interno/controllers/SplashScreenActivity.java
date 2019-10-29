package br.gov.caixa.loterias.apostar.interno.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.android.volley.VolleyError;
import com.crashlytics.android.Crashlytics;

import br.gov.caixa.loterias.apostar.interno.BuildConfig;
import br.gov.caixa.loterias.apostar.interno.model.bo.AcessoBO_;
import br.gov.caixa.loterias.apostar.interno.model.bo.RequestListener;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.VersionResponse;
import br.gov.caixa.loterias.apostar.interno.model.sp.LoginSP;
import io.fabric.sdk.android.Fabric;

public class SplashScreenActivity extends AppCompatActivity {

    //public static final int SPLASH_TIME = 2000;
    public static final int SPLASH_TIME = 0;
    //alterar depois

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Fabric.with(this, new Crashlytics());
        verificaVersao();
    }

    public void verificaVersao() {
        AcessoBO_.getInstance_(this).getVerificaVersao(BuildConfig.VERSION_NAME, new RequestListener<VersionResponse>() {
            @Override
            public void onResponse(VersionResponse response) {
                if (!response.getPayload()) {
                    startActivity(new Intent(SplashScreenActivity.this, ConfiguracaoRapidaoActivity.class));
                } else {
                    new Handler().postDelayed(() -> {
                        LoginSP.limpar();
                        startActivity(new Intent(SplashScreenActivity.this, ConfiguracaoRapidaoActivity.class));
                        finish();
                    }, SPLASH_TIME);
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                startActivityForResult(new Intent(SplashScreenActivity.this, ConfiguracaoRapidaoActivity.class), 1);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1) {
            verificaVersao();
        }
    }
}
