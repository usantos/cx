package caixa.gov.br.loterias.ui;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import java.util.ArrayList;
import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.ui.Inteface.CartelaActivityInterface;
import caixa.gov.br.loterias.ui.adapter.DialogListViewAdapter;
import caixa.gov.br.loterias.ui.fragment.CartelaFragment;
import caixa.gov.br.loterias.ui.fragment.CartelaFragment.CartelaFragmentListener;
import caixa.gov.br.loterias.ui.fragment.SurpresinhaFragment;
import caixa.gov.br.loterias.ui.fragment.SurpresinhaFragment.SurpresinhaFragmentListener;
import caixa.gov.br.loterias.utils.ColorEnum;
import caixa.gov.br.loterias.utils.TipoApostaEnum;
import caixa.gov.br.loterias.utils.TipoDialog;
import static caixa.gov.br.loterias.utils.ColorEnum.*;
import static java.lang.String.*;

@EActivity(R.layout.activity_simular_aposta)
public class SimularApostaActivity extends AppCompatActivity implements OnClickListener, CartelaFragmentListener, SurpresinhaFragmentListener{

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
    SurpresinhaFragment surpresinhaFragment;

    AlertDialog dialog;

    private CartelaActivityInterface cartelaActivityInterface ;
    private CartelaActivityInterface surpresinhaActivityInterface;

    //mocks
    int qtdDezenasPossiveisSelecionado;
    int qtdConcursoSelecionado;
    int numeroConcursoAtual;
    String dataSorteioAtual;
    ColorEnum typeGameColorLight;
    ColorEnum typeGameColorDark;
    String qtdNumerosList[] = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
    String qtdConcursosList[] = {"1", "2", "3", "4", "5"};
    TipoApostaEnum tipoJogo;

    int alertDialogPosicaoSelecionada;
    TipoDialog tipoDialogMostrando;

    @AfterViews
    protected void init() {
        invalidateOptionsMenu();

        configuraFragments();
        configuraActivity();
    }

    private void configuraFragments(){
        fragmentManager = getSupportFragmentManager();

        cartelaFragment = new CartelaFragment();
        surpresinhaFragment = new SurpresinhaFragment();

        cartelaActivityInterface = cartelaFragment;
        surpresinhaActivityInterface = surpresinhaFragment;

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragmentSimularApostas, cartelaFragment).commit();
    }

    private void configuraActivity() {
        tipoJogo = (TipoApostaEnum) getIntent().getSerializableExtra("tipoAposta");

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
        qtdConcursoSelecionado = 1;
        numeroConcursoAtual = 1456;
        dataSorteioAtual = "16/06";


        // Passando um link
        Bundle bundle = new Bundle();
        bundle.putInt("qtdDezenasPossiveisSelecionado", qtdDezenasPossiveisSelecionado);
        bundle.putInt("numeroConcursoAtual", numeroConcursoAtual);
        bundle.putString("dataSorteioAtual", "16/06");
        bundle.putSerializable("typeGameColorLight", typeGameColorLight);
        bundle.putSerializable("typeGameColorDark", typeGameColorDark);


        cartelaFragment.cartelaFragmentBuilder(bundle);
        surpresinhaFragment.surpresinhaFragmentBuilder(bundle);

        atualizaTextoBotoesQtdSelecionadas();
        simularTelaAposta.setBackgroundResource(R.drawable.icon_seleciona);
        surpresinhaTela.setBackgroundResource(R.drawable.icon_surpresinha);

        configuraBotoesListeners();
    }

    private void atualizaTextoBotoesQtdSelecionadas(){
        qtdNumerosButton.setText(format("%d números", qtdDezenasPossiveisSelecionado));
        qtdConcursosButton.setText(format("%d concurso", qtdConcursoSelecionado));
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
                fragmentManager.beginTransaction().replace(R.id.fragmentSimularApostas, cartelaFragment).commit();

                simularTelaAposta.setBackgroundResource(R.drawable.icon_seleciona);
                surpresinhaTela.setBackgroundResource(R.drawable.icon_surpresinha);
                break;
            }

            case R.id.surpresinhaTela: {
                fragmentManager.beginTransaction().replace(R.id.fragmentSimularApostas, surpresinhaFragment).commit();

                simularTelaAposta.setBackgroundResource(R.drawable.icon_seleciona2);
                surpresinhaTela.setBackgroundResource(R.drawable.icon_surpresinha2);
                break;
            }

            case R.id.qtdNumerosButton: {
                CallbackDialog callback = new CallbackDialog() {
                    @Override
                    public void executeCallback(int position) {
                        alertDialogPosicaoSelecionada = position;
                        tipoDialogMostrando = TipoDialog.NUM_CARTELA;
                    }
                };

                showDialogSelection(montaListDialog(TipoDialog.NUM_CARTELA, qtdNumerosList), "Quantidade de números", callback);
                break;
            }

            case R.id.qtdConcursosButton: {
                CallbackDialog callback = new CallbackDialog() {
                    @Override
                    public void executeCallback(int position) {
                        alertDialogPosicaoSelecionada = position;
                        tipoDialogMostrando = TipoDialog.NUM_CONCURSO;
                    }
                };

                showDialogSelection(montaListDialog(TipoDialog.NUM_CONCURSO, qtdConcursosList), "Teimosinha", callback);
                break;
            }

            case R.id.btnCancelarDialog: {
                alertDialogPosicaoSelecionada = 0;
                tipoDialogMostrando = null;

                dialog.cancel();
                break;
            }

            case R.id.btnOkDialog: {
                if (tipoDialogMostrando == TipoDialog.NUM_CARTELA){
                    qtdDezenasPossiveisSelecionado = Integer.parseInt(qtdNumerosList[alertDialogPosicaoSelecionada]);
                    cartelaActivityInterface.qtdDezenasPossiveisSelecionado(qtdDezenasPossiveisSelecionado);
                    surpresinhaActivityInterface.qtdDezenasPossiveisSelecionado(qtdDezenasPossiveisSelecionado);
                }else if (tipoDialogMostrando == TipoDialog.NUM_CONCURSO){
                    qtdConcursoSelecionado = Integer.parseInt(qtdConcursosList[alertDialogPosicaoSelecionada]);
                }

                atualizaTextoBotoesQtdSelecionadas();

                dialog.cancel();

                break;
            }
        }
    }

    private ArrayList<String> montaListDialog(TipoDialog tipoDialog, String valores[]){
        ArrayList<String> valoresTextoDialog = new ArrayList<>();

        for (String valor: valores) {
            if (tipoDialog == TipoDialog.NUM_CARTELA){
                String numeroTexto = Integer.parseInt(valor) == 1 ? "número" : "números";
                valoresTextoDialog.add(String.format("%s %s por R$ %.2f", valor, numeroTexto, Float.parseFloat(valor) * 2));
            }else{
                String concursoTexto = Integer.parseInt(valor) == 1 ? "concurso" : "concursos";
                valoresTextoDialog.add(String.format("%s %s", valor, concursoTexto));
            }
        }

        return valoresTextoDialog;
    }

    private void configuraTipoApostaTela(String titleActivity, ColorEnum lightColor, ColorEnum darkColor){
        setTitle(titleActivity);
        getWindow().setStatusBarColor(Color.parseColor(darkColor.toString()));
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(lightColor.toString())));

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        toolBarSimularAposta.setBackgroundColor(Color.parseColor(darkColor.toString()));

        typeGameColorLight = lightColor;
        typeGameColorDark = darkColor;
    }

    private void showDialogSelection(ArrayList<String> valores, String titulo, final CallbackDialog callback){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(SimularApostaActivity.this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_select_list, null);

        dialogView.findViewById(R.id.btnCancelarDialog).setOnClickListener(this);
        dialogView.findViewById(R.id.btnOkDialog).setOnClickListener(this);

        final TextView dialogTitulo = dialogView.findViewById(R.id.dialog_titulo);
        dialogTitulo.setText(titulo);

        final DialogListViewAdapter qtdNumerosAdapter = new DialogListViewAdapter(this, valores);

        final ListView numerosListView = dialogView.findViewById(R.id.listViewOpcoes);
        numerosListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                qtdNumerosAdapter.setSelectedIndex(position);
                callback.executeCallback(position);
            }
        });


//        ArrayAdapter<String> qtdNumerosAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, valores);
        numerosListView.setAdapter(qtdNumerosAdapter);

        mBuilder.setView(dialogView);
        dialog = mBuilder.create();
        dialog.show();
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

        configuraMenuItem(R.id.concursoAtual, "Concurso\n" + numeroConcursoAtual);
        configuraMenuItem(R.id.sorteioAtual, "Sorteio\n" + dataSorteioAtual);

        return true;
    }

    private void configuraMenuItem(int id, String texto){
        View menuItem = findViewById(id);

        if (menuItem != null && menuItem instanceof TextView) {
            ((TextView) menuItem).setText(texto);
            ((TextView) menuItem).setTextSize(10);
            menuItem.setPadding(5, 5, 5, 5);
        }
    }

    // MÉTODOS DO FRAGMENT CARTELAFRAGMENTLISTENER
    @Override
    public int verificaNovoQtdDezenasMax(int qtdDezenasAtual) {
        if (qtdDezenasAtual < qtdNumerosList.length){
            qtdDezenasPossiveisSelecionado += 1;
            atualizaTextoBotoesQtdSelecionadas();
            cartelaActivityInterface.qtdDezenasPossiveisSelecionado(qtdDezenasPossiveisSelecionado);
            surpresinhaActivityInterface.qtdDezenasPossiveisSelecionado(qtdDezenasPossiveisSelecionado);
        }

        return qtdDezenasPossiveisSelecionado;
    }

    @Override
    public void adicionarApostaCarrinho(){
        Intent activity = new Intent(this, ConfirmadoInclusaoCarrinhoActivity.class);
        activity.putExtra("tipoAposta", tipoJogo);
        startActivity(activity);
    }
}


interface CallbackDialog{
    void executeCallback(int position);
}