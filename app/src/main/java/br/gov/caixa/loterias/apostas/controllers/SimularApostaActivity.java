package br.gov.caixa.loterias.apostas.controllers;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bean.Paginacao;
import br.gov.caixa.loterias.apostas.model.bo.ApostaBO;
import br.gov.caixa.loterias.apostas.model.bo.ApostaBO_;
import br.gov.caixa.loterias.apostas.model.bo.DadosCorporativosBO;
import br.gov.caixa.loterias.apostas.model.bo.DadosCorporativosBO_;
import br.gov.caixa.loterias.apostas.model.bo.DadosUsuarioBO;
import br.gov.caixa.loterias.apostas.model.bo.MensagensNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaFavoritaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DTOEnumInteger;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroEquipe;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroJogoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroMesDeSorte;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroValorApostaDTO;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.AppCenterManager;
import br.gov.caixa.loterias.apostas.utils.BuildVersionUtil;
import br.gov.caixa.loterias.apostas.utils.Constantes;
import br.gov.caixa.loterias.apostas.utils.DialogUtils;
import br.gov.caixa.loterias.apostas.utils.FragmentUtils;
import br.gov.caixa.loterias.apostas.utils.PaginacaoUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.fragment.CartelaFragment;
import br.gov.caixa.loterias.apostas.view.fragment.MesesFragment;
import br.gov.caixa.loterias.apostas.view.fragment.MesesFragment_;
import br.gov.caixa.loterias.apostas.view.fragment.PaginacaoFragment;
import br.gov.caixa.loterias.apostas.view.fragment.SomadorCarrinhoFragment;
import br.gov.caixa.loterias.apostas.view.fragment.SurpresinhaFragment;
import br.gov.caixa.loterias.apostas.view.fragment.SurpresinhaFragment_;
import br.gov.caixa.loterias.apostas.view.fragment.TrevosFragment;
import br.gov.caixa.loterias.apostas.view.fragment.TrevosFragment_;
import br.gov.caixa.loterias.apostas.view.listener.CartelaFragmentListener;
import br.gov.caixa.loterias.apostas.view.listener.OnDialogListener;
import br.gov.caixa.loterias.apostas.view.listener.OnPaginacaoListener;
import br.gov.caixa.loterias.apostas.view.listener.OnSomadorListener;
import br.gov.caixa.loterias.apostas.view.listener.OnTrevosListener;
import br.gov.caixa.loterias.apostas.view.listener.SurpresinhaFragmentListener;

import static br.gov.caixa.loterias.apostas.controllers.CarrinhoActivity.CARRINHO;
import static br.gov.caixa.loterias.apostas.controllers.CarrinhoActivity.LER_CARRINHO_LOCAL;
import static br.gov.caixa.loterias.apostas.utils.Utils.isErroNegocial;
import static java.lang.String.format;

@EActivity(R.layout.activity_simular_aposta)
public class SimularApostaActivity<T> extends AppCompatActivity implements OnClickListener, CartelaFragmentListener, SurpresinhaFragmentListener, OnSomadorListener, OnTrevosListener, OnPaginacaoListener {

    private static int RESULT_FINISH = 3;

    @ViewById
    protected DrawerLayout drawerLayout;

    @ViewById
    protected ConstraintLayout toolBarLoteca, simularApostaLayoutCarrinhoApostas;

    @ViewById
    protected Button simularTelaAposta, surpresinhaTela, qtdNumerosButton, qtdConcursosButton, botaoLimparAposta, botaoAdicionarCompletarCartela, botaoCarrinho;

    @ViewById
    protected FrameLayout fragmentSimularApostas;

    @ViewById
    protected TextView labelValueSimplesLoteca, labelValueDuplasLoteca, labelValueTriplasLoteca, textoDeNomeInfo, textoSalveEstaAposta, valorApostaCartela, textInfoValorAposta, textViewCarrinhoCartelaCount,
            textInfoCarrinho, tvQuantidadeApostas, valorAtualCarrinho;

    @ViewById
    protected LinearLayout toolBarSimularAposta;

    @ViewById
    protected RelativeLayout salvarApostaLayout, carrinhoApostas, viewTabBar;

    @ViewById
    protected ImageButton btnSalvarAposta;

    @ViewById
    protected EditText editNomeAposta;

    @ViewById
    protected ImageView imageViewPaginacao, iconStarSalvar, imageQtdNumerosButton;

    @ViewById
    protected FrameLayout paginacaoView;

    @Extra
    protected IdentificaoDeUmaApostaDas8Modalidades aposta;

    // Fragments
    private FragmentManager fragmentManager;
    private CartelaFragment cartelaFragment;
    private SurpresinhaFragment surpresinhaFragment;
    private SomadorCarrinhoFragment somadorCarrinhoFragment;
    private PaginacaoFragment paginacaoFragment;
    private TrevosFragment trevosFragment;

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
    private Boolean isEspecial = false;
    private DadosCorporativosBO dadosCorporativosBO;
    private boolean isAvisou = false;

    //mocks
    public int qtdDezenasPossiveisSelecionado;
    public int qtdConcursoSelecionado;
    private Boolean flagApostaSalva = false;
    private int numeroConcursoAtual;

    private Boolean isSurpresinha = false;

    @AfterViews
    protected void init() {
        dadosCorporativosBO = DadosCorporativosBO_.getInstance_(SimularApostaActivity.this);
        isEspecial =  getIntent().getBooleanExtra(getResources().getString(R.string.extra_especial), false);
        invalidateOptionsMenu();
        zerarApostas();
        if(aposta != null){
            mesDeSorteCartela = aposta.getMesDeSorte();
        }

        //Tutorial permanente
        if (tipoJogo == ModalidadeEnum.SUPER_7 && !DadosUsuarioBO.checarTutorialSuperSete()) {
            startActivity(new Intent(this,TutorialSuperSeteActivity.class));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Paginacao paginacao = getPaginacao();
        if (paginacao != null && isFragmentCartela()){
            voltaPagina();
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
//            qtdConcursosButton.setText(qtdConcursosList.get(0));
            qtdConcursosButton.setText(textoBotaoSurpresinhasSelecionado);
        } else {
            qtdConcursosButton.setText(format(getResources().getString(R.string.percent_d_concurso), qtdConcursoSelecionado));
        }
    }

    // Pegar o handle dos botoes que mostrarao o seu respectivo fragment na activity
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.simularTelaAposta: {
                voltaPraEscolhaDeNumeros();
                break;
            }

            case R.id.surpresinhaTela: {
                surpresinhaFragment.setArguments(new Bundle());
                abrirTelaSurprezinha();
                if (temPaginacao()){
                    paginacaoView.setVisibility(View.GONE);
                }

                if (tipoJogo == ModalidadeEnum.MAIS_MILIONARIA){
                    imageQtdNumerosButton.setVisibility(View.GONE);
                    qtdNumerosButton.setVisibility(View.GONE);

                }
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

    private void voltaPraEscolhaDeNumeros() {
        if (temPaginacao()){
            paginacaoView.setVisibility(View.VISIBLE);
        }
        resetaCartela();
        imageQtdNumerosButton.setVisibility(View.VISIBLE);
        qtdNumerosButton.setVisibility(View.VISIBLE);
    }

    private boolean temPaginacao() {
        return parametroSimulacao.isTipoJogo(ModalidadeEnum.MAIS_MILIONARIA)
                || parametroSimulacao.isTipoJogo(ModalidadeEnum.DIA_DE_SORTE)
                || parametroSimulacao.isTipoJogo(ModalidadeEnum.TIMEMANIA)
                && paginacaoFragment != null;
    }

    private void resetaCartela() {
        cartelaFragment.setEscolhaTimeCoracaoSurpresinha(false);
        abrirTelaCartela(false);
        if (tipoJogo == ModalidadeEnum.MAIS_MILIONARIA){
            qtdNumerosButton.setOnClickListener(this);
            qtdNumerosButton.setText(textoBotaoPrognosticosSelecionado);
            botaoAdicionarCompletarCartela.setOnClickListener(this);
            botaoAdicionarCompletarCartela.setBackgroundColor(getResources().getColor(typeGameColorDark));
            botaoLimparAposta.setOnClickListener(this);
            voltaPagina();
        }

        if (parametroSimulacao.isTipoJogo(ModalidadeEnum.DIA_DE_SORTE)){
            voltaPagina();
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
            if(tipoJogo.equals(ModalidadeEnum.LOTECA)) {
                ((TextView) dataSorteio.getActionView().findViewById(R.id.item_titulo)).setText(getResources().getString(R.string.label_resultado));
            } else {
                ((TextView) dataSorteio.getActionView().findViewById(R.id.item_titulo)).setText(getResources().getString(R.string.sorteio));
            }
            ((TextView) dataSorteio.getActionView().findViewById(R.id.item_descricao)).setText(formatterDateFinal.format(dataFormatada));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
        //	Verifica condição de aviso caso ultrapasse o numero max por modalidade
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
        AlertDialogUtils.show(this);
        ApostaBO apostaBO = ApostaBO_.getInstance_(this);

        apostaBO.buscaCarrinho(new RequestListener<CarrinhoDTOResponse>() {
            @Override
            public void onResponse(CarrinhoDTOResponse response) {
                AlertDialogUtils.dismiss();
                Bundle args = new Bundle();
                args.putSerializable(CARRINHO, (Serializable) response.getPayload());
                startActivity(new Intent(SimularApostaActivity.this, CarrinhoActivity.class).putExtras(args));
                if (response.getRedirect() != null){
                    RedirectNetwork.checkRedirectSucesso( response.getRedirect(), SimularApostaActivity.this);
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                AlertDialogUtils.dismiss();
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

    public RelativeLayout getSalvarApostaLayout() {
        return salvarApostaLayout;
    }

    public EditText getEditNomeAposta() {
        return editNomeAposta;
    }

    public ImageView getIconStarSalvar() {
        return iconStarSalvar;
    }

    public IdentificaoDeUmaApostaDas8Modalidades getAposta() {
        return aposta;
    }

    public Boolean getFlagApostaSalva() {
        return flagApostaSalva;
    }

    public void salvarAposta(final ApostaFavoritaDTO apostaFavorita, String nomeAposta, T dezenasSelecionadas) {

        AlertDialogUtils.show(this);

        DTOEnumInteger modalidade = new DTOEnumInteger();
        modalidade.setDescricao(parametroSimulacao.getConcurso().getModalidadeDetalhada().getDescricao());
        modalidade.setValor(parametroSimulacao.getConcurso().getModalidadeDetalhada().getValor());
        apostaFavorita.setModalidade(modalidade);

        if (nomeAposta != null){
            if(nomeAposta.length() > 25){
                apostaFavorita.setNome(nomeAposta.substring(0, 25));
            } else {
                apostaFavorita.setNome(nomeAposta);
            }
        } else {
            apostaFavorita.setNome("");
        }
        apostaFavorita.setNumerosSelecionados(dezenasSelecionadas);
        apostaFavorita.setId(Constantes.ZERO_LONG);

        dadosCorporativosBO.validarApostaFavorita(apostaFavorita, new RequestListener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                salvarAposta(apostaFavorita);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                AlertDialogUtils.dismiss();
                AlertDialogUtils.dismiss();
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
        if(!AlertDialogUtils.isShow()){
            AlertDialogUtils.show(this);
        }
        dadosCorporativosBO.salvarApostaFavorita(aposta, new RequestListener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                AppCenterManager.registraEvento(getResources().getString(R.string.evento_efetuou_adicao_aposta_favorita_sucesso));

                AlertDialogUtils.dismiss();

                atualizaLayoutSalverAposta();
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                AlertDialogUtils.dismiss();
                RedirectNetwork.checkRedirect(error, SimularApostaActivity.this);

            }
        });
    }

    public void mesDeSorteFragment(List<Integer> dezenasSelecionadas, boolean surpresinha) {
        fragmentManager.beginTransaction().replace(R.id.fragmentSimularApostas, MesesFragment_.builder().modalidadeEnum(tipoJogo).
                parametroJogoDTO(parametroSimulacao).dezenasSelecionadas((ArrayList<Integer>) dezenasSelecionadas).build()).commit();

        if (surpresinha) {
            simularTelaAposta.setBackgroundResource(R.drawable.icon_seleciona2);
            surpresinhaTela.setBackgroundResource(R.drawable.icon_surpresinha2);
        }
        proximaPagina();
    }

    public void trevosFragment(List<Integer> dezenasSelecionadas){
        trevosFragment = TrevosFragment_.builder()
                                              .aposta(aposta)
                                              .parametro(parametroSimulacao)
                                              .dezenasSelecionadas((ArrayList<Integer>) dezenasSelecionadas).build();
        fragmentManager.beginTransaction()
                       .replace(R.id.fragmentSimularApostas, trevosFragment).commit();
        Paginacao paginacao = PaginacaoUtils.getPaginacaoMaisMilionaria(getApplicationContext(), dezenasSelecionadas.size(), R.color.mais_milionaria_escura);
        paginacaoFragment.atualizaPaginacao(paginacao);
        proximaPagina();
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
            ModalidadeEnum modalidade = (ModalidadeEnum) getIntent().getSerializableExtra(getResources().getString(R.string.tipoAposta));
            for (ParametroValorApostaDTO parametro : parametroSimulacao.getValoresAposta()) {

                if (modalidade == ModalidadeEnum.MAIS_MILIONARIA){
                    if (parametro.getNumeroTrevos() == parametroSimulacao.getTrevos().getQtdMinima()){
                        qtdNumerosList.add(getResources().getString(R.string.string_vazia) + parametro.getNumeroPrognosticos()
                                                   + getResources().getString(R.string.espaco_numeros_por_espaco)
                                                   + ViewUtils.getMoedaFormat(parametro.getValor()));
                    }
                }else {
                    qtdNumerosList.add(getResources().getString(R.string.string_vazia) + parametro.getNumeroPrognosticos()
                                               + getResources().getString(R.string.espaco_numeros_por_espaco)
                                               + ViewUtils.getMoedaFormat(parametro.getValor()));
                }
            }
            if (parametroSimulacao.getTeimosinhas() != null) {
                for (Integer numeroTeimosinha : parametroSimulacao.getTeimosinhas()) {
                    if (numeroTeimosinha == 0) {
                        qtdConcursosList.add(getString(R.string.teimosinhas_descricao));
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
        somadorCarrinhoFragment = FragmentUtils.startSomadorCarrinhoFragment(getSupportFragmentManager(), somadorCarrinhoFragment, R.id.id_fg_somador_carrinho, "TELA SIMULAR APOSTA");

        if (getPaginacao() != null) {
            paginacaoFragment = FragmentUtils.startPaginacaoFragment(getSupportFragmentManager(), paginacaoFragment, R.id.paginacaoView);
        }else {
            paginacaoView.setVisibility(View.GONE);
        }
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
            case MAIS_MILIONARIA:
                configuraTipoApostaTela(getResources().getString(R.string.underline_mais_milionaria_underline), R.color.mais_milionaria_clara, R.color.mais_milionaria_escura, View.VISIBLE, View.GONE);
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
        AppCenterManager.registraEvento(getResources().getString(R.string.evento_entrou_selecao_prognosticos));

        if (parametroSimulacao.isTipoJogo(ModalidadeEnum.MAIS_MILIONARIA)){
            DialogUtils.showDialogListaEscolha(SimularApostaActivity.this,
                                               getResources().getString(R.string.quant_de_numeros), getResources().getString(R.string.valores_para_apostas_com_2_trevos),
                                               qtdNumerosList, onNumerosDialogListener());
        } else {
            DialogUtils.showDialogListaEscolha(SimularApostaActivity.this,
                                               getResources().getString(R.string.quant_de_numeros), null,
                                               qtdNumerosList, onNumerosDialogListener());
        }
    }

    private OnDialogListener onNumerosDialogListener() {
        return new OnDialogListener() {
            @Override
            public void itemSelecionado(int position) {
                String itemValue = (String) qtdNumerosList.get(position);
                if(tipoJogo == ModalidadeEnum.MAIS_MILIONARIA){
                    String stringNumero = "";
                    if (itemValue.startsWith("1")){
                        stringNumero = itemValue.substring(0,2);
                    }else {
                        stringNumero = itemValue.substring(0, 1);
                    }
                    ParametroValorApostaDTO valorAposta = parametroSimulacao.getValorApostaBy(new Integer(stringNumero), 2);
                    
                    if (valorAposta != null){
                        textoBotaoPrognosticosSelecionado = valorAposta.getNumeroPrognosticos() + getResources().getString(R.string.espaco_numeros);
                        qtdDezenasPossiveisSelecionado = valorAposta.getNumeroPrognosticos();
                    }
                }else {
                    textoBotaoPrognosticosSelecionado = parametroSimulacao.getValoresAposta().get(position).getNumeroPrognosticos() + getResources().getString(R.string.espaco_numeros);
                    qtdDezenasPossiveisSelecionado = parametroSimulacao.getValoresAposta().get(position).getNumeroPrognosticos();
                }
                if ((position >= 1) && (!isAvisou)) {
                    ViewUtils.alertTitleButton(SimularApostaActivity.this, R.string.label_atencao, getString(R.string.msg_qtd_max_ultrapassada), getString(R.string.btn_entendi));
                    isAvisou = true;
                }
            }

            @Override
            public void ok() {
                qtdNumerosButton.setText(textoBotaoPrognosticosSelecionado);

                if (isFragmentCartela()) {
                    cartelaFragment.atualizaValorTotalAposta(qtdDezenasPossiveisSelecionado, qtdConcursoSelecionado);
                    cartelaFragment.atualizaStatusBotaoFinalizar(true);
                } else if (isFragmentMeses()){
                    voltaPraEscolhaDeNumeros();
                } else {
                    if (surpresinhaFragment.getParametroJogo() != null) {
                        surpresinhaFragment.atualizaValorTotalAposta(qtdDezenasPossiveisSelecionado, qtdConcursoSelecionado);
                        if (tipoJogo == ModalidadeEnum.MAIS_MILIONARIA){
                            surpresinhaFragment.resetQtdTrevos();
                        }
                    }
                }
            }

            @Override
            public void cancelar() {

            }
        };
    }

    private boolean isFragmentMeses() {
        return fragmentManager.findFragmentById(R.id.fragmentSimularApostas) instanceof MesesFragment;
    }

    public boolean isFragmentCartela(){
        return fragmentManager.findFragmentById(R.id.fragmentSimularApostas) instanceof CartelaFragment;
    }

    private void modalTeimosinhas() {
        AppCenterManager.registraEvento(getResources().getString(R.string.evento_entrou_popup_teimosinha));

        DialogUtils.showDialogListaEscolha(SimularApostaActivity.this,
                                           getResources().getString(R.string.teimosinhas_maiusculo), null,
                                           qtdConcursosList, onTeimosinhaDialogListener());

    }

    private OnDialogListener onTeimosinhaDialogListener() {
        return new OnDialogListener() {
            @Override
            public void itemSelecionado(int position) {
                if (position == 0) {
                    textoBotaoSurpresinhasSelecionado = getString(R.string.label_sem_teimosinha);
                }else{
                    textoBotaoSurpresinhasSelecionado = (String) qtdConcursosList.get(position);
                    qtdConcursoSelecionado = parametroSimulacao.getTeimosinhas().get(position);
                }

            }

            @Override
            public void ok() {
                qtdConcursosButton.setText(textoBotaoSurpresinhasSelecionado);

                if (fragmentManager.findFragmentById(R.id.fragmentSimularApostas) instanceof SurpresinhaFragment) {
                    if (surpresinhaFragment.getParametroJogo() != null) {
                        surpresinhaFragment.atualizaValorTotalAposta(qtdDezenasPossiveisSelecionado, qtdConcursoSelecionado);
                    }
                } else if (fragmentManager.findFragmentById(R.id.fragmentSimularApostas) instanceof TrevosFragment_){
                    if (trevosFragment != null && trevosFragment.getParametroValorApostaSelecionado() != null){
                        atualizaValorAposta(trevosFragment.getParametroValorApostaSelecionado());
                    }
                } else {
                    cartelaFragment.atualizaValorTotalAposta(qtdDezenasPossiveisSelecionado, qtdConcursoSelecionado);
                }
            }

            @Override
            public void cancelar() {

            }
        };
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

        if (BuildVersionUtil.isAutomacao() && tipoJogo == ModalidadeEnum.DUPLA_SENA){
            setTitle(ViewUtils.textFuturaAndFuturaBoldTitle(this, "dupla-sena"));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, darkColor));
        }
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, lightColor)));

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setElevation(0);
        }

        toolBarSimularAposta.setBackgroundColor(ContextCompat.getColor(this, darkColor));
        if (isEspecial) {
            switch (tipoJogo){
                case MEGA_SENA:
                    getSupportActionBar().setBackgroundDrawable(getDrawable(R.drawable.rodape_mega_da_virada_superior));
                    toolBarSimularAposta.setBackground(getDrawable(R.drawable.rodape_mega_da_virada_inferior));
                    break;
                case DUPLA_SENA:
                    getSupportActionBar().setBackgroundDrawable(getDrawable(R.drawable.rodape_dupla_de_pascoa_superior));
                    toolBarSimularAposta.setBackground(getDrawable(R.drawable.rodape_dupla_de_pascoa_inferior));
                    break;
                case LOTOFACIL:
                    getSupportActionBar().setBackgroundDrawable(getDrawable(R.drawable.rodape_lotofacil_da_independencia_superior));
                    toolBarSimularAposta.setBackground(getDrawable(R.drawable.rodape_lotofacil_da_independencia_inferior));
                    break;
                case QUINA:
                    getSupportActionBar().setBackgroundDrawable(getDrawable(R.drawable.rodape_sao_joao_superior));
                    toolBarSimularAposta.setBackground(getDrawable(R.drawable.rodape_sao_joao_inferior));
                    break;
                default:
            }
        }
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

    @Override
    public void atualizaDados(CarrinhoDTO carrinho) {

        ViewUtils.setMoedaFormatHtml(carrinho.getValorTotal(), valorAtualCarrinho);
        if (carrinho != null && carrinho.getApostas() != null) {
            if (carrinho.getApostas().size() <= 999) {
                tvQuantidadeApostas.setText(String.valueOf(carrinho.getApostas().size()));
            } else {
                tvQuantidadeApostas.setText(R.string.mais_999);
            }
        }else {
            tvQuantidadeApostas.setText(R.string.zero);
        }
    }

    @Override
    public Button getButtonQtdTrevos() {
        return qtdNumerosButton;
    }

    @Override
    public Button getButtonFinalizar() {
        return botaoAdicionarCompletarCartela;
    }

    @Override
    public Button getButtonLimpar() {
        return botaoLimparAposta;
    }

    @Override
    public void atualizaValorAposta(ParametroValorApostaDTO valorAposta) {
        BigDecimal valorEmReais = valorAposta.getValor();

        if (qtdConcursoSelecionado != 0) {
            valorEmReais = valorEmReais.multiply(BigDecimal.valueOf(qtdConcursoSelecionado).movePointLeft(0));
        }

        ViewUtils.setMoedaFormatHtml(valorEmReais, valorApostaCartela);
    }

    @Override
    public int getQtdConcursos() {
        return qtdConcursoSelecionado;
    }

    @Override
    public RelativeLayout getButtonSalvar() {
        return salvarApostaLayout;
    }

    @Override
    public String nomeApostaFavorita() {
        if(editNomeAposta != null){
            return editNomeAposta.getText().toString();
        }

        return "";
    }

    @Override
    public ImageButton btnSalvarAposta() {
        return btnSalvarAposta;
    }

    @Override
    public void atualizaLayoutSalverAposta() {
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
    public Paginacao getPaginacao() {
        Paginacao paginacao = null;
        switch (parametroSimulacao.getConcurso().getModalidade()){
            case MAIS_MILIONARIA:
                paginacao = PaginacaoUtils.getPaginacaoMaisMilionaria(getApplicationContext(), 0, R.color.mais_milionaria_escura);
                break;
            case TIMEMANIA:
                paginacao = PaginacaoUtils.getPaginacaoTimemania(getApplicationContext(), R.color.timemaniaescuro);
                break;
            case DIA_DE_SORTE:
                paginacao = PaginacaoUtils.getPaginacaoDiaDeSorte(getApplicationContext(), R.color.diasorteescuro);
                break;
        }

        return paginacao;
    }

    @Override
    public void proximaPagina() {
        paginacaoFragment.proximaPagina();
    }

    @Override
    public void voltaPagina() {
        paginacaoFragment.voltaPagina();
    }
}