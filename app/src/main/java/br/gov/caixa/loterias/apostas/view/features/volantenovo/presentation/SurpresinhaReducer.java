package br.gov.caixa.loterias.apostas.view.features.volantenovo.presentation;

import java.math.BigDecimal;
import java.util.ArrayList;

import br.gov.caixa.loterias.apostas.effect.AnalyticsEffect;
import br.gov.caixa.loterias.apostas.effect.ScreenSimulaEffect;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroJogoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroValorApostaDTO;
import br.gov.caixa.loterias.apostas.utils.EtapaAposta;

public class SurpresinhaReducer {

    public SimulaUiState alterarSurpresinha(
            SimulaUiState atual,
            boolean habilitada,
            ModalidadeEnum tipoJogoAtual,
            ParametroJogoDTO parametroJogoAtual
    ) {

        if (atual == null) {
            return null;
        }

        int qtdTrevosInicial =
                obterQuantidadeTrevosInicial(
                        parametroJogoAtual
                );



        if (habilitada) {

            ScreenSimulaEffect comando =
                    new ScreenSimulaEffect.AbrirSurpresinha(
                            tipoJogoAtual == ModalidadeEnum.MAIS_MILIONARIA,
                            true
                    );

            SimulaUiState state =  atual.copy()
                    .setSurpresinhaHabilitada(true)
                    .setComandoTelaSimula(comando)
                    // Ao ligar surpresinha, nao pode manter estado manual
                    .setExibirOpcaoOutrosNumeros(false)
                    .setOpcaoOutrosNumerosSelecionada(false)
                    .setExibirInfoEtapa(false)
                    .setTextoInfoEtapa("")
                    .setComandoAnalytics(
                            new AnalyticsEffect.QtdSurpresinhas(
                                    tipoJogoAtual,
                                    1
                            )
                    )
                    .setLimparOpcaoOutrosNumeros(true)
                    // Reseta campos da surpresinha para o estado inicial
                    .setQuantidadeSurpresinhas(1)
                    .setQuantidadeNumerosSurpresinha(atual.getQtdDezenasPossiveisSelecionado())
                    .setQuantidadeTrevosSurpresinha(qtdTrevosInicial)
                    // Limpa selecoes manuais que estavam na cartela
                    .setDezenasSelecionadas(new ArrayList<>())
                    .setTrevosSelecionados(new ArrayList<>())
                    .setQuantidadeTrevosSelecionada(0)
                    .setValorTrevosSelecionado(null)
                    .setValoresTrevos(new ArrayList<>())
                    .setLabelsQuantidadeTrevos(new ArrayList<>())
                    .setPosicaoTrevosSelecionada(0)
                    .setTextoBotaoQuantidadeTrevos("2 trevos")
                    .setEquipeSelecionada(null)
                    .setMesSelecionado(null)
                    .setMostrarSalvarAposta(false)
                    .setMostrarBotaoLimparAposta(false)
                    .setBotaoLimparHabilitado(false)
                    .setBotaoCompletarVisivel(false)
                    .setBotaoCompletarHabilitado(false)
                    .setBotaoNextStepHabilitado(false)
                    .setBotaoAdicionarHabilitado(true)
                    .setApostaFavoritada(false)
                    .setPossuiAlteracoesPendentes(false);
            return recalcularValorSurpresinha(
                    state,
                    parametroJogoAtual,
                    tipoJogoAtual
            );
        }


        return atual.copy()
                .setSurpresinhaHabilitada(false)
                .setBotaoCompletarVisivel(true)
                .setBotaoCompletarHabilitado(true)
                .setBotaoAdicionarHabilitado(false)
                // voltar para a etapa inicial
                .setEtapaAposta(EtapaAposta.NUMEROS)
                .setTextoInfoEtapa("1/2")
                // limpar seleções
                .setDezenasSelecionadas(new ArrayList<>())
                .setTrevosSelecionados(new ArrayList<>())
                .setQuantidadeTrevosSelecionada(0)
                .setTelaSelecaoTimeAtivado(false)
                .setEscolhaTimeCoracaoSurpresinha(false)
                .setEquipeSelecionada(null)
                .setMesSelecionado(null)
                .setExibirOpcaoOutrosNumeros(false)
                .setOpcaoOutrosNumerosSelecionada(false)
                .setLimparOpcaoOutrosNumeros(true)
                // limpar estado visual
                .setBotaoNextStepHabilitado(false)
                .setMostrarBotaoLimparAposta(true)
                .setComandoTelaSimula(
                        new ScreenSimulaEffect.VoltarEtapa(
                                EtapaAposta.NUMEROS
                        )
                );
    }
    public SimulaUiState voltarParaSurpresinha(
            SimulaUiState atual
    ) {
        if (atual == null) {
            return null;
        }

        return atual.copy()
                .setTelaSelecaoTimeAtivado(false)
                .setEscolhaTimeCoracaoSurpresinha(false)
                .setEtapaAposta(EtapaAposta.NUMEROS)
                .setExibirInfoEtapa(false)
                .setTextoInfoEtapa("")
                .setComandoTelaSimula(
                        new ScreenSimulaEffect.AbrirSurpresinha(
                                false,
                                true
                        )
                );
    }

    public SimulaUiState atualizarQuantidadeSurpresinhas(
            SimulaUiState atual,
            int quantidade,
            ParametroJogoDTO parametroJogoAtual,
            ModalidadeEnum tipoJogoAtual
    ) {

        if (atual == null) {
            return null;
        }

        SimulaUiState novoEstado =
                atual.copy()
                        .setQuantidadeSurpresinhas(
                                quantidade
                        )
                        .setComandoAnalytics(
                                new AnalyticsEffect.QtdSurpresinhas(
                                        tipoJogoAtual,
                                        quantidade
                                )
                        )
                        .setPossuiAlteracoesPendentes(true);

        return recalcularValorSurpresinha(
                novoEstado,
                parametroJogoAtual,
                tipoJogoAtual
        );
    }

    public SimulaUiState atualizarQuantidadeNumerosSurpresinha(
            SimulaUiState atual,
            int quantidade,
            ParametroJogoDTO parametroJogoAtual,
            ModalidadeEnum tipoJogoAtual
    ) {

        if (atual == null) {
            return null;
        }

        SimulaUiState novoEstado =
                atual.copy()
                        .setQuantidadeNumerosSurpresinha(
                                quantidade
                        )
                        .setQtdDezenasPossiveisSelecionado(
                                quantidade
                        )
                        .setTextoBotaoPrognosticosSelecionado(
                                quantidade + " números"
                        )
                        .setPossuiAlteracoesPendentes(true);

        return recalcularValorSurpresinha(
                novoEstado,
                parametroJogoAtual,
                tipoJogoAtual
        );
    }

    public SimulaUiState atualizarQuantidadeTrevosSurpresinha(
            SimulaUiState atual,
            int quantidade,
            ParametroJogoDTO parametroJogoAtual,
            ModalidadeEnum tipoJogoAtual
    ) {

        if (atual == null) {
            return null;
        }

        SimulaUiState novoEstado =
                atual.copy()
                        .setQuantidadeTrevosSurpresinha(
                                quantidade
                        )
                        .setPossuiAlteracoesPendentes(true);

        return recalcularValorSurpresinha(
                novoEstado,
                parametroJogoAtual,
                tipoJogoAtual
        );
    }

    public SimulaUiState recalcularValorSurpresinha(
            SimulaUiState atual,
            ParametroJogoDTO parametroJogoAtual,
            ModalidadeEnum tipoJogoAtual
    ) {

        if (atual == null) {
            return null;
        }

        if (parametroJogoAtual == null) {
            return atual;
        }

        int qtdNumeros =
                atual.getQuantidadeNumerosSurpresinha();

        if (qtdNumeros <= 0) {
            qtdNumeros =
                    atual.getQtdDezenasPossiveisSelecionado();
        }

        int qtdTrevos =
                atual.getQuantidadeTrevosSurpresinha();

        int qtdSurpresinhas =
                atual.getQuantidadeSurpresinhas();

        int qtdConcursos =
                atual.getQtdConcursoSelecionado();

        BigDecimal valorBase =
                BigDecimal.ZERO;

        if (tipoJogoAtual == ModalidadeEnum.MAIS_MILIONARIA) {

            ParametroValorApostaDTO valorTrevos =
                    parametroJogoAtual.getValorApostaBy(
                            qtdNumeros,
                            qtdTrevos
                    );

            if (valorTrevos != null
                    && valorTrevos.getValor() != null) {
                valorBase =
                        valorTrevos.getValor();
            }

        } else {

            valorBase =
                    obterValorBasePorQuantidadeNumeros(
                            parametroJogoAtual,
                            qtdNumeros
                    );
        }

        if (qtdConcursos > 0) {
            valorBase =
                    valorBase.multiply(
                            BigDecimal.valueOf(
                                    qtdConcursos
                            )
                    );
        }

        if (qtdSurpresinhas > 0) {
            valorBase =
                    valorBase.multiply(
                            BigDecimal.valueOf(
                                    qtdSurpresinhas
                            )
                    );
        }

        return atual.copy()
                .setValorAposta(
                        valorBase
                )
                .setValorGrande(
                        isValorGrande(
                                valorBase
                        )
                )
                .setBotaoAdicionarHabilitado(true);
    }

    private BigDecimal obterValorBasePorQuantidadeNumeros(
            ParametroJogoDTO parametroJogoAtual,
            int qtdNumeros
    ) {

        if (parametroJogoAtual == null
                || parametroJogoAtual.getValoresAposta() == null) {
            return BigDecimal.ZERO;
        }

        for (ParametroValorApostaDTO valor :
                parametroJogoAtual.getValoresAposta()) {

            if (valor == null) {
                continue;
            }

            if (valor.getNumeroPrognosticos() != null
                    && valor.getNumeroPrognosticos() == qtdNumeros
                    && valor.getValor() != null) {
                return valor.getValor();
            }
        }

        return BigDecimal.ZERO;
    }

    private boolean isValorGrande(
            BigDecimal valor
    ) {

        return valor != null
                && valor.doubleValue() > 999.99;
    }
    public SimulaUiState abrirSelecaoTimeCoracaoSurpresinha(
            SimulaUiState atual
    ) {
        if (atual == null) {
            return null;
        }

        return atual.copy()
                .setSurpresinhaHabilitada(true)
                .setEtapaAposta(EtapaAposta.TIME_CORACAO)

                // marca o cenário específico:
                // estou escolhendo time a partir da surpresinha
                .setTelaSelecaoTimeAtivado(true)
                .setEscolhaTimeCoracaoSurpresinha(true)

                // quando entrar nessa tela, não reaproveita time antigo
                .setEquipeSelecionada(null)

                // não é fluxo 2/2 normal da Timemania manual
                .setExibirInfoEtapa(false)
                .setTextoInfoEtapa("")
                .setPossuiAlteracoesPendentes(true)

                // rodapé específico desse cenário
                .setBotaoNextStepHabilitado(false)
                .setBotaoCompletarVisivel(false)
                .setBotaoCompletarHabilitado(false)
                .setBotaoLimparHabilitado(false)
                .setMostrarBotaoLimparAposta(false)
                .setMostrarSalvarAposta(false)
                .setApostaFavoritada(false)

                // adicionar aparece, mas o render pode habilitar só com time
                .setBotaoAdicionarHabilitado(false);
    }

    private int obterQuantidadeTrevosInicial(
            ParametroJogoDTO parametroJogoAtual
    ) {
        if (parametroJogoAtual == null
                || parametroJogoAtual.getTrevos() == null
                || parametroJogoAtual.getTrevos().getQtdMinima() == null) {
            return 0;
        }

        return parametroJogoAtual.getTrevos().getQtdMinima();
    }
}
