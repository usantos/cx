package br.gov.caixa.loterias.apostas.view.fragment;

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
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.controllers.PrincipalActivity;
import br.gov.caixa.loterias.apostas.effect.CompletarApostaEffect;
import br.gov.caixa.loterias.apostas.event.SimulaUiEvent;
import br.gov.caixa.loterias.apostas.model.bean.SessaoUsuario;
import br.gov.caixa.loterias.apostas.model.bo.EscudoBO;
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
import br.gov.caixa.loterias.apostas.state.SimulaUiState;
import br.gov.caixa.loterias.apostas.utils.ApostaUtils;
import br.gov.caixa.loterias.apostas.utils.Constantes;
import br.gov.caixa.loterias.apostas.utils.DialogUtils;
import br.gov.caixa.loterias.apostas.utils.EtapaAposta;
import br.gov.caixa.loterias.apostas.utils.IntentUtil;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.utils.VolanteUtils;
import br.gov.caixa.loterias.apostas.view.activity.LotecaActivity;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.ListaDezenaRecyclerView;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.LotecaAdapter;
import br.gov.caixa.loterias.apostas.view.config.DezenaConfig;
import br.gov.caixa.loterias.apostas.view.custom.ExpandableHeightRecyclerView;
import br.gov.caixa.loterias.apostas.view.holder.DezenaHolder;
import br.gov.caixa.loterias.apostas.view.listener.CartelaFragmentListener;
import br.gov.caixa.loterias.apostas.view.listener.LocateAdapterListener;
import br.gov.caixa.loterias.apostas.view.listener.LotogolListener;
import br.gov.caixa.loterias.apostas.view.listener.OnItemClickListener;
import br.gov.caixa.loterias.apostas.view.listener.custom.LoteriasSensorEventListener;
import br.gov.caixa.loterias.apostas.viewModel.LotecaViewModel;

public class LotecaFragment extends BaseEtapaFragment implements LocateAdapterListener, LotogolListener {
    private static final int QUANTIDADE_COLUNA_LISTA = 6;
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
    private LotecaActivity parentActivity;
    private View view;
    private ExpandableHeightRecyclerView recyclerViewPartida;

    private boolean isAvisou = false;
    public BigDecimal valorTotalAposta;
    public int typeGameColorLight, typeGameColorDark;
    public int corFonteFundoBranco, corFonteFundoClaro, corFonteFundoEscuro;
    public TextView valorAposta, valorTotalJogos, valorTotalSimples, valorTotalDuplas, valorTotalTriplas;
    public ListaDezenaRecyclerView simularDezenasAdapter;
    public String dataSorteioAtual;

    private SimularApostaModel model;
    private CartelaModel cartelaModel;

    private Context context;

    public LotecaFragment() {
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

        if (valorCalculado == null
                || BigDecimal.ZERO.compareTo(valorCalculado) == 0) {

            if (!parametroJogo.getValoresAposta().isEmpty()
                    && parametroJogo.getValoresAposta().get(0).getValor() != null) {

                valorCalculado =
                        parametroJogo.getValoresAposta().get(0).getValor();
            }
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
        viewModel = new ViewModelProvider(
                requireActivity()
        ).get(LotecaViewModel.class);

        parentActivity = ((LotecaActivity) getActivity());
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

        if (parametroJogo != null && dezenas.isEmpty() && parametroJogo.getPrognosticoMaximo() != null && !(tipoJogo == ModalidadeEnum.SUPER_7)) {
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

        if (parentActivity != null && parametroJogo != null) {
            if (parametroJogo.isTipoJogo(ModalidadeEnum.LOTECA) || parametroJogo.isTipoJogo(ModalidadeEnum.LOTOGOL)) {
//                configuraBotoesLotecaOuLotogol();
            }
        }

        if (parametroJogo.isTipoJogo(ModalidadeEnum.SUPER_7)) {
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

        if (parametroJogo.isTipoJogo(ModalidadeEnum.LOTECA)) {
            atualizaValorApostaLoteca();
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
            corFonteFundoBranco = dataBundle.getInt(LotecaActivity.COR_FONTE_FUNDO_BRANCO);
            corFonteFundoClaro = dataBundle.getInt(LotecaActivity.COR_FONTE_FUNDO_CLARO);
            corFonteFundoEscuro = dataBundle.getInt(LotecaActivity.COR_FONTE_FUNDO_ESCURO);
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
        return ApostaUtils.getApostaLoteca(
                parametroJogo,
                getDezenasSelecionadasState(),
                valorAtual,
                getStateAtual().getQtdConcursoSelecionado(),
                partidasSelecionadas
        );
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

    public void zeraLoteca() {
        limparPartidadasSelecionasLoteca();
        atualizarValorENotificarEstadoCartela();
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
                    getStateAtual().getDuplasLoteca();

            triplosLoteca =
                   getStateAtual().getTriplasLoteca();
        }

        int qtdPartidasSelecionadas =
                getStateAtual().getJogosLoteca();

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

            duplosLoteca = getStateAtual().getDuplasLoteca();

            triplosLoteca = getStateAtual().getTriplasLoteca();
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
               false;

        boolean etapaComplementarCompleta =
                calcularEtapaComplementarCompleta();

        dispatch(
                new SimulaUiEvent.CartelaAlterada(
                        parametroJogo.getConcurso().getModalidade(),
                        getDezenasSelecionadasState() != null
                                ? getDezenasSelecionadasState().size()
                                : 0,
                        qtdDezenasPossiveisParaEstado,
                        0,
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

            preencherPartidasAleatoriasLoteca();
            consumirComandoCompletarAposta();
            return;
        }


        if (state.getComandoCompletarAposta()
                instanceof CompletarApostaEffect.ApostaEffectProntaParaAdicionar) {

            CompletarApostaEffect.ApostaEffectProntaParaAdicionar comando =
                    (CompletarApostaEffect.ApostaEffectProntaParaAdicionar)
                            state.getComandoCompletarAposta();

            consumirComandoCompletarAposta();

            solicitarAdicionarCarrinho();

            if (comando.isLimparLotecaDepois()) {
                zeraLoteca();
            }

            return;
        }


        if (state.getComandoCompletarAposta()
                instanceof CompletarApostaEffect.ZerarLoteca) {

            zeraLoteca();

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

        if (state.isLimparLoteca()) {
            renderLimparLoteca();
            executouLimpeza = true;
        }

        if (executouLimpeza) {
            dispatch(
                    new SimulaUiEvent.LimpezaApostaRenderizada()
            );
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

    private void atualizarValorENotificarEstadoCartela() {

        if (parametroJogo == null || parentActivity == null) {
            return;
        }

        int qtdPossivelParaEstado =
                getStateAtual().getQtdDezenasPossiveisSelecionado();

        if (parametroJogo.isTipoJogo(ModalidadeEnum.LOTECA)) {

            atualizaValorApostaLoteca();

        } else {

            int qtdParaCalculo =
                    qtdPossivelParaEstado > 0
                            ? qtdPossivelParaEstado
                            : getQuantidadeMinimaSafe();

            atualizaValorTotalAposta(
                    qtdParaCalculo,
                    getStateAtual().getQtdConcursoSelecionado()
            );
        }

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
            if (parametroJogo.isTipoJogo(ModalidadeEnum.LOTECA)) {
                EscudoBO.getInstance().carregaEscudos(context, this::configurarTimesLoteca);
                //configurarTimesLoteca();

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
                    getStateAtual().getDuplasLoteca();

            triplosLoteca =
                    getStateAtual().getTriplasLoteca();
        }

        boolean equipeSelecionadaValida =
                getStateAtual()
                        .getEquipeSelecionada() != null
                        && getStateAtual()
                        .getEquipeSelecionada()
                        .isSelecionado();

        boolean outrosNumerosSelecionado =
                false;

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

                        0,

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

        //            voltaSalvarApostaParaLayoutInicial();
    }

    private void configurarTimesLoteca() {
        LotecaAdapter lotecaAdapter = new LotecaAdapter(parametroJogo, this);
        recyclerViewPartida.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewPartida.setAdapter(lotecaAdapter);
        recyclerViewPartida.setExpanded(true);
    }

    @Override
    public void clickEquipeLoteca(ParametroPartida parametroPartida) {
        int jogos = getStateAtual().getJogosLoteca();
        int simples = getStateAtual().getSimplesLoteca();
        int duplas = getStateAtual().getDuplasLoteca();
        int triplas = getStateAtual().getTriplasLoteca();

        if (parametroPartida.isSelecionado()) {
            if (parametroPartida.getQtdItensSelecionados() == 1) {
                simples = simples + 1;
                if (parametroPartida.getQtdItensSelecionadosAnterior() == 2) {
                    duplas = duplas - 1;
                }
            } else if (parametroPartida.getQtdItensSelecionados() == 2) {
                duplas = duplas + 1;
                if (parametroPartida.getQtdItensSelecionadosAnterior() == 3) {
                    triplas = triplas - 1;
                } else {
                    simples = simples - 1;
                }
            } else if (parametroPartida.getQtdItensSelecionados() == 3) {
                duplas = duplas - 1;
                triplas = triplas + 1;
               }
            partidasSelecionadas.add(parametroPartida);
        } else {
            simples = simples - 1;
            partidasSelecionadas.remove(parametroPartida);
        }
        jogos = simples + duplas + triplas;

        // Mantem o mesmo contrato de eventos usado no preenchimento aleatorio.
        publicarEstadoLoteca(
                jogos,
                simples,
                duplas,
                triplas
        );
    }

    private void limparPartidadasSelecionasLoteca() {
        for (ParametroPartida partida : parametroJogo.getPartidas()) {
            partida.setEmpate(Boolean.FALSE);
            partida.getEquipe1().setSelecionado(Boolean.FALSE);
            partida.getEquipe2().setSelecionado(Boolean.FALSE);
            partida.setSelecionado(Boolean.FALSE);
            partida.setQtdItensSelecionadosAnterior(0);
            partida.setQtdItensSelecionados(0);
        }
        recyclerViewPartida.getAdapter().notifyDataSetChanged();
        partidasSelecionadas.clear();
        parentActivity.zerarApostasLoteca();
        atualizaValorApostaLoteca();
    }

    @Override
    public void partidaSelecionada() {
    }

    @Override
    public void atualizarParametrosLotogol(ParametroPartida parametroPartida) {
        if (parametroPartida.isSelecionado()) {
            partidasSelecionadas.add(parametroPartida);
        } else {
            partidasSelecionadas.remove(parametroPartida);
        }
        atualizarValorENotificarEstadoCartela();
    }
    private void preencherPartidasAleatoriasLoteca() {
        if (parametroJogo == null
                || parametroJogo.getPartidas() == null
                || parametroJogo.getPartidas().isEmpty()
                || recyclerViewPartida == null
                || !(recyclerViewPartida.getAdapter() instanceof LotecaAdapter)) {
            return;
        }

        List<ParametroPartida> partidas =
                parametroJogo.getPartidas();

        List<Integer> posicoesAlteradas =
                new ArrayList<>();

        int totalPalpitesDesejado =
                getStateAtual().getPalpitesLoteca();

        Random random = new Random();

        for (int i = 0; i < partidas.size(); i++) {
            ParametroPartida partida = partidas.get(i);

            if (partida == null
                    || partida.getEquipe1() == null
                    || partida.getEquipe2() == null) {
                continue;
            }

            int quantidadeSelecionada =
                    calcularQuantidadeSelecionada(partida);

            if (quantidadeSelecionada > 0) {
                continue;
            }

            selecionarResultadoSimplesAleatorio(
                    partida,
                    random
            );

            posicoesAlteradas.add(i);
        }

        int totalPalpitesAtual =
                contarTotalPalpitesLoteca(partidas);

        int palpitesFaltantes =
                totalPalpitesDesejado - totalPalpitesAtual;

        while (palpitesFaltantes > 0) {
            List<ParametroPartida> partidasDisponiveis =
                    obterPartidasQueAceitamMaisUmPalpite(
                            partidas
                    );

            if (partidasDisponiveis.isEmpty()) {
                break;
            }

            ParametroPartida partidaSorteada =
                    partidasDisponiveis.get(
                            random.nextInt(
                                    partidasDisponiveis.size()
                            )
                    );

            adicionarResultadoAleatorio(
                    partidaSorteada,
                    random
            );

            int position = partidas.indexOf(partidaSorteada);

            if (position >= 0
                    && !posicoesAlteradas.contains(position)) {
                posicoesAlteradas.add(position);
            }

            palpitesFaltantes--;
        }

        reconstruirEstadoLoteca();

        LotecaAdapter adapter =
                (LotecaAdapter) recyclerViewPartida.getAdapter();

        for (Integer position : posicoesAlteradas) {
            adapter.atualizarEstadoVisual(position);
        }
    }

    private void selecionarResultadoSimplesAleatorio(
            ParametroPartida partida,
            Random random
    ) {
        limparSelecoesDaPartida(partida);

        int resultado = random.nextInt(3);

        partida.getEquipe1().setSelecionado(
                resultado == 0
        );

        partida.setEmpate(
                resultado == 1
        );

        partida.getEquipe2().setSelecionado(
                resultado == 2
        );

        partida.setSelecionado(true);
        partida.setQtdItensSelecionados(1);
        partida.setQtdItensSelecionadosAnterior(1);
    }
    private void limparSelecoesDaPartida(
            ParametroPartida partida
    ) {
        partida.getEquipe1().setSelecionado(false);
        partida.setEmpate(false);
        partida.getEquipe2().setSelecionado(false);
    }
    private int contarTotalPalpitesLoteca(
            List<ParametroPartida> partidas
    ) {
        int total = 0;

        for (ParametroPartida partida : partidas) {
            if (partida == null) {
                continue;
            }

            total += calcularQuantidadeSelecionada(
                    partida
            );
        }

        return total;
    }
    private List<ParametroPartida>
    obterPartidasQueAceitamMaisUmPalpite(
            List<ParametroPartida> partidas
    ) {
        List<ParametroPartida> disponiveis =
                new ArrayList<>();

        for (ParametroPartida partida : partidas) {
            if (partida == null) {
                continue;
            }

            int quantidadeSelecionada =
                    calcularQuantidadeSelecionada(partida);

            if (quantidadeSelecionada > 0
                    && quantidadeSelecionada < 3) {
                disponiveis.add(partida);
            }
        }

        return disponiveis;
    }
    private void adicionarResultadoAleatorio(
            ParametroPartida partida,
            Random random
    ) {
        List<Integer> resultadosDisponiveis =
                new ArrayList<>();

        if (!partida.getEquipe1().isSelecionado()) {
            resultadosDisponiveis.add(0);
        }

        if (!partida.isEmpate()) {
            resultadosDisponiveis.add(1);
        }

        if (!partida.getEquipe2().isSelecionado()) {
            resultadosDisponiveis.add(2);
        }

        if (resultadosDisponiveis.isEmpty()) {
            return;
        }

        int resultado =
                resultadosDisponiveis.get(
                        random.nextInt(
                                resultadosDisponiveis.size()
                        )
                );

        if (resultado == 0) {
            partida.getEquipe1().setSelecionado(true);
        } else if (resultado == 1) {
            partida.setEmpate(true);
        } else {
            partida.getEquipe2().setSelecionado(true);
        }

        int quantidadeSelecionada =
                calcularQuantidadeSelecionada(partida);

        partida.setSelecionado(true);
        partida.setQtdItensSelecionados(
                quantidadeSelecionada
        );
        partida.setQtdItensSelecionadosAnterior(
                quantidadeSelecionada
        );
    }
    private void reconstruirEstadoLoteca(
            ) {
        if (parametroJogo == null
                || parametroJogo.getPartidas() == null) {
            return;
        }

        List<ParametroPartida> partidas =
                parametroJogo.getPartidas();

        int simples = 0;
        int duplas = 0;
        int triplas = 0;

        if (partidasSelecionadas == null) {
            partidasSelecionadas = new HashSet<>();
        } else {
            partidasSelecionadas.clear();
        }

        for (ParametroPartida partida : partidas) {
            if (partida == null) {
                continue;
            }

            int quantidadeSelecionada =
                    calcularQuantidadeSelecionada(partida);

            boolean selecionada =
                    quantidadeSelecionada > 0;

            partida.setSelecionado(selecionada);
            partida.setQtdItensSelecionados(
                    quantidadeSelecionada
            );

            /*
             * Depois da reconstrução, o valor anterior precisa
             * corresponder ao valor atual para o próximo item click.
             */
            partida.setQtdItensSelecionadosAnterior(
                    quantidadeSelecionada
            );

            if (!selecionada) {
                continue;
            }

            partidasSelecionadas.add(partida);

            switch (quantidadeSelecionada) {
                case 1:
                    simples++;
                    break;

                case 2:
                    duplas++;
                    break;

                case 3:
                    triplas++;
                    break;

                default:
                    break;
            }
        }

        int jogos = simples + duplas + triplas;
        publicarEstadoLoteca(
                jogos,
                simples,
                duplas,
                triplas
        );

    }
    private void publicarEstadoLoteca(
            int jogosSelecionados,
            int simples,
            int duplas,
            int triplas
    ) {
        /*
         * Primeiro atualiza os valores da Loteca no state.
         *
         * Isso precisa acontecer antes do CartelaAlterada porque o
         * FluxoApostaReducer usa os valores atuais do state para
         * decidir o estado dos botoes do rodape.
         */
        dispatch(
                new SimulaUiEvent.LotecaAtualizada(
                        jogosSelecionados,
                        simples,
                        duplas,
                        triplas
                )
        );

        /*
         * Depois recalcula o fluxo e o estado dos botoes.
         *
         * Para Loteca:
         * qtdPartidasSelecionadas = jogosSelecionados
         * qtdPartidasTotal = simples
         */
        dispatch(
                new SimulaUiEvent.CartelaAlterada(
                        parametroJogo
                                .getConcurso()
                                .getModalidade(),
                        0,
                        getStateAtual()
                                .getQtdDezenasPossiveisSelecionado(),
                        0,
                        jogosSelecionados,
                        simples,
                        duplas,
                        triplas,
                        false,
                        false,
                        false,
                        false,
                        false,
                        false,
                        getDezenasSelecionadasState()
                )
        );
    }

    private int calcularQuantidadeSelecionada(
            ParametroPartida partida
    ) {
        int quantidade = 0;

        if (partida.getEquipe1() != null
                && partida.getEquipe1().isSelecionado()) {
            quantidade++;
        }

        if (partida.isEmpate()) {
            quantidade++;
        }

        if (partida.getEquipe2() != null
                && partida.getEquipe2().isSelecionado()) {
            quantidade++;
        }

        return quantidade;
    }
    private void atualizaValorApostaLoteca() {

        dispatch(new SimulaUiEvent.LotecaAtualizada(
                getStateAtual().getJogosLoteca(),
                getStateAtual().getSimplesLoteca(),
                getStateAtual().getDuplasLoteca(),
                getStateAtual().getTriplasLoteca()
        ));

        return;
    }

    private void editaAposta() {

        if (parentActivity.getAposta() == null) {
            return;
        }

        IdentificaoDeUmaApostaDas8Modalidades aposta =
                parentActivity.getAposta();

        IdentificaoDeUmaApostaDas8Modalidades apostaSuperSete =
                parentActivity.getAposta();


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

    @Override
    public EtapaAposta getEtapa() {
        return EtapaAposta.NUMEROS;
    }


}