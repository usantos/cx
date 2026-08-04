package br.gov.caixa.loterias.apostas.viewModel.reducer;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.effect.AnalyticsEffect;
import br.gov.caixa.loterias.apostas.event.SimulaUiEvent;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.state.SimulaUiState;

public class QuantidadeNumerosReducer {

    private final FluxoApostaReducer fluxoApostaReducer;

    public QuantidadeNumerosReducer(
            FluxoApostaReducer fluxoApostaReducer
    ) {
        this.fluxoApostaReducer = fluxoApostaReducer;
    }

    public SimulaUiState inicializar(
            SimulaUiState atual,
            SimulaUiEvent.InicializarQuantidadeNumeros event
    ) {

        if (atual == null || event == null) {
            return atual;
        }

        int qtdSelecionada =
                event.getQtdDezenasSelecionadaInicial();

        String textoBotao =
                montarTextoQuantidadeNumeros(
                        qtdSelecionada
                );

        return atual.toBuilder()
                .setLabelsQuantidadeNumeros(
                        event.getLabelsQuantidadeNumeros()
                )
                .setQtdPrognosticosQuantidadeNumeros(
                        event.getQtdPrognosticosQuantidadeNumeros()
                )
                .setQtdDezenasPossiveisSelecionado(
                        qtdSelecionada
                )
                .setTextoBotaoPrognosticosSelecionado(
                        textoBotao
                )
                .setPosicaoQuantidadeNumerosSelecionada(0)
                .setExibirDialogQuantidadeNumeros(false)
                .setExibirAvisoQuantidadeNumerosMaxima(false)
                .setAtualizarValorApostaPorQuantidadeNumeros(false)
                .setResetarTrevosPorQuantidadeNumeros(false)
                .build();
    }

    public SimulaUiState clicar(
            SimulaUiState atual
    ) {

        if (atual == null) {
            return null;
        }

        return atual.toBuilder()
                .setExibirDialogQuantidadeNumeros(true)
                .setExibirAvisoQuantidadeNumerosMaxima(false)
                .setAtualizarValorApostaPorQuantidadeNumeros(false)
                .setResetarTrevosPorQuantidadeNumeros(false)
                .build();
    }

    public SimulaUiState selecionar(
            SimulaUiState atual,
            SimulaUiEvent.SelecionarQuantidadeNumeros event,
            ModalidadeEnum tipoJogoAtual
    ) {

        if (event == null) {
            return atual;
        }

        return aplicarQuantidadeNumerosSelecionada(
                atual,
                event.getPosition(),
                false,
                tipoJogoAtual
        );
    }

    public SimulaUiState confirmar(
            SimulaUiState atual,
            SimulaUiEvent.ConfirmarQuantidadeNumeros event,
            ModalidadeEnum tipoJogoAtual
    ) {

        if (event == null) {
            return atual;
        }

        return aplicarQuantidadeNumerosSelecionada(
                atual,
                event.getPosition(),
                true,
                tipoJogoAtual
        );
    }

    private SimulaUiState aplicarQuantidadeNumerosSelecionada(
            SimulaUiState atual,
            int position,
            boolean confirmar,
            ModalidadeEnum tipoJogoAtual
    ) {

        if (atual == null) {
            return null;
        }

        List<Integer> prognosticos =
                atual.getQtdPrognosticosQuantidadeNumeros();

        if (prognosticos == null || prognosticos.isEmpty()) {
            return atual;
        }

        int posicaoValida =
                position;

        if (posicaoValida < 0
                || posicaoValida >= prognosticos.size()) {

            posicaoValida =
                    atual.getPosicaoQuantidadeNumerosSelecionada();
        }

        if (posicaoValida < 0
                || posicaoValida >= prognosticos.size()) {
            return atual;
        }

        Integer qtdPrognosticos =
                prognosticos.get(posicaoValida);

        if (qtdPrognosticos == null) {
            return atual;
        }
        int quantidadeSelecionadaAtual =
                obterQuantidadeSelecionadaAtual(
                        atual,
                        tipoJogoAtual
                );

        boolean deveLimparSelecao =
                confirmar
                        && quantidadeSelecionadaAtual > qtdPrognosticos;

        String textoBotao =
                montarTextoQuantidadeNumeros(
                        qtdPrognosticos
                );

        boolean deveExibirAviso =
                posicaoValida >= 1
                        && !atual.isAvisouQuantidadeNumerosMaxima();

        SimulaUiState.Builder builder =
                atual.toBuilder()
                        .setPosicaoQuantidadeNumerosSelecionada(
                                posicaoValida
                        )
                        .setQuantidadeNumerosSurpresinha(
                                qtdPrognosticos
                        )
                        .setQtdDezenasPossiveisSelecionado(
                                qtdPrognosticos
                        )
                        .setTextoBotaoPrognosticosSelecionado(
                                textoBotao
                        )
                        .setExibirAvisoQuantidadeNumerosMaxima(
                                deveExibirAviso
                        )
                        .setAvisouQuantidadeNumerosMaxima(
                                atual.isAvisouQuantidadeNumerosMaxima()
                                        || deveExibirAviso
                        );

        if (fluxoApostaReducer != null) {
            builder =
                    fluxoApostaReducer
                            .resetarEtapaComplementarSeNecessario(
                                    builder,
                                    atual,
                                    tipoJogoAtual,
                                    qtdPrognosticos
                            );
        }

        if (confirmar) {
            if (deveLimparSelecao) {
                builder
                        .setDezenasSelecionadas(
                                new ArrayList<Integer>()
                        )
                        .setLimparNumerosCartela(true)
                        .setTextoSelecionados(
                                "Selecione os numeros:"
                        )
                        .setBotaoAdicionarHabilitado(false)
                        .setBotaoNextStepHabilitado(false)
                        .setBotaoLimparHabilitado(false)
                        .setMostrarBotaoLimparAposta(false)
                        .setMostrarSalvarAposta(false)
                        .setApostaFavoritada(false)
                        .setPossuiAlteracoesPendentes(false);

                if (tipoJogoAtual == ModalidadeEnum.SUPER_7) {
                    builder
                            .setQtdTotalSelecionadosSuperSete(0)
                            .setLimparSuperSete(true);
                }

                if (tipoJogoAtual == ModalidadeEnum.MAIS_MILIONARIA) {
                    builder
                            .setTrevosSelecionados(
                                    new ArrayList<Integer>()
                            )
                            .setQuantidadeTrevosSelecionada(0)
                            .setValorTrevosSelecionado(null)
                            .setValoresTrevos(
                                    new ArrayList<>()
                            )
                            .setLabelsQuantidadeTrevos(
                                    new ArrayList<String>()
                            )
                            .setPosicaoTrevosSelecionada(0)
                            .setTextoBotaoQuantidadeTrevos(
                                    "2 trevos"
                            )
                            .setResetarTrevosPorQuantidadeNumeros(true);
                }
            }

            builder
                    .setExibirDialogQuantidadeNumeros(false)
                    .setComandoAnalytics(
                            new AnalyticsEffect
                                    .QuantidadeNumerosConfirmada(
                                    tipoJogoAtual,
                                    String.valueOf(
                                            qtdPrognosticos
                                    )
                            )
                    )
                    .setAtualizarValorApostaPorQuantidadeNumeros(true);

            if (tipoJogoAtual == ModalidadeEnum.MAIS_MILIONARIA
                    && !deveLimparSelecao) {
                builder.setResetarTrevosPorQuantidadeNumeros(true);
            }
        }

        return builder.build();
    }
    private int obterQuantidadeSelecionadaAtual(
            SimulaUiState atual,
            ModalidadeEnum tipoJogoAtual
    ) {
        if (atual == null) {
            return 0;
        }

        if (tipoJogoAtual == ModalidadeEnum.SUPER_7) {
            return atual.getQtdTotalSelecionadosSuperSete();
        }

        if (atual.getDezenasSelecionadas() == null) {
            return 0;
        }

        return atual.getDezenasSelecionadas().size();
    }
    public SimulaUiState consumirDialog(
            SimulaUiState atual
    ) {

        if (atual == null) {
            return null;
        }

        return atual.toBuilder()
                .setExibirDialogQuantidadeNumeros(false)
                .build();
    }

    public SimulaUiState consumirAviso(
            SimulaUiState atual
    ) {

        if (atual == null) {
            return null;
        }

        return atual.toBuilder()
                .setExibirAvisoQuantidadeNumerosMaxima(false)
                .build();
    }

    public SimulaUiState valorApostaAtualizado(
            SimulaUiState atual
    ) {

        if (atual == null) {
            return null;
        }

        return atual.toBuilder()
                .setAtualizarValorApostaPorQuantidadeNumeros(false)
                .setResetarTrevosPorQuantidadeNumeros(false)
                .build();
    }

    public String montarTextoQuantidadeNumeros(
            int qtdPrognosticos
    ) {

        return qtdPrognosticos + " numeros";
    }
}