package caixa.gov.br.loterias.view;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.model.sp.InformacoesUsuarioSP;

public class SplashScreenActivity extends AppCompatActivity {

    public static final int SPLASH_TIME = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(InformacoesUsuarioSP.existeUsuarioLogado()) {
                    PrincipalActivity_.intent(SplashScreenActivity.this).start();
                    finish();
                }else{
                    startActivity(new Intent(SplashScreenActivity.this, IntroducaoActivity.class));
                }
                finish();
            }
        }, SPLASH_TIME);
    }
}
