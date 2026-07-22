package br.gov.caixa.loterias.apostas.viewModel.reducer;

import br.gov.caixa.loterias.apostas.effect.FavoritarApostaEffect;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaFavoritaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DTOEnumInteger;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroJogoDTO;
import br.gov.caixa.loterias.apostas.state.SimulaUiState;
import br.gov.caixa.loterias.apostas.utils.Constantes;

public class FavoritarApostaReducer {

    public SimulaUiState abrirDialog(
            SimulaUiState atual
    ) {

        if (atual == null) {
            return null;
        }

        return atual.toBuilder()
                .setExibirDialogFavoritarAposta(true)
                .build();
    }

    public SimulaUiState consumirDialog(
            SimulaUiState atual
    ) {

        if (atual == null) {
            return null;
        }

        return atual.toBuilder()
                .setExibirDialogFavoritarAposta(false)
                .build();
    }

    public SimulaUiState publicarComando(
            SimulaUiState atual,
            FavoritarApostaEffect comando
    ) {

        if (atual == null) {
            return null;
        }

        return atual.toBuilder()
                .setComandoFavoritarAposta(comando)
                .build();
    }

    public SimulaUiState limparComando(
            SimulaUiState atual
    ) {

        if (atual == null) {
            return null;
        }

        return atual.toBuilder()
                .setComandoFavoritarAposta(null)
                .build();
    }

    public SimulaUiState sucessoFavoritar(
            SimulaUiState atual
    ) {

        if (atual == null) {
            return null;
        }

        return atual.toBuilder()
                .setApostaFavoritada(true)
                .setMostrarSalvarAposta(true)
                .build();
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