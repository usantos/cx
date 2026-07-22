package br.gov.caixa.loterias.apostas.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.event.SimulaUiEvent;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroJogoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroMesDeSorte;
import br.gov.caixa.loterias.apostas.state.SimulaUiState;
import br.gov.caixa.loterias.apostas.utils.ApostaUtils;
import br.gov.caixa.loterias.apostas.view.activity.SimulaActivity;
import br.gov.caixa.loterias.apostas.view.adapter.baseadapter.MesesAdapter;
import br.gov.caixa.loterias.apostas.view.custom.ExpandableHeightGridView;
import br.gov.caixa.loterias.apostas.view.custom.ItemRetanguloTextView;
import br.gov.caixa.loterias.apostas.viewModel.SimulaViewModel;

public class MesesFragment2 extends Fragment {
    private static final String ARG_MODADLIDADE = "ARG_MODALIDADE";
    private static final String ARG_PARAMENTRO = "ARG_PARAMENTRO";
    private static final String ARG_DEZENAS = "ARG_DEZENAS";

    private ExpandableHeightGridView mesesGridView;

    private ParametroJogoDTO parametroJogoDTO;
    private ArrayList<Integer> dezenasSelecionadas;

    private SimulaActivity parentActivity;
    private MesesAdapter mesesAdapter;
    private View view;
    private SimulaViewModel viewModel;


    private MesesFragment2(){}

    public static MesesFragment2 newInstance(ParametroJogoDTO parametroJogoDTO,
                                            ArrayList<Integer> dezenasSelecionadas){
        MesesFragment2 fragment  = new MesesFragment2();
        Bundle        args      = new Bundle();
        args.putSerializable(ARG_PARAMENTRO, parametroJogoDTO);
        args.putString(ARG_DEZENAS, String.valueOf(dezenasSelecionadas));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null){
            parametroJogoDTO = (ParametroJogoDTO) getArguments().getSerializable(ARG_PARAMENTRO);
            String json = getArguments().getString(ARG_DEZENAS);
            if(json!= null && !json.isEmpty()){
                dezenasSelecionadas = new Gson().fromJson(json, new TypeToken<List<Integer>>() {}.getType());
            } else {
                dezenasSelecionadas = new ArrayList<>();
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_meses, container, false);
        mesesGridView = view.findViewById(R.id.mesesGridView);
        init();
        return view;
    }

    private void init() {
        parentActivity = ((SimulaActivity) getActivity());
        viewModel = new ViewModelProvider(
                requireActivity()
        ).get(SimulaViewModel.class);
        setTelaDiaSorte();
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

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void setTelaDiaSorte() {
        initRecyclerView();
        acaoBotaoLimpar();
    }

    private void acaoBotaoLimpar() {

        parentActivity
                .getBotaoLimparAposta()
                .setOnClickListener(
                        v ->
                                viewModel.onEvent(
                                        new SimulaUiEvent
                                                .LimparMesDaSorte()
                                )
                );
    }

    public boolean possuiMesSelecionado() {

        return getStateAtual()
                .getMesSelecionado()
                != null;
    }
    public void onAdicionarRodapeClicado() {

        if (!possuiMesSelecionado()) {
            return;
        }
        SimulaUiState state =
                getStateAtual();

        ParametroMesDeSorte mes =
                state.getMesSelecionado();

        if (mes == null) {
            return;
        }

        viewModel.onEvent(
                new SimulaUiEvent.AdicionarCarrinhoSolicitado(
                        ApostaUtils.getApostaDiaDeSorte(
                                parametroJogoDTO,
                                dezenasSelecionadas,
                                state.getValorAposta(),
                                state.getQtdConcursoSelecionado(),
                                mes
                        ),
                        parentActivity.getBarraTituloDTO(),
                        false,
                        null
                )
        );
    }

    public void onCompletarRodapeClicado() {

        if (possuiMesSelecionado()) {
            return;
        }

        List<ParametroMesDeSorte> meses =
                parametroJogoDTO.getMeses();

        if (meses == null
                || meses.isEmpty()) {
            return;
        }

        int random =
                new Random().nextInt(
                        meses.size()
                );

        viewModel.onEvent(
                new SimulaUiEvent
                        .SelecionarMesDaSorte(
                        meses.get(random)
                )
        );
    }
    private SimulaUiState getStateAtual() {

        if (viewModel == null
                || viewModel.getUiState() == null
                || viewModel.getUiState().getValue() == null) {

            return SimulaUiState.initial();
        }

        return viewModel.getUiState().getValue();
    }

    public void onLimparRodapeClicado() {

        viewModel.onEvent(
                new SimulaUiEvent
                        .LimparMesDaSorte()
        );
    }

    private void initRecyclerView() {
        mesesAdapter = new MesesAdapter(parametroJogoDTO.getMeses());
        mesesGridView.setAdapter(mesesAdapter);
        mesesGridView.setExpanded(true);

        mesesGridView.setOnItemClickListener(
                (parent, view, position, id) -> {

                    ItemRetanguloTextView item =
                            (ItemRetanguloTextView) view;

                    viewModel.onEvent(
                            new SimulaUiEvent
                                    .SelecionarMesDaSorte(
                                    item.getMesDeSorte()
                            )
                    );
                }
        );
    }

    private void limparTodosMeses() {
        if (parametroJogoDTO.getMeses() != null) {
            for (ParametroMesDeSorte mesDeSorte : parametroJogoDTO.getMeses()) {
                mesDeSorte.setSelecionado(false);
            }
        }
    }
    private void renderState(
            SimulaUiState state
    ) {

        renderMesSelecionado(state);
    }
    private void renderMesSelecionado(
            SimulaUiState state
    ) {

        limparTodosMeses();

        ParametroMesDeSorte mesSelecionado =
                state.getMesSelecionado();

        if (mesSelecionado == null) {

            mesesAdapter.notifyDataSetChanged();

            return;
        }

        for (ParametroMesDeSorte mes :
                parametroJogoDTO.getMeses()) {

            if (mes.getNumero()
                    .equals(
                            mesSelecionado.getNumero()
                    )) {

                mes.setSelecionado(true);

                break;
            }
        }

        mesesAdapter.notifyDataSetChanged();
    }


}
