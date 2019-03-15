package br.gov.caixa.loterias.apostar.interno.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import com.crashlytics.android.Crashlytics;

import br.gov.caixa.loterias.apostar.interno.model.sp.LoginSP;
import br.gov.caixa.loterias.apostar.interno.utils.FirebaseManager;
import io.fabric.sdk.android.Fabric;

public class SplashScreenActivity extends AppCompatActivity {

    public static final int SPLASH_TIME = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Fabric.with(this, new Crashlytics());

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
