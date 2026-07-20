package br.gov.caixa.loterias.apostas.view.fragment;

import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.event.SimulaUiEvent;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.Dezena;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroJogoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroValorApostaDTO;
import br.gov.caixa.loterias.apostas.model.model.TrevoModel;
import br.gov.caixa.loterias.apostas.state.SimulaUiState;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.ApostaUtils;
import br.gov.caixa.loterias.apostas.utils.DialogUtils;
import br.gov.caixa.loterias.apostas.utils.ListaUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.ListaDezenaRecyclerView;
import br.gov.caixa.loterias.apostas.view.config.DezenaConfig;
import br.gov.caixa.loterias.apostas.view.config.ShapeConfig;
import br.gov.caixa.loterias.apostas.view.holder.DezenaHolder;
import br.gov.caixa.loterias.apostas.view.listener.OnDialogListener;
import br.gov.caixa.loterias.apostas.view.listener.OnItemClickListener;
import br.gov.caixa.loterias.apostas.viewModel.SimulaViewModel;

public class TrevosFragment2 extends Fragment implements OnItemClickListener<DezenaHolder> {
    private static final String ARG_PARAMENTRO = "ARG_PARAMENTRO";
    private static final String ARG_SELECIONADOS = "ARG_SELECIONADOS";
    private boolean isAvisou = false;
    private static final int QUANTIDADE_COLUNA_LISTA = 3;
    private RecyclerView listaTrevos;
    private ParametroJogoDTO parametro;
    private ArrayList<Integer> dezenasSelecionadas;
    private IdentificaoDeUmaApostaDas8Modalidades aposta;
    private List<ParametroValorApostaDTO> valoresApostas;
    private ParametroValorApostaDTO valorApostaSelecionado;
    private List<Dezena> dezenaList;
    private List<Integer> trevoSelecionadoList = new ArrayList<>();
    private ListaDezenaRecyclerView adapter;

    private TrevoModel model;
    private SimulaViewModel viewModel;
    private SimulaUiState state;

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

    private void init() {
        viewModel = new ViewModelProvider(
                requireActivity()
        ).get(SimulaViewModel.class);
        state = viewModel.getUiState().getValue();

        valoresApostas =
                parametro.getValoresTrevoByNumero(
                        getQuantidadeNumeros()
                );

        if (valoresApostas == null || valoresApostas.isEmpty()) {
            return;
        }

        valorApostaSelecionado =
                valoresApostas.get(0);
        viewModel.onEvent(
                new SimulaUiEvent.QuantidadeTrevosAlterada(
                        valorApostaSelecionado
                )
        );

        initRecyclerView();
        model = new TrevoModel(getActivity());
//        editAposta();
    }

    private void initRecyclerView(){
        DezenaConfig dezenaConfig = new DezenaConfig(false, R.color.milionaria_escuro_mkp,
                R.layout.item_dezena_trevo,
                new ShapeConfig(R.drawable.ic_item_trevo,
                        R.drawable.ic_item_trevo_selecionado,
                        R.color.milionaria_escuro_mkp, R.color.milionaria_escuro_mkp),
                true);
        adapter = new ListaDezenaRecyclerView(getListaDezenas(), trevoSelecionadoList, dezenaConfig, this);
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

        trevoSelecionadoList =
                ListaUtils.orderAscDezenas(
                        trevoSelecionadoList
                );

        viewModel.onEvent(
                new SimulaUiEvent.AdicionarCarrinhoSolicitado(
                        ApostaUtils.getApostaMaisMilionaria(
                                parametro,
                                dezenasSelecionadas,
                                estadoAtual.getValorAposta(),
                                estadoAtual.getQtdConcursoSelecionado(),
                                trevoSelecionadoList
                        ),
                        estadoAtual.getBarraTituloDTO(),
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
        if (valorApostaSelecionado == null
                || valorApostaSelecionado.getNumeroTrevos() == null) {
            return false;
        }

        return trevoSelecionadoList != null
                && trevoSelecionadoList.size()
                >= valorApostaSelecionado.getNumeroTrevos();
    }

    private void limparTrevosSelecionados() {
        if (trevoSelecionadoList != null) {
            trevoSelecionadoList.clear();
        }

        if (adapter != null) {
            adapter.atualizaSelecionados(
                    trevoSelecionadoList
            );
        }

        if (viewModel != null && valorApostaSelecionado != null) {
            viewModel.onEvent(
                    new SimulaUiEvent.TrevosAlterados(
                            0,
                            valorApostaSelecionado.getNumeroTrevos(),
                            valorApostaSelecionado
                    )
            );
        }
    }

    private void completarTrevosAleatoriamente() {
        AlertDialogUtils.show(getContext());
        model.preencheNumerosAleatorios(valorApostaSelecionado.getNumeroTrevos(), trevoSelecionadoList,
                parametro.getTrevos().getQtdMaxima(), new OnSilceListener<List<Integer>>() {
                    @Override
                    public void success(List<Integer> list) {
                        AlertDialogUtils.dismiss();
                        trevoSelecionadoList.clear();
                        trevoSelecionadoList.addAll(list);
                        adapter.atualizaSelecionados(list);
                        viewModel.onEvent(
                                new SimulaUiEvent.TrevosAlterados(
                                        trevoSelecionadoList.size(),
                                        valorApostaSelecionado.getNumeroTrevos(),
                                        valorApostaSelecionado
                                )
                        );
                    }

                    @Override
                    public void error(VolleyError error) {
                        AlertDialogUtils.dismiss();
                    }
                });
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }
    private ParametroValorApostaDTO buscarValorAtualTrevos() {

        if (valoresApostas == null) {
            return valorApostaSelecionado;
        }

        int quantidadeAtual =
                trevoSelecionadoList.size();

        for (ParametroValorApostaDTO valor : valoresApostas) {

            if (valor.getNumeroTrevos() != null
                    && valor.getNumeroTrevos() == quantidadeAtual) {

                return valor;
            }
        }

        return valorApostaSelecionado;
    }

    @Override
    public void itemClick(DezenaHolder holder, int position) {
        Dezena dezena = dezenaList.get(position);
        dezena.setSelected(!dezena.isSelected());
        Integer numero = Integer.valueOf(dezena.getValue());
        if (dezena.isSelected()) {
            if (!trevoSelecionadoList.contains(numero)) {
                trevoSelecionadoList.add(numero);
                if ((trevoSelecionadoList.size() > 2) && (!isAvisou)) {
                    DialogUtils.dialogEntendi(
                            getActivity(),
                            getString(R.string.msg_qtd_max_ultrapassada_milionaria)
                    );
                    isAvisou = true;
                }
            }
        } else {
            trevoSelecionadoList.remove(numero);
        }
        holder.atualizaDezena(dezena);

        ParametroValorApostaDTO valorAtual =
                buscarValorAtualTrevos();

        if (valorAtual != null) {
            valorApostaSelecionado =
                    valorAtual;
        }

        if (valorApostaSelecionado == null
                || valorApostaSelecionado.getNumeroTrevos() == null) {
            return;
        }

        viewModel.onEvent(
                new SimulaUiEvent.TrevosAlterados(
                        trevoSelecionadoList.size(),
                        valorApostaSelecionado.getNumeroTrevos(),
                        valorApostaSelecionado
                )
        );
    }
    public int getQuantidadeNumeros() {
        return dezenasSelecionadas != null
                ? dezenasSelecionadas.size()
                : 0;
    }

    private SimulaUiState getStateAtual() {
        if (viewModel == null
                || viewModel.getUiState() == null
                || viewModel.getUiState().getValue() == null) {
            return SimulaUiState.initial();
        }

        return viewModel.getUiState().getValue();
    }

    public void atualizarQuantidadeTrevos(
            ParametroValorApostaDTO valorSelecionado
    ) {
        if (valorSelecionado == null) {
            return;
        }

        if (parametro == null
                || parametro.getTrevos() == null) {
            return;
        }

        if (valoresApostas == null || valoresApostas.isEmpty()) {
            valoresApostas =
                    parametro.getValoresTrevoByNumero(
                            getQuantidadeNumeros()
                    );
        }

        valorApostaSelecionado =
                valorSelecionado;

        int ultimoValor =
                valoresApostas != null
                        ? valoresApostas.size() - 1
                        : -1;

        if (ultimoValor >= 0
                && valoresApostas.get(ultimoValor)
                .equals(valorApostaSelecionado)) {

            trevoSelecionadoList =
                    new ArrayList<>();

            for (int numero = 1;
                 numero <= parametro.getTrevos().getQtdPrognostico();
                 numero++) {

                trevoSelecionadoList.add(numero);
            }

            atualizarAdapterTrevos();
        }

        if (trevoSelecionadoList != null
                && valorApostaSelecionado.getNumeroTrevos() != null
                && trevoSelecionadoList.size()
                > valorApostaSelecionado.getNumeroTrevos()) {

            trevoSelecionadoList =
                    new ArrayList<>();

            atualizarAdapterTrevos();
        }

        viewModel.onEvent(
                new SimulaUiEvent.TrevosAlterados(
                        trevoSelecionadoList != null
                                ? trevoSelecionadoList.size()
                                : 0,
                        valorApostaSelecionado.getNumeroTrevos(),
                        valorApostaSelecionado
                )
        );
    }

    private void atualizarAdapterTrevos() {
        if (adapter != null) {
            adapter.atualizaSelecionados(
                    trevoSelecionadoList
            );
        }

        if (listaTrevos != null
                && adapter != null) {
            listaTrevos.setAdapter(adapter);
        }
    }

}
