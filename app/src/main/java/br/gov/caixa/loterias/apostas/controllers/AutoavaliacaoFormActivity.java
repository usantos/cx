package br.gov.caixa.loterias.apostas.controllers;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;

public class AutoavaliacaoFormActivity extends AppCompatActivity {

    //private TextView toolBarTxt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autoavaliacao_form);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(ViewUtils.textFuturaAndFuturaBoldTitle(this, getString(R.string.texto_futura_bold, getString(R.string.autoavaliacao_apostador))));


//        toolBarTxt = findViewById(R.id.toolbar_title);
//        toolBarTxt.setText(ViewUtils.textFuturaAndFuturaBoldTitle(this, getString(R.string.label_voltar_apostas_individuais)));
//        toolBarTxt.setVisibility(View.VISIBLE);
    }


}
