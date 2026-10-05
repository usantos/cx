package br.gov.caixa.loterias.apostas.view.features.volantenovo.presentation;

import br.gov.caixa.loterias.apostas.effect.AnalyticsEffect;
import br.gov.caixa.loterias.apostas.effect.CompletarApostaEffect;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
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
        if (tipoJogo == ModalidadeEnum.LOTECA){
            return adicionarLoteca(
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

        if (atual.getEtapaAposta() != EtapaAposta.TIME_CORACAO
                && atual.getEtapaAposta() != EtapaAposta.PRONTA) {
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
    private SimulaUiState adicionarLoteca(
            SimulaUiState atual,
            SimulaUiEvent.AdicionarCarrinhoClicado event
    ) {
        int totalPalpitesSelecionados =
                atual.getSimplesLoteca()
                        + (atual.getDuplasLoteca() * 2)
                        + (atual.getTriplasLoteca() * 3);

        boolean todasPartidasPreenchidas =
                atual.getJogosLoteca()
                        == event.getQtdPartidasTotal();

        boolean possuiDuploOuTriploLoteca =
                atual.getDuplasLoteca() > 0
                        || atual.getTriplasLoteca() > 0;

        boolean apostaCompleta =
                todasPartidasPreenchidas
                        && totalPalpitesSelecionados
                        == atual.getPalpitesLoteca()
                        && possuiDuploOuTriploLoteca;

        if (!apostaCompleta) {
            return atual;
        }

        return finalizarFluxoAdicionar(atual);
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
        return atual.copy()
                .setEtapaAposta(
                        EtapaAposta.PRONTA
                )
                .setBotaoNextStepHabilitado(false)
                .setBotaoCompletarHabilitado(false)
                .setBotaoAdicionarHabilitado(true)
                .setBotaoLimparHabilitado(true)
                .setMostrarBotaoLimparAposta(true)
                .setComandoAnalytics(
                        new AnalyticsEffect.AdicionarCarrinho(
                                atual.getTipoJogo()
                        )
                )
                .setComandoCompletarAposta(
                        new CompletarApostaEffect
                                .ApostaEffectProntaParaAdicionar(
                                false
                        )
                );
    }

    private SimulaUiState publicarComando(
            SimulaUiState atual,
            CompletarApostaEffect comando
    ) {
        return atual.copy()
                .setComandoCompletarAposta(
                        comando
                )
                .setMostrarBotaoLimparAposta(true);
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
                    && !event.isFragmentCartela()
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
