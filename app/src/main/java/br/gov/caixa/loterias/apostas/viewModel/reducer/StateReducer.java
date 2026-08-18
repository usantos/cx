package br.gov.caixa.loterias.apostas.viewModel.reducer;

import br.gov.caixa.loterias.apostas.state.SimulaUiState;

public interface StateReducer {

    SimulaUiState reduce(
            SimulaUiState state
    );
}