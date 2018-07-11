package caixa.gov.br.loterias.view;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import caixa.gov.br.loterias.R;

public class SplashScreenActivity extends AppCompatActivity {

    public static final int SPLASH_TIME = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
                startActivity(new Intent(SplashScreenActivity.this, IntroducaoActivity.class));
            }
        }, SPLASH_TIME);
    }
}
