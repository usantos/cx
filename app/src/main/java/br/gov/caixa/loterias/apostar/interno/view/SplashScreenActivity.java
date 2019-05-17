package br.gov.caixa.loterias.apostar.interno.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.android.volley.VolleyError;
import com.crashlytics.android.Crashlytics;

import br.gov.caixa.loterias.apostar.interno.BuildConfig;
import br.gov.caixa.loterias.apostar.interno.model.bo.AcessoBO;
import br.gov.caixa.loterias.apostar.interno.model.bo.AcessoBO_;
import br.gov.caixa.loterias.apostar.interno.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostar.interno.model.bo.RequestListener;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.MesAnoDTOResponse;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.VersionResponse;
import br.gov.caixa.loterias.apostar.interno.model.sp.LoginSP;
import io.fabric.sdk.android.Fabric;

public class SplashScreenActivity extends AppCompatActivity {

    public static final int SPLASH_TIME = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Fabric.with(this, new Crashlytics());

        AcessoBO_.getInstance_(this).getVerificaVersao(BuildConfig.VERSION_NAME, new RequestListener<VersionResponse>() {
            @Override
            public void onResponse(VersionResponse response) {
                if (!response.getPayload()) {
                    startActivity(new Intent(SplashScreenActivity.this, AtualizacaoActivity.class));
                } else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            LoginSP.limpar();
                            startActivity(new Intent(SplashScreenActivity.this, IntroducaoActivity.class));
                            finish();
                        }
                    }, SPLASH_TIME);
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                startActivity(new Intent(SplashScreenActivity.this, AppIndisponivelActivity.class));
            }
        });
    }
}
