package br.gov.caixa.loterias.apostas.controllers;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.VolleyError;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.ApostaBO_;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.AutoavaliacaoResponse;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;

public class AutoavaliacaoFormActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autoavaliacao_form);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(ViewUtils.textFuturaAndFuturaBoldTitle(this, getString(R.string.texto_futura_bold, getString(R.string.autoavaliacao_apostador))));

        buscaPerguntas();
    }

    public void buscaPerguntas() {
        ApostaBO_.getInstance_(this).getAutoavaliacaoPerguntas(new RequestListener<AutoavaliacaoResponse>() {
            @Override
            public void onResponse(AutoavaliacaoResponse result) {
                String xxx = "OK";
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                String xxx = "ERRO";

            }
        });
    }

}
