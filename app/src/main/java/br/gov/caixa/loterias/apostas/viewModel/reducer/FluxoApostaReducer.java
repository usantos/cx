package br.gov.caixa.loterias.apostas.viewModel.reducer;

import android.util.Log;

import java.util.ArrayList;

import br.gov.caixa.loterias.apostas.effect.CompletarApostaEffect;
import br.gov.caixa.loterias.apostas.effect.ScreenSimulaEffect;
import br.gov.caixa.loterias.apostas.event.SimulaUiEvent;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.state.SimulaUiState;
import br.gov.caixa.loterias.apostas.utils.EscolhaUtils;
import br.gov.caixa.loterias.apostas.utils.EtapaAposta;

public class FluxoApostaReducer {

    public SimulaUiState cartelaAlterada(
            SimulaUiState atual,
            SimulaUiEvent.CartelaAlterada event
    ) {

        if (atual == null || event == null) {
            return atual;
        }

        ResultadoFluxoAposta fluxo =
                resolverFluxoAposta(
                        atual,
                        event
                );

        ModalidadeEnum tipoJogo =
                event.getTipoJogo();

        EtapaAposta etapa =
                fluxo.etapa;

        boolean possuiEtapaComplementar =
                possuiEtapaComplementar(
                        tipoJogo
                );

        boolean estaNaEtapaNumeros =
                etapa == EtapaAposta.NUMEROS;

        boolean numerosCompletos =
                fluxo.numerosCompletos;

        boolean deveMostrarFluxoNextStep =
                possuiEtapaComplementar
                        && estaNaEtapaNumeros;

        boolean estaEmTimeCoracao =
                tipoJogo == ModalidadeEnum.TIMEMANIA
                        && etapa == EtapaAposta.TIME_CORACAO;

        boolean botaoNextStepHabilitado =
                possuiEtapaComplementar
                        && etapa == EtapaAposta.NUMEROS
                        && numerosCompletos;

        boolean botaoAdicionarHabilitado;
        boolean botaoCompletarHabilitado;
        boolean mostrarSalvarAposta;
        boolean botaoLimparHabilitado;

        boolean apostaFavoritada =
                atual.isApostaFavoritada();

        boolean escolhaTimeSurpresinha =
                atual.isSurpresinhaHabilitada()
                        && event.isEscolhaTimeCoracaoSurpresinha()
                        && etapa == EtapaAposta.TIME_CORACAO;

        if (escolhaTimeSurpresinha) {
            botaoAdicionarHabilitado = event.isEquipeSelecionadaValida();
            botaoCompletarHabilitado = false;
            mostrarSalvarAposta = false;
            botaoLimparHabilitado = false;
            apostaFavoritada = false;
        }
        else if (estaEmTimeCoracao) {

            botaoAdicionarHabilitado =
                    event.isEquipeSelecionadaValida();

            botaoCompletarHabilitado =
                    !event.isEquipeSelecionadaValida();

            mostrarSalvarAposta =
                    event.isEquipeSelecionadaValida();

            botaoLimparHabilitado =
                    event.isEquipeSelecionadaValida();

            if (!event.isEquipeSelecionadaValida()) {
                apostaFavoritada = false;
            }

        } else if (deveMostrarFluxoNextStep) {

            botaoAdicionarHabilitado = false;

            botaoCompletarHabilitado =
                    !numerosCompletos;

            mostrarSalvarAposta = false;

            botaoLimparHabilitado =
                    event.getQtdDezenasSelecionadas() > 0;

            if (!numerosCompletos) {
                apostaFavoritada = false;
            }

        } else {

            botaoAdicionarHabilitado =
                    fluxo.apostaPronta;

            botaoCompletarHabilitado =
                    !fluxo.apostaPronta;

            mostrarSalvarAposta =
                    fluxo.apostaPronta
                            && podeMostrarSalvarAposta(
                            tipoJogo
                    );

            botaoLimparHabilitado =
                    calcularBotaoLimparHabilitadoPorFluxo(
                            event,
                            fluxo
                    );

            if (!fluxo.apostaPronta) {
                apostaFavoritada = false;
            }
        }

        int qtdDezenasPossiveis =
                event.getQtdDezenasPossiveisSelecionado();

        int quantidadeSelecionadaTexto;

        if (tipoJogo == ModalidadeEnum.SUPER_7) {
            quantidadeSelecionadaTexto =
                    event.getQtdTotalSelecionadosSuperSete();
        } else {
            quantidadeSelecionadaTexto =
                    event.getQtdDezenasSelecionadas();
        }

        String textoSelecionados =
                montarTextoSelecionados(
                        quantidadeSelecionadaTexto,
                        qtdDezenasPossiveis
                );

        String textoBotaoQuantidadeNumeros =
                montarTextoQuantidadeNumeros(
                        qtdDezenasPossiveis
                );

        boolean exibirInfoEtapa =
                possuiEtapaComplementar(tipoJogo)
                        && !atual.isEscolhaTimeCoracaoSurpresinha();

        String textoInfoEtapa =
                atual.isEscolhaTimeCoracaoSurpresinha()
                        ? ""
                        : obterTextoInfoEtapa(
                        tipoJogo,
                        etapa
                );

        boolean possuiAlteracoesPendentes;

        if (tipoJogo == ModalidadeEnum.SUPER_7) {
            possuiAlteracoesPendentes =
                    event.getQtdTotalSelecionadosSuperSete() > 0;
        } else if(tipoJogo == ModalidadeEnum.LOTECA){
            possuiAlteracoesPendentes = event.getQtdPartidasSelecionadas() > 0;
        }else {
            possuiAlteracoesPendentes =
                    event.getQtdDezenasSelecionadas() > 0;
        }

        return atual.toBuilder()
                .setEtapaAposta(etapa)
                .setQtdDezenasPossiveisSelecionado(
                        qtdDezenasPossiveis
                )
                .setTextoBotaoPrognosticosSelecionado(
                        textoBotaoQuantidadeNumeros
                )
                .setBotaoNextStepHabilitado(
                        botaoNextStepHabilitado
                )
                .setTextoEscolha(
                        EscolhaUtils.obterTextoEscolha(tipoJogo)
                )
                .setBotaoAdicionarHabilitado(
                        botaoAdicionarHabilitado
                )
                .setBotaoCompletarHabilitado(
                        botaoCompletarHabilitado
                )
                .setBotaoLimparHabilitado(
                        botaoLimparHabilitado
                )
                .setMostrarBotaoLimparAposta(
                        botaoLimparHabilitado
                )
                .setQtdTotalSelecionadosSuperSete(
                        event.getQtdTotalSelecionadosSuperSete()
                )
                .setMostrarSalvarAposta(
                        mostrarSalvarAposta
                )
                .setApostaFavoritada(
                        apostaFavoritada
                )
                .setTextoSelecionados(
                        textoSelecionados
                )
                .setPossuiAlteracoesPendentes(possuiAlteracoesPendentes)
                .setExibirOpcaoOutrosNumeros(
                        event.isExibirOpcaoOutrosNumeros()
                )
                .setOpcaoOutrosNumerosSelecionada(
                        event.isOpcaoOutrosNumerosSelecionada()
                )
                .setExibirInfoEtapa(
                        exibirInfoEtapa
                )
                .setTelaSelecaoTimeAtivado(
                        escolhaTimeSurpresinha
                                || atual.isTelaSelecaoTimeAtivado()
                )
                .setEscolhaTimeCoracaoSurpresinha(
                        escolhaTimeSurpresinha
                                || atual.isEscolhaTimeCoracaoSurpresinha()
                )
                .setTextoInfoEtapa(
                        textoInfoEtapa
                )
                .setDezenasSelecionadas(
                        event.getDezenasSelecionadas()
                )
                .setJogosLoteca(event.getQtdPartidasSelecionadas())
                .setDuplasLoteca(event.getDuplosLoteca())
                .setTriplasLoteca(event.getTriplosLoteca())
                .setSimplesLoteca(event.getQtdPartidasTotal())
                .build();
    }

    public SimulaUiState nextStepClicado(
            SimulaUiState atual,
            SimulaUiEvent.NextStepClicado event,
            ModalidadeEnum tipoJogoAtual
    ) {

        if (atual == null || event == null || event.getTipoJogo() == null) {
            return atual;
        }

        switch (event.getTipoJogo()) {

            case MAIS_MILIONARIA:
                return abrirEtapaComplementar(
                        atual,
                        tipoJogoAtual,
                        EtapaAposta.TREVOS,
                        new CompletarApostaEffect.AbrirTrevos()
                );

            case DIA_DE_SORTE:
                return abrirEtapaComplementar(
                        atual,
                        tipoJogoAtual,
                        EtapaAposta.MES_SORTE,
                        new CompletarApostaEffect.AbrirMesDeSorte()
                );

            case TIMEMANIA:
                return abrirEtapaComplementar(
                        atual,
                        tipoJogoAtual,
                        EtapaAposta.TIME_CORACAO,
                        null
                );

            default:
                return atual;
        }
    }

    public SimulaUiState abrirEtapaComplementar(
            SimulaUiState atual,
            ModalidadeEnum tipoJogoAtual,
            EtapaAposta etapa,
            CompletarApostaEffect comando
    ) {

        if (atual == null || etapa == null) {
            return atual;
        }

        String textoSelecionados =
                atual.getTextoSelecionados();

        if (etapa == EtapaAposta.TREVOS) {
            textoSelecionados = "";
        } else if (etapa == EtapaAposta.MES_SORTE) {
            textoSelecionados = "Selecione o mes de sorte:";
        } else if (etapa == EtapaAposta.TIME_CORACAO) {
            textoSelecionados = "Escolha o time do coracao:";
        }

        return atual.toBuilder()
                .setEtapaAposta(etapa)
                .setBotaoNextStepHabilitado(false)
                .setBotaoAdicionarHabilitado(false)
                .setBotaoCompletarHabilitado(true)
                .setBotaoCompletarVisivel(true)
                .setBotaoLimparHabilitado(false)
                .setMostrarBotaoLimparAposta(false)
                .setMostrarSalvarAposta(false)
                .setApostaFavoritada(false)
                .setTextoSelecionados(textoSelecionados)
                .setComandoCompletarAposta(comando)
                .setExibirInfoEtapa(true)
                .setPossuiAlteracoesPendentes(false)
                .setTextoInfoEtapa(
                        obterTextoInfoEtapa(
                                tipoJogoAtual,
                                etapa
                        )
                )
                .build();
    }

    public SimulaUiState.Builder resetarEtapaComplementarSeNecessario(
            SimulaUiState.Builder builder,
            SimulaUiState atual,
            ModalidadeEnum tipoJogoAtual,
            int novaQuantidade
    ) {

        if (builder == null || atual == null || tipoJogoAtual == null) {
            return builder;
        }

        boolean mudouQuantidade =
                novaQuantidade
                        != atual.getQtdDezenasPossiveisSelecionado();

        if (!mudouQuantidade) {
            return builder;
        }

        switch (tipoJogoAtual) {

            case MAIS_MILIONARIA:
                if (atual.getEtapaAposta() == EtapaAposta.TREVOS) {
                    resetarParaEtapaNumeros(builder)
                            .setTrevosSelecionados(new ArrayList<Integer>())
                            .setQuantidadeTrevosSelecionada(0)
                            .setValorTrevosSelecionado(null)
                            .setValoresTrevos(new ArrayList<>())
                            .setLabelsQuantidadeTrevos(new ArrayList<String>())
                            .setPosicaoTrevosSelecionada(0)
                            .setTextoBotaoQuantidadeTrevos("2 trevos")
                            .setComandoTelaSimula(
                                    new ScreenSimulaEffect.VoltarEtapa(
                                            EtapaAposta.NUMEROS
                                    )
                            );
                }
                break;

            case DIA_DE_SORTE:
                if (atual.getEtapaAposta() == EtapaAposta.MES_SORTE) {
                    resetarParaEtapaNumeros(builder)
                            .setMesSelecionado(null)
                            .setComandoTelaSimula(
                                    new ScreenSimulaEffect.VoltarEtapa(
                                            EtapaAposta.NUMEROS
                                    )
                            );
                }
                break;

            case TIMEMANIA:
                if (atual.getEtapaAposta() == EtapaAposta.TIME_CORACAO) {
                    resetarParaEtapaNumeros(builder)
                            .setEquipeSelecionada(null)
                            .setTelaSelecaoTimeAtivado(false)
                            .setEscolhaTimeCoracaoSurpresinha(false)
                            .setComandoTelaSimula(
                                    new ScreenSimulaEffect.VoltarEtapa(
                                            EtapaAposta.NUMEROS
                                    )
                            );
                }
                break;

            default:
                break;
        }

        return builder;
    }

    private SimulaUiState.Builder resetarParaEtapaNumeros(
            SimulaUiState.Builder builder
    ) {

        return builder
                .setEtapaAposta(EtapaAposta.NUMEROS)
                .setTextoSelecionados("Selecione os números:")
                .setBotaoNextStepHabilitado(false)
                .setBotaoAdicionarHabilitado(false)
                .setBotaoCompletarHabilitado(true)
                .setBotaoCompletarVisivel(true)
                .setBotaoLimparHabilitado(false)
                .setMostrarBotaoLimparAposta(false)
                .setMostrarSalvarAposta(false)
                .setApostaFavoritada(false)
                .setTextoInfoEtapa("1/2");
    }

    public boolean possuiEtapaComplementar(
            ModalidadeEnum tipoJogo
    ) {

        return tipoJogo == ModalidadeEnum.MAIS_MILIONARIA
                || tipoJogo == ModalidadeEnum.DIA_DE_SORTE
                || tipoJogo == ModalidadeEnum.TIMEMANIA;
    }

    public String obterTextoInfoEtapa(
            ModalidadeEnum modalidade,
            EtapaAposta etapa
    ) {

        if (!possuiEtapaComplementar(modalidade)) {
            return "";
        }

        if (etapa == null) {
            return "";
        }

        switch (etapa) {

            case NUMEROS:
                return "1/2";

            case TREVOS:
            case MES_SORTE:
            case TIME_CORACAO:
                return "2/2";

            default:
                return "";
        }
    }

    public String montarTextoSelecionados(
            int qtdSelecionados,
            int qtdNecessarios
    ) {

        if (qtdSelecionados <= 0) {
            return "Selecione os números:";
        }

        return "Selecionados: "
                + qtdSelecionados
                + "/"
                + qtdNecessarios;
    }

    public String montarTextoQuantidadeNumeros(
            int qtdPrognosticos
    ) {

        return qtdPrognosticos + " números";
    }

    private ResultadoFluxoAposta resolverFluxoAposta(
            SimulaUiState atual,
            SimulaUiEvent.CartelaAlterada event
    ) {

        ModalidadeEnum tipoJogo =
                event.getTipoJogo();

        boolean numerosCompletos =
                isSelecaoPrincipalCompleta(
                        event
                );

        EtapaAposta etapaAtual =
                atual.getEtapaAposta();

        if (etapaAtual == null) {
            etapaAtual = EtapaAposta.NUMEROS;
        }

        if (tipoJogo == ModalidadeEnum.TIMEMANIA) {
            return resolverFluxoTimemania(
                    atual,
                    event,
                    etapaAtual,
                    numerosCompletos
            );
        }


        if (tipoJogo == ModalidadeEnum.DIA_DE_SORTE) {

            boolean estaNaEtapaMes =
                    etapaAtual == EtapaAposta.MES_SORTE;

            boolean mesCompleto =
                    event.isEtapaComplementarCompleta();

            return new ResultadoFluxoAposta(
                    estaNaEtapaMes
                            ? EtapaAposta.MES_SORTE
                            : EtapaAposta.NUMEROS,
                    numerosCompletos,
                    numerosCompletos && mesCompleto
            );
        }

        if (tipoJogo == ModalidadeEnum.MAIS_MILIONARIA) {

            boolean estaNaEtapaTrevos =
                    etapaAtual == EtapaAposta.TREVOS;

            return new ResultadoFluxoAposta(
                    estaNaEtapaTrevos
                            ? EtapaAposta.TREVOS
                            : EtapaAposta.NUMEROS,
                    numerosCompletos,
                    numerosCompletos
                            && atual.isBotaoAdicionarHabilitado()
            );
        }

        return new ResultadoFluxoAposta(
                numerosCompletos
                        ? EtapaAposta.PRONTA
                        : EtapaAposta.NUMEROS,
                numerosCompletos,
                numerosCompletos
        );
    }
    private ResultadoFluxoAposta resolverFluxoTimemania(
            SimulaUiState atual,
            SimulaUiEvent.CartelaAlterada event,
            EtapaAposta etapaAtual,
            boolean numerosCompletos
    ) {
        boolean timeValido =
                event.isEquipeSelecionadaValida();

        boolean escolhaTimeSurpresinha =
                atual != null
                        && atual.isSurpresinhaHabilitada()
                        && atual.isEscolhaTimeCoracaoSurpresinha();

        if (escolhaTimeSurpresinha) {
            return new ResultadoFluxoAposta(
                    EtapaAposta.TIME_CORACAO,
                    true,
                    timeValido
            );
        }

        if (!numerosCompletos) {
            return new ResultadoFluxoAposta(
                    EtapaAposta.NUMEROS,
                    false,
                    false
            );
        }

        if (etapaAtual == EtapaAposta.TIME_CORACAO
                || etapaAtual == EtapaAposta.PRONTA) {
            return new ResultadoFluxoAposta(
                    EtapaAposta.TIME_CORACAO,
                    true,
                    timeValido
            );
        }

        return new ResultadoFluxoAposta(
                EtapaAposta.NUMEROS,
                true,
                false
        );
    }

    private boolean isSelecaoPrincipalCompleta(
            SimulaUiEvent.CartelaAlterada event
    ) {

        ModalidadeEnum tipoJogo =
                event.getTipoJogo();

        if (tipoJogo == ModalidadeEnum.SUPER_7) {
            return event.getQtdTotalSelecionadosSuperSete()
                    == event.getQtdDezenasPossiveisSelecionado();
        }

        if (tipoJogo == ModalidadeEnum.LOTECA) {

            boolean partidasCompletas =
                    event.getQtdPartidasSelecionadas()
                            == event.getQtdPartidasTotal();

            boolean possuiDuploOuTriplo =
                    event.getDuplosLoteca() > 0
                            || event.getTriplosLoteca() > 0;

            return partidasCompletas
                    && possuiDuploOuTriplo;
        }

        if (tipoJogo == ModalidadeEnum.LOTOGOL) {
            return event.getQtdPartidasSelecionadas()
                    == event.getQtdPartidasTotal();
        }

        return event.getQtdDezenasSelecionadas()
                == event.getQtdDezenasPossiveisSelecionado();
    }
    public SimulaUiState voltarEtapa(
            SimulaUiState atual
    ) {
        if (atual == null) {
            return null;
        }

        switch (atual.getEtapaAposta()) {

            case TREVOS:
            case MES_SORTE:
            case TIME_CORACAO:

                return atual.toBuilder()
                        .setEtapaAposta(
                                EtapaAposta.NUMEROS
                        )
                        .setTextoSelecionados(
                                montarTextoSelecionados(
                                        atual.getDezenasSelecionadas().size(),
                                        atual.getQtdDezenasPossiveisSelecionado()
                                )
                        )
                        .setTextoInfoEtapa("1/2")
                        .setBotaoNextStepHabilitado(
                                atual.getDezenasSelecionadas().size()
                                        == atual.getQtdDezenasPossiveisSelecionado()
                        )
                        .setBotaoAdicionarHabilitado(false)
                        .setBotaoCompletarHabilitado(true)
                        .setBotaoCompletarVisivel(true)
                        .setMostrarSalvarAposta(false)
                        .setApostaFavoritada(false)
                        .setComandoTelaSimula(
                                new ScreenSimulaEffect.VoltarEtapa(
                                        EtapaAposta.NUMEROS
                                )
                        )
                        .build();

            default:
                return atual;
        }
    }
    private boolean calcularBotaoLimparHabilitadoPorFluxo(
            SimulaUiEvent.CartelaAlterada event,
            ResultadoFluxoAposta fluxo
    ) {

        ModalidadeEnum tipoJogo =
                event.getTipoJogo();

        if (tipoJogo == ModalidadeEnum.TIMEMANIA
                && fluxo.etapa == EtapaAposta.TIME_CORACAO) {
            return event.isEquipeSelecionadaValida();
        }

        if (tipoJogo == ModalidadeEnum.DIA_DE_SORTE
                && fluxo.etapa == EtapaAposta.MES_SORTE) {
            return false;
        }

        if (tipoJogo == ModalidadeEnum.MAIS_MILIONARIA
                && fluxo.etapa == EtapaAposta.TREVOS) {
            return false;
        }

        if (tipoJogo == ModalidadeEnum.LOTECA) {
            return event.getQtdPartidasSelecionadas() > 0;
        }

        if (tipoJogo == ModalidadeEnum.LOTOGOL) {
            return event.isPossuiPlacarLotogol();
        }

        if (tipoJogo == ModalidadeEnum.SUPER_7) {
            return event.getQtdTotalSelecionadosSuperSete() > 0;
        }

        return event.getQtdDezenasSelecionadas() > 0;
    }

    private boolean podeMostrarSalvarAposta(
            ModalidadeEnum tipoJogo
    ) {

        return tipoJogo != ModalidadeEnum.LOTECA
                && tipoJogo != ModalidadeEnum.LOTOGOL;
    }
    private static class ResultadoFluxoAposta {

        private final EtapaAposta etapa;
        private final boolean numerosCompletos;
        private final boolean apostaPronta;

        ResultadoFluxoAposta(
                EtapaAposta etapa,
                boolean numerosCompletos,
                boolean apostaPronta
        ) {
            this.etapa = etapa;
            this.numerosCompletos = numerosCompletos;
            this.apostaPronta = apostaPronta;
        }
    }
    public SimulaUiState recalcularEstadoCartela(
            SimulaUiState state,
            ModalidadeEnum tipoJogoAtual
    ) {
        if (state == null) {
            return null;
        }

        int qtdSelecionadas =
                obterQuantidadeSelecionadaParaCartela(
                        state,
                        tipoJogoAtual
                );

        int qtdPossiveis =
                state.getQtdDezenasPossiveisSelecionado();

        SimulaUiEvent.CartelaAlterada event =
                new SimulaUiEvent.CartelaAlterada(
                        tipoJogoAtual,
                        qtdSelecionadas,
                        qtdPossiveis,
                        state.getQtdTotalSelecionadosSuperSete(),
                        0,
                        state.getJogosLoteca(),
                        state.getDuplasLoteca(),
                        state.getTriplasLoteca(),
                        false,
                        state.getEquipeSelecionada() != null,
                        state.isEscolhaTimeCoracaoSurpresinha(),
                        state.isOpcaoOutrosNumerosSelecionada(),
                        state.isExibirOpcaoOutrosNumeros(),
                        calcularEtapaComplementarCompleta(
                                state,
                                tipoJogoAtual
                        ),
                        state.getDezenasSelecionadas()
                );

        return cartelaAlterada(
                state,
                event
        );
    }
    private int obterQuantidadeSelecionadaParaCartela(
            SimulaUiState state,
            ModalidadeEnum tipoJogoAtual
    ) {
        if (tipoJogoAtual == ModalidadeEnum.SUPER_7) {
            return state.getQtdTotalSelecionadosSuperSete();
        }

        if (state.getDezenasSelecionadas() == null) {
            return 0;
        }

        return state.getDezenasSelecionadas().size();
    }

    private boolean calcularEtapaComplementarCompleta(
            SimulaUiState state,
            ModalidadeEnum tipoJogoAtual
    ) {
        if (tipoJogoAtual == ModalidadeEnum.DIA_DE_SORTE) {
            return state.getMesSelecionado() != null;
        }

        if (tipoJogoAtual == ModalidadeEnum.TIMEMANIA) {
            return state.getEquipeSelecionada() != null;
        }

        if (tipoJogoAtual == ModalidadeEnum.MAIS_MILIONARIA) {
            return state.getQuantidadeTrevosSelecionada() > 0;
        }

        return true;
    }

}