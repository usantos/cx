package br.gov.caixa.loterias.apostar.interno.controllers;

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
import org.parceler.Parcels;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.gov.caixa.loterias.apostar.interno.R;
import br.gov.caixa.loterias.apostar.interno.model.bo.ApostaBO;
import br.gov.caixa.loterias.apostar.interno.model.bo.ApostaBO_;
import br.gov.caixa.loterias.apostar.interno.model.bo.DadosCorporativosBO;
import br.gov.caixa.loterias.apostar.interno.model.bo.DadosCorporativosBO_;
import br.gov.caixa.loterias.apostar.interno.model.bo.DadosUsuarioBO;
import br.gov.caixa.loterias.apostar.interno.model.bo.MensagensNetwork;
import br.gov.caixa.loterias.apostar.interno.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostar.interno.model.bo.RequestListener;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ApostaFavoritaDTO;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.CarrinhoDTOResponse;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.DTOEnumInteger;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ParametroEquipe;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ParametroJogoDTO;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ParametroMesDeSorte;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ParametroValorApostaDTO;
import br.gov.caixa.loterias.apostar.interno.utils.Constantes;
import br.gov.caixa.loterias.apostar.interno.utils.DialogUtils;
import br.gov.caixa.loterias.apostar.interno.utils.FirebaseManager;
import br.gov.caixa.loterias.apostar.interno.utils.ViewUtils;
import br.gov.caixa.loterias.apostar.interno.view.fragment.CartelaFragment;
import br.gov.caixa.loterias.apostar.interno.view.fragment.MesesFragment_;
import br.gov.caixa.loterias.apostar.interno.view.fragment.SurpresinhaFragment;
import br.gov.caixa.loterias.apostar.interno.view.fragment.SurpresinhaFragment.SurpresinhaFragmentListener;
import br.gov.caixa.loterias.apostar.interno.view.fragment.SurpresinhaFragment_;
import br.gov.caixa.loterias.apostar.interno.view.listener.CartelaFragmentListener;

import static br.gov.caixa.loterias.apostar.interno.controllers.CarrinhoActivity.CARRINHO;
import static br.gov.caixa.loterias.apostar.interno.controllers.CarrinhoActivity.LER_CARRINHO_LOCAL;
import static br.gov.caixa.loterias.apostar.interno.utils.Utils.isErroNegocial;
import static java.lang.String.format;

@EActivity(R.layout.activity_simular_aposta)
public class SimularApostaActivity<T> extends AppCompatActivity implements OnClickListener, CartelaFragmentListener, SurpresinhaFragmentListener {

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
    private final DadosCorporativosBO dadosCorporativosBO = DadosCorporativosBO_.getInstance_(this);

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
        if (tipoJogo == ModalidadeEnum.SUPER_7 && !DadosUsuarioBO.checarTutorialSuperSete()) {
            startActivity(new Intent(this,TutorialSuperSeteActivity.class));
        }
    }

    public void zerarApostas() {
        qtdNumerosList = new ArrayList<>();
        qtdConcursosList = new ArrayList<>();
        textoBotaoSurpresinhasSelecionado = getString(R.string.label_sem_teimosinha);
        mesDeSorteCartela = null;
        mesDeSorteSurpresinha = null;
        parametroSimulacao = new Gson().fromJson(getIntent().getStringExtra(getResources().getString(R.string.extra_modalidade)), ParametroJogoDTO.class);
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
        qtdNumerosButton.setText(format(getResources().getString(R.string.percent_d_numeros), qtdDezenasPossiveisSelecionado));
        textoBotaoPrognosticosSelecionado = qtdNumerosButton.getText().toString();
        if ((qtdConcursosList != null && !qtdConcursosList.isEmpty()) && (qtdConcursosList.size() == 1 || qtdConcursoSelecionado == 0)) {
            qtdConcursosButton.setText(qtdConcursosList.get(0));
        } else {
            qtdConcursosButton.setText(format(getResources().getString(R.string.percent_d_concurso), qtdConcursoSelecionado));
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
        ((TextView) concursoAtual.getActionView().findViewById(R.id.item_titulo)).setText(getResources().getString(R.string.concurso));
        ((TextView) concursoAtual.getActionView().findViewById(R.id.item_descricao)).setText(parametroSimulacao.getConcurso().getNumero().toString());

        SimpleDateFormat formatterDateFinal = new SimpleDateFormat(getResources().getString(R.string.dd_mm));
        DateFormat formatterDate = new SimpleDateFormat(getResources().getString(R.string.dd_mm_yyyy_hh_mm_ss));
        MenuItem dataSorteio = menu.findItem(R.id.sorteioAtual);

        try {
            Date dataFormatada = formatterDate.parse(parametroSimulacao.getConcurso().getDataHoraSorteio());
            ((TextView) dataSorteio.getActionView().findViewById(R.id.item_titulo)).setText(getResources().getString(R.string.sorteio));
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
        activity.putExtra(getResources().getString(R.string.extra_tipo_aposta), tipoJogo);

        if (isFragmentCartela()) {
            activity.putExtra(getResources().getString(R.string.extra_valor_aposta), cartelaFragment.valorTotalAposta);
        } else {
            activity.putExtra(getResources().getString(R.string.extra_valor_aposta), surpresinhaFragment.getValorTotalAposta());
        }
        activity.putExtra(getResources().getString(R.string.extra_modalidade), (new Gson()).toJson(parametroSimulacao));
        this.init();
        startActivityForResult(activity, 0);
    }

    @Override
    public void abreCarrinhoApostas() {
        DialogUtils.show(this);
        ApostaBO apostaBO = ApostaBO_.getInstance_(this);

        apostaBO.buscaCarrinho(new RequestListener<CarrinhoDTOResponse>() {
            @Override
            public void onResponse(CarrinhoDTOResponse response) {
                DialogUtils.dismiss();
                Bundle args = new Bundle();
                args.putParcelable(CARRINHO, Parcels.wrap(response.getPayload()));
                startActivity(new Intent(SimularApostaActivity.this, CarrinhoActivity.class).putExtras(args));
                if (response.getRedirect() != null){
                    RedirectNetwork.checkRedirectSucesso( response.getRedirect(), SimularApostaActivity.this);
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                DialogUtils.dismiss();
                if (MensagensNetwork.isUnauthorizedError(error)) {
                    startActivity(new Intent(SimularApostaActivity.this, CarrinhoActivity.class).putExtra(LER_CARRINHO_LOCAL, true));
                } else {
                    RedirectNetwork.checkRedirect( error, SimularApostaActivity.this );
                }
            }
        });
    }

    @Override
    public void adicionarTimeSurpresa(ParametroEquipe equipeSelecionada) {
        Bundle bundle = new Bundle();
        bundle.putString(getResources().getString(R.string.extra_equipe_selecionada), (new Gson()).toJson(equipeSelecionada));
        surpresinhaFragment.setArguments(bundle);
        abrirTelaSurprezinha();
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

    public void salvarAposta(final ApostaFavoritaDTO apostaFavorita, String nomeAposta, T dezenasSelecionadas) {

        DialogUtils.show(this);

        DTOEnumInteger modalidade = new DTOEnumInteger();
        modalidade.setDescricao(parametroSimulacao.getConcurso().getModalidadeDetalhada().getDescricao());
        modalidade.setValor(parametroSimulacao.getConcurso().getModalidadeDetalhada().getValor());
        apostaFavorita.setModalidade(modalidade);

        apostaFavorita.setNome(nomeAposta);
        apostaFavorita.setNumerosSelecionados(dezenasSelecionadas);
        apostaFavorita.setId(Constantes.ZERO_LONG);

        dadosCorporativosBO.validarApostaFavorita(apostaFavorita, new RequestListener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                salvarAposta(apostaFavorita);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                DialogUtils.dismiss();
                DialogUtils.dismiss();
                if(isErroNegocial(error)){
                    ViewUtils.alertTitleButtonYesOrNotListener(
                            SimularApostaActivity.this,
                            MensagensNetwork.getErrorMessage(error),
                            (dialogInterface, i) ->
                                    salvarAposta(apostaFavorita)

                                                              );
                } else {
                    RedirectNetwork.checkRedirect(error, SimularApostaActivity.this);
                }

            }
        });
    }
    private void salvarAposta(ApostaFavoritaDTO aposta){
        if(!DialogUtils.isShow()){
            DialogUtils.show(this);
        }
        dadosCorporativosBO.salvarApostaFavorita(aposta, new RequestListener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                FirebaseManager.registraEvento(SimularApostaActivity.this ,getResources().getString(R.string.evento_efetuou_adicao_aposta_favorita_sucesso));

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
                qtdNumerosList.add(getResources().getString(R.string.string_vazia) + parametro.getNumeroPrognosticos() + getResources().getString(R.string.espaco_numeros_por_espaco) + ViewUtils.getMoedaFormat(parametro.getValor()));
            }
            if (parametroSimulacao.getTeimosinhas() != null) {
                for (Integer numeroTeimosinha : parametroSimulacao.getTeimosinhas()) {
                    if (numeroTeimosinha == 0) {
                        qtdConcursosList.add(getString(R.string.label_sem_teimosinha));
                    } else if (numeroTeimosinha == 1) {
                        qtdConcursosList.add("" + numeroTeimosinha + getResources().getString(R.string.espaco_concurso));
                    } else {
                        qtdConcursosList.add("" + numeroTeimosinha + getResources().getString(R.string.espaco_concursos));
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

        tipoJogo = (ModalidadeEnum) getIntent().getSerializableExtra(getResources().getString(R.string.tipoAposta));

        switch (tipoJogo) {
            case MEGA_SENA:
                configuraTipoApostaTela(getResources().getString(R.string.unterline_mega_sena_underline), R.color.megasenaclaro, R.color.megasenaescuro, View.VISIBLE, View.GONE);
                this.toolBarMenu = R.menu.itens_simular_actionbar_duvida;
                break;
            case QUINA:
                configuraTipoApostaTela(getResources().getString(R.string.underline_quina_underline), R.color.quinaclaro, R.color.quinaescuro, View.VISIBLE, View.GONE);
                this.toolBarMenu = R.menu.itens_simular_actionbar_duvida;
                break;
            case LOTOFACIL:
                configuraTipoApostaTela(getResources().getString(R.string.underline_lotofacil_underline), R.color.lotofacilclaro, R.color.lotofacilescuro, View.VISIBLE, View.GONE);
                this.toolBarMenu = R.menu.itens_simular_actionbar_duvida;
                break;
            case DUPLA_SENA:
                configuraTipoApostaTela(getResources().getString(R.string.underline_dupla_sena_underline), R.color.duplasenaclaro, R.color.duplasenaescuro, View.VISIBLE, View.GONE);
                this.toolBarMenu = R.menu.itens_simular_actionbar_duvida;
                break;
            case TIMEMANIA:
                configuraTipoApostaTela(getResources().getString(R.string.underline_timemania_underline), R.color.timemaniaclaro, R.color.timemaniaescuro, View.VISIBLE, View.GONE);
                this.toolBarMenu = R.menu.itens_simular_actionbar_duvida;
                break;
            case LOTOMANIA:
                configuraTipoApostaTela(getResources().getString(R.string.underline_lotomania_underline), R.color.lotomaniaclaro, R.color.lotomaniaescuro, View.VISIBLE, View.GONE);
                this.toolBarMenu = R.menu.itens_simular_actionbar_duvida;
                break;
            case LOTECA:
                configuraTipoApostaTela(getResources().getString(R.string.underline_loteca_underline), R.color.lotecaclaro, R.color.lotecaescuro, View.GONE, View.VISIBLE);
                this.toolBarMenu = R.menu.itens_simular_actionbar_duvida;
                break;
            case LOTOGOL:
                configuraTipoApostaTela(getResources().getString(R.string.underline_lotogol_underline), R.color.lotogolclaro, R.color.lotogolescuro, View.GONE, View.GONE);
                this.toolBarMenu = R.menu.itens_simular_actionbar_duvida;
                break;
            case DIA_DE_SORTE:
                configuraTipoApostaTela(getResources().getString(R.string.underline_dia_de_Sorte_underline), R.color.diasorteclaro, R.color.diasorteescuro, View.VISIBLE, View.GONE);
                this.toolBarMenu = R.menu.itens_simular_actionbar_duvida;
                break;
            case SUPER_7:
                configuraTipoApostaTela(getResources().getString(R.string.underline_super_sete_underline), R.color.superseteclaro, R.color.supersetemedio, View.VISIBLE, View.GONE);
                this.toolBarMenu = R.menu.itens_simular_actionbar_duvida;
                break;
        }

        qtdDezenasPossiveisSelecionado = parametroSimulacao.getQuantidadeMinima() != null ? parametroSimulacao.getQuantidadeMinima() : 0;
        qtdConcursoSelecionado = parametroSimulacao.getTeimosinhas() != null ? parametroSimulacao.getTeimosinhas().get(0) == 0 ? 0 : parametroSimulacao.getTeimosinhas().get(0) : 0;
        dataSorteioAtual = parametroSimulacao.getConcurso().getDataSorteio();


        // Passando um link
        bundle = new Bundle();
        bundle.putInt(getResources().getString(R.string.extra_qtd_dezenas_possiveis_selecionado), qtdDezenasPossiveisSelecionado);
        bundle.putInt(getResources().getString(R.string.extra_numero_concurso_atual), numeroConcursoAtual);
        bundle.putString(getResources().getString(R.string.extra_data_sorteio_atual), dataSorteioAtual);
        bundle.putSerializable(getResources().getString(R.string.extra_type_game_color_light), typeGameColorLight);
        bundle.putSerializable(getResources().getString(R.string.extra_type_game_color_Dark), typeGameColorDark);


        cartelaFragment.cartelaFragmentBuilder(bundle);
        surpresinhaFragment.surpresinhaFragmentBuilder(bundle);

        atualizaTextoBotoesQtdSelecionadas();
        simularTelaAposta.setBackgroundResource(R.drawable.icon_seleciona);
        surpresinhaTela.setBackgroundResource(R.drawable.icon_surpresinha);

        configuraBotoesListeners();
    }

    public void configuraBotoesListeners() {
        simularTelaAposta.setOnClickListener(this);
        surpresinhaTela.setOnClickListener(this);
        qtdNumerosButton.setOnClickListener(this);
        qtdConcursosButton.setOnClickListener(this);

        simularTelaAposta.setBackgroundResource(R.drawable.icon_seleciona);
        surpresinhaTela.setBackgroundResource(R.drawable.icon_surpresinha);
    }

    private void modalNumeros() {
        FirebaseManager.registraEvento(this ,getResources().getString(R.string.evento_entrou_selecao_prognosticos));

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_dialog_list_view);

        final ListView listViewItens = dialog.findViewById(R.id.listaItensDialog);
        TextView tituloDialog = dialog.findViewById(R.id.tituloDialogCustom);
        tituloDialog.setText(getResources().getString(R.string.quant_de_numeros));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(SimularApostaActivity.this,
                R.layout.item_text_popup, R.id.textList, qtdNumerosList);

        listViewItens.setAdapter(adapter);
        listViewItens.setOnItemClickListener((parent, view, position, id) -> {
            String itemValue = (String) listViewItens.getItemAtPosition(position);
            textoBotaoPrognosticosSelecionado = parametroSimulacao.getValoresAposta().get(position).getNumeroPrognosticos() + getResources().getString(R.string.espaco_numeros);
            qtdDezenasPossiveisSelecionado = parametroSimulacao.getValoresAposta().get(position).getNumeroPrognosticos();
        });

        Button dialogButtonOk = dialog.findViewById(R.id.dialogButtonOK);
        Button dialogButtonCancelar = dialog.findViewById(R.id.dialogButtonCancelar);

        dialogButtonOk.setOnClickListener(v -> {
            qtdNumerosButton.setText(textoBotaoPrognosticosSelecionado);

            if (isFragmentCartela()) {
                cartelaFragment.atualizaValorTotalAposta(qtdDezenasPossiveisSelecionado, qtdConcursoSelecionado);
                cartelaFragment.atualizaStatusBotaoFinalizar(true);
            } else {
                if (surpresinhaFragment.getParametroJogo() != null) {
                    surpresinhaFragment.atualizaValorTotalAposta(qtdDezenasPossiveisSelecionado, qtdConcursoSelecionado);
                }
            }
            dialog.dismiss();
        });

        dialogButtonCancelar.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    public boolean isFragmentCartela(){
        return fragmentManager.findFragmentById(R.id.fragmentSimularApostas) instanceof CartelaFragment;
    }



    private void modalTeimosinhas() {
        FirebaseManager.registraEvento(this ,getResources().getString(R.string.evento_entrou_popup_teimosinha));

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_dialog_list_view);

        final ListView listViewItens = dialog.findViewById(R.id.listaItensDialog);
        TextView tituloDialog = dialog.findViewById(R.id.tituloDialogCustom);
        tituloDialog.setText(getResources().getString(R.string.teimosinhas_maiusculo));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(SimularApostaActivity.this,
                R.layout.item_text_popup, R.id.textList, qtdConcursosList);

        listViewItens.setAdapter(adapter);
        listViewItens.setOnItemClickListener((parent, view, position, id) -> {
            textoBotaoSurpresinhasSelecionado = (String) listViewItens.getItemAtPosition(position);
            qtdConcursoSelecionado = parametroSimulacao.getTeimosinhas().get(position);
        });

        Button dialogButtonOk = dialog.findViewById(R.id.dialogButtonOK);
        Button dialogButtonCancelar = dialog.findViewById(R.id.dialogButtonCancelar);

        dialogButtonOk.setOnClickListener(v -> {
            qtdConcursosButton.setText(textoBotaoSurpresinhasSelecionado);

            if (fragmentManager.findFragmentById(R.id.fragmentSimularApostas) instanceof SurpresinhaFragment) {
                if (surpresinhaFragment.getParametroJogo() != null) {
                    surpresinhaFragment.atualizaValorTotalAposta(qtdDezenasPossiveisSelecionado, qtdConcursoSelecionado);
                }
            } else {
                cartelaFragment.atualizaValorTotalAposta(qtdDezenasPossiveisSelecionado, qtdConcursoSelecionado);
            }

            dialog.dismiss();
        });

        dialogButtonCancelar.setOnClickListener(v -> dialog.dismiss());

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

    public Button getQtdNumerosButton() {
        return qtdNumerosButton;
    }

    private void configuraTipoApostaTela(String titleActivity, int lightColor, int darkColor, int visibilityToolBarSimularAposta, int visibilityToolBarLoteca) {
        setTitle(ViewUtils.textFuturaAndFuturaBoldTitle(this, titleActivity));

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
        activity.putExtra(getResources().getString(R.string.extra_ajuda), true);
        startActivity(activity);
    }
}