package br.gov.caixa.loterias.apostas.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.event.SimulaUiEvent;
import br.gov.caixa.loterias.apostas.model.bean.SessaoUsuario;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroJogoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.CarrinhoSingleton;
import br.gov.caixa.loterias.apostas.model.sp.LoginSP;
import br.gov.caixa.loterias.apostas.state.ComandoCarrinho;
import br.gov.caixa.loterias.apostas.state.ComandoCompletarAposta;
import br.gov.caixa.loterias.apostas.state.SimulaUiState;
import br.gov.caixa.loterias.apostas.utils.BarraTituloDTO;

public class SimulaViewModel extends ViewModel {

    private final MutableLiveData<SimulaUiState> uiState =
            new MutableLiveData<>(SimulaUiState.initial());

    private IdentificaoDeUmaApostaDas8Modalidades apostaPendente;
    private BarraTituloDTO barraTituloPendente;

    private ModalidadeEnum tipoJogoAtual;
    private ParametroJogoDTO parametroJogoAtual;

    public LiveData<SimulaUiState> getUiState() {
        return uiState;
    }

    public void onEvent(
            SimulaUiEvent event
    ) {

        if (event instanceof SimulaUiEvent.TelaInicializada) {

            handleTelaInicializada(
                    (SimulaUiEvent.TelaInicializada) event
            );

            return;
        }

        if (event instanceof SimulaUiEvent.AdicionarCarrinhoSolicitado) {

            handleAdicionarCarrinhoSolicitado(
                    (SimulaUiEvent.AdicionarCarrinhoSolicitado) event
            );

            return;
        }

        if (event instanceof SimulaUiEvent.ConfirmarAdicionarCarrinho) {

            executarAdicionarCarrinhoPendente();

            return;
        }

        if (event instanceof SimulaUiEvent.ComandoCarrinhoConsumido) {

            limparComandoCarrinho();

            return;
        }

        if (event instanceof SimulaUiEvent.LimparApostaSolicitado) {

            limparAposta();

            return;
        }

        if (event instanceof SimulaUiEvent.CompletarApostaSolicitado) {

            handleCompletarApostaSolicitado(
                    (SimulaUiEvent.CompletarApostaSolicitado) event
            );

            return;
        }

        if (event instanceof SimulaUiEvent.ComandoCompletarApostaConsumido) {

            limparComandoCompletarAposta();

            return;
        }

        if (event instanceof SimulaUiEvent.LimpezaApostaRenderizada) {

            limparFlagsDeRenderizacao();
        }
    }
    private void handleCompletarTimemania(
            SimulaUiEvent.CompletarApostaSolicitado event
    ) {

        boolean apostaCompleta =
                event.getQtdDezenasSelecionadas()
                        == event.getQtdDezenasPossiveisSelecionado();

        if (event.isEscolhaTimeBtnAtivado()
                && apostaCompleta) {

            if (!event.isTelaSelecaoTimeAtivado()) {

                publicarComandoCompletar(
                        new ComandoCompletarAposta.MostrarEscolhaTimeCoracao()
                );

                return;
            }

            if (event.isEquipeSelecionadaValida()) {

                publicarComandoCompletar(
                        new ComandoCompletarAposta.ApostaProntaParaAdicionar(false)
                );

            } else {

                publicarComandoCompletar(
                        new ComandoCompletarAposta.SelecionarTimeAleatorio()
                );
            }

            return;
        }

        if (event.isEscolhaTimeCoracaoSurpresinha()) {

            publicarComandoCompletar(
                    new ComandoCompletarAposta.AdicionarTimeSurpresa()
            );

            return;
        }

        publicarComandoCompletar(
                new ComandoCompletarAposta.PreencherNumerosAleatorios()
        );
    }
    private void handleCompletarLoteca(
            SimulaUiEvent.CompletarApostaSolicitado event
    ) {

        boolean partidasCompletas =
                event.getQtdPartidasSelecionadas()
                        == event.getQtdPartidasTotal();

        boolean possuiDuploOuTriplo =
                event.getDuplosLoteca() > 0
                        || event.getTriplosLoteca() > 0;

        if (partidasCompletas && possuiDuploOuTriplo) {

            publicarComandoCompletar(
                    new ComandoCompletarAposta.ApostaProntaParaAdicionar(true)
            );

            return;
        }

        publicarComandoCompletar(
                new ComandoCompletarAposta.ZerarLoteca()
        );
    }
    private void handleCompletarLotogol(
            SimulaUiEvent.CompletarApostaSolicitado event
    ) {

        boolean partidasCompletas =
                event.getQtdPartidasSelecionadas()
                        == event.getQtdPartidasTotal();

        if (partidasCompletas) {

            publicarComandoCompletar(
                    new ComandoCompletarAposta.ApostaProntaParaAdicionar(false)
            );

            return;
        }

        publicarComandoCompletar(
                new ComandoCompletarAposta.PreencherNumerosAleatorios()
        );
    }
    private void publicarComandoCompletar(
            ComandoCompletarAposta comando
    ) {

        SimulaUiState atual =
                getCurrentState();

        uiState.setValue(
                atual.toBuilder()
                        .setComandoCompletarAposta(comando)
                        .build()
        );
    }

    private void limparComandoCompletarAposta() {

        SimulaUiState atual =
                uiState.getValue();

        if (atual == null) {
            return;
        }

        uiState.setValue(
                atual.toBuilder()
                        .setComandoCompletarAposta(null)
                        .build()
        );
    }
    private void handleCompletarApostaSolicitado(
            SimulaUiEvent.CompletarApostaSolicitado event
    ) {

        ModalidadeEnum tipoJogo = event.getTipoJogo();

        if (tipoJogo == ModalidadeEnum.TIMEMANIA) {

            handleCompletarTimemania(event);
            return;
        }

        if (tipoJogo == ModalidadeEnum.LOTECA) {

            handleCompletarLoteca(event);
            return;
        }

        if (tipoJogo == ModalidadeEnum.LOTOGOL) {

            handleCompletarLotogol(event);
            return;
        }

        if (event.getQtdDezenasSelecionadas()
                == event.getQtdDezenasPossiveisSelecionado()) {

            if (tipoJogo == ModalidadeEnum.DIA_DE_SORTE
                    && event.isFragmentCartela()) {

                publicarComandoCompletar(
                        new ComandoCompletarAposta.AbrirMesDeSorte()
                );

                return;
            }

            if (tipoJogo == ModalidadeEnum.MAIS_MILIONARIA
                    && event.isFragmentCartela()) {

                publicarComandoCompletar(
                        new ComandoCompletarAposta.AbrirTrevos()
                );

                return;
            }

            publicarComandoCompletar(
                    new ComandoCompletarAposta.ApostaProntaParaAdicionar(false)
            );

            return;
        }

        if (tipoJogo == ModalidadeEnum.SUPER_7) {

            if (event.getQtdTotalSelecionadosSuperSete()
                    == event.getQtdDezenasPossiveisSelecionado()) {

                publicarComandoCompletar(
                        new ComandoCompletarAposta.ApostaProntaParaAdicionar(false)
                );

            } else {

                publicarComandoCompletar(
                        new ComandoCompletarAposta.PreencherSuperSete()
                );
            }

            return;
        }

        publicarComandoCompletar(
                new ComandoCompletarAposta.PreencherNumerosAleatorios()
        );
    }

    private void handleTelaInicializada(
            SimulaUiEvent.TelaInicializada event
    ) {

        tipoJogoAtual = event.getModalidade();
        parametroJogoAtual = event.getParametroSimulacao();

        BarraTituloDTO barraTituloDTO =
                criarBarraTitulo(
                        parametroJogoAtual,
                        event.isEspecial()
                );

        boolean exibirAnimacao = false;
        int animacaoRes = 0;
        float velocidadeAnimacao = 1f;

        if (event.isEspecial()) {

            if (tipoJogoAtual == ModalidadeEnum.LOTECA) {

                exibirAnimacao = true;
                animacaoRes = R.raw.copa_loteca_3;
                velocidadeAnimacao = 1.75f;

            } else if (tipoJogoAtual == ModalidadeEnum.QUINA) {

                exibirAnimacao = true;
                animacaoRes = R.raw.quina_sao_joao_1;
                velocidadeAnimacao = 1.5f;
            }
        }

        int qtdMinima = 0;
        int qtdConcurso = 0;

        if (parametroJogoAtual != null) {

            if (parametroJogoAtual.getQuantidadeMinima() != null) {
                qtdMinima = parametroJogoAtual.getQuantidadeMinima();
            }

            if (parametroJogoAtual.getTeimosinhas() != null
                    && !parametroJogoAtual.getTeimosinhas().isEmpty()) {

                Integer primeiraTeimosinha =
                        parametroJogoAtual.getTeimosinhas().get(0);

                qtdConcurso =
                        primeiraTeimosinha != null
                                ? primeiraTeimosinha
                                : 0;
            }
        }

        SimulaUiState atual =
                getCurrentState();

        uiState.setValue(
                atual.toBuilder()
                        .setBarraTituloDTO(barraTituloDTO)
                        .setTipoJogo(tipoJogoAtual)
                        .setExibirAnimacao(exibirAnimacao)
                        .setAnimacaoRes(animacaoRes)
                        .setVelocidadeAnimacao(velocidadeAnimacao)
                        .setComandoCarrinho(null)
                        .setDezenasSelecionadas(new ArrayList<Integer>())
                        .setQtdDezenasPossiveisSelecionado(qtdMinima)
                        .setQtdConcursoSelecionado(qtdConcurso)
                        .setMostrarBotaoLimparAposta(false)
                        .setMostrarSalvarAposta(false)
                        .setMostrarRodape(false)
                        .setBotaoFinalizarVisivel(true)
                        .setTextoBotaoFinalizarResId(R.string.completarCartela)
                        .build()
        );
    }

    private void handleAdicionarCarrinhoSolicitado(
            SimulaUiEvent.AdicionarCarrinhoSolicitado event
    ) {

        if (event.isEscolhaTimeCoracaoSurpresinha()) {

            publicarComando(
                    new ComandoCarrinho.AdicionarTimeSurpresa(
                            event.getEquipeSelecionada()
                    )
            );

            return;
        }

        apostaPendente = event.getAposta();
        barraTituloPendente = getBarraTituloSafe(
                event.getBarraTituloDTO()
        );

        if (LoginSP.isLoginRealizado()
                && checaLimiteDiario(apostaPendente)) {

            publicarComando(
                    new ComandoCarrinho.MostrarConfirmacaoLimiteDiario()
            );

            return;
        }

        executarAdicionarCarrinhoPendente();
    }

    private void executarAdicionarCarrinhoPendente() {

        if (apostaPendente == null) {
            return;
        }

        if (barraTituloPendente == null) {
            barraTituloPendente = getBarraTituloSafe(null);
        }

        publicarComando(
                new ComandoCarrinho.ExecutarAdicionarCarrinho(
                        apostaPendente,
                        barraTituloPendente
                )
        );
    }

    private boolean checaLimiteDiario(
            IdentificaoDeUmaApostaDas8Modalidades aposta
    ) {

        if (CarrinhoSingleton.getInstance().getCarrinho() == null
                || CarrinhoSingleton.getInstance().getCarrinho().getValorTotal() == null
                || SessaoUsuario.getInstance().getParametrosSimulacao() == null
                || SessaoUsuario.getInstance().getParametrosSimulacao().getValorLimiteDiario() == null
                || aposta == null
                || aposta.getValor() == null) {

            return false;
        }

        BigDecimal valorAtualCarrinho =
                CarrinhoSingleton
                        .getInstance()
                        .getCarrinho()
                        .getValorTotal();

        BigDecimal valorLimiteDiario =
                SessaoUsuario
                        .getInstance()
                        .getParametrosSimulacao()
                        .getValorLimiteDiario();

        return valorAtualCarrinho
                .add(aposta.getValor())
                .compareTo(valorLimiteDiario) > 0;
    }

    private void publicarComando(
            ComandoCarrinho comandoCarrinho
    ) {

        SimulaUiState atual =
                getCurrentState();

        uiState.setValue(
                atual.toBuilder()
                        .setComandoCarrinho(comandoCarrinho)
                        .build()
        );
    }

    private void limparComandoCarrinho() {

        SimulaUiState atual =
                uiState.getValue();

        if (atual == null) {
            return;
        }

        uiState.setValue(
                atual.toBuilder()
                        .setComandoCarrinho(null)
                        .build()
        );
    }

    private void limparAposta() {

        SimulaUiState state =
                uiState.getValue();

        if (state == null) {
            return;
        }

        ModalidadeEnum tipoJogo =
                tipoJogoAtual != null
                        ? tipoJogoAtual
                        : state.getTipoJogo();

        SimulaUiState.Builder builder =
                state.toBuilder()
                        .setMostrarSalvarAposta(false)
                        .setMostrarRodape(false)
                        .setMostrarBotaoLimparAposta(false)
                        .setLimparOpcaoOutrosNumeros(true)
                        .setBotaoFinalizarVisivel(true)
                        .setTextoBotaoFinalizarResId(R.string.completarCartela);

        if (tipoJogo == ModalidadeEnum.LOTECA) {

            builder
                    .setLimparLoteca(true)
                    .setJogosLoteca(0)
                    .setSimplesLoteca(0)
                    .setDuplasLoteca(0)
                    .setTriplasLoteca(0)
                    .setBotaoFinalizarVisivel(false)
                    .setTextoBotaoFinalizarResId(R.string.label_selecionar_times_aleatoriamente);

        } else if (tipoJogo == ModalidadeEnum.LOTOGOL) {

            builder
                    .setLimparLotogol(true)
                    .setBotaoFinalizarVisivel(false)
                    .setTextoBotaoFinalizarResId(R.string.label_complete_placar_aleatoriamente);

        } else if (tipoJogo == ModalidadeEnum.TIMEMANIA
                && state.isTelaSelecaoTimeAtivado()) {

            builder
                    .setEquipeSelecionada(null)
                    .setLimparTimeCoracao(true)
                    .setMostrarBotaoLimparAposta(false)
                    .setMostrarSalvarAposta(false)
                    .setTextoBotaoFinalizarResId(R.string.adicionarAoCarrinho);

        } else if (tipoJogo == ModalidadeEnum.SUPER_7) {

            int qtdMinima =
                    getQuantidadeMinimaSafe();

            builder
                    .setLimparSuperSete(true)
                    .setQtdTotalSelecionadosSuperSete(0)
                    .setQtdDezenasPossiveisSelecionado(qtdMinima)
                    .setMostrarBotaoLimparAposta(false)
                    .setMostrarSalvarAposta(false)
                    .setTextoBotaoFinalizarResId(R.string.completarCartela);

        } else {

            builder
                    .setDezenasSelecionadas(new ArrayList<Integer>())
                    .setLimparNumerosCartela(true)
                    .setMostrarBotaoLimparAposta(false)
                    .setMostrarSalvarAposta(false)
                    .setTextoBotaoFinalizarResId(R.string.completarCartela);
        }

        uiState.setValue(
                builder.build()
        );
    }

    private void limparFlagsDeRenderizacao() {

        SimulaUiState state =
                uiState.getValue();

        if (state == null) {
            return;
        }

        uiState.setValue(
                state.toBuilder()
                        .setLimparNumerosCartela(false)
                        .setLimparTimeCoracao(false)
                        .setLimparSuperSete(false)
                        .setLimparLoteca(false)
                        .setLimparLotogol(false)
                        .setLimparOpcaoOutrosNumeros(false)
                        .build()
        );
    }

    private BarraTituloDTO criarBarraTitulo(
            ParametroJogoDTO parametro,
            boolean especial
    ) {

        BarraTituloDTO dto =
                new BarraTituloDTO();

        dto.setEspecial(especial);

        if (parametro != null
                && parametro.getConcurso() != null) {

            dto.setNumeroConcurso(
                    String.valueOf(
                            parametro
                                    .getConcurso()
                                    .getNumero()
                    )
            );

            dto.setDataSorteio(
                    formatarDataSorteio(parametro)
            );
        }

        return dto;
    }

    private String formatarDataSorteio(
            ParametroJogoDTO parametro
    ) {

        try {

            if (parametro == null
                    || parametro.getConcurso() == null
                    || parametro.getConcurso().getDataHoraSorteio() == null) {

                return "";
            }

            DateFormat entrada =
                    new SimpleDateFormat(
                            "dd/MM/yyyy HH:mm:ss",
                            Locale.getDefault()
                    );

            DateFormat saida =
                    new SimpleDateFormat(
                            "dd/MM",
                            Locale.getDefault()
                    );

            Date data =
                    entrada.parse(
                            parametro
                                    .getConcurso()
                                    .getDataHoraSorteio()
                    );

            if (data == null) {
                return "";
            }

            return saida.format(data);

        } catch (Exception e) {
            return "";
        }
    }

    private SimulaUiState getCurrentState() {

        SimulaUiState atual =
                uiState.getValue();

        if (atual == null) {
            return SimulaUiState.initial();
        }

        return atual;
    }

    private BarraTituloDTO getBarraTituloSafe(
            BarraTituloDTO barraTituloDTO
    ) {

        if (barraTituloDTO != null) {
            return barraTituloDTO;
        }

        SimulaUiState state =
                uiState.getValue();

        if (state != null
                && state.getBarraTituloDTO() != null) {

            return state.getBarraTituloDTO();
        }

        return new BarraTituloDTO();
    }

    private int getQuantidadeMinimaSafe() {

        if (parametroJogoAtual == null
                || parametroJogoAtual.getQuantidadeMinima() == null) {

            return 0;
        }

        return parametroJogoAtual.getQuantidadeMinima();
    }
}