package br.gov.caixa.loterias.apostas.viewModel.reducer;

import br.gov.caixa.loterias.apostas.effect.CompletarApostaEffect;
import br.gov.caixa.loterias.apostas.event.SimulaUiEvent;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.state.SimulaUiState;

public class CompletarApostaReducer {

    public SimulaUiState completarAposta(
            SimulaUiState atual,
            SimulaUiEvent.CompletarApostaClicado event
    ) {

        if (atual == null || event == null) {
            return atual;
        }

        CompletarApostaEffect comando =
                obterComandoCompletar(
                        event
                );

        if (comando == null) {
            return atual;
        }

        return publicarComando(
                atual,
                comando
        );
    }

    public SimulaUiState publicarComando(
            SimulaUiState atual,
            CompletarApostaEffect comando
    ) {

        if (atual == null) {
            return null;
        }

        if (comando == null) {
            return atual;
        }

        return atual.toBuilder()
                .setComandoCompletarAposta(
                        comando
                )
                .setMostrarBotaoLimparAposta(
                        true
                )
                .build();
    }

    public SimulaUiState limparComando(
            SimulaUiState atual
    ) {

        if (atual == null) {
            return null;
        }

        return atual.toBuilder()
                .setComandoCompletarAposta(null)
                .build();
    }

    private CompletarApostaEffect obterComandoCompletar(
            SimulaUiEvent.CompletarApostaClicado event
    ) {

        ModalidadeEnum tipoJogo =
                event.getTipoJogo();

        if (tipoJogo == ModalidadeEnum.TIMEMANIA) {
            return obterComandoTimemania(
                    event
            );
        }

        if (tipoJogo == ModalidadeEnum.DIA_DE_SORTE) {
            return obterComandoDiaDeSorte(
                    event
            );
        }

        if (tipoJogo == ModalidadeEnum.MAIS_MILIONARIA) {
            return obterComandoMaisMilionaria(
                    event
            );
        }

        if (tipoJogo == ModalidadeEnum.SUPER_7) {
            return new CompletarApostaEffect
                    .PreencherSuperSete();
        }

        if (tipoJogo == ModalidadeEnum.LOTECA) {
            return new CompletarApostaEffect
                    .ZerarLoteca();
        }

        return new CompletarApostaEffect
                .PreencherNumerosAleatorios();
    }

    private CompletarApostaEffect obterComandoTimemania(
            SimulaUiEvent.CompletarApostaClicado event
    ) {

        boolean numerosCompletos =
                isNumerosCompletos(
                        event.getQtdDezenasSelecionadas(),
                        event.getQtdDezenasPossiveisSelecionado()
                );

        if (!numerosCompletos) {
            return new CompletarApostaEffect
                    .PreencherNumerosAleatorios();
        }

        if (event.isTelaSelecaoTimeAtivado()
                && !event.isEquipeSelecionadaValida()) {
            return new CompletarApostaEffect
                    .SelecionarTimeAleatorio();
        }

        return null;
    }

    private CompletarApostaEffect obterComandoDiaDeSorte(
            SimulaUiEvent.CompletarApostaClicado event
    ) {

        boolean numerosCompletos =
                isNumerosCompletos(
                        event.getQtdDezenasSelecionadas(),
                        event.getQtdDezenasPossiveisSelecionado()
                );

        if (!numerosCompletos) {
            return new CompletarApostaEffect
                    .PreencherNumerosAleatorios();
        }

        return null;
    }

    private CompletarApostaEffect obterComandoMaisMilionaria(
            SimulaUiEvent.CompletarApostaClicado event
    ) {

        boolean numerosCompletos =
                isNumerosCompletos(
                        event.getQtdDezenasSelecionadas(),
                        event.getQtdDezenasPossiveisSelecionado()
                );

        if (!numerosCompletos) {
            return new CompletarApostaEffect
                    .PreencherNumerosAleatorios();
        }

        return null;
    }

    private boolean isNumerosCompletos(
            int qtdSelecionadas,
            int qtdPossiveis
    ) {

        return qtdSelecionadas == qtdPossiveis;
    }
}