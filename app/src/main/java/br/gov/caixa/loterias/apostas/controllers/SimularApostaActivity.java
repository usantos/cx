package br.gov.caixa.loterias.apostas.controllers;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Gravity;
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

import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.google.gson.Gson;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.gov.caixa.loterias.apostas.LoteriasBaseAppActivity;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bean.Paginacao;
import br.gov.caixa.loterias.apostas.model.bo.DadosCorporativosSilceBO;
import br.gov.caixa.loterias.apostas.model.bo.DadosUsuarioBO;
import br.gov.caixa.loterias.apostas.model.bo.MensagensNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaFavoritaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DTOEnumInteger;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroEquipe;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroJogoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroMesDeSorte;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroValorApostaDTO;
import br.gov.caixa.loterias.apostas.model.model.AccordionModel;
import br.gov.caixa.loterias.apostas.utils.BarraTituloDTO;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.AppCenterManager;
import br.gov.caixa.loterias.apostas.utils.Constantes;
import br.gov.caixa.loterias.apostas.utils.DialogUtils;
import br.gov.caixa.loterias.apostas.utils.EspecialUtils;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidadeMKP;
import br.gov.caixa.loterias.apostas.utils.FragmentUtils;
import br.gov.caixa.loterias.apostas.utils.InputUtils;
import br.gov.caixa.loterias.apostas.utils.PaginacaoUtils;
import br.gov.caixa.loterias.apostas.utils.ServicoFactoryUtil;
import br.gov.caixa.loterias.apostas.utils.SharedPreferencesUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.custom.AccordionView;
import br.gov.caixa.loterias.apostas.view.custom.LottieManager;
import br.gov.caixa.loterias.apostas.view.fragment.CartelaFragment;
import br.gov.caixa.loterias.apostas.view.fragment.MesesFragment;
import br.gov.caixa.loterias.apostas.view.fragment.PaginacaoFragment;
import br.gov.caixa.loterias.apostas.view.fragment.RodapeSimulacaoApostaFragment;
import br.gov.caixa.loterias.apostas.view.fragment.SomadorCarrinhoFragment;
import br.gov.caixa.loterias.apostas.view.fragment.SurpresinhaFragment;
import br.gov.caixa.loterias.apostas.view.fragment.TrevosFragment;
import br.gov.caixa.loterias.apostas.view.listener.CartelaFragmentListener;
import br.gov.caixa.loterias.apostas.view.listener.OnDialogBotaoListener;
import br.gov.caixa.loterias.apostas.view.listener.OnDialogListener;
import br.gov.caixa.loterias.apostas.view.listener.OnPaginacaoListener;
import br.gov.caixa.loterias.apostas.view.listener.OnSomadorListener;
import br.gov.caixa.loterias.apostas.view.listener.OnTrevosListener;
import br.gov.caixa.loterias.apostas.view.listener.SurpresinhaFragmentListener;

import static br.gov.caixa.loterias.apostas.utils.Utils.isErroNegocial;
import static java.lang.String.format;

public class SimularApostaActivity<T> extends LoteriasBaseAppActivity implements OnClickListener, CartelaFragmentListener, SurpresinhaFragmentListener, OnSomadorListener, OnTrevosListener, OnPaginacaoListener {

    public static String COR_FONTE_FUNDO_BRANCO = "COR_FONTE_FUNDO_BRANCO";
    public static String COR_FONTE_FUNDO_CLARO = "COR_FONTE_FUNDO_CLARO";
    public static String COR_FONTE_FUNDO_ESCURO = "COR_FONTE_FUNDO_ESCURO";

    public final static String APOSTA_EXTRA = "aposta";
    private static final int RESULT_FINISH = 3;
    //TODO: MEGA 30 ANOS//
    public final static String extra_modalidade = "modalidade";
    private int numero_concurso;
    private ConstraintLayout toolBarLoteca;
    private Button qtdNumerosButton, qtdConcursosButton, botaoLimparAposta, botaoAdicionarCompletarCartela;
    private AppCompatImageView simularTelaAposta, surpresinhaTela;
    private TextView labelValueJogosLoteca, labelValueSimplesLoteca, labelValueDuplasLoteca,
            labelValueTriplasLoteca, textoSalveEstaAposta;
    private LinearLayout toolBarSimularAposta;
    private RelativeLayout salvarApostaLayout;
    private ImageButton btnSalvarAposta;
    private EditText editNomeAposta;
    private ImageView imageViewPaginacao, iconStarSalvar, imageQtdNumerosButton, imageQtdConcursosButton;
    private FrameLayout paginacaoView;

    protected IdentificaoDeUmaApostaDas8Modalidades aposta;

    // Fragments
    private FragmentManager fragmentManager;
    private CartelaFragment cartelaFragment;
    private SurpresinhaFragment surpresinhaFragment;
    private SomadorCarrinhoFragment somadorCarrinhoFragment;
    private PaginacaoFragment paginacaoFragment;
    private TrevosFragment trevosFragment;
    private RodapeSimulacaoApostaFragment rodapeFragment;

    private AccordionView accordionView;

    private int typeGameColorLight;
    private int typeGameColorDark;
    private List<String> qtdNumerosList;
    private List<String> qtdConcursosList;
    private ModalidadeEnum tipoJogo;
    private String textoBotaoPrognosticosSelecionado;
    private String textoBotaoSurpresinhasSelecionado;
    private ParametroJogoDTO parametroSimulacao;
    private ParametroMesDeSorte mesDeSorteCartela;
    private ParametroMesDeSorte mesDeSorteSurpresinha;
    private Boolean isEspecial = false;
    private DadosCorporativosSilceBO dadosCorporativosSilceBO;
    private boolean isAvisou = false;

    //mocks
    public int qtdDezenasPossiveisSelecionado;
    public int qtdConcursoSelecionado;
    private Boolean flagApostaSalva = false;

    private Boolean isSurpresinha = false;
    //TODO: MEGA 30 ANOS//
    private boolean isMega30;
    private EstiloModalidadeMKP estilo;
    private LottieManager lottieManager;

    public BarraTituloDTO barraTituloDTO = new BarraTituloDTO();
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simular_aposta);
        init();
    }
    
    protected void init() {
        dadosCorporativosSilceBO = DadosCorporativosSilceBO.getInstance();
        getExtras();
        try{
            barraTituloDTO.setDataSorteio(getDataSorteio());
            barraTituloDTO.setNumeroConcurso(parametroSimulacao.getConcurso().getNumero().toString());
            barraTituloDTO.setEspecial(isEspecial);

        } catch (Exception e){
            Log.e("ERROR", "Não foi possível adiquirir os elementos da barra de título");
        }
        isMega30 = EspecialUtils.isMega30(numero_concurso, isEspecial);
        estilo = new EstiloModalidadeMKP(tipoJogo, isMega30);
        preencheBarraTitulo();
        setViews();

        estilo = new EstiloModalidadeMKP(tipoJogo);
        zerarApostas();
        if(aposta != null){
            mesDeSorteCartela = aposta.getMesDeSorte();
        }

        //Tutorial permanente
        if (tipoJogo == ModalidadeEnum.SUPER_7 && !DadosUsuarioBO.checarTutorial(DadosUsuarioBO.SUPER_SETE_TUTORIAL)) {
            startActivity(new Intent(this,TutorialSuperSeteActivity.class));
        }

        //Animação Copa da Loteca
        if (tipoJogo == ModalidadeEnum.LOTECA && isEspecial) {
            lottieManager = new LottieManager(this, R.raw.copa_loteca_3);
            lottieManager.setSpeed(1.75f);
            lottieManager.start();
        }
        //Animação Quina de São João
        if (tipoJogo == ModalidadeEnum.QUINA && isEspecial) {
            lottieManager = new LottieManager(this, R.raw.quina_sao_joao_1);
            lottieManager.setSpeed(1.5f);
            lottieManager.start();
        }

        if (tipoJogo == ModalidadeEnum.INSTANTANEA ||
                tipoJogo == ModalidadeEnum.BOLAO ||
                tipoJogo == ModalidadeEnum.LOTOGOL ||
                tipoJogo == ModalidadeEnum.COMBO ||
                tipoJogo == ModalidadeEnum.LOTECA) {
            accordionView.setVisibility(View.GONE);
        } else {
            AccordionModel model = new AccordionModel(tipoJogo, isEspecial);
            accordionView.bind(model);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (lottieManager != null) {
            lottieManager.destroy();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (lottieManager != null) {
            lottieManager.pause();
        }
    }

    private void preencheBarraTitulo() {
        FragmentUtils.startFragmentBarraTituloSimulacao(getSupportFragmentManager(), R.id.id_fg_barra_titulo,
                tipoJogo, parametroSimulacao.getConcurso().getNumero().toString(), getDataSorteio(), isEspecial, getCorBackgroundBarraTitulo(), true);
    }

    private int getCorBackgroundBarraTitulo() {
        return estilo.getCorClara();
    }

    private String getDataSorteio() {
        String dataSorteio = "";
        SimpleDateFormat formatterDateFinal = new SimpleDateFormat(getResources().getString(R.string.dd_mm));
        DateFormat formatterDate = new SimpleDateFormat(getResources().getString(R.string.dd_mm_yyyy_hh_mm_ss));

        try {
            if (parametroSimulacao != null && parametroSimulacao.getConcurso() != null && parametroSimulacao.getConcurso().getDataHoraSorteio() != null){
                Date dataFormatada = formatterDate.parse(parametroSimulacao.getConcurso().getDataHoraSorteio());
                dataSorteio = formatterDateFinal.format(dataFormatada);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dataSorteio;
    }

    private void getExtras() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            //TODO: MEGA 30 ANOS//
            if (extras.containsKey(APOSTA_EXTRA)) {
                this.aposta = ((IdentificaoDeUmaApostaDas8Modalidades) extras.getSerializable(APOSTA_EXTRA));
                System.out.println("teste aposta " + aposta);
            }
            if (extras.containsKey("NUMERO_CONCURSO")){
                this.numero_concurso = extras.getInt("NUMERO_CONCURSO");
            }
            isEspecial = extras.getBoolean(getResources().getString(R.string.extra_especial), false);
            tipoJogo = (ModalidadeEnum) extras.getSerializable(getResources().getString(R.string.tipoAposta));
            parametroSimulacao = new Gson().fromJson(extras.getString(getResources().getString(R.string.extra_modalidade)), ParametroJogoDTO.class);
        }
    }

    private void setViews() {
        this.toolBarLoteca = findViewById(R.id.toolBarLoteca);
        this.simularTelaAposta = findViewById(R.id.simularTelaAposta);
        this.surpresinhaTela = findViewById(R.id.surpresinhaTela);
        this.qtdNumerosButton = findViewById(R.id.qtdNumerosButton);
        this.qtdConcursosButton = findViewById(R.id.qtdConcursosButton);
        this.botaoLimparAposta = findViewById(R.id.botaoLimparAposta);
        this.botaoAdicionarCompletarCartela = findViewById(R.id.botaoAdicionarCompletarCartela);
        this.labelValueJogosLoteca = findViewById(R.id.labelValueJogosLoteca);
        this.labelValueSimplesLoteca = findViewById(R.id.labelValueSimplesLoteca);
        this.labelValueDuplasLoteca = findViewById(R.id.labelValueDuplasLoteca);
        this.labelValueTriplasLoteca = findViewById(R.id.labelValueTriplasLoteca);
        this.textoSalveEstaAposta = findViewById(R.id.textoSalveEstaAposta);
        this.toolBarSimularAposta = findViewById(R.id.toolBarSimularAposta);
        this.salvarApostaLayout = findViewById(R.id.salvarApostaLayout);
        this.btnSalvarAposta = findViewById(R.id.btnSalvarAposta);
        this.editNomeAposta = findViewById(R.id.editNomeAposta);
        this.iconStarSalvar = findViewById(R.id.iconStarSalvar);
        this.imageQtdNumerosButton = findViewById(R.id.imageQtdNumerosButton);
        this.imageQtdConcursosButton = findViewById(R.id.imageQtdConcursosButton);
        this.paginacaoView = findViewById(R.id.paginacaoView);
        this.accordionView = findViewById(R.id.custom_accordion_view);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Paginacao paginacao = getPaginacao();
        if (paginacao != null && isFragmentCartela() && !parametroSimulacao.isTipoJogo(ModalidadeEnum.TIMEMANIA)){
            voltaPagina();
        }
    }

    public void zerarApostas() {
        qtdNumerosList = new ArrayList<>();
        qtdConcursosList = new ArrayList<>();
        textoBotaoSurpresinhasSelecionado = getString(R.string.label_sem_teimosinha);
        mesDeSorteCartela = null;
        mesDeSorteSurpresinha = null;
        configuraParametrosJogo();
        configuraFragments();
        configuraActivity();
        zerarApostasLoteca();
    }

    public void zerarApostasLoteca() {
        this.setLabelValueJogosLoteca(Constantes.ZERO_STRING);
        this.setLabelValueSimplesLoteca(Constantes.ZERO_STRING);
        this.setLabelValueDuplasLoteca(Constantes.ZERO_STRING);
        this.setLabelValueTriplasLoteca(Constantes.ZERO_STRING);
        setColorValuesLoteca();
    }

    public void atualizaTextoBotoesQtdSelecionadas() {
        qtdNumerosButton.setText(format(getResources().getString(R.string.percent_d_numeros), qtdDezenasPossiveisSelecionado));
        qtdNumerosButton.setTextColor(ContextCompat.getColor(this, estilo.getCorFonteFundoEscuro()));
        Drawable drawableQtdNumeros =  imageQtdNumerosButton.getDrawable();
        ViewUtils.setColorDrawable(this, drawableQtdNumeros, estilo.getCorFonteFundoEscuro());

        textoBotaoPrognosticosSelecionado = qtdNumerosButton.getText().toString();
        if ((qtdConcursosList != null) && (qtdConcursoSelecionado == 0)) {
            qtdConcursosButton.setText(textoBotaoSurpresinhasSelecionado);
        } else {
            qtdConcursosButton.setText(format(getResources().getString(R.string.percent_d_concurso), qtdConcursoSelecionado));
        }
        qtdConcursosButton.setTextColor(ContextCompat.getColor(this, estilo.getCorFonteFundoEscuro()));
        Drawable drawableQtdConcursos = imageQtdConcursosButton.getDrawable();
        ViewUtils.setColorDrawable(this, drawableQtdConcursos, estilo.getCorFonteFundoEscuro());
    }

    // Pegar o handle dos botoes que mostrarao o seu respectivo fragment na activity
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.simularTelaAposta:
                voltaPraEscolhaDeNumeros();
                break;
            case R.id.surpresinhaTela:
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

            case R.id.qtdNumerosButton:
                modalNumeros();
                break;

            case R.id.qtdConcursosButton:
                modalTeimosinhas();
                break;
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

    public void setColorValuesLoteca() {
        this.labelValueJogosLoteca.setTextColor(getResources().getColor(estilo.getCorClara()));
        this.labelValueSimplesLoteca.setTextColor(getResources().getColor(estilo.getCorClara()));
        this.labelValueDuplasLoteca.setTextColor(getResources().getColor(estilo.getCorClara()));
        this.labelValueTriplasLoteca.setTextColor(getResources().getColor(estilo.getCorClara()));
    }
    public void setLabelValueJogosLoteca(String labelValueJogosLoteca) {
        this.labelValueJogosLoteca.setText(labelValueJogosLoteca);
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

    public TextView getLabelValueJogosLoteca() {
        return labelValueJogosLoteca;
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
        return rodapeFragment.getTvValorAposta();
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

        ServicoFactoryUtil.getDadosCorporativoService().validarApostaFavorita(apostaFavorita, new RequestListener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                salvarAposta(apostaFavorita);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                AlertDialogUtils.dismiss();
                AlertDialogUtils.dismiss();
                if(isErroNegocial(error)){
//                    ViewUtils.alertTitleButtonYesOrNotListener(
//                            SimularApostaActivity.this,
//                            MensagensNetwork.getErrorMessage(error),
//                            (dialogInterface, i) ->
//                    );

                    DialogUtils.dialogSim(
                            SimularApostaActivity.this,
                            MensagensNetwork.getErrorMessage(error),
                            new OnDialogBotaoListener() {
                                @Override
                                public void onButtonClick(DialogInterface dialog, int which) {
                                    salvarAposta(apostaFavorita);
                                }
                            }
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

        ServicoFactoryUtil.getDadosCorporativoService().salvarApostaFavorita(aposta, new RequestListener<NetworkResponse>() {
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
        FragmentUtils.startMesesFragment(fragmentManager, R.id.fragmentSimularApostas, tipoJogo,
                parametroSimulacao, (ArrayList<Integer>) dezenasSelecionadas);

        if (surpresinha) {
            simularTelaAposta.setBackgroundResource(R.drawable.icon_seleciona2);
            surpresinhaTela.setBackgroundResource(R.drawable.icon_surpresinha2);
        }
        proximaPagina();
    }

    public void trevosFragment(List<Integer> dezenasSelecionadas){
        trevosFragment = FragmentUtils.startTrevosFragment(fragmentManager, R.id.fragmentSimularApostas,
                aposta, parametroSimulacao, (ArrayList<Integer>)dezenasSelecionadas);
        Paginacao paginacao = PaginacaoUtils.getPaginacaoMaisMilionaria(getApplicationContext(), dezenasSelecionadas.size(), R.color.milionaria_escuro_mkp);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_FINISH) {
            finish();
        }
    }

    private void abrirTelaSurprezinha() {
        FragmentUtils.startFragmentAllowingStateLoss(fragmentManager, R.id.fragmentSimularApostas, surpresinhaFragment);
        if (tipoJogo == ModalidadeEnum.TIMEMANIA){
            Drawable ic_numeros = ContextCompat.getDrawable(this, R.drawable.icon_seleciona2);
            ViewUtils.setColorDrawable(this, ic_numeros, estilo.getCorFonteFundoClaro());
            simularTelaAposta.setBackground(ic_numeros);
            surpresinhaTela.setBackgroundResource(R.drawable.icon_surpresinha2);
        } else {
            simularTelaAposta.setBackgroundResource(R.drawable.icon_seleciona2);
            surpresinhaTela.setBackgroundResource(R.drawable.icon_surpresinha2);
        }
    }

    private void configuraParametrosJogo() {
        if (parametroSimulacao != null && parametroSimulacao.getValoresAposta() != null) {
            for (ParametroValorApostaDTO parametro : parametroSimulacao.getValoresAposta()) {
                if (tipoJogo == ModalidadeEnum.MAIS_MILIONARIA){
                    if (parametro.getNumeroTrevos().equals(parametroSimulacao.getTrevos().getQtdMinima())){
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
        Bundle bundle = new Bundle();
        bundle.putSerializable("barraTituloModel", barraTituloDTO);

        cartelaFragment.setArguments(bundle);

        surpresinhaFragment = FragmentUtils.getSurpresinhaFragment();

        rodapeFragment = FragmentUtils.startRodapeSimulacaoAPosta(getSupportFragmentManager(), R.id.id_fg_rodape, new BigDecimal(0), new BigDecimal(0));

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragmentSimularApostas, cartelaFragment).commit();
        somadorCarrinhoFragment = FragmentUtils.startSomadorCarrinhoFragment(getSupportFragmentManager(), somadorCarrinhoFragment, R.id.id_fg_somador_carrinho, "TELA SIMULAR APOSTA");

        if (getPaginacao() != null) {
            paginacaoFragment = FragmentUtils.startPaginacaoFragment(getSupportFragmentManager(), paginacaoFragment, R.id.paginacaoView);
        }else {
            if (paginacaoView != null){
                paginacaoView.setVisibility(View.GONE);
            }
        }
    }

    private void configuraActivity() {
        switch (tipoJogo) {
            case MEGA_SENA:
                if(isMega30){
                    configuraTipoApostaTela(SharedPreferencesUtils.getValorString(EspecialUtils.MEGA_DUAS_LINHAS, ""), R.color.mega_verde_claro_mkp, R.color.mega_verde_escuro_mkp, View.VISIBLE, View.GONE);
                } else {
                    configuraTipoApostaTela(isEspecial? getResources().getString(R.string.underline_mega_virada_underline) : getResources().getString(R.string.unterline_mega_sena_underline), R.color.mega_verde_claro_mkp, R.color.mega_verde_escuro_mkp, View.VISIBLE, View.GONE);
                }
                break;
            case QUINA:
                configuraTipoApostaTela(isEspecial? getResources().getString(R.string.underline_quina_sao_joao_underline) : getResources().getString(R.string.underline_quina_underline), R.color.quina_claro_mkp, R.color.quina_escuro_mkp, View.VISIBLE, View.GONE);
                break;
            case LOTOFACIL:
                configuraTipoApostaTela(isEspecial? getResources().getString(R.string.underline_lotofacil_independencia_underline) : getResources().getString(R.string.underline_lotofacil_underline), R.color.lotofacil_claro_mkp, R.color.lotofacil_escuro_mkp, View.VISIBLE, View.GONE);
                break;
            case DUPLA_SENA:
                configuraTipoApostaTela(isEspecial? getResources().getString(R.string.underline_dupla_pascoa_underline) : getResources().getString(R.string.underline_dupla_sena_underline), R.color.dupla_sena_claro_mkp, R.color.dupla_sena_escuro_mkp, View.VISIBLE, View.GONE);
                break;
            case TIMEMANIA:
                configuraTipoApostaTela(getResources().getString(R.string.underline_timemania_underline), R.color.timemania_claro_mkp, R.color.timemania_escuro_mkp, View.VISIBLE, View.GONE);
                break;
            case LOTOMANIA:
                configuraTipoApostaTela(getResources().getString(R.string.underline_lotomania_underline), R.color.lotomania_claro_mkp, R.color.lotomania_escuro_mkp, View.VISIBLE, View.GONE);
                break;
            case LOTECA:
                configuraTipoApostaTela(getResources().getString(R.string.underline_loteca_underline), R.color.loteca_claro_mkp, R.color.loteca_escuro_mkp, View.GONE, View.VISIBLE);
                break;
            case DIA_DE_SORTE:
                configuraTipoApostaTela(getResources().getString(R.string.underline_dia_de_Sorte_underline), R.color.dia_sorte_claro_mkp, R.color.dia_sorte_escuro_mkp, View.VISIBLE, View.GONE);
                break;
            case SUPER_7:
                configuraTipoApostaTela(getResources().getString(R.string.underline_super_sete_underline), R.color.super_sete_claro_mkp, R.color.super_sete_escuro_mkp, View.VISIBLE, View.GONE);
                break;
            case MAIS_MILIONARIA:
                configuraTipoApostaTela(getResources().getString(R.string.underline_mais_milionaria_underline).toLowerCase(), R.color.milionaria_claro_mkp, R.color.milionaria_escuro_mkp, View.VISIBLE, View.GONE);
                break;
        }

        qtdDezenasPossiveisSelecionado = parametroSimulacao.getQuantidadeMinima() != null ? parametroSimulacao.getQuantidadeMinima() : 0;
        qtdConcursoSelecionado = parametroSimulacao.getTeimosinhas() != null ? parametroSimulacao.getTeimosinhas().get(0) == 0 ? 0 : parametroSimulacao.getTeimosinhas().get(0) : 0;
        String dataSorteioAtual = parametroSimulacao.getConcurso().getDataSorteio();

        // Passando um link
        Bundle bundle = new Bundle();
        bundle.putInt(getResources().getString(R.string.extra_qtd_dezenas_possiveis_selecionado), qtdDezenasPossiveisSelecionado);
        bundle.putString(getResources().getString(R.string.extra_data_sorteio_atual), dataSorteioAtual);
        bundle.putSerializable(getResources().getString(R.string.extra_type_game_color_light), typeGameColorLight);
        bundle.putSerializable(getResources().getString(R.string.extra_type_game_color_Dark), typeGameColorDark);
        bundle.putSerializable(COR_FONTE_FUNDO_BRANCO, estilo.getCorLetraLista());
        bundle.putSerializable(COR_FONTE_FUNDO_CLARO, estilo.getCorFonteFundoClaro());
        bundle.putSerializable(COR_FONTE_FUNDO_ESCURO, estilo.getCorFonteFundoEscuro());

        cartelaFragment.cartelaFragmentBuilder(bundle);
        surpresinhaFragment.surpresinhaFragmentBuilder(bundle);

        atualizaTextoBotoesQtdSelecionadas();

        simularTelaAposta.setBackgroundResource(R.drawable.icon_seleciona);
        if (tipoJogo == ModalidadeEnum.TIMEMANIA){
            Drawable ic_surpresinha = ContextCompat.getDrawable(this, R.drawable.icon_surpresinha);
            ViewUtils.setColorDrawable(this, ic_surpresinha, estilo.getCorFonteFundoClaro());
            surpresinhaTela.setBackground(ic_surpresinha);
        } else {
            surpresinhaTela.setBackgroundResource(R.drawable.icon_surpresinha);
        }

        configuraBotoesListeners();
    }

    public void configuraBotoesListeners() {
        simularTelaAposta.setOnClickListener(this);
        surpresinhaTela.setOnClickListener(this);
        qtdNumerosButton.setOnClickListener(this);
        qtdConcursosButton.setOnClickListener(this);
    }

    private void modalNumeros() {
        AppCenterManager.registraEvento(getResources().getString(R.string.evento_entrou_selecao_prognosticos));
        if (parametroSimulacao.isTipoJogo(ModalidadeEnum.MAIS_MILIONARIA)){
            DialogUtils.showDialogListItens(SimularApostaActivity.this,
                    getResources().getString(R.string.quant_de_numeros),
                    getResources().getString(R.string.valores_para_apostas_com_2_trevos),
                    qtdNumerosList,
                    "Confirmar",
                    "Cancelar",
                    onNumerosDialogListener());
        } else {
            DialogUtils.showDialogListItens(SimularApostaActivity.this,
                    getResources().getString(R.string.quant_de_numeros),
                    null,
                    qtdNumerosList,
                    "Confirmar",
                    "Cancelar",
                    onNumerosDialogListener());

        }
    }

    private OnDialogListener onNumerosDialogListener() {
        return new OnDialogListener() {
            @Override
            public void itemSelecionado(int position) {
                String itemValue = (String) qtdNumerosList.get(position);
                if(tipoJogo == ModalidadeEnum.MAIS_MILIONARIA){
                    String stringNumero;
                    if (itemValue.startsWith("1")){
                        stringNumero = itemValue.substring(0,2);
                    }else {
                        stringNumero = itemValue.substring(0, 1);
                    }
                    ParametroValorApostaDTO valorAposta = parametroSimulacao.getValorApostaBy(Integer.valueOf(stringNumero), 2);
                    
                    if (valorAposta != null){
                        textoBotaoPrognosticosSelecionado = valorAposta.getNumeroPrognosticos() + getResources().getString(R.string.espaco_numeros);
                        qtdDezenasPossiveisSelecionado = valorAposta.getNumeroPrognosticos();
                    }
                    if ((position >= 1) && (!isAvisou)) {
//                        ViewUtils.alertTitleButton(SimularApostaActivity.this,
//                                R.string.label_atencao,
//                                getString(R.string.msg_qtd_max_ultrapassada), getString(R.string.btn_entendi));

                        DialogUtils.dialogEntendi(
                                SimularApostaActivity.this,
                                getString(R.string.msg_qtd_max_ultrapassada)
                        );

                        isAvisou = true;
                    }
                }else {
                    textoBotaoPrognosticosSelecionado = parametroSimulacao.getValoresAposta().get(position).getNumeroPrognosticos() + getResources().getString(R.string.espaco_numeros);
                    qtdDezenasPossiveisSelecionado = parametroSimulacao.getValoresAposta().get(position).getNumeroPrognosticos();
                }
                if ((position >= 1) && (!isAvisou)) {
//                    ViewUtils.alertTitleButton(SimularApostaActivity.this,
//                            R.string.label_atencao, getString(R.string.msg_qtd_max_ultrapassada), getString(R.string.btn_entendi));
                    DialogUtils.dialogEntendi(
                            SimularApostaActivity.this,
                            getString(R.string.msg_qtd_max_ultrapassada)
                    );
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
            public void cancelar() {}
        };
    }

    private boolean isFragmentMeses() {
        return fragmentManager.findFragmentById(R.id.fragmentSimularApostas) instanceof MesesFragment;
    }

    public boolean isFragmentCartela(){
        return fragmentManager.findFragmentById(R.id.fragmentSimularApostas) instanceof CartelaFragment;
    }
//TODO//
    private void modalTeimosinhas() {
        AppCenterManager.registraEvento(getResources().getString(R.string.evento_entrou_popup_teimosinha));
        DialogUtils.showDialogListItensNovo(
                SimularApostaActivity.this,
                getResources().getString(R.string.teimosinhas_maiusculo),
                null,
                getString(R.string.teimosinhas_descricao),
                qtdConcursosList,
                "Confirmar",
                "Cancelar",
                onTeimosinhaDialogListener()
        );
    }

    private OnDialogListener onTeimosinhaDialogListener() {
        return new OnDialogListener() {
            @Override
            public void itemSelecionado(int position) {
                    textoBotaoSurpresinhasSelecionado = (String) qtdConcursosList.get(position);
                    qtdConcursoSelecionado = parametroSimulacao.getTeimosinhas().get(position);
            }

            @Override
            public void ok() {
                qtdConcursosButton.setText(textoBotaoSurpresinhasSelecionado);

                if (fragmentManager.findFragmentById(R.id.fragmentSimularApostas) instanceof SurpresinhaFragment) {
                    if (surpresinhaFragment.getParametroJogo() != null) {
                        surpresinhaFragment.atualizaValorTotalAposta(qtdDezenasPossiveisSelecionado, qtdConcursoSelecionado);
                    }
                } else if (fragmentManager.findFragmentById(R.id.fragmentSimularApostas) instanceof TrevosFragment){
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
            surpresinhaTela.setBackgroundResource(R.drawable.icon_surpresinha2);
            simularTelaAposta.setBackgroundResource(R.drawable.icon_seleciona2);
        }else{
            simularTelaAposta.setBackgroundResource(R.drawable.icon_seleciona);
            if (tipoJogo == ModalidadeEnum.TIMEMANIA){
                Drawable ic_surpresinha = ContextCompat.getDrawable(this, R.drawable.icon_surpresinha);
                ViewUtils.setColorDrawable(this, ic_surpresinha, estilo.getCorFonteFundoClaro());
                surpresinhaTela.setBackground(ic_surpresinha);
            } else {
                surpresinhaTela.setBackgroundResource(R.drawable.icon_surpresinha);
            }
        }
    }

    private void configuraTipoApostaTela(String titleActivity, int lightColor, int darkColor, int visibilityToolBarSimularAposta, int visibilityToolBarLoteca) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, lightColor));
        }

        toolBarSimularAposta.setBackgroundColor(ContextCompat.getColor(this, darkColor));
        if (isEspecial && !isMega30 && estilo.getImagemSubCabecalho() > 0) {
            toolBarSimularAposta.setBackground(AppCompatResources.getDrawable(this, estilo.getImagemSubCabecalho()));
        }
        toolBarSimularAposta.setVisibility(visibilityToolBarSimularAposta);
        toolBarLoteca.setVisibility(visibilityToolBarLoteca);

        typeGameColorLight = lightColor;
        typeGameColorDark = darkColor;
    }

    @Override
    public void atualizaDados(CarrinhoDTO carrinho) {
        ViewUtils.setMoedaFormatHtml(carrinho.getValorTotal(), rodapeFragment.getTvValorCarrinho());
        if (carrinho.getApostas() != null) {
            if (carrinho.getApostas().size() <= 999) {
                rodapeFragment.apresentaQtdApostas(true, String.valueOf(carrinho.getApostas().size()));
            } else {
                rodapeFragment.apresentaQtdApostas(true, getString(R.string.mais_999));
            }
        }else {
            rodapeFragment.apresentaQtdApostas(true, getString(R.string.zero));
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

        rodapeFragment.atualizaValorBolao(valorEmReais);
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
        if (parametroSimulacao != null && parametroSimulacao.getConcurso() != null && parametroSimulacao.getConcurso().getModalidade() != null){
            switch (parametroSimulacao.getConcurso().getModalidade()){
                case MAIS_MILIONARIA:
                    paginacao = PaginacaoUtils.getPaginacaoMaisMilionaria(getApplicationContext(),0, R.color.milionaria_claro_mkp);
                    break;
                case TIMEMANIA:
                    paginacao = PaginacaoUtils.getPaginacaoTimemania(getApplicationContext(),R.color.timemania_letra_mkp);
                    break;
                case DIA_DE_SORTE:
                    paginacao = PaginacaoUtils.getPaginacaoDiaDeSorte(getApplicationContext(), R.color.dia_sorte_letra_mkp);
                    break;
            }
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        InputUtils.closeKeyboard(this);
    }
}