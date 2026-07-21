package br.gov.caixa.loterias.apostas.viewModel.reducer;


import br.gov.caixa.loterias.apostas.effect.CompletarApostaEffect;
import br.gov.caixa.loterias.apostas.event.SimulaUiEvent;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.state.SimulaUiState;
import br.gov.caixa.loterias.apostas.utils.EtapaAposta;

public class AdicionarCarrinhoReducer {

    public SimulaUiState adicionarCarrinho(
            SimulaUiState atual,
            SimulaUiEvent.AdicionarCarrinhoClicado event
    ) {

        if (atual == null || event == null) {
            return atual;
        }

        if (event.isEscolhaTimeCoracaoSurpresinha()) {

            return publicarComando(
                    atual,
                    new CompletarApostaEffect
                            .AdicionarTimeSurpresa()
            );
        }

        ModalidadeEnum tipoJogo =
                event.getTipoJogo();

        if (tipoJogo == ModalidadeEnum.TIMEMANIA) {
            return adicionarTimemania(
                    atual,
                    event
            );
        }

        if (tipoJogo == ModalidadeEnum.DIA_DE_SORTE) {
            return adicionarDiaDeSorte(
                    atual,
                    event
            );
        }

        if (tipoJogo == ModalidadeEnum.MAIS_MILIONARIA) {
            return adicionarMaisMilionaria(
                    atual,
                    event
            );
        }

        if (!isApostaPronta(event)) {
            return atual;
        }

        return finalizarFluxoAdicionar(
                atual
        );
    }

    private SimulaUiState adicionarTimemania(
            SimulaUiState atual,
            SimulaUiEvent.AdicionarCarrinhoClicado event
    ) {

        boolean numerosCompletos =
                isNumerosCompletos(
                        event.getQtdDezenasSelecionadas(),
                        event.getQtdDezenasPossiveisSelecionado()
                );

        if (!numerosCompletos) {
            return atual;
        }

        if (!event.isTelaSelecaoTimeAtivado()) {
            return atual;
        }

        if (!event.isEquipeSelecionadaValida()) {
            return atual;
        }

        return finalizarFluxoAdicionar(
                atual
        );
    }

    private SimulaUiState adicionarDiaDeSorte(
            SimulaUiState atual,
            SimulaUiEvent.AdicionarCarrinhoClicado event
    ) {

        boolean numerosCompletos =
                isNumerosCompletos(
                        event.getQtdDezenasSelecionadas(),
                        event.getQtdDezenasPossiveisSelecionado()
                );

        if (!numerosCompletos) {
            return atual;
        }

        if (event.isFragmentCartela()) {
            return atual;
        }

        return finalizarFluxoAdicionar(
                atual
        );
    }

    private SimulaUiState adicionarMaisMilionaria(
            SimulaUiState atual,
            SimulaUiEvent.AdicionarCarrinhoClicado event
    ) {

        boolean numerosCompletos =
                isNumerosCompletos(
                        event.getQtdDezenasSelecionadas(),
                        event.getQtdDezenasPossiveisSelecionado()
                );

        if (!numerosCompletos) {
            return atual;
        }

        if (event.isFragmentCartela()) {
            return atual;
        }

        return finalizarFluxoAdicionar(
                atual
        );
    }

    private SimulaUiState finalizarFluxoAdicionar(
            SimulaUiState atual
    ) {

        return atual.toBuilder()
                .setEtapaAposta(
                        EtapaAposta.PRONTA
                )
                .setBotaoNextStepHabilitado(false)
                .setBotaoCompletarHabilitado(false)
                .setBotaoAdicionarHabilitado(true)
                .setBotaoLimparHabilitado(true)
                .setComandoCompletarAposta(
                        new CompletarApostaEffect
                                .ApostaEffectProntaParaAdicionar(
                                false
                        )
                )
                .build();
    }

    private SimulaUiState publicarComando(
            SimulaUiState atual,
            CompletarApostaEffect comando
    ) {

        return atual.toBuilder()
                .setComandoCompletarAposta(
                        comando
                )
                .setMostrarBotaoLimparAposta(true)
                .build();
    }

    public boolean isApostaPronta(
            SimulaUiEvent.AdicionarCarrinhoClicado event
    ) {

        ModalidadeEnum tipoJogo =
                event.getTipoJogo();

        if (tipoJogo == ModalidadeEnum.TIMEMANIA) {

            boolean numerosCompletos =
                    isNumerosCompletos(
                            event.getQtdDezenasSelecionadas(),
                            event.getQtdDezenasPossiveisSelecionado()
                    );

            return numerosCompletos
                    && event.isTelaSelecaoTimeAtivado()
                    && event.isEquipeSelecionadaValida();
        }

        if (tipoJogo == ModalidadeEnum.DIA_DE_SORTE
                || tipoJogo == ModalidadeEnum.MAIS_MILIONARIA) {

            boolean numerosCompletos =
                    isNumerosCompletos(
                            event.getQtdDezenasSelecionadas(),
                            event.getQtdDezenasPossiveisSelecionado()
                    );

            return numerosCompletos
                    && !event.isFragmentCartela();
        }

        if (tipoJogo == ModalidadeEnum.SUPER_7) {

            return event.getQtdTotalSelecionadosSuperSete()
                    == event.getQtdDezenasPossiveisSelecionado();
        }

        if (tipoJogo == ModalidadeEnum.LOTECA) {

            boolean partidasCompletas =
                    event.getQtdPartidasSelecionadas()
                            == event.getQtdPartidasTotal();

            boolean possuiDuploOuTriplo =
                    event.getDuplosLoteca() > 0
                            || event.getTriplosLoteca() > 0;

            return partidasCompletas
                    && possuiDuploOuTriplo;
        }

        if (tipoJogo == ModalidadeEnum.LOTOGOL) {

            return event.getQtdPartidasSelecionadas()
                    == event.getQtdPartidasTotal();
        }

        return event.getQtdDezenasSelecionadas()
                == event.getQtdDezenasPossiveisSelecionado();
    }

    private boolean isNumerosCompletos(
            int selecionadas,
            int possiveis
    ) {

        return selecionadas == possiveis;
    }
}