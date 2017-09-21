package caixa.gov.br.loterias.ui;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.ui.Inteface.CartelaActivityInterface;
import caixa.gov.br.loterias.ui.fragment.CartelaFragment;
import caixa.gov.br.loterias.ui.fragment.CartelaFragment.CartelaFragmentListener;
import caixa.gov.br.loterias.ui.fragment.CartelaFragment.OnFragmentInteractionListener;
import caixa.gov.br.loterias.utils.ColorEnum;
import caixa.gov.br.loterias.utils.TipoApostaEnum;
import caixa.gov.br.loterias.utils.ViewUtils;
import static caixa.gov.br.loterias.utils.ColorEnum.*;
import static java.lang.String.*;



@EActivity(R.layout.activity_simular_aposta)
public class SimularApostaActivity extends AppCompatActivity implements OnClickListener, OnFragmentInteractionListener, CartelaFragmentListener{

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

    // Fragments
    FragmentManager fragmentManager;
    CartelaFragment cartelaFragment;

    private CartelaActivityInterface cartelaActivityInterface ;

    //mocks
    int qtdDezenasPossiveisSelecionado;
    int qtdMinDezenasPossiveisSelecionado;
    int qtdMaxDezenasPossiveisSelecionado;
    int qtdConcursoMax;
    int numeroConcursoAtual;
    String dataSorteioAtual;
    ColorEnum typeGameColorLight;
    ColorEnum typeGameColorDark;



    @AfterViews
    protected void init() {
        invalidateOptionsMenu();

        configuraFragments();
        configuraActivity();
    }

    private void configuraFragments(){
        fragmentManager = getSupportFragmentManager();

        cartelaFragment = new CartelaFragment();
        cartelaActivityInterface = cartelaFragment;

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.fragmentSimularApostas, cartelaFragment);
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

        // MOCK
        qtdDezenasPossiveisSelecionado = 4;
        qtdMinDezenasPossiveisSelecionado = 4;
        qtdMaxDezenasPossiveisSelecionado = 6;
        qtdConcursoMax = 1;
        numeroConcursoAtual = 1456;
        dataSorteioAtual = "16/06";


        // Passando um link
        Bundle bundle = new Bundle();
        bundle.putInt("qtdDezenasPossiveisSelecionado", qtdDezenasPossiveisSelecionado);
        bundle.putInt("qtdMinDezenasPossiveisSelecionado", qtdMinDezenasPossiveisSelecionado);
        bundle.putInt("qtdMaxDezenasPossiveisSelecionado", qtdMaxDezenasPossiveisSelecionado);
        bundle.putInt("numeroConcursoAtual", numeroConcursoAtual);
        bundle.putString("dataSorteioAtual", "16/06");
        bundle.putSerializable("typeGameColorLight", typeGameColorLight);
        bundle.putSerializable("typeGameColorDark", typeGameColorDark);
        cartelaFragment.setArguments(bundle);

        atualizaTextoBotoesQtdSelecionadas();
        simularTelaAposta.setBackgroundResource(R.drawable.icon_seleciona);
        surpresinhaTela.setBackgroundResource(R.drawable.icon_surpresinha);

        configuraBotoesListeners();
    }

    private void atualizaTextoBotoesQtdSelecionadas(){
        qtdNumerosButton.setText(format("%d números", qtdDezenasPossiveisSelecionado));
        qtdConcursosButton.setText(format("%d concurso", qtdConcursoMax));
    }

    private void configuraBotoesListeners(){
        simularTelaAposta.setOnClickListener(this);
        surpresinhaTela.setOnClickListener(this);
        qtdNumerosButton.setOnClickListener(this);
        qtdConcursosButton.setOnClickListener(this);

        simularTelaAposta.setBackgroundResource(R.drawable.icon_seleciona);
        surpresinhaTela.setBackgroundResource(R.drawable.icon_surpresinha);
    }

    // Pegar o handle dos botoes que mostrarao o seu respectivo fragment na activity
    public void onClick(View v) {
        switch (v.getId()) {
            case  R.id.simularTelaAposta: {
                if((fragmentManager.findFragmentById(R.id.fragmentSimularApostas) instanceof CartelaFragment)){
                    fragmentManager.beginTransaction().show(fragmentManager.findFragmentById(R.id.fragmentSimularApostas)).commit();
                } else {
                    fragmentManager.beginTransaction().add(R.id.fragmentSimularApostas, cartelaFragment).commit();
                }
//                if(fragmentManager.findFragmentByTag("two") != null){
//                    //if the other fragment is visible, hide it.
//                    fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("two")).commit();
//                }

                simularTelaAposta.setBackgroundResource(R.drawable.icon_seleciona);
                surpresinhaTela.setBackgroundResource(R.drawable.icon_surpresinha);
                }

                break;

            case R.id.surpresinhaTela: {
                fragmentManager.beginTransaction().hide(fragmentManager.findFragmentById(R.id.fragmentSimularApostas)).commit();

                simularTelaAposta.setBackgroundResource(R.drawable.icon_seleciona2);
                surpresinhaTela.setBackgroundResource(R.drawable.icon_surpresinha2);
                break;
            }

            case R.id.qtdNumerosButton: {
                if (qtdDezenasPossiveisSelecionado < qtdMaxDezenasPossiveisSelecionado){
                    qtdDezenasPossiveisSelecionado += 1;
                    atualizaTextoBotoesQtdSelecionadas();
                    cartelaActivityInterface.qtdDezenasPossiveisSelecionado(qtdDezenasPossiveisSelecionado);
                }

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


    // MÉTODOS DO FRAGMENT CARTELAFRAGMENTLISTENER
    @Override
    public int verificaNovoQtdDezenasMax(int qtdDezenasAtual) {
        if (qtdDezenasAtual < qtdMaxDezenasPossiveisSelecionado){
            qtdDezenasPossiveisSelecionado += 1;
            atualizaTextoBotoesQtdSelecionadas();
            cartelaActivityInterface.qtdDezenasPossiveisSelecionado(qtdDezenasPossiveisSelecionado);
        }

        return qtdDezenasPossiveisSelecionado;
    }
}
