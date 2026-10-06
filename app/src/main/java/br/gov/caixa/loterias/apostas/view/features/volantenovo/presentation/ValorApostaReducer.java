package br.gov.caixa.loterias.apostas.view.features.volantenovo.presentation;
import java.math.BigDecimal;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroJogoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroValorApostaDTO;
import br.gov.caixa.loterias.apostas.utils.EtapaAposta;

public class ValorApostaReducer {

    public SimulaUiState recalcular(
            SimulaUiState state,
            ParametroJogoDTO parametroJogo,
            ModalidadeEnum modalidade
    ) {
        if (state == null) {
            return null;
        }

        BigDecimal valor;

        if (state.isSurpresinhaHabilitada()) {
            valor = calcularValorSurpresinha(
                    state,
                    parametroJogo,
                    modalidade
            );
        } else {
            valor = calcularValorApostaManual(
                    state,
                    parametroJogo,
                    modalidade
            );
        }

        return state.copy()
                .setValorAposta(valor)
                .setValorGrande(isValorGrande(valor))
                .setAtualizarValorApostaPorQuantidadeNumeros(false)
                .setAtualizarValorApostaPorTeimosinha(false);
    }

    private BigDecimal calcularValorApostaManual(
            SimulaUiState state,
            ParametroJogoDTO parametroJogo,
            ModalidadeEnum modalidade
    ) {
        if (parametroJogo == null) {
            return BigDecimal.ZERO;
        }

        int qtdNumeros = normalizarQuantidadeNumeros(
                state.getQtdDezenasPossiveisSelecionado(),
                parametroJogo
        );

        int qtdConcursos = state.getQtdConcursoSelecionado();

        BigDecimal valorBase;

        if (modalidade == ModalidadeEnum.MAIS_MILIONARIA) {
            valorBase = calcularValorMaisMilionariaManual(
                    state,
                    parametroJogo,
                    qtdNumeros
            );
        } else {
            valorBase = obterValorBasePorQuantidadeNumeros(
                    parametroJogo,
                    qtdNumeros
            );
        }

        return aplicarConcursos(
                valorBase,
                qtdConcursos
        );
    }

    private BigDecimal calcularValorMaisMilionariaManual(
            SimulaUiState state,
            ParametroJogoDTO parametroJogo,
            int qtdNumeros
    ) {
        if (state.getEtapaAposta() == EtapaAposta.TREVOS
                && state.getValorTrevosSelecionado() != null
                && state.getValorTrevosSelecionado().getValor() != null) {
            return state.getValorTrevosSelecionado().getValor();
        }

        int qtdTrevos = 0;

        if (parametroJogo.getTrevos() != null
                && parametroJogo.getTrevos().getQtdMinima() != null) {
            qtdTrevos = parametroJogo.getTrevos().getQtdMinima();
        }

        ParametroValorApostaDTO valorAposta =
                parametroJogo.getValorApostaBy(
                        qtdNumeros,
                        qtdTrevos
                );

        if (valorAposta != null
                && valorAposta.getValor() != null) {
            return valorAposta.getValor();
        }

        return obterValorMinimoSeguro(parametroJogo);
    }

    private BigDecimal calcularValorSurpresinha(
            SimulaUiState state,
            ParametroJogoDTO parametroJogo,
            ModalidadeEnum modalidade
    ) {
        if (parametroJogo == null) {
            return BigDecimal.ZERO;
        }

        int qtdNumeros = state.getQuantidadeNumerosSurpresinha();

        if (qtdNumeros <= 0) {
            qtdNumeros = normalizarQuantidadeNumeros(
                    state.getQtdDezenasPossiveisSelecionado(),
                    parametroJogo
            );
        }

        int qtdConcursos = state.getQtdConcursoSelecionado();
        int qtdSurpresinhas = state.getQuantidadeSurpresinhas();

        BigDecimal valorBase;

        if (modalidade == ModalidadeEnum.MAIS_MILIONARIA) {
            ParametroValorApostaDTO valorTrevos =
                    parametroJogo.getValorApostaBy(
                            qtdNumeros,
                            state.getQuantidadeTrevosSurpresinha()
                    );

            valorBase =
                    valorTrevos != null && valorTrevos.getValor() != null
                            ? valorTrevos.getValor()
                            : BigDecimal.ZERO;
        } else {
            valorBase = obterValorBasePorQuantidadeNumeros(
                    parametroJogo,
                    qtdNumeros
            );
        }

        BigDecimal valor = aplicarConcursos(
                valorBase,
                qtdConcursos
        );

        if (qtdSurpresinhas > 0) {
            valor = valor.multiply(
                    BigDecimal.valueOf(qtdSurpresinhas)
            );
        }

        return valor;
    }

    private BigDecimal obterValorBasePorQuantidadeNumeros(
            ParametroJogoDTO parametroJogo,
            int qtdNumeros
    ) {
        if (parametroJogo == null
                || parametroJogo.getValoresAposta() == null) {
            return BigDecimal.ZERO;
        }

        for (ParametroValorApostaDTO valorAposta
                : parametroJogo.getValoresAposta()) {
            if (valorAposta == null
                    || valorAposta.getNumeroPrognosticos() == null
                    || valorAposta.getValor() == null) {
                continue;
            }

            if (valorAposta.getNumeroPrognosticos().equals(qtdNumeros)) {
                return valorAposta.getValor();
            }
        }

        return obterValorMinimoSeguro(parametroJogo);
    }

    private BigDecimal obterValorMinimoSeguro(
            ParametroJogoDTO parametroJogo
    ) {
        if (parametroJogo != null
                && parametroJogo.getValorApostaMinima() != null) {
            return parametroJogo.getValorApostaMinima();
        }

        return BigDecimal.ZERO;
    }

    private int normalizarQuantidadeNumeros(
            int qtdNumeros,
            ParametroJogoDTO parametroJogo
    ) {
        if (qtdNumeros > 0) {
            return qtdNumeros;
        }

        if (parametroJogo != null
                && parametroJogo.getQuantidadeMinima() != null) {
            return parametroJogo.getQuantidadeMinima();
        }

        return 0;
    }

    private BigDecimal aplicarConcursos(
            BigDecimal valorBase,
            int qtdConcursos
    ) {
        BigDecimal valorSeguro =
                valorBase != null
                        ? valorBase
                        : BigDecimal.ZERO;

        if (qtdConcursos > 0) {
            return valorSeguro.multiply(
                    BigDecimal.valueOf(qtdConcursos)
            );
        }

        return valorSeguro;
    }

    private boolean isValorGrande(
            BigDecimal valor
    ) {
        return valor != null
                && valor.doubleValue() > 999.99;
    }
    private BigDecimal calcularValorLoteca(
            ParametroJogoDTO parametroJogo,
            int duplos,
            int triplos
    ) {
        if (parametroJogo == null) {
            return BigDecimal.ZERO;
        }

        if (duplos > 0 || triplos > 0) {

            ParametroValorApostaDTO valorAposta =
                    parametroJogo.getValorApostaLotecaBy(duplos, triplos);

            return valorAposta != null && valorAposta.getValor() != null
                    ? valorAposta.getValor()
                    : BigDecimal.ZERO;
        }

        return parametroJogo.getValorApostaMinima() != null
                ? parametroJogo.getValorApostaMinima()
                : BigDecimal.ZERO;
    }

    public SimulaUiState atualizarLoteca(
            SimulaUiState state,
            SimulaUiEvent.LotecaAtualizada event,
            ParametroJogoDTO parametro
    ){
        BigDecimal valor = calcularValorLoteca(
                parametro,
                event.getDuplas(),
                event.getTriplas()
        );

        return state.copy()
                .setJogosLoteca(event.getJogos())
                .setSimplesLoteca(event.getSimples())
                .setDuplasLoteca(event.getDuplas())
                .setTriplasLoteca(event.getTriplas())
                .setValorAposta(valor);
    }
}
