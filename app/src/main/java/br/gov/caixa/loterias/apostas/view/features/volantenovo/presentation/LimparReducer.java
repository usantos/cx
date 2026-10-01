package br.gov.caixa.loterias.apostas.view.features.volantenovo.presentation;

import java.math.BigDecimal;
import java.util.ArrayList;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.utils.EtapaAposta;

public class LimparReducer {

    public SimulaUiState limparAposta(
            SimulaUiState state,
            ModalidadeEnum tipoJogo,
            int qtdNumerosAtual,
            BigDecimal valorApostaAtual
    ) {

        if (state == null) {
            return null;
        }

        BigDecimal valorSeguro =
                valorApostaAtual != null
                        ? valorApostaAtual
                        : BigDecimal.ZERO;

        SimulaUiState builder =
                state.copy()
                        .setMostrarSalvarAposta(false)
                        .setMostrarBotaoLimparAposta(false)
                        .setTextoSelecionados("Selecione os números:")
                        .setBotaoCompletarHabilitado(true)
                        .setBotaoAdicionarHabilitado(false)
                        .setBotaoLimparHabilitado(false)
                        .setApostaFavoritada(false)
                        .setOpcaoOutrosNumerosSelecionada(false)
                        .setBotaoNextStepHabilitado(false)
                        .setExibirOpcaoOutrosNumeros(false)
                        .setPossuiAlteracoesPendentes(false)
                        .setValorAposta(valorSeguro)
                        .setValorGrande(isValorGrande(valorSeguro))
                        .setLimparOpcaoOutrosNumeros(true)
                        .setBotaoCompletarVisivel(true);

        if (tipoJogo == ModalidadeEnum.LOTECA) {

            limparLoteca(
                    builder,
                    state.getPalpitesLoteca()
            );

        } else if (tipoJogo == ModalidadeEnum.LOTOGOL) {

            limparLotogol(builder);

        } else if (tipoJogo == ModalidadeEnum.TIMEMANIA
                && state.getEtapaAposta() == EtapaAposta.TIME_CORACAO) {

            limparTimeCoracao(builder);

        } else if (tipoJogo == ModalidadeEnum.DIA_DE_SORTE
                && state.getEtapaAposta() == EtapaAposta.MES_SORTE) {

            limparMesDaSorte(builder);

        } else if (tipoJogo == ModalidadeEnum.MAIS_MILIONARIA
                && state.getEtapaAposta() == EtapaAposta.TREVOS) {

            limparTrevos(builder);

        } else if (tipoJogo == ModalidadeEnum.SUPER_7) {

            limparSuperSete(
                    builder,
                    qtdNumerosAtual
            );

        } else {

            limparNumerosCartela(builder);
        }

        return builder;
    }
    public SimulaUiState limparFlagsDeRenderizacao(
            SimulaUiState state
    ) {

        if (state == null) {
            return null;
        }

        return state.copy()
                .setLimparNumerosCartela(false)
                .setLimparTimeCoracao(false)
                .setLimparSuperSete(false)
                .setLimparLoteca(false)
                .setLimparLotogol(false)
                .setPossuiAlteracoesPendentes(false)
                .setLimparOpcaoOutrosNumeros(false);
    }

    private void limparLoteca(
            SimulaUiState builder,
            int palpitesMaximos
    ) {

        builder
                .setLimparLoteca(true)
                .setJogosLoteca(0)
                .setSimplesLoteca(0)
                .setDuplasLoteca(0)
                .setTriplasLoteca(0)
                .setExibirInfoEtapa(true)
                .setTextoInfoEtapa(
                        "Selecionados: 0/"
                                + Math.max(palpitesMaximos, 0)
                )
                .setMostrarBotaoLimparAposta(false)
                .setBotaoCompletarVisivel(false);
    }

    private void limparLotogol(
            SimulaUiState builder
    ) {

        builder
                .setLimparLotogol(true)
                .setBotaoCompletarVisivel(false);
    }

    private void limparTimeCoracao(
            SimulaUiState builder
    ) {

        builder
                .setEquipeSelecionada(null)
                .setLimparTimeCoracao(true)
                .setBotaoAdicionarHabilitado(false)
                .setBotaoCompletarHabilitado(true)
                .setMostrarBotaoLimparAposta(false)
                .setMostrarSalvarAposta(false)
                .setEtapaAposta(EtapaAposta.TIME_CORACAO);
    }

    private void limparMesDaSorte(
            SimulaUiState builder
    ) {

        builder
                .setLimparOpcaoOutrosNumeros(true)
                .setBotaoAdicionarHabilitado(false)
                .setBotaoCompletarHabilitado(true)
                .setMostrarBotaoLimparAposta(false)
                .setMostrarSalvarAposta(false)
                .setEtapaAposta(EtapaAposta.MES_SORTE);
    }

    private void limparTrevos(
            SimulaUiState builder
    ) {

        builder
                .setLimparOpcaoOutrosNumeros(true)
                .setTrevosSelecionados(new ArrayList<>())
                .setQuantidadeTrevosSelecionada(0)
                .setBotaoAdicionarHabilitado(false)
                .setBotaoCompletarHabilitado(true)
                .setMostrarBotaoLimparAposta(false)
                .setMostrarSalvarAposta(false)
                .setEtapaAposta(EtapaAposta.TREVOS);
    }

    private void limparSuperSete(
            SimulaUiState builder,
            int qtdNumerosAtual
    ) {

        builder
                .setLimparSuperSete(true)
                .setQtdTotalSelecionadosSuperSete(0)
                .setQtdDezenasPossiveisSelecionado(qtdNumerosAtual)
                .setMostrarBotaoLimparAposta(false)
                .setMostrarSalvarAposta(false);
    }

    private void limparNumerosCartela(
            SimulaUiState builder
    ) {

        builder
                .setDezenasSelecionadas(new ArrayList<>())
                .setLimparNumerosCartela(true)
                .setMostrarBotaoLimparAposta(false)
                .setMostrarSalvarAposta(false);
    }

    private boolean isValorGrande(
            BigDecimal valor
    ) {

        return valor != null
                && valor.doubleValue() > 999.99;
    }
}
