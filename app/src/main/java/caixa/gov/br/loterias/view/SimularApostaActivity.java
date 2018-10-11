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
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
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

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.model.bo.DadosCorporativosBO;
import caixa.gov.br.loterias.model.bo.DadosCorporativosBO_;
import caixa.gov.br.loterias.model.bo.RedirectNetwork;
import caixa.gov.br.loterias.model.bo.RequestListener;
import caixa.gov.br.loterias.model.bo.silce.dto.ApostaFavoritaDTO;
import caixa.gov.br.loterias.model.bo.silce.dto.DTOEnumInteger;
import caixa.gov.br.loterias.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import caixa.gov.br.loterias.model.bo.silce.dto.ModalidadeEnum;
import caixa.gov.br.loterias.model.bo.silce.dto.ParametroEquipe;
import caixa.gov.br.loterias.model.bo.silce.dto.ParametroJogoDTO;
import caixa.gov.br.loterias.model.bo.silce.dto.ParametroMesDeSorte;
import caixa.gov.br.loterias.model.bo.silce.dto.ParametroValorApostaDTO;
import caixa.gov.br.loterias.utils.Constantes;
import caixa.gov.br.loterias.utils.DialogUtils;
import caixa.gov.br.loterias.utils.ViewUtils;
import caixa.gov.br.loterias.view.fragment.CartelaFragment;
import caixa.gov.br.loterias.view.fragment.MesesFragment_;
import caixa.gov.br.loterias.view.fragment.SurpresinhaFragment;
import caixa.gov.br.loterias.view.fragment.SurpresinhaFragment.SurpresinhaFragmentListener;
import caixa.gov.br.loterias.view.fragment.SurpresinhaFragment_;
import caixa.gov.br.loterias.view.listener.CartelaFragmentListener;

import static java.lang.String.format;

@EActivity(R.layout.activity_simular_aposta)
public class SimularApostaActivity extends AppCompatActivity implements OnClickListener, CartelaFragmentListener, SurpresinhaFragmentListener {

    private static int RESULT_FINISH = 3;

    @ViewById
    protected DrawerLayout drawerLayout;

    @ViewById
    protected ConstraintLayout toolBarLoteca;

    @ViewById
    protected Button simularTelaAposta, surpresinhaTela, qtdNumerosButton, qtdConcursosButton, botaoLimparAposta, botaoAdicionarCompletarCartela, botaoCarrinho;

    @ViewById
    protected FrameLayout fragmentSimularApostas;

    @ViewById
    protected TextView labelValueSimplesLoteca, labelValueDuplasLoteca, labelValueTriplasLoteca, textoDeNomeInfo, textoSalveEstaAposta, valorApostaCartela, textInfoValorAposta, textViewCarrinhoCartelaCount,
            textInfoCarrinho;

    @ViewById
    protected LinearLayout toolBarSimularAposta;

    @ViewById
    protected RelativeLayout salvarApostaLayout, carrinhoApostas, simularApostaLayoutCarrinhoApostas, viewTabBar;

    @ViewById
    protected ImageButton btnSalvarAposta;

    @ViewById
    protected EditText editNomeAposta;

    @ViewById
    protected ImageView imageViewPaginacao, iconStarSalvar;

    @Extra
    protected IdentificaoDeUmaApostaDas8Modalidades aposta;

    // Fragments
    private FragmentManager fragmentManager;
    private CartelaFragment cartelaFragment;
    private SurpresinhaFragment surpresinhaFragment;

    private String dataSorteioAtual;
    private int typeGameColorLight;
    private int typeGameColorDark;
    private List<String> qtdNumerosList;
    private List<String> qtdConcursosList;
    private ModalidadeEnum tipoJogo;
    private String textoBotaoPrognosticosSelecionado;
    private String textoBotaoSurpresinhasSelecionado;
    private ParametroJogoDTO parametroSimulacao;
    private int toolBarMenu;
    private Bundle bundle;
    private ParametroMesDeSorte mesDeSorteCartela;
    private ParametroMesDeSorte mesDeSorteSurpresinha;

    //mocks
    public int qtdDezenasPossiveisSelecionado;
    public int qtdConcursoSelecionado;
    private Boolean flagApostaSalva = false;
    private int numeroConcursoAtual;

    @AfterViews
    protected void init() {
        invalidateOptionsMenu();
        zerarApostas();
        if(aposta != null){
            mesDeSorteCartela = aposta.getMesDeSorte();
        }
    }

    public void zerarApostas() {
        qtdNumerosList = new ArrayList<>();
        qtdConcursosList = new ArrayList<>();
        textoBotaoSurpresinhasSelecionado = "nenhum";
        mesDeSorteCartela = null;
        mesDeSorteSurpresinha = null;
        parametroSimulacao = new Gson().fromJson(getIntent().getStringExtra("modalidade"), ParametroJogoDTO.class);
        configuraParametrosJogo();
        configuraFragments();
        configuraActivity();
        zerarApostasLoteca();

    }

    public void zerarApostasLoteca() {
        this.setLabelValueSimplesLoteca(Constantes.ZERO_STRING);
        this.setLabelValueDuplasLoteca(Constantes.ZERO_STRING);
        this.setLabelValueTriplasLoteca(Constantes.ZERO_STRING);
    }

    public void atualizaTextoBotoesQtdSelecionadas() {
        qtdNumerosButton.setText(format("%d números", qtdDezenasPossiveisSelecionado));
        textoBotaoPrognosticosSelecionado = qtdNumerosButton.getText().toString();
        if ((qtdConcursosList != null && !qtdConcursosList.isEmpty()) && (qtdConcursosList.size() == 1 || qtdConcursoSelecionado == 0)) {
            qtdConcursosButton.setText(qtdConcursosList.get(0));
        } else {
            qtdConcursosButton.setText(format("%d concurso", qtdConcursoSelecionado));
        }
    }


    // Pegar o handle dos botoes que mostrarao o seu respectivo fragment na activity
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.simularTelaAposta: {
                cartelaFragment.setEscolhaTimeCoracaoSurpresinha(false);
                abrirTelaCartela(false);
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

    public ParametroJogoDTO getParametroSimulacao() {
        return parametroSimulacao;
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

        if (isFragmentCartela()) {

            activity.putExtra("valorAposta", cartelaFragment.valorTotalAposta);
        } else {

            activity.putExtra("valorAposta", surpresinhaFragment.getValorTotalAposta());
        }
        activity.putExtra("modalidade", (new Gson()).toJson(parametroSimulacao));
        this.init();
        startActivityForResult(activity, 0);
    }

    @Override
    public void abreCarrinhoApostas() {
        CarrinhoActivity_.intent(this).start();
    }

    @Override
    public void adicionarTimeSurpresa(ParametroEquipe equipeSelecionada) {
        Bundle bundle = new Bundle();
        bundle.putString("equipeSelecionada", (new Gson()).toJson(equipeSelecionada));
        surpresinhaFragment.setArguments(bundle);
        abrirTelaSurprezinha();
    }

    public void adicionarDiaDeSorteCarrinho() {
        abrirTelaCartela(false);
    }

    @Override
    public void escolherOutroTimeSurpresinha() {
        cartelaFragment.setEscolhaTimeCoracaoSurpresinha(true);
        abrirTelaCartela(true);
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

    public FrameLayout getFragmentSimularApostas() {
        return fragmentSimularApostas;
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

    public IdentificaoDeUmaApostaDas8Modalidades getAposta() {
        return aposta;
    }

    public Boolean getFlagApostaSalva() {
        return flagApostaSalva;
    }

    public void salvarAposta(final ApostaFavoritaDTO aposta, String nomeAposta, List<Integer> dezenasSelecionadas) {
        final DadosCorporativosBO dadosCorporativosBO = DadosCorporativosBO_.getInstance_(this);
        DialogUtils.show(this);

        DTOEnumInteger modalidade = new DTOEnumInteger();
        modalidade.setDescricao(parametroSimulacao.getConcurso().getModalidadeDetalhada().getDescricao());
        modalidade.setValor(parametroSimulacao.getConcurso().getModalidadeDetalhada().getValor());
        aposta.setModalidade(modalidade);

        aposta.setNome(nomeAposta);
        aposta.setNumerosSelecionados(dezenasSelecionadas);
        aposta.setId(Constantes.ZERO_LONG);

        dadosCorporativosBO.validarApostaFavorita(aposta, new RequestListener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                dadosCorporativosBO.salvarApostaFavorita(aposta, new RequestListener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        DialogUtils.dismiss();

                        RelativeLayout salvarApostaLayout = getSalvarApostaLayout();

                        salvarApostaLayout.setBackgroundResource(R.color.verdeazul);
                        ViewGroup.LayoutParams relativelayoutParams = salvarApostaLayout.getLayoutParams();

                        final float scale = getResources().getDisplayMetrics().density;
                        relativelayoutParams.height = (int) (64 * scale + 0.5f);

                        salvarApostaLayout.setLayoutParams(relativelayoutParams);

                        TextView tituloSalvarAposta = getTextoSalveEstaAposta();
                        tituloSalvarAposta.setText(R.string.label_aposta_salva_carinha);
                        tituloSalvarAposta.setGravity(Gravity.CENTER);

                        ImageView starSalvarApostaImageView = getIconStarSalvar();
                        starSalvarApostaImageView.setBackgroundResource(R.drawable.icon_favoritado);

                        flagApostaSalva = true;
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        DialogUtils.dismiss();
                        RedirectNetwork.checkRedirect(error, SimularApostaActivity.this);

                    }
                });
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                DialogUtils.dismiss();
                RedirectNetwork.checkRedirect(error, SimularApostaActivity.this);
            }
        });
    }

    public void mesDeSorteFragment(List<Integer> dezenasSelecionadas, boolean surpresinha) {
        fragmentManager.beginTransaction().replace(R.id.fragmentSimularApostas, MesesFragment_.builder().modalidadeEnum(tipoJogo).
                parametroJogoDTO(parametroSimulacao).dezenasSelecionadas(dezenasSelecionadas).build()).commit();

        if (surpresinha) {
            simularTelaAposta.setBackgroundResource(R.drawable.icon_seleciona2);
            surpresinhaTela.setBackgroundResource(R.drawable.icon_surpresinha2);
        }
    }

    public ParametroMesDeSorte getMesDeSorteCartela() {
        return mesDeSorteCartela;
    }

    public void setMesDeSorteCartela(ParametroMesDeSorte mesDeSorteCartela) {
        this.mesDeSorteCartela = mesDeSorteCartela;
    }

    public ParametroMesDeSorte getMesDeSorteSurpresinha() {
        return mesDeSorteSurpresinha;
    }

    public void setMesDeSorteSurpresinha(ParametroMesDeSorte mesDeSorteSurpresinha) {
        this.mesDeSorteSurpresinha = mesDeSorteSurpresinha;
    }

    @Click
    protected void simularApostaLayoutCarrinhoApostas() {
        abreCarrinhoApostas();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_FINISH) {
            finish();
        }
    }

    private void abrirTelaSurprezinha() {
        fragmentManager.beginTransaction().replace(R.id.fragmentSimularApostas, surpresinhaFragment).commit();

        simularTelaAposta.setBackgroundResource(R.drawable.icon_seleciona2);
        surpresinhaTela.setBackgroundResource(R.drawable.icon_surpresinha2);
    }

    private void configuraParametrosJogo() {
        if (parametroSimulacao != null) {
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
        surpresinhaFragment = SurpresinhaFragment_.builder().build();

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragmentSimularApostas, cartelaFragment).commit();
    }

    private void configuraActivity() {

        tipoJogo = (ModalidadeEnum) getIntent().getSerializableExtra("tipoAposta");

        switch (tipoJogo) {
            case MEGA_SENA:
                configuraTipoApostaTela("_mega-sena_", R.color.megasenaclaro, R.color.megasenaescuro, View.VISIBLE, View.GONE);
                this.toolBarMenu = R.menu.itens_simular_actionbar_duvida;
                break;
            case QUINA:
                configuraTipoApostaTela("_quina_", R.color.quinaclaro, R.color.quinaescuro, View.VISIBLE, View.GONE);
                this.toolBarMenu = R.menu.itens_simular_actionbar_duvida;
                break;
            case LOTOFACIL:
                configuraTipoApostaTela("_lotofácil_", R.color.lotofacilclaro, R.color.lotofacilescuro, View.VISIBLE, View.GONE);
                this.toolBarMenu = R.menu.itens_simular_actionbar_duvida;
                break;
            case DUPLA_SENA:
                configuraTipoApostaTela("_dupla sena_", R.color.duplasenaclaro, R.color.duplasenaescuro, View.VISIBLE, View.GONE);
                this.toolBarMenu = R.menu.itens_simular_actionbar_duvida;
                break;
            case TIMEMANIA:
                configuraTipoApostaTela("_timemania_", R.color.timemaniaclaro, R.color.timemaniaescuro, View.VISIBLE, View.GONE);
                this.toolBarMenu = R.menu.itens_simular_actionbar_duvida;
                break;
            case LOTOMANIA:
                configuraTipoApostaTela("_lotomania_", R.color.lotomaniaclaro, R.color.lotomaniaescuro, View.VISIBLE, View.GONE);
                this.toolBarMenu = R.menu.itens_simular_actionbar_duvida;
                break;
            case LOTECA:
                configuraTipoApostaTela("_loteca_", R.color.lotecaclaro, R.color.lotecaescuro, View.GONE, View.VISIBLE);
                this.toolBarMenu = R.menu.itens_simular_actionbar_duvida;
                break;
            case LOTOGOL:
                configuraTipoApostaTela("_lotogol_", R.color.lotogolclaro, R.color.lotogolescuro, View.GONE, View.GONE);
                this.toolBarMenu = R.menu.itens_simular_actionbar_duvida;
                break;
            case DIA_DE_SORTE:
                configuraTipoApostaTela("_dia-sorte_", R.color.diasorteclaro, R.color.diasorteescuro, View.VISIBLE, View.GONE);
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

    private void configuraBotoesListeners() {
        simularTelaAposta.setOnClickListener(this);
        surpresinhaTela.setOnClickListener(this);
        qtdNumerosButton.setOnClickListener(this);
        qtdConcursosButton.setOnClickListener(this);

        simularTelaAposta.setBackgroundResource(R.drawable.icon_seleciona);
        surpresinhaTela.setBackgroundResource(R.drawable.icon_surpresinha);
    }

    private void modalNumeros() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_dialog_list_view);

        final ListView listViewItens = (ListView) dialog.findViewById(R.id.listaItensDialog);
        TextView tituloDialog = (TextView) dialog.findViewById(R.id.tituloDialogCustom);
        tituloDialog.setText("Quant. de Números");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(SimularApostaActivity.this,
                R.layout.item_text_popup, R.id.textList, qtdNumerosList);

        listViewItens.setAdapter(adapter);
        listViewItens.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String itemValue = (String) listViewItens.getItemAtPosition(position);
                textoBotaoPrognosticosSelecionado = parametroSimulacao.getValoresAposta().get(position).getNumeroPrognosticos() + " números";
                qtdDezenasPossiveisSelecionado = parametroSimulacao.getValoresAposta().get(position).getNumeroPrognosticos();
            }

        });

        Button dialogButtonOk = dialog.findViewById(R.id.dialogButtonOK);
        Button dialogButtonCancelar = dialog.findViewById(R.id.dialogButtonCancelar);

        dialogButtonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qtdNumerosButton.setText(textoBotaoPrognosticosSelecionado);

                if (isFragmentCartela()) {
                    cartelaFragment.atualizaValorTotalAposta(qtdDezenasPossiveisSelecionado, qtdConcursoSelecionado);
                    cartelaFragment.atualizaStatusBotaoFinalizar();
                } else {
                    if (surpresinhaFragment.getParametroJogo() != null) {
                        surpresinhaFragment.atualizaValorTotalAposta(qtdDezenasPossiveisSelecionado, qtdConcursoSelecionado);
                    }
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

    public boolean isFragmentCartela(){
        return fragmentManager.findFragmentById(R.id.fragmentSimularApostas) instanceof CartelaFragment;
    }

    private void modalTeimosinhas() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_dialog_list_view);

        final ListView listViewItens = dialog.findViewById(R.id.listaItensDialog);
        TextView tituloDialog = dialog.findViewById(R.id.tituloDialogCustom);
        tituloDialog.setText("Teimosinhas");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(SimularApostaActivity.this,
                R.layout.item_text_popup, R.id.textList, qtdConcursosList);

        listViewItens.setAdapter(adapter);
        listViewItens.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                textoBotaoSurpresinhasSelecionado = (String) listViewItens.getItemAtPosition(position);
                ;
                qtdConcursoSelecionado = parametroSimulacao.getTeimosinhas().get(position);
            }

        });

        Button dialogButtonOk = dialog.findViewById(R.id.dialogButtonOK);
        Button dialogButtonCancelar = dialog.findViewById(R.id.dialogButtonCancelar);

        dialogButtonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qtdConcursosButton.setText(textoBotaoSurpresinhasSelecionado);

                if (fragmentManager.findFragmentById(R.id.fragmentSimularApostas) instanceof CartelaFragment) {

                    cartelaFragment.atualizaValorTotalAposta(qtdDezenasPossiveisSelecionado, qtdConcursoSelecionado);
                } else {

                    if (surpresinhaFragment.getParametroJogo() != null) {
                        surpresinhaFragment.atualizaValorTotalAposta(qtdDezenasPossiveisSelecionado, qtdConcursoSelecionado);
                    }
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

    private void abrirTelaCartela(boolean surpresinha) {
        fragmentManager.beginTransaction().replace(R.id.fragmentSimularApostas, cartelaFragment).commit();

        if(surpresinha){
            simularTelaAposta.setBackgroundResource(R.drawable.icon_seleciona2);
            surpresinhaTela.setBackgroundResource(R.drawable.icon_surpresinha2);
        }else{
            simularTelaAposta.setBackgroundResource(R.drawable.icon_seleciona);
            surpresinhaTela.setBackgroundResource(R.drawable.icon_surpresinha);
        }
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

    private void configuraMenuItem(int id) {
        View menuItem = findViewById(id);

        if (menuItem != null && menuItem instanceof TextView) {
            ((TextView) menuItem).setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
            menuItem.setPadding(5, 5, 5, 5);
        }
    }

    private void abrirTermos() {
        Intent activity = new Intent(SimularApostaActivity.this, TermosUsoActivity.class);
        startActivity(activity);
    }

    private void abrirAjuda() {
        Intent activity = new Intent(SimularApostaActivity.this, TermosUsoActivity.class);
        activity.putExtra("ajuda", true);
        startActivity(activity);
    }
}