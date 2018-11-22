package br.gov.caixa.loterias.apostar.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import br.gov.caixa.loterias.apostar.R;
import br.gov.caixa.loterias.apostar.model.bo.DadosUsuarioBO;
import br.gov.caixa.loterias.apostar.utils.ViewUtils;

public class RestritoDezoitoAnosActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restrito_dezoito_anos);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView textViewTextoRestrito = (TextView) findViewById(R.id.textViewTextoRestrito);
        textViewTextoRestrito.setText(ViewUtils.textFuturaAndFuturaBold(this, " O uso do aplicativo é restrito a\nmaiores de _18 anos._"));

        Button buttonDigitarAposta = (Button) findViewById(R.id.entendiButton);
        buttonDigitarAposta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent activity = new Intent(RestritoDezoitoAnosActivity.this, IntroducaoActivity.class);
                startActivity(activity);
            }
        });
    }

}
