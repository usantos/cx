package br.gov.caixa.loterias.apostas.view.features.volantenovo.presentation;

import br.gov.caixa.loterias.apostas.utils.EtapaAposta;

public class MesDaSorteReducer {

    public SimulaUiState mesSorteAlterado(
            SimulaUiState atual,
            SimulaUiEvent.MesSorteAlterado event
    ) {

        if (atual == null || event == null) {
            return atual;
        }

        return aplicarEstadoMesSorte(
                atual,
                event.isMesSelecionado()
        );
    }

    public SimulaUiState selecionarMesDaSorte(
            SimulaUiState atual,
            SimulaUiEvent.SelecionarMesDaSorte event
    ) {

        if (atual == null || event == null) {
            return atual;
        }

        SimulaUiState novoEstado =
                atual.copy()
                        .setMesSelecionado(
                                event.getMes()
                        );

        return aplicarEstadoMesSorte(
                novoEstado,
                true
        );
    }

    public SimulaUiState limparMesDaSorte(
            SimulaUiState atual
    ) {

        if (atual == null) {
            return null;
        }

        SimulaUiState novoEstado =
                atual.copy()
                        .setMesSelecionado(null);

        return aplicarEstadoMesSorte(
                novoEstado,
                false
        );
    }

    private SimulaUiState aplicarEstadoMesSorte(
            SimulaUiState atual,
            boolean mesSelecionado
    ) {

        return atual
                .setEtapaAposta(
                        EtapaAposta.MES_SORTE
                )
                .setBotaoNextStepHabilitado(false)
                .setBotaoAdicionarHabilitado(
                        mesSelecionado
                )
                .setBotaoCompletarHabilitado(
                        !mesSelecionado
                )
                .setBotaoCompletarVisivel(
                        !mesSelecionado
                )
                .setBotaoLimparHabilitado(
                        mesSelecionado
                )
                .setMostrarBotaoLimparAposta(
                        mesSelecionado
                )
                .setMostrarSalvarAposta(
                        mesSelecionado
                )
                .setApostaFavoritada(
                        mesSelecionado
                                && atual.isApostaFavoritada()
                );
    }
}
