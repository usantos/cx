package caixa.gov.br.loterias.view;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.bo.silce.dto.ModalidadeEnum;
import caixa.gov.br.loterias.bo.silce.dto.ParametroEquipe;
import caixa.gov.br.loterias.bo.silce.dto.ParametroJogoDTO;
import caixa.gov.br.loterias.bo.silce.dto.ParametroValorApostaDTO;
import caixa.gov.br.loterias.utils.TipoDialog;
import caixa.gov.br.loterias.utils.ViewUtils;
import caixa.gov.br.loterias.utils.interfaces.CartelaFragmentListener;
import caixa.gov.br.loterias.view.fragment.CartelaFragment;
import caixa.gov.br.loterias.view.fragment.SurpresinhaFragment;
import caixa.gov.br.loterias.view.fragment.SurpresinhaFragment.SurpresinhaFragmentListener;

import static java.lang.String.format;

@EActivity(R.layout.activity_simular_aposta)
public class SimularApostaActivity extends AppCompatActivity implements OnClickListener, CartelaFragmentListener, SurpresinhaFragmentListener {

    public static final String ESCOLHA_TIME_CORACAO_SURPRESINHA = "escolhaTimeCoracaoSurpresinha";
    @ViewById
    DrawerLayout drawer_layout;

    @ViewById
    ConstraintLayout toolBarLoteca, viewTabBar;

    @ViewById
    Button simularTelaAposta, surpresinhaTela, qtdNumerosButton, qtdConcursosButton, botaoLimparAposta, botaoAdicionarCompletarCartela, botaoCarrinho;

    @ViewById
    FrameLayout fragmentSimularApostas;

    @ViewById
    TextView labelValueSimplesLoteca, labelValueDuplasLoteca, labelValueTriplasLoteca, textoDeNomeInfo, textoSalveEstaAposta, valorApostaCartela, textInfoValorAposta, textViewCarrinhoCartelaCount,
            textInfoCarrinho;

    @ViewById
    LinearLayout toolBarSimularAposta;

    @ViewById
    RelativeLayout salvarApostaLayout, carrinhoApostas, simularApostaLayoutCarrinhoApostas;

    @ViewById
    ImageButton btnSalvarAposta;

    @ViewById
    EditText editNomeAposta;

    @ViewById
    ImageView imageViewPaginacao, iconStarSalvar;

    private static int RESULT_FINISH = 3;

    // Fragments
    FragmentManager fragmentManager;
    CartelaFragment cartelaFragment;
    SurpresinhaFragment surpresinhaFragment;

    AlertDialog dialog;

    //mocks
    public int qtdDezenasPossiveisSelecionado;
    public int qtdConcursoSelecionado;
    int numeroConcursoAtual;
    String dataSorteioAtual;
    int typeGameColorLight;
    int typeGameColorDark;


    ArrayList<String> qtdNumerosList = new ArrayList<String>();
    ArrayList<String> qtdConcursosList = new ArrayList<String>();
    ModalidadeEnum tipoJogo;

    String textoBotaoPrognosticosSelecionado = "";
    String textoBotaoSurpresinhasSelecionado = "";

    int alertDialogPosicaoSelecionada;
    TipoDialog tipoDialogMostrando;
    public ParametroJogoDTO parametroSimulacao;

    private int toolBarMenu;

    private Bundle bundle;


    public ParametroJogoDTO getParametroSimulacao() {
        return parametroSimulacao;
    }

    public void setParametroSimulacao(ParametroJogoDTO parametroSimulacao) {
        this.parametroSimulacao = parametroSimulacao;
    }

    @AfterViews
    protected void init() {
        invalidateOptionsMenu();

        parametroSimulacao = new Gson().fromJson(getIntent().getStringExtra("modalidade"), ParametroJogoDTO.class);
        configuraParametrosJogo();
        configuraFragments();
        configuraActivity();
    }

    private void configuraParametrosJogo() {
        if(parametroSimulacao != null) {
            for (ParametroValorApostaDTO parametro : parametroSimulacao.getValoresAposta()) {
                qtdNumerosList.add("" + parametro.getNumeroPrognosticos() + " números por " + ViewUtils.getMoedaFormat(parametro.getValor()));
            }
            if (parametroSimulacao.getTeimosinhas() != null) {
                for (Integer numeroTeimosinha : parametroSimulacao.getTeimosinhas()) {
                    if (numeroTeimosinha == 0) {
                        qtdConcursosList.add("nenhum");
                    } else if (numeroTeimosinha == 1) {
                        qtdConcursosList.add("" + numeroTeimosinha + " concurso");
                    } else {
                        qtdConcursosList.add("" + numeroTeimosinha + " concursos");
                    }
                }
            }
        }
    }

    private void configuraFragments() {
        fragmentManager = getSupportFragmentManager();

        cartelaFragment = new CartelaFragment();
        surpresinhaFragment = new SurpresinhaFragment();


        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragmentSimularApostas, cartelaFragment).commit();
    }

    private void configuraActivity() {

        tipoJogo = (ModalidadeEnum) getIntent().getSerializableExtra("tipoAposta");

        switch (tipoJogo) {
            case MEGA_SENA:
                configuraTipoApostaTela("_mega-sena_", R.color.megasenaclaro, R.color.megasenaescuro, View.VISIBLE, View.GONE);
                this.toolBarMenu = R.menu.itens_simular_actionbar_termos;
                break;
            case QUINA:
                configuraTipoApostaTela("_quina_", R.color.quinaclaro, R.color.quinaescuro, View.VISIBLE, View.GONE);
                this.toolBarMenu = R.menu.itens_simular_actionbar_termos;
                break;
            case LOTOFACIL:
                configuraTipoApostaTela("_lotofácil_", R.color.lotofacilclaro, R.color.lotofacilescuro, View.VISIBLE, View.GONE);
                this.toolBarMenu = R.menu.itens_simular_actionbar_termos;
                break;
            case DUPLA_SENA:
                configuraTipoApostaTela("_dupla sena_", R.color.duplasenaclaro, R.color.duplasenaescuro, View.VISIBLE, View.GONE);
                this.toolBarMenu = R.menu.itens_simular_actionbar_termos;
                break;
            case TIMEMANIA:
                configuraTipoApostaTela("_timemania_", R.color.timemaniaclaro, R.color.timemaniaescuro, View.VISIBLE, View.GONE);
                this.toolBarMenu = R.menu.itens_simular_actionbar_duvida;
                break;
            case LOTOMANIA:
                configuraTipoApostaTela("_lotomania_", R.color.lotomaniaclaro, R.color.lotomaniaescuro, View.VISIBLE, View.GONE);
                this.toolBarMenu = R.menu.itens_simular_actionbar_termos;
                break;
            case LOTECA:
                configuraTipoApostaTela("_loteca_", R.color.lotecaclaro, R.color.lotecaescuro, View.GONE, View.VISIBLE);
                this.toolBarMenu = R.menu.itens_simular_actionbar_duvida;
                break;
            case LOTOGOL:
                configuraTipoApostaTela("_lotogol_", R.color.lotogolclaro, R.color.lotogolescuro, View.GONE, View.GONE);
                this.toolBarMenu = R.menu.itens_simular_actionbar_duvida;
                break;
        }

        qtdDezenasPossiveisSelecionado = parametroSimulacao.getQuantidadeMinima() != null ? parametroSimulacao.getQuantidadeMinima() : 0;
        qtdConcursoSelecionado = parametroSimulacao.getTeimosinhas() != null ? parametroSimulacao.getTeimosinhas().get(0) == 0 ? 0 : parametroSimulacao.getTeimosinhas().get(0) : 0;
        dataSorteioAtual = parametroSimulacao.getConcurso().getDataSorteio();


        // Passando um link
        bundle = new Bundle();
        bundle.putInt("qtdDezenasPossiveisSelecionado", qtdDezenasPossiveisSelecionado);
        bundle.putInt("numeroConcursoAtual", numeroConcursoAtual);
        bundle.putString("dataSorteioAtual", dataSorteioAtual);
        bundle.putSerializable("typeGameColorLight", typeGameColorLight);
        bundle.putSerializable("typeGameColorDark", typeGameColorDark);


        cartelaFragment.cartelaFragmentBuilder(bundle);
        surpresinhaFragment.surpresinhaFragmentBuilder(bundle);

        atualizaTextoBotoesQtdSelecionadas();
        simularTelaAposta.setBackgroundResource(R.drawable.icon_seleciona);
        surpresinhaTela.setBackgroundResource(R.drawable.icon_surpresinha);

        configuraBotoesListeners();
    }

    public void atualizaTextoBotoesQtdSelecionadas() {
        qtdNumerosButton.setText(format("%d números", qtdDezenasPossiveisSelecionado));
        if (qtdConcursoSelecionado == 0) {
            qtdConcursosButton.setText("1 concurso");
        } else {
            qtdConcursosButton.setText(format("%d concurso", qtdConcursoSelecionado));
        }
    }

    private void configuraBotoesListeners() {
        simularTelaAposta.setOnClickListener(this);
        surpresinhaTela.setOnClickListener(this);
        qtdNumerosButton.setOnClickListener(this);
        qtdConcursosButton.setOnClickListener(this);

        simularTelaAposta.setBackgroundResource(R.drawable.icon_seleciona);
        surpresinhaTela.setBackgroundResource(R.drawable.icon_surpresinha);
    }

    private void modalNumeros() {
        // custom dialog
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_dialog_list_view);

        // set the custom dialog components - listview and button

        final ListView listViewItens = (ListView) dialog.findViewById(R.id.listaItensDialog);
        TextView tituloDialog = (TextView) dialog.findViewById(R.id.tituloDialogCustom);
        tituloDialog.setText("Quant. de Números");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(SimularApostaActivity.this,
                R.layout.item_text_popup, R.id.textList, qtdNumerosList);

        // Assign adapter to ListView
        listViewItens.setAdapter(adapter);
        // ListView Item Click Listener
        listViewItens.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // ListView Clicked item index
                int itemPosition = position;

                // ListView Clicked item value
                String itemValue = (String) listViewItens.getItemAtPosition(position);
                textoBotaoPrognosticosSelecionado = parametroSimulacao.getValoresAposta().get(position).getNumeroPrognosticos() + " números";
                qtdDezenasPossiveisSelecionado = parametroSimulacao.getValoresAposta().get(position).getNumeroPrognosticos();
            }

        });

        Button dialogButtonOk = (Button) dialog.findViewById(R.id.dialogButtonOK);
        Button dialogButtonCancelar = (Button) dialog.findViewById(R.id.dialogButtonCancelar);

        // if button is clicked, close the custom dialog
        dialogButtonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qtdNumerosButton.setText(textoBotaoPrognosticosSelecionado);
                cartelaFragment.atualizaValorTotalAposta(qtdDezenasPossiveisSelecionado, qtdConcursoSelecionado);
                cartelaFragment.atualizaStatusBotaoFinalizar();
                if (surpresinhaFragment.parametroJogo != null) {
                    surpresinhaFragment.atualizaValorTotalAposta(qtdDezenasPossiveisSelecionado, qtdConcursoSelecionado);
                }
                dialog.dismiss();
            }
        });

        dialogButtonCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void modalTeimosinhas() {
        // custom dialog
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_dialog_list_view);

        // set the custom dialog components - listview and button

        final ListView listViewItens = (ListView) dialog.findViewById(R.id.listaItensDialog);
        TextView tituloDialog = (TextView) dialog.findViewById(R.id.tituloDialogCustom);
        tituloDialog.setText("Teimosinhas");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(SimularApostaActivity.this,
                R.layout.item_text_popup, R.id.textList, qtdConcursosList);

        // Assign adapter to ListView
        listViewItens.setAdapter(adapter);
        // ListView Item Click Listener
        listViewItens.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // ListView Clicked item index
                int itemPosition = position;

                // ListView Clicked item value
                String itemValue = (String) listViewItens.getItemAtPosition(position);
                textoBotaoSurpresinhasSelecionado = itemValue;
                qtdConcursoSelecionado = parametroSimulacao.getTeimosinhas().get(position);
            }

        });

        Button dialogButtonOk = (Button) dialog.findViewById(R.id.dialogButtonOK);
        Button dialogButtonCancelar = (Button) dialog.findViewById(R.id.dialogButtonCancelar);

        // if button is clicked, close the custom dialog
        dialogButtonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qtdConcursosButton.setText(textoBotaoSurpresinhasSelecionado);
                cartelaFragment.atualizaValorTotalAposta(qtdDezenasPossiveisSelecionado, qtdConcursoSelecionado);
                if (surpresinhaFragment.parametroJogo != null) {
                    surpresinhaFragment.atualizaValorTotalAposta(qtdDezenasPossiveisSelecionado, qtdConcursoSelecionado);
                }
                dialog.dismiss();
            }
        });

        dialogButtonCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    // Pegar o handle dos botoes que mostrarao o seu respectivo fragment na activity
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.simularTelaAposta: {
                cartelaFragment.setEscolhaTimeCoracaoSurpresinha(false);
                abrirTelaCartela();
                break;
            }

            case R.id.surpresinhaTela: {
                surpresinhaFragment.setArguments(new Bundle());
                abrirTelaSurprezinha();
                break;
            }

            case R.id.qtdNumerosButton: {
                modalNumeros();
                break;
            }

            case R.id.qtdConcursosButton: {
                modalTeimosinhas();
                break;
            }
        }
    }

    private void abrirTelaCartela() {
        fragmentManager.beginTransaction().replace(R.id.fragmentSimularApostas, cartelaFragment).commit();

        simularTelaAposta.setBackgroundResource(R.drawable.icon_seleciona);
        surpresinhaTela.setBackgroundResource(R.drawable.icon_surpresinha);
    }

    private void configuraTipoApostaTela(String titleActivity, int lightColor, int darkColor, int visibilityToolBarSimularAposta, int visibilityToolBarLoteca) {
        setTitle(ViewUtils.textFuturaAndFuturaBold(this, titleActivity));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, darkColor));
        }
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, lightColor)));

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setElevation(0);
        }

        toolBarSimularAposta.setBackgroundColor(ContextCompat.getColor(this, darkColor));
        toolBarSimularAposta.setVisibility(visibilityToolBarSimularAposta);
        toolBarLoteca.setVisibility(visibilityToolBarLoteca);

        typeGameColorLight = lightColor;
        typeGameColorDark = darkColor;
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();

        return true;
    }

    // Mudando o texto dos MenuItem de acordo com os dados
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        configuraMenuItem(R.id.concursoAtual);
        configuraMenuItem(R.id.sorteioAtual);

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(toolBarMenu, menu);

        MenuItem concursoAtual = menu.findItem(R.id.concursoAtual);
        ((TextView) concursoAtual.getActionView().findViewById(R.id.item_titulo)).setText("Concurso");
        ((TextView) concursoAtual.getActionView().findViewById(R.id.item_descricao)).setText(parametroSimulacao.getConcurso().getNumero().toString());

        SimpleDateFormat formatterDateFinal = new SimpleDateFormat("dd/MM");
        DateFormat formatterDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        MenuItem dataSorteio = menu.findItem(R.id.sorteioAtual);

        try {
            Date dataFormatada = formatterDate.parse(parametroSimulacao.getConcurso().getDataHoraSorteio());
            ((TextView) dataSorteio.getActionView().findViewById(R.id.item_titulo)).setText("Sorteio");
            ((TextView) dataSorteio.getActionView().findViewById(R.id.item_descricao)).setText(formatterDateFinal.format(dataFormatada));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_termos) {
            abrirTermos();
            return true;
        } else if (id == R.id.action_duvida) {
            abrirAjuda();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void configuraMenuItem(int id) {
        View menuItem = findViewById(id);

        if (menuItem != null && menuItem instanceof TextView) {
            ((TextView) menuItem).setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
            menuItem.setPadding(5, 5, 5, 5);
        }
    }

    // MÉTODOS DO FRAGMENT CARTELAFRAGMENTLISTENER
    @Override
    public int verificaNovoQtdDezenasMax(int qtdDezenasAtual) {
        if (qtdDezenasAtual <= parametroSimulacao.getQuantidadeMaxima()) {
            atualizaTextoBotoesQtdSelecionadas();
        }

        return qtdDezenasPossiveisSelecionado;
    }

    @Override
    public void adicionarApostaCarrinho() {
        Intent activity = new Intent(this, ConfirmadoInclusaoCarrinhoActivity.class);
        activity.putExtra("tipoAposta", tipoJogo);
        activity.putExtra("valorAposta", cartelaFragment.valorTotalAposta);
        activity.putExtra("modalidade", (new Gson()).toJson(parametroSimulacao));
        startActivityForResult(activity, 0);
    }

    @Override
    public void abreCarrinhoApostas() {
        Intent activity = new Intent(this, CarrinhoActivity.class);

        startActivity(activity);
    }

    @Override
    public void adicionarTimeSurpresa(ParametroEquipe equipeSelecionada) {
        Bundle bundle = new Bundle();
        bundle.putString("equipeSelecionada", (new Gson()).toJson(equipeSelecionada));
        surpresinhaFragment.setArguments(bundle);
        abrirTelaSurprezinha();
    }

    private void abrirTelaSurprezinha() {
        fragmentManager.beginTransaction().replace(R.id.fragmentSimularApostas, surpresinhaFragment).commit();

        simularTelaAposta.setBackgroundResource(R.drawable.icon_seleciona2);
        surpresinhaTela.setBackgroundResource(R.drawable.icon_surpresinha2);
    }

    @Override
    public void escolherOutroTimeSurpresinha() {
        cartelaFragment.setEscolhaTimeCoracaoSurpresinha(true);
        abrirTelaCartela();
    }

    private void abrirTermos() {
        Intent activity = new Intent(SimularApostaActivity.this, TermosUsoActivity.class);
        startActivity(activity);
    }

    private void abrirAjuda() {
        Intent activity = new Intent(SimularApostaActivity.this, AjudaActivity.class);
        activity.putExtra("tipoAposta", tipoJogo);
        startActivity(activity);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_FINISH) {
            finish();
        }
    }

    public void setLabelValueSimplesLoteca(String labelValueSimplesLoteca) {
        this.labelValueSimplesLoteca.setText(labelValueSimplesLoteca);
    }

    public void setLabelValueDuplasLoteca(String labelValueDuplasLoteca) {
        this.labelValueDuplasLoteca.setText(labelValueDuplasLoteca);
    }

    public void setLabelValueTriplasLoteca(String labelValueTriplasLoteca) {
        this.labelValueTriplasLoteca.setText(labelValueTriplasLoteca);
    }

    public TextView getLabelValueSimplesLoteca() {
        return labelValueSimplesLoteca;
    }

    public TextView getLabelValueDuplasLoteca() {
        return labelValueDuplasLoteca;
    }

    public TextView getLabelValueTriplasLoteca() {
        return labelValueTriplasLoteca;
    }

    public Button getBotaoLimparAposta() {
        return botaoLimparAposta;
    }

    public Button getBotaoAdicionarCompletarCartela() {
        return botaoAdicionarCompletarCartela;
    }

    public TextView getTextoSalveEstaAposta() {
        return textoSalveEstaAposta;
    }

    public TextView getValorApostaCartela() {
        return valorApostaCartela;
    }


    public TextView getTextViewCarrinhoCartelaCount() {
        return textViewCarrinhoCartelaCount;
    }

    public RelativeLayout getSalvarApostaLayout() {
        return salvarApostaLayout;
    }

    public EditText getEditNomeAposta() {
        return editNomeAposta;
    }


    public ImageView getIconStarSalvar() {
        return iconStarSalvar;
    }

    public RelativeLayout getSimularApostaLayoutCarrinhoApostas() {
        return simularApostaLayoutCarrinhoApostas;
    }
}