package br.gov.caixa.loterias.apostas.viewModel.reducer;

import java.util.List;

import br.gov.caixa.loterias.apostas.effect.AnalyticsEffect;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.state.SimulaUiState;

public class TeimosinhaReducer {

    public SimulaUiState inicializar(
            SimulaUiState atual,
            List<String> labels,
            List<Integer> concursos,
            int concursoSelecionado,
            boolean especial,
            String textoInicial
    ) {

        String textoBotao = textoInicial;

        if (concursoSelecionado != 0
                && labels != null
                && concursos != null) {

            for (int i = 0; i < concursos.size(); i++) {

                if (concursos.get(i)
                        == concursoSelecionado
                        && i < labels.size()) {

                    textoBotao = labels.get(i);
                    break;
                }
            }
        }

        return atual.toBuilder()
                .setLabelsTeimosinhas(labels)
                .setConcursosTeimosinhas(concursos)
                .setQtdConcursoSelecionado(
                        concursoSelecionado
                )
                .setEspecial(especial)
                .setTextoBotaoTeimosinhas(
                        textoBotao
                )
                .setExibirDialogTeimosinha(false)
                .setExibirDialogSemTeimosinha(false)
                .setAtualizarValorApostaPorTeimosinha(false)
                .build();
    }

    public SimulaUiState clicar(
            SimulaUiState atual
    ) {

        if (atual.isEspecial()) {

            return atual.toBuilder()
                    .setExibirDialogTeimosinha(false)
                    .setExibirDialogSemTeimosinha(true)
                    .setAtualizarValorApostaPorTeimosinha(false)
                    .build();
        }

        return atual.toBuilder()
                .setExibirDialogTeimosinha(true)
                .setExibirDialogSemTeimosinha(false)
                .setAtualizarValorApostaPorTeimosinha(false)
                .build();
    }

    public SimulaUiState confirmar(
            SimulaUiState atual,
            int position,
            ModalidadeEnum modalidade
    ) {

        if (atual.getConcursosTeimosinhas() == null
                || position < 0
                || position >= atual
                .getConcursosTeimosinhas()
                .size()) {

            return atual;
        }

        int qtdConcursoSelecionado =
                atual.getConcursosTeimosinhas()
                        .get(position);

        String textoBotao =
                atual.getTextoBotaoTeimosinhas();

        if (atual.getLabelsTeimosinhas() != null
                && position <
                atual.getLabelsTeimosinhas()
                        .size()) {

            textoBotao =
                    atual.getLabelsTeimosinhas()
                            .get(position);
        }

        return atual.toBuilder()
                .setQtdConcursoSelecionado(
                        qtdConcursoSelecionado
                )
                .setTextoBotaoTeimosinhas(
                        textoBotao
                )
                .setExibirDialogTeimosinha(false)
                .setExibirDialogSemTeimosinha(false)
                .setComandoAnalytics(
                        new AnalyticsEffect
                                .TeimosinhaConfirmada(
                                modalidade,
                                textoBotao
                        )
                )
                .setAtualizarValorApostaPorTeimosinha(
                        true
                )
                .build();
    }

    public SimulaUiState consumirEvento(
            SimulaUiState atual
    ) {

        return atual.toBuilder()
                .setExibirDialogTeimosinha(false)
                .setExibirDialogSemTeimosinha(false)
                .build();
    }

    public SimulaUiState valorAtualizado(
            SimulaUiState atual
    ) {

        return atual.toBuilder()
                .setAtualizarValorApostaPorTeimosinha(
                        false
                )
                .build();
    }
}