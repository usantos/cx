package br.gov.caixa.loterias.apostas.view.features.volantenovo.presentation;

import java.math.BigDecimal;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IncluirSurpresinhaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroJogoDTO;
import br.gov.caixa.loterias.apostas.utils.ApostaUtils;

public class SurpresinhaCarrinhoReducer {

    public IncluirSurpresinhaDTO criar(
            SimulaUiState state,
            ParametroJogoDTO parametroJogo,
            ModalidadeEnum modalidade,
            boolean espelhoLotomania
    ) {
        if (state == null
                || parametroJogo == null
                || modalidade == null) {
            return null;
        }

        int quantidadeNumeros =
                state.getQuantidadeNumerosSurpresinha();

        int quantidadeSurpresinhas =
                state.getQuantidadeSurpresinhas();

        int quantidadeConcursos =
                state.getQtdConcursoSelecionado();

        BigDecimal valorAposta =
                state.getValorAposta() != null
                        ? state.getValorAposta()
                        : BigDecimal.ZERO;

        if (modalidade == ModalidadeEnum.LOTOMANIA
                && espelhoLotomania) {
            valorAposta =
                    valorAposta.multiply(
                            BigDecimal.valueOf(2)
                    );
        }

        switch (modalidade) {
            case TIMEMANIA:
                return ApostaUtils.getSurpresinhaTimemania(
                        parametroJogo,
                        quantidadeNumeros,
                        quantidadeSurpresinhas,
                        quantidadeConcursos,
                        valorAposta,
                        state.getEquipeSelecionada()
                );

            case LOTOMANIA:
                return ApostaUtils.getSurpresinhaLotomania(
                        parametroJogo,
                        quantidadeNumeros,
                        quantidadeSurpresinhas,
                        quantidadeConcursos,
                        valorAposta,
                        espelhoLotomania
                );

            case MAIS_MILIONARIA:
                return ApostaUtils.getSurpresinhaMaisMilionaria(
                        parametroJogo,
                        quantidadeNumeros,
                        quantidadeSurpresinhas,
                        quantidadeConcursos,
                        valorAposta,
                        state.getQuantidadeTrevosSurpresinha()
                );

            default:
                return ApostaUtils.getSurpresinha(
                        parametroJogo,
                        quantidadeNumeros,
                        quantidadeSurpresinhas,
                        quantidadeConcursos,
                        valorAposta
                );
        }
    }
}
