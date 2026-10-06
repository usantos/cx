package br.gov.caixa.loterias.apostas.view.features.volantenovo.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaFavoritaDTO;
import br.gov.caixa.loterias.apostas.utils.BarraTituloDTO;
import br.gov.caixa.loterias.apostas.view.features.volantenovo.presentation.SimulaUiEvent;
import br.gov.caixa.loterias.apostas.view.features.volantenovo.presentation.SimulaUiState;
import br.gov.caixa.loterias.apostas.view.features.volantenovo.presentation.SimulaViewModel;
import br.gov.caixa.loterias.apostas.view.fragment.EtapaFragment;
import br.gov.caixa.loterias.apostas.utils.shake.ApostaShakeController;

public abstract class BaseEtapaFragment
        extends Fragment
        implements EtapaFragment {

    protected SimulaViewModel viewModel;
    private ApostaShakeController apostaShake;

    protected boolean suportaApostaShake() { return false; }
    protected boolean apostaShakeCompleta() { return possuiSelecaoValida(); }
    protected void preencherApostaShake(boolean renovar) { }

    @Override public void onResume() {
        super.onResume();
        if (!suportaApostaShake()) return;
        if (apostaShake == null) {
            apostaShake = new ApostaShakeController(requireActivity(), new ApostaShakeController.Volante() {
                @Override public boolean disponivel() {
                    return isResumed() && isVisible() && !getStateAtual().isSurpresinhaHabilitada();
                }
                @Override public boolean completo() { return apostaShakeCompleta(); }
                @Override public void preencher(boolean renovar) { preencherApostaShake(renovar); }
            });
        }
        apostaShake.iniciar();
    }

    @Override public void onPause() {
        if (apostaShake != null) apostaShake.parar();
        super.onPause();
    }

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
                new SimulaUiEvent.SalvarApostaFavoritaSolicitado(
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
