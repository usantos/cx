package br.gov.caixa.loterias.apostas.view.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;

import br.gov.caixa.loterias.apostas.event.SimulaUiEvent;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaFavoritaDTO;
import br.gov.caixa.loterias.apostas.state.SimulaUiState;
import br.gov.caixa.loterias.apostas.utils.BarraTituloDTO;
import br.gov.caixa.loterias.apostas.viewModel.SimulaViewModel;

public abstract class BaseEtapaFragment
        extends Fragment
        implements EtapaFragment {

    protected SimulaViewModel viewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel =
                new ViewModelProvider(
                        requireActivity()
                ).get(SimulaViewModel.class);
    }
    protected SimulaUiState getStateAtual() {

        if (viewModel == null
                || viewModel.getUiState() == null
                || viewModel.getUiState().getValue() == null) {

            return SimulaUiState.initial();
        }

        return viewModel.getUiState().getValue();
    }
    @Override
    public void onSalvarFavoritoRodapeConfirmado(
            String nome
    ) {

        dispatch(
                new SimulaUiEvent
                        .SalvarApostaFavoritaSolicitado(
                        criarApostaFavoritaDTO(),
                        nome,
                        getDezenasSelecionadas()
                )
        );
    }

    protected void dispatch(
            SimulaUiEvent event
    ) {
        if(viewModel != null) {
            viewModel.onEvent(event);
        }
    }
    protected abstract ApostaFavoritaDTO criarApostaFavoritaDTO();
    protected Object getDezenasSelecionadas() {

        return new ArrayList<>(
                getStateAtual()
                        .getDezenasSelecionadas()
        );
    }

    protected BarraTituloDTO getBarraTitulo() {

        return getStateAtual().getBarraTituloDTO() != null
                ? getStateAtual().getBarraTituloDTO()
                : new BarraTituloDTO();
    }
    @Override
    public void onQuantidadeNumerosAlterada() {}
}