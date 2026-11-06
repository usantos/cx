package br.gov.caixa.loterias.apostas.view.features.volantenovo.presentation;

import android.app.Application;

import androidx.annotation.NonNull;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;

public class LotecaViewModel extends SimulaViewModel {

    public LotecaViewModel(@NonNull Application application) {
        super(application);
    }

    @Override
    protected boolean handleCartelaEvents(
            SimulaUiEvent event
    ) {

        if (event instanceof SimulaUiEvent.CartelaAlterada cartelaAlterada) {

            handleCartelaAlterada(cartelaAlterada);

            return true;
        }
        if (event instanceof SimulaUiEvent.ValorApostaAlterado valorApostaAlterado) {

            handleValorApostaAlterado(valorApostaAlterado);

            return true;
        }

        if(event instanceof SimulaUiEvent.LotecaAtualizada){
            handleLotecaAtualizada((SimulaUiEvent.LotecaAtualizada) event);
            return true;
        }

        if (event instanceof SimulaUiEvent.PalpitesLotecaAtualizado) {
            handlePalpitesLotecaAtualizado(
                    (SimulaUiEvent.PalpitesLotecaAtualizado) event
            );
            return true;
        }

        if (event instanceof SimulaUiEvent.ResetarAvisoPalpitesLoteca) {
            handleResetarAvisoPalpitesLoteca();
            return true;
        }

        if (event instanceof SimulaUiEvent.InicializarParametrosAposta inicializarParametrosAposta) {
            inicializarParametrosAposta(
                    inicializarParametrosAposta
            );
            return true;
        }

        if (event instanceof SimulaUiEvent.TelaInicializada telaInicializada) {

            handleTelaInicializada(telaInicializada);

            return true;
        }

        if (event instanceof SimulaUiEvent.ComoJogarClicado) {

            handleComoJogar();

            return true;
        }

        if (event instanceof SimulaUiEvent.DialogComoJogarConsumido) {

            consumirDialogComoJogar();

            return true;
        }


        return false;
    }
    private void handleLotecaAtualizada(
            SimulaUiEvent.LotecaAtualizada event
    ){
        updateState(state ->
                valorApostaReducer.atualizarLoteca(
                        state,
                        event,
                        parametroJogoAtual
                )
        );
    }

    private void handlePalpitesLotecaAtualizado(
            SimulaUiEvent.PalpitesLotecaAtualizado event
    ) {
        if (event == null) {
            return;
        }

        int quantidade = event.getQuantidadePalpites();

        if (quantidade < 15 || quantidade > 26) {
            return;
        }

        // Detectar se houve aumento de palpites e se o aviso ainda nao foi
        // exibido nesta sessao de tela (evita popup repetido a cada clique).
        SimulaUiState estadoAnterior = getCurrentState();
        boolean jaExibiuNestaSessao = estadoAnterior != null
                && estadoAnterior.isAvisoAumentoPalpitesLotecaJaExibido();
        boolean aumentouPalpites = estadoAnterior != null
                && quantidade > estadoAnterior.getPalpitesLoteca()
                && !jaExibiuNestaSessao;

        updateState(state -> state.copy()
                .setPalpitesLoteca(quantidade)
                .setAvisouAumentoPalpitesLoteca(aumentouPalpites)
                .setAvisoAumentoPalpitesLotecaJaExibido(
                        jaExibiuNestaSessao || aumentouPalpites
                )
        );

        recalcularFluxoLotecaComEstadoAtual();
    }

    private void recalcularFluxoLotecaComEstadoAtual() {
        SimulaUiState atual = getCurrentState();

        if (atual == null) {
            return;
        }

        int qtdPartidasTotal =
                parametroJogoAtual != null
                        && parametroJogoAtual.getPartidas() != null
                        ? parametroJogoAtual.getPartidas().size()
                        : 0;

        updateState(state ->
                fluxoApostaReducer.cartelaAlterada(
                        state,
                        new SimulaUiEvent.CartelaAlterada(
                                ModalidadeEnum.LOTECA,
                                0,
                                state.getQtdDezenasPossiveisSelecionado(),
                                0,
                                state.getJogosLoteca(),
                                qtdPartidasTotal,
                                state.getDuplasLoteca(),
                                state.getTriplasLoteca(),
                                false,
                                false,
                                state.isEscolhaTimeCoracaoSurpresinha(),
                                false,
                                false,
                                false,
                                state.getDezenasSelecionadas()
                        )
                )
        );
    }

    private void handleResetarAvisoPalpitesLoteca() {
        updateState(state -> state.copy()
                .setAvisouAumentoPalpitesLoteca(false)
        );
    }

    @Override
    protected String obterTextoCompletar(
            SimulaUiState state
    ) {
        if (state == null) {
            return "Aposta aleatória";
        }

        boolean possuiTimesSelecionados =
                state.getPossuiAlteracoesPendentes() != null
                && state.getPossuiAlteracoesPendentes();

        return possuiTimesSelecionados
                ? "Completar jogo"
                : "Aposta aleatória";
    }
}

