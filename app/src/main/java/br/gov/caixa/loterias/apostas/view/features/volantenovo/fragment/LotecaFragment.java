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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.controllers.PrincipalActivity;
import br.gov.caixa.loterias.apostas.effect.CompletarApostaEffect;
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
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.PartidaLotecaDTO;
import br.gov.caixa.loterias.apostas.utils.ApostaUtils;
import br.gov.caixa.loterias.apostas.utils.Constantes;
import br.gov.caixa.loterias.apostas.utils.DialogUtils;
import br.gov.caixa.loterias.apostas.utils.EtapaAposta;
import br.gov.caixa.loterias.apostas.utils.IntentUtil;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.utils.VolanteUtils;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.ListaDezenaRecyclerView;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.LotecaAdapter;
import br.gov.caixa.loterias.apostas.view.config.DezenaConfig;
import br.gov.caixa.loterias.apostas.view.custom.ExpandableHeightRecyclerView;
import br.gov.caixa.loterias.apostas.view.features.volantenovo.activity.LotecaActivity;
import br.gov.caixa.loterias.apostas.view.features.volantenovo.presentation.LotecaViewModel;
import br.gov.caixa.loterias.apostas.view.features.volantenovo.presentation.SimulaUiEvent;
import br.gov.caixa.loterias.apostas.view.features.volantenovo.presentation.SimulaUiState;
import br.gov.caixa.loterias.apostas.view.holder.DezenaHolder;
import br.gov.caixa.loterias.apostas.view.listener.CartelaFragmentListener;
import br.gov.caixa.loterias.apostas.view.listener.LocateAdapterListener;
import br.gov.caixa.loterias.apostas.view.listener.LotogolListener;
import br.gov.caixa.loterias.apostas.view.listener.OnItemClickListener;
import br.gov.caixa.loterias.apostas.view.listener.custom.LoteriasSensorEventListener;

public class LotecaFragment extends BaseEtapaFragment implements LocateAdapterListener, LotogolListener {
    private static final int QUANTIDADE_COLUNA_LISTA = 6;
    private static final int MAXIMO_PALPITES_LOTECA = 26;
    private SensorManager mSensorManager;

    private final SensorEventListener mSensorListener = new LoteriasSensorEventListener(0.00f,
            SensorManager.GRAVITY_EARTH,
            SensorManager.GRAVITY_EARTH).getSensor();

    private List<Dezena> dezenas = new ArrayList<>();
    private HashSet<ParametroPartida> partidasSelecionadas;
    private Integer palpitesLotecaPendente;

    private CartelaFragmentListener CartelaFragmentListener;

    private ParametroJogoDTO parametroJogo;
    private ModalidadeEnum tipoJogo;
    private LotecaActivity parentActivity;
    private View view;
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

        if (parametroJogo != null && dezenas.isEmpty() && parametroJogo.getPrognosticoMaximo() != null && tipoJogo != ModalidadeEnum.SUPER_7) {
            dezenas = VolanteUtils.getDezenas(tipoJogo, parametroJogo.getPrognosticoMaximo());
        }

        partidasSelecionadas = new HashSet<>();

    }

    @Override
    public void onStart() {
        super.onStart();

        loadParametroJogo();

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
                new HashSet<>(partidasSelecionadas)
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

        boolean etapaComplementarCompleta = false;

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


        if (state.getComandoCompletarAposta() instanceof CompletarApostaEffect.ApostaEffectProntaParaAdicionar comando) {

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

        palpitesLotecaPendente = null;

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
                false;

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
                        getString(R.string.msg_qtd_max_loteca)
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

    private void configurarTimesLoteca() {
        LotecaAdapter lotecaAdapter = new LotecaAdapter(parametroJogo, this);
        recyclerViewPartida.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewPartida.setAdapter(lotecaAdapter);
        recyclerViewPartida.setExpanded(true);
    }

    @Override
    public boolean clickEquipeLoteca(ParametroPartida parametroPartida) {
        int[] contadores = calcularContadoresLotecaSemEfeito();
        int simples = contadores[0];
        int duplas = contadores[1];
        int triplas = contadores[2];

        boolean combinacaoValida =
                parametroJogo == null
                        || parametroJogo.isCombinacaoValidaLoteca(duplas, triplas);

        if (!combinacaoValida) {
            palpitesLotecaPendente = null;
            exibirAvisoCombinacaoInvalidaLoteca();
            return false;
        }

        confirmarAumentoPendentePalpitesLoteca();
        reconstruirEstadoLoteca();

        return true;
    }

    private void confirmarAumentoPendentePalpitesLoteca() {
        if (palpitesLotecaPendente == null) {
            return;
        }

        dispatch(
                new SimulaUiEvent.PalpitesLotecaAtualizado(
                        palpitesLotecaPendente
                )
        );

        palpitesLotecaPendente = null;
    }

    private int[] calcularContadoresLotecaSemEfeito() {
        int simples = 0;
        int duplas = 0;
        int triplas = 0;

        if (parametroJogo != null && parametroJogo.getPartidas() != null) {
            for (ParametroPartida partida : parametroJogo.getPartidas()) {

                int quantidadeSelecionada =
                        calcularQuantidadeSelecionada(partida);

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
        }

        return new int[]{simples, duplas, triplas};
    }

    private void exibirAvisoCombinacaoInvalidaLoteca() {
        DialogUtils.dialogEntendi(
                getActivity(),
                getString(R.string.label_combinacao_invalida_loteca)
        );
    }

    @Override
    public boolean podeAdicionarResultadoLoteca() {
        SimulaUiState state = getStateAtual();

        if (state == null) {
            return true;
        }

        int totalPalpitesSelecionados =
                calcularTotalPalpitesLoteca(
                        state.getSimplesLoteca(),
                        state.getDuplasLoteca(),
                        state.getTriplasLoteca()
                );

        int limiteAtual = state.getPalpitesLoteca();

        if (totalPalpitesSelecionados < limiteAtual) {
            palpitesLotecaPendente = null;
            return true;
        }

        if (limiteAtual >= MAXIMO_PALPITES_LOTECA) {
            palpitesLotecaPendente = null;
            return false;
        }

        palpitesLotecaPendente = limiteAtual + 1;

        return true;
    }

    @Override
    public void onLimitePalpitesLotecaAtingido() {
        SimulaUiState state = getStateAtual();

        int limite = state != null
                ? state.getPalpitesLoteca()
                : 15;

        if (limite >= MAXIMO_PALPITES_LOTECA) {
            DialogUtils.dialogEntendi(
                    getActivity(),
                    getString(R.string.label_nao_possivel_selecionar_quantidade_numeros_superior_limite_modalidade_loterica)
            );

            return;
        }

        DialogUtils.dialogEntendi(
                getActivity(),
                getString(R.string.msg_qtd_max_loteca)
        );
    }

    private void limparPartidadasSelecionasLoteca() {
        palpitesLotecaPendente = null;
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
        // Empty implementation, can be used for future logic if needed
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

    @Override protected boolean suportaApostaShake() { return true; }
    @Override protected void preencherApostaShake(boolean renovar) {
        preencherPartidasAleatoriasLoteca();
    }

    private void preencherPartidasAleatoriasLoteca() {

        if (parametroJogo == null
                || parametroJogo.getPartidas() == null
                || parametroJogo.getPartidas().isEmpty()
                || recyclerViewPartida == null
                || !(recyclerViewPartida.getAdapter() instanceof LotecaAdapter adapter)) {
            return;
        }

        List<ParametroPartida> partidas = parametroJogo.getPartidas();

        int tamanho = partidas.size();
        int[] qtdInicialPorPartida = new int[tamanho];

        for (int i = 0; i < tamanho; i++) {
            qtdInicialPorPartida[i] =
                    calcularQuantidadeSelecionada(
                            partidas.get(i)
                    );
        }

        int palpitesAtuais =
                getStateAtual().getPalpitesLoteca();

        Random random = new Random();

        // Ordem de prioridade:
        // 1) tenta preservar com os palpites atuais
        // 2) relaxa preservacao por prioridade (1 selecao, 2 selecoes, geral)
        // 3) so depois aumenta palpites e repete a mesma ordem de preservacao
        for (int alvoPalpites = palpitesAtuais;
             alvoPalpites <= MAXIMO_PALPITES_LOTECA;
             alvoPalpites++) {

            for (int estrategia = 0; estrategia <= 3; estrategia++) {

                ResultadoPreenchimentoLoteca resultado =
                        tentarPreencherPartidasLoteca(
                                partidas,
                                qtdInicialPorPartida,
                                estrategia,
                                alvoPalpites,
                                random
                        );

                if (resultado == null || !resultado.sucesso()) {
                    continue;
                }

                if (alvoPalpites > palpitesAtuais) {
                    dispatch(
                            new SimulaUiEvent.PalpitesLotecaAtualizado(
                                    alvoPalpites
                            )
                    );
                }

                reconstruirEstadoLoteca();

                for (Integer position : resultado.posicoesAlteradas()) {
                    adapter.atualizarEstadoVisual(position);
                }

                return;
            }
        }
    }

    private ResultadoPreenchimentoLoteca tentarPreencherPartidasLoteca(
            List<ParametroPartida> partidas,
            int[] qtdInicialPorPartida,
            int estrategia,
            int alvoPalpites,
            Random random
    ) {
        int totalPartidas = partidas.size();

        List<Integer> indicesPreservados = new ArrayList<>();
        List<Integer> indicesLivres = new ArrayList<>();

        int preservadasSimples = 0;
        int preservadasDuplas = 0;
        int preservadasTriplas = 0;

        for (int i = 0; i < totalPartidas; i++) {
            int qtdInicial = qtdInicialPorPartida[i];

            if (devePreservarLinha(qtdInicial, estrategia)) {
                indicesPreservados.add(i);

                if (qtdInicial == 1) {
                    preservadasSimples++;
                } else if (qtdInicial == 2) {
                    preservadasDuplas++;
                } else if (qtdInicial >= 3) {
                    preservadasTriplas++;
                }

                continue;
            }

            indicesLivres.add(i);
        }

        ParametroValorApostaDTO configuracao =
                encontrarConfiguracaoValida(
                        alvoPalpites,
                        totalPartidas,
                        preservadasSimples,
                        preservadasDuplas,
                        preservadasTriplas,
                        indicesLivres.size(),
                        random
                );

        if (configuracao == null) {
            return null;
        }

        Integer qtdDuplasConfig = configuracao.getQuantidadeDuplos();
        Integer qtdTriplasConfig = configuracao.getQuantidadeTriplos();

        int duplasAlvo = qtdDuplasConfig != null ? qtdDuplasConfig : 0;
        int triplasAlvo = qtdTriplasConfig != null ? qtdTriplasConfig : 0;
        int simplesAlvo = totalPartidas - duplasAlvo - triplasAlvo;

        int duplasLivresNecessarias = duplasAlvo - preservadasDuplas;
        int triplasLivresNecessarias = triplasAlvo - preservadasTriplas;
        int simplesLivresNecessarias = simplesAlvo - preservadasSimples;

        if (duplasLivresNecessarias < 0
                || triplasLivresNecessarias < 0
                || simplesLivresNecessarias < 0) {
            return null;
        }

        if (duplasLivresNecessarias
                + triplasLivresNecessarias
                + simplesLivresNecessarias != indicesLivres.size()) {
            return null;
        }

        List<Integer> posicoesAlteradas = new ArrayList<>();

        Collections.shuffle(indicesLivres, random);

        int cursor = 0;

        for (int i = 0; i < triplasLivresNecessarias; i++) {
            int index = indicesLivres.get(cursor++);
            ParametroPartida partida = partidas.get(index);
            if (aplicarSelecaoAleatoria(partida, 3, random)) {
                posicoesAlteradas.add(index);
            }
        }

        for (int i = 0; i < duplasLivresNecessarias; i++) {
            int index = indicesLivres.get(cursor++);
            ParametroPartida partida = partidas.get(index);
            if (aplicarSelecaoAleatoria(partida, 2, random)) {
                posicoesAlteradas.add(index);
            }
        }

        for (int i = 0; i < simplesLivresNecessarias; i++) {
            int index = indicesLivres.get(cursor++);
            ParametroPartida partida = partidas.get(index);
            if (aplicarSelecaoAleatoria(partida, 1, random)) {
                posicoesAlteradas.add(index);
            }
        }

        for (Integer index : indicesPreservados) {
            ParametroPartida partida = partidas.get(index);
            int quantidade = calcularQuantidadeSelecionada(partida);
            atualizarContadoresPartida(partida, quantidade);
        }

        return new ResultadoPreenchimentoLoteca(
                true,
                posicoesAlteradas
        );
    }

    private boolean devePreservarLinha(
            int quantidadeInicial,
            int estrategia
    ) {
        return switch (estrategia) {
            case 0 -> quantidadeInicial > 0;
            case 1 -> quantidadeInicial >= 2;
            case 2 -> quantidadeInicial >= 3;
            default -> false;
        };
    }

    private ParametroValorApostaDTO encontrarConfiguracaoValida(
            int alvoPalpites,
            int totalPartidas,
            int preservadasSimples,
            int preservadasDuplas,
            int preservadasTriplas,
            int quantidadeLivres,
            Random random
    ) {

        if (parametroJogo == null
                || parametroJogo.getValoresAposta() == null
                || parametroJogo.getValoresAposta().isEmpty()) {
            return null;
        }

        List<ParametroValorApostaDTO> candidatas = new ArrayList<>();

        for (ParametroValorApostaDTO valor : parametroJogo.getValoresAposta()) {
            if (valor == null) {
                continue;
            }

            int duplas = valor.getQuantidadeDuplos() != null
                    ? valor.getQuantidadeDuplos()
                    : 0;

            int triplas = valor.getQuantidadeTriplos() != null
                    ? valor.getQuantidadeTriplos()
                    : 0;

            int simples = totalPartidas - duplas - triplas;

            if (simples < 0) {
                continue;
            }

            int totalPalpites = simples + (duplas * 2) + (triplas * 3);

            if (totalPalpites != alvoPalpites) {
                continue;
            }

            if (duplas < preservadasDuplas
                    || triplas < preservadasTriplas
                    || simples < preservadasSimples) {
                continue;
            }

            int livresNecessarias =
                    (duplas - preservadasDuplas)
                            + (triplas - preservadasTriplas)
                            + (simples - preservadasSimples);

            if (livresNecessarias != quantidadeLivres) {
                continue;
            }

            candidatas.add(valor);
        }

        if (candidatas.isEmpty()) {
            return null;
        }

        return candidatas.get(
                random.nextInt(candidatas.size())
        );
    }

    private boolean aplicarSelecaoAleatoria(
            ParametroPartida partida,
            int quantidade,
            Random random
    ) {
        if (partida == null
                || partida.getEquipe1() == null
                || partida.getEquipe2() == null) {
            return false;
        }

        boolean antesEquipe1 = partida.getEquipe1().isSelecionado();
        boolean antesEmpate = partida.isEmpate();
        boolean antesEquipe2 = partida.getEquipe2().isSelecionado();

        int quantidadeAtual = 0;
        if (antesEquipe1) {
            quantidadeAtual++;
        }
        if (antesEmpate) {
            quantidadeAtual++;
        }
        if (antesEquipe2) {
            quantidadeAtual++;
        }

        if (quantidadeAtual > 0
                && quantidade >= quantidadeAtual
                && aplicarSelecaoAleatoriaPreservandoExistentes(
                partida,
                quantidade,
                random
        )) {
            return antesEquipe1 != partida.getEquipe1().isSelecionado()
                    || antesEmpate != partida.isEmpate()
                    || antesEquipe2 != partida.getEquipe2().isSelecionado();
        }

        limparSelecoesDaPartida(partida);

        if (quantidade >= 3) {
            partida.getEquipe1().setSelecionado(true);
            partida.setEmpate(true);
            partida.getEquipe2().setSelecionado(true);
            atualizarContadoresPartida(partida, 3);
        } else if (quantidade == 2) {
            int excluir = random.nextInt(3);

            partida.getEquipe1().setSelecionado(excluir != 0);
            partida.setEmpate(excluir != 1);
            partida.getEquipe2().setSelecionado(excluir != 2);
            atualizarContadoresPartida(partida, 2);
        } else {
            int resultado = random.nextInt(3);

            partida.getEquipe1().setSelecionado(resultado == 0);
            partida.setEmpate(resultado == 1);
            partida.getEquipe2().setSelecionado(resultado == 2);
            atualizarContadoresPartida(partida, 1);
        }

        return antesEquipe1 != partida.getEquipe1().isSelecionado()
                || antesEmpate != partida.isEmpate()
                || antesEquipe2 != partida.getEquipe2().isSelecionado();
    }

    private boolean aplicarSelecaoAleatoriaPreservandoExistentes(
            ParametroPartida partida,
            int quantidade,
            Random random
    ) {
        List<Integer> selecionadas = new ArrayList<>(3);
        List<Integer> livres = new ArrayList<>(3);

        if (partida.getEquipe1().isSelecionado()) {
            selecionadas.add(0);
        } else {
            livres.add(0);
        }

        if (partida.isEmpate()) {
            selecionadas.add(1);
        } else {
            livres.add(1);
        }

        if (partida.getEquipe2().isSelecionado()) {
            selecionadas.add(2);
        } else {
            livres.add(2);
        }

        if (quantidade < selecionadas.size()) {
            return false;
        }

        Collections.shuffle(livres, random);

        while (selecionadas.size() < quantidade && !livres.isEmpty()) {
            selecionadas.add(livres.remove(0));
        }

        if (selecionadas.size() != quantidade) {
            return false;
        }

        aplicarSelecaoNaPartida(partida, selecionadas);
        atualizarContadoresPartida(partida, quantidade);
        return true;
    }

    private void aplicarSelecaoNaPartida(
            ParametroPartida partida,
            List<Integer> selecoes
    ) {
        boolean selecionarEquipe1 = selecoes.contains(0);
        boolean selecionarEmpate = selecoes.contains(1);
        boolean selecionarEquipe2 = selecoes.contains(2);

        partida.getEquipe1().setSelecionado(selecionarEquipe1);
        partida.setEmpate(selecionarEmpate);
        partida.getEquipe2().setSelecionado(selecionarEquipe2);
    }

    private void atualizarContadoresPartida(
            ParametroPartida partida,
            int quantidadeSelecionada
    ) {
        partida.setSelecionado(quantidadeSelecionada > 0);
        partida.setQtdItensSelecionados(quantidadeSelecionada);
        partida.setQtdItensSelecionadosAnterior(quantidadeSelecionada);
    }

    private record ResultadoPreenchimentoLoteca(boolean sucesso, List<Integer> posicoesAlteradas) {
            private ResultadoPreenchimentoLoteca(
                    boolean sucesso,
                    List<Integer> posicoesAlteradas
            ) {
                this.sucesso = sucesso;
                this.posicoesAlteradas =
                        posicoesAlteradas != null
                                ? posicoesAlteradas
                                : new ArrayList<>();
            }
        }

    private void limparSelecoesDaPartida(
            ParametroPartida partida
    ) {
        if (partida == null
                || partida.getEquipe1() == null
                || partida.getEquipe2() == null) {
            return;
        }

        partida.getEquipe1().setSelecionado(false);
        partida.setEmpate(false);
        partida.getEquipe2().setSelecionado(false);
    }

    private void reconstruirEstadoLoteca() {
        if (parametroJogo == null
                || parametroJogo.getPartidas() == null) {
            return;
        }

        List<ParametroPartida> partidas = parametroJogo.getPartidas();

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
            partida.setQtdItensSelecionados(quantidadeSelecionada);
            partida.setQtdItensSelecionadosAnterior(quantidadeSelecionada);

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
        int qtdPartidasTotal =
                parametroJogo != null
                        && parametroJogo.getPartidas() != null
                        ? parametroJogo.getPartidas().size()
                        : 0;

        int totalPalpitesSelecionados =
                calcularTotalPalpitesLoteca(
                        simples,
                        duplas,
                        triplas
                );

        dispatch(
                new SimulaUiEvent.LotecaAtualizada(
                        jogosSelecionados,
                        simples,
                        duplas,
                        triplas
                )
        );

        sincronizarPalpitesLotecaComSelecao(
                totalPalpitesSelecionados
        );

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
                        qtdPartidasTotal,
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

    private int calcularTotalPalpitesLoteca(
            int simples,
            int duplas,
            int triplas
    ) {
        return Math.max(simples, 0)
                + (Math.max(duplas, 0) * 2)
                + (Math.max(triplas, 0) * 3);
    }

    private void sincronizarPalpitesLotecaComSelecao(
            int totalPalpitesSelecionados
    ) {
        SimulaUiState state = getStateAtual();

        if (state == null) {
            return;
        }

        int palpitesAtuais = state.getPalpitesLoteca();

        if (totalPalpitesSelecionados <= palpitesAtuais
                || totalPalpitesSelecionados > MAXIMO_PALPITES_LOTECA) {
            return;
        }

        dispatch(
                new SimulaUiEvent.PalpitesLotecaAtualizado(
                        totalPalpitesSelecionados
                )
        );
    }

    private int calcularQuantidadeSelecionada(
            ParametroPartida partida
    ) {
        int quantidade = 0;

        if (partida != null
                && partida.getEquipe1() != null
                && partida.getEquipe1().isSelecionado()) {
            quantidade++;
        }

        if (partida != null && partida.isEmpate()) {
            quantidade++;
        }

        if (partida != null
                && partida.getEquipe2() != null
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
    }

    private void editaAposta() {

        if (parentActivity.getAposta() == null) {
            return;
        }

        IdentificaoDeUmaApostaDas8Modalidades aposta =
                parentActivity.getAposta();

        if (CollectionUtils.isNotEmpty(aposta.getListaNumerosSelecionados())) {

            List<Integer> selecionadas = aposta.getListaNumerosSelecionados();
            atualizarDezenasSelecionadas(selecionadas);

            if (simularDezenasAdapter != null) {
                simularDezenasAdapter.atualizaSelecionados(
                        selecionadas
                );
            }

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

        if (CollectionUtils.isNotEmpty(aposta.getPartidasLoteca())) {
            restaurarPartidasLoteca(aposta.getPartidasLoteca());
        }

        publicarValorAposta(
                aposta.getValor()
        );

        notificarEstadoCartelaAlterado();
    }

    private void restaurarPartidasLoteca(
            List<PartidaLotecaDTO> partidasSalvas
    ) {
        if (parametroJogo == null
                || parametroJogo.getPartidas() == null
                || CollectionUtils.isEmpty(partidasSalvas)) {
            return;
        }

        for (PartidaLotecaDTO partidaSalva : partidasSalvas) {

            if (partidaSalva == null || partidaSalva.getNumero() == null) {
                continue;
            }

            ParametroPartida partidaAtual =
                    encontrarPartidaPorNumero(partidaSalva.getNumero());

            if (partidaAtual == null
                    || partidaAtual.getEquipe1() == null
                    || partidaAtual.getEquipe2() == null) {
                continue;
            }

            boolean equipe1Selecionada =
                    partidaSalva.getEquipe1() != null
                            && Boolean.TRUE.equals(partidaSalva.getEquipe1().getVitoria());

            boolean equipe2Selecionada =
                    partidaSalva.getEquipe2() != null
                            && Boolean.TRUE.equals(partidaSalva.getEquipe2().getVitoria());

            boolean empateSelecionado =
                    Boolean.TRUE.equals(partidaSalva.getEmpate());

            partidaAtual.getEquipe1().setSelecionado(equipe1Selecionada);
            partidaAtual.getEquipe2().setSelecionado(equipe2Selecionada);
            partidaAtual.setEmpate(empateSelecionado);
        }

        reconstruirEstadoLoteca();

        if (recyclerViewPartida != null
                && recyclerViewPartida.getAdapter() != null) {

            recyclerViewPartida.getAdapter().notifyDataSetChanged();
        }
    }

    private ParametroPartida encontrarPartidaPorNumero(
            Integer numero
    ) {
        if (numero == null
                || parametroJogo == null
                || parametroJogo.getPartidas() == null) {
            return null;
        }

        for (ParametroPartida partida : parametroJogo.getPartidas()) {
            if (partida != null
                    && numero.equals(partida.getNumero())) {
                return partida;
            }
        }

        return null;
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
