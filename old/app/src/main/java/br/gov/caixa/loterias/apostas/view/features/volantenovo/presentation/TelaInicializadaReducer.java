package br.gov.caixa.loterias.apostas.view.features.volantenovo.presentation;

import java.math.BigDecimal;
import java.util.ArrayList;

import br.gov.caixa.loterias.apostas.effect.AnimacaoEffect;
import br.gov.caixa.loterias.apostas.effect.ScreenSimulaEffect;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.utils.BarraTituloDTO;
import br.gov.caixa.loterias.apostas.utils.EscolhaUtils;
import br.gov.caixa.loterias.apostas.utils.EtapaAposta;

public class TelaInicializadaReducer {

    private final FluxoApostaReducer fluxoApostaReducer;

    public TelaInicializadaReducer(
            FluxoApostaReducer fluxoApostaReducer
    ) {
        this.fluxoApostaReducer =
                fluxoApostaReducer;
    }

    public SimulaUiState criarEstadoInicializado(
            ModalidadeEnum tipoJogo,
            boolean especial,
            BarraTituloDTO barraTituloDTO,
            BigDecimal valorInicialAposta,
            AnimacaoEffect animacaoEffect,
            int qtdMinima,
            int qtdConcurso,
            int qtdTrevosMinima,
            String textoBotaoQuantidadeNumeros
    ) {

        BigDecimal valorSeguro =
                valorInicialAposta != null
                        ? valorInicialAposta
                        : BigDecimal.ZERO;

        String textoBotao =
                textoBotaoQuantidadeNumeros != null
                        ? textoBotaoQuantidadeNumeros
                        : "";

        SimulaUiState builder = SimulaUiState.initial()
                .setBarraTituloDTO(
                        barraTituloDTO
                )
                .setTipoJogo(
                        tipoJogo
                )
                .setEspecial(
                        especial
                )
                .setComandoAnimacao(
                        animacaoEffect
                )
                .setComandoTelaSimula(
                        new ScreenSimulaEffect.VoltarEtapa(
                                EtapaAposta.NUMEROS
                        )
                )
                .setTextoEscolha(EscolhaUtils.obterTextoEscolha(tipoJogo))
                .setComandoCarrinho(null)
                .setComandoCompletarAposta(null)
                .setComandoFavoritarAposta(null)
                .setComandoAnalytics(null)
                .setValorAposta(
                        valorSeguro
                )
                .setValorGrande(
                        isValorGrande(
                                valorSeguro
                        )
                )
                .setEtapaAposta(
                        EtapaAposta.NUMEROS
                )
                .setSurpresinhaHabilitada(false)
                .setTelaSelecaoTimeAtivado(false)
                .setEscolhaTimeCoracaoSurpresinha(false)
                .setDezenasSelecionadas(
                        new ArrayList<>()
                )
                .setTrevosSelecionados(
                        new ArrayList<>()
                )
                .setValorTrevosSelecionado(null)
                .setValoresTrevos(
                        new ArrayList<>()
                )
                .setLabelsQuantidadeTrevos(
                        new ArrayList<>()
                )
                .setPosicaoTrevosSelecionada(0)
                .setTextoBotaoQuantidadeTrevos(
                        "2 trevos"
                )
                .setMesSelecionado(null)
                .setEquipeSelecionada(null)
                .setTelaSelecaoTimeAtivado(false)
                .setEscolhaTimeCoracaoSurpresinha(false)
                .setQuantidadeSurpresinhas(1)
                .setQuantidadeNumerosSurpresinha(
                        qtdMinima
                )
                .setQuantidadeTrevosSurpresinha(
                        qtdTrevosMinima
                )
                .setQuantidadeTrevosSelecionada(0)
                .setQtdDezenasPossiveisSelecionado(
                        qtdMinima
                )
                .setQtdConcursoSelecionado(
                        qtdConcurso
                )
                .setTextoSelecionados(
                        "Selecione os números:"
                )
                .setTextoBotaoPrognosticosSelecionado(
                        textoBotao
                )
                .setBotaoCompletarVisivel(true)
                .setBotaoCompletarHabilitado(true)
                .setBotaoAdicionarHabilitado(false)
                .setBotaoNextStepHabilitado(false)
                .setBotaoLimparHabilitado(false)
                .setMostrarBotaoLimparAposta(false)
                .setMostrarSalvarAposta(false)
                .setApostaFavoritada(false)
                .setExibirOpcaoOutrosNumeros(false)
                .setOpcaoOutrosNumerosSelecionada(false)
                .setLimparNumerosCartela(false)
                .setLimparTimeCoracao(false)
                .setLimparSuperSete(false)
                .setLimparLoteca(false)
                .setLimparLotogol(false)
                .setLimparOpcaoOutrosNumeros(false)
                .setQtdTotalSelecionadosSuperSete(0)
                .setJogosLoteca(0)
                .setSimplesLoteca(0)
                .setDuplasLoteca(0)
                .setTriplasLoteca(0)
                .setExibirDialogComoJogar(false)
                .setMensagemComoJogar("")
                .setPossuiAlteracoesPendentes(false)
                .setTitleComoJogar("")
                .setExibirDialogFavoritarAposta(false)
                .setExibirDialogTeimosinha(false)
                .setExibirDialogSemTeimosinha(false)
                .setAtualizarValorApostaPorTeimosinha(false)
                .setExibirDialogQuantidadeNumeros(false)
                .setExibirAvisoQuantidadeNumerosMaxima(false)
                .setAtualizarValorApostaPorQuantidadeNumeros(false)
                .setResetarTrevosPorQuantidadeNumeros(false)
                .setPosicaoQuantidadeNumerosSelecionada(0)
                .setAbrirCarrinho(false)
                .setExibirInfoEtapa(
                        possuiEtapaComplementar(
                                tipoJogo
                        )
                )
                .setTextoInfoEtapa(
                        obterTextoInfoEtapa(
                                tipoJogo,
                                EtapaAposta.NUMEROS
                        )
                )
                .setExibirDialogQuantidadeTrevos(false)
                .setExibirAvisoTrevosMaximo(false)
                .setSubtituloTrevos("");

        if(tipoJogo == ModalidadeEnum.LOTECA){
            return criarInicialLoteca(builder);
        }
        else {
            return builder;
        }
    }
    private SimulaUiState criarInicialLoteca(SimulaUiState builder){
        return builder
                .setMostrarSalvarAposta(false)
                .setMostrarBotaoLimparAposta(false)
                .setBotaoCompletarVisivel(false)
                .setBotaoNextStepHabilitado(false);
    }
    private boolean possuiEtapaComplementar(
            ModalidadeEnum tipoJogo
    ) {

        if (fluxoApostaReducer != null) {
            return fluxoApostaReducer
                    .possuiEtapaComplementar(
                            tipoJogo
                    );
        }

        return tipoJogo == ModalidadeEnum.MAIS_MILIONARIA
                || tipoJogo == ModalidadeEnum.DIA_DE_SORTE
                || tipoJogo == ModalidadeEnum.TIMEMANIA;
    }

    private String obterTextoInfoEtapa(
            ModalidadeEnum tipoJogo,
            EtapaAposta etapa
    ) {

        if (fluxoApostaReducer != null) {
            return fluxoApostaReducer
                    .obterTextoInfoEtapa(
                            tipoJogo,
                            etapa
                    );
        }

        if (!possuiEtapaComplementar(tipoJogo)) {
            return "";
        }

        if (etapa == EtapaAposta.NUMEROS) {
            return "1/2";
        }

        if (etapa == EtapaAposta.TREVOS
                || etapa == EtapaAposta.MES_SORTE
                || etapa == EtapaAposta.TIME_CORACAO) {
            return "2/2";
        }

        return "";
    }

    private boolean isValorGrande(
            BigDecimal valor
    ) {

        return valor != null
                && valor.doubleValue() > 999.99;
    }
}
