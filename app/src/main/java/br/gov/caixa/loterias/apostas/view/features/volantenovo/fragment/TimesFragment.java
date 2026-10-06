package br.gov.caixa.loterias.apostas.view.features.volantenovo.fragment;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.EscudoBO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaFavoritaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroEquipe;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroJogoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroValorApostaDTO;
import br.gov.caixa.loterias.apostas.utils.ApostaUtils;
import br.gov.caixa.loterias.apostas.utils.EtapaAposta;
import br.gov.caixa.loterias.apostas.utils.InputUtils;
import br.gov.caixa.loterias.apostas.utils.ListaUtils;
import br.gov.caixa.loterias.apostas.utils.Utils;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.TimeCoracaoAdapter;
import br.gov.caixa.loterias.apostas.view.custom.NomeTimemaniaTextWatcher;
import br.gov.caixa.loterias.apostas.view.features.volantenovo.presentation.SimulaUiEvent;
import br.gov.caixa.loterias.apostas.view.features.volantenovo.presentation.SimulaUiState;
import br.gov.caixa.loterias.apostas.view.listener.NomeTimeTextWatcherListener;
import br.gov.caixa.loterias.apostas.view.listener.OnItemClickListener;

public class TimesFragment extends BaseEtapaFragment {

    private static final String ARG_PARAMETRO = "ARG_PARAMETRO";
    private static final String ARG_SELECIONADOS = "ARG_SELECIONADOS";
    private static final String ARG_ESCOLHA_TIME_SURPRESINHA =
            "ARG_ESCOLHA_TIME_SURPRESINHA";

    private static final int QUANTIDADE_COLUNA_LISTA = 3;

    private ParametroJogoDTO parametro;

    private LinearLayout selecioneTimeCoracaoLayout;
    private RecyclerView gridTimes;
    private EditText informeNomeTime;

    private TimeCoracaoAdapter timeCoracaoAdapter;
    private List<ParametroEquipe> listaEquipeFiltro;

    private boolean isEscolhaTimeCoracaoSurpresinha;

    public TimesFragment() {
    }

    public static TimesFragment newInstance(
            ParametroJogoDTO parametro,
            ArrayList<Integer> dezenasSelecionadas
    ) {
        return newInstance(
                parametro,
                dezenasSelecionadas,
                false
        );
    }

    public static TimesFragment newInstance(
            ParametroJogoDTO parametro,
            ArrayList<Integer> dezenasSelecionadas,
            boolean escolhaTimeCoracaoSurpresinha
    ) {
        TimesFragment fragment =
                new TimesFragment();

        Bundle args =
                new Bundle();

        args.putSerializable(
                ARG_PARAMETRO,
                parametro
        );

        args.putString(
                ARG_SELECIONADOS,
                new Gson().toJson(
                        dezenasSelecionadas
                )
        );

        args.putBoolean(
                ARG_ESCOLHA_TIME_SURPRESINHA,
                escolhaTimeCoracaoSurpresinha
        );

        fragment.setArguments(
                args
        );

        return fragment;
    }

    @Override
    public void onCreate(
            @Nullable Bundle savedInstanceState
    ) {
        super.onCreate(
                savedInstanceState
        );

        if (getArguments() != null) {
            parametro =
                    (ParametroJogoDTO)
                            getArguments()
                                    .getSerializable(
                                            ARG_PARAMETRO
                                    );

            String jsonSelecionados =
                    getArguments()
                            .getString(
                                    ARG_SELECIONADOS
                            );

            isEscolhaTimeCoracaoSurpresinha =
                    getArguments()
                            .getBoolean(
                                    ARG_ESCOLHA_TIME_SURPRESINHA,
                                    false
                            );

        }

    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view =
                inflater.inflate(
                        R.layout.fragment_times,
                        container,
                        false
                );

        bindViews(
                view
        );

        inicializar();

        return view;
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(
                view,
                savedInstanceState
        );

        viewModel
                .getUiState()
                .observe(
                        getViewLifecycleOwner(),
                        this::renderState
                );
    }

    private void bindViews(
            View view
    ) {
        selecioneTimeCoracaoLayout =
                view.findViewById(
                        R.id.selecioneTimeCoracao
                );

        gridTimes =
                view.findViewById(
                        R.id.gridTimes
                );

        informeNomeTime =
                view.findViewById(
                        R.id.informe_nome_time
                );

        if (informeNomeTime == null
                && selecioneTimeCoracaoLayout != null) {
            informeNomeTime =
                    selecioneTimeCoracaoLayout
                            .findViewById(
                                    R.id.informe_nome_time
                            );
        }
    }

    private void inicializar() {
        preencherVisibilidadeTimeCoracao();

        configurarBuscaTime();

        EscudoBO.getInstance()
                .carregaEscudos(
                        requireContext(),
                        this::configurarSelecaoTimeCoracao
                );

        configurarSelecaoTimeCoracao();

        atualizarValorAposta();

        notificarEstadoTimeAlterado(
                getEquipeSelecionadaAtual()
        );
    }

    private void preencherVisibilidadeTimeCoracao() {
        if (selecioneTimeCoracaoLayout != null) {
            selecioneTimeCoracaoLayout.setVisibility(
                    View.VISIBLE
            );
        }

        if (gridTimes != null) {
            gridTimes.setVisibility(
                    View.VISIBLE
            );
        }
    }

    private void configurarBuscaTime() {
        if (informeNomeTime == null
                || parametro == null
                || parametro.getEquipes() == null) {
            return;
        }

        informeNomeTime.addTextChangedListener(
                new NomeTimemaniaTextWatcher(
                        getActivity(),
                        informeNomeTime,
                        parametro.getEquipes(),
                        onNomeTimeTextWatchListener()
                )
        );
    }

    private NomeTimeTextWatcherListener onNomeTimeTextWatchListener() {
        return new NomeTimeTextWatcherListener() {

            @Override
            public void isValido(
                    boolean isValid
            ) {
                if (informeNomeTime == null) {
                    return;
                }

                if (isValid) {
                    aplicaCheckTimeSelecionado();
                } else {
                    informeNomeTime
                            .setCompoundDrawablesWithIntrinsicBounds(
                                    0,
                                    0,
                                    0,
                                    0
                            );
                }
            }

            @Override
            public void limparFiltro() {
                if (listaEquipeFiltro != null) {
                    listaEquipeFiltro.clear();
                }
            }

            @Override
            public void addFiltro(
                    List<ParametroEquipe> listaFiltrada
            ) {
                if (listaEquipeFiltro == null) {
                    listaEquipeFiltro =
                            new ArrayList<>();
                }

                listaEquipeFiltro.addAll(
                        listaFiltrada
                );

                if (timeCoracaoAdapter != null) {
                    timeCoracaoAdapter
                            .notifyDataSetChanged();
                }
            }
        };
    }

    private void configurarSelecaoTimeCoracao() {
        if (parametro == null
                || parametro.getEquipes() == null
                || gridTimes == null) {
            return;
        }

        listaEquipeFiltro =
                new ArrayList<>(
                        parametro.getEquipes()
                );

        timeCoracaoAdapter =
                new TimeCoracaoAdapter(
                        getActivity(),
                        listaEquipeFiltro,
                        onTimeSelecionadoListener()
                );

        gridTimes.setLayoutManager(
                new GridLayoutManager(
                        getActivity(),
                        QUANTIDADE_COLUNA_LISTA
                )
        );
        gridTimes.addItemDecoration(
                new RecyclerView.ItemDecoration() {
                    @Override
                    public void getItemOffsets(
                            Rect outRect,
                            View view,
                            RecyclerView parent,
                            RecyclerView.State state
                    ) {

                        outRect.left = 8;
                        outRect.right = 8;
                        outRect.top = 8;
                        outRect.bottom = 8;
                    }
                }
        );

        gridTimes.setAdapter(
                timeCoracaoAdapter
        );

        aplicarSelecaoVisual(
                getEquipeSelecionadaAtual()
        );
    }

    private OnItemClickListener onTimeSelecionadoListener() {
        return (holder, position) -> {
            if (listaEquipeFiltro == null
                    || position < 0
                    || position >= listaEquipeFiltro.size()) {
                return;
            }

            ParametroEquipe equipe =
                    listaEquipeFiltro.get(
                            position
                    );

            selecionarEquipe(
                    equipe
            );

            InputUtils.closeKeyboard(
                    getActivity()
            );
        };
    }

    private void selecionarEquipe(
            ParametroEquipe equipe
    ) {
        if (equipe == null
                || parametro == null
                || parametro.getEquipes() == null) {
            return;
        }

        limparMarcacaoTimeAtual();

        equipe.setSelecionado(
                true
        );

        dispatch(
                new SimulaUiEvent.SelecionarTimeCoracao(
                        equipe
                )
        );

        preencherNomeTimeSelecionado(
                equipe
        );

        resetarFiltroParaListaCompleta();

        aplicarSelecaoVisual(
                equipe
        );

        atualizarValorAposta();

        notificarEstadoTimeAlterado(
                equipe
        );
    }

    private void resetarFiltroParaListaCompleta() {
        if (parametro == null
                || parametro.getEquipes() == null) {
            return;
        }

        if (listaEquipeFiltro == null) {
            listaEquipeFiltro =
                    new ArrayList<>();
        }

        listaEquipeFiltro.clear();
        listaEquipeFiltro.addAll(
                parametro.getEquipes()
        );

        if (timeCoracaoAdapter != null) {
            timeCoracaoAdapter
                    .notifyDataSetChanged();
        }
    }

    private void preencherNomeTimeSelecionado(
            ParametroEquipe equipe
    ) {
        if (informeNomeTime == null
                || equipe == null) {
            return;
        }

        String nomeTime =
                String.format(
                        "%s-%s",
                        equipe.getNome(),
                        equipe.getUf()
                );

        if (!nomeTime.equals(
                informeNomeTime
                        .getText()
                        .toString()
        )) {
            informeNomeTime.setText(
                    nomeTime
            );
        }

        aplicaCheckTimeSelecionado();
    }

    private void aplicaCheckTimeSelecionado() {
        if (informeNomeTime == null) {
            return;
        }

        informeNomeTime
                .setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        0,
                        R.drawable.ic_check_time,
                        0
                );
    }

    private void limparMarcacaoTimeAtual() {
        if (parametro != null
                && parametro.getEquipes() != null) {
            for (ParametroEquipe equipe
                    : parametro.getEquipes()) {
                if (equipe != null) {
                    equipe.setSelecionado(
                            false
                    );
                }
            }
        }

        if (listaEquipeFiltro != null) {
            for (ParametroEquipe equipe
                    : listaEquipeFiltro) {
                if (equipe != null) {
                    equipe.setSelecionado(
                            false
                    );
                }
            }
        }
    }

    private void aplicarSelecaoVisual(
            ParametroEquipe equipeSelecionadaState
    ) {
        if (parametro == null
                || parametro.getEquipes() == null) {
            return;
        }

        if (equipeSelecionadaState == null
                || equipeSelecionadaState.getNumero() == null) {
            limparMarcacaoTimeAtual();

            if (timeCoracaoAdapter != null) {
                timeCoracaoAdapter
                        .notifyDataSetChanged();
            }

            return;
        }

        for (ParametroEquipe equipe
                : parametro.getEquipes()) {
            if (equipe == null
                    || equipe.getNumero() == null) {
                continue;
            }

            equipe.setSelecionado(
                    equipe.getNumero()
                            .equals(
                                    equipeSelecionadaState
                                            .getNumero()
                            )
            );
        }

        if (listaEquipeFiltro != null) {
            for (ParametroEquipe equipe
                    : listaEquipeFiltro) {
                if (equipe == null
                        || equipe.getNumero() == null) {
                    continue;
                }

                equipe.setSelecionado(
                        equipe.getNumero()
                                .equals(
                                        equipeSelecionadaState
                                                .getNumero()
                                )
                );
            }
        }

        if (timeCoracaoAdapter != null) {
            timeCoracaoAdapter
                    .notifyDataSetChanged();
        }
    }

    private void renderState(
            SimulaUiState state
    ) {
        if (state == null
                || state.getEtapaAposta()
                != EtapaAposta.TIME_CORACAO) {
            return;
        }

        if (state.isLimparTimeCoracao()) {
            limparTimeSelecionado();

            dispatch(
                    new SimulaUiEvent.LimpezaApostaRenderizada()
            );

            return;
        }

        ParametroEquipe equipeState =
                state.getEquipeSelecionada();

        if (equipeState != null) {
            aplicarSelecaoVisual(
                    equipeState
            );

            preencherNomeTimeSelecionado(
                    equipeState
            );
        }
    }

    public void onAdicionarRodapeClicado() {
        ParametroEquipe equipe =
                getEquipeSelecionadaAtual();

        if (!possuiSelecaoValida()) {
            notificarEstadoTimeAlterado(
                    equipe
            );
            return;
        }

        SimulaUiState state =
                getStateAtual();

        if (isEscolhaTimeCoracaoSurpresinha) {
            dispatch(
                    new SimulaUiEvent
                            .AdicionarCarrinhoSolicitado(
                            null,
                            getBarraTitulo(),
                            true,
                            equipe
                    )
            );
            return;
        }

        dispatch(
                new SimulaUiEvent
                        .AdicionarCarrinhoSolicitado(
                        criarApostaTimemania(
                                state,
                                equipe
                        ),
                        getBarraTitulo(),
                        false,
                        null
                )
        );
    }

    public void onCompletarRodapeClicado() {
        selecionarTimeAleatorio();
    }

    @Override protected boolean suportaApostaShake() { return true; }
    @Override protected boolean apostaShakeCompleta() {
        ParametroEquipe equipe = getEquipeSelecionadaAtual();
        return equipe != null && equipe.isSelecionado();
    }
    @Override protected void preencherApostaShake(boolean renovar) {
        if (!renovar) {
            selecionarTimeAleatorio();
            return;
        }
        if (parametro == null || parametro.getEquipes() == null || parametro.getEquipes().size() < 2) return;
        ParametroEquipe atual = getEquipeSelecionadaAtual();
        List<ParametroEquipe> alternativas = new ArrayList<>(parametro.getEquipes());
        alternativas.remove(atual);
        selecionarEquipe(alternativas.get(Utils.getRandom().nextInt(alternativas.size())));
    }

    public void onLimparRodapeClicado() {
        dispatch(
                new SimulaUiEvent.LimparApostaSolicitado()
        );
    }
    protected ApostaFavoritaDTO criarApostaFavoritaDTO(){
        ParametroEquipe equipe =
                getEquipeSelecionadaAtual();

        if (equipe == null
                || !equipe.isSelecionado()) {
            return null;
        }

        ApostaFavoritaDTO apostaFavorita = new ApostaFavoritaDTO();

        apostaFavorita.setTimeDoCoracao(
                equipe
        );
        return apostaFavorita;
    }

    private IdentificaoDeUmaApostaDas8Modalidades criarApostaTimemania(
            SimulaUiState state,
            ParametroEquipe equipe
    ) {
        return ApostaUtils.getApostaTimemania(
                parametro,
                ListaUtils.orderAscDezenas(
                        new ArrayList<>(
                                getStateAtual().getDezenasSelecionadas()
                        )
                ),
                state.getValorAposta(),
                state.getQtdConcursoSelecionado(),
                equipe
        );
    }

    private void selecionarTimeAleatorio() {
        if (parametro == null
                || parametro.getEquipes() == null
                || parametro.getEquipes().isEmpty()) {
            return;
        }

        List<ParametroEquipe> equipes =
                parametro.getEquipes();

        Random random =
                Utils.getRandom();

        int randomInt =
                random.nextInt(
                        equipes.size()
                );

        selecionarEquipe(
                equipes.get(
                        randomInt
                )
        );
    }

    private void limparTimeSelecionado() {
        limparMarcacaoTimeAtual();

        if (informeNomeTime != null) {
            informeNomeTime.setText(
                    StringUtils.EMPTY
            );

            informeNomeTime
                    .setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.ic_lupa,
                            0,
                            0,
                            0
                    );
        }

        if (timeCoracaoAdapter != null) {
            timeCoracaoAdapter.notifyDataSetChanged();
        }
    }

    private ParametroEquipe getEquipeSelecionadaAtual() {
        SimulaUiState state =
                getStateAtual();

        if (state == null) {
            return null;
        }

        return state.getEquipeSelecionada();
    }

    public void atualizarValorAposta() {
        if (parametro == null) {
            return;
        }

        SimulaUiState state =
                getStateAtual();

        BigDecimal valor =
                obterValorApostaPorParametros(
                        state.getQtdDezenasPossiveisSelecionado(),
                        state.getQtdConcursoSelecionado()
                );

        dispatch(
                new SimulaUiEvent
                        .ValorApostaAlterado(
                        valor
                )
        );
    }
    public void revalidarEstado() {
        notificarEstadoTimeAlterado(
                getEquipeSelecionadaAtual()
        );
    }

    private BigDecimal obterValorApostaPorParametros(
            int qtdPrognosticos,
            int qtdConcursos
    ) {
        if (parametro == null) {
            return BigDecimal.ZERO;
        }

        if (qtdPrognosticos <= 0
                && parametro.getQuantidadeMinima() != null) {
            qtdPrognosticos =
                    parametro.getQuantidadeMinima();
        }

        BigDecimal valorBase =
                BigDecimal.ZERO;

        if (parametro.getValoresAposta() != null) {
            for (ParametroValorApostaDTO valorAposta
                    : parametro.getValoresAposta()) {
                if (valorAposta == null
                        || valorAposta.getNumeroPrognosticos() == null
                        || valorAposta.getValor() == null) {
                    continue;
                }

                if (valorAposta
                        .getNumeroPrognosticos()
                        .equals(
                                qtdPrognosticos
                        )) {
                    valorBase =
                            valorAposta.getValor();
                    break;
                }
            }
        }

        if (valorBase.compareTo(
                BigDecimal.ZERO
        ) == 0
                && parametro.getValorApostaMinima() != null) {
            valorBase =
                    parametro.getValorApostaMinima();
        }

        if (qtdConcursos > 0) {
            valorBase =
                    valorBase.multiply(
                            BigDecimal.valueOf(
                                    qtdConcursos
                            )
                    );
        }

        return valorBase;
    }

    private void notificarEstadoTimeAlterado(
            ParametroEquipe equipe
    ) {
        if (parametro == null) {
            return;
        }

        SimulaUiState state =
                getStateAtual();

        boolean equipeSelecionadaValida =
                equipe != null
                        && equipe.isSelecionado();

       dispatch(
                new SimulaUiEvent.CartelaAlterada(
                        parametro.getConcurso().getModalidade(),
                        getStateAtual().getDezenasSelecionadas() != null
                                ? getStateAtual().getDezenasSelecionadas().size()
                                : 0,
                        state.getQtdDezenasPossiveisSelecionado(),
                        0,
                        0,
                        0,
                        0,
                        0,
                        false,
                        equipeSelecionadaValida,
                        isEscolhaTimeCoracaoSurpresinha,
                        false,
                        false,
                        equipeSelecionadaValida,
                        getStateAtual().getDezenasSelecionadas()
                )
        );
    }

    @Override
    public boolean possuiSelecaoValida() {
        ParametroEquipe equipe =
                getEquipeSelecionadaAtual();

        return equipe != null
                && equipe.isSelecionado();
    }

    @Override
    public EtapaAposta getEtapa() {
        return EtapaAposta.TIME_CORACAO;
    }
    @Override
    public void onQuantidadeNumerosAlterada() {
        atualizarValorAposta();
        revalidarEstado();
    }

}
