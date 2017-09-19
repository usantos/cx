package caixa.gov.br.loterias.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.ui.fragment.CartelaFragment;
import caixa.gov.br.loterias.utils.ColorEnum;
import caixa.gov.br.loterias.utils.TipoApostaEnum;
import caixa.gov.br.loterias.utils.ViewUtils;

import static caixa.gov.br.loterias.utils.ColorEnum.*;
import static java.lang.String.*;

@EActivity(R.layout.activity_simular_aposta)
public class SimularApostaActivity extends AppCompatActivity implements View.OnClickListener, CartelaFragment.OnFragmentInteractionListener {

    @ViewById
    DrawerLayout drawer_layout;

    @ViewById
    ConstraintLayout toolBarSimularAposta;

    @ViewById
    Button simularTelaAposta;

    @ViewById
    Button surpresinhaTela;

    @ViewById
    Button qtdNumerosButton;



    @ViewById
    Button qtdConcursosButton;

    private String[] dezenas = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20"
            , "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43"
            , "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59", "60", "61", "62", "63", "64", "65", "66"};
    private ArrayList<String> dezenasSelecionadas = new ArrayList<>();

    //mocks
    int qtdNumerosMax;
    int numeroConcursoAtual;
    String dataSorteioAtual;

    ColorEnum typeGameColorLight;
    ColorEnum typeGameColorDark;

    // Fragments
    FragmentManager fm;
    CartelaFragment surpresinhaFragment;

    @AfterViews
    protected void init() {
        invalidateOptionsMenu();

        configuraFragments();
        configuraActivity();
    }

    private void configuraFragments(){
        fm = getSupportFragmentManager();

        surpresinhaFragment = new CartelaFragment();

        FragmentManager fm = getSupportFragmentManager();

// Abre uma transação e adiciona
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.fragmentSimularApostas, surpresinhaFragment);
        ft.commit();
    }

    private void configuraActivity() {
        TipoApostaEnum tipoJogo = (TipoApostaEnum) getIntent().getSerializableExtra("tipoAposta");

        switch (tipoJogo){
            case MEGASENA:
                configuraTipoApostaTela("Mega Sena", MEGASENACLARO, MEGASENAESCURO);
                break;
            case QUINA:
                configuraTipoApostaTela("Quina", QUINACLARO, QUINAESCURO);
                break;
            case LOTOFACIL:
                configuraTipoApostaTela("Lotofácil", LOTOFACILCLARO, LOTOFACILESCURO);
                break;
            case DUPLASENA:
                configuraTipoApostaTela("Duplasena", DUPLASENACLARO, DUPLASENAESCURO);
        }

        qtdNumerosMax = 4;
        numeroConcursoAtual = 1456;
        dataSorteioAtual = "16/06";

        // Passando um link
        Bundle bundle = new Bundle();
        bundle.putInt("qtdNumerosMax", qtdNumerosMax);
        bundle.putInt("numeroConcursoAtual", numeroConcursoAtual);
        bundle.putString("dataSorteioAtual", "16/06");
        bundle.putSerializable("typeGameColorLight", typeGameColorLight);
        bundle.putSerializable("typeGameColorDark", typeGameColorDark);
        surpresinhaFragment.setArguments(bundle);

        qtdNumerosButton.setText(format("%d números", qtdNumerosMax));

        simularTelaAposta.setBackgroundResource(R.drawable.icon_seleciona);
        surpresinhaTela.setBackgroundResource(R.drawable.icon_surpresinha);

        configuraBotoesListeners();
    }

    private void configuraBotoesListeners(){
        simularTelaAposta.setOnClickListener(this);
        surpresinhaTela.setOnClickListener(this);
        qtdNumerosButton.setOnClickListener(this);
        qtdConcursosButton.setOnClickListener(this);
    }

    // Pegar o handle dos botoes que mostrarao o seu respectivo fragment na activity
    public void onClick(View v) {
        switch (v.getId()) {
            case  R.id.simularTelaAposta: {
                if(!(fm.findFragmentById(R.id.fragmentSimularApostas) instanceof CartelaFragment)){
                    // Substitui um Fragment
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.replace(R.id.fragmentSimularApostas, surpresinhaFragment);
                    ft.commit();

                    simularTelaAposta.setBackgroundResource(R.drawable.icon_seleciona);
                    surpresinhaTela.setBackgroundResource(R.drawable.icon_surpresinha);

                }

                break;
            }

            case R.id.surpresinhaTela: {
                // Remove um Fragment
                FragmentTransaction ft = fm.beginTransaction();
                ft.remove(surpresinhaFragment);
                ft.commit();

                simularTelaAposta.setBackgroundResource(R.drawable.icon_seleciona2);
                surpresinhaTela.setBackgroundResource(R.drawable.icon_surpresinha2);
                break;
            }

            case R.id.qtdNumerosButton: {
                ViewUtils.alert(this, "TESTE NUMEROS");

                break;
            }

            case R.id.qtdConcursosButton: {
                ViewUtils.alert(this, "TESTE CONCURSOS");

                break;
            }
        }
    }

    private void configuraTipoApostaTela(String titleActivity, ColorEnum lightColor, ColorEnum darkColor){
        setTitle(titleActivity);
        getWindow().setStatusBarColor(Color.parseColor(darkColor.toString()));
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(lightColor.toString())));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolBarSimularAposta.setBackgroundColor(Color.parseColor(darkColor.toString()));

        typeGameColorLight = lightColor;
        typeGameColorDark = darkColor;
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.itens_simular_actionbar, menu);

        return true;
    }

    // Mudando o texto dos MenuItem de acordo com os dados
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        menu.findItem(R.id.concursoAtual).setTitle("Concurso\n" + numeroConcursoAtual);
        menu.findItem(R.id.sorteioAtual).setTitle("Sorteio\n" + dataSorteioAtual);

        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }




}
