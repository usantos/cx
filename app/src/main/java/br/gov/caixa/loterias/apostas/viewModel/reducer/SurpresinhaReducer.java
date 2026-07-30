package br.gov.caixa.loterias.apostas.viewModel.reducer;

import java.math.BigDecimal;
import java.util.ArrayList;

import br.gov.caixa.loterias.apostas.effect.ScreenSimulaEffect;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroJogoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroValorApostaDTO;
import br.gov.caixa.loterias.apostas.state.SimulaUiState;
import br.gov.caixa.loterias.apostas.utils.EtapaAposta;

public class SurpresinhaReducer {

    public SimulaUiState alterarSurpresinha(
            SimulaUiState atual,
            boolean habilitada,
            ModalidadeEnum tipoJogoAtual
    ) {

        if (atual == null) {
            return null;
        }

        if (habilitada) {

            ScreenSimulaEffect comando =
                    new ScreenSimulaEffect.AbrirSurpresinha(
                            tipoJogoAtual == ModalidadeEnum.MAIS_MILIONARIA,
                            true
                    );

            return atual.toBuilder()
                    .setSurpresinhaHabilitada(true)
                    .setComandoTelaSimula(comando)
                    .setMostrarSalvarAposta(false)
                    .setMostrarBotaoLimparAposta(false)
                    .setBotaoLimparHabilitado(false)
                    .setBotaoCompletarVisivel(false)
                    .setBotaoCompletarHabilitado(false)
                    .setBotaoAdicionarHabilitado(true)
                    .build();
        }


        return atual.toBuilder()
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
                // limpar estado visual
                .setBotaoNextStepHabilitado(false)
                .setMostrarBotaoLimparAposta(true)
                .setComandoTelaSimula(
                        new ScreenSimulaEffect.VoltarEtapa(
                                EtapaAposta.NUMEROS
                        )
                )
                .build();
    }

    public SimulaUiState surpresinhaAberta(
            SimulaUiState atual
    ) {

        if (atual == null) {
            return null;
        }

        return atual.toBuilder()
                .setSurpresinhaHabilitada(true)
                .setMostrarSalvarAposta(false)
                .setMostrarBotaoLimparAposta(false)
                .setBotaoLimparHabilitado(false)
                .setBotaoCompletarVisivel(false)
                .setBotaoCompletarHabilitado(false)
                .setBotaoAdicionarHabilitado(true)
                .build();
    }

    public SimulaUiState surpresinhaFechada(
            SimulaUiState atual
    ) {

        if (atual == null) {
            return null;
        }

        return atual.toBuilder()
                .setSurpresinhaHabilitada(false)
                .setBotaoCompletarVisivel(true)
                .setBotaoCompletarHabilitado(true)
                .setTelaSelecaoTimeAtivado(false)
                .setEscolhaTimeCoracaoSurpresinha(false)
                .setBotaoAdicionarHabilitado(false)
                .build();
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
                atual.toBuilder()
                        .setQuantidadeSurpresinhas(
                                quantidade
                        )
                        .build();

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
                atual.toBuilder()
                        .setQuantidadeNumerosSurpresinha(
                                quantidade
                        )
                        .build();

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
                atual.toBuilder()
                        .setQuantidadeTrevosSurpresinha(
                                quantidade
                        )
                        .build();

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

        return atual.toBuilder()
                .setValorAposta(
                        valorBase
                )
                .setValorGrande(
                        isValorGrande(
                                valorBase
                        )
                )
                .build();
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

        return atual.toBuilder()
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

                // rodapé específico desse cenário
                .setBotaoNextStepHabilitado(false)
                .setBotaoCompletarVisivel(false)
                .setBotaoCompletarHabilitado(false)
                .setBotaoLimparHabilitado(false)
                .setMostrarBotaoLimparAposta(false)
                .setMostrarSalvarAposta(false)
                .setApostaFavoritada(false)

                // adicionar aparece, mas o render pode habilitar só com time
                .setBotaoAdicionarHabilitado(false)

                .build();
    }
}