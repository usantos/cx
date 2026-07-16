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

    private ModalidadeEnum modalidadeEnum;
    private ParametroJogoDTO parametroJogoDTO;
    private ArrayList<Integer> dezenasSelecionadas;

    private ParametroMesDeSorte mesDeSorte;
    private SimulaActivity parentActivity;
    private MesesAdapter mesesAdapter;
    private View view;
    private SimulaViewModel viewModel;


    private MesesFragment2(){}

    public static MesesFragment2 newInstance(ModalidadeEnum modalidade,
                                            ParametroJogoDTO parametroJogoDTO,
                                            ArrayList<Integer> dezenasSelecionadas){
        MesesFragment2 fragment  = new MesesFragment2();
        Bundle        args      = new Bundle();
        args.putSerializable(ARG_MODADLIDADE, modalidade);
        args.putSerializable(ARG_PARAMENTRO, parametroJogoDTO);
        args.putString(ARG_DEZENAS, String.valueOf(dezenasSelecionadas));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null){
            modalidadeEnum = (ModalidadeEnum) getArguments().getSerializable(ARG_MODADLIDADE);
            parametroJogoDTO = (ParametroJogoDTO) getArguments().getSerializable(ARG_PARAMENTRO);
            String json = getArguments().getString(ARG_DEZENAS);
            if(json!= null && json.length() > 0){
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

        switch (modalidadeEnum) {
            case DIA_DE_SORTE:
                setTelaDiaSorte();
                break;
            default:
                break;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void setTelaDiaSorte() {
        //parentActivity.getQtdNumerosButton().setOnClickListener(null);

//        parentActivity.getBotaoAdicionarCompletarCartela().setText(ViewUtils.textFuturaAndFuturaBold(getContext(), getString(R.string.adicionarAoCarrinho)));
        if (dezenasSelecionadas == null) {
            parentActivity.getBotaoLimparAposta().setVisibility(View.VISIBLE);
        }
        initRecyclerView();
        setMesDeSorte();

        acaoBotaoLimpar();
    }

    private void acaoBotaoLimpar() {
        parentActivity.getBotaoLimparAposta().setOnClickListener(v -> {
            limparTodosMeses();

            mesesAdapter.notifyDataSetChanged();

            limparMesDeSorte();

            viewModel.onEvent(
                    new SimulaUiEvent.MesSorteAlterado(
                            false
                    )
            );

        });
    }
    public boolean possuiMesSelecionado() {
        return getItemSelecionado() != null;
    }
    public void onAdicionarRodapeClicado() {

        if (!possuiMesSelecionado()) {
            return;
        }

        viewModel.onEvent(
                new SimulaUiEvent.AdicionarCarrinhoSolicitado(
                        ApostaUtils.getApostaDiaDeSorte(
                                parametroJogoDTO,
                                dezenasSelecionadas,
                                parentActivity.getValorTotalApostaAtual(),
                                parentActivity.qtdConcursoSelecionado,
                                parentActivity.getMesDeSorteCartela()
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

        if (meses == null || meses.isEmpty()) {
            return;
        }

        int random =
                new Random().nextInt(
                        meses.size()
                );

        ParametroMesDeSorte mes =
                meses.get(random);

        limparTodosMeses();

        mes.setSelecionado(true);

        setMesDeSorte(mes);

        mesesAdapter.notifyDataSetChanged();

        viewModel.onEvent(
                new SimulaUiEvent.MesSorteAlterado(
                        true
                )
        );
    }

    public void onLimparRodapeClicado() {

        limparTodosMeses();

        mesesAdapter.notifyDataSetChanged();

        limparMesDeSorte();

        viewModel.onEvent(
                new SimulaUiEvent.MesSorteAlterado(
                        false
                )
        );
    }

    private void initRecyclerView() {
        mesesAdapter = new MesesAdapter(parametroJogoDTO.getMeses());
        mesesGridView.setAdapter(mesesAdapter);
        mesesGridView.setExpanded(true);

        mesesGridView.setOnItemClickListener((parent, view, position, id) -> {
            ItemRetanguloTextView viewClickada = (ItemRetanguloTextView) view;
            limparTodosMeses();
            viewClickada.getMesDeSorte().setSelecionado(true);
            setMesDeSorte(viewClickada.getMesDeSorte());
            mesesAdapter.notifyDataSetChanged();
            viewModel.onEvent(
                    new SimulaUiEvent.MesSorteAlterado(true)
            );
        });
    }

    private void limparTodosMeses() {
        if (parametroJogoDTO.getMeses() != null) {
            for (ParametroMesDeSorte mesDeSorte : parametroJogoDTO.getMeses()) {
                mesDeSorte.setSelecionado(false);
            }
        }
    }

    private ParametroMesDeSorte getItemSelecionado() {
        if (parametroJogoDTO.getMeses() != null) {
            for (ParametroMesDeSorte mesDeSorte : parametroJogoDTO.getMeses()) {
                if (mesDeSorte.isSelecionado()) {
                    return mesDeSorte;
                }
            }
        }
        return null;
    }

    private void setMesDeSorte() {
        if(dezenasSelecionadas == null){
            this.mesDeSorte = parentActivity.getMesDeSorteSurpresinha();
        }else{
            this.mesDeSorte = parentActivity.getMesDeSorteCartela();
        }

        if (parametroJogoDTO.getMeses() != null && this.mesDeSorte != null) {
            for (ParametroMesDeSorte mesDeSorte : parametroJogoDTO.getMeses()) {
                if (mesDeSorte.getNumero().equals(this.mesDeSorte.getNumero())) {
                    mesDeSorte.setSelecionado(true);
                    break;
                }
            }
        }
        mesesAdapter.notifyDataSetChanged();
    }

    private void setMesDeSorte(ParametroMesDeSorte mesDeSorte) {
        if (dezenasSelecionadas != null) {
            parentActivity.setMesDeSorteCartela(mesDeSorte);
        } else {
            parentActivity.setMesDeSorteSurpresinha(mesDeSorte);
        }
    }

    private void limparMesDeSorte() {
        if (dezenasSelecionadas != null) {
            parentActivity.setMesDeSorteCartela(getItemSelecionado());
        }else{
            parentActivity.setMesDeSorteSurpresinha(getItemSelecionado());
        }
    }

}
