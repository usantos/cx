package br.gov.caixa.loterias.apostas.view.features.volantenovo.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaFavoritaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.Dezena;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroJogoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroTrevo;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroValorApostaDTO;
import br.gov.caixa.loterias.apostas.model.model.TrevoModel;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.ApostaUtils;
import br.gov.caixa.loterias.apostas.utils.EtapaAposta;
import br.gov.caixa.loterias.apostas.utils.ListaUtils;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.ListaDezenaRecyclerView;
import br.gov.caixa.loterias.apostas.view.config.DezenaConfig;
import br.gov.caixa.loterias.apostas.view.config.ShapeConfig;
import br.gov.caixa.loterias.apostas.view.features.volantenovo.presentation.SimulaUiEvent;
import br.gov.caixa.loterias.apostas.view.features.volantenovo.presentation.SimulaUiState;
import br.gov.caixa.loterias.apostas.view.holder.DezenaHolder;
import br.gov.caixa.loterias.apostas.view.listener.OnItemClickListener;

public class TrevosFragment2 extends BaseEtapaFragment implements OnItemClickListener<DezenaHolder> {
    private static final String ARG_PARAMENTRO = "ARG_PARAMENTRO";
    private static final String ARG_SELECIONADOS = "ARG_SELECIONADOS";
    private static final int QUANTIDADE_COLUNA_LISTA = 3;
    private RecyclerView listaTrevos;
    private ParametroJogoDTO parametro;
    private ArrayList<Integer> dezenasSelecionadas;
    private List<ParametroValorApostaDTO> valoresApostas;
    private List<Dezena> dezenaList;
    private ListaDezenaRecyclerView adapter;

    private TrevoModel model;

    private TrevosFragment2() {
    }

    public static TrevosFragment2 newInstance(
                                              ParametroJogoDTO parametro,
                                              ArrayList<Integer> dezenasSelecionadas) {
        TrevosFragment2 frag = new TrevosFragment2();
        Bundle args = new Bundle();
//        args.putSerializable(ARG_APOSTA, aposta);
        args.putSerializable(ARG_PARAMENTRO, parametro);
        args.putString(ARG_SELECIONADOS, String.valueOf(dezenasSelecionadas));
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            aposta = (IdentificaoDeUmaApostaDas8Modalidades) getArguments().getSerializable(ARG_APOSTA);
            parametro = (ParametroJogoDTO) getArguments().getSerializable(ARG_PARAMENTRO);
            String json = getArguments().getString(ARG_SELECIONADOS);
            if (json != null && !json.isEmpty()) {
                dezenasSelecionadas = new Gson().fromJson(json, new TypeToken<List<Integer>>() {
                }.getType());
            } else {
                dezenasSelecionadas = new ArrayList<>();
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_trevos, container, false);
        listaTrevos = view.findViewById(R.id.listaTrevos);
        init();

        return view;
    }
    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        viewModel.getUiState()
                .observe(
                        getViewLifecycleOwner(),
                        this::renderState
                );
    }
    private int getQuantidadeNumerosAtual() {
        return getStateAtual()
                .getQtdDezenasPossiveisSelecionado();
    }
    protected ApostaFavoritaDTO criarApostaFavoritaDTO(){
        ParametroTrevo parametroTrevo =
                new ParametroTrevo();

        parametroTrevo.setTrevosSelecionados(
                new ArrayList<>(
                        getTrevosSelecionados()
                )
        );
        ApostaFavoritaDTO apostaFavorita = new ApostaFavoritaDTO();

        apostaFavorita.setParametroTrevo(
                parametroTrevo
        );

        return apostaFavorita;
    }

    private void renderState(
            SimulaUiState state
    ) {
        if (state.getEtapaAposta()
                != EtapaAposta.TREVOS) {
            return;
        }

        atualizarSelecaoVisualTrevos(
                state.getTrevosSelecionados()
        );

        atualizarAdapterTrevos(
                state.getTrevosSelecionados()
        );
    }

    private void atualizarSelecaoVisualTrevos(
            List<Integer> trevos
    ) {

        if (dezenaList == null) {
            return;
        }

        for (Dezena dezena : dezenaList) {

            Integer numero =
                    Integer.valueOf(
                            dezena.getValue()
                    );

            dezena.setSelected(
                    trevos.contains(numero)
            );
        }
    }

    private void init() {
        valoresApostas =
                parametro.getValoresTrevoByNumero(
                        getQuantidadeNumerosAtual()
                );

        ParametroValorApostaDTO valorInicial = null;

        if (valoresApostas != null
                && !valoresApostas.isEmpty()) {
            valorInicial = valoresApostas.get(0);
        }

        if (valorInicial != null) {
            dispatch(
                    new SimulaUiEvent.QuantidadeTrevosAlterada(
                            valorInicial
                    )
            );
        }

        initRecyclerView();
        model = new TrevoModel(getActivity());
//        editAposta();
    }
    private ParametroValorApostaDTO getValorTrevosAtual() {
        return getStateAtual()
                .getValorTrevosSelecionado();
    }

    private void initRecyclerView(){
        DezenaConfig dezenaConfig = new DezenaConfig(false, R.color.milionaria_escuro_mkp,
                R.layout.item_dezena_trevo,
                new ShapeConfig(R.drawable.ic_trevo,
                        R.drawable.ic_trevo_selecionado,
                        R.color.milionaria_escuro_mkp, R.color.milionaria_escuro_mkp),
                true);
        adapter = new ListaDezenaRecyclerView(getListaDezenas(), getTrevosSelecionados(), dezenaConfig, this);
        listaTrevos.setAdapter(adapter);
        RecyclerView.LayoutManager layot = new GridLayoutManager(getActivity(), QUANTIDADE_COLUNA_LISTA);
        listaTrevos.setLayoutManager(layot);
    }

    private List<Dezena> getListaDezenas() {
        if (dezenaList == null || dezenaList.size() != parametro.getTrevos().getQtdPrognostico()) {
            dezenaList = new ArrayList<>();
            for (int numero = 1; numero <= parametro.getTrevos().getQtdPrognostico(); numero++) {
                Dezena dezena = new Dezena(String.valueOf(numero), Boolean.FALSE, String.valueOf(numero));
                dezenaList.add(dezena);
            }
        }
        return dezenaList;
    }

    public void onAdicionarRodapeClicado() {
        if (!isApostaTrevosCompleta()) {
            return;
        }

        SimulaUiState estadoAtual =
                getStateAtual();

        List<Integer> trevos =
                ListaUtils.orderAscDezenas(
                        new ArrayList<>(
                                getTrevosSelecionados()
                        )
                );

       dispatch(
                new SimulaUiEvent.AdicionarCarrinhoSolicitado(
                        ApostaUtils.getApostaMaisMilionaria(
                                parametro,
                                dezenasSelecionadas,
                                estadoAtual.getValorAposta(),
                                estadoAtual.getQtdConcursoSelecionado(),
                                trevos
                        ),
                        getBarraTitulo(),
                        false,
                        null
                )
        );
    }

    public void onCompletarRodapeClicado() {
        completarTrevosAleatoriamente();
    }

    public void onLimparRodapeClicado() {
        limparTrevosSelecionados();
    }

    private boolean isApostaTrevosCompleta() {
        ParametroValorApostaDTO valorAtual =
                getValorTrevosAtual();

        if (valorAtual == null
                || valorAtual.getNumeroTrevos() == null) {
            return false;
        }

        return getTrevosSelecionados().size()
                == valorAtual.getNumeroTrevos();
    }

    private void limparTrevosSelecionados() {

        atualizarTrevosSelecionados(
                new ArrayList<>()
        );
    }

    private void completarTrevosAleatoriamente() {
        ParametroValorApostaDTO valorAtual =
                getValorTrevosAtual();

        if (valorAtual == null
                || valorAtual.getNumeroTrevos() == null) {
            return;
        }

        AlertDialogUtils.show(getContext());

        model.preencheNumerosAleatorios(
                valorAtual.getNumeroTrevos(),
                getTrevosSelecionados(),
                parametro.getTrevos().getQtdMaxima(),
                new OnSilceListener<List<Integer>>() {
                    @Override
                    public void success(List<Integer> list) {
                        AlertDialogUtils.dismiss();

                        atualizarTrevosSelecionados(
                                new ArrayList<>(list)
                        );
                    }

                    @Override
                    public void error(VolleyError error) {
                        AlertDialogUtils.dismiss();
                    }
                }
        );
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }


    @Override
    public void itemClick(
            DezenaHolder holder,
            int position
    ) {

        Dezena dezena =
                dezenaList.get(position);

        int numero =
                Integer.parseInt(dezena.getValue());

        dispatch(
                new SimulaUiEvent.ToggleTrevo(
                        numero
                )
        );
    }
    private List<Integer> getTrevosSelecionados() {

        return getStateAtual()
                .getTrevosSelecionados();
    }

    private void atualizarTrevosSelecionados(
            List<Integer> trevos
    ) {
        ParametroValorApostaDTO valorAtual =
                getValorTrevosAtual();

        if (valorAtual == null || valorAtual.getNumeroTrevos() == null) {
            return;
        }

        dispatch(
                new SimulaUiEvent.TrevosAlterados(
                        trevos,
                        valorAtual.getNumeroTrevos(),
                        valorAtual
                )
        );
    }

    @Override
    public void atualizarQuantidadeTrevos(
            ParametroValorApostaDTO valorSelecionado
    ) {


        if (valoresApostas == null
                || valoresApostas.isEmpty()) {

            valoresApostas =
                    parametro.getValoresTrevoByNumero(
                            getQuantidadeNumerosAtual()
                    );
        }

        int ultimoValor =
                valoresApostas != null
                        ? valoresApostas.size() - 1
                        : -1;

        if (ultimoValor >= 0
                && valoresApostas.get(ultimoValor)
                .equals(valorSelecionado)) {

            List<Integer> novosTrevos =
                    new ArrayList<>();


            int quantidade =
                    valorSelecionado.getNumeroTrevos();

            for (int numero = 1;
                 numero <= quantidade;
                 numero++) {

                novosTrevos.add(numero);
            }


            atualizarTrevosSelecionados(
                    novosTrevos
            );

            return;
        }

        List<Integer> trevosAtuais =
                getTrevosSelecionados();

        if (trevosAtuais.size() > valorSelecionado.getNumeroTrevos()) {

            List<Integer> ajustados =
                    new ArrayList<>(trevosAtuais);

            ajustados = ajustados.subList(
                    0,
                    valorSelecionado.getNumeroTrevos()
            );

            atualizarTrevosSelecionados(
                    new ArrayList<>(ajustados)
            );

            return;
        }

        atualizarTrevosSelecionados(
                trevosAtuais
        );
    }

    private void atualizarAdapterTrevos(
            List<Integer> trevos
    ) {
        if (adapter != null) {
            adapter.atualizaSelecionados(
                    trevos
            );
        }

        if (listaTrevos != null
                && adapter != null) {
            listaTrevos.setAdapter(adapter);
        }
    }
    @Override
    public boolean possuiSelecaoValida() {
        return isApostaTrevosCompleta();
    }

    @Override
    public EtapaAposta getEtapa() {
        return EtapaAposta.TREVOS;
    }


}
