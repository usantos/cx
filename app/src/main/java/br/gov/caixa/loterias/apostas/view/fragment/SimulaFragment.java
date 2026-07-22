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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;

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
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaFavoritaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.Dezena;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroEquipe;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroJogoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroPartida;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroSimulacao;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroTrevo;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroValorApostaDTO;
import br.gov.caixa.loterias.apostas.model.model.CartelaModel;
import br.gov.caixa.loterias.apostas.model.model.SimularApostaModel;
import br.gov.caixa.loterias.apostas.state.SimulaUiState;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.ApostaUtils;
import br.gov.caixa.loterias.apostas.utils.BarraTituloDTO;
import br.gov.caixa.loterias.apostas.utils.Constantes;
import br.gov.caixa.loterias.apostas.utils.DialogUtils;
import br.gov.caixa.loterias.apostas.utils.InputUtils;
import br.gov.caixa.loterias.apostas.utils.IntentUtil;
import br.gov.caixa.loterias.apostas.utils.ListaUtils;
import br.gov.caixa.loterias.apostas.utils.Utils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.utils.VolanteUtils;
import br.gov.caixa.loterias.apostas.view.activity.SimulaActivity;
import br.gov.caixa.loterias.apostas.view.adapter.baseadapter.SuperSeteAdapter;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.ListaDezenaRecyclerView;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.LotecaAdapter;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.TimeCoracaoAdapter;
import br.gov.caixa.loterias.apostas.view.custom.ExpandableHeightGridView;
import br.gov.caixa.loterias.apostas.view.custom.ExpandableHeightRecyclerView;
import br.gov.caixa.loterias.apostas.view.custom.NomeTimemaniaTextWatcher;
import br.gov.caixa.loterias.apostas.view.holder.DezenaHolder;
import br.gov.caixa.loterias.apostas.view.listener.CartelaFragmentListener;
import br.gov.caixa.loterias.apostas.view.listener.LocateAdapterListener;
import br.gov.caixa.loterias.apostas.view.listener.LotogolListener;
import br.gov.caixa.loterias.apostas.view.listener.NomeTimeTextWatcherListener;
import br.gov.caixa.loterias.apostas.view.listener.OnItemClickListener;
import br.gov.caixa.loterias.apostas.view.listener.custom.LoteriasSensorEventListener;
import br.gov.caixa.loterias.apostas.viewModel.SimulaViewModel;

public class SimulaFragment extends Fragment implements LocateAdapterListener, LotogolListener {
    private static final int QUANTIDADE_COLUNA_LISTA = 6;
    private SensorManager mSensorManager;

    private final SensorEventListener mSensorListener = new LoteriasSensorEventListener(0.00f,
            SensorManager.GRAVITY_EARTH,
            SensorManager.GRAVITY_EARTH).getSensor();

    private List<Dezena> dezenas = new ArrayList<>();
    private List<Integer> dezenasSelecionadas = new ArrayList<>();
    private List<List<Integer>> matrizSelecionada;
    private HashSet<ParametroPartida> partidasSelecionadas;

    private CartelaFragmentListener CartelaFragment2Listener;

    private ParametroJogoDTO parametroJogo;
    private ModalidadeEnum tipoJogo;
    private SimulaActivity parentActivity;
    private AppCompatCheckBox selecioneOutrosNumeros;
    private View view;
    private LinearLayout opcaoOutrosNumerosLayout;
    private RecyclerView listaDezenas;
    private ArrayList<ExpandableHeightGridView> listaGridsSuperSete;
    private ArrayList<SuperSeteAdapter> listaAdaptersSuperSete;
    private ExpandableHeightRecyclerView recyclerViewPartida;
    private int qtdTotalSelecionadosSuperSete = 0;

    private boolean isEcolhaTimeBtnAtivado;
    private boolean isEscolhaTimeCoracaoSurpresinha;
    private boolean isAvisou = false;
    public BigDecimal valorTotalAposta;
    public int typeGameColorLight, typeGameColorDark;
    public int corFonteFundoBranco, corFonteFundoClaro, corFonteFundoEscuro;
    public TextView valorAposta, valorTotalJogos, valorTotalSimples, valorTotalDuplas, valorTotalTriplas;
    public ListaDezenaRecyclerView simularDezenasAdapter;
    public String dataSorteioAtual;

    private SimularApostaModel model;
    private CartelaModel cartelaModel;
    private SimulaViewModel viewModel;

    public BarraTituloDTO barraTituloDTO = new BarraTituloDTO();
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

        SimulaUiState state =
                viewModel.getUiState().getValue();

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

            if (state.getQtdConcursoSelecionado() > 0) {
                valorCalculado =
                        valorCalculado.multiply(
                                BigDecimal.valueOf(
                                        state.getQtdConcursoSelecionado()
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
                && state != null
                && state.isOpcaoOutrosNumerosSelecionada()) {

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
        viewModel =
                new ViewModelProvider(
                        requireActivity()
                ).get(SimulaViewModel.class);


        barraTituloDTO = getBarraTituloSafe();
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

        if (parametroJogo != null && dezenas.isEmpty() && parametroJogo.getPrognosticoMaximo() != null && !(tipoJogo == ModalidadeEnum.SUPER_7)) {
            dezenas = VolanteUtils.getDezenas(tipoJogo, parametroJogo.getPrognosticoMaximo());
        }

        model = new SimularApostaModel(getActivity());
        cartelaModel = new CartelaModel();
        partidasSelecionadas = new HashSet<>();

    }
    private SimulaUiState getStateAtual() {
        if (viewModel == null
                || viewModel.getUiState() == null
                || viewModel.getUiState().getValue() == null) {
            return SimulaUiState.initial();
        }

        return viewModel.getUiState().getValue();
    }

    private int getQuantidadeSelecionada() {
        return getStateAtual()
                .getQtdDezenasPossiveisSelecionado();
    }

    private BarraTituloDTO getBarraTituloSafe() {

        if (viewModel == null) {
            return new BarraTituloDTO();
        }

        SimulaUiState state = viewModel.getUiState().getValue();

        if (state == null) {
            return new BarraTituloDTO();
        }

        BarraTituloDTO barra = state.getBarraTituloDTO();

        return barra != null
                ? barra
                : new BarraTituloDTO();
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

        if (parametroJogo.isTipoJogo(ModalidadeEnum.LOTOMANIA)) {

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

    public void onSalvarFavoritoRodapeConfirmado(
            String nome
    ) {
        solicitarSalvarApostaFavorita(nome);
    }
    private void atualizarQuantidadeSelecionada(
            int quantidade
    ) {
        viewModel.onEvent(
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
            CartelaFragment2Listener = (CartelaFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context + " must implement CartelaFragment2Listener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.context = null;
        CartelaFragment2Listener = null;
    }

    private void identificarTipoJogo() {
        if (parentActivity.getIntent() != null) {
            tipoJogo = (ModalidadeEnum) parentActivity.getIntent().getSerializableExtra("tipoAposta");
        }
    }

    private void solicitarAdicionarCarrinho() {

        IdentificaoDeUmaApostaDas8Modalidades apostaParaCarrinho = null;

        if (!isEscolhaTimeCoracaoSurpresinha) {
            apostaParaCarrinho = criarApostaParaCarrinho();
        }

        viewModel.onEvent(
                new SimulaUiEvent.AdicionarCarrinhoSolicitado(
                        apostaParaCarrinho,
                        barraTituloDTO,
                        isEscolhaTimeCoracaoSurpresinha,
                        getStateAtual().getEquipeSelecionada()
                )
        );
    }

    private IdentificaoDeUmaApostaDas8Modalidades criarApostaParaCarrinho() {

        if (isEscolhaTimeCoracaoSurpresinha) {
            return null;
        }

        dezenasSelecionadas =
                ListaUtils.orderAscDezenas(
                        dezenasSelecionadas
                );

        if (parametroJogo.isTipoJogo(ModalidadeEnum.SUPER_7)) {
            return ApostaUtils.getApostaSuperSete(
                    parametroJogo,
                    dezenasSelecionadas,
                    parentActivity.getValorTotalApostaAtual(),
                    getStateAtual().getQtdConcursoSelecionado(),
                    ListaUtils.pegaListaSuperSete(
                            listaAdaptersSuperSete
                    )
            );
        }

        IdentificaoDeUmaApostaDas8Modalidades aposta =
                getApostaPreenchida();

        SimulaUiState state =
                viewModel.getUiState().getValue();

        if (parametroJogo.isTipoJogo(ModalidadeEnum.LOTOMANIA)
                && state != null
                && state.isOpcaoOutrosNumerosSelecionada()) {

            aposta.setGerarEspelho(true);

            if (aposta.getValor() != null) {
                aposta.setValor(
                        aposta.getValor()
                                .divide(Constantes.DOIS_BIG_DECIMAL)
                );
            }
        }

        return aposta;
    }

    private IdentificaoDeUmaApostaDas8Modalidades getApostaPreenchida() {

        BigDecimal valorAtual =
                parentActivity.getValorTotalApostaAtual();

        if (parametroJogo.isTipoJogo(ModalidadeEnum.DIA_DE_SORTE)) {

            return ApostaUtils.getApostaDiaDeSorte(
                    parametroJogo,
                    dezenasSelecionadas,
                    valorAtual,
                    getStateAtual().getQtdConcursoSelecionado(),
                    parentActivity.getMesDeSorteCartela()
            );

        } else if (parametroJogo.isTipoJogo(ModalidadeEnum.LOTECA)) {

            return ApostaUtils.getApostaLoteca(
                    parametroJogo,
                    dezenasSelecionadas,
                    valorAtual,
                    getStateAtual().getQtdConcursoSelecionado(),
                    partidasSelecionadas
            );

        } else if (parametroJogo.isTipoJogo(ModalidadeEnum.LOTOGOL)) {

            return ApostaUtils.getApostaLotogol(
                    parametroJogo,
                    dezenasSelecionadas,
                    valorAtual,
                    getStateAtual().getQtdConcursoSelecionado(),
                    partidasSelecionadas
            );

        } else if (parametroJogo.isTipoJogo(ModalidadeEnum.TIMEMANIA)) {

            return ApostaUtils.getApostaTimemania(
                    parametroJogo,
                    dezenasSelecionadas,
                    valorAtual,
                    getStateAtual().getQtdConcursoSelecionado(),
                    getStateAtual().getEquipeSelecionada()
            );

        } else {

            return ApostaUtils.getApostaPreenchida(
                    parametroJogo,
                    dezenasSelecionadas,
                    valorAtual,
                    getStateAtual().getQtdConcursoSelecionado()
            );
        }
    }

    private void zeraLoteca() {
        limparPartidadasSelecionasLoteca();
        limparOpcaoOutrosNumerosLayout();
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

        if (viewModel == null || parametroJogo == null || parentActivity == null) {
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

        viewModel.onEvent(
                new SimulaUiEvent.CompletarApostaClicado(
                        parametroJogo.getConcurso().getModalidade(),
                        dezenasSelecionadas != null ? dezenasSelecionadas.size() : 0,
                        getStateAtual().getQtdDezenasPossiveisSelecionado(),
                        qtdTotalSelecionadosSuperSete,
                        qtdPartidasSelecionadas,
                        qtdPartidasTotal,
                        isEscolhaTimeCoracaoSurpresinha,
                        isEcolhaTimeBtnAtivado,
                        equipeSelecionadaValida,
                        parentActivity.isFragmentCartela()
                )
        );
    }

    private void solicitarAdicionarCarrinhoPorBotao() {

        if (viewModel == null || parametroJogo == null || parentActivity == null) {
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

        viewModel.onEvent(
                new SimulaUiEvent.AdicionarCarrinhoClicado(
                        parametroJogo.getConcurso().getModalidade(),
                        dezenasSelecionadas != null ? dezenasSelecionadas.size() : 0,
                        getStateAtual().getQtdDezenasPossiveisSelecionado(),
                        qtdTotalSelecionadosSuperSete,
                        qtdPartidasSelecionadas,
                        qtdPartidasTotal,
                        duplosLoteca,
                        triplosLoteca,
                        isEscolhaTimeCoracaoSurpresinha,
                        equipeSelecionadaValida,
                        parentActivity.isFragmentCartela()
                )
        );
    }

    private void notificarEstadoCartelaAlterado(
            int qtdDezenasPossiveisParaEstado
    ) {

        if (viewModel == null || parametroJogo == null || parentActivity == null) {
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
                        && dezenasSelecionadas.size()
                        == qtdDezenasPossiveisParaEstado;

        boolean outrosNumerosSelecionado =
                selecioneOutrosNumeros != null
                        && selecioneOutrosNumeros.isChecked();

        boolean etapaComplementarCompleta =
                calcularEtapaComplementarCompleta();

        viewModel.onEvent(
                new SimulaUiEvent.CartelaAlterada(
                        parametroJogo.getConcurso().getModalidade(),
                        dezenasSelecionadas != null
                                ? dezenasSelecionadas.size()
                                : 0,
                        qtdDezenasPossiveisParaEstado,
                        qtdTotalSelecionadosSuperSete,
                        qtdPartidasSelecionadas,
                        qtdPartidasTotal,
                        duplosLoteca,
                        triplosLoteca,
                        possuiPlacarLotogol,
                        equipeSelecionadaValida,
                        isEscolhaTimeCoracaoSurpresinha,
                        outrosNumerosSelecionado,
                        exibirOutrosNumeros,
                        etapaComplementarCompleta,
                        dezenasSelecionadas
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
                instanceof CompletarApostaEffect.AbrirMesDeSorte) {

            parentActivity.mesDeSorteFragment(
                    dezenasSelecionadas
            );

            consumirComandoCompletarAposta();
            return;
        }

        if (state.getComandoCompletarAposta()
                instanceof CompletarApostaEffect.AbrirTrevos) {

            parentActivity.trevosFragment(
                    dezenasSelecionadas
            );

            consumirComandoCompletarAposta();
            return;
        }

        if (state.getComandoCompletarAposta()
                instanceof CompletarApostaEffect.MostrarEscolhaTimeCoracao) {

            parentActivity.timesFragment(
                    dezenasSelecionadas
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
            return;
        }

        if (state.getComandoCompletarAposta()
                instanceof CompletarApostaEffect.ZerarLoteca) {

            zeraLoteca();

            consumirComandoCompletarAposta();
        }
    }

    private void consumirComandoCompletarAposta() {
        viewModel.onEvent(
                new SimulaUiEvent.ComandoCompletarApostaConsumido()
        );
    }

    private void solicitarSalvarApostaFavorita(
            String nome
    ) {

        ApostaFavoritaDTO apostaFavorita =
                criarApostaFavoritaDTO();

        Object numerosSelecionados =
                obterNumerosSelecionadosFavoritos();

        viewModel.onEvent(
                new SimulaUiEvent.SalvarApostaFavoritaSolicitado(
                        apostaFavorita,
                        nome,
                        numerosSelecionados
                )
        );
    }

    private ApostaFavoritaDTO criarApostaFavoritaDTO() {

        ApostaFavoritaDTO aposta =
                new ApostaFavoritaDTO();

        if (parametroJogo.isTipoJogo(
                ModalidadeEnum.TIMEMANIA
        ) && getStateAtual().getEquipeSelecionada() != null) {

            aposta.setTimeDoCoracao(
                    getStateAtual().getEquipeSelecionada()
            );
        }

        if (parametroJogo.isTipoJogo(
                ModalidadeEnum.MAIS_MILIONARIA
        )) {

            aposta.setParametroTrevo(
                    new ParametroTrevo()
            );
        }

        return aposta;
    }

    private Object obterNumerosSelecionadosFavoritos() {

        if (parametroJogo.isTipoJogo(
                ModalidadeEnum.SUPER_7
        )) {

            return ListaUtils.pegaListaSuperSete(
                    listaAdaptersSuperSete
            );
        }

        return new ArrayList<>(
                dezenasSelecionadas
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
            renderLimparSuperSete(state);
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
            viewModel.onEvent(
                    new SimulaUiEvent.LimpezaApostaRenderizada()
            );
        }
    }

    private void renderLimparNumerosCartela(
            SimulaUiState state
    ) {
        dezenasSelecionadas.clear();

        if (state.getDezenasSelecionadas() != null) {
            dezenasSelecionadas.addAll(state.getDezenasSelecionadas());
        }

        if (simularDezenasAdapter != null) {
            simularDezenasAdapter.atualizaSelecionados(dezenasSelecionadas);
        }

    }

    private void renderLimparSuperSete(
            SimulaUiState state
    ) {
        if (listaGridsSuperSete != null) {
            for (ExpandableHeightGridView grid : listaGridsSuperSete) {
                if (grid.getAdapter() instanceof SuperSeteAdapter) {
                    ((SuperSeteAdapter) grid.getAdapter()).limpaNumeros();
                }
            }
        }

        qtdTotalSelecionadosSuperSete =
                state.getQtdTotalSelecionadosSuperSete();

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

        if (viewModel == null) {
            return;
        }

        viewModel.onEvent(
                new SimulaUiEvent.ValorApostaAlterado(
                        valorSeguro
                )
        );
    }


    private void limparOpcaoOutrosNumerosLayout() {
        if (parametroJogo.isTipoJogo(ModalidadeEnum.LOTOMANIA) && opcaoOutrosNumerosLayout.getVisibility() == View.VISIBLE) {
            setVisibilityCheckedNumerosLayout();
            atualizarValorENotificarEstadoCartela();
        }
    }

    private void setVisibilityCheckedNumerosLayout() {
        opcaoOutrosNumerosLayout.setVisibility(View.GONE);
        selecioneOutrosNumeros.setChecked(Boolean.FALSE);
    }

    private void onClickOutrosNumerosLayout() {

        opcaoOutrosNumerosLayout.setOnClickListener(v -> viewModel.onEvent(
                new SimulaUiEvent.ToggleOutrosNumeros()
        ));
    }

    private void preencheNumerosAleatorios() {
        AlertDialogUtils.show(getActivity());

        dezenasSelecionadas = ListaUtils.orderAscDezenas(dezenasSelecionadas);
        model.preencheNumerosAleatorios(getStateAtual().getQtdDezenasPossiveisSelecionado(), dezenasSelecionadas, parametroJogo.getPrognosticoMaximo(),
                new OnSilceListener<List<Integer>>() {
                    @Override
                    public void success(List<Integer> list) {
                        AlertDialogUtils.dismiss();
                        isEcolhaTimeBtnAtivado = true;
                        dezenasSelecionadas.clear();
                        dezenasSelecionadas.addAll(list);
                        simularDezenasAdapter.atualizaSelecionados(dezenasSelecionadas);
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
        dezenasSelecionadas = ListaUtils.orderAscDezenas(dezenasSelecionadas);

        model.preencheNumerosAleatoriosSuperSete(getStateAtual().getQtdDezenasPossiveisSelecionado(), listaInteiros, parametroJogo.getConcurso().getModalidade().name(),
                new OnSilceListener<List<List<Integer>>>() {
                    @Override
                    public void success(List<List<Integer>> payload) {
                        AlertDialogUtils.dismiss();
                        isEcolhaTimeBtnAtivado = true;

                        preencheSuperSete(payload);
                        atualizarValorENotificarEstadoCartela();
                    }

                    @Override
                    public void error(VolleyError error) {
                        AlertDialogUtils.dismiss();
                    }
                });
    }

    private void atualizaSuperSetePrognosticoMaior(ArrayList<ArrayList<Integer>> listaInteiros) {
        int qtdSelecionada =
                getQtdTotalSelecionadosSuperSete(
                        listaInteiros
                );

        if (qtdSelecionada
                > getStateAtual()
                .getQtdDezenasPossiveisSelecionado()) {

            atualizarQuantidadeSelecionada(
                    qtdSelecionada
            );
            qtdTotalSelecionadosSuperSete = 0;
            limpaNumerosSuperSete();
        }

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
        qtdTotalSelecionadosSuperSete = 0;
        if (matrizSelecionada != null) {
            matrizSelecionada = null;
        }
        for (SuperSeteAdapter adapter : listaAdaptersSuperSete) {
            adapter.dezenasSelecionadas = numerosSelecionados.get(coluna);
            qtdTotalSelecionadosSuperSete += adapter.dezenasSelecionadas.size();
            adapter.notifyDataSetChanged();
            coluna++;
        }
    }

    private boolean validaSelecaoSuperSete() {
        //lista de cima igual aos selecionados e no minimo 1 por coluna
        boolean mostraSalvar = false;
        int contColunasComSelecao = 0;
        ArrayList<ArrayList<Integer>> listaSuperSete = ListaUtils.pegaListaSuperSete(listaAdaptersSuperSete);
        for (ArrayList<Integer> listaSelecionados : listaSuperSete) {
            if (!listaSelecionados.isEmpty()) {
                contColunasComSelecao++;
            }
        }
        if (contColunasComSelecao == 7) {
            mostraSalvar = true;
        }
        return mostraSalvar;
    }


//    private void configuraBotoesLotecaOuLotogol() {
//        botaoCompletar = parentActivity.getBotaoCompletar();
//        botaoCompletar.setVisibility(View.INVISIBLE);
//        String textoBotao;
//        if (parametroJogo.isTipoJogo(ModalidadeEnum.LOTECA)) {
//            textoBotao = getString(R.string.label_selecionar_times_aleatoriamente);
//        } else {
//            textoBotao = getString(R.string.label_complete_placar_aleatoriamente);
//        }
//        botaoCompletar.setText(ViewUtils.textCaixaSTDBold(getActivity(), textoBotao));
//    }

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

    private void limpaNumerosSuperSete() {
        if (listaGridsSuperSete != null) {
            for (ExpandableHeightGridView grid : listaGridsSuperSete) {
                ((SuperSeteAdapter) grid.getAdapter()).limpaNumeros();
            }
        }
        Button botaoLimpar =
                getBotaoLimparAposta();

        if (botaoLimpar != null) {
            botaoLimpar.setEnabled(false);
        }

        int quantidadeMinima =
                parametroJogo.getQuantidadeMinima() != null
                        ? parametroJogo.getQuantidadeMinima()
                        : 0;

        atualizarQuantidadeSelecionada(
                quantidadeMinima
        );

        qtdTotalSelecionadosSuperSete = 0;

        atualizaValorTotalAposta(
                quantidadeMinima,
                getStateAtual()
                        .getQtdConcursoSelecionado()
        );

        notificarEstadoCartelaAlterado(
                quantidadeMinima
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


    public void atualizaStatusBotaoFinalizar(Boolean isPickerPrognosticos) {

        if (parametroJogo.isTipoJogo(ModalidadeEnum.LOTOGOL)) {
            if (partidasSelecionadas.size() < parametroJogo.getPartidas().size()) {
                setBotaoCompletarEnabled(false);
            } else {
                setBotaoCompletarEnabled(true);
                atualizarValorENotificarEstadoCartela();
            }
        } else if (parametroJogo.isTipoJogo(ModalidadeEnum.TIMEMANIA)) {
                atualizarAdicionarCarinhoCompletarCartela(isPickerPrognosticos);
        } else if (parametroJogo.isTipoJogo(ModalidadeEnum.SUPER_7)) {
            if (matrizSelecionada != null) {
                preencheSuperSete(matrizSelecionada);
                matrizSelecionada = null;
            }
            if (validaSelecaoSuperSete() && qtdTotalSelecionadosSuperSete == getStateAtual().getQtdDezenasPossiveisSelecionado()) {
                atualizarValorENotificarEstadoCartela();
            } else {
                atualizarAdicionarCarinhoCompletarCartela(isPickerPrognosticos);
            }
        } else {
            atualizarAdicionarCarinhoCompletarCartela(isPickerPrognosticos);
        }
    }

    private void atualizaStatusBotaoFinalizarLoteca(Boolean mostrarBotao) {
        if (parametroJogo.isTipoJogo(ModalidadeEnum.LOTECA)) {
            int duplo = Integer.parseInt(parentActivity.getLabelValueDuplasLoteca().getText().toString());
            int triplo = Integer.parseInt(parentActivity.getLabelValueTriplasLoteca().getText().toString());

            if (partidasSelecionadas.size() == parametroJogo.getPartidas().size() && (duplo > 0 || triplo > 0)) {
                if (mostrarBotao) {
                    setBotaoCompletarEnabled(true);
                    atualizarValorENotificarEstadoCartela();
                } else {
                    setBotaoCompletarEnabled(false);
                }

            } else {
                setBotaoCompletarEnabled(false);
            }
        }
    }
    private void setBotaoCompletarEnabled(
            boolean enabled
    ) {
        Button button =
                getBotaoCompletar();

        if (button != null) {
            button.setEnabled(enabled);
        }
    }

    private void atualizarAdicionarCarinhoCompletarCartela(Boolean isPickerPrognosticos) {
        if (dezenasSelecionadas.size() == getStateAtual().getQtdDezenasPossiveisSelecionado() || isEscolhaTimeCoracaoSurpresinha) {
            if (parametroJogo.isTipoJogo(ModalidadeEnum.DIA_DE_SORTE)) {
                atualizarValorENotificarEstadoCartela();
            } else if (parametroJogo.isTipoJogo(ModalidadeEnum.MAIS_MILIONARIA)) {
                atualizarValorENotificarEstadoCartela();
            } else {
                atualizarValorENotificarEstadoCartela();
            }

        } else if (dezenasSelecionadas.size() > getStateAtual().getQtdDezenasPossiveisSelecionado()) {
            dezenasSelecionadas.clear();
            simularDezenasAdapter.notifyDataSetChanged();
            atualizarValorENotificarEstadoCartela();

            Button botaoLimpar =
                    getBotaoLimparAposta();

            if (botaoLimpar != null) {
                botaoLimpar.setEnabled(false);
            }
        } else {
            if (parametroJogo.isTipoJogo(ModalidadeEnum.SUPER_7) && isPickerPrognosticos) {
                atualizaSuperSetePrognosticoMaior(ListaUtils.pegaListaSuperSete(listaAdaptersSuperSete));
            }
            atualizarValorENotificarEstadoCartela();
        }
    }

    private void configuraListaDezenas(View fragmentView) {
        listaDezenas = fragmentView.findViewById(R.id.listaDezenas);
        recyclerViewPartida = fragmentView.findViewById(R.id.recyclerViewPartida);

        if (dezenas != null && !dezenas.isEmpty() && !parametroJogo.isTipoJogo(ModalidadeEnum.SUPER_7)) {
            simularDezenasAdapter = new ListaDezenaRecyclerView(dezenas, dezenasSelecionadas,
                    corFonteFundoBranco, onItemClickListener());
            listaDezenas.setAdapter(simularDezenasAdapter);

            RecyclerView.LayoutManager layoutDezenas = new GridLayoutManager(context, QUANTIDADE_COLUNA_LISTA);
            listaDezenas.setLayoutManager(layoutDezenas);
            listaDezenas.setNestedScrollingEnabled(false);
        } else {
            if (parametroJogo.isTipoJogo(ModalidadeEnum.LOTECA)) {
                EscudoBO.getInstance().carregaEscudos(context, this::configurarTimesLoteca);
                //configurarTimesLoteca();

                fragmentView.findViewById(R.id.layoutTitleLoteca).setVisibility(View.VISIBLE);
            } else if (parametroJogo.isTipoJogo(ModalidadeEnum.SUPER_7)) {
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
                        dezenasSelecionadas.contains(
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

                if (parametroJogo.isTipoJogo(ModalidadeEnum.TIMEMANIA)) {
                    isEcolhaTimeBtnAtivado = true;
                }


                if (simularDezenasAdapter != null) {
                    simularDezenasAdapter.atualizaSelecionados(
                            dezenasSelecionadas
                    );
                }

                atualizarValorENotificarEstadoCartela();

            } catch (Exception e) {
                Log.d(
                        "",
                        e.getLocalizedMessage()
                );
            }
        };
    }

    private void removerDezenaSelecionada(
            Dezena dezena,
            Integer valorSelecionado
    ) {

        dezenasSelecionadas.remove(
                valorSelecionado
        );

        dezena.setSelected(false);

        int quantidadeMinima =
                parametroJogo.getQuantidadeMinima() != null
                        ? parametroJogo.getQuantidadeMinima()
                        : 0;

        atualizarQuantidadeSelecionada(
                Math.max(
                        dezenasSelecionadas.size(),
                        quantidadeMinima
                ));

    }

    private void tentarAdicionarDezenaSelecionada(
            Dezena dezena,
            Integer valorSelecionado
    ) {

        int quantidadeAtualSelecionada =
                dezenasSelecionadas.size();

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
                    CartelaFragment2Listener.verificaNovoQtdDezenasMax(
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

        dezenasSelecionadas.add(
                valorSelecionado
        );

        dezena.setSelected(true);

        //            voltaSalvarApostaParaLayoutInicial();
    }

    private void configurarTimesLoteca() {
        LotecaAdapter lotecaAdapter = new LotecaAdapter(parametroJogo, this);
        recyclerViewPartida.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewPartida.setAdapter(lotecaAdapter);
        recyclerViewPartida.setExpanded(true);
    }

    private void configuraSuperSete(View viewLayout) {
        listaGridsSuperSete = cartelaModel.getListGridSuperSete(viewLayout);

        listaAdaptersSuperSete = cartelaModel.getListAdaptersSuperSete(typeGameColorLight, parentActivity);

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
                        qtdTotalSelecionadosSuperSete--;
                        if (qtdTotalSelecionadosSuperSete >= parametroJogo.getQuantidadeMinima()) {
                            atualizarQuantidadeSelecionada(getStateAtual().getQtdDezenasPossiveisSelecionado() - 1);
                            int novaQuantidade =
                                    parentActivity.verificaNovoQtdDezenasMax(
                                            qtdTotalSelecionadosSuperSete
                                    );

                            atualizarQuantidadeSelecionada(
                                    novaQuantidade
                            );
                        }
                        atualizarValorENotificarEstadoCartela();
                    } else {
                        DialogUtils.dialogEntendi(getActivity(),
                                getString(R.string.msg_bloqueio_coluna_super_sete_desmarcar).replace(
                                        "{qtdColunas}",
                                        String.valueOf(listaSelecionados.size())));
                    }
                } else {
                    int maxNumerosParaSelecionar = parametroJogo.getQuantidadeMaxima();
                    if (qtdTotalSelecionadosSuperSete == getStateAtual().getQtdDezenasPossiveisSelecionado()) {
                        // VERIFICA SE TEM OPÇÃO DE SELECIONAR MAIS DEZENAS

                        int quantidadeAtual =
                                getStateAtual()
                                        .getQtdDezenasPossiveisSelecionado();

                        int novaQuantidade =
                                CartelaFragment2Listener
                                        .verificaNovoQtdDezenasMax(
                                                quantidadeAtual
                                        );

                        if (quantidadeAtual < maxNumerosParaSelecionar) {
                            if (listaSelecionados.size() < SuperSeteAdapter.QTD_MAX_POR_COLUNA) {
                                if (listaSelecionados.size() < adapter.getQtdColunasMaximas((novaQuantidade))) {
                                    atualizarQuantidadeSelecionada(novaQuantidade);
                                    atualizarValorENotificarEstadoCartela();
                                    listaSelecionados.add(valorSelecionado);
                                    qtdTotalSelecionadosSuperSete++;

                                    if ((qtdTotalSelecionadosSuperSete == (getStateAtual().getQtdDezenasPossiveisSelecionado())) && (!isAvisou)) {
                                        DialogUtils.dialogEntendi(getActivity(), getString(R.string.msg_qtd_max_ultrapassada));
                                        isAvisou = true;
                                    }
                                } else {
                                    DialogUtils.dialogEntendi(getActivity(), getString(R.string.msg_bloqueio_coluna_super_sete_marcar).replace(
                                            "{qtdColunas}",
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
                                qtdTotalSelecionadosSuperSete++;
                            } else {
                                DialogUtils.dialogEntendi(getActivity(), getString(R.string.msg_bloqueio_coluna_super_sete_marcar).replace(
                                        "{qtdColunas}",
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

    @Override
    public void clickEquipeLoteca(ParametroPartida parametroPartida) {
        if (parametroPartida.isSelecionado()) {
            if (parametroPartida.getQtdItensSelecionados() == 1) {
                parentActivity.setLabelValueSimplesLoteca(String.valueOf(Integer.parseInt(parentActivity.getLabelValueSimplesLoteca().getText().toString()) + 1));
                if (parametroPartida.getQtdItensSelecionadosAnterior() == 2) {
                    parentActivity.setLabelValueDuplasLoteca(String.valueOf(Integer.parseInt(parentActivity.getLabelValueDuplasLoteca().getText().toString()) - 1));
                }
            } else if (parametroPartida.getQtdItensSelecionados() == 2) {
                parentActivity.setLabelValueDuplasLoteca(String.valueOf(Integer.parseInt(parentActivity.getLabelValueDuplasLoteca().getText().toString()) + 1));
                if (parametroPartida.getQtdItensSelecionadosAnterior() == 3) {
                    parentActivity.setLabelValueTriplasLoteca(String.valueOf(Integer.parseInt(parentActivity.getLabelValueTriplasLoteca().getText().toString()) - 1));
                } else {
                    parentActivity.setLabelValueSimplesLoteca(String.valueOf(Integer.parseInt(parentActivity.getLabelValueSimplesLoteca().getText().toString()) - 1));
                }
            } else if (parametroPartida.getQtdItensSelecionados() == 3) {
                parentActivity.setLabelValueDuplasLoteca(String.valueOf(Integer.parseInt(parentActivity.getLabelValueDuplasLoteca().getText().toString()) - 1));
                parentActivity.setLabelValueTriplasLoteca(String.valueOf(Integer.parseInt(parentActivity.getLabelValueTriplasLoteca().getText().toString()) + 1));
            }
            partidasSelecionadas.add(parametroPartida);
        } else {
            parentActivity.setLabelValueSimplesLoteca(String.valueOf(Integer.parseInt(parentActivity.getLabelValueSimplesLoteca().getText().toString()) - 1));
            partidasSelecionadas.remove(parametroPartida);
        }
        int somaTotais;
        int valueSimples = Integer.parseInt((String.valueOf(Integer.parseInt(parentActivity.getLabelValueSimplesLoteca().getText().toString()))));
        int valueDuplas = Integer.parseInt((String.valueOf(Integer.parseInt(parentActivity.getLabelValueDuplasLoteca().getText().toString()))));
        int valueTriplas = Integer.parseInt((String.valueOf(Integer.parseInt(parentActivity.getLabelValueTriplasLoteca().getText().toString()))));
        somaTotais = valueSimples + valueDuplas + valueTriplas;

        parentActivity.setLabelValueJogosLoteca(String.valueOf(Integer.parseInt(String.valueOf(somaTotais))));

        valorTotalJogos = parentActivity.getLabelValueJogosLoteca();
        valorTotalSimples = parentActivity.getLabelValueSimplesLoteca();
        valorTotalDuplas = parentActivity.getLabelValueDuplasLoteca();
        valorTotalTriplas = parentActivity.getLabelValueTriplasLoteca();


        if (somaTotais >= 14) {
            valorTotalJogos.setTextColor(ContextCompat.getColor(context,R.color.verdeazul));
            valorTotalSimples.setTextColor(ContextCompat.getColor(context,R.color.verdeazul));
        } else {
            valorTotalJogos.setTextColor(ContextCompat.getColor(context,R.color.loteca_claro_mkp));
            valorTotalSimples.setTextColor(ContextCompat.getColor(context,R.color.loteca_claro_mkp));
        }
        if (valueDuplas == 0 && valueTriplas == 0) {
            valorTotalDuplas.setTextColor(ContextCompat.getColor(context,R.color.loteca_claro_mkp));
            valorTotalTriplas.setTextColor(ContextCompat.getColor(context,R.color.loteca_claro_mkp));
        } else {
            valorTotalDuplas.setTextColor(ContextCompat.getColor(context,R.color.verdeazul));
            valorTotalTriplas.setTextColor(ContextCompat.getColor(context,R.color.verdeazul));
        }


        Boolean mostraBotao = atualizaValorApostaLoteca();
        atualizaStatusBotaoFinalizarLoteca(mostraBotao);
        atualizarValorENotificarEstadoCartela();
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
        setBotaoCompletarEnabled(false);
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

    public void setEscolhaTimeCoracaoSurpresinha(boolean escolhaTimeCoracaoSurpresinha) {
        isEscolhaTimeCoracaoSurpresinha = escolhaTimeCoracaoSurpresinha;
    }

    private boolean atualizaValorApostaLoteca() {

        boolean mostraBotao = false;

        int duplos =
                getIntTextSafe(
                        parentActivity.getLabelValueDuplasLoteca()
                );

        int triplos =
                getIntTextSafe(
                        parentActivity.getLabelValueTriplasLoteca()
                );

        BigDecimal valorCalculado =
                BigDecimal.ZERO;

        if (duplos > 0 || triplos > 0) {

            for (ParametroValorApostaDTO parametroValorApostaDTO
                    : parametroJogo.getValoresAposta()) {

                if (parametroValorApostaDTO.getQuantidadeDuplos().equals(duplos)
                        && parametroValorApostaDTO.getQuantidadeTriplos().equals(triplos)) {

                    valorCalculado =
                            parametroValorApostaDTO.getValor();

                    mostraBotao = true;
                    break;
                }
            }

        } else if (parametroJogo.getValorApostaMinima() != null) {

            valorCalculado =
                    parametroJogo.getValorApostaMinima();
        }

        BigDecimal valorEmReais = valorCalculado != null
                ? valorCalculado
                : BigDecimal.ZERO;

        publicarValorAposta(valorEmReais);

        return mostraBotao;
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

            qtdTotalSelecionadosSuperSete =
                    getQtdTotalSelecionadosSuperSete(
                            apostaSuperSete.getMatrizNumerosSelecionados()
                    );

            configuraSuperSete(view);

            matrizSelecionada = new ArrayList<>();
            matrizSelecionada.addAll(
                    apostaSuperSete.getMatrizNumerosSelecionados()
            );

            atualizarQuantidadeSelecionada(
                    apostaSuperSete.getQuantidadeNumeros());

            atualizarQtdConcurso(apostaSuperSete.getQuantidadeTeimosinhas());

            publicarValorAposta(aposta.getValor());

            Button botaoLimpar =
                    getBotaoLimparAposta();

            if (botaoLimpar != null) {
                botaoLimpar.setEnabled(mostraLimparSuperSete());
            }
            notificarEstadoCartelaAlterado(
                    apostaSuperSete.getQuantidadeNumeros()
            );

            return;
        }

        if (CollectionUtils.isNotEmpty(aposta.getListaNumerosSelecionados())) {

            dezenasSelecionadas =
                    aposta.getListaNumerosSelecionados();


            simularDezenasAdapter.atualizaSelecionados(
                    dezenasSelecionadas
            );

            atualizarQuantidadeSelecionada(
                    aposta.getQuantidadeNumeros());

            atualizarQtdConcurso(aposta.getQuantidadeTeimosinhas());



            if (aposta.getTimeDoCoracao() != null) {
                isEcolhaTimeBtnAtivado = true;

                viewModel.onEvent(
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
        viewModel.onEvent(
                new SimulaUiEvent
                        .QtdConcursoAtualizadaInternamente(
                        quantidade
                )
        );
    }

    private Button getBotaoCompletar() {
        return parentActivity != null
                ? parentActivity.getBotaoCompletar()
                : null;
    }

    private Button getBotaoLimparAposta() {
        return parentActivity != null
                ? parentActivity.getBotaoLimparAposta()
                : null;
    }
}