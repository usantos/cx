package br.gov.caixa.loterias.apostas.controllers;

import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.utils.AppCenterManager;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.fragment.BottomSheetFragment;
import br.gov.caixa.loterias.apostas.view.fragment.JogoResponsavelApostadorFragment;
import br.gov.caixa.loterias.apostas.view.fragment.JogoResponsavelConhecaFragment;
import br.gov.caixa.loterias.apostas.view.fragment.JogoResponsavelPrincipiosFragment;


public class JogoResponsavelActivity extends AppCompatActivity {

    //region Layout Variables
    private TextView tvRegras;
    private RadioButton rbConheca,rbPrincipios,rbApostador;
    private ImageView ivDuvidas ;
    private BottomSheetFragment bottomSheetFragment;
    //endregion

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
        ivDuvidas = findViewById(R.id.iv_duvidas);
        bottomSheetFragment = new BottomSheetFragment();

        tvRegras.setText(ViewUtils.textColor(this,getResources().getString(R.string.jr_aprender_regras),R.color.jr_amarelo));

        setaToolbar();
    }

    public void setaMetodos(){

        rbConheca.setOnClickListener(view -> {
            rbPrincipios.setTypeface(null, Typeface.NORMAL);
            rbConheca.setTypeface(null, Typeface.BOLD);
            rbApostador.setTypeface(null, Typeface.NORMAL);
            setaFragmentConheca();
        });

        rbPrincipios.setOnClickListener(view -> {
            rbPrincipios.setTypeface(null, Typeface.BOLD);
            rbConheca.setTypeface(null, Typeface.NORMAL);
            rbApostador.setTypeface(null, Typeface.NORMAL);
            setaFragmentPrincipios();

        });

        rbApostador.setOnClickListener(view -> {
            rbPrincipios.setTypeface(null, Typeface.NORMAL);
            rbConheca.setTypeface(null, Typeface.NORMAL);
            rbApostador.setTypeface(null, Typeface.BOLD);
            setaFragmentApostador();
        });

        ivDuvidas.setOnClickListener(view -> {
            AppCenterManager.registraEvento(getResources().getString(R.string.evento_jr_duvida));
            setaBottomSheet();
        });

        rbConheca.performClick();
    }

    private void setaToolbar() {
        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.background_gradient_jogo_responsavel));
        //setTitle(getResources().getString(R.string.jr_titulo));
        setTitle(ViewUtils.textCaixaSTDBoldTitle(this, getResources().getString(R.string.jr_titulo)));

    }

    private void setaFragmentConheca(){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.v_container_fragment, JogoResponsavelConhecaFragment.newInstance());
        fragmentTransaction.commit();
    }

    private void setaFragmentPrincipios(){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.v_container_fragment, JogoResponsavelPrincipiosFragment.newInstance());
        fragmentTransaction.commit();
    }

    private void setaFragmentApostador(){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.v_container_fragment, JogoResponsavelApostadorFragment.newInstance());
        fragmentTransaction.commit();
    }



    private void setaBottomSheet() {
        bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
    }
    //endregion

    //region Support Methods
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    //endregion Support Methods
}