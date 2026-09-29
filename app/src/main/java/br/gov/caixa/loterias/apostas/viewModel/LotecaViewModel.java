package br.gov.caixa.loterias.apostas.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
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
import br.gov.caixa.loterias.apostas.effect.AnimacaoEffect;
import br.gov.caixa.loterias.apostas.effect.CarrinhoEffect;
import br.gov.caixa.loterias.apostas.event.SimulaUiEvent;
import br.gov.caixa.loterias.apostas.model.bean.SessaoUsuario;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroJogoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroValorApostaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.CarrinhoSingleton;
import br.gov.caixa.loterias.apostas.model.dao.DBLoteriasCrud;
import br.gov.caixa.loterias.apostas.model.model.AccordionModel;
import br.gov.caixa.loterias.apostas.model.sp.LoginSP;
import br.gov.caixa.loterias.apostas.state.SimulaUiState;
import br.gov.caixa.loterias.apostas.utils.BarraTituloDTO;
import br.gov.caixa.loterias.apostas.utils.ServicoFactoryUtil;
import br.gov.caixa.loterias.apostas.viewModel.reducer.AdicionarCarrinhoReducer;
import br.gov.caixa.loterias.apostas.viewModel.reducer.CarrinhoReducer;
import br.gov.caixa.loterias.apostas.viewModel.reducer.CompletarApostaReducer;
import br.gov.caixa.loterias.apostas.viewModel.reducer.FavoritarApostaReducer;
import br.gov.caixa.loterias.apostas.viewModel.reducer.FluxoApostaReducer;
import br.gov.caixa.loterias.apostas.viewModel.reducer.LimparReducer;
import br.gov.caixa.loterias.apostas.viewModel.reducer.MesDaSorteReducer;
import br.gov.caixa.loterias.apostas.viewModel.reducer.QuantidadeNumerosReducer;
import br.gov.caixa.loterias.apostas.viewModel.reducer.StateReducer;
import br.gov.caixa.loterias.apostas.viewModel.reducer.SurpresinhaCarrinhoReducer;
import br.gov.caixa.loterias.apostas.viewModel.reducer.SurpresinhaReducer;
import br.gov.caixa.loterias.apostas.viewModel.reducer.TeimosinhaReducer;
import br.gov.caixa.loterias.apostas.viewModel.reducer.TelaInicializadaReducer;
import br.gov.caixa.loterias.apostas.viewModel.reducer.TrevosReducer;
import br.gov.caixa.loterias.apostas.viewModel.reducer.ValorApostaReducer;

public class LotecaViewModel extends SimulaViewModel {

    private final MutableLiveData<SimulaUiState> uiState = new MutableLiveData<>(SimulaUiState.initial());
    private final TeimosinhaReducer teimosinhaReducer = new TeimosinhaReducer();
    private final FluxoApostaReducer fluxoApostaReducer = new FluxoApostaReducer();
    private final QuantidadeNumerosReducer quantidadeNumerosReducer = new QuantidadeNumerosReducer(fluxoApostaReducer);
    private final TrevosReducer trevosReducer = new TrevosReducer();
    private final CarrinhoReducer carrinhoReducer = new CarrinhoReducer();
    private final SurpresinhaReducer surpresinhaReducer = new SurpresinhaReducer();
    private final SurpresinhaCarrinhoReducer surpresinhaCarrinhoReducer = new SurpresinhaCarrinhoReducer();
    private final MesDaSorteReducer mesDaSorteReducer = new MesDaSorteReducer();
    private final FavoritarApostaReducer favoritarApostaReducer = new FavoritarApostaReducer();
    private final LimparReducer limparReducer = new LimparReducer();
    private final TelaInicializadaReducer telaInicializadaReducer = new TelaInicializadaReducer(fluxoApostaReducer);
    private final CompletarApostaReducer completarApostaReducer = new CompletarApostaReducer();
    private final AdicionarCarrinhoReducer adicionarCarrinhoReducer = new AdicionarCarrinhoReducer();
    private final ValorApostaReducer valorApostaReducer = new ValorApostaReducer();
    private IdentificaoDeUmaApostaDas8Modalidades apostaPendente;
    private BarraTituloDTO barraTituloPendente;

    private ModalidadeEnum tipoJogoAtual;
    private ParametroJogoDTO parametroJogoAtual;
    private boolean isEspecialAtual;

    public LotecaViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<SimulaUiState> getUiState() {
        return uiState;
    }

    public void onEvent(SimulaUiEvent event) {
        if (handleCarrinhoEvents(event)) {
            return;
        }
        if(handleLimparEvents(event)){
            return;
        }
        if(handleCompletarEvents(event)){
            return;
        }
        if(handleCartelaEvents(event)){
            return;
        }
        if(handleTeimosinhaEvents(event)){
            return;
        }
        if(handleNumerosEvents(event)){
            return;
        }
    }

    private boolean handleCarrinhoEvents(
            SimulaUiEvent event
    ) {

        if (event instanceof
                SimulaUiEvent.AdicionarCarrinhoSolicitado) {

            handleAdicionarCarrinhoSolicitado(
                    (SimulaUiEvent
                            .AdicionarCarrinhoSolicitado)
                            event
            );

            return true;
        }

        if (event instanceof
                SimulaUiEvent.AdicionarCarrinhoClicado) {

            handleAdicionarCarrinhoClicado(
                    (SimulaUiEvent
                            .AdicionarCarrinhoClicado)
                            event
            );

            return true;
        }

        if (event instanceof
                SimulaUiEvent.ConfirmarAdicionarCarrinho) {

            executarAdicionarCarrinhoPendente();

            return true;
        }

        if (event instanceof
                SimulaUiEvent.CarrinhoClicado) {

            abrirCarrinho();

            return true;
        }

        if (event instanceof
                SimulaUiEvent.CarregarCarrinho) {

            carregarCarrinho();

            return true;
        }

        if (event instanceof SimulaUiEvent.AbrirCarrinhoConsumido) {
            consumirAbrirCarrinho();
            return true;
        }

        if (event instanceof SimulaUiEvent.ComandoCarrinhoConsumido) {

            limparComandoCarrinho();

            return true;
        }

        return false;
    }
    private boolean handleLimparEvents(
            SimulaUiEvent event
    ) {
        if (event instanceof SimulaUiEvent.LimparApostaSolicitado) {

            limparAposta();

            return true;
        }

        if (event instanceof SimulaUiEvent.ComandoTelaSimulaConsumido) {
            limparComandoTelaSimula();
            return true;
        }

        if (event instanceof
                SimulaUiEvent.LimparMesDaSorte) {

            handleLimparMesDaSorte();

            return true;
        }

        if (event instanceof SimulaUiEvent.ComandoAnalyticsConsumido) {
            limparComandoAnalytics();
            return true;
        }

        if (event instanceof
                SimulaUiEvent.ComandoAnimacaoConsumido) {
            limparComandoAnimacao();
            return true;
        }

        if (event instanceof SimulaUiEvent.LimpezaApostaRenderizada) {
            limparFlagsDeRenderizacao();
            return true;
        }

        return false;
    }
    private boolean handleCartelaEvents(
            SimulaUiEvent event
    ) {

        if (event instanceof SimulaUiEvent.CartelaAlterada) {

            handleCartelaAlterada((SimulaUiEvent.CartelaAlterada) event);

            return true;
        }
        if (event instanceof SimulaUiEvent.ValorApostaAlterado) {

            handleValorApostaAlterado((SimulaUiEvent.ValorApostaAlterado) event);

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

        if (event instanceof SimulaUiEvent.InicializarParametrosAposta) {
            inicializarParametrosAposta(
                    (SimulaUiEvent
                            .InicializarParametrosAposta)
                            event
            );
            return true;
        }

        if (event instanceof SimulaUiEvent.TelaInicializada) {

            handleTelaInicializada((SimulaUiEvent.TelaInicializada) event);

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
    private boolean handleTeimosinhaEvents(
            SimulaUiEvent event
    ) {
        if (event instanceof SimulaUiEvent.InicializarTeimosinhas) {
            inicializarTeimosinhas((SimulaUiEvent.InicializarTeimosinhas) event);
            return true;
        }

        if (event instanceof SimulaUiEvent.ValorApostaAtualizado) {
            valorApostaAtualizado();
            return true;
        }

        if (event instanceof SimulaUiEvent.ClicarTeimosinhas) {
            clicarTeimosinhas();
            return true;
        }

        if (event instanceof
                SimulaUiEvent.QtdConcursoAtualizadaInternamente) {

            handleQtdConcursoAtualizadaInternamente(
                    (SimulaUiEvent
                            .QtdConcursoAtualizadaInternamente)
                            event
            );
            return true;
        }

        if (event instanceof SimulaUiEvent.ConfirmarTeimosinha) {
            confirmarTeimosinha((SimulaUiEvent.ConfirmarTeimosinha) event);
            return true;
        }

        if (event instanceof SimulaUiEvent.ConsumirEventoUnico) {
            consumirEventoUnico();
            return true;
        }

        return false;
    }
    private boolean handleNumerosEvents(
            SimulaUiEvent event
    ) {
        if (event instanceof SimulaUiEvent.InicializarQuantidadeNumeros) {
            inicializarQuantidadeNumeros((SimulaUiEvent.InicializarQuantidadeNumeros) event);
            return true;
        }
        if (event instanceof
                SimulaUiEvent.QuantidadeNumerosAtualizadaInternamente) {

            atualizarQuantidadeInterna(
                    (SimulaUiEvent.QuantidadeNumerosAtualizadaInternamente)
                            event
            );
            return true;
        }

        if (event instanceof SimulaUiEvent.ClicarQuantidadeNumeros) {
            clicarQuantidadeNumeros();
            return true;
        }

        if (event instanceof SimulaUiEvent.SelecionarQuantidadeNumeros) {
            selecionarQuantidadeNumeros((SimulaUiEvent.SelecionarQuantidadeNumeros) event);
            return true;
        }

        if (event instanceof SimulaUiEvent.ConfirmarQuantidadeNumeros) {
            confirmarQuantidadeNumeros((SimulaUiEvent.ConfirmarQuantidadeNumeros) event);
            return true;
        }

        if (event instanceof SimulaUiEvent.DialogQuantidadeNumerosConsumido) {
            consumirDialogQuantidadeNumeros();
            return true;
        }

        if (event instanceof SimulaUiEvent.AvisoQuantidadeNumerosConsumido) {
            consumirAvisoQuantidadeNumeros();
            return true;
        }

        if (event instanceof SimulaUiEvent.ValorApostaQuantidadeNumerosAtualizado) {
            valorApostaQuantidadeNumerosAtualizado();
            return true;
        }

        return false;
    }

    private boolean handleCompletarEvents(
            SimulaUiEvent event
    ) {
        if (event instanceof SimulaUiEvent.CompletarApostaClicado) {

            handleCompletarApostaClicado((SimulaUiEvent.CompletarApostaClicado) event);

            return true;
        }
        if (event instanceof SimulaUiEvent.ComandoCompletarApostaConsumido) {

            limparComandoCompletarAposta();

            return true;
        }

        return false;
    }

    private void inicializarParametrosAposta(
            SimulaUiEvent.InicializarParametrosAposta event
    ) {
        updateState( state ->
                state
                        .copy()
                        .setQtdDezenasPossiveisSelecionado(
                                event.getQtdDezenas()
                        )
                        .setQtdConcursoSelecionado(
                                event.getQtdConcurso()
                        )
        );
    }
    private void handleQtdConcursoAtualizadaInternamente(
            SimulaUiEvent.QtdConcursoAtualizadaInternamente event
    ) {
        updateState( state ->
                state.copy()
                        .setQtdConcursoSelecionado(
                                event.getQuantidade()
                        )
        );
    }
    private void atualizarQuantidadeInterna(
            SimulaUiEvent
                    .QuantidadeNumerosAtualizadaInternamente event
    ) {
        updateState( state ->
                state
                        .copy()
                        .setQtdDezenasPossiveisSelecionado(
                                event.getQuantidade()
                        )
        );
    }
    private void handleLimparMesDaSorte() {

        updateState(mesDaSorteReducer::limparMesDaSorte);
    }

    private void handleQuantidadeSurpresinhas(
            SimulaUiEvent.AtualizarQuantidadeSurpresinhas event
    ) {

        updateState( state ->
                surpresinhaReducer.atualizarQuantidadeSurpresinhas(
                        state,
                        event.getQuantidade(),
                        parametroJogoAtual,
                        tipoJogoAtual
                )
        );
    }

    private void handleQuantidadeTrevosSurpresinha(
            SimulaUiEvent.AtualizarQuantidadeTrevosSurpresinha event
    ) {

        updateState( state ->
                surpresinhaReducer.atualizarQuantidadeTrevosSurpresinha(
                        state,
                        event.getQuantidade(),
                        parametroJogoAtual,
                        tipoJogoAtual
                )
        );
    }

    private void handleQuantidadeNumerosSurpresinha(
            SimulaUiEvent.AtualizarQuantidadeNumerosSurpresinha event
    ) {

        updateState( state ->
                surpresinhaReducer.atualizarQuantidadeNumerosSurpresinha(
                        state,
                        event.getQuantidade(),
                        parametroJogoAtual,
                        tipoJogoAtual
                )
        );
    }

    private void handleToggleTrevo(
            SimulaUiEvent.ToggleTrevo event
    ) {
        SimulaUiState atual =
                getCurrentState();

        List<ParametroValorApostaDTO> valoresTrevos =
                obterValoresTrevosParaQuantidadeAtual(
                        atual
                );

       updateState(
                trevosReducer.toggleTrevo(
                        atual,
                        event,
                        valoresTrevos
                )
        );
    }

    private List<ParametroValorApostaDTO> obterValoresTrevosParaQuantidadeAtual(
            SimulaUiState atual
    ) {
        if (parametroJogoAtual == null
                || atual == null) {
            return new ArrayList<>();
        }

        int quantidadeNumeros =
                atual.getQtdDezenasPossiveisSelecionado();

        if (quantidadeNumeros <= 0) {
            quantidadeNumeros =
                    getQuantidadeMinimaSafe();
        }

        List<ParametroValorApostaDTO> valores =
                parametroJogoAtual.getValoresTrevoByNumero(
                        quantidadeNumeros
                );

        if (valores == null) {
            return new ArrayList<>();
        }

        return valores;
    }

    private void consumirAvisoTrevosMaximo() {

        updateState(trevosReducer::consumirAvisoTrevosMaximo);
    }

    private void selecionarQuantidadeTrevos(
            SimulaUiEvent.SelecionarQuantidadeTrevos event
    ) {

        updateState(state ->
                trevosReducer.selecionarQuantidadeTrevos(
                        state,
                        event
                )
        );
    }

    private void consumirDialogQuantidadeTrevos() {

        updateState(trevosReducer::consumirDialogQuantidadeTrevos);
    }

    private void limparComandoAnimacao() {
        updateState(state ->
                state.copy()
                        .setComandoAnimacao(null)
        );
    }

    private void limparComandoTelaSimula() {
        updateState(state -> state.copy().setComandoTelaSimula(null));
    }

    private void abrirCarrinho() {

        updateState(carrinhoReducer::abrirCarrinho);
    }

    private void consumirAbrirCarrinho() {

       updateState(carrinhoReducer::consumirAbrirCarrinho
        );
    }

    private void inicializarQuantidadeNumeros(
            SimulaUiEvent.InicializarQuantidadeNumeros event
    ) {

        updateState(state ->
                quantidadeNumerosReducer.inicializar(
                        state,
                        event
                )
        );
    }

    private void clicarQuantidadeNumeros() {

        updateState(quantidadeNumerosReducer::clicar);
    }

    private void selecionarQuantidadeNumeros(
            SimulaUiEvent.SelecionarQuantidadeNumeros event
    ) {

        updateState(state ->
                quantidadeNumerosReducer.selecionar(
                        state,
                        event,
                        tipoJogoAtual
                )
        );
    }

    private void confirmarQuantidadeNumeros(
            SimulaUiEvent.ConfirmarQuantidadeNumeros event
    ) {
        SimulaUiState novoEstado =
                quantidadeNumerosReducer.confirmar(
                        getCurrentState(),
                        event,
                        tipoJogoAtual
                );

        novoEstado =
                fluxoApostaReducer.recalcularEstadoCartela(
                        novoEstado,
                        tipoJogoAtual
                );

        updateState(
                novoEstado
        );

        recalcularValorApostaAtual();
    }

    private String montarTextoQuantidadeNumeros(
            int qtdPrognosticos
    ) {

        return quantidadeNumerosReducer
                .montarTextoQuantidadeNumeros(
                        qtdPrognosticos
                );
    }

    private void consumirDialogQuantidadeNumeros() {

        updateState(quantidadeNumerosReducer::consumirDialog);
    }

    private void consumirAvisoQuantidadeNumeros() {

        updateState(quantidadeNumerosReducer::consumirAviso);
    }

    private void valorApostaQuantidadeNumerosAtualizado() {

        updateState(quantidadeNumerosReducer::valorApostaAtualizado);
    }

    private void inicializarTeimosinhas(SimulaUiEvent.InicializarTeimosinhas event) {
        updateState(state ->
                teimosinhaReducer.inicializar(
                        state,
                        event.getQntTeimosinhas(),
                        event.getQntConcursos(),
                        event.getQntConcursoSelecionado(),
                        event.isEspecial(),
                        event.getTextoInicialTeimosinha()
                )
        );
    }

    private void clicarTeimosinhas() {
        updateState(teimosinhaReducer::clicar);
    }

    private void confirmarTeimosinha(SimulaUiEvent.ConfirmarTeimosinha event) {
        updateState(state ->
                teimosinhaReducer.confirmar(
                        state,
                        event.getPosition(),
                        tipoJogoAtual
                )
        );

        recalcularValorApostaAtual();
    }

    private void consumirEventoUnico() {

       updateState(teimosinhaReducer::consumirEvento);
    }

    private void valorApostaAtualizado() {

        updateState(teimosinhaReducer::valorAtualizado);
    }
    private void handleComoJogar() {
        updateState(state -> state.copy().setMensagemComoJogar(obterTextoComoJogar(tipoJogoAtual, isEspecialAtual)).setTitleComoJogar(obterTitleComoJogar(tipoJogoAtual, isEspecialAtual)).setExibirDialogComoJogar(true));
    }

    public static String obterTextoComoJogar(ModalidadeEnum modalidade, boolean especial) {
        return new AccordionModel(modalidade, especial).getBodyText();
    }

    public static String obterTitleComoJogar(ModalidadeEnum modalidade, boolean especial) {
        return new AccordionModel(modalidade, especial).getTituloComoJogar(modalidade);
    }

    private void consumirDialogComoJogar() {
        updateState(state -> state.copy().setExibirDialogComoJogar(false));
    }

    private void carregarCarrinho() {

        publicarEstadoCarrinho(CarrinhoSingleton.getInstance().getCarrinho(), true);

        if (LoginSP.isLoginRealizado()) {
            carregarCarrinhoOnline();
            return;
        }

        carregarCarrinhoLocal();
    }

    private void carregarCarrinhoOnline() {

        ServicoFactoryUtil.getApostaService().buscaCarrinho(new RequestListener<CarrinhoDTOResponse>() {

            @Override
            public void onResponse(CarrinhoDTOResponse response) {

                CarrinhoDTO carrinho = response != null ? response.getPayload() : null;

                if (carrinho == null) {
                    carrinho = new CarrinhoDTO();
                }

                adicionarApostasLocaisNoCarrinho(carrinho);

                CarrinhoSingleton.getInstance().setCarrinho(carrinho);

                publicarEstadoCarrinho(carrinho, false);
            }

            @Override
            public void onErrorResponse(VolleyError error) {

                carregarCarrinhoLocal();
            }
        });
    }

    private void carregarCarrinhoLocal() {

        CarrinhoDTO carrinho = new CarrinhoDTO();

        carrinho.setApostas(getApostasLocais());

        CarrinhoSingleton.getInstance().setCarrinho(carrinho);

        publicarEstadoCarrinho(carrinho, false);
    }

    private void adicionarApostasLocaisNoCarrinho(CarrinhoDTO carrinho) {

        List<IdentificaoDeUmaApostaDas8Modalidades> apostasLocais = getApostasLocais();

        if (apostasLocais == null || apostasLocais.isEmpty()) {
            return;
        }

        if (carrinho.getApostas() == null) {
            carrinho.setApostas(new ArrayList<>());
        }

        carrinho.getApostas().addAll(apostasLocais);
    }

    private List<IdentificaoDeUmaApostaDas8Modalidades> getApostasLocais() {

        DBLoteriasCrud crud = new DBLoteriasCrud(getApplication());

        return crud.readAllIdentificaoDeUmaApostaDas8Modalidades();
    }
    private void handleSolicitarSelecaoTimeCoracao() {
        updateState(surpresinhaReducer::abrirSelecaoTimeCoracaoSurpresinha
        );
    }
    private void publicarEstadoCarrinho(
            CarrinhoDTO carrinho,
            boolean carregando
    ) {

        updateState(state ->
                carrinhoReducer.publicarEstadoCarrinho(
                        state,
                        carrinho,
                        carregando
                )
        );
    }

    private void handleCartelaAlterada(
            SimulaUiEvent.CartelaAlterada event
    ) {

        updateState(state ->
                fluxoApostaReducer.cartelaAlterada(
                        state,
                        event
                )
        );
    }


    private void handleValorApostaAlterado(SimulaUiEvent.ValorApostaAlterado event) {

        BigDecimal valor = event.getValor();

        updateState(state -> state.copy().setValorAposta(valor).setValorGrande(valor != null && valor.doubleValue() > 999.99));
    }

    private void handleCompletarApostaClicado(
            SimulaUiEvent.CompletarApostaClicado event
    ) {

        updateState(state ->
                completarApostaReducer.completarAposta(
                        state,
                        event
                )
        );
    }

    private void handleAdicionarCarrinhoClicado(
            SimulaUiEvent.AdicionarCarrinhoClicado event
    ) {

        updateState(state ->
                adicionarCarrinhoReducer
                        .adicionarCarrinho(
                                state,
                                event
                        )
        );
    }

    private void limparComandoCompletarAposta() {

        updateState(completarApostaReducer::limparComando);
    }
    private void handleTelaInicializada(
            SimulaUiEvent.TelaInicializada event
    ) {

        tipoJogoAtual =
                event.getModalidade();

        parametroJogoAtual =
                event.getParametroSimulacao();

        isEspecialAtual =
                event.isEspecial();

        BarraTituloDTO barraTituloDTO =
                criarBarraTitulo(
                        parametroJogoAtual,
                        event.isEspecial()
                );

        boolean exibirAnimacao =
                false;

        int animacaoRes =
                0;

        float velocidadeAnimacao =
                1f;

        if (event.isEspecial()) {

            if (tipoJogoAtual == ModalidadeEnum.LOTOFACIL) {

                exibirAnimacao =
                        true;

                animacaoRes =
                        R.raw.lotofacil_independencia;

                velocidadeAnimacao =
                        0.60f;

            }
        }

        int qtdMinima =
                getQuantidadeMinimaSafe();

        int qtdConcurso =
                0;


        BigDecimal valorInicialAposta =
                obterValorApostaPorParametros(
                        qtdMinima,
                        qtdConcurso
                );

        AnimacaoEffect animacaoEffect =
                null;

        if (exibirAnimacao) {

            animacaoEffect =
                    new AnimacaoEffect.Exibir(
                            animacaoRes,
                            velocidadeAnimacao
                    );
        }

        String textoBotaoQuantidadeNumeros =
                qtdMinima > 0
                        ? montarTextoQuantidadeNumeros(
                        qtdMinima
                )
                        : "";

        updateState(
                telaInicializadaReducer.criarEstadoInicializado(
                        tipoJogoAtual,
                        event.isEspecial(),
                        barraTituloDTO,
                        valorInicialAposta,
                        animacaoEffect,
                        qtdMinima,
                        qtdConcurso,
                        0,
                        textoBotaoQuantidadeNumeros
                )
        );
    }

    private void handleAdicionarCarrinhoSolicitado(
            SimulaUiEvent.AdicionarCarrinhoSolicitado event
    ) {

        apostaPendente = event.getAposta();
        barraTituloPendente = getBarraTituloSafe(event.getBarraTituloDTO());

        if (LoginSP.isLoginRealizado() && checaLimiteDiario(apostaPendente)) {
            publicarComando(new CarrinhoEffect.MostrarConfirmacaoLimiteDiario());
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

        publicarComando(new CarrinhoEffect.ExecutarAdicionarCarrinhoEffect(apostaPendente, barraTituloPendente));
    }

    private boolean checaLimiteDiario(IdentificaoDeUmaApostaDas8Modalidades aposta) {

        if (CarrinhoSingleton.getInstance().getCarrinho() == null || CarrinhoSingleton.getInstance().getCarrinho().getValorTotal() == null || SessaoUsuario.getInstance().getParametrosSimulacao() == null || SessaoUsuario.getInstance().getParametrosSimulacao().getValorLimiteDiario() == null || aposta == null || aposta.getValor() == null) {

            return false;
        }

        BigDecimal valorAtualCarrinho = CarrinhoSingleton.getInstance().getCarrinho().getValorTotal();

        BigDecimal valorLimiteDiario = SessaoUsuario.getInstance().getParametrosSimulacao().getValorLimiteDiario();

        return valorAtualCarrinho.add(aposta.getValor()).compareTo(valorLimiteDiario) > 0;
    }

    private void publicarComando(CarrinhoEffect carrinhoEffect) {
        updateState(state -> state.copy().setComandoCarrinho(carrinhoEffect));
    }

    private void limparComandoCarrinho() {
        updateState(state -> state.copy().setComandoCarrinho(null));
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

        updateState(
                limparReducer.limparAposta(
                        state,
                        tipoJogo,
                        qtdNumerosAtual,
                        valorApostaAtual
                )
        );
    }

    private BigDecimal obterValorApostaPorParametros(int qtdPrognosticos, int qtdConcursos) {

        if (parametroJogoAtual == null) {
            return BigDecimal.ZERO;
        }

        if (qtdPrognosticos <= 0) {
            qtdPrognosticos = getQuantidadeMinimaSafe();
        }

        BigDecimal valorBase = BigDecimal.ZERO;

        if (parametroJogoAtual.getValoresAposta() != null && !parametroJogoAtual.getValoresAposta().isEmpty()) {

            if (tipoJogoAtual == ModalidadeEnum.MAIS_MILIONARIA && parametroJogoAtual.getTrevos() != null && parametroJogoAtual.getTrevos().getQtdMinima() != null) {

                ParametroValorApostaDTO valorAposta = parametroJogoAtual.getValorApostaBy(qtdPrognosticos, parametroJogoAtual.getTrevos().getQtdMinima());

                if (valorAposta != null && valorAposta.getValor() != null) {

                    valorBase = valorAposta.getValor();
                }

            } else {

                for (ParametroValorApostaDTO parametro : parametroJogoAtual.getValoresAposta()) {

                    if (parametro == null) {
                        continue;
                    }

                    if (parametro.getNumeroPrognosticos() != null && parametro.getNumeroPrognosticos() == qtdPrognosticos && parametro.getValor() != null) {

                        valorBase = parametro.getValor();
                        break;
                    }
                }
            }
        }

        if (valorBase.compareTo(BigDecimal.ZERO) == 0 && parametroJogoAtual.getValorApostaMinima() != null) {

            valorBase = parametroJogoAtual.getValorApostaMinima();
        }

        if (qtdConcursos > 0) {
            valorBase = valorBase.multiply(BigDecimal.valueOf(qtdConcursos));
        }

        return valorBase;
    }

    private void limparFlagsDeRenderizacao() {

        updateState(limparReducer::limparFlagsDeRenderizacao);
    }

    private BarraTituloDTO criarBarraTitulo(ParametroJogoDTO parametro, boolean especial) {

        BarraTituloDTO dto = new BarraTituloDTO();

        dto.setEspecial(especial);

        if (parametro != null && parametro.getConcurso() != null) {

            dto.setNumeroConcurso(String.valueOf(parametro.getConcurso().getNumero()));

            dto.setDataSorteio(formatarDataSorteio(parametro));
        }

        return dto;
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

    private String formatarDataSorteio(ParametroJogoDTO parametro) {

        try {

            if (parametro == null || parametro.getConcurso() == null || parametro.getConcurso().getDataHoraSorteio() == null) {

                return "";
            }

            DateFormat entrada = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());

            DateFormat saida = new SimpleDateFormat("dd/MM", Locale.getDefault());

            Date data = entrada.parse(parametro.getConcurso().getDataHoraSorteio());

            if (data == null) {
                return "";
            }

            return saida.format(data);

        } catch (Exception e) {
            return "";
        }
    }

    private SimulaUiState getCurrentState() {

        SimulaUiState atual = uiState.getValue();

        if (atual == null) {
            return SimulaUiState.initial();
        }

        return atual;
    }

    private void limparComandoAnalytics() {
       updateState(state ->
                state.copy()
                        .setComandoAnalytics(null)
        );
    }

    private BarraTituloDTO getBarraTituloSafe(BarraTituloDTO barraTituloDTO) {

        if (barraTituloDTO != null) {
            return barraTituloDTO;
        }

        SimulaUiState state = uiState.getValue();

        if (state != null && state.getBarraTituloDTO() != null) {

            return state.getBarraTituloDTO();
        }

        return new BarraTituloDTO();
    }

    private int getQuantidadeMinimaSafe() {

        if (parametroJogoAtual == null || parametroJogoAtual.getQuantidadeMinima() == null) {

            return 0;
        }

        return parametroJogoAtual.getQuantidadeMinima();
    }

    private void recalcularValorApostaAtual() {
        updateState(state ->
                valorApostaReducer.recalcular(
                        state,
                        parametroJogoAtual,
                        tipoJogoAtual
                )
        );
    }
    private void handleVoltarParaSurpresinha() {
        updateState(
                surpresinhaReducer::voltarParaSurpresinha
        );
    }
    private String obterTextoCompletar(
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

    private void updateState(
            SimulaUiState novoEstado
    ) {
        if (novoEstado == null) {
            return;
        }

        SimulaUiState estadoFinal =
                novoEstado.copy()
                        .setTextoCompletar(
                                obterTextoCompletar(
                                        novoEstado
                                )
                        );

        uiState.setValue(
                estadoFinal
        );
    }

    private void updateState(
            StateReducer reducer
    ) {
        if (reducer == null) {
            return;
        }

        updateState(
                reducer.reduce(
                        getCurrentState()
                )
        );
    }

}

