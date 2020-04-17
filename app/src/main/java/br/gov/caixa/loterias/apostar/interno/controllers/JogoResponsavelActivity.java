package br.gov.caixa.loterias.apostar.interno.controllers;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import br.gov.caixa.loterias.apostar.interno.R;
import br.gov.caixa.loterias.apostar.interno.utils.ViewUtils;

public class JogoResponsavelActivity extends AppCompatActivity {

    private TextView tvRegras;
    private RadioButton rbConheca,rbPrincipios,rbApostador;
    private ImageView ivIndicator1,ivIndicator2,ivIndicator3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jogo_responsavel);
        setaViews();
        setaMetodos();
    }

    //region Initialization Methods
    private void setaViews() {
        tvRegras = findViewById(R.id.tv_aprender_regras);
        rbConheca = findViewById(R.id.rb_conheca);
        rbApostador = findViewById(R.id.rb_apostador);
        rbPrincipios = findViewById(R.id.rb_principios);
        ivIndicator1 = findViewById(R.id.iv_indicator_1);
        ivIndicator2 = findViewById(R.id.iv_indicator_2);
        ivIndicator3 = findViewById(R.id.iv_indicator_3);


        tvRegras.setText(ViewUtils.textColor(this,getResources().getString(R.string.jr_aprender_regras),R.color.jr_amarelo));

        setaToolbar();
    }

    public void setaMetodos(){

        rbConheca.setOnClickListener(view -> {
            ivIndicator1.setVisibility(View.VISIBLE);
            ivIndicator2.setVisibility(View.INVISIBLE);
            ivIndicator3.setVisibility(View.INVISIBLE);
            rbPrincipios.setTypeface(null, Typeface.NORMAL);
            rbConheca.setTypeface(null, Typeface.BOLD);
            rbApostador.setTypeface(null, Typeface.NORMAL);
        });

        rbPrincipios.setOnClickListener(view -> {
            ivIndicator1.setVisibility(View.INVISIBLE);
            ivIndicator2.setVisibility(View.VISIBLE);
            ivIndicator3.setVisibility(View.INVISIBLE);
            rbPrincipios.setTypeface(null, Typeface.BOLD);
            rbConheca.setTypeface(null, Typeface.NORMAL);
            rbApostador.setTypeface(null, Typeface.NORMAL);

        });

        rbApostador.setOnClickListener(view -> {
            ivIndicator1.setVisibility(View.INVISIBLE);
            ivIndicator2.setVisibility(View.INVISIBLE);
            ivIndicator3.setVisibility(View.VISIBLE);
            rbPrincipios.setTypeface(null, Typeface.NORMAL);
            rbConheca.setTypeface(null, Typeface.NORMAL);
            rbApostador.setTypeface(null, Typeface.BOLD);
        });

        rbConheca.performClick();

    }

    private void setaToolbar() {
        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.background_gradient_jogo_responsavel));
        setTitle(getResources().getString(R.string.jr_titulo));
    }
    //endregion

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}