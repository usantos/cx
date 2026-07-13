package br.gov.caixa.loterias.apostas.view.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.android.material.button.MaterialButton;

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
import br.gov.caixa.loterias.apostas.event.SimulaUiEvent;
import br.gov.caixa.loterias.apostas.model.bean.SessaoUsuario;
import br.gov.caixa.loterias.apostas.model.bo.DadosUsuarioBO;
import br.gov.caixa.loterias.apostas.model.bo.EscudoBO;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaFavoritaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.Dezena;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroEquipe;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroJogoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroPartida;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroTrevo;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroValorApostaDTO;
import br.gov.caixa.loterias.apostas.model.model.CartelaModel;
import br.gov.caixa.loterias.apostas.model.model.SimularApostaModel;
import br.gov.caixa.loterias.apostas.state.ComandoCompletarAposta;
import br.gov.caixa.loterias.apostas.state.SimulaUiState;
import br.gov.caixa.loterias.apostas.utils.AlertDialogExperimenteLogarSingleton;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.ApostaUtils;
import br.gov.caixa.loterias.apostas.utils.BarraTituloDTO;
import br.gov.caixa.loterias.apostas.utils.Constantes;
import br.gov.caixa.loterias.apostas.utils.DialogUtils;
import br.gov.caixa.loterias.apostas.utils.InputUtils;
import br.gov.caixa.loterias.apostas.utils.IntentUtil;
import br.gov.caixa.loterias.apostas.utils.ListaUtils;
import br.gov.caixa.loterias.apostas.utils.TextViewUtils;
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
import br.gov.caixa.loterias.apostas.view.listener.OnDialogBotaoListener;
import br.gov.caixa.loterias.apostas.view.listener.OnDialogFavoritarListener;
import br.gov.caixa.loterias.apostas.view.listener.OnEscudoListener;
import br.gov.caixa.loterias.apostas.view.listener.OnItemClickListener;
import br.gov.caixa.loterias.apostas.view.listener.custom.LoteriasSensorEventListener;
import br.gov.caixa.loterias.apostas.viewModel.SimulaViewModel;

public class CartelaFragment2 extends Fragment implements OnClickListener, LocateAdapterListener, LotogolListener {
    private static final int QUANTIDADE_COLUNA_LISTA = 6;
    private SensorManager mSensorManager;
    private BigDecimal valorEmReais = BigDecimal.ZERO;
    private LotecaAdapter lotecaAdapter;

    private final SensorEventListener mSensorListener = new LoteriasSensorEventListener(0.00f,
            SensorManager.GRAVITY_EARTH,
            SensorManager.GRAVITY_EARTH).getSensor();

    private List<Dezena> dezenas = new ArrayList<>();
    private List<Integer> dezenasSelecionadas = new ArrayList<>();
    private List<Integer> dezenasApostaEspelho = new ArrayList<>();
    private List<List<Integer>> matrizSelecionada;
    private List<ParametroEquipe> listaEquipefiltro;
    private HashSet<ParametroPartida> partidasSelecionadas;

    private CartelaFragmentListener CartelaFragment2Listener;

    private ParametroEquipe equipeSelecionada = null;
    private ParametroJogoDTO parametroJogo;
    private ModalidadeEnum tipoJogo;
    private TimeCoracaoAdapter timeCoracaoAdapter;

    private SimulaActivity parentActivity;
    private AppCompatCheckBox selecioneOutrosNumeros;
    private View view;
    private EditText informeNomeTime;
    private RelativeLayout opcaoOutrosNumerosLayout, rlColunasSuperSete;
    private LinearLayout selecioneTimeCoracaoLayout;
    private RecyclerView mGridTimeCoracao;
    private RecyclerView listaDezenas;
    private ArrayList<ExpandableHeightGridView> listaGridsSuperSete;
    private ArrayList<SuperSeteAdapter> listaAdaptersSuperSete;
    private ExpandableHeightRecyclerView recyclerViewPartida;
    private ScrollView svSuperSete;
    private int qtdTotalSelecionadosSuperSete = 0;

    private boolean isEcolhaTimeBtnAtivado;
    private boolean isTelaSelecaoTimeAtivado;
    private boolean isEscolhaTimeCoracaoSurpresinha;
    private boolean isAvisou = false;
    public BigDecimal valorTotalAposta;
    public int typeGameColorLight, typeGameColorDark;
    public int corFonteFundoBranco, corFonteFundoClaro, corFonteFundoEscuro;
    public Button botaoCompletar, botaoAdicionar, botaoLimparAposta, botaoFavoritar;
    public TextView valorAposta, valorTotalJogos, valorTotalSimples, valorTotalDuplas, valorTotalTriplas;
    public ListaDezenaRecyclerView simularDezenasAdapter;
    public String dataSorteioAtual;

    private SimularApostaModel model;
    private CartelaModel cartelaModel;
    private View rodape;
    private boolean mudouTamanhoValor = false;
    private SimulaViewModel viewModel;

    public BarraTituloDTO barraTituloDTO = new BarraTituloDTO();
    private TextView textSelecionados;

    public CartelaFragment2() {
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
                    new OnDialogBotaoListener() {
                        @Override
                        public void onButtonClick(
                                DialogInterface dialog,
                                int which
                        ) {
                            Intent intent =
                                    IntentUtil.getIntentLimpandoPilhaActivities(
                                            getActivity(),
                                            PrincipalActivity.class
                                    );

                            getActivity().startActivity(intent);
                            getActivity().finish();
                        }
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

            if (parentActivity.getQtdConcursos() > 0) {
                valorCalculado =
                        valorCalculado.multiply(
                                BigDecimal.valueOf(
                                        parentActivity.getQtdConcursos()
                                )
                        );
            }

            parentActivity.atualizaValorAposta(
                    parametroValorAposta
            );

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
                && selecioneOutrosNumeros != null
                && selecioneOutrosNumeros.isChecked()) {

            valorCalculado =
                    valorCalculado.multiply(
                            Constantes.DOIS_BIG_DECIMAL
                    );
        }

        publicarValorAposta(valorCalculado);
    }

    private void loadParametroJogo() {
        if (parametroJogo == null) {
            SessaoUsuario sessaoUsuario = SessaoUsuario.getInstance();
            parametroJogo = ViewUtils.getParametroSimulacao(sessaoUsuario, tipoJogo).getParametroJogo();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSensorManager = (SensorManager) this.getContext().getSystemService(Context.SENSOR_SERVICE);
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

        if (parametroJogo != null && dezenas.size() == 0 && parametroJogo.getPrognosticoMaximo() != null
                && !parametroJogo.isTipoJogo(ModalidadeEnum.SUPER_7)) {
            for (int i = 1; i <= parametroJogo.getPrognosticoMaximo(); i++) {
                if (parametroJogo.isTipoJogo(ModalidadeEnum.LOTOMANIA) && i == 100) {
                    dezenas.add(new Dezena("" + i, Boolean.FALSE, "00"));
                } else {
                    dezenas.add(new Dezena("" + i, Boolean.FALSE, "" + i));
                }
            }
        }

        if (parametroJogo != null && dezenas.size() == 0 && parametroJogo.getPrognosticoMaximo() != null && !(tipoJogo == ModalidadeEnum.SUPER_7)) {
            dezenas = VolanteUtils.getDezenas(tipoJogo, parametroJogo.getPrognosticoMaximo());
        }

        model = new SimularApostaModel(getActivity());
        cartelaModel = new CartelaModel();
        partidasSelecionadas = new HashSet<>();

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
                configuraBotoesLotecaOuLotogol();
            }
        }

        if (parametroJogo.isTipoJogo(ModalidadeEnum.LOTOMANIA)) {
            opcaoOutrosNumerosLayout = view.findViewById(R.id.opcaoOutrosNumerosLayout);
            selecioneOutrosNumeros = opcaoOutrosNumerosLayout.findViewById(R.id.selcioneOutrosNumeros);
            TextView infoSelecioneOutrosNumeros = opcaoOutrosNumerosLayout.findViewById(R.id.infoSelecioneOutrosNumeros);
            infoSelecioneOutrosNumeros.setText(ViewUtils.fromHtml(Constantes.INFO_OUTROS_50_NUMEROS));
            onClickOutrosNumerosLayout();
            if (dezenasSelecionadas.size() == parentActivity.qtdDezenasPossiveisSelecionado) {
                setVisibilityCheckedNumerosLayout(View.VISIBLE);
            } else {
                setVisibilityCheckedNumerosLayout(View.GONE);
            }
        }

        if (parametroJogo.isTipoJogo(ModalidadeEnum.SUPER_7)) {
            rlColunasSuperSete = view.findViewById(R.id.rl_colunas_super_7);
            svSuperSete = view.findViewById(R.id.sv_super_sete);
            rlColunasSuperSete.setVisibility(View.VISIBLE);
            svSuperSete.setVisibility(View.VISIBLE);
        }

        configuraBotoes(view);
        // Chama a tela de escolha de time
        if (isEscolhaTimeCoracaoSurpresinha) {
            mostrarCartelaEscolhaTimeCoracao();
        } else {
            isTelaSelecaoTimeAtivado = false;
            limparTimeSelecionado();
        }

        if (!isEscolhaTimeCoracaoSurpresinha) {
            configuraListaDezenas(view);
        }

        if (parentActivity.getAposta() != null) {
            editaAposta();
        } else {
            inicializarValorApostaMinima();
            notificarEstadoCartelaAlterado();
        }
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
                parentActivity.qtdDezenasPossiveisSelecionado > 0
                        ? parentActivity.qtdDezenasPossiveisSelecionado
                        : getQuantidadeMinimaSafe();

        atualizaValorTotalAposta(
                qtdMinima,
                parentActivity.qtdConcursoSelecionado
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

            typeGameColorLight = (int) dataBundle.get("typeGameColorLight");
            typeGameColorDark = (int) dataBundle.get("typeGameColorDark");
            corFonteFundoBranco = (int) dataBundle.get(SimulaActivity.COR_FONTE_FUNDO_BRANCO);
            corFonteFundoClaro = (int) dataBundle.get(SimulaActivity.COR_FONTE_FUNDO_CLARO);
            corFonteFundoEscuro = (int) dataBundle.get(SimulaActivity.COR_FONTE_FUNDO_ESCURO);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_cartela_novo, container, false);
        rodape = view.findViewById(R.id.rodape_espacamento);
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
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            CartelaFragment2Listener = (CartelaFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement CartelaFragment2Listener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        CartelaFragment2Listener = null;
    }

    private void identificarTipoJogo() {
        if (parentActivity.getIntent() != null) {
            tipoJogo = (ModalidadeEnum) parentActivity.getIntent().getSerializableExtra("tipoAposta");
        }
    }

    private void preencherVisibilidaeGridView(int visibilidadeEscolhaTimeCoracao, int visibilidadeSelecioneTimeCoracao, int visibilidadeEscolhaLoteca,
                                              int visibilidadeDezanas) {
        if (mGridTimeCoracao != null) {
            mGridTimeCoracao.setVisibility(visibilidadeEscolhaTimeCoracao);
        }

        if (selecioneTimeCoracaoLayout != null) {
            selecioneTimeCoracaoLayout.setVisibility(visibilidadeSelecioneTimeCoracao);
        }

        if (recyclerViewPartida != null) {
            recyclerViewPartida.setVisibility(visibilidadeEscolhaLoteca);

        }

        if (listaDezenas != null) {
            listaDezenas.setVisibility(visibilidadeDezanas);
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
                        equipeSelecionada
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
                    getValorTotalApostaAtual(),
                    parentActivity.qtdConcursoSelecionado,
                    ListaUtils.pegaListaSuperSete(
                            listaAdaptersSuperSete
                    )
            );
        }

        return getApostaPreenchida();
    }

    private IdentificaoDeUmaApostaDas8Modalidades getApostaPreenchida() {

        BigDecimal valorAtual =
                getValorTotalApostaAtual();

        if (parametroJogo.isTipoJogo(ModalidadeEnum.DIA_DE_SORTE)) {

            return ApostaUtils.getApostaDiaDeSorte(
                    parametroJogo,
                    dezenasSelecionadas,
                    valorAtual,
                    parentActivity.qtdConcursoSelecionado,
                    parentActivity.getMesDeSorteCartela()
            );

        } else if (parametroJogo.isTipoJogo(ModalidadeEnum.LOTECA)) {

            return ApostaUtils.getApostaLoteca(
                    parametroJogo,
                    dezenasSelecionadas,
                    valorAtual,
                    parentActivity.qtdConcursoSelecionado,
                    partidasSelecionadas
            );

        } else if (parametroJogo.isTipoJogo(ModalidadeEnum.LOTOGOL)) {

            return ApostaUtils.getApostaLotogol(
                    parametroJogo,
                    dezenasSelecionadas,
                    valorAtual,
                    parentActivity.qtdConcursoSelecionado,
                    partidasSelecionadas
            );

        } else if (parametroJogo.isTipoJogo(ModalidadeEnum.TIMEMANIA)) {

            return ApostaUtils.getApostaTimemania(
                    parametroJogo,
                    dezenasSelecionadas,
                    valorAtual,
                    parentActivity.qtdConcursoSelecionado,
                    equipeSelecionada
            );

        } else {

            return ApostaUtils.getApostaPreenchida(
                    parametroJogo,
                    dezenasSelecionadas,
                    valorAtual,
                    parentActivity.qtdConcursoSelecionado
            );
        }
    }

    private void zeraLoteca() {
        limparPartidadasSelecionasLoteca(Boolean.FALSE);
        limparOpcaoOutrosNumerosLayout();
        atualizarValorENotificarEstadoCartela();
        ocutarSalvarAposta();
    }

    // METODO DO PROTOCOLO ONCLICK
    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btnCompletar: {
                solicitarCompletarAposta();
            }
            break;

            case R.id.btnAdicionarCarrinho: {
                solicitarAdicionarCarrinhoPorBotao();
            }
            break;

            case R.id.btnLimpar: {
                viewModel.onEvent(
                        new SimulaUiEvent.LimparApostaSolicitado()
                );
            }
            break;

            case R.id.favoritar_aposta: {
                viewModel.onEvent(
                        new SimulaUiEvent.FavoritarApostaClicado()
                );
            }
            break;

            case R.id.salvarApostaLayout: {
                RelativeLayout salvarApostaLayout = parentActivity.getSalvarApostaLayout();

                ViewGroup.LayoutParams relativelayoutParams = salvarApostaLayout.getLayoutParams();

                final float scale = parentActivity.getResources().getDisplayMetrics().density;
                if (relativelayoutParams.height != ((int) (140 * scale + 0.5f))) {
                    relativelayoutParams.height = (int) (140 * scale + 0.5f);
                } else {
                    relativelayoutParams.height = (int) (64 * scale + 0.5f);
                }

                salvarApostaLayout.setLayoutParams(relativelayoutParams);

                if (!parametroJogo.isTipoJogo(ModalidadeEnum.MAIS_MILIONARIA)) {
                    salvarApostaLayout.findViewById(R.id.btnSalvarAposta).setOnClickListener(this);
                }

                if (!DadosUsuarioBO.checarUsuarioLogado(parentActivity)) {
                    AlertDialogExperimenteLogarSingleton.show(getActivity(), false, null);
                }
            }
            break;

            case R.id.btnSalvarAposta: {
                EditText nomeAposta = parentActivity.getEditNomeAposta();

                if (!TextUtils.isEmpty(nomeAposta.getText().toString())) {
                    ApostaFavoritaDTO aposta = new ApostaFavoritaDTO();
                    if (parametroJogo.isTipoJogo(ModalidadeEnum.TIMEMANIA) && equipeSelecionada != null) {
                        aposta.setTimeDoCoracao(equipeSelecionada);
                    }

                    if (parametroJogo.isTipoJogo(ModalidadeEnum.MAIS_MILIONARIA)) {
                        aposta.setParametroTrevo(new ParametroTrevo());
                    }

                    if (parametroJogo.isTipoJogo(ModalidadeEnum.SUPER_7)) {
                        parentActivity.salvarAposta(aposta, nomeAposta.getText().toString(), ListaUtils.pegaListaSuperSete(listaAdaptersSuperSete));
                    } else {
                        parentActivity.salvarAposta(aposta, nomeAposta.getText().toString(), dezenasSelecionadas);

                    }
                } else {
                    DialogUtils.dialogEntendi(getContext(), getString(R.string.msg_favor_inserir_nome_aposta));
                }
            }

        }
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
                equipeSelecionada != null
                        && equipeSelecionada.isSelecionado();

        viewModel.onEvent(
                new SimulaUiEvent.CompletarApostaClicado(
                        parametroJogo.getConcurso().getModalidade(),
                        dezenasSelecionadas != null ? dezenasSelecionadas.size() : 0,
                        parentActivity.qtdDezenasPossiveisSelecionado,
                        qtdTotalSelecionadosSuperSete,
                        qtdPartidasSelecionadas,
                        qtdPartidasTotal,
                        isEscolhaTimeCoracaoSurpresinha,
                        isEcolhaTimeBtnAtivado,
                        isTelaSelecaoTimeAtivado,
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
                equipeSelecionada != null
                        && equipeSelecionada.isSelecionado();

        viewModel.onEvent(
                new SimulaUiEvent.AdicionarCarrinhoClicado(
                        parametroJogo.getConcurso().getModalidade(),
                        dezenasSelecionadas != null ? dezenasSelecionadas.size() : 0,
                        parentActivity.qtdDezenasPossiveisSelecionado,
                        qtdTotalSelecionadosSuperSete,
                        qtdPartidasSelecionadas,
                        qtdPartidasTotal,
                        duplosLoteca,
                        triplosLoteca,
                        isEscolhaTimeCoracaoSurpresinha,
                        isTelaSelecaoTimeAtivado,
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
                equipeSelecionada != null
                        && equipeSelecionada.isSelecionado();

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
                        isTelaSelecaoTimeAtivado,
                        equipeSelecionadaValida,
                        isEscolhaTimeCoracaoSurpresinha
                )
        );
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

    private void renderValorAposta(
            SimulaUiState state
    ) {

        if (valorAposta == null) {
            return;
        }

        BigDecimal valor =
                state.getValorAposta() != null
                        ? state.getValorAposta()
                        : BigDecimal.ZERO;

        if (state.isValorGrande()) {

            if (!mudouTamanhoValor) {
                TextViewUtils.mudarTamanhoPorPorcentagem(
                        valorAposta,
                        -30
                );

                mudouTamanhoValor = true;
            }

        } else {

            if (mudouTamanhoValor) {
                TextViewUtils.mudarTamanhoPorPorcentagem(
                        valorAposta,
                        30
                );

                mudouTamanhoValor = false;
            }
        }

        ViewUtils.setMoedaFormatHtml(
                valor,
                valorAposta
        );

        valorAposta.setTextAlignment(
                View.TEXT_ALIGNMENT_CENTER
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

        renderValorAposta(state);
        renderEstadoBotoes(state);
        renderTextoSelecionados(state);
        renderLimpezaAposta(state);
        renderCompletarAposta(state);
        renderDialogFavoritarAposta(state);
    }

    private void renderDialogFavoritarAposta(
            SimulaUiState state
    ) {

        if (!state.isExibirDialogFavoritarAposta()) {
            return;
        }

        Dialog dialog =
                DialogUtils.buildDialogFavoritar(
                        requireContext(),
                        getString(R.string.favoritar_aposta),
                        getString(R.string.exemplo_aposta),
                        new OnDialogFavoritarListener() {

                            @Override
                            public void Confirmar(
                                    String nome,
                                    boolean manterSurpresinhas
                            ) {

                                if (TextUtils.isEmpty(nome)) {

                                    DialogUtils.dialogEntendi(
                                            getContext(),
                                            getString(
                                                    R.string.msg_favor_inserir_nome_aposta
                                            )
                                    );

                                    return;
                                }

                                solicitarSalvarApostaFavorita(
                                        nome
                                );
                            }

                            @Override
                            public void Cancelar() {
                            }
                        },
                        false
                );

        if (dialog != null) {
            dialog.show();
        }

        viewModel.onEvent(
                new SimulaUiEvent.DialogFavoritarApostaConsumido()
        );
    }
    private void renderTextoSelecionados(
            SimulaUiState state
    ) {

        if (textSelecionados == null) {
            return;
        }

        textSelecionados.setText(
                state.getTextoSelecionados()
        );
    }

    private void renderCompletarAposta(
            SimulaUiState state
    ) {

        if (state.getComandoCompletarAposta() == null) {
            return;
        }

        if (state.getComandoCompletarAposta()
                instanceof ComandoCompletarAposta.PreencherNumerosAleatorios) {

            preencheNumerosAleatorios();
            consumirComandoCompletarAposta();
            return;
        }

        if (state.getComandoCompletarAposta()
                instanceof ComandoCompletarAposta.PreencherSuperSete) {

            preencheNumerosAleatoriosSuperSete(
                    ListaUtils.pegaListaSuperSete(listaAdaptersSuperSete)
            );

            consumirComandoCompletarAposta();
            return;
        }

        if (state.getComandoCompletarAposta()
                instanceof ComandoCompletarAposta.ApostaProntaParaAdicionar) {

            ComandoCompletarAposta.ApostaProntaParaAdicionar comando =
                    (ComandoCompletarAposta.ApostaProntaParaAdicionar)
                            state.getComandoCompletarAposta();

            consumirComandoCompletarAposta();

            solicitarAdicionarCarrinho();

            if (comando.isLimparLotecaDepois()) {
                zeraLoteca();
            }

            return;
        }

        if (state.getComandoCompletarAposta()
                instanceof ComandoCompletarAposta.AbrirMesDeSorte) {

            parentActivity.mesDeSorteFragment(
                    dezenasSelecionadas,
                    false
            );

            consumirComandoCompletarAposta();
            return;
        }

        if (state.getComandoCompletarAposta()
                instanceof ComandoCompletarAposta.AbrirTrevos) {

            parentActivity.trevosFragment(
                    dezenasSelecionadas
            );

            consumirComandoCompletarAposta();
            return;
        }

        if (state.getComandoCompletarAposta()
                instanceof ComandoCompletarAposta.MostrarEscolhaTimeCoracao) {

            mostrarCartelaEscolhaTimeCoracao();
            timemaniaTimeSelecionadoEdicao();
            parentActivity.proximaPagina();

            consumirComandoCompletarAposta();
            return;
        }

        if (state.getComandoCompletarAposta()
                instanceof ComandoCompletarAposta.SelecionarTimeAleatorio) {

            selecionarTimeAleatorio();

            consumirComandoCompletarAposta();
            return;
        }

        if (state.getComandoCompletarAposta()
                instanceof ComandoCompletarAposta.AdicionarTimeSurpresa) {

            parentActivity.adicionarTimeSurpresa(
                    equipeSelecionada
            );

            consumirComandoCompletarAposta();
            return;
        }

        if (state.getComandoCompletarAposta()
                instanceof ComandoCompletarAposta.ZerarLoteca) {

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
        ) && equipeSelecionada != null) {

            aposta.setTimeDoCoracao(
                    equipeSelecionada
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

        if (state.isLimparTimeCoracao()) {
            renderLimparTimeCoracao();
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

        voltaSalvarApostaParaLayoutInicial();
    }

    private void renderLimparTimeCoracao() {

        if (equipeSelecionada != null) {
            equipeSelecionada.setSelecionado(false);
        }

        equipeSelecionada = null;

        if (informeNomeTime != null) {
            informeNomeTime.setText(StringUtils.EMPTY);
            informeNomeTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }

        if (timeCoracaoAdapter != null) {
            timeCoracaoAdapter.notifyDataSetChanged();
        }

        voltaSalvarApostaParaLayoutInicial();
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

        voltaSalvarApostaParaLayoutInicial();
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

        voltaSalvarApostaParaLayoutInicial();
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

    private void renderEstadoBotoes(
            SimulaUiState state
    ) {

        boolean mostrarFavoritar =
                state.isMostrarSalvarAposta();

        if (botaoFavoritar != null) {

            botaoFavoritar.setVisibility(
                    mostrarFavoritar
                            ? View.VISIBLE
                            : View.GONE
            );
        }

        if (botaoCompletar != null) {

            if (mostrarFavoritar) {

                botaoCompletar.setVisibility(
                        View.GONE
                );

            } else {

                boolean surpresinhaHabilitada =
                        state.isSurpresinhaHabilitada();

                botaoCompletar.setEnabled(
                        !surpresinhaHabilitada
                                && state.isBotaoCompletarHabilitado()
                );

                botaoCompletar.setVisibility(
                        surpresinhaHabilitada
                                ? View.GONE
                                : View.VISIBLE
                );
            }
        }

        if (botaoAdicionar != null) {

            boolean habilitado =
                    state.isBotaoAdicionarHabilitado();

            botaoAdicionar.setEnabled(
                    habilitado
            );

            atualizarVisualBotaoAdicionar(
                    habilitado
            );

        }

        if (botaoLimparAposta != null) {
            botaoLimparAposta.setEnabled(
                    state.isBotaoLimparHabilitado()
            );
        }

        if (parentActivity != null
                && parentActivity.getSalvarApostaLayout() != null) {

            parentActivity.getSalvarApostaLayout().setVisibility(
                    state.isMostrarSalvarAposta()
                            ? View.VISIBLE
                            : View.GONE
            );
        }

        if (rodape != null) {
            rodape.setVisibility(
                    state.isMostrarRodape()
                            ? View.VISIBLE
                            : View.GONE
            );
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

        if (viewModel == null) {
            return;
        }

        viewModel.onEvent(
                new SimulaUiEvent.ValorApostaAlterado(
                        valorSeguro
                )
        );
    }

    private void atualizarVisualBotaoAdicionar(boolean habilitado) {

        if (!(botaoAdicionar instanceof MaterialButton)) {
            return;
        }

        MaterialButton btn = (MaterialButton) botaoAdicionar;

        if (habilitado) {

            btn.setBackgroundTintList(
                    ContextCompat.getColorStateList(
                            requireContext(),
                            parentActivity.estilo.getCorClara()
                    )
            );

            btn.setTextColor(
                    ContextCompat.getColor(
                            requireContext(),
                            parentActivity.estilo.getCorFonteFundoClaro()
                    )
            );

            btn.setStrokeColor(
                    ContextCompat.getColorStateList(
                            requireContext(),
                            parentActivity.estilo.getCorEscura()
                    )
            );

        } else {

            // volta para o visual original do XML

            btn.setBackgroundTintList(
                    ContextCompat.getColorStateList(
                            requireContext(),
                            R.color.button_rodape
                    )
            );

            btn.setTextColor(
                    ContextCompat.getColorStateList(
                            requireContext(),
                            R.color.text_rodape
                    )
            );

            btn.setStrokeColor(
                    ContextCompat.getColorStateList(
                            requireContext(),
                            R.color.text_rodape
                    )
            );
        }
    }

    private BigDecimal getValorTotalApostaAtual() {

        if (viewModel == null
                || viewModel.getUiState() == null
                || viewModel.getUiState().getValue() == null
                || viewModel.getUiState().getValue().getValorAposta() == null) {

            return BigDecimal.ZERO;
        }

        return viewModel
                .getUiState()
                .getValue()
                .getValorAposta();
    }


    private void selecionarTimeAleatorio() {
        int randomInt = 0;
        Random randomGenerator = Utils.getRandom();
        randomInt = randomGenerator.nextInt(listaEquipefiltro.size() - 1);
        equipeSelecionada = listaEquipefiltro.get(randomInt);
        equipeSelecionada.setSelecionado(true);
        String nomeTime = String.format("%s-%s", equipeSelecionada.getNome(), equipeSelecionada.getUf());
        informeNomeTime.setText(nomeTime);
        timeCoracaoAdapter.notifyDataSetChanged();
        atualizarValorENotificarEstadoCartela();
        atualizarAdicionarCarinhoCompletarCartela(false);
    }

    private void limparOpcaoOutrosNumerosLayout() {
        if (parametroJogo.isTipoJogo(ModalidadeEnum.LOTOMANIA) && opcaoOutrosNumerosLayout.getVisibility() == View.VISIBLE) {
            setVisibilityCheckedNumerosLayout(View.GONE);
            atualizarValorENotificarEstadoCartela();
        }
    }

    private void setVisibilityCheckedNumerosLayout(int visibility) {
        opcaoOutrosNumerosLayout.setVisibility(visibility);
        selecioneOutrosNumeros.setChecked(Boolean.FALSE);
    }

    private void limparTimeSelecionado() {
        if (equipeSelecionada != null) {
            equipeSelecionada.setSelecionado(false);
            if (informeNomeTime != null) {
                informeNomeTime.setText(StringUtils.EMPTY);
            }
        }

        if (timeCoracaoAdapter != null) {
            timeCoracaoAdapter.notifyDataSetChanged();
        }

        voltaSalvarApostaParaLayoutInicial();
    }

    private void onClickOutrosNumerosLayout() {
        opcaoOutrosNumerosLayout.setOnClickListener(view -> {
            if (selecioneOutrosNumeros.isChecked()) {
                selecioneOutrosNumeros.setChecked(false);
            } else {
                selecioneOutrosNumeros.setChecked(true);
            }
            atualizarValorENotificarEstadoCartela();
        });
    }

    private void preencheNumerosAleatorios() {
        AlertDialogUtils.show(getActivity());

        dezenasSelecionadas = ListaUtils.orderAscDezenas(dezenasSelecionadas);
        model.preencheNumerosAleatorios(parentActivity.qtdDezenasPossiveisSelecionado, dezenasSelecionadas, parametroJogo.getPrognosticoMaximo(),
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

        model.preencheNumerosAleatoriosSuperSete(parentActivity.qtdDezenasPossiveisSelecionado, listaInteiros, parametroJogo.getConcurso().getModalidade().name(),
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

    private ArrayList<ArrayList<Integer>> atualizaSuperSetePrognosticoMaior(ArrayList<ArrayList<Integer>> listaInteiros) {
        if (parentActivity.qtdDezenasPossiveisSelecionado < getQtdTotalSelecionadosSuperSete(listaInteiros)) {
            qtdTotalSelecionadosSuperSete = 0;
            listaInteiros = new ArrayList<>();
            limpaNumerosSuperSete();
        }
        return listaInteiros;
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
        Boolean mostraSalvar = false;
        int contColunasComSelecao = 0;
        ArrayList<ArrayList<Integer>> listaSuperSete = ListaUtils.pegaListaSuperSete(listaAdaptersSuperSete);
        for (ArrayList<Integer> listaSelecionados : listaSuperSete) {
            if (listaSelecionados.size() > 0) {
                contColunasComSelecao++;
            }
        }
        if (contColunasComSelecao == 7) {
            mostraSalvar = true;
        }
        return mostraSalvar;
    }

    private void mostrarCartelaEscolhaTimeCoracao() {
        // Estando a aposta completa apresenta a tela de escolha do time
        // Seta a paginação

        isEcolhaTimeBtnAtivado = true;
        isTelaSelecaoTimeAtivado = true;
        selecioneTimeCoracaoLayout = view.findViewById(R.id.selecioneTimeCoracao);
        mGridTimeCoracao = view.findViewById(R.id.gridTimes);
        preencherVisibilidaeGridView(View.VISIBLE, View.VISIBLE, View.GONE, View.GONE);
        informeNomeTime = selecioneTimeCoracaoLayout.findViewById(R.id.informe_nome_time);
        onChangeInformeNomeTime();
        EscudoBO.getInstance().carregaEscudos(getContext(), () -> configurarSelecaoTimeCoracao());
        configurarSelecaoTimeCoracao();
        atualizarValorENotificarEstadoCartela();

    }

    private void onChangeInformeNomeTime() {
        informeNomeTime.addTextChangedListener(new NomeTimemaniaTextWatcher(getActivity(), informeNomeTime, parametroJogo.getEquipes(),
                onNomeTimeTextWatchListener()));
    }

    private NomeTimeTextWatcherListener onNomeTimeTextWatchListener() {
        return new NomeTimeTextWatcherListener() {
            @Override
            public void isValido(boolean isValid) {
                if (isValid) {
                    aplicaCheckTimeSelecionado();
                } else {
                    informeNomeTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                }
            }

            @Override
            public void limparFiltro() {
                listaEquipefiltro.clear();
            }

            @Override
            public void addFiltro(List<ParametroEquipe> listaFriltrada) {
                listaEquipefiltro.addAll(listaFriltrada);
                timeCoracaoAdapter.notifyDataSetChanged();
            }
        };
    }

    private void aplicaCheckTimeSelecionado() {
        informeNomeTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_input_ok, 0);
    }

    private void configuraBotoesLotecaOuLotogol() {
        botaoCompletar = parentActivity.getBotaoCompletar();
        botaoCompletar.setOnClickListener(this);
        botaoCompletar.setVisibility(View.INVISIBLE);
        String textoBotao;
        if (parametroJogo.isTipoJogo(ModalidadeEnum.LOTECA)) {
            textoBotao = getString(R.string.label_selecionar_times_aleatoriamente);
        } else {
            textoBotao = getString(R.string.label_complete_placar_aleatoriamente);
        }
        botaoCompletar.setText(ViewUtils.textCaixaSTDBold(getActivity(), textoBotao));
    }

    private void atualizarValorENotificarEstadoCartela() {

        if (parametroJogo == null || parentActivity == null) {
            return;
        }

        int qtdPossivelParaEstado =
                parentActivity.qtdDezenasPossiveisSelecionado;

        if (parametroJogo.isTipoJogo(ModalidadeEnum.LOTECA)) {

            atualizaValorApostaLoteca();

        } else {

            int qtdParaCalculo =
                    qtdPossivelParaEstado > 0
                            ? qtdPossivelParaEstado
                            : getQuantidadeMinimaSafe();

            atualizaValorTotalAposta(
                    qtdParaCalculo,
                    parentActivity.qtdConcursoSelecionado
            );
        }

        parentActivity.qtdDezenasPossiveisSelecionado =
                qtdPossivelParaEstado;

        notificarEstadoCartelaAlterado(
                qtdPossivelParaEstado
        );
    }
    private void notificarEstadoCartelaAlterado() {

        if (parentActivity == null) {
            return;
        }

        notificarEstadoCartelaAlterado(
                parentActivity.qtdDezenasPossiveisSelecionado
        );
    }

    private int obterQtdParaCalculoValor() {

        if (parametroJogo == null || parentActivity == null) {
            return 0;
        }

        if (parentActivity.qtdDezenasPossiveisSelecionado > 0) {
            return parentActivity.qtdDezenasPossiveisSelecionado;
        }

        return getQuantidadeMinimaSafe();
    }

    private void configuraBotoes(
            View fragmentView
    ) {
        valorAposta =
                parentActivity.getValorApostaCartela();

        botaoCompletar =
                parentActivity.getBotaoCompletar();

        botaoCompletar.setOnClickListener(this);

        botaoFavoritar = parentActivity.getBotaoFavoritar();
        botaoFavoritar.setOnClickListener(this);

        botaoAdicionar =
                parentActivity.getBotaoAdicionar();

        textSelecionados = parentActivity.getTextSelecionados();

        botaoAdicionar.setOnClickListener(this);

        botaoLimparAposta =
                parentActivity.getBotaoLimparAposta();

        botaoLimparAposta.setOnClickListener(this);

        notificarEstadoCartelaAlterado();
    }

    public void voltaSalvarApostaParaLayoutInicial() {
        RelativeLayout salvarApostaLayout = parentActivity.getSalvarApostaLayout();

        salvarApostaLayout.setBackgroundResource(R.color.cinzaescuro);
        TextView tituloSalvarAposta = parentActivity.getTextoSalveEstaAposta();

        tituloSalvarAposta.setText(R.string.label_salve_esta_aposta_carinha);

        ViewGroup.LayoutParams relativelayoutParams = salvarApostaLayout.getLayoutParams();

        final float scale = parentActivity.getResources().getDisplayMetrics().density;
        relativelayoutParams.height = (int) (64 * scale + 0.5f);

        salvarApostaLayout.setLayoutParams(relativelayoutParams);

        ImageView starSalvarApostaImageView = parentActivity.getIconStarSalvar();
        starSalvarApostaImageView.setBackgroundResource(R.drawable.icon_favoritar);

        salvarApostaLayout.setOnClickListener(this);

        EditText nomeAposta = parentActivity.getEditNomeAposta();
        nomeAposta.setText("");
        salvarApostaLayout.setVisibility(View.GONE);
    }

    private void limpaNumerosSuperSete() {
        if (listaGridsSuperSete != null) {
            for (ExpandableHeightGridView grid : listaGridsSuperSete) {
                ((SuperSeteAdapter) grid.getAdapter()).limpaNumeros();
            }
        }
        botaoLimparAposta.setEnabled(false);
        parentActivity.qtdDezenasPossiveisSelecionado = parametroJogo.getQuantidadeMinima() != null ? parametroJogo.getQuantidadeMinima() : 0;
        parentActivity.atualizaTextoBotoesQtdSelecionadas();

        qtdTotalSelecionadosSuperSete = 0;
        atualizarValorENotificarEstadoCartela();
    }

    private boolean mostraLimparSuperSete() {
        if (listaGridsSuperSete != null) {
            for (ExpandableHeightGridView grid : listaGridsSuperSete) {
                if (((SuperSeteAdapter) grid.getAdapter()).dezenasSelecionadas.size() > 0) {
                    return true;
                }
            }
        }
        return false;
    }

    private void ocutarSalvarAposta() {
        RelativeLayout salvarApostaLayout = parentActivity.getSalvarApostaLayout();

        salvarApostaLayout.setVisibility(View.GONE);
        verificaMostraRodape();
    }

    private void verificaMostraRodape() {
        if (parentActivity.getBotaoLimparAposta().getVisibility() == View.VISIBLE ||
                parentActivity.getSalvarApostaLayout().getVisibility() == View.VISIBLE) {
            rodape.setVisibility(View.VISIBLE);
        } else {
            rodape.setVisibility(View.GONE);
        }
    }

    public void atualizaStatusBotaoFinalizar(Boolean isPickerPrognosticos) {
        if (parametroJogo.isTipoJogo(ModalidadeEnum.LOTOGOL)) {
            if (partidasSelecionadas.size() < parametroJogo.getPartidas().size()) {
                botaoCompletar.setEnabled(false);
            } else {
                botaoCompletar.setEnabled(true);
                atualizarValorENotificarEstadoCartela();
            }
        } else if (parametroJogo.isTipoJogo(ModalidadeEnum.TIMEMANIA)) {
            if (dezenasSelecionadas.size() == parentActivity.qtdDezenasPossiveisSelecionado && isEcolhaTimeBtnAtivado) {
//                botaoFinalizar.setBackgroundColor(ContextCompat.getColor(parentActivity, typeGameColorDark));

                if (mGridTimeCoracao != null) {
//                    botaoCompletar.setText(ViewUtils.textCaixaSTDBold(parentActivity, getString(R.string.label_selecionar_time_aleatoriamente)));
//                    botaoCompletar.setTextColor(getResources().getColor(R.color.branco));
//                    botaoCompletar.setBackgroundColor(getContext().getResources().getColor(R.color.cinza));
                } else {
//                    botaoCompletar.setText(ViewUtils.textCaixaSTDBold(parentActivity, getString(R.string.label_escolha_time_coracao)));
//                    botaoCompletar.setTextColor(getResources().getColor(corFonteFundoEscuro));
//                    botaoCompletar.setBackgroundColor(ContextCompat.getColor(parentActivity, typeGameColorDark));
                }
            } else {
                atualizarAdicionarCarinhoCompletarCartela(isPickerPrognosticos);
            }
        } else if (parametroJogo.isTipoJogo(ModalidadeEnum.SUPER_7)) {
            if (matrizSelecionada != null) {
                preencheSuperSete(matrizSelecionada);
                matrizSelecionada = null;
                if (validaSelecaoSuperSete() && qtdTotalSelecionadosSuperSete == parentActivity.qtdDezenasPossiveisSelecionado) {
                    atualizarValorENotificarEstadoCartela();
                } else {
                    atualizarAdicionarCarinhoCompletarCartela(isPickerPrognosticos);
                }
            } else {
                if (validaSelecaoSuperSete() && qtdTotalSelecionadosSuperSete == parentActivity.qtdDezenasPossiveisSelecionado) {
                    atualizarValorENotificarEstadoCartela();
                } else {
                    atualizarAdicionarCarinhoCompletarCartela(isPickerPrognosticos);
                }
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
                    botaoCompletar.setEnabled(true);
                    atualizarValorENotificarEstadoCartela();
                } else {
                    botaoCompletar.setEnabled(false);
                }

            } else {
                botaoCompletar.setEnabled(false);
//                botaoCompletar.setBackgroundColor(ContextCompat.getColor(parentActivity, R.color.cinza));
//                botaoCompletar.setText(ViewUtils.textFuturaAndFuturaBold(parentActivity, getString(R.string.label_selecionar_times_aleatoriamente)));
//                botaoCompletar.setTextColor(getResources().getColor(R.color.branco));
            }
        }
    }

    private void atualizarAdicionarCarinhoCompletarCartela(Boolean isPickerPrognosticos) {
        if (dezenasSelecionadas.size() == parentActivity.qtdDezenasPossiveisSelecionado || isEscolhaTimeCoracaoSurpresinha) {
            if (parametroJogo.isTipoJogo(ModalidadeEnum.DIA_DE_SORTE)) {
                atualizarValorENotificarEstadoCartela();
            } else if (parametroJogo.isTipoJogo(ModalidadeEnum.MAIS_MILIONARIA)) {
                atualizarValorENotificarEstadoCartela();
            } else {
                atualizarValorENotificarEstadoCartela();
                if (parametroJogo.isTipoJogo(ModalidadeEnum.TIMEMANIA)) {
                    if (equipeSelecionada != null && equipeSelecionada.isSelecionado()) {
                        botaoCompletar.setClickable(true);
//                        botaoCompletar.setBackgroundColor(ContextCompat.getColor(parentActivity, typeGameColorDark));
                    } else {
                        botaoCompletar.setClickable(false);
//                        botaoCompletar.setBackgroundColor(ContextCompat.getColor(parentActivity, R.color.azulclaro));
                        parentActivity.getSalvarApostaLayout().setVisibility(View.GONE);
                    }
                }
            }

        } else if (dezenasSelecionadas.size() > parentActivity.qtdDezenasPossiveisSelecionado) {
            dezenasSelecionadas.clear();
            simularDezenasAdapter.notifyDataSetChanged();
            atualizarValorENotificarEstadoCartela();

            botaoLimparAposta.setEnabled(false);
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

        if (dezenas != null && dezenas.size() > 0 && !parametroJogo.isTipoJogo(ModalidadeEnum.SUPER_7)) {
            //simularDezenasAdapter = new ListaDezenaRecyclerView(dezenas, dezenasSelecionadas,
            //													typeGameColorDark, onItemClickListener());
            simularDezenasAdapter = new ListaDezenaRecyclerView(dezenas, dezenasSelecionadas,
                    corFonteFundoBranco, onItemClickListener());
            listaDezenas.setAdapter(simularDezenasAdapter);

            RecyclerView.LayoutManager layoutDezenas = new GridLayoutManager(getContext(), QUANTIDADE_COLUNA_LISTA);
            listaDezenas.setLayoutManager(layoutDezenas);
            listaDezenas.setNestedScrollingEnabled(false);
        } else {
            if (parametroJogo.isTipoJogo(ModalidadeEnum.LOTECA)) {
                EscudoBO.getInstance().carregaEscudos(getContext(), new OnEscudoListener() {
                    @Override
                    public void aposCarregarEscudos() {
                        configurarTimesLoteca();
                    }
                });
                //configurarTimesLoteca();

                fragmentView.findViewById(R.id.layoutTitleLoteca).setVisibility(View.VISIBLE);
            } else if (parametroJogo.isTipoJogo(ModalidadeEnum.SUPER_7)) {
                configuraSuperSete(fragmentView);
            }
        }
    }

    @NotNull
    private OnItemClickListener<DezenaHolder> onItemClickListener() {

        return (OnItemClickListener<DezenaHolder>) (holder, position) -> {
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

                if (parametroJogo.isTipoJogo(ModalidadeEnum.LOTOMANIA)) {
                    atualizarVisibilidadeOutrosNumeros();
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

        int novaQuantidadePossivel =
                Math.max(
                        dezenasSelecionadas.size(),
                        quantidadeMinima
                );

        parentActivity.qtdDezenasPossiveisSelecionado =
                novaQuantidadePossivel;

        parentActivity.atualizaTextoBotoesQtdSelecionadas();

        if (parametroJogo.isTipoJogo(ModalidadeEnum.LOTOMANIA)
                && selecioneOutrosNumeros != null
                && selecioneOutrosNumeros.isChecked()) {

            selecioneOutrosNumeros.setChecked(false);
        }
    }
    private void tentarAdicionarDezenaSelecionada(
            Dezena dezena,
            Integer valorSelecionado
    ) {

        int quantidadeAtualSelecionada =
                dezenasSelecionadas.size();

        int quantidadePossivelAtual =
                parentActivity.qtdDezenasPossiveisSelecionado;

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

            int novaQuantidadePossivel =
                    CartelaFragment2Listener.verificaNovoQtdDezenasMax(
                            quantidadePossivelAtual
                    );

            parentActivity.qtdDezenasPossiveisSelecionado =
                    novaQuantidadePossivel;

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

        if (parentActivity.getFlagApostaSalva()) {
            voltaSalvarApostaParaLayoutInicial();
        }
    }
    private void atualizarVisibilidadeOutrosNumeros() {

        if (opcaoOutrosNumerosLayout == null) {
            return;
        }

        setVisibilityCheckedNumerosLayout(
                dezenasSelecionadas.size()
                        == parentActivity.qtdDezenasPossiveisSelecionado
                        ? View.VISIBLE
                        : View.GONE
        );
    }

    private void configurarTimesLoteca() {
        lotecaAdapter = new LotecaAdapter(parametroJogo, this);
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
                    if (listaSelecionados.size() == adapter.getQtdColunasMaximas(parentActivity.qtdDezenasPossiveisSelecionado)) {
                        listaSelecionados.remove(valorSelecionado);
                        qtdTotalSelecionadosSuperSete--;
                        if (qtdTotalSelecionadosSuperSete >= parametroJogo.getQuantidadeMinima()) {
                            parentActivity.qtdDezenasPossiveisSelecionado -= 1;
                            parentActivity.verificaNovoQtdDezenasMax(qtdTotalSelecionadosSuperSete);
                            atualizarValorENotificarEstadoCartela();
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
                    if (qtdTotalSelecionadosSuperSete == parentActivity.qtdDezenasPossiveisSelecionado) {
                        // VERIFICA SE TEM OPÇÃO DE SELECIONAR MAIS DEZENAS
                        int qtdDezenasPossiveisSelecionadoAtual = parentActivity.qtdDezenasPossiveisSelecionado;

                        parentActivity.qtdDezenasPossiveisSelecionado = CartelaFragment2Listener.verificaNovoQtdDezenasMax(parentActivity.qtdDezenasPossiveisSelecionado);

                        if (qtdDezenasPossiveisSelecionadoAtual < maxNumerosParaSelecionar) {
                            if (listaSelecionados.size() < adapter.QTD_MAX_POR_COLUNA) {
                                if (listaSelecionados.size() < adapter.getQtdColunasMaximas((parentActivity.qtdDezenasPossiveisSelecionado + 1))) {
                                    parentActivity.qtdDezenasPossiveisSelecionado += 1;
                                    parentActivity.verificaNovoQtdDezenasMax(parentActivity.qtdDezenasPossiveisSelecionado);
                                    atualizarValorENotificarEstadoCartela();
                                    if (parentActivity.getFlagApostaSalva()) {
                                        voltaSalvarApostaParaLayoutInicial();
                                    }
                                    listaSelecionados.add(valorSelecionado);
                                    qtdTotalSelecionadosSuperSete++;

                                    if ((qtdTotalSelecionadosSuperSete == (parentActivity.qtdDezenasPossiveisSelecionado)) && (!isAvisou)) {
                                        DialogUtils.dialogEntendi(getActivity(), getString(R.string.msg_qtd_max_ultrapassada));
                                        isAvisou = true;
                                    }
                                } else {
                                    DialogUtils.dialogEntendi(getActivity(), getString(R.string.msg_bloqueio_coluna_super_sete_marcar).replace(
                                            "{qtdColunas}",
                                            String.valueOf(adapter.getQtdColunasMaximas(parentActivity.qtdDezenasPossiveisSelecionado))));
                                }
                            } else {
                                DialogUtils.dialogEntendi(getActivity(), getString(R.string.msg_qtd_max_col_super_sete));
                            }
                        } else {
                            DialogUtils.dialogEntendi(getActivity(), getString(R.string.label_nao_possivel_selecionar_quantidade_numeros_superior_limite_modalidade_loterica));
                        }
                    } else {
                        if (listaSelecionados.size() < adapter.QTD_MAX_POR_COLUNA) {
                            if (listaSelecionados.size() < adapter.getQtdColunasMaximas(parentActivity.qtdDezenasPossiveisSelecionado)) {
                                listaSelecionados.add(valorSelecionado);
                                qtdTotalSelecionadosSuperSete++;
                            } else {
                                DialogUtils.dialogEntendi(getActivity(), getString(R.string.msg_bloqueio_coluna_super_sete_marcar).replace(
                                        "{qtdColunas}",
                                        String.valueOf(adapter.getQtdColunasMaximas(parentActivity.qtdDezenasPossiveisSelecionado))));
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

    /**
     * Metodo configura a cartela de times do coracao
     */
    private void configurarSelecaoTimeCoracao() {
        listaEquipefiltro = new ArrayList<>(parametroJogo.getEquipes());
        timeCoracaoAdapter = new TimeCoracaoAdapter(getActivity(), listaEquipefiltro, onTimeSelecionadoListener());
        mGridTimeCoracao.setLayoutManager(new GridLayoutManager(getContext(), 3));
        mGridTimeCoracao.setAdapter(timeCoracaoAdapter);
    }

    private OnItemClickListener onTimeSelecionadoListener() {
        return (holder, position) -> {
            if (equipeSelecionada != null && equipeSelecionada.isSelecionado()) {
                equipeSelecionada.setSelecionado(false);
            }
            equipeSelecionada = listaEquipefiltro.get(position);
            equipeSelecionada.setSelecionado(true);
            String nomeTime = String.format("%s-%s", equipeSelecionada.getNome(), equipeSelecionada.getUf());
            informeNomeTime.setText(nomeTime);
            aplicaCheckTimeSelecionado();
            listaEquipefiltro.clear();
            listaEquipefiltro.addAll(parametroJogo.getEquipes());
            timeCoracaoAdapter.notifyDataSetChanged();
            atualizarValorENotificarEstadoCartela();
            atualizarAdicionarCarinhoCompletarCartela(false);
            InputUtils.closeKeyboard(getActivity());
        };
    }

    @Override
    public void clickEquipeLoteca(ParametroPartida parametroPartida) {
        BigDecimal ultimoValor = valorEmReais;

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
        int somaTotais = 0;
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
            valorTotalJogos.setTextColor(getResources().getColor(R.color.verdeazul));
            valorTotalSimples.setTextColor(getResources().getColor(R.color.verdeazul));
        } else {
            valorTotalJogos.setTextColor(getResources().getColor(R.color.loteca_claro_mkp));
            valorTotalSimples.setTextColor(getResources().getColor(R.color.loteca_claro_mkp));
        }
        if (valueDuplas == 0 && valueTriplas == 0) {
            valorTotalDuplas.setTextColor(getResources().getColor(R.color.loteca_claro_mkp));
            valorTotalTriplas.setTextColor(getResources().getColor(R.color.loteca_claro_mkp));
        } else {
            valorTotalDuplas.setTextColor(getResources().getColor(R.color.verdeazul));
            valorTotalTriplas.setTextColor(getResources().getColor(R.color.verdeazul));
        }


        Boolean mostraBotao = atualizaValorApostaLoteca();
        atualizaStatusBotaoFinalizarLoteca(mostraBotao);
        atualizarValorENotificarEstadoCartela();
    }

    private void limparPartidadasSelecionasLoteca(boolean desativado) {
        for (ParametroPartida partida : parametroJogo.getPartidas()) {
            partida.setEmpate(desativado);
            partida.getEquipe1().setSelecionado(desativado);
            partida.getEquipe2().setSelecionado(desativado);
            partida.setSelecionado(desativado);
            partida.setQtdItensSelecionadosAnterior(0);
            partida.setQtdItensSelecionados(0);
        }
        recyclerViewPartida.getAdapter().notifyDataSetChanged();
        partidasSelecionadas.clear();
        parentActivity.zerarApostasLoteca();
        ocutarSalvarAposta();
        atualizaValorApostaLoteca();
        botaoCompletar.setEnabled(false);
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

        valorEmReais =
                valorCalculado != null
                        ? valorCalculado
                        : BigDecimal.ZERO;

        publicarValorAposta(valorEmReais);

        return mostraBotao;
    }

    private void editaAposta() {

        if (parentActivity.getAposta() == null) {
            return;
        }

        IdentificaoDeUmaApostaDas8Modalidades<List<Integer>> aposta =
                parentActivity.getAposta();

        IdentificaoDeUmaApostaDas8Modalidades<List<List<Integer>>> apostaSuperSete =
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

            parentActivity.qtdDezenasPossiveisSelecionado =
                    apostaSuperSete.getQuantidadeNumeros();

            parentActivity.qtdConcursoSelecionado =
                    apostaSuperSete.getQuantidadeTeimosinhas();

            parentActivity.atualizaTextoBotoesQtdSelecionadas();

            publicarValorAposta(aposta.getValor());

            botaoLimparAposta.setEnabled(
                    mostraLimparSuperSete()
            );

            return;
        }

        if (CollectionUtils.isNotEmpty(aposta.getListaNumerosSelecionados())) {

            dezenasSelecionadas =
                    aposta.getListaNumerosSelecionados();

            if (parametroJogo.isTipoJogo(ModalidadeEnum.LOTOMANIA)) {
                setVisibilityCheckedNumerosLayout(
                        dezenasSelecionadas.size()
                                == parentActivity.qtdDezenasPossiveisSelecionado
                                ? View.VISIBLE
                                : View.GONE
                );
            }

            simularDezenasAdapter.atualizaSelecionados(
                    dezenasSelecionadas
            );

            parentActivity.qtdDezenasPossiveisSelecionado =
                    aposta.getQuantidadeNumeros();

            parentActivity.qtdConcursoSelecionado =
                    aposta.getQuantidadeTeimosinhas();

            parentActivity.atualizaTextoBotoesQtdSelecionadas();

            equipeSelecionada =
                    aposta.getTimeDoCoracao();

            if (equipeSelecionada != null) {
                isEcolhaTimeBtnAtivado = true;
            }

            verificaMostraRodape();
        }

        publicarValorAposta(
                aposta.getValor()
        );

        notificarEstadoCartelaAlterado();
    }

    private void timemaniaTimeSelecionadoEdicao() {
        if (equipeSelecionada != null) {
            equipeSelecionada.setSelecionado(Boolean.TRUE);
            String nomeTime = String.format("%s-%s", equipeSelecionada.getNome(), equipeSelecionada.getUf());
            informeNomeTime.setText(nomeTime);

            for (ParametroEquipe equipe : listaEquipefiltro) {
                if (equipe.getNumero().equals(equipeSelecionada.getNumero())) {
                    equipe.setSelecionado(equipeSelecionada.isSelecionado());
                    equipeSelecionada = equipe;
                    break;
                }
            }
            timeCoracaoAdapter.notifyDataSetChanged();
            atualizarValorENotificarEstadoCartela();
            atualizarAdicionarCarinhoCompletarCartela(false);
        }
    }
}