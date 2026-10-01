package br.gov.caixa.loterias.apostas.view.features.volantenovo.presentation;

import br.gov.caixa.loterias.apostas.effect.FavoritarApostaEffect;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaFavoritaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DTOEnumInteger;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroJogoDTO;
import br.gov.caixa.loterias.apostas.utils.Constantes;

public class FavoritarApostaReducer {

    public SimulaUiState abrirDialog(
            SimulaUiState atual
    ) {

        if (atual == null) {
            return null;
        }

        return atual.copy()
                .setExibirDialogFavoritarAposta(true);
    }

    public SimulaUiState consumirDialog(
            SimulaUiState atual
    ) {

        if (atual == null) {
            return null;
        }

        return atual.copy()
                .setExibirDialogFavoritarAposta(false);
    }

    public SimulaUiState publicarComando(
            SimulaUiState atual,
            FavoritarApostaEffect comando
    ) {

        if (atual == null) {
            return null;
        }

        return atual.copy()
                .setComandoFavoritarAposta(comando);
    }

    public SimulaUiState limparComando(
            SimulaUiState atual
    ) {

        if (atual == null) {
            return null;
        }

        return atual.copy()
                .setComandoFavoritarAposta(null);
    }

    public SimulaUiState sucessoFavoritar(
            SimulaUiState atual
    ) {

        if (atual == null) {
            return null;
        }

        return atual.copy()
                .setApostaFavoritada(true)
                .setMostrarSalvarAposta(true)
                .setPossuiAlteracoesPendentes(true);
    }

    public ApostaFavoritaDTO prepararApostaFavorita(
            ApostaFavoritaDTO apostaFavorita,
            ParametroJogoDTO parametroJogoAtual,
            String nomeAposta,
            Object numerosSelecionados
    ) {

        if (apostaFavorita == null) {
            return null;
        }

        if (parametroJogoAtual != null
                && parametroJogoAtual.getConcurso() != null
                && parametroJogoAtual
                .getConcurso()
                .getModalidadeDetalhada() != null) {

            DTOEnumInteger modalidade =
                    new DTOEnumInteger();

            modalidade.setDescricao(
                    parametroJogoAtual
                            .getConcurso()
                            .getModalidadeDetalhada()
                            .getDescricao()
            );

            modalidade.setValor(
                    parametroJogoAtual
                            .getConcurso()
                            .getModalidadeDetalhada()
                            .getValor()
            );

            apostaFavorita.setModalidade(
                    modalidade
            );
        }

        if (nomeAposta != null) {

            String nome =
                    nomeAposta.trim();

            if (nome.length() > 25) {

                apostaFavorita.setNome(
                        nome.substring(
                                0,
                                25
                        )
                );

            } else {

                apostaFavorita.setNome(
                        nome
                );
            }

        } else {

            apostaFavorita.setNome("");
        }

        apostaFavorita.setNumerosSelecionados(
                numerosSelecionados
        );

        apostaFavorita.setId(
                Constantes.ZERO_LONG
        );

        return apostaFavorita;
    }
}
