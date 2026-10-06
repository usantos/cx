package br.gov.caixa.loterias.apostas.view.features.volantenovo.fragment;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.controllers.PrincipalActivity;
import br.gov.caixa.loterias.apostas.effect.CompletarApostaEffect;
import br.gov.caixa.loterias.apostas.model.bean.SessaoUsuario;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaFavoritaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.Dezena;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroJogoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroPartida;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroSimulacao;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroValorApostaDTO;
import br.gov.caixa.loterias.apostas.model.model.CartelaModel;
import br.gov.caixa.loterias.apostas.model.model.SimularApostaModel;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.ApostaUtils;
import br.gov.caixa.loterias.apostas.utils.Constantes;
import br.gov.caixa.loterias.apostas.utils.DialogUtils;
import br.gov.caixa.loterias.apostas.utils.EtapaAposta;
import br.gov.caixa.loterias.apostas.utils.IntentUtil;
import br.gov.caixa.loterias.apostas.utils.ListaUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.utils.VolanteUtils;
import br.gov.caixa.loterias.apostas.view.adapter.baseadapter.SuperSeteAdapter;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.ListaDezenaRecyclerView;
import br.gov.caixa.loterias.apostas.view.config.DezenaConfig;
import br.gov.caixa.loterias.apostas.view.custom.ExpandableHeightGridView;
import br.gov.caixa.loterias.apostas.view.custom.ExpandableHeightRecyclerView;
import br.gov.caixa.loterias.apostas.view.features.volantenovo.activity.SimulaActivity;
import br.gov.caixa.loterias.apostas.view.features.volantenovo.presentation.SimulaUiEvent;
import br.gov.caixa.loterias.apostas.view.features.volantenovo.presentation.SimulaUiState;
import br.gov.caixa.loterias.apostas.view.holder.DezenaHolder;
import br.gov.caixa.loterias.apostas.view.listener.CartelaFragmentListener;
import br.gov.caixa.loterias.apostas.view.listener.OnItemClickListener;
import br.gov.caixa.loterias.apostas.view.listener.custom.LoteriasSensorEventListener;

public class SimulaFragment extends BaseEtapaFragment {
    private static final int QUANTIDADE_COLUNA_LISTA = 6;
    private static final String QUANTIDADE_COLUNAS = "{qtdColunas}";
    private SensorManager mSensorManager;

    private final SensorEventListener mSensorListener = new LoteriasSensorEventListener(0.00f,
            SensorManager.GRAVITY_EARTH,
            SensorManager.GRAVITY_EARTH).getSensor();

    private List<Dezena> dezenas = new ArrayList<>();
    private List<List<Integer>> matrizSelecionada;
    private HashSet<ParametroPartida> partidasSelecionadas;

    private CartelaFragmentListener CartelaFragmentListener;

    private ParametroJogoDTO parametroJogo;
    private ModalidadeEnum tipoJogo;
    private SimulaActivity parentActivity;
    private AppCompatCheckBox selecioneOutrosNumeros;
    private View view;
    private LinearLayout opcaoOutrosNumerosLayout;
    private ArrayList<ExpandableHeightGridView> listaGridsSuperSete;
    private ArrayList<SuperSeteAdapter> listaAdaptersSuperSete;
    private ExpandableHeightRecyclerView recyclerViewPartida;

    private boolean isAvisou = false;
    public BigDecimal valorTotalAposta;
    public int typeGameColorLight;
    public int typeGameColorDark;
    public int corFonteFundoBranco;
    public int corFonteFundoClaro;
    public int corFonteFundoEscuro;
    public TextView valorAposta;
    public ListaDezenaRecyclerView simularDezenasAdapter;
    public String dataSorteioAtual;

    private SimularApostaModel model;
    private CartelaModel cartelaModel;

    private Context context;

    public SimulaFragment() {
        // Required empty public constructor
    }

    public void atualizaValorTotalAposta(
            int numPrognostico,
            int qtdTeimosinhas
    ) {
        loadParametroJogo();

        if (parametroJogo == null) {
            publicarValorAposta(BigDecimal.ZERO);
            return;
        }

        if (numPrognostico <= 0) {
            publicarValorAposta(BigDecimal.ZERO);
            return;
        }


        if (parametroJogo.getValoresAposta() == null) {
            DialogUtils.dialogEntendiListener(
                    getActivity(),
                    getString(R.string.nao_concluiu_aposta),
                    (dialog, which) -> {
                        Intent intent =
                                IntentUtil.getIntentLimpandoPilhaActivities(
                                        getActivity(),
                                        PrincipalActivity.class
                                );

                        getActivity().startActivity(intent);
                        getActivity().finish();
                    }
            );

            publicarValorAposta(BigDecimal.ZERO);
            return;
        }

        BigDecimal valorCalculado = BigDecimal.ZERO;

        if (parametroJogo.isTipoJogo(ModalidadeEnum.MAIS_MILIONARIA)) {

            ParametroValorApostaDTO parametroValorAposta =
                    parametroJogo.getValorApostaBy(
                            numPrognostico,
                            parametroJogo.getTrevos().getQtdMinima()
                    );

            if (parametroValorAposta != null
                    && parametroValorAposta.getValor() != null) {

                valorCalculado = parametroValorAposta.getValor();
            }

            if (getStateAtual().getQtdConcursoSelecionado() > 0) {
                valorCalculado =
                        valorCalculado.multiply(
                                BigDecimal.valueOf(
                                        getStateAtual().getQtdConcursoSelecionado()
                                )
                        );
            }


            publicarValorAposta(valorCalculado);
            return;
        }

        for (ParametroValorApostaDTO parametro : parametroJogo.getValoresAposta()) {
            if (parametro.getNumeroPrognosticos() == numPrognostico) {
                valorCalculado = parametro.getValor();
                break;
            }
        }

        if ((valorCalculado == null
                || BigDecimal.ZERO.compareTo(valorCalculado) == 0) && !parametroJogo.getValoresAposta().isEmpty()
                    && parametroJogo.getValoresAposta().get(0).getValor() != null) {

                valorCalculado =
                        parametroJogo.getValoresAposta().get(0).getValor();
            }


        if (valorCalculado == null) {
            valorCalculado = BigDecimal.ZERO;
        }

        if (qtdTeimosinhas > 0) {
            valorCalculado =
                    valorCalculado.multiply(
                            BigDecimal.valueOf(qtdTeimosinhas)
                    );
        }

        if (parametroJogo.isTipoJogo(ModalidadeEnum.LOTOMANIA)
                && getStateAtual() != null
                && getStateAtual().isOpcaoOutrosNumerosSelecionada()) {

            valorCalculado =
                    valorCalculado.multiply(
                            Constantes.DOIS_BIG_DECIMAL
                    );
        }

        publicarValorAposta(valorCalculado);
    }

    private void loadParametroJogo() {

        if (parametroJogo != null) {
            return;
        }

        SessaoUsuario sessaoUsuario =
                SessaoUsuario.getInstance();

        ParametroSimulacao parametroSimulacao =
                ViewUtils.getParametroSimulacao(
                        sessaoUsuario,
                        tipoJogo
                );

        if (parametroSimulacao == null) {
            return;
        }

        parametroJogo =
                parametroSimulacao.getParametroJogo();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSensorManager = (SensorManager) this.context.getSystemService(Context.SENSOR_SERVICE);
        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);

        parentActivity = ((SimulaActivity) getActivity());
        identificarTipoJogo();
        if (parentActivity != null) {
            parametroJogo = parentActivity.getParametroSimulacao();
        }

        if (parametroJogo != null && dezenas.isEmpty() && parametroJogo.getPrognosticoMaximo() != null
                && !parametroJogo.isTipoJogo(ModalidadeEnum.SUPER_7)) {
            for (int i = 1; i <= parametroJogo.getPrognosticoMaximo(); i++) {
                if (parametroJogo.isTipoJogo(ModalidadeEnum.LOTOMANIA) && i == 100) {
                    dezenas.add(new Dezena("" + i, Boolean.FALSE, "00"));
                } else {
                    dezenas.add(new Dezena("" + i, Boolean.FALSE, "" + i));
                }
            }
        }

        if (parametroJogo != null && dezenas.isEmpty() && parametroJogo.getPrognosticoMaximo() != null && tipoJogo != ModalidadeEnum.SUPER_7) {
            dezenas = VolanteUtils.getDezenas(tipoJogo, parametroJogo.getPrognosticoMaximo());
        }

        model = new SimularApostaModel(getActivity());
        cartelaModel = new CartelaModel();
        partidasSelecionadas = new HashSet<>();

    }

    @Override
    public void onStart() {
        super.onStart();

        loadParametroJogo();

        if (parametroJogo != null
                && parametroJogo.isTipoJogo(
                ModalidadeEnum.LOTOMANIA
        ))  {

            opcaoOutrosNumerosLayout =
                    parentActivity.findViewById(
                            R.id.opcaoOutrosNumerosLayout
                    );

            selecioneOutrosNumeros =
                    parentActivity.findViewById(
                            R.id.selcioneOutrosNumeros
                    );

            TextView infoSelecioneOutrosNumeros =
                    parentActivity.findViewById(
                            R.id.infoSelecioneOutrosNumeros
                    );

            infoSelecioneOutrosNumeros.setText(
                    ViewUtils.fromHtml(
                            Constantes.INFO_OUTROS_50_NUMEROS
                    )
            );

            onClickOutrosNumerosLayout();
        }
        if(parametroJogo != null && parametroJogo.isTipoJogo(ModalidadeEnum.SUPER_7)) {
                RelativeLayout rlColunasSuperSete = view.findViewById(R.id.rl_colunas_super_7);
                ScrollView svSuperSete = view.findViewById(R.id.sv_super_sete);
                rlColunasSuperSete.setVisibility(View.VISIBLE);
                svSuperSete.setVisibility(View.VISIBLE);
            }


        notificarEstadoCartelaAlterado();

        configuraListaDezenas(
                view
        );

        if (parentActivity.getAposta() != null) {
            editaAposta();
        } else {
            inicializarValorApostaMinima();
            notificarEstadoCartelaAlterado();
        }
    }

    public void onCompletarRodapeClicado() {
        solicitarCompletarAposta();
    }

    public void onAdicionarRodapeClicado() {
        solicitarAdicionarCarrinhoPorBotao();
    }

    protected ApostaFavoritaDTO criarApostaFavoritaDTO() {

        return new ApostaFavoritaDTO();
    }

    @Override
    protected Object getDezenasSelecionadas() {

        if (parametroJogo.isTipoJogo(
                ModalidadeEnum.SUPER_7
        )) {

            return ListaUtils.pegaListaSuperSete(
                    listaAdaptersSuperSete
            );
        }

        return new ArrayList<>(
                getDezenasSelecionadasState()
        );
    }

    private void atualizarQuantidadeSelecionada(
            int quantidade
    ) {
        dispatch(
                new SimulaUiEvent.QuantidadeNumerosAtualizadaInternamente(
                        quantidade
                )
        );
    }

    private void inicializarValorApostaMinima() {

        if (parametroJogo == null || parentActivity == null) {
            publicarValorAposta(BigDecimal.ZERO);
            return;
        }


        int qtdMinima =
                getStateAtual().getQtdDezenasPossiveisSelecionado() > 0
                        ? getStateAtual().getQtdDezenasPossiveisSelecionado()
                        : getQuantidadeMinimaSafe();

        atualizaValorTotalAposta(
                qtdMinima,
                getStateAtual().getQtdConcursoSelecionado()
        );
    }

    private int getQuantidadeMinimaSafe() {

        if (parametroJogo == null
                || parametroJogo.getQuantidadeMinima() == null) {
            return 0;
        }

        return parametroJogo.getQuantidadeMinima();
    }

    public void cartelaFragmentBuilder(Bundle dataBundle) {
        if (dataBundle != null) {
            dataSorteioAtual = dataBundle.getString("dataSorteioAtual");

            typeGameColorLight = dataBundle.getInt("typeGameColorLight");
            typeGameColorDark = dataBundle.getInt("typeGameColorDark");
            corFonteFundoBranco = dataBundle.getInt(SimulaActivity.COR_FONTE_FUNDO_BRANCO);
            corFonteFundoClaro = dataBundle.getInt(SimulaActivity.COR_FONTE_FUNDO_CLARO);
            corFonteFundoEscuro = dataBundle.getInt(SimulaActivity.COR_FONTE_FUNDO_ESCURO);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_cartela_novo, container, false);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onPause() {
        mSensorManager.unregisterListener(mSensorListener);
        super.onPause();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        try {
            CartelaFragmentListener = (CartelaFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context + " must implement CartelaFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.context = null;
        CartelaFragmentListener = null;
    }

    private void identificarTipoJogo() {
        if (parentActivity.getIntent() != null) {
            tipoJogo = (ModalidadeEnum) parentActivity.getIntent().getSerializableExtra("tipoAposta");
        }
    }

    private void solicitarAdicionarCarrinho() {

        IdentificaoDeUmaApostaDas8Modalidades apostaParaCarrinho = null;

        if (!getStateAtual().isEscolhaTimeCoracaoSurpresinha()) {
            apostaParaCarrinho = criarApostaParaCarrinho();
        }

        dispatch(
                new SimulaUiEvent.AdicionarCarrinhoSolicitado(
                        apostaParaCarrinho,
                        getBarraTitulo(),
                        getStateAtual().isEscolhaTimeCoracaoSurpresinha(),
                        getStateAtual().getEquipeSelecionada()
                )
        );
    }

    private List<Integer> getDezenasSelecionadasState() {

        SimulaUiState state = getStateAtual();

        return state.getDezenasSelecionadas();
    }

    private IdentificaoDeUmaApostaDas8Modalidades criarApostaParaCarrinho() {

        if (getStateAtual().isEscolhaTimeCoracaoSurpresinha()) {
            return null;
        }

        if (parametroJogo.isTipoJogo(ModalidadeEnum.SUPER_7)) {
            return ApostaUtils.getApostaSuperSete(
                    parametroJogo,
                    ListaUtils.orderAscDezenas(
                            getDezenasSelecionadasState()
                    ),
                    parentActivity.getValorTotalApostaAtual(),
                    getStateAtual().getQtdConcursoSelecionado(),
                    ListaUtils.pegaListaSuperSete(
                            listaAdaptersSuperSete
                    )
            );
        }

        IdentificaoDeUmaApostaDas8Modalidades aposta =
                getApostaPreenchida();

        SimulaUiState state = getStateAtual();

        if (parametroJogo.isTipoJogo(ModalidadeEnum.LOTOMANIA)
                && state != null
                && state.isOpcaoOutrosNumerosSelecionada()) {

            aposta.setGerarEspelho(true);
        }

        return aposta;
    }

    private IdentificaoDeUmaApostaDas8Modalidades getApostaPreenchida() {
        BigDecimal valorAtual =
                obterValorApostaParaCarrinho();

        if (parametroJogo.isTipoJogo(ModalidadeEnum.DIA_DE_SORTE)) {
            return ApostaUtils.getApostaDiaDeSorte(
                    parametroJogo,
                    getDezenasSelecionadasState(),
                    valorAtual,
                    getStateAtual().getQtdConcursoSelecionado(),
                    parentActivity.getMesDeSorteCartela()
            );
        } else if (parametroJogo.isTipoJogo(ModalidadeEnum.LOTECA)) {
            return ApostaUtils.getApostaLoteca(
                    parametroJogo,
                    getDezenasSelecionadasState(),
                    valorAtual,
                    getStateAtual().getQtdConcursoSelecionado(),
                    partidasSelecionadas
            );
        } else if (parametroJogo.isTipoJogo(ModalidadeEnum.LOTOGOL)) {
            return ApostaUtils.getApostaLotogol(
                    parametroJogo,
                    getDezenasSelecionadasState(),
                    valorAtual,
                    getStateAtual().getQtdConcursoSelecionado(),
                    partidasSelecionadas
            );
        } else if (parametroJogo.isTipoJogo(ModalidadeEnum.TIMEMANIA)) {
            return ApostaUtils.getApostaTimemania(
                    parametroJogo,
                    getDezenasSelecionadasState(),
                    valorAtual,
                    getStateAtual().getQtdConcursoSelecionado(),
                    getStateAtual().getEquipeSelecionada()
            );
        } else {
            return ApostaUtils.getApostaPreenchida(
                    parametroJogo,
                    getDezenasSelecionadasState(),
                    valorAtual,
                    getStateAtual().getQtdConcursoSelecionado()
            );
        }
    }
    private BigDecimal obterValorApostaParaCarrinho() {
        BigDecimal valorAtual =
                parentActivity != null
                        ? parentActivity.getValorTotalApostaAtual()
                        : BigDecimal.ZERO;

        SimulaUiState state = getStateAtual();

        if (parametroJogo != null
                && parametroJogo.isTipoJogo(ModalidadeEnum.LOTOMANIA)
                && state != null
                && state.isOpcaoOutrosNumerosSelecionada()) {

            return valorAtual.divide(BigDecimal.valueOf(2));
        }

        return valorAtual;
    }


    @Override
    public void onViewCreated(
            @NonNull View view,
            Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        viewModel
                .getUiState()
                .observe(
                        getViewLifecycleOwner(),
                        this::render
                );
    }

    private void solicitarCompletarAposta() {

        if (parametroJogo == null || parentActivity == null) {
            return;
        }


        dispatch(
                new SimulaUiEvent.CompletarApostaClicado(
                        parametroJogo.getConcurso().getModalidade(),
                        getDezenasSelecionadasState() != null ? getDezenasSelecionadasState().size() : 0,
                        getStateAtual().getQtdDezenasPossiveisSelecionado()
                )
        );
    }

    private void solicitarAdicionarCarrinhoPorBotao() {

        if (parametroJogo == null || parentActivity == null) {
            return;
        }

        int duplosLoteca = 0;
        int triplosLoteca = 0;

        if (parametroJogo.isTipoJogo(ModalidadeEnum.LOTECA)) {

            duplosLoteca =
                    Integer.parseInt(
                            parentActivity
                                    .getLabelValueDuplasLoteca()
                                    .getText()
                                    .toString()
                    );

            triplosLoteca =
                    Integer.parseInt(
                            parentActivity
                                    .getLabelValueTriplasLoteca()
                                    .getText()
                                    .toString()
                    );
        }

        int qtdPartidasSelecionadas =
                partidasSelecionadas != null
                        ? partidasSelecionadas.size()
                        : 0;

        int qtdPartidasTotal =
                parametroJogo.getPartidas() != null
                        ? parametroJogo.getPartidas().size()
                        : 0;

        boolean equipeSelecionadaValida =
                getStateAtual().getEquipeSelecionada() != null
                        && getStateAtual().getEquipeSelecionada().isSelecionado();

        dispatch(
                new SimulaUiEvent.AdicionarCarrinhoClicado(
                        parametroJogo.getConcurso().getModalidade(),
                        getDezenasSelecionadasState() != null ? getDezenasSelecionadasState().size() : 0,
                        getStateAtual().getQtdDezenasPossiveisSelecionado(),
                        getStateAtual().getQtdTotalSelecionadosSuperSete(),
                        qtdPartidasSelecionadas,
                        qtdPartidasTotal,
                        duplosLoteca,
                        triplosLoteca,
                        getStateAtual().isEscolhaTimeCoracaoSurpresinha(),
                        equipeSelecionadaValida,
                        true
                )
        );
    }

    private void notificarEstadoCartelaAlterado(
            int qtdDezenasPossiveisParaEstado
    ) {

        if (parametroJogo == null || parentActivity == null) {
            return;
        }

        int qtdPartidasSelecionadas =
                partidasSelecionadas != null
                        ? partidasSelecionadas.size()
                        : 0;

        int qtdPartidasTotal =
                parametroJogo.getPartidas() != null
                        ? parametroJogo.getPartidas().size()
                        : 0;

        int duplosLoteca = 0;
        int triplosLoteca = 0;

        if (parametroJogo.isTipoJogo(ModalidadeEnum.LOTECA)) {

            duplosLoteca =
                    getIntTextSafe(
                            parentActivity.getLabelValueDuplasLoteca()
                    );

            triplosLoteca =
                    getIntTextSafe(
                            parentActivity.getLabelValueTriplasLoteca()
                    );
        }

        boolean possuiPlacarLotogol =
                possuiPlacarLotogol();

        boolean equipeSelecionadaValida =
                getStateAtual().getEquipeSelecionada() != null
                        && getStateAtual().getEquipeSelecionada().isSelecionado();

        boolean exibirOutrosNumeros =
                parametroJogo.isTipoJogo(ModalidadeEnum.LOTOMANIA)
                        && getDezenasSelecionadasState().size()
                        == qtdDezenasPossiveisParaEstado;

        boolean outrosNumerosSelecionado =
                selecioneOutrosNumeros != null
                        && selecioneOutrosNumeros.isChecked();

        boolean etapaComplementarCompleta =
                calcularEtapaComplementarCompleta();

        dispatch(
                new SimulaUiEvent.CartelaAlterada(
                        parametroJogo.getConcurso().getModalidade(),
                        getDezenasSelecionadasState() != null
                                ? getDezenasSelecionadasState().size()
                                : 0,
                        qtdDezenasPossiveisParaEstado,
                        getQtdSuperSeteAtual(),
                        qtdPartidasSelecionadas,
                        qtdPartidasTotal,
                        duplosLoteca,
                        triplosLoteca,
                        possuiPlacarLotogol,
                        equipeSelecionadaValida,
                        getStateAtual().isEscolhaTimeCoracaoSurpresinha(),
                        outrosNumerosSelecionado,
                        exibirOutrosNumeros,
                        etapaComplementarCompleta,
                        getDezenasSelecionadasState()
                )
        );
    }

    private boolean calcularEtapaComplementarCompleta() {
        if (parametroJogo == null) {
            return false;
        }

        if (parametroJogo.isTipoJogo(ModalidadeEnum.TIMEMANIA)) {
            return getStateAtual().getEquipeSelecionada() != null
                    && getStateAtual().getEquipeSelecionada().isSelecionado();
        }

        if (parametroJogo.isTipoJogo(ModalidadeEnum.DIA_DE_SORTE)) {
            return parentActivity != null
                    && parentActivity.getMesDeSorteCartela() != null;
        }

        return false;
    }

    private int getIntTextSafe(
            TextView textView
    ) {

        if (textView == null || textView.getText() == null) {
            return 0;
        }

        try {
            return Integer.parseInt(
                    textView.getText().toString()
            );
        } catch (Exception e) {
            return 0;
        }
    }

    private boolean possuiPlacarLotogol() {

        if (parametroJogo == null
                || parametroJogo.getPartidas() == null) {
            return false;
        }

        if (!parametroJogo.isTipoJogo(ModalidadeEnum.LOTOGOL)) {
            return false;
        }

        for (ParametroPartida partida : parametroJogo.getPartidas()) {

            boolean possuiPlacarEquipe1 =
                    partida.getEquipe1() != null
                            && StringUtils.isNotEmpty(
                            partida.getEquipe1().getPlacar()
                    );

            boolean possuiPlacarEquipe2 =
                    partida.getEquipe2() != null
                            && StringUtils.isNotEmpty(
                            partida.getEquipe2().getPlacar()
                    );

            if (possuiPlacarEquipe1 || possuiPlacarEquipe2) {
                return true;
            }
        }

        return false;
    }

    private void render(
            SimulaUiState state
    ) {
        if (state == null) {
            return;
        }

        renderLimpezaAposta(state);
        renderCompletarAposta(state);
    }

    private void renderCompletarAposta(
            SimulaUiState state
    ) {

        if (state.getComandoCompletarAposta() == null) {
            return;
        }

        if (state.getComandoCompletarAposta()
                instanceof CompletarApostaEffect.PreencherNumerosAleatorios) {

            preencheNumerosAleatorios();
            consumirComandoCompletarAposta();
            return;
        }

        if (state.getComandoCompletarAposta()
                instanceof CompletarApostaEffect.PreencherSuperSete) {

            preencheNumerosAleatoriosSuperSete(
                    ListaUtils.pegaListaSuperSete(listaAdaptersSuperSete)
            );

            consumirComandoCompletarAposta();
            return;
        }

        if (state.getComandoCompletarAposta()
                instanceof CompletarApostaEffect.ApostaEffectProntaParaAdicionar) {

            consumirComandoCompletarAposta();

            solicitarAdicionarCarrinho();


            return;
        }

        if (state.getComandoCompletarAposta()
                instanceof CompletarApostaEffect.AbrirMesDeSorte) {

            parentActivity.mesDeSorteFragment(
                    getDezenasSelecionadasState()
            );

            consumirComandoCompletarAposta();
            return;
        }

        if (state.getComandoCompletarAposta()
                instanceof CompletarApostaEffect.AbrirTrevos) {

            parentActivity.trevosFragment(
                    getDezenasSelecionadasState()
            );

            consumirComandoCompletarAposta();
            return;
        }

        if (state.getComandoCompletarAposta()
                instanceof CompletarApostaEffect.MostrarEscolhaTimeCoracao) {

            parentActivity.timesFragment(
                    getDezenasSelecionadasState()
            );

            consumirComandoCompletarAposta();
            return;
        }

        if (state.getComandoCompletarAposta()
                instanceof CompletarApostaEffect.AdicionarTimeSurpresa) {

            parentActivity.adicionarTimeSurpresa(
                    getStateAtual().getEquipeSelecionada()
            );

            consumirComandoCompletarAposta();
        }

    }

    private void consumirComandoCompletarAposta() {
        dispatch(
                new SimulaUiEvent.ComandoCompletarApostaConsumido()
        );
    }

    private void renderLimpezaAposta(
            SimulaUiState state
    ) {

        boolean executouLimpeza = false;

        if (state.isLimparNumerosCartela()) {
            renderLimparNumerosCartela(state);
            executouLimpeza = true;
        }

        if (state.isLimparSuperSete()) {
            renderLimparSuperSete();
            executouLimpeza = true;
        }

        if (state.isLimparLoteca()) {
            renderLimparLoteca();
            executouLimpeza = true;
        }
        if (state.isLimparLotogol()) {
            renderLimparLotogol();
            executouLimpeza = true;
        }


        if (state.isLimparOpcaoOutrosNumeros()) {
            renderLimparOpcaoOutrosNumeros();
            executouLimpeza = true;
        }

        if (executouLimpeza) {
            dispatch(
                    new SimulaUiEvent.LimpezaApostaRenderizada()
            );
        }
    }

    private void renderLimparNumerosCartela(
            SimulaUiState state
    ) {

        if (simularDezenasAdapter != null) {

            simularDezenasAdapter.atualizaSelecionados(
                    state.getDezenasSelecionadas()
            );
        }
    }

    private void renderLimparSuperSete(
    ) {
        if (listaGridsSuperSete != null) {
            for (ExpandableHeightGridView grid : listaGridsSuperSete) {
                if (grid.getAdapter() instanceof SuperSeteAdapter superSeteAdapter) {
                    superSeteAdapter.limpaNumeros();
                }
            }
        }
    }

    private void renderLimparLoteca() {

        if (parametroJogo != null && parametroJogo.getPartidas() != null) {
            for (ParametroPartida partida : parametroJogo.getPartidas()) {
                partida.setEmpate(false);
                partida.getEquipe1().setSelecionado(false);
                partida.getEquipe2().setSelecionado(false);
                partida.setSelecionado(false);
                partida.setQtdItensSelecionadosAnterior(0);
                partida.setQtdItensSelecionados(0);
            }
        }

        if (recyclerViewPartida != null
                && recyclerViewPartida.getAdapter() != null) {

            recyclerViewPartida.getAdapter().notifyDataSetChanged();
        }

        if (partidasSelecionadas != null) {
            partidasSelecionadas.clear();
        }

    }

    private void renderLimparLotogol() {

        if (parametroJogo != null && parametroJogo.getPartidas() != null) {
            for (ParametroPartida partida : parametroJogo.getPartidas()) {
                partida.setSelecionado(false);

                if (partida.getEquipe1() != null) {
                    partida.getEquipe1().setPlacar(StringUtils.EMPTY);
                }

                if (partida.getEquipe2() != null) {
                    partida.getEquipe2().setPlacar(StringUtils.EMPTY);
                }
            }
        }

        if (recyclerViewPartida != null && recyclerViewPartida.getAdapter() != null) {
            recyclerViewPartida.getAdapter().notifyDataSetChanged();
        }

        if (partidasSelecionadas != null) {
            partidasSelecionadas.clear();
        }
    }

    private void renderLimparOpcaoOutrosNumeros() {

        if (parametroJogo == null) {
            return;
        }

        if (!parametroJogo.isTipoJogo(ModalidadeEnum.LOTOMANIA)) {
            return;
        }

        if (opcaoOutrosNumerosLayout == null || selecioneOutrosNumeros == null) {
            return;
        }

        opcaoOutrosNumerosLayout.setVisibility(View.GONE);
        selecioneOutrosNumeros.setChecked(false);
    }

    private void publicarValorAposta(
            BigDecimal valor
    ) {

        BigDecimal valorSeguro =
                valor != null
                        ? valor
                        : BigDecimal.ZERO;

        valorTotalAposta = valorSeguro;

        dispatch(
                new SimulaUiEvent.ValorApostaAlterado(
                        valorSeguro
                )
        );
    }

    private void onClickOutrosNumerosLayout() {
        opcaoOutrosNumerosLayout.setOnClickListener(v -> {
            dispatch(
                    new SimulaUiEvent.ToggleOutrosNumeros()
            );

            atualizarValorENotificarEstadoCartela();
        });
    }

    private void preencheNumerosAleatorios() {
        AlertDialogUtils.show(getActivity());
        List<Integer> selecionadas = getDezenasSelecionadasState();
        selecionadas = ListaUtils.orderAscDezenas(selecionadas);
        model.preencheNumerosAleatorios(getStateAtual().getQtdDezenasPossiveisSelecionado(), selecionadas, parametroJogo.getPrognosticoMaximo(),
                new OnSilceListener<List<Integer>>() {
                    @Override
                    public void success(List<Integer> list) {
                        AlertDialogUtils.dismiss();
                        simularDezenasAdapter.atualizaSelecionados(list);
                        atualizarDezenasSelecionadas(list);
                        atualizarValorENotificarEstadoCartela();
                    }

                    @Override
                    public void error(VolleyError error) {
                        AlertDialogUtils.dismiss();
                    }
                });
    }

    private void preencheNumerosAleatoriosSuperSete(ArrayList<ArrayList<Integer>> listaInteiros) {
        AlertDialogUtils.show(getActivity());
        atualizarDezenasSelecionadas(ListaUtils.orderAscDezenas(getDezenasSelecionadasState()));
        model.preencheNumerosAleatoriosSuperSete(getStateAtual().getQtdDezenasPossiveisSelecionado(), listaInteiros, parametroJogo.getConcurso().getModalidade().name(),
                new OnSilceListener<List<List<Integer>>>() {
                    @Override
                    public void success(List<List<Integer>> payload) {
                        AlertDialogUtils.dismiss();

                        preencheSuperSete(payload);
                        atualizarValorENotificarEstadoCartela();
                    }

                    @Override
                    public void error(VolleyError error) {
                        AlertDialogUtils.dismiss();
                    }
                });
    }

    private int getQtdTotalSelecionadosSuperSete(ArrayList<ArrayList<Integer>> listaInteiros) {
        int qtdTotal = 0;
        for (ArrayList<Integer> listaSelecionados : listaInteiros) {
            qtdTotal += listaSelecionados.size();
        }
        return qtdTotal;
    }

    private void preencheSuperSete(List<List<Integer>> numerosSelecionados) {
        int coluna = 0;
        if (matrizSelecionada != null) {
            matrizSelecionada = null;
        }
        for (SuperSeteAdapter adapter : listaAdaptersSuperSete) {
            adapter.dezenasSelecionadas = numerosSelecionados.get(coluna);
            adapter.notifyDataSetChanged();
            coluna++;
        }
    }

    private void atualizarValorENotificarEstadoCartela() {

        if (parametroJogo == null || parentActivity == null) {
            return;
        }

        int qtdPossivelParaEstado =
                getStateAtual().getQtdDezenasPossiveisSelecionado();


        int qtdParaCalculo =
                    qtdPossivelParaEstado > 0
                            ? qtdPossivelParaEstado
                            : getQuantidadeMinimaSafe();

        atualizaValorTotalAposta(
                    qtdParaCalculo,
                    getStateAtual().getQtdConcursoSelecionado()
            );


        atualizarQuantidadeSelecionada(
                qtdPossivelParaEstado);

        notificarEstadoCartelaAlterado(
                qtdPossivelParaEstado
        );
    }

    private void notificarEstadoCartelaAlterado() {

        if (parentActivity == null) {
            return;
        }

        notificarEstadoCartelaAlterado(
                getStateAtual().getQtdDezenasPossiveisSelecionado()
        );
    }

    private boolean mostraLimparSuperSete() {
        if (listaGridsSuperSete != null) {
            for (ExpandableHeightGridView grid : listaGridsSuperSete) {
                if (!((SuperSeteAdapter) grid.getAdapter()).dezenasSelecionadas.isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }


    private ListaDezenaRecyclerView getCorFonte(ModalidadeEnum modalidade){
        if(modalidade == ModalidadeEnum.TIMEMANIA){
            return new ListaDezenaRecyclerView(
                    dezenas,
                    getDezenasSelecionadasState(),
                    new DezenaConfig(
                            true,
                            corFonteFundoBranco,
                            R.color.timemania_letra_mkp,
                            true
                    ),
                    onItemClickListener());
        }
        else if(modalidade == ModalidadeEnum.DIA_DE_SORTE){
                return new ListaDezenaRecyclerView(
                        dezenas,
                        getDezenasSelecionadasState(),
                        new DezenaConfig(
                                true,
                                corFonteFundoBranco,
                                corFonteFundoClaro,
                                true
                        ),
                        onItemClickListener());
        }
        else{
            return new ListaDezenaRecyclerView(dezenas, getDezenasSelecionadasState(),
                    corFonteFundoBranco, onItemClickListener());
        }
    }

    private void configuraListaDezenas(View fragmentView) {
        RecyclerView listaDezenas = fragmentView.findViewById(R.id.listaDezenas);
        recyclerViewPartida = fragmentView.findViewById(R.id.recyclerViewPartida);

        if (dezenas != null && !dezenas.isEmpty() && !parametroJogo.isTipoJogo(ModalidadeEnum.SUPER_7)) {
            simularDezenasAdapter = getCorFonte(parametroJogo.getConcurso().getModalidade());
            listaDezenas.setAdapter(simularDezenasAdapter);

            RecyclerView.LayoutManager layoutDezenas = new GridLayoutManager(context, QUANTIDADE_COLUNA_LISTA);
            listaDezenas.setLayoutManager(layoutDezenas);
            listaDezenas.setNestedScrollingEnabled(false);
        } else {
            if (parametroJogo.isTipoJogo(ModalidadeEnum.SUPER_7)) {
                configuraSuperSete(fragmentView);
            }
        }
    }

    @NotNull
    private OnItemClickListener<DezenaHolder> onItemClickListener() {

        return (holder, position) -> {
            try {
                if (parametroJogo == null
                        || parentActivity == null
                        || dezenas == null
                        || position < 0
                        || position >= dezenas.size()) {
                    return;
                }

                Dezena dezena =
                        dezenas.get(position);

                if (dezena == null
                        || dezena.getValue() == null) {
                    return;
                }

                Integer valorSelecionado =
                        Integer.valueOf(
                                dezena.getValue()
                        );

                boolean estavaSelecionada =
                        getDezenasSelecionadasState().contains(
                                valorSelecionado
                        );

                if (estavaSelecionada) {

                    removerDezenaSelecionada(
                            dezena,
                            valorSelecionado
                    );

                } else {

                    tentarAdicionarDezenaSelecionada(
                            dezena,
                            valorSelecionado
                    );
                }


                if (simularDezenasAdapter != null) {
                    simularDezenasAdapter.atualizaSelecionados(
                            getDezenasSelecionadasState()
                    );
                }

                atualizarValorENotificarEstadoCartela();

            } catch (Exception e) {
                Log.d(
                        "ERRO_ITEM_CLICK",
                        e.getLocalizedMessage() != null
                                ? e.getLocalizedMessage()
                                : "Erro sem mensagem"
                );
            }
        };
    }

    private void atualizarDezenasSelecionadas(
            List<Integer> novasDezenas
    ) {

        int qtdDezenasPossiveis =
                getStateAtual()
                        .getQtdDezenasPossiveisSelecionado();

        int qtdPartidasSelecionadas =
                partidasSelecionadas != null
                        ? partidasSelecionadas.size()
                        : 0;

        int qtdPartidasTotal =
                parametroJogo.getPartidas() != null
                        ? parametroJogo.getPartidas().size()
                        : 0;

        int duplosLoteca = 0;
        int triplosLoteca = 0;

        if (parametroJogo.isTipoJogo(
                ModalidadeEnum.LOTECA
        )) {

            duplosLoteca =
                    getIntTextSafe(
                            parentActivity
                                    .getLabelValueDuplasLoteca()
                    );

            triplosLoteca =
                    getIntTextSafe(
                            parentActivity
                                    .getLabelValueTriplasLoteca()
                    );
        }

        boolean equipeSelecionadaValida =
                getStateAtual()
                        .getEquipeSelecionada() != null
                        && getStateAtual()
                        .getEquipeSelecionada()
                        .isSelecionado();

        boolean outrosNumerosSelecionado =
                selecioneOutrosNumeros != null
                        && selecioneOutrosNumeros.isChecked();

        boolean exibirOutrosNumeros =
                parametroJogo.isTipoJogo(
                        ModalidadeEnum.LOTOMANIA
                )
                        && novasDezenas.size()
                        == qtdDezenasPossiveis;

        boolean etapaComplementarCompleta =
                calcularEtapaComplementarCompleta();

        dispatch(
                new SimulaUiEvent.CartelaAlterada(
                        parametroJogo
                                .getConcurso()
                                .getModalidade(),

                        novasDezenas.size(),

                        qtdDezenasPossiveis,

                        getQtdSuperSeteAtual(),

                        qtdPartidasSelecionadas,

                        qtdPartidasTotal,

                        duplosLoteca,

                        triplosLoteca,

                        possuiPlacarLotogol(),

                        equipeSelecionadaValida,

                        getStateAtual().isEscolhaTimeCoracaoSurpresinha(),

                        outrosNumerosSelecionado,

                        exibirOutrosNumeros,

                        etapaComplementarCompleta,

                        new ArrayList<>(novasDezenas)
                )
        );
    }

    private void removerDezenaSelecionada(
            Dezena dezena,
            Integer valorSelecionado
    ) {

        List<Integer> selecionadas = getDezenasSelecionadasState();
        selecionadas.remove(valorSelecionado);
        dezena.setSelected(false);
        atualizarDezenasSelecionadas(selecionadas);

        int quantidadeMinima =
                parametroJogo.getQuantidadeMinima() != null
                        ? parametroJogo.getQuantidadeMinima()
                        : 0;

        atualizarQuantidadeSelecionada(
                Math.max(
                        getDezenasSelecionadasState().size(),
                        quantidadeMinima
                ));

    }

    private void tentarAdicionarDezenaSelecionada(
            Dezena dezena,
            Integer valorSelecionado
    ) {

        int quantidadeAtualSelecionada =
                getDezenasSelecionadasState().size();

        int quantidadePossivelAtual =
                getStateAtual().getQtdDezenasPossiveisSelecionado();

        int quantidadeMaxima =
                parametroJogo.getQuantidadeMaxima();

        if (quantidadeAtualSelecionada < quantidadePossivelAtual) {

            adicionarDezena(
                    dezena,
                    valorSelecionado
            );

            return;
        }

        if (quantidadePossivelAtual < quantidadeMaxima) {

            atualizarQuantidadeSelecionada(
                    CartelaFragmentListener.verificaNovoQtdDezenasMax(
                            quantidadePossivelAtual
                    ));

            adicionarDezena(
                    dezena,
                    valorSelecionado
            );

            if (!isAvisou) {
                DialogUtils.dialogEntendi(
                        getActivity(),
                        getString(R.string.msg_qtd_max_ultrapassada)
                );

                isAvisou = true;
            }

            return;
        }

        dezena.setSelected(false);

        DialogUtils.dialogEntendi(
                getActivity(),
                getString(
                        R.string.label_nao_possivel_selecionar_quantidade_numeros_superior_limite_modalidade_loterica
                )
        );
    }

    private void adicionarDezena(
            Dezena dezena,
            Integer valorSelecionado
    ) {
        List<Integer> dezenasSelecionadas = getDezenasSelecionadasState();

        dezenasSelecionadas.add(
                valorSelecionado
        );
        atualizarDezenasSelecionadas(dezenasSelecionadas);
        dezena.setSelected(true);
    }

    private void atualizarQtdSuperSeteState() {
        dispatch(
                new SimulaUiEvent.CartelaAlterada(
                        parametroJogo.getConcurso().getModalidade(),
                        getDezenasSelecionadasState().size(),
                        getStateAtual().getQtdDezenasPossiveisSelecionado(),
                        getQtdSuperSeteAtual(),
                        partidasSelecionadas != null
                                ? partidasSelecionadas.size()
                                : 0,
                        parametroJogo.getPartidas() != null
                                ? parametroJogo.getPartidas().size()
                                : 0,
                        0,
                        0,
                        possuiPlacarLotogol(),
                        getStateAtual().getEquipeSelecionada() != null,
                        getStateAtual().isEscolhaTimeCoracaoSurpresinha(),
                        false,
                        false,
                        calcularEtapaComplementarCompleta(),
                        getDezenasSelecionadasState()
                )
        );
    }

    private void configuraSuperSete(View viewLayout) {
        listaGridsSuperSete = cartelaModel.getListGridSuperSete(viewLayout);

        listaAdaptersSuperSete = cartelaModel.getListAdaptersSuperSete(typeGameColorLight, parentActivity);
        for (ExpandableHeightGridView grid : listaGridsSuperSete) {
            grid.setExpanded(true);
        }
        cartelaModel.preencheGridComListaSuperSete(listaGridsSuperSete, listaAdaptersSuperSete);

        for (ExpandableHeightGridView listaNumeros : listaGridsSuperSete) {
            listaNumeros.setVisibility(View.VISIBLE);
            listaNumeros.setOnItemClickListener((adapterView, view, position, l) -> {
                SuperSeteAdapter adapter = ((SuperSeteAdapter) listaNumeros.getAdapter());
                Dezena dezena = adapter.dezenas.get(position);
                ArrayList<Integer> listaSelecionados = (ArrayList<Integer>) adapter.dezenasSelecionadas;
                Integer valorSelecionado = Integer.valueOf(dezena.getValue());

                if (listaSelecionados.contains(valorSelecionado)) {
                    if (listaSelecionados.size() == adapter.getQtdColunasMaximas(getStateAtual().getQtdDezenasPossiveisSelecionado())) {
                        listaSelecionados.remove(valorSelecionado);
                        atualizarQtdSuperSeteState();
                        if (getStateAtual().getQtdTotalSelecionadosSuperSete() >= parametroJogo.getQuantidadeMinima()) {
                            atualizarQuantidadeSelecionada(getStateAtual().getQtdDezenasPossiveisSelecionado() - 1);
                            int novaQuantidade =
                                    parentActivity.verificaNovoQtdDezenasMax(
                                            getStateAtual().getQtdTotalSelecionadosSuperSete()
                                    );

                            atualizarQuantidadeSelecionada(
                                    novaQuantidade
                            );
                        }
                        atualizarValorENotificarEstadoCartela();
                    } else {
                        DialogUtils.dialogEntendi(getActivity(),
                                getString(R.string.msg_bloqueio_coluna_super_sete_desmarcar).replace(
                                        QUANTIDADE_COLUNAS,
                                        String.valueOf(listaSelecionados.size())));
                    }
                } else {
                    int maxNumerosParaSelecionar = parametroJogo.getQuantidadeMaxima();
                    if (getStateAtual().getQtdTotalSelecionadosSuperSete() == getStateAtual().getQtdDezenasPossiveisSelecionado()) {
                        // VERIFICA SE TEM OPÇÃO DE SELECIONAR MAIS DEZENAS

                        int quantidadeAtual =
                                getStateAtual()
                                        .getQtdDezenasPossiveisSelecionado();

                        int novaQuantidade =
                                CartelaFragmentListener
                                        .verificaNovoQtdDezenasMax(
                                                quantidadeAtual
                                        );

                        if (quantidadeAtual < maxNumerosParaSelecionar) {
                            if (listaSelecionados.size() < SuperSeteAdapter.QTD_MAX_POR_COLUNA) {
                                if (listaSelecionados.size() < adapter.getQtdColunasMaximas((novaQuantidade))) {
                                    atualizarQuantidadeSelecionada(novaQuantidade);
                                    atualizarValorENotificarEstadoCartela();
                                    listaSelecionados.add(valorSelecionado);
                                    atualizarQtdSuperSeteState();

                                    if ((getStateAtual().getQtdTotalSelecionadosSuperSete() == (getStateAtual().getQtdDezenasPossiveisSelecionado())) && (!isAvisou)) {
                                        DialogUtils.dialogEntendi(getActivity(), getString(R.string.msg_qtd_max_ultrapassada));
                                        isAvisou = true;
                                    }
                                } else {
                                    DialogUtils.dialogEntendi(getActivity(), getString(R.string.msg_bloqueio_coluna_super_sete_marcar).replace(
                                            QUANTIDADE_COLUNAS,
                                            String.valueOf(adapter.getQtdColunasMaximas(getStateAtual().getQtdDezenasPossiveisSelecionado()))));
                                }
                            } else {
                                DialogUtils.dialogEntendi(getActivity(), getString(R.string.msg_qtd_max_col_super_sete));
                            }
                        } else {
                            DialogUtils.dialogEntendi(getActivity(), getString(R.string.label_nao_possivel_selecionar_quantidade_numeros_superior_limite_modalidade_loterica));
                        }
                    } else {
                        if (listaSelecionados.size() < SuperSeteAdapter.QTD_MAX_POR_COLUNA) {
                            if (listaSelecionados.size() < adapter.getQtdColunasMaximas(getStateAtual().getQtdDezenasPossiveisSelecionado())) {
                                listaSelecionados.add(valorSelecionado);
                                atualizarQtdSuperSeteState();
                            } else {
                                DialogUtils.dialogEntendi(getActivity(), getString(R.string.msg_bloqueio_coluna_super_sete_marcar).replace(
                                        QUANTIDADE_COLUNAS,
                                        String.valueOf(adapter.getQtdColunasMaximas(getStateAtual().getQtdDezenasPossiveisSelecionado()))));
                            }
                        } else {
                            DialogUtils.dialogEntendi(getActivity(), getString(R.string.msg_qtd_max_col_super_sete));

                        }

                    }
                }

                atualizarValorENotificarEstadoCartela();
                adapter.notifyDataSetChanged();
            });
        }
    }

    private void editaAposta() {

        if (parentActivity.getAposta() == null) {
            return;
        }

        IdentificaoDeUmaApostaDas8Modalidades aposta =
                parentActivity.getAposta();

        IdentificaoDeUmaApostaDas8Modalidades apostaSuperSete =
                parentActivity.getAposta();

        if (parentActivity.getAposta().getModalidade() == ModalidadeEnum.SUPER_7) {

            int qtdSuperSete =
                    getQtdTotalSelecionadosSuperSete(
                            apostaSuperSete.getMatrizNumerosSelecionados()
                    );
            dispatch(
                    new SimulaUiEvent.CartelaAlterada(
                            parametroJogo.getConcurso().getModalidade(),
                            0,
                            apostaSuperSete.getQuantidadeNumeros(),
                            qtdSuperSete,
                            0,
                            0,
                            0,
                            0,
                            false,
                            false,
                            false,
                            false,
                            false,
                            false,
                            getDezenasSelecionadasState()
                    )
            );

            configuraSuperSete(view);

            matrizSelecionada = new ArrayList<>();
            matrizSelecionada.addAll(
                    apostaSuperSete.getMatrizNumerosSelecionados()
            );

            atualizarQuantidadeSelecionada(
                    apostaSuperSete.getQuantidadeNumeros());

            atualizarQtdConcurso(apostaSuperSete.getQuantidadeTeimosinhas());
            preencheSuperSete(matrizSelecionada);
            publicarValorAposta(aposta.getValor());

            Button botaoLimpar =
                    getBotaoLimparAposta();

            if (botaoLimpar != null) {
                botaoLimpar.setEnabled(mostraLimparSuperSete());
            }

            return;
        }

        if (CollectionUtils.isNotEmpty(aposta.getListaNumerosSelecionados())) {

            List<Integer> selecionadas = aposta.getListaNumerosSelecionados();
            atualizarDezenasSelecionadas(selecionadas);

            simularDezenasAdapter.atualizaSelecionados(
                    selecionadas
            );

            atualizarQuantidadeSelecionada(
                    aposta.getQuantidadeNumeros());

            atualizarQtdConcurso(aposta.getQuantidadeTeimosinhas());


            if (aposta.getTimeDoCoracao() != null) {

                dispatch(
                        new SimulaUiEvent
                                .SelecionarTimeCoracao(
                                aposta.getTimeDoCoracao()
                        )
                );
            }

        }

        publicarValorAposta(
                aposta.getValor()
        );

        notificarEstadoCartelaAlterado();
    }

    private void atualizarQtdConcurso(
            int quantidade
    ) {
        dispatch(
                new SimulaUiEvent
                        .QtdConcursoAtualizadaInternamente(
                        quantidade
                )
        );
    }

    private Button getBotaoLimparAposta() {
        return parentActivity != null
                ? parentActivity.getBotaoLimparAposta()
                : null;
    }

    @Override
    public void onLimparRodapeClicado() {
        dispatch(
                new SimulaUiEvent.LimparApostaSolicitado()
        );
    }

    @Override
    public boolean possuiSelecaoValida() {
        return getStateAtual().isBotaoAdicionarHabilitado();
    }

    @Override protected boolean suportaApostaShake() { return true; }

    @Override protected boolean apostaShakeCompleta() {
        return getStateAtual().isBotaoAdicionarHabilitado();
    }

    @Override protected void preencherApostaShake(boolean renovar) {
        if (renovar) {
            if (tipoJogo != ModalidadeEnum.SUPER_7 && parametroJogo != null) {
                List<Integer> nova = br.gov.caixa.loterias.apostas.utils.shake.ApostaShakeRandom.numeros(
                        getStateAtual().getQtdDezenasPossiveisSelecionado(),
                        getDezenasSelecionadasState(), 1,
                        parametroJogo.getPrognosticoMaximo(), true, new java.util.Random());
                onLimparRodapeClicado();
                simularDezenasAdapter.atualizaSelecionados(nova);
                atualizarDezenasSelecionadas(nova);
                atualizarValorENotificarEstadoCartela();
                return;
            }
            preencheNumerosAleatorios();
        } else {
            solicitarCompletarAposta();
        }
    }

    @Override
    public EtapaAposta getEtapa() {
        return EtapaAposta.NUMEROS;
    }
    private int getQtdSuperSeteAtual() {

        if (listaAdaptersSuperSete == null) {
            return getStateAtual().getQtdTotalSelecionadosSuperSete();
        }

        return getQtdTotalSelecionadosSuperSete(
                ListaUtils.pegaListaSuperSete(
                        listaAdaptersSuperSete
                )
        );
    }

}
