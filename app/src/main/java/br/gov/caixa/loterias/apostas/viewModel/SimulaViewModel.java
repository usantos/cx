package br.gov.caixa.loterias.apostas.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.VolleyError;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.event.SimulaUiEvent;
import br.gov.caixa.loterias.apostas.model.bean.SessaoUsuario;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroJogoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.CarrinhoSingleton;
import br.gov.caixa.loterias.apostas.model.dao.DBLoteriasCrud;
import br.gov.caixa.loterias.apostas.model.model.AccordionModel;
import br.gov.caixa.loterias.apostas.model.sp.LoginSP;
import br.gov.caixa.loterias.apostas.state.ComandoCarrinho;
import br.gov.caixa.loterias.apostas.state.ComandoCompletarAposta;
import br.gov.caixa.loterias.apostas.state.SimulaUiState;
import br.gov.caixa.loterias.apostas.utils.BarraTituloDTO;
import br.gov.caixa.loterias.apostas.utils.ServicoFactoryUtil;

public class SimulaViewModel extends AndroidViewModel {

    private final MutableLiveData<SimulaUiState> uiState =
            new MutableLiveData<>(SimulaUiState.initial());

    private IdentificaoDeUmaApostaDas8Modalidades apostaPendente;
    private BarraTituloDTO barraTituloPendente;

    private ModalidadeEnum tipoJogoAtual;
    private ParametroJogoDTO parametroJogoAtual;
    private boolean isEspecialAtual;
    public SimulaViewModel(
            @NonNull Application application
    ) {
        super(application);
    }

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

        if (event instanceof SimulaUiEvent.CompletarApostaClicado) {

            handleCompletarApostaClicado(
                    (SimulaUiEvent.CompletarApostaClicado) event
            );

            return;
        }

        if (event instanceof SimulaUiEvent.AdicionarCarrinhoClicado) {

            handleAdicionarCarrinhoClicado(
                    (SimulaUiEvent.AdicionarCarrinhoClicado) event
            );

            return;
        }

        if (event instanceof SimulaUiEvent.ComandoCompletarApostaConsumido) {

            limparComandoCompletarAposta();

            return;
        }

        if (event instanceof SimulaUiEvent.CartelaAlterada) {

            handleCartelaAlterada(
                    (SimulaUiEvent.CartelaAlterada) event
            );

            return;
        }
        if (event instanceof SimulaUiEvent.ValorApostaAlterado) {

            handleValorApostaAlterado(
                    (SimulaUiEvent.ValorApostaAlterado) event
            );

            return;
        }

        if (event instanceof SimulaUiEvent.CarregarCarrinho) {
            carregarCarrinho();
            return;
        }
        if (event instanceof SimulaUiEvent.ComoJogarClicado) {

            handleComoJogar();

            return;
        }

        if (event instanceof SimulaUiEvent.DialogComoJogarConsumido) {

            consumirDialogComoJogar();

            return;
        }

        if (event instanceof SimulaUiEvent.LimpezaApostaRenderizada) {

            limparFlagsDeRenderizacao();;
        }
        if (event instanceof SimulaUiEvent.InicializarTeimosinhas) {
            inicializarTeimosinhas((SimulaUiEvent.InicializarTeimosinhas) event);
            return;
        }

        if (event instanceof SimulaUiEvent.ClicarTeimosinhas) {
            clicarTeimosinhas();
            return;
        }

        if (event instanceof SimulaUiEvent.ConfirmarTeimosinha) {
            confirmarTeimosinha((SimulaUiEvent.ConfirmarTeimosinha) event);
            return;
        }

        if (event instanceof SimulaUiEvent.ConsumirEventoUnico) {
            consumirEventoUnico();
            return;
        }

        if (event instanceof SimulaUiEvent.ValorApostaAtualizado) {
            valorApostaAtualizado();
            return;
        }
        if (event instanceof SimulaUiEvent.InicializarQuantidadeNumeros) {
            inicializarQuantidadeNumeros(
                    (SimulaUiEvent.InicializarQuantidadeNumeros) event
            );
            return;
        }

        if (event instanceof SimulaUiEvent.ClicarQuantidadeNumeros) {
            clicarQuantidadeNumeros();
            return;
        }

        if (event instanceof SimulaUiEvent.SelecionarQuantidadeNumeros) {
            selecionarQuantidadeNumeros(
                    (SimulaUiEvent.SelecionarQuantidadeNumeros) event
            );
            return;
        }

        if (event instanceof SimulaUiEvent.ConfirmarQuantidadeNumeros) {
            confirmarQuantidadeNumeros(
                    (SimulaUiEvent.ConfirmarQuantidadeNumeros) event
            );
            return;
        }

        if (event instanceof SimulaUiEvent.DialogQuantidadeNumerosConsumido) {
            consumirDialogQuantidadeNumeros();
            return;
        }

        if (event instanceof SimulaUiEvent.AvisoQuantidadeNumerosConsumido) {
            consumirAvisoQuantidadeNumeros();
            return;
        }

        if (event instanceof SimulaUiEvent.ValorApostaQuantidadeNumerosAtualizado) {
            valorApostaQuantidadeNumerosAtualizado();
            return;
        }
    }
    private void inicializarQuantidadeNumeros(
            SimulaUiEvent.InicializarQuantidadeNumeros event
    ) {
        SimulaUiState state = getState();

        int qtdSelecionada =
                event.getQtdDezenasSelecionadaInicial();

        String textoBotao =
                montarTextoQuantidadeNumeros(qtdSelecionada);

        uiState.setValue(
                state.toBuilder()
                        .setLabelsQuantidadeNumeros(
                                event.getLabelsQuantidadeNumeros()
                        )
                        .setQtdPrognosticosQuantidadeNumeros(
                                event.getQtdPrognosticosQuantidadeNumeros()
                        )
                        .setQtdDezenasPossiveisSelecionado(qtdSelecionada)
                        .setTextoBotaoPrognosticosSelecionado(textoBotao)
                        .setPosicaoQuantidadeNumerosSelecionada(0)
                        .setExibirDialogQuantidadeNumeros(false)
                        .setExibirAvisoQuantidadeNumerosMaxima(false)
                        .setAtualizarValorApostaPorQuantidadeNumeros(false)
                        .setResetarTrevosPorQuantidadeNumeros(false)
                        .build()
        );
    }

    private void clicarQuantidadeNumeros() {
        SimulaUiState state = getState();

        uiState.setValue(
                state.toBuilder()
                        .setExibirDialogQuantidadeNumeros(true)
                        .setExibirAvisoQuantidadeNumerosMaxima(false)
                        .setAtualizarValorApostaPorQuantidadeNumeros(false)
                        .setResetarTrevosPorQuantidadeNumeros(false)
                        .build()
        );
    }

    private void selecionarQuantidadeNumeros(
            SimulaUiEvent.SelecionarQuantidadeNumeros event
    ) {
        aplicarQuantidadeNumerosSelecionada(
                event.getPosition(),
                false
        );
    }

    private void confirmarQuantidadeNumeros(
            SimulaUiEvent.ConfirmarQuantidadeNumeros event
    ) {
        aplicarQuantidadeNumerosSelecionada(
                event.getPosition(),
                true
        );
    }

    private void aplicarQuantidadeNumerosSelecionada(
            int position,
            boolean confirmar
    ) {
        SimulaUiState state = getState();

        List<Integer> prognosticos =
                state.getQtdPrognosticosQuantidadeNumeros();

        if (prognosticos == null || prognosticos.isEmpty()) {
            return;
        }

        int posicaoValida = position;

        if (posicaoValida < 0 || posicaoValida >= prognosticos.size()) {
            posicaoValida = state.getPosicaoQuantidadeNumerosSelecionada();
        }

        if (posicaoValida < 0 || posicaoValida >= prognosticos.size()) {
            return;
        }

        Integer qtdPrognosticos =
                prognosticos.get(posicaoValida);

        if (qtdPrognosticos == null) {
            return;
        }

        String textoBotao =
                montarTextoQuantidadeNumeros(qtdPrognosticos);

        boolean deveExibirAviso =
                posicaoValida >= 1
                        && !state.isAvisouQuantidadeNumerosMaxima();

        SimulaUiState.Builder builder =
                state.toBuilder()
                        .setPosicaoQuantidadeNumerosSelecionada(posicaoValida)
                        .setQtdDezenasPossiveisSelecionado(qtdPrognosticos)
                        .setTextoBotaoPrognosticosSelecionado(textoBotao)
                        .setExibirAvisoQuantidadeNumerosMaxima(deveExibirAviso)
                        .setAvisouQuantidadeNumerosMaxima(
                                state.isAvisouQuantidadeNumerosMaxima()
                                        || deveExibirAviso
                        );

        if (confirmar) {
            builder
                    .setExibirDialogQuantidadeNumeros(false)
                    .setAtualizarValorApostaPorQuantidadeNumeros(true)
                    .setResetarTrevosPorQuantidadeNumeros(
                            tipoJogoAtual == ModalidadeEnum.MAIS_MILIONARIA
                    );
        }

        uiState.setValue(
                builder.build()
        );
    }

    private String montarTextoQuantidadeNumeros(
            int qtdPrognosticos
    ) {
        return qtdPrognosticos
                + getApplication()
                .getString(R.string.espaco_numeros);
    }

    private void consumirDialogQuantidadeNumeros() {
        SimulaUiState state = getState();

        uiState.setValue(
                state.toBuilder()
                        .setExibirDialogQuantidadeNumeros(false)
                        .build()
        );
    }

    private void consumirAvisoQuantidadeNumeros() {
        SimulaUiState state = getState();

        uiState.setValue(
                state.toBuilder()
                        .setExibirAvisoQuantidadeNumerosMaxima(false)
                        .build()
        );
    }

    private void valorApostaQuantidadeNumerosAtualizado() {
        SimulaUiState state = getState();

        uiState.setValue(
                state.toBuilder()
                        .setAtualizarValorApostaPorQuantidadeNumeros(false)
                        .setResetarTrevosPorQuantidadeNumeros(false)
                        .build()
        );
    }

    private void inicializarTeimosinhas(SimulaUiEvent.InicializarTeimosinhas event) {
        SimulaUiState state = getState();

        String textoBotao = event.getTextoInicialTeimosinha();

        List<String> labels = event.getQntTeimosinhas();
        List<Integer> concursos = event.getQntConcursos();

        if (event.getQntConcursoSelecionado() != 0 && labels != null && concursos != null) {
            for (int i = 0; i < concursos.size(); i++) {
                if (concursos.get(i) == event.getQntConcursoSelecionado() && i < labels.size()) {
                    textoBotao = labels.get(i);
                    break;
                }
            }
        }

        uiState.setValue(
                state.toBuilder()
                        .setLabelsTeimosinhas(labels)
                        .setConcursosTeimosinhas(concursos)
                        .setQtdConcursoSelecionado(event.getQntConcursoSelecionado())
                        .setEspecial(event.isEspecial())
                        .setTextoBotaoTeimosinhas(textoBotao)
                        .setExibirDialogTeimosinha(false)
                        .setExibirDialogSemTeimosinha(false)
                        .setAtualizarValorApostaPorTeimosinha(false)
                        .build()
        );
    }

    private void clicarTeimosinhas() {
        SimulaUiState state = getState();

        if (state.isEspecial()) {
            uiState.setValue(
                    state.toBuilder()
                            .setExibirDialogTeimosinha(false)
                            .setExibirDialogSemTeimosinha(true)
                            .setAtualizarValorApostaPorTeimosinha(false)
                            .build()
            );
            return;
        }

        uiState.setValue(
                state.toBuilder()
                        .setExibirDialogTeimosinha(true)
                        .setExibirDialogSemTeimosinha(false)
                        .setAtualizarValorApostaPorTeimosinha(false)
                        .build()
        );
    }

    private void confirmarTeimosinha(SimulaUiEvent.ConfirmarTeimosinha event) {
        SimulaUiState state = getState();

        int position = event.getPosition();

        if (state.getConcursosTeimosinhas() == null || position < 0 || position >= state.getConcursosTeimosinhas().size()) {
            return;
        }

        int qtdConcursoSelecionado = state.getConcursosTeimosinhas().get(position);

        String textoBotao = state.getTextoBotaoTeimosinhas();

        if (state.getLabelsTeimosinhas() != null && position < state.getLabelsTeimosinhas().size()) {
            textoBotao = state.getLabelsTeimosinhas().get(position);
        }

        uiState.setValue(
                state.toBuilder()
                        .setQtdConcursoSelecionado(qtdConcursoSelecionado)
                        .setTextoBotaoTeimosinhas(textoBotao)
                        .setExibirDialogTeimosinha(false)
                        .setExibirDialogSemTeimosinha(false)
                        .setAtualizarValorApostaPorTeimosinha(true)
                        .build()
        );
    }

    private void consumirEventoUnico() {
        SimulaUiState state = getState();

        uiState.setValue(
                state.toBuilder()
                        .setExibirDialogTeimosinha(false)
                        .setExibirDialogSemTeimosinha(false)
                        .build()
        );
    }

    private void valorApostaAtualizado() {
        SimulaUiState state = getState();

        uiState.setValue(
                state.toBuilder()
                        .setAtualizarValorApostaPorTeimosinha(false)
                        .build()
        );
    }

    private SimulaUiState getState() {
        SimulaUiState state = uiState.getValue();
        return state != null ? state : SimulaUiState.initial();
    }

    private void handleComoJogar() {

        SimulaUiState atual =
                getCurrentState();

        uiState.setValue(
                atual.toBuilder()
                        .setMensagemComoJogar(
                                obterTextoComoJogar(tipoJogoAtual,isEspecialAtual)
                        )
                        .setTitleComoJogar(obterTitleComoJogar(tipoJogoAtual,isEspecialAtual))
                        .setExibirDialogComoJogar(true)
                        .build()
        );
    }
    public static String obterTextoComoJogar(
            ModalidadeEnum modalidade,
            boolean especial
    ) {
        return new AccordionModel(
                modalidade,
                especial
        ).getBodyText();
    }

    public static String obterTitleComoJogar(
            ModalidadeEnum modalidade,
            boolean especial
    ) {
        return new AccordionModel(
                modalidade,
                especial
        ).getTituloComoJogar(modalidade);
    }

    private void consumirDialogComoJogar() {

        SimulaUiState atual =
                getCurrentState();

        uiState.setValue(
                atual.toBuilder()
                        .setExibirDialogComoJogar(false)
                        .build()
        );
    }

    private void carregarCarrinho() {

        publicarEstadoCarrinho(
                CarrinhoSingleton
                        .getInstance()
                        .getCarrinho(),
                true
        );

        if (LoginSP.isLoginRealizado()) {
            carregarCarrinhoOnline();
            return;
        }

        carregarCarrinhoLocal();
    }

    private void carregarCarrinhoOnline() {

        ServicoFactoryUtil
                .getApostaService()
                .buscaCarrinho(
                        new RequestListener<CarrinhoDTOResponse>() {

                            @Override
                            public void onResponse(
                                    CarrinhoDTOResponse response
                            ) {

                                CarrinhoDTO carrinho =
                                        response != null
                                                ? response.getPayload()
                                                : null;

                                if (carrinho == null) {
                                    carrinho = new CarrinhoDTO();
                                }

                                adicionarApostasLocaisNoCarrinho(
                                        carrinho
                                );

                                CarrinhoSingleton
                                        .getInstance()
                                        .setCarrinho(carrinho);

                                publicarEstadoCarrinho(
                                        carrinho,
                                        false
                                );
                            }

                            @Override
                            public void onErrorResponse(
                                    VolleyError error
                            ) {

                                carregarCarrinhoLocal();
                            }
                        }
                );
    }

    private void carregarCarrinhoLocal() {

        CarrinhoDTO carrinho =
                new CarrinhoDTO();

        carrinho.setApostas(
                getApostasLocais()
        );

        CarrinhoSingleton
                .getInstance()
                .setCarrinho(carrinho);

        publicarEstadoCarrinho(
                carrinho,
                false
        );
    }

    private void adicionarApostasLocaisNoCarrinho(
            CarrinhoDTO carrinho
    ) {

        List<IdentificaoDeUmaApostaDas8Modalidades> apostasLocais =
                getApostasLocais();

        if (apostasLocais == null
                || apostasLocais.isEmpty()) {
            return;
        }

        if (carrinho.getApostas() == null) {
            carrinho.setApostas(
                    new ArrayList<IdentificaoDeUmaApostaDas8Modalidades>()
            );
        }

        carrinho
                .getApostas()
                .addAll(apostasLocais);
    }

    private List<IdentificaoDeUmaApostaDas8Modalidades> getApostasLocais() {

        DBLoteriasCrud crud =
                new DBLoteriasCrud(
                        getApplication()
                );

        return crud
                .readAllIdentificaoDeUmaApostaDas8Modalidades();
    }

    private void publicarEstadoCarrinho(
            CarrinhoDTO carrinho,
            boolean carregando
    ) {

        BigDecimal valorCarrinho =
                obterValorCarrinho(carrinho);

        int quantidadeApostas =
                obterQuantidadeApostas(carrinho);

        boolean exibirCarrinho =
                valorCarrinho.compareTo(BigDecimal.ZERO) > 0;

        uiState.setValue(
                getCurrentState()
                        .toBuilder()
                        .setCarrinho(carrinho)
                        .setValorCarrinho(valorCarrinho)
                        .setQuantidadeApostasCarrinho(quantidadeApostas)
                        .setExibirCarrinho(exibirCarrinho)
                        .setCarregandoCarrinho(carregando)
                        .build()
        );
    }

    private BigDecimal obterValorCarrinho(
            CarrinhoDTO carrinho
    ) {

        if (carrinho == null) {
            return BigDecimal.ZERO;
        }

        if (carrinho.getValorTotal() != null
                && carrinho.getValorTotal().compareTo(BigDecimal.ZERO) > 0) {

            return carrinho.getValorTotal();
        }

        return somarValorDasApostas(
                carrinho
        );
    }

    private BigDecimal somarValorDasApostas(
            CarrinhoDTO carrinho
    ) {

        if (carrinho == null
                || carrinho.getApostas() == null
                || carrinho.getApostas().isEmpty()) {

            return BigDecimal.ZERO;
        }

        BigDecimal total =
                BigDecimal.ZERO;

        for (IdentificaoDeUmaApostaDas8Modalidades aposta
                : carrinho.getApostas()) {

            if (aposta != null
                    && aposta.getValor() != null) {

                total =
                        total.add(
                                aposta.getValor()
                        );
            }
        }

        return total;
    }

    private int obterQuantidadeApostas(
            CarrinhoDTO carrinho
    ) {

        if (carrinho == null
                || carrinho.getApostas() == null) {

            return 0;
        }

        return carrinho
                .getApostas()
                .size();
    }

    private void handleCartelaAlterada(
            SimulaUiEvent.CartelaAlterada event
    ) {

        boolean botaoLimparHabilitado =
                calcularBotaoLimparHabilitado(event);

        boolean botaoAdicionarHabilitado =
                calcularBotaoAdicionarHabilitado(event);

        boolean botaoCompletarHabilitado =
                calcularBotaoCompletarHabilitado(
                        event,
                        botaoAdicionarHabilitado
                );

        boolean mostrarSalvarAposta =
                calcularMostrarSalvarAposta(event);

        boolean mostrarRodape =
                botaoLimparHabilitado || mostrarSalvarAposta;

        String textoSelecionados =
                montarTextoSelecionados(
                        event.getQtdDezenasSelecionadas(),
                        event.getQtdDezenasPossiveisSelecionado()
                );

        SimulaUiState atual =
                getCurrentState();

        uiState.setValue(
                atual.toBuilder()
                        .setBotaoCompletarHabilitado(botaoCompletarHabilitado)
                        .setBotaoAdicionarHabilitado(botaoAdicionarHabilitado)
                        .setBotaoLimparHabilitado(botaoLimparHabilitado)
                        .setMostrarBotaoLimparAposta(botaoLimparHabilitado)
                        .setMostrarSalvarAposta(mostrarSalvarAposta)
                        .setMostrarRodape(mostrarRodape)
                        .setTextoSelecionados(textoSelecionados)
                        .build()
        );
    }
    private String montarTextoSelecionados(
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
    private boolean calcularBotaoLimparHabilitado(
            SimulaUiEvent.CartelaAlterada event
    ) {

        ModalidadeEnum tipoJogo =
                event.getTipoJogo();

        if (tipoJogo == ModalidadeEnum.LOTECA) {
            return event.getQtdPartidasSelecionadas() > 0;
        }

        if (tipoJogo == ModalidadeEnum.LOTOGOL) {
            return event.isPossuiPlacarLotogol();
        }

        if (tipoJogo == ModalidadeEnum.TIMEMANIA) {

            if (event.isTelaSelecaoTimeAtivado()) {
                return event.isEquipeSelecionadaValida();
            }

            return event.getQtdDezenasSelecionadas() > 0;
        }

        if (tipoJogo == ModalidadeEnum.SUPER_7) {
            return event.getQtdTotalSelecionadosSuperSete() > 0;
        }

        return event.getQtdDezenasSelecionadas() > 0;
    }

    private boolean calcularBotaoAdicionarHabilitado(
            SimulaUiEvent.CartelaAlterada event
    ) {

        ModalidadeEnum tipoJogo =
                event.getTipoJogo();

        if (event.isEscolhaTimeCoracaoSurpresinha()) {
            return event.isEquipeSelecionadaValida();
        }

        if (tipoJogo == ModalidadeEnum.DIA_DE_SORTE
                || tipoJogo == ModalidadeEnum.MAIS_MILIONARIA) {

            return false;
        }

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

            return partidasCompletas && possuiDuploOuTriplo;
        }

        if (tipoJogo == ModalidadeEnum.LOTOGOL) {

            return event.getQtdPartidasSelecionadas()
                    == event.getQtdPartidasTotal();
        }

        if (tipoJogo == ModalidadeEnum.TIMEMANIA) {

            boolean dezenasCompletas =
                    event.getQtdDezenasSelecionadas()
                            == event.getQtdDezenasPossiveisSelecionado();

            return dezenasCompletas
                    && event.isTelaSelecaoTimeAtivado()
                    && event.isEquipeSelecionadaValida();
        }

        return event.getQtdDezenasSelecionadas()
                == event.getQtdDezenasPossiveisSelecionado();
    }

    private boolean calcularBotaoCompletarHabilitado(
            SimulaUiEvent.CartelaAlterada event,
            boolean botaoAdicionarHabilitado
    ) {

        ModalidadeEnum tipoJogo =
                event.getTipoJogo();

        if (botaoAdicionarHabilitado) {
            return false;
        }

        if (tipoJogo == ModalidadeEnum.DIA_DE_SORTE
                || tipoJogo == ModalidadeEnum.MAIS_MILIONARIA) {

            return event.getQtdDezenasSelecionadas()
                    == event.getQtdDezenasPossiveisSelecionado();
        }

        if (tipoJogo == ModalidadeEnum.TIMEMANIA) {

            boolean dezenasCompletas =
                    event.getQtdDezenasSelecionadas()
                            == event.getQtdDezenasPossiveisSelecionado();

            if (dezenasCompletas && !event.isTelaSelecaoTimeAtivado()) {
                return true;
            }

            if (event.isTelaSelecaoTimeAtivado()
                    && !event.isEquipeSelecionadaValida()) {
                return true;
            }

            return !dezenasCompletas;
        }

        return true;
    }

    private boolean calcularMostrarSalvarAposta(
            SimulaUiEvent.CartelaAlterada event
    ) {

        ModalidadeEnum tipoJogo =
                event.getTipoJogo();

        if (tipoJogo == ModalidadeEnum.LOTECA
                || tipoJogo == ModalidadeEnum.LOTOGOL
                || tipoJogo == ModalidadeEnum.DIA_DE_SORTE
                || tipoJogo == ModalidadeEnum.MAIS_MILIONARIA) {

            return false;
        }

        if (tipoJogo == ModalidadeEnum.TIMEMANIA) {
            return event.isTelaSelecaoTimeAtivado()
                    && event.isEquipeSelecionadaValida();
        }

        if (tipoJogo == ModalidadeEnum.SUPER_7) {
            return event.getQtdTotalSelecionadosSuperSete()
                    == event.getQtdDezenasPossiveisSelecionado();
        }

        return event.getQtdDezenasSelecionadas()
                == event.getQtdDezenasPossiveisSelecionado();
    }
    private void handleValorApostaAlterado(
            SimulaUiEvent.ValorApostaAlterado event
    ) {

        BigDecimal valor =
                event.getValor();

        uiState.setValue(
                getCurrentState()
                        .toBuilder()
                        .setValorAposta(valor)
                        .setValorGrande(
                                valor != null
                                        && valor.doubleValue() > 999.99
                        )
                        .build()
        );
    }

    private void handleCompletarApostaClicado(
            SimulaUiEvent.CompletarApostaClicado event
    ) {

        ModalidadeEnum tipoJogo = event.getTipoJogo();

        if (tipoJogo == ModalidadeEnum.SUPER_7) {

            publicarComandoCompletar(
                    new ComandoCompletarAposta.PreencherSuperSete()
            );

            return;
        }

        if (tipoJogo == ModalidadeEnum.TIMEMANIA) {

            boolean apostaCompleta =
                    event.getQtdDezenasSelecionadas()
                            == event.getQtdDezenasPossiveisSelecionado();

            if (apostaCompleta && !event.isTelaSelecaoTimeAtivado()) {

                publicarComandoCompletar(
                        new ComandoCompletarAposta.MostrarEscolhaTimeCoracao()
                );

                return;
            }

            if (event.isTelaSelecaoTimeAtivado()
                    && !event.isEquipeSelecionadaValida()) {

                publicarComandoCompletar(
                        new ComandoCompletarAposta.SelecionarTimeAleatorio()
                );

                return;
            }

            publicarComandoCompletar(
                    new ComandoCompletarAposta.PreencherNumerosAleatorios()
            );

            return;
        }

        if (tipoJogo == ModalidadeEnum.LOTECA) {

            publicarComandoCompletar(
                    new ComandoCompletarAposta.ZerarLoteca()
            );

            return;
        }

        if (tipoJogo == ModalidadeEnum.LOTOGOL) {

            publicarComandoCompletar(
                    new ComandoCompletarAposta.PreencherNumerosAleatorios()
            );

            return;
        }

        publicarComandoCompletar(
                new ComandoCompletarAposta.PreencherNumerosAleatorios()
        );
    }

    private void handleAdicionarCarrinhoClicado(
            SimulaUiEvent.AdicionarCarrinhoClicado event
    ) {

        ModalidadeEnum tipoJogo = event.getTipoJogo();

        if (event.isEscolhaTimeCoracaoSurpresinha()) {

            publicarComandoCompletar(
                    new ComandoCompletarAposta.AdicionarTimeSurpresa()
            );

            return;
        }

        if (!isApostaProntaParaAdicionar(event)) {

//            publicarComandoCompletar(
//                    new ComandoCompletarAposta.MostrarMensagem(
//                            R.string.completarCartela
//                    )
//            );

            return;
        }

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
        habilitarModoAdicionar();
        publicarComandoCompletar(
                new ComandoCompletarAposta.ApostaProntaParaAdicionar(false)
        );
    }
    private boolean isApostaProntaParaAdicionar(
            SimulaUiEvent.AdicionarCarrinhoClicado event
    ) {

        ModalidadeEnum tipoJogo = event.getTipoJogo();

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

            return partidasCompletas && possuiDuploOuTriplo;
        }

        if (tipoJogo == ModalidadeEnum.LOTOGOL) {
            return event.getQtdPartidasSelecionadas()
                    == event.getQtdPartidasTotal();
        }

        if (tipoJogo == ModalidadeEnum.TIMEMANIA) {

            boolean dezenasCompletas =
                    event.getQtdDezenasSelecionadas()
                            == event.getQtdDezenasPossiveisSelecionado();

            return dezenasCompletas
                    && event.isEquipeSelecionadaValida();
        }

        return event.getQtdDezenasSelecionadas()
                == event.getQtdDezenasPossiveisSelecionado();
    }

    private void publicarComandoCompletar(
            ComandoCompletarAposta comando
    ) {

        SimulaUiState atual =
                getCurrentState();

        uiState.setValue(
                atual.toBuilder()
                        .setComandoCompletarAposta(comando)
                        .setMostrarBotaoLimparAposta(true)
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


    private void handleTelaInicializada(
            SimulaUiEvent.TelaInicializada event
    ) {

        tipoJogoAtual = event.getModalidade();
        parametroJogoAtual = event.getParametroSimulacao();
        isEspecialAtual = event.isEspecial();

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
        BigDecimal valorInicialAposta =
                obterValorApostaPorParametros(
                        qtdMinima,
                        qtdConcurso
                );

        SimulaUiState atual =
                getCurrentState();

        uiState.setValue(
                atual.toBuilder()
                        .setBarraTituloDTO(barraTituloDTO)
                        .setTipoJogo(tipoJogoAtual)
                        .setExibirAnimacao(exibirAnimacao)
                        .setAnimacaoRes(animacaoRes)
                        .setVelocidadeAnimacao(velocidadeAnimacao)
                        .setValorAposta(valorInicialAposta)
                        .setTextoSelecionados("Selecione os números:")
                        .setValorGrande(
                                valorInicialAposta.doubleValue() > 999.99
                        )
                        .setComandoCarrinho(null)
                        .setDezenasSelecionadas(new ArrayList<Integer>())
                        .setQtdDezenasPossiveisSelecionado(qtdMinima)
                        .setQtdConcursoSelecionado(qtdConcurso)
                        .setMostrarBotaoLimparAposta(false)
                        .setBotaoCompletarHabilitado(true)
                        .setBotaoAdicionarHabilitado(false)
                        .setBotaoLimparHabilitado(false)
                        .setMostrarSalvarAposta(false)
                        .setMostrarRodape(false)
                        .setBotaoCompletarVisivel(true)
                        .setTextoBotaoFinalizarResId(R.string.completarBotao)
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

        int qtdNumerosAtual =
                state.getQtdDezenasPossiveisSelecionado() > 0
                        ? state.getQtdDezenasPossiveisSelecionado()
                        : getQuantidadeMinimaSafe();

        int qtdConcursoAtual =
                state.getQtdConcursoSelecionado();

        BigDecimal valorApostaAtual =
                obterValorApostaPorParametros(
                        qtdNumerosAtual,
                        qtdConcursoAtual
                );

        SimulaUiState.Builder builder =
                state.toBuilder()
                        .setMostrarSalvarAposta(false)
                        .setMostrarRodape(false)
                        .setMostrarBotaoLimparAposta(false)
                        .setTextoSelecionados("Selecione os números:")
                        .setBotaoCompletarHabilitado(true)
                        .setBotaoAdicionarHabilitado(false)
                        .setBotaoLimparHabilitado(false)
                        .setValorAposta(valorApostaAtual)
                        .setValorGrande(
                                isValorGrande(valorApostaAtual)
                        )
                        .setLimparOpcaoOutrosNumeros(true)
                        .setBotaoCompletarVisivel(true)
                        .setTextoBotaoFinalizarResId(
                                R.string.completarBotao
                        );

        if (tipoJogo == ModalidadeEnum.LOTECA) {

            builder
                    .setLimparLoteca(true)
                    .setJogosLoteca(0)
                    .setSimplesLoteca(0)
                    .setDuplasLoteca(0)
                    .setTriplasLoteca(0)
                    .setBotaoCompletarVisivel(false)
                    .setTextoBotaoFinalizarResId(R.string.label_selecionar_times_aleatoriamente);

        } else if (tipoJogo == ModalidadeEnum.LOTOGOL) {

            builder
                    .setLimparLotogol(true)
                    .setBotaoCompletarVisivel(false)
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

            builder
                    .setLimparSuperSete(true)
                    .setQtdTotalSelecionadosSuperSete(0)
                    .setQtdDezenasPossiveisSelecionado(qtdNumerosAtual)
                    .setMostrarBotaoLimparAposta(false)
                    .setMostrarSalvarAposta(false)
                    .setTextoBotaoFinalizarResId(R.string.completarCartela);

        } else {

            builder
                    .setDezenasSelecionadas(new ArrayList<Integer>())
                    .setLimparNumerosCartela(true)
                    .setMostrarBotaoLimparAposta(false)
                    .setMostrarSalvarAposta(false)
                    .setTextoBotaoFinalizarResId(R.string.completarBotao);
        }

        uiState.setValue(
                builder.build()
        );
    }
    private BigDecimal obterValorApostaPorParametros(
            int qtdPrognosticos,
            int qtdConcursos
    ) {

        if (parametroJogoAtual == null) {
            return BigDecimal.ZERO;
        }

        if (qtdPrognosticos <= 0) {
            qtdPrognosticos = getQuantidadeMinimaSafe();
        }

        BigDecimal valorBase =
                BigDecimal.ZERO;

        if (parametroJogoAtual.getValoresAposta() != null
                && !parametroJogoAtual.getValoresAposta().isEmpty()) {

            if (tipoJogoAtual == ModalidadeEnum.MAIS_MILIONARIA
                    && parametroJogoAtual.getTrevos() != null
                    && parametroJogoAtual.getTrevos().getQtdMinima() != null) {

                br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroValorApostaDTO valorAposta =
                        parametroJogoAtual.getValorApostaBy(
                                qtdPrognosticos,
                                parametroJogoAtual.getTrevos().getQtdMinima()
                        );

                if (valorAposta != null
                        && valorAposta.getValor() != null) {

                    valorBase = valorAposta.getValor();
                }

            } else {

                for (br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroValorApostaDTO parametro
                        : parametroJogoAtual.getValoresAposta()) {

                    if (parametro == null) {
                        continue;
                    }

                    if (parametro.getNumeroPrognosticos() != null
                            && parametro.getNumeroPrognosticos() == qtdPrognosticos
                            && parametro.getValor() != null) {

                        valorBase = parametro.getValor();
                        break;
                    }
                }
            }
        }

        if (valorBase.compareTo(BigDecimal.ZERO) == 0
                && parametroJogoAtual.getValorApostaMinima() != null) {

            valorBase = parametroJogoAtual.getValorApostaMinima();
        }

        if (qtdConcursos > 0) {
            valorBase =
                    valorBase.multiply(
                            BigDecimal.valueOf(qtdConcursos)
                    );
        }

        return valorBase;
    }
    private boolean isValorGrande(
            BigDecimal valor
    ) {

        return valor != null
                && valor.doubleValue() > 999.99;
    }
    private BigDecimal obterValorInicialAposta() {

        int qtdMinima =
                getQuantidadeMinimaSafe();

        int qtdConcursoInicial = 0;

        if (parametroJogoAtual != null
                && parametroJogoAtual.getTeimosinhas() != null
                && !parametroJogoAtual.getTeimosinhas().isEmpty()) {

            Integer primeiraTeimosinha =
                    parametroJogoAtual.getTeimosinhas().get(0);

            qtdConcursoInicial =
                    primeiraTeimosinha != null
                            ? primeiraTeimosinha
                            : 0;
        }

        return obterValorApostaPorParametros(
                qtdMinima,
                qtdConcursoInicial
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
    private void habilitarModoAdicionar() {

        SimulaUiState atual =
                getCurrentState();

        uiState.setValue(
                atual.toBuilder()
                        .setBotaoCompletarHabilitado(false)
                        .setBotaoAdicionarHabilitado(true)
                        .setBotaoLimparHabilitado(true)
                        .build()
        );
    }
}